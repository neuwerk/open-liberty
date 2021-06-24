/**
 * 
 * autoFVT/src/jaxwsejb30/ejbapps/annotations/resource/test/ResourceTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
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
 * 04/01/2008  samerrell    LIDB4511.45          New file
 *
 */
package jaxwsejb30.ejbapps.annotations.resource.test;

import java.net.URL;

import javax.xml.namespace.QName;

import jaxwsejb30.ejbapps.annotations.resource.ejbservice.HelloRefSupplier;
import jaxwsejb30.ejbapps.annotations.resource.ejbservice.HelloRefSupplierService;
import jaxwsejb30.ejbapps.annotations.resource.ejbrefchecker.ResourceRefChecker;
import jaxwsejb30.ejbapps.annotations.resource.ejbrefchecker.ResourceRefCheckerService;
import jaxwsejb30.ejbapps.annotations.resource.warservice.WarRefSupplier;
import jaxwsejb30.ejbapps.annotations.resource.warservice.WarRefSupplierService;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.TopologyActions;

/**
 * @author samerrel
 * 
 */
public class ResourceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static IAppServer tstServer = TopologyDefaults.defaultAppServer;
    private static String machine = tstServer.getMachine().getHostname();
//    private static Integer testPort = tstServer.getPort( Ports.BOOTSTRAP_ADDRESS );
    private static Integer defaultPort = tstServer.getPort( Ports.WC_defaulthost );
    private static String fvtHostName = TopologyActions.FVT_HOSTNAME;
    private static String className = ResourceTest.class.getName();

    private static URL loc1;
    private static QName qn1;
    private static ResourceRefCheckerService rrcs;
    private static ResourceRefChecker rrc;
    private static String url;

    /**
     * Constructor to create a test case with a given name
     * 
     * @param arg0 - The name of the test case
     */
    public ResourceTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        System.out.println( className );
        return new TestSuite( ResourceTest.class );
    }

    /**
     * This method will run before each testXXX method. Initialize any variables that need to be setup before each test.
     */
    public void setUp() {
    	
    	// Below code has been moved from static block to here.
    	// The reason is because when this test suite is executed, the server is started in suiteSetup().
    	// The original static block will fail because at that time the server is not started yet. The
    	// access request will always fail.
        try {
            url = "http://" + machine + ":" + defaultPort + "/";
            loc1 = new URL( url
                    + "jwsejb30-anno-resource-ejbrefchecker/ResourceRefCheckerService?wsdl" );
            qn1 = new QName( "http://ejbrefchecker.resource.annotations.ejbapps.jaxwsejb30/",
                    "ResourceRefCheckerService" );
            rrcs = new ResourceRefCheckerService( loc1, qn1 );
            rrc = rrcs.getResourceRefCheckerPort();
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
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
     * Test that the EJB-based webserivce can be called directly
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls referenced web services from an EJB-based service using the webserviceref annotation", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testResourceEjbDirectCall() {
        try {
            System.out.println( "URL:     " + url );
            URL loc = new URL( url
                    + "jwsejb30-anno-resource-ejbservice/HelloRefSupplierService?wsdl" );
            QName qn = new QName( "http://ejbservice.resource.annotations.ejbapps.jaxwsejb30/",
                    "HelloRefSupplierService" );
            HelloRefSupplierService hrcs = new HelloRefSupplierService( loc, qn );
            System.out.println( "Getting HelloRefSupplierPort..." );
            HelloRefSupplier hrs = hrcs.getHelloRefSupplierPort();
            String result = hrs.sayHello( "Direct call" );
            assertEquals( "Hello, Direct call", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    /**
     * Test that the WAR-based webserivce can be called directly
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls referenced web services from a WAR-based service using the webserviceref annotation", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrWarDirectCall() {
        try {
            URL loc = new URL( url + "WebHelloRefSupplierService/WarRefSupplierService?wsdl" );
            QName qn = new QName( "http://warservice.resource.annotations.ejbapps.jaxwsejb30/",
                    "WarRefSupplierService" );
            WarRefSupplierService wrss = new WarRefSupplierService( loc, qn );
            System.out.println( "Getting WarRefSupplierService" );
            WarRefSupplier wrs = wrss.getWarRefSupplierPort();
            String result = wrs.sayHello( "Direct call" );
            assertEquals( "Hello, Direct call", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls referenced web services from an EJB-based service using the resource annotation", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckEjbFieldInjection() {
        try {
            String result = rrc.checkEjbFieldInjection( "checkEjbFieldInjection" );
            assertEquals( "Hello, checkEjbFieldInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls referenced web services from an WAR-based service using the resource annotation", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckWarFieldInjection() {
        try {
            String result = rrc.checkWarFieldInjection( "checkWarFieldInjection" );
            assertEquals( "Hello, checkWarFieldInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an injected EJB-based service on a method", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckEjbMethodInjection() {
        try {
            String result = rrc.checkEjbMethodInjection( "checkEjbMethodInjection" );
            assertEquals( "Hello, checkEjbMethodInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an WAR-based injected service on a method", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckWarMethodInjection() {
        try {
            String result = rrc.checkWarMethodInjection( "checkWarMethodInjection" );
            assertEquals( "Hello, checkWarMethodInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls a class level EJB-based service from the Resource anotation", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckClassLevelEjbResource() {
        try {
            String result = rrc.checkClassLevelEjbResource( "checkClassLevelEjbResource" );
            assertEquals( "Hello, checkClassLevelEjbResource", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls a class level WAR-based service from the Resources annotation", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckClassLevelEjbResources() {
        try {
            String result = rrc.checkClassLevelEjbResources( "checkClassLevelEjbResources" );
            assertEquals( "Hello, checkClassLevelEjbResources", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls a class level WAR-based service from the Resources annotation", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckClassLevelWarResources() {
        try {
            String result = rrc.checkClassLevelWarResources( "checkClassLevelWarResources" );
            assertEquals( "Hello, checkClassLevelWarResources", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

}
