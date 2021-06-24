//
// @(#) 1.1 autoFVT/src/mtom21/mixedsetting/wsfvt/test/MixedSetting_ClientON_ProxyNoTypeTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:38:45 [8/8/12 06:40:58]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/10/08 jramos      LIDB4418-12.01  New File

package mtom21.mixedsetting.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import mtom21.mixedsetting.wsfvt.client.ClientON_MixedSetting_ProxyNoTypeClient;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 * 
 * 
 */
public class MixedSetting_ClientON_ProxyNoTypeTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public MixedSetting_ClientON_ProxyNoTypeTest(String name) {
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
        return new TestSuite(MixedSetting_ClientON_ProxyNoTypeTest.class);
    }
    
   
  
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_NoMimeType_SmallJpeg_SeverOFFSOAP11_ClientON() {
	   	
	   ClientON_MixedSetting_ProxyNoTypeClient client = new ClientON_MixedSetting_ProxyNoTypeClient();
   	
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
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
   public void testProxy_NoMimeType_LargeJpeg_SeverOFFSOAP11_ClientON() {
	   	
	   ClientON_MixedSetting_ProxyNoTypeClient client = new ClientON_MixedSetting_ProxyNoTypeClient();
   	
   	try {
            // compare
            assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
		} catch (Exception ex) {
 		   System.out.println("Test failed with exception.");	
 		   ex.printStackTrace();     
 		}
   }



}
