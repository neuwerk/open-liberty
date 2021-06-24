/**
 * @(#) 1.2 autoFVT/src/jaxwsejb31Singleton/annotationsRef/wsfvt/test/EjbAnnotationRefTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/3/09 14:05:50 [8/8/12 06:58:47] 
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
 * 11/18/09    varadan     F743-17947-01        New tests for Singleton and WebServiceRef 
 * 12/03/09    jtnguyen    F743-17947-01        Add tests for Singleton+WebServiceRef+Startup (startup in DD)
 */
package jaxwsejb31Singleton.annotationsRef.wsfvt.test;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.application.Application;
import com.ibm.websphere.simplicity.application.ApplicationManager;
import com.ibm.websphere.simplicity.application.EnterpriseApplication;

import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;


/**
 * This class verifies that multiple client requests use the same instance of a Singleton EJB. 
 * The Singleton EJB has Singleton and WebServiceRef annotations.
 */
public class EjbAnnotationRefTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static String className = EjbAnnotationRefTest.class.getName();

    private Node node = null;
    private Cell cell = null;
    
    private static String host = null;
    private static Integer port = null;
    
    // each row has values as {num1, num2, sum, accumulateSum}
    private static final  int[][] source = { {1,1,2,2}, {2,2,4,6}, {3,3,6,12}, {4,4,8,20}, {5,5,10,30} };   
                                                                                      
          
    /**
     * Constructor to create a test case with a given name
     * 
     * @param arg0 -
     *                The name of the test case
     */
    public EjbAnnotationRefTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        System.out.println( className );
        return new TestSuite( EjbAnnotationRefTest.class );
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
     * This method tests that annotations Singleton and WebServiceRef in an EJB-based service work properly.
     * 
     * @testStrategy A client sends numReq requests to update a counter in a Singleton EJB,
     * then it sends a request to read the last counter's value and verifies it. 
     */
    public void testVerifyRequests_SingletonAndWebServiceRef() {
        
        URL loc1;
        QName qn1;
        Ejb1 src;
        String url;
        String appName = "jaxwsejb31Singleton-anno-Ejb1";

        try {
     	
	    node = TopologyDefaults.getDefaultAppServer().getNode();
	
	        host = node.getHostname();
	        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);                 
	        
            url = "http://" + host + ":" + port + "/";
            
            
            loc1 = new URL( url + "jaxwsejb31Singleton-anno-Ejb1/Ejb1Service?wsdl" );
            qn1 = new QName( "http://server.wsfvt.ejb1.annotationsRef.jaxwsejb31Singleton/",
                             "Ejb1Service" );
            System.out.println("-- loc1 = " + loc1 + ", qname = " + qn1.toString() );

        	// restart app
            System.out.println("***** Restart app to reset the counter in the EJB before the test");

	        //addRestartApplicationCmd(appName);
            // Use another way to restart app
            restartLiberty();

            Ejb1Service svc = new Ejb1Service( loc1, qn1 );
           	src = svc.getEjb1Port();

            // send multiple requests
            int result = 0;
            int numReq = 5;
              
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
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
             
    }

    
    /**
     * This method tests that annotations Singleton and WebServiceRef in an EJB-based service work properly
     * when the EJB has init-on-startup set to True in the deployment descriptor.
     *
     * Note:  This test can be run as-is when WAS V8 supports annotation @Startup.  
     *        To test annotations Singleton + WebServiceRef + Startup, we will need to remove the DD's element
     *        init-on-startup and add annotation @Startup in the bean Ejb1Startup.
     * 
     * @testStrategy A client sends numReq requests to update a counter in a Singleton EJB,
     * then it sends a request to read the last counter's value and verifies it. 
     */
    public void testVerifyRequests_SingletonAndWebServiceRefAndStartup() {
        
        URL loc1;
        QName qn1;
        Ejb1Startup src;
        String url;
        String appName = "jaxwsejb31Singleton-anno-Ejb1Startup";

        try {
     	
	        node = TopologyDefaults.getDefaultAppServer().getNode();
	
	        host = node.getHostname();
	        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);                 
	        
            url = "http://" + host + ":" + port + "/";
            
            
            loc1 = new URL( url + "jaxwsejb31Singleton-anno-Ejb1Startup/Ejb1StartupService?wsdl" );
            qn1 = new QName( "http://server.wsfvt.ejb1startup.annotationsRef.jaxwsejb31Singleton/",
                             "Ejb1StartupService" );
        	System.out.println("-- loc1 = " + loc1 + ", qname = " + qn1.toString() );

        	// restart app
            System.out.println("***** Restart app to reset the counter in the EJB before the test");

	        //addRestartApplicationCmd(appName);
            restartLiberty();

            Ejb1StartupService svc = new Ejb1StartupService( loc1, qn1 );
           	src = svc.getEjb1StartupPort();

            // send multiple requests
            int result = 0;
            int numReq = 5;
              
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