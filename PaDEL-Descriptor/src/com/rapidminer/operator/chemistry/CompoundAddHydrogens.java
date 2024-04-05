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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/*
    This operator adds hydrogen atoms based on atom type definitions.
 */
public class CompoundAddHydrogens extends CompoundAbstractProcessing
{
    public CompoundAddHydrogens(OperatorDescription description)
    {
        super(description);
    }

    @Override
    public void doWork() throws OperatorException
    {
    	Compounds mols = molInput.getData();
        Compounds ori = new Compounds();

        for (int i=0, endi=mols.size(); i<endi; ++i)
        {
            IAtomContainer molecule = mols.getMolecule(i);

            try
            {
                ori.addMolecule((IAtomContainer)molecule.clone());
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
                CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
                adder.addImplicitHydrogens(molecule);
                AtomContainerManipulator.convertImplicitToExplicitHydrogens(molecule);
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
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