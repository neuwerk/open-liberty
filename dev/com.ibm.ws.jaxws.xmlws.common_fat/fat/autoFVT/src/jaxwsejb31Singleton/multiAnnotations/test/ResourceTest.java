/**
 * 
 * @(#) 1.1 autoFVT/src/jaxwsejb31Singleton/multiAnnotations/test/ResourceTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/19/09 11:16:53 [8/8/12 06:39:43]
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2009
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId       Feature/Defect       Description
 * -----------------------------------------------------------------------------
 * 11/19/2009  varadan      F743-17947-01        New file
 *
 */
package jaxwsejb31Singleton.multiAnnotations.test;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.ibm.websphere.simplicity.Node;
import com.ibm.ws.wsfvt.test.framework.FvtTest;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * @author varadan
 * 
 */
public class ResourceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static String className = ResourceTest.class.getName();
    private static URL loc;
    private static QName qn;
    private static Node node = null;
    private static String host = null;
    private static Integer port = null;
    private static String url;
    private static int expectedCount = 2;

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
            int count = 0;
            String result = null;
            String str = null;
            
            node = TopologyDefaults.getDefaultAppServer().getNode();
    
            host = node.getHostname();
            port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost); 
            url = "http://" + host + ":" + port + "/";
            System.out.println( "URL:     " + url );
            
            loc = new URL( url + "jwsejb31-anno-resource-ejbservice/HelloRefSupplierService?wsdl" );
            qn = new QName( "http://ejbservice.multiAnnotations.jaxwsejb31Singleton/",
                    "HelloRefSupplierService" );
            System.out.println("-- location = " + loc + "qname = " + qn.toString() );
          
            HelloRefSupplierService hrcs = new HelloRefSupplierService( loc, qn );
            System.out.println( "Getting HelloRefSupplierPort..." );
            HelloRefSupplier hrs = hrcs.getHelloRefSupplierPort(); 

            result = hrs.sayHello( "Direct call" );
            System.out.println( "result : " + result);
            assertEquals( "Hello, Direct call", result );
  
            str = hrs.incrementCounter();
            System.out.println( "str : " + str);

            str = hrs.incrementCounter();
            System.out.println( "str : " + str);

            count = hrs.getCounter();
            System.out.println( "Result : " + count);

            assertEquals( String.valueOf(expectedCount), String.valueOf(count) );
            expectedCount *=2;
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }

    public void testResourceEjbDirectCallSecond() {
        try {
            int count = 0;
            String result = null;
            String str = null;
            
            node = TopologyDefaults.getDefaultAppServer().getNode();
            
            host = node.getHostname();
            port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost); 
            url = "http://" + host + ":" + port + "/";
            System.out.println( "URL:     " + url );
                        
            loc = new URL( url + "jwsejb31-anno-resource-ejbservice/HelloRefSupplierService?wsdl" );
            qn = new QName( "http://ejbservice.multiAnnotations.jaxwsejb31Singleton/",
                    "HelloRefSupplierService" );
            System.out.println("-- location = " + loc + "qname = " + qn.toString() );

            HelloRefSupplierService hrcs = new HelloRefSupplierService( loc, qn );
            System.out.println( "Getting HelloRefSupplierPort..." );
            HelloRefSupplier hrs = hrcs.getHelloRefSupplierPort(); 

            result = hrs.sayHello( "Direct call" );
            System.out.println( "result : " + result);
            assertEquals( "Hello, Direct call", result );

            str = hrs.incrementCounter();
            System.out.println( "str : " + str);

            str = hrs.incrementCounter();
            System.out.println( "str : " + str);

            count = hrs.getCounter();
            System.out.println( "Result : " + count);

            assertEquals( String.valueOf(expectedCount), String.valueOf(count) );
            expectedCount *=2;
        }
        catch ( Exception e ) {
            e.printStackTrace( System.err );
            fail( className + ": There was an exception! " + e );
        }
    }
}
