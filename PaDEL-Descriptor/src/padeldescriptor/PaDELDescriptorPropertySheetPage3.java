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
public class PaDELDescriptorPropertySheetPage3 extends JPanel {

    final public Bean data = new Bean();
    final public PropertySheetPanel sheet = new PropertySheetPanel();

    public PaDELDescriptorPropertySheetPage3()
    {
        setLayout(LookAndFeelTweaks.createVerticalPercentLayout());

        data.setAutocorrelation3D(true);
        data.setCPSA(true);
        data.setGravitationalIndex(true);
        data.setLengthOverBreadth(true);
        data.setMomentOfInertia(true);
        data.setPetitjeanShapeIndex(true);
        data.setRDF(true);
        data.setWHIM(true);

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
        private boolean Autocorrelation3D;
        private boolean CPSA;
        private boolean GravitationalIndex;
        private boolean LengthOverBreadth;
        private boolean MomentOfInertia;
        private boolean PetitjeanShapeIndex;
        private boolean RDF;
        private boolean WHIM;

        public boolean isAutocorrelation3D() {
            return Autocorrelation3D;
        }

        public void setAutocorrelation3D(boolean Autocorrelation3D) {
            this.Autocorrelation3D = Autocorrelation3D;
        }

        public boolean isCPSA() {
            return CPSA;
        }

        public void setCPSA(boolean CPSA) {
            this.CPSA = CPSA;
        }

        public boolean isGravitationalIndex() {
            return GravitationalIndex;
        }

        public void setGravitationalIndex(boolean GravitationalIndex) {
            this.GravitationalIndex = GravitationalIndex;
        }

        public boolean isLengthOverBreadth() {
            return LengthOverBreadth;
        }

        public void setLengthOverBreadth(boolean LengthOverBreadth) {
            this.LengthOverBreadth = LengthOverBreadth;
        }

        public boolean isMomentOfInertia() {
            return MomentOfInertia;
        }

        public void setMomentOfInertia(boolean MomentOfInertia) {
            this.MomentOfInertia = MomentOfInertia;
        }

        public boolean isPetitjeanShapeIndex() {
            return PetitjeanShapeIndex;
        }

        public void setPetitjeanShapeIndex(boolean PetitjeanShapeIndex) {
            this.PetitjeanShapeIndex = PetitjeanShapeIndex;
        }

        public boolean isRDF() {
            return RDF;
        }

        public void setRDF(boolean RDF) {
            this.RDF = RDF;
        }

        public boolean isWHIM() {
            return WHIM;
        }

        public void setWHIM(boolean WHIM) {
            this.WHIM = WHIM;
        }

    }

    public static class BeanBeanInfo extends BaseBeanInfo {
        public BeanBeanInfo() {
            super(Bean.class);

            addProperty("Autocorrelation3D");
            addProperty("CPSA");
            addProperty("GravitationalIndex");
            addProperty("LengthOverBreadth");
            addProperty("MomentOfInertia");
            addProperty("PetitjeanShapeIndex");
            addProperty("RDF");
            addProperty("WHIM");
        }
    }

    public static class BeanRB extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                {"Autocorrelation3D", "Autocorrelation3D"},
                {"Autocorrelation3D.shortDescription","3D topological distance based autocorrelation."},
                {"CPSA", "CPSA"},
                {"CPSA.shortDescription","Charged Partial Surface Area (CPSA) descriptors, which are related to the Polar Surface Area descriptors."},
                {"GravitationalIndex", "GravitationalIndex"},
                {"GravitationalIndex.shortDescription","Characterize the mass distribution of the molecule."},
                {"LengthOverBreadth", "LengthOverBreadth"},
                {"LengthOverBreadth.shortDescription","Length over breadth descriptors."},
                {"MomentOfInertia", "MomentOfInertia"},
                {"MomentOfInertia.shortDescription","Moment of inertia and radius of gyration. Moment of inertia (MI) values characterize the mass distribution of a molecule."},
                {"PetitjeanShapeIndex", "PetitjeanShapeIndex"},
                {"PetitjeanShapeIndex.shortDescription","Petitjean shape indices."},
                {"RDF", "RDF"},
                {"RDF.shortDescription","Radial distribution function."},
                {"WHIM", "WHIM"},
                {"WHIM.shortDescription","Weighted Holistic Invariant Molecular (WHIM)."}};
        }
    }
}
