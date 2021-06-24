//
// @(#) 1.1 autoFVT/src/jaxws/dynamicbeangen/test/DynamicBeanTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 7/12/10 14:12:39 [8/8/12 06:57:46]
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
// 07/10/10 btiffany                    New File
//

package jaxws.dynamicbeangen.test;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import jaxws.dynamicbeangen.client.MessageCaptureHandler;

import jaxws.dynamicbeangen.server.DynamicBeans;
import jaxws.dynamicbeangen.server.DynamicBeansService;
import jaxws.dynamicbeangen.server.ExtClass;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
  

/**
 * This class will test that when jaxws beans are -not- packaged with an app, generated beans
 * are used. Also, will check that when beans are supplied, they are used, not the generated ones.
 * These are conformance requirements in JAXWS 2.2 SEC 3.6.2.1 
 * 
 * Since: version 8
 * RTC Story: 13250, task 30962
 *  
 */
public class DynamicBeanTest extends FVTTestCase {   
   
    static String hostAndPort= null;
    DynamicBeans port = null;
    MessageCaptureHandler mch = null;
    
 
    
    /**
     * This is a basic case that should always work, it has nothing to do with the beans missing.
     * If things aren't working with the beans supplied, we've got bigger problems. 
     * @throws Exception
     */
    public void testThatOperationsWorkWithoutWsdlWithPackagedBeans() throws Exception {
        String wsdlUrl = hostAndPort + "/jaxwsdynamicbeans/allbeansnowsdl?wsdl";
        int dlwminUsageCount = exerciseService(wsdlUrl);
        assertTrue("doclit minimal marshaller was used but should not have been", dlwminUsageCount == 0);
        
        // it's unlikely, but possible that we could be ignoring the packaged beans and 
        // using the generated beans.  Let's call getStackTrace, which contains our special packaged bean,
        // and make sure we're getting the right one.  It will return a non-normal response if
        // the packaged bean is being used.
        DynamicBeans port =  (new DynamicBeansService(new URL(wsdlUrl))).getDynamicBeansPort();
        addHandler((BindingProvider)port);
        
        String expected = "This is a modified bean packaged with the app";
        String actual  = port.getStackTrace();
        System.out.println("received raw soap message:\n " + mch.getInboundMsgAsString()+ "\n");
        System.out.println("expected ="+expected + " actual =" + actual);
        assertTrue("wrong bean was used", actual.compareTo(expected)==0 );
    }
    
    /**
     * in This case, beans are not packaged with the app.  Prior to this story, we had a proprietary marshaller
     * we called doc-lit-minimal that would handle such a case.  After this story, the runtime-generated 
     * beans themselves should  be used.  We inspect the response message and check that we are not using
     * the doc-lit-minimal marshaller. 
     * @throws Exception
     */
    public void testThatOperationsWorkWithoutWsdlWithoutBeans() throws Exception {
        String wsdlUrl = hostAndPort + "/jaxwsdynamicbeans_nobeans_nowsdl/nobeansnowsdl?wsdl";        
        int dlwminUsageCount = exerciseService(wsdlUrl);
        assertTrue("doclit minimal marshaller was used but should not have been", dlwminUsageCount == 0);
    }
    

    
    /**
     * per jaxws2.2, supplied beans must be used instead of generated ones if present. 
     * We will package an app with some of the beans missing.  The packaged beans will
     * be modified so the operation returns a specific message.  We will check for the 
     * presence of that message to be sure the packaged beans are being used. 
     * @throws Exception
     */
    public void testThatSuppliedBeansAreUsedNotGeneratedOnes() throws Exception {        
        String wsdlUrl = hostAndPort + "/jaxwsdynamicbeans_somebeans_nowsdl/somebeansnowsdl?wsdl";        
        
        // just exercise and verify no surprises here
         int dlwminUsageCount = exerciseService(wsdlUrl);
         assertTrue("doclit minimal marshaller was used but should not have been", dlwminUsageCount == 0);
        
        // now the real test, we should get the response from the modified bean
        DynamicBeans port =  (new DynamicBeansService(new URL(wsdlUrl))).getDynamicBeansPort();
        addHandler((BindingProvider)port);
        
        // debug
       // ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
        //        "http://localhost:19080/jaxwsdynamicbeans_somebeans_nowsdl/somebeansnowsdl");
        
        String expected = "This is a modified bean packaged with the app";
        String actual  = port.getStackTrace();
        System.out.println("received raw soap message:\n " + mch.getInboundMsgAsString()+ "\n");
        System.out.println("expected ="+expected + " actual =" + actual);
        assertTrue("wrong bean was used", actual.compareTo(expected)==0 );
         
    }
    
