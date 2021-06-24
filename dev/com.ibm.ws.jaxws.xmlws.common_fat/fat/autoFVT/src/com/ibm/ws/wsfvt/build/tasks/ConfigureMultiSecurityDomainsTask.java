/*
 * @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ConfigureMultiSecurityDomainsTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/14/11 09:39:31 [8/8/12 06:40:34]
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
 * 04/06/2011  himab       694339          remove dependency on newwang LDAP server 
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Cluster;
import com.ibm.websphere.simplicity.config.securitydomain.DomainLDAPSettings;
import com.ibm.websphere.simplicity.config.securitydomain.LDAPServerType;
import com.ibm.websphere.simplicity.config.securitydomain.SecurityDomain;
import com.ibm.websphere.simplicity.config.securitydomain.UserRealmType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task creates and configures a second security domain. It maps the default server or cluster
 * to the second domain.
 * 
 * @author jramos
 */
public class ConfigureMultiSecurityDomainsTask extends Task {

    public static final String DOMAIN_NAME = "securityDomain2";

    public static final String LDAP_USER_REALM_KEY = "user-realm-2";
    public static String LDAP_HOST = "ctldap1.austin.ibm.com";
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
        try {
            // create the second security domain
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            System.out.println("Creating security domain " + DOMAIN_NAME + "...");
            SecurityDomain domain = cell.getSecurityConfiguration().createSecurityDomain(DOMAIN_NAME, "Test domain")
                    .getResult();
            System.out.println("Security domain created: " + domain.getName());

            // configure LDAP on the domain
            System.out.println("Configuring LDAP on the domain...");
            DomainLDAPSettings ldapSettings = new DomainLDAPSettings();
            ldapSettings.setRealmName(LDAP_HOST + ":" + LDAP_PORT);
            ldapSettings.setLdapHost(LDAP_HOST);
            ldapSettings.setLdapType(LDAP_TYPE);
            ldapSettings.setLdapPort(LDAP_PORT);
            ldapSettings.setBaseDN(LDAP_BASE_DN);
            ldapSettings.setBindDN(LDAP_BIND_DN);
            ldapSettings.setBindPassword(LDAP_BIND_PASSWORD);
            domain.getUserRealms().configureRealm(UserRealmType.LDAPUserRegistry, ldapSettings);

            // set this as the active registry
            System.out.println("Setting the LDAP user registry as the active registry for the domain...");
            domain.getUserRealms().setActiveUserRealm(UserRealmType.LDAPUserRegistry);

            // configure app security and Java 2 security
            domain.getApplicationSecurity().setEnabled(true);
            domain.getJava2Security().setEnforceJava2Security(true);

            // map the server or cluster to the domain; this assumes for now that if there is a
            // cluster,
            // the server is in the cluster. this is the topology our tests support
            if (cell.getClusters().size() > 0) {
                // map the clusters to the domain
                Set<Cluster> clusters = cell.getClusters();
                for (Cluster cluster : clusters) {
                    System.out.println("Mapping " + cluster.getName() + " to domain " + domain.getName() + "...");
                    domain.mapScopeToDomain(cluster);
                }
            } else {
                // map the server to the domain
                ApplicationServer server = TopologyDefaults.getDefaultAppServer();
                System.out.println("Mapping " + server.getName() + " to domain " + domain.getName() + "...");
                domain.mapScopeToDomain(server);
            }
            
            cell.getWorkspace().saveAndSync();

            System.out.println("Restarting servers...");
            cell.stop();
            cell.start();
        } catch (Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException(e);
        }
    }
}
