/*
 *
 *  @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/CopyGlmKeys.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01 3/16/10 16:38:01 [8/8/12 06:32:41]
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
 * 03/10/10    syed        F18567-23226       New file for GLM tests
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
 * The CopyGlmKeys class is a custom task for copying GLM keystore files to 
 * the etc directory of the appServer.
 * The class conforms to the standards set in the Ant build framework for 
 * creating custom tasks.
 */
public class CopyGlmKeys extends Task {

    public void execute() throws BuildException {
        try {
            Node node = TopologyDefaults.getDefaultAppServer().getNode();
            File keysDir = new File(AppConst.FVT_HOME + "/src/wssecfvt/glmFisFats/keys");
            String[] keys = keysDir.list();
            String destDir = node.getProfileDir() + "/etc/ws-security/glm";
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
