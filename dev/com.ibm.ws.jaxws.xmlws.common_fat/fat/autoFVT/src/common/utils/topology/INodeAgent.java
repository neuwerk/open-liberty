package common.utils.topology;

import com.ibm.websphere.simplicity.Server;

public class INodeAgent extends IServerProcess {

    public INodeAgent(INodeContainer node, Server server) {
        super(node, server);
    }
    
    public IManagedNodeContainer getManagedNodeContainer() {
        return (IManagedNodeContainer)this.node;
    }

    @Override
    public ServerProcessType getServerProcessType() {
        return ServerProcessType.NODE_AGENT;
    }
    
}
