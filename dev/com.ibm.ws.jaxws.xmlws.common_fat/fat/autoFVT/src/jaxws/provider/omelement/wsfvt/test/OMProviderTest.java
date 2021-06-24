/**
 * @(#) 1.3 autoFVT/src/jaxws/provider/omelement/wsfvt/test/OMProviderTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/21/09 10:18:08 [8/8/12 06:09:27] 
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
 * 10/05/2009 samerrel      615001          New File
 * 12/21/2009 scheu         632322.fvt      corrected invalid assertion
 * 
 */
package jaxws.provider.omelement.wsfvt.test;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.provider.common.Constants;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Server;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * Tests the WPS performance updates dealing with <code>OMElement</code>. Makes
 * sure the <code>DataSource</code> in the <code>OMElement</code> in a
 * <code>Provider&lt;OMElement&gt;</code> is unexpanded in both Soap 1.1 and
 * 1.2.
 * 
 */
public class OMProviderTest extends FVTTestCase {

    private String endpointUrl;
    private QName serviceName = new QName("http://ws.apache.org/axis2", "OMProviderService");
    private QName portName = new QName("http://ws.apache.org/axis2", "OMProviderPort");
    private static String request = "<invokeOp>Hello Provider OM</invokeOp>";
    private static XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    /**
     * Constructor to create a test case with a given name.
     * 
     * @param name
     *            The name of the test case
     */
    public OMProviderTest(String name) {
        super(name);
    }

    /**
     * The main method.
     * 
     * @param args
     *            The command line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(OMProviderTest.class);
    }

    protected void suiteSetup(ConfigRequirement testSkipCondition) throws Exception {
        super.suiteSetup(testSkipCondition);

    }

    protected void suiteTeardown() throws Exception {
        super.suiteTeardown();

    }

    protected void setUp() throws Exception {
        Server server = TopologyDefaults.getDefaultAppServer();
        String hostname = server.getNode().getHostname();
        Integer port = server.getPortNumber(PortType.WC_defaulthost);
        endpointUrl = "http://" + hostname + ":" + port
                      + "/jwpv.omelement/services/OMProviderService";
        System.out.println("endpointURL=" + endpointUrl);
    }

    /**
     * Test sending a SOAP 1.1 request in MESSAGE mode
     */
    public void testOMElementDispatchMessageMode() throws Exception {

        OMElement response = invokeService(Mode.MESSAGE, false);
        assertNotNull(response);
        String responseText = response.toStringWithConsume();
        checkResponse(responseText, false);

        // Second call
        System.out.println("2nd call");
        response = invokeService(Mode.MESSAGE, false);
        assertNotNull(response);
        String responseTxt = response.toStringWithConsume();
        checkResponse(responseTxt, false);
        checkDataSourceClass(responseTxt);
    }

    /**
     * Test sending a SOAP 1.2 request in MESSAGE mode
     */
    public void testOMElementDispatchMessageMode12() throws Exception {
        OMElement response = invokeService(Mode.MESSAGE, true);
        assertNotNull(response);
        String responseText = response.toStringWithConsume();
        checkResponse(responseText, false);

        System.out.println("2nd call");
        response = invokeService(Mode.MESSAGE, true);
        assertNotNull(response);
        String responseTxt = response.toStringWithConsume();
        checkResponse(responseTxt, false);
        checkDataSourceClass(responseTxt);
    }

    /**
     * Test sending a SOAP 1.1 request in PAYLOAD mode
     */
    public void testOMElementDispatchPayloadMode() throws Exception {
        OMElement response = invokeService(Mode.PAYLOAD, false);
        assertNotNull(response);
        String responseText = response.toStringWithConsume();
        checkResponse(responseText, true);

        System.out.println("2nd call");
        response = invokeService(Mode.PAYLOAD, false);
        assertNotNull(response);

        String responseText2 = response.toStringWithConsume();
        checkResponse(responseText2, true);
        checkDataSourceClass(responseText2);
    }

    /**
     * Test sending a SOAP 1.2 request in PAYLOAD mode
     */
    public void testOMElementDispatchPayloadMode12() throws Exception {
        OMElement response = invokeService(Mode.PAYLOAD, true);
        assertNotNull(response);
        String responseText = response.toStringWithConsume();
        checkResponse(responseText, true);

        System.out.println("2nd call");
        OMElement response2 = invokeService(Mode.PAYLOAD, true);
        assertNotNull(response2);
        String responseText2 = response2.toStringWithConsume();
        checkResponse(responseText2, true);
        checkDataSourceClass(responseText2);
    }

    private void checkResponse(String responseText, boolean payloadMode) {
        if ( responseText != null && !"".equals(responseText) )
            System.out.println("Response: " + responseText);

        if ( payloadMode ) {
            // The payload may contain prefix declarations from the ancestor nodes
            // but it should not contain the Body or Envelope elements.
            //assertTrue(!responseText.contains("soap"));
            assertTrue(!responseText.contains("Body"));
            assertTrue(!responseText.contains("Envelope"));
        } else {
            assertTrue(responseText.contains("soap"));
            assertTrue(responseText.contains("Body"));
            assertTrue(responseText.contains("Envelope"));
        }
        assertTrue(responseText.contains("Hello Dispatch OM"));
    }

    /**
     * Checks that the backing <code>DataSource</code> of the
     * <code>OMDataSource</code> is correct. For WAS700x it is
     * <code>org.apache.axis2.jaxws.contex.listener.ParsedEntityDataSource</code>
     * and for Matterhorn it is <code>"org.apache.axiom.om.ds.ParserInputStreamDataSource</code>.
     * 
     * @param responseText
     */
    private void checkDataSourceClass(String responseText) {
        System.out.println("Checking DataSource class...");
        boolean containsDataSourceClass = responseText.contains("org.apache.axis2.jaxws.context.listener.ParsedEntityDataSource")
        || responseText.contains("org.apache.axiom.om.ds.ParserInputStreamDataSource");
        assertTrue(containsDataSourceClass);
    }

    public OMElement invokeService(Mode serviceMode, boolean isSoap12) throws XMLStreamException {
        System.out.println("Creating service...");
        Service service = Service.create(serviceName);
        String requestMsg = Constants.SOAP11_HEADER + request + Constants.SOAP11_TRAILER;

        if ( isSoap12 ) {
            requestMsg = Constants.SOAP12_HEADER + request + Constants.SOAP12_TRAILER;
            service.addPort(portName, SOAPBinding.SOAP12HTTP_BINDING, endpointUrl);
        } else {
            service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);
        }

        System.out.println("Creating dispatch");
        Dispatch<OMElement> dispatch = service.createDispatch(portName, OMElement.class,
                                                              serviceMode);

        System.out.println("requestMsg=" + requestMsg);
        StringReader sr = new StringReader(requestMsg);
        XMLStreamReader inputReader = inputFactory.createXMLStreamReader(sr);
        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(inputReader, null);
        OMElement om = builder.getDocumentElement();
        System.out.println("Invoking dispatch");
        return dispatch.invoke(om);
    }
}
