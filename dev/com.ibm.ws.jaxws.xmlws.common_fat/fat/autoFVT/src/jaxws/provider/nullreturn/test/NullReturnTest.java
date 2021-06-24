//
// @(#) 1.3 autoFVT/src/jaxws/provider/nullreturn/test/NullReturnTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/19/11 12:17:51 [8/8/12 06:57:44]
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
// 06/24/2010 btiffany                    New File
// 03/09/2011 jtnguyen  690107.1          Change to make V8 work the same as V7 after 690107                
// 12/06/2011 jtnguyen  724041            Change jar name from 8.0 to 8.5
//

package jaxws.provider.nullreturn.test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.Enumeration;

import jaxws.provider.nullreturn.client.PostMsgSender;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.websphere.simplicity.WsadminConnectionOptions;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

import javax.xml.transform.dom.DOMSource;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


/**
 * This test will check jaxws specification compliance for sec 5.1.1., regarding
 * provider impls not being required to return a response when the response is null.
 * 
 * (In that case, it behaves as a one-way and returns an ack, only unlike a one-way,
 * it runs inbound handlers first)
 * 
 * For compatibility with v7 users, we have a flag that forces the old behavior, 
 * (returning an empty soap body). We test that it works. 
 * 
 * For the case where a wsdl is packed with the app and the wsdl contains a response,
 * we adhere to the wsdl contract regardless of the compatibility flag setting.
 * 
 * This feature is also being backported to v7 as an apar, and should be effective on 
 * fp13.  In that case, the default will be reversed, legacy mode is the v7 default. 
 * 
 * 
 * TEST MECHANICS NOTE:   
 *  NullReturnLegacyModeTest extends this class  and runs the legacy tests.  The
 *  suite() method is customized to control which tests to run. This class runs
 *  every test method that does NOT contain "legacy" in the method name.   
 * 
 * 
 * Since: version 8  Also present in v7 at fp13+  , apar PM16015
 * RTC Story: 13351, task 23021, feature F743-23021
 *  
 *
 * since 690107: now the logacy mode(false) is the default. so both V7 and V8 will work the same:  
 *   - when there is no WSDL, when the response is null, it will return an empty soap body and return code = 200
 *   - V8 script is no longer needed
 */
public class NullReturnTest extends FVTTestCase {
    static Cell cell = null;   
    static File enableScript = null;
    static File disableScript = null;

    private static IAppServer server = QueryDefaultNode.defaultAppServer;
    private static String testMachine = server.getMachine().getHostname();    
    private  static String defaultPort =  (server.getPortMap().get(Ports.WC_defaulthost)).toString();
    private static String hostAndPort = "http://"+testMachine + ":"+ defaultPort ;
    
    private static String postSoapaction = "";
    private int postTimeoutSec = 30;
    private boolean postIgnoreContents = false;
  
    private static int numtests = 0;     // work around framework limitation
    private static int numtestsrun = 0;    
    
    
    // the string we'll use for all requests where we want an empty/null response back from the service 
    private static String nullRequestMsg =   "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsdl=\"http://wsdl.server.nullreturn.provider.jaxws/\">" +
    "<soapenv:Header/><soapenv:Body><wsdl:echo><arg0>yes_null</arg0></wsdl:echo> </soapenv:Body></soapenv:Envelope>";
    
    // send this to get back a non-null response
    private static String notNullRequestMsg =   "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsdl=\"http://wsdl.server.nullreturn.provider.jaxws/\">" +
    "<soapenv:Header/><soapenv:Body><wsdl:echo><arg0>not_null</arg0></wsdl:echo> </soapenv:Body></soapenv:Envelope>";
    
 // the string we'll use for all requests where we want an empty/null response back from the service 
    private static String nullRequestNoResponseMsg =   "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsdl=\"http://wsdlnoresp.server.nullreturn.provider.jaxws/\">" +
    "<soapenv:Header/><soapenv:Body><wsdl:echo><arg0>yes_null</arg0></wsdl:echo> </soapenv:Body></soapenv:Envelope>";
    
