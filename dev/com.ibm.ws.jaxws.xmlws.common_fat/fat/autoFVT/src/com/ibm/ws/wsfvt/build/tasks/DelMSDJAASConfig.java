/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/DelMSDJAASConfig.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:14 [8/8/12 06:30:54]
 * 
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 02/27/2008  syed        LIDB3430.2.02      Created for LI 3430 I7 FVT
 * 10/22/2008  jramos      559143             Incorporate Simplicity
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The DelMSDJAASConfig class is a custom task for adding kerberos
 * JAAS login modules for kerberos Identity mapping tests.
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */

public class DelMSDJAASConfig extends Task {

    /**
     * This method adds the kerberos Custom Identity mapping and Default
     * Identity mapping JAAS login modules that are needed for kerberos
     * identity mapping tests.
     * 
     * @throws BuildException
     *             An error encountered while adding JAAS login modules
     */
    public void execute() throws BuildException {

        Cell cell = null;
        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            // Delete the Global JAAS login module
            cell.getSecurityConfiguration().getGlobalSecurityDomain().getJAASSystemLogins().deleteJAASLogin(
                    AddMSDJAASConfig.GLOBAL_JAAS_LOGIN);
            
            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            // Ignore exception - may not exixt.if(cell != null) {
            try {
                cell.getWorkspace().discard();
            } catch (Exception e1) {
            }
        }
    } // execute

}
