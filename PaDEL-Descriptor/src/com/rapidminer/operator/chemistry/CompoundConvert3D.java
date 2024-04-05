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
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import libpadeldescriptor.libPaDELDescriptorJob;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/*
    This operator converts a molecule to 3D coordinates.
    Aromaticity should be detected using the Detect Aromaticity operator and
    hydrogen atoms should be added using the Add Hydrogens operator before
    using this operator.
 */
public class CompoundConvert3D extends CompoundAbstractProcessing
{
    public static final String PARAMETER_FORCEFIELD = "forcefield";
    public static final String[] convert3DStrings = { libPaDELDescriptorJob.MM2, libPaDELDescriptorJob.MMFF94 };

    public static final String PARAMETER_CONVERT_ALL = "convert_all_compounds";

    public static final String PARAMETER_CPD_INDEX = "index_of_compound_to_convert";

    public CompoundConvert3D(OperatorDescription description)
    {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes()
    {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = null;

        type = new ParameterTypeCategory(PARAMETER_FORCEFIELD, "Forcefield for converting compounds to 3D.", convert3DStrings, 0);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_CONVERT_ALL, "Convert all compounds to 3D.", false);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_CPD_INDEX, "Index of compound to rename.", 1, Integer.MAX_VALUE, 1);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_CONVERT_ALL, true, false));
        type.setExpert(false);
        types.add(type);

        return types;
    }

    @Override
    public void doWork() throws OperatorException
    {
    	Compounds mols = molInput.getData();
        Compounds ori = new Compounds();
        try
        {
            for (int i=0, endi=mols.size(); i<endi; ++i)
            {
                ori.addMolecule((IAtomContainer)mols.getMolecule(i).clone());
            }
        }
        catch (Exception ex)
        {
            // Not suppose to happen.
        }

        String forcefield = getParameterAsString(PARAMETER_FORCEFIELD);
        if (getParameterAsBoolean(PARAMETER_CONVERT_ALL))
        {
            for (int i=0, endi=mols.size(); i<endi; ++i)
            {
                mols.setMolecule(i, Convert3D(mols.getMolecule(i), forcefield));
            }
        }
        else
        {
            int cpdIndex = getParameterAsInt(PARAMETER_CPD_INDEX);            
            mols.setMolecule(cpdIndex-1, Convert3D(mols.getMolecule(cpdIndex-1), forcefield));
        }
        
        mols.recalculateStatistics();

    	molOriginal.deliver(ori);
    	molOutput.deliver(mols);
    }

    private IAtomContainer Convert3D(IAtomContainer molecule, String forcefield)
    {
        try
        {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            // Calculate 3D coordinates.
            TemplateHandler3D template = TemplateHandler3D.getInstance();
            ModelBuilder3D mb3d = ModelBuilder3D.getInstance(template,forcefield);
            return (IAtomContainer) mb3d.generate3DCoordinates((IMolecule) molecule, true);
        }
        catch (Exception ex)
        {
            Logger.getLogger("global").log(Level.FINE, null, ex);
            return null;
        }
    }
}