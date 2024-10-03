package libpadeldescriptor;

import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.DefaultChemObjectBuilder;

/**
 * Make PubchemFingerprinter in CDK runnable in a separate thread.
 * 
 * @author yapchunwei
 */
public class CDK_PubchemFingerprinter extends CDK_Fingerprint
{
    public CDK_PubchemFingerprinter()
    {
        super();
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        boolean someBooleanValue = true;
        cdkFingerprinter_ = new PubchemFingerprinter(builder, someBooleanValue);
        initDescriptorsValues();
        this.setPrefix("PubchemFP");
    }

        /**
     *
     * @return List of descriptor names.
     */
    @Override
    public String[] getDescriptorNames()
    {
        if (descriptorNames_ == null)
        {
            int maxFingerprints = getFingerprintCount();
            descriptorNames_ = new String[maxFingerprints];
            for (int i=0; i<maxFingerprints; ++i)
            {
                descriptorNames_[i] = prefix_ + Integer.toString(i);
            }
        }

        return descriptorNames_;
    }
}
