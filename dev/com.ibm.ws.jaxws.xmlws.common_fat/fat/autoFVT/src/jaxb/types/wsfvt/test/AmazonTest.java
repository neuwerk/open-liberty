//
// @(#) 1.2 autoFVT/src/jaxb/types/wsfvt/test/AmazonTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/14/09 12:43:09 [8/8/12 06:57:15]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 01/29/08 ulbricht    290938          New File
// 04/14/09 btiffany    561054          add cache test
//

package jaxb.types.wsfvt.test;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import jaxb.types.wsfvt.client.AWSECommerceServiceClient;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import com.ibm.ws.wsfvt.test.framework.FvtTest;

/**
 * This class will test the ability to use the Amazon AWSE
 * WSDL.
 */
public class AmazonTest extends FVTTestCase {

    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public AmazonTest(String name) {
        super(name);
    }

    /** 
     * The main method.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /** 
     * This method controls which test methods to run.
     *
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(AmazonTest.class);
    }
    
    /** 
     * @testStrategy This test calls the help method from the Amazon
     *               WSDL and verifies that the correct request processing
     *               time is returned.  Please see defect 290938 for more
     *               explanation of the WSDL characteristics that make the
     *               help and multiOperation methods interesting
     * @throws Exception Any kind of exception 
     */
    @FvtTest(description="This test calls the help method from the Amazon WSDL and verifies that the correct request processing time is returned.  Please see defect 290938 for more explanation of the WSDL characteristics that make the help and multiOperation methods interesting.",
             expectedResult="",
             since=FvtTest.Releases.WSFP)
    public void testAmazonHelpService() throws Exception {
        AWSECommerceServiceClient client = new AWSECommerceServiceClient();
        String expected = "Request Processing Time = 1.2323289";
        String actual = client.help();
        assertEquals(expected, actual);
    }

    /** 
     * @testStrategy This test calls the help method from the Amazon WSDL
     *               and verifies that the correct request processing time
     *               is returned.  Please see defect 290938 for more explanation
     *               of the WSDL characteristics that make the help and
     *               multiOperation methods interesting
     * @throws Exception Any kind of exception 
     */
    @FvtTest(description="This test calls the multiOperation method from the Amazon WSDL and verifies that the correct request processing time is returned.  Please see defect 290938 for more explanation of the WSDL characteristics that make the help and multiOperation methods interesting.",
             expectedResult="",
             since=FvtTest.Releases.WSFP)
    public void testAmazonMultiOperationService() throws Exception {
        AWSECommerceServiceClient client = new AWSECommerceServiceClient();
        String expected = "Request Processing Time = 1.23232";
        String actual = client.multiOperation();
        assertEquals(expected, actual);
        
       
    }

    /**
     * call each test method multiple times to see if we can find any
     * caching problems in jaxb fastpath. 
     */
    public void testCaching()throws Exception {
        testAmazonMultiOperationService();
        testAmazonHelpService();
        testAmazonMultiOperationService();
        testAmazonHelpService();
        testAmazonMultiOperationService();
        testAmazonHelpService();
        testAmazonMultiOperationService();
        testAmazonHelpService();
        
    }
}
