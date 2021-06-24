//
// @(#) 1.2 autoFVT/src/jaxwsejbinwar/test/EjbInWarTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/3/10 13:54:35 [8/8/12 06:57:46]
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
// 07/10/10 btiffany    F743-31413       New File
// 08/03/10 btiffany    F743-31533       Add client ref tests
//

package jaxwsejbinwar.test;
//import  jaxwsejbinwar.server.*;
import java.net.URL;

import javax.xml.ws.soap.SOAPFaultException;

import jaxwsejbinwar.generatedclient.EjbInJarHelloService;
import jaxwsejbinwar.generatedclient.EjbInWarClassesHelloService;
import jaxwsejbinwar.generatedclient.EjbInWarLibHelloService;
import jaxwsejbinwar.generatedclient.ServletInWarClassesHelloService;
import jaxwsejbinwar.generatedclient.ServletInWarLibHelloService;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
  

/**
 * This class will test that jax-ws ejb beans can be packaged in a war file,
 * deployed and invoked successfully.  It will also check that service-refs in 
 * a war are usable by ejb and servlet-based beans. 
 *
 * 
 * Since: version 8
 * RTC Story: 28425, task 31413  (server side)  31513 (client side) 
 * 
 *  
 */
public class EjbInWarTest extends FVTTestCase {   
   
    private static String hostAndPort= null;
    
    // for debug, a method we can call outside junit. 
    static {
    	try {
        String host = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
        String port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();  
        hostAndPort  = "http://" + host + ":" + port ;
        System.out.println("hostAndPort = "+hostAndPort );
    	} catch (java.lang.Exception e) {
    		// do nothing
    	}
    }
    
    /** 
     * The main method.  Only used for debugging.
     * @param args The command line arguments
     */
    public static void main(String[] args) throws java.lang.Exception  {
       // TestRunner.run(suite());
        EjbInWarTest t = new EjbInWarTest("x");
        t.testServiceRefsInWebFragmentXml();
    }
    
    /**
     *  Simple case of single ejb-based and war-based services in WAR 
     *  Four services are present, two in web-inf/classes, and two in a jar file in web-inf /lib.
     *  We verify that the services can be invoked over the http protocol from a separate client.
     *   
     */
    public void testEjbsInWarCanBeInvokedByRemoteClients() throws java.lang.Exception {
        boolean failure = false;
        URL wsdlUrl = new URL(hostAndPort + "/ejbinwar/EjbInWarClassesHelloService?wsdl");
        String expected = "ejbinwarclasseshello hello invoked with arg: hello there";
        String actual = (new EjbInWarClassesHelloService(wsdlUrl)).getEjbInWarClassesHelloPort().hello("hello there");
        System.out.println("expected = " + expected + "\nactual = " + actual);
        if (actual.compareTo(expected)!= 0) {failure = true;}
       
        
        System.out.println("\n\n\n");
        wsdlUrl = new URL(hostAndPort + "/ejbinwar/EjbInWarLibHelloService?wsdl");
        expected = "ejbinwarlibhello hello invoked with arg: hello there";
        actual = (new EjbInWarLibHelloService(wsdlUrl)).getEjbInWarLibHelloPort().hello("hello there");
        System.out.println("expected = " + expected + "\nactual = " + actual);
        if (actual.compareTo(expected)!= 0) {failure = true;}
       
        
        System.out.println("\n\n\n");
        wsdlUrl = new URL(hostAndPort + "/ejbinwar/ServletInWarLibHelloService?wsdl");
        expected = "servletinwarlibhello hello invoked with arg: hello there";
        actual = (new ServletInWarLibHelloService(wsdlUrl)).getServletInWarLibHelloPort().hello("hello there");
        System.out.println("expected = " + expected + "\nactual = " + actual);
        if (actual.compareTo(expected)!=0) {failure = true;}
      
        System.out.println("\n\n\n");
        wsdlUrl = new URL(hostAndPort + "/ejbinwar/ServletInWarClassesHelloService?wsdl");
        expected = "servletinwarclasseshello hello invoked with arg: hello there";
        actual = (new ServletInWarClassesHelloService(wsdlUrl)).getServletInWarClassesHelloPort().hello("hello there");
        System.out.println("expected = " + expected + "\nactual = " + actual);
        if (actual.compareTo(expected)!= 0) {failure = true;}
        assertFalse("wrong message(s) received, see systemout", failure);
      
    }
    
