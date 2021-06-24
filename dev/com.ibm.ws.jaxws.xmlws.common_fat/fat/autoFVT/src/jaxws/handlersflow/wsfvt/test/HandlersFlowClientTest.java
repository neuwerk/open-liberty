//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date      UserId         Defect          Description
// ----------------------------------------------------------------------------
// 05/10/07  mzheng         443868          Use ACUTE framework
// 03/07/08  mzheng         502861          Fix file format issue with z/OS
// 06/16/08  mzheng         516161.1        Update OneWay test cases
// 01/08/09  jramos         563913          Change log locations 
//07/29/10   nthaker	  661818           Changing test case to not look for wsException based on new @xmlRootElement logic change in runtime.
//

package jaxws.handlersflow.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.io.File;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;

import com.ibm.ws.wsfvt.build.tools.AppConst;

import common.utils.execution.ExecutionException;
import common.utils.execution.ExecutionFactory;
import common.utils.topology.IAppServer;
import common.utils.topology.TopologyActions;
import common.utils.topology.visitor.QueryDefaultNode;

import jaxws.handlersflow.wsfvt.common.*;
import jaxws.handlersflow.wsfvt.client.*;

/**
 * This class consists of test cases that verify client side handlers are 
 * invoked, correct callbacks are made during message processing, and 
 * subsequent actions following handler completion comply with JAX-WS spec.
 */
