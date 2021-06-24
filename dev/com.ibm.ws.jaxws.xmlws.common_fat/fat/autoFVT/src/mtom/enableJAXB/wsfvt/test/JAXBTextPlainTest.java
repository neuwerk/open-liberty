//
// @(#) 1.2 autoFVT/src/mtom/enableJAXB/wsfvt/test/JAXBTextPlainTest.java, WAS.websvcs.fvt, WASX.FVT 1/26/07 13:45:57 [7/11/07 13:18:24]
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
public class JAXBTextPlainTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public JAXBTextPlainTest(String name) {
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
        return new TestSuite(JAXBTextPlainTest.class);
    }
    
    
    // Test sending/receiving a message with in-line attachment = a short string
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatch_ServerDefault_ClientON_PlainTextMIME_ShortString() {
    	
    	EnableJAXBTextPlainClient client = new EnableJAXBTextPlainClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentShortString());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatch_ServerDefault_ClientON_PlainTextMIME_LongString() {
    	
    	EnableJAXBTextPlainClient client = new EnableJAXBTextPlainClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentLongString());
		} catch (Exception ex) {
  		   System.out.println("Test failed with exception.");	
  		   ex.printStackTrace();     
  		}
    }
    
        
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatch_ServerDefault_ClientON_PlainTextMIME_FromXMLFile() {
    	
    	EnableJAXBTextPlainClient client = new EnableJAXBTextPlainClient();
    	
    	try {
             // compare
             assertEquals(goodResult, client.JAXB_AttachmentFromFile());
		} catch (Exception ex) {
  		   ex.printStackTrace();     
  		}
    }
    
    
}
