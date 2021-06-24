//
// @(#) 1.5 autoFVT/src/jaxws22/addressing/test/AddressingJsr224v22FactoryMethodsTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/13/11 12:29:48 [8/8/12 06:57:42]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 04/10/10 btiffany                    New File
// 04/13/11 btiffany                    add explanation when we fail with wrong exception received.
//                                           

package jaxws22.addressing.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.*;

import jaxws22.addressing.client.MessageCaptureHandler;
import jaxws22.addressing.server.*;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * This class will test that the addressing webservice feature can be
 * applied correctly using static factory methods, and that
 * conformance statements are met.
 * 
 * We already exercised most of the factory methods with MtomFeature, so we'll
 * just hit some common variations and cases particular to addressing, and since 
 * this is the last one to test, do a few combinations. 
 * 
 * 
 * 
 * Since: version 8 RTC Task 23607 
 */
public class AddressingJsr224v22FactoryMethodsTest extends FVTTestCase {
    static String hostAndPort = null;
    static String fvtdir = null;
    static String wsdldir = null; 
    static String unifyFaultsPropInitialValue = null;    

    /**
     * The main method. Only used for debugging.
     * 
     * @param args  The command line arguments
     */
    public static void main(String[] args) throws java.lang.Exception {
       //  TestRunner.run(suite());
        AddressingJsr224v22FactoryMethodsTest t = new AddressingJsr224v22FactoryMethodsTest("x");        
        //        t.testNoAddressing();
       // t.testNonAnonymous();
      //  t.testRequiredOn();
        //t.testGetPort3Arg1() ;
        //System.out.println("R0E1 - should pass -----------------");
        //t.testRequiredOffEnabledOn();
       t.suiteTeardown();
       
    }
    
    /**
     * 
     * check that we can disable addressing even though it's configured in the wsdl.
     * There should be no addressing headers in the outbound request.
     */
    public void testNoAddressing_ServiceOff() throws java.lang.Exception{
        String contextroot = "annoOnDDOff";  // this service has the  ability to return the request headers. 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
        // not enabled, required, non-anon.
        Echo port = s.getEchoPort(new AddressingFeature(false,true, AddressingFeature.Responses.NON_ANONYMOUS));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        String result = port.echo("testNoAddressing");
        System.out.println(result);
        result = port.echo("SEND_BACK_MY_HEADERS testNoAddressing");
        System.out.println(result);
        assertFalse("addressing was used in the request but should not have been", result.contains("header"));
    }
    
    /**
     * test that we can turn on the required attribute. 
     * Some sort of exception should be thrown because the response does not contain addressing headers. 
     * 
     * failing as of 4/28.
     */ 
    public void testRequiredOnEnabledOn_ServiceOff() throws java.lang.Exception{
        System.out.println("Addressing is set to required on the client. \n" +
                "A service without addressing is invoked.\n"+
                "An exception is expected because the response will not contain \n"+
                "the addressing headers required by the client");
        String contextroot = "annoOnDDOff";  // this service has the  ability to return the request headers. 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
        // enabled, required, all
        Echo port = s.getEchoPort(new AddressingFeature(true,true, AddressingFeature.Responses.ALL));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        addHandler((BindingProvider)port);
        
        try{        
            String result = port.echo("testRequiredOn");
            System.out.println(result);
        }catch( WebServiceException e ){
            System.out.println("Caught expected exception when invoking service without addressing ");
            assertTrue("wrong exception received", wasAddressingRequiredException(e));
            System.out.println("testRequiredOn passed");
            return;
        } catch(java.lang.Exception e){
            System.out.println("caught an unexpected exception:");
            e.printStackTrace(System.out);
            fail("caught an unexpected exception, see systemout.");
        }
        System.out.println("response from server was:\n" + MessageCaptureHandler.getInboundMsgAsString());
        fail("addressing was required by client but a message without addressing was accepted.");
    }
    
