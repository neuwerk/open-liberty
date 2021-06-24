package common.utils.topology;

import com.ibm.websphere.simplicity.Server;

public class IAdminAgent extends IServerProcess {

    public IAdminAgent(INodeContainer node, Server server) {
        super(node, server);
    }

    @Override
    public ServerProcessType getServerProcessType() {
        return ServerProcessType.ADMIN_AGENT;
    }
    
}
