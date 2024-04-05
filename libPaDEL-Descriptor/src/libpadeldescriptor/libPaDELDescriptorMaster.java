package libpadeldescriptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import libpadeljobs.Master;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Calculate CDK descriptors using different threads.
 * 
 * @author yapchunwei
 */
public class libPaDELDescriptorMaster extends Master<libPaDELDescriptorJob, libPaDELDescriptorWorker>
{
    // Variables to set before running descriptor calculation
    private String moleculeDirFile; // Directory containing molecule files.
    private String resultsFile;  // File to save descriptors
    private boolean compute2D = false;  // Calculate 1D, 2D descriptors.
    private boolean compute3D = false;  // Calculate 3D descriptors.
    private boolean computeFingerprints = false;    // Calculate fingerprint.
    private boolean removeSalt = true; // If true, remove salts like Na, Cl from the molecule before calculation of descriptors.
    private boolean detectAromaticity = true; // If true, remove existing aromaticity information and automatically detect aromaticity in the molecule before calculation of descriptors.
    private boolean standardizeTautomers = true;    // If true, standardize tautomers.
    private boolean standardizeNitro = true;    // If true, standardize nitro groups to N(:O):O. 
    private boolean retain3D = false;   // If true, retain 3D coordinates when standardizing structure. However, this may prevent some structures from being standardized.
    private boolean convertTo3D = false; // If true, convert molecule to 3D before calculation of descriptors.

    // Optional variables
    private int maxMolPerFile = -1;  // Maximum number of molecule to store in a descriptor file.
    private String forcefield = libPaDELDescriptorJob.MM2; // Forcefield to use for converting molecules to 3D.
    private String[] tautomerList;    // SMIRKS list to convert tautomers.
    private boolean logResults = false; // Log results.
    private boolean retainOrder = true; // Retain order of molecules in structural files for descriptor file. This may lead to large memory use if descriptor calculations are stuck at one molecule as the others will not be written to file and cleared from memory.
    private boolean useFilenameAsMolName = false; // Use filename (minus the extension) as molecule name.
    private long maxRunTime = -1;   // Maximum running time in milliseconds for calculation of descriptors for a single molecule. If <=0, means no restriction on running time.

    // Variables containing progress of descriptor calculation
    private int progress; // Total number of molecules that have been read from files. Used as job id.
    private int curMolFileIndex = -1; // Current molecule file index.
    private int molProcessed = 0;   // Number of molecules that has completed descriptor calculation.
    private int curResultsFileCount = 1;
    private PaDELResultsFile results = new PaDELResultsFile();
    private String curResultsFile;  // Current file to save descriptor.
    private String curLogFile;      // Current log file.

    // General variables
    private PaDELLogFile log;
    private long startTime;
    private long endTime;
    private File[] fMolecules;
    private int maxMolecules = -1;
    private List<String> descriptorNames;
    private Set<String> descriptorTypes;
    private String resultsFileName;
    private String resultsFileExt;
    private libPaDELDescriptorJob[] completedJobs;
    private int savedMoleculesIndex;

    // Variables for each iteration
    private IteratingPaDELReader reader;
    private int PaDELReaderCount;
    private String molName;
    private IAtomContainer molStructure;

    public libPaDELDescriptorMaster(String moleculeDirFile,
                                    String resultsFile,
                                    boolean compute2D,
                                    boolean compute3D,
                                    boolean computeFingerprints,
                                    boolean removeSalt,                                    
                                    boolean detectAromaticity,
                                    boolean standardizeTautomers,
                                    boolean standardizeNitro,
                                    boolean retain3D,
                                    boolean convertTo3D)
    {
        super();
        this.moleculeDirFile = moleculeDirFile;
        this.resultsFile = resultsFile;
        this.compute2D = compute2D;
        this.compute3D = compute3D;
        this.computeFingerprints = computeFingerprints;
        this.removeSalt = removeSalt;
        this.detectAromaticity = detectAromaticity;
        this.standardizeTautomers = standardizeTautomers;
        this.standardizeNitro = standardizeNitro;
        this.retain3D = retain3D;
        this.convertTo3D = convertTo3D;
        descriptorNames = new ArrayList<String>();
        log = new PaDELLogFile();
        results = new PaDELResultsFile();
    }

