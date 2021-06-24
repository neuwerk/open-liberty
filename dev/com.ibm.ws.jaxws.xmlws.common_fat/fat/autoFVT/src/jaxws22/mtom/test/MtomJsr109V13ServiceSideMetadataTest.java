//
// @(#) 1.1 autoFVT/src/jaxws22/mtom/test/MtomJsr109V13ServiceSideMetadataTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/2/10 11:50:47 [8/8/12 06:57:49]
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

package jaxws22.mtom.test;


import jaxws22.mtom.client.CommonMTOMClient;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * This class will test that the use of jsr109v1.3 deploymment descriptors 
 * governing mtom behavior works as expected in the war container.
 * 
 * In all of these tests, client sends a message consisting of a byte array
 * to a service that will echo it.  The byte array should be returned as a
 * mime attachment if mtom is enabled and the threshold is exceeded.
 * 
 * The client will capture the raw soap message by using a handler. 
 * While the soap envelope is "reconstituted" before the handler ever
 * gets the message, the mime part separators will still be present in the soap message.
 * By checking for the presence of those, we can infer whether mtom was used or not.
 * 
 * Checking the wire level message for the mime attachment would have required the
 * use of a tcpmon-like proxy utility, which would have been considerably more complex.  
 * 
 * In each case, the tests will check that the message was echoed intact,
 * and then examine the inbound soapmessage (response) to infer if mtom was used 
 * correctly given the mix of deployment descriptor settings, annotation settings,
 * and message size.
 * 
 * Since: version 8
 * RTC Story: 18112
 *  
 */
public class MtomJsr109V13ServiceSideMetadataTest extends FVTTestCase {
    
    private static CommonMTOMClient client = null;
    private static String hostAndPort= null;
	
	static {
		try {
			String host = TopologyDefaults.getDefaultAppServer().getNode()
					.getHostname();
			String port = TopologyDefaults.getDefaultAppServer()
					.getPortNumber(PortType.WC_defaulthost).toString();

			hostAndPort = "http://" + host + ":" + port;
			client = new CommonMTOMClient();
			client.hostAndPort = hostAndPort;
			System.out.println("hostAndPort = " + hostAndPort);
		} catch (Exception e) {
			// do nothing
		}
	}
    
