//
// @(#) 1.3 autoFVT/src/mtom/defaultsetting/wsfvt/test/MTOMDefaultSourceSOAPBindingPayloadTest.java, WAS.websvcs.fvt, WASX.FVT 4/19/07 13:01:38 [7/11/07 13:17:32]
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
// 03/06/07 jtnguyen    424875          called DefaultSetting_DispatchSource_.02()
// 04/18/07 jtnguyen    433294          Improved error logging

package mtom.defaultsetting.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import mtom.defaultsetting.wsfvt.client.MTOMDefaultSourceSOAPBindingPayloadClient;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 *
 * Clients use Dispatch<Source> with payload mode.
 */
public class MTOMDefaultSourceSOAPBindingPayloadTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public MTOMDefaultSourceSOAPBindingPayloadTest(String name) {
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
        return new TestSuite(MTOMDefaultSourceSOAPBindingPayloadTest.class);
    }
    
    // Test sending/receiving the message as Source type, image/jpeg MIME type
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_DispatchSource_PayloadMode_Image() throws Exception {
    	
    	MTOMDefaultSourceSOAPBindingPayloadClient client = new MTOMDefaultSourceSOAPBindingPayloadClient();
    	
        // compare
        assertEquals(goodResult, client.DefaultSetting_DispatchSource_01());
       
    }
    // Test sending/receiving the message as Source type, plain/text MIME type

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testDefaultSetting_DispatchSource_PayloadMode_Text() throws Exception  {
    	
    	MTOMDefaultSourceSOAPBindingPayloadClient client = new MTOMDefaultSourceSOAPBindingPayloadClient();
    	
        // compare
        assertEquals(goodResult, client.DefaultSetting_DispatchSource_02());
       
    }
 
}
