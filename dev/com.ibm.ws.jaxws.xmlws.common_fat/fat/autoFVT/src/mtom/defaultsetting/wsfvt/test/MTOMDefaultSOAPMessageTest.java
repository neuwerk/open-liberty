//
// @(#) 1.2 autoFVT/src/mtom/defaultsetting/wsfvt/test/MTOMDefaultSOAPMessageTest.java, WAS.websvcs.fvt, WASX.FVT 4/19/07 13:01:40 [7/11/07 13:17:33]
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
// 04/18/07 jtnguyen    433294          Improved error logging

package mtom.defaultsetting.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import mtom.defaultsetting.wsfvt.client.*;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 * Clients use Dispatch<SOAPMessage>.
 */
public class MTOMDefaultSOAPMessageTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public MTOMDefaultSOAPMessageTest(String name) {
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
        return new TestSuite(MTOMDefaultSOAPMessageTest.class);
    }
    
    // Test sending/receiving the message as Source type, image/jpeg MIME type
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_DispatchSOAPMessage_Text() throws Exception {
    	
    	SOAPMessageClient client = new SOAPMessageClient();
    	
        // compare
        assertEquals(goodResult, client.DefaultSetting_SOAPMessage_TextMessage());
       
    }
    //DefaultSetting_SOAPMessage_ImageMessage
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_DispatchSOAPMessage_Image() throws Exception {
    	
    	SOAPMessageClient client = new SOAPMessageClient();
    	
        // compare
        assertEquals(goodResult, client.DefaultSetting_SOAPMessage_ImageMessage());
       
    }
    /*
     *  SOAP 12
     */
          
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_DispatchSOAPMessage_SOAP12_Text() throws Exception {
    	
    	    	
        SOAPMessageSOAP12Client client = new SOAPMessageSOAP12Client();
    	
        // compare
        assertEquals(goodResult, client.DefaultSetting_SOAPMessage_TextMessage());
       
    }
    //DefaultSetting_SOAPMessage_ImageMessage
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_DispatchSOAPMessage_SOAP12_Image() throws Exception {
    	
    	SOAPMessageSOAP12Client client = new SOAPMessageSOAP12Client();
    	
        // compare
        assertEquals(goodResult, client.DefaultSetting_SOAPMessage_ImageMessage());
       
    }

}
