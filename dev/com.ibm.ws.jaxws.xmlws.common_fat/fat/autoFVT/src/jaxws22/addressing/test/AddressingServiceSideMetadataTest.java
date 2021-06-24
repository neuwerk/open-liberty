//
// @(#) 1.2 autoFVT/src/jaxws22/addressing/test/AddressingServiceSideMetadataTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/11/10 10:40:48 [8/8/12 06:57:42]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/03/10 btiffany                    New File
//

package jaxws22.addressing.test;


import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import jaxws22.addressing.client.*;
import jaxws22.addressing.server.*;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import common.utils.execution.ExecutionFactory;
import common.utils.execution.IExecution;
import common.utils.topology.Cell;
import common.utils.topology.IMachine;
import common.utils.topology.INodeContainer;
import common.utils.topology.IWASInstall;
import common.utils.topology.TopologyActions;
import javax.xml.ws.soap.*;


/**
 * This class will test that the use of jsr109v1.3 deploymment descriptors
 * governing addressing server-side behavior works as expected.
 * 
 * Note that we are not checking the functionality of addressing itself, only
 * that it can be correctly configured through the interaction of annotations
 * and deployment descriptors.  The ws-addressing team covers the functionality
 * of addressing itself. 
 * 
 * The general approach is for a client will send a message
 * to a configured service and either look for a failure or inspect the response
 * to determine if configuration was correct. 
 * 
 * The name of each test usually corresponds to the context root of a configured service
 * that is installed on WAS. 
 *
 * 
 * Since: version 8
 * RTC Task: 22213 
 */
public class AddressingServiceSideMetadataTest extends FVTTestCase {
 
  private static String hostandPort = null;
  private static AddrClient client = null;
  
  //The main method.  Only used for debugging.
  public static void main(String[] args) throws java.lang.Exception {
    boolean debug = true; 
    if(debug == false){
        TestRunner.run(suite());
    } else {
         AddressingServiceSideMetadataTest t = new AddressingServiceSideMetadataTest("x");
         t.hostandPort="http://localhost:19080";
         client.hostAndPort = t.hostandPort;
         //t.testAnnoOnNoDD();
         //t.testNoAnnoDDOn();
         //t.testAnnoOnDDOff();
      //   t.testAnnoOffDDOn();
        // t.testAnnoOnImplicitDDOff();
         //t.testValidRequiredAnnoTrueOk();
    }  
     
 }
  

  
  /**
   * test that dd can override every field of an addressing annotation to On
   * It also checks that response can be overridden from anon to non-anon, and that 
   * an anon request is rejected.
   * 
   *  If a request is sent without a replyTo: header, or if it has one and it specifies the anonymous uri,
   *  then the request is considered to be anonymous 
   *  http://www.w3.org/TR/2006/REC-ws-addr-core-20060509/#msgaddrpropsinfoset
   * @throws java.lang.Exception
   */
  public void testAnnoOffDDOnNonAnonymous() throws java.lang.Exception{
      try{
          client.invokeNoAddr("annoOffDDOn","invoking without addressing, and this should not work");          
      }catch( SOAPFaultException e ){
          System.out.println("Caught expected exception when invoked without addressing ");
          assertTrue("wrong exception received", client.wasAddressingRequiredException(e));
      }    
      
      try{
          client.invokeAddrAnonymous("annoOffDDOn", "invoking with anonymous addressing, and this should not work");
      } catch ( SOAPFaultException e ){
          System.out.println("Caught expected exception when invoked with anonymous addressing ");
          e.printStackTrace(System.out);
          assertTrue("wrong exception received", client.wasAddressingRequiredException(e));
          return;
      }
      
      fail("did not receive expected exception ");          
          
  }
  
