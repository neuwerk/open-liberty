//
// @(#) 1.1 autoFVT/src/mtom/disableSOAPBinding/wsfvt/test/ClientDefault_DisableSB_DispatchTest.java, WAS.websvcs.fvt, WASX.FVT 10/5/06 23:00:12 [7/11/07 13:18:07]
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

package mtom21.disablesoapbinding.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import mtom21.disablesoapbinding.wsfvt.client.DisableSBDispatchSOAP12Client;
import mtom21.disablesoapbinding.wsfvt.client.DispatchImageJpegClient;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 * 
 * 
 */
public class ClientDefault_DisableSB_DispatchTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public ClientDefault_DisableSB_DispatchTest(String name) {
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
        return new TestSuite(ClientDefault_DisableSB_DispatchTest.class);
    }
    
    
    //-------------------------------
    // Tests DispatchImageJpegClient
    //-------------------------------
    // Tests sending/receiving a message using Dispatch 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_SmallJpeg_SeverOFFSOAP11_ClientDefault()
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
 
    // Tests sending/receiving a message with in-line attachment = a short string
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_LargeJpeg_SeverOFFSOAP11_ClientDefault() {
    	
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
    // Tests DisableSBDispatchSOAP12Client
    //-------------------------------
    // Tests sending/receiving a message using Dispatch 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_SmallJpeg_SeverOFFSOAP12_ClientDefault()
    {
    	
    	DisableSBDispatchSOAP12Client client = new DisableSBDispatchSOAP12Client();
    	
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
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_LargeJpeg_SeverOFFSOAP12_ClientDefault() {
    	
    	DisableSBDispatchSOAP12Client client = new DisableSBDispatchSOAP12Client();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentLargeJpeg());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }

}
