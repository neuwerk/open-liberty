/*
 * @(#) 1.9 autoFVT/src/annotations/webfault/customization/test/WebFaultCustomizationTest.java, WAS.websvcs.fvt, WASX.FVT 2/16/07 10:22:06 [7/11/07 13:11:39]
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
 * 08/25/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.webfault.customization.test;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.webfault.customization.client.WebFaultCustomizationClient;
import annotations.webfault.customization.server.WebFaultCustomizationException;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/*
 * notes 1.10.07 - rewriting to test fully qualified webfault annotation.  Removing binding
 * customization test for now.  Test is passing. 
 */
public class WebFaultCustomizationTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    
	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public WebFaultCustomizationTest(String name) {
		super(name);
	}

    /* @testStrategy - cause a WebFaultCustomizationException to be thrown
     * by the server.  It should be returned to the client, and the use of
     * inline bindings in the wsdl should cause it to be transformed to 
     * a MultiplyNumbersException. 
     *
	 */
    /**
     * removed until we get the base case working. 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_ExceptionExpected_Multiply() throws Exception {
        System.out.println("******** running test_runtime_ExceptionExpected_Multiply *******");
    	int result = 0;
    	result = WebFaultCustomizationClient.callMultiplyService(-10, 20);
 		assertTrue("UNEXPECTED result..! (" + result + ")", (result == -2));
        System.out.println("******* test passed ********");
 		
	}
    **/

    /* @testStrategy - cause a WebFaultCustomizationException to be thrown
     * by the server.  It should be returned to the client and marshalled
     * as such. This checks our ability to customize an exception starting 
     * from Java using the webfault annotation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- cause a WebFaultCustomizationException to be thrown by the server.  It should be returned to the client and marshalled as such. This checks our ability to customize an exception starting from Java using the webfault annotation.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_runtime_ExceptionExpected_Add() throws Exception {
        System.out.println("******** running test_runtime_ExceptionExpected_Add() *******");
    	int result = 0;
    	result = WebFaultCustomizationClient.callAddService(-10, 20);
        // client will return 1 if it catches a webfaultcustomization exception. 
 		assertTrue("UNEXPECTED result..! (" + result + ")", (result == -1)); 		
 		System.out.println("******* test passed ********");
	}
    
    public static junit.framework.Test suite() {
    	System.out.println(WebFaultCustomizationTest.class.getName());
        return new TestSuite(WebFaultCustomizationTest.class);
    }   
	
	public static void main(String[] args) throws Exception {	
		if(true){
            TestRunner.run(suite());
        } else{
            new WebFaultCustomizationTest("").test_runtime_ExceptionExpected_Add();
            //new WebFaultCustomizationTest("").test_runtime_ExceptionExpected_Multiply();
        }
	}
}
