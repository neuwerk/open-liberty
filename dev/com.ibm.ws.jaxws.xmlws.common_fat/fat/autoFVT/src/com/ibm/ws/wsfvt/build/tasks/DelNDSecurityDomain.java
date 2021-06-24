/*
 *
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/DelNDSecurityDomain.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:16 [8/8/12 06:30:55]
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
 * 09/17/08    Syed        549282          Automate setup steps
 * 10/22/2008  jramos      559143          Incorporate Simplicity
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.config.SecurityConfiguration;
import com.ibm.websphere.simplicity.config.securitydomain.GlobalSecurityDomain;
import com.ibm.websphere.simplicity.config.securitydomain.SecurityDomain;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The DelNDSecurityDomain class is a custom task for creating a 
 * new security domain by copying from global security domain.
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */

public class DelNDSecurityDomain extends Task {

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
        SecurityConfiguration sec = null;
        ApplicationServer s1 = null;
        ApplicationServer s2 = null;

        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            sec = cell.getSecurityConfiguration();
            s1 = TopologyDefaults.getDefaultAppServer();
            s2 = (ApplicationServer) cell.getServerByName(AddNDServer2.SERVER2);

            if (s1 != null) {
                SecurityDomain sdom1 = s1.getSecurityDomain();
                // If this is global security domain, return.
                if (sdom1 instanceof GlobalSecurityDomain) {
                    return;
                }
                // If this is our security domain, delete it
                sdom1.removeScopeFromDomain(s1);
                sec.deleteSecurityDomain(sdom1.getName());
            }

            if (s2 != null) {
                SecurityDomain sdom2 = s2.getSecurityDomain();
                // If this is global security domain, return.
                if (sdom2 instanceof GlobalSecurityDomain) {
                    return;
                }
                // If this is our security domain, delete it
                sdom2.removeScopeFromDomain(s2);
                sec.deleteSecurityDomain(sdom2.getName());
            }
            
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
