/**
 * L2FProd.com Common Components 7.3 License.
 *
 * Copyright 2005-2007 L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package padeldescriptor;

import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.demo.BeanBinder;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import com.l2fprod.common.swing.LookAndFeelTweaks;

import com.l2fprod.common.swing.UserPreferences;
import com.l2fprod.common.util.ResourceManager;
import java.io.File;
import java.util.ListResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import libpadel.PaDELDirectoryPropertyEditor;
import libpadel.PaDELFilePropertyEditor;
import libpadel.PaDELLoadFilePropertyEditor;

/**
 * PropertySheetPage. <br>
 *  
 */
public class PaDELDescriptorPropertySheetPage1 extends JPanel {

    final public Bean data = new Bean();
    final public PropertySheetPanel sheet = new PropertySheetPanel();

    static final public String FORCEFIELD_NO = "No";
    static final public String FORCEFIELD_MM2 = "Yes (use MM2 forcefield)";
    static final public String FORCEFIELD_MMFF94 = "Yes (use MMFF94 forcefield)";

    public PaDELDescriptorPropertySheetPage1()
    {
        setLayout(LookAndFeelTweaks.createVerticalPercentLayout());

        data.setDirectory("");
        data.setDescriptorFile("");
        data.setRemoveSalt(true);
        data.setDetectAromaticity(true);
        data.setStandardizeTautomers(false);
        data.setTautomerFile("");
        data.setStandardizeNitro(true);
        data.setRetain3D(false);
        data.setConvert3D("No");
        data.setCompute2D(true);
        data.setCompute3D(false);
        data.setComputeFingerprints(false);
        data.setMaxThreads(-1);
        data.setMaxJobsWaiting(-1);
        data.setMaxRunTime(-1);
        data.setLog(true);
        data.setMaxCpdPerFile(0);
        data.setRetainOrder(true);
        data.setUseFilenameAsMolName(false);
        
        sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
        sheet.setDescriptionVisible(true);
        sheet.setSortingCategories(false);
        sheet.setSortingProperties(false);
        sheet.setRestoreToggleStates(true);
        PropertySheetTable table = sheet.getTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMinWidth(10);
        table.getColumnModel().getColumn(0).setMaxWidth(500);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        add(sheet, "*");

        // everytime a property change, update the sheet with it
        new BeanBinder(data, sheet);

        PropertySheetTableModel model = table.getSheetModel();
        for (int i=0; i<model.getRowCount(); ++i)
        {
            Item item = model.getPropertySheetElement(i);
            if (item.getName().equals("Advanced")) item.toggle();
        }
    }

    public static class Bean {
        private String directory;
        private String descriptorFile;
        private boolean removeSalt;
        private boolean detectAromaticity;
        private boolean standardizeTautomers;
        private String tautomerFile;
        private boolean standardizeNitro;
        private boolean retain3D;
        private String convert3D;
        private boolean compute2D;
        private boolean compute3D;
        private boolean computeFingerprints;
        private boolean log;
        private int maxThreads;
        private int maxJobsWaiting;
        private long maxRunTime;
        private int maxCpdPerFile;
        private boolean retainOrder;
        private boolean useFilenameAsMolName;

        public boolean isCompute2D() {
            return compute2D;
        }

        public void setCompute2D(boolean compute2D) {
            this.compute2D = compute2D;
        }

        public boolean isCompute3D() {
            return compute3D;
        }

        public void setCompute3D(boolean compute3D) {
            this.compute3D = compute3D;
        }

        public boolean isComputeFingerprints() {
            return computeFingerprints;
        }

        public void setComputeFingerprints(boolean computeFingerprints) {
            this.computeFingerprints = computeFingerprints;
        }

        public String getConvert3D() {
            return convert3D;
        }

        public void setConvert3D(String convert3D) {
            this.convert3D = convert3D;
        }

        public String getDescriptorFile() {
            return descriptorFile;
        }

        public void setDescriptorFile(String descriptorFile) {
            this.descriptorFile = descriptorFile;
        }

        public boolean isDetectAromaticity() {
            return detectAromaticity;
        }

