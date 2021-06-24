package common.utils.topology;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.Topology;

public abstract class IServerProcess {

    protected INodeContainer node;
    protected Server server;
    
    public IServerProcess(INodeContainer node, Server server) {
        this.node = node;
        this.server = server;
    }
    
    public Cell getCell() {
        return this.node.getCell();
    }
    
    public String getDeploymentTarget() {
        if(this.server instanceof ApplicationServer) {
            return ((ApplicationServer)server).getMappingName();
        } else {
            return null;
        }
    }
    
    public String getKey() {
        return this.server.getBootstrapFileKey();
    }
    
    public IMachine getMachine() {
        return this.node.getMachine();
    }
    
    public INodeContainer getNodeContainer() {
        return this.node;
    }
    
    public Integer getPort(Ports port) {
        try {
            if(this.server instanceof ApplicationServer) {
                PortType portType = PortType.valueOf(port.getPortName());
                return ((ApplicationServer)this.server).getPortNumber(portType);
            }
            return null;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Map<Ports, Integer> getPortMap() {
        try {
            if(this.server instanceof ApplicationServer) {
                Map<Ports, Integer> ret = new HashMap<Ports, Integer>();
                PortType[] portTypes = PortType.values();
                Integer port = null;
                for(int i = 0; i < portTypes.length; ++i) {
                    port = ((ApplicationServer)this.server).getPortNumber(portTypes[i]);
                    if(port != null) {
                        ret.put(Ports.valueOf(portTypes[i].getPortName()), port);
                    }
                }
                return ret;
            } else {
                return null;
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getServerName() {
        return this.server.getName();
    }
    
    public abstract ServerProcessType getServerProcessType();
    
    public File getTopologyFile() {
        return Topology.getBootStrappingFile();
    }
    
    public IWASInstall getWASInstall() {
        return this.node.getWASInstall();
    }
    
    public Server getSimplicityServer() {
        return this.server;
    }
}
