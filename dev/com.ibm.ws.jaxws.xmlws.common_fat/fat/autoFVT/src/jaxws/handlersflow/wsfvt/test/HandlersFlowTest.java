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
// 01/08/09  jramos         563913          Change log locations 
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

import com.ibm.ws.wsfvt.build.tools.AppConst;

import common.utils.execution.ExecutionException;
import common.utils.execution.ExecutionFactory;
import common.utils.topology.IAppServer;
import common.utils.topology.TopologyActions;
import common.utils.topology.visitor.QueryDefaultNode;

import jaxws.handlersflow.wsfvt.common.*;
import jaxws.handlersflow.wsfvt.client.*;

/**
 * This class verifies client and server handlers are invoked during normal 
 * message processing.
 */
public class HandlersFlowTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
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
    public HandlersFlowTest(String name) {
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
        return new TestSuite(HandlersFlowTest.class);
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
     * Verifies handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Server handler chain is configured using
     *               annotation @HandlerChain.  Verifies handler execution 
     *               flow when both client and server side handlers 
     *               handleMessage() returns true.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testNormalFlow() {
        System.err.println("====== In testNormalFlow() ======");

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
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testNormalFlow() ======\n");
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
            System.err.println("====== SUCCESS: testNormalFlow() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testNormalFlow() ======\n");
            fail("Exception caught in testNormalFlow(): " + e.getClass().getName());
        }
    }


    /**
     * Verifies handlers execution flow complies withJAX-WS spec.
     *
     * @throws Exception Any kind of exception
     * @testStrategy This test programmatically configures client handler
     *               chain.  Server handler chain is configured using
     *               annotation @HandlerChain.  Verifies handler execution
     *               flow when both client and server side handlers
     *               handleMessage() returns true when processing message 
     *               for a OneWay operation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testNormalFlowOneWay() {
        System.err.println("====== In testNormalFlowOneWay() ======");
        String expClientFlow = "Client:" + 
                    "Client_ClientLogicalHandler_handleMessage_Outbound:" + 
                    "Client_ClientSOAPHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_handleMessage_Outbound:" + 
                    "Client_ClientPropHandler_close:" + 
                    "Client_ClientSOAPHandler_close:" + 
                    "Client_ClientLogicalHandler_close:"; 

        String expServerFlow = 
                    "Server_ServerPropHandler_handleMessage_Inbound:" + 
                    "Server_ServerSOAPHandler_handleMessage_Inbound:" + 
                    "Server_ServerLogicalHandler_handleMessage_Inbound:" + 
                    "Server_ServerLogicalHandler_close:" +
                    "Server_ServerSOAPHandler_close:" + 
                    "Server_ServerPropHandler_close:" +
                    "Server_sendString:"; 

        _log.setLogfileName(clientLogfile);
        _log.log("Client:");
        try {
            CountStringPortType myPort = (new CountStringService()).getCountStringPort();
   
            Map reqCtxt = ((BindingProvider) myPort).getRequestContext();
            reqCtxt.put(TestConstants.CLIENT_LOG_PROPERTY, clientLogfile);
            reqCtxt.put(TestConstants.SERVER_LOG_PROPERTY, serverLogfile);

            if (!configHandlerChain((BindingProvider) myPort)) {
                System.err.println("====== END testNormalFlowOneWay() ======\n");
                fail("Failed to configure proxy handler chain");
            }

            myPort.sendString(testString);

            String actualFlow = TestConstants.getLogFileContents(clientLogfile);
            // String actualFlow = ExecutionFactory.getExecution().executeGetFileContents(clientLogfile, clientHost);
            assertEquals("Incorrect client execution flow", 
                         expClientFlow, actualFlow);

            // wait for server to complete processing
            Thread.sleep(1000L);
            ExecutionFactory.getExecution().executeCopyFile(serverLogfile, serverHost, localServerLogfile, clientHost);
            actualFlow = TestConstants.getLogFileContents(localServerLogfile);
            // actualFlow = ExecutionFactory.getExecution().executeGetFileContents(serverLogfile, serverHost);
            assertEquals("Incorrect server execution flow", 
                         expServerFlow, actualFlow);
            System.err.println("====== SUCCESS: testNormalFlowOneWay() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("====== END testNormalFlowOneWay() ======\n");
            fail("Exception caught in testNormalFlowOneWay(): " + e.getClass().getName());
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

