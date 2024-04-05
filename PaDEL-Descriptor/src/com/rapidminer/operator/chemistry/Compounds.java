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

import com.rapidminer.tools.PaDELSimpleTable;
import java.util.ArrayList;
import libpadeldescriptor.CDK_AtomCountDescriptor;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;

public class Compounds extends PaDELSimpleTable
{
    private static final long serialVersionUID = 3621893012632345833L;

    private ArrayList<IAtomContainer> mols = new ArrayList<IAtomContainer>();
    
    public Compounds()
    {
        super("Molecules", new String[] { "Compound", "nAtoms", "nBonds", "nFragments", "nHydrogens" });
    }

    public int size()
    {
        return mols.size();
    }

    public void addMolecule(IAtomContainer mol)
    {
        mols.add(mol);
        addRow(String.valueOf(mols.size()), new String[] { (String)mol.getProperty("cdk:Title"), "", "", "", "" });
        calculateStatistics(mol, mols.size()-1);
    }

    public void recalculateStatistics()
    {
        for (int i=0, endi=mols.size(); i<endi; ++i)
        {
            calculateStatistics(mols.get(i), i);
        }
    }

    public void calculateStatistics(IAtomContainer mol, int index)
    {
        String name = (String)mol.getProperty("cdk:Title");
        String nAtoms = String.valueOf(mol.getAtomCount());
        String nBonds = String.valueOf(mol.getBondCount());
        String nFragments = "1";
        if (!ConnectivityChecker.isConnected(mol))
        {
            IMoleculeSet molSet = ConnectivityChecker.partitionIntoMolecules(mol);
            nFragments = String.valueOf(molSet.getMoleculeCount());
        }
        CDK_AtomCountDescriptor nH = new CDK_AtomCountDescriptor(new String[] {"H"});
        nH.setMolecule(mol);
        nH.run();
        String nHydrogens = String.valueOf(nH.getDescriptorValues()[0]);

        this.setValue(index, 0, name);
        this.setValue(index, 1, nAtoms);
        this.setValue(index, 2, nBonds);
        this.setValue(index, 3, nFragments);
        this.setValue(index, 4, nHydrogens);
    }

    public ArrayList<IAtomContainer> getMolecules()
    {
        ArrayList<IAtomContainer> temp = new ArrayList<IAtomContainer>(mols.size());
        for (int i=0, endi=mols.size(); i<endi; ++i)
        {
            try
            {
                temp.add((IAtomContainer)mols.get(i).clone());
            }
            catch (Exception ex)
            {
                // Not suppose to happen.
            }
        }
        return temp;
    }

    public IAtomContainer getMolecule(int index)
    {
        return mols.get(index);
    }

    public void setMolecule(int index, IAtomContainer mol)
    {
        mols.set(index, mol);
        calculateStatistics(mol, index);
    }

    public String getMoleculeName(int index)
    {
        return (String)mols.get(index).getProperty("cdk:Title");
    }

    public void setMoleculeName(int index, String name)
    {
        mols.get(index).setProperty("cdk:Title", name);
    }
}