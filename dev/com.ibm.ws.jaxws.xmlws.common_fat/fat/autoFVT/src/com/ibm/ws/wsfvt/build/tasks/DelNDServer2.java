/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/DelNDServer2.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:17 [8/8/12 06:30:55]
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
 * 09/17/2008  syed        549282          New File
 * 10/22/2008  jramos      559143          Incorporate Simplicity
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Taskdef;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Server;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;


/**
 * Adds a task definition to the current project, such that this new task can be
 * used in the current project. Two attributes are needed, the name that identifies
 * this task uniquely, and the full name of the class (including the packages) that
 * implements this task.</p>
 * <P>This DelNDServer2 is trying to config needed  ND envirnment for testing securityDomain </p>
 * <pre>
 * taskname=fully.qualified.java.classname
 * </pre>
 * @since Ant 1.1
 * @ant.task category="internal"
 */
public class DelNDServer2 extends Taskdef {

    public void execute() {
        
        Cell cell = null;
        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            Server server = cell.getServerByName(AddNDServer2.SERVER2);

            if (server == null) {
                System.out.println("Server not found!");
                return;
            }

            server.getNode().deleteServer(server.getName());
            
            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            e.printStackTrace();
            throw new BuildException("Error deleting server2", e);

        }

    } // execute
}
