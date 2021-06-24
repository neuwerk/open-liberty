/*
 * @(#) 1.18.1.1 autoFVT/src/annotations/webmethod_g2/runtime/test/WebMethod_g2RuntimeTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/26/10 10:06:55 [8/8/12 06:54:59]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 *                         LIDB3296.31.01     new file
 * 05/24/2007  jramos      440922             Integrate ACUTE
 * 09/03/2009  jramos      611078             Update test cases for new JDK behavior: http://washome.austin.ibm.com/xwiki/bin/view/WebSvcLevel3Team/WebMethodInterpretationChanges
 * 10/01/2009  jramos      617604             JDK behavior has reverted back on all platforms except solaris
 * 03/16/2010  lizet       643282             Upgrade JAXWS/JAXB 2.2
 */

package annotations.webmethod_g2.runtime.test;
import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.Support;
import annotations.webmethod_g2.runtime.client.WebMethod_g2RuntimeClient;
import annotations.webmethod_g2.runtime.client.WebMethod_g2RuntimeVoidClient;

import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.ws.wsfvt.build.tools.AntProperties;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.Topology;
import common.utils.topology.visitor.QueryDefaultNode;
/**
 * @throws Exception Any kind of exception
 *   
 * @author btiffany
 * 
 * notes:
 * 12.11.06 d0649.23
 *   - testunannotated, testchangetononpublic, swapandexclude4dyamic, testvoidreturndynamic,
 *     - all failing due to schmea retr. 390930.1
 *     
 *     - why is g2rt4 not deploying? - 409584 - - chema in wrong place
 *     - testvoidreturncached - 392386 - opname not handled 
 *     
 * 12.14.06 f0650.10
 * - testunannotated, - passed
 *  - testaddinganannotation2 - 411756 - new defect.
 *  testchangetononpublic - passed
 *  testswapandexclude  - failing with 411576, 392386
 *  
 *  testvoidreturn - looks ok except for defects already identified.
 *  
 *  1.23.07 h0702.29 - failing disambiguation in voidmethodcheck.
 *  looks like I might have been too agressive with req/resp bean renames.
 *  But, when making them "nicer", can't reinstall due to classloader error.
 *  Changed build to not keep wsdl for void method in src tree.
 *  
 *  Others are failing with methodname... "not known to this context" error.
 *    This will be a resgression of 408916 if it persists.
 *  I think I'll sit tight and try a newer build before opening a defect.
 *  
 *  1.23.07 n0703.10 - opened 408916.1 on "not known to this context"
 *                     416325 on illegalarguementexception on echo(int) in void client.
 *  1.24 that came back, opened 416618
 *  1.25 n0703.19 everything working except overloaded method call, 416325.     
 *  
 *  2.05 n0704.24 - overloaded method not getting illegalargument exception,
 *         but failing to demarshal.  Looks like I put the req/resp beans
 *         in the wrong package.  Rebuilt and retrying.. and the answer is... it worked.               
 *                     
 *                     
 *  4.12 432050 open on testSwappedMethod6
 *  
 *  
 *  
 *     
 *
 */

