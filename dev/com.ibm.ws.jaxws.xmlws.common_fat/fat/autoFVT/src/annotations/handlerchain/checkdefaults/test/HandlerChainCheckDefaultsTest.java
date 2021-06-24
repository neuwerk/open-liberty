/*
 * @(#) 1.6 autoFVT/src/annotations/handlerchain/checkdefaults/test/HandlerChainCheckDefaultsTest.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:28:02 [7/11/07 13:09:53]
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
 * 07/20/2006  euzunca     LIDB3296.31.01     new file
 * 05/24/2007  jramos      440922             Integrate ACUTE
 * 01/13/2009  jramos      570716             Update for Simplicity
 * 
 */
package annotations.handlerchain.checkdefaults.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.IBM_ImplementationAdapter;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.application.Application;
import com.ibm.websphere.simplicity.application.ApplicationManager;
import com.ibm.ws.wsfvt.build.tasks.WsadminScript;
import com.ibm.ws.wsfvt.build.tools.AdminApp;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/*
 * Notes 1.12.07 - f 0651.08 test_runtime_AnnotationInImplBean_noSEI() failing, looks like 
 *                 handler is never called.  CTS has similar defect 413151.
 *                 I've tried to convert Engin's tests but only that one has really been attempted.
 *                 Since this is descope function I guess there's no rush.
 *                 
 *  3.22.07        it lives!  Back in we go.
 *  
 * Here's that this test basically does:
 * 
 * There are 4 servers annotated in various ways and we call them and make sure the handlers
 * are working as intended. in all cases the handlers are message handlers. 
 * 
 * server   has_impl    has_sei handler_on  soap_mode  handler file access mode
 * ------   --------    ------- ----------  ---------  ----------------------------
 * server1      y           y       both    dlw        direct (impl should overrule)
 * server2      y           y       impl    rpc        direct (passing) 
 * server3      y           y       sei     rpc        direct (passing)
 * server4      y           n       impl    dlb        direct (passing) 
 * server5      y           n       impl    dlw        relative path
 * server6      y           n       impl    dlw        http url
 * 
 * 
 * M.Z. is testing: wsdl customization, protocol handlers (soap11 and 12), client side handlers,
 * handler error and exception pathing.
 * 
 * 4.2.07 Tests failing on v0713.03, Min has def 430282 open. 
 * 
 *
 */

public class HandlerChainCheckDefaultsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
   
	private static String buildDir = null;
	private static String testDir  = null;
    private static IBM_ImplementationAdapter imp  = null;
    private static String hostAndPort = null;
    private static IAppServer server;
	static {
		buildDir = AppConst.FVT_HOME + File.separator +
		          "build";
		testDir = "annotations" + File.separator +
			      "handlerchain" + File.separator +
			      "checkdefaults";
        
        // figure out host and port.
        server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
        
        // we're going to use this to run the client from different directories
        // and get around the frameworks classpath limitations. 
        imp = new IBM_ImplementationAdapter(buildDir+"/work/"+testDir);
	}
    
	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public HandlerChainCheckDefaultsTest(String name) {
		super(name);
	}
    
    // for debugging
    public static void main(String [] args) throws Exception{ 
        if (false){
                TestRunner.run(suite());
        }
        else {
        
            HandlerChainCheckDefaultsTest h = new HandlerChainCheckDefaultsTest("x");
           // h.test_runtime_AnnotationInImplBean_withSEI(); // server2
            // h.test_runtime_AnnotationInSEI();             // server3
            /*
            h.test_runtime_AnnotationInBoth();              // server1
            h.test_runtime_AnnotationInImplBean_withSEI(); // server2
            
            h.test_runtime_AnnotationInImplBean_noSEI();   // server4
            */ 
           // h.testRelativePathToHandler();                    // server5
            h.testURLPathToHandler() ;                      // server6
        }
    }


    /*
	 * @testStrategy put handler annotation on impl bean, but with sei, see if 
     * handler works. 
     * uses rpc, server2  
	 */
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="put handler annotation on impl bean, but with sei, see if  handler works. uses rpc, server2",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_AnnotationInImplBean_withSEI() throws Exception {
        System.out.println("*********** test_runtime_annotationInImplBean_withSEI (rpc) starting ***********");
        int result = 0;
        // put this dir on the classpath and invoke the client. 
        imp.setWorkingDir(buildDir+"/work/annotations/handlerchain/checkdefaults/client2");
        result = imp.invoke("annotations.handlerchain.checkdefaults.client2.AddNumbersClient",
                            hostAndPort+"/HandlerChainCheckDefaults2/HandlerChainCheckDefaults?wsdl");      

 		assertTrue("Incorrect response, handler(Impl) is NOT working..! (" +
 				   result + ")", (result == 50));
        System.out.println("***** test passed *********");
	}
    

    /*
	 * @testStrategy put handler annotation on sei, see if handler works.
     * uses rpc-lit mode, server3
	 */
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="put handler annotation on sei, see if handler works. uses rpc-lit mode, server3",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_AnnotationInSEI() throws Exception {
        System.out.println("*********** test_runtime_annotationInSEI starting (rpc) ***********");
    	int result = 0;
        // put this dir on the classpath and invoke the client. 
        imp.setWorkingDir(buildDir+"/work/annotations/handlerchain/checkdefaults/client3");
        result = imp.invoke("annotations.handlerchain.checkdefaults.client.AddNumbersClient",
                            hostAndPort+"/HandlerChainCheckDefaults3/HandlerChainCheckDefaults?wsdl");      

        /*
    	annotations.handlerchain.checkdefaults.client3.AddNumbersClient client = 
    		new annotations.handlerchain.checkdefaults.client3.AddNumbersClient();
    	result = client.callAddService(10, 20);
        */
 		assertTrue("Incorrect response, handler(SEI) is NOT working..! (" +
				   result + ")", (result == 208));
        System.out.println("***** test passed *********");
	}


    /*  
     * @testStrategy - test function of handler on an impl with sei.
 
     * Uses server1, annotated with file server1/etc/handlerImpl.xml, which defines 
     * annotations.handlerchain.checkdefaults.common.LoggingImplHandler 
     * Also puts LoggingSEIHandler on the interface. 
     * The impl handler, if working, should intercept the outbound soap message and
     * replace the first child element (30) with the value 50.
     * 
     * The SEIHandler works the same but should change the first child element
     * to 208.  
     * 
     * jsr181 4.6.1 says that if both sei and impl are annotated, the impl's
     * annotation should be used, so the outbound result should be 50.
     * 
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- test function of handler on an impl with sei. Uses server1, annotated with file server1/etc/handlerImpl.xml, which defines annotations.handlerchain.checkdefaults.common.LoggingImplHandler Also puts LoggingSEIHandler on the interface. The impl handler, if working, should intercept the outbound soap message and replace the first child element (30) with the value 50.  The SEIHandler works the same but should change the first child element to 208.  jsr181 4.6.1 says that if both sei and impl are annotated, the impl's annotation should be used, so the outbound result should be 50.  ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_AnnotationInBoth() throws Exception {
        System.out.println("*********** test_runtime_annotationInBoth starting ***********");
    	int result = 0;
        // put this dir on the classpath and invoke the client. 
        imp.setWorkingDir(buildDir+"/work/annotations/handlerchain/checkdefaults/client1");
        result = imp.invoke("annotations.handlerchain.checkdefaults.client.AddNumbersClient",
                            hostAndPort+"/HandlerChainCheckDefaults1/HandlerChainCheckDefaults?wsdl");    	
    	//result = client.callAddService(10, 20);
        
 		assertTrue("Incorrect response, handler(both) is NOT working..! (" +
				   result + ")", (result == 50));
      
        System.out.println("***** test passed *********");
	}
    
    /*  
     * @testStrategy - test function of handler on an impl with no sei.
     * Uses server4, annotated with file server4/etc/handlerImpl.xml, which defines 
     * annotations.handlerchain.checkdefaults.common.LoggingImplHandler 
     * 
     * The handler, if working, should intercept the outbound soap message and
     * replace the first child element (30) with the value 500. 
     * 
     * uses default soap mode, doc-lit-wr
     */    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- test function of handler on an impl with no sei. Uses server4, annotated with file server4/etc/handlerImpl.xml, which defines annotations.handlerchain.checkdefaults.common.LoggingImplHandler  The handler, if working, should intercept the outbound soap message and replace the first child element (30) with the value 500.  uses default soap mode, doc-lit-wr",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_AnnotationInImplBean_noSEI() throws Exception {
        System.out.println("*********** test_runtime_AnnotationInImplBean_noSEI starting ***********");
        int result = 0;
        // put this dir on the classpath and invoke the client. 
        imp.setWorkingDir(buildDir+"/work/annotations/handlerchain/checkdefaults/client4");
        result = imp.invoke("annotations.handlerchain.checkdefaults.client2.AddNumbersClient",
                            hostAndPort+"/HandlerChainCheckDefaults4/HandlerChainCheckDefaults?wsdl");      

        /*annotations.handlerchain.checkdefaults.client4.AddNumbersClient client = 
            new annotations.handlerchain.checkdefaults.client4.AddNumbersClient();
        result = client.callAddService(10, 20);
        */
        assertTrue("Incorrect response, handler(Impl) is NOT working..! (" +
                   result + ")", (result == 50));
        System.out.println("***** test passed *********");
    }
    
    /**
     * @testStrategy - same test as server4 but server5 has an annotation with a .. in the path.
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- same test as server4 but server5 has an annotation with a .. in the path. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRelativePathToHandler() throws Exception {
        System.out.println("*********** testRelativePathToHandler ***********");
        int result = 0;
        // put this dir on the classpath and invoke the client. 
        imp.setWorkingDir(buildDir+"/work/annotations/handlerchain/checkdefaults/client5");
        result = imp.invoke("annotations.handlerchain.checkdefaults.client2.AddNumbersClient",
                            hostAndPort+"/HandlerChainCheckDefaults5/HandlerChainCheckDefaults?wsdl");      

        assertTrue("Incorrect response, handler(Impl) is NOT working..! (" +
                   result + ")", (result == 50));
        System.out.println("***** test passed *********");
    }  
    
    /**
     * @testStrategy - check that http retrieval of handler file works.
     * Since we need the file available for the service to deploy,
     * we actually put server6's file into server5.  So server5 has to 
     * be deployed first. 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testURLPathToHandler() throws Exception {
        System.out.println("*********** testURLPathToHandler ***********");
        
        // Use liberty way to restart the test app 
        System.out.println("restarting application");        
        
        boolean started =  TopologyDefaults.libServer.restartDropinsApplication("HandlerChainCheckDefaults6.ear");
        if(started)
        {
        System.out.println("restart done");
        
 
        int result = 0;
        // first, make sure the handler xml file is reachable over http.
        String xmlUrl = hostAndPort+"/HandlerChainCheckDefaults5/handlersImpl.xml";
        assertTrue("test problem - handler xml file unreachable via http", Operations.getUrlStatus(xmlUrl, 30));        
        
        // put this dir on the classpath and invoke the client. 
        imp.setWorkingDir(buildDir+"/work/annotations/handlerchain/checkdefaults/client5");
        result = imp.invoke("annotations.handlerchain.checkdefaults.client2.AddNumbersClient",
                            hostAndPort+"/HandlerChainCheckDefaults6/HandlerChainCheckDefaults?wsdl");      

        assertTrue("Incorrect response, handler(Impl) is NOT working..! (" +
                   result + ")", (result == 50));
        System.out.println("***** test passed *********");
        }
        else
        {
     	   System.out.println("***** test failed as we can not restart app6! *********");
        }
    }  


    
    
        


    public static junit.framework.Test suite() {
    	System.out.println(HandlerChainCheckDefaultsTest.class.getName());
        return new TestSuite(HandlerChainCheckDefaultsTest.class);
    }   
	

}
