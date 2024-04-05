/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package libpadeldescriptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.openscience.cdk.ChemFile;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.formats.HINFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.io.formats.PubChemCompoundsXMLFormat;
import org.openscience.cdk.io.formats.PubChemSubstancesASNFormat;
import org.openscience.cdk.io.formats.PubChemSubstancesXMLFormat;
import org.openscience.cdk.io.formats.SMILESFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.io.iterator.IteratingPCCompoundASNReader;
import org.openscience.cdk.io.iterator.IteratingPCCompoundXMLReader;
import org.openscience.cdk.io.iterator.IteratingPCSubstancesXMLReader;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 *
 * @author     Yap Chun Wei
 * @cdk.created    2009-12-03
 *
 * @cdk.keyword    file format
 */
public class IteratingPaDELReader extends DefaultIteratingChemObjectReader {

    private BufferedReader input;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(IteratingPaDELReader.class);
    private IResourceFormat currentFormat;
    private boolean nextAvailableIsKnown;
    private boolean hasNext;
    private IChemObjectBuilder builder;
    private IAtomContainer nextMolecule;

    private File molecule;
    private PaDELIteratingSMILESReader iteratingSMILESReader;
    private IteratingMDLReader iteratingMDLReader;
    private IteratingPCCompoundASNReader iteratingPCCompoundASNReader;
    private IteratingPCCompoundXMLReader iteratingPCCompoundXMLReader;
    private IteratingPCSubstancesXMLReader iteratingPCSubstancesXMLReader;
    private ISimpleChemObjectReader iSimpleChemObjectReader;
    private int curLigIndex;
    private List<IAtomContainer> container = new ArrayList<IAtomContainer>();

    /**
     * Constructs a new IteratingPaDELReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     * @param builder The builder to use
     * @see org.openscience.cdk.DefaultChemObjectBuilder
     * @see org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder
     */
    public IteratingPaDELReader(File molecule) throws FileNotFoundException {
        this.builder = DefaultChemObjectBuilder.getInstance();
        this.molecule = molecule;
    	setReader(new FileInputStream(molecule));
    }

    /**
     * Constructs a new IteratingPaDELReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     * @param builder The builder to use
     * @see org.openscience.cdk.DefaultChemObjectBuilder
     * @see org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder
     */
    public IteratingPaDELReader(Reader in, IChemObjectBuilder builder) {
        this.builder = builder;
    	setReader(in);
    }

    /**
     * Constructs a new IteratingPaDELReader that can read Molecule from a given InputStream.
     *
     * This method will use @link{DefaultChemObjectBuilder} to build the actual molecules
     *
     * @param  in  The InputStream to read from
     */
    public IteratingPaDELReader(InputStream in) {
        this(new InputStreamReader(in), DefaultChemObjectBuilder.getInstance());
    }

    /**
     * Constructs a new IteratingPaDELReader that can read Molecule from a given InputStream.
     *
     * @param  in  The InputStream to read from
     * @param builder The builder
     */
    public IteratingPaDELReader(InputStream in, IChemObjectBuilder builder) {
        this(new InputStreamReader(in), builder);
    }

    /**
     * Get the format for this reader.
     *
     * @return 
     */
    @Override
    public IResourceFormat getFormat() {
        return currentFormat;
    }

    /**
     * Checks whether there is another molecule to read.
     *
     * @return  true if there are molecules to read, false otherwise
     */
    @Override
    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;