public class HandlersFlowClientTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static IAppServer server = QueryDefaultNode.defaultAppServer;

    private static final String clientLogfile = AppConst.FVT_HOME + File.separator + "build" + File.separator + "work" + File.separator + TestConstants.CLIENT_LOGFILE;

    private static final String localServerLogfile = AppConst.FVT_HOME + File.separator + "build" + File.separator + "work" + File.separator + TestConstants.SERVER_LOGFILE;

    private static final String serverLogfile = server.getNodeContainer().getProfileDir() + File.separator + "logs" + File.separator + TestConstants.SERVER_LOGFILE;

    private static final String serverHost = server.getMachine().getHostname();

    private static final String clientHost = TopologyActions.FVT_MACHINE.getHostname();

    private static Log _log = new Log(AppConst.FVT_HOME + File.separator + "logs" + File.separator + TestConstants.DEFAULT_LOGFILE);

    private static final String testString = "Hello!";

    /*
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public HandlersFlowClientTest(String name) {
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
        return new TestSuite(HandlersFlowClientTest.class);
    }


    public void setUp() {
        // delete old log files before running each test
        try {
            ExecutionFactory.getExecution().executeDeleteFileSystemEntry(serverHost, serverLogfile);
        } catch (ExecutionException e) {
        } 

        try {
            ExecutionFactory.getExecution().executeDeleteFileSystemEntry(clientHost, clientLogfile);
        } catch (ExecutionException e) {
        } 
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client 
     *               side logical handler handleMessage() returns false 
     *               when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalOutMessageFalse() {
        System.err.println("====== In testLogicalOutMessageFalse() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalOutMessageFalse() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a WebServiceException in testLogicalOutMessageFalse()");
        } catch (WebServiceException e) {
            try {
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                              expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalOutMessageFalse() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalOutMessageFalse() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalOutMessageFalse() ======\n");
            fail("Exception caught in testLogicalOutMessageFalse(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() returns false
     *               when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPOutMessageFalse() {
        System.err.println("====== In testSOAPOutMessageFalse() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientLogicalHandler_handleMessage_Inbound:" + 
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPOutMessageFalse() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a WebServiceException in testSOAPOutMessageFalse()");
        } catch (WebServiceException e) {
            try {
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                              expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalOutMessageFalse() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPOutMessageFalse() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPOutMessageFalse() ======\n");
            fail("Exception caught in testSOAPOutMessageFalse(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() returns false
     *               when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropOutMessageFalse() {
        System.err.println("====== In testPropOutMessageFalse() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Inbound:" + 
                    "Client_ClientLogicalHandler_handleMessage_Inbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropOutMessageFalse() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            //We should not see any expception while or after webservice invocation is complete.
            //Even thougth the server handler is returning false, which would mean the response
            //message essentially is a request, the engine has ability to demarshal it. Engine can fail
            //ocassionally with type cast or other problems but the idea is that if the test case handler
            //returns false but does not set response message that matches wsdl we should expect
            //irratic behavior, in thise case we do not expect a Webservices Exception.
            try {
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                              expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropOutMessageFalse() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropOutMessageFalse() ======\n");
        } catch (WebServiceException e) {
            //Keeping the webservces excepiton around just in case the runtime throws Exception while 
            //demarshalling the response.
            try {
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                              expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropOutMessageFalse() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropOutMessageFalse() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropOutMessageFalse() ======\n");
            fail("Exception caught in testPropOutMessageFalse(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleMessage() throws a 
     *               ProtocolException when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalOutMessageProtocolException() {
        System.err.println("====== In testLogicalOutMessageProtocolException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalOutMessageProtocolException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testLogicalOutMessageProtocolException()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalOutMessageProtocolException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalOutMessageProtocolException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalOutMessageProtocolException() ======\n");
            fail("Exception caught in testLogicalOutMessageProtocolException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() throws a 
     *               ProtocolException when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPOutMessageProtocolException() {
        System.err.println("====== In testSOAPOutMessageProtocolException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientLogicalHandler_handleFault_Inbound:" + 
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPOutMessageProtocolException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testSOAPOutMessageProtocolException()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPOutMessageProtocolException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPOutMessageProtocolException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPOutMessageProtocolException() ======\n");
            fail("Exception caught in testSOAPOutMessageProtocolException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() throws a 
     *               ProtocolException when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropOutMessageProtocolException() {
        System.err.println("====== In testPropOutMessageProtocolException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleFault_Inbound:" + 
                    "Client_ClientLogicalHandler_handleFault_Inbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropOutMessageProtocolException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testPropOutMessageProtocolException()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropOutMessageProtocolException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropOutMessageProtocolException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropOutMessageProtocolException() ======\n");
            fail("Exception caught in testPropOutMessageProtocolException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleMessage() throws a 
     *               RuntimeException when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalOutMessageRuntimeException() {
        System.err.println("====== In testLogicalOutMessageRuntimeException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalOutMessageRuntimeException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a RuntimeException in testLogicalOutMessageRuntimeException()");

        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Incorrect RuntimeException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalOutMessageRuntimeException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalOutMessageRuntimeException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalOutMessageRuntimeException() ======\n");
            fail("Exception caught in testLogicalOutMessageRuntimeException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() throws a 
     *               RuntimeException when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPOutMessageRuntimeException() {
        System.err.println("====== In testSOAPOutMessageRuntimeException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPOutMessageRuntimeException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a RuntimeException in testSOAPOutMessageRuntimeException()");

        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Incorrect RuntimeException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPOutMessageRuntimeException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPOutMessageRuntimeException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPOutMessageRuntimeException() ======\n");
            fail("Exception caught in testSOAPOutMessageRuntimeException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() throws a 
     *               RuntimeException when processing outbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropOutMessageRuntimeException() {
        System.err.println("====== In testPropOutMessageRuntimeException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropOutMessageRuntimeException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a RuntimeException in testPropOutMessageRuntimeException()");

        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Incorrect RuntimeException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropOutMessageRuntimeException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropOutMessageRuntimeException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropOutMessageRuntimeException() ======\n");
            fail("Exception caught in testPropOutMessageRuntimeException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleMessage() returns false
     *               when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalInMessageFalse() {
        System.err.println("====== In testLogicalInMessageFalse() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Inbound:" +
                    "Client_ClientLogicalHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalInMessageFalse() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            assertEquals("Incorrect return value", testString.length(), retVal);

            String actualFlow = TestConstants.getLogFileContents(clientLogfile);
            // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
            assertEquals("Incorrect client execution flow", 
                         expClientFlow, actualFlow);

            ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
            actualFlow = TestConstants.getLogFileContents(localServerLogfile);
            // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
            assertEquals("Incorrect server execution flow",
                         expServerFlow, actualFlow);

            System.err.println("====== SUCCESS: testLogicalInMessageFalse() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalInMessageFalse() ======\n");
            fail("Exception caught in testLogicalInMessageFalse(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() returns false
     *               when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPInMessageFalse() {
        System.err.println("====== In testSOAPInMessageFalse() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPInMessageFalse() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            assertEquals("Incorrect return value", testString.length(), retVal);

            String actualFlow = TestConstants.getLogFileContents(clientLogfile);
            // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
            assertEquals("Incorrect client execution flow", 
                         expClientFlow, actualFlow);

            ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
            actualFlow = TestConstants.getLogFileContents(localServerLogfile);
            // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
            assertEquals("Incorrect server execution flow",
                         expServerFlow, actualFlow);

            System.err.println("====== SUCCESS: testSOAPInMessageFalse() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPInMessageFalse() ======\n");
            fail("Exception caught in testSOAPInMessageFalse(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() returns false
     *               when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropInMessageFalse() {
        System.err.println("====== In testPropInMessageFalse() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropInMessageFalse() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            assertEquals("Incorrect return value", testString.length(), retVal);

            String actualFlow = TestConstants.getLogFileContents(clientLogfile);
            // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
            assertEquals("Incorrect client execution flow", 
                         expClientFlow, actualFlow);

            ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
            actualFlow = TestConstants.getLogFileContents(localServerLogfile);
            // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
            assertEquals("Incorrect server execution flow",
                         expServerFlow, actualFlow);

            System.err.println("====== SUCCESS: testPropInMessageFalse() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropInMessageFalse() ======\n");
            fail("Exception caught in testPropInMessageFalse(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleMessage() throws a 
     *               ProtocolException when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalInMessageProtocolException() {
        System.err.println("====== In testLogicalInMessageProtocolException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Inbound:" +
                    "Client_ClientLogicalHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();

        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalInMessageProtocolException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testLogicalInMessageProtocolException()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);

                ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
                actualFlow = TestConstants.getLogFileContents(localServerLogfile);
                // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
                assertEquals("Incorrect server execution flow",
                             expServerFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalInMessageProtocolException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalInMessageProtocolException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalInMessageProtocolException() ======\n");
            fail("Exception caught in testLogicalInMessageProtocolException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() throws a 
     *               ProtocolException when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPInMessageProtocolException() {
        System.err.println("====== In testSOAPInMessageProtocolException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();

        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPInMessageProtocolException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testSOAPInMessageProtocolException()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);

                ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
                actualFlow = TestConstants.getLogFileContents(localServerLogfile);
                // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
                assertEquals("Incorrect server execution flow",
                             expServerFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPInMessageProtocolException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPInMessageProtocolException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPInMessageProtocolException() ======\n");
            fail("Exception caught in testSOAPInMessageProtocolException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() throws a  
     *               ProtocolException when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropInMessageProtocolException() {
        System.err.println("====== In testPropInMessageProtocolException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();

        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropInMessageProtocolException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testPropInMessageProtocolException()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);

                ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
                actualFlow = TestConstants.getLogFileContents(localServerLogfile);
                // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
                assertEquals("Incorrect server execution flow",
                             expServerFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropInMessageProtocolException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropInMessageProtocolException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropInMessageProtocolException() ======\n");
            fail("Exception caught in testPropInMessageProtocolException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleMessage() throw
     *               RuntimeException when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalInMessageRuntimeException() {
        System.err.println("====== In testLogicalInMessageRuntimeException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Inbound:" +
                    "Client_ClientLogicalHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalInMessageRuntimeException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a RuntimeException in testLogicalInMessageRuntimeException()");

        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Incorrect RuntimeException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);

                ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
                actualFlow = TestConstants.getLogFileContents(localServerLogfile);
                // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
                assertEquals("Incorrect server execution flow",
                             expServerFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalInMessageRuntimeException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalInMessageRuntimeException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalInMessageRuntimeException() ======\n");
            fail("Exception caught in testLogicalInMessageRuntimeException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() throw
     *               RuntimeException when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPInMessageRuntimeException() {
        System.err.println("====== In testSOAPInMessageRuntimeException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPInMessageRuntimeException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a RuntimeException in testSOAPInMessageRuntimeException()");

        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Incorrect RuntimeException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);

                ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
                actualFlow = TestConstants.getLogFileContents(localServerLogfile);
                // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
                assertEquals("Incorrect server execution flow",
                             expServerFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPInMessageRuntimeException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPInMessageRuntimeException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPInMessageRuntimeException() ======\n");
            fail("Exception caught in testSOAPInMessageRuntimeException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() throw
     *               RuntimeException when processing inbound message.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropInMessageRuntimeException() {
        System.err.println("====== In testPropInMessageRuntimeException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" +
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Outbound:" +
                    "Client_ClientPropHandler_handleMessage_Inbound:" +
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:";

        String expServerFlow =
                    "Server_ServerPropHandler_handleMessage_Inbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" +
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" +
                    "Server_countString:" +
                    "Server_ServerLogicalHandler_handleMessage_Outbound:" +
                    "Server_ServerSOAPHandler_handleMessage_Outbound:" +
                    "Server_ServerPropHandler_handleMessage_Outbound:" +
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" +
                    "Server_ServerPropHandler_close:";

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.INBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropInMessageRuntimeException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a RuntimeException in testPropInMessageRuntimeException()");

        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Incorrect RuntimeException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);

                ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
                actualFlow = TestConstants.getLogFileContents(localServerLogfile);
                // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
                assertEquals("Incorrect server execution flow",
                             expServerFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropInMessageRuntimeException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropInMessageRuntimeException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropInMessageRuntimeException() ======\n");
            fail("Exception caught in testPropInMessageRuntimeException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleMessage() returns false
     *               when processing outbound message of a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalOutMessageFalseOneWay() {
        System.err.println("====== In testLogicalOutMessageFalseOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalOutMessageFalseOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try {
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                              expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalOutMessageFalseOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalOutMessageFalseOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalOutMessageFalseOneWay() ======\n");
            fail("Exception caught in testLogicalOutMessageFalseOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() returns false
     *               when processing outbound message of a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPOutMessageFalseOneWay() {
        System.err.println("====== In testSOAPOutMessageFalseOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPOutMessageFalseOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try {
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                              expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPOutMessageFalseOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPOutMessageFalseOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPOutMessageFalseOneWay() ======\n");
            fail("Exception caught in testSOAPOutMessageFalseOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() returns false
     *               when processing outbound message of a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropOutMessageFalseOneWay() {
        System.err.println("====== In testPropOutMessageFalseOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        try {
            ArrayList<String> testPath = new ArrayList<String> ();
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_FALSE);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropOutMessageFalseOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try {
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                              expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropOutMessageFalseOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropOutMessageFalseOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropOutMessageFalseOneWay() ======\n");
            fail("Exception caught in testPropOutMessageFalseOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleMessage() throws a 
     *               ProtocolException when processing outbound message of 
     *               a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalOutMessageProtocolExceptionOneWay() {
        System.err.println("====== In testLogicalOutMessageProtocolExceptionOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalOutMessageProtocolExceptionOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalOutMessageProtocolExceptionOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalOutMessageProtocolExceptionOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalOutMessageProtocolExceptionOneWay() ======\n");
            // fail("Exception caught in testLogicalOutMessageProtocolExceptionOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() throws a  
     *               ProtocolException when processing outbound message of
     *               a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPOutMessageProtocolExceptionOneWay() {
        System.err.println("====== In testSOAPOutMessageProtocolExceptionOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPOutMessageProtocolExceptionOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPOutMessageProtocolExceptionOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPOutMessageProtocolExceptionOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPOutMessageProtocolExceptionOneWay() ======\n");
            // fail("Exception caught in testSOAPOutMessageProtocolExceptionOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() throws a  
     *               ProtocolException when processing outbound message of
     *               a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropOutMessageProtocolExceptionOneWay() {
        System.err.println("====== In testPropOutMessageProtocolExceptionOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropOutMessageProtocolExceptionOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropOutMessageProtocolExceptionOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropOutMessageProtocolExceptionOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropOutMessageProtocolExceptionOneWay() ======\n");
            // fail("Exception caught in testPropOutMessageProtocolExceptionOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleMessage() throws a  
     *               RuntimeException when processing outbound message of
     *               a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalOutMessageRuntimeExceptionOneWay() {
        System.err.println("====== In testLogicalOutMessageRuntimeExceptionOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientLogicalHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalOutMessageRuntimeExceptionOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalOutMessageRuntimeExceptionOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalOutMessageRuntimeExceptionOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalOutMessageRuntimeExceptionOneWay() ======\n");
            // fail("Exception caught in testLogicalOutMessageRuntimeExceptionOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleMessage() throws a 
     *               RuntimeException when processing outbound message of
     *               a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPOutMessageRuntimeExceptionOneWay() {
        System.err.println("====== In testSOAPOutMessageRuntimeExceptionOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientSOAPHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPOutMessageRuntimeExceptionOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPOutMessageRuntimeExceptionOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPOutMessageRuntimeExceptionOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPOutMessageRuntimeExceptionOneWay() ======\n");
            // fail("Exception caught in testSOAPOutMessageRuntimeExceptionOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side last SOAP handler handleMessage() throws a  
     *               RuntimeException when processing outbound message of
     *               a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPropOutMessageRuntimeExceptionOneWay() {
        System.err.println("====== In testPropOutMessageRuntimeExceptionOneWay() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);
 
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testPropOutMessageRuntimeExceptionOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testPropOutMessageRuntimeExceptionOneWay() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testPropOutMessageRuntimeExceptionOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testPropOutMessageRuntimeExceptionOneWay() ======\n");
            // fail("Exception caught in testPropOutMessageRuntimeExceptionOneWay(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleFault() returns false.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalHandleFaultFalse() {
        System.err.println("====== In testLogicalHandleFaultFalse() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleFault_Inbound:" + 
                    "Client_ClientLogicalHandler_handleFault_Inbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        ArrayList<String> faultPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            faultPath.add(TestConstants.CLIENT_ROLE);
            faultPath.add("ClientLogicalHandler");
            faultPath.add(TestConstants.INBOUND);
            faultPath.add(TestConstants.PATH_FALSE);

            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);
            reqCtxt.put(TestConstants.FAULT_PATH_PROPERTY, faultPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalHandleFaultFalse() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testLogicalHandleFaultFalse()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalHandleFaultFalse() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalHandleFaultFalse() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalHandleFaultFalse() ======\n");
            fail("Exception caught in testLogicalHandleFaultFalse(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleFault() throws a  
     *               ProtocolException.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalHandleFaultProtocolException() {
        System.err.println("====== In testLogicalHandleFaultProtocolException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleFault_Inbound:" + 
                    "Client_ClientLogicalHandler_handleFault_Inbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        ArrayList<String> faultPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            faultPath.add(TestConstants.CLIENT_ROLE);
            faultPath.add("ClientLogicalHandler");
            faultPath.add(TestConstants.INBOUND);
            faultPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);

            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);
            reqCtxt.put(TestConstants.FAULT_PATH_PROPERTY, faultPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalHandleFaultProtocolException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testLogicalHandleFaultProtocolException()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         faultPath.get(3) + " thrown in " + faultPath.get(0) + " " + faultPath.get(1) + " handleFault() " + faultPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalHandleFaultProtocolException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalHandleFaultProtocolException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalHandleFaultProtocolException() ======\n");
            fail("Exception caught in testLogicalHandleFaultProtocolException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleFault() throws a 
     *               RuntimeException.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLogicalHandleFaultRuntimeException() {
        System.err.println("====== In testLogicalHandleFaultRuntimeException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleFault_Inbound:" + 
                    "Client_ClientLogicalHandler_handleFault_Inbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        ArrayList<String> faultPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            faultPath.add(TestConstants.CLIENT_ROLE);
            faultPath.add("ClientLogicalHandler");
            faultPath.add(TestConstants.INBOUND);
            faultPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);

            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);
            reqCtxt.put(TestConstants.FAULT_PATH_PROPERTY, faultPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testLogicalHandleFaultRuntimeException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a RuntimeException in testLogicalHandleFaultRuntimeException()");

        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Incorrect RuntimeException message", 
                         faultPath.get(3) + " thrown in " + faultPath.get(0) + " " + faultPath.get(1) + " handleFault() " + faultPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testLogicalHandleFaultRuntimeException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testLogicalHandleFaultRuntimeException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testLogicalHandleFaultRuntimeException() ======\n");
            fail("Exception caught in testLogicalHandleFaultRuntimeException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleFault() returns false.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPHandleFaultFalse() {
        System.err.println("====== In testSOAPHandleFaultFalse() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleFault_Inbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        ArrayList<String> faultPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            faultPath.add(TestConstants.CLIENT_ROLE);
            faultPath.add("ClientSOAPHandler");
            faultPath.add(TestConstants.INBOUND);
            faultPath.add(TestConstants.PATH_FALSE);

            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);
            reqCtxt.put(TestConstants.FAULT_PATH_PROPERTY, faultPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPHandleFaultFalse() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testSOAPHandleFaultFalse()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         testPath.get(3) + " thrown in " + testPath.get(0) + " " + testPath.get(1) + " handleMessage() " + testPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPHandleFaultFalse() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPHandleFaultFalse() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPHandleFaultFalse() ======\n");
            fail("Exception caught in testSOAPHandleFaultFalse(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side SOAP handler handleFault() throws a 
     *               ProtocolException.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPHandleFaultProtocolException() {
        System.err.println("====== In testSOAPHandleFaultProtocolException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleFault_Inbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        ArrayList<String> faultPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            faultPath.add(TestConstants.CLIENT_ROLE);
            faultPath.add("ClientSOAPHandler");
            faultPath.add(TestConstants.INBOUND);
            faultPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);

            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);
            reqCtxt.put(TestConstants.FAULT_PATH_PROPERTY, faultPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPHandleFaultProtocolException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a ProtocolException in testSOAPHandleFaultProtocolException()");

        } catch (ProtocolException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         faultPath.get(3) + " thrown in " + faultPath.get(0) + " " + faultPath.get(1) + " handleFault() " + faultPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPHandleFaultProtocolException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPHandleFaultProtocolException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPHandleFaultProtocolException() ======\n");
            fail("Exception caught in testSOAPHandleFaultProtocolException(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies client handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Verifies handler execution flow when client
     *               side logical handler handleFault() throws a 
     *               RuntimeException.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPHandleFaultRuntimeException() {
        System.err.println("====== In testSOAPHandleFaultRuntimeException() ======");

        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleFault_Inbound:" + 
                    "Client_ClientPropHandler_close:" +
                    "Client_ClientSOAPHandler_close:" +
                    "Client_ClientLogicalHandler_close:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");

        ArrayList<String> testPath = new ArrayList<String> ();
        ArrayList<String> faultPath = new ArrayList<String> ();
        try {
            testPath.add(TestConstants.CLIENT_ROLE);
            testPath.add("ClientPropHandler");
            testPath.add(TestConstants.OUTBOUND);
            testPath.add(TestConstants.PATH_PROTOCOL_EXCEPTION);
 
            faultPath.add(TestConstants.CLIENT_ROLE);
            faultPath.add("ClientSOAPHandler");
            faultPath.add(TestConstants.INBOUND);
            faultPath.add(TestConstants.PATH_RUNTIME_EXCEPTION);

            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);
            reqCtxt.put(TestConstants.MESSAGE_PATH_PROPERTY, testPath);
            reqCtxt.put(TestConstants.FAULT_PATH_PROPERTY, faultPath);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testSOAPHandleFaultRuntimeException() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            int retVal = myPort.countString(testString);
            fail("Should have caught a RuntimeException in testSOAPHandleFaultRuntimeException()");

        } catch (RuntimeException e) {
            e.printStackTrace();
            assertEquals("Incorrect ProtocolException message", 
                         faultPath.get(3) + " thrown in " + faultPath.get(0) + " " + faultPath.get(1) + " handleFault() " + faultPath.get(2), 
                         e.getMessage());
            try { 
                String actualFlow = TestConstants.getLogFileContents(clientLogfile);
                // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
                assertEquals("Incorrect client execution flow", 
                             expClientFlow, actualFlow);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("====== END testSOAPHandleFaultRuntimeException() ======\n");
                fail("Exception caught when verifying execution flow: " + ex.getClass().getName());
            }

            System.err.println("====== SUCCESS: testSOAPHandleFaultRuntimeException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testSOAPHandleFaultRuntimeException() ======\n");
            fail("Exception caught in testSOAPHandleFaultRuntimeException(): " + e.getClass().getName());
        }
    }


    /**
     * Configure handler chain for binding provider
     */
    private boolean configHandlerChain(BindingProvider bp) {
        try {
            // set binding handler chain
            Binding binding = bp.getBinding();

            // can create new list or use existing one
            List<Handler> handlerList = binding.getHandlerChain();

            if (handlerList == null) {
                handlerList = new ArrayList<Handler>();
            }

            handlerList.add(new ClientSOAPHandler());
            handlerList.add(new ClientPropHandler());
            handlerList.add(new ClientLogicalHandler());

            binding.setHandlerChain(handlerList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception caught in configHandlerChain(): " + e.getClass().getName());
        }
        return false;
    }
}

