/*
 *  @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ImportSSLKey.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01 8/5/10 14:31:41 [8/8/12 06:32:57]
 *
 * IBM Confidential OCO Source Material
 * 5724-i63 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect   Author     Description
 * ----------  ---------------  --------   ---------------------------------
 * 07/20/2010  F23424-23442     syed       Import TFIM STS key for SSL setup
 * 08/05/2010  663315           syed       Change alias name  of imported key
 *
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ibm.websphere.simplicity.Topology;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.ProgramOutput;

import com.ibm.websphere.simplicity.Node;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.websphere.simplicity.WebSphereTopologyType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import com.ibm.websphere.simplicity.Cell;


/**
 * This class will import the signer certificate from an external STS to 
 * the default SSL trust store on the test machine.
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class ImportSSLKey extends Task {

    private String trustFile;
    private String keyAlias = "default_tfim99";
    private String keyOperation = "-import";  // -import is default
    private String storePassword = "WebAS";

    private String certFile =  AppConst.FVT_HOME + "/src/wssecfvt/apis/keys/svtwin006.cer";


    /**
     * This method will the keytool command to add  a signer certificate to 
     * the default SSL trust store file - trust.p12.
     *
     * @throws org.apache.tools.ant.BuildException Any kind of build exception
     */
    public void execute() throws BuildException {
        
        try {

            Node node = null;

            OperatingSystem osName = OperatingSystem.WINDOWS;

            Topology.init();
            Cell cell = TopologyDefaults.getDefaultAppServer().getCell();
            if (cell.getTopologyType() == WebSphereTopologyType.ND) {
               node = cell.getManager().getNode();
            }
            else {
               node = TopologyDefaults.getDefaultAppServer().getNode();
            }

            Machine localMachine = Machine.getLocalMachine();
            // Node node = TopologyDefaults.getDefaultAppServer().getNode();
            trustFile = node.getProfileDir() + "/etc/trust.p12";
            
            System.out.println("SSL trust file: " + trustFile);

            String keytoolCmd = null;
            File keytoolExecFile = null; 

            // Command is different depending on OS
            osName = localMachine.getOperatingSystem();
            if (osName == OperatingSystem.WINDOWS) {
                // System.out.println("It's WINDOWS...");
                keytoolExecFile = new File(AppConst.WAS_HOME + "/"
                                       + "java/jre/bin/"
                                       + "keytool.exe");
                keytoolCmd = keytoolExecFile.getPath();
            } else if (osName == OperatingSystem.ZOS) {
                // System.out.println("It's zOS...");
                keytoolExecFile = new File(AppConst.WAS_HOME + "/"
                                       + "java/bin/"
                                       + "keytool");
                keytoolCmd = keytoolExecFile.getPath();
            }
            else {
                // System.out.println("It's NOT WINDOWS or zOS...");
                keytoolExecFile = new File(AppConst.WAS_HOME + "/"
                                       + "java/jre/bin/"
                                       + "keytool");
                keytoolCmd = keytoolExecFile.getPath();
            }

            String[] params = new String[] {keyOperation, "-keystore ", trustFile, "-v", "-storetype",  "pkcs12", "-alias", keyAlias, "-file ", certFile, "-storepass", storePassword, "-noprompt"};

            System.out.println("Keytool command: " + keytoolCmd);
    
            if (!keytoolExecFile.exists()) {

                throw new BuildException("keytool executable file "
                                         + "not found at " + keytoolExecFile.getPath()
                                         + ".");

            }

            ProgramOutput po = localMachine.execute(keytoolCmd, params);
            System.out.println("keytool returned: " + po.getStdout() +
                                " " +  po.getStderr() ); 
        } catch (Exception e) {
            System.out.println("keytool returned Exception"); 
            throw new BuildException(e);
	}

    }

    /**
     * This method accepts full path to the SSL trust store file.
     *
     * @param _trustFile is location of trust store file
     */
    public void setTrustStoreFile(String _trustFile) {
        trustFile = _trustFile;
    }


    /**
     * This method accepts full path to the certificate file.
     *
     * @param _certFile is location certificate file to be imported
     */
    public void setCertFile(String _certFile) {
        certFile = _certFile;
    }


    /**
     * This method accepts alias of the certificate to be imported
     *
     * @param _keyAlias is location certificate file to be imported
     */
    public void setKeyAlias(String _keyAlias) {
        keyAlias = _keyAlias;
    }

    /**
     * This method accepts password of keystore file
     *
     * @param _storePassword is password of keystore file
     */
    public void setStorePassword(String _storePassword) {
        storePassword = _storePassword;
    }

    /**
     * This method accepts keytool paramter import or delete
     *
     * @param _keyOperation is either import or delete
     */
    public void setKeyOperation(String _keyOperation) {
        keyOperation = _keyOperation;
    }


}
