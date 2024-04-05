/*
 * PaDELDescriptorApp.java
 */

package padeldescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import libpadeldescriptor.PaDELStandardize;
import libpadeldescriptor.libPaDELDescriptorJob;
import libpadeldescriptor.libPaDELDescriptorMaster;
import libpadeldescriptor.libPaDELDescriptorType;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

/**
 * The main class of the application.
 */
public class PaDELDescriptorApp extends SingleFrameApplication {

    private StartTask start;
    private Options options = new Options();
    private String directory;
    private String descriptorFile;
    private boolean compute2D = false;
    private boolean compute3D = false;
    private boolean computeFingerprints = false;
    private boolean removeSalt = true;
    private boolean detectAromaticity = true;
    private boolean standardizeTautomers = false;
    private String[] tautomerList = null;
    private boolean standardizeNitro = true;
    private boolean retain3D = false;
    private boolean convert3D = false;
    private String forceField = libPaDELDescriptorJob.MM2;
    private boolean logResults = false;
    private int maxThreads = -1;
    private int maxJobsWaiting = -1;
    private long maxRunTime = -1;
    private int maxCpdPerFile = 0;
    private boolean retainOrder = true;
    private boolean useFilenameAsMolName = false;

    // Variables for commandline
    private String config;
    private String descriptortypes;

    @SuppressWarnings("static-access")
    public PaDELDescriptorApp()
    {
        options.addOption(OptionBuilder.withArgName("config").hasArg().withDescription("Configuration file").create("config"));
        options.addOption(OptionBuilder.withArgName("descriptortypes").hasArg().withDescription("Descriptor types file").create("descriptortypes"));
        options.addOption(OptionBuilder.withArgName("directory").hasArg().withDescription("Set directory containing structural files").create("dir"));
        options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("Set file to save calculated descriptors").create("file"));
        options.addOption("2d", false, "Calculate 1D and 2D descriptors");
        options.addOption("3d", false, "Calculate 3D descriptors");
        options.addOption("fingerprints", false, "Calculate fingerprints");
        options.addOption("removesalt", false, "Remove salt from molecule");
        options.addOption("detectaromaticity", false, "Remove existing aromaticity information and automatically detect aromaticity in the molecule before calculation of descriptors");
        options.addOption("standardizetautomers", false, "Standardize tautomers");
        options.addOption(OptionBuilder.withArgName("tautomerlist").hasArg().withDescription("SMIRKS tautomers file").create("tautomerlist"));
        options.addOption("standardizenitro", false, "Standardize nitro groups to N(:O):O");
        options.addOption("retain3d", false, "Retain 3D coordinates when standardizing structure. However, this may prevent some structures from being standardized");
        options.addOption("convert3d", false, "Convert molecule to 3D");
        options.addOption("log", false, "Create a log file.\nName of log file is the name of the descriptors file with a .log extension.");
        options.addOption(OptionBuilder.withArgName("threads").hasArg().withDescription("Maximum number of threads to use. Use -1 to use as many threads as the number of cpu cores").create("threads"));
        options.addOption(OptionBuilder.withArgName("waitingjobs").hasArg().withDescription("Maximum number of jobs to store in queue for worker threads to process. Use -1 to set it to 50*Max threads.").create("waitingjobs"));
        options.addOption(OptionBuilder.withArgName("maxruntime").hasArg().withDescription("Maximum running time per molecule (in milliseconds). Use -1 for unlimited.").create("maxruntime"));
        options.addOption(OptionBuilder.withArgName("maxcpdperfile").hasArg().withDescription("Maximum number of compounds to be stored in each descriptor file. Use 0 for unlimited").create("maxcpdperfile"));
        options.addOption("retainorder", false, "Retain order of molecules in structural files for descriptor file. This may lead to large memory use if descriptor calculations are stuck at one molecule as the others will not be written to file and cleared from memory");
        options.addOption("usefilenameasmolname", false, "Use filename (minus the extension) as molecule name");
        options.addOption("help", false, "Print this message");
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new PaDELDescriptorView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of PaDELDescriptorApp
     */
    public static PaDELDescriptorApp getApplication() {
        return Application.getInstance(PaDELDescriptorApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            launch(PaDELDescriptorApp.class, args);
        }
        else
        {
            PaDELDescriptorApp.getApplication().launchCommandLine(args);
        }
    }

