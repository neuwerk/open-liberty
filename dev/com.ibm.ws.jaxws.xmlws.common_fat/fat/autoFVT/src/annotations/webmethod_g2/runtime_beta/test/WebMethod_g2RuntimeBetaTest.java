/*
 * @(#) 1.6 autoFVT/src/annotations/webmethod_g2/runtime_beta/test/WebMethod_g2RuntimeBetaTest.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:13 [7/11/07 13:12:00]
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
 * 9/20/06     btiffany    LIDB3296.31.01     new file
 * 05/24/2007  jramos      440922             Integrate ACUTE
 *
 */

package annotations.webmethod_g2.runtime_beta.test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;
/**
 * @throws Exception Any kind of exception
 *   
 * @author btiffany
 * 
 * notes:
 * 12.11 d0649.23 methodswitch, dynamic failing on 390939.1 - schema retrieval.
 *                basicwebmethod is passing with cached wsdl - just need to change back.
 *                
 * 12.14 f0650.10 bascicwebmethod passing, removed cached wsdl hack   
 *                opswitch test failing w 392386
 *                opswitch dynamic wsdl had war file construction problem.
 *                  - failing w 392386      
 *                  
 * 01.11 f0701.17 opswitch passes!     
 * 
 * 01.25 n0703.19 methodone and method2 calls failing with def 416195 (Min's).  Per J.B. 
 * this is due to not having soap:actions in the wsdl. Revising wsdl.   
 * Fix that and everything is passing except the dynamic wsdl case is getting
 * a failure retrieving the schema. 
 * 
 * 02.05 n0704.24 passed.
 *                 
 *
 */

public class WebMethod_g2RuntimeBetaTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
       
    static String hostAndPort = null;
	
    static{
        // figure out host and port.
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
    }
    
    public static void main(String[] args) throws Exception {   
        TestRunner.run(suite());
        /*
        WebMethod_g2RuntimeBetaTest t = new  WebMethod_g2RuntimeBetaTest();
        t.testBasicWebMethod();
        t.testMethodSwitch();
        t.testMethodSwitchDynamicWsdl();
        */
    }

    /**
     * junit needs next 3.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(WebMethod_g2RuntimeBetaTest.class); 
    }   
    
	public WebMethod_g2RuntimeBetaTest(String str) {
		super(str);
	}
	
	public WebMethod_g2RuntimeBetaTest(){
		super();
	}
    
    // junit will run this before every test
    public void setUp(){    
    }
    
    /** 
     * @testStrategy - Test @WebMethod on SEI's, since that's all beta supports.
     * Since this is the only testing we have of this anno on interfaces, it needs to stay
     * put even after beta.
     * 
     * 389081 - no duplicate servicenames allowed across was.
     * So we commented out the basic service rather than make new client, wsdl, etc. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- Test @WebMethod on SEI's, since that's all beta supports. Since this is the only testing we have of this anno on interfaces, it needs to stay put even after beta.  389081 - no duplicate servicenames allowed across was. So we commented out the basic service rather than make new client, wsdl, etc.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBasicWebMethod() throws Exception {
        System.out.println("*********** running  testbasic WebMethod *****************");
        //fail("389081 - due to axis not allowing duplicate servicenames, this test cannot run as written.\n" +
        //        "Change installtest.xml to deploy AnnotationsWebMethodg2Beta.ear after defect is fixed.");
        
        String result = annotations.webmethod_g2.runtime_beta.client.WebMethodg2BetaClient.runme(hostAndPort+
                "/webmethodg2beta/webmethodg2beta?wsdl");
        
        // hack....
        /* 
        String wsdlFile = Support.getFvtBaseDir()+"/src/annotations/webmethod_g2/runtime_beta/etc/wsdl/InterfaceTestImplService.wsdl";        
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = "/tmp/InterfaceTestImplService.wsdl" + "static";
        String relativeUrl = "/webmethodg2beta/webmethodg2beta";
         wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort + relativeUrl );
         //wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );

        String result = annotations.webmethod_g2.runtime_beta.client.WebMethodg2BetaClient.runme("file:"+newWsdlFile);
        */
        String expected = "methodOne returns: calling methodOnemethodTwo returns: calling methodTwo";
        assertTrue("unexpected result from client \n "+
                   "expected = "+expected + "\n" +
                   "actual = "+result 
                   , result.contentEquals(expected));
        
        System.out.println("*********** test passed *************");
        
    }

    /**
     * @testStrategy 
     * We can't check the exclude parameter, but we can define two methods and point them at 
     * each other by switching the operation names, and see if the runtime can do that.
     * We do that on an impl class in webmethod_g2.runtime
     * 
     * see webmethod_g2.runtime.testSwapAndExclude4CachedWsdl() for this test on an impl class directly.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description=" We can't check the exclude parameter, but we can define two methods and point them at each other by switching the operation names, and see if the runtime can do that. We do that on an impl class in webmethod_g2.runtime  see webmethod_g2.runtime.testSwapAndExclude4CachedWsdl() for this test on an impl class directly. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testMethodSwitch() throws Exception {  
        System.out.println("*********** running  testMethodSwitch *****************");
        //String url = hostAndPort+ "/webmethodg2betatwo/webmethodg2beta/wsdl/InterfaceTestImplService.wsdl" ;
        String url = hostAndPort+ "/webmethodg2betatwo/webmethodg2beta?wsdl" ;
        
        /*
        // hack - use cached wsdl while wsdl retrieval is broken
        String wsdlFile = Support.getFvtBaseDir()+"/src/annotations/webmethod_g2/runtime_beta/etc/wsdl/InterfaceTestImplService.wsdl";        
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = "/tmp/InterfaceTestImplService.wsdl" + "static2";
        String relativeUrl = "/webmethodg2betatwo/webmethodg2beta";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort + relativeUrl );
         //wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        url="file:"+newWsdlFile;
        */
        
        String result = annotations.webmethod_g2.runtime_beta.client.WebMethodg2BetaClient.runme(url);
                
        String expected = "methodTwo returns: calling methodOnemethodOne returns: calling methodTwo";
        assertTrue(" def 392386: unexpected result from client \n "+
                   "expected = "+expected + "\n" +
                   "actual = "+result 
                   , result.contentEquals(expected));      
        
         System.out.println("*********** test passed *************");
        
    }
    
    /**
     * @testStrategy - same as testMethodSwitch but server needs to generate correct wsdl.
     * also tests our ability to generate wsdl for an SEI.  
     * in webmethod_g2Runtime, we test it for an impl.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- same as testMethodSwitch but server needs to generate correct wsdl. also tests our ability to generate wsdl for an SEI. in webmethod_g2Runtime, we test it for an impl. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testMethodSwitchDynamicWsdl(){
        System.out.println("*********** running  testMethodSwitchDynamicWsdl() *****************");
        String result = annotations.webmethod_g2.runtime_beta.client.WebMethodg2BetaClient.runme(hostAndPort+
        "/webmethodg2beta3/webmethodg2beta?wsdl");        
        String expected = "methodTwo returns: calling methodOnemethodOne returns: calling methodTwo";
        assertTrue(" def 392386: unexpected result from client \n "+
                   "expected = "+expected + "\n" +
                   "actual = "+result 
                   , result.contentEquals(expected));  
        System.out.println("*********** test passed *************");
        
    }       
}
