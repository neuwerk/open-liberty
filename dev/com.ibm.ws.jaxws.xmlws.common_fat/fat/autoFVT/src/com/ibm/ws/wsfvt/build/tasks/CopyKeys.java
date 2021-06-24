/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/CopyKeys.java, WAS.websvcs.fvt, WASX.FVT, nn1011.18 11/7/08 11:06:11 [3/19/10 14:29:24]
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
 * 06/18/07    jramos      446691             New File
 * 10/23/2008  jramos      559143             Incorporate Simplicity
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
 * The CopyKeys class is a custom task for copying keys to the etc directory of the appServer
 * 
 * The class conforms to the standards set in the Ant build framework for creating custom tasks.
 */
public class CopyKeys extends Task {

    String strFromLocalDir = null;
    String strToRemoteDir = null;
    public void execute() throws BuildException {
        try {
            Node node = TopologyDefaults.getDefaultAppServer().getNode();
            if( strFromLocalDir == null ){
                strFromLocalDir = AppConst.FVT_HOME + "/src/keys";
            }
            File keysDir = new File(strFromLocalDir );
            String[] keys = keysDir.list();
            if( strToRemoteDir == null ){
                strToRemoteDir = node.getProfileDir() + "/etc/ws-security/samples";
            }
            String destDir = strToRemoteDir; 
            node.getMachine().getFile(destDir).mkdirs();

            System.out.println( "copy from localDir:'" + strFromLocalDir + "' to remote '" + strToRemoteDir + "'" );

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

    public void setFromLocalDir(String localDir) {
        strFromLocalDir = localDir;
    }

    public void setToRemoteDir(String remoteDir) {
        strToRemoteDir = remoteDir;
    }

}
