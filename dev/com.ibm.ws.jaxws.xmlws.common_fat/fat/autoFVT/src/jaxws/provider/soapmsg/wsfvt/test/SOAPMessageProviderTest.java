//
// @(#) 1.4 autoFVT/src/jaxws/provider/soapmsg/wsfvt/test/SOAPMessageProviderTest.java, WAS.websvcs.fvt, WASX.FVT 12/5/06 12:10:35 [7/11/07 13:15:53]
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
// 12/05/06 sedov       408880          Refactored to new package
// 

package jaxws.provider.soapmsg.wsfvt.test;

import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.provider.common.Constants;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for JAX-WS Provider-SOAPMessage to be able to receive and process different types
 * of Requests. SOAPMessage is only valid in SOAP11/12 Message mode
 * 
 * Test requests are sent to the server as twoWay messages, with test_message
 * string replaced with a request name as #request#
 */
public class SOAPMessageProviderTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase{

	public SOAPMessageProviderTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SOAPMessageProviderTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}
	
	/**
	 * @testStrategy Test for sending/receivined messages from SOAP11/MESSAGE
	 *               SOAPMessage Provider
	 * @wsdl Provider_SOAP11SOAPMesage.wsdl
	 * @target SOAP11SOAPMessagePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_SOAP11() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOAPMESSAGE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.SOAP11_HEADER + Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#") + Constants.SOAP11_TRAILER);

		Source reply = dispatch.invoke(req);
		String resp = Constants.toString(reply);
		assertTrue("Response message does not contain a valid reply", resp.indexOf("twoWayResponse") != -1);

	}

	/**
	 * @testStrategy Test for sending/receivined messages from SOAP12/MESSAGE
	 *               SOAPMessage Provider
	 * @wsdl Provider_SOAP11SOAPMesage.wsdl
	 * @target SOAP11SOAPMessagePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_SOAP12() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP12HTTP_BINDING,
				Constants.SOAPMESSAGE_SOAP12_ADDRESS);

		String msg = Constants.SOAP12_HEADER + Constants.TWOWAY_MSG.replace(
				"test_message", "#twoWay#") + Constants.SOAP12_TRAILER;
		Source message = Constants.toStreamSource(msg);

		Source reply = dispatch.invoke(message);
		String resp = Constants.toString(reply);
		assertTrue("Response message does not contain a valid reply", resp.indexOf("twoWayResponse") != -1);
	}

	
	/**
	 * @testStrategy Test for endpoint returning an empty envelope. This is valid
	 *               for SOAPMessage as an empty envelope will come back
	 * @wsdl Provider_SOAP11SOAPMessage.wsdl
	 * @target SOAP11SOAPMessagePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_testReturnEmpty() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOAPMESSAGE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.SOAP11_HEADER + Constants.TWOWAY_MSG.replace(
				"test_message", "#returnEmpty#") + Constants.SOAP11_TRAILER);

		Source reply = dispatch.invoke(req);
		assertTrue("Response message does not contain a valid reply", reply != null);
			
	}
	
	/**
	 * @testStrategy Test for endpoint returning a null (this is how oneWay is
	 *               implemeneted on the endpoint)
	 * @wsdl Provider_SOAP11SOAPMessage.wsdl
	 * @target SOAP11SOAPMessagePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAPMessage_testReturnNull() {
		Dispatch<Source> dispatch = getDispatch(SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOAPMESSAGE_SOAP11_ADDRESS);

		Source req = Constants.toStreamSource(Constants.SOAP11_HEADER + Constants.TWOWAY_MSG.replace(
				"test_message", "#oneWay#") + Constants.SOAP11_TRAILER);

		dispatch.invokeOneWay(req);
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(String binding, String endpoint) {
		Service service = Service.create(Constants.SERVICE_QNAME);
		service.addPort(Constants.PORT_QNAME, binding, endpoint);
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME,
				Source.class, Service.Mode.MESSAGE);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}
}
