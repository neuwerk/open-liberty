//
// @(#) 1.2 autoFVT/src/faultbean/wsfvt/test/AddNumbersTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/27/10 10:03:39 [8/8/12 06:58:49]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 06/18/2010 jtnguyen    657385          New File
// 08/27/2010 jtnguyen    667709.1        Clean up output log

package faultbean.wsfvt.test;

import javax.xml.ws.BindingProvider;

import junit.framework.Test;
import junit.framework.TestSuite;

import faultbean.wsfvt.server.*;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * 
 * @author jtnguyen
 *  When a user chooses not to package FaultBeans, in this case JAX-WS runtime is responsible for 
 *  generating and using such Beans.  This can be recreated using start from Java method, with wsdl and generated 
 *  artifacts (and fault beans) are not packaged by the user.

 *  Without packaging the fault beans and WDLD in the application, we test three cases:
 *    1. use of @WebFault() - use all default values
 *    2. use of @WebFault(name="LocalName")  - name is different from exception class name (AddNegativesException)
 *    3. use of @WebFault(name="AnnoException",targetNamespace="http://server.wsfvt.faultbean") - use both name and targetNamespace.
 */
public class AddNumbersTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase{
 
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
    public AddNumbersTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite( AddNumbersTest.class );
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

    // verify service works for 2 positive numbers
    public void testAddPositiveNumbers() throws Exception {
                       
        String actual = invokeService(1,2,"test1");
        String expected = "Result = 3";
        assertTrue("Expected output to contain \"" + expected
        + "\", but instead it contained: " + actual + ".",
        actual.indexOf(expected) != -1);
 	}
    

    // verify service works for 2 negative numbers
    public void testAddNegativeNumbers() throws Exception {
                
        String actual = invokeService(-1,-2,"test2");
        
        //System.out.println("---- actual = " + actual);
        String expected = "Result = -3";
        assertTrue("Expected output to contain \"" + expected
        + "\", but instead it contained: " + actual + ".",
        actual.indexOf(expected) != -1);    
    }
    
    // verify the use of @WebFault() with all default values
    public void testAddNumbersException(){
    	String actual = null;
        try {                 
	        actual = invokeService(1,-2,"test1");
            fail("Should have caught an AddNumbersException_Exception.  Actual =" + actual);
        } catch (AddNumbersException_Exception e) {    	
                  //  System.out.println("-- Expected exception:  " + e.getMessage()); 
        	//assertEquals("faultbean.wsfvt.server.AddNumbersException",e.getMessage());
        	assertEquals("Sum is less than 0.",e.getFaultInfo().getInfo());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception.");
        }
    }
 
    // Verify the use of @WebFault(name="LocalName")  - name is different from exception class name (AddNegativesException)
    public void testAddNegativesException(){
    	String actual = null;
        try {                 
	    actual = invokeService(1,-2,"test2");
            fail("Should have caught an AddNegativesException_Exception.  Actual =" + actual);
        } catch (AddNegativesException e) { 
           // System.out.println("-- Expected exception: " + e.getMessage()); 
           // assertEquals("faultbean.wsfvt.server.AddNegativesException",e.getMessage());
            assertEquals("Expected all negative numbers.",e.getFaultInfo().getInfo());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception.");
        }
    }
    
    // verify the use of @WebFault(name="AnnoException",targetNamespace="http://server.wsfvt.faultbean")
    public void testAnnoException(){
    	String actual = null;
        try {                 
	    actual = invokeService(1,-2,"test3");
            fail("Should have caught an AnnoException_Exception.  Actual =" + actual);
        } catch (AnnoException_Exception e) { 
           // System.out.println("-- Expected exception: " + e.getMessage()); 
           // assertEquals("faultbean.wsfvt.server.AnnoException",e.getMessage());
            assertEquals("userAnno: Sum is less than 0.",e.getFaultInfo().getInfo());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception.");
        }
    }

    private String invokeService(int num1, int num2, String testNum) throws Exception{
 
    	String response = null;
    	AddNumbers addNumberPort = null;
        node = TopologyDefaults.getDefaultAppServer().getNode();    	
        host = node.getHostname();
        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);                         
        url = "http://" + host + ":" + port + "/FaultBean/AddNumbers";
        
        System.out.println("--- url = " + url);       
		

        AddNumbers_Service service = new AddNumbers_Service();
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
