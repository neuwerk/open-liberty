/*
 *
 *  @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ActivateNDSecurityDomains.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/25/09 17:14:46 [8/8/12 05:56:48]
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
 * 07/12/09    SLL          587624.6       For activating securityDomain ND automation
 * 09/25/09    syed         615757         Fix framework bug
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
public class ActivateNDSecurityDomains extends Task {

    public static final String DOMAIN_NAME_1 = "fvtnd-dom1";
    public static final String DOMAIN_NAME_2 = "fvtnd-dom2";

    public void execute() throws BuildException {
        Cell cell = null;

        SecurityDomain domain1 = null;
        SecurityDomain domain2 = null;

        try {

            Topology.init();
            cell = TopologyDefaults.getDefaultAppServer().getCell();

            domain1 = cell.getSecurityConfiguration().getSecurityDomainByName(DOMAIN_NAME_1);
            System.out.println("Security domain one: " + domain1.getName());
            domain2 = cell.getSecurityConfiguration().getSecurityDomainByName(DOMAIN_NAME_2);
            System.out.println("Security domain two: " + domain2.getName());


            // Configure custom user registry for fvtnd-dom2
            Map<String, String> customProps = new HashMap<String, String>();
            customProps
                    .put("usersFile", (AppConst.FVT_HOME).replace('\\', '/') + "/src/wssecfvt/securitydomains/templates/custom/users-msd.txt");
            customProps.put("groupsFile", (AppConst.FVT_HOME).replace('\\', '/')
                    + "/src/wssecfvt/securitydomains/templates/custom/groups-msd.txt");
            DomainCustomSettings settings = new DomainCustomSettings();
            settings.setCustomProperties(customProps);
            settings.setIgnoreCase(false);
            
            // set the active user registry for domain 1
            domain2.getUserRealms().configureRealm(UserRealmType.CustomUserRegistry, settings);
            
            // set the active user registry for domain 2
            domain2.getUserRealms().setActiveUserRealm(UserRealmType.CustomUserRegistry);
            
            cell.getWorkspace().saveAndSync();
            System.out.println("activating security domains done..");
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