    /**
     * test that clients within the ear can call the other services in the ear.
     * The callAnotherHello operation invokes a web service client, which calls the 
     * specified service, which then echoes back.   This just checks that nothings 
     * wrong with transport within the server before we start trying to use references. 
     * 
     * @throws java.lang.Exception
     */
    public void testAppsCanCallEachOtherUsingHTTP() throws java.lang.Exception {
        // the wsdl url's of each service.
        String siwl = hostAndPort+"/ejbinwar/ServletInWarLibHelloService?wsdl";
        String siwc = hostAndPort+"/ejbinwar/ServletInWarClassesHelloService?wsdl";
        String eiwl = hostAndPort+"/ejbinwar/EjbInWarLibHelloService?wsdl";
        String eiwc = hostAndPort+"/ejbinwar/EjbInWarClassesHelloService?wsdl";
        
        URL wsdlUrl = new URL(eiwc);
        String preface = "common client call to: ";
        String expected = preface + siwc;
        String actual = (new EjbInWarClassesHelloService(wsdlUrl)).getEjbInWarClassesHelloPort()
          .callAnotherHello(siwc);  // call servletinwarclasses
        System.out.println("expected = " + expected + "\nactual = " + actual);
        assertTrue("wrong message received, see systemout", actual.contains(expected));
        
        wsdlUrl = new URL(eiwl);
        expected = preface +  eiwc;
        actual = (new EjbInWarLibHelloService(wsdlUrl)).getEjbInWarLibHelloPort()
           .callAnotherHello(eiwc);  
        System.out.println("expected = " + expected + "\nactual = " + actual);
        assertTrue("wrong message received, see systemout", actual.contains(expected));
        
        wsdlUrl = new URL(siwl);
        expected = preface +  eiwc;
        actual = (new ServletInWarLibHelloService(wsdlUrl)).getServletInWarLibHelloPort()
         .callAnotherHello(eiwc);
        System.out.println("expected = " + expected + "\nactual = " + actual);
        assertTrue("wrong message received, see systemout", actual.contains(expected));
      
        wsdlUrl = new URL(siwc);
        expected = preface + eiwl;
        actual = (new ServletInWarClassesHelloService(wsdlUrl)).getServletInWarClassesHelloPort()
           .callAnotherHello(eiwl);
        System.out.println("expected = " + expected + "\nactual = " + actual);
        assertTrue("wrong message received, see systemout", actual.contains(expected));
    }
    
    
    
    /**
     * Assure that when we have an ear with a mix of ejb and servlet endpoints in ejb and web 
     * modules, our tooling, deployment and runtime can handle this mixture without error.
     * 
     * let's see if we can invoke the service that's in an ejb jar.  If so, it made it through
     * tooling and deployment. 
     * @throws Exception
     * 
     * Since this is an ejb in a jar without dd's, the rules for context root are a little different. 
     *
     */
    public void testMixOfWebServiceEndpointsInAnEar() throws java.lang.Exception {
        URL wu = new URL(hostAndPort+ "/ejbjar/EjbInJarHelloService?wsdl");
        String expected = "ejbinjarhello hello invoked with arg: greetings";
        String actual = (new EjbInJarHelloService(wu)).getEjbInJarHelloPort()
           .hello("greetings");
        System.out.println("expected = " + expected + "\nactual = " + actual);
        assertTrue("wrong message received, see systemout", actual.compareTo(expected)== 0);
        
    }
    
    /**
     * Test that service-refs declared in ejb-jar.xml which is packaged in a war can be used.
     *  
     * A services callAnotherUsingServiceRef method will be called.  That will cause it to 
     * invoke some other service via a reference that has been injected or looked up.  If all
     * is well, we'll get back a response from the service that was invoked by reference. 
     */
    public void testServiceRefsInEjbJarXmlUsedbyEJBs() throws java.lang.Exception{
        boolean failure = false;
        // this one checks a service ref in an ejb-jar.xml inside the web-inf directory
//      this one should get a ref to and call ejbinwarclasseshello.
        URL wsdlUrl = new URL(hostAndPort + "/ejbinwar/EjbInWarLibHelloService?wsdl");
        String expected = "ejbinwarclasseshello hello invoked with arg: common client reference call to ejbinwarclasses returns: hello there";
        String actual = (new EjbInWarLibHelloService(wsdlUrl)).getEjbInWarLibHelloPort().callAnotherUsingServiceRef("hello there");
        if (actual.compareTo(expected)!= 0) {failure = true;}
        System.out.println("expected = " + expected + "\nactual = " + actual);
       // assertTrue("wrong message received, see systemout", actual.compareTo(expected)== 0);
        assertFalse("wrong message(s) received, see systemout", failure);
        
        System.out.println("\n\n\n");
        
        // this one checks a service ref in an ejb-jar.xml inside the web-inf/lib/jar's meta-inf directory
        // this should not work as the only ejb-jar to be processed should be web-inf/ejb.jar.
        try{
            wsdlUrl = new URL(hostAndPort + "/ejbinwar/EjbInWarLibHelloService?wsdl");
            expected = "ejbinwarclasseshello hello invoked with arg: common client reference call to ejbinwarclasses returns: try second reference";
            actual = (new EjbInWarLibHelloService(wsdlUrl)).getEjbInWarLibHelloPort().callAnotherUsingServiceRef("try second reference");
            System.out.println("expected = " + expected + "\nactual = " + actual);
            
        } catch (Exception e){
             if (e.getMessage().contains("NameNotFoundException")){
                 System.out.println("caught expected exception ");
                 return;
             } else {
                 e.printStackTrace(System.out);
                 fail("did not catch expected exception, soapfaultexception with not found in context message ");
             }
            
        }
        fail("did not catch expected exception ");
        
    }
    
