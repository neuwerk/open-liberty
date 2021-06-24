/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/* updated by btiffany 2/2009 for WAS FVT environment */

package jaxws.handlerextensions.wsfvt.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.handler.Handler;

import junit.framework.*;


import org.apache.axis2.Constants;
import org.apache.axis2.jaxws.sample.headershandler.*;
import org.test.headershandler.HeadersHandlerResponse;
/* begin simplicity -specific imports */
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import common.utils.topology.Ports;
import common.utils.execution.OperatingSystem;
//import com.ibm.ws.wsfvt.build.tools.topology.Cell;
//import com.ibm.ws.wsfvt.build.tools.topology.Server;
/* end simplicity-specific imports */
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import com.ibm.ws.wsfvt.build.tools.*;
import javax.xml.ws.Response;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;


/**
 * @author rott * 
 * extended and modified by bt. 
 * 
 * comments by bt: 
 * A proprietary extension has been added to the jax-ws message context to improve the 
 * performance of application handlers.  This test checks that extension.  
 * In normal application handlers, the entire soap envelope is demarshalled before the headers 
 * are accessible.  This is a performance problem for large envelopes.  The extension adds 
 * methods to manipulate headers without the overhead of demarshalling the body.  It should work
 * in both logical and protocol handlers.
 * 
 * The new api's look something like this:
 * (Map<QName, List<String>>)messagecontext.get/put(Constants.JAXWS_OUTBOUND/INBOUND_SOAP_HEADERS)
 * 
 * Testing needs to cover that these work as expected on server and client side,
 * they don't interfere with reading the 
 * payload or soap envelope, and that they don't interact adversely with the old method
 * of getting and updating soap headers.
 * 
 * handler refresher (jsr224 ch9):
 * prototcol handlers only work on a specific protocol while logical handlers are protocol agnostic
 * given a defined chain of proto and logical handlers like this:
 *   class --> proto1, logo1, proto2, logo2 --> wire
 *   on outbound the order will be logo1, , logo2, proto1, proto2
 *   on inbound the order will be proto2, proto1, logo2, logo1. 
 * 
 * To debug, it may be helfpul to run only a single test so the log files from the client
 * and server handlers can be closely examined.  Some of the tests dump them to std out.
 * 
 * Note that if a handler throws an exception, axis2 will eat it and you won't be able to tell
 * what went wrong.  If that is suspected, use tracker.log_exception(e) inside a catch block
 * around the handler's method, that will log the exception to the handler log so you can see
 * the full stack trace.
 *  
 *
 */
public class HeadersHandlerTestCase extends FVTTestCase {
    
    protected static boolean filterLogsForQos = false;
    protected static boolean isSCQOS = false;
    protected static boolean isRMQOS = false;
    protected static Server server;
    // sethostAndPort to some non-null value to override, null to resolve at runtime.
    protected static String hostAndPort = null;
    //protected static String hostAndPort = "http://localhost:80";
    protected static int testsrun = 0;
    protected static boolean skipped = false;  // the whole testcase is to be skipped
    protected static int totaltests = 0;
    protected static String axisEndpoint = null;
    protected static boolean isDistributed = true; // is this os400 or Z?
    protected static boolean isWindows = false;    
    
    // the location of these files has to match what's hardcoded in HandlerTracker
    // the handlers write activity to this log file so we can check it.
    // we do not want nonportable build time burning in of the file locations. 
    
    protected static final String filelognameclient = AppConst.FVT_HOME+ "/build/classes/ClientHeadersHandlerTests.log";
    //protected static final String filelognameserver = AppConst.WAS_HOME +"/ServerHeadersHandlerTests.log";
    protected static String filelognameserver = null;  // set later in setup
    protected static boolean serverIsRemote = false;
    
    // constructor
    public HeadersHandlerTestCase(String s){
        super(s);
    }

    // this lets us select only some tests for debugging
    
    public static Test suite() {
        TestSuite suite = null;
        boolean debug = false;
        if(!debug){
            suite = new TestSuite(HeadersHandlerTestCase.class);
        } else {    
            
            suite = new TestSuite();
            suite.addTest(new HeadersHandlerTestCase("testHeadersHandlerAsyncCallback")); 
     
            /*
            suite.addTest(new HeadersHandlerTestCase("testFaultPath"));
            suite.addTest(new HeadersHandlerTestCase("testHeadersHandler"));
            suite.addTest(new HeadersHandlerTestCase("testHeadersHandlerAsyncCallback"));      
            suite.addTest(new HeadersHandlerTestCase("testHeadersHandlerServerInboundFlowReversal"));
            */
            
        }   
        totaltests = suite.countTestCases();
        return suite;
        
    }
    
    /**
     * method to set up for qos, may be overridden by subclasses.
     *
     */
    protected void QosSetUp()throws Exception {}
    protected void QosTearDown() throws Exception {}
    
    /**
     * runs before each test
     */
    @Override
    protected void setUp() throws Exception  {  
        try{
            System.out.println("setup. testsrun, totaltests = "+testsrun + " " + totaltests);
            if (testsrun++ == 0 ){    // execute once per class               
                    //server = Cell.getDefaultCell().getDefaultServer();   

                   server = TopologyDefaults.getDefaultAppServer();
                   String fvtHostName = TopologyDefaults.defaultAppServerCell.getRootNodeHostname();                   
                    if(hostAndPort == null){
                       // hostAndPort = "http://" + server.getHostname() + ":"
                       //                + server.getWCDefaultHost();
                       hostAndPort = "http://"+ server.getNode().getHostname()+ ":" 
                        +  TopologyDefaults.defaultAppServer.getPort(Ports.WC_defaulthost).toString();
                        
                    }    
                    axisEndpoint = hostAndPort+"/handlerextensions/HeadersHandlerService";
                    OperatingSystem os = TopologyDefaults.defaultAppServer.getMachine().getOperatingSystem();
                    if (os.compareTo( OperatingSystem.WINDOWS) ==0 ){
                        isWindows = true;
                    }               
                    if (os.compareTo(OperatingSystem.ISERIES) ==0 || os.compareTo(OperatingSystem.ZOS)== 0){
                        isDistributed = false;
                    }
                    String fvtHost = TopologyDefaults.getRootServer().getNode().getHostname();
                    String serverHost = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
                    if (fvtHost.compareTo(serverHost)!= 0){ serverIsRemote = true;}
                    System.out.println("qos call");
                    QosSetUp();            
            }       
            //new File(logdir).mkdir();
            File file = new File(filelognameclient);        
            file.delete();  // yes, delete for each retrieval, which should only happen once per test          
            filelognameserver = server.getNode().getProfileDir()+"/ServerHeadersHandlerTests.log";
            Machine m = TopologyDefaults.getDefaultAppServer().getNode().getMachine();
            RemoteFile f = new RemoteFile(m,filelognameserver); 
            f.delete();  // yes, delete for each retrieval, which should only happen once per test
        } catch (Exception e){
            e.printStackTrace(System.out);
            throw e;
        }
    }
    
