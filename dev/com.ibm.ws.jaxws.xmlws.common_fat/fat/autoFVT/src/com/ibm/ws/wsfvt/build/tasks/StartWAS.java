/*
 * @(#) 1.9 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/StartWAS.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 6/10/09 17:19:00 [8/8/12 06:56:44]
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 05/20/2003  ulbricht    D166543            Add username & password for security
 * 04/28/2005  ulbricht    D269183.5          Add profile for iSeries
 * 08/14/2006  smithd      D381622            Integrate the Cell object (ND support)
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 10/22/2007  jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/22/2007  sedov       476785             Print more detail on start error
 * 06/12/2008  jramos      524904             Add support for second cell for migration testing
 * 10/30/2008  jramos      559143             Incorporate Simplicity
 * 06/10/2009  jramos      596010             Use RMI instead of SOAP
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.BootStrappingProperty;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.WebSphereTopologyType;
import com.ibm.websphere.simplicity.configuration.ConfigurationProvider;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 *  The StartWAS class is a custom task for starting
 *  WebSphere Application Server.
 *
 *  The class conforms to the standards set in the Ant
 *  build framework for creating custom tasks.
 */
public class StartWAS extends Task {

    /**
     * This method executes the command for starting 
     * the WebSphere Application Server.
     *
     * @throws BuildException Any kind of BuildException
     */
    public void execute() throws BuildException {
        try {
            if(TopologyDefaults.getDefaultAdminAgent() != null) {
                // in this env lets make sure we start the admin agent cell first
                Cell aaCell = TopologyDefaults.getDefaultAdminAgent().getCell();
                aaCell.start();
                for(Cell cell : Topology.getCells()) {
                    if(!cell.equals(aaCell)) {
                        cell.start();
                    }
                }
            } else {
                for(Cell cell : Topology.getCells()) {
                    if(cell.getTopologyType() == WebSphereTopologyType.ND) {
                        // first start the dmgr then perform a sync node
                        cell.getManager().start();
                        Set<Node> nodes = cell.getNodes();
                        for(Node node : nodes) {
                            if(node.getManager() != cell.getManager()) {
                                Machine m = node.getMachine();
                                String cmd = node.getProfileDir() + "/bin/syncNode" + m.getOperatingSystem().getDefaultScriptSuffix();
                                String[] parameters = null;
                                if(cell.getSecurityConfiguration().getGlobalSecurityDomain().isGlobalSecurityEnabled()) {
                                    parameters = new String[7];
                                    parameters[3] = "-username";
                                    parameters[4] = cell.getConnInfo().getUser();
                                    parameters[5] = "-password";
                                    parameters[6] = cell.getConnInfo().getPassword();
                                }
                                else
                                    parameters = new String[3];
                                parameters[0] = cell.getManager().getNode().getMachine().getHostname();
                                parameters[1] = "" + cell.getManager().getPortNumber(ConnectorType.SOAP);
                                parameters[2] = "-stopservers";
                                m.execute(cmd, parameters);
                            }
                        }
                    }
                    // start the rest of the servers
                    cell.start();
                }
            }
            for(Cell cell : Topology.getCells()) {
                if(cell.getConnInfo().getConnType() == ConnectorType.NONE) {
                    ConnectionInfo connInfo = cell.getConnInfo();
                    cell.popAllConnections();
                    cell.pushConnection(ConnectorType.RMI, connInfo.getUser(), connInfo.getPassword());
                    // make this default in bootstrapping props
                    ConfigurationProvider c = Topology.getBootstrapFileOps().getConfigurationProvider();
                    c.setProperty(cell.getBootstrapFileKey() + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.CONN_TYPE, ConnectorType.RMI.toString());
                    c.setProperty(cell.getBootstrapFileKey() + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.CONN_PORT, cell.getManager().getPortNumber(ConnectorType.RMI).toString());
                    c.writeProperties();
                }
            }
        } catch (Exception e) {
            log(e.getMessage());
            throw new BuildException("Error Starting WebSphere", e);
        }

    }
    
    public static void main(String[] args) {
        new StartWAS().execute();
    }
}