    // send this to get back a non-null response
    private static String notNullRequestNoResponseMsg =   "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsdl=\"http://wsdlnoresp.server.nullreturn.provider.jaxws/\">" +
    "<soapenv:Header/><soapenv:Body><wsdl:echo><arg0>not_null</arg0></wsdl:echo> </soapenv:Body></soapenv:Envelope>";
  
    
    
    
    // for debug only
    public static void main(String [] args) throws Exception {
       // new NullReturnTest().testdefaultsNoWsdl();
        TestRunner.run(suite());
    }
    
    
    /**
     * payload and message -based providers that return null are invoked.
     * The return message should not contain a soap envelope at all, and should have an http 202 header.
     * 
     * When an http202 is received, there are no other headers in the response, except maybe
     * trnafer-encoding, so we don't need to inspect them. 
     * @throws Exception
     */
    public void testNoWsdl() throws Exception {    
         // http://localhost:9080/jaxws/nullprovidernowsdl/ProvMessageImplService
         // http://localhost:9080/jaxws/nullprovidernowsdl/ProvPayloadImplService
        //                                          wsdl
        //                                          wsdlnoresp
        
        
        String url = hostAndPort + "/jaxws/nullprovidernowsdl/ProvMessageImplService";        
        PostMsgSender sender = new PostMsgSender();
        
        System.out.println("sending to Message-based provider");
        System.out.println("received response: " + sender.postToURL(url, nullRequestMsg, postSoapaction, postTimeoutSec, postIgnoreContents));
        assertTrue("did not get expected http response code of 202", sender.responseCode == 202);              
        assertTrue("did not get empty response body", sender.response.length()==0);

     // now the payload provider, should be no different
        url = hostAndPort + "/jaxws/nullprovidernowsdl/ProvPayloadImplService";
        
        System.out.println("sending to Payload-based provider");
        System.out.println("received response: " + sender.postToURL(url, nullRequestMsg,  postSoapaction, postTimeoutSec, postIgnoreContents));
        
        
        assertTrue("did not get expected http response code of 202", sender.responseCode == 202);
        assertTrue("did not get empty response body", sender.response.length()==0);
        


    }
    
    /**
     * with the legacy mode jvm property set, a null return should 
     * return an http 202 and an empty soap body.      
     * @throws Exception
     */
    public void testLegacyModeNoWsdl() throws Exception { 
        String url = hostAndPort + "/jaxws/nullprovidernowsdl/ProvMessageImplService";        
        PostMsgSender sender = new PostMsgSender();
        System.out.println("sending to Message-based provider");
        System.out.println("received response: " + sender.postToURL(url, nullRequestMsg, postSoapaction, postTimeoutSec, postIgnoreContents));
        assertTrue("did not get expected http response code of 202", sender.responseCode == 202);
        assertTrue("did not get empty response body", sender.response.length()==0);    
    }
     
    
    /**
     * a wsdl with no response  should return an ack regardless of mode
     * 
     * Non-null responses are covered in other provider test packagse.
     * @throws Exception
     */
    public void testLegacyModeWsdlNoResp() throws Exception {  
        testWsdlNoResp();        
    }
    
    /**
     * a provider with no response in wsdl should return an ack, regardless of mode.
     * @throws Exception
     */
    public void testWsdlNoResp() throws Exception {  
        String url = hostAndPort + "/jaxws/nullproviderwsdlnoresp/ProvMessageImplService";        
        PostMsgSender sender = new PostMsgSender();
        System.out.println("sending to Message-based provider");
        System.out.println("received response: " + sender.postToURL(url, nullRequestNoResponseMsg, postSoapaction, postTimeoutSec, postIgnoreContents));
        assertTrue("did not get expected http response code of 202", sender.responseCode == 202);
        assertTrue("did not get empty response body", sender.response.length()==0);
        
        // now the payload provider, should be no different
        url = hostAndPort + "/jaxws/nullproviderwsdlnoresp/ProvPayloadImplService";
        System.out.println("sending to Payload-based provider");
        System.out.println("received response: " + sender.postToURL(url, nullRequestNoResponseMsg,  postSoapaction, postTimeoutSec, postIgnoreContents));
        assertTrue("did not get expected http response code of 202", sender.responseCode == 202);
        assertTrue("did not get empty response body", sender.response.length()==0); 
        
        // what happens when a provider returns a non-empty response, in disagreement with the wsdl?
        // now the payload provider, should be no different
        System.out.println("trying mismatch.  Wsdl= no response, provider returns non-empty response");
        url = hostAndPort + "/jaxws/nullproviderwsdlnoresp/ProvPayloadImplService";
        System.out.println("sending to Payload-based provider");
        System.out.println("received response: " + sender.postToURL(url, notNullRequestNoResponseMsg,  postSoapaction, postTimeoutSec, postIgnoreContents));
        assertTrue("did not get expected http response code of 202", sender.responseCode == 202);
        assertTrue("did not get empty response body", sender.response.length()==0); 
    }
    
