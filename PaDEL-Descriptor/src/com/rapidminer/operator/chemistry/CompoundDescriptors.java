/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2010 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package com.rapidminer.operator.chemistry;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.gui.wizards.MoleculeDescriptorsConfigurationWizardCreator;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeConfiguration;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import com.rapidminer.tools.Ontology;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import libpadeldescriptor.DescriptorStruct;
import libpadeldescriptor.PaDELStandardize;
import libpadeldescriptor.libPaDELDescriptorJob;
import libpadeldescriptor.libPaDELDescriptorMasterGeneral;
import libpadeldescriptor.libPaDELDescriptorType;
import padeldescriptor.PaDELDescriptorPropertySheetPage2;
import padeldescriptor.PaDELDescriptorPropertySheetPage3;
import padeldescriptor.PaDELDescriptorPropertySheetPage4;
import padeldescriptor.PaDELDescriptorView;

/*
    This is the RapidMiner operator for PaDEL-Descriptor. It uses the
    same descriptor calculation engine as PaDEL-Descriptor.
 */
public class CompoundDescriptors extends Operator
{
    public static final String PARAMETER_COMPUTE_2D = "compute_1D/2D";
    public static final String PARAMETER_COMPUTE_3D = "compute_3D";
    public static final String PARAMETER_COMPUTE_FINGERPRINTS = "compute_fingerprints";

    public static final String PARAMETER_USE_DESCRIPTOR_FILE = "use_descriptor_file";
    public static final String PARAMETER_DESCRIPTOR_FILE = "descriptor_file";

    public static final String PARAMETER_REMOVE_SALT = "remove_salt";
    public static final String PARAMETER_DETECT_AROMATICITY = "detect_aromaticity";
    public static final String PARAMETER_STANDARDIZE_TAUTOMERS = "standardize_tautomers";
    public static final String PARAMETER_TAUTOMER_FILE = "tautomer_file";
    public static final String PARAMETER_STANDARDIZE_NITRO = "standardize_nitro";
    public static final String PARAMETER_RETAIN_3D = "retain_3D";
    public static final String PARAMETER_CONVERT_3D = "convert_3D";
    public static final String[] convert3DStrings = { "No", "Yes (use MM2 forcefield)", "Yes (use MMFF94 forcefield)" };

    public static final String PARAMETER_LOG = "log_process";
    public static final String PARAMETER_LOG_FILE = "log_file";
    public static final String PARAMETER_MAX_THREADS = "max_threads";
    public static final String PARAMETER_MAX_WAITING_JOBS = "max_waiting_jobs";
    public static final String PARAMETER_MAX_RUN_TIME = "max_run_time";

    public static final String PARAMETER_HIDDEN_2D = "2D";
    public static final String PARAMETER_HIDDEN_3D = "3D";
    public static final String PARAMETER_HIDDEN_FINGERPRINTS = "fingerprints";
    public static final String PARAMETER_HIDDEN_2D_INACTIVE = "2DInactive";
    public static final String PARAMETER_HIDDEN_3D_INACTIVE = "3DInactive";
    public static final String PARAMETER_HIDDEN_FINGERPRINTS_INACTIVE = "fingerprintsInactive";

    protected final InputPort molInput = getInputPorts().createPort("mol");
    protected final OutputPort exampleSetOutput = getOutputPorts().createPort("exa");
    protected final OutputPort molOriginal = getOutputPorts().createPort("ori");

    public PaDELDescriptorPropertySheetPage2 descriptor2DConfig = new PaDELDescriptorPropertySheetPage2();
    public PaDELDescriptorPropertySheetPage3 descriptor3DConfig = new PaDELDescriptorPropertySheetPage3();
    public PaDELDescriptorPropertySheetPage4 fingerprintConfig = new PaDELDescriptorPropertySheetPage4();

    public CompoundDescriptors(OperatorDescription description)
    {
        super(description);

        getTransformer().addPassThroughRule(molInput, molOriginal);
    }

    @Override
    public List<ParameterType> getParameterTypes()
    {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = null;

        type = new ParameterTypeBoolean(PARAMETER_COMPUTE_2D, "Calculate 1D and 2D descriptors.", true);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_COMPUTE_3D, "Calculate 3D descriptors.", false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_COMPUTE_FINGERPRINTS, "Calculate fingerprints.", false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_USE_DESCRIPTOR_FILE, "Load descriptors types to calculate from file.", false);
        type.setExpert(false);
        types.add(type);
        
