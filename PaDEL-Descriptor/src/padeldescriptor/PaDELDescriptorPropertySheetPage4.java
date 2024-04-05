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
import com.l2fprod.common.demo.BeanBinder;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.swing.LookAndFeelTweaks;

import java.util.ListResourceBundle;

import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * PropertySheetPage. <br>
 *  
 */
public class PaDELDescriptorPropertySheetPage4 extends JPanel {

    final public Bean data = new Bean();
    final public PropertySheetPanel sheet = new PropertySheetPanel();

    public PaDELDescriptorPropertySheetPage4()
    {
        setLayout(LookAndFeelTweaks.createVerticalPercentLayout());

        data.setFingerprinter(false);
        data.setExtendedFingerprinter(false);
        data.setEStateFingerprinter(false);
        data.setGraphOnlyFingerprinter(false);
        data.setMACCSFingerprinter(false);
        data.setPubchemFingerprinter(true);
        data.setSubstructureFingerprinter(false);
        data.setSubstructureFingerprintCount(false);
        data.setKlekotaRothFingerprinter(false);
        data.setKlekotaRothFingerprintCount(false);
        data.setAtomPairs2DFingerprinter(false);
        data.setAtomPairs2DFingerprintCount(false);

        sheet.setMode(PropertySheet.VIEW_AS_FLAT_LIST);
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
    }

    public static class Bean {
        private boolean Fingerprinter;
        private boolean ExtendedFingerprinter;
        private boolean EStateFingerprinter;
        private boolean GraphOnlyFingerprinter;
        private boolean MACCSFingerprinter;
        private boolean PubchemFingerprinter;
        private boolean SubstructureFingerprinter;
        private boolean SubstructureFingerprintCount;
        private boolean KlekotaRothFingerprinter;
        private boolean KlekotaRothFingerprintCount;
        private boolean AtomPairs2DFingerprinter;
        private boolean AtomPairs2DFingerprintCount;
        
        public boolean isAtomPairs2DFingerprinter() {
            return AtomPairs2DFingerprinter;
        }

        public void setAtomPairs2DFingerprinter(boolean AtomPairs2DFingerprinter) {
            this.AtomPairs2DFingerprinter = AtomPairs2DFingerprinter;
        }

        public boolean isAtomPairs2DFingerprintCount() {
            return AtomPairs2DFingerprintCount;
        }

        public void setAtomPairs2DFingerprintCount(boolean AtomPairs2DFingerprintCount) {
            this.AtomPairs2DFingerprintCount = AtomPairs2DFingerprintCount;
        }

        public boolean isEStateFingerprinter() {
            return EStateFingerprinter;
        }

        public void setEStateFingerprinter(boolean EStateFingerprinter) {
            this.EStateFingerprinter = EStateFingerprinter;
        }

        public boolean isExtendedFingerprinter() {
            return ExtendedFingerprinter;
        }

        public void setExtendedFingerprinter(boolean ExtendedFingerprinter) {
            this.ExtendedFingerprinter = ExtendedFingerprinter;
        }

        public boolean isFingerprinter() {
            return Fingerprinter;
        }

        public void setFingerprinter(boolean Fingerprinter) {
            this.Fingerprinter = Fingerprinter;
        }

        public boolean isGraphOnlyFingerprinter() {
            return GraphOnlyFingerprinter;
        }

        public void setGraphOnlyFingerprinter(boolean GraphOnlyFingerprinter) {
            this.GraphOnlyFingerprinter = GraphOnlyFingerprinter;
        }

        public boolean isKlekotaRothFingerprinter() {
            return KlekotaRothFingerprinter;
        }

        public void setKlekotaRothFingerprinter(boolean KlekotaRothFingerprinter) {
            this.KlekotaRothFingerprinter = KlekotaRothFingerprinter;
        }

        public boolean isKlekotaRothFingerprintCount() {
            return KlekotaRothFingerprintCount;
        }

        public void setKlekotaRothFingerprintCount(boolean KlekotaRothFingerprintCount) {
            this.KlekotaRothFingerprintCount = KlekotaRothFingerprintCount;
        }

