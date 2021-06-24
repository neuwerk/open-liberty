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
// 07/09/07  mzheng       449269          Enable test cases
// 03/07/08  mzheng       502861          Add test case comments
//

package jaxws.clienthandlers.wsfvt.test;

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

import jaxws.clienthandlers.wsfvt.client.*;


public class ClientHandlersProxyTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
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
    public ClientHandlersProxyTest(String name) {
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
        return new TestSuite(ClientHandlersProxyTest.class);
    }


    /**
     * Tests accessing an SEI based Web service using proxy works correctly.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test has a client that accesses an SEI based
     *               SOAP 1.1 Web service using proxy.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSimpleProxyNoHandler() {
        System.err.println("====== In testSimpleProxyNoHandler() ======");

        try {
            EchoMessagePortType myPort = (new EchoMessageSOAP11Service()).getEchoMessageSOAP11Port();

            String actualString = myPort.echoMessage(testString);
            assertEquals("Unexpected result for myPort", resultStringSOAP11, actualString);

            System.err.println("====== SUCCESS: testSimpleProxyNoHandler() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSimpleProxyNoHandler() ======\n");
            fail("Exception caught in testSimpleProxyNoHandler(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Test that changing handler chain configuration for a binding 
     * instance does not cause any change to other binding instances.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programatically configures handler chains for
     *               proxy clients.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProxyHandlersConfig() {
        System.err.println("====== In testProxyHandlersConfig() ======");

        String actualString = "";

        try {
            // create binding providers 
            EchoMessageSOAP11Service myService = new EchoMessageSOAP11Service();

            EchoMessagePortType myPort1 = myService.getEchoMessageSOAP11Port();

            EchoMessagePortType myPort2 = myService.getEchoMessageSOAP11Port();

            // set binding handler chain
            Binding binding = ((BindingProvider)myPort1).getBinding();

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
            actualString = myPort2.echoMessage(testString);
            assertEquals("Unexpected result for myPort2", resultStringSOAP11, actualString);

            actualString = myPort1.echoMessage(testString);
            assertEquals("Unexpected result for myPort1", resultString1, actualString);

            // another binding instance
            EchoMessagePortType myPort3 = myService.getEchoMessageSOAP11Port();

            actualString = myPort3.echoMessage(testString);
            assertEquals("Unexpected result for myPort3", resultStringSOAP11, actualString);

            System.err.println("====== SUCCESS: testProxyHandlersConfig() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testProxyHandlersConfig() ======\n");
            fail("Exception caught in testProxyHandlersConfig(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Changing handler resolver configured for a service instance does not 
     * affect the handlers on previously created proxies.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programatically configures handler chains for
     *               service instances.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBindingProviderHandlersConfig() {
        System.err.println("====== In testBindingProviderHandlersConfig() =====n");

        String actualString = "";

        try {
            // create binding providers 
            EchoMessageSOAP11Service myService = new EchoMessageSOAP11Service();

            EchoMessagePortType myPort1 = myService.getEchoMessageSOAP11Port();

            myService.setHandlerResolver(new ServiceHandlerResolver(myService.getHandlerResolver()));

            EchoMessagePortType myPort2 = myService.getEchoMessageSOAP11Port();

            assertNotNull("Null myService HandlerResolver", 
                          myService.getHandlerResolver());

            // call the service
            actualString = myPort1.echoMessage(testString);
            assertEquals("Unexpected result for myPort1", resultStringSOAP11, actualString);

            actualString = myPort2.echoMessage(testString);
            assertEquals("Unexpected result for myPort2", resultString2, actualString);

            System.err.println("====== SUCCESS: testBindingProviderHandlersConfig() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testBindingProviderHandlersConfig() ======\n");
            fail("Exception caught in testServiceHandlersConfig(): " + e.getClass().getName() + ": " + e.getMessage());
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
        System.err.println("====== In testServiceHandlersConfig() ======\n");

        String actualString = "";

        try {
            // create binding providers 
            EchoMessageSOAP11Service myService1 = new EchoMessageSOAP11Service();
            EchoMessageSOAP11Service myService2 = new EchoMessageSOAP11Service();

            myService2.setHandlerResolver(new ServiceHandlerResolver(myService2.getHandlerResolver()));

            EchoMessagePortType myPort1 = myService1.getEchoMessageSOAP11Port();

            EchoMessagePortType myPort2 = myService2.getEchoMessageSOAP11Port();

            EchoMessagePortType myPort3 = (new EchoMessageSOAP11Service()).getEchoMessageSOAP11Port();

            assertNotNull("Null myService2 HandlerResolver", 
                          myService2.getHandlerResolver());

            // call the service
            actualString = myPort1.echoMessage(testString);
            assertEquals("Unexpected result", resultStringSOAP11, actualString);

            actualString = myPort2.echoMessage(testString);
            assertEquals("Unexpected result", resultString2, actualString);

            actualString = myPort3.echoMessage(testString);
            assertEquals("Unexpected result", resultStringSOAP11, actualString);

            System.err.println("====== SUCCESS: testServiceHandlersConfig() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testServiceHandlersConfig() ======\n");
            fail("Exception caught in testServiceHandlersConfig(): " + e.getClass().getName() + ": " + e.getMessage());
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
            EchoMessageSOAP11Service myService1 = new EchoMessageSOAP11Service();

            EchoMessageSOAP12Service myService2 = new EchoMessageSOAP12Service();

            EchoMessagePortType myPort1 = myService1.getEchoMessageSOAP11Port();

            EchoMessagePortType myPort2 = myService2.getEchoMessageSOAP12Port();

            myService1.setHandlerResolver(new PortHandlerResolver(myService1.getHandlerResolver()));

            myService2.setHandlerResolver(new PortHandlerResolver(myService2.getHandlerResolver()));

            EchoMessagePortType myPort3 = myService1.getEchoMessageSOAP11Port();

            EchoMessagePortType myPort4 = myService2.getEchoMessageSOAP12Port();

            assertNotNull("Null myService1 HandlerResolver", 
                          myService1.getHandlerResolver());

            assertNotNull("Null myService2 HandlerResolver", 
                          myService2.getHandlerResolver());

            actualString = myPort1.echoMessage(testString);
            assertEquals("Unexpected result for myPort1", resultStringSOAP11, actualString);

            actualString = myPort2.echoMessage(testString);
            assertEquals("Unexpected result for myPort2", resultStringSOAP12, actualString);

            actualString = myPort3.echoMessage(testString);
            assertEquals("Unexpected result for myPort3", resultString4, actualString);

            actualString = myPort4.echoMessage(testString);
            assertEquals("Unexpected result for myPort4", resultString5, actualString);

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
            EchoMessageSOAP11Service myService1 = new EchoMessageSOAP11Service();

            EchoMessageSOAP12Service myService2 = new EchoMessageSOAP12Service();

            EchoMessagePortType myPort1 = myService1.getEchoMessageSOAP11Port();

            EchoMessagePortType myPort2 = myService2.getEchoMessageSOAP12Port();

            /* 
             * myService1 handler chain: ClientLogicalHandlerA, 
             *                           ClientSOAP11Handler
             */
            myService1.setHandlerResolver(new PortHandlerResolver(myService1.getHandlerResolver()));

            /* 
             * myService2 handler chain: ClientLogicalHandler, 
             *                           ClientSOAPHandler,
             *                           ClientSOAPHandler2
             */
            myService2.setHandlerResolver(new ServiceHandlerResolver(myService2.getHandlerResolver()));

            EchoMessagePortType myPort3 = myService1.getEchoMessageSOAP11Port();

            Binding binding = ((BindingProvider)myPort3).getBinding();

            List<Handler> handlerList = binding.getHandlerChain();

            if (handlerList == null) {
                fail("No HandlerChain configured for this binding");
            }

            handlerList.add(new ClientSOAPHandler2());
            handlerList.add(new ClientLogicalHandlerB());
             
            EchoMessagePortType myPort4 = myService2.getEchoMessageSOAP12Port();

            myService1.setHandlerResolver(new ServiceHandlerResolver(null));
            EchoMessagePortType myPort5 = myService1.getEchoMessageSOAP11Port();

            myService2.setHandlerResolver(new PortHandlerResolver(null));
            EchoMessagePortType myPort6 = myService2.getEchoMessageSOAP12Port();

            assertNotNull("Null binding HandlerChain", binding.getHandlerChain());

            assertNotNull("Null myService1 HandlerResolver", 
                          myService1.getHandlerResolver());

            assertNotNull("Null myService2 HandlerResolver", 
                          myService2.getHandlerResolver());

            actualString = myPort1.echoMessage(testString);
            assertEquals("Unexpected result for myPort1", resultStringSOAP11, actualString);

            actualString = myPort2.echoMessage(testString);
            assertEquals("Unexpected result for myPort2", resultStringSOAP12, actualString);

            actualString = myPort3.echoMessage(testString);
            assertEquals("Unexpected result for myPort3", resultString6, actualString);

            actualString = myPort4.echoMessage(testString);
            assertEquals("Unexpected result for myPort4", resultString3, actualString);

            actualString = myPort5.echoMessage(testString);
            assertEquals("Unexpected result for myPort5", resultString2, actualString);

            actualString = myPort6.echoMessage(testString);
            assertEquals("Unexpected result for myPort6", resultString5, actualString);

            System.err.println("====== SUCCESS: testHandlerChainSnapshot() ======\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("====== END testHandlerChainSnapshot() ======\n");
            fail("Exception caught in testHandlerChainSnapshot(): " + e.getClass().getName() + ": " + e.getMessage());
        }
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

