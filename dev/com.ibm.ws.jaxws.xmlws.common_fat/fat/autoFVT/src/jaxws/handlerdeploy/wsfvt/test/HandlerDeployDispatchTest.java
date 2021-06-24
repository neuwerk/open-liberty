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
// 05/25/07  jramos       440922          Integrate ACUTE
// 07/09/07  mzheng       449269          Enable test cases
// 03/07/08  mzheng       502861          Add test case comments
//

package jaxws.handlerdeploy.wsfvt.test;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.w3c.dom.Node;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

public class HandlerDeployDispatchTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static IAppServer server = QueryDefaultNode.defaultAppServer;

    private static String soap11EpAddress1 =
        "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
        "/jaxws-handlerdeploy/EchoMessageSOAP11Service";

    private static String soap12EpAddress1 =
        "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
        "/jaxws-handlerdeploy/EchoMessageSOAP12Service";

    private static String soap11EpAddress2 =
        "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
        "/jaxws-handlerdeploy/EchoMessageSOAP11SimpleService";

    private static String soap12EpAddress2 =
        "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
        "/jaxws-handlerdeploy/EchoMessageSOAP12SimpleService";

    private static String soap11ProviderAddress =
        "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
        "/jaxws-handlerdeploy/EchoMessageSOAP11Provider";

    private static String soap12ProviderAddress =
        "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
        "/jaxws-handlerdeploy/EchoMessageSOAP12Provider";

    private static final String testNS = "http://handlerdeploy.jaxws";

    private static QName soap11EpQName1 = 
        new QName(testNS, "EchoMessageSOAP11Service");

    private static QName soap11PortQName1 = 
        new QName(testNS, "EchoMessageSOAP11Port");

    private static QName soap12EpQName1 = 
        new QName(testNS, "EchoMessageSOAP12Service");

    private static QName soap12PortQName1 = 
        new QName(testNS, "EchoMessageSOAP12Port");

    private static QName soap11EpQName2 = 
        new QName(testNS, "EchoMessageSOAP11SimpleService");

    private static QName soap11PortQName2 = 
        new QName(testNS, "EchoMessageSOAP11SimplePort");

    private static QName soap12EpQName2 = 
        new QName(testNS, "EchoMessageSOAP12SimpleService");

    private static QName soap12PortQName2 = 
        new QName(testNS, "EchoMessageSOAP12SimplePort");

    private static QName soap11ProviderQName = 
        new QName(testNS, "EchoMessageSOAP11Provider");

    private static QName soap11ProviderPortQName = 
        new QName(testNS, "EchoMessageSOAP11ProviderPort");

    private static QName soap12ProviderQName = 
        new QName(testNS, "EchoMessageSOAP12Provider");

    private static QName soap12ProviderPortQName = 
        new QName(testNS, "EchoMessageSOAP12ProviderPort");

    private static String testString = "Client_Hello:";

    /*
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public HandlerDeployDispatchTest(String name) {
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
        return new TestSuite(HandlerDeployDispatchTest.class);
    }


    /** 
     * Tests SEI based SOAP 1.1 Web service with @HandlerChain using Dispatch.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that dispatches to an SEI based
     *               SOAP 1.1 Web service.  The Web service has a handler 
     *               chain configured using @HandlerChain annotation.
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
            Service myService = createSOAP11Service();
            Dispatch<Source> myDispatch = myService.createDispatch(soap11PortQName1, Source.class, Service.Mode.PAYLOAD);

            Source req = createRequestSource();
            Source ret = myDispatch.invoke(req);

            String actualString = getRespString(ret);

            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP11EndpointHandlerChain() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP11EndpointHandlerChain() ======\n");
            fail("Exception caught in testSOAP11EndpointHandlerChain(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests SEI based SOAP 1.1 Web service with handler chain using Dispatch.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that dispatches to an SEI based
     *               SOAP 1.1 Web service.  The Web service has a handler 
     *               chain configured through WSDL customization.
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
            Service myService = createSOAP11SimpleService();
            Dispatch<Source> myDispatch = myService.createDispatch(soap11PortQName2, Source.class, Service.Mode.PAYLOAD);

            Source req = createRequestSource();
            Source ret = myDispatch.invoke(req);

            String actualString = getRespString(ret);

            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP11Endpoint() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP11Endpoint() ======\n");
            fail("Exception caught in testSOAP11Endpoint(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests SEI based SOAP 1.2 Web service with @HandlerChain using Dispatch.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that dispatches to an SEI based
     *               SOAP 1.2 Web service.  The Web service has a handler
     *               chain configured using @HandlerChain annotation.
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
            Service myService = createSOAP12Service();
            Dispatch<Source> myDispatch = myService.createDispatch(soap12PortQName1, Source.class, Service.Mode.PAYLOAD);

            Source req = createRequestSource();
            Source ret = myDispatch.invoke(req);

            String actualString = getRespString(ret);

            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP12EndpointHandlerChain() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP12EndpointHandlerChain() ======\n");
            fail("Exception caught in testSOAP12EndpointHandlerChain(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests SEI based SOAP 1.2 Web service with handler chain using Dispatch.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that dispatches to an SEI based
     *               SOAP 1.2 Web service.  The Web service has a handler
     *               chain configured through WSDL customization.
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
            Service myService = createSOAP12SimpleService();
            Dispatch<Source> myDispatch = myService.createDispatch(soap12PortQName2, Source.class, Service.Mode.PAYLOAD);

            Source req = createRequestSource();
            Source ret = myDispatch.invoke(req);

            String actualString = getRespString(ret);

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
     * Dispatch.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that dispatches to an Provider 
     *               based SOAP 1.1 Web service.  The Web service has a 
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
            Service myService = createSOAP11ProviderService();
            Dispatch<Source> myDispatch = myService.createDispatch(soap11ProviderPortQName, Source.class, Service.Mode.PAYLOAD);

            Source req = createRequestSource();
            Source ret = myDispatch.invoke(req);

            String actualString = getRespString(ret);

            assertEquals("Unexpected result", resultString, actualString);

            System.err.println("====== SUCCESS: testSOAP11ProviderHandlerChain() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP11ProviderHandlerChain() ======\n");
            fail("Exception caught in testSOAP11ProviderHandlerChain(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Tests Provider based SOAP 1.2 Web service with @HandlerChain using
     * Dispatch.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that dispatches to an Provider
     *               based SOAP 1.2 Web service.  The Web service has a
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
            Service myService = createSOAP12ProviderService();
            Dispatch<Source> myDispatch = myService.createDispatch(soap12ProviderPortQName, Source.class, Service.Mode.PAYLOAD);

            Source req = createRequestSource();
            Source ret = myDispatch.invoke(req);

            String actualString = getRespString(ret);

            assertEquals("Unexpected result", resultString, actualString);
            System.err.println("====== SUCCESS: testSOAP12ProviderHandlerChain() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAP12ProviderHandlerChain() ======\n");
            fail("Exception caught in testSOAP12ProviderHandlerChain(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    private Service createSOAP11Service() {
        try {
            Service thisService = Service.create(soap11EpQName1);
            thisService.addPort(soap11PortQName1, null, soap11EpAddress1);

            return thisService;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception in createSOAP11Service(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }


    private Service createSOAP12Service() {
        try {
            Service thisService = Service.create(soap12EpQName1);
            thisService.addPort(soap12PortQName1, SOAPBinding.SOAP12HTTP_BINDING, soap12EpAddress1);

            return thisService;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception in createSOAP12Service(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }


    private Service createSOAP11SimpleService() {
        try {
            Service thisService = Service.create(soap11EpQName2);
            thisService.addPort(soap11PortQName2, null, soap11EpAddress2);

            return thisService;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception in createSOAP11SimpleService(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }


    private Service createSOAP12SimpleService() {
        try {
            Service thisService = Service.create(soap12EpQName2);
            thisService.addPort(soap12PortQName2, SOAPBinding.SOAP12HTTP_BINDING, soap12EpAddress2);

            return thisService;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception in createSOAP12SimpleService(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }


    private Service createSOAP11ProviderService() {
        try {
            Service thisService = Service.create(soap11ProviderQName);
            thisService.addPort(soap11ProviderPortQName, null, soap11ProviderAddress);

            return thisService;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception in createSOAP11ProviderService(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }


    private Service createSOAP12ProviderService() {
        try {
            Service thisService = Service.create(soap12ProviderQName);
            thisService.addPort(soap12ProviderPortQName, SOAPBinding.SOAP12HTTP_BINDING, soap12ProviderAddress);

            return thisService;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception in createSOAP12Service(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }


    /**
     * Create a Source request to be used by Dispatch<Source>
     */
    private Source createRequestSource() {

        String reqString = null;

        String ns = testNS + "/xsd";
        String prefix = "test-ns";
        String operation = "echoMessage";

/*
        reqString = "<" + prefix + ":" + operation + 
                    " xmlns:" + prefix + "=\"" + ns + "\">" +
                    "<request>" + testString + "</request>" +
                    "</" + prefix + ":" + operation + ">";
*/
        reqString = "<" + operation + 
                    " xmlns=\"" + ns + "\">" +
                    "<request>" + testString + "</request>" +
                    "</" + operation + ">";

        Source request = new StreamSource(new StringReader(reqString));

        return request;
    }


    private String getRespString(Source s) {
        try {
            DOMResult dom = new DOMResult();
            Transformer transformer =
              TransformerFactory.newInstance().newTransformer();

            transformer.transform(s, dom);
            Node node = dom.getNode();
            Node root = node.getFirstChild();
            Node child  = root.getFirstChild();
            String respString = child.getFirstChild().getNodeValue();
            return respString;
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception in getRespString(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }
}

