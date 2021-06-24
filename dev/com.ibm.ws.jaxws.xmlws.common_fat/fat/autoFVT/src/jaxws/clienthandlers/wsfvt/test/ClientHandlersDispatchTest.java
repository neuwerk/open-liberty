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
// 04/20/07  mzheng       LIDB3296-40.01  New File
// 05/25/07  jramos       440922          Integrate ACUTE
// 07/09/07  mzheng       449269          Enable test cases
// 03/07/08  mzheng       502861          Add test case comments
//

package jaxws.clienthandlers.wsfvt.test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.clienthandlers.wsfvt.client.ClientLogicalHandler;
import jaxws.clienthandlers.wsfvt.client.ClientLogicalHandlerA;
import jaxws.clienthandlers.wsfvt.client.ClientLogicalHandlerB;
import jaxws.clienthandlers.wsfvt.client.ClientSOAP11Handler;
import jaxws.clienthandlers.wsfvt.client.ClientSOAP12Handler;
import jaxws.clienthandlers.wsfvt.client.ClientSOAPHandler;
import jaxws.clienthandlers.wsfvt.client.ClientSOAPHandler1;
import jaxws.clienthandlers.wsfvt.client.ClientSOAPHandler2;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.w3c.dom.Node;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;


public class ClientHandlersDispatchTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static IAppServer server = QueryDefaultNode.defaultAppServer;

    private static String soap11EpAddress =
        "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
        "/jaxws-clienthandlers/EchoMessageSOAP11Service";

    private static String soap12EpAddress =
        "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) +
        "/jaxws-clienthandlers/EchoMessageSOAP12Service";

    private static final String testNS = "http://clienthandlers.jaxws";

    private static QName soap11EpQName = 
        new QName(testNS, "EchoMessageSOAP11Service");

    private static QName soap11PortQName = 
        new QName(testNS, "EchoMessageSOAP11Port");

    private static QName soap12EpQName = 
        new QName(testNS, "EchoMessageSOAP12Service");

    private static QName soap12PortQName = 
        new QName(testNS, "EchoMessageSOAP12Port");

    private static String testString = "Client_Hello:";

    private static String resultStringSOAP11 = "Client_Hello:Server_SOAP11:";

    private static String resultStringSOAP12 = "Client_Hello:Server_SOAP12:";

    private static String resultString1 = "Client_Hello:" + 
                                  "ClientLogicalHandlerA_Outbound:" +
                                  "ClientLogicalHandlerB_Outbound:" +
                                  "ClientSOAPHandler1_Outbound:" +
                                  "ClientSOAPHandler2_Outbound:" +
                                  "Server_SOAP11:" + 
                                  "ClientSOAPHandler2_Inbound:" +
                                  "ClientSOAPHandler1_Inbound:" +
                                  "ClientLogicalHandlerB_Inbound:" +
                                  "ClientLogicalHandlerA_Inbound:";

    private static String resultString2 = "Client_Hello:" + 
                                  "ClientLogicalHandler_Outbound:" +
                                  "ClientSOAPHandler_Outbound:" +
                                  "ClientSOAPHandler1_Outbound:" +
                                  "Server_SOAP11:" + 
                                  "ClientSOAPHandler1_Inbound:" +
                                  "ClientSOAPHandler_Inbound:" +
                                  "ClientLogicalHandler_Inbound:";

    private static String resultString3 = "Client_Hello:" + 
                                  "ClientLogicalHandler_Outbound:" +
                                  "ClientSOAPHandler_Outbound:" +
                                  "ClientSOAPHandler2_Outbound:" +
                                  "Server_SOAP12:" + 
                                  "ClientSOAPHandler2_Inbound:" +
                                  "ClientSOAPHandler_Inbound:" +
                                  "ClientLogicalHandler_Inbound:";

    private static String resultString4 = "Client_Hello:" + 
                                  "ClientLogicalHandlerA_Outbound:" +
                                  "ClientSOAP11Handler_Outbound:" +
                                  "Server_SOAP11:" +
                                  "ClientSOAP11Handler_Inbound:" +
                                  "ClientLogicalHandlerA_Inbound:"; 

    private static String resultString5 = "Client_Hello:" + 
                                  "ClientLogicalHandlerB_Outbound:" +
                                  "ClientSOAP12Handler_Outbound:" +
                                  "Server_SOAP12:" + 
                                  "ClientSOAP12Handler_Inbound:" +
                                  "ClientLogicalHandlerB_Inbound:"; 

    private static String resultString6 = "Client_Hello:" + 
                                  "ClientLogicalHandlerA_Outbound:" +
                                  "ClientLogicalHandlerB_Outbound:" +
                                  "ClientSOAP11Handler_Outbound:" +
                                  "ClientSOAPHandler2_Outbound:" +
                                  "Server_SOAP11:" + 
                                  "ClientSOAPHandler2_Inbound:" +
                                  "ClientSOAP11Handler_Inbound:" +
                                  "ClientLogicalHandlerB_Inbound:" +
                                  "ClientLogicalHandlerA_Inbound:"; 


    /*
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public ClientHandlersDispatchTest(String name) {
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
        return new TestSuite(ClientHandlersDispatchTest.class);
    }


    /**
     * Tests Dispatch to an SEI based Web service works correctly.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that dispatches to an SEI based
     *               SOAP 1.1 Web service.  
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimpleDispatchNoHandler() {
        System.err.println("====== In testSimpleDispatchNoHandler() ======");

        try {
            Service myService = createSOAP11Service();
            Dispatch<Source> myDispatch = myService.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            Source req = createRequestSource();

            Source ret = myDispatch.invoke(req);

            String actualString = getRespString(ret);
            assertEquals("Unexpected result for myDispatch", resultStringSOAP11, actualString);

            System.err.println("====== SUCCESS: testSimpleDispatchNoHandler() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSimpleDispatchNoHandler() ======\n");
            fail("Exception caught in testSimpleDispatchNoHandler(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Test that changing handler chain configuration for a binding 
     * instance does not cause any change to other binding instances.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programatically configures handler chains for 
     *               dispatch clients.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatchHandlersConfig() {
        System.err.println("====== In testDispatchHandlersConfig() ======");

        String actualString = "";

        try {
            // create binding providers 
            Service myService = createSOAP11Service();

            Dispatch<Source> myDispatch1 = myService.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            Dispatch<Source> myDispatch2 = myService.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            // set binding handler chain
            Binding binding = ((BindingProvider)myDispatch1).getBinding();

            // can create new list or use existing one
            List<Handler> handlerList = binding.getHandlerChain();

            if (handlerList == null) {
                handlerList = new ArrayList<Handler>();
            } 

            handlerList.add(new ClientSOAPHandler1());
            handlerList.add(new ClientSOAPHandler2());
            handlerList.add(new ClientLogicalHandlerA());
            handlerList.add(new ClientLogicalHandlerB());

            binding.setHandlerChain(handlerList);

            int hCode = handlerList.hashCode();
            assertNotNull("Null binding HandlerChain", binding.getHandlerChain());
            assertEquals("Incorrect binding HandlerChain", 
                         hCode, binding.getHandlerChain().hashCode());

            // call the service
            Source req = createRequestSource();
            actualString = getRespString(myDispatch2.invoke(req));
            assertEquals("Unexpected result for myDispatch2", resultStringSOAP11, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch1.invoke(req));
            assertEquals("Unexpected result for myDispatch1", resultString1, actualString);

            // another binding instance
            Dispatch<Source> myDispatch3 = myService.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);
            req = createRequestSource();
            actualString = getRespString(myDispatch3.invoke(req));
            assertEquals("Unexpected result for myDispatch3", resultStringSOAP11, actualString);

            System.err.println("====== SUCCESS: testDispatchHandlersConfig() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testDispatchHandlersConfig() ======\n");
            fail("Exception caught in testDispatchHandlersConfig(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Changing handler resolver configured for a service instance does not
     * affect the handlers on previously created Dispatches.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programatically configures handler chains for
     *               service instances.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBindingProviderHandlersConfig() {
        System.err.println("====== In testBindingProviderHandlersConfig() ======");

        String actualString = "";

        try {
            // create binding providers 
            Service myService = createSOAP11Service();

            Dispatch<Source> myDispatch1 = myService.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            myService.setHandlerResolver(new ServiceHandlerResolver(myService.getHandlerResolver()));

            Dispatch<Source> myDispatch2 = myService.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            assertNotNull("Null myService HandlerResolver", 
                          myService.getHandlerResolver());

            // call the service
            Source req = createRequestSource();
            actualString = getRespString(myDispatch1.invoke(req));
            assertEquals("Unexpected result for myDispatch1", resultStringSOAP11, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch2.invoke(req));
            assertEquals("Unexpected result for myDispatch2", resultString2, actualString);

            System.err.println("====== SUCCESS: testBindingProviderHandlersConfig() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testBindingProviderHandlersConfig() ======\n");
            fail("Exception caught in testPerServiceHandlersConfig(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Changing handler resolver configured for a service instance does not
     * affect other service instances.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programatically configures handler chains for
     *               service instances.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPerServiceHandlersConfig() {
        System.err.println("====== In testPerServiceHandlersConfig() ======");

        String actualString = "";

        try {
            // create binding providers 
            Service myService1 = createSOAP11Service();
            Service myService2 = createSOAP11Service();

            myService2.setHandlerResolver(new ServiceHandlerResolver(myService2.getHandlerResolver()));

            Dispatch<Source> myDispatch1 = myService1.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            Dispatch<Source> myDispatch2 = myService2.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            Dispatch<Source> myDispatch3 = createSOAP11Service().createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            assertNotNull("Null myService2 HandlerResolver", 
                          myService2.getHandlerResolver());

            // call the service
            Source req = createRequestSource();
            actualString = getRespString(myDispatch1.invoke(req));
            assertEquals("Unexpected result for myDispatch1", resultStringSOAP11, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch2.invoke(req));
            assertEquals("Unexpected result for myDispatch2", resultString2, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch3.invoke(req));
            assertEquals("Unexpected result for myDispatch", resultStringSOAP11, actualString);

            System.err.println("====== SUCCESS: testPerServiceHandlersConfig() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPerServiceHandlersConfig() ======\n");
            fail("Exception caught in testPerServiceHandlersConfig(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Test that handler chain can be configured at per port level
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programatically configures handler chains for
     *               different ports.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPerPortHandlersConfig() {
        System.err.println("====== In testPerPortHandlersConfig() ======");

        String actualString = "";

        try {
            // create binding providers 
            Service myService1 = createSOAP11Service();
            Service myService2 = createSOAP12Service();

            Dispatch<Source> myDispatch1 = myService1.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            Dispatch<Source> myDispatch2 = myService2.createDispatch(soap12PortQName, Source.class, Service.Mode.PAYLOAD);

            myService1.setHandlerResolver(new PortHandlerResolver(myService1.getHandlerResolver()));

            myService2.setHandlerResolver(new PortHandlerResolver(myService2.getHandlerResolver()));

            Dispatch<Source> myDispatch3 = myService1.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            Dispatch<Source> myDispatch4 = myService2.createDispatch(soap12PortQName, Source.class, Service.Mode.PAYLOAD);

            assertNotNull("Null myService1 HandlerResolver", 
                          myService1.getHandlerResolver());

            assertNotNull("Null myService2 HandlerResolver", 
                          myService2.getHandlerResolver());

            Source req = createRequestSource();
            actualString = getRespString(myDispatch1.invoke(req));
            assertEquals("Unexpected result for myDispatch1", resultStringSOAP11, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch2.invoke(req));
            assertEquals("Unexpected result for myDispatch2", resultStringSOAP12, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch3.invoke(req));
            assertEquals("Unexpected result for myDispatch3", resultString4, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch4.invoke(req));
            assertEquals("Unexpected result for myDispatch4", resultString5, actualString);

            System.err.println("====== SUCCESS: testPerPortHandlersConfig() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPerPortHandlersConfig() ======\n");
            fail("Exception caught in testPerPortHandlersConfig(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Test handler chain configuration combinations
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programatically configures handler chains for
     *               different binding and service instances, and ports.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testHandlerChainSnapshot() {
        System.err.println("====== In testHandlerChainSnapshot() ======");

        String actualString = "";

        try {
            // create binding providers 
            Service myService1 = createSOAP11Service();
            Service myService2 = createSOAP12Service();

            Dispatch<Source> myDispatch1 = myService1.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            Dispatch<Source> myDispatch2 = myService2.createDispatch(soap12PortQName, Source.class, Service.Mode.PAYLOAD);

            myService1.setHandlerResolver(new PortHandlerResolver(myService1.getHandlerResolver()));

            myService2.setHandlerResolver(new ServiceHandlerResolver(myService2.getHandlerResolver()));

            Dispatch<Source> myDispatch3 = myService1.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            Binding binding = ((BindingProvider)myDispatch3).getBinding();

            List<Handler> handlerList = binding.getHandlerChain();

            if (handlerList == null) {
                fail("No HandlerChain configured for this binding");
            }

            handlerList.add(new ClientSOAPHandler2());
            handlerList.add(new ClientLogicalHandlerB());
             
            Dispatch<Source> myDispatch4 = myService2.createDispatch(soap12PortQName, Source.class, Service.Mode.PAYLOAD);

            myService1.setHandlerResolver(new ServiceHandlerResolver(null));
            Dispatch<Source> myDispatch5 = myService1.createDispatch(soap11PortQName, Source.class, Service.Mode.PAYLOAD);

            myService2.setHandlerResolver(new PortHandlerResolver(null));
            Dispatch<Source> myDispatch6 = myService2.createDispatch(soap12PortQName, Source.class, Service.Mode.PAYLOAD);

            assertNotNull("Null binding HandlerChain", binding.getHandlerChain());

            assertNotNull("Null myService1 HandlerResolver", 
                          myService1.getHandlerResolver());

            assertNotNull("Null myService2 HandlerResolver", 
                          myService2.getHandlerResolver());

            Source req = createRequestSource();
            actualString = getRespString(myDispatch1.invoke(req));
            assertEquals("Unexpected result for myDispatch", resultStringSOAP11, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch2.invoke(req));
            assertEquals("Unexpected result for myDispatch", resultStringSOAP12, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch3.invoke(req));
            assertEquals("Unexpected result for myDispatch", resultString6, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch4.invoke(req));
            assertEquals("Unexpected result for myDispatch", resultString3, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch5.invoke(req));
            assertEquals("Unexpected result for myDispatch", resultString2, actualString);

            req = createRequestSource();
            actualString = getRespString(myDispatch6.invoke(req));
            assertEquals("Unexpected result for myDispatch", resultString5, actualString);

            System.err.println("====== SUCCESS: testHandlerChainSnapshot() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testHandlerChainSnapshot() ======\n");
            fail("Exception caught in testHandlerChainSnapshot(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    private Service createSOAP11Service() {
        try {
            Service thisService = Service.create(soap11EpQName);
            thisService.addPort(soap11PortQName, null, soap11EpAddress);

            return thisService;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught Exception in createSOAP11Service(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }


    private Service createSOAP12Service() {
        try {
            Service thisService = Service.create(soap12EpQName);
            thisService.addPort(soap12PortQName, SOAPBinding.SOAP12HTTP_BINDING, soap12EpAddress);

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


    // inner class that implements the service HandlerResolver
    class ServiceHandlerResolver implements HandlerResolver {
        private HandlerResolver _currentHandlerResolver = null;

        public ServiceHandlerResolver(HandlerResolver h) {
            _currentHandlerResolver = h;
        }

        public List<Handler> getHandlerChain(PortInfo info) {
            List<Handler> handlerList = null;

            if (_currentHandlerResolver != null) {
                handlerList = _currentHandlerResolver.getHandlerChain(info);
            }

            if (handlerList == null) {
                handlerList = new ArrayList<Handler>();
            }

            handlerList.add(new ClientSOAPHandler());
            handlerList.add(new ClientLogicalHandler());

            // add handlers to list based on PortInfo.getServiceName()
            if (info.getServiceName().getLocalPart() == "EchoMessageSOAP11Service") {
                handlerList.add(new ClientSOAPHandler1());
            } else if (info.getServiceName().getLocalPart() == "EchoMessageSOAP12Service") {
                handlerList.add(new ClientSOAPHandler2());
            }

            return handlerList;
        }
    }


    // inner class that implements the per port HandlerResolver
    class PortHandlerResolver implements HandlerResolver {
        private HandlerResolver _currentHandlerResolver = null;

        public PortHandlerResolver(HandlerResolver h) {
            _currentHandlerResolver = h;
        }

        public List<Handler> getHandlerChain(PortInfo info) {
            List<Handler> handlerList = null;

            if (_currentHandlerResolver != null) {
                handlerList = _currentHandlerResolver.getHandlerChain(info);
            }

            if (handlerList == null) {
                handlerList = new ArrayList<Handler>();
            }

            // add handlers to list based on PortInfo.getPortName()
            if (info.getPortName().getLocalPart() == "EchoMessageSOAP11Port") {
                handlerList.add(new ClientSOAP11Handler());
                handlerList.add(new ClientLogicalHandlerA());
            } else if (info.getPortName().getLocalPart() == "EchoMessageSOAP12Port") {
                handlerList.add(new ClientLogicalHandlerB());
                handlerList.add(new ClientSOAP12Handler());
            }

            return handlerList;
        }
    }
}

