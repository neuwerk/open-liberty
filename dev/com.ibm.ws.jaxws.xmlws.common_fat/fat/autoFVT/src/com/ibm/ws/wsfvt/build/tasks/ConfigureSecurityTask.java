/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ConfigureSecurityTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/11/08 13:14:10 [8/8/12 06:40:34]
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
 * 10/22/2008  jramos      559143          Incorporate Simplicity
 */
 
 package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.config.securitydomain.AdminLDAPSettings;
import com.ibm.websphere.simplicity.config.securitydomain.AdminWIMSettings;
import com.ibm.websphere.simplicity.config.securitydomain.GlobalSecurityDomain;
import com.ibm.websphere.simplicity.config.securitydomain.LDAPServerType;
import com.ibm.websphere.simplicity.config.securitydomain.UserRealmType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task configures and enables global security for the web services test framework.
 * 
 * @author jramos
 * 
 */
public class ConfigureSecurityTask extends Task {

    // WIM
    public static final String WIM_USER_REALM_KEY = "user-realm-1";
    public static final String WIM_USER = "wimUser";
    public static final String WIM_PASSWORD = "wimPassword";

    // LDAP
    public static final String LDAP_USER = "testuser";
    public static final String LDAP_PASSWORD = "testuserpwd";
    public static final String LDAP_USER_REALM_KEY = "user-realm-2";
    public static String LDAP_HOST = "newwang.austin.ibm.com";
    public static final LDAPServerType LDAP_TYPE = LDAPServerType.IBM_DIRECTORY_SERVER;
    public static int LDAP_PORT = 389;
    public static final String LDAP_BASE_DN = "o=ibm,c=us";
    public static final String LDAP_BIND_DN = "cn=root";
    public static final String LDAP_BIND_PASSWORD = "rootpwd";

    public void execute() throws BuildException {
    
        // if ldap server and port set as properties, use 'em.
        String buf;
        if( (buf = getProject().getProperty("ldap.server.hostname")) != null ){
              LDAP_HOST = buf;
        }
        if( (buf = getProject().getProperty("ldap.server.port")) != null ){
              LDAP_PORT = Integer.parseInt(buf);
        }

        Cell cell = null;
        Cell adminAgentCell = null;
        try {
            // TODO Jesse: disable SAF for z/OS

            // configure WIM security on the default server cell
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            GlobalSecurityDomain global = cell.getSecurityConfiguration().getGlobalSecurityDomain();
            AdminWIMSettings wim = new AdminWIMSettings();
            wim.setPrimaryAdminId(WIM_USER);
            wim.setAutoGenerateServerId(true);
            global.getUserRealms().configureRealm(wim, WIM_PASSWORD);
            global.getUserRealms().setActiveUserRealm(UserRealmType.WIMUserRegistry);

            // configure other security settings
            global.getApplicationSecurity().setEnabled(true);
            global.getJava2Security().setEnforceJava2Security(true);
            global.getJava2Security().setIssuePermissionWarning(true);

            // enable
            global.enableAdministrativeSecurity();

            // if we are in an admin agent topology, configure LDAP on the admin agent
            if(TopologyDefaults.getDefaultAdminAgent() != null) {
                adminAgentCell = TopologyDefaults.getDefaultAdminAgent().getCell();
                
                // TODO Jesse: disable SAF for z/OS
                
                // configure LDAP
                AdminLDAPSettings ldap = new AdminLDAPSettings();
                ldap.setLdapHost(LDAP_HOST);
                ldap.setLdapType(LDAP_TYPE);
                ldap.setLdapPort(LDAP_PORT);
                ldap.setBaseDN(LDAP_BASE_DN);
                ldap.setBindDN(LDAP_BIND_DN);
                ldap.setBindPassword(LDAP_BIND_PASSWORD);
                ldap.setAutoGenerateServerId(true);
                ldap.setPrimaryAdminId(LDAP_USER);
                adminAgentCell.getSecurityConfiguration().getGlobalSecurityDomain().getUserRealms().configureRealm(UserRealmType.LDAPUserRegistry, ldap);
                
                // configure other security settings
                adminAgentCell.getSecurityConfiguration().getGlobalSecurityDomain().getJava2Security().setEnforceJava2Security(true);
                adminAgentCell.getSecurityConfiguration().getGlobalSecurityDomain().getJava2Security().setIssuePermissionWarning(true);

                System.out.println("Enabling LDAP Security on admin agent cell...");
                adminAgentCell.getSecurityConfiguration().getGlobalSecurityDomain().enableAdministrativeSecurity();
            }

            // restart servers
            cell.getWorkspace().saveAndSync();
            cell.stop();
            if(adminAgentCell != null) {
                adminAgentCell.getWorkspace().save();
                adminAgentCell.stop();
                adminAgentCell.start();
            }
            cell.start();
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
    
    public static void main(String[] args) {
        new ConfigureSecurityTask().execute();
    }
}
