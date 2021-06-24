/**
 * autoFVT/src/jaxwsejb30/ejbapps/depdesc/webxml/test/WebxmlDepdescTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 01/03/2008  samerrell    LIDB4511.45.01       New file
 *
 */
package jaxwsejb30.ejbapps.depdesc.webxml.test;

import java.net.URL;

import javax.xml.namespace.QName;

import jaxwsejb30.ejbapps.depdesc.webxml.ejbservice.HelloRefSupplier;
import jaxwsejb30.ejbapps.depdesc.webxml.ejbservice.HelloRefSupplierService;
import jaxwsejb30.ejbapps.depdesc.webxml.warrefchecker.WarRefChecker;
import jaxwsejb30.ejbapps.depdesc.webxml.warrefchecker.WarRefCheckerService;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FvtTest;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.TopologyActions;

/**
 * This class tests the {@link WarRefChecker} service to determine if the
 * injected resources have been properly overridden from the WarRefChecker's
 * web.xml file.
 * 
 * @see WarRefChecker
 */
public class WebxmlDepdescTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    /*
     * ACUTE classes used to access server information such as server name and
     * default port, these are used to avoid compile-time hard coding of URLs,
     * hosts, ports, etc
     */
    private static IAppServer tstServer = TopologyDefaults.defaultAppServer;
    private static String machine = tstServer.getMachine().getHostname();
//    private static Integer testPort = tstServer.getPort( Ports.BOOTSTRAP_ADDRESS );
    private static Integer defaultPort = tstServer.getPort( Ports.WC_defaulthost );
    private static String fvtHostName = TopologyActions.FVT_HOSTNAME;
    private static String className = WebxmlDepdescTest.class.getName();

    /*
     * These static members are used in all test runs, to reduce overhead they
     * are static and used throughout the tests instead of initializing a new
     * one each test
     */
    private static URL loc1;
    private static WarRefCheckerService wrcs;
    private static WarRefChecker wrc;
    private static QName qn1;
    private static String url;

    /**
     * Constructor to create a test case with a given name
     * 
     * @param arg0 -
     *                The name of the test case
     */
    public WebxmlDepdescTest( String name ) {
        super( name );
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        System.out.println( className );
        return new TestSuite( WebxmlDepdescTest.class );
    }

    /**
     * This method will run before each testXXX method. Initialize any variables
     * that need to be setup before each test.
     */
    public void setUp() {
    	// Below code has been moved from static block to here.
    	// The reason is because when this test suite is executed, the server is started in suiteSetup().
    	// The original static block will fail because at that time the server is not started yet. The
    	// access request will always fail.
        try {
            url = "http://" + machine + ":" + defaultPort + "/";
            loc1 = new URL( url + "jwsejb30-depdesc-webxml-warrefchecker/WarRefCheckerService?wsdl" );
            qn1 = new QName( "http://warrefchecker.webxml.depdesc.ejbapps.jaxwsejb30/",
                             "WarRefCheckerService" );
            wrcs = new WarRefCheckerService( loc1, qn1 );
            wrc = wrcs.getWarRefCheckerPort();
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
     * Tests that an EJB-based service declared in the service-ref element of a
     * web.xml file is injected properly.
     * 
     * @testStrategy This test calls the
     *               {@link WarRefChecker#checkFieldNameOverride(String)} method
     *               which in turn calls the
     *               {@link HelloRefSupplier#sayHello(String)} method. if the
     *               sayHello method returns "Hello, String" the service is
     *               being injected properly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Tests an EJB-based service declared in the service-ref of WarRefChecker's web.xml file", since = FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckFieldNameOverride() {
        try {
            String result = wrc.checkFieldNameOverride( "checkFieldNameOverride" );
            assertEquals( "Hello, checkFieldNameOverride", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    /**
     * Tests that the wsdlLocation element of a WebServiceRef annotation is
     * overridden by the service-ref/wsdl-file in a web.xml file. The service
     * being injected is EJB-based.
     * 
     * @testStrategy This test calls
     *               {@link WarRefChecker#checkFieldWsdlLocationOverride(String)}
     *               method which in turn calls the
     *               {@link HelloRefSupplier#sayHello(String)} method. if the
     *               sayHello method returns "Hello, String" the service is
     *               being injected properly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Tests that the wsdlLocation element of a WebServiceRef annotation is overridden by service-ref/wsdl-file", since = FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckFieldWsdlLocationOverride() {
        try {
            String result = wrc.checkFieldWsdlLocationOverride( "checkFieldWsdlLocationOverride" );
            assertEquals( "Hello, checkFieldWsdlLocationOverride", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }

    }

    /**
     * Tests that the service-ref defined in the web.xml file can be called
     * using JNDI. The service being injected is EJB-based.
     * 
     * @testStrategy This test calls
     *               {@link WarRefChecker#checkJndiLookup(String)} method which
     *               in turn calls the {@link HelloRefSupplier#sayHello(String)}
     *               method. if the sayHello method returns "Hello, String" the
     *               service is being injected properly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Tests that the service-ref defined in the web.xml file can be called using JNDI.", since = FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckJndiLookup() {
        try {
            String result = wrc.checkJndiLookup( "testCheckJndiLookup" );
            assertEquals( "Hello, testCheckJndiLookup", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    /**
     * Tests that a WAR-based service declared in the service-ref element of a
     * web.xml file is injected properly.
     * 
     * @testStrategy This test calls
     *               {@link WarRefChecker#checkJndiLookup(String)} method which
     *               in turn calls the {@link HelloRefSupplier#sayHello(String)}
     *               method. if the sayHello method returns "Hello, String" the
     *               service is being injected properly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest( description = "Tests an WAR-based service declared in the service-ref of WarRefChecker\'s web.xml file", since = FvtTest.Releases.WAS70, expectedResult = "" )
    public void testCheckNameOverrideWar() {
        try {
            String result = wrc.checkNameOverrideWar( "checkNameOverrideWar" );
            assertEquals( "Hello, checkNameOverrideWar", result );
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception!" + e );
        }
    }

}