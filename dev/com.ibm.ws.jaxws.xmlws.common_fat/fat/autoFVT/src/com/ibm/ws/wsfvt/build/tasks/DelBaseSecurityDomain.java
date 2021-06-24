/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/DelBaseSecurityDomain.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:13 [8/8/12 06:30:54]
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
 * 10/23/2008  jramos      559143             Incorporate Simplicity
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.config.securitydomain.GlobalSecurityDomain;
import com.ibm.websphere.simplicity.config.securitydomain.SecurityDomain;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The DelBaseSecurityDomain class is a custom task for creating a 
 * new security domain by copying from global security domain.
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */

public class DelBaseSecurityDomain extends Task {

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
        ApplicationServer server1 = null;
        SecurityDomain domain = null;

        try {
            server1 = TopologyDefaults.getDefaultAppServer();
            cell = server1.getCell();
            domain = server1.getSecurityDomain();

            // If this is global security domain, return.
            if (domain instanceof GlobalSecurityDomain) {
                return;
            }

            domain.removeScopeFromDomain(server1);
            cell.getSecurityConfiguration().deleteSecurityDomain(domain.getName());
            
            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException(e);
        }
    } // execute

}
