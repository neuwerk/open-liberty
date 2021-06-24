/*
 * @(#) 1.4 autoFVT/src/annotations/webservice_g2/inheritance/test/WebserviceInheritanceRuntimeTest.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:49 [7/11/07 13:12:44]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 *                         LIDB3296.31.01     new file
 * 05/24/2007  jramos      440922             Integrate ACUTE
 *
 */

package annotations.webservice_g2.inheritance.test;
import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import annotations.support.Support;
import annotations.webservice_g2.inheritance.client.WebServiceInheritanceClient;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * @throws Exception Any kind of exception
 *   
 * @author btiffany
 * 
 * notes:
 * 12/18/2006 f0650.10
 * testImplInheritanceAtRuntime failing with 409137.  Reopened that defect today. 
 * testSEIInheritanceAtRuntime failing w 410814, should be fixed in f0650.19
 * 
 * 12/19/2006 f0651.02 - rejected 410814.  Dustin says 412158 should fix.
 *            Testing patch.  Seems to deploy now, but can't invoke due to 
 *            soap problem, 405902.1
 *            
 *            SEI not deploying, will open new defect for that. - 412267 
 *            
 * 1/3/2007   f0651.08 - trying to ver 410814.1, still blocked by 412267  
 * 2/7/2007   n0704.24 - converted to wrapped style to clear 409137.2
 * getting new failure on bottomimpl now, something like
 * "overloaded wsdl operations not supported".
 * going to hold off on a defect until 412267 is available, p0705.15 or higher,
 * then should open both problems in a single defect since they're probably related,
 * and add a note to route to Ann. 
 * 
 * 2/8/ p0705.23 only overriden, inherited method is failing on impl. 420042.
 * Stripped the sei service out of the ear so that was not a factor, had no effect.
 * 
 * Still need to retest the sei service.
 * 
 * 2/22/07 r0707.15 sei inh. failing to deploy, open 420249.1.  Impl inh passing. 
 *          
 *            
 *
 */
public class WebserviceInheritanceRuntimeTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	private static String workDir = null;
    private static String hostAndPort = null;
    
    // get host and port - only once.
    static{
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
    }
    
    public static void main(String[] args) throws Exception {   
        //TestRunner.run(suite());
        WebserviceInheritanceRuntimeTest t = new WebserviceInheritanceRuntimeTest();
       //t._testImplInheritanceAtRuntimeCachedWsdl();
       // t.testImplInheritanceAtRuntime();
        t.testSEIInheritanceAtRuntime();
    }

    
    /**
     * @testStrategy 
     * need to test runtime behavior of services that inherit through both 
     * impls and interfaces.  The right behavior is ambiguous at present, even
     * to development.
     * 
     * Also need to check dynamic wsdl generation
     */
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description=" need to test runtime behavior of services that inherit through both impls and interfaces.  The right behavior is ambiguous at present, even to development.  Also need to check dynamic wsdl generation",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testInfo(){}
    
    /**
     * @testStrategy - while the  status of  398900 and 397702 is being debated,
     * we still need to test that what the developers THINK is right works as they
     * intend.  So, we test the following:  
     * 
     * on a class, if a superclass has @WebService on it or @WebMethod in it, 
     * it's method(s) are exposed according to the rules for those annotations, and 
     * those methods can be invoked. 
     * Otherwise, nothing that's inherited is exposed.
     * 
     * on an interface, same deal.  
     *
     * this one will use AnnotationsWebservice_g2InheritanceImpl.ear,
     * sei is server.BottomImpl
     * we should see impl1echo, impl1echo2, echobottom, and nothing in the wsdl
     * named "surprise"
     * 
     * This one uses dynamic wsdl generation
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testImplInheritanceAtRuntime(){
        System.out.println("****** testImplInheritanceAtRuntime starting *******"); 
		// Liberty does not support SEI/?wsdl wsdl URL style
        String url = hostAndPort+ "/annotationsWebserviceInheritance/BottomImplService?wsdl";
        //String url = "http://localhost:19080/annotationsWebserviceInheritance/BottomImplService/BottomImplService.wsdl";

        // check that wsdl doesn't have surprise in it.
        String wsdl =com.ibm.ws.wsfvt.build.tools.utils.Operations.getUrlContents(url, 30);
        assertFalse("wsdl exposes wrong method(s)", wsdl.contains("surprise"));
        
        // check that we can invoke each method and get the right result
        String result = WebServiceInheritanceClient.runme(url);
        String expected = "impl1echo:hello "+"Impl2.impl1echo2:hello "+
                          "impl2echo:hello "+"echoBottom:hello";
        assertTrue("unexpected result from client:\n expected="+expected +"\n actual:"
                    +result, expected.compareTo(result)==0);        
       System.out.println("****** testImplInheritanceAtRuntime passed *******");
    }
    
    /**
     * @testStrategy - same as above, but cache the wsdl since dynamic generation
     * will be late.
     * 
     * removing 12.8.06 - wsdl retrieval is working now. 
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- same as above, but cache the wsdl since dynamic generation will be late.  removing 12.8.06 - wsdl retrieval is working now. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testImplInheritanceAtRuntimeCachedWsdl() throws Exception {
        System.out.println("****** testImplInheritanceAtRunTimeCahcedWsdl starting *******");
        
        // To test before dynamic wsdl is available, patch up the wsdl
        // we produced when building the client and use that.
        String relativeUrl = "/annotationsWebserviceInheritance/BottomImplService";
        String wsdlFile = Support.getFvtBaseDir()+"/build/work/webservice_g2_inheritance/BottomImplService.wsdl";
        if (wsdlFile.contains(":")) {
            wsdlFile = wsdlFile.substring(2);  // strip drive letter
        }
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        //wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+"/" + relativeUrl );
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://btiffany:9080" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        String wsdlurl = "file:" + newWsdlFile;
        System.out.println("wsdlurl = "+wsdlurl);        
        
        String result = WebServiceInheritanceClient.runme(wsdlurl);
        String expected = "impl1echo:hello "+"Impl2.impl1echo2:hello "+
                          "impl2echo:hello "+"echoBottom:hello";
        assertTrue("unexpected result from client:\n expected="+expected +"\n actual:"
                    +result, expected.compareTo(result)==0);
       System.out.println("****** test passed *******");
        
    }
    
    
    /**
     * @testStrategy - same as testImpl... but we're testing interface inheritance
     * to build up an equivalent service. 
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- same as testImpl... but we're testing interface inheritance to build up an equivalent service. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSEIInheritanceAtRuntime(){       
        System.out.println("****** testSEIInheritanceAtRuntime starting *******"); 

        String url = hostAndPort+
                     "/annotationsWebserviceInheritance2/BottomImplService?wsdl";                     

        // check that wsdl doesn't have surprise in it.
        String wsdl =com.ibm.ws.wsfvt.build.tools.utils.Operations.getUrlContents(url, 30);
        assertFalse("wsdl exposes wrong method(s)", wsdl.contains("surprise"));
        
        // check that we can invoke each method and get the right result
        String result = WebServiceInheritanceClient.runme(url);
        String expected = "SEIimpl1echo:hello "+"SEIimpl1echo2:hello "+
                          "SEIimpl2echo:hello "+"SEIechoBottom:hello";
        assertTrue("unexpected result from client:\n expected="+expected +"\n actual:"
                    +result, expected.compareTo(result)==0);
       System.out.println("****** testSEIInheritanceAtRuntime passed *******");        
    }

    /**
     * junit needs next 3.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(WebserviceInheritanceRuntimeTest.class); 
    }   
    
	public WebserviceInheritanceRuntimeTest(String str) {
		super(str);
	}
	
	public WebserviceInheritanceRuntimeTest(){
		super();
	}
    
    
}
