//
// @(#) 1.1 autoFVT/src/mtom21/disablejaxb/wsfvt/test/DisableJAXBDispatchTextPlainTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:27:35 [8/8/12 06:40:44]
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
import mtom21.disablejaxb.wsfvt.client.DisableJAXBTextPlainClient;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * This class will test the ability to reach a JAX-WS based server side implementation after the EAR
 * has been installed into WebSphere Application Server.
 * 
 * 
 */
public class DisableJAXBDispatchTextPlainTest extends FVTTestCase {

    public static final String goodResult = "Message processed";

    /**
     * A constructor to create a test case with a given name.
     * 
     * @param name The name of the test case to be created
     */
    public DisableJAXBDispatchTextPlainTest(String name) {
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
        return new TestSuite(DisableJAXBDispatchTextPlainTest.class);
    }

    // Test sending/receiving a message with in-line attachment = a short string
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_plaintextMIME_ShortString_VerifyMTOM() throws Exception {

        DisableJAXBTextPlainClient client = new DisableJAXBTextPlainClient();

            // compare
            assertEquals(goodResult, client.jaxb_AttachmentShortString_VerifyMTOM());
    }

    // Test sending/receiving a message with in-line attachment = a short string
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_plaintextMIME_ShortString() throws Exception {

        DisableJAXBTextPlainClient client = new DisableJAXBTextPlainClient();

            // compare
            assertEquals(goodResult, client.JAXB_AttachmentShortString());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_plaintextMIME_LongString_VerifyMTOM() throws Exception {

        DisableJAXBTextPlainClient client = new DisableJAXBTextPlainClient();

            // compare
            assertEquals(goodResult, client.jaxb_AttachmentLongString_VerifyMTOM());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_plaintextMIME_LongString() throws Exception {

        DisableJAXBTextPlainClient client = new DisableJAXBTextPlainClient();

            // compare
            assertEquals(goodResult, client.JAXB_AttachmentLongString());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_plaintextMIME_FromXMLFile_VerifyMTOM() throws Exception {

        DisableJAXBTextPlainClient client = new DisableJAXBTextPlainClient();

            // compare
            assertEquals(goodResult, client.jaxb_AttachmentFromFile_VerifyMTOM());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_ServerDefault_ClientOFF_plaintextMIME_FromXMLFile() throws Exception {

        DisableJAXBTextPlainClient client = new DisableJAXBTextPlainClient();

            // compare
            assertEquals(goodResult, client.JAXB_AttachmentFromFile());
    }

}