        public void setDetectAromaticity(boolean detectAromaticity) {
            this.detectAromaticity = detectAromaticity;
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public boolean isRemoveSalt() {
            return removeSalt;
        }

        public void setRemoveSalt(boolean removeSalt) {
            this.removeSalt = removeSalt;
        }
        
        public boolean isRetain3D() {
            return retain3D;
        }

        public void setRetain3D(boolean retain3D) {
            this.retain3D = retain3D;
        }
        
        public boolean isStandardizeNitro() {
            return standardizeNitro;
        }

        public void setStandardizeNitro(boolean standardizeNitro) {
            this.standardizeNitro = standardizeNitro;
        }
        
        public boolean isStandardizeTautomers() {
            return standardizeTautomers;
        }

        public void setStandardizeTautomers(boolean standardizeTautomers) {
            this.standardizeTautomers = standardizeTautomers;
        }
        
        public String getTautomerFile() {
            return tautomerFile;
        }

        public void setTautomerFile(String tautomerFile) {
            this.tautomerFile = tautomerFile;
        }
        
        public boolean isLog() {
            return log;
        }

        public void setLog(boolean log) {
            this.log = log;
        }

        public int getMaxThreads() {
            return maxThreads;
        }

        public void setMaxThreads(int maxThreads) {
            this.maxThreads = maxThreads;
        }

        public int getMaxJobsWaiting() {
            return maxJobsWaiting;
        }

        public void setMaxJobsWaiting(int maxJobsWaiting) {
            this.maxJobsWaiting = maxJobsWaiting;
        }
        
        public long getMaxRunTime()
        {
            return maxRunTime;
        }

        public void setMaxRunTime(long maxRunTime)
        {
            this.maxRunTime = maxRunTime;
        }
        
        public int getMaxCpdPerFile() {
            return maxCpdPerFile;
        }

        public void setMaxCpdPerFile(int maxCpdPerFile) {
            this.maxCpdPerFile = maxCpdPerFile;
        }

        public boolean isRetainOrder() {
            return retainOrder;
        }

        public void setRetainOrder(boolean retainOrder) {
            this.retainOrder = retainOrder;
        }

        public boolean isUseFilenameAsMolName() {
            return useFilenameAsMolName;
        }

        public void setUseFilenameAsMolName(boolean useFilenameAsMolName) {
            this.useFilenameAsMolName = useFilenameAsMolName;
        }

        @Override
        public String toString() {
            return "[directory=" + getDirectory() +
                   ",descriptorFile=" + getDescriptorFile() +
                   ",removeSalt=" + isRemoveSalt() +
                   ",detectAromaticity=" + isDetectAromaticity() +
                   ",standardizeTautomers=" + isStandardizeTautomers() +
                   ",tautomerFile=" + getTautomerFile() +
                   ",standardizeNitro=" + isStandardizeNitro() +
                   ",retain3D=" + isRetain3D() +
                   ",convert3D=" + getConvert3D() +
                   ",compute2D=" + isCompute2D() +
                   ",compute3D=" + isCompute3D() +
                   ",computeFingerprints=" + isComputeFingerprints() +
                   ",log=" + isLog() +
                   ",maxThreads=" + getMaxThreads() +
                   ",maxJobsWaiting=" + getMaxJobsWaiting() +
                   ",maxRunTime=" + getMaxRunTime() +
                   ",maxCpdPerFile=" + getMaxCpdPerFile() +
                   ",retainOrder=" + isRetainOrder() +
                   ",useFilenameAsMolName=" + isUseFilenameAsMolName() +
                   "]";
        }
    }

    public static class BeanBeanInfo extends BaseBeanInfo {
        public BeanBeanInfo() {
            super(Bean.class);

            addProperty("directory").setPropertyEditorClass(PaDELDirectoryPropertyEditor.class);
            addProperty("descriptorFile").setPropertyEditorClass(DescriptorFileEditor.class);
            addProperty("compute2D").setCategory("Descriptors");
            addProperty("compute3D").setCategory("Descriptors");
            addProperty("computeFingerprints").setCategory("Descriptors");
            addProperty("removeSalt").setCategory("Standardize");
            addProperty("detectAromaticity").setCategory("Standardize");
            addProperty("standardizeTautomers").setCategory("Standardize");
            addProperty("tautomerFile").setCategory("Standardize").setPropertyEditorClass(PaDELLoadFilePropertyEditor.class);
            addProperty("standardizeNitro").setCategory("Standardize");            
            addProperty("retain3D").setCategory("Standardize");            
            addProperty("convert3D").setCategory("Standardize").setPropertyEditorClass(ConvertTo3DEditor.class);
            addProperty("log").setCategory("Advanced");
            addProperty("maxThreads").setCategory("Advanced");
            addProperty("maxJobsWaiting").setCategory("Advanced");
            addProperty("maxRunTime").setCategory("Advanced");
            addProperty("maxCpdPerFile").setCategory("Advanced");
            addProperty("retainOrder").setCategory("Advanced");
            addProperty("useFilenameAsMolName").setCategory("Advanced");
        }
    }

    public static class DescriptorFileEditor extends PaDELFilePropertyEditor {
        @Override
        protected void selectFile() {
            ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);

