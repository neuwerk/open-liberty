/*
 * @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ManageSimplicitySession.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 6/29/09 12:30:57 [8/8/12 06:41:10]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 11/06/2008  jramos      559143             New File
 * 06/10/2009  jramos      596010             Add logging for errors
 * 06/29/2009  jramos      598850             Do not close JIIWS session
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Topology;

public class ManageSimplicitySession extends Task {
    
    private boolean closeSession = false;
    private boolean openSession = false;

    public void execute() throws BuildException {
        
        try {
            Topology.init();
            List<Cell> cells = Topology.getCells();
            for(Cell cell : cells) {
                if(closeSession) {
                    cell.disconnect();
                }
                if(openSession) {
                    try {
                        Thread.sleep(10000);
                    } catch(Exception e) {}
                    cell.connect();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }
    
    public void setCloseSession(boolean closeSession) {
        this.closeSession = closeSession;
    }
    
    public void setOpenSession(boolean openSession) {
        this.openSession = openSession;
    }
}
