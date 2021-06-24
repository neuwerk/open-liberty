/*
 * @(#) 1.8 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/SetFrameworkTopology.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/26/09 16:15:25 [8/8/12 06:41:15]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 01/15/09    jramos       571132            New File
 * 02/05/09    jramos       573731            Update keystore in boostrapping.properties
 * 03/18/09    jramos       575547            Always create test server
 * 03/23/09    jramos       581370            Update keystore location before calling Topology.init()
 * 08/25/09    jramos       608573            Use different server names for the multi node config
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.BootStrappingProperty;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Cluster;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.ServerType;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.WebSphereTopologyType;
import com.ibm.websphere.simplicity.configuration.ConfigurationProvider;
import com.ibm.websphere.simplicity.server.AppServerCreationOptions;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.ws.wsfvt.build.tools.AppConst;

public class SetFrameworkTopology extends Task {

    private String topology = "singleAppServer";
    
    public void execute() throws BuildException {
        try {
            // update keystore location
            ConfigurationProvider cp = Topology.getBootstrapFileOps().getConfigurationProvider();
            String keystore = cp.getProperty(BootStrappingProperty.KEYSTORE.toString());
            if(keystore != null && keystore.length() > 0) {
                System.out.println("Updating keystore location in bootstrapping.properties.");
                File temp = new File(keystore);
                String fileName = temp.getName();
                String localKeystore = (AppConst.FVT_HOME + "/common/files/" + fileName).replace('\\', '/');
                temp = new File(localKeystore);
                if(temp.exists()) {
                    System.out.println("keystore location on local machine: " + localKeystore);
                    cp.setProperty(BootStrappingProperty.KEYSTORE.toString(), localKeystore);
                    cp.writeProperties();
                    // update the location in the in memory cache
                    Machine m = null;
                    Topology.init();
                    List<Cell> cells = Topology.getCells();
                    for(Cell cell: cells) {
                        Set<Node> nodes = cell.getNodes();
                        for(Node node : nodes) {
                            m = node.getMachine();
                            m.setKeystore(temp);
                        }
                    }
                }
            } else {
                Topology.init();
            }
            
            System.out.println("Configuring a " + topology + " topology.");
            if("singleAppServer".equals(topology)) {
                configureND();
            } else if("multiNode".equals(topology)) {
                configureMultiNodeND();
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }
    
    /**
     * This method will delete all servers and clusters and create a single appserver.
     * 
     * @throws Exception
     */
    private void configureND() throws Exception {
        Set<Cell> cells = Topology.getCellsByType(WebSphereTopologyType.ND);
        
        if(cells.size() == 0)
            return;
        
        Cell cell = cells.iterator().next();
        // delete all the clusters
        System.out.println("Deleting clusters.");
        if(cell.getClusters().size() > 0) {
            for(Cluster cluster : cell.getClusters()) {
                cluster.stopSynchronous(600, 10);
                cell.deleteCluster(cluster.getName());
            }
        }
        // delete all the appservers
        System.out.println("Deleting appservers.");
        Set<Node> nodes = cell.getNodes();
        for(Node node : nodes) {
            if(node.getManager().getServerType() == ServerType.NODE_AGENT) {
                for(Server server : node.getServers()) {
                    if(server.getServerType() != ServerType.NODE_AGENT) {
                        server.stop();
                        node.deleteServer(server.getName());
                    }
                }
            }
        }
        // now create a single appserver on one of the nodes
        System.out.println("Creating appserver.");
        for(Node node : nodes) {
            // for now we'll also make sure the server is on the same machine to avoid breaks
            if(node.getManager().getServerType() == ServerType.NODE_AGENT && node.getMachine().equals(cell.getManager().getNode().getMachine())) {
                System.out.println("Creating server1 on node " + node.getName() + ".");
                AppServerCreationOptions options = new AppServerCreationOptions();
                options.setServerName("server1");
                ApplicationServer server1 = node.createApplicationServer(options).getResult();
                // now we need to create a virtual host alias for the default port
                // TODO replace this with Simplicity code when it gets added
                Wsadmin wsadmin = Wsadmin.getProviderInstance(cell);
                String virtualHosts = wsadmin.executeCommand("print AdminConfig.list('VirtualHost')");
                StringTokenizer t = new StringTokenizer(virtualHosts, "\r\n\t\f");
                String defaultHost = null;
                while(t.hasMoreTokens()) {
                    defaultHost = t.nextToken();
                    if(defaultHost.indexOf("default") != -1)
                        break;
                }
                System.out.println("Configuring virtual host.");
                // default port
                try {
                    int port = server1.getPortNumber(PortType.WC_defaulthost);
                    wsadmin.executeCommand("AdminConfig.create('HostAlias', \'"+defaultHost+"\', '[[port \""+port+"\"] [hostname \"*\"]]\')");
                } catch(Exception e) {
                    System.out.println("Error creating a virtual host for default port. It might already exist: " + e.getMessage());
                }
                // default secure port
                try {
                    int port = server1.getPortNumber(PortType.WC_defaulthost_secure);
                    wsadmin.executeCommand("AdminConfig.create('HostAlias', \'"+defaultHost+"\', '[[port \""+port+"\"] [hostname \"*\"]]\')");
                } catch(Exception e) {
                    System.out.println("Error creating a virtual host for default port. It might already exist: " + e.getMessage());
                }
                break;
            }
        }
        
        cell.getWorkspace().saveAndSync();
    }
    
    /**
     * This method will delete all servers and clusters and then create
     * a single appserver on each node.
     * 
     * @throws Exception
     */
    private void configureMultiNodeND() throws Exception {
        Set<Cell> cells = Topology.getCellsByType(WebSphereTopologyType.ND);
        Cell cell = cells.iterator().next();
        // delete all the clusters
        if(cell.getClusters().size() > 0) {
            for(Cluster cluster : cell.getClusters()) {
                cluster.stopSynchronous(600, 10);
                cell.deleteCluster(cluster.getName());
            }
        }
        // delete all the appservers
        Set<Node> nodes = cell.getNodes();
        for(Node node : nodes) {
            if(node.getManager().getServerType() == ServerType.NODE_AGENT) {
                for(Server server : node.getServers()) {
                    if(server.getServerType() == ServerType.APPLICATION_SERVER) {
                        server.stop();
                        node.deleteServer(server.getName());
                    }
                }
            }
        }
        // now create a single appserver on each of the nodes
        int i = 0;
        for(Node node : nodes) {
            if(node.getManager().getServerType() == ServerType.NODE_AGENT) {
                AppServerCreationOptions options = new AppServerCreationOptions();
                options.setServerName("server"+i);
                i++;
                System.out.println("Creating server server" + i + " on node " + node.getName());
                ApplicationServer server1 = node.createApplicationServer(options).getResult();
                // now we need to create a virtual host alias for the default port
                // TODO replace this with Simplicity code when it gets added
                Wsadmin wsadmin = Wsadmin.getProviderInstance(cell);
                String virtualHosts = wsadmin.executeCommand("print AdminConfig.list('VirtualHost')");
                StringTokenizer t = new StringTokenizer(virtualHosts, "\r\n\t\f");
                String defaultHost = null;
                while(t.hasMoreTokens()) {
                    defaultHost = t.nextToken();
                    if(defaultHost.indexOf("default") != -1)
                        break;
                }
                System.out.println("Configuring virtual host.");
                // default port
                try {
                    int port = server1.getPortNumber(PortType.WC_defaulthost);
                    System.out.println(wsadmin.executeCommand("AdminConfig.create('HostAlias', \'"+defaultHost+"\', '[[port \""+port+"\"] [hostname \"*\"]]\')"));
                } catch(Exception e) {
                    System.out.println("Error creating a virtual host for default port. It might already exist: " + e.getMessage());
                }
                // default secure port
                try {
                    int port = server1.getPortNumber(PortType.WC_defaulthost_secure);
                    System.out.println(wsadmin.executeCommand("AdminConfig.create('HostAlias', \'"+defaultHost+"\', '[[port \""+port+"\"] [hostname \"*\"]]\')"));
                } catch(Exception e) {
                    System.out.println("Error creating a virtual host for default port. It might already exist: " + e.getMessage());
                }
            }
        }
        
        cell.getWorkspace().saveAndSync();
    }
    
    public void setTopology(String topology) {
        this.topology = topology;
    }
    
    public static void main(String[] args) {
        SetFrameworkTopology task = new SetFrameworkTopology();
        task.setTopology("singleAppServer");
        task.execute();
    }
}
