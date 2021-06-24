//
// @(#) 1.3 autoFVT/src/mtom/defaultsetting/wsfvt/test/defaultsettingTest.java, WAS.websvcs.fvt, WASX.FVT 4/19/07 13:01:36 [7/11/07 13:17:32]
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
// 01/25/07 jtnguyen    410864          Removed invalid Message mode test
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
 * 
 * 
 */
public class defaultsettingTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public defaultsettingTest(String name) {
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
        return new TestSuite(defaultsettingTest.class);
    }
    
    //---------------------------
    // tests JAXBImageJpegClient
    //---------------------------
    
    // Tests sending/receiving a message with in-line attachment = a small image
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_Dispatch_SmallJpeg() throws Exception {
    	
    	JAXBImageJpegClient client = new JAXBImageJpegClient();
    	assertEquals(goodResult, client.JAXB_AttachmentSmallJpeg());
           
    }
    // Test sending/receiving a message with in-line attachment = a large image
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void ttestDefaultSetting_Dispatch__LargeJpeg() throws Exception  {
    	
    	JAXBImageJpegClient client = new JAXBImageJpegClient();
        assertEquals(goodResult, client.JAXB_AttachmentLargeJpeg());
    }
      
    //---------------------------
    // tests JAXBTextPlainClient
    //---------------------------
  
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_Dispatch__ShortString() throws Exception {
    	
    	JAXBTextPlainClient client = new JAXBTextPlainClient();
        assertEquals(goodResult, client.JAXB_AttachmentShortString());
	    
    }
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_Dispatch__LongString() throws Exception  {
    	
    	JAXBTextPlainClient client = new JAXBTextPlainClient();   
    	assertEquals(goodResult, client.JAXB_AttachmentLongString());
		    
    }
    
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaultSetting_Dispatch_FromXMLFile() throws Exception {
    	
    	JAXBTextPlainClient client = new JAXBTextPlainClient();   
    	assertEquals(goodResult, client.JAXB_AttachmentFromFile());
	    
    }

}
