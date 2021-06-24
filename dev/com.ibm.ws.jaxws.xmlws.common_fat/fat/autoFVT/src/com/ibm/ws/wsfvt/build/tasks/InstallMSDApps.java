/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/InstallMSDApps.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:31 [8/8/12 06:30:56]
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
 * 09/17/2008  syed        549282          New File: automate MSD setup
 * 10/29/2008  jramos      559143          Incorporate Simplicity
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task will start all the Applications that are installed on the server.
 * 
 * @author smithd
 * 
 */
public class InstallMSDApps extends Task {
	

       private String installDir = (AppConst.FVT_HOME).replace('\\', '/')
                   + "/build/installableApps";
		
       private String ndEar1 = installDir + "/Wssecfvt_SecDomainsND_01.ear";
       private String ndEar2 = installDir + "/Wssecfvt_SecDomainsND_02.ear";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {

        Cell cell = null;
        try {
            cell = TopologyDefaults.getDefaultAppServer().getCell();
            Node myNode = TopologyDefaults.getDefaultAppServer().getNode();
    
            String cellName = cell.getName();
            String nodeName = myNode.getName();
    
            String  installEar1 = "AdminApp.install('" + ndEar1 + "',"
                  + " '[ -nopreCompileJSPs -distributeApp -nouseMetaDataFromBinary" 
                  + " -nodeployejb -appname Wssecfvt_SecDomainsND_01"
                  + " -createMBeansForResources -noreloadEnabled -nodeployws"
                  + " -validateinstall warn -noprocessEmbeddedConfig"
                  + " -filepermission .*\\.dll=755#.*\\.so=755#.*\\.a=755#.*\\.sl=755"
                  + " -noallowDispatchRemoteInclude -noallowServiceRemoteInclude"
                  + " -asyncRequestDispatchType DISABLED -nouseAutoLink"
                  + " -MapModulesToServers [[ .*\\.war .*\\.war,WEB-INF/web.xml"
                  + " WebSphere:cell=" + cellName +  ",node=" + nodeName 
                  + ",server=server1 ] -MapWebModToVH [[ .*\\.war .*\\.war,"
                  + "WEB-INF/web.xml default_host ]]]' )";
                   
            String  installEar2 = "AdminApp.install('" + ndEar2 + "',"
                  + " '[ -nopreCompileJSPs -distributeApp -nouseMetaDataFromBinary" 
                  + " -nodeployejb -appname Wssecfvt_SecDomainsND_02"
                  + " -createMBeansForResources -noreloadEnabled -nodeployws"
                  + " -validateinstall warn -noprocessEmbeddedConfig"
                  + " -filepermission .*\\.dll=755#.*\\.so=755#.*\\.a=755#.*\\.sl=755"
                  + " -noallowDispatchRemoteInclude -noallowServiceRemoteInclude"
                  + " -asyncRequestDispatchType DISABLED -nouseAutoLink"
                  + " -MapModulesToServers [[ .*\\.war .*\\.war,WEB-INF/web.xml"
                  + " WebSphere:cell=" + cellName +  ",node=" + nodeName 
                  + ",server=server2 ] -MapWebModToVH [[ .*\\.war .*\\.war,"
                  + "WEB-INF/web.xml wsfvt_vhost1 ]]]' )";
    
            // System.out.println("Script command installEar1 : " + installEar1);
            // System.out.println("Script command installEar2 : " + installEar2);
    
            Wsadmin wsadmin = Wsadmin.getProviderInstance(cell);
            
            System.out.println("Installing " + ndEar1);
            System.out.println(wsadmin.executeCommand(installEar1));
    
            System.out.println("Installing " + ndEar2);
            System.out.println(wsadmin.executeCommand(installEar2));
            
            cell.getWorkspace().saveAndSync();
        } catch(Exception e) {
            if(cell != null) {
                try {
                    cell.getWorkspace().discard();
                } catch (Exception e1) {
                }
            }
            throw new BuildException(e);
        }

   }  // execute

}

