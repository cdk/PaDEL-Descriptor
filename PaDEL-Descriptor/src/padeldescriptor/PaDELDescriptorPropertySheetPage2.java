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
public class PaDELDescriptorPropertySheetPage2 extends JPanel {

    final public Bean data = new Bean();
    final public PropertySheetPanel sheet = new PropertySheetPanel();

    public PaDELDescriptorPropertySheetPage2()
    {
        setLayout(LookAndFeelTweaks.createVerticalPercentLayout());

        data.setAcidicGroupCount(true);
        data.setALOGP(true);
        data.setAminoAcidCount(false);
        data.setAPol(true);
        data.setAromaticAtomsCount(true);
        data.setAromaticBondsCount(true);
        data.setAtomCount(true);
        data.setAutocorrelation(true);
        data.setBaryszMatrix(true);
        data.setBasicGroupCount(true);
        data.setBCUT(true);
        data.setBondCount(true);
        data.setBPol(true);
        data.setBurdenModifiedEigenvalues(true);
        data.setCarbonTypes(true);
        data.setChiChain(true);
        data.setChiCluster(true);
        data.setChiPathCluster(true);
        data.setChiPath(true);
        data.setConstitutional(true);
        data.setCrippen(true);
        data.setDetourMatrix(true);
        data.setEccentricConnectivityIndex(true);
        data.setEStateAtomType(true);
        data.setExtendedTopochemicalAtom(true);
        data.setFMF(true);
        data.setFragmentComplexity(true);
        data.setHBondAcceptorCount(true);
        data.setHBondDonorCount(true);
        data.setHybridizationRatio(true);
        data.setInformationContent(true);
        data.setIPMolecularLearning(false);
        data.setKappaShapeIndices(true);
        data.setKierHallSmarts(false);
        data.setLargestChain(true);
        data.setLargestPiSystem(true);
        data.setLongestAliphaticChain(true);
        data.setMannholdLogP(true);
        data.setMcGowanVolume(true);
        data.setMDE(true);
        data.setMLFER(true);
        data.setPathCount(true);
        data.setPetitjeanNumber(true);
        data.setRingCount(true);
        data.setRotatableBondsCount(true);
        data.setRuleOfFive(true);
        data.setTopological(true);
        data.setTopologicalCharge(true);
        data.setTopologicalDistanceMatrix(true);
        data.setTPSA(true);
        data.setVABC(true);
        data.setVAdjMa(true);
        data.setWalkCount(true);
        data.setWeight(true);
        data.setWeightedPath(true);
        data.setWienerNumbers(true);
        data.setXLogP(true);
        data.setZagrebIndex(true);

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
        private boolean AcidicGroupCount;
        private boolean ALOGP;
        private boolean AminoAcidCount;
        private boolean APol;
        private boolean AromaticAtomsCount;
        private boolean AromaticBondsCount;
        private boolean AtomCount;
        private boolean Autocorrelation;
        private boolean BaryszMatrix;
        private boolean BasicGroupCount;
        private boolean BCUT;
        private boolean BondCount;
        private boolean BPol;
        private boolean BurdenModifiedEigenvalues;
        private boolean CarbonTypes;
        private boolean ChiChain;
        private boolean ChiCluster;
        private boolean ChiPathCluster;
        private boolean ChiPath;
        private boolean Constitutional;
        private boolean Crippen;
        private boolean DetourMatrix;
        private boolean EccentricConnectivityIndex;
        private boolean EStateAtomType;
        private boolean ExtendedTopochemicalAtom;
        private boolean FMF;
        private boolean FragmentComplexity;
        private boolean HBondAcceptorCount;
        private boolean HBondDonorCount;
        private boolean HybridizationRatio;
        private boolean InformationContent;
        private boolean IPMolecularLearning;
        private boolean KappaShapeIndices;
        private boolean KierHallSmarts;
        private boolean LargestChain;
        private boolean LargestPiSystem;
        private boolean LongestAliphaticChain;
        private boolean MannholdLogP;
        private boolean McGowanVolume;
        private boolean MDE;
        private boolean MLFER;
        private boolean PathCount;
        private boolean PetitjeanNumber;
        private boolean RingCount;
        private boolean RotatableBondsCount;
        private boolean RuleOfFive;
        private boolean Topological;
        private boolean TopologicalCharge;
        private boolean TopologicalDistanceMatrix;
        private boolean TPSA;
        private boolean VABC;
        private boolean VAdjMa;
        private boolean WalkCount;
        private boolean Weight;
        private boolean WeightedPath;
        private boolean WienerNumbers;
        private boolean XLogP;
        private boolean ZagrebIndex;

        public boolean isAcidicGroupCount() {
            return AcidicGroupCount;
        }

        public void setAcidicGroupCount(boolean AcidicGroupCount) {
            this.AcidicGroupCount = AcidicGroupCount;
        }

        public boolean isALOGP() {
            return ALOGP;
        }

        public void setALOGP(boolean ALOGP) {
            this.ALOGP = ALOGP;
        }

        public boolean isAPol() {
            return APol;
        }

        public void setAPol(boolean APol) {
            this.APol = APol;
        }

        public boolean isAminoAcidCount() {
            return AminoAcidCount;
        }

        public void setAminoAcidCount(boolean AminoAcidCount) {
            this.AminoAcidCount = AminoAcidCount;
        }

        public boolean isAromaticAtomsCount() {
            return AromaticAtomsCount;
        }

        public void setAromaticAtomsCount(boolean AromaticAtomsCount) {
            this.AromaticAtomsCount = AromaticAtomsCount;
        }

        public boolean isAromaticBondsCount() {
            return AromaticBondsCount;
        }

        public void setAromaticBondsCount(boolean AromaticBondsCount) {
            this.AromaticBondsCount = AromaticBondsCount;
        }

        public boolean isAtomCount() {
            return AtomCount;
        }

        public void setAtomCount(boolean AtomCount) {
            this.AtomCount = AtomCount;
        }

        public boolean isAutocorrelation() {
            return Autocorrelation;
        }

        public void setAutocorrelation(boolean Autocorrelation) {
            this.Autocorrelation = Autocorrelation;
        }

        public boolean isBaryszMatrix() {
            return BaryszMatrix;
        }

        public void setBaryszMatrix(boolean BaryszMatrix) {
            this.BaryszMatrix = BaryszMatrix;
        }

        public boolean isBasicGroupCount() {
            return BasicGroupCount;
        }

        public void setBasicGroupCount(boolean BasicGroupCount) {
            this.BasicGroupCount = BasicGroupCount;
        }

        public boolean isBCUT() {
            return BCUT;
        }

        public void setBCUT(boolean BCUT) {
            this.BCUT = BCUT;
        }

        public boolean isBPol() {
            return BPol;
        }

        public void setBPol(boolean BPol) {
            this.BPol = BPol;
        }

        public boolean isBurdenModifiedEigenvalues() {
            return BurdenModifiedEigenvalues;
        }

        public void setBurdenModifiedEigenvalues(boolean BurdenModifiedEigenvalues) {
            this.BurdenModifiedEigenvalues = BurdenModifiedEigenvalues;
        }

        public boolean isBondCount() {
            return BondCount;
        }

        public void setBondCount(boolean BondCount) {
            this.BondCount = BondCount;
        }

        public boolean isCarbonTypes() {
            return CarbonTypes;
        }

        public void setCarbonTypes(boolean CarbonTypes) {
            this.CarbonTypes = CarbonTypes;
        }

        public boolean isChiChain() {
            return ChiChain;
        }

        public void setChiChain(boolean ChiChain) {
            this.ChiChain = ChiChain;
        }

        public boolean isChiCluster() {
            return ChiCluster;
        }

        public void setChiCluster(boolean ChiCluster) {
            this.ChiCluster = ChiCluster;
        }

        public boolean isChiPath() {
            return ChiPath;
        }

        public void setChiPath(boolean ChiPath) {
            this.ChiPath = ChiPath;
        }

        public boolean isChiPathCluster() {
            return ChiPathCluster;
        }

        public void setChiPathCluster(boolean ChiPathCluster) {
            this.ChiPathCluster = ChiPathCluster;
        }
        
        public boolean isConstitutional() {
            return Constitutional;
        }

        public void setConstitutional(boolean Constitutional) {
            this.Constitutional = Constitutional;
        }

        public boolean isCrippen() {
            return Crippen;
        }

        public void setCrippen(boolean Crippen) {
            this.Crippen = Crippen;
        }

        public boolean isDetourMatrix() {
            return DetourMatrix;
        }

        public void setDetourMatrix(boolean DetourMatrix) {
            this.DetourMatrix = DetourMatrix;
        }

        public boolean isEccentricConnectivityIndex() {
            return EccentricConnectivityIndex;
        }

        public void setEccentricConnectivityIndex(boolean EccentricConnectivityIndex) {
            this.EccentricConnectivityIndex = EccentricConnectivityIndex;
        }
        
        public boolean isEStateAtomType() {
            return EStateAtomType;
        }

        public void setEStateAtomType(boolean EStateAtomType) {
            this.EStateAtomType = EStateAtomType;
        }

        public boolean isExtendedTopochemicalAtom() {
            return ExtendedTopochemicalAtom;
        }

        public void setExtendedTopochemicalAtom(boolean ExtendedTopochemicalAtom) {
            this.ExtendedTopochemicalAtom = ExtendedTopochemicalAtom;
        }

        public boolean isFMF() {
            return FMF;
        }

        public void setFMF(boolean FMF) {
            this.FMF = FMF;
        }

        public boolean isFragmentComplexity() {
            return FragmentComplexity;
        }

        public void setFragmentComplexity(boolean FragmentComplexity) {
            this.FragmentComplexity = FragmentComplexity;
        }

        public boolean isHBondAcceptorCount() {
            return HBondAcceptorCount;
        }

        public void setHBondAcceptorCount(boolean HBondAcceptorCount) {
            this.HBondAcceptorCount = HBondAcceptorCount;
        }

        public boolean isHBondDonorCount() {
            return HBondDonorCount;
        }

        public void setHBondDonorCount(boolean HBondDonorCount) {
            this.HBondDonorCount = HBondDonorCount;
        }

        public boolean isHybridizationRatio() {
            return HybridizationRatio;
        }

        public void setHybridizationRatio(boolean HybridizationRatio) {
            this.HybridizationRatio = HybridizationRatio;
        }
        
        public boolean isInformationContent() {
            return InformationContent;
        }

        public void setInformationContent(boolean InformationContent) {
            this.InformationContent = InformationContent;
        }

        public boolean isIPMolecularLearning() {
            return IPMolecularLearning;
        }

        public void setIPMolecularLearning(boolean IPMolecularLearning) {
            this.IPMolecularLearning = IPMolecularLearning;
        }

        public boolean isKappaShapeIndices() {
            return KappaShapeIndices;
        }

        public void setKappaShapeIndices(boolean KappaShapeIndices) {
            this.KappaShapeIndices = KappaShapeIndices;
        }

        public boolean isKierHallSmarts() {
            return KierHallSmarts;
        }

        public void setKierHallSmarts(boolean KierHallSmarts) {
            this.KierHallSmarts = KierHallSmarts;
        }

        public boolean isLargestChain() {
            return LargestChain;
        }

        public void setLargestChain(boolean LargestChain) {
            this.LargestChain = LargestChain;
        }

        public boolean isLargestPiSystem() {
            return LargestPiSystem;
        }

        public void setLargestPiSystem(boolean LargestPiSystem) {
            this.LargestPiSystem = LargestPiSystem;
        }

        public boolean isLongestAliphaticChain() {
            return LongestAliphaticChain;
        }

        public void setLongestAliphaticChain(boolean LongestAliphaticChain) {
            this.LongestAliphaticChain = LongestAliphaticChain;
        }

        public boolean isMDE() {
            return MDE;
        }

        public void setMDE(boolean MDE) {
            this.MDE = MDE;
        }

        public boolean isMLFER() {
            return MLFER;
        }

        public void setMLFER(boolean MLFER) {
            this.MLFER = MLFER;
        }

        public boolean isMannholdLogP() {
            return MannholdLogP;
        }

        public void setMannholdLogP(boolean MannholdLogP) {
            this.MannholdLogP = MannholdLogP;
        }

        public boolean isMcGowanVolume() {
            return McGowanVolume;
        }

        public void setMcGowanVolume(boolean McGowanVolume) {
            this.McGowanVolume = McGowanVolume;
        }

        public boolean isPathCount() {
            return PathCount;
        }

        public void setPathCount(boolean PathCount) {
            this.PathCount = PathCount;
        }

        public boolean isPetitjeanNumber() {
            return PetitjeanNumber;
        }

        public void setPetitjeanNumber(boolean PetitjeanNumber) {
            this.PetitjeanNumber = PetitjeanNumber;
        }

        public boolean isRingCount() {
            return RingCount;
        }

        public void setRingCount(boolean RingCount) {
            this.RingCount = RingCount;
        }

        public boolean isRotatableBondsCount() {
            return RotatableBondsCount;
        }

        public void setRotatableBondsCount(boolean RotatableBondsCount) {
            this.RotatableBondsCount = RotatableBondsCount;
        }

        public boolean isRuleOfFive() {
            return RuleOfFive;
        }

        public void setRuleOfFive(boolean RuleOfFive) {
            this.RuleOfFive = RuleOfFive;
        }
        
        public boolean isTopological() {
            return Topological;
        }
        
        public void setTopological(boolean Topological) {
            this.Topological = Topological;
        }
        
        public boolean isTopologicalCharge() {
            return TopologicalCharge;
        }
        
        public void setTopologicalCharge(boolean TopologicalCharge) {
            this.TopologicalCharge = TopologicalCharge;
        }
        
        public boolean isTopologicalDistanceMatrix() {
            return TopologicalDistanceMatrix;
        }

        public void setTopologicalDistanceMatrix(boolean TopologicalDistanceMatrix) {
            this.TopologicalDistanceMatrix = TopologicalDistanceMatrix;
        }

        public boolean isTPSA() {
            return TPSA;
        }

        public void setTPSA(boolean TPSA) {
            this.TPSA = TPSA;
        }

        public boolean isVABC() {
            return VABC;
        }

        public void setVABC(boolean VABC) {
            this.VABC = VABC;
        }

        public boolean isVAdjMa() {
            return VAdjMa;
        }

        public void setVAdjMa(boolean VAdjMa) {
            this.VAdjMa = VAdjMa;
        }

        public boolean isWalkCount() {
            return WalkCount;
        }

        public void setWalkCount(boolean WalkCount) {
            this.WalkCount = WalkCount;
        }

        public boolean isWeight() {
            return Weight;
        }

        public void setWeight(boolean Weight) {
            this.Weight = Weight;
        }

        public boolean isWeightedPath() {
            return WeightedPath;
        }

        public void setWeightedPath(boolean WeightedPath) {
            this.WeightedPath = WeightedPath;
        }

        public boolean isWienerNumbers() {
            return WienerNumbers;
        }

        public void setWienerNumbers(boolean WienerNumbers) {
            this.WienerNumbers = WienerNumbers;
        }

        public boolean isXLogP() {
            return XLogP;
        }

        public void setXLogP(boolean XLogP) {
            this.XLogP = XLogP;
        }

        public boolean isZagrebIndex() {
            return ZagrebIndex;
        }

        public void setZagrebIndex(boolean ZagrebIndex) {
            this.ZagrebIndex = ZagrebIndex;
        }
    }

