//
// @(#) 1.7 FVT/ws/code/websvcs.fvt/src/com/ibm/ws/wsfvt/build/tools/utils/TopologyDefaults.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 3/15/10 12:39:38 [8/8/12 06:40:29]
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 10/22/07    jramos      476750          New File
// 06/12/08    jramos      524904          Restrict default to Pyxis server
// 10/21/08    jramos      559143          Incorporate Simplicity
// 01/15/09    jramos      571132          Fix bug for ND
// 08/21/09    jramos      608573          Make default server the same version and same machine as the cell manager
// 03/08/10    agheinzm    642032          Tolerate having a Dmgr and v7 node on seperate machines
//

package com.ibm.ws.wsfvt.build.tools.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.websphere.simplicity.AdminAgent;
import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.ServerType;
import com.ibm.websphere.simplicity.Topology;

import com.ibm.websphere.simplicity.util.AdminAgentServerFilter;
import com.ibm.websphere.simplicity.util.AlphabeticScopeComparator;
import com.ibm.websphere.simplicity.util.ApplicationServerFilter;
import com.ibm.websphere.simplicity.util.CollectionUtility;
import common.utils.topology.CellFactory;

import common.utils.topology.IAppServer;
import common.utils.topology.INodeContainer;
import common.utils.topology.IServerProcess;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * Defines how to start finding Topology information in Simplicity or ACUTE.
 * Deprecated public variables are for ACUTE. Simplicity uses getter methods.
 * You will want to use objects obtained from this class to get most of your
 * configuration information.
 * 
 * @author jramos
 */
public class TopologyDefaults {

	/**
	 * This is the cell of the default application server. You should use
	 * TopologyDefaults.getDefaultAppServer().getCell().
	 * 
	 * @deprecated
	 * @see #getDefaultAppServer()
	 * @see com.ibm.websphere.simplicity.ApplicationServer#getCell()
	 */
	@Deprecated
	public static common.utils.topology.Cell defaultAppServerCell;

	/**
	 * This is the node of the default application server. You should use
	 * TopologyDefaults.getDefaultAppServer().getNode().
	 * 
	 * @deprecated
	 * @see #getDefaultAppServer()
	 * @see com.ibm.websphere.simplicity.ApplicationServer#getNode()
	 */
	@Deprecated
	public static INodeContainer defaultAppServerNode;

	/**
	 * This is the default application server. You should use
	 * TopologyDefaults.getDefaultAppServer().
	 * 
	 * @deprecated
	 * @see #getDefaultAppServer()
	 */
	@Deprecated
	public static IAppServer defaultAppServer;

	
	
	public static LibertyServer libServer;
	public static String libertyServerName;
	public static final String DEFAULT_SERVER_NAME = "jaxws_fat_server";	

