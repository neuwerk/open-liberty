package common.utils.topology;

import com.ibm.websphere.simplicity.Node;

public class IDmgrNodeContainer extends INodeContainer {

    public IDmgrNodeContainer(Cell cell, Node node) {
        super(cell, node);
    }

    @Override
    public NodeContainerType getNodeContainerType() {
        return NodeContainerType.DEPLOYMENT_MANAGER;
    }
    
    public IDmgr getDmgr() {
        return (IDmgr)getRootServerProcess();
    }
}
