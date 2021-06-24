//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date      UserId       Defect          Description
// ----------------------------------------------------------------------------
// 04/10/07  mzheng       435342          New File
// 07/09/07  mzheng       449269          Enable test cases
// 03/07/08  mzheng       502861          Add test case comments
//

package jaxws.handlerdeploy.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import javax.xml.ws.Service;
import javax.xml.ws.BindingProvider;

import jaxws.handlerdeploy.wsfvt.client.*;

public class HandlerDeployProxyTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    private static String testString = "Client_Hello:";

    private static String resultStringSOAP11 = "Client_Hello:SEI_SOAP11:";

    private static String resultStringSOAP12 = "Client_Hello:SEI_SOAP12:";

    /*
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public HandlerDeployProxyTest(String name) {
        super(name);
    }


    /*
     * The main method.
     *
     * @param args The command line arguments
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
        return new TestSuite(HandlerDeployProxyTest.class);
    }


    /**
     * Tests SEI based SOAP 1.1 Web service with @HandlerChain using proxy.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that accesses an SEI based SOAP
     *               1.1 Web service using proxy.  The Web service has a 
     *               handler chain configured using @HandlerChain annotation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP11EndpointHandlerChain() {
        System.err.println("====== In testSOAP11EndpointHandlerChain() ======");

        String resultString = "Client_Hello:" +
                              "ServerSOAPHandler_Inbound:" +
                              "ServerLogicalHandler_Inbound:" +
                              "SEI_SOAP11:" +
                              "ServerLogicalHandler_Outbound:" +
                              "ServerSOAPHandler_Outbound:";

        try {
            EchoMessagePortType myPort = (new EchoMessageSOAP11Service()).getEchoMessageSOAP11Port();

            String actualString = myPort.echoMessage(testString);
            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP11EndpointHandlerChain() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP11EndpointHandlerChain() ======\n");
            fail("Exception caught in testSOAP11EndpointHandlerChain(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests SEI based SOAP 1.1 Web service with handler chain using proxy.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that accesses an SEI based SOAP
     *               1.1 Web service using proxy.  The Web service has a
     *               handler chain configured through WSDL customization.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP11Endpoint() {
        System.err.println("====== In testSOAP11Endpoint() ======");

        String resultString = "Client_Hello:" +
                              "ServerSOAPHandler_Inbound:" +
                              "ServerLogicalHandler_Inbound:" +
                              "Simple_SEI_SOAP11:" +
                              "ServerLogicalHandler_Outbound:" +
                              "ServerSOAPHandler_Outbound:";

        try {
            EchoMessagePortType myPort = (new EchoMessageSOAP11SimpleService()).getEchoMessageSOAP11SimplePort();

            String actualString = myPort.echoMessage(testString);
            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP11Endpoint() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP11Endpoint() ======\n");
            fail("Exception caught in testSOAP11Endpoint(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests SEI based SOAP 1.2 Web service with @HandlerChain using proxy.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that accesses an SEI based SOAP
     *               1.2 Web service using proxy.  The Web service has a
     *               handler chain configured using @HandlerChain annotation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12EndpointHandlerChain() {
        System.err.println("====== In testSOAP12EndpointHandlerChain() ======");

        String resultString = "Client_Hello:" +
                              "ServerSOAPHandler_Inbound:" +
                              "ServerLogicalHandler_Inbound:" +
                              "SEI_SOAP12:" +
                              "ServerLogicalHandler_Outbound:" +
                              "ServerSOAPHandler_Outbound:";

        try {
            EchoMessagePortType myPort = (new EchoMessageSOAP12Service()).getEchoMessageSOAP12Port();

            String actualString = myPort.echoMessage(testString);
            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP12EndpointHandlerChain() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP12EndpointHandlerChain() ======\n");
            fail("Exception caught in testSOAP12EndpointHandlerChain(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests SEI based SOAP 1.2 Web service with handler chain using proxy.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that accesses an SEI based SOAP
     *               1.2 Web service using proxy.  The Web service has a 
     *               handler chain configured through WSDL customization.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12Endpoint() {
        System.err.println("====== In testSOAP12Endpoint() ======");

        String resultString = "Client_Hello:" +
                              "ServerSOAPHandler_Inbound:" +
                              "ServerLogicalHandler_Inbound:" +
                              "Simple_SEI_SOAP12:" +
                              "ServerLogicalHandler_Outbound:" +
                              "ServerSOAPHandler_Outbound:";

        try {
            EchoMessagePortType myPort = (new EchoMessageSOAP12SimpleService()).getEchoMessageSOAP12SimplePort();

            String actualString = myPort.echoMessage(testString);
            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP12Endpoint() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP12Endpoint() ======\n");
            fail("Exception caught in testSOAP12Endpoint(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests Provider based SOAP 1.1 Web service with @HandlerChain using 
     * proxy.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that accesses a Provider based 
     *               SOAP 1.1 Web service using proxy.  The Web service has a
     *               handler chain configured using @HandlerChain annotation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP11ProviderHandlerChain() {
        System.err.println("====== In testSOAP11ProviderHandlerChain() ======");

        String resultString = "Client_Hello:" +
                              "ServerSOAPHandler_Inbound:" +
                              "ServerLogicalHandler_Inbound:" +
                              "Provider_SOAP11:" +
                              "ServerLogicalHandler_Outbound:" +
                              "ServerSOAPHandler_Outbound:";

        try {
            EchoMessagePortType myPort = (new EchoMessageSOAP11Provider()).getEchoMessageSOAP11ProviderPort();

            String actualString = myPort.echoMessage(testString);
            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP11ProviderHandlerChain() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP11ProviderHandlerChain() ======\n");
            fail("Exception caught in testSOAP11ProviderHandlerChain(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests Provider based SOAP 1.1 Web service with @HandlerChain using
     * proxy.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that accesses a Provider based
     *               SOAP 1.1 Web service using proxy.  The Web service has a
     *               handler chain configured using @HandlerChain annotation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12ProviderHandlerChain() {
        System.err.println("====== In testSOAP12ProviderHandlerChain() ======");

        String resultString = "Client_Hello:" +
                              "ServerSOAPHandler_Inbound:" +
                              "ServerLogicalHandler_Inbound:" +
                              "Provider_SOAP12:" +
                              "ServerLogicalHandler_Outbound:" +
                              "ServerSOAPHandler_Outbound:";

        try {
            EchoMessagePortType myPort = (new EchoMessageSOAP12Provider()).getEchoMessageSOAP12ProviderPort();

            String actualString = myPort.echoMessage(testString);
            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP12ProviderHandlerChain() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP12ProviderHandlerChain() ======\n");
            fail("Exception caught in testSOAP12ProviderHandlerChain(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
}

