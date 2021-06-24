/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/GenPluginCfgTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:28 [8/8/12 06:56:43]
 *
 * COMPONENT_NAME: WAS.webservices.fvt
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2003, 2004
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect       Author           Description
 * ----------  -----------------    ---------        ----------------------------
 * 02/25/2003  LIDB1270.256.09      ulbricht         New file
 * 01/07/2004  D186680              ulbricht         z/OS needs different exec method
 * 08/17/2006  D381622              smithd           Add ND support
 * 01/04/2007  D411800              smithd           Changes to executeCommand methods
 * 05/22/2007  440922               jramos           Changes for Pyxis
 * 10/29/2008  559143               jramos           Incorporate Simplicity
 *
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Node;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import common.utils.execution.ExecutionFactory;

/**
 * This class will run the GenPluginCfg.bat/sh file which will create the
 * <WAS_HOME>/config/cells/plugin-cfg.xml file. This file allows WebSphere and web server to work
 * with each other.
 * 
 * The class conforms to the standards set in the Ant build framework for creating custom tasks.
 */
public class GenPluginCfgTask extends Task {

	/**
	 * This method will perform the command necessary to run the GenPluginCfg.
	 * 
	 * @throws org.apache.tools.ant.BuildException
	 *             Any kind of build exception
	 */
	public void execute() throws BuildException {

        try {
            Node node = TopologyDefaults.getDefaultAppServer().getNode();
    		String fileSeparator = node.getMachine().getOperatingSystem().getLineSeparator();
    
    		File genPluginCfg = new File(node.getWASInstall().getInstallRoot(), fileSeparator + "bin"
    				+ fileSeparator + "GenPluginCfg" + node.getMachine().getOperatingSystem().getDefaultScriptSuffix());
    
    		if (!genPluginCfg.exists()) {
    			throw new BuildException("GenPluginCfg not found at " + genPluginCfg.getPath() + ".");
    		}
    
    			ExecutionFactory.getExecution().executeCommand(genPluginCfg.getPath(), new ArrayList(),
    					node.getMachine().getHostname());
		} catch (Exception e) {
			throw new BuildException(e);
		}

	}

}
