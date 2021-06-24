/**
 * 
 * autoFVT/src/jaxwsejb30/ejbapps/annotations/wsr/test/WsrTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 01/24/2008  samerrell    LIDB4511.45          Created class
 *
 */
package jaxwsejb30.ejbapps.annotations.wsr.test;

import java.net.URL;

import javax.xml.namespace.QName;

import jaxwsejb30.ejbapps.annotations.wsr.ejbservice.ReferenceSupplier;
import jaxwsejb30.ejbapps.annotations.wsr.ejbservice.ReferenceSupplierService;
import jaxwsejb30.ejbapps.annotations.wsr.ejbrefchecker.ReferenceChecker;
import jaxwsejb30.ejbapps.annotations.wsr.ejbrefchecker.ReferenceCheckerService;
import jaxwsejb30.ejbapps.annotations.wsr.warservice.WebReferenceSupplier;
import jaxwsejb30.ejbapps.annotations.wsr.warservice.WebReferenceSupplierService;

import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import common.utils.execution.ExecutionFactory;
import common.utils.execution.IExecution;
import common.utils.topology.IAppServer;
import common.utils.topology.IMachine;
import common.utils.topology.Ports;
import common.utils.topology.Topology;
import common.utils.topology.TopologyActions;

/**
 * @author samerrel
 * 
 */
public class WsrTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static IAppServer tstServer = TopologyDefaults.defaultAppServer;
    private static String machine = tstServer.getMachine().getHostname();
//    private static Integer testPort = tstServer.getPort( Ports.BOOTSTRAP_ADDRESS );
    private static Integer defaultPort = tstServer.getPort( Ports.WC_defaulthost );
    private static String fvtHostName = TopologyActions.FVT_HOSTNAME;
    private static String className = WsrTest.class.getName();

    private static String url1;
    private static URL loc3;
    private static QName qn3;
    private static ReferenceChecker rc;
    private static ReferenceCheckerService rcs;

    /**
     * Constructor to create a test case with a given name
     * 
     * @param arg0 - The name of the test case
     */
    public WsrTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        System.out.println( className );
        return new TestSuite( WsrTest.class );
    }

    /**
     * This method will run before each testXXX method. Initialize any variables that need to be setup before each test.
     */
    public void setUp() {
    	// Below code has been moved from static block to here.
    	// The reason is because when this test suite is executed, the server is started in suiteSetup().
    	// The original static block will fail because at that time the server is not started yet. The
    	// access request will always fail.

    	// Initialize the ReferenceChecker statically here so the test methods can use the
        // same object
        try {
            url1 = "http://" + machine + ":" + defaultPort + "/";
            loc3 = new URL( url1 + "jwsejb30-anno-wsr-ejbrefchecker/ReferenceCheckerService?wsdl" );
            qn3 = new QName( "http://ejbrefchecker.wsr.annotations.ejbapps.jaxwsejb30/",
                    "ReferenceCheckerService" );
            rcs = new ReferenceCheckerService( loc3, qn3 );
            rc = rcs.getReferenceCheckerPort();
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
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
    public void testWsrEjbDirectCall() {
        try {
            String url = "http://" + machine + ":" + defaultPort + "/";
            System.out.println( "URL:     " + url );
            URL loc = new URL( url + "jwsejb30-anno-wsr-ejbservice/ReferenceSupplierService?wsdl" );
            QName qn = new QName( "http://ejbservice.wsr.annotations.ejbapps.jaxwsejb30/",
                    "ReferenceSupplierService" );
            ReferenceSupplierService rss = new ReferenceSupplierService( loc, qn );
            System.out.println( " Getting ReferenceSupplierPort..." );
            ReferenceSupplier rs = rss.getReferenceSupplierPort();
            String result = rs.sayHello( "Direct Call" );
            assertEquals( "Hello, Direct Call", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    /**
     * Test that the WAR-based webserivce can be called directly
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls referenced web services from a WAR-based service using the webserviceref annotation", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrWarDirectCall() {
        try {
            String url = "http://" + machine + ":" + defaultPort + "/";
            URL loc2 = new URL( url + "ReferenceSupplierService3/WebReferenceSupplierService?wsdl" );
            QName qn2 = new QName( "http://warservice.wsr.annotations.ejbapps.jaxwsejb30/",
                    "WebReferenceSupplierService" );
            WebReferenceSupplierService wrss = new WebReferenceSupplierService( loc2, qn2 );
            System.out.println( "[WsrClient] Getting WebReferenceSupplierPort..." );
            WebReferenceSupplier wrs = wrss.getWebReferenceSupplierPort();
            String result = wrs.sayHello( "Direct Call" );
            assertEquals( "Hello, Direct Call", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an injected service on a field", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrGenSEIFieldInj() {
        try {
            String result = rc.checkGenSEIFieldInjection( "checkGenSEIFieldInjection" );
            assertEquals( "Hello, checkGenSEIFieldInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an injected service on a method", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrGenSEIMethodInj() {
        try {
            String result = rc.checkGenSEIMethodInjection( "checkGenSEIMethodInjection" );
            assertEquals( "Hello, checkGenSEIMethodInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an injected port on a field", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrCheckImplFieldInj() {
        try {
            String result = rc.checkImplFieldInjection( "checkImplFieldInjection" );
            assertEquals( "Hello, checkImplFieldInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an injected port on a method", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrCheckImplMethodInj() {
        try {
            String result = rc.checkImplMethodInjection( "checkImplMethodInjection" );
            assertEquals( "Hello, checkImplMethodInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an injected service on a field using JNDI", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrCheckGenSEIJndiFieldInj() {
        try {
            String result = rc.checkGenSEIJndiFieldInjection( "checkGenSEIFieldInjection" );
            assertEquals( "Hello, checkGenSEIFieldInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an injected port on a field using JNDI", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrCheckJndiImplFieldInj() {
        try {
            String result = rc.checkJndiImplFieldInjection( "checkJndiImplFieldInjection" );
            assertEquals( "Hello, checkJndiImplFieldInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls an injected war based service on a field using JNDI", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrCheckWarJndiImplFieldInj() {
        try {
            String result = rc.checkWarJndiImplFieldInjection( "checkWarJndiImplFieldInjection" );
            assertEquals( "Hello, checkWarJndiImplFieldInjection", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Calls a class level EJB-based service", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WAS70, expectedResult = "" )
    public void testWsrCheckClassLevelEJBRef() {
        try {
            String result = rc.checkClassLevelEJBReference( "checkClassLevelEJBReference" );
            assertEquals( "Hello, checkClassLevelEJBReference", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.out );
            fail( className + ": There was an exception! " + e );
        }
    }
}
