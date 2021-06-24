//
// @(#) 1.1.1.4 autoFVT/src/jaxws/dispatch/wsfvt/test/NegativeReceiveTest.java, WAS.websvcs.fvt, WASX.FVT 2/14/07 11:42:55 [7/11/07 13:15:18]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 05/31/06 sedov    LIDB3296.42        New File
// 08/23/06 sedov    LIDB3296-42.02     Beta Drop
// 02/14/07 sedov    420835             Updated nonXmlPayload test
//

package jaxws.dispatch.wsfvt.test;

import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.dispatch.wsfvt.common.Constants;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test for receiving invalid data combinations from the endpoint
 */
public class NegativeReceiveTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public NegativeReceiveTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(NegativeReceiveTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}
	
	/**
	 * @testStrategy Request for the servlet to send back a wsdl:fault with
	 *               twoWayExceptionFault detail
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_WSFault() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendFault#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when a Fault is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back a SOAP fault without
	 *               any faultactor/faultstring/detail/etc
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
	/*public void testReceive_EmptyWSFault() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendEmptyFault#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when an empty SOAP Fault envelope is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}//*/

	/**
	 * @testStrategy Request for the servlet to send back a SOAP fault that is
	 *               not WS-I compliant. detail contains a string, while the
	 *               exception is outside of the detail
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_NonWSIFault() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendNonWSIFault#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));
			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when a non WS-I compliant SOAP Fault is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back a SOAP fault that is
	 *               not inside of a SOAP envelope (just the raw payload)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_NonEnvelopedFault() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendFaultNoEnvelope#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));
			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when a Fault not wrapped in a SOAP envelope is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back an empty message (HTTP
	 *               200 without a SOAP envelope or a payload)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_EmptyMessage() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendEmptyMessage#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when an empty message is received from Endpoint");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back a non-XML message
	 *               (HTTP 200 with some plain text)
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_NonXMLMessage() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendNonXMLMessage#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when a non XML message is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back a SOAP envelope with
	 *               an incorrect namespace. Checking for envelope version
	 *               checking
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_IncorrectSOAPNSMessage() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendWrongSOAPNSMessage#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when a SOAP envelope with wrong Namespace is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back a SOAP envelope
	 *               without a namespace. Checking for envelope version checking
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_NoSOAPNSMessage() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendNonNSQualifiedMessage#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when a SOAP envelope with wrong Namespace is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back a valid SOAP envelope
	 *               without a payload
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_EmptyEnvelope() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendEmptyEnvelope#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			assertNull(
					"Response Object should be null when an empty SOAP Envelope is received",
					response);
			// fail("WebServiceException should be thrown when an empty SOAP
			// envelope is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back a non WS-I compliant
	 *               message. Payload is found outside of Body
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
	/*public void testReceive_NonWSIEnvelope() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendNonWSIEnvelope#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when a SOAP Envelope is received with Payload outside of soap:Body");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}//*/

	/**
	 * @testStrategy Request for the servlet to send back an invalid XML
	 *               payload, a valid tag is missing a correcponding close tag
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
	/*public void testReceive_InvalidXMLPayload() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendInvalidXMLPayload#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when an empty SOAP envelope is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}//*/

	/**
	 * @testStrategy Request for the servlet to send back a payload that
	 *               consists of plain text
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testReceive_NonXMLPayload() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendNonXMLPayload#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			assertNull("Either a WSE or a null value is expected when non XML Payload is received", response);
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Request for the servlet to send back a payload that
	 *               is not namespace qualified
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
	/*public void testReceive_UnqualifiedXMLPayload() throws Exception {
		javax.xml.ws.Dispatch<Source> dispatch = getDispatch(
				Service.Mode.PAYLOAD, Constants.SIMPLE_ENDPOINT_ADDRESS);
		try {
			String request = Constants.TWOWAY_MSG.replace("test_message",
					"#sendNonNSQualifiedPayload#");
			Source response = dispatch.invoke(Constants.toStreamSource(request));

			// log the received message
			System.out.println(getName() + ": " + Constants.toString(response));

			fail("WebServiceException should be thrown when Unqualified XML Payload is received");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}//*/

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint) {
		Service service = Service.create(Constants.SERVICE_QNAME);
		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				endpoint);
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				mode);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}
}
