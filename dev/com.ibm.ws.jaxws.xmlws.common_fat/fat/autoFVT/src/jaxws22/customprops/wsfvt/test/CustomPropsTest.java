//
// @(#) 1.2 autoFVT/src/jaxws22/customprops/wsfvt/test/CustomPropsTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 7/21/11 15:13:06 [8/8/12 06:59:01]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date         UserId     Defect                   Description
// ----------------------------------------------------------------------
// 12/07/2010   jtnguyen   F743-23362           new file
// 07/21/2011   jtnguyen   703199               remove env parm from launchClient cmd for AIX/HP
//



package jaxws22.customprops.wsfvt.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ProgramOutput;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import jaxws22.customprops.wsfvt.client.*;


/**
 * This tests that the values from jaxws request context at runtime
 * must be overridden successfully using values from the properties in client CustomProperties policy.
 * We support Custom Properties on client side only, and at this time, only value of BindingProvider.ENDPOINT_ADDRESS_PROPERTY
 * can be overridden. 
 */
public class CustomPropsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {       
    
    private static final String SERVICE_NAME            = "CustomPropsService";
    private static final String CONTEXT_ROOT            = "CustomProps";    
    private static final String URL_IN_BINDING = "CustomPropsService_WRONG_NAME";
    
    private static String hostAndPort = null;

	static {
		try {
			String host = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
			String port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();                 
        
			hostAndPort  = "http://" + host + ":" + port ;    
			System.out.println("-- in test suiteSetup:  hostAndPort = "+hostAndPort );
		} catch (Exception e) {
		// do nothing
		}
	}

    /**
     * A constructor to create a test case with a given name.
     * 
     * @param name
     *            The name of the test case to be created
     */
    public CustomPropsTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite( CustomPropsTest.class );
    }

    /**
     * The main method.
     * 
     * @param args
     *            The command line arguments
     */
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }

    
    public void suiteSetup(ConfigRequirement req) throws Exception {
        super.suiteSetup(req);
        System.out.println("suiteSetup() called");       
    }

    /** 
     * This test focuses on using proxy as a client.
     */
    
    public void testCustomProperties_proxyClient() {
        String endpointUrl = hostAndPort + "/" + CONTEXT_ROOT + "/" + SERVICE_NAME;
        System.out.println("-- endpointUrl = " + endpointUrl);

        CustomPropsClient client = new CustomPropsClient();
        String output = client.callProvider(endpointUrl);
        System.out.println("-- output = " + output);
        assertTrue("Expected exception to contain \"" + URL_IN_BINDING
                + "\", but instead it contained: " + output + ".",
                output.indexOf(URL_IN_BINDING) != -1);       
    }

    /** 
     * This test uses client application and launchClient tool.
     * The binding directory META-INF must exist at location of EAR file for launchClient use
     */
    /*comment this case out since there is no "/bin/launchClient" command in Liberty
    public void test_callLaunchClient() throws Exception {

        String endpointUrl = hostAndPort + "/" + CONTEXT_ROOT + "/" + SERVICE_NAME;
        System.out.println("-- endpointUrl = " + endpointUrl);
        
        String host = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
        String port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();                 
        
        String clientInstallRoot = TopologyDefaults.getDefaultAppServer()
                .getNode().getWASInstall().getInstallRoot();
        System.out.println("clientInstallRoot = " + clientInstallRoot);
        
        String workDir = AppConst.FVT_HOME + "/build/work";
        
        
         * tricky part for binding with client app:  
         * client EAR file and META-INF directory must be at 
         * the same directory, and the classpath must point to location of META-INF directory.
         
        String cmd = clientInstallRoot
                + "/bin/launchClient"
                + Machine.getLocalMachine().getOperatingSystem()
                        .getDefaultScriptSuffix();

        List<String> parms = new ArrayList<String>();
        parms.add((AppConst.FVT_HOME + "/build/installableApps/CustomPropsManagedClient.ear").replace('\\', '/'));
        parms.add("-CCBootstrapHost=" + host);
        parms.add("-CCBootstrapPort=" + port);
        parms.add(endpointUrl);
        parms.add("-CCclasspath=" + AppConst.FVT_HOME + "/build/installableApps");  // to use META-INF from this locatin
        
        System.out.println("cmd = " + cmd + ", parms = " + parms.toString());

        ProgramOutput output = Machine.getLocalMachine().execute(cmd,
                parms.toArray(new String[parms.size()]), workDir,
                null);

        System.out.println("------------------- standard output ------------------\n" + output.getStdout());
        System.out.println("------------------- standard error ------------------\n" + output.getStderr());
        
        assertTrue("Expected exception to contain \"" + URL_IN_BINDING
                + "\", but instead it contained: " + output.getStderr() + ".",
                output.getStderr().indexOf(URL_IN_BINDING) != -1);    

    }*/
}
