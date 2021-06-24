/*
 * @(#) 1.8 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/StartAppsTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:45 [8/8/12 06:40:26]
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
 * 01/15/2007  jramos      413702             Fix out of memory issue
 * 02/15/2007  jramos      416386.1           startApps.py now takes fewer args
 * 04/09/2007  jramos      431249             Pass in the FVT_HOME that is local to the process being run on
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 10/22/07    jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/29/2008  jramos      559143             Incorporate Simplicity
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.application.Application;
import com.ibm.websphere.simplicity.application.EnterpriseApplication;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task will start all the Applications that are installed on the server.
 * 
 * @author smithd
 * 
 */
public class StartAppsTask extends Task {
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		
        try {
            Cell cell = TopologyDefaults.getDefaultAppServer().getCell();
            
            // first make sure the nodes are synced
            for(Node node : cell.getNodes()){
                node.sync();
            }
            
            // now start all the apps
            Set<Application> apps = cell.getApplicationManager().getApplications();
            for(Application app : apps) {
                EnterpriseApplication application = (EnterpriseApplication)app;
                System.out.println("Starting application " + application.getName() + ".");
                // make sure it is distributed
                int timeout = 60;
                while(timeout > 0 && !application.isAppReady(false)) {
                    timeout--;
                }
                // sometimes right after installing an app, WebSphere will think it's running when it really isn't
                try {
                    application.stop();
                } catch(Exception e1){// ignore
                }
                try {
                    application.start();
                } catch(Exception e1) {
                    System.out.println("Application " + application.getName() + " failed to start: " + e1.getMessage());
                }
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }		
	}
    
    public static void main(String[] args) {
        new StartAppsTask().execute();
    }
}