    public void launchCommandLine(String[] args)
    {
        CommandLineParser parser = new GnuParser();
        try
        {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help"))
            {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar PaDEL-Descriptor.jar", options);
            }
            else
            {
                config = line.getOptionValue("config");
                if (config!=null)
                {
                    // Configuration file specified. Use options from configuration file.
                    try
                    {
                        Properties configFile = new Properties();
                        FileInputStream fis = new FileInputStream(config);
                        configFile.load(fis);

                        directory = configFile.getProperty("Directory");
                        descriptorFile = configFile.getProperty("DescriptorFile");
                        compute2D = Boolean.valueOf(configFile.getProperty("Compute2D"));
                        compute3D = Boolean.valueOf(configFile.getProperty("Compute3D"));
                        computeFingerprints = Boolean.valueOf(configFile.getProperty("ComputeFingerprints"));
                        removeSalt = Boolean.valueOf(configFile.getProperty("RemoveSalt"));
                        detectAromaticity = Boolean.valueOf(configFile.getProperty("DetectAromaticity"));
                        standardizeTautomers = Boolean.valueOf(configFile.getProperty("StandardizeTautomers"));
                        standardizeNitro = Boolean.valueOf(configFile.getProperty("StandardizeNitro"));
                        retain3D = Boolean.valueOf(configFile.getProperty("Retain3D"));
                        String tautomerFile = configFile.getProperty("TautomerFile");
                        if (tautomerFile!=null && !tautomerFile.trim().isEmpty())
                        {
                            tautomerList = PaDELStandardize.getTautomerList(new File(tautomerFile));
                        }
                        String temp = configFile.getProperty("Convert3D");
                        if (temp.equals(PaDELDescriptorPropertySheetPage1.FORCEFIELD_NO))
                        {
                            convert3D = false;
                        }
                        else
                        {
                            convert3D = true;
                            if (temp.equals(PaDELDescriptorPropertySheetPage1.FORCEFIELD_MM2))
                            {
                                forceField = libPaDELDescriptorJob.MM2;
                            }
                            else
                            {
                                forceField = libPaDELDescriptorJob.MMFF94;
                            }
                        }
                        logResults = Boolean.valueOf(configFile.getProperty("Log"));
                        maxThreads = Integer.valueOf(configFile.getProperty("MaxThreads"));
                        maxJobsWaiting = Integer.valueOf(configFile.getProperty("MaxJobsWaiting"));
                        maxRunTime = Long.valueOf(configFile.getProperty("MaxRunTime"));
                        maxCpdPerFile = Integer.valueOf(configFile.getProperty("MaxCpdPerFile"));
                        retainOrder = Boolean.valueOf(configFile.getProperty("RetainOrder"));
                        useFilenameAsMolName = Boolean.valueOf(configFile.getProperty("UseFilenameAsMolName"));
                        fis.close();
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Cannot load configuration");
                        return;
                    }
                }
                else
                {
                    compute2D = false;
                    compute3D = false;
                    computeFingerprints = false;
                    removeSalt = false;
                    detectAromaticity = false;
                    standardizeTautomers = false;
                    standardizeNitro = false;
                    retain3D = false;
                    convert3D = false;
                    logResults = false;
                    retainOrder = false;
                    useFilenameAsMolName = false;      
                    
                    // Configuration file not specified. Use provided options
                    directory = line.getOptionValue("dir");
                    descriptorFile = line.getOptionValue("file");
                    if (line.hasOption("2d")) 
		    {
                        compute2D = true;
                    }		    
                    if (line.hasOption("3d"))
                    {
                        compute3D = true;
                    }
                    if (line.hasOption("fingerprints"))
                    {
                        computeFingerprints = true;
                    }
                    if (line.hasOption("removesalt"))
                    {
                        removeSalt = true;
                    }
                    if (line.hasOption("detectaromaticity"))
                    {
                        detectAromaticity = true;
                    }
                    if (line.hasOption("standardizetautomers"))
                    {
                        standardizeTautomers = true;
                    }
                    String tautomerFile = line.getOptionValue("tautomerlist");
                    if (tautomerFile!=null && !tautomerFile.trim().isEmpty())
                    {
                        tautomerList = PaDELStandardize.getTautomerList(new File(tautomerFile));
                    }
                    if (line.hasOption("standardizenitro"))
                    {
                        standardizeNitro = true;
                    }
                    if (line.hasOption("retain3d"))
                    {
                        retain3D = true;
                    }
                    if (line.hasOption("convert3d"))
                    {
                        convert3D = true;
                    }
                    if (line.hasOption("log"))
                    {
                        logResults = true;
                    }
                    if (line.hasOption("threads"))
                    {
                        maxThreads = Integer.valueOf(line.getOptionValue("threads"));
                    }
                    if (line.hasOption("waitingjobs"))
                    {
                        maxJobsWaiting = Integer.valueOf(line.getOptionValue("waitingjobs"));
                    }
                    if (line.hasOption("maxruntime"))
                    {
                        maxRunTime = Long.valueOf(line.getOptionValue("maxruntime"));
                    }
                    if (line.hasOption("maxcpdperfile"))
                    {
                        maxCpdPerFile = Integer.valueOf(line.getOptionValue("maxcpdperfile"));
                    }
                    if (line.hasOption("retainorder"))
                    {
                        retainOrder = true;
                    }
                    if (line.hasOption("usefilenameasmolname"))
                    {
                        useFilenameAsMolName = true;
                    }
                }
                descriptortypes = line.getOptionValue("descriptortypes");

                StartCmd();
            }
        }
        catch (ParseException ex)
        {
            Logger.getLogger("global").log(Level.WARNING, ex.getMessage());
        }
    }