            // now try to parse the next Molecule
            if (currentFormat instanceof SMILESFormat)
            {
                if (iteratingSMILESReader.hasNext())
                {
                    nextMolecule = (IAtomContainer) iteratingSMILESReader.next();
                    ++curLigIndex;
                    hasNext = true;
                }
                else
                {
                    hasNext = false;
                }
            }
            else if (currentFormat instanceof MDLFormat || currentFormat instanceof MDLV2000Format || currentFormat instanceof MDLV3000Format)
            {
                if (iteratingMDLReader.hasNext())
                {
                    nextMolecule = (IAtomContainer)iteratingMDLReader.next();
                    ++curLigIndex;
                    hasNext = true;
                }
                else
                {
                    hasNext = false;
                }
            }
            else if (currentFormat instanceof PubChemSubstancesASNFormat)
            {
                if (iteratingPCCompoundASNReader.hasNext())
                {
                    nextMolecule = (IAtomContainer)iteratingPCCompoundASNReader.next();
                    ++curLigIndex;
                    hasNext = true;
                }
                else
                {
                    hasNext = false;
                }
            }
            else if (currentFormat instanceof PubChemSubstancesXMLFormat)
            {
                if (iteratingPCSubstancesXMLReader.hasNext())
                {
                    nextMolecule = (IAtomContainer)iteratingPCSubstancesXMLReader.next();
                    ++curLigIndex;
                    hasNext = true;
                }
                else
                {
                    hasNext = false;
                }
            }
            else if (currentFormat instanceof PubChemCompoundsXMLFormat)
            {
                if (iteratingPCCompoundXMLReader.hasNext())
                {
                    nextMolecule = (IAtomContainer)iteratingPCCompoundXMLReader.next();
                    ++curLigIndex;
                    hasNext = true;
                }
                else
                {
                    hasNext = false;
                }
            }
            else
            {
                if (curLigIndex<container.size())
                {
                    nextMolecule = container.get(curLigIndex++);
                    hasNext = true;
                }
                else
                {
                    hasNext = false;
                }
            }
            if (!hasNext) nextMolecule = null;            
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }

    /**
     * Get the next molecule from the stream.
     *
     * @return The next molecule
     */
    @Override
    public IChemObject next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextMolecule;
    }

    /**
     * Close the reader.
     *
     * @throws IOException if there is an error during closing
     */
    @Override
    public void close() throws IOException {
        input.close();
        if (iteratingSMILESReader!=null) iteratingSMILESReader.close();
        if (iteratingMDLReader!=null) iteratingMDLReader.close();
        if (iteratingPCCompoundASNReader!=null) iteratingPCCompoundASNReader.close();
        if (iteratingPCCompoundXMLReader!=null) iteratingPCCompoundXMLReader.close();
        if (iteratingPCSubstancesXMLReader!=null) iteratingPCSubstancesXMLReader.close();
        if (iSimpleChemObjectReader!=null) iSimpleChemObjectReader.close();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReader(Reader reader) {
		if (reader instanceof BufferedReader) {
			input = (BufferedReader)reader;
		} else {
			input = new BufferedReader(reader);
		}
        nextAvailableIsKnown = false;
        curLigIndex = 0;
        
        try
        {
            if (molecule.getName().endsWith(".smi"))
            {
                // Have to manually check for SMILES file because CDK cannot detect it properly yet.
                currentFormat = SMILESFormat.getInstance();
            }
            else if (molecule.getName().endsWith(".hin"))
            {
                // Have to manually check for HIN file because CDK cannot detect it properly yet.
                iSimpleChemObjectReader = new PaDELHINReader(input);
                currentFormat = HINFormat.getInstance();
            }
            else 
            {
                iSimpleChemObjectReader = new ReaderFactory().createReader(input);
                currentFormat = iSimpleChemObjectReader.getFormat();
            }
            
            if (currentFormat instanceof SMILESFormat)
            {
                iteratingSMILESReader = new PaDELIteratingSMILESReader(input, builder);
            }
            else if (currentFormat instanceof MDLFormat || currentFormat instanceof MDLV2000Format || currentFormat instanceof MDLV3000Format)
            {
                iteratingMDLReader = new IteratingMDLReader(input, builder);
            }
            else if (currentFormat instanceof PubChemSubstancesASNFormat)
            {
                iteratingPCCompoundASNReader = new IteratingPCCompoundASNReader(input, builder);
            }
            else if (currentFormat instanceof PubChemSubstancesXMLFormat)
            {
                iteratingPCSubstancesXMLReader = new IteratingPCSubstancesXMLReader(input, builder);
            }
            else if (currentFormat instanceof PubChemCompoundsXMLFormat)
            {
                iteratingPCCompoundXMLReader = new IteratingPCCompoundXMLReader(input, builder);
            }
            else if (currentFormat instanceof PDBFormat)
            {
                try
                {
                    // Have to manually check for PDB file because CDK cannot read it properly yet so have to use a modified version of PDBReader
                    PaDELPDBReader pdb = new PaDELPDBReader(input);
                    ChemFile cf = new ChemFile();
                    pdb.read(cf);
                    container.clear();
                    for (int i=0, endi=cf.getChemSequenceCount(); i<endi; ++i)
                    {
                        IChemSequence cs = cf.getChemSequence(i);
                        for (int j=0, endj=cs.getChemModelCount(); j<endj; ++j)
                        {
                            IMoleculeSet ms = cs.getChemModel(j).getMoleculeSet();
                            for (int k=0, endk=ms.getMoleculeCount(); k<endk; ++k)
                            {
                                container.add(ms.getMolecule(k));
                            }
                        }
                    }
                }
                catch (CDKException ex1)
                {
                    logger.error("Error while reading molecule: " + ex1.getMessage());
                }
            }
            else
            {
                IChemFile content = (IChemFile) iSimpleChemObjectReader.read(builder.newInstance(IChemFile.class));
                if (content!=null)
                {
                    container = ChemFileManipulator.getAllAtomContainers(content);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("Error while reading molecule: " + ex.getMessage());
            logger.debug(ex);
            nextAvailableIsKnown = true;
        }
        nextMolecule = null;
        hasNext = false;
    }

    @Override
    public void setReader(InputStream reader) {
	    setReader(new InputStreamReader(reader));
    }
}