    public void SetDescriptorTypes(String descriptorFile) throws Exception
    {
        descriptorTypes = libPaDELDescriptorType.GetActiveDescriptorTypes(descriptorFile, compute2D, compute3D, computeFingerprints);
        libPaDELDescriptorType.SetDescriptorTypes(descriptorTypes, descriptorNames, null, null, null);
    }

    public void SetDescriptorTypes(InputStream in) throws Exception
    {
        descriptorTypes = libPaDELDescriptorType.GetActiveDescriptorTypes(in, compute2D, compute3D, computeFingerprints);
        libPaDELDescriptorType.SetDescriptorTypes(descriptorTypes, descriptorNames, null, null, null);
    }

    public void SetDescriptorTypes(Set<String> descriptorTypes)
    {
        this.descriptorTypes = descriptorTypes;
        libPaDELDescriptorType.SetDescriptorTypes(descriptorTypes, descriptorNames, null, null, null);
    }
    
    public void setTautomerList(String[] tautomerList)
    {
        this.tautomerList = tautomerList;
    }
    
    public void setForcefield(String forcefield)
    {
        this.forcefield = forcefield;
    }

    public void setMaxMolPerFile(int maxMolPerFile)
    {
        this.maxMolPerFile = maxMolPerFile;
    }

    public void setLogResults(boolean logResults)
    {
        this.logResults = logResults;
    }

    public void setRetainOrder(boolean retainOrder)
    {
        this.retainOrder = retainOrder;
    }

    public void setUseFilenameAsMolName(boolean useFilenameAsMolName)
    {
        this.useFilenameAsMolName = useFilenameAsMolName;
    }

    public void setMaxRunTime(long maxRunTime)
    {
        this.maxRunTime = maxRunTime;
    }

    public String CheckRequirements()
    {
        String errorMessage = new String();
        if (moleculeDirFile==null || moleculeDirFile.isEmpty()) errorMessage += "Please set directory containing structural files or a structural file.\n";
        if (resultsFile==null || resultsFile.isEmpty()) errorMessage += "Please set file to save calculated descriptors.\n";
        if (compute3D && (detectAromaticity || standardizeTautomers) && !retain3D && !convertTo3D) errorMessage += "Standardization of molecules will remove 3D information.\n[3D descriptors cannot be calculated unless\n  - 3D information is retained (may not standardize properly) or\n  - convert molecule to 3D option is checked.]\n";
        if (errorMessage.isEmpty()) return null;
        else return errorMessage;
    }

    @Override
    public String Initialize()
    {
        super.Initialize();

        startTime = System.nanoTime();
        GetMaxMolecules();

        curResultsFileCount = 1;
        curResultsFile = resultsFile;
        curLogFile = curResultsFile + ".log";
        if (maxMolPerFile>0 && maxMolecules>maxMolPerFile)
        {
            // Break log and descriptor files into smaller sizes.
            int extIndex = resultsFile.lastIndexOf(".");
            if (extIndex == -1)
            {
                resultsFileName = resultsFile;
                resultsFileExt = "";
            }
            else
            {
                resultsFileName = resultsFile.substring(0, extIndex);
                resultsFileExt = resultsFile.substring(extIndex);
            }

            curResultsFile = resultsFileName + "_" + curResultsFileCount + resultsFileExt;
            curLogFile = resultsFile + "_" + curResultsFileCount + ".log";
        }

        // Prepare log file.
        String error = log.Open(curLogFile, logResults);
        if (error!=null) return error;

        // Descriptor file header
        error = results.Open(curResultsFile);
        if (error!=null) return error;
        results.Write("Name");
        for (String descriptor : descriptorNames)
        {
            results.Write(",");
            results.Write(descriptor);
        }
        results.Write("\n");
       
        File dirFile = new File(moleculeDirFile);
        if (dirFile.isDirectory())
        {
            fMolecules = dirFile.listFiles();
        }
        else
        {
            fMolecules = new File[1];
            fMolecules[0] = dirFile;
        }
        curMolFileIndex = -1;
        progress = 0;
        molProcessed = 0;
        savedMoleculesIndex = 0;
        completedJobs = new libPaDELDescriptorJob[GetMaxMolecules()];

        // Initialize and start workers.
        for (int i=0; i<maxThreads; ++i)
        {
            libPaDELDescriptorWorker worker = new libPaDELDescriptorWorker(this, i, jobsWaiting, jobsRunning, jobsCompleted, descriptorTypes);
            worker.start();
            workers.add(worker);
        }

        return null;
    }

