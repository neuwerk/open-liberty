/*
 * @(#) 1.7 autoFVT/src/com/ibm/ws/wsfvt/build/tools/IHS.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/9/09 12:35:02 [8/8/12 06:40:26]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 11/08/2006  jramos      404047             New File
 * 12/01/2006  jramos      405804             Edited Dmgr constructor call
 * 01/04/2007  smithd      411800             Changes to executeCommand methods
 * 04/16/2007  jramos      432444             Edited Dmgr constructor call
 * 05/24/2007  jramos      440922             Changes for Pyxis
 */

package com.ibm.ws.wsfvt.build.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import common.utils.CommonUtilsConst;
import common.utils.execution.ExecutionException;
import common.utils.execution.ExecutionFactory;
import com.ibm.websphere.simplicity.OperatingSystem;
import common.utils.topology.IMachine;
import common.utils.topology.MachineFactory;

// TODO: Update this class when Simplicity supports IHS
public class IHS {

	/**
	 * Constants
	 */
	public static final String IHS_DEFAULT_KEY = "web-server-1";
	public static final String WINDOWS_START_FILE = "Apache.exe";
	public static final String LINUX_START_FILE = "apachectl";
	
	/**
	 * private data
	 */
	private String ihsInstallRoot = null;
	private String hostname = null;
	private boolean isWindows = false;

	/**
	 * Default Constructor. 
	 * Uses the default topology props file and default key.
	 */
	public IHS() {
		this(CommonUtilsConst.DEFAULT_TOPOLOGY_FILE, IHS_DEFAULT_KEY);
	}

	/**
	 * Constructor that takes in a topology properties file.
	 * Uses the default key.
	 * 
	 * @param topologyPropsFile 
	 *            The topology properties file to use to create the IHS object
	 */
	public IHS(File topologyPropsFile) {
		this(topologyPropsFile, IHS_DEFAULT_KEY);
	}
	
	/**
	 * Constructor that takes in a topology props file and web server key
	 * 
	 * @param topologyPropsFile
	 *            The topology properties file to use to create the IHS object
	 * @param webServerKey
	 *            The property key in the properties file
	 */
	public IHS(File topologyPropsFile, String webServerKey) {
		// load the IHS install root from the topology props file
		Properties propsReader = new Properties();
		try {
			propsReader.load(new FileInputStream(topologyPropsFile));
			ihsInstallRoot = propsReader.getProperty(IHS_DEFAULT_KEY + ".installRoot");
			hostname = propsReader.getProperty(IHS_DEFAULT_KEY + ".hostname");
            IMachine machine = null;
            try {
                machine = MachineFactory.getMachine(topologyPropsFile, hostname);
            } catch (ExecutionException e) {
                e.printStackTrace();
                System.out
                .println("[ERROR]: Unexpected ExecutionException loading topology properties file from "
                        + topologyPropsFile.getPath() + ".");
            }
			isWindows = (machine.getOperatingSystem() == OperatingSystem.WINDOWS);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out
					.println("[ERROR]: Unexpected FileNotFoundException loading topology properties file from "
							+ topologyPropsFile.getPath() + ".");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out
					.println("[ERROR]: Unexpected IOException loading topology properties file from "
							+ topologyPropsFile.getPath() + ".");
			return;
		}
		
	}
	
	/**
	 * Start IHS
	 * 
	 * @throws STAFException
	 */
	public void startIHS() throws ExecutionException {
		try {
			ExecutionFactory.getExecution().executeCommand(getIHSCommandPrefix(), getStartIHSCommandParams(), hostname);
		} catch(ExecutionException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Get the params to start IHS
	 * 
	 * @return The start IHS params
	 */
	private List<String> getStartIHSCommandParams() {
		List<String> params = new ArrayList<String>();
		params.add(" -k start");
		
		return params;
	}
	
	/**
	 * Stop IHS
	 * 
	 * @throws STAFException
	 */
	public void stopIHS() throws ExecutionException {
		try {
            ExecutionFactory.getExecution().executeCommand(getIHSCommandPrefix(), getStopIHSCommandParams(), hostname);
		} catch(ExecutionException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Get the params to stop IHS
	 * 
	 * @return The stop IHS params
	 */
	private List<String> getStopIHSCommandParams() {
		List<String> params = new ArrayList<String>();
		params.add(" -k stop");
		
		return params;
	}
	
	/**
	 * Get the IHS start/stop command prefix
	 * 
	 * @return The beginning of the IHS start/stop command
	 */
	private String getIHSCommandPrefix() {
		String cmd = ihsInstallRoot + "/bin/";
		if(isWindows)
			cmd += WINDOWS_START_FILE;
		else
			cmd += LINUX_START_FILE;
		
		return cmd;
	}

	/**
	 * Get the IHS install root
	 * 
	 * @return The install root
	 */
	public String getIHSInstallRoot() {
		return this.ihsInstallRoot;
	}
	
	/**
	 * Get the IHS hostname
	 * 
	 * @return The hostname
	 */
	public String getHostname() {
		return this.hostname;
	}
	
	/**
	 * Get whether or not the IHS Server is on Windows
	 * 
	 * @return True if on Windows
	 */
	public boolean isWindows() {
		return this.isWindows;
	}
	
	public static void main(String[] args) {
		IHS ihs = new IHS();
		System.out.println(ihs.isWindows());
	}
}
