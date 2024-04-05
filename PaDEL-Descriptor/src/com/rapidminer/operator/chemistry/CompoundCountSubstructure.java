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
import com.rapidminer.parameter.ParameterTypeString;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

/*
    This operator counts the number of times that a substructure is present in a molecule.
    Aromaticity should be detected using the Detect Aromaticity operator and
    hydrogen atoms should be added using the Add Hydrogens operator before
    using this operator.
 */
public class CompoundCountSubstructure extends CompoundAbstractProcessing
{
    public static final String PARAMETER_SMARTS = "SMARTS";

    public CompoundCountSubstructure(OperatorDescription description)
    {
        super(description);
    }

    @Override
    public List<ParameterType> getParameterTypes()
    {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = null;

        type = new ParameterTypeString(PARAMETER_SMARTS, "SMARTS pattern.", false);
        type.setExpert(false);
        types.add(type);

        return types;
    }

    @Override
    public void doWork() throws OperatorException
    {
    	Compounds mols = molInput.getData();
        Compounds ori = new Compounds();

        String smart = getParameterAsString(PARAMETER_SMARTS);      
        ArrayList<String> counts = new ArrayList<String>(mols.size());
        for (int i=0, endi=mols.size(); i<endi; ++i)
        {
            IAtomContainer molecule = mols.getMolecule(i);

            try
            {
                ori.addMolecule((IAtomContainer)molecule.clone());

                SMARTSQueryTool sqt = new SMARTSQueryTool(smart);
                if (sqt.matches(molecule))
                {
                    counts.add(String.valueOf(sqt.getUniqueMatchingAtoms().size()));
                }
                else
                {
                    counts.add("0");
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger("global").log(Level.FINE, null, ex);
            }
        }
        mols.addColumn(smart, counts.toArray(new String[] {}));

        molOriginal.deliver(ori);
    	molOutput.deliver(mols);
    }
}