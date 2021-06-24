//
// @(#) 1.1 autoFVT/src/mtom21/security/wsfvt/test/Security_ProxyTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:39:33 [8/8/12 06:41:00]
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

package mtom21.security.wsfvt.test;

import java.util.Date;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import mtom21.security.wsfvt.client.ClientOFF_Security_ProxyClient;
import mtom21.security.wsfvt.client.ClientOFF_Security_ProxySOAP12Client;
import mtom21.security.wsfvt.client.ClientON_Security_ProxyClient;
import mtom21.security.wsfvt.client.ClientON_Security_ProxySOAP12Client;

/**
 * This class will test the ability to reach a JAX-WS based
 * server side implementation after the EAR has been installed
 * into WebSphere Application Server.
 * 
 * 
 */
public class Security_ProxyTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public static final String goodResult = "Message processed";
    /** 
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public Security_ProxyTest(String name) {
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
     * Need to check whether the https port is ready.
     */
    protected void suiteSetup(ConfigRequirement testSkipCondition) throws Exception {
        super.suiteSetup(testSkipCondition);
        
        System.out.println("Start to check if http secure port is started: " + new Date(System.currentTimeMillis()));
        System.out.println("Output=" + TopologyDefaults.libServer.waitForStringInLog("CWWKO0219I.*-ssl"));
    }
    
    /** 
     * This method controls which test methods to run.
     *
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(Security_ProxyTest.class);
    }
 
  
  /*----------------------------------
     Client MTOM=OFF, Server MTOM=ON
  ----------------------------------*/ 
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that MTOM is not used on the client side with SSL enabled using SOAP 1.1.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testClientProxyMTOMOFF() throws Exception {
        ClientOFF_Security_ProxyClient client = new ClientOFF_Security_ProxyClient();
        assertEquals(goodResult, client.proxyClient_AttachmentLargeJpeg_SOAPProvider());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that MTOM is used on the server side with SSL enabled using SOAP 1.1.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testServerProxyMTOMON() throws Exception {
        ClientOFF_Security_ProxyClient client = new ClientOFF_Security_ProxyClient();
        assertEquals(goodResult, client.soapClient_AttachmentLargeJpeg_ProxyServer());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that an attachment is sent back and forth between a proxy client and proxy server with MTOM enabled on the server side, using SOAP 1.1 with SSL enabled.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testClientProxyMTOMOFFServerProxyMTOMON() throws Exception {
        ClientOFF_Security_ProxyClient client = new ClientOFF_Security_ProxyClient();
        assertEquals(goodResult, client.proxyClient_AttachmentLargeJpeg_ProxyServer());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that MTOM is not used on the client side with SSL enabled using SOAP 1.2.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testClientProxyMTOMOffSOAP12() throws Exception {
        ClientOFF_Security_ProxySOAP12Client client = new ClientOFF_Security_ProxySOAP12Client();
        assertEquals(goodResult, client.clientProxy_AttachmentLargeJpeg_ServerProvider());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that MTOM is used on the server side with SSL enabled using SOAP 1.2.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testServerProxyMTOMOnSOAP12() throws Exception {
        ClientOFF_Security_ProxySOAP12Client client = new ClientOFF_Security_ProxySOAP12Client();
        assertEquals(goodResult, client.soapClient_AttachmentLargeJpeg_ProxyServer());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that an attachment is sent back and forth between a proxy client and proxy server with MTOM enabled on the server side, using SOAP 1.2 with SSL enabled.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testClientProxyMTOMOffServerProxyMTOMOnSOAP12() throws Exception {
        ClientOFF_Security_ProxySOAP12Client client = new ClientOFF_Security_ProxySOAP12Client();
        assertEquals(goodResult, client.proxyClient_AttachmentLargeJpeg_ProxyServer());
    }
     

    /*-----------------------------------
     Client MTOM=ON, Server MTOM=OFF
     ----------------------------------*/

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that MTOM is used on the client side with SSL enabled using SOAP 1.1.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testClientProxyMTOMOn() throws Exception {
        ClientON_Security_ProxyClient client = new ClientON_Security_ProxyClient();
        assertEquals(goodResult, client.proxyClient_AttachmentLargeJpeg_SOAPProvider());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that MTOM is not used on the server side with SSL enabled using SOAP 1.1.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testServerProxyMTOMOff() throws Exception {
        ClientON_Security_ProxyClient client = new ClientON_Security_ProxyClient();
        assertEquals(goodResult, client.soapClient_AttachmentLargeJpeg_ProxyServer());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that an attachment is sent back and forth between a proxy client and proxy server with MTOM enabled on the client side, using SOAP 1.1 with SSL enabled.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testClientProxyMTOMOnServerProxyMTOMOff() throws Exception {
        ClientON_Security_ProxyClient client = new ClientON_Security_ProxyClient();
        assertEquals(goodResult, client.clientProxy_AttachmentLargeJpeg_ServerProxy());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that MTOM is used on the client side with SSL enabled using SOAP 1.2.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testClientProxyMTOMOnSOAP12() throws Exception {
        ClientON_Security_ProxySOAP12Client client = new ClientON_Security_ProxySOAP12Client();
        assertEquals(goodResult, client.proxyClient_AttachmentLargeJpeg_SOAPProvider());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that MTOM is not used on the server side with SSL enabled using SOAP 1.2.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testServerProxyMTOMOffSOAP12() throws Exception {
        ClientON_Security_ProxySOAP12Client client = new ClientON_Security_ProxySOAP12Client();
        assertEquals(goodResult, client.soapClient_AttachmentLargeJpeg_ProxyServer());
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test verifies that an attachment is sent back and forth between a proxy client and proxy server with MTOM enabled on the client side, using SOAP 1.2 with SSL enabled.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70)
    public void testClientProxyMTOMOnServerProxyMTOMOffSOAP12() throws Exception {
        ClientON_Security_ProxySOAP12Client client = new ClientON_Security_ProxySOAP12Client();
        assertEquals(goodResult, client.proxyClient_AttachmentLargeJpeg_proxyServer());
    }
     
}
