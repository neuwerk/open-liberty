//
// @(#) 1.1 autoFVT/src/mtom/disableSOAPBinding/wsfvt/test/DisableSB_DataHandler_ProxyTest.java, WAS.websvcs.fvt, WASX.FVT 10/5/06 23:00:28 [7/11/07 13:18:09]
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

package mtom.disableSOAPBinding.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import mtom.disableSOAPBinding.wsfvt.client.*;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 * 
 * 
 */
public class DisableSB_DataHandler_ProxyTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public DisableSB_DataHandler_ProxyTest(String name) {
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
        return new TestSuite(DisableSB_DataHandler_ProxyTest.class);
    }
    
    //------------------------------------
   // Tests DisableSBProxyDataHandlerClient
   //------------------------------------
  
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxyDataHandler_SmallJpeg_SeverOFFSOAP11_ClientDefault() {
   	
	   DisableSBProxyDataHandlerClient client = new DisableSBProxyDataHandlerClient();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }
   //------------------------------------
   // Tests DisableSBProxyDataHandlerSOAP12Client
   //------------------------------------
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxyDataHandler_SmallJpeg_SeverOFFSOAP12_ClientDefault() {
	   	
	   DisableSBProxyDataHandlerSOAP12Client client = new DisableSBProxyDataHandlerSOAP12Client();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }
   //------------------------------------
   // Tests ClientOFF_DisableSBProxyDataHandlerClient
   //------------------------------------

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxyDataHandler_SmallJpeg_SeverOFFSOAP11_ClientOFF() {
	   	
	   ClientOFF_DisableSBProxyDataHandlerClient client = new ClientOFF_DisableSBProxyDataHandlerClient();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxyDataHandler_LargeJpeg_SeverOFFSOAP11_ClientOFF() {
	   	
	   ClientOFF_DisableSBProxyDataHandlerClient client = new ClientOFF_DisableSBProxyDataHandlerClient();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }

   //------------------------------------
   // Tests ClientOFF_DisableSBProxyDataHandlerSOAP12Client
   //------------------------------------
  
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxyDataHandler_SmallJpeg_SeverOFFSOAP12_ClientOFF() {
   	
	   ClientOFF_DisableSBProxyDataHandlerSOAP12Client client = new ClientOFF_DisableSBProxyDataHandlerSOAP12Client();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxyDataHandler_LargeJpeg_SeverOFFSOAP12_ClientOFF() {
	   	
	   ClientOFF_DisableSBProxyDataHandlerSOAP12Client client = new ClientOFF_DisableSBProxyDataHandlerSOAP12Client();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }


}
