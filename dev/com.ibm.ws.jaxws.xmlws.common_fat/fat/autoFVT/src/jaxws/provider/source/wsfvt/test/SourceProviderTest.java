//
// @(#) 1.6 autoFVT/src/jaxws/provider/source/wsfvt/test/SourceProviderTest.java, WAS.websvcs.fvt, WASX.FVT 4/24/07 09:47:24 [7/11/07 13:16:02]
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
// 07/31/06 sedov       LIDB3296.42     New File
// 08/31/06 sedov       LIDB3296-42.03  Beta drop
// 01/11/07 sedov       414452.1        Updated return empty test to not catch WSE
// 04/24/07 sedov       433838          Catch null responses
//
package jaxws.provider.source.wsfvt.test;

import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import jaxws.provider.common.Constants;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for JAX-WS Provider-Source to be able to receive and process different
 * types of Source Requests
 * 
 * Test requests are sent to the server as twoWay messages, with test_message
 * string replaced with a request name as #request#
 */
public class SourceProviderTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public SourceProviderTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SourceProviderTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}
	
	
	/**
	 * @testStrategy Test for sending/receivined messages from SOAP11/PAYLOAD
	 *               JAXBSource Provider
	 * @wsdl Provider_SOAP11JAXBSource.wsdl
	 * @target SOAP11JAXBSourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_SOAP11_JAXBSource() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.JAXBSOURCE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#"));

		try {
			Source reply = dispatch.invoke(req);
			String resp = Constants.toString(reply);
			
			assertNotNull("Response is null", resp);
			assertTrue("Response message does not contain a valid reply", resp
					.indexOf("twoWayResponse") != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WSE: " + wse);
		}
	}

	/**
	 * @testStrategy Test for sending/receivined messages from SOAP11/PAYLOAD
	 *               SAXSource Provider
	 * @wsdl Provider_SOAP11SAXSource.wsdl
	 * @target SOAP11SAXSourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_SOAP11_SAXSource() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SAXSOURCE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#"));

		try {
			Source reply = dispatch.invoke(req);
			String resp = Constants.toString(reply);
			
			assertNotNull("Response is null", resp);
			assertTrue("Response message does not contain a valid reply", resp
					.indexOf("twoWayResponse") != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WSE: " + wse);
		}
	}

	/**
	 * @testStrategy Test for sending/receivined messages from SOAP11/PAYLOAD
	 *               DOMSource Provider
	 * @wsdl Provider_SOAP11DOMSource.wsdl
	 * @target SOAP11DOMSourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_SOAP11_DOMSource() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.DOMSOURCE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#"));

		try {
			Source reply = dispatch.invoke(req);
			String resp = Constants.toString(reply);
			
			assertNotNull("Response is null", resp);
			assertTrue("Response message does not contain a valid reply", resp
					.indexOf("twoWayResponse") != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WSE: " + wse);
		}
	}

	/**
	 * @testStrategy Test for sending/receivined messages from SOAP11/PAYLOAD
	 *               DOMSource Provider
	 * @wsdl Provider_SOAP11DOMSource.wsdl
	 * @target SOAP11DOMSourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_SOAP11_StreamSource() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.STREAMSOURCE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#"));

		try {
			Source reply = dispatch.invoke(req);
			String resp = Constants.toString(reply);
			
			assertNotNull("Response is null", resp);
			assertTrue("Response message does not contain a valid reply", resp
					.indexOf("twoWayResponse") != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WSE: " + wse);
		}
	}	

	/**
	 * @testStrategy Test for sending/receivined messages from SOAP11/MESSAGE
	 *               Source Provider
	 * @wsdl Provider_SOAP11SourceMessage.wsdl
	 * @target SOAP11SourceMessagePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_SOAP11Message_Source() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOURCE_SOAP11MESSAGE_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#"));

		try {
			Source reply = dispatch.invoke(req);
			String resp = Constants.toString(reply);
			
			assertNotNull("Response is null", resp);
			assertTrue("Response message does not contain a valid reply", resp
					.indexOf("twoWayResponse") != -1);
			
			assertTrue("Message PAYLOAD received when expecting MESSAGE", resp
					.indexOf("#twoWay#") != -1);			
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WSE: " + wse);
		}
	}	
	
	/**
	 * @testStrategy Test for sending/receivined messages from SOAP12/PAYLOAD
	 *               Source Provider
	 * @wsdl Provider_SOAP12Source.wsdl
	 * @target SOAP12SourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_SOAP12_StreamSource() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP12HTTP_BINDING,
				Constants.SOURCE_SOAP12_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#"));

		try {
			Source reply = dispatch.invoke(req);
			String resp = Constants.toString(reply);
			
			assertNotNull("Response is null", resp);
			assertTrue("Response message does not contain a valid reply", resp
					.indexOf("twoWayResponse") != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WSE: " + wse);
		}
	}

	/**
	 * @testStrategy Test for sending/receivined messages from SOAP12/PAYLOAD
	 *               Source Provider
	 * @wsdl Provider_SOAP11Source.wsdl
	 * @target SOAP11SourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_SOAP12Message_Source() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP12HTTP_BINDING,
				Constants.SOURCE_SOAP12MESSAGE_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#"));

		try {
			Source reply = dispatch.invoke(req);

			String resp = Constants.toString(reply);
			
			assertNotNull("Response is null", resp);
			assertTrue("Response message does not contain a valid reply", resp
					.indexOf("twoWayResponse") != -1);
			
			assertTrue("Message PAYLOAD received when expecting MESSAGE", resp
					.indexOf("#twoWay#") != -1);			
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WSE: " + wse);
		}
	}	
	
	/**
	 * @testStrategy Test for sending/receivined messages from HTTP Source
	 *               Provider
	 * @wsdl Provider_HTTPSource.wsdl
	 * @target HTTPSourcePortImpl
	 */
	/*public void testSource_XMLHTTP() {
		Dispatch<Source> dispatch = getDispatch(HTTPBinding.HTTP_BINDING,
				Constants.SOURCE_HTTP_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#"));

		try {
			Source reply = dispatch.invoke(req);
			String resp = Constants.toString(reply);
			assertTrue("Response message does not contain a valid reply", resp
					.indexOf("twoWayResponse") != -1);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);	
			fail("Unexpected WSE: " + wse);
		}
	}//*/

	/**
	 * @testStrategy Test for endpoint returning an empty envelope (this is like
	 *               returning a null object in JAX-B mode)
	 * @wsdl Provider_SOAP11Source.wsdl
	 * @target SOAP11SourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_returnEmpty() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOURCE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#returnEmpty#"));

		Source reply = dispatch.invoke(req);

		//assertTrue("Response message does not contain a valid reply",
		//			reply == null);
	}

	/**
	 * @testStrategy Test for endpoint returning a null (this is how oneWay is
	 *               implemeneted on the endpoint)
	 * @wsdl Provider_SOAP11Source.wsdl
	 * @target SOAP11SourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_returnNull() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOURCE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#oneWay#"));

		try {
			dispatch.invokeOneWay(req);
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
			fail("Unexpected WSE: " + wse);
		}
	}

	/**
	 * @testStrategy Test for endpoint throwing an unexpected exception (a
	 *               division by zero is performed)
	 * @wsdl Provider_SOAP11Source.wsdl
	 * @target SOAP11SourcePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSource_throwFault() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOURCE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG.replace(
				"test_message", "#unexpectedFailure#"));

		try {
			dispatch.invoke(req);
			fail("WebServiceException expected when Provider throws DivByZeroException");
		} catch (WebServiceException wse) {
			wse.printStackTrace(System.out);
		}
	}

	/**
	 * @testStrategy Endpoint constructs a SOAPFault with a wsdl:fault detail
	 * @wsdl Provider_SOAP11Source.wsdl
	 * @target SOAP11SourcePortImplion
	 */
	/*public void testSource_sendWSDLFault() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOURCE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.TWOWAY_MSG_EXCEPTION.replace(
				"test_message", "#twoWayException#"));

		try {
			dispatch.invoke(req);
			fail("SOAPFaultException expected when Provider throws WSDLFault");
		} catch (SOAPFaultException sf) {
			sf.printStackTrace(System.out);
		}
		//
	}//*/

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(String binding, String endpoint) {
		System.out.println("getDispatch (" + binding + "," + endpoint + ")");
		
		Service service = Service.create(Constants.SERVICE_QNAME);
		service.addPort(Constants.PORT_QNAME, binding, endpoint);
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				Service.Mode.PAYLOAD);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}
}
