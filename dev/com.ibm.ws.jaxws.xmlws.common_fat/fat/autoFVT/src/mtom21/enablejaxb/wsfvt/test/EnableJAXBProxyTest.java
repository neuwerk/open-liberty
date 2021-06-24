//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/test/EnableJAXBProxyTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:30:53 [8/8/12 06:40:51]
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

package mtom21.enablejaxb.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import mtom21.enablejaxb.wsfvt.client.EnableJAXBProxyDataHandlerClient;
import mtom21.enablejaxb.wsfvt.client.EnableJAXBProxyImageJpegClient;

/**
 * This class will test the ability to reach a JAX-WS based server side implementation after the EAR
 * has been installed into WebSphere Application Server.
 * 
 * 
 */
public class EnableJAXBProxyTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    public static final String goodResult = "Message processed";

    /**
     * A constructor to create a test case with a given name.
     * 
     * @param name The name of the test case to be created
     */
    public EnableJAXBProxyTest(String name) {
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
        return new TestSuite(EnableJAXBProxyTest.class);
    }

    // ---------------------------------------
    // tests EnableJAXBProxyDataHandlerClient
    // ---------------------------------------

    // Test sending/receiving a message with in-line attachment
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_ServerDefault_ClientON_MultipartMIME_smallJpeg_VerifyMTOM() throws Exception {

        EnableJAXBProxyDataHandlerClient client = new EnableJAXBProxyDataHandlerClient();

            // compare
            assertEquals(goodResult, client.proxy_AttachmentSmallJpeg_VerifyMTOM());
    }

    // Test sending/receiving a message with in-line attachment
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_ServerDefault_ClientON_MultipartMIME_smallJpeg() throws Exception {

        EnableJAXBProxyDataHandlerClient client = new EnableJAXBProxyDataHandlerClient();

            // compare
            assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
    }

    // Proxy_SequenceLargeJpeg
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_ServerDefault_ClientON_Sequence_MultipartMIME_smallJpeg_VerifyMTOM() throws Exception {

        EnableJAXBProxyDataHandlerClient client = new EnableJAXBProxyDataHandlerClient();

            // compare
            assertEquals(goodResult, client.proxy_AttachmentLargeJpeg_VerifyMTOM());
    }

    // Proxy_SequenceLargeJpeg
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_ServerDefault_ClientON_Sequence_MultipartMIME_smallJpeg() throws Exception {

        EnableJAXBProxyDataHandlerClient client = new EnableJAXBProxyDataHandlerClient();

            // compare
            assertEquals(goodResult, client.Proxy_SequenceLargeJpeg());
    }

    // ---------------------------------------
    // tests EnableJAXBProxyImageJpegClient
    // ---------------------------------------

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_ServerDefault_ClientON_ImageJpegMIME_smallJpeg_VerifyMTOM() throws Exception {

        EnableJAXBProxyImageJpegClient client = new EnableJAXBProxyImageJpegClient();

            assertEquals(goodResult, client.proxy_AttachmentSmallJpeg_VerifyMTOM());

    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_ServerDefault_ClientON_ImageJpegMIME_smallJpeg() throws Exception {

        EnableJAXBProxyImageJpegClient client = new EnableJAXBProxyImageJpegClient();

            assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());

    }

}
