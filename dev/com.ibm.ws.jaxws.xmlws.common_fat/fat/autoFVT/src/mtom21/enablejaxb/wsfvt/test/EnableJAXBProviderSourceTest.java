//
// @(#) 1.1 autoFVT/src/mtom21/enablejaxb/wsfvt/test/EnableJAXBProviderSourceTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:30:44 [8/8/12 06:40:51]
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
import mtom21.enablejaxb.wsfvt.client.EnableJAXBProviderSourceClient;

/**
 * This class will test the ability to reach a JAX-WS based server side implementation after the EAR
 * has been installed into WebSphere Application Server.
 */
public class EnableJAXBProviderSourceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    public static final String goodResult = "Message processed";

    /**
     * A constructor to create a test case with a given name.
     * 
     * @param name The name of the test case to be created
     */
    public EnableJAXBProviderSourceTest(String name) {
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
        return new TestSuite(EnableJAXBProviderSourceTest.class);
    }

    // Test sending/receiving the message as Source type, image/jpeg MIME type
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProviderSource_ServerDefault_ClientON_SmallImage_VerifyMTOM() throws Exception {

        EnableJAXBProviderSourceClient client = new EnableJAXBProviderSourceClient();

        // compare
        assertEquals(goodResult, client.sourceSOAP11Payload_01_VerifyMTOM());

    }

    // Test sending/receiving the message as Source type, image/jpeg MIME type
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProviderSource_ServerDefault_ClientON_SmallImage() {

        EnableJAXBProviderSourceClient client = new EnableJAXBProviderSourceClient();

        // compare
        assertEquals(goodResult, client.SourceSOAP11Payload_01());

    }

    // Test sending/receiving the message as Source type, plain/text MIME type

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProviderSource_ServerDefault_ClientON_String_VerifyMTOM() throws Exception {

        EnableJAXBProviderSourceClient client = new EnableJAXBProviderSourceClient();

        // compare
        assertEquals(goodResult, client.sourceSOAP11Payload_02_VerifyMTOM());

    }

    // Test sending/receiving the message as Source type, plain/text MIME type

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProviderSource_ServerDefault_ClientON_String() throws Exception {

        EnableJAXBProviderSourceClient client = new EnableJAXBProviderSourceClient();

        // compare
        assertEquals(goodResult, client.SourceSOAP11Payload_02());

    }

    // SourceSequenceSOAP11Payload
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProviderSource_ServerDefault_ClientON_String_Sequence_VerifyMTOM() throws Exception {

        EnableJAXBProviderSourceClient client = new EnableJAXBProviderSourceClient();

        // compare
        assertEquals(goodResult, client.sourceSequenceSOAP11Payload_VerifyMTOM());

    }

    // SourceSequenceSOAP11Payload
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProviderSource_ServerDefault_ClientON_String_Sequence() {

        EnableJAXBProviderSourceClient client = new EnableJAXBProviderSourceClient();

        // compare
        assertEquals(goodResult, client.SourceSequenceSOAP11Payload());

    }

}
