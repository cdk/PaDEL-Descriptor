package libpadeldescriptor;

import org.openscience.cdk.qsar.descriptors.molecular.ChiPathClusterDescriptor;

/**
 * Make ChiPathClusterDescriptor in CDK runnable in a separate thread.
 * 
 * @author yapchunwei
 */
public class CDK_ChiPathClusterDescriptor extends CDK_Descriptor
{
    public CDK_ChiPathClusterDescriptor()
    {
        super();
        cdkDescriptor_ = new ChiPathClusterDescriptor();
        initDescriptorsValues();
    }
}