public class WebMethod_g2RuntimeTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    static boolean onSun = false;       // set true sun dp.
    static String hostAndPort = null;
    
	//WAS default value for LegacyWebmethod, change this param if defalt is true;
    static boolean defaultLegacyWebMethod=false;
	
	public static void main(String[] args) throws Exception {	
        if (false){
            TestRunner.run(suite());
        }
        else {
            WebMethod_g2RuntimeTest t = new WebMethod_g2RuntimeTest();
            t.testSwappedMethods6();
            /*
            
            t.testUnannotatedMethods();
                        
            t.testAddingAnAnnotation2();            
            t.testChangeMethodToNonpublic3();
            t._testVoidReturnCachedWsdl();
            t.testVoidReturnDynamicWsdl();
           
            */
            
            
        }
	}
    
    static{
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
    }

    /**
     * junit needs next 3.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(WebMethod_g2RuntimeTest.class); 
    }   
    
	public WebMethod_g2RuntimeTest(String str) {
		super(str);
	}
	
	public WebMethod_g2RuntimeTest(){
		super();
	}
    
    // junit will run this before every test
    public void setUp(){    
    }
    
    /** 
     * @testStrategy check that unannotated and non-public methods on a bean are properly exposed - or not.
     * First a class with some unannotated public methods has wsdl and client generated for it.  All methods 
     * should be exposed in this case.  Then the @WebMethod anno is added to one method, other public methods 
     * should disappear in this case (jsr 181 3.1, jsr250 2.1.2).  Then a method is changed to non-public, it 
     * should disappear.  In the second two cases, wsdl, client, and all generated classes are all kept the 
     * same, only the imp. bean is changed, to see if the runtime will handle the change correctly.  It's possible
     * that the service won't deploy if the implementation carefully checks the wsdl first,
     * so we check that we can reach the service before we invoke the modified ones.  Finally,
     * if we were able to reach the service, we check that the implementation did not
     * generate a wsdl that just deleted the hidden methods, which would violate jsr 5.2.5.3.
     * 
     * Client calls noannomethod, annomethod, and annomethod2 in order.  They all just echo 
     * the call.  Result string is concatenated from returns.  We look at the result string to 
     * see what got executed.
     * 
     * We need the no-multiple-services-by-same-name bug in axis to be 
     * fixed for any of these tests to work. 
     *  
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testUnannotatedMethods() throws Exception {
    	System.out.println("********** testUnannotatedMethods() is running **********");
        //String wsdlurl = AntProperties.getServerURL()+"/webmethod_g2rt/WebMethodRuntimeAnnotatedCheckService?wsdl";
        String wsdlurl = hostAndPort+"/webmethod_g2rt/WebMethodRuntimeAnnotatedCheckService?wsdl";
    	if (onSun) wsdlurl = AntProperties.getServerURL()+"/AnnotationsWebMethodg2Runtime/webmethodg2runtime?wsdl";
        _testUnannotated(wsdlurl);  
    }
 
    /*
    //  invalid - we need wsdl to remain the same to test that other changes work as anticipated
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
     public void testUnannotatedMethodsCachedWsdl() throws Exception {
        // To test before dynamic wsdl is available, patch up the wsdl
        // we produced when building the client and use that. 
        String wsdlFile = Support.getFvtBaseDir()+"/build/work/annotations/webmethod_g2/runtime/WebMethodRuntimeAnnotatedCheckService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webmethod_g2rt/WebMethodRuntimeAnnotatedCheckService1d";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+"/" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        _testUnannotated("file:/"+newWsdlFile); 
        
    }
   */
    
    /**
     * uses WebMethodRuntimeAnnotatedCheck.java, deployed in AnnotationsWebMethodg2Runtime.ear
     * @param wsdlurl
     * @throws Exception
     * called by testUnannotated()
     */
    private void _testUnannotated(String wsdlurl) throws Exception{
        String result = null;
        // make sure we can invoke the service with all default,unannotated methods.
        result = WebMethod_g2RuntimeClient.runme(wsdlurl);
        System.out.println("client returned" + result);
        String expected = "calling noAnnoMethodcalling annoMethodcalling annoMethod2";
        assertTrue("wrong methods exposed, \n expected= "+ expected+ " \n actual= "+ result, result.compareTo(expected)==0);
        System.out.println("********** test passed **********");
        
    }
    
    /**
     * @testStrategy Add a @WebMethod annotation and make sure all the 
     * unannotated methods disappear.  
     * @throws Exception
     * 
     * Client calls noannomethod, annomethod, and annomethod2 in order.  They all just echo 
     * the call.  Result string is concatenated from returns.  We look at the result string to 
     * see what got executed.
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAddingAnAnnotation2() throws Exception {
    	System.out.println("********** testAddingAnAnnotation2() is running **********");
        //String wsdlurl = AntProperties.getServerURL()+"/webmethod_g2rt2/WebMethodRuntimeAnnotatedCheckService?wsdl";
        String wsdlurl = hostAndPort+"/webmethod_g2rt2/WebMethodRuntimeAnnotatedCheckService?wsdl";
        if (onSun) wsdlurl = AntProperties.getServerURL()+"/AnnotationsWebMethodg2Runtime2/webmethodg2runtime2?wsdl";
        _testAddingAnAnnotation2(wsdlurl);
    	
    }
    
    /*
    //  invalid - we need wsdl to remain the same to test that other changes work as anticipated
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAddingAnAnnotation2CachedWsdl() throws Exception {
        System.out.println("********** testAddingAnAnnotation2CachedWsdl() is running **********");
        // To test before dynamic wsdl is available, patch up the wsdl
        // we produced when building the client and use that. 
        String wsdlFile = Support.getFvtBaseDir()+"/build/work/annotations/webmethod_g2/runtime2/WebMethodRuntimeAnnotatedCheckService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webmethod_g2rt2/WebMethodRuntimeAnnotatedCheckService";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+"/" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );

        _testAddingAnAnnotation2("file:/"+newWsdlFile);
        
    }
    */
    
    private void _testAddingAnAnnotation2(String wsdlurl) throws Exception {
        // it's possible the runtime didn't deploy due to wsdl/class inconsistency. 
        if(Operations.getUrlStatus(wsdlurl, 20)){       
            String result = null;
            result = WebMethod_g2RuntimeClient.runme(wsdlurl);
            System.out.println("client returned" + result);
			if(defaultLegacyWebMethod){
			    String expected = "calling annoMethod" + "";
			    assertTrue("wrong methods exposed, \n expected= "+ expected+ " \n actual= "+ result, result.compareTo(expected)==0);
			}else{
			    String expected = "calling noAnnoMethodcalling annoMethodcalling annoMethod2";
				assertTrue("wrong methods exposed, \n expected= "+ expected+ " \n actual= "+ result, result.compareTo(expected)==0);
			}
            
            // last, we need to check that the wsdl portype definitions were not illegally 
            // replaced by the runtime.  Nice feature, but violates 5.2.5.3.
            if(!Operations.getUrlContents(wsdlurl, 20).contains("annoMethod2")){
                fail("wsdl improperly regenerated, supplied portType modified");
            }            
        }   
        System.out.println("********** test passed **********");

        
    }
    
    /**
     * @testStrategy see testChangedAnno.  this time we make a method nonpublic and make sure it disppears.
     * @throws Exception
     * 
     * Client calls noannomethod, annomethod, and annomethod2 in order.  They all just echo 
     * the call.  Result string is concatenated from returns.  We look at the result string to 
     * see what got executed.
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testChangeMethodToNonpublic3() throws Exception {
    	System.out.println("********** testChangeMethodToNonpublic3()  is running **********");
        //String wsdlurl = AntProperties.getServerURL()+"/webmethod_g2rt3/WebMethodRuntimeAnnotatedCheckService?wsdl";
        String wsdlurl = hostAndPort+ "/webmethod_g2rt3/WebMethodRuntimeAnnotatedCheckService?wsdl";
    	if (onSun) wsdlurl = AntProperties.getServerURL()+"/AnnotationsWebMethodg2Runtime3/webmethodg2runtime3?wsdl";
        _testChangeMethodToNonpublic3(wsdlurl); 
        System.out.println("********** test passed ***********");
    }
    
    /*
    // invalid - we need wsdl to remain the same to test that other changes work as anticipated 
     
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testChangeMethodToNonpublic3CachedWsdl() throws Exception{
        String wsdlFile = Support.getFvtBaseDir()+"/build/work/annotations/webmethod_g2/runtime3/WebMethodRuntimeAnnotatedCheckService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webmethod_g2rt3/WebMethodRuntimeAnnotatedCheckService";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+"/" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );

        String wsdlurl = "file:/" + newWsdlFile;
        
        _testChangeMethodToNonpublic3(wsdlurl);        
    }
    */
    
    private void _testChangeMethodToNonpublic3(String wsdlurl) throws Exception{        
        // it's possible the runtime didn't deploy due to wsdl/class inconsistency. 
        if(Operations.getUrlStatus(wsdlurl, 20)){               
            String result = null;
            result = WebMethod_g2RuntimeClient.runme(wsdlurl);
            System.out.println("client returned" + result);
            String expected = "calling noAnnoMethodcalling annoMethod";
            assertTrue("wrong methods exposed, \n expected= "+ expected+ "\n actual= "+ result, result.compareTo(expected)==0);
            
            // last, we need to check that the wsdl portype definitions were not illegally 
            // replaced by the runtime.  Nice feature, but violates 5.2.5.3.
            if(!Operations.getUrlContents(wsdlurl, 20).contains("annoMethod2")){
                fail("wsdl improperly regenerated, supplied portType modified");
            }
        }
        System.out.println("********** test passed **********");
    }
    
    /**
     * @testStrategy see testChangedAnno.  this time we rename two methods so they point to each other, 
     * and make sure the runtime performs that fixup.  We add an exclude param to a method,
     * and make sure it is not invoked.   The swap was failing in beta with 392386. 
     * 
     * Client calls noannomethod, annomethod, and annomethod2 in order.  They all just echo 
     * the call.  Result string is concatenated from returns.  We look at the result string to 
     * see what got executed.
     * 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testExcludedMethod4() throws Exception {
    	System.out.println("********** testSwapAndExclude4() is running **********");
        String wsdlurl = hostAndPort +"/webmethod_g2rt4/WebMethodRuntimeAnnotatedCheckService?wsdl";
    	if(onSun) wsdlurl = AntProperties.getServerURL()+"/AnnotationsWebMethodg2Runtime4/webmethodg2runtime4?wsdl";
        
        String result = null;
        result = WebMethod_g2RuntimeClient.runme(wsdlurl);
        System.out.println("client returned: " + result);
        
        String expected = "annoMethod returns: calling noAnnoMethodnoAnnoMethod returns: calling annoMethod";
        assertEquals("wrong methods exposed", expected, result);
        
        System.out.println("******* test passed *******");
        //_testSwapAndExclude4(wsdlurl);
        
   }   
    /*
     * check that we can switch two methods by switching their operation names.
     * Beta test checks this on an sei, here we check on direct impl. 
     * The two methods should swap and then we should get an exception on the third method
     * because it has been excluded.
     * 
     * Server uses WebMethodRuntimeAnnotatedCheck4.java_nowsdl.
     * It's the same, as ...check4.java, but lets the server generate
     * the wsdl.
     * 
     * 4.19.07 - since client side validation is working now, we have
     * to expose all methods that were used to generate the client.
     * So the server method has been changed to exclude a method 
     * that wasn't present when the client was generated.  We will
     * search the wsdl for it to make sure exclude is working
     * on the server. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSwappedMethods6() throws Exception {
        System.out.println("********** testSwappedMethods6() is running **********");
        String wsdlurl = hostAndPort +"/webmethod_g2rt6/WebMethodRuntimeAnnotatedCheckService?wsdl";
        String result = null;
        result = WebMethod_g2RuntimeClient.runme(wsdlurl);
        System.out.println("client returned" + result);
        //String expected = "annoMethod returns: calling noAnnoMethodnoAnnoMethod returns: calling annoMethod";
        
        String expected =   "annoMethod returns: calling noAnnoMethodnoAnnoMethod returns: calling annoMethodannoMethod2 returns: calling annoMethod2";
        assertTrue("wrong methods exposed, \n expected= >"+ expected+ "<\n actual=  >"+ result, result.compareTo(expected)==0);     
        
        
        System.out.println("******* test passed *******");
    }
    
    /**
     * @testStrategy - we look for an excluded method in the dynamically
     * generated wsdl and make sure it's not there. 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testExcludedMethodDynamicWsdl6() throws Exception {
        System.out.println("********** testExcludedMethodDynamicWsdl6() is running **********");
        String wsdlurl = hostAndPort +"/webmethod_g2rt6/WebMethodRuntimeAnnotatedCheckService?wsdl";
        
        assertTrue("service did not deploy", Operations.getUrlStatus(wsdlurl, 30));
        String wsdl = Operations.getUrlContents(wsdlurl , 30);
        assertFalse("An excluded operation is exposed in wsdl",
                    wsdl.contains("surprise"));
        
        System.out.println("******* test passed *******");
        
    }
    
    /**
     * @testStrategy try a method that returns void and make sure it works.
     * Also uses dyamic wsdl and checks exclude and operationname behaviors,
     * and checks disambiguation of an overloaded method through use of annotations.
     * 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testVoidReturnDynamicWsdl() throws Exception {
    	System.out.println("********** testVoidReturnDynamicWsdl() is running **********");
        String wsdlurl = hostAndPort+"/webmethod_g2rt5/WebMethodRuntimeVoidCheckService?wsdl";    	
        String wsdl=Operations.getUrlContents(wsdlurl,30);
        // check dynamic wsdl - see if any blocked methods were exposed
        assertFalse("excluded method echoInherited is exposed", wsdl.contains("echoInherited"));
        assertFalse("excluded method echoBlocked is exposed", wsdl.contains("echoBlocked"));
        assertFalse("renamed method echo2 is exposed", wsdl.contains("echo2"));
        OperatingSystem os = TopologyDefaults.getRootServer().getNode().getMachine().getOperatingSystem();
        assertTrue("nonpublic method nonpublic is not exposed", wsdl.contains("nonpublic")); // the method will be exposed only on Solaris which is not an IBM JDK; HP doesn't have the problem
        // now try to invoke all the ones that should be exposed.
        _testVoidReturn( wsdlurl);
        System.out.println("********** test passed *************");
   } 
    
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testVoidReturnCachedWsdl() throws Exception {
        System.out.println("********** testVoidReturnCachedWsdl() is running **********");
        String wsdlFile = Support.getFvtBaseDir()+"/build/work/annotations/webmethod_g2/runtime5/WebMethodRuntimeVoidCheckService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webmethod_g2rt5/WebMethodRuntimeVoidCheckService";
        //wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort + relativeUrl );
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );

        String wsdlurl = "file:" + newWsdlFile;
        
        _testVoidReturn( wsdlurl);
        System.out.println("********** test passed *************");
    }
    
    private void _testVoidReturn(String wsdlurl) throws Exception {
        String result = null;
        result = WebMethod_g2RuntimeVoidClient.runme(wsdlurl);
        System.out.println("client returned" + result);
        String expected = "success";
        assertTrue("wrong result, \n expected= "+ expected+ "\n actual= "+ result, result.compareTo(expected)==0);     
        System.out.println("********** test passed **********");            
    }
    
    /**
     * @testStrategy - see runtime_beta tests for tests with an SEI.
     * We test swapping two methods and that a basic call with SEI works. 
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- see runtime_beta tests for tests with an SEI. We test swapping two methods and that a basic call with SEI works. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testSEI(){}
    
    /**
     * @testStrategy - see webservice_g2.inheritance for these tests.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- see webservice_g2.inheritance for these tests. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testInheritance(){}
       
}
