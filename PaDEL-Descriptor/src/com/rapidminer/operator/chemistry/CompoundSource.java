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
import com.rapidminer.operator.io.AbstractReader;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDirectory;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import java.io.File;
import java.util.List;
import libpadeldescriptor.IteratingPaDELReader;
import org.openscience.cdk.interfaces.IAtomContainer;

/*
    This operator reads compounds from molecular files. CDK library is
    used to read the molecular files so most file formats (e.g. MDL mol/sdf,
    PDB, SMILES, etc) are supported. The operator can read compounds from a
    single file or from all supported files in one or several directories.
    The operator can also read just a single compound from a file containing
    multiple compounds. After reading the compounds, basic information like
    name of compound, number of atoms, bonds, fragments and hydrogen atoms
    are computed and displayed.
 */
public class CompoundSource extends AbstractReader<Compounds>
{
    public static final String PARAMETER_READ_FILEDIR = "read";

    public static final String PARAMETER_MOL_FILE = "file";

    public static final String PARAMETER_READ_SINGLE = "read_single_compound";

    public static final String PARAMETER_CPD_INDEX = "index_of_compound_to_read";

    public static final String PARAMETER_DIRECTORIES = "directories";

    public static final String PARAMETER_DIRECTORY_IGNORE = "enter_anything_here";

    public static final String PARAMETER_DIRECTORY = "directory";

    public static final String PARAMETER_FILENAME_AS_NAME = "use_filename_as_compound_name";

    public static final String[] readFileDirStrings = { "File", "Directories" };

    static
    {
        AbstractReader.registerReaderDescription(new ReaderDescription("", CompoundSource.class, PARAMETER_MOL_FILE));
    }

    public CompoundSource(OperatorDescription description)
    {
        super(description, Compounds.class);
    }

    @Override
    public List<ParameterType> getParameterTypes()
    {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = null;
        
        type = new ParameterTypeCategory(PARAMETER_READ_FILEDIR, "Read file/directory", readFileDirStrings, 0);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeFile(PARAMETER_MOL_FILE, "Filename of molecular file to read compound(s).", null, true);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_READ_FILEDIR, readFileDirStrings, true, 0));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_READ_SINGLE, "Read a single compound only.", false);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_READ_FILEDIR, readFileDirStrings, true, 0));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_CPD_INDEX, "Index of compound in file to read.", 1, Integer.MAX_VALUE, 1);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_READ_SINGLE, true, true));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeList(PARAMETER_DIRECTORIES, "Directories with molecular files.",
                                     new ParameterTypeString(PARAMETER_DIRECTORY_IGNORE, "Leave this empty."),
                                     new ParameterTypeDirectory(PARAMETER_DIRECTORY, "Directory with molecular files.", true));
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_READ_FILEDIR, readFileDirStrings, true, 1));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_FILENAME_AS_NAME, "Use filename as compound name.", false);
        type.setExpert(true);
        types.add(type);

        return types;
    }

    @Override
    public Compounds read() throws OperatorException
    {
        int readFileFile = getParameterAsInt(PARAMETER_READ_FILEDIR);
        if (readFileFile == 0)
        {
            Compounds molecules = new Compounds();
            return readFile(molecules, getParameterAsFile(PARAMETER_MOL_FILE), false);
        }
        else
        {
            return readDir();
        }
    }

    private Compounds readFile(Compounds molecules, File fMol, boolean readAll) throws OperatorException
    {
        IteratingPaDELReader reader = null;
        try
        {
            reader = new IteratingPaDELReader(fMol);
        }
        catch (Exception ex)
        {
            throw new OperatorException("Cannot read molecular file: " + fMol.getAbsolutePath());
        }

        int index = -1;
        if (!readAll && getParameterAsBoolean(PARAMETER_READ_SINGLE))
        {
            index = getParameterAsInt(PARAMETER_CPD_INDEX);
        }
        int PaDELReaderCount = 0;
        while (reader.hasNext())
        {
            // Still have another molecule to read in file.
            ++PaDELReaderCount;
            IAtomContainer mol = (IAtomContainer)reader.next();

            // Get molecule name
            StringBuffer molName = new StringBuffer();
            molName.setLength(0);
            if (mol.getProperty("cdk:Title")!=null && getParameterAsBoolean(PARAMETER_FILENAME_AS_NAME)==false)
            {
               molName.append(mol.getProperty("cdk:Title"));
            }
            else
            {
                String prefix = getParameterAsBoolean(PARAMETER_FILENAME_AS_NAME) ? "" : "AUTOGEN_";
                int lastIndex = fMol.getName().lastIndexOf(".");
                if (lastIndex!=-1)
                {
                    molName.append(prefix + fMol.getName().substring(0, lastIndex));
                }
                else
                {
                    molName.append(prefix + fMol.getName());
                }
                if (PaDELReaderCount>1 || reader.hasNext()) molName.append("_" + PaDELReaderCount);
                mol.setProperty("cdk:Title", molName.toString());
            }
            mol.setProperty("cdk:Remark", "Pharmaceutical Data Exploration Laboratory");

            if (index==-1)
            {
                molecules.addMolecule(mol);
            }
            else if (PaDELReaderCount==index)
            {
                molecules.addMolecule(mol);
                break;
            }
        }
        return molecules;
    }

    private Compounds readDir() throws OperatorException
    {
        List<String[]> directories = getParameterList(PARAMETER_DIRECTORIES);

        Compounds molecules = new Compounds();
        for (int i=0, endi=directories.size(); i<endi; ++i)
        {
            File dir = new File(directories.get(i)[1]);
            for (File fMol : dir.listFiles())
            {
                if (fMol.getName().equals(".") || fMol.getName().equals("..") || fMol.isDirectory())
                {
                    continue;
                }

                this.readFile(molecules, fMol, true);
            }
        }
        return molecules;
    }
}