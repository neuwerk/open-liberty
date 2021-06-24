/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/AddMSDJAASConfig.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:05:49 [8/8/12 06:30:54]
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
import com.ibm.websphere.simplicity.config.securitydomain.JAASSystemLogin;
import com.ibm.websphere.simplicity.config.securitydomain.LoginModuleSettings;
import com.ibm.websphere.simplicity.config.securitydomain.SecurityDomain;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The AddMSDJAASConfig class is a custom task for adding kerberos JAAS login modules for kerberos
 * Identity mapping tests.
 * 
 * The class conforms to the standards set in the Ant build framework for creating custom tasks.
 */
public class AddMSDJAASConfig extends Task {

    public static final String GLOBAL_JAAS_LOGIN = "wss.consume.KRBBaseGlobal";
    public static final String DOMAIN_JAAS_LOGIN = "wss.consume.KRBBaseLocal";
    public static final String MODULE_1 = "com.ibm.ws.wssecurity.wssapi.token.impl.KRBConsumeLoginModule";
    public static final String MODULE_2 = "com.ibm.ws.wssecurity.wssapi.token.impl.DKTConsumeLoginModule";

    /**
     * This method adds the kerberos Custom Identity mapping and Default Identity mapping JAAS login
     * modules that are needed for kerberos identity mapping tests.
     * 
     * @throws BuildException An error encountered while adding JAAS login modules
     */
    public void execute() throws BuildException {

        Cell cell = null;
        GlobalSecurityDomain global = null;
        SecurityDomain domain = null;

        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            global = cell.getSecurityConfiguration().getGlobalSecurityDomain();
            domain = cell.getSecurityConfiguration().getSecurityDomainByName(AddBaseSecurityDomain.DOMAIN_NAME);
            if (domain == null) {
                AddBaseSecurityDomain task = new AddBaseSecurityDomain();
                task.execute();
                domain = cell.getSecurityConfiguration().getSecurityDomainByName(AddBaseSecurityDomain.DOMAIN_NAME);
            }

            // create the Global JAAS login module
            JAASSystemLogin globalLogin = global.getJAASSystemLogins().createJAASSystemLogin(GLOBAL_JAAS_LOGIN)
                    .getResult();

            // create login modules
            LoginModuleSettings settings = new LoginModuleSettings();
            settings.setAuthStrategy(AuthenticationStrategy.REQUIRED);

            settings.setLoginModule(MODULE_1);
            globalLogin.createJAASLoginModule(settings);

            settings.setLoginModule(MODULE_2);
            globalLogin.createJAASLoginModule(settings);

            // create the JAAS login module for security domain
            JAASSystemLogin domainLogin = domain.getJAASSystemLogins().createJAASSystemLogin(DOMAIN_JAAS_LOGIN)
                    .getResult();

            // create login modules
            settings.setLoginModule(MODULE_1);
            domainLogin.createJAASLoginModule(settings);

            settings.setLoginModule(MODULE_2);
            domainLogin.createJAASLoginModule(settings);

            cell.getWorkspace().saveAndSync();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException("Error creating JAAS login", e);
        }
    } // execute

    public static void main(String[] args) {
        AddMSDJAASConfig msdjaas = new AddMSDJAASConfig();
        msdjaas.execute();
    }

}
