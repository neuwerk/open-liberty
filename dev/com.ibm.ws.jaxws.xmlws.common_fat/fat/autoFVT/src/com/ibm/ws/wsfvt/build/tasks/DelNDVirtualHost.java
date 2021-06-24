/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/DelNDVirtualHost.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:19 [8/8/12 06:30:55]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect  Description
 * -----------------------------------------------------------------------------
 * 09/17/2008  syed        549282          New File: automate MSD setup
 * 10/22/2008  jramos      559143          Incorporate Simplicity
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;


public class DelNDVirtualHost extends Task {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
        
        // TODO Jesse: Rework when Simplicity supports virtual hosts

        Cell cell = null;
        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
        } catch (Exception e1) {
            throw new BuildException(e1);
        }
        String cellName = cell.getName();

        // Get the Virtual host id, needs to be passed to remove call
        String getVHostId = "print AdminConfig.getid( '/Cell:" + cellName + "/VirtualHost:wsfvt_vhost1/')";

        String r2 = null;
        try {
            r2 = Wsadmin.getProviderInstance(cell).executeCommand(getVHostId);
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException("Error getting Virtual host", e);
        }

        // Remove virtual host name from the result
        String r_cmd = r2.replaceFirst("wsfvt_vhost1", "");
        r_cmd = r_cmd.trim();

        String remVHost = "AdminConfig.remove('" + r_cmd + "')";

        try {
            Wsadmin.getProviderInstance(cell).executeCommand(remVHost);
            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException("Error deleting Virtual host", e);
        }
    } // execute

    public static void main(String[] args) {
        new DelNDVirtualHost().execute();
    }
}