    private boolean hasNextJob()
    {
        if (reader==null || !PaDELReaderHasNext())
        {
            // Not in the process of reading a file containing multiple molecules.
            ++curMolFileIndex;
            if (curMolFileIndex < fMolecules.length)
            {
                // Still have molecules not processed.
                File fMolecule = fMolecules[curMolFileIndex];

                if (fMolecule.getName().equals(".") || fMolecule.getName().equals("..") || fMolecule.isDirectory())
                {
                    return hasNextJob();
                }

                try
                {
                    reader = new IteratingPaDELReader(fMolecule);
                }
                catch (Exception ex)
                {
                    // Cannot read molecule file.
                    Logger.getLogger("global").log(Level.SEVERE, fMolecule.getName(), ex);
                    reader = null;
                    return hasNextJob();
                }

                PaDELReaderCount = 0;
                if (!PaDELReaderHasNext())
                {
                    return hasNextJob();
                }
                return true;
            }
            else
            {                
                return false;
            }
        }
        return true;
    }

    private boolean PaDELReaderHasNext()
    {
        if (reader.hasNext())
        {
            // Still have another molecule to read in file.
            ++PaDELReaderCount;
            ++progress;
            molStructure = (IAtomContainer)reader.next();

            // Get molecule name
            StringBuffer name = new StringBuffer();
            name.setLength(0);
            if (useFilenameAsMolName)
            {
                int lastIndex = fMolecules[curMolFileIndex].getName().lastIndexOf(".");
                if (lastIndex!=-1)
                {
                    name.append(fMolecules[curMolFileIndex].getName().substring(0, lastIndex));
                }
                else
                {
                    name.append(fMolecules[curMolFileIndex].getName());
                }
                if (PaDELReaderCount>1 || reader.hasNext()) name.append("_" + PaDELReaderCount);
            }
            else
            {
                if (molStructure.getProperty("cdk:Title")!=null &&
                    !((String)molStructure.getProperty("cdk:Title")).trim().isEmpty())
                {
                   name.append(molStructure.getProperty("cdk:Title"));
                }
                else
                {
                    int lastIndex = fMolecules[curMolFileIndex].getName().lastIndexOf(".");
                    if (lastIndex!=-1)
                    {
                        name.append("AUTOGEN_" + fMolecules[curMolFileIndex].getName().substring(0, lastIndex));
                    }
                    else
                    {
                        name.append("AUTOGEN_" + fMolecules[curMolFileIndex].getName());
                    }
                    if (PaDELReaderCount>1 || reader.hasNext()) name.append("_" + PaDELReaderCount);
                }
            }
            molName = name.toString();

            return true;
        }
        else
        {
            try
            {
                reader.close();
                reader = null;
            } 
            catch (IOException ex)
            {
                Logger.getLogger("global").log(Level.FINE, "Cannot close IteratingPaDELReader", ex);
            }
            return false;
        }
    }

    private libPaDELDescriptorJob nextJob()
    {
        // Create next job for workers.
        libPaDELDescriptorJob job = new libPaDELDescriptorJob(progress, fMolecules[curMolFileIndex].getName(), molName, molStructure, removeSalt, detectAromaticity, standardizeTautomers, tautomerList, standardizeNitro, retain3D, convertTo3D, forcefield, maxRunTime);
        return job;
    }

