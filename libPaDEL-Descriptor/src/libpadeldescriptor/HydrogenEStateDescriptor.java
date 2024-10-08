/*
 *  $RCSfile$
 *  $Author: yapchunwei $
 *  $Date: 2008-06-10 18:12:38 +0800 (Tue, 10 Jun 2008) $
 *  $Revision: 1 $
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package libpadeldescriptor;


import java.io.IOException;
import java.util.Iterator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  Hydrogen Electrotopological State of an atom. 
 *  There must not be any implicit hydrogens for all the atoms.<p>
 *
 *  This descriptor uses these parameters:
 *  <table border="1">
 * <caption>Table Caption</caption>
 *    <tr>
 *
 *      <td>
 *        Name
 *      </td>
 *
 *      <td>
 *        Default
 *      </td>
 *
 *      <td>
 *        Description
 *      </td>
 *
 *    </tr>
 *
 *    <tr>
 *
 *      <td>
 *        
 *      </td>
 *
 *      <td>
 *        
 *      </td>
 *
 *      <td>
 *        no parameters
 *      </td>
 *
 *    </tr>
 *
 *  </table>
 *
 *
 *@author         yapchunwei
 *@cdk.created    2008-06-10
 *@cdk.module     qsaratomic
 *@cdk.svnrev     $Revision: 1 $
 *@cdk.set        qsar-descriptors
 *@cdk.dictref    qsar-descriptors:hydrogenEStateDescriptor
 */
public class HydrogenEStateDescriptor implements IAtomicDescriptor {

    private static final String[] names = {"HydrogenEState"};

	/**
	 *  Constructor for the HydrogenEStateDescriptor object
	 *
	 */
	public HydrogenEStateDescriptor() {
	}

    private IChemObjectBuilder builder;

    @Override
    public void initialise(IChemObjectBuilder builder) {
        this.builder = builder;
    }

    /**
     *  Gets the specification attribute of the HydrogenEStateDescriptor
     *  object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"hydrogenEState",
				this.getClass().getName(),
				"$Id: ElectrotopologicalStateDescriptor.java 1 2008-06-10 10:12:38Z yapchunwei $",
				"PaDEL");
	}


	/**
     * This descriptor does have any parameter.
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
    }


	/**
	 *  Gets the parameters attribute of the HydrogenEStateDescriptor
	 *  object
	 *
	 * @return    The parameters value
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    /**
     * Gets the names of descriptors
     * 
     * @return  Names of descriptors
     */
    @Override
    public String[] getDescriptorNames() {
        return names;
    }
    
    /**
     * Get dummy descriptor values when error occurs.
     * 
     * @param e Exception that prevents the proper calculation of the descriptors
     * @return  Dummy descriptor values
     */
    private DescriptorValue getDummyDescriptorValue(Exception e) {
        int ndesc = getDescriptorNames().length;
        DoubleArrayResult results = new DoubleArrayResult(ndesc);
        for (int i = 0; i < ndesc; i++) results.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), results, getDescriptorNames(), e);
    }

	/**
	 *  There must not be any implicit hydrogens for all the atoms.
	 *
	 *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
	 *@return                   a double with hydrogen eletrotopological state of the heavy atom
	 */
    @Override
        public DescriptorValue calculate(IAtom atom, IAtomContainer ac) {
            try
            {
                KierHallElectronegativityDescriptor khed = new KierHallElectronegativityDescriptor();             
                double HEState = 0.0;                
                int atomicNumber = atom.getAtomicNumber();
                if (atomicNumber!=1)
                {           
                    double RKHE = ((DoubleResult)khed.calculate(atom, ac).getValue()).doubleValue();
                    double dHI = 0.0;
                    for (Iterator<IAtom> atoms = ac.atoms().iterator(); atoms.hasNext();) 
                    {
                        IAtom cAtom = atoms.next();
                        if (cAtom==atom) 
                        {
                            continue;
                        }
                        if (!cAtom.getSymbol().equals("H"))
                        {
                            int rij = PathTools.getShortestPath(ac, atom, cAtom).size(); // Profiling has shown this to be the most time consuming step in the algorithm.
                            dHI += (RKHE-((DoubleResult)khed.calculate(cAtom, ac).getValue()).doubleValue()) / (rij*rij);
                        }                    
                    }

                    HEState = dHI - RKHE;
                    if (AtomContainerManipulator.countHydrogens(ac, atom)>0) 
                    {
                        HEState += -0.2;
                    }
                    HEState = -HEState;
                }
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(HEState), names);
            }
            catch (Exception e)
            {
                return getDummyDescriptorValue(new CDKException("Error in ElementPTFactory: " + e.getMessage()));
            }
        }
        
        /**
         * 
         * @param  atom              The IAtom for which the DescriptorValue is requested
         * @param  ac                AtomContainer
         * @param pathLengths        Array of shortest paths between atoms and other atoms in the container.
         * @return                   a double with hydrogen eletrotopological state of the heavy atom
         */
        public DescriptorValue calculate(IAtom atom, IAtomContainer ac, int[] pathLengths) {
            try
            {
                KierHallElectronegativityDescriptor khed = new KierHallElectronegativityDescriptor();             
                double HEState = 0.0;                
                int atomicNumber = atom.getAtomicNumber();
                if (atomicNumber!=1)
                {           
                    double RKHE = ((DoubleResult)khed.calculate(atom, ac).getValue()).doubleValue();
                    double dHI = 0.0;
                    int maxAtoms = ac.getAtomCount();
                    for (int j=0; j<maxAtoms; ++j) 
                    {
                        IAtom cAtom = ac.getAtom(j);
                        if (cAtom==atom) 
                        {
                            continue;
                        }
                        if (!cAtom.getSymbol().equals("H"))
                        {
                            int rij = pathLengths[j] + 1;
                            dHI += (RKHE-((DoubleResult)khed.calculate(cAtom, ac).getValue()).doubleValue()) / (rij*rij);
                        }                    
                    }

                    HEState = dHI - RKHE;
                    if (AtomContainerManipulator.countHydrogens(ac, atom)>0) 
                    {
                        HEState += -0.2;
                    }
                    HEState = -HEState;
                }
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(HEState), names);
            }
            catch (Exception e)
            {
                return getDummyDescriptorValue(new CDKException("Error in ElementPTFactory: " + e.getMessage()));
            }
        }
        


	/**
	 *  Gets the parameterNames attribute of the HydrogenEStateDescriptor  object.
	 *
	 * @return    The parameterNames value
	 */
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }


	/**
	 *  Gets the parameterType attribute of the HydrogenEStateDescriptor object.
	 *
	 * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }
}

