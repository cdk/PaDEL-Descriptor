package libpadeldescriptor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import libpadeljobs.Worker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;

/**
 * Worker class to calculate descriptor for a molecule.
 *
 * @author yapchunwei
 */
public class libPaDELDescriptorWorker extends Worker<libPaDELDescriptorJob>
{
    protected libPaDELDescriptorMaster master = null;
    protected libPaDELDescriptorMasterGeneral masterG = null;
    protected int channelID;    // Channel id to communicate with master.
    protected Set<String> descriptorTypes;
    protected List<CDK_Descriptor> cdk_descriptors;
    protected List<CDK_Fingerprint> cdk_fingerprints;
    protected List<CDK_FingerprintCount> cdk_fingerprints_count;

    public libPaDELDescriptorWorker(libPaDELDescriptorMaster master,
                                     int channelID,
                                     LinkedBlockingQueue<libPaDELDescriptorJob> jobsWaiting,
                                     LinkedBlockingQueue<libPaDELDescriptorJob> jobsRunning,
                                     LinkedBlockingQueue<libPaDELDescriptorJob> jobsCompleted,
                                     Set<String> descriptorTypes)
    {
        super(jobsWaiting, jobsRunning, jobsCompleted);
        this.master = master;
        this.channelID = channelID;
        this.descriptorTypes = descriptorTypes;
    }

    public libPaDELDescriptorWorker(libPaDELDescriptorMasterGeneral masterG,
                                     int channelID,
                                     LinkedBlockingQueue<libPaDELDescriptorJob> jobsWaiting,
                                     LinkedBlockingQueue<libPaDELDescriptorJob> jobsRunning,
                                     LinkedBlockingQueue<libPaDELDescriptorJob> jobsCompleted,
                                     Set<String> descriptorTypes)
    {
        super(jobsWaiting, jobsRunning, jobsCompleted);
        this.masterG = masterG;
        this.channelID = channelID;      
        this.descriptorTypes = descriptorTypes;
    }

    @Override
    public void DoJob()
    {
        cdk_descriptors = new ArrayList<CDK_Descriptor>();
        cdk_fingerprints = new ArrayList<CDK_Fingerprint>();
        cdk_fingerprints_count = new ArrayList<CDK_FingerprintCount>();
        libPaDELDescriptorType.SetDescriptorTypes(descriptorTypes, null, cdk_descriptors, cdk_fingerprints, cdk_fingerprints_count);
        
        if (master!=null)
        {
            String status = "Processing " + job.getName() + " in " + job.getFilename() + " (" + job.getId() + "/" + master.GetMaxMolecules() + "). ";
            if (master.getMolProcessed()>0)
            {
                double speed = (System.nanoTime()-master.getStartTime()) / 1e9 / master.getMolProcessed();
                status += "Average speed: " + new DecimalFormat("#0.00").format(speed) + " s/mol.";
            }
            master.addMessage(channelID, status);
        }

        if (masterG!=null)
        {
            String status = "Processing " + job.getName() + " (" + job.getId() + "/" + masterG.GetMaxMolecules() + "). ";
            if (masterG.getMolProcessed()>0)
            {
                double speed = (System.nanoTime()-masterG.getStartTime()) / 1e9 / masterG.getMolProcessed();
                status += "Average speed: " + new DecimalFormat("#0.00").format(speed) + " s/mol.";
            }
            masterG.addMessage(channelID, status);
        }

        IAtomContainer molecule = job.getStructure();
        
        // Standardize molecule.
        PaDELStandardize standardize = new PaDELStandardize();
        standardize.setRemoveSalt(job.isRemoveSalt());
        standardize.setDearomatize(job.detectAromaticity);
        standardize.setStandardizeTautomers(job.isStandardizeTautomers());
        standardize.setTautomerList(job.getTautomerList());
        standardize.setStandardizeNitro(job.isStandardizeNitro());
        standardize.setRetain3D(job.isRetain3D());
        try
        {
            molecule = standardize.Standardize(molecule);
        }
        catch (Exception ex)
        {
            Logger.getLogger("global").log(Level.FINE, null, ex);
        }        

        if (job.isConvertTo3D())
        {
            try
            {
                // Calculate 3D coordinates.
                TemplateHandler3D template = TemplateHandler3D.getInstance();
                ModelBuilder3D mb3d = ModelBuilder3D.getInstance(template,job.getForcefield());
                molecule = (IAtomContainer) mb3d.generate3DCoordinates((IMolecule) molecule, true);
            }
            catch (Exception ex)
            {
                Logger.getLogger("global").log(Level.FINE, null, ex);
            }
        }
        
        ArrayList<String> descriptors = new ArrayList<String>();
        if (job.getMaxRunTime()<=0)
        {        
            for (CDK_Descriptor cdk_descriptor : cdk_descriptors)
            {
                try
                {
                    cdk_descriptor.setMolecule((IAtomContainer)molecule.clone());
                    cdk_descriptor.run();
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.FINE, null, ex);
                }
                descriptors.addAll(Arrays.asList(cdk_descriptor.descriptorValues_));
            }

            for (CDK_Fingerprint cdk_fingerprint : cdk_fingerprints)
            {
                try
                {
                    cdk_fingerprint.setMolecule((IAtomContainer)molecule.clone());
                    cdk_fingerprint.run();
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.FINE, null, ex);
                }
                descriptors.addAll(Arrays.asList(cdk_fingerprint.descriptorValues_));
            }