    @Override
    public void AddJobsToQueue()
    {
        // Calculate the maximum number of jobs that can be added to queue without causing blocking in Master.
        int maxJobsToAdd = maxJobsWaiting - jobsWaiting.size();
        libPaDELDescriptorJob job = new libPaDELDescriptorJob(NO_MORE_JOBS);
        for (int i=0; i<maxJobsToAdd; ++i)
        {
            try
            {
                if (hasNextJob())
                {
                    job = nextJob();
                    jobsWaiting.put(job);
                }
                else
                {
                    // No more new jobs so add marker to tell workers to stop.
                    for (int w=0; w<workers.size()*2; ++w)
                    {
                        jobsWaiting.offer(new libPaDELDescriptorJob(NO_MORE_JOBS));
                    }
                    break;
                }
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger("global").log(Level.SEVERE, Integer.toString(job.getId()), ex);
            }
        }
    }

    @Override
    public void ProcessRunningJobs()
    {
        // Get status from workers.
        status = clearMessages();
        if (!status.isEmpty()) log.Write(status);
    }
    
    @Override
    public void ProcessCompletedJobs()
    {
        // Save descriptors.
        libPaDELDescriptorJob job;
        while ((job=jobsCompleted.poll())!=null)
        {
            ++molProcessed;
            StoreAndSaveDescriptors(job);
            if (jobsWaiting.size()<maxJobsWaiting/10 && reader!=null)
            {
                // Waiting job queue is getting low.
                // Stop processing completed job so that Master has a chance to add jobs to the queue.
                return;
            }
        }

        // Check if workers have stopped working suddenly.
        boolean hasWorkersStopped = true;
        for (libPaDELDescriptorWorker worker : workers)
        {
            if (worker.isAlive()) hasWorkersStopped = false;
        }
        if (molProcessed==maxMolecules)
        {
            // All molecules processed. So stop Master.
            if (retainOrder && savedMoleculesIndex<maxMolecules)
            {
                while (completedJobs[savedMoleculesIndex]!=null)
                {
                    SaveDescriptors(completedJobs[savedMoleculesIndex]);
                    completedJobs[savedMoleculesIndex] = null;
                    ++savedMoleculesIndex;
                }
            }

            endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            String timeTaken = TimeTaken(startTime, endTime);
            double speed = totalTime / 1e9 / molProcessed;
            status = "Descriptor calculation completed in " + timeTaken + ". Average speed: " + new DecimalFormat("#0.00").format(speed) + " s/mol.";
            log.Write(status + "\n");
            log.Close();
            results.Close();
            hasWork = false;
            StopAllWorkers();
        }
        else if (hasWorkersStopped)
        {
            // All workers have somehow stopped..
            endTime = System.nanoTime();
            status = "All workers stopped for unknown reason.";
            log.Write(status + "\n");
            log.Close();
            results.Close();
            hasWork = false;            
        }
        else
        {
            // Still have jobs to be done.
            if (jobsCompleted.isEmpty())
            {
                try
                {
                    // Purposely look at one more job in order to block Master.
                    // Set a time-out just in case all workers stop working suddenly.
                    job = jobsCompleted.poll(1, TimeUnit.SECONDS);
                    if (job!=null)
                    {
                        ++molProcessed;
                        StoreAndSaveDescriptors(job);
                    }
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger("global").log(Level.SEVERE, Integer.toString(job.getId()), ex);
                }
            }
        }
    }

    private void StoreAndSaveDescriptors(libPaDELDescriptorJob job)
    {
        // Save descriptors
        if (retainOrder)
        {
            completedJobs[job.getId()-1] = job;
            while (savedMoleculesIndex<maxMolecules && completedJobs[savedMoleculesIndex]!=null)
            {
                SaveDescriptors(completedJobs[savedMoleculesIndex]);
                completedJobs[savedMoleculesIndex] = null;
                ++savedMoleculesIndex;
            }
        }
        else SaveDescriptors(job);
    }

