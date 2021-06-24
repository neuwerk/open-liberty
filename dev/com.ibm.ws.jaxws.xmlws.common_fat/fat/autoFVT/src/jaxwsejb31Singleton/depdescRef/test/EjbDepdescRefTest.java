/**
 * @(#) 1.1 autoFVT/src/jaxwsejb31Singleton/depdescRef/test/EjbDepdescRefTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/20/09 12:48:05 [8/8/12 06:58:47] 
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId       Feature/Defect       Description
 * -----------------------------------------------------------------------------
 * 11/18/09    jtnguyen     F743-17947-02        New tests for Singleton and WebServiceRef
 */
package jaxwsejb31Singleton.depdescRef.test;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
//import com.ibm.ws.wsfvt.test.framework.FvtTest;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.application.Application;
import com.ibm.websphere.simplicity.application.ApplicationManager;
import com.ibm.websphere.simplicity.application.EnterpriseApplication;


import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;


/**
 * This class verifies that multiple client requests use the same instance of a Singleton EJB. 
 * The Singleton EJB has Singleton and service-ref elements in the 
 * META-INF/ejb-jar.xml deployment descriptor file.
 */
public class EjbDepdescRefTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static String className = EjbDepdescRefTest.class.getName();

    private Node node = null;
    private Cell cell = null;
    
    private final static String appName = "jaxwsejb31Singleton-Ejb1";
        

    /*
     * These static members are used in all test runs, to reduce overhead they
     * are static and used throughout the tests instead of initializing a new
     * one each test
     */
    private static String host = null;
    private static Integer port = null;

    private static URL loc1;
    private static QName qn1;
    private static Ejb1 src;
    private static String url;
    
    private static int firstTestSum = 30;  // lastTotalSum from first test
        	
            
          
    /**
     * Constructor to create a test case with a given name
     * 
     * @param arg0 -
     *                The name of the test case
     */
    public EjbDepdescRefTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        System.out.println( className );
        return new TestSuite( EjbDepdescRefTest.class );
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

    /**
     * This method tests an Singleton EJB-based service which has the Singleton and service-ref 
     * elements in ejb-jar.xml file.
     * 
     * @testStrategy A client sends multiple requests to a Singleton EJB.  This singleton EJB in turn
     * sends the requests to a Stateless EJB via service-ref element in its ejb-jar.xml file.
     * The client then sends a request to read the last value which was updated by all invocations,
     * to verify that the client has been accessing the same instance of the Singleton EJB. 
     */
    public void testVerifyRequests_01() {
        try {
     	
	        node = TopologyDefaults.getDefaultAppServer().getNode();
	
	        host = node.getHostname();
	        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);                 
	        
            url = "http://" + host + ":" + port + "/";                       
            


        	// restart app
            System.out.println("***** Restart app to reset the counter in the EJB before the test");

	        //addRestartApplicationCmd(appName);
            restartLiberty();            
            
            if(src == null){
            	src = this.getEJBSrv();
            }
            
        	System.out.println("-- loc1 = " + loc1 + ", qname = " + qn1.toString() );

            // send multiple requests
            int result = 0;
            int numReq = 5;
            // each row has values as {num1, num2, sum, accumulateSum}
            int[][] source = { {1,1,2,2}, {2,2,4,6}, {3,3,6,12}, {4,4,8,20}, {5,5,10,30} };
              
            for (int i = 0; i< numReq; i++){
            	int num1 = source[i][0];
            	int num2 = source[i][1];
            	int sum = source[i][2];
            	
            	System.out.println("-- num1=" + num1 + ",num2=" + num2);
            	
            	result = src.doSum(num1,num2);  
            	System.out.println("-- result = " + result);
                assertEquals( String.valueOf(result),String.valueOf(sum));
            }
         
            // check total count after numReq times
        	int lastSum = src.lastTotalSum(); 
        	int totalSum = source[4][3];
        	System.out.println("-- lastTotalSum = " + lastSum);
            assertEquals( String.valueOf(lastSum),String.valueOf(totalSum));
       	
        }
        catch ( Exception e ) {
            e.printStackTrace();
            fail( className + ": There was an exception! " + e );
        }
             
    }

    
    /**
     * This function tests that this client request will access the same instance of a Singleton EJB-based service 
     * that was instantiated earlier in the previous test.
     * The Singleton and service-ref are declared in the ejb-jar.xml file.
     * 
     * @testStrategy This second client sends a request to update the lastTotalSum in a Singleton EJB,
     * then it sends a request to read the updated value and verifies it. 
     */
    
    
    public void testVerifyRequests_02() {
        try {
     	 
            // send one more request - num1=15,num2=16.  total = 31
        	// firstTestSum is the last sum from the first test testVerifyRequests_01
        	int total = 31;
        	
            if(src == null){
            	src = this.getEJBSrv();
            }
           	
        	int result = src.doSum(15,16);  
        	System.out.println("-- second test: num1=15,num2=16,result = " + result);
            assertEquals( String.valueOf(result),String.valueOf(total));

            // check total count 
        	int lastSum = src.lastTotalSum(); 
        	System.out.println("-- lastTotalSum = " + lastSum);
            assertEquals( String.valueOf(lastSum),String.valueOf(firstTestSum + total));           
       	
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
             
    }
    
    
    /**
     * Restart a running application
     * 
     * @param appName
     *            The name of the application
     */
    public void addRestartApplicationCmd( String appName ) {
        try {

        	node = TopologyDefaults.getDefaultAppServer().getNode();
        	cell = node.getCell();
            addWaitForAppReady( appName );
            ApplicationManager appMgr = cell.getApplicationManager();
            Application app = appMgr.getApplicationByName(appName);
            app.stop();
            app.start();
        } catch(Exception e) {
            e.printStackTrace();
            fail("Unable to restart the application: " + e.getMessage());
        }
    }
    private void addWaitForAppReady(String appName) {
        try {
            System.out.println("***** Waiting for app to be ready");
            
            int timeout = 120;            
        	node = TopologyDefaults.getDefaultAppServer().getNode();
        	cell = node.getCell();
           
            Application app = cell.getApplicationManager().getApplicationByName(appName);
            while (timeout > 0 && !((EnterpriseApplication) app).isAppReady(true)) {
                --timeout;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error waiting for application to be ready: " + e.getMessage());
        }
    }

    // Extract to this method for defect 158330, because the FAT execution order may be random is other platform
    private Ejb1 getEJBSrv() {
    	try {
    		loc1 = new URL( url + "jaxwsejb31Singleton-Ejb1/Ejb1Service?wsdl" );
    		qn1 = new QName( "http://server.wsfvt.ejb1.depdescRef.jaxwsejb31Singleton/", "Ejb1Service" );
    		Ejb1Service svc = new Ejb1Service( loc1, qn1 );
    		src = svc.getEjb1Port();
    	} catch (MalformedURLException e){
    		e.printStackTrace();
    	}
        
        return src;
    }


}