    private void StartCmd()
    {
        libPaDELDescriptorMaster master = new libPaDELDescriptorMaster(directory, descriptorFile, compute2D, compute3D, computeFingerprints, removeSalt, detectAromaticity, standardizeTautomers, standardizeNitro, retain3D, convert3D);

        String errorMessage = master.CheckRequirements();
        if (errorMessage!=null && !errorMessage.isEmpty())
        {
            System.out.println(errorMessage);
            return;
        }

        try
        {
            if (descriptortypes!=null)
            {
                try
                {
                    master.SetDescriptorTypes(descriptortypes);
                }
                catch (Exception ex1)
                {
                    Logger.getLogger("global").log(Level.SEVERE, "Cannot read in descriptor types file", ex1);
                    System.out.println("Cannot read in descriptor types file.");
                    return;
                }
            }
            else master.SetDescriptorTypes("descriptors.xml");
        }
        catch (Exception ex)
        {
            try
            {
                master.SetDescriptorTypes(this.getClass().getClassLoader().getResourceAsStream("META-INF/descriptors.xml"));
            }
            catch (Exception ex1)
            {
                Logger.getLogger("global").log(Level.SEVERE, "Cannot read in descriptor types file", ex1);
                System.out.println("Cannot read in descriptor types file.");
                return;
            }
        }
        if (maxThreads>0) master.setMaxThreads(maxThreads);
        if (maxJobsWaiting>0) master.setMaxJobsWaiting(maxJobsWaiting);
        if (maxRunTime>0) master.setMaxRunTime(maxRunTime);
        master.setMaxMolPerFile(maxCpdPerFile);
        master.setLogResults(logResults);
        master.setRetainOrder(retainOrder);
        master.setUseFilenameAsMolName(useFilenameAsMolName);

        if (tautomerList!=null)
        {
            master.setTautomerList(tautomerList);
        }
        else
        {
            try
            {
                master.setTautomerList(PaDELStandardize.getTautomerList(this.getClass().getClassLoader().getResourceAsStream("META-INF/tautomerlist.txt")));
            }
            catch (Exception ex1)
            {
                Logger.getLogger("global").log(Level.SEVERE, "Cannot read in SMIRKS tautomers file", ex1);
                System.out.println("Cannot read in SMIRKS tautomers file.");
                return;
            }
        }

        master.setForcefield(forceField);

        errorMessage = master.Initialize();
        if (errorMessage!=null && !errorMessage.isEmpty())
        {
            System.out.println(errorMessage);
            return;
        }

        while (master.HasWork())
        {
            String status = master.getStatus();
            if (!status.isEmpty()) System.out.print(status);
            master.DoWork();
        }
        String status = master.getStatus();
        if (!status.isEmpty()) System.out.println(status);
    }

    @Action
    public Task Start() {
        if (start == null || start.isDone())
        {
            start = new StartTask(org.jdesktop.application.Application.getInstance(padeldescriptor.PaDELDescriptorApp.class));
        }
        else
        {
            start.Stop();
            start = null;
        }
        return start;
    }

    private class StartTask extends org.jdesktop.application.Task<Object, Void> {

        private boolean wantsToRun = true;
        private libPaDELDescriptorMaster master;

        StartTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to StartTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.

            PaDELDescriptorApp app = (PaDELDescriptorApp)this.getApplication();
            PaDELDescriptorView view = (PaDELDescriptorView)app.getMainView();