    /**
     * addressing is required on both sides, message exchange should succeed.
     * @throws java.lang.Exception
     */
    public void testRequiredOnEnabledOn_ServiceOn() throws java.lang.Exception{
        System.out.println("Addressing is set to required on the client. \n" +
                "A service with addressing is invoked and this should succeed.");                
        String contextroot = "annoOnNoDD";  
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
        // enabled, required, all
        Echo port = s.getEchoPort(new AddressingFeature(true,true, AddressingFeature.Responses.ALL));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        addHandler((BindingProvider)port);
        String result = null;
        try{ 
             result = port.echo("SEND_BACK_MY_HEADERS testRequiredOn_ServiceOn");
        } catch (java.lang.Exception e){
            System.out.println("caught an unexpected exception:");
            e.printStackTrace(System.out);
            fail("caught an unexpected exception when invoking service");
        }
        System.out.println("received response: "+result);       
        System.out.println("response from server was:\n" + MessageCaptureHandler.getInboundMsgAsString());
        //if we get here without an exception, so far so good.
        
        assertTrue("Problem, service did not return headers, so we should have thrown an exception", wasAddressingUsedinResponse());
        assertTrue("Problem, request did not contain headers, although they were enabled and required", result.contains("inbound header:"));
        
    }
    
    /**
     * check that this beahves the same as j2se, addressing headers should not be generated and not required
     * @throws java.lang.Exception
     */
    public void testRequiredOnEnabledOff() throws java.lang.Exception{
        String contextroot = "annoOnDDOff";  // this service has the  ability to return the request headers. 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoService.wsdl"));
        // enabled, required, all
        Echo port = s.getEchoPort(new AddressingFeature(false, true, AddressingFeature.Responses.ANONYMOUS));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        addHandler((BindingProvider)port);   
        String result = port.echo("SEND_BACK_MY_HEADERS testRequiredOffEnabledOn");
        assertFalse("addressing disabled on client but addressing headers were generated.",
                result.contains("wsa:Action"));
       
     
    }
    
    /**
     * check that this beahves the same as j2se, addressing headers should be generated 
     * by the  client, but not required
     * in response. 
     * @throws java.lang.Exception
     */
    public void testRequiredOffEnabledOn_ServiceOff() throws java.lang.Exception{
        String contextroot = "annoOnDDOff";  // this service has the  ability to return the request headers. 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
        // enabled, required, response
        Echo port = s.getEchoPort(new AddressingFeature(true,false, AddressingFeature.Responses.ALL));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        addHandler((BindingProvider)port);   
        String result = port.echo("SEND_BACK_MY_HEADERS testRequiredOffEnabledOn");
        assertTrue("addressing enabled on client but no addressing headers were generated.",
                result.contains("wsa:Action"));
       
              
    }
    
    /**
     * test that we can set the anonymous response attribute.
     * As far as we know, and to be consistent with the j2se, this should have no effect. 
     * This was worked into the tests above so these tests are not needed.
     * @throws java.lang.Exception
     */
    public void _testAnonymous() throws java.lang.Exception{
        fail("implement me");        
     }
    