    /**
     * When nothing is specified in annotation or dd, pass a large
     * message and make sure we don't have any hardcoded threshold
     * above which we mtom enable. 
     * 
     * This service impl has annotations on the methods, which should
     * be ignored. Mtom should not be used. 
     * @throws Exception
     * 
     * I think this one is failing pending a bug Phil was fixing, where method 
     * level annotations are read and improperly applied to the class. 
     */
    public void testDefaultWithLargeMessage() throws Exception {
        String serviceToInvoke="MTOMonMultipleMethodsAnnotationOnlyService";
        //fail("need to create a service with nothin in dd or anno");
        byte[] expect = genByteArray(70000);        
        byte [] actual= client.testProxy(serviceToInvoke, expect);        
        //System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertFalse("mtom was not expected to be enabled but was",
                checkResponseforMTOMUsage());
        
    }
    
    /**
     * when deployment descriptor configures mtom, service should
     * work as configured.
     * service config: enabled, threshold=2048, no annotation present
     * @throws Exception
     */
    public void testDDOnlyEnabledAboveThreshold() throws Exception{
        String serviceToInvoke="MTOMDDOnly";
        byte[] expect = genByteArray(2049);        
        byte [] actual= client.testProxy(serviceToInvoke, expect);        
        System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertTrue("mtom was expected to be enabled but was not",
                checkResponseforMTOMUsage());
    }
    
    /*
     * only sizes above the threshold should be mtom'd.
     */
    public void testDDOnlyEnabledAtThreshold() throws Exception{
        String serviceToInvoke="MTOMDDOnly";
        byte[] expect = genByteArray(2048); 
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertFalse("mtom was expected to be disabled but was not",
                checkResponseforMTOMUsage());
        
    }
    
    /*
     * sizes below the threshold should definitely not be mtom'd
     */
    public void testDDOnlyEnabledBelowThreshold() throws Exception{
        String serviceToInvoke="MTOMDDOnly";
        
        // try one at the threshold
        byte[] expect = genByteArray(2047);
        System.out.println(expect.length);
        //expect = "hello there".getBytes();
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertFalse("mtom was expected to be disabled but was not",
                checkResponseforMTOMUsage());
        
    }
    
    /*
     * sizes below the threshold should definitely not be mtom'd
     */
    public void testDDOnlyEnabledFarBelowThreshold() throws Exception{
        String serviceToInvoke="MTOMDDOnly";
      
        byte[] expect = genByteArray(25);
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertFalse("mtom was expected to be disabled but was not",
                checkResponseforMTOMUsage());
        
    }

    
    
    /**
     * when deployment descriptor configures mtom, service should
     * work as configured.
     * service config: disabled, although service is annotated for mtom.
     * dd should override annotation
     * 
     *      
     */
    public void testDDOnlyDisabled() throws Exception{
        String serviceToInvoke="MTOMDDNoMTOM";
        byte[] expect = genByteArray(4096);
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertFalse("mtom was expected to be disabled but was not",
                checkResponseforMTOMUsage());
        
        
    }
    /**
     * dd = true, 3072, annotation = true, 4096
     * dd threshold value should override annotation threshold value
     * @throws Exception
     */
    public void testDDOverrideAtThreshold() throws Exception{
        String serviceToInvoke="MTOMDDOverride";
        byte[] expect = genByteArray(3073);
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertTrue("mtom was expected to be enabled but was not" + client.getInboundMsgAsString(),
                checkResponseforMTOMUsage());        
    }
    
    /**
     * dd = true, 3072, annotation = true, 4096
     * dd threshold value should override annotation threshold value
     * @throws Exception
     */
    public void testDDOverrideBelowThreshold() throws Exception{
        String serviceToInvoke="MTOMDDOverride";
        byte[] expect = genByteArray(3071);
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertFalse("mtom was expected to be disabled but was not",
                checkResponseforMTOMUsage());
        
    }

    
    /**
     * annotation only, with threshold = 0
     * @throws Exception
     */
    public void testAnnoOnlyEnabled() throws Exception{
        String serviceToInvoke="MTOMAnnotationOnly";
        byte[] expect = genByteArray(2000);
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertTrue("mtom was expected to be enabled but was not",
                checkResponseforMTOMUsage());
        
    }
    
    /**
     * annotation only, disabled.
     * @throws Exception
     */
    public void testAnnoOnlyDisabled() throws Exception{
        String serviceToInvoke="MTOMAnnotationNoMTOM";
        byte[] expect = genByteArray(48000);
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        //System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertFalse("mtom was expected to be disabled but was not",
                checkResponseforMTOMUsage());       
    }
    

    /**
     * another way of enabling mtom is with the bindingtype annotation.
     * Make sure that still works
     * 
     */
    public void testBindingTypeMTOMAnnotationOnly() throws Exception {
        String serviceToInvoke="BindingTypeMTOMAnnotationOnly";
        byte[] expect = genByteArray(48000);
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        //System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertTrue("mtom was expected to be enabled but was not",
                checkResponseforMTOMUsage());       
        
    }
    
    /**
     * another way of enabling mtom is with the bindingtype annotation.
     * DD should be able to override that behavior too. 
     * dd = false, bindingtypeannotation=true, dd should win
     * @throws Exception
     */
    public void testBindingTypeMTOMAnnotationDDOverride() throws Exception {
        String serviceToInvoke="BindingTypeMTOMAnnotationDDOverride";
        byte[] expect = genByteArray(48000);
        byte [] actual= client.testProxy(serviceToInvoke, expect);
        //System.out.println(client.getInboundMsgAsString());
        assertTrue("received message did not match what was sent",
                compareByteArrays(expect, actual));
        assertFalse("mtom was expected to be disabled but was not",
                checkResponseforMTOMUsage());       
        
    }
    
 
    
    /** 
     * A constructor to create a test case with a given name.
     * @param name The name of the test case to be created
     */
    public MtomJsr109V13ServiceSideMetadataTest(String name) {
        super(name);
    }

    /** 
     * The main method.  Only used for debugging.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args)throws Exception {
        //TestRunner.run(suite());
        MtomJsr109V13ServiceSideMetadataTest t = new MtomJsr109V13ServiceSideMetadataTest("x");        
        t.testDefaultWithLargeMessage();
    }

    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(MtomJsr109V13ServiceSideMetadataTest.class);   
    }
    
    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws Exception {
    }
    
    // make sure everything is running at the end of each test
    public void tearDown()throws Exception{
    }
    
    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws Exception {
        super.suiteSetup(cr);
        System.out.println("suiteSetup() called");    
    }

    
//  our nonportable test teardownp method
    protected void suiteTeardown() throws Exception {
        super.suiteTeardown();
        System.out.println("suiteTeardown() called");
    }
    
    private byte [] genByteArray(int size){
        byte [] ba = new byte[size];
        int i = 0;
        byte j = 0;
        for(i=0; i < size; i++){
            ba[i]=j++;
            if( j>250 ){ j=0;}
        }
      // System.out.println("request: "+size+" actual: "+ba.length);
       return ba;
    }
    
    private boolean compareByteArrays(byte[] expect, byte [] actual){
        if (expect.length != actual.length ){
            System.out.println("length mismatch, expect =" +expect.length + 
                    " actual = " + actual.length);
            return false;
        }
        int max = expect.length;
        for(int i=0; i < max; i++){
            if (expect[i] != actual [i]){
                System.out.println("content mismatch at offset "+i);
                return false;
            }            
        }
        return true;
    }
    
    /**
     * look for the mime boundary in the soap message, which would indicate mtom
     * was probably used.   (No way to tell for sure without a wire monitor)
     * @return
     */
    private boolean checkResponseforMTOMUsage(){
        if (client.getInboundMsgAsString().contains("_Part_")){ return true; }
        return false;
    }
    
 
}