    /**
     * this method invokes all the operations of the service and checks that they work.
     * It examines the response soap message and looks for the namespace prefix "dlwmin".
     * That prefix indicates that the non-performant "doc-lit-wrapped-minimal" 
     * marshaller is being used to marshall the response on the server side.
     * 
     * The method returns the total count of operations where dlwmin was found in 
     * the response. 
     * 
     * We could also have determined use of the minimal marshaller by capturing and parsing
     * trace, but this is easier. 
     * 
     * After we comply with jaxws 2.2, and generate the real jax-b beans
     * if they are not packaged with the app, we should  
     * be using those instead, so in those cases the dlwmin usage count should be zero.
     * 
     * It was decided in the cases where wsdl is packaged but no beans, we will 
     * not generate, so dlwmin will still be used in that case. 
     */   
    private int exerciseService(String wsdlUrl) throws Exception {
        int dlwminUsageCount = 0;
        port =  (new DynamicBeansService(new URL(wsdlUrl))).getDynamicBeansPort();
        addHandler((BindingProvider)port);
        
        System.out.println("testing anytype");
        String expected = "hello world";
        String actual = port.echoAnyTypeObject(expected).toString();
        System.out.println("expected ="+ expected + "\n actual = "+actual);
        assertTrue("did not get expected echo response, see systemout", actual.compareTo(expected)== 0);
        if(wasDlwminUsed()){ dlwminUsageCount ++; }
        
        
        System.out.println("testing non-default bean names");
        expected = "hello world non default bean names";
        actual = port.echoNonDefaultBeanNames(expected);
        System.out.println("expected ="+ expected + "\n actual = "+actual);
        assertTrue("did not get expected echo response, see systemout", actual.compareTo(expected)== 0);
        if(wasDlwminUsed()){ dlwminUsageCount ++; }
        
        System.out.println("testing List of Strings");
        List<String> expect2 = new ArrayList<String>();
        expect2.add("list of strings foo");
        expect2.add("bar");
        List<String> actual2 = port.echoArrayofStrings(expect2);
        if(wasDlwminUsed()){ dlwminUsageCount ++; }
        
        expected = "[list of strings foo, bar]";
        actual = actual2.toString();
        System.out.println("expected ="+ expected + "\n actual = "+actual);
        assertTrue("did not get expected echo response, see systemout", actual.compareTo(expected)== 0);
        if(wasDlwminUsed()){ dlwminUsageCount ++; }
        
        System.out.println("testing array  of Strings");
        // array of strings also becomes a list in generated client code. 
        actual = port.echoArrayofStrings(expect2).toString();
        System.out.println("expected ="+ expected + "\n actual = "+actual);
        assertTrue("did not get expected echo response, see systemout", actual.compareTo(expected)== 0);
        if(wasDlwminUsed()){ dlwminUsageCount ++; }
        
        System.out.println("testing primitive int ");
        // array of strings also becomes a list in generated client code.
        int expectedi = 12345;
        int actuali = port.echoInt(expectedi);
        System.out.println("expected ="+ expectedi + "\n actuali = "+actuali);
        assertTrue("did not get expected echo response, see systemout", actuali == expectedi);
        if(wasDlwminUsed()){ dlwminUsageCount ++; }
        
        System.out.println("testing polymorphic type");
        ExtClass ex = new ExtClass();  // this is the client side  bean class, not the actual service side class. 
        ex.setBaseString("extclass base string");        
        ex.setExtString("extclass ext string");
        ExtClass ex2 = port.echoPolyMorphExtClass(ex);
        actual = ex2.getBaseString()+ " " + ex2.getExtString();
        expected = "extclass base string extclass ext string";
        System.out.println("expected ="+ expected + "\n actual = "+actual);
        assertTrue("did not get expected echo response, see systemout", actual.compareTo(expected)== 0);
        if(wasDlwminUsed()){ dlwminUsageCount ++; }
        
        return dlwminUsageCount;
        
    }
    
    /**
     * install a handler on a port.  We'll use the handler to capture the soap message.
     * Much easier than traffic monitoring, etc.   
     * @param port
     */
    private   MessageCaptureHandler addHandler(BindingProvider port){
        // set binding handler chain
        Binding binding = ((BindingProvider)port).getBinding();

        // can create new list or use existing one
        List<Handler> handlerList = binding.getHandlerChain();

        if (handlerList == null) {
            handlerList = new ArrayList<Handler>();
        }

        MessageCaptureHandler mch = new MessageCaptureHandler();
        handlerList.add(mch);        

        binding.setHandlerChain(handlerList);
        
        this.mch = mch;
        return mch;
    }
    
    /**
     * inspect the soap response message captured by the handler and look for the dlwmin
     * namespace within it. 
     * @return
     */
    private boolean wasDlwminUsed(){
        boolean used = mch.getInboundMsgAsString().contains("dlwmin");
        if (used){ System.out.println("---dlwmin usage detected---");}
        return used;
    }

    
    // ================== junit plumbing beneath here ==================
    
    /** 
     * A constructor to create a test case with a given name.
     * @param name The name of the test case to be created
     */
    public DynamicBeanTest(String name) {
        super(name);
    }

    /** 
     * The main method.  Only used for debugging.
     * @param args The command line arguments
     */
    public static void main(String[] args) throws Exception  {
      
       // TestRunner.run(suite());
        
        DynamicBeanTest t = new DynamicBeanTest("x");
        t.suiteSetup2();
        System.out.println("-- all beans present --");
        t.testThatOperationsWorkWithoutWsdlWithPackagedBeans();
        t.testThatSuppliedBeansAreUsedNotGeneratedOnes();
        //t.testThatOperationsWorkWithoutWsdlWithoutBeans();
       // t.testThatCorrectMarshallerIsUsed();
    }
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(DynamicBeanTest.class);   
    }  

 
    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws Exception {
    }
    
    // make sure everything is running at the end of each test
    public void tearDown()throws Exception{
    }
    
    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws Exception {
        super.suiteSetup(cr);
        System.out.println("suiteSetup() called");
        suiteSetup2();
    }
    
    // for debug, a method we can call outside junit. 
    private void suiteSetup2()throws Exception {
        String host = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
        String port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();  
        hostAndPort  = "http://" + host + ":" + port ;       
        System.out.println("hostAndPort = "+hostAndPort );
        
    }
    
//  our nonportable test teardown method
    protected void suiteTeardown() throws Exception {
        super.suiteTeardown();
        System.out.println("suiteTeardown() called");
    }
    

}    