package common.utils.topology;

import com.ibm.websphere.simplicity.Server;

public class IDmgr extends IServerProcess {

    public IDmgr(INodeContainer node, Server server) {
        super(node, server);
    }

    public IDmgrNodeContainer getDmgrNodeContainer() {
        return (IDmgrNodeContainer)this.node;
    }
    
    @Override
    public ServerProcessType getServerProcessType() {
        return ServerProcessType.DEPLOYMENT_MANAGER;
    }
    
}
