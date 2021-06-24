/**
 * @(#) 1.1 autoFVT/src/jaxwsejb31Singleton/depdesc/wsfvt/test/EjbDepdescTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/18/09 22:53:00 [8/8/12 06:58:46] 
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
 * 11/18/09    jtnguyen     F743-17947-02        New tests for Singleton EJB with Deployment Desc
 */
package jaxwsejb31Singleton.depdesc.wsfvt.test;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.test.framework.FvtTest;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.application.Application;
import com.ibm.websphere.simplicity.application.ApplicationManager;
import com.ibm.websphere.simplicity.application.EnterpriseApplication;

import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;


/**
 * This class verifies that multiple client requests use the same instance of a Singleton EJB. 
 * The Singleton is defined via the <session-type>Singleton</session-type> element within the 
 * META-INF/ejb-jar.xml deployment descriptor file.
 * @see EjbDepdesc
 */
public class EjbDepdescTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static String className = EjbDepdescTest.class.getName();

    private Node node = null;
    private Cell cell = null;
    

    private final static int numReq = 5;
    private final static String appName = "jaxwsejb31Singleton-EjbDepdesc";
        

    /*
     * These static members are used in all test runs, to reduce overhead they
     * are static and used throughout the tests instead of initializing a new
     * one each test
     */
    private static String host = null;
    private static Integer port = null;

    private static URL loc1;
    private static QName qn1;
    private static EjbDepdesc src;
    private static String url;
        	
            
          
    /**
     * Constructor to create a test case with a given name
     * 
     * @param arg0 -
     *                The name of the test case
     */
    public EjbDepdescTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        System.out.println( className );
        return new TestSuite( EjbDepdescTest.class );
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
     * This method tests that an Singleton EJB-based service declared in the Singleton element of an
     * ejb-jar.xml file works properly.
     * 
     * @testStrategy A client sends numReq requests to update a counter in a Singleton EJB,
     * then it sends a request to read the last counter's value and verifies it. 
     */
    public void testVerifyRequests_01() {
        try {
     	
	        node = TopologyDefaults.getDefaultAppServer().getNode();
	
	        host = node.getHostname();
	        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);                 
	        
            url = "http://" + host + ":" + port + "/";
            loc1 = new URL( url + "jaxwsejb31Singleton-EjbDepdesc/EjbDepdescService?wsdl" );
            qn1 = new QName( "http://server.wsfvt.depdesc.jaxwsejb31Singleton/",
                             "EjbDepdescService" );
        	System.out.println("-- loc1 = " + loc1 + ", qname = " + qn1.toString() );

        	// restart app
            System.out.println("***** Restart app to reset the counter in the EJB before the test");

	        //addRestartApplicationCmd(appName);
            restartLiberty();

            EjbDepdescService svc = new EjbDepdescService( loc1, qn1 );
           	src = svc.getEjbDepdescPort();

            // send multiple requests
            String result = null;
                
            for (int i = 1; i< numReq +1 ; i++){
            	result = src.sayHello( "test number " + i );  
            	System.out.println("-- result = " + result);
                assertEquals( "Hello, test number " + i, result );
            }
         
            // check total count after numReq times
        	int count = src.lastCount();      
        	System.out.println("-- lastCount = " + count);
            assertEquals( String.valueOf(numReq) , String.valueOf(count) );        
       	
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
             
    }

    
    /**
     * This function tests that a Singleton EJB-based service declared in the Singleton element of an
     * ejb-jar.xml file is worked properly.
     * 
     * @testStrategy This second client sends a request to update a counter in a Singleton EJB,
     * then it sends a request to read the last counter's value and verifies it. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Tests an EJB-based service declared in the Singleton of EjbDepdesc\'s ejb-jar.xml file.", since = FvtTest.Releases.WAS70, expectedResult = "" )
    public void testVerifyRequests_02() {
        try {
     	 
            // send one more request after numReq times 
            String result = src.sayHello( "test number " + (numReq +1) );
        	System.out.println("-- result = " + result);

            assertEquals( "Hello, test number " + (numReq +1), result );
         
            // check total count
        	int count = src.lastCount();   
        	System.out.println("-- lastCount = " + count);

            assertEquals( String.valueOf(numReq +1 ) , String.valueOf(count) );
           
       	
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




}