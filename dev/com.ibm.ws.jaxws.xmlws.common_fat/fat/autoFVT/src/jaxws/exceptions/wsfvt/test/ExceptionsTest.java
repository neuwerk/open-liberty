//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId       Defect           Description
// ----------------------------------------------------------------------------
// 10/05/2006  mzheng       LIDB3296-46.02   New File
// 01/26/2007  mzheng       417209           Fixed validation string
// 01/30/2007  mzheng       417803           Fixed validation string
// 03/12/2007  mzheng       425326           Added test cases for soap 1.2
// 05/12/2007  mzheng       438404           Fix assert for iSeries
// 05/25/2007  jramos       440922           Integrate ACUTE
// 06/27/2007  mzheng       445695           Overwrite wsdlLocation at runtime
//

package jaxws.exceptions.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.WebServiceException;

import com.ibm.websphere.simplicity.OperatingSystem;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

import com.ibm.ws.wsfvt.build.tools.AntProperties;

import jaxws.exceptions.wsfvt.client.soap11.*;
import jaxws.exceptions.wsfvt.client.soap12.*;

public class ExceptionsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private IAppServer server = QueryDefaultNode.defaultAppServer;

    private boolean isServerWindows = (server.getMachine().getOperatingSystem() == OperatingSystem.WINDOWS);

    private final static String testString = "Hello World";

    private final static int UNSUPPORTED_OPERATION = 101;

    private static final String NAMESPACE = "http://exceptions.jaxws";

    private static final String PREFIX = "ns1";

    private String epAddress =
      "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) + 
      "/jaxws-exceptions/WSStringService";

    private static final QName serviceQName = 
                               new QName(NAMESPACE, "WSStringService");

    private static final QName portQName =
                               new QName(NAMESPACE, "WSStringServicePort");

    private String soap12EpAddress =
      "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) + 
      "/jaxws-exceptions/WSStringServiceSOAP12";

    private static final QName soap12ServiceQName = 
                               new QName(NAMESPACE, "WSStringServiceSOAP12");

    private static final QName soap12PortQName =
                               new QName(NAMESPACE, "WSStringServiceSOAP12Port");

    private static final String wsdlLocationSOAP12 = AntProperties.getAntProperty("FVT.build.work.dir") + File.separator + "jaxws" + File.separator + "exceptions" + File.separator + "etc" + File.separator + "WSStringServiceSOAP12.wsdl";

    private static final URL wsdlURLSOAP12;
    static {
        URL url = null;
        try {
            File wsdlFile = new File(wsdlLocationSOAP12);
            url = wsdlFile.toURL();
        } catch (MalformedURLException e) {
            System.err.println("Caught MalformedURLException when creating file URL");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getName() + " when creating file URL");
            e.printStackTrace();
        }
        wsdlURLSOAP12 = url;
    }

    /**
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public ExceptionsTest(String name) {
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
        return new TestSuite(ExceptionsTest.class);
    }


    /**
     * @testStrategy This test sends a request to SEI based endpoint and 
     *               triggers a service specific exception.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test sends a request to SEI based endpoint and  triggers a service specific exception.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testOperationExceptionUseProxy() {

        System.err.println("====== In testOperationExceptionUseProxy() ======");

        String action = "fault";
        String arg = "abc";

        try {
            WSStringService myService = new WSStringService();

            WSStringServicePortType myProxy = myService.getWSStringServicePort();

            String ret = myProxy.doStringOperation(testString, action, arg);

            fail("Should have caught an OperationException_Exception");

        } catch (jaxws.exceptions.wsfvt.client.soap11.OperationException_Exception e) {

            assertEquals("Unsupported operation", e.getMessage());

            assertEquals("Unsupported operation", e.getFaultInfo().getMessage());

            assertEquals(action + " is not a supported String operation", e.getFaultInfo().getDetail());

            assertEquals(UNSUPPORTED_OPERATION, e.getFaultInfo().getCode());

            System.err.println("====== SUCCESS: testOperationExceptionUseProxy() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testOperationExceptionUseProxy()");
        }

    }


    /**
     * @testStrategy This test triggers a service specific exception and 
     *               verifies exception name is customized and fault bean 
     *               is wrapped correctly
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a service specific exception and  verifies exception name is customized and fault bean is wrapped correctly",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testCustomizedServiceExceptionUseProxy() {

        System.err.println("====== In testCustomizedServiceExceptionUseProxy() ======");

        String action = "charAt";
        String arg = "3a";

        try {
            WSStringService myService = new WSStringService();

            WSStringServicePortType myProxy = myService.getWSStringServicePort();

            String ret = myProxy.doStringOperation(testString, action, arg);

            fail("Should have caught a TestServiceException");

        } catch (jaxws.exceptions.wsfvt.client.soap11.TestServiceException e) {

            assertEquals("Runtime exception caught", e.getMessage());

            assertEquals("Runtime exception caught", e.getFaultInfo().getMessage());

            assertEquals("Integer.parseInt() throws NumberFormatException: " + arg, e.getFaultInfo().getDetail());

            System.err.println("====== SUCCESS: testCustomizedServiceExceptionUseProxy() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testCustomizedServiceExceptionUseProxy()");
        }
    }


    /**
     * @testStrategy This test triggers a runtime IndexOutOfBoundsException
     *               and verifies the exception is handled correctly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a runtime IndexOutOfBoundsException and verifies the exception is handled correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testUncheckedExceptionUseProxy() {

        System.err.println("====== In testUncheckedExceptionUseProxy() ======");

        String action = "charAt";
        String arg = "100";

        try {
            WSStringService myService = new WSStringService();

            WSStringServicePortType myProxy = myService.getWSStringServicePort();

            String ret = myProxy.doStringOperation(testString, action, arg);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            // For some platforms, like iSeries, HPUX, 
            // StringIndexOutOfBoundsException.getMessage() may not be null, 
            // in which cases platform specific message would be used for 
            // SOAP FaultString
            if (isServerWindows) {
                assertEquals("java.lang.StringIndexOutOfBoundsException", e.getFault().getFaultString());
            }

            System.err.println("====== SUCCESS: testUncheckedExceptionUseProxy() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testUncheckedExceptionUseProxy()");
        }
    }


    /**
     * @testStrategy This test triggers a WebServiceException thrown by
     *               service implementation and client catches 
     *               SOAPFaultException
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a WebServiceException thrown by service implementation and client catches SOAPFaultException",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWebServiceExceptionUseProxy() {

        System.err.println("====== In testWebServiceExceptionUseProxy() ======");

        String action = "WebServiceException";
        String arg = "abc";

        try {
            WSStringService myService = new WSStringService();

            WSStringServicePortType myProxy = myService.getWSStringServicePort();

            String ret = myProxy.doStringOperation(testString, action, arg);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Client requested WebServiceException", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testWebServiceExceptionUseProxy() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testWebServiceExceptionUseProxy()");
        }
    }


    /**
     * @testStrategy This test sends a request to SEI based endpoint and 
     *               triggers a service specific exception.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test sends a request to SEI based endpoint and  triggers a service specific exception.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testOperationExceptionUseDispatch() {

        System.err.println("====== In testOperationExceptionUseDispatch() ======");

        String action = "fault";
        String arg = "abc";

        try {
            Dispatch<Source> myDispatch = getDispatch();

            Source request = createRequest(action, arg);

            Source result = myDispatch.invoke(request);

            fail("Should have caught an SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Unsupported operation", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testOperationExceptionUseDispatch() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testOperationExceptionUseDispatch()");
        }

    }


    /**
     * @testStrategy This test triggers a service specific exception use 
     *               Dispatch<Source>
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a service specific exception use  Dispatch<Source>",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testCustomizedServiceExceptionUseDispatch() {

        System.err.println("====== In testCustomizedServiceExceptionUseDispatch() ======");
        String action = "charAt";
        String arg = "3a";

        try {
            Dispatch<Source> myDispatch = getDispatch();

            Source request = createRequest(action, arg);

            Source result = myDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Runtime exception caught", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testCustomizedServiceExceptionUseDispatch() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testCustomizedServiceExceptionUseDispatch(()");
        }
    }


    /**
     * @testStrategy This test triggers a runtime IndexOutOfBoundsException
     *               and verifies the exception is handled correctly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a runtime IndexOutOfBoundsException and verifies the exception is handled correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testUncheckedExceptionUseDispatch() {

        System.err.println("====== In testUncheckedExceptionUseDispatch() ======");

        String action = "charAt";
        String arg = "100";

        try {
            Dispatch<Source> myDispatch = getDispatch();

            Source request = createRequest(action, arg);

            Source result = myDispatch.invoke(request);

            fail("Should have caught an SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            // For some platforms, like iSeries, HPUX, 
            // StringIndexOutOfBoundsException.getMessage() may not be null, 
            // in which cases platform specific message would be used for 
            // SOAP FaultString
            if (isServerWindows) {
                assertEquals("java.lang.StringIndexOutOfBoundsException", e.getFault().getFaultString());
            }

            System.err.println("====== SUCCESS: testUncheckedExceptionUseDispatch() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testUncheckedExceptionUseDispatch()");
        }
    }


    /**
     * @testStrategy This test triggers a WebServiceException thrown by
     *               service implementation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a WebServiceException thrown by service implementation.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWebServiceExceptionUseDispatch() {

        System.err.println("====== In testWebServiceExceptionUseDispatch() ======");

        String action = "WebServiceException";
        String arg = "abc";

        try {
            Dispatch<Source> myDispatch = getDispatch();

            Source request = createRequest(action, arg);

            Source result = myDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Server", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Client requested WebServiceException", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testWebServiceExceptionUseDispatch() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testWebServiceExceptionUseDispatch()");
        }
    }


    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12OperationExceptionUseProxy() {

        System.err.println("====== In testSOAP12OperationExceptionUseProxy() ======");

        String action = "fault";
        String arg = "abc";

        try {
            System.err.println( "wsdlLocationSOAP12=" + wsdlLocationSOAP12 );
            System.err.println( "wsdlURLSOAP12=" + wsdlURLSOAP12.toString());
            WSStringServiceSOAP12 myService = new WSStringServiceSOAP12(wsdlURLSOAP12, soap12ServiceQName);

            WSStringServiceSOAP12PortType myProxy = myService.getWSStringServiceSOAP12Port();

            String ret = myProxy.doStringOperationSOAP12(testString, action, arg);

            fail("Should have caught an OperationException_Exception");

        } catch (jaxws.exceptions.wsfvt.client.soap12.OperationException_Exception e) {

            assertEquals("Unsupported operation (SOAP12)", e.getMessage());

            assertEquals("Unsupported operation (SOAP12)", e.getFaultInfo().getMessage());

            assertEquals(action + " is not a supported String operation", e.getFaultInfo().getDetail());

            assertEquals(UNSUPPORTED_OPERATION, e.getFaultInfo().getCode());

            System.err.println("====== SUCCESS: testSOAP12OperationExceptionUseProxy() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testSOAP12OperationExceptionUseProxy()");
        }

    }


    /**
     * @testStrategy This test triggers a service specific exception and 
     *               verifies exception name is customized and fault bean 
     *               is wrapped correctly
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a service specific exception and  verifies exception name is customized and fault bean is wrapped correctly",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12CustomizedServiceExceptionUseProxy() {

        System.err.println("====== In testSOAP12CustomizedServiceExceptionUseProxy() ======");

        String action = "charAt";
        String arg = "3a";

        try {
            System.err.println( "wsdlLocationSOAP12=" + wsdlLocationSOAP12 );
            System.err.println( "wsdlURLSOAP12=" + wsdlURLSOAP12.toString());
            WSStringServiceSOAP12 myService = new WSStringServiceSOAP12(wsdlURLSOAP12, soap12ServiceQName);

            WSStringServiceSOAP12PortType myProxy = myService.getWSStringServiceSOAP12Port();

            String ret = myProxy.doStringOperationSOAP12(testString, action, arg);

            fail("Should have caught a TestServiceException");

        } catch (jaxws.exceptions.wsfvt.client.soap12.TestServiceException e) {

            assertEquals("Runtime exception caught (SOAP12)", e.getMessage());

            assertEquals("Runtime exception caught (SOAP12)", e.getFaultInfo().getMessage());

            assertEquals("Integer.parseInt() throws NumberFormatException: " + arg, e.getFaultInfo().getDetail());

            System.err.println("====== SUCCESS: testSOAP12CustomizedServiceExceptionUseProxy() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testSOAP12CustomizedServiceExceptionUseProxy()");
        }
    }


    /**
     * @testStrategy This test triggers a runtime IndexOutOfBoundsException
     *               and verifies the exception is handled correctly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a runtime IndexOutOfBoundsException and verifies the exception is handled correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12UncheckedExceptionUseProxy() {

        System.err.println("====== In testSOAP12UncheckedExceptionUseProxy() ======");

        String action = "charAt";
        String arg = "100";

        try {
            System.err.println( "wsdlLocationSOAP12=" + wsdlLocationSOAP12 );
            System.err.println( "wsdlURLSOAP12=" + wsdlURLSOAP12.toString());
            WSStringServiceSOAP12 myService = new WSStringServiceSOAP12(wsdlURLSOAP12, soap12ServiceQName);

            WSStringServiceSOAP12PortType myProxy = myService.getWSStringServiceSOAP12Port();

            String ret = myProxy.doStringOperationSOAP12(testString, action, arg);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Receiver", e.getFault().getFaultCodeAsQName().getLocalPart());

            // For some platforms, like iSeries, HPUX, 
            // StringIndexOutOfBoundsException.getMessage() may not be null, 
            // in which cases platform specific message would be used for 
            // SOAP FaultString
            if (isServerWindows) {
                assertEquals("java.lang.StringIndexOutOfBoundsException", e.getFault().getFaultString());
            }

            System.err.println("====== SUCCESS: testSOAP12UncheckedExceptionUseProxy() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testSOAP12UncheckedExceptionUseProxy()");
        }
    }


    /**
     * @testStrategy This test triggers a WebServiceException thrown by
     *               service implementation and client catches 
     *               SOAPFaultException
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers a WebServiceException thrown by service implementation and client catches SOAPFaultException",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12WebServiceExceptionUseProxy() {

        System.err.println("====== In testSOAP12WebServiceExceptionUseProxy() ======");

        String action = "WebServiceException";
        String arg = "abc";

        try {
            System.err.println( "wsdlLocationSOAP12=" + wsdlLocationSOAP12 );
            System.err.println( "wsdlURLSOAP12=" + wsdlURLSOAP12.toString());
            WSStringServiceSOAP12 myService = new WSStringServiceSOAP12(wsdlURLSOAP12, soap12ServiceQName);

            WSStringServiceSOAP12PortType myProxy = myService.getWSStringServiceSOAP12Port();

            String ret = myProxy.doStringOperationSOAP12(testString, action, arg);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Receiver", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Client requested WebServiceException (SOAP12)", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testSOAP12WebServiceExceptionUseProxy() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testSOAP12WebServiceExceptionUseProxy()");
        }
    }


    /**
     * @testStrategy This testSOAP12 sends a request to SEI based endpoint and 
     *               triggers a service specific exception.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This testSOAP12 sends a request to SEI based endpoint and  triggers a service specific exception.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12OperationExceptionUseDispatch() {

        System.err.println("====== In testSOAP12OperationExceptionUseDispatch() ======");

        String action = "fault";
        String arg = "abc";

        try {
            Dispatch<Source> myDispatch = getSOAP12Dispatch();

            Source request = createSOAP12Request(action, arg);

            Source result = myDispatch.invoke(request);

            fail("Should have caught an SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Receiver", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Unsupported operation (SOAP12)", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testSOAP12OperationExceptionUseDispatch() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testSOAP12OperationExceptionUseDispatch()");
        }

    }


    /**
     * @testStrategy This testSOAP12 triggers a service specific exception use 
     *               Dispatch<Source>
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This testSOAP12 triggers a service specific exception use  Dispatch<Source>",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12CustomizedServiceExceptionUseDispatch() {

        System.err.println("====== In testSOAP12CustomizedServiceExceptionUseDispatch() ======");
        String action = "charAt";
        String arg = "3a";

        try {
            Dispatch<Source> myDispatch = getSOAP12Dispatch();

            Source request = createSOAP12Request(action, arg);

            Source result = myDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Receiver", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Runtime exception caught (SOAP12)", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testSOAP12CustomizedServiceExceptionUseDispatch() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testSOAP12CustomizedServiceExceptionUseDispatch(()");
        }
    }


    /**
     * @testStrategy This testSOAP12 triggers a runtime IndexOutOfBoundsException
     *               and verifies the exception is handled correctly.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This testSOAP12 triggers a runtime IndexOutOfBoundsException and verifies the exception is handled correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12UncheckedExceptionUseDispatch() {

        System.err.println("====== In testSOAP12UncheckedExceptionUseDispatch() ======");

        String action = "charAt";
        String arg = "100";

        try {
            Dispatch<Source> myDispatch = getSOAP12Dispatch();

            Source request = createSOAP12Request(action, arg);

            Source result = myDispatch.invoke(request);

            fail("Should have caught an SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Receiver", e.getFault().getFaultCodeAsQName().getLocalPart());

            // For some platforms, like iSeries, HPUX, 
            // StringIndexOutOfBoundsException.getMessage() may not be null, 
            // in which cases platform specific message would be used for 
            // SOAP FaultString
            if (isServerWindows) {
                assertEquals("java.lang.StringIndexOutOfBoundsException", e.getFault().getFaultString());
            }

            System.err.println("====== SUCCESS: testSOAP12UncheckedExceptionUseDispatch() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testSOAP12UncheckedExceptionUseDispatch()");
        }
    }


    /**
     * @testStrategy This testSOAP12 triggers a WebServiceException thrown by
     *               service implementation.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This testSOAP12 triggers a WebServiceException thrown by service implementation.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAP12WebServiceExceptionUseDispatch() {

        System.err.println("====== In testSOAP12WebServiceExceptionUseDispatch() ======");

        String action = "WebServiceException";
        String arg = "abc";

        try {
            Dispatch<Source> myDispatch = getSOAP12Dispatch();

            Source request = createSOAP12Request(action, arg);

            Source result = myDispatch.invoke(request);

            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {

            assertEquals("Receiver", e.getFault().getFaultCodeAsQName().getLocalPart());

            assertEquals("Client requested WebServiceException (SOAP12)", e.getFault().getFaultString());

            System.err.println("====== SUCCESS: testSOAP12WebServiceExceptionUseDispatch() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in testSOAP12WebServiceExceptionUseDispatch()");
        }
    }


    private Dispatch<Source> getDispatch() {
 
        Dispatch<Source> thisDispatch = null;

        try {
            Service thisService = Service.create(serviceQName);
            thisService.addPort(portQName, null, epAddress);

            thisDispatch = thisService.createDispatch(portQName, Source.class, Service.Mode.PAYLOAD);

        } catch (WebServiceException e) {
            e.printStackTrace();
            fail("Caught WebServiceException in getDispatch()");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in getDispatch()");
        }

        return thisDispatch;
    }


    private Source createRequest(String action, String arg) {
        
        String operation = "doStringOperation";
        String arg0String = "<arg0>" + testString + "</arg0>";;
        String arg1String = null;
        String arg2String = null;

        if (action != null) {
            arg1String = "<arg1>" + action + "</arg1>"; 
        } else {
            return null;
        }

        if (arg != null) {
            arg2String = "<arg2>" + arg + "</arg2>";
        } else {
            return null;
        }
            
        String reqString = "<" + PREFIX + ":" + operation +
                    " xmlns:" + PREFIX +
                    "=\"" + NAMESPACE + "\">" +
                    arg0String + arg1String + arg2String +
                    "</" + PREFIX + ":" + operation + ">";

        Source request = new StreamSource(new StringReader(reqString));

        return request;
    }
 

    private Dispatch<Source> getSOAP12Dispatch() {
 
        Dispatch<Source> thisDispatch = null;

        try {
            Service thisService = Service.create(soap12ServiceQName);
            thisService.addPort(soap12PortQName, SOAPBinding.SOAP12HTTP_BINDING, soap12EpAddress);

            thisDispatch = thisService.createDispatch(soap12PortQName, Source.class, Service.Mode.PAYLOAD);

        } catch (WebServiceException e) {
            e.printStackTrace();
            fail("Caught WebServiceException in getSOAP12Dispatch()");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Caught unexpected " + e.getClass().getName() + " exception in getSOAP12Dispatch()");
        }

        return thisDispatch;
    }


    private Source createSOAP12Request(String action, String arg) {
        
        String operation = "doStringOperationSOAP12";
        String arg0String = "<arg0>" + testString + "</arg0>";;
        String arg1String = null;
        String arg2String = null;

        if (action != null) {
            arg1String = "<arg1>" + action + "</arg1>"; 
        } else {
            return null;
        }

        if (arg != null) {
            arg2String = "<arg2>" + arg + "</arg2>";
        } else {
            return null;
        }
            
        String reqString = "<" + PREFIX + ":" + operation +
                    " xmlns:" + PREFIX +
                    "=\"" + NAMESPACE + "\">" +
                    arg0String + arg1String + arg2String +
                    "</" + PREFIX + ":" + operation + ">";

        Source request = new StreamSource(new StringReader(reqString));

        return request;
    }
}
