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
package com.rapidminer.gui.wizards;

import com.rapidminer.operator.io.ExampleSource;
import com.rapidminer.operator.chemistry.CompoundDescriptors;
import com.rapidminer.parameter.ParameterType;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import padeldescriptor.PaDELDescriptorPropertySheetPage2;
import padeldescriptor.PaDELDescriptorPropertySheetPage3;
import padeldescriptor.PaDELDescriptorPropertySheetPage4;


/**
 * This class is the creator for wizard dialogs defining the configuration for 
 * {@link ExampleSource} operators.
 * 
 * @author Ingo Mierswa
 */
public class MoleculeDescriptorsConfigurationDialog
{
    private static final long serialVersionUID = 3839592326135393078L;

    /** Creates a new wizard. */
    public MoleculeDescriptorsConfigurationDialog(ParameterType type, ConfigurationListener listener)
    {
        CompoundDescriptors md = (CompoundDescriptors)listener;
        md.SetDescriptors();
        
        JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();
        PaDELDescriptorPropertySheetPage2 descriptor2DConfig = md.descriptor2DConfig;
        PaDELDescriptorPropertySheetPage3 descriptor3DConfig = md.descriptor3DConfig;
        PaDELDescriptorPropertySheetPage4 fingerprintConfig = md.fingerprintConfig;

        descriptor2DConfig.setName("descriptor2DConfig");
        jTabbedPane1.addTab("1D & 2D", descriptor2DConfig);

        descriptor3DConfig.setName("descriptor3DConfig");
        jTabbedPane1.addTab("3D", descriptor3DConfig);

        fingerprintConfig.setName("fingerprintConfig");
        jTabbedPane1.addTab("Fingerprints", fingerprintConfig);

        JOptionPane pane = new JOptionPane(new Object[] {jTabbedPane1}, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dialog = pane.createDialog("Choose Descriptors");
        dialog.setVisible(true);
        dialog.dispose();

        md.UpdateDescriptors();
    }
}
