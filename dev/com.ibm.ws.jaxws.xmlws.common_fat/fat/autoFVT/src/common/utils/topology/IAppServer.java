package common.utils.topology;

import com.ibm.websphere.simplicity.Server;

public class IAppServer extends IServerProcess {

    public IAppServer(INodeContainer node, Server server) {
        super(node, server);
    }

    @Override
    public ServerProcessType getServerProcessType() {
        return ServerProcessType.APPLICATION_SERVER;
    }
    
}
