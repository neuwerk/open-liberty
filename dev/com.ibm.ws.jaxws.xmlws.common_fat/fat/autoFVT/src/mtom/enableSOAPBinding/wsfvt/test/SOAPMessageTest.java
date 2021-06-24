//
// @(#) 1.2 autoFVT/src/mtom/enableSOAPBinding/wsfvt/test/SOAPMessageTest.java, WAS.websvcs.fvt, WASX.FVT 5/2/07 19:08:47 [7/11/07 13:18:41]
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
// 03/14/07 jtnguyen    LIDB3402-07.08  New File
// 05/02/07 jtnguyen    434549          Used Provider<SOAPMessage> and changed error logging

package mtom.enableSOAPBinding.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import mtom.enableSOAPBinding.wsfvt.client.*;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 * Clients use Dispatch<SOAPMessage>.
 */
public class SOAPMessageTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public SOAPMessageTest(String name) {
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
        return new TestSuite(SOAPMessageTest.class);
    }
    
    // Test sending/receiving the message as Source type, image/jpeg MIME type
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatchSOAPMessage_Text() throws Exception {
    	
    	SOAPMessageClient client = new SOAPMessageClient();
    	
        // compare
        assertEquals(goodResult, client.SOAPMessage_TextMessage());
       
    }
    //SOAPMessage_ImageMessage
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatchSOAPMessage_Image() throws Exception {
    	
    	SOAPMessageClient client = new SOAPMessageClient();
    	
        // compare
        assertEquals(goodResult, client.SOAPMessage_ImageMessage());
       
    }
    /*
     *  SOAP 12
     */
          
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatchSOAPMessage_SOAP12_Text() throws Exception {
    	
    	    	
        SOAPMessageSOAP12Client client = new SOAPMessageSOAP12Client();
    	
        // compare
        assertEquals(goodResult, client.SOAPMessage_TextMessage());
       
    }
    //SOAPMessage_ImageMessage
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatchSOAPMessage_SOAP12_Image() throws Exception {
    	
    	SOAPMessageSOAP12Client client = new SOAPMessageSOAP12Client();
    	
        // compare
        assertEquals(goodResult, client.SOAPMessage_ImageMessage());
       
    }

}