    // same as above, but the reference is used in a servlet.  Should work, it's just jndi code. 
    public void testServiceRefsInEjbJarXmlUsedbyServlets() throws java.lang.Exception{
        boolean failure = false;
        // this one checks a service ref in an ejb-jar.xml inside the web-inf directory
        //      this one should get a ref to and call ejbinwarclasseshello.
        URL wsdlUrl = new URL(hostAndPort + "/ejbinwar/ServletInWarClassesHelloService?wsdl");
        String expected = "ejbinwarclasseshello hello invoked with arg: common client reference call to ejbinwarclasses returns: from a servlet using a ejb serviceref: hello there";
        String actual = (new ServletInWarClassesHelloService(wsdlUrl)).getServletInWarClassesHelloPort().callAnotherUsingServiceRef("hello there");
        if (actual.compareTo(expected)!= 0) {failure = true;}
        System.out.println("expected = " + expected + "\nactual = " + actual);
        assertFalse("wrong message(s) received, see systemout", failure);        
    }

    /**
     * test an @Webserviceref annotation that injects a client ref without anything being in a deployment descriptor.
     * getEjbInWarClassesHelloPort().callAnotherUsingServiceRef will use this annotation in the ejbinwarclasses provider.
     * 
     * @throws java.lang.Exception
     */
    public void testWebServiceRefInjectionUsingAnnotationsInEjbsInWars() throws java.lang.Exception {
        boolean failure = false;
        URL wsdlUrl = new URL(hostAndPort + "/ejbinwar/EjbInWarClassesHelloService?wsdl");
        String expected = "servletinwarlibhello hello invoked with arg: hello there pure annotation injection";
        String actual = (new EjbInWarClassesHelloService(wsdlUrl)).getEjbInWarClassesHelloPort().callAnotherUsingServiceRef("hello there pure annotation injection");
        System.out.println("expected = " + expected + "\nactual = " + actual);
        if (actual.compareTo(expected)!= 0) {failure = true;}
        assertFalse("wrong message(s) received, see systemout", failure);
        
    }
    
    /**
     * Test that service-refs declared in web.xml can be used by an ejb based services 
     * 
     * EjbinWarLibHello will be called and will do a jndi lookup to ejbinwarclasses using a service ref
     * defined in web.xml.  We should get back the response from ejbinwarclasses.
     * 
     * @throws java.lang.Exception
     */
    public void testServiceRefsInWebXmlUsedByEjb() throws java.lang.Exception{
        
        // first, an ejb looks up another ejb via a service ref. 
        System.out.println("-- ejb to ejb call --");
        boolean failure = false;
        URL wsdlUrl = new URL(hostAndPort + "/ejbinwar/EjbInWarLibHelloService?wsdl");
        String expected = "ejbinwarclasseshello hello invoked with arg: common client reference "+
            "call to ejbinwarclasses returns: using web.xml ejb reference web.xml ejb reference";
        String actual = (new EjbInWarLibHelloService(wsdlUrl)).getEjbInWarLibHelloPort().callAnotherUsingServiceRef("web.xml ejb reference");
        if (actual.compareTo(expected)!= 0) {failure = true;}
        System.out.println("expected = " + expected + "\nactual  = " + actual + "\n");
        assertFalse("wrong message(s) received, see systemout", failure);
       
        // next we have the ejb in invoke a servlet in the same way.
        // ejbinwarclasses will lookup and invoke servletinwarlib
        System.out.println("-- ejb to servlet call --");
        wsdlUrl = new URL(hostAndPort + "/ejbinwar/EjbInWarLibHelloService?wsdl");
        expected = "servletinwarlibhello hello invoked with arg: common client reference "+
            "call to servletinwarlib returns: using web.xml servlet reference web.xml servlet reference";
        actual = (new EjbInWarLibHelloService(wsdlUrl)).getEjbInWarLibHelloPort().callAnotherUsingServiceRef("web.xml servlet reference");
        if (actual.compareTo(expected)!= 0) {failure = true;}
        System.out.println("expected = " + expected + "\nactual = " + actual + "\n");
        assertFalse("wrong message(s) received, see systemout", failure);
    }
    
