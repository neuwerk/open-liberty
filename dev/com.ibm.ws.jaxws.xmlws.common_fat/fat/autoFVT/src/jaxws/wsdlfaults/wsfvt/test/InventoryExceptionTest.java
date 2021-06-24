//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId         Defect            Description
// ----------------------------------------------------------------------------
// 10/05/2006  mzheng         LIDB3296-46.01    New File
// 01/26/2007  mzheng         417209            Added testSEIProtocolException()
// 01/30/2007  mzheng         417803            Fixed Dispatch source
// 05/25/2007  jramos         440922            Integrate ACUTE
// 06/13/2014  zzebj          131103            don't check exception type since security reason.

package jaxws.wsdlfaults.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import javax.xml.ws.Dispatch;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import jaxws.wsdlfaults.wsfvt.client.inventory.*;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;


/**
 * This test cases verify:
 *
 * (1) The Provider based Web service throws correct ProtocolException when 
 *     there is error processing the request
 *
 * (2) The SEI based Web service may throw application specific exception and 
 *     JAX-WS maps such exception to SOAP fault message and deserialize it 
 *     per JAX-WS spec
 *
 * (3) Unchecked server side exception is handled appropriately
 *
 * (4) The wsdl:fault to Java exception mapping can be customized, and JAX-WS 
 *     handles customized wrapper exception correctly
 */