    /**
     * a provider with a response in wsdl should always return a
     * soap envelope, regardless of the setting of legacy mode.  
     * @throws Exception
     * 
     * todo: This might have problems if the hand-coded response doesn't match the wsdl.
     */
    public void testNotEmptyResponseWsdl() throws Exception {   
        System.out.println("testing message provider");
        String url = hostAndPort + "/jaxws/nullproviderwsdl/ProvMessageImplService";        
        PostMsgSender sender = new PostMsgSender();
        System.out.println("sending to Message-based provider");
        System.out.println("received response: " + sender.postToURL(url, notNullRequestMsg, postSoapaction, postTimeoutSec, postIgnoreContents));
        assertTrue("did not get expected http response code of 200", sender.responseCode == 200);
        assertTrue("did not get non-empty response body", sender.response.length()>=0);
           
        
        // change so provider returns null, we should get an empty soap body back 
        System.out.println("trying mismatch.  Wsdl= response, provider returns null");
        url = hostAndPort + "/jaxws/nullproviderwsdl/ProvMessageImplService";
        System.out.println("sending to Message-based provider");
        System.out.println("received response: " + sender.postToURL(url, nullRequestMsg, postSoapaction, postTimeoutSec, postIgnoreContents));
        assertTrue("did not get expected http response code of 202", sender.responseCode == 202);
        assertTrue("did not get non-empty response body", sender.response.length()==0);
        System.out.println("passed");
        
    }
    
    // same as test it calls, but will run when server is in legacy mode. 
    public void testLegacyModeNotEmptyResponseWsdl() throws Exception {
        testNotEmptyResponseWsdl();
    }
    
    /**
     * a provider that returns a message sometimes and an ack other times
     * should not cause problems for the runtime. 
     * We'll do a lot of mixed calls to see if we can exhaust ports or any other resource. 
     * @throws Exception
     */
    public void testMixedResponses() throws Exception {          
        PostMsgSender sender = new PostMsgSender();
        String url1 = hostAndPort + "/jaxws/nullprovidernowsdl/ProvPayloadImplService";
        String url2 = hostAndPort + "/jaxws/nullprovidernowsdl/ProvMessageImplService";
        String url  = null;
        int i = 0;
        boolean usenull = false;
        String msg = null;
        while (i++ <500){
            usenull = (Math.random()>0.5) ?  true : false;   // use null about half the time
            msg = usenull ? nullRequestMsg : nullRequestMsg;
            url     = (Math.random()>0.5) ?  url1 : url2;    // mix and match payload and message services
          
            System.out.println("pass: "+ i + " requesting null: "+ usenull);
            try{ 
                sender.postToURL(url, msg, postSoapaction, postTimeoutSec, postIgnoreContents);    
                assertTrue("did not get expected http response code of 202", sender.responseCode == 202);
                assertTrue("did not get an empty response body", sender.response.length() == 0);   
                    
            // for brevity, let's only print the response if we're about to fail
            }catch (junit.framework.AssertionFailedError e){
                System.out.println("received incorrect response: " + sender.response); 
                throw e;
            }
            
        }
        
        
    }
    
    
    /**
     * a provider that returns a message sometimes and an ack other times
     * should not cause problems for the runtime. 
     * @throws Exception
     * 
     * combined with above test.
     */
    public void _testMessageMixedResponses() throws Exception {        
    }
    
    /**
     * if we have a way to set legacy mode with a deployment descriptor,
     * so we can do it on a per-service or per-module basis, we need to check that. 
     * 
     * We don't have that feature, so we don't have that test.
     * 
     * @throws Exception
     */
    public void _testDeploymentDescriptorElement() throws Exception {        
    }
    
    /**
     * this test may be difficult to implement but it would be good to check
     * that we don't break QOS's when a provider responds with an Ack.
     * For one thing, we want to be sure the QOS handler got closed. 
     * @throws Exception
     */
    public void _testQOS() throws Exception {        
    }
    
   
    
    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws Exception{ 
    	super.suiteSetup(cr);
    	System.out.println("suiteSetup called");
    }
    
    //   our nonportable test teardown method
    protected void suiteTeardown() throws Exception {
    	super.suiteTeardown();
        System.out.println("============== suiteTeardown() called ============");       
    }
    
    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws Exception {
    }
    
    // make sure everything is running at the end of each test
    public void tearDown()throws Exception{
        // suiteTeardown isn't working because we are manually manipulating the 
        // number of tests in the suite, so trigger it this way
        numtestsrun++;       
        if(numtestsrun == numtests) {suiteTeardown();}
    }
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     * 
     * This method looks at the names of the tests when deciding which ones to add.
     * If we are in legacy mode, only the legacy tests are added
     * Otherwise, only the non-legacy tests are added.
     * 
     * This will allow us to override the testcase and change merely the legacy mode variable
     * to select a different set of tests to run after setting the legacy mode jvm prop on the server.
     * 
     */
    public static Test suite() {    
        System.out.println("selecting which tests to run, legacy mode or the others");
     
        TestSuite ts = new TestSuite(NullReturnTest.class);
        TestSuite ts2 = new TestSuite();
        Enumeration<Test> e = ts.tests();
        while(e.hasMoreElements()){
            Test t = e.nextElement();
            if( !t.toString().toLowerCase().contains("testlegacy") ){
                System.out.println("adding non-legacy mode test" + t.toString());
                ts2.addTest(t);
                numtests++;
            }
        }
        return ts2;
        
    }
}