            for (CDK_FingerprintCount cdk_fingerprint_count : cdk_fingerprints_count)
            {
                try
                {
                    cdk_fingerprint_count.setMolecule((IAtomContainer)molecule.clone());
                    cdk_fingerprint_count.run();
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.FINE, null, ex);
                }
                descriptors.addAll(Arrays.asList(cdk_fingerprint_count.descriptorValues_));
            }
        }
        else
        {
            // Enable descriptor calculation to be stopped if it takes too long.
            long startTime = System.nanoTime();
            for (CDK_Descriptor cdk_descriptor : cdk_descriptors)
            {
                try
                {
                    cdk_descriptor.setMolecule((IAtomContainer)molecule.clone());
                    cdk_descriptor.start();
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.FINE, null, ex);
                }
            }

            for (CDK_Fingerprint cdk_fingerprint : cdk_fingerprints)
            {
                try
                {
                    cdk_fingerprint.setMolecule((IAtomContainer)molecule.clone());
                    cdk_fingerprint.start();
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.FINE, null, ex);
                }
            }

            for (CDK_FingerprintCount cdk_fingerprint_count : cdk_fingerprints_count)
            {
                try
                {
                    cdk_fingerprint_count.setMolecule((IAtomContainer)molecule.clone());
                    cdk_fingerprint_count.start();
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.FINE, null, ex);
                }
            }
            
            long maxRunTime = job.maxRunTime * 1000000;
            boolean isAllDead = true;
            while (System.nanoTime()-startTime < maxRunTime)
            {
                isAllDead = true;
                for (CDK_Descriptor cdk_descriptor : cdk_descriptors)
                {
                    if (cdk_descriptor.isAlive()) 
                    {
                        isAllDead = false;
                        break;
                    }
                }
                for (CDK_Fingerprint cdk_fingerprint : cdk_fingerprints)
                {
                    if (cdk_fingerprint.isAlive()) 
                    {
                        isAllDead = false;
                        break;
                    }
                }
                for (CDK_FingerprintCount cdk_fingerprint_count : cdk_fingerprints_count)
                {
                    if (cdk_fingerprint_count.isAlive()) 
                    {
                        isAllDead = false;
                        break;
                    }
                }
                if (isAllDead) break; // Quit waiting if all descriptor calculation threads have completed.
            }
            
            if (!isAllDead)
            {
                // Interrrupt descriptor calculation threads that are still running.
                for (CDK_Descriptor cdk_descriptor : cdk_descriptors)
                {
                    if (cdk_descriptor.isAlive()) cdk_descriptor.interrupt();
                }
                for (CDK_Fingerprint cdk_fingerprint : cdk_fingerprints)
                {
                    if (cdk_fingerprint.isAlive()) cdk_fingerprint.interrupt();
                }
                for (CDK_FingerprintCount cdk_fingerprint_count : cdk_fingerprints_count)
                {
                    if (cdk_fingerprint_count.isAlive()) cdk_fingerprint_count.interrupt();
                }
            }
            
            // Save descriptors.
            for (CDK_Descriptor cdk_descriptor : cdk_descriptors) descriptors.addAll(Arrays.asList(cdk_descriptor.descriptorValues_));
            for (CDK_Fingerprint cdk_fingerprint : cdk_fingerprints) descriptors.addAll(Arrays.asList(cdk_fingerprint.descriptorValues_));
            for (CDK_FingerprintCount cdk_fingerprint_count : cdk_fingerprints_count) descriptors.addAll(Arrays.asList(cdk_fingerprint_count.descriptorValues_));
        }
        job.setDescriptors(descriptors);
    }
}
