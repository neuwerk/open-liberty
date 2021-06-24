/*
 *
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/AddNDJAASConfig.java, WAS.websvcs.fvt, WASX.FVT, uu0925.34 11/7/08 11:05:51 [7/1/09 09:44:19]
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
 * 09/17/08    Syed        549282             Automate setup steps
 * 10/22/2008  jramos      559143             Incorporate Simplicity
 * 07/12/2009  SLL         587624.6           Use correct security domain name for ND
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.config.securitydomain.AuthenticationStrategy;
import com.ibm.websphere.simplicity.config.securitydomain.JAASSystemLogin;
import com.ibm.websphere.simplicity.config.securitydomain.LoginModuleSettings;
import com.ibm.websphere.simplicity.config.securitydomain.SecurityDomain;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The AddNDJAASConfig class is a custom task for adding kerberos JAAS login modules for MSD tests
 * in ND environment.
 * 
 * The class conforms to the standards set in the Ant build framework for creating custom tasks.
 */

public class AddNDJAASConfig extends Task {

    public static final String JAAS_LOGIN = "wss.consume.KrbLocalUNT";
    public static final String LOGIN_MODULE = "com.ibm.ws.wssecurity.wssapi.token.impl.UNTConsumeLoginModule";

    /**
     * This method adds a new kerberos JAAS login to the security domain fvtnd-dom1. This is the
     * same JAAS login module that is installed with WAS, but with a different alias name.
     * 
     * @throws BuildException An error encountered while adding JAAS login modules
     */
    public void execute() throws BuildException {

        Cell cell = null;
        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();

            // create the JAAS login module for security domain
            SecurityDomain domain = cell.getSecurityConfiguration().getSecurityDomainByName(
                    AddNDSecurityDomains.DOMAIN_NAME_1);
            if (domain == null) {
                new AddBaseSecurityDomain().execute();
            }
            JAASSystemLogin login = domain.getJAASSystemLogins().createJAASSystemLogin(JAAS_LOGIN).getResult();

            LoginModuleSettings settings = new LoginModuleSettings();
            settings.setAuthStrategy(AuthenticationStrategy.REQUIRED);
            settings.setLoginModule(LOGIN_MODULE);
            login.createJAASLoginModule(settings);
            
            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            e.printStackTrace();
            throw new BuildException("Error creating JAAS login", e);
        }
    } // execute

    public static void main(String[] args) {
        AddNDJAASConfig msdjaas = new AddNDJAASConfig();
        msdjaas.execute();
    }

}