    protected void tearDown() throws Exception { 
        System.out.println("teardown. testsrun, totaltests = "+testsrun + " " + totaltests);
        if (testsrun == totaltests ){  QosTearDown(); } 
    }    
    
    public static void main(String [] args) throws Exception {
        HeadersHandlerTestCase me = new HeadersHandlerTestCase("x");
        System.out.println( me.readLogFile(filelognameserver));
    }

    /**
     * route a message through a variety of handlers and verify that the handlers see what is
     * expected along the way, each adding, deleting, and checking messages as they go. 
     *
     */
    public void testHeadersHandler() throws Exception {
        try {
            System.out.println("----------------------------------");
            System.out.println("test: " + getName());

            // create the client dynamically
            // handlers are attached via a handlerchain annotation in 
            // HeadersHandlerPortType
            
           // HeadersHandlerService service = new HeadersHandlerService();
            // Liberty does not support SEI/SEI.wsdl wsdl URL style yet
            // URL wsdlLocation = new URL(axisEndpoint + "/HeadersHandlerService.wsdl");
            URL wsdlLocation = new URL(axisEndpoint + "?wsdl");
            HeadersHandlerService service = new HeadersHandlerService(wsdlLocation,
                    new QName("http://org/test/headershandler", "HeadersHandlerService"));
            HeadersHandlerPortType proxy = service.getHeadersHandlerPort();
            BindingProvider p = (BindingProvider) proxy;
            Map<String, Object> requestCtx = p.getRequestContext();
            
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, axisEndpoint);
            
            /*
             * add several headers by way of HeadersAdapter property
             */
            String acoh1, acoh2, acoh3, acoh4, acoh5, acoh6;
            SOAPFactory sf = SOAPFactory.newInstance();
        	try {
            	Map<QName, List<String>> requestHeaders = new HashMap<QName, List<String>>();
            	
            	// QName used here should match the key for the list set on the requestCtx
            	acoh1 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL1);
            	
            	// QName used here should match the key for the list set on the requestCtx
            	acoh2 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL2);
            	
