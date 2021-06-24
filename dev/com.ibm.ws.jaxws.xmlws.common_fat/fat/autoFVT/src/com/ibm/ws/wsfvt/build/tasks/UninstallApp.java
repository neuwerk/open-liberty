/*
 * @(#) 1.7.1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/UninstallApp.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/27/10 16:56:40 [8/8/12 06:56:44]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 03/14/2005  ulbricht    D261009            Default uninstall mode
 * 04/22/2005  ulbricht    D269183.1          Set profile
 * 08/15/2006  smithd      D381622            Add Cell support (ND support)
 * 10/23/2006  smithd      D395172            Improve usability in Cell
 * 03/14/2007  jramos      426382             Add remote execution support
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 06/12/2008  jramos      524904             Add support for downLevelServer for migration and mixed cell testing
 * 10/30/2008  jramos      559143             Incorporate Simplicity
 * 05/27/10   agheinzm     654566              Fix bug in execute() that was causing loop to end early
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.ServerType;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.websphere.simplicity.application.Application;
import com.ibm.ws.wsfvt.build.tools.AdminApp;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.PrintToFile;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 *  The UninstallApp class is a custom task for uninstalling
 *  an application from WebSphere Application Server.
 *
 *  The class conforms to the standards set in the Ant
 *  build framework for creating custom tasks.
 */
public class UninstallApp extends Task {

    private String appName = "";
    private String fvtRootLocation = "";
    private boolean localInstall = true;
    private String uninstallMode = "com.ibm.ws.wsfvt.build.uninstall.BatchUninstallImpl"; 
    private String cellKey = null;

    /**
     * This method executes the command to uninstall an
     * application.
     *
     * @throws BuildException Any kind of build exception
     */
    public void execute() throws BuildException {        

        try {
    		// we need this property set for the Cell (if it is not already)
    		if (System.getProperty("FVT.base.dir") == null) {
    			System.setProperty("FVT.base.dir", getProject().getProperty("FVT.base.dir"));
    		}
    
            Cell cell = null;
            if (cellKey == null) {
    			// since they didn't specify which cell to use... we'll hope they want the default
    			cell = TopologyDefaults.getDefaultAppServer().getCell();
            } else if("downLevelServer".equals(cellKey)) {
                log("first else");
                // lets try to find a down level server in the config; assumption is made that baseline version is highest
                WebSphereVersion baseLineVersion = TopologyDefaults.getDefaultAppServer().getNode().getBaseProductVersion();
                log("V8 version: " + baseLineVersion.toString());
                List<Cell> cells = Topology.getCells();
                // loop through all the nodes in the cell
                for(Cell c : cells) {
                    log("Processing cell " + c.getName());
                    Set<Node> nodes = c.getNodes();
                    for(Node node : nodes) {
                        log("Processing node " + node.getName());
                        log("Node version: " + node.getBaseProductVersion());
                        log("Equals: " + !node.getBaseProductVersion().equals(baseLineVersion));
                        if(!node.getBaseProductVersion().equals(baseLineVersion)) {
                            // look for an appserver
                            Set<Server> servers = node.getServers();
                            for(Server downServer : servers) {
                                log("Server type: " + downServer.getServerType());
                                if(downServer.getServerType() == ServerType.APPLICATION_SERVER) {
                                    cell = c;
                                    break;
                                }
                            }
                        }
                    }
                    if(cell != null) {
                        break;
                    }
                }
                if(cell == null) {
                    throw new BuildException("Unable to find a down level server in the configuration.");
                }
            } else {
                Topology.init();
        		cell = Topology.getCellByBootstrapKey(cellKey);
            }
            
            log("UninstallApp " + appName);
            log("UninstallApp has the following values: "
                + System.getProperty("line.separator")
                + "    appName             = " + appName
                + System.getProperty("line.separator")
                + "    localInstall        = " + localInstall
                + System.getProperty("line.separator")
                + "    fvtRootLocation     = " + fvtRootLocation
                + System.getProperty("line.separator")
                + "    uninstallMode       = " + uninstallMode
                + System.getProperty("line.separator")
                + "    cellKey           = " + cellKey
                + System.getProperty("line.separator"),
                Project.MSG_VERBOSE);
            
            Application app = cell.getApplicationManager().getApplicationByName(appName);
            if(app == null) {
                System.out.println(appName + " is not installed.");
                return;
            }
            
            localInstall = cell.getConnInfo().getConnType() == ConnectorType.NONE;
    
            AdminApp adminApp = new AdminApp(cell);
            adminApp.setAppName(appName);
            adminApp.setLocal(localInstall);
            log(adminApp.uninstallApp());
        } catch(Exception e) {
        	if(e.getMessage().indexOf("ConfigServiceException") == -1 && e.getMessage().indexOf("PrivilegedActionException") == -1) { // ignore this for product defect on z/OS that was deffered to Matterhorn
	            e.printStackTrace();
	            PrintToFile file = new PrintToFile();
	            file.printException(AppConst.UNINSTALL_ERR_LOG_NAME, e);
	            throw new BuildException(e);
        	}
        }
    }

    /**
     * This method accepts a string containing the name of the
     * application to uninstall.
     *
     * @param appName Name of the application to uninstall
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * This method accepts a boolean indicating
     * whether the WebSphere Application Server will be
     * running when install is attempted.  If the server
     * will not be running, localInstall will be true.
     *
     * @param localInstall A boolean indicating true if a local install
     *                     should be tried
     */
    public void setLocalInstall(boolean localInstall) {
        this.localInstall = localInstall;
    }

    /**
     * This method accepts a string containing directory
     * of the base FVT install.
     *
     * @param fvtRootLocation Name of the base FVT install directory
     */
    public void setFvtRootLocation(String fvtRootLocation) {
        this.fvtRootLocation = fvtRootLocation;
    }

    /**
     * This method allow the uninstall mode to be set.
     *
     * @param uninstallMode The implementation of install that
     *                      should be used.
     */
    public void setUninstallMode(String uninstallMode) {
        this.uninstallMode = uninstallMode;
    }

	/**
	 * @param cellKey the cellKey to set
	 */
	public void setServerKey(String cellKey) {
		this.cellKey = cellKey;
	}
    
    public static void main(String[] args) {
        UninstallApp task = new UninstallApp();
        task.setAppName("WIDeploy");
        task.setLocalInstall(true);
        task.execute();
    }
}
