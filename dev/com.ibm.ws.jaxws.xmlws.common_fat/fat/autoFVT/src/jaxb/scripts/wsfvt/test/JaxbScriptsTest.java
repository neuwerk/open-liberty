//
// autoFVT/src/jaxb/scripts/wsfvt/test/JaxbScriptsTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 05/18/06 ulbricht    LIDB3295-18.01  New File
// 05/25/07 jramos      440922          Integrate ACUTE
// 04/29/09 btiffany    587427.1        add diagnostic output
// 05/01/09 jramos      587427          Change .equals to .contains
//

package jaxb.scripts.wsfvt.test;

import java.util.ArrayList;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.ws.wsfvt.build.tools.Utils;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import common.utils.execution.ExecutionFactory;

/**
 * This class will performs some tests of the JAXB scripts.
 */
public class JaxbScriptsTest extends
		com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static String profileBaseBinDir = null;
	private static String execFileSuffix = null;
	private static String hostName = null;
	private static boolean dumpedFileList = false;

	/**
	 * A constructor to create a test case with a given name.
	 * 
	 * @param name
	 *            The name of the test case to be created
	 */
	public JaxbScriptsTest(String name) {
		super(name);
	}

	/**
	 * The main method
	 * 
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	/**
	 * This method controls which test methods to run.
	 * 
	 * @return A suite of tests to run
	 */
	public static Test suite() {
		return new TestSuite(JaxbScriptsTest.class);
	}

	/**
	 * This is a set up method that will run before each test method.
	 */
	public void setUp() throws Exception {

		if (hostName == null) {
			ApplicationServer server = TopologyDefaults.getDefaultAppServer();
			profileBaseBinDir = server.getNode().getProfileDir() + "/jaxb/bin";
			profileBaseBinDir = profileBaseBinDir.replace('\\', '/');
			execFileSuffix = server.getNode().getMachine().getOperatingSystem()
					.getDefaultScriptSuffix();
			hostName = server.getNode().getMachine().getHostname();
		}

	}

	/**
	 * This test method will verify that the JAXB xjc script exists.
	 * 
	 * @throws Exception
	 *             Any kind of exception
	 * @testStrategy This test will verify that the xjc script exists
	 */
	@com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test will verify that the xjc script exists", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testXjcExists() throws Exception {
		findFile("xjc" + execFileSuffix);
	}

	/**
	 * This is a utility method for the test to find a file in the WebSphere App
	 * Server bin directory.
	 * 
	 * @return True if the file exists
	 * @throws Exception
	 *             Any kind of exception
	 */
	private void findFile(String fileName) throws Exception {

		String[] files = ExecutionFactory.getExecution().executeListDirectory(
				profileBaseBinDir, hostName);

		// 587427 - dump this so we can figure out why it doesn't work
		// sometimes.
		if (!dumpedFileList) {
			System.out
					.println("result of ExecutionFactory.getExecution().executeListDirectory( "
							+ profileBaseBinDir + " , " + hostName + " ):");
			for (int i = 0; i < files.length; i++) {
				System.out.println(files[i]);
			}
			dumpedFileList = true;
		}

		boolean found = false;
		for (int i = 0; ((i < files.length) && (!found)); i++) {
			if (files[i].contains(fileName)) {
				found = true;
			}
		}

		assertTrue("Did not find file: " + fileName + " in directory "
				+ profileBaseBinDir + " on host " + hostName + ".", found);
	}

	/**
	 * This test method will verify that the JAXB schemagen script exists.
	 * 
	 * @throws Exception
	 *             Any kind of exception
	 * @testStrategy This test will verify that the xjc script exists
	 */
	@com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test will verify that the xjc script exists", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSchemagenExists() throws Exception {
		findFile("schemagen" + execFileSuffix);
	}

	/**
	 * This test will verify that the xjc script is executable.
	 * 
	 * @throws Exception
	 *             Any kind of exception
	 * @testStrategy This test will verify that the xjc script is executable
	 */
	@com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test will verify that the xjc script is executable", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testXjcExecutable() throws Exception {
		checkExecutable("xjc");
	}

	/**
	 * This test will verify that the schemagen script is executable.
	 * 
	 * @throws Exception
	 *             Any kind of exception
	 * @testStrategy This test will verify that the schemagen script is
	 *               executable
	 */
	@com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test will verify that the schemagen script is executable", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSchemagenExecutable() throws Exception {
		checkExecutable("schemagen");
	}

	/**
	 * This is a utility method for the scripts to determine if the file is
	 * executable.
	 * 
	 * @param fileName
	 *            The executable file name
	 * @throws Exception
	 *             any kind of exception
	 */
	private void checkExecutable(String fileName) throws Exception {

		StringBuffer cmd = new StringBuffer();
		cmd.append(profileBaseBinDir + "/" + fileName + execFileSuffix);

		System.out.println("*** EXECUTING: " + cmd + " ***");
		ApplicationServer server = TopologyDefaults.getDefaultAppServer();

		Properties p = null;
		/*
		 * for Windows, HP, and AIX, leaving the p = null seems to be the correct thing to do.
		 */
//		if (server.getNode().getMachine().getOperatingSystem() != OperatingSystem.HP) {
//			/*
//			 * for some reason under a C-shell running locally on an HP-UX
//			 * machine, the properties must be null (inherit environment
//			 * variables from the running process, see Runtime/Process exec
//			 * method for empty Properties object vs. null). Otherwise, simple
//			 * commands like the "dirname" in some of the tested shell scripts
//			 * do not work. When run on the command line (outside of
//			 * automation), the scripts seem fine so inheriting the enviornment
//			 * does not seem like an issue.
//			 */
//			p = new Properties();
//		}
		ProgramOutput output = server.getNode().getMachine().execute(
				cmd.toString(), new String[] {}, profileBaseBinDir,
				p);
		System.out.println("*** RESULT:  " + output.getStdout() + " -------\n"
				+ output.getStderr());

		String expected = "Usage: " + fileName;
		String sysOutData = output.getStdout();
		String sysErrData = output.getStderr();
		assertNotNull("The System.out should have contained: \"" + expected
				+ "\", but instead contained null.  The System.err "
				+ "contained \"" + sysErrData + "\".", sysOutData);
		assertTrue("Expected the input stream to contain a String" + " with \""
				+ expected + "\", but instead it " + "contained \""
				+ sysOutData + "\"", sysOutData.indexOf(expected) != -1);
		assertTrue("The System.err was expected to not contain any "
				+ "data, but instead contained: \"" + sysErrData + "\".",
				(sysErrData == null || sysErrData.length() == 0));

	}

}
