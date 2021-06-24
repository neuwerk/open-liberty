//
// autoFVT/src/jaxb/types/wsfvt/test/JaxbTypesTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/02/06 ulbricht    420151          New File
// 04/14/09 btiffany    561054          add cache test
//

package jaxb.types.wsfvt.test;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import jaxb.types.wsfvt.client.JaxbTypesClient;

/**
 * This class will test the ability to test various real
 * customer WSDLs.
 */
public class JaxbTypesTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public JaxbTypesTest(String name) {
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
        return new TestSuite(JaxbTypesTest.class);
    }
    
    /** 
     * This test method will verify that a JAX-WS Proxy based
     * client can used to call a mock Wachovia based Web
     * service.
     *
     * @testStrategy This test uses a Wachovia WSDL/XSD to
     *               create a service that uses the JAX-WS
     *               Proxy client API to multiply two
     *               numbers. 
     * @throws Exception Any kind of exception 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWachoviaWsdl() throws Exception {
        JaxbTypesClient client = new JaxbTypesClient();
        String expected = "30";
        String actual = client.callService(null);
        assertEquals(expected, actual);
    }
    
    /**
     * call each test method multiple times to see if we can find any
     * caching problems in jaxb fastpath. 
     */
    public void testCaching()throws Exception {
        testWachoviaWsdl();
        testWachoviaWsdl();
        testWachoviaWsdl();    
    }

}
