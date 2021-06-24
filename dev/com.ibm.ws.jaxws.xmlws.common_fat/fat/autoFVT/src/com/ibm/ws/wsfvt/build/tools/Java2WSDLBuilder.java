/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tools/Java2WSDLBuilder.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:18 [8/8/12 06:56:44]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 04/15/2003  ulbricht    D164386             New File
 * 01/07/2004  ulbricht    D186680             z/OS needs different exec method
 * 01/04/2007  smithd      D411800             Changes to executeCommand methods
 * 05/24/2007  jramos      440922              Changes for Pyxis
 * 11/03/2008  jramos      550143              Incorporate Simplicity
 *
 */
package com.ibm.ws.wsfvt.build.tools;

import org.apache.tools.ant.BuildException;

import com.ibm.websphere.simplicity.Machine;

/**
 * This class is an abstract class for running the Java2WSDL command.
 */
public abstract class Java2WSDLBuilder {

	/**
	 * This class will start the Java2WSDL process.
	 * 
	 * @param args
	 *            The command line args
	 */
	public static void main(String[] args) {
		try {
			String testBuilderName = System.getProperty("test.builder");
			Class testBuilderCls = Class.forName(testBuilderName);
			Java2WSDLBuilder builder = (Java2WSDLBuilder) testBuilderCls.newInstance();
			builder.runJava2WSDL(builder.getJava2WSDLCmd());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is an abstract method that must be defined by extended classes to define the Java2WSDL
	 * command to run.
	 * 
	 * @return A string containing the entire Java2WSDL command
	 */
	public abstract String getJava2WSDLCmd();

	/**
	 * This method will run the Java2WSDL command through a Process object.
	 * 
	 * @param cmd
	 *            The Java2WSDL command to run
	 */
	public void runJava2WSDL(String cmd) {
		System.out.println("******* " + cmd + " *********");
		try {
			// I don't know where the command is defined, where it should be run against, or if this
			// is being used.. so no params and use local hostname
            Machine.getLocalMachine().execute(cmd);
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

}
