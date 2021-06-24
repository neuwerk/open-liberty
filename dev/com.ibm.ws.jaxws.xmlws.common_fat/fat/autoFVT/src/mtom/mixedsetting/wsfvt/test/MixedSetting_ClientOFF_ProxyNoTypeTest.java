//
// @(#) 1.1 autoFVT/src/mtom/mixedsetting/wsfvt/test/MixedSetting_ClientOFF_ProxyNoTypeTest.java, WAS.websvcs.fvt, WASX.FVT 10/17/06 22:28:25 [7/11/07 13:18:55]
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
public class MixedSetting_ClientOFF_ProxyNoTypeTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public MixedSetting_ClientOFF_ProxyNoTypeTest(String name) {
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
        return new TestSuite(MixedSetting_ClientOFF_ProxyNoTypeTest.class);
    }
    
   
  
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_NoMimeType_SmallJpeg_SeverONSOAP11_ClientOFF() {
   	
	   ClientOFF_MixedSetting_ProxyNoTypeClient client = new ClientOFF_MixedSetting_ProxyNoTypeClient();
   	
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
   public void testProxy_NoMimeType_LargeJpeg_SeverONSOAP11_ClientOFF() {
	   	
	   ClientOFF_MixedSetting_ProxyNoTypeClient client = new ClientOFF_MixedSetting_ProxyNoTypeClient();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }
 


}
