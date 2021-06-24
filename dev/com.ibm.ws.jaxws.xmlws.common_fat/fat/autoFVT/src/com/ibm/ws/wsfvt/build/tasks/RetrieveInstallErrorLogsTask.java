/*
 * @(#) 1.6 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/RetrieveInstallErrorLogsTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/21/08 12:27:34 [8/8/12 06:40:27]
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
 * 03/14/2007  jramos      426382             New File
 * 04/09/2007  jramos      431249             Retrieve startApps.log
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 10/22/07    jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/29/2008  jramos      559143             Incorporate Simplicity
 * 11/21/2008  jramos      566458             Remove start apps log
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The RetrieveInstallErrorLogsTask class is a custom task for
 * retrieving the error logs from a remote machine.
 * 
 * The class conforms to the standards set in the Ant build framework
 * for creating custom tasks.
 */
public class RetrieveInstallErrorLogsTask extends Task {

	private String cellKey = null;
	
	public void execute() {
		
		Server process = null;
		String remoteInstLog = null;
		String remoteUninstLog = null;
		
		// we need this property set for the Cell (if it is not already)
		if (System.getProperty("FVT.base.dir") == null) {
			System.setProperty("FVT.base.dir", getProject().getProperty("FVT.base.dir"));
		}

        Cell cell = null;
        
        try {
            if (cellKey == null) {
    			// since they didn't specify which cell to use... we'll hope they want the default
    			cell = TopologyDefaults.getDefaultAppServer().getCell();
            } else {
        		Topology.init();
                Topology.getCellByBootstrapKey(cellKey);
            }
            process = cell.getManager();
            remoteInstLog = process.getNode().getMachine().getTempDir() + "/logs/was/" + AppConst.INSTALL_ERR_LOG_NAME;
            remoteUninstLog = process.getNode().getMachine().getTempDir() + "/logs/was/" + AppConst.UNINSTALL_ERR_LOG_NAME;
            
            // get log files if necessary
            if(!process.getNode().getMachine().equals(Machine.getLocalMachine())) {
                RemoteFile remote = null;
                RemoteFile local = null;
                try {
                    remote = process.getNode().getMachine().getFile(remoteInstLog);
                    local = Machine.getLocalMachine().getFile(AppConst.INSTALL_ERR_LOG);
                    local.copyFromSource(remote);
                } catch(Exception e){}
                
                try {
                    remote = process.getNode().getMachine().getFile(remoteUninstLog);
                    local = Machine.getLocalMachine().getFile(AppConst.UNINSTALL_ERR_LOG);
                    local.copyFromSource(remote);
                } catch (Exception e) {} 
                
            }
        } catch(Exception e) {
            throw new BuildException(e);
        }
	}

	/**
	 * @param cellKey the cellKey to set
	 */
	public void setServerKey(String cellKey) {
		this.cellKey = cellKey;
	}
}
