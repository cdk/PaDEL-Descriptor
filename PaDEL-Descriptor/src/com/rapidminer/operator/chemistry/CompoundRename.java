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
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;

/*
    This operator is used to rename a compound.
 */
public class CompoundRename extends CompoundAbstractProcessing
{
    public static final String PARAMETER_CPD_INDEX = "index_of_compound_to_rename";

    public static final String PARAMETER_CPD_NEW_NAME = "new_name";

    public CompoundRename(OperatorDescription description)
    {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes()
    {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = null;
        
        type = new ParameterTypeInt(PARAMETER_CPD_INDEX, "Index of compound to rename.", 1, Integer.MAX_VALUE, 1);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeString(PARAMETER_CPD_NEW_NAME, "Compound's new name.", false);
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

        int cpdIndex = getParameterAsInt(PARAMETER_CPD_INDEX);
        String cpdNewName = getParameterAsString(PARAMETER_CPD_NEW_NAME);
        mols.setMoleculeName(cpdIndex-1, cpdNewName);
        mols.calculateStatistics(mols.getMolecule(cpdIndex-1), cpdIndex-1);

    	molOriginal.deliver(ori);
    	molOutput.deliver(mols);
    }
}