    private void SaveDescriptors(libPaDELDescriptorJob job)
    {
        // Save descriptors
        results.Write("\"" + job.getName() + "\"");
        ArrayList<String> descriptors = job.getDescriptors();
        for (String value : descriptors)
        {
            results.Write("," + value);
        }
        results.Write("\n");

        if (maxMolPerFile>0 && molProcessed%maxMolPerFile==0 && molProcessed<maxMolecules)
        {
            // Maximum number of descriptors reached in descriptor file.
            // Create new log and descriptor file.
            ++curResultsFileCount;
            curResultsFile = resultsFileName + "_" + curResultsFileCount + resultsFileExt;
            curLogFile = resultsFile + "_" + curResultsFileCount + ".log";

            log.Open(curLogFile, logResults);

            results.Open(curResultsFile);
            results.Write("Name");
            for (String descriptor : descriptorNames)
            {
                results.Write(",");
                results.Write(descriptor);
            }
            results.Write("\n");
        }
    }

    @Override
    public void StopAllWorkers()
    {
        super.StopAllWorkers();
        if (workers==null || jobsWaiting==null) return;
        for (int w=0; w<workers.size()*2; ++w)
        {
            jobsWaiting.offer(new libPaDELDescriptorJob(NO_MORE_JOBS));
        }
    }

    public List<String> getDescriptorNames()
    {
        return descriptorNames;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public int getMolProcessed()
    {
        return molProcessed;
    }

    public int GetMaxMolecules()
    {
        if (maxMolecules == -1)
        {
            maxMolecules = 0;
            File dirFile = new File(moleculeDirFile);
            if (dirFile.isDirectory())
            {
                for (File fMolecule : dirFile.listFiles())
                {
                    if (fMolecule.getName().equals(".") || fMolecule.getName().equals("..") || fMolecule.isDirectory())
                    {
                        continue;
                    }

                    maxMolecules += GetMaxMoleculesInFile(fMolecule);
                }
            }
            else maxMolecules += GetMaxMoleculesInFile(dirFile);
        }
        return maxMolecules;
    }

    public int GetMaxMoleculesInFile(File fMolecule)
    {
        int maxMoleculesInFile = 0;
        IteratingPaDELReader lreader = null;
        try
        {
            lreader = new IteratingPaDELReader(fMolecule);
            while (lreader.hasNext())
            {
                lreader.next();
                ++maxMoleculesInFile;
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger("global").log(Level.FINE, "Cannot read from IteratingPaDELReader", ex);
        }
        finally
        {
            if (lreader!=null)
            {
                try
                {
                    lreader.close();
                }
                catch (IOException ex1)
                {
                    Logger.getLogger("global").log(Level.FINE, "Cannot close IteratingPaDELReader", ex1);
                }
            }
        }
        return maxMoleculesInFile;
    }

    private String TimeTaken(long startTime, long endTime)
    {
        long totalTime = endTime - startTime;
        int msec = (int)((((double)totalTime/1e9) - (int)((double)totalTime/1e9))*1000);
        int sec = (int) ((totalTime / 1e9) % 60);
        int min = (int) ((totalTime / 1e9 / 60) % 60);
        int hours = (int) ((totalTime / 1e9 / 60 / 60) % 24);
        int days = (int) ((totalTime / 1e9 / 60 / 60 / 24) % 7);
        int weeks = (int) (totalTime / 1e9 / 60 / 60 / 24 / 7);
        StringBuffer timeTaken = new StringBuffer();
        if (weeks > 0)
        {
            timeTaken.append(weeks + " weeks ");
        }
        if (days > 0)
        {
            timeTaken.append(days + " days ");
        }
        if (hours > 0)
        {
            timeTaken.append(hours + " hours ");
        }
        if (min > 0)
        {
            timeTaken.append(min + " mins ");
        }
        timeTaken.append(sec + "." + msec + " secs ");
        return timeTaken.toString();
    }
}

