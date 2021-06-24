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
// 09/18/2006  mzheng         LIDB3296-46.02    New File
// 02/01/2007  mzheng         418257            Fixed test cases
// 03/08/2007  mzheng         425326            Enable testServerCodeVersionMismatch()
// 03/22/2007  mzheng         428129            Not sent mustUnderstand="true"
//

package jaxws.soapfaults.wsfvt.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.StringReader;

import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.WebServiceException;

import jaxws.soapfaults.wsfvt.client.*;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * This scenario verifies:
 *
 * - Application exception is mapped to SOAP 1.2 fault message and 
 *   fault bean is serialized as SOAP fault detail
 *
 * - SOAP fault detail is unmarshaled correctly
 *
 * - Application can throw SOAPFaultException using SAAJ 1.3 APIs
 *
 * - SOAP Fault code and subcode namespace and prefix, and fault reason 
 *   text are correctly handled by JAX-WS
 */

public class SOAP12FaultsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    private static final String testNS = "http://soapfaults.jaxws/xsd";

    private static final String testPrefix = "soap-faults-test";

    private final static String DEFAULT = "default";

    /*
     * A constructor to create a test case with a given name.
     *
     * @param name The name of the test case to be created
     */
    public SOAP12FaultsTest(String name) {
        super(name);
    }


    /*
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
        return new TestSuite(SOAP12FaultsTest.class);
    }


    /**
     * @testStrategy This test triggers the application to throw a 
     *               SOAPFaultException with detail, verifies serialized 
     *               service exception in SOAP fault detail is 
     *               unmarshaled correctly 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers the application to throw a  SOAPFaultException with detail, verifies serialized service exception in SOAP fault detail is unmarshaled correctly",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAppSOAPFaultDetail() {
        System.err.println("====== In testAppSOAPFaultDetail() ====== \n");

        DoFaultTestType req = new DoFaultTestType();
        CodeHeaderTypeSOAP12 cHeader = new CodeHeaderTypeSOAP12();
        NamespaceHeaderType nHeader = new NamespaceHeaderType();

        cHeader.setMustUnderstand(false);
       
        nHeader.setURI(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        nHeader.setPrefix(SOAPConstants.SOAP_ENV_PREFIX);

        SOAP12FaultsPortType myPort = null;
        DoFaultTestResponseType resp = null;

        try {
            myPort = (new SOAP12FaultsService()).getSOAP12FaultsPort();
            if (myPort == null) {
                fail("Failed to create proxy");
                return;
            }

            resp = myPort.doFaultTest(req, cHeader, nHeader);
            fail("Should have caught a ValidateFault");
        } catch (ValidateFault e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider)myPort).getResponseContext();
            //assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            ValidateFaultType faultInfo = e.getFaultInfo();

            assertEquals("Incorrect ValidateFault msgID", 
                         400, faultInfo.getMsgID());

            assertEquals("Incorrect ValidateFault msg",
                         "Client did not send request string",
                         faultInfo.getMsg());

            assertEquals("Incorrect ValidateFault field",
                         "doFaultTest",
                         faultInfo.getField());

            System.err.println("====== SUCCESS: testAppSOAPFaultDetail() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test verifies server serialize service exception 
     *               as SOAP fault detail and it is unmarshaled correctly 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies server serialize service exception  as SOAP fault detail and it is unmarshaled correctly",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServerSOAPFaultDetail() {
        System.err.println("====== In testServerSOAPFaultDetail() ====== \n");

        DoFaultTestType req = new DoFaultTestType();
        CodeHeaderTypeSOAP12 cHeader = new CodeHeaderTypeSOAP12();
        NamespaceHeaderType nHeader = new NamespaceHeaderType();

        req.setReqString("null"); 

        cHeader.setMustUnderstand(false);
       
        nHeader.setURI(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        nHeader.setPrefix(SOAPConstants.SOAP_ENV_PREFIX);

        SOAP12FaultsPortType myPort = null;
        DoFaultTestResponseType resp = null;

        try {
            myPort = (new SOAP12FaultsService()).getSOAP12FaultsPort();
            if (myPort == null) {
                fail("Failed to create proxy");
                return;
            }

            resp = myPort.doFaultTest(req, cHeader, nHeader);
            fail("Should have caught a ValidateFault");

        } catch (ValidateFault e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider)myPort).getResponseContext();
            //assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            ValidateFaultType faultInfo = e.getFaultInfo();

            assertEquals("Incorrect ValidateFault msgID", 
                         400, faultInfo.getMsgID());

            assertEquals("Incorrect ValidateFault msg",
                         "Client did not send request string",
                         faultInfo.getMsg());

            assertEquals("Incorrect ValidateFault field",
                         "doFaultTest",
                         faultInfo.getField());

            System.err.println("====== SUCCESS: testServerSOAPFaultDetail() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test triggers application to throw a 
     *               SOAPFaultException without detail
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers application to throw a  SOAPFaultException without detail",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAppSOAPFaultException() {
        System.err.println("====== In testAppSOAPFaultException() ======\n");

        DoFaultTestType req = new DoFaultTestType();
        CodeHeaderTypeSOAP12 cHeader = new CodeHeaderTypeSOAP12();
        NamespaceHeaderType nHeader = new NamespaceHeaderType();

        cHeader.setMustUnderstand(false);
       
        SOAP12FaultsPortType myPort = null;
        DoFaultTestResponseType resp = null;

        try {
            myPort = (new SOAP12FaultsService()).getSOAP12FaultsPort();
            if (myPort == null) {
                fail("Failed to create proxy");
                return;
            }

            resp = myPort.doFaultTest(req, cHeader, nHeader);
            fail("Should have caught a SOAPFaultException"); 

        } catch (SOAPFaultException e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider)myPort).getResponseContext();
            // assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            SOAPFault fault = e.getFault();

            try {
                assertEquals("Incorrect SOAP fault reason text",
                             "Null namespace header field",
                             fault.getFaultReasonText(Locale.US));
            } catch (SOAPException se) {
                fail("Unexpected SOAPException caught: " + se.getMessage());
            }

            assertEquals("Incorrect SOAP faultcode", 
                         "Receiver", 
                         fault.getFaultCodeAsQName().getLocalPart());

/*
            assertEquals("Incorrect SOAP faultcode namespace",
                         SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE,
                         fault.getFaultCodeAsQName().getNamespaceURI());

            assertEquals("Incorrect SOAP faultcode prefix",
                         SOAPConstants.SOAP_ENV_PREFIX,
                         fault.getFaultCodeAsQName().getPrefix());
            // SOAP 1.2 fault subcode
            QName subcodeQName = (QName) fault.getFaultSubcodes().next();
            assertEquals("Incorrect SOAP fault subcode",
                         "DataEncodingUnknown",
                         subcodeQName.getLocalPart());

            assertEquals("Incorrect SOAP fault subcode namespace",
                         SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE,
                         subcodeQName.getNamespaceURI());
*/

            System.err.println("====== SUCCESS: testAppSOAPFaultException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test triggers application to throw a 
     *               SOAPFaultException with code Client
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test triggers application to throw a  SOAPFaultException with code Client",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServerSOAPFaultException() {
        System.err.println("====== In testServerSOAPFaultException() ======\n");

        DoFaultTestType req = new DoFaultTestType();
        CodeHeaderTypeSOAP12 cHeader = new CodeHeaderTypeSOAP12();
        NamespaceHeaderType nHeader = new NamespaceHeaderType();

        cHeader.setMustUnderstand(false);
       
        nHeader.setURI("null");
        nHeader.setPrefix("null");

        SOAP12FaultsPortType myPort = null;
        DoFaultTestResponseType resp = null;

        try {
            myPort = (new SOAP12FaultsService()).getSOAP12FaultsPort();
            if (myPort == null) {
                fail("Failed to create proxy");
                return;
            }

            resp = myPort.doFaultTest(req, cHeader, nHeader);
            fail("Should have caught a SOAPFaultException"); 

        } catch (SOAPFaultException e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider)myPort).getResponseContext();
            // assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            SOAPFault fault = e.getFault();

            assertEquals("Incorrect SOAP faultstring",
                         "Unrecognized namespace header field",
                         fault.getFaultString());