        type = new ParameterTypeFile(PARAMETER_DESCRIPTOR_FILE, "Filename of descriptor types file. This file can be generated using PaDEL-Descriptor.", "xml", true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_DESCRIPTOR_FILE, true, true));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeConfiguration(MoleculeDescriptorsConfigurationWizardCreator.class, this);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_DESCRIPTOR_FILE, true, false));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_REMOVE_SALT, "Remove salt from a molecule. This option assumes that the largest fragment is the desired molecule.", true);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_DETECT_AROMATICITY, "Remove existing aromaticity information and automatically detect aromaticity in the molecule before calculation of descriptors.", true);
        type.setExpert(false);
        types.add(type);
        
        type = new ParameterTypeBoolean(PARAMETER_STANDARDIZE_TAUTOMERS, "Standardize tautomers.", true);
        type.setExpert(false);
        types.add(type);       
        
        type = new ParameterTypeFile(PARAMETER_TAUTOMER_FILE, "File containing SMIRKS tautomers.", "txt", false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_STANDARDIZE_NITRO, "Standardize nitro groups to N(:O):O.", true);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_RETAIN_3D, "Retain 3D coordinates when standardizing structure. However, this may prevent some structures from being standardized.", false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeCategory(PARAMETER_CONVERT_3D, "Convert molecule to 3D.", convert3DStrings, 0);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_LOG, "Create a log file.", true);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeFile(PARAMETER_LOG_FILE, "The path to the log file.", null, true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_LOG, true, true));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_MAX_THREADS, "Maximum number of threads to use. Use -1 to use as many threads as the number of cpu cores.", -1, Integer.MAX_VALUE, -1);
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_MAX_WAITING_JOBS, "Maximum number of jobs to store in queue for worker threads to process. Use -1 to set it to 50*Max threads.", -1, Integer.MAX_VALUE, -1);
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_MAX_RUN_TIME, "Maximum running time per molecule (in milliseconds). Use -1 for unlimited.", -1, Integer.MAX_VALUE, -1);
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeString(PARAMETER_HIDDEN_2D, "2D", true);
        type.setExpert(true);
        type.setHidden(true);
        types.add(type);

        type = new ParameterTypeString(PARAMETER_HIDDEN_3D, "3D", true);
        type.setExpert(true);
        type.setHidden(true);
        types.add(type);

        type = new ParameterTypeString(PARAMETER_HIDDEN_FINGERPRINTS, "fingerprints", true);
        type.setExpert(true);
        type.setHidden(true);
        types.add(type);

        type = new ParameterTypeString(PARAMETER_HIDDEN_2D_INACTIVE, "2DInactive", true);
        type.setExpert(true);
        type.setHidden(true);
        types.add(type);

        type = new ParameterTypeString(PARAMETER_HIDDEN_3D_INACTIVE, "3DInactive", true);
        type.setExpert(true);
        type.setHidden(true);
        types.add(type);

        type = new ParameterTypeString(PARAMETER_HIDDEN_FINGERPRINTS_INACTIVE, "fingerprintsInactive", true);
        type.setExpert(true);
        type.setHidden(true);
        types.add(type);

        return types;
    }

    @Override
    public void doWork() throws OperatorException
    {
    	Compounds mols = molInput.getData();

        ArrayList<List<String> > results = new ArrayList<List<String> >();
        boolean compute2D = getParameterAsBoolean(PARAMETER_COMPUTE_2D);
        boolean compute3D = getParameterAsBoolean(PARAMETER_COMPUTE_3D);
        boolean computeFingerprints = getParameterAsBoolean(PARAMETER_COMPUTE_FINGERPRINTS);
        boolean removeSalt = getParameterAsBoolean(PARAMETER_REMOVE_SALT);
        boolean detectAromaticity = getParameterAsBoolean(PARAMETER_DETECT_AROMATICITY);
        boolean standardizeTautomers = getParameterAsBoolean(PARAMETER_STANDARDIZE_TAUTOMERS);
        boolean standardizeNitro = getParameterAsBoolean(PARAMETER_STANDARDIZE_NITRO);
        boolean retain3D = getParameterAsBoolean(PARAMETER_RETAIN_3D);

        boolean convert3D = false;
        String forceField = libPaDELDescriptorJob.MM2;
        int temp = getParameterAsInt(PARAMETER_CONVERT_3D);
        if (temp == 0)
        {
            convert3D = false;
        }
        else
        {
            convert3D = true;
            if (temp == 1)
            {
                forceField = libPaDELDescriptorJob.MM2;
            }
            else
            {
                forceField = libPaDELDescriptorJob.MMFF94;
            }
        }

        boolean logResults = getParameterAsBoolean(PARAMETER_LOG);
        String logFile = getParameterAsString(PARAMETER_LOG_FILE);
        
        libPaDELDescriptorMasterGeneral master = new libPaDELDescriptorMasterGeneral(mols.getMolecules(), results, compute2D, compute3D, computeFingerprints, removeSalt, detectAromaticity, standardizeTautomers, standardizeNitro, retain3D, convert3D, logFile);

        if (getParameterAsBoolean(PARAMETER_USE_DESCRIPTOR_FILE))
        {
            try
            {
                master.SetDescriptorTypes(getParameterAsString(PARAMETER_DESCRIPTOR_FILE));
            }
            catch (Exception ex)
            {
                Logger.getLogger("global").log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            SetDescriptors();
            Set<DescriptorStruct> descriptorTypes = PaDELDescriptorView.GetDescriptors(descriptor2DConfig, descriptor3DConfig, fingerprintConfig);
            master.SetDescriptorTypes(libPaDELDescriptorType.GetActiveDescriptorTypes(descriptorTypes, compute2D, compute3D, computeFingerprints));
        }
        
        String tautomerFile = getParameterAsString(PARAMETER_TAUTOMER_FILE);
        String[] tautomerList = null;
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
        master.setTautomerList(tautomerList);
        
        int maxThreads = getParameterAsInt(PARAMETER_MAX_THREADS);
        if (maxThreads>0) master.setMaxThreads(maxThreads);

        int maxJobsWaiting = getParameterAsInt(PARAMETER_MAX_WAITING_JOBS);
        if (maxJobsWaiting>0) master.setMaxJobsWaiting(maxJobsWaiting);
        
        long maxRunTime = getParameterAsInt(PARAMETER_MAX_RUN_TIME);
        if (maxRunTime>0) master.setMaxRunTime(maxRunTime);
        
        master.setLogResults(logResults);
        master.setForcefield(forceField);

        master.Initialize();
        while (master.HasWork())
        {
            master.DoWork();
        }

        // Create ExampleTable
        List<Attribute> attributes = new LinkedList<Attribute>();
        Attribute label = AttributeFactory.createAttribute("Compound", Ontology.NOMINAL);
        attributes.add(label);
        List<String> descriptorNames = results.get(0);
        for (int i=0, endi=descriptorNames.size(); i<endi; ++i)
        {
            attributes.add(AttributeFactory.createAttribute(descriptorNames.get(i), Ontology.REAL));
        }
        MemoryExampleTable exampleTable = new MemoryExampleTable(attributes);

        // Add data rows
        for (int i=1, endi=results.size(); i<endi; ++i)
        {
            List<String> descriptorValues = results.get(i);
            int maxDescriptors = descriptorValues.size() + 1;
            double[] data = new double[maxDescriptors];
            data[0] = exampleTable.findAttribute("Compound").getMapping().mapString(mols.getMoleculeName(i-1));
            for (int j=0, endj=descriptorValues.size(); j<endj; ++j)
            {
                if (!descriptorValues.get(j).equals(""))
                {
                    data[j+1] = Double.parseDouble(descriptorValues.get(j));
                    if (Double.isInfinite(data[j+1]))
                    {
                        data[j+1] = Double.NaN;
                    }
                }
                else
                {
                    data[j+1] = Double.NaN;
                }
            }
            exampleTable.addDataRow(new DoubleArrayDataRow(data));
        }
    	molOriginal.deliver(mols);
    	exampleSetOutput.deliver(exampleTable.createExampleSet(null, null, label));
    }

    public void SetDescriptors()
    {
        try
        {
            String descriptor2D = getParameterAsString(PARAMETER_HIDDEN_2D);
            String descriptor3D = getParameterAsString(PARAMETER_HIDDEN_3D);
            String fingerprint = getParameterAsString(PARAMETER_HIDDEN_FINGERPRINTS);
            String descriptor2DInactive = getParameterAsString(PARAMETER_HIDDEN_2D_INACTIVE);
            String descriptor3DInactive = getParameterAsString(PARAMETER_HIDDEN_3D_INACTIVE);
            String fingerprintInactive = getParameterAsString(PARAMETER_HIDDEN_FINGERPRINTS_INACTIVE);

            Method setMethod = null;
            Class params[] = { boolean.class };
            if (!descriptor2D.equals(""))
            {
                String[] descriptors = descriptor2D.split("\\|");
                for (int i=0, endi=descriptors.length; i<endi; ++i)
                try
                {
                    setMethod = descriptor2DConfig.data.getClass().getMethod("set" + descriptors[i], params);
                    if (setMethod!=null)
                    {
                        setMethod.invoke(descriptor2DConfig.data, new Object[]{true});
                        descriptor2DConfig.sheet.readFromObject(descriptor2DConfig.data);
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.SEVERE, "Cannot find descriptor type.", ex);
                }
            }

            if (!descriptor3D.equals(""))
            {
                String[] descriptors = descriptor3D.split("\\|");
                for (int i=0, endi=descriptors.length; i<endi; ++i)
                try
                {
                    setMethod = descriptor3DConfig.data.getClass().getMethod("set" + descriptors[i], params);
                    if (setMethod!=null)
                    {
                        setMethod.invoke(descriptor3DConfig.data, new Object[]{true});
                        descriptor3DConfig.sheet.readFromObject(descriptor3DConfig.data);
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.SEVERE, "Cannot find descriptor type.", ex);
                }
            }

            if (!fingerprint.equals(""))
            {
                String[] descriptors = fingerprint.split("\\|");
                for (int i=0, endi=descriptors.length; i<endi; ++i)
                try
                {
                    setMethod = fingerprintConfig.data.getClass().getMethod("set" + descriptors[i], params);
                    if (setMethod!=null)
                    {
                        setMethod.invoke(fingerprintConfig.data, new Object[]{true});
                        fingerprintConfig.sheet.readFromObject(fingerprintConfig.data);
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.SEVERE, "Cannot find descriptor type.", ex);
                }
            }

            if (!descriptor2DInactive.equals(""))
            {
                String[] descriptors = descriptor2DInactive.split("\\|");
                for (int i=0, endi=descriptors.length; i<endi; ++i)
                try
                {
                    setMethod = descriptor2DConfig.data.getClass().getMethod("set" + descriptors[i], params);
                    if (setMethod!=null)
                    {
                        setMethod.invoke(descriptor2DConfig.data, new Object[]{false});
                        descriptor2DConfig.sheet.readFromObject(descriptor2DConfig.data);
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.SEVERE, "Cannot find descriptor type.", ex);
                }
            }

            if (!descriptor3DInactive.equals(""))
            {
                String[] descriptors = descriptor3DInactive.split("\\|");
                for (int i=0, endi=descriptors.length; i<endi; ++i)
                try
                {
                    setMethod = descriptor3DConfig.data.getClass().getMethod("set" + descriptors[i], params);
                    if (setMethod!=null)
                    {
                        setMethod.invoke(descriptor3DConfig.data, new Object[]{false});
                        descriptor3DConfig.sheet.readFromObject(descriptor3DConfig.data);
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.SEVERE, "Cannot find descriptor type.", ex);
                }
            }

            if (!fingerprintInactive.equals(""))
            {
                String[] descriptors = fingerprintInactive.split("\\|");
                for (int i=0, endi=descriptors.length; i<endi; ++i)
                try
                {
                    setMethod = fingerprintConfig.data.getClass().getMethod("set" + descriptors[i], params);
                    if (setMethod!=null)
                    {
                        setMethod.invoke(fingerprintConfig.data, new Object[]{false});
                        fingerprintConfig.sheet.readFromObject(fingerprintConfig.data);
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger("global").log(Level.SEVERE, "Cannot find descriptor type.", ex);
                }
            }
        }
        catch (Exception e)
        {
        }
    }

    public void UpdateDescriptors()
    {
        String descriptor2D = new String();
        String descriptor3D = new String();
        String fingerprint = new String();
        String descriptor2DInactive = new String();
        String descriptor3DInactive = new String();
        String fingerprintInactive = new String();
        Set<DescriptorStruct> descriptors = PaDELDescriptorView.GetDescriptors(descriptor2DConfig, descriptor3DConfig, fingerprintConfig);
        for (DescriptorStruct descriptor : descriptors)
        {            
            String type = descriptor.getType();
            if (type.equals(DescriptorStruct.TYPE_2D))
            {
                if (descriptor.isActive())
                {
                    descriptor2D += descriptor.getName() + "|";
                }
                else
                {
                    descriptor2DInactive += descriptor.getName() + "|";
                }
            }

            if (type.equals(DescriptorStruct.TYPE_3D))
            {
                if (descriptor.isActive())
                {
                    descriptor3D += descriptor.getName() + "|";
                }
                else
                {
                    descriptor3DInactive += descriptor.getName() + "|";
                }
            }

            if (type.equals(DescriptorStruct.TYPE_FINGERPRINT))
            {
                if (descriptor.isActive())
                {
                    fingerprint += descriptor.getName() + "|";
                }
                else
                {
                    fingerprintInactive += descriptor.getName() + "|";
                }
            }
        }
        this.setParameter(PARAMETER_HIDDEN_2D, descriptor2D);
        this.setParameter(PARAMETER_HIDDEN_3D, descriptor3D);
        this.setParameter(PARAMETER_HIDDEN_FINGERPRINTS, fingerprint);
        this.setParameter(PARAMETER_HIDDEN_2D_INACTIVE, descriptor2DInactive);
        this.setParameter(PARAMETER_HIDDEN_3D_INACTIVE, descriptor3DInactive);
        this.setParameter(PARAMETER_HIDDEN_FINGERPRINTS_INACTIVE, fingerprintInactive);
    }
}