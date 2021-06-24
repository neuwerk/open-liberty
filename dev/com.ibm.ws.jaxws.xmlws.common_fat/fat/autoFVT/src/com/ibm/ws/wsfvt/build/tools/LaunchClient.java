//
// @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tools/LaunchClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:20 [8/8/12 06:56:44]
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date         UserId      Defect          Description
// ----------------------------------------------------------------------------
// 12/01/2003   ulbricht    158794.1        New File
// 06/18/2004   ulbricht    210605          Add proxy server capability
// 05/05/2005   ulbricht    273758          Need to use profileName
// 07/29/2005   ulbricht    294431          Add bootstrap port for iSeries
// 10/11/2005   ulbricht    308997          Update for z/OS
// 08/15/2006   smithd      381622          Add ND support
// 05/24/2007   jramos      440922          Changes for Pyxis
// 11/03/2008   jramos      550143          Incorporate Simplicity
//
package com.ibm.ws.wsfvt.build.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This class allows for an abstraction of the launchClient command that has proliferated in the
 * JSR-109 style Web services tests.
 * 
 * It was originally created because of the need to allow trace on the client.
 */
public class LaunchClient {

	private String earName;
	private String jarName;
	private List<String> paramList;
	private boolean secManager;

	/**
	 * A no arg constructor for LaunchClient.
	 */
	public LaunchClient() {

	}

	/**
	 * A setter for the ear file name.
	 * 
	 * @param _earName
	 *            The application client ear file name
	 */
	public void setEarName(String _earName) {
		earName = _earName;
	}

	/**
	 * A getter for the ear file name.
	 * 
	 * @return The application client ear name.
	 */
	public String getEarName() {
		return earName;
	}

	/**
	 * A setter for the jar name to be used within the application client ear.
	 * 
	 * @param _jarName
	 *            The jar to run within the application client
	 */
	public void setJarName(String _jarName) {
		jarName = _jarName;
	}

	/**
	 * A getter for the jar file name.
	 * 
	 * @return The jar file name to be run.
	 */
	public String getJarName() {
		return jarName;
	}

	/**
	 * A setter for the security manager to be used within the application client ear.
	 * 
	 * @param _jarName
	 *            The jar to run within the application client
	 */
	public void setSecManager() {
		secManager = true;
	}

	/**
	 * This method allows for adding command line arguments to the application being called through
	 * launchClient.
	 * 
	 * @param param
	 *            A command line argument
	 */
	public void addParam(String param) {
		if (paramList == null) {
			paramList = new ArrayList<String>();
		}
		paramList.add(param);
	}

	/**
	 * This method will format the launchClient command.
	 * 
	 * @return The launchClient command
	 */
	public String getCmd() {
        try {
    		// get the default server
    		ApplicationServer server = TopologyDefaults.getDefaultAppServer();
    		StringBuffer cmd = new StringBuffer();
    		cmd.append(server.getNode().getProfileDir() + "/bin/");
    		cmd.append("launchClient" + server.getNode().getMachine().getOperatingSystem().getDefaultScriptSuffix());
    		cmd.append(" -profileName " + server.getNode().getProfileName());
    		cmd.append(" " + earName);
    		cmd.append(" -CCDuser.install.root=" + server.getNode().getProfileDir());
    		if (jarName != null) {
    			cmd.append(" -CCjar=" + jarName);
    		}
//    		if (server.getNode().getMachine().getOperatingSystem() == OperatingSystem.ISERIES
//    				&& server.getPortNumber(PortType.BOOTSTRAP_ADDRESS) != -1) {
//    			cmd.append(" -CCBootstrapPort=" + server.getPortNumber(PortType.BOOTSTRAP_ADDRESS));
//    		}
    		if (AppConst.CLIENT_TRACE_ENABLED) {
    			cmd.append(" -CCtrace=" + AppConst.CLIENT_TRACE_PKG_NAME + "=all=enabled");
    			cmd.append(" -CCtracefile=" + AppConst.FVT_HOME + "/logs/was/CLIENT-"
    					+ new File(earName).getName());
    			if (jarName != null) {
    				cmd.append("-" + jarName);
    			}
    			cmd.append("-trace.log");
    		}
    		if (secManager && server.getNode().getMachine().getOperatingSystem() != OperatingSystem.ZOS) {
    			cmd.append(" -CCsecurityManager=enable");
    		}
    		if ((AppConst.HTTP_PROXY_HOST != null) && (AppConst.HTTP_PROXY_PORT != null)) {
    			cmd.append(" -CCDhttp.proxyHost=" + AppConst.HTTP_PROXY_HOST);
    			cmd.append(" -CCDhttp.proxyPort=" + AppConst.HTTP_PROXY_PORT);
    		}
    		if (paramList != null && !paramList.isEmpty()) {
    			for (Iterator i = paramList.iterator(); i.hasNext();) {
    				cmd.append(" " + (String) i.next());
    			}
    		}
    		String cmdString = cmd.toString();
    		System.out.println("*** " + cmdString + " ***");
    		return cmdString;
        } catch(Exception e) {
            return null;
        }
	}

}
