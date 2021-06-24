/**
 * autoFVT/src/jaxwsejb30/ejbapps/serverdepdesc/test/WebservicesXmlOverrideTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date       UserId       Feature/Defect       Description
 * -----------------------------------------------------------------------------
 * 06/28/2008 Samerrel     LIDB4511.45.01       New File
 *
 */
package jaxwsejb30.ejbapps.serverdepdesc.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

import jaxwsejb30.ejbapps.serverdepdesc.server.echoejb.EjbEcho;
import jaxwsejb30.ejbapps.serverdepdesc.server.echoejb.EjbEchoService;
import jaxwsejb30.ejbapps.serverdepdesc.server.echowar.WarEcho;
import jaxwsejb30.ejbapps.serverdepdesc.server.echowar.WarEchoService;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import com.ibm.ws.wsfvt.test.framework.FvtTest;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.TopologyActions;

/**
 * This class tests the {@link EjbEcho} and {@link WarEcho} JAX-WS services to
 * determine if the JSR-109 webservices.xml file overrides the WebService
 * annotation.
 * 
 */
public class WebservicesXmlOverrideTest extends FVTTestCase {

    /*
     * ACUTE classes used to access server information such as server name and
     * default port, these are used to avoid compile-time hard coding of URLs,
     * hosts, ports, etc
     */
    private static final IAppServer TST_SVR = TopologyDefaults.defaultAppServer;
    private static final String MACHINE = TST_SVR.getMachine().getHostname();
//    private static final Integer TST_PORT = TST_SVR
//                                                   .getPort(Ports.BOOTSTRAP_ADDRESS);
    private static final Integer DEFAULT_PORT = TST_SVR
                                                       .getPort(Ports.WC_defaulthost);
    private static final String FVT_HOST_NAME = TopologyActions.FVT_HOSTNAME;
    private static final String SVRURL = "http://" + MACHINE + ":"
                                         + DEFAULT_PORT + "/";

    /*
     * EjbEcho
     */
    private static URL serviceUrl;
    private static QName qn;
    private static EjbEcho ejbEch;
    private static EjbEchoService ejbEchSvc;

    /*
     * WarEcho
     */
    private static URL su;
    private static QName qn1;
    private static WarEcho warEch;
    private static WarEchoService warEchSvc;