    /**
     * Test that service-refs declared in web.xml can be used by a servlet-based webservice.
     * 
     * warclasseshelloservice will do a jndi lookup of warlibhelloservice using a reference defined
     * in web.xml.  If all goes well we will get the response from warlibhelloservice back.  
     * @throws java.lang.Exception
     */
    public void testServiceRefsInWebXmlUsedByServlet() throws java.lang.Exception{
        
        // first, a serlet looks up another servlet via a service ref. 
        System.out.println("--- servlet to servlet call ---");
        boolean failure = false;
        URL wsdlUrl = new URL(hostAndPort + "/ejbinwar/ServletInWarClassesHelloService?wsdl");
        String expected = "servletinwarlibhello hello invoked with arg: common client reference call "+
            "to servletinwarlib returns: from servletinwarclasses:web.xml servlet reference";
        String actual = (new ServletInWarClassesHelloService(wsdlUrl)).getServletInWarClassesHelloPort().callAnotherUsingServiceRef("web.xml servlet reference");
        if (actual.compareTo(expected)!= 0) {failure = true;}
        System.out.println("expected = " + expected + "\nactual  = " + actual);
        assertFalse("wrong message(s) received, see systemout", failure);
       
        // next, servlet looks up ejb via service ref.
        System.out.println("--- servlet to ejb call ---");
        failure = false;
        wsdlUrl = new URL(hostAndPort + "/ejbinwar/ServletInWarClassesHelloService?wsdl");
        expected = "ejbinwarclasseshello hello invoked with arg: common client reference call"+
            " to ejbinwarclasses returns: from servletinwarclasses:web.xml ejb reference";
        actual = (new ServletInWarClassesHelloService(wsdlUrl)).getServletInWarClassesHelloPort().callAnotherUsingServiceRef("web.xml ejb reference");
        if (actual.compareTo(expected)!= 0) {failure = true;}
        System.out.println("expected = >" + expected + "<\nactual   = >" + actual + "<\n");
      
        assertFalse("wrong message(s) received, see systemout", failure);
    }
    
    /**
     * test that a service-ref declared in a web-fragment.xml file is usable.
     * @throws java.lang.Exception
     */
    public void testServiceRefsInWebFragmentXml() throws java.lang.Exception{
        // an ejb looks up another ejb via a service ref. 
        System.out.println("-- ejb to ejb call --");
        boolean failure = false;
        URL wsdlUrl = new URL(hostAndPort + "/ejbinwar/EjbInWarLibHelloService?wsdl");
        String expected = "ejbinwarclasseshello hello invoked with arg: common client reference "+
            "call to ejbinwarclasses returns: using web-fragment.xml ejb reference web-fragment.xml ejb reference";
        String actual = (new EjbInWarLibHelloService(wsdlUrl)).getEjbInWarLibHelloPort().callAnotherUsingServiceRef("web-fragment.xml ejb reference");
        if (actual.compareTo(expected)!= 0) {failure = true;}
        System.out.println("expected = " + expected + "\nactual  = " + actual + "\n");
        assertFalse("wrong message(s) received, see systemout", failure);
    }
    
    
    /**
     * Assure that we can use webservices.xml to override the webservices annotation in a war-based ejb.
     * 
     * Webservices.xml will override the wsdl location for the service.
     * The packaged wsdl will contain a comment string.
     * We will check for the comment string to make sure we are picking up the specified wsdl,
     * which means the processing of webservices.xml and ejb-jar.xml is working, or at least alive,
     *  for an ejb in a war file.
     * 
     */
   public void testWebServiceXMLWorksOnEJBInWar() throws java.lang.Exception {
       String eiwc = hostAndPort+"/ejbinwar/EjbInWarClassesHelloService?wsdl";
       String wsdl = Operations.getUrlContents(eiwc, 30);
       boolean test = wsdl.contains("This wsdl is packaged by the app");
       if(!test){
           System.out.println("received wsdl:\n" + wsdl);
           fail("wsdl does not contain comment, wrong wsdl may have been used, see systemout");
       }

    } 
 
    // ================== junit plumbing beneath here ==================
    
    /** 
     * A constructor to create a test case with a given name.
     * @param name The name of the test case to be created
     */
    public EjbInWarTest(String name) {
        super(name);
    }
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(EjbInWarTest.class);   
    } 
    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws java.lang.Exception {
    }
    
    // make sure everything is running at the end of each test
    public void tearDown()throws java.lang.Exception{
    }
    
    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
		super.suiteSetup(cr);
        System.out.println("suiteSetup() called");
    }
    
    //  our nonportable test teardown method
    protected void suiteTeardown() throws java.lang.Exception {
    	super.suiteTeardown();
        System.out.println("suiteTeardown() called");
    }
    

}    