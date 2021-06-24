//
// @(#) 1.1 autoFVT/src/mtom/enableJAXB/wsfvt/test/EnableJAXBProxyTest.java, WAS.websvcs.fvt, WASX.FVT 10/8/06 08:51:42 [7/11/07 13:18:23]
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

package mtom.enableJAXB.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import mtom.enableJAXB.wsfvt.client.*;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 * 
 * 
 */
public class EnableJAXBProxyTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public EnableJAXBProxyTest(String name) {
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
        return new TestSuite(EnableJAXBProxyTest.class);
    }
    
    //---------------------------------------
    // tests EnableJAXBProxyDataHandlerClient
    //---------------------------------------

    // Test sending/receiving a message with in-line attachment
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProxy_ServerDefault_ClientON_MultipartMIME_smallJpeg() {
    	
    	EnableJAXBProxyDataHandlerClient client = new EnableJAXBProxyDataHandlerClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }
    //Proxy_SequenceLargeJpeg
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProxy_ServerDefault_ClientON_Sequence_MultipartMIME_smallJpeg() {
    	
    	EnableJAXBProxyDataHandlerClient client = new EnableJAXBProxyDataHandlerClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.Proxy_SequenceLargeJpeg());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }
   
     //---------------------------------------
    // tests EnableJAXBProxyImageJpegClient
    //---------------------------------------

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_ServerDefault_ClientON_ImageJpegMIME_smallJpeg() {
    	
	   EnableJAXBProxyImageJpegClient client = new EnableJAXBProxyImageJpegClient();
    	
	   	try {
           assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
	 	} catch (Exception ex) {
	 		   System.out.println("EnableJAXBProxyImageJpegClient.Proxy_AttachmentSmallJpeg - Client failed with exception.");	
	 		   ex.printStackTrace();     
	    }
      
    }



}
