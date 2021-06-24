/*
 *  @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/CopySamlApiKeys.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01 4/29/10 15:36:56 [8/8/12 06:32:53]
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
 * 04/14/10    syed        F23421-23429       New file for saml api keystore
 * 04/29/10    syed        648622             Removed code that copied the self-issue properties file to WAS
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The CopySamlApiKeys class is a custom task for copying SAML keystore files to 
 * the etc directory of the appServer.
 * The class conforms to the standards set in the Ant build framework for 
 * creating custom tasks.
 */
public class CopySamlApiKeys extends Task {

    public void execute() throws BuildException {

        try {

            Node node = TopologyDefaults.getDefaultAppServer().getNode();
            File keysDir = new File(AppConst.FVT_HOME + "/src/wssecfvt/apis/samlbearer/keys");
            String[] keys = keysDir.list();
            String destDir = node.getProfileDir() + "/etc/ws-security/samples";
            node.getMachine().getFile(destDir).mkdirs();
            RemoteFile source = null;
            RemoteFile dest = null;
            for (int i = 0; i < keys.length; ++i) {
                source = Machine.getLocalMachine().getFile(keysDir.getAbsolutePath() + "/" + keys[i]);
                dest = node.getMachine().getFile(destDir + "/" + keys[i]);
                source.copyToDest(dest);
            }

        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
