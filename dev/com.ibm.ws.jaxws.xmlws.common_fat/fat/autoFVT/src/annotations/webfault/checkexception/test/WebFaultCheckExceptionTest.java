/*
 * %Z% %I% %W% %G% %U% [%H% %T%]
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
 * 06/26/2006  euzunca     LIDB3296.31.01     new file
 * 06/29/2006  euzunca     LIDB3296.31.01     javadoc/import/path corrections
 * 08/01/2006  euzunca     LIDB3296.31.01     improved the test method implementations
 * 01/03/2007  btiffany                       make url passable to client.
 * 05/24/2007  jramos      440922             Integrate ACUTE
 */
package annotations.webfault.checkexception.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.webfault.checkexception.client.AddNumbersClient;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/*
 * @testStrategy For this annotation, the spec uses the phrase "in the absence
 * 				 of customizations". We make the assumption that use of the 
 * 				 annotation itself without any property (i.e., @WebFault) is 
 * 				 legitimate for any conformance point that is bounded by 
 * 				 this phrase. In other words, absence of annotation and
 * 				 absence of customizations identify two different situations. 
 * 
 * We test the ability to return an unannotated legacy exception to the client. 
 * See the customization package for annotated exceptions. 
 * 
 * Notes:
 * 1.17.07 g0702.02 - I thought this had passed before, but now it looks like the
 * fault is not being properly demarshalled - it's coming back as a webserviceexception.
 * That was 412051 which we cancelled because this test was passing before. 
 * Class files from client and server were intermixed on the wrong sides, rewrote the buildfile.
 * Now that we have dynamic wsdl, we have an unmarshal exception on the most basic method,
 * that worked before.   This class has only one method, so the soap:action should not
 * be the problem now.  Cking with N.T., he may already have a defect on this...
 * yes, we're blocked by 414477
 * 
 * 1.19 g0702.02 - put NT's patch on for demarshalling, can get an invoke now, but something
 * else has gone wrong, can't retrieve schema, Dustin can't recreate. 
 * 
 * Opened 412051.1 for demarshal problem. 
 * 
 * 2.5.07 n0704.24 - passing
 * 
 * 
 * 
 */

public class WebFaultCheckExceptionTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    
    private final static String hostAndPort;
    private static String url;
    static{
        // figure out host and port.
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                + server.getPortMap().get(Ports.WC_defaulthost);
        url = hostAndPort+"/webfaultchkexc/services/WebFaultCheckExceptionService?wsdl";
    }
    
    public static void main(String[] args)throws Exception {    
        if(false){
            TestRunner.run(suite());
        } else{
         // new WebFaultCheckExceptionTest("").test_runtime_NoExceptionExpected();
         //  new WebFaultCheckExceptionTest("")._test_runtime_tcpmon_ExceptionExpected();
          new WebFaultCheckExceptionTest("").test_runtime_ExceptionExpected();
        }
    }
    
   
	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public WebFaultCheckExceptionTest(String name) {
		super(name);
	}

    /*
	 * This test method will verify that a web service DOES NOT throw exception
	 * (when not expected) as specified in the implementation.
	 * 
	 * @testStrategy This test builds the given service implementation,
	 * generates the war file and deploys the service to the server. Service
	 * implementation specifies that an exception will be thrown if any of the
	 * arguments passed to the server is a negative integer. This test will
	 * check the case in which none of the arguments are negative and thus no
	 * exception occurs.
	 */	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test builds the given service implementation, generates the war file and deploys the service to the server. Service implementation specifies that an exception will be thrown if any of the arguments passed to the server is a negative integer. This test will check the case in which none of the arguments are negative and thus no exception occurs.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_NoExceptionExpected() throws Exception {
        System.out.println("******** running test_runtime_NoExceptionExpected()**********");
    	int result = 0;
    	result = AddNumbersClient.callAddService(url, 10, 20);
 		assertTrue("Unexpected exception..! (" + result + ")", (result == 30));
 		System.out.println("******* test passed ****************");
	}
    
    /*
	 * This test method will verify that a web service throws exception as
	 * specified in the implementation.
	 * 
	 * @testStrategy This test builds the given service implementation,
	 * generates the war file and deploys the service to the server. Service
	 * implementation specifies that an exception will be thrown if any of the
	 * arguments passed to the server is a negative integer. This test will
	 * check the case in which one of the arguments is negative and thus an
	 * exception is thrown.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test builds the given service implementation, generates the war file and deploys the service to the server. Service implementation specifies that an exception will be thrown if any of the arguments passed to the server is a negative integer. This test will check the case in which one of the arguments is negative and thus an exception is thrown.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_runtime_ExceptionExpected() throws Exception {
        System.out.println("******** running test_runtime_ExceptionExpected()**********");
    	int result = 0;
    	result = AddNumbersClient.callAddService(url, -10, 20);
 		assertTrue("Unexpected exception..!", (result == -1));
        //fail("need to remove @WebFault from exception class");
        System.out.println("******* test passed ****************");
 		
	}
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _test_runtime_tcpmon_ExceptionExpected() throws Exception {
        System.out.println("******** running test_runtime_tcpmon_ExceptionExpected()**********");
        int result = 0;
        /* - this didn't work, manually retrieve and patch the wsdl
        String wsdl = Operations.getUrlContents(url, 30);
        wsdl=wsdl.replace("btiffany:9080", "localhost:19080");
        Operations.writeFile("/tmp/temp.wsdlstatic", wsdl);
        */
        url = "file:/tmp/AddNumbersImplService.wsdlstatic";
        result = AddNumbersClient.callAddService(url, 10, 20);
        System.out.println("10 + 20 equals:"+ result);        
        result = AddNumbersClient.callAddService(url, -10, 20);
        assertTrue("Unexpected exception..!", (result == -1));
        System.out.println("******* test passed ****************");
        
    }

    public static junit.framework.Test suite() {
    	System.out.println(WebFaultCheckExceptionTest.class.getName());
        return new TestSuite(WebFaultCheckExceptionTest.class);
    }
	
}
