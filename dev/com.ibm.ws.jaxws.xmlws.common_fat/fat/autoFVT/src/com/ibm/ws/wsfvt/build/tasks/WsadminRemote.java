/*
 *  @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/WsadminRemote.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01 10/12/11 15:14:30 [8/8/12 06:33:12]
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
 * 09/26/2011  syed        718227             Created for SAML Web SSO tests
 *
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
import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This class will run the specified wsadmin command.
 *
 * The class conforms to the standards set in the Ant
 * build framework for creating custom tasks.
 */
public class WsadminRemote extends Task {

    private String _strResult = "";
    private File scriptFile;
    private String lang = "jython";

    // IdP properties, filled in by set methods

    private String idpHostname =    "";
    private String idpPortnum  =    "";
    private String idpConnType =    "";
    private String idpAdminID =     "";
    private String idpAdminPswd =   "";
    private String wasAdminID =     "";
    private String wasAdminPswd =   "";
    private String wasProfileDir =  "";
        
        
    /**
     * This method will perform the wsadmin command.
     *
     * @throws BuildException Any kind of build exception
     */
    public void execute() throws BuildException {

        
        if (scriptFile == null || (scriptFile != null && !scriptFile.exists())) {
            throw new BuildException(scriptFile.getPath()
                                     + " does not exist.");
        }

        Server server = null;
        Wsadmin wsadmin = null;
        WsadminConnectionOptions options = null;
        Integer remotePort = new Integer(idpPortnum);
        ConnectorType remConType =  ConnectorType.valueOf(idpConnType);
        
        try {

             ConnectionInfo cinfo1 = new ConnectionInfo(remConType,
                          idpHostname,
                          remotePort,
                          idpAdminID,
                          idpAdminPswd);

             options = new WsadminConnectionOptions(remConType,
                       idpHostname,
                       remotePort,
                       wasAdminID,
                       wasAdminPswd,
                       wasProfileDir,
                       cinfo1);

        } catch(Exception e) {
            throw new BuildException(e);
        }
        
        options.setLang(lang);

        try {
            wsadmin = Wsadmin.getInstance(options);
            _strResult = wsadmin.executeScriptFile(scriptFile);
            System.out.println( _strResult );
        } catch (Exception e) {
              throw new BuildException(e);
        } finally {
           try {
              if (wsadmin != null) {
                 wsadmin.closeSession();
              }
           } catch (Exception e) {}
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
     * This method will set the connType option on the
     * wsadmin invocation.  The current valid options are
     * SOAP and NONE.  The default is SOAP if the -connType
     * option is not specified.
     *
     * @param idpConnType The connection type to be used.  Valid
     *                  values are SOAP and NONE.
     */
    public void setIdpConnType(String idpConnType) {
       this.idpConnType = idpConnType;
    }


    /**
     * Sets the scripting language - jacl or jython
    **/
    public void setLang(String lang) {
       this.lang = lang;
    }

    public void setIdpHostname(String idpHostname) {
        this.idpHostname = idpHostname;
    }

    public void setIdpPortnum(String idpPortnum) {
        this.idpPortnum = idpPortnum;
    }

    public void setIdpAdminID(String idpAdminID) {
        this.idpAdminID = idpAdminID;
    }

    public void setIdpAdminPswd(String idpAdminPswd) {
        this.idpAdminPswd = idpAdminPswd;
    }

    public void setWasAdminID(String wasAdminID) {
        this.wasAdminID = wasAdminID;
    }

    public void setWasAdminPswd(String wasAdminPswd) {
        this.wasAdminPswd = wasAdminPswd;
    }

    public void setWasProfileDir(String wasProfileDir) {
        this.wasProfileDir = wasProfileDir;
    }

        
    /**
     * @param args
     */
    public static void main(String[] args) {

        WsadminRemote task = new WsadminRemote();
        task.setScriptFile(new File("C:/wsfvt/autoFVT/src/jython/serverTune.py"));
        task.setLang("jython");
        task.execute();
    }


}