            JFileChooser chooser = UserPreferences.getDefaultFileChooser();
            chooser.setDialogTitle(rm.getString("FilePropertyEditor.dialogTitle"));
            chooser.setApproveButtonText(rm.getString("FilePropertyEditor.approveButtonText"));
            chooser.setApproveButtonMnemonic(rm.getChar("FilePropertyEditor.approveButtonMnemonic"));
            String oldFile = (String)getValue();
            File curFile = new File(oldFile);
            if (curFile != null)
            {
                chooser.setCurrentDirectory(curFile);
            }
            customizeFileChooser(chooser);

            if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(editor)) {
                String newFile = chooser.getSelectedFile().getAbsolutePath();
                if (!newFile.endsWith(".csv"))
                {
                    newFile += ".csv";
                }
                File f = new File(newFile);
                if (f.exists())
                {
                    int selection = JOptionPane.showConfirmDialog(null, "File exists. Overwrite file?", "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (selection == JOptionPane.YES_OPTION)
                    {              
                        String text = newFile;
                        textfield.setText(text);
                        firePropertyChange(oldFile, text);
                    }
                }
                else
                {
                    String text = newFile;
                    textfield.setText(text);
                    firePropertyChange(oldFile, text);
                }
            }
        }

        @Override
        protected void customizeFileChooser(JFileChooser chooser) {
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV file", "csv"));
            chooser.setAcceptAllFileFilterUsed(false);
        }
    }

    public static class ConvertTo3DEditor extends ComboBoxPropertyEditor {
        public ConvertTo3DEditor()
        {
            super();
            setAvailableValues(new String[]{FORCEFIELD_NO, FORCEFIELD_MM2, FORCEFIELD_MMFF94});
        }
    }

    public static class BeanRB extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                {"directory", "Molecules directory/file"},
                {"directory.shortDescription", "Directory containing structural files or a structural file."},
                {"descriptorFile", "Descriptor output file"},
                {"descriptorFile.shortDescription", "File to save calculated descriptors."},
                {"removeSalt", "Remove salt"},
                {"removeSalt.shortDescription", "Remove salt from a molecule.<br>This option assumes that the largest fragment is the desired molecule."},
                {"detectAromaticity", "Detect aromaticity"},
                {"detectAromaticity.shortDescription", "Remove existing aromaticity information and automatically detect aromaticity in the molecule before calculation of descriptors."},
                {"standardizeTautomers", "Standardize tautomers"},
                {"standardizeTautomers.shortDescription", "Standardize tautomers."},
                {"tautomerFile", "SMIRKS tautomers file"},
                {"tautomerFile.shortDescription", "File containing SMIRKS tautomers."},
                {"standardizeNitro", "Standardize nitro groups"},
                {"standardizeNitro.shortDescription", "Standardize nitro groups to N(:O):O."},
                {"retain3D", "Retain 3D coordinates"},
                {"retain3D.shortDescription", "Retain 3D coordinates when standardizing structure. However, this may prevent some structures from being standardized."},
                {"convert3D", "Convert to 3D"},
                {"convert3D.shortDescription", "Convert molecule to 3D."},
                {"compute2D", "1D & 2D"},
                {"compute2D.shortDescription", "Calculate 1D and 2D descriptors."},
                {"compute3D", "3D"},
                {"compute3D.shortDescription", "Calculate 3D descriptors."},
                {"computeFingerprints", "Fingerprints"},
                {"computeFingerprints.shortDescription", "Calculate fingerprints."},
                {"log", "Log"},
                {"log.shortDescription", "Create a log file.<br>Name of log file is the name of the descriptors file with a .log extension."},
                {"maxThreads", "Max. threads"},
                {"maxThreads.shortDescription", "Maximum number of threads to use.<br>Use -1 to use as many threads as the number of cpu cores."},
                {"maxJobsWaiting", "Max. waiting jobs"},
                {"maxJobsWaiting.shortDescription", "Maximum number of jobs to store in queue for worker threads to process<br>Use -1 to set it to 50*Max threads."},
                {"maxRunTime", "Max. running time per molecule"},
                {"maxRunTime.shortDescription", "Maximum running time per molecule (in milliseconds)<br>Use -1 for unlimited."},
                {"maxCpdPerFile", "Max. compounds per file"},
                {"maxCpdPerFile.shortDescription", "Maximum number of compounds to be stored in each descriptor file. Use 0 for unlimited."},
                {"retainOrder", "Retain molecules order"},
                {"retainOrder.shortDescription", "Retain order of molecules in structural files for descriptor file. This may lead to large memory use if descriptor calculations are stuck at one molecule as the others will not be written to file and cleared from memory."},
                {"useFilenameAsMolName", "Use filename as molecule name"},
                {"useFilenameAsMolName.shortDescription", "Use filename (minus the extension) as molecule name."}};
        }
    }
}
