package common.utils.topology;

import com.ibm.websphere.simplicity.Node;

public class IBaseNodeContainer extends INodeContainer {
    
    public IBaseNodeContainer(Cell cell, Node node) {
        super(cell, node);
    }

    @Override
    public NodeContainerType getNodeContainerType() {
        return NodeContainerType.BASE;
    }
    
    public IAppServer getAppServer() {
        return (IAppServer)getRootServerProcess();
    }
}
