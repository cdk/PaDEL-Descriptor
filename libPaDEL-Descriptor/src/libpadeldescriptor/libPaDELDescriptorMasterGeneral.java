package libpadeldescriptor;

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
public class libPaDELDescriptorMasterGeneral extends Master<libPaDELDescriptorJob, libPaDELDescriptorWorker>
{
    // Variables to set before running descriptor calculation
    private ArrayList<IAtomContainer> mols;
    private boolean compute2D = false;  // Calculate 1D, 2D descriptors.
    private boolean compute3D = false;  // Calculate 3D descriptors.
    private boolean computeFingerprints = false;    // Calculate fingerprint.
    private boolean removeSalt = true; // If true, remove salts like Na, Cl from the molecule before calculation of descriptors.
    private boolean detectAromaticity = true; // If true, remove existing aromaticity information and automatically detect aromaticity in the molecule before calculation of descriptors.
    private boolean standardizeTautomers = true;    // If true, standardize tautomers.
    private boolean standardizeNitro = true;    // If true, standardize nitro groups to N(:O):O. 
    private boolean retain3D = false;   // If true, retain 3D coordinates when standardizing structure. However, this may prevent some structures from being standardized.
    private boolean convertTo3D = false; // If true, convert molecule to 3D before calculation of descriptors.
    private ArrayList<List<String> > results;  // File to save log
    private String logFile;  // File to save log

    // Optional variables
    private String forcefield = libPaDELDescriptorJob.MM2; // Forcefield to use for converting molecules to 3D.
    private String[] tautomerList;    // SMIRKS list to convert tautomers.
    private boolean logResults = false; // Log results.
    private long maxRunTime = -1;   // Maximum running time in milliseconds for calculation of descriptors for a single molecule. If <=0, means no restriction on running time.

    // Variables containing progress of descriptor calculation
    private int curMolIndex = -1; // Current molecule file index. Also used as job id (curMolIndex+1)
    private int molProcessed = 0;   // Number of molecules that has completed descriptor calculation.

    // General variables
    private PaDELLogFile log;
    private long startTime;
    private long endTime;
    private int maxMolecules = -1;
    private List<String> descriptorNames;
    private Set<String> descriptorTypes;

    // Variables for each iteration
    private String molName;
    private IAtomContainer molStructure;

    public libPaDELDescriptorMasterGeneral(ArrayList<IAtomContainer> mols,
                                           ArrayList<List<String> > results,
                                           boolean compute2D,
                                           boolean compute3D,
                                           boolean computeFingerprints,
                                           boolean removeSalt,                                    
                                           boolean detectAromaticity,
                                           boolean standardizeTautomers,
                                           boolean standardizeNitro,
                                           boolean retain3D,
                                           boolean convertTo3D,                                           
                                           String logFile)
    {
        super();
        this.mols = mols;
        this.results = results;
        this.compute2D = compute2D;
        this.compute3D = compute3D;
        this.computeFingerprints = computeFingerprints;
        this.removeSalt = removeSalt;
        this.detectAromaticity = detectAromaticity;
        this.standardizeTautomers = standardizeTautomers;
        this.standardizeNitro = standardizeNitro;
        this.retain3D = retain3D;
        this.convertTo3D = convertTo3D;
        this.logFile = logFile;
        descriptorNames = new ArrayList<String>();
        log = new PaDELLogFile();
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

    public void setLogResults(boolean logResults)
    {
        this.logResults = logResults;
    }
    
    public void setMaxRunTime(long maxRunTime)
    {
        this.maxRunTime = maxRunTime;
    }

    @Override
    public String Initialize()
    {
        super.Initialize();

        startTime = System.nanoTime();
        maxMolecules = mols.size();

        // Prepare log file.
        String error = log.Open(logFile, logResults);
        if (error!=null) return error;

        results.clear();
        results.add(descriptorNames);
        ArrayList<String> temp = new ArrayList<String>();
        for (int i=0, endi=maxMolecules; i<endi; ++i)
        {
            // Prepare results to hold descriptors for all molecules.
            // Note, it does not matter that all the results are initialized to
            // the same array reference as each will be replaced later with an unique array.
            results.add(temp);
        }

        curMolIndex = -1;
        molProcessed = 0;

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
        if (++curMolIndex < maxMolecules)
        {
            molName = (String)mols.get(curMolIndex).getProperty("cdk:Title");
            molStructure = mols.get(curMolIndex);
            return true;
        }
        else return false;
    }

    private libPaDELDescriptorJob nextJob()
    {
        // Create next job for workers.
        libPaDELDescriptorJob job = new libPaDELDescriptorJob(curMolIndex+1, null, molName, molStructure, removeSalt, detectAromaticity, standardizeTautomers, tautomerList, standardizeNitro, retain3D, convertTo3D, forcefield, maxRunTime);
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
            results.set(job.getId(), job.getDescriptors());
            if (jobsWaiting.size()<maxJobsWaiting/10 && curMolIndex < maxMolecules)
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
            endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            String timeTaken = TimeTaken(startTime, endTime);
            double speed = totalTime / 1e9 / molProcessed;
            status = "Descriptor calculation completed in " + timeTaken + ". Average speed: " + new DecimalFormat("#0.00").format(speed) + " s/mol.";
            log.Write(status + "\n");
            log.Close();
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
                        results.set(job.getId(), job.getDescriptors());
                    }
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger("global").log(Level.SEVERE, Integer.toString(job.getId()), ex);
                }
            }
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
            maxMolecules = mols.size();
        }
        return maxMolecules;
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

