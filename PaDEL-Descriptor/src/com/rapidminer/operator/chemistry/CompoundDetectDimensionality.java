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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/*
    This operator computes the mean, minimum and maximum x, y, z
    coordinates of a molecule and detects its dimensionality (0D, 1D, 2D or 3D).
 */
public class CompoundDetectDimensionality extends CompoundAbstractProcessing
{
    public CompoundDetectDimensionality(OperatorDescription description)
    {
        super(description);
    }

    @Override
    public void doWork() throws OperatorException
    {
    	Compounds mols = molInput.getData();
        Compounds ori = new Compounds();

        ArrayList<String> dim = new ArrayList<String>(mols.size());
        ArrayList<String> xcoord = new ArrayList<String>(mols.size());
        ArrayList<String> ycoord = new ArrayList<String>(mols.size());
        ArrayList<String> zcoord = new ArrayList<String>(mols.size());
        DecimalFormat df = new DecimalFormat("#0.0000");
        for (int i=0, endi=mols.size(); i<endi; ++i)
        {
            IAtomContainer molecule = mols.getMolecule(i);

            try
            {
                ori.addMolecule((IAtomContainer)molecule.clone());                
            }
            catch (Exception ex)
            {
                Logger.getLogger("global").log(Level.FINE, null, ex);
            }

            double sumx = 0; double minx = Double.MAX_VALUE; double maxx = -Double.MAX_VALUE;
            double sumy = 0; double miny = Double.MAX_VALUE; double maxy = -Double.MAX_VALUE;
            double sumz = 0; double minz = Double.MAX_VALUE; double maxz = -Double.MAX_VALUE;

            for (int j=0, endj=molecule.getAtomCount(); j<endj; ++j)
            {
                IAtom atom = molecule.getAtom(j);
                Point3d coords3D = atom.getPoint3d();
                if (coords3D!=null)
                {
                    sumx += coords3D.x; minx = Math.min(minx, coords3D.x); maxx = Math.max(maxx, coords3D.x);
                    sumy += coords3D.y; miny = Math.min(miny, coords3D.y); maxy = Math.max(maxy, coords3D.y);
                    sumz += coords3D.z; minz = Math.min(minz, coords3D.z); maxz = Math.max(maxz, coords3D.z);
                }
                else
                {
                    Point2d coords2D = atom.getPoint2d();
                    if (coords2D!=null)
                    {
                        sumx += coords2D.x; minx = Math.min(minx, coords2D.x); maxx = Math.max(maxx, coords2D.x);
                        sumy += coords2D.y; miny = Math.min(miny, coords2D.y); maxy = Math.max(maxy, coords2D.y);
                        sumz += 0.0; minz = Math.min(minz, 0.0); maxz = Math.max(maxz, 0.0);
                    }
                    else
                    {
                        sumx += 0.0; minx = Math.min(minx, 0.0); maxx = Math.max(maxx, 0.0);
                        sumy += 0.0; miny = Math.min(miny, 0.0); maxy = Math.max(maxy, 0.0);
                        sumz += 0.0; minz = Math.min(minz, 0.0); maxz = Math.max(maxz, 0.0);
                    }
                }
            }
            xcoord.add(df.format(sumx/molecule.getAtomCount()) + " [" + df.format(minx) + " ; " + df.format(maxx) + "]");
            ycoord.add(df.format(sumy/molecule.getAtomCount()) + " [" + df.format(miny) + " ; " + df.format(maxy) + "]");
            zcoord.add(df.format(sumz/molecule.getAtomCount()) + " [" + df.format(minz) + " ; " + df.format(maxz) + "]");

            if (minx==maxx && miny==maxy && minz==maxz)
            {
                dim.add("0D");
            }
            else if ((minx==maxx && miny==maxy) ||
                     (minx==maxx && minz==maxz) ||
                     (miny==maxy && minz==maxz))
            {
                dim.add("1D");
            }
            else if (minx==maxx || miny==maxy || minz==maxz)
            {
                dim.add("2D");
            }
            else
            {
                dim.add("3D");
            }
        }
        mols.addColumn("xcoord", xcoord.toArray(new String[] {}));
        mols.addColumn("ycoord", ycoord.toArray(new String[] {}));
        mols.addColumn("zcoord", zcoord.toArray(new String[] {}));
        mols.addColumn("dimensionality", dim.toArray(new String[] {}));

    	molOriginal.deliver(ori);
    	molOutput.deliver(mols);
    }
}