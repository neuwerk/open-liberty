//
// @(#) 1.1 autoFVT/src/faultbean/wsfvt/test/AddNumbersCustomBindingTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/29/10 13:54:20 [8/8/12 06:58:55]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 09/28/2010 jtnguyen    671978          New file for story 13354 cust. fault name via binding

package faultbean.wsfvt.test;

import javax.xml.ws.BindingProvider;

import junit.framework.Test;
import junit.framework.TestSuite;

import faultbean.binding.clienttest.*;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * 
 * @author jtnguyen
 *  This test is to verify that at runtime the mapping of the fault class name is correct where
 *  the fault class name at the client side is customized using an external binding file.
 *
 *  On client's side, wsimport will create the client artifacts using the wsdl (from wsgen) and the custom binding file which has
 *  the new fault class name.
 *
 *  On server's side, without packaging the fault beans and WDLD in the application, we have three cases:
 *    1. use of @WebFault() - use all default values
 *    2. use of @WebFault(name="LocalName")  - name is different from exception class name (AddNegativesException)
 *    3. use of @WebFault(name="AnnoException",targetNamespace="http://server.wsfvt.faultbean") - use both name and targetNamespace.
 */
public class AddNumbersCustomBindingTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase{
 
    private String url = null;	
    private Node node = null;
    private static String host = null;
    private static Integer port = null;

    /**
     * Constructor to create a test case with a given name
     * 
     * @param arg0 -
     *                The name of the test case
     */
    public AddNumbersCustomBindingTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite( AddNumbersCustomBindingTest.class );
    }

    /**
     * This method will run before each testXXX method. Initialize any variables
     * that need to be setup before each test.
     */
    public void setUp() {
    }

    /**
     * The main method
     * 
     * @param args
     */
    public static void main( String[] args ) {
        junit.textui.TestRunner.run( suite() );
    }

    
    /*
     * Sevver has faultbean.wsfvt.server.AddNumbersException
     * Client uses customized name NewNameAddNumbersException
     */
    public void testAddNumbersCustomBindingException(){
        String actual = null;
        try {                 
                actual = invokeWithBindingService(1,-2,"test1");
            fail("Should have caught an Exception.  Actual =" + actual);
        } catch (faultbean.binding.clienttest.NewNameAddNumbersException e) {    	
                System.out.println("-- Expected exception:  " + e.getMessage()); 
                e.printStackTrace();
    
                // we validate the actual exception name sent from server, not the one the client customized in binding file
               // assertEquals("faultbean.wsfvt.server.AddNumbersException",e.getMessage());   
                //assertEquals("faultbean.binding.wsfvt.server.NewNameAddNumbersException",e.getMessage());  
                assertEquals("Sum is less than 0.",e.getFaultInfo().getInfo());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception.");
        }


    }

    /*
     * Sevver has faultbean.wsfvt.server.AddNegativesException
     * Client customize name to NewNameAddNegativesException
     */
 
    public void testAddNegativesCustomBindingException(){
        String actual = null;
        try {                 
            actual = invokeWithBindingService(1,-2,"test2");
            fail("Should have caught an Exception.  Actual =" + actual);
        } catch (faultbean.binding.clienttest.NewNameAddNegativesException e) { 
            System.out.println("-- Expected exception: " + e.getMessage()); 
            e.printStackTrace();
                       
           // assertEquals("faultbean.wsfvt.server.AddNegativesException",e.getMessage());
            assertEquals("Expected all negative numbers.",e.getFaultInfo().getInfo());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception.");
        }
    }
    
    /*
     * Sevver has faultbean.wsfvt.server.AnnoException
     * Client customize name to NewNameAnnoException
     */

    public void testAnnoCustomBindingException(){
        String actual = null;
        try {                 
            actual = invokeWithBindingService(1,-2,"test3");
            fail("Should have caught an AnnoException_Exception.  Actual =" + actual);
        } catch (faultbean.binding.clienttest.NewNameAnnoException e) { 
            System.out.println("-- Expected exception: " + e.getMessage()); 
            e.printStackTrace();
            
           // assertEquals("faultbean.wsfvt.server.AnnoException",e.getMessage());
            assertEquals("userAnno: Sum is less than 0.",e.getFaultInfo().getInfo());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception.");
        }
    }


    
    private String invokeWithBindingService(int num1, int num2, String testNum) throws Exception{
 
        String response = null;
        faultbean.binding.clienttest.AddNumbers addNumberPort = null;
        node = TopologyDefaults.getDefaultAppServer().getNode();    
        host = node.getHostname();
        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);                         
        url = "http://" + host + ":" + port + "/FaultBean/AddNumbers";
        
        System.out.println("--- url = " + url);       

        faultbean.binding.clienttest.AddNumbers_Service service = new faultbean.binding.clienttest.AddNumbers_Service();
        addNumberPort = service.getAddNumbersPort();
        BindingProvider provider = (BindingProvider)addNumberPort;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        if (testNum.equals("test1")){
                            
            //System.out.println("---- test1");
            response = addNumberPort.addNumbers(num1,num2);
        } else if (testNum.equals("test2")){
                            
            //System.out.println("---- test2");
            response = addNumberPort.addNegatives(num1,num2);
        } else if (testNum.equals("test3")){
                            
            //System.out.println("---- test3");
            response = addNumberPort.useAnno(num1,num2);

        }

         return response;
    }
    
}