    /*
     * These static members are used in all test runs, to reduce overhead they
     * are static and used throughout the tests instead of initializing a new
     * one each test
     */
    @Override
    protected void suiteSetup(ConfigRequirement testSkipCondition) throws Exception {
    	super.suiteSetup(testSkipCondition);
        
    	try {

            /*
             * EjbEcho Instantiation
             */
            serviceUrl = new URL(
                                 SVRURL
                                         + "jwsejb30-serverdepdesc-echoejb/EjbEchoService?wsdl");
            qn = new QName(
                           "http://echoejb.server.serverdepdesc.ejbapps.jaxwsejb30/",
                           "EjbEchoService");
            ejbEchSvc = new EjbEchoService(serviceUrl, qn);
            ejbEch = ejbEchSvc.getEjbEchoPort();

            /*
             * WarEcho Instantiation
             */
            su = new URL(SVRURL
                         + "jwsejb30-serverdepdesc-echowar/WarEchoService?wsdl");
            qn1 = new QName(
                            "http://echowar.server.serverdepdesc.ejbapps.jaxwsejb30/",
                            "WarEchoService");
            warEchSvc = new WarEchoService(su, qn1);
            warEch = warEchSvc.getWarEchoPort();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        System.out.println(WebservicesXmlOverrideTest.class.getName());
        return new TestSuite(WebservicesXmlOverrideTest.class);
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
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /*
     * Tests:
     * 
     * Call the web service
     * 
     * Test wsdlLoc override
     * 
     * MTOM test
     * 
     * handler chain tests
     */

    /**
     * Tests that the {@link EjBEcho} service is available.
     * 
     * @testStrategy - calls the {@link EjbEcho#echo(String)} method, expects
     *               "ECHO: (message)"
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "Calls the EjbEcho echo method.", since = FvtTest.Releases.WAS70, expectedResult = "")
    public static void testCallEjbEcho() {
        assertEquals("ECHO: testCallEjbEcho", ejbEch.echo("testCallEjbEcho"));
    }

    /**
     * Tests that the {@link WarEcho} service is available.
     * 
     * @testStrategy - calls the {@link WarEcho#echo(String)} method, expects
     *               "ECHO: (message)"
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "Calls the WarEcho echo method.", since = FvtTest.Releases.WAS70, expectedResult = "")
    public static void testCallWarEcho() {
        assertEquals("ECHO: testCallWarEcho", warEch.echo("testCallWarEcho"));
    }

    /**
     * Tests that the <code>&lt;wsdl-file&gt;</code> element of webservices.xml
     * overrides the <code>@WebService(wsdlLocation)</code> attribute.
     * 
     * @testStrategy - Opens a stream to the overridden WSDL location to
     *               determine if the WSDL is present. Fails if an exception is
     *               thrown when opening the stream to the WSDL.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "Tests the wsdlLocation is overridden by webservices.xml wsdl-file.", since = FvtTest.Releases.WAS70, expectedResult = "")
    public static void testWsdlLocOverrideEjb() {
        InputStream input = null;
        URL myUrl = null;
        URI myUri = null;
        /*
         * TODO: It might be a good idea to take this URL and allow it to be set
         * by a parameter. This would keep the test from being too fragile if
         * WAS changes how it shows the WSDL in a URL
         */
        /* String wsdlloc = SVRURL
                         + "jwsejb30-serverdepdesc-echoejb/EjbEchoService/"
                         + "META-INF/wsdl/EjbEchoService1.wsdl";
                         */
        String wsdlloc = SVRURL + "jwsejb30-serverdepdesc-echoejb/EjbEchoService?wsdl";

        System.out.println("Attempting to retrieve wsdl at: " + wsdlloc);
        try {
            myUri = URI.create(wsdlloc);
            myUrl = myUri.toURL();
            /*
             * Check to see if something is there.
             */
            input = myUrl.openStream();
            String wsdlFileContent = convertStreamToString(input, "UTF-8");
            String uniqueString = "xmlns:wsp=\"http://www.w3.org/ns/ws-policy\"";
            assertFalse("Unexpected content of EjbEchoService1.wsdl returns", wsdlFileContent.contains(uniqueString));
        } catch (Exception e) {
            e.printStackTrace();
            fail("could not find wsdl at: " + wsdlloc);
        }
        // We didn't encounter any problems
        assertTrue(true);
    }

    /**
     * Tests that the <code>&lt;wsdl-file&gt;</code> element of webservices.xml
     * overrides the <code>@WebService(wsdlLocation)</code> attribute.
     * 
     * @testStrategy - Opens a stream to the overridden WSDL location to
     *               determine if the WSDL is present. Fails if an exception is
     *               thrown when opening the stream to the WSDL.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "Tests the wsdlLocation is overridden by webservices.xml wsdl-file.", since = FvtTest.Releases.WAS70, expectedResult = "")
    public static void testWsdlLocOverrideWar() {
        InputStream input = null;
        URL myUrl = null;
        URI myUri = null;
        // Liberty does not support this kind of URL
        /*String wsdlloc = SVRURL
                         + "jwsejb30-serverdepdesc-echowar/WarEchoService/"
                         + "WEB-INF/wsdl/WarEchoService1.wsdl";*/
        String wsdlloc = SVRURL + "jwsejb30-serverdepdesc-echowar/WarEchoService?wsdl";

        System.out.println("Attempting to retrieve wsdl at: " + wsdlloc);
        try {
            myUri = URI.create(wsdlloc);
            myUrl = myUri.toURL();
            /*
             * Check to see if something is there.
             */
            input = myUrl.openStream();
            String wsdlFileContent = convertStreamToString(input, "UTF-8");
            String uniqueString = "xmlns:wsp=\"http://www.w3.org/ns/ws-policy\"";
            assertFalse("Unexpected content of WarEchoService1.wsdl returns", wsdlFileContent.contains(uniqueString));
        } catch (Exception e) {
            e.printStackTrace();
            fail("could not find expected wsdl at: " + wsdlloc);
        }
    }
    
    private static String convertStreamToString(InputStream ins, String encoding) throws IOException {
    	InputStreamReader inputReader = new InputStreamReader(ins, encoding);
    	
    	StringWriter writer = new StringWriter();
    	
    	char[] buffer = new char[4096];
        int count = 0;
        int n = 0;
        while (-1 != (n = inputReader.read(buffer))) {
          writer.write(buffer, 0, n);
          count += n;
        }
        System.out.println("Read " + count + " bytes");
        
        return writer.toString();    	
    }

}
