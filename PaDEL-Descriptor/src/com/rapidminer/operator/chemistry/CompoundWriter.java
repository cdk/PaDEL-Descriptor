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
import com.rapidminer.operator.io.AbstractWriter;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDirectory;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import com.rapidminer.parameter.conditions.NonEqualTypeCondition;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.Mol2Writer;
import org.openscience.cdk.io.PDBWriter;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;

/*
    This operator writes compounds to one or several
    molecular files. CDK library is used to write the molecular files.
    MDL mol/sdf, PDB, SMILES and Sybyl mol2 molecular formats are supported.
 */

public class CompoundWriter extends AbstractWriter<Compounds>
{
    public static final String PARAMETER_MOL_TYPE = "molecular_format";

    public static final String PARAMETER_USE_NAME = "use_compound_name_as_filename";

    public static final String PARAMETER_MOL_DIR = "directory_to_write_files";

    public static final String PARAMETER_MOL_FILE = "file_to_write_compound";

    public static final String PARAMETER_SDF_FILE = "sdf_file_to_write_compounds";

    public static final String PARAMETER_CPD_INDEX = "index_of_compound_to_write";
   
    public static final String[] typeStrings = { "MDL (mol)", "PDB (pdb)", "SDF (SDF)", "SMILES (smi)", "Sybyl (mol2)" };
    public static final String[] typeExtStrings = { ".mol", ".pdb", ".sdf", ".smi", ".mol2" };

    public CompoundWriter(OperatorDescription description)
    {
        super(description, Compounds.class);
    }

    @Override
    public List<ParameterType> getParameterTypes()
    {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = null;

        type = new ParameterTypeCategory(PARAMETER_MOL_TYPE, "Molecular format to write the compounds.", typeStrings, 0);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_USE_NAME, "Use compound name as filename. Files will be overwritten if two compounds have the same name.", true);
        type.setExpert(false);
        types.add(type);
        
        type = new ParameterTypeDirectory(PARAMETER_MOL_DIR, "Directory to write the molecular files.", true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_NAME, true, true));
        type.setExpert(false);
        types.add(type);
        
        type = new ParameterTypeFile(PARAMETER_MOL_FILE, "Filename of molecular file to write compound.", null, true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_NAME, true, false));
        type.registerDependencyCondition(new NonEqualTypeCondition(this, PARAMETER_MOL_TYPE, typeStrings, true, 2));
        type.setExpert(false);
        types.add(type);
        
        type = new ParameterTypeFile(PARAMETER_SDF_FILE, "Filename of molecular file to write compounds.", null, true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_NAME, true, false));
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_MOL_TYPE, typeStrings, true, 2));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_CPD_INDEX, "Index of compound to write to molecular file.", 1, Integer.MAX_VALUE, 1);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_NAME, true, false));
        type.registerDependencyCondition(new NonEqualTypeCondition(this, PARAMETER_MOL_TYPE, typeStrings, true, 2));
        type.setExpert(false);
        types.add(type);
        
        return types;
    }

    @Override
    public Compounds write(Compounds mols) throws OperatorException
    {        
        if (getParameterAsBoolean(PARAMETER_USE_NAME))
        {
            String dir = getParameterAsString(PARAMETER_MOL_DIR);
            File fDir = new File(dir);
            boolean hasDir = fDir.exists();
            if (!hasDir) hasDir = fDir.mkdirs();
            if (hasDir)
            {
                for (int i=0, endi=mols.size(); i<endi; ++i)
                {
                    WriteMolecule(dir + File.separator + mols.getMoleculeName(i), mols.getMolecule(i));
                }
            }
            else
            {
                throw new OperatorException("Cannot create directory: " + dir);
            }
        }
        else
        {
            int type = getParameterAsInt(PARAMETER_MOL_TYPE);
            if (type != 2)
            {
                int cpdIndex = getParameterAsInt(PARAMETER_CPD_INDEX) - 1;
                WriteMolecule(getParameterAsString(PARAMETER_MOL_FILE), mols.getMolecule(cpdIndex));
            }
            else
            {
                DefaultChemObjectWriter writer = null;
                String path = getParameterAsString(PARAMETER_SDF_FILE);
                try
                {
                    int lastIndex = path.lastIndexOf(".");
                    path += (lastIndex!=-1 && path.substring(lastIndex).equals(typeExtStrings[type]) ? "" : typeExtStrings[type]);
                    FileWriter fw = new FileWriter(path);
                    writer = new SDFWriter(fw);
                    for (int i=0, endi=mols.size(); i<endi; ++i)
                    {
                        writer.write(mols.getMolecule(i));
                    }
                }
                catch (Exception ex)
                {
                    throw new OperatorException("Cannot write molecular file: " + path);
                }
                finally
                {
                    if (writer!=null)
                    {
                        try
                        {
                            writer.close();
                        }
                        catch (Exception ex1)
                        {
                            Logger.getLogger("global").log(Level.FINE, null, ex1);
                        }
                    }
                }
            }
        }        

        return mols;
    }

    private void WriteMolecule(String path, IAtomContainer mol) throws OperatorException
    {
        DefaultChemObjectWriter writer = null;
        int type = getParameterAsInt(PARAMETER_MOL_TYPE);
        try
        {
            // Prepare writer to write molecule to a file, adding the correct file extension if necessary.
            int lastIndex = path.lastIndexOf(".");
            path += (lastIndex!=-1 && path.substring(lastIndex).equals(typeExtStrings[type]) ? "" : typeExtStrings[type]);
            FileWriter fw = new FileWriter(path);
            switch (type)
            {
                case 0: // MDL (mol/sdf)
                    writer = new MDLV2000Writer(fw);
                    break;
                case 1: // PDB (pdb)
                    writer = new PDBWriter(fw);
                    break;
                case 2: // SDF (sdf)
                    writer = new SDFWriter(fw);
                    break;
                case 3: // SMILES (smi)
                    writer = new SMILESWriter(fw);
                    break;
                case 4: // Sybyl (mol2)
                    writer = new Mol2Writer(fw);
                    break;
            }
            writer.write(mol);
        }
        catch (Exception ex)
        {
            if (type == 1) // PDB (pdb)
            {
                // Check if failure to write to PDB is due to lack of 3D coordinates.
                try
                {
                    TemplateHandler3D template = TemplateHandler3D.getInstance();
                    ModelBuilder3D mb3d = ModelBuilder3D.getInstance(template,"mm2");
                    mol = (IAtomContainer) mb3d.generate3DCoordinates((IMolecule) mol, true);
                    writer.write(mol);
                }
                catch (Exception ex1)
                {
                    // Cannot calculate 3D coordinates for file
                    throw new OperatorException("Cannot write molecular file: " + path);
                }                
            }
            else throw new OperatorException("Cannot write molecular file: " + path);
        }
        finally
        {
            if (writer!=null)
            {
                try
                {
                    writer.close();
                }
                catch (Exception ex1)
                {
                    Logger.getLogger("global").log(Level.FINE, null, ex1);
                }
            }
        }
    }
}