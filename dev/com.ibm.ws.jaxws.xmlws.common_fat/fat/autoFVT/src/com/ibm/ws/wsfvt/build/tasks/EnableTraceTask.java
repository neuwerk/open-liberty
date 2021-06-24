/*
 * @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/EnableTraceTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:24 [8/8/12 06:56:43]
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
 * 10/09/2003  ulbricht    D179331            Use wsadmin to enable/disable trace
 * 03/11/2005  ulbricht    D261009            Add ability to use different profile
 * 08/10/2006  smithd      D381622            Add ND support
 * 04/02/2007  jramos      430068             Add ability to set client trace and trace while server is running
 * 05/11/2007  jramos      438657             Write client trace packages on separate lines
 * 10/22/2007  jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/29/2008  jramos      559143             Incorporate Simplicity
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.config.TraceService;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The EnableTraceTask class is a custom task for enabling trace on a WebSphere package or class.
 * 
 * The class conforms to the standards set in the Ant build framework for creating custom tasks.
 */
public class EnableTraceTask extends Task {

	private String serverName = "server1";
	private File installRootLocation;
	private String packageName = "com.ibm.ws.webservices.*=all=enabled";
	private boolean modifyServerTrace = false;
	private boolean enableTrace = false;
	private String profile = "default";
	private boolean modifyClientTrace = false;
	private boolean enableClientTrace = false;
	private String clientPackageName = "com.ibm.ws.webservices.*=all=enabled";
	private String clientTraceFile = null;
	private boolean serverRunning = false;

	/**
	 * This method will enable trace for the packageName that is specified as a parameter to this
	 * task.
	 */
	public void execute() throws BuildException {

		try {
            Machine localMachine = Machine.getLocalMachine();
            log("EnableTraceTask has the following values: " + localMachine.getOperatingSystem().getLineSeparator()
                    + "    serverName          = " + serverName + localMachine.getOperatingSystem().getLineSeparator()
                    + "    packageName         = " + packageName + localMachine.getOperatingSystem().getLineSeparator()
                    + "    enableTrace         = " + enableTrace + localMachine.getOperatingSystem().getLineSeparator()
                    + "    profile             = " + profile + localMachine.getOperatingSystem().getLineSeparator()
                    + "    installRootLocation = " + installRootLocation
                    + localMachine.getOperatingSystem().getLineSeparator(), Project.MSG_VERBOSE);
            
            Cell cell = TopologyDefaults.getDefaultAppServer().getCell();
            
			if (modifyServerTrace) {
                Set<Node> nodes = cell.getNodes();
                for (Node node : nodes) {
                    Set<Server> servers = node.getServers();
                    for (Server server : servers) {
                        if (enableTrace) {
                            if (serverRunning) {
                                ((ApplicationServer) server).getRuntimeServices().getTraceService()
                                        .setTraceSpecification(packageName);
                            } else {
                                TraceService.getInstance((ApplicationServer) server).setTraceSpecification(packageName);
                            }
                        } else {
                            if (serverRunning) {
                                ((ApplicationServer) server).getRuntimeServices().getTraceService()
                                        .setTraceSpecification("");
                            } else {
                                TraceService.getInstance((ApplicationServer) server).setTraceSpecification("");
                            }
                        }
                    }
                }
                cell.getWorkspace().saveAndSync();
            }

			if (modifyClientTrace) {
				// enable client trace if requested
				File tempTraceSettings = null;
				tempTraceSettings = new File(AppConst.FVT_HOME
						+ "/build/work/TraceSettings.properties");
				tempTraceSettings.delete();
				if (enableClientTrace) {
					if (clientTraceFile == null)
						throw new BuildException(
								"clientTraceFile must be specified to set client trace");

					FileOutputStream fos = null;
					PrintStream ps = null;
					try {
						fos = new FileOutputStream(tempTraceSettings);
						ps = new PrintStream(fos);
						StringTokenizer st = new StringTokenizer(clientPackageName, ":");
						String nextPackage = "";
						while(st.hasMoreTokens()) {
							nextPackage = st.nextToken();
							ps.println(nextPackage);
						}
					} catch(Exception e) {

						throw new BuildException(e);
					} finally {
						if (ps != null)
							ps.close();
						else if (fos != null)
							fos.close();
					}
					// copy file to local WAS_HOME
                    RemoteFile source = Machine.getLocalMachine().getFile(tempTraceSettings.getAbsolutePath());
                    RemoteFile dest = Machine.getLocalMachine().getFile(AppConst.WAS_HOME + "/properties/TraceSettings.properties");
                    source.copyToDest(dest);
				} else {
					File traceSettings = new File(AppConst.WAS_HOME
							+ "/properties/TraceSettings.properties");
					System.out.println(traceSettings.delete());
				}
			}
		} catch (Exception e) {
            e.printStackTrace();
			throw new BuildException(e);
		}

	}

	/**
	 * This method accepts a string containing the name of the server.
	 * 
	 * @param serverName
	 *            A string containing the server name (ex. server1)
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * This method accepts a string containing the name of the base WAS install root.
	 * 
	 * @param installRootLocation
	 *            Name of the base WAS install root
	 */
	public void setInstallRootLocation(File installRootLocation) {
		this.installRootLocation = installRootLocation;
	}

	/**
	 * This method accepts a string containing the name of the trace package.
	 * 
	 * @param packageName
	 *            A String containing the name of the package to trace
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * This will set whether trace is to be set on the server or not.
	 * 
	 * param enableTrace True if trace should be set on the server
	 */
	public void setTrace(boolean enableTrace) {
		this.enableTrace = enableTrace;
	}

	/**
	 * This will set the profile name for which the trace should be modified.
	 * 
	 * @param profile
	 *            The name of the profile that is being used
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}

	/**
	 * This will set whether trace is to be set on the client or not.
	 * 
	 * @param enableClientTrace
	 *            True if the trace should be set on the client
	 */
	public void setClientTrace(boolean enableClientTrace) {
		this.enableClientTrace = enableClientTrace;
	}

	/**
	 * This method accepts a string containing the name of the trace package for the client.
	 * 
	 * @param clientPackageName
	 *            A String containing the name of the package to trace for the client
	 */
	public void setClientPackageName(String clientPackageName) {
		this.clientPackageName = clientPackageName;
	}

	/**
	 * This will set the trace file for the client trace.
	 * 
	 * @param clientTraceFile
	 *            A String containing the path to write the client trace file to on the local
	 *            machine
	 */
	public void setClientTraceFile(String clientTraceFile) {
		this.clientTraceFile = clientTraceFile;
	}

	/**
	 * This will set whether or not the server(s) is(are) running. This affects how trace is
	 * enabled.
	 * 
	 * @param serverRunning
	 *            True if the server(s) is(are) running.
	 */
	public void setServerRunning(boolean serverRunning) {
		this.serverRunning = serverRunning;
	}

	/**
	 * This will set whether or not the server trace should be modified.
	 * 
	 * @param modifyServerTrace
	 *            True if server trace should be enabled or disabled.
	 */
	public void setModifyServerTrace(boolean modifyServerTrace) {
		this.modifyServerTrace = modifyServerTrace;
	}

	/**
	 * This will set whether or not the client trace should be modified.
	 * 
	 * @param modifyClientTrace
	 *            True if the client trace should be enabled or disabled
	 */
	public void setModifyClientTrace(boolean modifyClientTrace) {
		this.modifyClientTrace = modifyClientTrace;
	}
}
