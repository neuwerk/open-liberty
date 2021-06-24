/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/AddKerberosJAASConfig.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:05:47 [8/8/12 06:30:36]
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
import com.ibm.websphere.simplicity.config.securitydomain.AuthenticationStrategy;
import com.ibm.websphere.simplicity.config.securitydomain.GlobalSecurityDomain;
import com.ibm.websphere.simplicity.config.securitydomain.JAASLoginModule;
import com.ibm.websphere.simplicity.config.securitydomain.JAASSystemLogin;
import com.ibm.websphere.simplicity.config.securitydomain.JAASSystemLogins;
import com.ibm.websphere.simplicity.config.securitydomain.LoginModuleSettings;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The AddKerberosJAASConfig class is a custom task for adding kerberos JAAS login modules for
 * kerberos Identity mapping tests.
 * 
 * The class conforms to the standards set in the Ant build framework for creating custom tasks.
 */

public class AddKerberosJAASConfig extends Task {

    private String jaasLogin = "wss.consume.KRB5BSTCustomIdMapping";
    private String loginModule1 = "com.ibm.ws.wssecurity.wssapi.token.impl.KRBConsumeLoginModule";
    private String loginModule2 = "com.ibm.websphere.wssecurity.auth.module.CustomMappingLoginModule";

    /**
     * This method adds the kerberos Custom Identity mapping and Default Identity mapping JAAS login
     * modules that are needed for kerberos identity mapping tests.
     * 
     * @throws BuildException An error encountered while adding JAAS login modules
     */
    public void execute() throws BuildException {
        Cell cell = null;
        GlobalSecurityDomain global = null;
        JAASSystemLogins systemLogins = null;

        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            global = cell.getSecurityConfiguration().getGlobalSecurityDomain();
            systemLogins = global.getJAASSystemLogins();

            // Delete old login modules, if present
            JAASSystemLogin login = systemLogins.getJAASSystemLoginByAlias(jaasLogin);
            if (login != null) {
                log("Deleting existing JAAS login.");
                systemLogins.deleteJAASLogin(jaasLogin);
            }
        } catch (Exception e) {
            // Ignore exception as JAAS module may not exist.
        }

        try {
            // create the Custom Identity login module
            JAASSystemLogin login = systemLogins.createJAASSystemLogin(jaasLogin).getResult();
            JAASLoginModule module = login.getLoginModuleByClassName(loginModule1);
            if(module == null) {
                LoginModuleSettings settings = new LoginModuleSettings();
                settings.setAuthStrategy(AuthenticationStrategy.REQUIRED);
                settings.setLoginModule(loginModule1);
                login.createJAASLoginModule(settings);
            }
            module = login.getLoginModuleByClassName(loginModule2);
            if(module == null) {
                LoginModuleSettings settings = new LoginModuleSettings();
                settings.setLoginModule(loginModule2);
                login.createJAASLoginModule(settings);
            }
            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            e.printStackTrace();
            throw new BuildException("Error Creating JAAS login and modules", e);
        }
    } // execute

    public static void main(String[] args) {
        AddKerberosJAASConfig krbidmap = new AddKerberosJAASConfig();
        krbidmap.execute();
    }

}
