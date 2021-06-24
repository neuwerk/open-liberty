//
// @(#) 1.3.1.5 autoFVT/src/jaxws/dispatch/wsfvt/test/SAXSourceTest.java, WAS.websvcs.fvt, WASX.FVT 1/22/07 11:46:26 [7/11/07 13:15:19]
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
// 01/20/07 sedov    415799             Added FAULT_TEXT constant
//

package jaxws.dispatch.wsfvt.test;

import java.io.StringReader;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import jaxws.dispatch.wsfvt.common.CallbackHandler;
import jaxws.dispatch.wsfvt.common.Constants;
import jaxws.dispatch.wsfvt.common.KillerThread;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Tests for JAX-WS Dispatch SAXSource messages can be sent in both MESSAGE and
 * PAYLOAD mode. When SAXSource is used, the returnned Source must be an
 * instance of SAXSource as well
 */
public class SAXSourceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static final String MSG_TYPE_MISMATCH = "When using SAXSource objects, returned types must be SAXSource objects.";

	// monitor used for interrupting hung async invocations
	private KillerThread killer = null;

	public SAXSourceTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SAXSourceTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Test invokeOneWay messages over SAXSource/MESSAGE mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invokeOneWay_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.ONEWAY_MSG.replace("test_message", getName());
		Source message = Constants.toSAXSource(Constants.SOAP11_HEADER + msg
				+ Constants.SOAP11_TRAILER);

		Constants.logRequest(msg);
		dispatch.invokeOneWay(message);
	}

	/**
	 * @testStrategy Test invokeOneWay messages over SAXSource/PAYLOAD mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invokeOneWay_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.ONEWAY_MSG.replace("test_message", getName());
		Source payload = Constants.toSAXSource(msg);

		// invoke
		Constants.logRequest(msg);
		dispatch.invokeOneWay(payload);
	}

	/**
	 * @testStrategy Test invoke messages over SAXSource/MESSAGE mode. Returned
	 *               type must also be a SAXSource
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invoke_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		Source message = Constants.toSAXSource(msg);

		// invoke
		Constants.logRequest(msg);
		Source res = dispatch.invoke(message);

		String resp =  Constants.toString(res);
		Constants.testEnvelope(resp, true);		
		
		// validate response
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", resp.indexOf(
				getName()) != -1);

		// if type checking is enabled, ensure that the response is
		// of the same type as SAXSource
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, res instanceof SAXSource);
		}
	}

	/**
	 * @testStrategy Test invoke messages over SAXSource/PAYLOAD mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invoke_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source payload = Constants.toSAXSource(msg);

		// invoke
		Constants.logRequest(msg);
		Source res = dispatch.invoke(payload);

		String resp =  Constants.toString(res);
		Constants.testEnvelope(resp, false);
		
		
		// validate the response
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", resp.indexOf(
				getName()) != -1);

		// if type checking is enabled, ensure that the response is
		// of the same type as SAXSource
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, res instanceof SAXSource);
		}
	}

	/**
	 * @testStrategy Test invokeAsync (polling) messages over SAXSource/MESSAGE
	 *               mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invokeAsycPoll_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		Source message = Constants.toSAXSource(msg);

		// invoke and block until response arrives
		Constants.logRequest(msg);
		Response<Source> response = dispatch.invokeAsync(message);
		Source res = response.get();

		// validate response
		String resp =  Constants.toString(res);
		Constants.testEnvelope(resp, true);
		
		
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response", resp.indexOf(
				getName()) != -1);

		// if type checking is enabled, ensure that the response is
		// of the same type as SAXSource
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, res instanceof SAXSource);
		}
	}

	/**
	 * @testStrategy Test invokeAsync (polling) messages over SAXSource/PAYLOAD
	 *               mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invokeAsycPoll_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source payload = Constants.toSAXSource(msg);

		// blocks until response arrives
		Constants.logRequest(msg);
		Response<Source> response = dispatch.invokeAsync(payload);
		Source res = response.get();

		// validate response
		String resp =  Constants.toString(res);
		Constants.testEnvelope(resp, false);
		
		assertTrue("Response is null", res != null);
		assertTrue("Unexpected Response",resp.indexOf(
				getName()) != -1);

		// if type checking is enabled, ensure that the response is
		// of the same type as SAXSource
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {
			assertTrue(MSG_TYPE_MISMATCH, res instanceof SAXSource);
		}
	}

	/**
	 * @testStrategy Test invokeAsync (callback) messages over SAXSource/MESSAGE
	 *               mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invokeAsycCallback_Message() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.MESSAGE,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<Source> handler = new CallbackHandler<Source>();

		String msg = Constants.SOAP11_HEADER
				+ Constants.TWOWAY_MSG.replace("test_message", getName())
				+ Constants.SOAP11_TRAILER;
		Source message = Constants.toSAXSource(msg);

		// invoke and wait for response
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(message, handler);
		handler.waitBlocking(monitor);

		try {
			// validate response
			Source res = handler.get();

			String resp =  Constants.toString(res);
			Constants.testEnvelope(resp, true);			
			
			assertTrue("Response is null", res != null);
			assertTrue("Unexpected Response", resp.indexOf(
					getName()) != -1);

			// if type checking is enabled, ensure that the response is
			// of the same type as SAXSource
			if (Constants.ENABLE_RETURNTYPE_CHECKING) {
				assertTrue(MSG_TYPE_MISMATCH, res instanceof SAXSource);
			}
		} catch (WebServiceException e) {
			e.printStackTrace(System.out);
			fail("Unexpected: " + e);
		}
	}

	/**
	 * @testStrategy Test invokeAsync (callback) messages over SAXSource/PAYLOAD
	 *               mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target SOAP11DispatchPortImpl
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invokeAsycCallback_Payload() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		CallbackHandler<Source> handler = new CallbackHandler<Source>();

		// compose message
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		Source payload = Constants.toSAXSource(msg);

		// invoke and wait for response
		Constants.logRequest(msg);
		Future<?> monitor = dispatch.invokeAsync(payload, handler);
		handler.waitBlocking(monitor);

		try {
			// validate response
			Source res = handler.get();

			String resp =  Constants.toString(res);
			Constants.testEnvelope(resp, false);
			
			assertTrue("Response is null", res != null);
			assertTrue("Unexpected Response", resp.indexOf(
					getName()) != -1);

			// if type checking is enabled, ensure that the response is
			// of the same type as SAXSource
			if (Constants.ENABLE_RETURNTYPE_CHECKING) {
				assertTrue(MSG_TYPE_MISMATCH, res instanceof SAXSource);
			}
		} catch (WebServiceException e) {
			e.printStackTrace(System.out);
			fail("Unexpected: " + e);
		}
	}

	/**
	 * @testStrategy Test sending 1MB message over SAXSource. A string array is
	 *               dynamically built up until payload size reaches 1MB.
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invoke_largeMessage() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		// build a large message
		StringBuffer message = new StringBuffer();
		message.append("<ns1:twoWay xmlns:ns1=\"" + Constants.WSDL_NAMESPACE
				+ "\">");
		for (int i = 0; i < 30000; i++) {
			message.append("<ns1:value>test_message_" + i + "</ns1:value>");
		}
		message.append("</ns1:twoWay>");

		// invoek and see what happens
		dispatch.invoke(Constants.toSAXSource(message.toString()));
	}

	/**
	 * @testStrategy Request the endpoint to throw an exception, make sure it
	 *               can be received in SAXSource mode
	 * 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_invoke_endpointException() throws Exception {
		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		String message = Constants.TWOWAY_MSG_EXCEPTION.replace("test_message",
				"#service-specific#");

		try {
			dispatch.invoke(Constants.toSAXSource(message));
			fail("WebServiceException expected when endpoint returns a soap:Fault.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);

			assertTrue("WSE should contain the exception text.", wse
					.getMessage() != null
					&& wse.getMessage().indexOf(Constants.FAULT_TEXT) != -1);

			assertTrue("WSE should be an instance of SOAPFaultException",
					wse instanceof SOAPFaultException);
		}
	}

	/**
	 * @testStrategy Test for creating a dispatch explicitly typed with
	 *               SAXSource
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_ExplicitlyTyped() throws Exception {
		if (Constants.ENABLE_RETURNTYPE_CHECKING) {

			// create a dispatch explicitly typed with DOMSource
			Service service = Service.create(Constants.SERVICE_QNAME);
			service.addPort(Constants.PORT_QNAME,
					SOAPBinding.SOAP11HTTP_BINDING,
					Constants.SOAP11_ENDPOINT_ADDRESS);

			Dispatch<SAXSource> dispatch = null;
			dispatch = service.createDispatch(Constants.PORT_QNAME,
					SAXSource.class, Service.Mode.PAYLOAD);

			assertNotNull("Dispatch not null", dispatch);

			// only invoke if type checking is enabled
			// invoke
			String msg = Constants.TWOWAY_MSG
					.replace("test_message", getName());
			SAXSource response = dispatch.invoke(Constants.toSAXSource(msg));

			// validate response
			String res = Constants.toString(response);
			assertTrue("Invalid Message Returned", res.indexOf(getName()) != -1);
		}
	}

	/**
	 * @testStrategy Create a SAX Source by providing an XMLReader via
	 *               setXMLReader
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Create a SAX Source by providing an XMLReader via setXMLReader",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_XMLReader() throws Exception {
		XMLReader xmlr = XMLReaderFactory.createXMLReader();

		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		InputSource is = new InputSource(new StringReader(msg));
		xmlr.parse(is);

		SAXSource ss = new SAXSource();
		ss.setXMLReader(xmlr);

		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			dispatch.invoke(ss);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
		}

	}

	/**
	 * @testStrategy Create a SAX Source by providing an InputSource via
	 *               setInputSource
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Create a SAX Source by providing an InputSource via setInputSource",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_InputSource() throws Exception {
		SAXSource ss = new SAXSource();
		String msg = Constants.TWOWAY_MSG.replace("test_message", getName());
		InputSource is = new InputSource(new StringReader(msg));
		ss.setInputSource(is);

		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		Source reply = dispatch.invoke(ss);

		// print a logging statement about what has been returned
		if (reply instanceof SAXSource) {
			System.out.println(getName() + " SAXSource.getXMLReader="
					+ ((SAXSource) reply).getXMLReader());
			System.out.println(getName() + " SAXSource.getInputSource="
					+ ((SAXSource) reply).getInputSource());
		} else {
			System.out.println(getName() + " Returned a "
					+ reply.getClass().getName());
		}
	}

	/**
	 * @testStrategy Create a SAX Source by using the default constructor and
	 *               populating anything in
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Create a SAX Source by using the default constructor and populating anything in",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSAXSource_EmptySource() throws Exception {

		Dispatch<Source> dispatch = getDispatch(Service.Mode.PAYLOAD,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			dispatch.invoke(new SAXSource());
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
		}
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint) {
		// stop the killer thread if it is active
		if (killer != null) killer.abort();
		killer = null;

		Service service = Service.create(Constants.SERVICE_QNAME);
		service.setExecutor(Executors.newSingleThreadExecutor());

		// create a killer monitor thread that will make
		// sure to kill the executor after a while
		killer = new KillerThread(service, Constants.CLIENT_MAX_SLEEP_SEC);
		killer.start();

		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				endpoint);
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				mode);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}

}