    /**
     * test that we can set the non-anonymous response attribute.
     * As far as we know, and to be consistent with the j2se, this should have no effect.
     * This was worked into the tests above so these tests are not needed.
     * @throws java.lang.Exception
     */
    public void _testNonAnonymous() throws java.lang.Exception{
        fail("implement me");        
     }
    
    
    /**
     * specify a feature more than once with different parameters.
     * It's not clear what should happen here, but if we don't process it then the 
     * exception message should be meaningful.  Probably the last feature processed wins. 
     * There's no assert so this will always pass unless we throw an exception. 
     * 
     */
    public void testDuplicateFeatures() throws java.lang.Exception{
        String contextroot = "annoOnDDOff";  // this service has the  ability to return the request headers. 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
      
        Echo port = s.getEchoPort(new AddressingFeature(true,true, AddressingFeature.Responses.ALL), 
                new AddressingFeature(true,true, AddressingFeature.Responses.ALL),
                new AddressingFeature(true,false, AddressingFeature.Responses.ALL),
                new AddressingFeature(true,true, AddressingFeature.Responses.ALL),
                new AddressingFeature(false,true, AddressingFeature.Responses.NON_ANONYMOUS));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        addHandler((BindingProvider)port);
               
        String result = port.echo("testDuplicateFeatures");
        System.out.println(result);
        System.out.println("request headers:\n"+port.echo("SEND_BACK_MY_HEADERS"));
        
        // no exception, we passed. 
      
    }
    /**
     * specify multiple features and verify that they  don't get in the way.
     */
    public void testMultipleFeatures() throws java.lang.Exception{
        String contextroot = "annoOnDDOff";  // this service has the  ability to return the request headers. 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceInvalidRequiredBinding.wsdl"));
      
        Echo port = s.getEchoPort(new AddressingFeature(true,false, AddressingFeature.Responses.ALL), 
                new MTOMFeature(true, 1),
                new RespectBindingFeature(true));
                
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        addHandler((BindingProvider)port);
        
        // respectbinding should cause exception 
        try{
            String result = port.echo("multipleFeatures");
        } catch (WebServiceException e){
            System.out.println("caught expected exception:\n" + getStackTraceAsString(e));            
            assertTrue("wrong exception!", getStackTraceAsString(e).contains("@RespectBinding was enabled, but"));
        }
        
        // now turn it off and try again 
        port = s.getEchoPort(new AddressingFeature(true,false, AddressingFeature.Responses.ALL), 
                new MTOMFeature(true, 1),
                new RespectBindingFeature(false));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        addHandler((BindingProvider)port);
        String result = port.echo("SEND_BACK_MY_HEADERS");
        System.out.println("request headers:\n" + result);
        assertTrue("addressing should have been used but was not", result.contains("header"));
    }
    
    
    /**
     * this should be the same as no features, make sure we can process it.
     */
    public void testEmptyArrayOfFeatures() throws java.lang.Exception{
        String contextroot = "annoOnNoDD";  // this service has the  ability to return the request headers. 
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));

        WebServiceFeature[] wsfa = {};
        Echo port = s.getEchoPort(wsfa);
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        String result = port.echo("testNoAddressing");
        System.out.println(result);
        result = port.echo("SEND_BACK_MY_HEADERS testNoAddressing");
        System.out.println(result);
        assertTrue("addressing was specified in wsdl but not used in request", result.contains("header"));
    
        
    }
    
    /**
     * check the other getport methods just to be safe
     * @throws java.lang.Exception
     * 
     * failing on qq + Phil's patch. 
     */
    public void testGetPort3Arg1() throws java.lang.Exception{
        System.out.println("Addressing is set to required on the client. \n" +
                "A service without addressing is invoked.\n"+
                "An exception is expected because the response will not contain \n"+
                "the addressing headers required by the client");
        String contextroot = "annoOnDDOff";  // this service has the  ability to return the request headers. 
        QName portqn = new QName("http://server.addressing.jaxws22/", "EchoPort");            
        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
        
        
        Echo port = s. getPort(portqn, Echo.class, new AddressingFeature(true,true));
        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
        addHandler((BindingProvider)port);  // install the monitoring handler
        
        // should fail becuase response will not use addressing
        String result = null;
        try{
            result = port.echo("SEND_BACK_MY_HEADERS testGetPort3Arg1");
        } catch ( WebServiceException e ){
            System.out.println("Caught expected exception when non-addressing service when addressing required on client ");
            assertTrue("wrong exception received", wasAddressingRequiredException(e));
            return;
        }  
      
       System.out.println("request headers:\n" + result);
       System.out.println("response message\n:" + MessageCaptureHandler.getInboundMsgAsString());
       fail("did not receive expected exception");
        
    }

    /**
     * check the other getport methods just to be safe
     * @throws java.lang.Exception
     */