            	// QName used here should match the key for the list set on the requestCtx
            	acoh3 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL3);
            	
            	// QName used here should match the key for the list set on the requestCtx
            	acoh4 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);
            	
            	// create additional header strings that will need to be checked:
                
                // use small message for now
        		acoh5 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH3_HEADER_QNAME, TestHeaders.CONTENT_LARGE);
        		acoh6 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH4_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);				
                
            	
            	List<String> list1 = new ArrayList<String>();
            	list1.add(acoh1);
            	list1.add(acoh2);
            	
            	List<String> list2 = new ArrayList<String>();
            	list2.add(acoh3);
            	list2.add(acoh4);
            	
                // put both lists into the map.
            	requestHeaders.put(TestHeaders.ACOH1_HEADER_QNAME, list1);
            	requestHeaders.put(TestHeaders.ACOH2_HEADER_QNAME, list2);
                
                // put the headers into the message - it's magic!
                
                // the HeadersHandlerPortType class contains a handlerchain annotation
                // which will apply the client side handlerchain specified in HeadersClientHandler.xml 
                
                // you can skip this step on handlers, but not in the client and impl 
                // itself.
            	requestCtx.put(Constants.JAXWS_OUTBOUND_SOAP_HEADERS, requestHeaders);
        	} catch (Throwable e) {
        		fail(e.getMessage());  //we're toast.
        		return;
        	}
        	
        	// some handlers decrement the value, so we can confirm SOAP body manipulation does not corrupt the headers
        	int numOfHandlerHitsInFlow = 3;

            // *********************************
            // ****** call the service *********
            // *********************************
            // qos's need these next two
            requestCtx.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
            requestCtx.put(BindingProvider.SOAPACTION_URI_PROPERTY, "headersHandler");
            System.out.println("calling the service");
            int intParam1 = 10;
            int intParam2 = 10;
            int total = proxy.headersHandler(intParam1, intParam2);
            
            // check that message was munged as we expect it to be 
            assertEquals("Return value should be " + (intParam1 + intParam2 - numOfHandlerHitsInFlow) + " but was " + total ,
                         (intParam1 + intParam2 - numOfHandlerHitsInFlow),
                         total);
            System.out.println("Total (after handler manipulation) = " + total);
			
            /*
             * I tried to give enough info below in the expected_calls list so you can tell what's
             * being tested without having to look at handler code.  All header manipulation is
             * done by SOAPHeadersAdapter.
             * 
             * TODO: I would very much like to have done some other means of
             * header manipulation, but the Axis2 SAAJ module is lacking necessary implementation
             * to do this with any reliability.
             */
			
            
            // check that the changes detected and logged by the handlers are what we expected.
            String client_log = readLogFile(filelognameclient);
            
            // client outbound then inbound 
            String expected_client_calls =
                "HeadersClientLogicalHandler HANDLE_MESSAGE_OUTBOUND\n" 
              + "HeadersClientLogicalHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n" 
              + "HeadersClientLogicalHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
              +  "HeadersClientLogicalHandler CHECKED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientLogicalHandler CHECKED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 4</acoh2pre:acoh2>\n"
              +  "HeadersClientLogicalHandler REMOVED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n"
              +  "HeadersClientLogicalHandler REMOVED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientLogicalHandler REMOVED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 4</acoh2pre:acoh2>\n"
              +  "HeadersClientProtocolHandler HANDLE_MESSAGE_OUTBOUND\n"
              +  "HeadersClientProtocolHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
              +  "HeadersClientProtocolHandler ADDED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler ADDED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientProtocolHandler2 HANDLE_MESSAGE_OUTBOUND\n"
              +  "HeadersClientProtocolHandler2 CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
              +  "HeadersClientProtocolHandler2 CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler2 CHECKED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +   "HeadersClientProtocolHandler GET_HEADERS\n"  
              +   "HeadersClientProtocolHandler2 GET_HEADERS\n"
              // server handlers run in between here
              +  "HeadersClientProtocolHandler2 HANDLE_MESSAGE_INBOUND\n"
              +  "HeadersClientProtocolHandler2 CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler2 ADDED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientProtocolHandler HANDLE_MESSAGE_INBOUND\n"
              +  "HeadersClientProtocolHandler CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler CHECKED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientProtocolHandler REMOVED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler ADDED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 4</acoh2pre:acoh2>\n"
              +  "HeadersClientLogicalHandler HANDLE_MESSAGE_INBOUND\n"
              +  "HeadersClientProtocolHandler2 CLOSE\n"
              +  "HeadersClientProtocolHandler CLOSE\n"
              +  "HeadersClientLogicalHandler CLOSE\n";      
            
            System.out.println("---------- client log expected ---------\n" + expected_client_calls);
            System.out.println("-----------client log actual------------\n" + client_log);
            
            String server_log = readLogFile(filelognameserver);
            String expected_server_calls = ""
                + "HeadersServerProtocolHandler HANDLE_MESSAGE_INBOUND\n"
                + "HeadersServerProtocolHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
                + "HeadersServerProtocolHandler REMOVED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler ADDED_HEADER <acoh4pre:acoh4 xmlns:acoh4pre=\"http://acoh4ns\">small content 4</acoh4pre:acoh4>\n"
                + "HeadersServerLogicalHandler HANDLE_MESSAGE_INBOUND\n"
                + "HeadersServerLogicalHandler CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
                + "HeadersServerLogicalHandler CHECKED_HEADER <acoh4pre:acoh4 xmlns:acoh4pre=\"http://acoh4ns\">small content 4</acoh4pre:acoh4>\n"
                + "HeadersServerLogicalHandler REMOVED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
                + "HeadersServerLogicalHandler REMOVED_HEADER <acoh4pre:acoh4 xmlns:acoh4pre=\"http://acoh4ns\">small content 4</acoh4pre:acoh4>\n"
                // service impl runs here 
                + "HeadersServerLogicalHandler HANDLE_MESSAGE_OUTBOUND\n"
                + "HeadersServerLogicalHandler ADDED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler HANDLE_MESSAGE_OUTBOUND\n"
                + "HeadersServerProtocolHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler REMOVED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler ADDED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
                + "HeadersServerLogicalHandler CLOSE\n"
                + "HeadersServerProtocolHandler CLOSE\n";    
                
            System.out.println("---------- server log expected ---------\n" + expected_server_calls);
            System.out.println("-----------server log actual------------\n" + server_log);    
            
            assertTrue("client log mismatch, see sysout", expected_client_calls.compareTo(client_log)==0);
            if(!server.getNode().getMachine().getOperatingSystem().equals(com.ibm.websphere.simplicity.OperatingSystem.ZOS)) // not valid on Z
            	assertTrue("serverlog mismatch, see sysout" , expected_server_calls.compareTo(server_log)==0); 
            System.out.println("-----------test passed! -----------------------");
            
        } catch (Exception e) {
            System.out.println("the testcase caught an unexpected exception:");
            e.printStackTrace(System.out);
            System.out.println("if a handler failed, the handler log files may contain more detailed information");
            System.out.println("client log:");
            System.out.println(readLogFile(filelognameclient));
            System.out.println("server log:");
            System.out.println(readLogFile(filelognameserver));
            fail(e.getMessage());
        }
    }  // end method
    
	
   /** 
    * test handler flow in an async callback
    * we use the same handler configuration as testHeadersHandler()
    * but on an async callback, should get the same response. 
    *
    */
   public void testHeadersHandlerAsyncCallback()throws Exception  {
      
        try {
            System.out.println("----------------------------------");
            System.out.println("test: " + getName());
 
            //HeadersHandlerService service = new HeadersHandlerService();
            // Liberty does not support SEI.wsdl wsdl URL style yet
            //URL wsdlLocation = new URL(axisEndpoint + "/HeadersHandlerService.wsdl");
            URL wsdlLocation = new URL(axisEndpoint + "?wsdl");
            HeadersHandlerService service = new HeadersHandlerService(wsdlLocation,
                    new QName("http://org/test/headershandler", "HeadersHandlerService"));
            HeadersHandlerPortType proxy = service.getHeadersHandlerPort();
            BindingProvider p = (BindingProvider) proxy;
            Map<String, Object> requestCtx = p.getRequestContext();
            
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, axisEndpoint);
            
            /*
             * add several headers by way of HeadersAdapter property
             */
            String acoh1, acoh2, acoh3, acoh4, acoh5, acoh6;
            SOAPFactory sf = SOAPFactory.newInstance();
            try {
                Map<QName, List<String>> requestHeaders = new HashMap<QName, List<String>>();
                
                // QName used here should match the key for the list set on the requestCtx
                acoh1 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL1);
                
                // QName used here should match the key for the list set on the requestCtx
                acoh2 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL2);
                
                // QName used here should match the key for the list set on the requestCtx
                acoh3 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL3);
                
                // QName used here should match the key for the list set on the requestCtx
                acoh4 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);
                
                // create additional header strings that will need to be checked:
                acoh5 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH3_HEADER_QNAME, TestHeaders.CONTENT_LARGE);
                acoh6 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH4_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);
                
                List<String> list1 = new ArrayList<String>();
                list1.add(acoh1);
                list1.add(acoh2);
                
                List<String> list2 = new ArrayList<String>();
                list2.add(acoh3);
                list2.add(acoh4);
                
                requestHeaders.put(TestHeaders.ACOH1_HEADER_QNAME, list1);
                requestHeaders.put(TestHeaders.ACOH2_HEADER_QNAME, list2);
                requestCtx.put(Constants.JAXWS_OUTBOUND_SOAP_HEADERS, requestHeaders);
                
                // enable wire async process.
                // wire async is not working.  I'm missing the soapaction, or wsa-action, or something.
                // it's not critical to testing of the handlers.  We'll use vanilla async instead.
                //requestCtx.put("com.ibm.websphere.webservices.use.async.mep", Boolean.TRUE);
                requestCtx.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
                requestCtx.put(BindingProvider.SOAPACTION_URI_PROPERTY, "headersHandler");
            } catch (Throwable e) {
                fail(e.getMessage());
                return;
            }
            
            // some handlers decrement the value, so we can confirm SOAP body manipulation does not corrupt the headers
            int numOfHandlerHitsInFlow = 3;
            
            int intParam1 = 10;
            int intParam2 = 10;
            
            HeadersHandlerAsyncCallback callback = new HeadersHandlerAsyncCallback();
            Future<?> future = proxy.headersHandlerAsync(intParam1, intParam2, callback);            
           
            while (!future.isDone()) {
                Thread.sleep(1000);
                System.out.println("Async invocation incomplete");
            }

            int total = callback.getResponseValue();
            
            assertEquals("Return value should be " + (intParam1 + intParam2 - numOfHandlerHitsInFlow) + " but was " + total ,
                         (intParam1 + intParam2 - numOfHandlerHitsInFlow),
                         total);
            System.out.println("Total (after handler manipulation) = " + total);
            // check that the changes detected and logged by the handlers are what we expected.
            String client_log = readLogFile(filelognameclient);
            
            // client outbound then inbound 
            String expected_client_calls =
                "HeadersClientLogicalHandler HANDLE_MESSAGE_OUTBOUND\n" 
              + "HeadersClientLogicalHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n" 
              + "HeadersClientLogicalHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
              +  "HeadersClientLogicalHandler CHECKED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientLogicalHandler CHECKED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 4</acoh2pre:acoh2>\n"
              +  "HeadersClientLogicalHandler REMOVED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n"
              +  "HeadersClientLogicalHandler REMOVED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientLogicalHandler REMOVED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 4</acoh2pre:acoh2>\n"
              +  "HeadersClientProtocolHandler HANDLE_MESSAGE_OUTBOUND\n"
              +  "HeadersClientProtocolHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
              +  "HeadersClientProtocolHandler ADDED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler ADDED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientProtocolHandler2 HANDLE_MESSAGE_OUTBOUND\n"
              +  "HeadersClientProtocolHandler2 CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
              +  "HeadersClientProtocolHandler2 CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler2 CHECKED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +   "HeadersClientProtocolHandler GET_HEADERS\n"  
              +   "HeadersClientProtocolHandler2 GET_HEADERS\n"
              // server handlers run in between here
              +  "HeadersClientProtocolHandler2 HANDLE_MESSAGE_INBOUND\n"
              +  "HeadersClientProtocolHandler2 CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler2 ADDED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientProtocolHandler HANDLE_MESSAGE_INBOUND\n"
              +  "HeadersClientProtocolHandler CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler CHECKED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 3</acoh2pre:acoh2>\n"
              +  "HeadersClientProtocolHandler REMOVED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
              +  "HeadersClientProtocolHandler ADDED_HEADER <acoh2pre:acoh2 xmlns:acoh2pre=\"http://acoh2ns\">small content 4</acoh2pre:acoh2>\n"
              +  "HeadersClientLogicalHandler HANDLE_MESSAGE_INBOUND\n"
              +  "HeadersClientProtocolHandler2 CLOSE\n"
              +  "HeadersClientProtocolHandler CLOSE\n"
              +  "HeadersClientLogicalHandler CLOSE\n";      
            
            System.out.println("---------- client log expected ---------\n" + expected_client_calls);
            System.out.println("-----------client log actual------------\n" + client_log);
            
            String server_log = readLogFile(filelognameserver);
            String expected_server_calls = ""
                + "HeadersServerProtocolHandler HANDLE_MESSAGE_INBOUND\n"
                + "HeadersServerProtocolHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
                + "HeadersServerProtocolHandler REMOVED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 2</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler ADDED_HEADER <acoh4pre:acoh4 xmlns:acoh4pre=\"http://acoh4ns\">small content 4</acoh4pre:acoh4>\n"
                + "HeadersServerLogicalHandler HANDLE_MESSAGE_INBOUND\n"
                + "HeadersServerLogicalHandler CHECKED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
                + "HeadersServerLogicalHandler CHECKED_HEADER <acoh4pre:acoh4 xmlns:acoh4pre=\"http://acoh4ns\">small content 4</acoh4pre:acoh4>\n"
                + "HeadersServerLogicalHandler REMOVED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
                + "HeadersServerLogicalHandler REMOVED_HEADER <acoh4pre:acoh4 xmlns:acoh4pre=\"http://acoh4ns\">small content 4</acoh4pre:acoh4>\n"
                // service impl runs here 
                + "HeadersServerLogicalHandler HANDLE_MESSAGE_OUTBOUND\n"
                + "HeadersServerLogicalHandler ADDED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler HANDLE_MESSAGE_OUTBOUND\n"
                + "HeadersServerProtocolHandler CHECKED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler REMOVED_HEADER <acoh1pre:acoh1 xmlns:acoh1pre=\"http://acoh1ns\">small content 1</acoh1pre:acoh1>\n"
                + "HeadersServerProtocolHandler ADDED_HEADER <acoh3pre:acoh3 xmlns:acoh3pre=\"http://acoh3ns\">replaceMeWithALargeString</acoh3pre:acoh3>\n"
                + "HeadersServerLogicalHandler CLOSE\n"
                + "HeadersServerProtocolHandler CLOSE\n";    
                
            System.out.println("---------- server log expected ---------\n" + expected_server_calls);
            System.out.println("-----------server log actual------------\n" + server_log);    
            
            assertTrue("client log mismatch, see sysout", expected_client_calls.compareTo(client_log)==0);
            if(!server.getNode().getMachine().getOperatingSystem().equals(com.ibm.websphere.simplicity.OperatingSystem.ZOS)) // not valid on Z
            	assertTrue("serverlog mismatch, see sysout" , expected_server_calls.compareTo(server_log)==0); 
            System.out.println("-----------test passed! -----------------------");
            
        } catch (Exception e) {
            System.out.println("caught unexpected exception:");
            e.printStackTrace(System.out);
            System.out.println("if a handler failed, the handler log files may contain more detailed information");
            System.out.println("client log:");
            System.out.println(readLogFile(filelognameclient));
            System.out.println("server log:");
            System.out.println(readLogFile(filelognameserver));
            fail(e.getMessage());
        }   
        System.out.println("----------- test passed -----------------------");
    }	
	
   /**
    * an input variable of value=66 is placed in the message, this causes
    * the serverLogicalHandler to throw a fault and reverse the flow.
    * The test checks that an exception is received, and that the message is as expected. 
    * @throws Exception
    */
    public void testHeadersHandlerServerInboundFault() throws Exception  {
        System.out.println("----------------------------------");
        System.out.println("test: " + getName());
        // Liberty does not support SEI.wsdl wsdl URL style yet
        //URL wsdlLocation = new URL(axisEndpoint + "/HeadersHandlerService.wsdl");
        URL wsdlLocation = new URL(axisEndpoint + "?wsdl");
        HeadersHandlerService service = new HeadersHandlerService(wsdlLocation,
                new QName("http://org/test/headershandler", "HeadersHandlerService"));        
        HeadersHandlerPortType proxy = service.getHeadersHandlerPort();
        BindingProvider p = (BindingProvider) proxy;
        Map<String, Object> requestCtx = p.getRequestContext();

        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, axisEndpoint);

        /*
         * add several headers by way of HeadersAdapter property
         */
        String acoh1, acoh2, acoh3, acoh4, acoh5, acoh6;
        SOAPFactory sf;
        try {
            sf = SOAPFactory.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        Map<QName, List<String>> requestHeaders = new HashMap<QName, List<String>>();

        // QName used here should match the key for the list set on the requestCtx
        acoh1 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL1);

        // QName used here should match the key for the list set on the requestCtx
        acoh2 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL2);

        // QName used here should match the key for the list set on the requestCtx
        acoh3 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL3);

        // QName used here should match the key for the list set on the requestCtx
        acoh4 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);

        // create additional header strings that will need to be checked:
        acoh5 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH3_HEADER_QNAME, TestHeaders.CONTENT_LARGE);
        acoh6 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH4_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);

        List<String> list1 = new ArrayList<String>();
        list1.add(acoh1);
        list1.add(acoh2);

        List<String> list2 = new ArrayList<String>();
        list2.add(acoh3);
        list2.add(acoh4);

        requestHeaders.put(TestHeaders.ACOH1_HEADER_QNAME, list1);
        requestHeaders.put(TestHeaders.ACOH2_HEADER_QNAME, list2);
        requestCtx.put(Constants.JAXWS_OUTBOUND_SOAP_HEADERS, requestHeaders);
        
        // qos's need these next two
        requestCtx.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
        requestCtx.put(BindingProvider.SOAPACTION_URI_PROPERTY, "headersHandler");

        // some handlers decrement the value, so we can confirm SOAP body manipulation does not corrupt the headers
        int numOfHandlerHitsInFlow = 3;

        int intParam1 = 10;
        int intParam2 = 66;
            
        try {
            int total = proxy.headersHandler(intParam1, intParam2);
            fail("headersHandler should have caused an exception, but did not.");
            
            /*
             * I tried to give enough info below in the expected_calls list so you can tell what's
             * being tested without having to look at handler code.  All header manipulation is
             * done by SOAPHeadersAdapter.
             * 
             * TODO: I would very much like to have done some other means of
             * header manipulation, but the Axis2 SAAJ module is lacking necessary implementation
             * to do this with any reliability.
             */
            
        } catch (Exception e) {
              System.out.println("caught an exception, maybe even the one we were expecting");
              e.printStackTrace(System.out);
              System.out.println("message: "+ e.getMessage());
            
            
            String cl_log = readLogFile(filelognameclient);
            String expected_client_calls =
                    // client outbound
                      "HeadersClientLogicalHandler HANDLE_MESSAGE_OUTBOUND\n"
                    + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh1+"\n"
                    + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh2+"\n"
                    + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh3+"\n"
                    + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh4+"\n"
                    + "HeadersClientLogicalHandler REMOVED_HEADER "+acoh1+"\n"
                    + "HeadersClientLogicalHandler REMOVED_HEADER "+acoh3+"\n"
                    + "HeadersClientLogicalHandler REMOVED_HEADER "+acoh4+"\n"   // message manipulated after this action
                    + "HeadersClientProtocolHandler HANDLE_MESSAGE_OUTBOUND\n"
                    + "HeadersClientProtocolHandler CHECKED_HEADER "+acoh2+"\n"                   
                    + "HeadersClientProtocolHandler ADDED_HEADER "+acoh5+"\n"
                    + "HeadersClientProtocolHandler ADDED_HEADER "+acoh3+"\n"
                    + "HeadersClientProtocolHandler2 HANDLE_MESSAGE_OUTBOUND\n"
                    + "HeadersClientProtocolHandler2 CHECKED_HEADER "+acoh2+"\n"
                    + "HeadersClientProtocolHandler2 CHECKED_HEADER "+acoh5+"\n"
                    + "HeadersClientProtocolHandler2 CHECKED_HEADER "+acoh3+"\n"
                    // these next two come from the handlers' getHeaders method,
                    // not clear what is calling it. 
                    + "HeadersClientProtocolHandler GET_HEADERS\n"
                    + "HeadersClientProtocolHandler2 GET_HEADERS\n"
       
                    // client inbound
                    + "HeadersClientProtocolHandler2 HANDLE_FAULT_INBOUND\n"
                    + "HeadersClientProtocolHandler2 CHECKED_HEADER "+acoh5+"\n"
                    + "HeadersClientProtocolHandler2 REMOVED_HEADER "+acoh5+"\n"
                    + "HeadersClientProtocolHandler HANDLE_FAULT_INBOUND\n"
                    + "HeadersClientLogicalHandler HANDLE_FAULT_INBOUND\n"   // getPayload called, just to exercise underlying code
                    + "HeadersClientProtocolHandler2 CLOSE\n"
                    + "HeadersClientProtocolHandler CLOSE\n"
                    + "HeadersClientLogicalHandler CLOSE\n";
            
              
              // server inbound
              String svr_log = readLogFile(filelognameserver);
              String expected_server_calls = ""
               // + "HeadersServerProtocolHandler GET_HEADERS\n"
              + "HeadersServerProtocolHandler HANDLE_MESSAGE_INBOUND\n"
              + "HeadersServerProtocolHandler CHECKED_HEADER "+acoh2+"\n"
              + "HeadersServerProtocolHandler CHECKED_HEADER "+acoh5+"\n"
              + "HeadersServerProtocolHandler REMOVED_HEADER "+acoh2+"\n"
              + "HeadersServerProtocolHandler ADDED_HEADER "+acoh6+"\n"
              + "HeadersServerLogicalHandler HANDLE_MESSAGE_INBOUND\n"
              + "HeadersServerLogicalHandler CHECKED_HEADER "+acoh5+"\n"
              + "HeadersServerLogicalHandler CHECKED_HEADER "+acoh6+"\n"
              + "HeadersServerLogicalHandler REMOVED_HEADER "+acoh5+"\n"   // message manipulated after this action
              + "HeadersServerLogicalHandler REMOVED_HEADER "+acoh6+"\n"   // throws protocol exception
              + "HeadersServerLogicalHandler HeadersServerLogicalHandler throwing protocol exception as instructed\n"
              // server outbound
              + "HeadersServerProtocolHandler HANDLE_FAULT_OUTBOUND\n"
              + "HeadersServerProtocolHandler ADDED_HEADER "+acoh5+"\n"
              + "HeadersServerLogicalHandler CLOSE\n"
              + "HeadersServerProtocolHandler CLOSE\n"  ;
              
            System.out.println("client expected:\n" + expected_client_calls);
            System.out.println("client actual:\n" + cl_log);
            System.out.println("\n\n server expected:\n" + expected_server_calls); 
            System.out.println("server actual:\n" + svr_log);
            
            boolean fail = false;
            if(expected_client_calls.compareTo(cl_log) != 0 ){
                System.out.println("client log does not match");
                fail = true;
            }
            if(!server.getNode().getMachine().getOperatingSystem().equals(com.ibm.websphere.simplicity.OperatingSystem.ZOS) && expected_server_calls.compareTo(svr_log) != 0 ){ // not valid on Z
                System.out.println("server log does not match");
                fail = true;
            }
            // explanation, expected, actual
            assertEquals("the expected message from the handler was not received", "I don't like 66", e.getMessage());
            assertFalse("server or client log does not match, see systemout", fail );
            
        }
        System.out.println("----------- test passed -----------------------");
    }	
	
	
	
    /**
     * tell the outermost client protocol handler to throw a fault.
     * (By sending the "throw fault" string on a header)
     * This should reverse the message flow, check that we can
     * get the headers map object, and that it does not have
     * any outbound headers in it. 
     * @throws Exception
     */
    public void testFaultPath() throws Exception {
        try {
            System.out.println("----------------------------------");
            System.out.println("test: " + getName());
    
            // create the client dynamically
           // HeadersHandlerService service = new HeadersHandlerService();
            // Liberty does not support SEI.wsdl wsdl URL style yet
            //URL wsdlLocation = new URL(axisEndpoint + "/HeadersHandlerService.wsdl");
            URL wsdlLocation = new URL(axisEndpoint + "?wsdl");
            HeadersHandlerService service = new HeadersHandlerService(wsdlLocation,
                    new QName("http://org/test/headershandler", "HeadersHandlerService"));
            HeadersHandlerPortType proxy = service.getHeadersHandlerPort();
            BindingProvider p = (BindingProvider) proxy;
            Map<String, Object> requestCtx = p.getRequestContext();
            
            requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, axisEndpoint);
            
            /*
             * add several headers by way of HeadersAdapter property
             */
            String acoh1, acoh2, acoh3, acoh4, acoh5;
            SOAPFactory sf = SOAPFactory.newInstance();
            try {
                Map<QName, List<String>> requestHeaders = new HashMap<QName, List<String>>();
                
                // QName used here should match the key for the list set on the requestCtx
                acoh1 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL1);
                
                // QName used here should match the key for the list set on the requestCtx
                acoh2 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.THROW_FAULT);
                
                // QName used here should match the key for the list set on the requestCtx
                acoh3 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL3);
                
                // QName used here should match the key for the list set on the requestCtx
                acoh4 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);
                
                // create additional header strings that will need to be checked:
                
                // use small message for now
                acoh5 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH3_HEADER_QNAME, TestHeaders.CONTENT_LARGE);
                
                
                List<String> list1 = new ArrayList<String>();
                list1.add(acoh1);
                list1.add(acoh2);
                
                List<String> list2 = new ArrayList<String>();
                list2.add(acoh3);
                list2.add(acoh4);
                
                // put both lists into the map.
                requestHeaders.put(TestHeaders.ACOH1_HEADER_QNAME, list1);
                requestHeaders.put(TestHeaders.ACOH2_HEADER_QNAME, list2);
                
                // put the headers into the message - it's magic!
                
                // the HeadersHandlerPortType class contains a handlerchain annotation
                // which will apply the client side handlerchain specified in HeadersClientHandler.xml 
                requestCtx.put(Constants.JAXWS_OUTBOUND_SOAP_HEADERS, requestHeaders);
            } catch (Throwable e) {
                fail(e.getMessage());  //we're toast.
                return;
            }
            
            // some handlers decrement the value, so we can confirm SOAP body manipulation does not corrupt the headers
            int numOfHandlerHitsInFlow = 1;
    
            // *********************************
            // ****** call the service *********
            // *********************************
            System.out.println("calling the service");
            int intParam1 = 10;
            int intParam2 = 10;
            try{
                int total = proxy.headersHandler(intParam1, intParam2);
            } catch( Exception e ){
                System.out.println("caught expected exception ");
            }
         
            
            // check that the changes detected and logged by the handlers are what we expected. 
            System.out.println("checking client side handler log");
            String log = readLogFile(filelognameclient);
            // we look for these in sequence, but allow additional content as well.
            String [] expected = {
                    "HeadersClientProtocolHandler2 HeadersCleintProtocolHandler2 throwing fault as instructed",
                    "HeadersClientProtocolHandler HANDLE_FAULT_INBOUND",
                    "HeadersClientLogicalHandler HANDLE_FAULT_INBOUND"};
                 
            // write to systemout for debug,  avoid junit alteration of output
            System.out.println("====== expected in sequence: ======\n");
            for(int i=0; i<expected.length; i++){
                System.out.println(expected[i]);
            }
                    
            System.out.println("\n\n\n =======actual:======== \n" + log);
            
            boolean resultOk = checkForStringsInSequence(expected, log);
    
            assertTrue("log did not contain correct strings in sequence, see systemout", resultOk);
            System.out.println("-----------test passed! -----------------------");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public boolean checkForStringsInSequence(String []expected, String actual){
        for(int i=0, offset=0; i <expected.length; i++){
            offset = actual.indexOf(expected[i],offset);
            if (offset <0 ) {
                System.out.println("expected string not found in sequence: "+expected[i]);
                return false;
            }
        }
        return true;
    }
	
    /**
     * A "33" is passed as a method arg., this will cause the 
     * HeadersServerLogicalHandler to return false, thus reversing the flow
     * while processing the inbound message.  In that situation, the request 
     * message gets "reversed" and becomes the response message, and the
     * headers on the original request message should be preseved and returned.
     *  
     * The test checks for the correct flow and message content.
     * @throws Exception
     */
    public void testHeadersHandlerServerInboundFlowReversal() throws Exception {
        System.out.println("----------------------------------");
        System.out.println("test: " + getName());
        
        // for scqos, when the request message gets reversed and goes back as a response,
        // the scqos global handlers are not amused, so let's not go there. 
        if (isSCQOS || isRMQOS ){
            System.out.println("this test is not valid with a qos, skipping");
            return;
        }

        // Liberty does not support SEI.wsdl wsdl URL style yet
        //URL wsdlLocation = new URL(axisEndpoint + "/HeadersHandlerService.wsdl");
        URL wsdlLocation = new URL(axisEndpoint + "?wsdl");
        HeadersHandlerService service = new HeadersHandlerService(wsdlLocation,
                new QName("http://org/test/headershandler", "HeadersHandlerService"));
        //HeadersHandlerService service = new HeadersHandlerService();
        HeadersHandlerPortType proxy = service.getHeadersHandlerPort();
        BindingProvider p = (BindingProvider) proxy;
        Map<String, Object> requestCtx = p.getRequestContext();

        requestCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, axisEndpoint);

        /*
         * add several headers by way of HeadersAdapter property
         */
        String acoh1, acoh2, acoh3, acoh4, acoh5, acoh6;
        SOAPFactory sf;
        try {
            sf = SOAPFactory.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        Map<QName, List<String>> requestHeaders = new HashMap<QName, List<String>>();

        // QName used here should match the key for the list set on the requestCtx
        acoh1 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL1);

        // QName used here should match the key for the list set on the requestCtx
        acoh2 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH1_HEADER_QNAME, TestHeaders.CONTENT_SMALL2);

        // QName used here should match the key for the list set on the requestCtx
        acoh3 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL3);

        // QName used here should match the key for the list set on the requestCtx
        acoh4 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH2_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);

        // create additional header strings that will need to be checked:
        acoh5 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH3_HEADER_QNAME, TestHeaders.CONTENT_LARGE);
        acoh6 = TestHeaders.createHeaderXMLString(TestHeaders.ACOH4_HEADER_QNAME, TestHeaders.CONTENT_SMALL4);

        List<String> list1 = new ArrayList<String>();
        list1.add(acoh1);
        list1.add(acoh2);

        List<String> list2 = new ArrayList<String>();
        list2.add(acoh3);
        list2.add(acoh4);

        requestHeaders.put(TestHeaders.ACOH1_HEADER_QNAME, list1);
        requestHeaders.put(TestHeaders.ACOH2_HEADER_QNAME, list2);
        
      
        requestCtx.put(Constants.JAXWS_OUTBOUND_SOAP_HEADERS, requestHeaders);

        int intParam1 = 10;
        int intParam2 = 33;
        
        // qos's need these next two
        requestCtx.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
        requestCtx.put(BindingProvider.SOAPACTION_URI_PROPERTY, "headersHandler");
            
        try {
            int total = proxy.headersHandler(intParam1, intParam2);
        } catch (Exception e) {
            /*
             * I tried to give enough info below in the expected_calls list so you can tell what's
             * being tested without having to look at handler code.  All header manipulation is
             * done by SOAPHeadersAdapter.
             * 
             * TODO: I would very much like to have done some other means of
             * header manipulation, but the Axis2 SAAJ module is lacking necessary implementation
             * to do this with any reliability.
             */
            
        
            String log = readLogFile(filelognameserver);
            String expected_calls = ""
            // client outbound
 
                    // server inbound
            //+ "HeadersServerProtocolHandler GET_HEADERS\n"
            + "HeadersServerProtocolHandler HANDLE_MESSAGE_INBOUND\n"
            + "HeadersServerProtocolHandler CHECKED_HEADER "+acoh2+"\n"
            + "HeadersServerProtocolHandler CHECKED_HEADER "+acoh5+"\n"
            + "HeadersServerProtocolHandler REMOVED_HEADER "+acoh2+"\n"
            + "HeadersServerProtocolHandler ADDED_HEADER "+acoh6+"\n"
            + "HeadersServerLogicalHandler HANDLE_MESSAGE_INBOUND\n"
            + "HeadersServerLogicalHandler CHECKED_HEADER "+acoh5+"\n"
            + "HeadersServerLogicalHandler CHECKED_HEADER "+acoh6+"\n"
            + "HeadersServerLogicalHandler REMOVED_HEADER "+acoh5+"\n"   // message manipulated after this action
            + "HeadersServerLogicalHandler REMOVED_HEADER "+acoh6+"\n"   // returns false
            + "HeadersServerLogicalHandler REMOVED_HEADER small content 3\n"
            //  returns false here and flow reverses
            // the request message flows back as a response message
            + "HeadersServerProtocolHandler HANDLE_MESSAGE_OUTBOUND\n"
            + "HeadersServerProtocolHandler ADDED_HEADER "+acoh1+"\n"
            
            + "HeadersServerProtocolHandler CHECKED_HEADER "+acoh1+"\n"
            + "HeadersServerProtocolHandler REMOVED_HEADER "+acoh1+"\n"
            + "HeadersServerProtocolHandler ADDED_HEADER "+acoh5+"\n"
            + "HeadersServerLogicalHandler CLOSE\n"
            + "HeadersServerProtocolHandler CLOSE\n";
           
            
            String cl_log = readLogFile(filelognameclient);
            String expected_client_calls =
            "HeadersClientLogicalHandler HANDLE_MESSAGE_OUTBOUND\n"
            + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh1+"\n"
            + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh2+"\n"
            + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh3+"\n"
            + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh4+"\n"
            + "HeadersClientLogicalHandler REMOVED_HEADER "+acoh1+"\n"
            + "HeadersClientLogicalHandler REMOVED_HEADER "+acoh3+"\n"
            + "HeadersClientLogicalHandler REMOVED_HEADER "+acoh4+"\n"   // message manipulated after this action
            + "HeadersClientProtocolHandler HANDLE_MESSAGE_OUTBOUND\n"
            + "HeadersClientProtocolHandler CHECKED_HEADER "+acoh2+"\n"                   
            + "HeadersClientProtocolHandler ADDED_HEADER "+acoh5+"\n"
            + "HeadersClientProtocolHandler ADDED_HEADER "+acoh3+"\n"
            + "HeadersClientProtocolHandler2 HANDLE_MESSAGE_OUTBOUND\n"
            + "HeadersClientProtocolHandler2 CHECKED_HEADER "+acoh2+"\n"
            + "HeadersClientProtocolHandler2 CHECKED_HEADER "+acoh5+"\n"
            + "HeadersClientProtocolHandler2 CHECKED_HEADER "+acoh3+"\n"
            + "HeadersClientProtocolHandler GET_HEADERS\n"
            + "HeadersClientProtocolHandler2 GET_HEADERS\n"
            // client inbound
            + "HeadersClientProtocolHandler2 HANDLE_MESSAGE_INBOUND\n"
            + "HeadersClientProtocolHandler2 CHECKED_HEADER "+acoh5+"\n"
            + "HeadersClientProtocolHandler2 ADDED_HEADER "+acoh3+"\n"
            + "HeadersClientProtocolHandler HANDLE_MESSAGE_INBOUND\n"
            + "HeadersClientProtocolHandler CHECKED_HEADER "+acoh5+"\n"
            + "HeadersClientProtocolHandler CHECKED_HEADER "+acoh3+"\n"
            + "HeadersClientProtocolHandler REMOVED_HEADER "+acoh5+"\n"
            + "HeadersClientProtocolHandler ADDED_HEADER "+acoh4+"\n"
            + "HeadersClientLogicalHandler HANDLE_MESSAGE_INBOUND\n"
            + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh3+"\n"
            + "HeadersClientLogicalHandler CHECKED_HEADER "+acoh4+"\n"
            + "HeadersClientLogicalHandler REMOVED_HEADER "+acoh3+"\n"
            + "HeadersClientLogicalHandler REMOVED_HEADER "+acoh4+"\n"
            + "HeadersClientProtocolHandler2 CLOSE\n"
            + "HeadersClientProtocolHandler CLOSE\n"
            + "HeadersClientLogicalHandler CLOSE\n";
                
            System.out.println("client expected:\n" + expected_client_calls);
            System.out.println("client actual:\n" + cl_log);
            System.out.println("\n\n server expected:\n" + expected_calls); 
            System.out.println("server actual:\n" + log);
            
            boolean fail = false;
            if(expected_client_calls.compareTo(cl_log) != 0 ){
                System.out.println("client log does not match");
                fail = true;
            }
            if(!server.getNode().getMachine().getOperatingSystem().equals(com.ibm.websphere.simplicity.OperatingSystem.ZOS) && expected_calls.compareTo(log) != 0 ){ // not valid on Z
                System.out.println("server log does not match");
                fail = true;
            }
            // explanation, expected, actual        
            assertFalse("server or client log does not match, see systemout", fail );
            
            assertEquals("I don't like 33", e.getMessage());
            
        }
        System.out.println("-------------- test passed --------------------");
    }
    
    
    /*
     * A callback implementation that can be used to collect the exceptions
     */
    class HeadersHandlerAsyncCallback implements AsyncHandler<HeadersHandlerResponse> {

        private Exception exception;
        private int retVal;

        public void handleResponse(Response<HeadersHandlerResponse> response) {
            try {
                System.out.println("HeadersHandlerAsyncCallback.handleResponse() was called");
                HeadersHandlerResponse r = response.get();
                System.out.println("No exception was thrown from Response.get()");
                retVal = r.getReturn();
            } catch (Exception e) {
                System.out.println("An exception was thrown in our callback handler: " + e.getClass());
                e.printStackTrace(System.out);
                exception = e;
            }
        }

        public int getResponseValue() {
            return retVal;
        }

        public Exception getException() {
            return exception;
        }
    }	
	
    /**
     * read the handler log files. 
     * We'll need to filter out messages that  count strings when qos' are active, as
     * qos's  change the counts and render the original test invalid.
     * @param filename
     * @return
     */
    private String readLogFile(String filename)throws Exception  {
          System.out.println("reading "+ filename);
          // deal with remote file, argh. 
          // trying to avoid refactoring all the tests for this. 
          if (filename.contains("Server") && serverIsRemote ){
              Machine remotem = TopologyDefaults.getDefaultAppServer().getNode().getMachine();
              Machine localm = TopologyDefaults.getRootServer().getNode().getMachine();
              RemoteFile localf = new RemoteFile(localm,"./ServerHeadersHandlerTests.log");
              RemoteFile serverf = new RemoteFile(remotem,filename); 
              localf.copyFromSource(serverf);
              filename = "./ServerHeadersHandlerTests.log";
              
          }
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader inputStream = new BufferedReader(fr);
            String line = null;
            String ret = null;
            String filterout1 = "expected size";
            String filterout2 = "HeadersClientProtocolHandler2: List of headers under QName {http://acoh3ns}acoh3 is missing.";
            while ((line = inputStream.readLine()) != null) {
                if (ret == null) {
                    ret = "";
                }
                // qos tests will have different numbers of headers, so we need to
                // ignore the messages about the expected number of headers, which do not account for QOS. 
                if(filterLogsForQos && (line.contains(filterout1)|| line.contains(filterout2))){
                   continue; 
                }
                ret = ret.concat(line + "\n");
            }
            fr.close();
            return ret;
        } catch (FileNotFoundException fnfe) {
            // it's possible the test does not actually call any handlers and therefore
            // no file would have been written.  The test should account for this by
            // assertNull on the return value from here
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail(ioe.getMessage());
        }
        return null;
    }

}
