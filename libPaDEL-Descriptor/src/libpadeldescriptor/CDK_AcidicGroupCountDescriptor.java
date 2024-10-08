package libpadeldescriptor;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.descriptors.molecular.AcidicGroupCountDescriptor;

/**
 * Make AcidicGroupCountDescriptor in CDK runnable in a separate thread.
 * 
 * @author yapchunwei
 */
public class CDK_AcidicGroupCountDescriptor extends CDK_Descriptor
{
    public CDK_AcidicGroupCountDescriptor()
    {
        super();
        try
        {
            cdkDescriptor_ = new AcidicGroupCountDescriptor();
            initDescriptorsValues();
        }
        finally
        {
        }
    }
}