	static {
		// set default app server pointers
		if (defaultAppServer == null || defaultAppServerNode == null
				|| defaultAppServerCell == null) {
			ApplicationServer server;
			try {
				try {
		    		Topology.init();    		
					Collection<LibertyServer> knownLibertyServers = LibertyServerFactory.getKnownLibertyServers("com.ibm.websphere.simplicity.Topology");
					for (LibertyServer appserver : knownLibertyServers) {
						libServer = appserver;
						libertyServerName = libServer.getServerName();
						// only use first defined server
						break;				
					}
					
					if (libServer == null) {
						libServer = LibertyServerFactory.getLibertyServer(DEFAULT_SERVER_NAME, null, false);
						libertyServerName = libServer.getServerName();
					}
					
					/*if (libServer != null && libServer.getMachine().getOperatingSystem() != OperatingSystem.WINDOWS) {
						libServer.restartServer();
					}*/
		    	} catch (Exception e) {
		    		System.out.println("Got exception: " + e.getMessage());
		    		e.printStackTrace();    		
		    	}
				
				server = getDefaultAppServer();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			defaultAppServerCell = CellFactory.getCell(server.getCell());
			Set<INodeContainer> nodes = defaultAppServerCell
					.getNodeContainers();
			for (INodeContainer node : nodes) {
				if (node.getNodeName().equals(server.getNode().getName())) {
					defaultAppServerNode = node;
					Set<IServerProcess> servers = defaultAppServerNode
							.getServerProcesses();
					for (IServerProcess s : servers) {
						if (s.getServerName().equals(server.getName())) {
							defaultAppServer = (IAppServer) s;
							break;
						}
					}
					break;
				}
			}
		}

		// set default admin agent pointers
		/*if (defaultAdminAgent == null || defaultAdminAgentNode == null
				|| defaultAdminAgentCell == null) {
			List<com.ibm.websphere.simplicity.Cell> cells = null;
			try {
				// cells = Topology.getCellsByType(WebSphereTopologyType.ADMIN_AGENT);
				cells = Topology.getCells();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			if (cells != null && cells.size() > 0) {
				defaultAdminAgentCell = (IAdminAgentCell) CellFactory
						.getCell(cells.iterator().next());
				defaultAdminAgentNode = defaultAdminAgentCell
						.getAdminAgentNodeContainer();
				defaultAdminAgent = ((IAdminAgentNodeContainer) defaultAdminAgentNode)
						.getAdminAgent();
			}
		}*/
	}

	private static ApplicationServer defaultApplicationServer;
	private static AdminAgent defaultAdminAgentServer;

	/**
	 * Returns the default application server to be used. If this is a base
	 * installation, it uses the base application server. If this is an ND or
	 * administrative agent environment, the framework chooses 1 server to
	 * always install applications and configuration data to. This method
	 * returns that default application server.
	 * 
	 * If you're looking for a port to connect use web services from, you
	 * probably want to use this method to get the server.
	 * 
	 * @return the default application server
	 * @throws Exception
	 */
	public static ApplicationServer getDefaultAppServer() throws Exception {
		if (defaultApplicationServer == null) {
			List<com.ibm.websphere.simplicity.Cell> cells = Topology.getCells();
			Set<Server> servers = null;
			Set<Server> candidates = new HashSet<Server>();
			boolean isMixedCell = false;
			for (com.ibm.websphere.simplicity.Cell cell : cells) {
				servers = cell.getServers();
				for(Server server : servers) {
				    if(server.getNode().getBaseProductVersion().equals(cell.getManager().getNode().getBaseProductVersion())
				        && server.getNode().getMachine().equals(cell.getManager().getNode().getMachine()))
				            candidates.add(server);
				    else if (!server.getNode().getBaseProductVersion().equals(cell.getManager().getNode().getBaseProductVersion()))
	                        isMixedCell = true;
				}
				List<Server> sorted = CollectionUtility.sort(CollectionUtility
						.filter(candidates, new ApplicationServerFilter()),
						new AlphabeticScopeComparator<Server>());
				if (sorted.size() > 0) {
					defaultApplicationServer = (ApplicationServer) sorted
							.get(0);
					System.out.println("Found default server: "
							+ defaultApplicationServer.getName());
					break;
				} else if (isMixedCell) {
					for (Server server : servers) {
						if (server.getNode().getBaseProductVersion().equals(cell.getManager().getNode().getBaseProductVersion()))
							candidates.add(server);
					}
					sorted = CollectionUtility.sort(CollectionUtility.filter(candidates, new ApplicationServerFilter()), new AlphabeticScopeComparator<Server>());
					if (sorted.size() > 0) {
						defaultApplicationServer = (ApplicationServer)sorted.get(0);
						System.out.println("Found default server: " + defaultApplicationServer.getName());
						break;
					}
				}
			}
		}
		return defaultApplicationServer;
	}

	/**
	 * Returns the default admin agent if this is an admin agent topology.
	 * 
	 * @return the admin agent
	 * @throws Exception
	 */
	public static AdminAgent getDefaultAdminAgent() throws Exception {
		if (defaultAdminAgentServer == null) {
			Topology.init();
			List<com.ibm.websphere.simplicity.Cell> cells = Topology.getCells();
			Set<Server> servers = null;
			for (com.ibm.websphere.simplicity.Cell cell : cells) {
				servers = cell.getServers();
				List<Server> sorted = CollectionUtility.sort(CollectionUtility
						.filter(servers, new AdminAgentServerFilter()),
						new AlphabeticScopeComparator<Server>());
				if (sorted.size() > 0) {
					defaultAdminAgentServer = (AdminAgent) sorted.get(0);
					break;
				}
			}
		}
		return defaultAdminAgentServer;
	}

	/**
	 * Get the root configuration server in a topology. The root server is the
	 * deployment manager in an ND environment, base server in a stand-alone
	 * server environment, or the admin agent in an admin agent environment.
	 * 
	 * @return the root server
	 * @throws Exception
	 */
	public static ApplicationServer getRootServer() throws Exception {
		Server root = null;

		Topology.init();
		List<Cell> cells = Topology.getCells();
		Server current = null;
		for (Cell cell : cells) {
			current = cell.getManager();
			if (root == null) {
				root = current;
			} else if (root.getServerType() == current.getServerType()
					&& root.getNode().getBaseProductVersion().isLowerThan(
							current.getNode().getBaseProductVersion())) {
				root = current;
			} else if (current.getServerType() == ServerType.DEPLOYMENT_MANAGER
					|| current.getServerType() == ServerType.ADMIN_AGENT) {
				root = current;
			}
		}

		return (ApplicationServer) root;
	}

	/**
	 * Prints the default application server name.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out
					.println(TopologyDefaults.getDefaultAppServer().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}