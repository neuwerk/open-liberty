/*
 * @(#) 1.3 autoFVT/src/annotations/handlerchain/provider/test/HandlerChainProviderTest.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:15 [7/11/07 13:09:58]
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
 * 0524/2007   jramos      440922             Integrate ACUTE
 * 
 */
package annotations.handlerchain.provider.test;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.handlerchain.provider.client.AddNumbersClient;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

public class HandlerChainProviderTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
   
	private static String buildDir = null;
	private static String testDir  = null;
    private static String hostAndPort = null;
	static {
		buildDir = AppConst.FVT_HOME + File.separator +
		          "build";
		testDir = "annotations" + File.separator +
			      "handlerchain" + File.separator +
			      "checkdefaults";
        
        // figure out host and port.
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
	}
    
	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public HandlerChainProviderTest(String name) {
		super(name);
	}

    /*
     * 
     * @testStrategy  - test server-side handler on a provider. 
     * Required by jsr224 9.2.1.3.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description=" - test server-side handler on a provider.  Required by jsr224 9.2.1.3.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProvider() throws Exception {
        System.out.println("************** testProvider is starting *********");
    	int result = 0;
        String url = hostAndPort+"/HandlerChainProvider1/AddNumbersImplService?wsdl";
        
        System.out.println("Calling service with args "+url+ "  10 20");
    	result = AddNumbersClient.callAddService(url, 10, 20);
        /* we expect the handler to intercept the first input argument message and
         * change it to 50, so the result should be 70.
         *         */ 
        
        System.out.println("received response:"+result);
         
 		assertTrue("Incorrect response, handler(Impl) is NOT working..! (" +
 				   result + ")", (result == 70));
        
        System.out.println("********** test passed ***********");
	}

    public static junit.framework.Test suite() {
    	System.out.println(HandlerChainProviderTest.class.getName());
        return new TestSuite(HandlerChainProviderTest.class);
    }   
	
	public static void main(String[] args) {	
		TestRunner.run(suite());
	}
}