//    public void testGetPort3ArgEpr() throws java.lang.Exception{
//        System.out.println("Addressing is set to required on the client using \n" +
//                "The getport(epr, class, feature) api." +
//                "A service without addressing is invoked.\n"+
//                "An exception is expected because the response will not contain \n"+
//                "the addressing headers required by the client");
//        String contextroot = "annoOnDDOff";  // this service has the  ability to return the request headers. 
//        QName portqn = new QName("http://server.addressing.jaxws22/", "EchoPort");            
//        EchoService s = new EchoService(new URL("file:"+  wsdldir + "/EchoServiceRequiredAnon.wsdl"));
//        
//       
//        Echo port = s. getPort(portqn, Echo.class);
//        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));
//        
//        // we need to get the endpoint reference so we can use it to get the port again.
//        // Convoluted, but easiest way to test that api.        
//        EndpointReference epr = ((BindingProvider)port).getEndpointReference();
//        
//        
//        
//        // now try the api we intend to test
//        port = s.getPort(epr, Echo.class, new AddressingFeature(true,true));
//        updateEndpointURL(hostAndPort+"/" + contextroot + "/EchoService" , ((BindingProvider)port));  // ? - doesn't work.
//        addHandler((BindingProvider)port);  // install the monitoring handler
//        
//        // should fail becuase response will not use addressing
//        String result = null;
//        try{
//            result = port.echo("SEND_BACK_MY_HEADERS testGetPort3Arg1");
//        } catch ( WebServiceException e ){
//            System.out.println("Caught expected exception when non-addressing service when addressing required on client ");
//            assertTrue("wrong exception received", wasAddressingRequiredException(e));
//            return;
//        }  
//      
//       System.out.println("request headers:\n" + result);
//       System.out.println("response message\n:" + MessageCaptureHandler.getInboundMsgAsString());
//       fail("did not receive expected exception");
//        
//    }
    
    
    // =============== helper methods next ==================
    
    private boolean wasAddressingRequiredException(java.lang.Exception e) {
        boolean exceptionOK = false;
        String str1 = "A required header representing a Message Addressing Property is not present";
        String str2 = "A header representing a Message Addressing Property is not valid";
        String st = getStackTraceAsString(e);
        exceptionOK = (st.contains(str1) || st.contains(str2)) ;
                
        if( ! exceptionOK ){
             System.out.println("The wrong kind of exception was detected. \n" +
                                "The stack trace was expected to contain: \n" +
                                str1 + "\n" +
                                " or:  " + str2 + "\n" +
                                "but instead the stack trace was: \n\n" + 
                                st + "\n\n"
                               ); 
                                
        }        
        return exceptionOK;
    }    
    
    private String getStackTraceAsString(java.lang.Exception e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
    
    private boolean wasAddressingUsedinResponse(){
        return MessageCaptureHandler.getInboundMsgAsString().contains("http://www.w3.org/2005/08/addressing");
    }
    
    /**
     * update the endpoint url
     */
    private static void updateEndpointURL(String serviceURL, BindingProvider port){        
        Map<String, Object> rc = ((BindingProvider) port)
            .getRequestContext();         
         rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);        
    }
    
    /**
     * install a handler on a port.  We'll use the handler to capture the soap message.
     * Much easier than traffic monitoring, etc.   
     * @param port
     */
    private static void addHandler(BindingProvider port){
        // set binding handler chain
        Binding binding = ((BindingProvider)port).getBinding();

        // can create new list or use existing one
        List<Handler> handlerList = binding.getHandlerChain();

        if (handlerList == null) {
            handlerList = new ArrayList<Handler>();
            System.out.println("new list");
        }

        handlerList.add(new MessageCaptureHandler());  
        binding.setHandlerChain(handlerList);
        //System.out.println("handler added");
    }
    
 
 

    // ============== everything below here is junit stuff ==========================

    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws java.lang.Exception {
    }

    // make sure everything is running at the end of each test
    public void tearDown() throws java.lang.Exception {
    }

    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        super.suiteSetup(cr);
        System.out.println("suiteSetup() called");
        
    }

    
	static {

		try {

			String host = TopologyDefaults.getDefaultAppServer().getNode()
					.getHostname();
			String port = TopologyDefaults.getDefaultAppServer()
					.getPortNumber(PortType.WC_defaulthost).toString();

			hostAndPort = "http://" + host + ":" + port;
			System.out.println("hostAndPort = " + hostAndPort);

			fvtdir = AppConst.FVT_HOME;
			// debug hack:
			if (fvtdir == null | fvtdir.contains("null")) {
				fvtdir = "/test/wasx_kk1003.32/autoFVT";
			}
			wsdldir = fvtdir + "/src/jaxws22/addressing/client/wsdl";

		} catch (java.lang.Exception e) {
			// do nothing
		}

	}

    // our nonportable test teardownp method
    protected void suiteTeardown() throws java.lang.Exception {
        super.suiteTeardown();
        System.out.println("suiteTeardown() called");
        /*
        System.out.println("restoring jvm prop webservices.unify.faults to it's initial value");
        if( unifyFaultsPropInitialValue == null){
           System.clearProperty("webservices.unify.faults");
           
        } else {
           System.setProperty("webservices.unify.faults", unifyFaultsPropInitialValue);
        }
        */
        
    }
    
    /**
     * A constructor to create a test case with a given name.
     * 
     * @param name
     *            The name of the test case to be created
     */
    public AddressingJsr224v22FactoryMethodsTest(String name) {
        super(name);
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(AddressingJsr224v22FactoryMethodsTest.class);
    }


}
