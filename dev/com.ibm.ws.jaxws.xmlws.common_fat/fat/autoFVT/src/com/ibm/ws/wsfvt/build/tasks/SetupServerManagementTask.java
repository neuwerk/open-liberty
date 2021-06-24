/*
 * @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/SetupServerManagementTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/21/08 13:01:31 [8/8/12 06:40:29]
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
 * 09/18/2007  jramos      467003             New File
 * 10/22/07    jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/29/2008  jramos      559143             Incorporate Simplicity
 * 11/21/2008  jramos      566458             Put semaphore in profile directory
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task checks if all the servers in the config have been set up
 * to run the automation. The result is set in the environment
 * 
 * @author jramos
 *
 */
public class SetupServerManagementTask extends Task {
    public static final String SERVER_NOT_SETUP = "server.not.setup";
    
    private boolean checkServers = true;
    private boolean markServers = false;
    
    public void execute() {
        
        log("checkServers=" + checkServers 
                + System.getProperty("line.separator")
                + "markServers=" + markServers);
        
        if(markServers) {
            markServers();
        }
        
        if(checkServers) {
            checkServers();
        }
    }
    
    /**
     * Mark the servers in the config as having been set up
     *
     */
    private void markServers() {
        try {
            Cell cell = TopologyDefaults.getDefaultAppServer().getCell();
            Set<Node> nodes = cell.getNodes();
            String semaphoreLoc = null;
            File localSemaphore = new File(AppConst.FVT_BUILD_WORK_DIR + "/webservices-fvt.log");
            
            // create local copy of semaphore to copy to profiles
            try {
                if(!localSemaphore.exists()) {
                    localSemaphore.createNewFile();
                }
            } catch (IOException e) {
                throw new BuildException(e);
            }
            
            for(Node node: nodes) {
                semaphoreLoc = node.getProfileDir() + "/webservices-fvt.log";
                RemoteFile src = Machine.getLocalMachine().getFile(localSemaphore.getAbsolutePath());
                RemoteFile dest = node.getMachine().getFile(semaphoreLoc);
                src.copyToDest(dest);
            }
        } catch(Exception e) {
            throw new BuildException(e);
        }
    }
    
    /**
     * Check if the servers have been marked as set up
     *
     */
    private void checkServers() {
        try {
            Cell cell = TopologyDefaults.getDefaultAppServer().getCell();
            Set<Node> nodes = cell.getNodes();
            // iterate through the nodes and check for the semaphore
            RemoteFile semaphoreLog = null;
            RemoteFile[] dirContents = null;
            for (Node node : nodes) {
                semaphoreLog = node.getMachine().getFile(node.getProfileDir() + "/webservices-fvt.log");
                dirContents = node.getMachine().getFile(node.getProfileDir()).list(false);
                boolean found = false;
                for(int i = 0; i < dirContents.length; ++i) {
                    if(dirContents[i].getAbsolutePath().equals(semaphoreLog.getAbsolutePath())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    getProject().setProperty(SERVER_NOT_SETUP, "true");
                    log("One or more servers not set up. Setting " + SERVER_NOT_SETUP + " property.");
                    break;
                }
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }        
    }
    
    public void setCheckServers(boolean checkServers) {
        this.checkServers = checkServers;
    }
    
    public void setMarkServers(boolean markServers) {
        this.markServers = markServers;
    }
}