/*
            try {
                assertEquals("Incorrect SOAP faultstring",
                             "Unrecognized namespace header field",
                             fault.getFaultReasonText(Locale.US));
            } catch (SOAPException se) {
                fail("Unexpected SOAPException caught: " + se.getMessage());
            }
*/

            assertEquals("Incorrect SOAP faultcode", 
                         "Sender", 
                         fault.getFaultCodeAsQName().getLocalPart());

/*
            assertEquals("Incorrect SOAP faultcode namespace", 
                         SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, 
                         fault.getFaultCodeAsQName().getNamespaceURI());

            assertEquals("Incorrect SOAP faultcode prefix", 
                         SOAPConstants.SOAP_ENV_PREFIX,
                         fault.getFaultCodeAsQName().getPrefix());

            // SOAP 1.2 fault subcode
            QName subcodeQName = (QName) fault.getFaultSubcodes().next();
            assertEquals("Incorrect SOAP fault subcode",
                         "DataEncodingUnknown",
                         subcodeQName.getLocalPart());

            assertEquals("Incorrect SOAP fault subcode namespace",
                         SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE,
                         subcodeQName.getNamespaceURI());
*/

            System.err.println("====== SUCCESS: testServerSOAPFaultException() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test verifies a service specific exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test verifies a service specific exception",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServiceException() {
        System.err.println("====== In testServiceException() ======\n");

        DoFaultTestType req = new DoFaultTestType();
        CodeHeaderTypeSOAP12 cHeader = new CodeHeaderTypeSOAP12();
        NamespaceHeaderType nHeader = new NamespaceHeaderType();

        req.setReqString("test");

        cHeader.setCode(DEFAULT);
        cHeader.setMustUnderstand(false);
       
        nHeader.setURI(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        nHeader.setPrefix(SOAPConstants.SOAP_ENV_PREFIX);

        SOAP12FaultsPortType myPort = null;
        DoFaultTestResponseType resp = null;

        try {
            myPort = (new SOAP12FaultsService()).getSOAP12FaultsPort();
            if (myPort == null) {
                fail("Failed to create proxy");
                return;
            }

            resp = myPort.doFaultTest(req, cHeader, nHeader);
            fail("Should have caught a ProcessFault");

        } catch (ProcessFault e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider)myPort).getResponseContext();
            // assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            //
            // Verify the nested ProcessFault exceprion 
            // 
            ProcessFaultType faultInfo = e.getFaultInfo();

            assertEquals("Incorrect ProcessFault msgID", 
                         500, faultInfo.getMsgID());

            assertEquals("Incorrect ProcessFault msg",
                         "Unexpected request",
                         faultInfo.getMsg());

            System.err.println("====== SUCCESS: testServiceException() ======\n");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }


    /**
     * @testStrategy This test catches the SOAPFaultException thrown by 
     *               application and verifies SOAP faultcode MustUnderstand
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches the SOAPFaultException thrown by  application and verifies SOAP faultcode MustUnderstand",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAppCodeMustUnderstand() {
        System.err.println("====== In testAppCodeMustUnderstand() ======\n");

        DoFaultTestType req = new DoFaultTestType();
        CodeHeaderTypeSOAP12 cHeader = new CodeHeaderTypeSOAP12();
        NamespaceHeaderType nHeader = new NamespaceHeaderType();
 
        req.setReqString("test");

        cHeader.setCode("mustUnderstand");
        cHeader.setMustUnderstand(false);

        nHeader.setURI(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        nHeader.setPrefix(SOAPConstants.SOAP_ENV_PREFIX);

        SOAP12FaultsPortType myPort = null;
        DoFaultTestResponseType resp = null;

        try {
            myPort = (new SOAP12FaultsService()).getSOAP12FaultsPort();
            if (myPort == null) {
                fail("Failed to create proxy");
                return;
            }

            resp = myPort.doFaultTest(req, cHeader, nHeader);
            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider)myPort).getResponseContext();
            // assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            SOAPFault fault = e.getFault();

            assertEquals("Incorrect SOAP faultcode", 
                         "MustUnderstand", 
                         fault.getFaultCodeAsQName().getLocalPart());

            assertEquals("Incorrect SOAP faultstring",
                         "Incorrect code in codeHeader",
                         fault.getFaultString());

            try {
                assertEquals("Incorrect SOAP faultstring",
                             "Incorrect code in codeHeader",
                             fault.getFaultReasonText(Locale.US));
            } catch (SOAPException se) {
                fail("Unexpected SOAPException caught: " + se.getMessage());
            }

            assertEquals("Incorrect SOAP faultcode namespace", 
                         SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, 
                         fault.getFaultCodeAsQName().getNamespaceURI());

/*
            assertEquals("Incorrect SOAP faultcode prefix", 
                         SOAPConstants.SOAP_ENV_PREFIX,
                         fault.getFaultCodeAsQName().getPrefix());
*/

            // SOAP 1.2 fault subcode
            QName subcodeQName = (QName) fault.getFaultSubcodes().next();
            assertEquals("Incorrect SOAP fault subcode",
                         "DataEncodingUnknown",
                         subcodeQName.getLocalPart());

            assertEquals("Incorrect SOAP fault subcode namespace",
                         SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE,
                         subcodeQName.getNamespaceURI());

            System.err.println("====== SUCCESS: testAppCodeMustUnderstand() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());

        }
    }


    /**
     * @testStrategy This test uses Dispatch to send a SOAP 1.1 request
     *               to SOAP 1.2 endpoint, and verifies server throws 
     *               SOAPFaultException with fault code VersionMismatch.
     */
    /** 
     * 
     * comment this test case out from Liberty Because:
     * Per jax-ws 2.2 spec,  when you send a soap11 message to a soap12 endpoint, you can either
     * 1. throw a versionMismatch exception.
     * 2. process the message and return a soap11 response.
     * In tWAS, it use the approach 1, while in Liberty CXF, it use approach 2.
    */ 
    /*
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test uses Dispatch to send a SOAP 1.1 request to SOAP 1.2 endpoint, and verifies server throws SOAPFaultException with fault code VersionMismatch.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServerCodeVersionMismatch() {
        Dispatch<SOAPMessage> myDispatch = getSOAP12DispatchMessage();

        try {
            SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
            SOAPPart part = message.getSOAPPart();
            SOAPEnvelope env = part.getEnvelope();
            SOAPHeader header = env.getHeader();
            SOAPBody body = env.getBody();

            // Build the first soap header element
            SOAPHeaderElement headerElement1 =
            header.addHeaderElement(env.createName("codeHeader", testPrefix, testNS));

            headerElement1.addChildElement("Code").addTextNode(DEFAULT);
            headerElement1.setMustUnderstand(false);

            // Build the second soap header element
            SOAPHeaderElement headerElement2 =
            header.addHeaderElement(env.createName("namespaceHeader", testPrefix, testNS));

            headerElement2.addChildElement("URI").addTextNode(testNS);
            headerElement2.addChildElement("prefix").addTextNode(testPrefix);

            // Build the soap body
            SOAPBodyElement bodyElement =
            body.addBodyElement(env.createName("doFaultTest", testPrefix, testNS));
            bodyElement.addChildElement("reqString").addTextNode("test");

            // Save the message
            message.saveChanges();

            SOAPMessage result = myDispatch.invoke(message);
        } catch (SOAPFaultException e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider) myDispatch).getResponseContext();
            // assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            SOAPFault fault = e.getFault();
            assertEquals("Incorrect SOAP fault code",
                         "VersionMismatch",
                         fault.getFaultCodeAsName().getLocalName());

        } catch (WebServiceException e) {
            e.printStackTrace();
            fail("Unexpected WebServiceException caught: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getMessage());
        }

    }
*/

    /**
     * @testStrategy This test catches a SOAPFaultException and verifies
     *               SOAP 1.2 fault Role
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches a SOAPFaultException and verifies SOAP 1.2 fault Role",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSOAPFaultRole() {
        System.err.println("====== In testSOAPFaultRole() ======\n");

        DoFaultTestType req = new DoFaultTestType();
        CodeHeaderTypeSOAP12 cHeader = new CodeHeaderTypeSOAP12();
        NamespaceHeaderType nHeader = new NamespaceHeaderType();

        req.setReqString("role");

        cHeader.setCode(DEFAULT);
        cHeader.setMustUnderstand(false);

        nHeader.setURI(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        nHeader.setPrefix(SOAPConstants.SOAP_ENV_PREFIX);

        SOAP12FaultsPortType myPort = null;
        DoFaultTestResponseType resp = null;

        try {
            myPort = (new SOAP12FaultsService()).getSOAP12FaultsPort();
            if (myPort == null) {
                fail("Failed to create proxy");
                return;
            }

            resp = myPort.doFaultTest(req, cHeader, nHeader);
            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider)myPort).getResponseContext();
            // assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            SOAPFault fault = e.getFault();

            assertEquals("Incorrect SOAP faultstring",
                         "Test SOAP 1.2 Fault Role",
                         fault.getFaultString());

            assertEquals("Incorrect SOAP faultcode",
                         "Receiver",
                         fault.getFaultCodeAsQName().getLocalPart());

            assertEquals("Incorrect SOAP fault Role",
                         SOAPConstants.URI_SOAP_1_2_ROLE_NONE,
                         fault.getFaultRole());

            System.err.println("====== SUCCESS: testSOAPFaultRole() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": "
 + e.getMessage());

        }
    }


    /**
     * @testStrategy This test catches a SOAPFaultException.  The namespace 
     *               for soap fault code is 
     *               SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, however, the 
     *               customer prefix is used.  The runtime should not pick 
     *               up a different prefix.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test catches a SOAPFaultException.  The namespace  for soap fault code is SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, however, the customer prefix is used.  The runtime should not pick up a different prefix.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAppSOAPFaultCodeNamespace() {
        System.err.println("====== In testAppSOAPFaultCodeNamespace() ======\n");

        DoFaultTestType req = new DoFaultTestType();
        CodeHeaderTypeSOAP12 cHeader = new CodeHeaderTypeSOAP12();
        NamespaceHeaderType nHeader = new NamespaceHeaderType();

        req.setReqString("role");

        cHeader.setCode(DEFAULT);
        cHeader.setMustUnderstand(false);

        nHeader.setURI(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        nHeader.setPrefix(SOAPConstants.SOAP_ENV_PREFIX + "-test");

        SOAP12FaultsPortType myPort = null;
        DoFaultTestResponseType resp = null;

        try {
            myPort = (new SOAP12FaultsService()).getSOAP12FaultsPort();
            if (myPort == null) {
                fail("Failed to create proxy");
                return;
            }

            resp = myPort.doFaultTest(req, cHeader, nHeader);
            fail("Should have caught a SOAPFaultException");

        } catch (SOAPFaultException e) {
            // Make sure we get 500 HTTP response code when an
            // exception is caught
            Map respCtx = ((BindingProvider)myPort).getResponseContext();
            // assertEquals("Incorrect HTTP response code", "500", respCtx.get(MessageContext.HTTP_RESPONSE_CODE));

            SOAPFault fault = e.getFault();

            assertEquals("Incorrect SOAP faultstring",
                         "Test SOAP 1.2 Fault Role",
                         fault.getFaultString());

            assertEquals("Incorrect SOAP faultcode", 
                         "Receiver", 
                         fault.getFaultCodeAsQName().getLocalPart());

            assertEquals("Incorrect SOAP faultcode namespace", 
                         SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, 
                         fault.getFaultCodeAsQName().getNamespaceURI());

/*
            assertEquals("Incorrect SOAP faultcode prefix", 
                         SOAPConstants.SOAP_ENV_PREFIX + "-test",
                         fault.getFaultCodeAsQName().getPrefix());
*/
            // SOAP 1.2 fault subcode
            QName subcodeQName = (QName) fault.getFaultSubcodes().next();
            assertEquals("Incorrect SOAP fault subcode",
                         "DataEncodingUnknown",
                         subcodeQName.getLocalPart());

            assertEquals("Incorrect SOAP fault subcode namespace",
                         SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE,
                         subcodeQName.getNamespaceURI());

            System.err.println("====== SUCCESS: testAppSOAPFaultCodeNamespace() ======\n");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected Exception caught: " + e.getClass().getName() + ": " + e.getMessage());

        }

    }


    private Dispatch<SOAPMessage> getSOAP12DispatchMessage() {

        IAppServer server = QueryDefaultNode.defaultAppServer;

        String epAddress = "http://" + server.getMachine().getHostname() + ":" + server.getPortMap().get(Ports.WC_defaulthost) + "/jaxws-soapfaults/SOAP12FaultsService";

        QName serviceQName = new QName(testNS, "SOAP12FaultsService");

        QName portQName = new QName(testNS, "SOAP12FaultsPort");

        Service myService = Service.create(serviceQName);
        myService.addPort(portQName, SOAPBinding.SOAP12HTTP_BINDING, epAddress);

        Dispatch<SOAPMessage> myDispatch = myService.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);

        return myDispatch;
    }
}
