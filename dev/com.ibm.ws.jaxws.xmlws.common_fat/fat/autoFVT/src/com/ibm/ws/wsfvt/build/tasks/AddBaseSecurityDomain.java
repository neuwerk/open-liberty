/*
 *  @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/AddBaseSecurityDomain.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/25/09 17:03:17 [8/8/12 06:30:54]
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
 * 09/04/2008  syed        549159              Automate base secdom tests
 * 10/21/2008  jramos      559143              Incorporate Simplicity
 * 09/25/2009  syed        615757              Fix framework bug
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.config.securitydomain.DomainCustomSettings;
import com.ibm.websphere.simplicity.config.securitydomain.SecurityDomain;
import com.ibm.websphere.simplicity.config.securitydomain.UserRealmType;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The AddBaseSecurityDomain class is a custom task for creating a new security domain by copying
 * from global security domain.
 * 
 * The class conforms to the standards set in the Ant build framework for creating custom tasks.
 */

public class AddBaseSecurityDomain extends Task {
    
    public static final String DOMAIN_NAME = "fvtbase-dom1";

    /**
     * This method adds the kerberos Custom Identity mapping and Default Identity mapping JAAS login
     * modules that are needed for kerberos identity mapping tests.
     * 
     * @throws BuildException An error encountered while adding JAAS login modules
     */
    public void execute() throws BuildException {

        SecurityDomain sdom1 = null;
        ApplicationServer appServer = null;
        Cell cell = null;

        try {
            Topology.init();
            appServer = TopologyDefaults.getDefaultAppServer();
            cell = appServer.getCell();
            sdom1 = cell.getSecurityConfiguration().createSecurityDomain(DOMAIN_NAME, "This is FVT security domain")
                    .getResult();
            // Bind server1 to this new domain.
            // System.out.println("Mapping server process...\n");
            sdom1.mapScopeToDomain(appServer);

            Map<String, String> customProps = new HashMap<String, String>();
            customProps.put("usersFile", (AppConst.FVT_HOME).replace('\\', '/')
                    + "/src/wssecfvt/securitydomains/templates/custom/users-msd.txt");
            customProps.put("groupsFile", (AppConst.FVT_HOME).replace('\\', '/')
                    + "/src/wssecfvt/securitydomains/templates/custom/groups-msd.txt");
            DomainCustomSettings settings = new DomainCustomSettings();
            settings.setCustomProperties(customProps);
            settings.setRealmName(DOMAIN_NAME);
            settings.setCustomRegistryClass("com.ibm.websphere.security.FileRegistrySample");
            settings.setIgnoreCase(false);
            sdom1.getUserRealms().configureRealm(UserRealmType.CustomUserRegistry, settings);

            // Set custom registry as active registry.
            sdom1.getUserRealms().setActiveUserRealm(UserRealmType.CustomUserRegistry);
        
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

    public static void main(String[] args) {
        AddBaseSecurityDomain secdom1 = new AddBaseSecurityDomain();
        secdom1.execute();
    }

}
