package common.utils.topology.visitor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.util.AlphabeticScopeComparator;
import com.ibm.websphere.simplicity.util.ApplicationServerFilter;
import com.ibm.websphere.simplicity.util.CollectionUtility;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import common.utils.topology.Cell;
import common.utils.topology.CellFactory;
import common.utils.topology.IAppServer;
import common.utils.topology.INodeContainer;
import common.utils.topology.IServerProcess;
import common.utils.topology.NodeContainerType;
import common.utils.topology.ServerProcessType;
import common.utils.topology.WASVersion;

public class QueryDefaultNode {

	/**
	 * The default IAppServer based on the predefined query definition
	 */
    @Deprecated
	public static IAppServer defaultAppServer = null;
	/**
	 * The default INodeContainer based on the predefined query definition
	 */
    @Deprecated
	public static INodeContainer defaultNodeContainer = null;
	/**
	 * Initialize static members
	 */
	static {
		QueryDefaultNode.refreshDefaults();
	}
    
    private Cell cell = null;
    private String fvtWorkDir = null;
    private String key = null;
    private NodeContainerType type = null;
    private String nodeName = null;
    private String profileDir = null;
    private String profileName = null;
    private String topologyFile = null;
    private WASVersion version = null;

	/**
	 * Construct a QueryDefaultNode with predefined query parameters for the web services framework
	 * 
	 */
    @Deprecated
	public QueryDefaultNode() {
	}

	/**
	 * Set the static default members based on the current topology of <code>Cell.getDefaultCell()</code>
	 */
    @Deprecated
	public static void refreshDefaults() {
        try {
            Topology.init();
            List<com.ibm.websphere.simplicity.Cell> cells = Topology.getCells();
            Set<Server> servers = null;
            Server server = null;
            for(com.ibm.websphere.simplicity.Cell cell : cells) {
                servers = cell.getServers();
                List<Server> sorted = CollectionUtility.sort(CollectionUtility.filter(servers, new ApplicationServerFilter()), new AlphabeticScopeComparator<Server>());
                if(sorted.size() > 0) {
                    server = sorted.get(0);
                    break;
                }
            }
            if(server != null) {
                Cell cell = CellFactory.getCell(server.getCell());
                for(INodeContainer node : cell.getNodeContainers()) {
                    if(node.getNodeName().equals(server.getNode().getName())) {
                        for(IServerProcess iserver : node.getServerProcesses()) {
                            if(iserver.getServerName().equals(server.getName())) {
                                defaultAppServer = (IAppServer)iserver;
                            }
                        }
                    }
                }
            }
            if(defaultAppServer != null) {
                defaultNodeContainer = defaultAppServer.getNodeContainer();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * Get the first IAppServer found the default node container. This is not guarranteed to return the same app server
	 * with each call
	 * 
	 * @return The default IAppServer based on this Object's node query. If no IAppServer is found, null is returned
	 */
    @Deprecated
	public IAppServer getDefaultAppServer() {
		INodeContainer node = getDefaultNodeContainer();
        Set<IServerProcess> servers = node.getServerProcesses();
        if(servers != null && servers.size() > 0) {
            Iterator<IServerProcess> iter = servers.iterator();
            IServerProcess server = null;
            while(iter.hasNext()) {
                server = iter.next();
                if(server.getServerProcessType() == ServerProcessType.APPLICATION_SERVER) {
                    return (IAppServer)server;
                }
            }
        }
        return null;
	}

	/**
	 * Get the first INodeContainer that matches the node query and has at least one application server. This is not
	 * guarranteed to return the same node container with each call
	 * 
	 * @return The default INodeContainer based on this Object's query. If no INodeContainer is found, null is returned.
	 */
    @Deprecated
	public INodeContainer getDefaultNodeContainer() {
        try {
    		Topology.init();
            INodeContainer ret = null;
            Set<INodeContainer> nodes = new HashSet<INodeContainer>();
            
            List<com.ibm.websphere.simplicity.Cell> cells = Topology.getCells();
            if(this.cell != null) {
                for(com.ibm.websphere.simplicity.Cell cell : cells) {
                    if(cell.getName().equals(this.cell.getCellName())) {
                        nodes.addAll(CellFactory.getCell(cell).getNodeContainers());
                    }
                }
            } else {
                for(com.ibm.websphere.simplicity.Cell cell : cells) {
                    nodes.addAll(CellFactory.getCell(cell).getNodeContainers());
                }
            }
            
            
            for(INodeContainer node : nodes) {
                if(fvtWorkDir != null && !fvtWorkDir.equals(node.getFvtWorkDir())) {
                    continue;
                }
                if(key != null && !key.equals(node.getKey())) {
                    continue;
                }
                if(type != null && !type.equals(node.getNodeContainerType())) {
                    continue;
                }
                if(nodeName != null && !nodeName.equals(node.getNodeName())) {
                    continue;
                }
                if(profileDir != null && !profileDir.equals(node.getProfileDir())) {
                    continue;
                }
                if(profileName != null && !profileName.equals(node.getProfileName())) {
                    continue;
                }
                if(topologyFile != null && !topologyFile.equals(node.getTopologyFile())) {
                    continue;
                }
                if(version != null && !version.equals(node.getWASVersion())) {
                    continue;
                }
                return node;
            }
            
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        
	}

	/**
	 * Set the Cell to query.
	 * 
	 * @param cell
	 */
    @Deprecated
	public void setCell(Cell cell) {
		this.cell = cell;
	}

	/**
	 * Set the fvtWorkDir to query
	 * 
	 * @param fvtWorkDir
	 */
    @Deprecated
	public void setFvtWorkDir(String fvtWorkDir) {
		this.fvtWorkDir = fvtWorkDir;
	}

	/**
	 * Set the key to query
	 * 
	 * @param key
	 */
    @Deprecated
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Set the NodeContainerType to query
	 * 
	 * @param type
	 */
    @Deprecated
	public void setNodeContainerType(NodeContainerType type) {
		this.type = type;
	}

	/**
	 * Set the node name to query
	 * 
	 * @param nodeName
	 */
    @Deprecated
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Set the profile directory to query
	 * 
	 * @param profileDir
	 */
    @Deprecated
	public void setProfileDir(String profileDir) {
		this.profileDir = profileDir;
	}

	/**
	 * Set the profile name to query
	 * 
	 * @param profileName
	 */
    @Deprecated
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 * Set the topology file to query
	 * 
	 * @param topologyFile
	 */
    @Deprecated
	public void setTopologyFile(String topologyFile) {
		this.topologyFile = topologyFile;
	}

	/**
	 * Set the WASVersion to query
	 * 
	 * @param version
	 */
    @Deprecated
	public void setWASVersion(WASVersion version) {
		this.version = version;
	}
}