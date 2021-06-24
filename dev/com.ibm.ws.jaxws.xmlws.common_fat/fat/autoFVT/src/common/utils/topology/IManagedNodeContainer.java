package common.utils.topology;

import java.util.HashSet;
import java.util.Set;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.ServerType;

public class IManagedNodeContainer extends INodeContainer {

    public IManagedNodeContainer(Cell cell, Node node) {
        super(cell, node);
    }
    
    public Set<IAppServer> getAppServers() {
        try {
            Set<Server> servers = this.node.getServers();
            Set<IAppServer> ret = new HashSet<IAppServer>();
            for(Server server : servers) {
                if(server.getServerType() == ServerType.APPLICATION_SERVER) {
                    ret.add(new IAppServer(this, server));
                }
            }
            return ret;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public INodeAgent getNodeAgent() {
        return (INodeAgent)getRootServerProcess();
    }

    @Override
    public NodeContainerType getNodeContainerType() {
        return NodeContainerType.MANAGED;
    }
    
}
