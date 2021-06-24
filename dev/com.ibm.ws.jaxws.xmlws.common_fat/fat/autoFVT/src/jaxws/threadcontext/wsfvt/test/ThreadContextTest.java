// @(#) 1.5 autoFVT/src/jaxws/threadcontext/wsfvt/test/ThreadContextTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 7/21/11 17:23:38 [8/8/12 06:39:43]
//
// IBM Confidential OCO Source Materials
//
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2006, 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/03/10 varadan     637058.2        Initial Version. FVT testcases for JAX-WS Async callback thread context
// 05/13/10 padams      648719          Improve serviceability.
// 05/24/10 padams      648719.1        Pass hostAndPort to the app client on cmd-line
// 07/21/11 jtnguyen    703199          remove env parm from launchClient cmd for AIX/HP
//

package jaxws.threadcontext.wsfvt.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.Utils;
import com.ibm.ws.wsfvt.build.tools.configRequirements.Requirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import com.ibm.ws.wsfvt.test.framework.FvtTest;
import common.utils.execution.ExecutionFactory;
import common.utils.execution.OperatingSystem;
import common.utils.topology.Cell;
import common.utils.topology.IAppServer;
import common.utils.topology.TopologyActions;
import common.utils.topology.visitor.QueryDefaultNode;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ProgramOutput;

/**
 * This test checks that when a jndi initial context is used to make a request, and the response
 * is processed by an async method,  the context remains available to the async callback or 
 * response handler. 
    
 * The client is contained in a war file, so JNDI is available.  It is called by a servlet.
 * The testcase invokes the servlet and retrieves the result of the client call.
 */
public class ThreadContextTest extends FVTTestCase {
    
    public static final String ENDPOINT_URL = "/ThreadContextServlet/servlet";
    
    private static IAppServer server =
        QueryDefaultNode.defaultAppServer;
    
    // this gets used by the subclasses.
    
    protected static Cell cell = TopologyDefaults.defaultAppServerCell;
    private static String clientInstallRoot = null;
    private static String hostName = null;
//    private static int bootStrapPortNumber = -1;
    private static int serverPortNumber = -1;
    private static String hostAndPort = null;
    

    // allow us to warn if setup/teardown did not complete.
    private static boolean setupDirty = false;
        
    /* some FVTTestCase magic  that allows tests to be skipped under certain conditions */
    public void suiteSetup(ConfigRequirement reqs) throws Exception {
        super.suiteSetup(reqs);
        
        System.out.println("suiteSetup() called");    

        clientInstallRoot = TopologyDefaults.getDefaultAppServer()
                .getNode().getWASInstall().getInstallRoot();
        System.out.println("clientInstallRoot = " + clientInstallRoot);        

        hostName = TopologyDefaults.getDefaultAppServer().getNode().getMachine().getHostname();
//        bootStrapPortNumber = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.BOOTSTRAP_ADDRESS);
        serverPortNumber = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);

	System.out.println("hostName: " + hostName);
//	System.out.println("bootStrapPortNumber: " + bootStrapPortNumber);
	System.out.println("serverPortNumber: " + serverPortNumber);

        hostAndPort  = "http://" + hostName + ":" + serverPortNumber;
        System.out.println("hostAndPort = " + hostAndPort);
    }
    
    /**
     * This is a one arg constructor for JUnit.
     *
     * @param name The name of the test to run
     */
    public ThreadContextTest(String name) {
        super(name);
    }

    /**
     * The main method.
     *
     * @param args The command line args
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    

    /**
     * This method returns a suite of tests to run.
     *
     * @return A Test object containing the tests to run
     */
    public static Test suite() {
        return new TestSuite(ThreadContextTest.class);
    }

    /**
     * This method will test the Async ThreadContext using
     * SOAP 1.1.
     *
     * This test uses httpunit to make a call
     * to the ThreadContextServlet servlet. The WebForm object
     * of httpunit is used to fill in the parameters
     * necessary to call the Asynchronous ThreadContext service.
     * The text returned in the OUTPUT text field
     * is then queried for the correct values.
     * @throws Exception Any kind of exception
     */
    public void testThreadContext() throws Exception {
        if (server.getMachine().getOperatingSystem() == OperatingSystem.ZOS) {
            return;
        }

	System.out.println("hostAndPort = " + hostAndPort);

        String url = hostAndPort + ENDPOINT_URL + "?hostAndport=" + hostAndPort;
	System.out.println("url = " + url);

        String temp = com.ibm.ws.wsfvt.build.tools.utils.Operations.getUrlContents(url, 60);
        String expected = "hello there, hi server from Async"; 
        assertTrue("response page did not contain expected message :"+ expected + "\n actual:" + temp, temp.contains(expected));
    }
    
    /**
     * This test method will verify that an application from the client
     * container can use the Application Client product to access a JAX-WS Web
     * Service using HTTP.
     * 
     * @throws Exception
     *             any kind of exception
     */
    public void testThreadContextLaunchClient() throws Exception {
        String temp = callLaunchClient(hostAndPort);
        String expected = "hello there, hi server from Async"; 
        assertTrue("response page did not contain expected message :" + expected, 
		   temp.contains(expected));
    }
    
    /**
     * This is a utility method for calling the client from the client
     * container.
     *
     * @throws Exception
     *             any kind of exception
     */
    private String callLaunchClient(String hostAndPort) throws Exception {
        String cmd = clientInstallRoot
                + "/bin/launchClient"
                + Machine.getLocalMachine().getOperatingSystem()
                        .getDefaultScriptSuffix();

        List<String> parms = new ArrayList<String>();
        parms.add((AppConst.FVT_HOME + "/build/installableApps/ThreadContextClient.ear").replace('\\', '/'));
        parms.add("-CCBootstrapHost=" + hostName);
//        parms.add("-CCBootstrapPort=" + bootStrapPortNumber);                
	parms.add(hostAndPort);

	System.out.println("About to execute this command: " + cmd);
	System.out.println("   Command args: " + parms.toString());

        ProgramOutput output = Machine.getLocalMachine().execute(cmd,
                parms.toArray(new String[parms.size()]),
                clientInstallRoot + "/bin", null);

	System.out.println("stdout=[\n" + output.getStdout() + "\n]");
	System.out.println("stderr=[\n" + output.getStderr() + "\n]");

        return output.getStdout();
    }
    
    /**
     * This is a setUp method that runs before each test method.  It
     * is marked as final so that classes extending the WSFPSamplesTest
     * override the doSetUp() method instead of this method.
     */
    public final void setUp() {
        if (setupDirty){
            System.out.println(("WARNING: Prior setup or teardown did not complete normally. "+ 
                     "policy sets may be corrupt and cause tests to fail."));
        }
        setupDirty = true;
    }

    /**
     * This method will run after every test method.  It is
     * marked as final so that classes extending the WSFPSamplesTest
     * override the doTearDown() method instead of this method.
     */
    public final void tearDown() {
        doTearDown();
        setupDirty = false;
    }

    /** 
     * This is a no operation method for the base WSFPSamplesTest.
     * However, if a QOS were to extend this class, they could add
     * some operations that are necessary for tearDown purposes.
     */
    public void doTearDown() {
    }
}
