/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/AddNDTrustRealm.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:05:56 [8/8/12 06:30:55]
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

import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.config.securitydomain.SecurityDomain;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task will start all the Applications that are installed on the server.
 * 
 * @author smithd
 * 
 */
public class AddNDTrustRealm extends Task {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException {

        Cell cell = null;
        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            SecurityDomain domain = cell.getSecurityConfiguration().getSecurityDomainByName(
                    AddNDSecurityDomains.DOMAIN_NAME_2);
            if (domain == null) {
                new AddNDSecurityDomains().execute();
                domain = cell.getSecurityConfiguration().getSecurityDomainByName(AddNDSecurityDomains.DOMAIN_NAME_2);
            }

            Set<String> realms = new HashSet<String>();
            realms.add(AddNDSecurityDomains.DOMAIN_NAME_1);
            domain.getUserRealms().addTrustedRealms(true, realms);
            
            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException("Error adding trusted realm", e);
        }
    }
}
