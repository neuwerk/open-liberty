//
// @(#) 1.1 autoFVT/src/mtom21/enablesoapbinding/wsfvt/test/EnableBindingServerONSOAP12Test.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:34:45 [8/8/12 06:40:54]
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

package mtom21.enablesoapbinding.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import mtom21.enablesoapbinding.wsfvt.client.EnableSBDispatchSOAP12Client;
import mtom21.enablesoapbinding.wsfvt.client.EnableSBProxyDataHandlerSOAP12Client;
import mtom21.enablesoapbinding.wsfvt.client.EnableSBProxySOAP12Client;

/**
 * This class will test the ability to reach a JAX-WS based server side implementation after the EAR
 * has been installed into WebSphere Application Server.
 * 
 * 
 */
public class EnableBindingServerONSOAP12Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    public static final String goodResult = "Message processed";

    /**
     * A constructor to create a test case with a given name.
     * 
     * @param name The name of the test case to be created
     */
    public EnableBindingServerONSOAP12Test(String name) {
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
        return new TestSuite(EnableBindingServerONSOAP12Test.class);
    }

    // -------------------------------
    // Tests EnableSBDispatchSOAP12Client
    // -------------------------------
    
    public void testDispatch_SmallJpeg_SeverONSOAP12_ClientDefault_VerifyMTOM() throws Exception {

        EnableSBDispatchSOAP12Client client = new EnableSBDispatchSOAP12Client();

        // compare
        assertEquals(goodResult, client.jaxb_AttachmentSmallJpeg_VerifyMTOM());
    }
    
    // Tests sending/receiving a message using Dispatch
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_SmallJpeg_SeverONSOAP12_ClientDefault() throws Exception {

        EnableSBDispatchSOAP12Client client = new EnableSBDispatchSOAP12Client();

        // compare
        assertEquals(goodResult, client.JAXB_AttachmentSmallJpeg());
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_LargeJpeg_SeverONSOAP12_ClientDefault_VerifyMTOM () throws Exception {

        EnableSBDispatchSOAP12Client client = new EnableSBDispatchSOAP12Client();

        // compare
        assertEquals(goodResult, client.jaxb_AttachmentLargeJpeg_VerifyMTOM());
    }

    // Tests sending/receiving a message with in-line attachment = a short string
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testDispatch_LargeJpeg_SeverONSOAP12_ClientDefault() throws Exception {

        EnableSBDispatchSOAP12Client client = new EnableSBDispatchSOAP12Client();

        // compare
        assertEquals(goodResult, client.JAXB_AttachmentLargeJpeg());
    }

    // -------------------------------
    // Tests EnableSBProxySOAP12Client
    // -------------------------------

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_SmallJpeg_SeverONSOAP12_ClientDefault() throws Exception {

        EnableSBProxySOAP12Client client = new EnableSBProxySOAP12Client();

        // compare
        assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_LargeJpeg_SeverONSOAP12_ClientDefault() throws Exception {

        EnableSBProxySOAP12Client client = new EnableSBProxySOAP12Client();

        // compare
        assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_XMLFile_SeverONSOAP12_ClientDefault() throws Exception {

        EnableSBProxySOAP12Client client = new EnableSBProxySOAP12Client();

        // compare
        assertEquals(goodResult, client.Proxy_AttachmentFromXMLFile());
    }

    // ----------------------------------

    // -------------------------------
    // Tests EnableSBProxyDataHandlerSOAP12Client
    // -------------------------------

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_MultipartMIME_SmallJpeg_ServerONSOAP12_ClientDefault_VerifyMTOM() throws Exception {

        EnableSBProxyDataHandlerSOAP12Client client = new EnableSBProxyDataHandlerSOAP12Client();

        // compare
        assertEquals(goodResult, client.proxy_AttachmentSmallJpeg_VerifyMTOM());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testProxy_MultipartMIME_SmallJpeg_ServerONSOAP12_ClientDefault() throws Exception {

        EnableSBProxyDataHandlerSOAP12Client client = new EnableSBProxyDataHandlerSOAP12Client();

        // compare
        assertEquals(goodResult, client.Proxy_AttachmentSmallJpeg());
    }

}
