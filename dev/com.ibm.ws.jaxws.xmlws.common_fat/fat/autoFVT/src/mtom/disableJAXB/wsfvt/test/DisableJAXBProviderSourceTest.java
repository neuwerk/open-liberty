//
// @(#) 1.1 autoFVT/src/mtom/disableJAXB/wsfvt/test/DisableJAXBProviderSourceTest.java, WAS.websvcs.fvt, WASX.FVT 10/5/06 11:56:46 [7/11/07 13:17:53]
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
// 09/25/06 jtnguyen    LIDB3402-07.01  New File

package mtom.disableJAXB.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import mtom.disableJAXB.wsfvt.client.*;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 */
public class DisableJAXBProviderSourceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public DisableJAXBProviderSourceTest(String name) {
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
        return new TestSuite(DisableJAXBProviderSourceTest.class);
    }
    
    // Test sending/receiving the message as Source type, image/jpeg MIME type
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProviderSource_ServerDefault_ClientOFF_Image() {
    	
    	DisableJAXBProviderSourceClient client = new DisableJAXBProviderSourceClient();
    	
        // compare
        assertEquals(goodResult, client.SourceSOAP11Payload_01());
       
    }
    // Test sending/receiving the message as Source type, plain/text MIME type

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProviderSource_ServerDefault_ClientOFF_Text() {
    	
	   DisableJAXBProviderSourceClient client = new DisableJAXBProviderSourceClient();
    	
        // compare
        assertEquals(goodResult, client.SourceSOAP11Payload_02());
       
    }
   //SourceSequenceSOAP11Payload
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProviderSource_ServerDefault_ClientOFF_TextSequence() {
   	
	   DisableJAXBProviderSourceClient client = new DisableJAXBProviderSourceClient();
    	
        // compare
        assertEquals(goodResult, client.SourceSequenceSOAP11Payload());
       
    }

}
