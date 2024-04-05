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

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeFile;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import libpadeldescriptor.PaDELStandardize;
import org.openscience.cdk.interfaces.IAtomContainer;

/*
    This operator standardizes a molecule.
 */
public class CompoundStandardize extends CompoundAbstractProcessing
{
    public static final String PARAMETER_REMOVE_SALT = "remove_salt";
    public static final String PARAMETER_DETECT_AROMATICITY = "detect_aromaticity";
    public static final String PARAMETER_STANDARDIZE_TAUTOMERS = "standardize_tautomers";
    public static final String PARAMETER_TAUTOMER_FILE = "tautomer_file";
    public static final String PARAMETER_STANDARDIZE_NITRO = "standardize_nitro";
    public static final String PARAMETER_RETAIN_3D = "retain_3D";

    public CompoundStandardize(OperatorDescription description)
    {
        super(description);
    }
    
    @Override
    public List<ParameterType> getParameterTypes()
    {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = null;

        type = new ParameterTypeBoolean(PARAMETER_REMOVE_SALT, "Remove salt from a molecule. This option assumes that the largest fragment is the desired molecule.", true);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_DETECT_AROMATICITY, "Remove existing aromaticity information and automatically detect aromaticity in the molecule before calculation of descriptors.", true);
        type.setExpert(false);
        types.add(type);
        
        type = new ParameterTypeBoolean(PARAMETER_STANDARDIZE_TAUTOMERS, "Standardize tautomers.", true);
        type.setExpert(false);
        types.add(type);       
        
        type = new ParameterTypeFile(PARAMETER_TAUTOMER_FILE, "File containing SMIRKS tautomers. Leave blank if using default settings.", "txt", true);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_STANDARDIZE_NITRO, "Standardize nitro groups to N(:O):O.", true);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_RETAIN_3D, "Retain 3D coordinates when standardizing structure. However, this may prevent some structures from being standardized.", false);
        type.setExpert(false);
        types.add(type);

        return types;
    }

    @Override
    public void doWork() throws OperatorException
    {
    	Compounds mols = molInput.getData();
        Compounds ori = new Compounds();
        
        boolean removeSalt = getParameterAsBoolean(PARAMETER_REMOVE_SALT);
        boolean detectAromaticity = getParameterAsBoolean(PARAMETER_DETECT_AROMATICITY);
        boolean standardizeTautomers = getParameterAsBoolean(PARAMETER_STANDARDIZE_TAUTOMERS);
        boolean standardizeNitro = getParameterAsBoolean(PARAMETER_STANDARDIZE_NITRO);
        boolean retain3D = getParameterAsBoolean(PARAMETER_RETAIN_3D);
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
            
        PaDELStandardize standardize = new PaDELStandardize();
        standardize.setRemoveSalt(removeSalt);
        standardize.setDearomatize(detectAromaticity);
        standardize.setStandardizeTautomers(standardizeTautomers);
        standardize.setTautomerList(tautomerList);
        standardize.setStandardizeNitro(standardizeNitro);
        standardize.setRetain3D(retain3D);
            
        for (int i=0, endi=mols.size(); i<endi; ++i)
        {
            IAtomContainer molecule = mols.getMolecule(i);

            try
            {
                molecule = standardize.Standardize(molecule);
                mols.setMolecule(i, molecule);
            }
            catch (Exception ex)
            {
                Logger.getLogger("global").log(Level.FINE, null, ex);
            }            
        }

        mols.recalculateStatistics();
        
    	molOriginal.deliver(ori);
    	molOutput.deliver(mols);
    }
}