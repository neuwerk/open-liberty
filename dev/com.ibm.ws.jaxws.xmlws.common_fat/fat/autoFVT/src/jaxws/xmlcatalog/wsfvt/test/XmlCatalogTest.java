/**
 * @(#) 1.2 autoFVT/src/jaxws/xmlcatalog/wsfvt/test/XmlCatalogTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/13/09 18:19:55 [8/8/12 06:09:26] 
 * 
 * IBM Confidential OCO Source Material
 * (C) COPYRIGHT International Business Machines Corp. 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date       UserId        Feature/Defect  Description
 * ----------------------------------------------------------------------------
 * 06/29/2009 samerrel      552420          New File
 * 08/13/2009 samerrel      607011          Fixed suitesetup to get hostandport on ND
 * 
 */
package jaxws.xmlcatalog.wsfvt.test;

import jaxws.xmlcatalog.wsfvt.client.WSClient;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * Tests the XML Catalog functionality in JAX-WS, including resolving xsd
 * imports, and wsdlLocation.
 * 
 */
public class XmlCatalogTest extends FVTTestCase {

    public static WSClient wsc; // Used to drive the tests.

    /**
     * Constructor to create a test case with a given name.
     * 
     * @param name
     *            The name of the test case
     */
    public XmlCatalogTest(String name) {
        super(name);
    }

    /**
     * The main method.
     * 
     * @param args
     *            The command line arguments
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(XmlCatalogTest.class);
    }

    /**
     * 
     * <p>
     * Runs the <code>manageprofiles</code> command to create and augment
     * profiles used in the tests.
     * </p>
     */
    protected void suiteSetup(ConfigRequirement testSkipCondition) throws Exception {
		super.suiteSetup(testSkipCondition);
        ApplicationServer server = TopologyDefaults.getDefaultAppServer();
        String hostandport = server.getNode().getMachine().getHostname() + ":"
                             + server.getPortNumber(PortType.WC_defaulthost);
        wsc = new WSClient();
        wsc.setHostandport(hostandport);
    }

    /**
     * Tests a direct call to the HelloWar web service, the service has an xsd
     * import in the wsdl.
     * 
     * **NOTE** This test currently does not work, and the corresponding defect
     * will not be complete until Matterhorn.
     */
    public void _testHelloWar() {
        String msg = "";
        try {
            msg = wsc.testHelloWar("testHelloWar");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("Hello, testHelloWar!", msg);
    }

    /**
     * Tests that the HelloWarClient web service's WebServiceRef annotation
     * resolves to the correct wsdl in the jax-ws-catalog instead of the
     * non-existent wsdlLocation from the annotation. This test calls the
     * HelloWar service.
     */
    public void testHelloWarClientBadWsdlloc() {
        String msg = "";
        try {
            msg = wsc.testHelloWarClientBadWsdlLoc("testHelloWarClientBadWsdlloc");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("Hello, testHelloWarClientBadWsdlloc!", msg);
    }

    /**
     * Tests that the HelloWarClient web service's WebServiceRef annotation
     * resolves to the correct wsdl in the jax-ws-catalog instead of the
     * non-existent wsdlLocation from the annotation. This test calls the
     * HelloEjb service.
     */
    public void testHelloWarClientEjbRefBadWsdlloc() {
        String msg = "";
        try {
            msg = wsc.testHelloWarClientEjbRefBadWsdlLoc("testHelloWarClientEjbRefBadWsdlloc");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("Hello, testHelloWarClientEjbRefBadWsdlloc!", msg);
    }

    /**
     * Tests a direct call to the HelloEjb web service, the service has an xsd
     * import in the wsdl.
     * 
     * **NOTE** This test currently does not work, and the corresponding defect
     * will not be complete until Matterhorn.
     */
    public void _testHelloEjb() {
        String msg = "";
        try {
            msg = wsc.testHelloEjb("testHelloEjb");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("Hello, testHelloEjb!", msg);
    }

    /**
     * Tests that the HelloEjbClient web service's WebServiceRef annotation
     * resolves to the correct wsdl in the jax-ws-catalog instead of the
     * non-existent wsdlLocation from the annotation. This test calls the
     * HelloEjb service.
     */
    public void testHelloEjbClientBadWsdlloc() {
        String msg = "";
        try {
            msg = wsc.testHelloEjbClientBadWsdlLoc("testHelloEjbClientBadWsdlloc");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("Hello, testHelloEjbClientBadWsdlloc!", msg);
    }

    /**
     * Tests that the HelloWarClient web service's WebServiceRef annotation
     * resolves to the correct wsdl in the jax-ws-catalog instead of the
     * non-existent wsdlLocation from the annotation. This test calls the
     * HelloWar service.
     */
    public void testHelloEjbClientWarRefBadWsdlloc() {
        String msg = "";
        try {
            msg = wsc.testHelloEjbClientWarRefBadWsdlLoc("testHelloEjbClientWarRefBadWsdlloc");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("Hello, testHelloEjbClientWarRefBadWsdlloc!", msg);
    }

}
