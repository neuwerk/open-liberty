//
// @(#) 1.1 autoFVT/src/jaxws/defaultpackage/wsfvt/test/DefaultPackageTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 6/29/10 16:14:28 [8/8/12 06:58:50]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 06/28/2010 jtnguyen    658405          New File

package jaxws.defaultpackage.wsfvt.test;

import javax.xml.ws.BindingProvider;

import junit.framework.Test;
import junit.framework.TestSuite;

import jaxws.defaultpackage.wsfvt.server.*;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * 
 * @author jtnguyen
 *  This test component ensures the Jaxws22 3.6 Conformance "Overriding JAXB types empty namespace: JAX-WS tools and runtimes MUST override
 *  the default empty namespace for JAXB types and elements to SEI's targetNamespace."
 *
 *  What in the package:
 *  - Service is of type document/literal BARE
 *  - WSDL doesn't have elementFormdefault="qualified" to ensure that it will generate empty targetNamespace for operation in JAXB artifacts
 *  - SEI has targetNamespace not null
 *  - WSDL is packaged in the application.  No generated artifacts are in the EAR.
 *
 * 
 */
public class DefaultPackageTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase{
 
    private String url = null;	
    private Node node = null;
    private static String host = null;
    private static Integer port = null;

	
    /**
     * Constructor to create a test case with a given name
     * 
     * @param arg0 -
     *                The name of the test case
     */
    public DefaultPackageTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite( DefaultPackageTest.class );
    }

    /**
     * This method will run before each testXXX method. Initialize any variables
     * that need to be setup before each test.
     */
    public void setUp() {
    }

    /**
     * The main method
     * 
     * @param args
     */
    public static void main( String[] args ) {
        junit.textui.TestRunner.run( suite() );
    }

    /*
     * This test is to make sure all is well - it just sends and receives the same msg
     */
    public void testSimpleMsg() throws Exception {
        
        String str = "Echo string test.";
        String actual = invokeService(str);
        assertTrue("Expected output to contain \"" + str
        + "\", but instead it contained: " + actual + ".",
        actual.indexOf(str) != -1);
 	}

    /*
     *  Post a message and verify the returning message on the wire 
     *  The post message was taken from a w
     */
    public void testMsgOnWire() throws Exception {
		// won't run this case on ZOS
		if(isZOS()) {
			System.out.println("Ignored case testMsgOnWire if running on z/OS for CharSet/encoding issue");
			return;
		}
        
        String xmlMsg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
			   + "<soapenv:Body>\n"
			   + "<a:echo xmlns:a=\"http://server.wsfvt.defaultpackage.jaxws/\">test1</a:echo>\n"
			   + "</soapenv:Body>\n"
			   + "</soapenv:Envelope>";
        String tn = "http://schemas.xmlsoap.org/soap/envelope";
        
        /* expected PostMsgSender.response:
        <?xml version="1.0" encoding="UTF-8"?>
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
           <soapenv:Body>
              <echoResponse xmlns="http://server.wsfvt.defaultpackage.jaxws/">test1</echoResponse>
           </soapenv:Body>
        </soapenv:Envelope>
        */
        
        node = TopologyDefaults.getDefaultAppServer().getNode();    	
        host = node.getHostname();
        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);                         
        String url = "http://" + host + ":" + port + "/DefaultPackage/EchoStringService";
         
        String soapaction = "";
        int timeoutSec = 5;
        boolean ignoreContent = false;
        String actual = PostMsgSender.postToURL(url, xmlMsg, soapaction, timeoutSec, ignoreContent);
        
        System.out.println("Received: " + actual);
        
        assertTrue("Expected msg to contain targetNamespace \"" + tn
                + "\", but instead the message is: " + actual + ".",
                actual.indexOf(tn) != -1);
 	}
  
    private String invokeService(String input) throws Exception{
 
    	String response = null;
    	EchoString echoStringPort = null;
        node = TopologyDefaults.getDefaultAppServer().getNode();    	
        host = node.getHostname();
        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);                         
        url = "http://" + host + ":" + port + "/DefaultPackage/EchoStringService";
        
        System.out.println("--- url: " + url);       
		

        EchoStringService service = new EchoStringService();
    	echoStringPort = service.getEchoStringPort();
    	BindingProvider provider = (BindingProvider)echoStringPort;
    	provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
                
        response = echoStringPort.echo(input);
        
        System.out.println("---- received: " + response);
        
	return response;
    }
}