    public static class BeanBeanInfo extends BaseBeanInfo {
        public BeanBeanInfo() {
            super(Bean.class);

            addProperty("AcidicGroupCount");
            addProperty("ALOGP");
//            addProperty("AminoAcidCount");
            addProperty("APol");
            addProperty("AromaticAtomsCount");
            addProperty("AromaticBondsCount");
            addProperty("AtomCount");
            addProperty("Autocorrelation");
            addProperty("BaryszMatrix");
            addProperty("BasicGroupCount");
            addProperty("BCUT");
            addProperty("BondCount");
            addProperty("BPol");
            addProperty("BurdenModifiedEigenvalues");
            addProperty("CarbonTypes");
            addProperty("ChiChain");
            addProperty("ChiCluster");
            addProperty("ChiPathCluster");
            addProperty("ChiPath");
            addProperty("Constitutional");
            addProperty("Crippen");
            addProperty("DetourMatrix");
            addProperty("EccentricConnectivityIndex");
            addProperty("EStateAtomType");
            addProperty("ExtendedTopochemicalAtom");
            addProperty("FMF");
            addProperty("FragmentComplexity");
            addProperty("HBondAcceptorCount");
            addProperty("HBondDonorCount");
            addProperty("HybridizationRatio");
            addProperty("InformationContent");
//            addProperty("IPMolecularLearning");
            addProperty("KappaShapeIndices");
//            addProperty("KierHallSmarts");
            addProperty("LargestChain");
            addProperty("LargestPiSystem");
            addProperty("LongestAliphaticChain");
            addProperty("MannholdLogP");
            addProperty("McGowanVolume");
            addProperty("MDE");
            addProperty("MLFER");
            addProperty("PathCount");
            addProperty("PetitjeanNumber");
            addProperty("RingCount");
            addProperty("RotatableBondsCount");
            addProperty("RuleOfFive");
            addProperty("Topological");
            addProperty("TopologicalCharge");
            addProperty("TopologicalDistanceMatrix");
            addProperty("TPSA");
            addProperty("VABC");
            addProperty("VAdjMa");
            addProperty("WalkCount");
            addProperty("Weight");
            addProperty("WeightedPath");
            addProperty("WienerNumbers");
            addProperty("XLogP");
            addProperty("ZagrebIndex");
        }
    }