public class InventoryExceptionTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private IAppServer server = QueryDefaultNode.defaultAppServer;

    private static final String NAME_SPACE = "http://inventory.wsdlfaults.jaxws";

    private static final String SCHEMA_NAMESPACE = "http://inventory.wsdlfaults.jaxws/xsd";

    private static final String SCHEMA_PREFIX = "ns1";

    private String provider_epAddress =
      "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) + 
      "/jaxws-wsdlfaults2/InventoryProvider";

    private static final QName providerQName =
        new QName(NAME_SPACE, "InventoryProvider");

    private static final QName providerPortQName =
        new QName(NAME_SPACE, "InventoryProviderPort");

    private String sei_epAddress =
      "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) + 
      "/jaxws-wsdlfaults2/InventoryService";

    private static final QName seiQName =
        new QName(NAME_SPACE, "InventoryService");

    private static final QName seiPortQName =
        new QName(NAME_SPACE, "InventoryPort");

    /**
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public InventoryExceptionTest(String name) {
        super(name);
    }


    /**
     * The main method.
     *
     * @param args The command line arguments
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
        return new TestSuite(InventoryExceptionTest.class);
    }


    /**
     * @testStrategy This test invokes the Provider based service with a 
     *               wrong operation and JAX-WS client runtime should 
     *               throw a WebServiceException. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test invokes the Provider based service with a  wrong operation and JAX-WS client runtime should throw a WebServiceException.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProviderWrongOperation() {
        System.err.println("====== In testProviderWrongOperation() ======\n");

        String operation = "testInventory";
        String id = "T40";
        Integer count = new Integer(5);

        try {
            Dispatch<Source> testDispatch = getProviderDispatch();

            Source request = createRequest(operation, id, count);

            Source result = testDispatch.invoke(request);

            fail("Should have caught a WebServiceException");
        } catch (WebServiceException e) {

            // Expect a WSE thrown by JAX-WS client runtime
            
            System.err.println("====== SUCCESS: testProviderWrongOperation() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test uses Dispatch to invokes the Provider bases 
     *               service and triggers a service specific exception.  
     *               Client should be able to catch SOAPFaultException
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test uses Dispatch to invokes the Provider bases  service and triggers a service specific exception. Client should be able to catch SOAPFaultException",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProviderServiceException() {
        System.err.println("====== In testProviderServiceException() ======\n");

        String operation = "removeFromInventory";
        String id = "T20";
        Integer count = new Integer(20);

        try {
            Dispatch<Source> testDispatch = getProviderDispatch();

            Source request = createRequest(operation, id, count);

            Source result = testDispatch.invoke(request);

            fail("Should have caught an SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Caught InventoryOperationException in " + operation, e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testProviderServiceException() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test catches unchecked ProtocolException in 
     *               Provider based service. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches unchecked ProtocolException in  Provider based service.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProviderUncheckedProtocolException() {

        System.err.println("====== In testProviderUncheckedProtocolException() ======\n");
        String operation = "removeFromInventory";
        String id = "T40";
        Integer count = new Integer(5);

        try {
            Dispatch<Source> testDispatch = getProviderDispatch();

            Source request = createRequest(operation, id, count);

            Source result = testDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("No product to remove: " + id, e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testProviderUncheckedProtocolException() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test tests checked NullPointerException in 
     *               Provider based service. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test tests checked NullPointerException in  Provider based service.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProviderCheckedNPE() {

        System.err.println("====== In testProviderCheckedNPE() ======\n");
        String operation = "removeFromInventory";
        String id = "null";
        Integer count = new Integer(5);

        try {
            Dispatch<Source> testDispatch = getProviderDispatch();

            Source request = createRequest(operation, id, count);

            Source result = testDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Caught InventoryOperationException in " + operation, e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testProviderCheckedNPE() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test triggers an unchecked NullPointerException 
     *               thrown by Provider based service.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers an unchecked NullPointerException  thrown by Provider based service.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProviderUncheckedNPE() {

        System.err.println("====== In testProviderUncheckedNPE() ======\n");
        String operation = "addToInventory";
        String id = "null";
        Integer count = new Integer(5);

        try {
            Dispatch<Source> testDispatch = getProviderDispatch();

            Source request = createRequest(operation, id, count);

            Source result = testDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Null product id", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testProviderUncheckedNPE() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught in testProviderUncheckedNPE(): " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test invokes the Provider bases service and triggers
     *               a service specific exception.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test invokes the Provider bases service and triggers a service specific exception.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProxyToProviderServiceException() {
        System.err.println("====== In testProxyToProviderServiceException() ======\n");

        String id = "T20";
        int count = 30;

        try {
            InventoryProvider myService = new InventoryProvider();

            InventoryPortType myPort = myService.getInventoryProviderPort();

            boolean ret = myPort.removeFromInventory(id, count);

            fail("Should have caught an SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Caught InventoryOperationException in removeFromInventory", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testProxyToProviderServiceException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test catches unchecked ProtocolException in 
     *               Provider based service. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches unchecked ProtocolException in  Provider based service.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProxyToProviderUncheckedProtocolException() {

        System.err.println("====== In testProxyToProviderUncheckedProtocolException() ======\n");

        String id = "T40";
        int count = 6;

        try {
            InventoryProvider myService = new InventoryProvider();

            InventoryPortType myPort = myService.getInventoryProviderPort();

            boolean ret = myPort.removeFromInventory(id, count);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("No product to remove: " + id, e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testProxyToProviderUncheckedProtocolException() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test tests checked NullPointerException in 
     *               Provider based service. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test tests checked NullPointerException in  Provider based service.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProxyToProviderCheckedNPE() {

        System.err.println("====== In testProxyToProviderCheckedNPE() ======\n");
        String id = "null";
        int count = 8;

        try {
            InventoryProvider myService = new InventoryProvider();

            InventoryPortType myPort = myService.getInventoryProviderPort();

            boolean ret = myPort.removeFromInventory(id, count);

            fail("Should have caught an SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Caught InventoryOperationException in removeFromInventory", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testProxyToProviderCheckedNPE() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test triggers an unchecked NullPointerException 
     *               thrown by Provider based service.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers an unchecked NullPointerException  thrown by Provider based service.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testProxyToProviderUncheckedNPE() {

        System.err.println("====== In testProxyToProviderUncheckedNPE() ======\n");

        String id = "null";
        int count = 9;

        try {
            InventoryProvider myService = new InventoryProvider();

            InventoryPortType myPort = myService.getInventoryProviderPort();

            boolean ret = myPort.addToInventory(id, count);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Null product id", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testProxyToProviderUncheckedNPE() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught in testProviderUncheckedNPE(): " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test sends a request to SEI based endpoint and 
     *               triggers service specific exception 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test sends a request to SEI based endpoint and  triggers service specific exception",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSEIServiceException() {
        System.err.println("====== In testSEIServiceException() ======\n");

        String id = "T20";
        int count = 20;

        try {
            InventoryService myService = new InventoryService();

            InventoryPortType myPort = myService.getInventoryPort();

            boolean ret = myPort.removeFromInventory(id, count);

            fail("Should have caught an OperationException_Exception");
        } catch (OperationFault_Exception e) {
            assertEquals("Not enough products to remove", e.getMessage());

            assertEquals("Request to remove: " + count + ", Available: 10", e.getFaultInfo().getDetail());

            System.err.println("====== SUCCESS: testSEIServiceException() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught in testSEIServiceException(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test catches ProtocolException thrown in 
     *               application
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches ProtocolException thrown in  application",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSEIProtocolException() {
        System.err.println("====== In testSEIProtocolException() ======\n");

        String id = "T40";
        int count = 5;

        try {
            InventoryService myService = new InventoryService();

            InventoryPortType myPort = myService.getInventoryPort();

            boolean ret = myPort.removeFromInventory(id, count);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("No product to remove: " + id, e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testSEIProtocolException() ======\n");
        } catch (Exception e) {

            e.printStackTrace();
            fail("Unexpected Exception caught in testSEIProtocolException(): " + e.getClass().getName() + ": " + e.getMessage());
        }

    }

    /**
     * @testStrategy This test triggers a service specific exception and 
     *               verifies exception cause are set correctly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a service specific exception and  verifies exception cause are set correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSEICheckedNPE() {
        System.err.println("====== In testSEICheckedNPE() ======\n");

        String id = "null";
        int count = 5;

        try {
            InventoryService myService = new InventoryService();

            InventoryPortType myPort = myService.getInventoryPort();

            boolean ret = myPort.removeFromInventory(id, count);

            fail("Should have caught an OperationException_Exception");

        } catch (OperationFault_Exception e) {
            assertEquals("Caught NPE", e.getMessage());

            assertEquals("Runtime NullPointerException caught in removeFromInventory()", e.getFaultInfo().getDetail());

            System.err.println("====== SUCCESS: testSEICheckedNPE() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught in testSEICheckedNPE(): " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test will trigger a unchecked NullPointerException,
     *               and verifies server throws ProtocolException or one of 
     *               its subclasses caused by NPE. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test will trigger a unchecked NullPointerException, and verifies server throws ProtocolException or one of its subclasses caused by NPE.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSEIUncheckedNPE() {
        System.err.println("====== In testSEIUncheckedNPE() ======\n");

        String id = "null";
        int count = 5;

        try {
            InventoryService myService = new InventoryService();

            InventoryPortType myPort = myService.getInventoryPort();

            boolean ret = myPort.addToInventory(id, count);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Null product id", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testSEIUncheckedNPE() ======\n");

        } catch (Exception e) {

            e.printStackTrace();
            fail("Caught unexpected exception in testSEIUncheckedNPE(): "  + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test triggers a service specific exception as 
     *               testSEIException().  However, since Dispatch<Source> 
     *               is used, SOAPFaultException should be caught 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a service specific exception as  testSEIException().  However, since Dispatch<Source> is used, SOAPFaultException should be caught",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatchToSEIException() {
        System.err.println("====== In testDispatchToSEIException() ======\n");

        String operation = "removeFromInventory";
        String id = "T20";
        Integer count = new Integer(35);

        try {
            Dispatch<Source> testDispatch = getSEIDispatch();

            Source request = createRequest(operation, id, count);

            Source result = testDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Not enough products to remove", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testDispatchToSEIException() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught in testDispatchToSEIException(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test triggers a service specific exception and 
     *               verifies exception cause are set correctly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a service specific exception and  verifies exception cause are set correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatchToSEICheckedNPE() {
        System.err.println("====== In testDispatchToSEICheckedNPE() ======\n");

        String operation = "removeFromInventory";
        String id = "null";
        Integer count = new Integer(15);

        try {
            Dispatch<Source> testDispatch = getSEIDispatch();

            Source request = createRequest(operation, id, count);

            Source result = testDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Caught NPE", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testDispatchToSEICheckedNPE() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught in testDispatchToSEICheckedNPE(): " + e.getClass().getName() + ": " + e.getMessage());
        }

    }


    /**
     * @testStrategy This test will trigger a unchecked NullPointerException,
     *               and verifies server throws ProtocolException or one of 
     *               its subclasses caused by NPE. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test will trigger a unchecked NullPointerException, and verifies server throws ProtocolException or one of its subclasses caused by NPE.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDispatchToSEIUncheckedNPE() {
        System.err.println("====== In testDispatchToSEIUncheckedNPE() ======\n");

        String operation = "queryInventory";
        String id = "null";

        try {
            Dispatch<Source> testDispatch = getSEIDispatch();

            Source request = createRequest(operation, id, null);

            Source result = testDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");
        } catch (SOAPFaultException e) {
            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            //this behiviour will be changed in cxf3.0 since security reason. The ws engine shouldn't expose server side exceptions to client.
            //assertEquals("java.lang.NullPointerException", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testDispatchToSEIUncheckedNPE() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught in testDispatchToSEIUncheckedNPE(): " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * Create a Dispatch<Source> instance for InventoryProvider service
     */
    private Dispatch<Source> getProviderDispatch() {
        Dispatch<Source> thisDispatch = null;

        try {
            Service thisService = Service.create(providerQName);
            thisService.addPort(providerPortQName, null, provider_epAddress);

            thisDispatch = thisService.createDispatch(providerPortQName, Source.class, Service.Mode.PAYLOAD);

        } catch (WebServiceException e) {
            e.printStackTrace();
            fail("Caught WebServiceException in getProviderDispatch()");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception in getProviderDispatch()");
        }

        return thisDispatch;
    }


    /**
     * Create a Dispatch<Source> instance for InventoryService service
     */
    private Dispatch<Source> getSEIDispatch() {
 
        Dispatch<Source> thisDispatch = null;

        try {
            Service thisService = Service.create(seiQName);
            thisService.addPort(seiPortQName, null, sei_epAddress);

            thisDispatch = thisService.createDispatch(seiPortQName, Source.class, Service.Mode.PAYLOAD);

        } catch (WebServiceException e) {
            e.printStackTrace();
            fail("Caught WebServiceException in getSEIDispatch()");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected exception in getSEIDispatch()");
        }

        return thisDispatch;
    }


    /**
     * Create a Source request to be used by Dispatch<Source>
     */
    private Source createRequest(String operation,
                                 String id, 
                                 Integer count) {
        
        String idString = null;
        String countString = null;
        String reqString = null;

        if (id != null) {
            idString = "<id>" + id + "</id>"; 
        } else {
            idString = "<id xsi:nil=\"true\" + xmlns:xsi=\"http://www.w3.org/2001/XMLSchema\"></id>";
        }

        if (count != null) {
            countString = "<count>" + count.intValue() + "</count>";

            reqString = "<" + SCHEMA_PREFIX + ":" + operation +
                        " xmlns:" + SCHEMA_PREFIX +
                        "=\"" + SCHEMA_NAMESPACE + "\">" +
                        idString + countString +
                        "</" + SCHEMA_PREFIX + ":" + operation + ">";
        } else {
            reqString = "<" + SCHEMA_PREFIX + ":" + operation +
                        " xmlns:" + SCHEMA_PREFIX +
                        "=\"" + SCHEMA_NAMESPACE + "\">" + idString +
                        "</" + SCHEMA_PREFIX + ":" + operation + ">";
        }

        Source request = new StreamSource(new StringReader(reqString));

        return request;
    }
}
