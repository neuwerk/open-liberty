package common.utils.topology;

import com.ibm.websphere.simplicity.Node;

public class IAdminAgentNodeContainer extends INodeContainer {

    public IAdminAgentNodeContainer(Cell cell, Node node) {
        super(cell, node);
    }
    
    public IAdminAgent getAdminAgent() {
        return (IAdminAgent)getRootServerProcess();
    }

    @Override
    public NodeContainerType getNodeContainerType() {
        return NodeContainerType.ADMIN_AGENT;
    }
    
}
