/*
 * autoFVT/src/com/ibm/ws/wsfvt/build/tasks/EnableLTPASecurity.java, WAS.websvcs.fvt, WASX.FVT 
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 02/13/09    btiffany     575083            New File
 * 03/11/09    btiffany     578313            push connection after changing state, don't reset
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.WsadminConnectionOptions;
import com.ibm.websphere.simplicity.config.SecurityConfiguration;
import com.ibm.websphere.simplicity.config.securitydomain.GlobalSecurityDomain;
import com.ibm.websphere.simplicity.configuration.ConfigurationProvider;
import com.ibm.websphere.simplicity.runtime.ProcessStatus;
import com.ibm.ws.wsfvt.build.tools.AntProperties;
import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * check to see if security is enabled on the server.
 * If not, enable ldap security and update bootstraping.properties.
 * 
 * This is not designed to run outside the framework.
 * 
 *  CAUTION:  uses config objects, so there should not be any other live Simplicty objects
 *  in play when this runs. 
 *  
 * reads the ldap server and port from properties.xml.
 *  
 * arguments:  stopserveronexit  - optionally stop server when we're done.
 *                  (A restart is required for this to take effect, 
 *                   but maybe we'd rather do it later). 
 *             disable - disable security instead of enabling it.
 *
 * @author btiffany
 * 
 * TODO: get this to work on multi-cell and admin agent topos. 
 *
 */
public class EnableLTPASecurity extends Task {
    boolean disable = false;  // disable security instead of enabling it.
    boolean stopOnExit = false;  // stop the server when we're done.
    
    // disable security isntead of enabling it
    public void setDisable(boolean buf){
        disable=buf;
    }
    
    // stop server when we're done, normally we leave it running.
    public void setStopServerOnExit(boolean buf){
        stopOnExit=buf;
    }
    
    public void execute() throws BuildException {
        try {
            Topology.init();
            // System.out.println("init done");
            List<Cell> cells = Topology.getCells();
            Cell cell = cells.get(0);

            GlobalSecurityDomain gsd =  new GlobalSecurityDomain(cell, (new SecurityConfiguration(cell)));
            
            // this is read from the props file if the props file has been populated. 
            boolean secon = gsd.isGlobalSecurityEnabled();
            
            boolean startedServer = false;
            System.out.println("Global security is enabled: "+ secon);
            if( (!secon && !disable) || (secon && disable)){
                // the server has to be running for the shipped WAS scripts to work,
                // so start the server if it's not started.
                if (cell.getManager().getServerStatus() == ProcessStatus.STOPPED ){
                    System.out.println("starting server");
                    cell.getManager().start();
                    System.out.println("started");
                    startedServer = true;
                }
                
                
                // some of the wsadmin options like script params and profile 
                // go on the ConnectionOptions object, set those up now
                List <File> profileList = new LinkedList<File>();
                profileList.add(new File(AppConst.WAS_HOME + "/bin/LTPA_LDAPSecurityProcs.jacl"));          
                
                WsadminConnectionOptions wco = new WsadminConnectionOptions(cell, ConnectorType.SOAP);
                wco.setProfile(profileList);
                
                List<String> params = new LinkedList<String>();
                // LTPA_LDAPSecurityOn ${ldap.server.shortname} testuser testuserpwd ${ldap.server.port}           
                
                if(!disable){                   
                    System.out.println("enabling LDAP security");
                    params.add("LTPA_LDAPSecurityOn");            
                    params.add(AntProperties.getAntProperty("ldap.server.shortname"));
                    params.add("testuser");
                    params.add("testuserpwd");
                    params.add(AntProperties.getAntProperty("ldap.server.port"));
                    params.add(AntProperties.getAntProperty("ldap.server.dns.suffix"));
                } else {
                    System.out.println("disabling LDAP security");
                    params.add("LTPA_LDAPSecurityOff"); 
                }
                
                wco.setScriptParameters(params);            
                
                // now we can finally get wsadmin handle
                com.ibm.websphere.simplicity.wsadmin.Wsadmin w =
                    com.ibm.websphere.simplicity.wsadmin.Wsadmin.getInstance(wco) ;
                
                // we could have probably just skipped this script, but we'll keep it working like the old stuff.
                System.out.println("executing script");
                System.out.println(w.executeScriptFile(new File(AppConst.FVT_HOME + "/src/jacls/wssec.jacl")));
                
                // release the wsadmin session 
                w.closeSession();
                
                
                // An fvt performance issue is too many server restarts.  We're not going to
                // stop the server unless asked 
                if(stopOnExit)  {
                    System.out.println("stopping server");
                    cell.getManager().stop();
                } else {
                    System.out.println("Left the server running, stopserveronexit option may be specified to stop it.");
                    System.out.println("The server needs to be restarted for changes to take effect");
                }
                
                // now we need to refresh the simplicity object model to pick this up
                // and update bootstrapping.properties.
                
                // when one turns on global security, app security gets turned on too,
                // and stays on even if global security is disabled later (why??)
                // we'll call this just to keep simplicity in sync with what we've done.
                // skip this, it's not behaving. 
                //if(!disable){ gsd.enableAdministrativeSecurity(); }                
                
                
                System.out.println("updating bootstrapping.properties");
                ConfigurationProvider cp = Topology.getBootstrapFileOps().getConfigurationProvider();  
                if(!disable){
                    cp.setProperty("was.cell.1.WASPassword", "testuserpwd");
                    cp.setProperty("was.cell.1.WASUsername", "testuser");
                    cp.setProperty("was.cell.1.data.securityEnabled","true");
                } else {
                    cp.setProperty("was.cell.1.WASPassword", "");
                    cp.setProperty("was.cell.1.WASUsername", "");
                    cp.setProperty("was.cell.1.data.securityEnabled","false");
                }
                

                // if we changed server stop/start state, put that in props file too.
                if (startedServer){
                    cp.setProperty("was.cell.1.connType","SOAP");
                }
                if (stopOnExit){
                    cp.setProperty("was.cell.1.connType","NONE");
                    cell.pushConnection(ConnectorType.NONE, "testuser", "testuserpwd");
                } else {
                    cell.pushConnection(ConnectorType.SOAP, "testuser", "testuserpwd");
                }
                cp.writeProperties();
                  
                // this turned out to be a bad idea.
                //System.out.println("resetting Topology");
                //Topology.reset();
                
                
            } else {
                System.out.println("security state was not changed");
            }  // end if-changing-security-state      
            
            System.out.println("ending normally");
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }
    

    public static void main(String [] args){
        System.out.println("I am an ant task");
        EnableLTPASecurity me = new EnableLTPASecurity();
        //me.setDisable(true);
        //me.setStopServerOnExit(true);
        me.execute();
    }
}
