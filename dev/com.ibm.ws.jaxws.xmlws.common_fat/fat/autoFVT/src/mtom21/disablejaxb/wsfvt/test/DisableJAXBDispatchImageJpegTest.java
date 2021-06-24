//
// @(#) 1.1 autoFVT/src/mtom21/disablejaxb/wsfvt/test/DisableJAXBDispatchImageJpegTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:27:33 [8/8/12 06:40:44]
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

package mtom21.disablejaxb.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import mtom21.disablejaxb.wsfvt.client.DisableJAXBDispatchImageJpegClient;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * This class will test the ability to reach a JAX-WS based server side implementation after the EAR
 * has been installed into WebSphere Application Server.
 * 
 * 
 */
public class DisableJAXBDispatchImageJpegTest extends FVTTestCase {

    public static final String goodResult = "Message processed";
    
    private static DisableJAXBDispatchImageJpegClient client = new DisableJAXBDispatchImageJpegClient();
       
    /**
     * A constructor to create a test case with a given name.
     * 
     * @param name The name of the test case to be created
     */
    public DisableJAXBDispatchImageJpegTest(String name) {
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
        return new TestSuite(DisableJAXBDispatchImageJpegTest.class);
    }

    // Test sending/receiving a message with in-line attachment
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_SOAP11_SmallJpeg() throws Exception {
        // compare
        assertEquals(goodResult, client.JAXB_AttachmentSmallJpeg());
    }

    // Test sending/receiving a message with in-line attachment
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "Verify that MTOM is not used over the wire.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_SOAP11_SmallJpegverifyMTOMOFF() throws Exception {
        // compare
        assertEquals(goodResult, client.jaxb_Sequence_SmallJpeg_VerifyMTOMOFF());
    }

    // Test sending/receiving a message with in-line attachment
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_SOAP11_LargeJpeg() throws Exception {
        // compare
        assertEquals(goodResult, client.JAXB_AttachmentLargeJpeg());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "Verify that MTOM is not used over the wire.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_SOAP11_LargeJpeg_MTOMOFF() throws Exception {
        // compare
        assertEquals(goodResult, client.jaxb_AttachmentLargeJpeg_verifyMTOMOFF());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_DoubleSets_LargeJpeg_verifyMTOMOFF() throws Exception {
        // compare
        assertEquals(goodResult, client.jaxb_doubleSets_AttachmentLargeJpeg_VerifyMTOMOFF());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_DoubleSets_LargeJpeg() throws Exception {
        // compare
        assertEquals(goodResult, client.JAXB_doubleSets_AttachmentLargeJpeg());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_SmallJpegSequence() throws Exception {
        // compare
        assertEquals(goodResult, client.JAXB_Sequence_SmallJpeg());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_SmallJpegSequence_verifyMTOMOFF() throws Exception {
        // compare
        assertEquals(goodResult, client.jaxb_Sequence_SmallJpeg_VerifyMTOMOFF());
    }
}
