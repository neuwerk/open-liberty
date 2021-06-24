/*
 * @(#) 1.8 autoFVT/src/annotations/partialwsdl/test/PartialWsdlTest.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:45 [7/11/07 13:10:35]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * This was a late add to the test bucket so failures will be
 * logged against annotations.webservice for tracking. 
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 06/26/2006  euzunca     LIDB3296.31.01     new file
 * 06/29/2006  euzunca     LIDB3296.31.01     javadoc/import/path corrections
 * 08/01/2006  euzunca     LIDB3296.31.01     improved the test method implementations
 * 01/04/2007 btiffany                        clarify testStrategy notes
 * 05/24/2007  jramos      440922             Integrate ACUTE
 */
package annotations.partialwsdl.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.partialwsdl.client.AddNumbersClient;

import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

public class PartialWsdlTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    private static String hostAndPort = null;
    private static IAppServer server = null;
    
    static{     
        server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                + server.getPortMap().get(Ports.WC_defaulthost);
    }
      
	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public PartialWsdlTest(String name) {
		super(name);
	}

	/**
	 * @testStrategy - invoke a service with no wsdl and verify it works. 
     * There are numerous other annotation tests that use dynamic wsdl as well.  
     * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_NoWsdl() throws Exception {
        String url = hostAndPort+"/partialwsdlnw/services/AddNumbersImplNoWsdlService";        
        assertTrue ("Service did not come up: "+url, Operations.getUrlStatus(url, 30));
    	int result = 0;
    	result = AddNumbersClient.addNumbers_NoWsdl(10, 20);
 		assertTrue("Unexpected result..! (" + result + ")", (result == 30));
 		System.out.println("test_runtime_NoWsdl(): " + 
 				"service responded with the correct answer...(" + result + ")");
	}
    
    /**
	 * @testStrategy - given a war file with partial wsdl, verify that the server can 
     * deploy it and we can invoke it.  Definitions through port type is present, bindings 
     * are missing. 
     * 
     * We test case 5.2.5.6 (schema only) in WebParamAnnoBetaDynamicWsdl
	 */	
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- given a war file with partial wsdl, verify that the server can  deploy it and we can invoke it.  Definitions through port type is present, bindings are missing.  We test case 5.2.5.6 (schema only) in WebParamAnnoBetaDynamicWsdl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_Partial1() throws Exception {
        String url = hostAndPort+"/partialwsdlp1/services/AddNumbersImplPartial1Service";        
        assertTrue ("def 413971? - Service did not come up: "+url, Operations.getUrlStatus(url, 30));
    	int result = 0;
    	result = AddNumbersClient.addNumbers_Partial1(10, 20);
 		assertTrue("Unexpected exception..! (" + result + ")", (result == 30));
 		System.out.println("test_runtime_Partial1(): " + 
 				"service responded with correct result...");
	}    

    /*
     * @testStrategy - given a war file with partial wsdl, verify that the server can 
     * deploy it and we can invoke it.  Definitions through port type is present, bindings 
     * are missing.  Soap 1.2 is used.
     * 
     *  1.10.07 - removed, suspected invalid.  If bindings are missing, soap12 is never
     *  going to be supplied as a binding type because it is a nonstandard extension.
     * 
     *
	public void _test_runtime_Partial2() throws Exception {
        String url = hostAndPort+"/partialwsdlp2/services/AddNumbersImplPartial2Service";        
        assertTrue ("Service did not come up: "+url, Operations.getUrlStatus(url, 30));
    	int result = 0;
    	result = AddNumbersClient.addNumbers_Partial2(10, 20);
 		assertTrue("Unexpected exception..! (" + result + ")", (result == 30));
 		
 		System.out.println("test_runtime_Partial2(): " + "service responded with the correct answer...");
	}
    */

    /*public void test_runtime_Zero() throws Exception {
    	int result = 0;
    	result = AddNumbersClient.addNumbers_Zero(10, 20);
 		assertTrue("Unexpected result..! (" + result + ")", (result == 30));
 		System.out.println("test_runtime_NoWsdl(): " + "service responded with the correct answer...");
	}*/
    
    /* here we just check that, given an "empty" wsdl, the service can 
     * generate one.  (jsr 224 5.2.5.6) 
     * TODO: write a client.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_Partial2() throws Exception {
        String wsdl = Operations.getUrlContents(hostAndPort+"/partialwsdlp2/services/AddNumbersImplPartial2Service?wsdl",30);
        assertTrue("WSDL not generated or incorrect",
                    wsdl.contains("name=\"AddNumbersImplPartial2Port\"" ));
    }

    public static junit.framework.Test suite() {
    	System.out.println(PartialWsdlTest.class.getName());
        return new TestSuite(PartialWsdlTest.class);
    }   
	
	public static void main(String[] args) {	
		TestRunner.run(suite());
	}
}
