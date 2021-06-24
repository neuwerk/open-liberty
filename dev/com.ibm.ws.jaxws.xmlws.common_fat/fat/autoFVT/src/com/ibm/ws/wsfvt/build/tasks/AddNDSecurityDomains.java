/*
 *
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/AddNDSecurityDomains.java, WAS.websvcs.fvt, WASX.FVT, uu0925.34 11/7/08 11:05:53 [7/1/09 09:44:19]
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
 * 02/05/2008  jramos      495713          New File
 * 08/22/08    SLL                         For securityDomain ND automation
 * 09/17/08    Syed        549282          Automate setup steps
 * 10/22/2008  jramos      559143          Incorporate Simplicity
 * 07/12/2009  SLL         587624.6        Add recycle logic after add server to domain or activate will fail
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.config.securitydomain.DomainCustomSettings;
import com.ibm.websphere.simplicity.config.securitydomain.SecurityDomain;
import com.ibm.websphere.simplicity.config.securitydomain.UserRealmType;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task creates and configures a second security domain. It maps the default server or cluster
 * to the second domain.
 * 
 * @author jramos
 */
public class AddNDSecurityDomains extends Task {

    public static final String DOMAIN_NAME_1 = "fvtnd-dom1";
    public static final String DOMAIN_NAME_2 = "fvtnd-dom2";

    public void execute() throws BuildException {
        Cell cell = null;
        Server s1 = null;
        Server s2 = null;
        String sName2 = "server2";

        try {
            Topology.init();
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            s1 = TopologyDefaults.getDefaultAppServer();
            s2 = cell.getServerByName(sName2);
            if(s2 == null) {
                new AddNDServer2().execute();
                s2 = cell.getServerByName(sName2);
            }
        } catch (Exception e) {
            throw new BuildException("Unable to find servers.", e);
        }

        SecurityDomain domain1 = null;
        SecurityDomain domain2 = null;
        try {
            domain1 = cell.getSecurityConfiguration().copyGlobalSecurityDomain(DOMAIN_NAME_1,
                    "Wssecfvt security domain 1", null).getResult();
            System.out.println("Security domain created: " + domain1.getName());
            domain2 = cell.getSecurityConfiguration().copyGlobalSecurityDomain(DOMAIN_NAME_2,
                    "Wssecfvt security domain 2", null).getResult();
            System.out.println("Security domain created: " + domain2.getName());

            // Assign server1 to fvtnd-dom1 amd server2 to fvtnd-dom2
            domain1.mapScopeToDomain(s1);
            domain2.mapScopeToDomain(s2);

            //Sy  save domain first 
            cell.getWorkspace().saveAndSync();
            System.out.println("domains saved .......");

        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            e.printStackTrace();
            throw new BuildException("Exception setting active registry", e);
        }

    } // execute

}
