/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/AddNDVirtualHost.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:05:58 [8/8/12 06:30:55]
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

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task will start all the Applications that are installed on the server.
 * 
 * @author smithd
 * 
 */
public class AddNDVirtualHost extends Task {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {

        // TODO Jesse: Update this when virtual host support is added to Simplicity
        Cell cell = null;
        ApplicationServer s2 = null;
        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            s2 = (ApplicationServer) cell.getServerByName(AddNDServer2.SERVER2);
            if(s2 == null) {
                new AddNDServer2().execute();
                s2 = (ApplicationServer) cell.getServerByName(AddNDServer2.SERVER2);
            }
        } catch (Exception e) {
            throw new BuildException("server2 not found!");
        }

        int portNum;
        try {
            portNum = s2.getPortNumber(PortType.WC_defaulthost);
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException("Unable to get WC_defaulthost port from server2.");
        }

        String createVHost = "AdminConfig.create('VirtualHost', \'" + cell.getConfigId().getConfigId()
                + "\', '[[name \"wsfvt_vhost1\"]]')";
        String createHAlias = "AdminConfig.create('HostAlias', AdminConfig.getid('/Cell:" + cell.getName()
                + "/VirtualHost:wsfvt_vhost1/'), '[[port \"" + portNum + "\"] [hostname \"*\"]]')";

        try {
            Wsadmin wsadmin = Wsadmin.getProviderInstance(cell);
            System.out.println(wsadmin.executeCommand(createVHost));
            System.out.println(wsadmin.executeCommand(createHAlias));
            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException("Error creating virtual host and host alias.", e);
        }

    } // execute

    public static void main(String[] args) {
        new AddNDVirtualHost().execute();
    }
}
