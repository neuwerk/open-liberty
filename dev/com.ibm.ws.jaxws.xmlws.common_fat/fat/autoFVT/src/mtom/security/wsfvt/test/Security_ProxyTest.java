//
// @(#) 1.2 autoFVT/src/mtom/security/wsfvt/test/Security_ProxyTest.java, WAS.websvcs.fvt, WASX.FVT 5/7/07 13:42:45 [7/11/07 13:19:08]
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
// 10/30/06 jtnguyen    LIDB3402-07.04  New File
// 05/07/07 jtnguyen    435716          Added Throw exception

package mtom.security.wsfvt.test;

import java.util.Date;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import mtom.security.wsfvt.client.*;

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
     * Need to check whether the https port is ready.
     */
    protected void suiteSetup(ConfigRequirement testSkipCondition) throws Exception {
        super.suiteSetup(testSkipCondition);
        
        System.out.println("Start to check if http secure port is started: " + new Date(System.currentTimeMillis()));
        System.out.println("Output=" + TopologyDefaults.libServer.waitForStringInLog("CWWKO0219I.*-ssl"));
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
        return new TestSuite(Security_ProxyTest.class);
    }
    
   

/*--------------------------------
  Client MTOM=ON, Server MTOM=OFF
  --------------------------------*/ 
    
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SeverONSOAP12_ClientOFF() throws Exception{
	   	
	   ClientOFF_Security_ProxySOAP12Client client = new ClientOFF_Security_ProxySOAP12Client();
           assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
   }

      
/*--------------------------------
  Client MTOM=OFF, Server MTOM=OFF
  --------------------------------*/ 
        
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SeverOFFSOAP12_ClientOFF() throws Exception{
	   	
	   ClientOFF_ServerOFF_Security_ProxySOAP12Client client = new ClientOFF_ServerOFF_Security_ProxySOAP12Client();
           assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
   }
/*----------------------------------
     Client MTOM=OFF, Server MTOM=ON
  ----------------------------------*/ 
         
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SeverONSOAP11_ClientOFF() throws Exception{
	   	
	   ClientOFF_Security_ProxyClient client = new ClientOFF_Security_ProxyClient();
           assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
   }

       
/*-----------------------------------
     Client MTOM=OFF, Server MTOM=OFF
  ----------------------------------*/ 
   
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SeverOFFSOAP11_ClientOFF() throws Exception {
	   	
	   ClientOFF_ServerOFF_Security_ProxyClient client = new ClientOFF_ServerOFF_Security_ProxyClient();
   	
           assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
    }
          


    /*-----------------------------------
        Client MTOM=ON, Server MTOM=OFF
     ----------------------------------*/ 
   
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SeverOFFSOAP12_ClientON() throws Exception  {
	   	
	   ClientON_Security_ProxySOAP12Client client = new ClientON_Security_ProxySOAP12Client();
   	
           assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
   }

     
    /*-----------------------------------
        Client MTOM=ON, Server MTOM=ON
     ----------------------------------*/ 

  
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SeverONSOAP12_ClientON() throws Exception {
	   	
	   ClientON_ServerON_Security_ProxySOAP12Client client = new ClientON_ServerON_Security_ProxySOAP12Client();
           assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
   }

          
/*-----------------------------------
     Client MTOM=ON, Server MTOM=OFF
  ----------------------------------*/ 
        
  
 
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SeverOFFSOAP11_ClientON() throws Exception {
	   	
	   ClientON_Security_ProxyClient client = new ClientON_Security_ProxyClient();
           assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
   }

/*-----------------------------------
     Client MTOM=ON, Server MTOM=ON
  ----------------------------------*/ 
   
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testProxy_SeverONSOAP11_ClientON() throws Exception{
	   	
	   ClientON_ServerON_Security_ProxyClient client = new ClientON_ServerON_Security_ProxyClient();
   	
           assertEquals(goodResult, client.Proxy_AttachmentLargeJpeg());
   }
     
}
