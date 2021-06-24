/*
 * @(#) 1.7.1.1 FVT/ws/code/websvcs.fvt/src/com/ibm/ws/wsfvt/build/tasks/UninstallApp.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/27/10 16:56:40 [8/8/12 06:56:44]
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
package com.ibm.ws.wsfvt.build.tasks.liberty;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;


import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.PrintToFile;

/**
 *  The UninstallApp class is a custom task for uninstalling
 *  an application from WebSphere Application Server.
 *
 *  The class conforms to the standards set in the Ant
 *  build framework for creating custom tasks.
 */
public class UninstallApp extends ParentTask {

    private String appName = "";
    // keep these since we don't want to change those spread in lots of UninstallTest.xml
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
			//if (ensureServerStarted()) {
				String serverRoot = libServer.getServerRoot();
				File dropinsFolder = new File(serverRoot, "dropins/");
				for (File f : dropinsFolder.listFiles()) {
					FileUtils.delete(f);
				}
				// This call throws IllegalMonitorStateException
				// libServer.wait(15*1000);						
				// Thread.sleep(1*1000);
				// Stop server after installation
				//libServer.stopServer();
			//}			
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
