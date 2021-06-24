/*
 * @(#) 1.8 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/WsadminScript.java, WAS.websvcs.fvt, WASX.FVT, o0906.43 11/7/08 11:07:06 [2/16/09 19:21:26]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 06/03/2003  ulbricht    D166543            New File
 * 06/30/2003  ulbricht    D170881            Allow for security; connType
 * 01/07/2004  ulbricht    D186680            z/OS needs different exec method
 * 04/25/2005  ulbricht    D269183.4          Changes for iSeries
 * 08/17/2006  smithd      D381622            Add ND support
 * 10/23/2006  smithd      D395172            Improve usability in Cell
 * 10/31/2006  sedov       D401994            Add lang tag
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 08/17/2007  btiffany    459641             allow Project to be null so can be called outside of ant.
 * 10/22/2007  jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 03/04/2008  sedov       502496             Added fail on error and command line parameters
 * 03/26/2008  sedov       507699             Added new options
 * 10/30/2008  jramos      559143             Incorporate Simplicity
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.WsadminConnectionOptions;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This class will run the specified wsadmin command.
 *
 * The class conforms to the standards set in the Ant
 * build framework for creating custom tasks.
 */
public class WsadminScript extends Task {

    private String _strResult = "";
    private File scriptFile;
    private File propertiesFile;
    private String connType;
    private String serverKey;
        private String lang;
        private String parms = null;
        
        private File profile = null;
        
        // default is true
        private boolean failOnError = true;
        
    /**
     * This method will perform the wsadmin command.
     *
     * @throws BuildException Any kind of build exception
     */
    public void execute() throws BuildException {

        if (lang == null){
                lang = "jython";
        }
        
        if (scriptFile == null || (scriptFile != null && !scriptFile.exists())) {
            throw new BuildException(scriptFile.getPath()
                                     + " does not exist.");
        }

        if (propertiesFile != null && !propertiesFile.exists()) {
            throw new BuildException(propertiesFile.getPath()
                                     + " does not exist.");
        }

        Server server = null;
        Wsadmin wsadmin = null;
        try{
                log("WsadminScript has the following values: "
                    + System.getProperty("line.separator")
                    + "    scriptFile          = "
                    + scriptFile != null ? scriptFile.getPath() : "null"
                    + System.getProperty("line.separator")
                    + "    propertiesFile      = "
                    + propertiesFile != null ? propertiesFile.getPath() : "null"
                    + System.getProperty("line.separator")
                    + "    serverKey           = "
                    + serverKey != null ? serverKey : "null"
                    + System.getProperty("line.separator")
                    + "    connType            = " + connType
                    + System.getProperty("line.separator")
                    + "    lang                = " + lang
                    + System.getProperty("line.separator"),
                    Project.MSG_VERBOSE);
        } catch (Exception e){
                System.out.println("WsadminScript.java exception occured during logging - ignored");
        }

        WsadminConnectionOptions options = null;
        
        try {
            server = TopologyDefaults.getDefaultAppServer();
            if (connType != null && ConnectorType.valueOf(connType) != server.getConnInfo().getConnType()) {
                options = new WsadminConnectionOptions(server.getCell(), ConnectorType.valueOf(connType));
            } else {
                WsadminConnectionOptions connOps = (WsadminConnectionOptions)server.getConnInfo();
                options = new WsadminConnectionOptions(connOps.getConnType(), connOps.getHost(), connOps.getPort(), connOps.getUser(), connOps.getPassword(), connOps.getProfileDir(), connOps.getMachineConnectionInfo());
            }
        } catch(Exception e) {
            throw new BuildException(e);
        }
        
        if (profile != null) {
            List<File> profileParm = new ArrayList<File>();
            profileParm.add(profile);
            options.setProfile(profileParm);
        }
        
        if (propertiesFile != null){
            List<File> pParm = new ArrayList<File>();
            pParm.add(propertiesFile);
            options.setP(pParm);
        }
                
                if (parms != null && parms.length() > 0) {
            List<String> scriptParms = new ArrayList<String>();
            scriptParms.add(parms);
                        options.setScriptParameters(scriptParms);
        }

        options.setLang(lang);

        try {
            wsadmin = Wsadmin.getInstance(options);
            _strResult = wsadmin.executeScriptFile(scriptFile);
            System.out.println( _strResult );
        } catch (Exception e) {
                        if (failOnError){
                    throw new BuildException(e);
                        } else {
                                log("Error running wsadmin: " + e);
                        }
        } finally {
            try {
                if (wsadmin != null) {
                    if (!Wsadmin.getProviderInstance(server.getCell()).equals(wsadmin)) {
                        wsadmin.closeSession();
                    }
                    server.getWorkspace().discard();
                }
            } catch(Exception e) {}
        }
    }

    public String getLatestResult(){
        return _strResult;
    }

    /**
     * This method accepts a script file.
     *
     * @param _scriptFile A script file to be run
     */
    public void setScriptFile(File _scriptFile) {
        scriptFile = _scriptFile;
    }

    /**
     * This method accepts a properties file.
     *
     * @param _propertiesFile A property file.
     */
    public void setPropertiesFile(File _propertiesFile) {
        propertiesFile = _propertiesFile;
    }

    /**
     * This method will set the connType option on the
     * wsadmin invocation.  The current valid options are
     * SOAP and NONE.  The default is SOAP if the -connType
     * option is not specified.
     *
     * @param _connType The connection type to be used.  Valid
     *                  values are SOAP and NONE.
     */
    public void setConnType(String _connType) {
        connType = _connType;
    }

    /**
         * @param serverKey
         *            the serverKey to set
         */
        public void setServerKey(String _serverKey) {
                this.serverKey = _serverKey;
        }

        /**
         * Set commant line parameters to the script
         **/
        public void setLang(String lang){
                this.lang = lang;
        }
        
        /**
         * Set the profile option (-profile)
         **/
        public void setProfile(File profile){
                this.profile = profile;
        }
        
        /**
         * Set flag indicating that a BuildException should be thrown if wsadmin call
         * fails. Otherwise
         **/
        public void setFailOnError(boolean failOnError){
                this.failOnError = failOnError;
        }
        
        /**
         * Set command line parameters to the script
         **/
        public void setParms(String parms){
                this.parms = parms;
        }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        WsadminScript task = new WsadminScript();
        task.setScriptFile(new File("C:/wsfvt/autoFVT/src/jython/serverTune.py"));
        task.setLang("jython");
        task.setFailOnError(false);
        task.execute();
    }
}
