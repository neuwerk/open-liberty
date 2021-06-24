//
// @(#) 1.1 autoFVT/src/mtom/enableSOAPBinding/wsfvt/test/EnableBindingServerONSOAP11Test.java, WAS.websvcs.fvt, WASX.FVT 10/10/06 10:54:26 [7/11/07 13:18:40]
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
 * 
 * 
 */
public class EnableBindingServerONSOAP11Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
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
        return new TestSuite(EnableBindingServerONSOAP11Test.class);
    }
    
    
    //-------------------------------
    // Tests DispatchImageJpegClient
    //-------------------------------
    // Tests sending/receiving a message using Dispatch 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatch_SmallJpeg_SeverONSOAP11_ClientDefault()
    {
    	
    	DispatchImageJpegClient client = new DispatchImageJpegClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentSmallJpeg());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }
 
    /** 
	 * A constructor to create a test case with a given name.
	 *
	 * @param name The name of the test case to be created
	 */
	public EnableBindingServerONSOAP11Test(String name) {
	    super(name);
	}

	// Tests sending/receiving a message with in-line attachment = a short string
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatch_LargeJpeg_SeverONSOAP11_ClientDefault() {
    	
    	DispatchImageJpegClient client = new DispatchImageJpegClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentLargeJpeg());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }
 
    //-------------------------------
    // Tests EnableSBProxyClient
    //-------------------------------

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SmallJpeg_SeverONSOAP11_ClientDefault() {
    	
	   EnableSBProxyClient client = new EnableSBProxyClient();
    	
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
   public void testProxy_LargeJpeg_SeverONSOAP11_ClientDefault() {
   	
	   EnableSBProxyClient client = new EnableSBProxyClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_XMLFile_SeverONSOAP11_ClientDefault() {
   	
	   EnableSBProxyClient client = new EnableSBProxyClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.Proxy_AttachmentFromXMLFile());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }

   //------------ EnableSBProxyDataHandlerClient soap11 on server -----------------
   
   //-------------------------------
   // Tests EnableSBProxyDataHandlerClient
   //-------------------------------
  
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_MultipartMIME_SmallJpeg_ServerONSOAP11_ClientDefault() {
   	
	   EnableSBProxyDataHandlerClient client = new EnableSBProxyDataHandlerClient();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }
   


}