            directory = view.config.data.getDirectory();
            descriptorFile = view.config.data.getDescriptorFile();
            compute2D = view.config.data.isCompute2D();
            compute3D = view.config.data.isCompute3D();
            computeFingerprints = view.config.data.isComputeFingerprints();
            removeSalt = view.config.data.isRemoveSalt();
            detectAromaticity = view.config.data.isDetectAromaticity();
            standardizeTautomers = view.config.data.isStandardizeTautomers();
            String tautomerFile = view.config.data.getTautomerFile();
            if (tautomerFile!=null && !tautomerFile.trim().isEmpty())
            {
                tautomerList = PaDELStandardize.getTautomerList(new File(tautomerFile));
            }
            else
            {
                try
                {
                    tautomerList = PaDELStandardize.getTautomerList(this.getClass().getClassLoader().getResourceAsStream("META-INF/tautomerlist.txt"));
                }
                catch (Exception ex1)
                {
                    Logger.getLogger("global").log(Level.SEVERE, "Cannot read in SMIRKS tautomers file", ex1);
                }
            }
            standardizeNitro = view.config.data.isStandardizeNitro();
            retain3D = view.config.data.isRetain3D();
            String temp = view.config.data.getConvert3D();
            if (temp.equals(PaDELDescriptorPropertySheetPage1.FORCEFIELD_NO))
            {
                convert3D = false;
            }
            else
            {
                convert3D = true;
                if (temp.equals(PaDELDescriptorPropertySheetPage1.FORCEFIELD_MM2))
                {
                    forceField = libPaDELDescriptorJob.MM2;
                }
                else
                {
                    forceField = libPaDELDescriptorJob.MMFF94;
                }
            }
            logResults = view.config.data.isLog();
            maxThreads = view.config.data.getMaxThreads();
            maxJobsWaiting = view.config.data.getMaxJobsWaiting();
            maxRunTime = view.config.data.getMaxRunTime();
            maxCpdPerFile = view.config.data.getMaxCpdPerFile();
            retainOrder = view.config.data.isRetainOrder();
            useFilenameAsMolName = view.config.data.isUseFilenameAsMolName();

            master = new libPaDELDescriptorMaster(directory, descriptorFile, compute2D, compute3D, computeFingerprints, removeSalt, detectAromaticity, standardizeTautomers, standardizeNitro, retain3D, convert3D);

            String errorMessage = master.CheckRequirements();
            if (errorMessage!=null && !errorMessage.isEmpty()) return errorMessage;

            master.SetDescriptorTypes(libPaDELDescriptorType.GetActiveDescriptorTypes(view.GetDescriptors(), compute2D, compute3D, computeFingerprints));

            if (maxThreads>0) master.setMaxThreads(maxThreads);
            if (maxJobsWaiting>0) master.setMaxJobsWaiting(maxJobsWaiting);
            if (maxRunTime>0) master.setMaxRunTime(maxRunTime);
            master.setMaxMolPerFile(maxCpdPerFile);
            master.setLogResults(logResults);
            master.setRetainOrder(retainOrder);
            master.setUseFilenameAsMolName(useFilenameAsMolName);
            master.setTautomerList(tautomerList);
            master.setForcefield(forceField);

            errorMessage = master.Initialize();
            if (errorMessage!=null && !errorMessage.isEmpty()) return errorMessage;

            view.start.setText("Stop");
            view.start.setToolTipText("Stop descriptor calculation");

            while (master.HasWork())
            {
                if (!wantsToRun)
                {
                    master.StopAllWorkers();
                    break;
                }

                String status = master.getMessagesWorker();
                status = status.replaceAll("\n", "<br>");
                status = "<html>" + status + "</html>";
                view.status.setText(status);
                setProgress(master.getMolProcessed(), 0, master.GetMaxMolecules());

                master.DoWork();
            }

            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            PaDELDescriptorApp app = (PaDELDescriptorApp)this.getApplication();
            PaDELDescriptorView view = (PaDELDescriptorView)app.getMainView();
            master.StopAllWorkers();
            if (result!=null && !((String)result).isEmpty())
            {
                String errorMessage = (String)result;
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                if (wantsToRun)
                {
                    long totalTime = master.getEndTime() - master.getStartTime();
                    double speed = totalTime / 1e9 / master.getMolProcessed();
                    String message = "Completed. Average speed: " + new DecimalFormat("#0.00").format(speed) + " s/mol.";
                    view.status.setText(message);

                    message = "";
                    message += master.getStatus();
                    JOptionPane.showMessageDialog(null, message, "Completed", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    view.status.setText("Cancelled");
                    JOptionPane.showMessageDialog(null, "Descriptor calculation cancelled", "Cancelled", JOptionPane.WARNING_MESSAGE);
                }
            }
            view.start.setText("Start");
            view.start.setToolTipText("Start descriptor calculations");
         }

        public void Stop()
        {
            wantsToRun = false;
        }
    }
}