        public boolean isMACCSFingerprinter() {
            return MACCSFingerprinter;
        }

        public void setMACCSFingerprinter(boolean MACCSFingerprinter) {
            this.MACCSFingerprinter = MACCSFingerprinter;
        }

        public boolean isPubchemFingerprinter() {
            return PubchemFingerprinter;
        }

        public void setPubchemFingerprinter(boolean PubchemFingerprinter) {
            this.PubchemFingerprinter = PubchemFingerprinter;
        }

        public boolean isSubstructureFingerprinter() {
            return SubstructureFingerprinter;
        }

        public void setSubstructureFingerprinter(boolean SubstructureFingerprinter) {
            this.SubstructureFingerprinter = SubstructureFingerprinter;
        }

        public boolean isSubstructureFingerprintCount() {
            return SubstructureFingerprintCount;
        }

        public void setSubstructureFingerprintCount(boolean SubstructureFingerprintCount) {
            this.SubstructureFingerprintCount = SubstructureFingerprintCount;
        }
    }

    public static class BeanBeanInfo extends BaseBeanInfo {
        public BeanBeanInfo() {
            super(Bean.class);

            addProperty("Fingerprinter");
            addProperty("ExtendedFingerprinter");
            addProperty("EStateFingerprinter");
            addProperty("GraphOnlyFingerprinter");
            addProperty("MACCSFingerprinter");
            addProperty("PubchemFingerprinter");
            addProperty("SubstructureFingerprinter");
            addProperty("SubstructureFingerprintCount");
            addProperty("KlekotaRothFingerprinter");
            addProperty("KlekotaRothFingerprintCount");
            addProperty("AtomPairs2DFingerprinter");
            addProperty("AtomPairs2DFingerprintCount");
        }
    }

    public static class BeanRB extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                {"Fingerprinter", "Fingerprinter"},
                {"Fingerprinter.shortDescription","Fingerprint of length 1024 and search depth of 8."},
                {"ExtendedFingerprinter", "ExtendedFingerprinter"},
                {"ExtendedFingerprinter.shortDescription","Extends the Fingerprinter with additional bits describing ring features."},
                {"EStateFingerprinter", "EStateFingerprinter"},
                {"EStateFingerprinter.shortDescription","E-State fragments."},
                {"GraphOnlyFingerprinter", "GraphOnlyFingerprinter"},
                {"GraphOnlyFingerprinter.shortDescription","Specialized version of the Fingerprinter which does not take bond orders into account."},
                {"MACCSFingerprinter", "MACCSFingerprinter"},
                {"MACCSFingerprinter.shortDescription","MACCS keys."},
                {"PubchemFingerprinter", "PubchemFingerprinter"},
                {"PubchemFingerprinter.shortDescription","Pubchem fingerprint."},
                {"SubstructureFingerprinter", "SubstructureFingerprinter"},
                {"SubstructureFingerprinter.shortDescription","Presence of SMARTS Patterns for Functional Group Classification by Christian Laggner."},
                {"SubstructureFingerprintCount", "SubstructureFingerprintCount"},
                {"SubstructureFingerprintCount.shortDescription","Count of SMARTS Patterns for Functional Group Classification by Christian Laggner."},
                {"KlekotaRothFingerprinter", "KlekotaRothFingerprinter"},
                {"KlekotaRothFingerprinter.shortDescription","Presence of chemical substructures."},
                {"KlekotaRothFingerprintCount", "KlekotaRothFingerprintCount"},
                {"KlekotaRothFingerprintCount.shortDescription","Count of chemical substructures."},
                {"AtomPairs2DFingerprinter", "AtomPairs2DFingerprinter"},
                {"AtomPairs2DFingerprinter.shortDescription","Presence of atom pairs at various topological distances."},
                {"AtomPairs2DFingerprintCount", "AtomPairs2DFingerprintCount"},
                {"AtomPairs2DFingerprintCount.shortDescription","Count of atom pairs at various topological distances."}};
        }
    }
}