    public static class BeanRB extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                {"AcidicGroupCount", "AcidicGroupCount"},
                {"AcidicGroupCount.shortDescription","Number of acidic groups. The list of acidic groups is defined by SMARTS originally presented in JOELib."},
                {"ALOGP", "ALOGP"},
                {"ALOGP.shortDescription","ALOGP (Ghose-Crippen LogKow) and the Ghose-Crippen molar refractivity."},
//                {"AminoAcidCount", "AminoAcidCount"},
//                {"AminoAcidCount.shortDescription",""},
                {"APol", "APol"},
                {"APol.shortDescription","Sum of the atomic polarizabilities (including implicit hydrogens)."},
                {"AromaticAtomsCount", "AromaticAtomsCount"},
                {"AromaticAtomsCount.shortDescription","Number of aromatic atoms."},
                {"AromaticBondsCount", "AromaticBondsCount"},
                {"AromaticBondsCount.shortDescription","Number of aromatic bonds."},
                {"AtomCount", "AtomCount"},
                {"AtomCount.shortDescription","Total number of atoms and number of atoms of a certain element type."},
                {"Autocorrelation", "Autocorrelation"},
                {"Autocorrelation.shortDescription","Autocorrelation descriptors (Moreau-Broto, Moran, Geary)."},
                {"BaryszMatrix", "BaryszMatrix"},
                {"BaryszMatrix.shortDescription","Barysz matrix descriptors."},
                {"BasicGroupCount", "BasicGroupCount"},
                {"BasicGroupCount.shortDescription","Number of basic groups. The list of basic groups is defined by SMARTS originally presented in JOELib."},
                {"BCUT", "BCUT"},
                {"BCUT.shortDescription","Based on a weighted version of the Burden matrix, which takes into account both the connectivity as well as atomic properties of a molecule."},
                {"BondCount", "BondCount"},
                {"BondCount.shortDescription","Number of bonds of a certain bond order."},
                {"BPol", "BPol"},
                {"BPol.shortDescription","Sum of the absolute value of the difference between atomic polarizabilities of all bonded atoms in the molecule (including implicit hydrogens)."},
                {"BurdenModifiedEigenvalues", "BurdenModifiedEigenvalues"},
                {"BurdenModifiedEigenvalues.shortDescription","Burden modified eigenvalues."},
                {"CarbonTypes", "CarbonTypes"},
                {"CarbonTypes.shortDescription","Topological descriptor characterizing the carbon connectivity."},
                {"ChiChain", "ChiChain"},
                {"ChiChain.shortDescription","Simple and valence chi chain descriptors of orders 3, 4, 5, 6 and 7."},
                {"ChiCluster", "ChiCluster"},
                {"ChiCluster.shortDescription","Simple and valence chi cluster descriptors of orders 3, 4,5 and 6."},
                {"ChiPathCluster", "ChiPathCluster"},
                {"ChiPathCluster.shortDescription","Simple and valence chi path cluster descriptors of orders 3, 4,5 and 6."},
                {"ChiPath", "ChiPath"},
                {"ChiPath.shortDescription","Simple and valence chi path descriptors of orders 0 to 7."},
                {"Constitutional", "Constitutional"},
                {"Constitutional.shortDescription","Constitutional descriptors like sum/mean of atomic vDW, electronegativities, polarizabilities."},
                {"Crippen", "Crippen"},
                {"Crippen.shortDescription","Wildman-Crippen LogP and MR."},
                {"DetourMatrix", "DetourMatrix"},
                {"DetourMatrix.shortDescription","Detour matrix descriptors."},
                {"EccentricConnectivityIndex", "EccentricConnectivityIndex"},
                {"EccentricConnectivityIndex.shortDescription","A topological descriptor combining distance and adjacency information."},
                {"EStateAtomType", "EStateAtomType"},
                {"EStateAtomType.shortDescription","Electrotopological state descriptors"},
                {"ExtendedTopochemicalAtom", "ExtendedTopochemicalAtom"},
                {"ExtendedTopochemicalAtom.shortDescription","Extended Topochemical Atom (ETA) descriptors"},
                {"FMF", "FMF"},
                {"FMF.shortDescription","Complexity of a molecule."},
                {"FragmentComplexity", "FragmentComplexity"},
                {"FragmentComplexity.shortDescription","Complexity of a system. C=abs(B^2-A^2+A)+H/100, where C=complexity, A=number of non-hydrogen atoms, B=number of bonds and H=number of heteroatoms."},
                {"HBondAcceptorCount", "HBondAcceptorCount"},
                {"HBondAcceptorCount.shortDescription","Number of hydrogen bond acceptors."},
                {"HBondDonorCount", "HBondDonorCount"},
                {"HBondDonorCount.shortDescription","Number of hydrogen bond donors."},
                {"HybridizationRatio", "HybridizationRatio"},
                {"HybridizationRatio.shortDescription","Fraction of sp3 carbons to sp2 carbons."},
                {"InformationContent", "InformationContent"},
                {"InformationContent.shortDescription","Multigraph information content indices."},
//                {"IPMolecularLearning", "IPMolecularLearning"},
//                {"IPMolecularLearning.shortDescription","Ionization potential of a molecule."},
                {"KappaShapeIndices", "KappaShapeIndices"},
                {"KappaShapeIndices.shortDescription","Kier and Hall kappa molecular shape indices compare the molecular graph with minimal and maximal molecular graphs and are intended to capture different aspects of molecular shape."},
//                {"KierHallSmarts", "KierHallSmarts"},
//                {"KierHallSmarts.shortDescription","A fragment count descriptor that uses e-state fragments."},
                {"LargestChain", "LargestChain"},
                {"LargestChain.shortDescription","Number of atoms in the largest chain."},
                {"LargestPiSystem", "LargestPiSystem"},
                {"LargestPiSystem.shortDescription","Number of atoms in the largest pi system."},
                {"LongestAliphaticChain", "LongestAliphaticChain"},
                {"LongestAliphaticChain.shortDescription","Number of atoms in the longest aliphatic chain."},
                {"MannholdLogP", "MannholdLogP"},
                {"MannholdLogP.shortDescription","Prediction of logP based on the number of carbon and hetero atoms."},
                {"McGowanVolume", "McGowanVolume"},
                {"McGowanVolume.shortDescription","McGowan characteristic volume."},
                {"MDE", "MDE"},
                {"MDE.shortDescription","Molecular Distance Edge descriptors."},
                {"MLFER", "MLFER"},
                {"MLFER.shortDescription","Molecular linear free energy relation."},
                {"PathCount", "PathCount"},
                {"PathCount.shortDescription","Path counts."},
                {"PetitjeanNumber", "PetitjeanNumber"},
                {"PetitjeanNumber.shortDescription","Eccentricity of a vertex corresponds to the distance from that vertex to the most remote vertex in the graph."},
                {"RingCount", "RingCount"},
                {"RingCount.shortDescription","Number of rings"},
                {"RotatableBondsCount", "RotatableBondsCount"},
                {"RotatableBondsCount.shortDescription","Number of rotatable bonds"},
                {"RuleOfFive", "RuleOfFive"},
                {"RuleOfFive.shortDescription","Number of failures of the Lipinski's Rule Of 5"},
                {"Topological", "Topological"},
                {"Topological.shortDescription","Topological descriptors, such as topological radius, topological diameter and topological shape"},
                {"TopologicalCharge", "TopologicalCharge"},
                {"TopologicalCharge.shortDescription","Topological charge descriptors."},
                {"TopologicalDistanceMatrix", "TopologicalDistanceMatrix"},
                {"TopologicalDistanceMatrix.shortDescription","Topological distance matrix descriptors."},
                {"TPSA", "TPSA"},
                {"TPSA.shortDescription","Topological polar surface area based on fragment contributions (TPSA)"},
                {"VABC", "VABC"},
                {"VABC.shortDescription","Van der Waals volume calculated using the method proposed in [Zhao, Yuan H. and Abraham, Michael H. and Zissimos, Andreas M., Fast Calculation of van der Waals Volume as a Sum of Atomic and Bond Contributions and Its Application to Drug Compounds, The Journal of Organic Chemistry, 2003, 68:7368-7373]."},
                {"VAdjMa", "VAdjMa"},
                {"VAdjMa.shortDescription","Vertex adjacency information (magnitude): 1 + log2 m where m is the number of heavy-heavy bonds. If m is zero, then zero is returned."},
                {"WalkCount", "WalkCount"},
                {"WalkCount.shortDescription","Walk counts."},
                {"Weight", "Weight"},
                {"Weight.shortDescription","Molecular weight."},
                {"WeightedPath", "WeightedPath"},
                {"WeightedPath.shortDescription","Characterize molecular branching."},
                {"WienerNumbers", "WienerNumbers"},
                {"WienerNumbers.shortDescription","Wiener Path number and the Wiener Polarity Number."},
                {"XLogP", "XLogP"},
                {"XLogP.shortDescription","Prediction of logP based on the atom-type method called XLogP"},
                {"ZagrebIndex", "ZagrebIndex"},
                {"ZagrebIndex.shortDescription","Sum of the squares of atom degree over all heavy atoms i."}};
        }
    }
}
