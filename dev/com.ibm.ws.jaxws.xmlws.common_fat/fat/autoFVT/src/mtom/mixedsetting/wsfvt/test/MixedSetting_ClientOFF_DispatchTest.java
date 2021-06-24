//
// @(#) 1.2 autoFVT/src/mtom/mixedsetting/wsfvt/test/MixedSetting_ClientOFF_DispatchTest.java, WAS.websvcs.fvt, WASX.FVT 2/25/07 20:40:43 [7/11/07 13:18:55]
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
// 02/25/07 jtnguyen    421977          Removed soap12 methods (can't use with soap 11 service endpoint)

package mtom.mixedsetting.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import mtom.mixedsetting.wsfvt.client.*;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 * 
 * 
 */
public class MixedSetting_ClientOFF_DispatchTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public MixedSetting_ClientOFF_DispatchTest(String name) {
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
        return new TestSuite(MixedSetting_ClientOFF_DispatchTest.class);
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatch_SmallJpeg_ServerONSOAP11_ClientOFFSOAP11() {
       	
 	   ClientOFF_MixedSetting_DispatchClient client = new ClientOFF_MixedSetting_DispatchClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentSmallJpeg());
 		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testDispatch_LargeJpeg_ServerONSOAP11_ClientOFFSOAP11() {
	   	
	   ClientOFF_MixedSetting_DispatchClient client = new ClientOFF_MixedSetting_DispatchClient();
   	
   	try {
            // compare
            assertEquals(goodResult, client.JAXB_AttachmentLargeJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }
  
 
  
}