  /*
   * test that dd can override every field of an annotation to off
   * can't really check if required or response type are working, once we disable,
   * so there are more tests for that. 
   */
  public void testAnnoOnDDOff() throws java.lang.Exception {
      String service = "annoOnDDOff";
      client.invokeNoAddr(service,"this should work");  
      System.out.println(client.getInboundMsgAsString());
      assertFalse("addressing should not have been used", client.wasAddressingUsedinResponse());
      client.invokeAddrAnonymousWithFeature (service, "this should also work and service should ignore addressing");
      System.out.println(client.getInboundMsgAsString());
      assertFalse("addressing should not have been used", client.wasAddressingUsedinResponse());
      
  }
  
  /**
   * test that dd can set every field if no annotation is present. 
   * Also set responsetype to non-anon, confirm anon is rejected. 
   * @throws java.lang.Exception
   */
  public void testNoAnnoDDOn() throws java.lang.Exception{
      String service = "noAnnoDDOn";
      try{
          client.invokeNoAddr(service,"this should not work");          
      }catch( SOAPFaultException e ){
          System.out.println("Caught expected exception ");
          assertTrue("wrong exception received", client.wasAddressingRequiredException(e));   
      }
      try{
          client.invokeAddrAnonymous("noAnnoDDOn", "invoking with anonymous addressing, and this should not work");
      } catch ( SOAPFaultException e ){
          System.out.println("Caught expected exception when invoked with anonymous addressing ");
          e.printStackTrace(System.out);
          assertTrue("wrong exception received", client.wasAddressingRequiredException(e));
          return;
      }
      
      fail("expected exception not caught, addressing should have been required by the service");      
      
  }
  
  public void testAnnoOnNoDD() throws java.lang.Exception {
      String service = "annoOnNoDD";
      try{
          client.invokeNoAddr(service,"this should not work");          
      }catch( SOAPFaultException e ){
          System.out.println("Caught expected exception ");
          assertTrue("wrong exception received", client.wasAddressingRequiredException(e));
          client.invokeAddrAnonymous(service, "this should work");
          assertTrue("client accepted message but did not use addressing", 
                  client.wasAddressingUsedinResponse());
          // TODO: check for anonymous after that's implemented.       
          return;
      }
      fail("expected exception not caught, addressing should have been required by the service");      
  }
  
  /**
   * here the anno will have required=true.
   * The dd will explicitly set only the responsetype.
   * The other two attributes (required and enabled) should be reset back to their default values.
   * false and true respectively.  We should be able to invoke and get back
   * a response that contains addressing if the request contained it, and not if the request did not.
   * @throws java.lang.Exception
   */
  public void testAnnoOnImplicitDDOff() throws java.lang.Exception {
      String service = "annoOnImplicitDDOff";
      client.invokeNoAddr(service,"this should work");  
      System.out.println(client.getInboundMsgAsString());
      assertFalse("addressing should not have been used", client.wasAddressingUsedinResponse());
      client.invokeAddrAnonymous(service, "this should also work and service should ignore addressing");
      System.out.println(client.getInboundMsgAsString());
      assertTrue("addressing should have been used", client.wasAddressingUsedinResponse());
      
  }
  
 
  
   
/** === everything below here is junit stuff ================= **/
  
   /** 
     * A constructor to create a test case with a given name.
     * @param name The name of the test case to be created
     */
    public AddressingServiceSideMetadataTest(String name) {
        super(name);
    }
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(AddressingServiceSideMetadataTest.class);   
    }
    
    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws java.lang.Exception {
    }
    
    // make sure everything is running at the end of each test
    public void tearDown()throws java.lang.Exception{
    }
    
    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        super.suiteSetup(cr);
    }
	
	static {
		try {
			String host = TopologyDefaults.getDefaultAppServer().getNode()
					.getHostname();
			String port = TopologyDefaults.getDefaultAppServer()
					.getPortNumber(PortType.WC_defaulthost).toString();

			hostandPort = "http://" + host + ":" + port;
			System.out.println("hostAndPort = " + hostandPort);

			client = new AddrClient();
			client.hostAndPort = hostandPort;
		} catch (java.lang.Exception e) {

		}
	}
    
    
    //  our nonportable test teardownp method
    protected void suiteTeardown() throws java.lang.Exception {
        super.suiteTeardown();
        System.out.println("suiteTeardown() called");
    }
    
  
}
