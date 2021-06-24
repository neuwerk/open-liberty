//
// @(#) 1.2 autoFVT/src/mtom/enableSOAPBinding/wsfvt/test/EnableBindingServerONSOAP12Test.java, WAS.websvcs.fvt, WASX.FVT 11/21/06 16:37:36 [7/11/07 13:18:40]
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
// 11/21/06 jtnguyen    407277          Fixed call to largeJPEG 

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
public class EnableBindingServerONSOAP12Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public EnableBindingServerONSOAP12Test(String name) {
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
        return new TestSuite(EnableBindingServerONSOAP12Test.class);
    }
    
    
    //-------------------------------
    // Tests EnableSBDispatchSOAP12Client
    //-------------------------------
    // Tests sending/receiving a message using Dispatch 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatch_SmallJpeg_SeverONSOAP12_ClientDefault()
    {
    	
    	EnableSBDispatchSOAP12Client client = new EnableSBDispatchSOAP12Client();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentSmallJpeg());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }
 
    // Tests sending/receiving a message with in-line attachment = a short string
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatch_LargeJpeg_SeverONSOAP12_ClientDefault() {
    	
    	EnableSBDispatchSOAP12Client client = new EnableSBDispatchSOAP12Client();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentLargeJpeg());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }
 
    //-------------------------------
    // Tests EnableSBProxySOAP12Client
    //-------------------------------

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SmallJpeg_SeverONSOAP12_ClientDefault() {
    	
	   EnableSBProxySOAP12Client client = new EnableSBProxySOAP12Client();
    	
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
   public void testProxy_LargeJpeg_SeverONSOAP12_ClientDefault() {
   	
	   EnableSBProxySOAP12Client client = new EnableSBProxySOAP12Client();
    	
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
   public void testProxy_XMLFile_SeverONSOAP12_ClientDefault() {
   	
	   EnableSBProxySOAP12Client client = new EnableSBProxySOAP12Client();
    	
    	try {
             // compare
             assertEquals(goodResult, client.Proxy_AttachmentFromXMLFile());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }

//----------------------------------
   
   //-------------------------------
   // Tests EnableSBProxyDataHandlerSOAP12Client
   //-------------------------------
  
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_MultipartMIME_SmallJpeg_ServerONSOAP12_ClientDefault() {
   	
	   EnableSBProxyDataHandlerSOAP12Client client = new EnableSBProxyDataHandlerSOAP12Client();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }


}
