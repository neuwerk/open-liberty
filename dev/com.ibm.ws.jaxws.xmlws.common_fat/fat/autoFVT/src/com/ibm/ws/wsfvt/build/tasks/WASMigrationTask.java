/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/WASMigrationTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/27/10 16:50:44 [8/8/12 06:41:04]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 06/12/08    jramos      524904             New File
 * 11/03/2008  jramos      559143            Incorporate Simplicity
 * 05/27/10   agheinzm     654566            Add the command that is running to logging
 */

 package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.MigrationUtils;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;

/**
 * This task is for performing a WAS migration.
 * Currently only BASE to BASE migration is supported.
 * 
 * @author jramos
 */
public class WASMigrationTask extends Task {
    
    public void execute() throws BuildException {
        try {
            Node sourceNode = MigrationUtils.getSourceServer().getNode();
            Node targetNode = MigrationUtils.getTargetServer().getNode();
            
            // save the wsadmin.properties file for the target profile
            RemoteFile wsadminProps = targetNode.getMachine().getFile(targetNode.getProfileDir() + "/properties/wsadmin.properties");
            RemoteFile backupWsadminProps = Machine.getLocalMachine().getFile(AppConst.FVT_BUILD_WORK_DIR + "/wsadmin.properties");
            wsadminProps.copyToDest(backupWsadminProps);
            
            // perform WASPreUpgrade
            System.out.println("Executing WASPreUpgrade command.");
            String command = targetNode.getWASInstall().getInstallRoot() + "/bin/WASPreUpgrade" + targetNode.getMachine().getOperatingSystem().getDefaultScriptSuffix();
            List<String> params = new LinkedList<String>();
            params.add(MigrationUtils.getProfileMigrationDirectory().getAbsolutePath().replace('\\', '/'));
            params.add(sourceNode.getWASInstall().getInstallRoot());
            params.add("-oldProfile");
            params.add(sourceNode.getProfileName());
            String paramsString = command;
            for(String string : params){
            	paramsString += " " + string;
            }
            System.out.println(paramsString);
            String result = targetNode.getMachine().execute(command, params.toArray(new String[0])).getStdout();
            System.out.println(result);
            
            // log the output
            File preUpgradeLog = MigrationUtils.getWASPreUpgradeSummaryLog();
            preUpgradeLog.delete();
            try {
                Operations.writeFile(preUpgradeLog.getAbsolutePath(), result);
            } catch(Exception e) {
                log("Unable to create " + preUpgradeLog.getAbsolutePath());
            }
			
            if(result.indexOf("MIGR0420I") == -1) {
                throw new BuildException("Migration failed!!!! WASPreUpgrade did not complete successfully.");
            }
            
            // perform WASPostUpgrade
            System.out.println("Executing WASPostUpgrade command.");
            command = targetNode.getWASInstall().getInstallRoot() + "/bin/WASPostUpgrade" + targetNode.getMachine().getOperatingSystem().getDefaultScriptSuffix();
            params = new LinkedList<String>();
            params.add(MigrationUtils.getProfileMigrationDirectory().getAbsolutePath().replace('\\', '/'));
            params.add("-profileName");
            params.add(targetNode.getProfileName());
            params.add("-oldProfile");
            params.add(sourceNode.getProfileName());
            params.add("-backupConfig");
            params.add("true");
            if(targetNode.getCell().getSecurityConfiguration().getGlobalSecurityDomain().isGlobalSecurityEnabled()) {
                params.add("-username");
                params.add(targetNode.getCell().getConnInfo().getUser());
                params.add("-password");
                params.add(targetNode.getCell().getConnInfo().getPassword());
            }
            params.add("-replacePorts");
            params.add("false");
            params.add("-includeApps");
            params.add("true");
            params.add("-scriptCompatibility");
            params.add("true");
            paramsString = command;
            for(String string : params){
            	paramsString += " " + string;
            }
            System.out.println(paramsString);
            result = targetNode.getMachine().execute(command, params.toArray(new String[0])).getStdout();
            System.out.println(result);
                
            // log the output
            File postUpgradeLog = MigrationUtils.getWASPostUpgradeSummeryLog();
            postUpgradeLog.delete();
            try {
                Operations.writeFile(postUpgradeLog.getAbsolutePath(), result);
            } catch(Exception e) {
                log("Unable to create " + postUpgradeLog.getAbsolutePath());
            }
			
            if(result.indexOf("MIGR0271W") == -1) {
                throw new BuildException("Migration failed!!!! WASPostUpgrade did not complete successfully.");
            }
            
            // restore the wsadmin.properties file
            wsadminProps.copyFromSource(backupWsadminProps);
        } catch(Exception e) {
            throw new BuildException(e);
        }
    }
    
    public static void main(String[] args) {
        WASMigrationTask task = new WASMigrationTask();
        task.execute();
    }
}
