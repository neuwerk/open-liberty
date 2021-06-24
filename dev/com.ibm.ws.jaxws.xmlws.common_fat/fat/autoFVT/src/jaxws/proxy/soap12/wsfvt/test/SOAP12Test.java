package jaxws.proxy.soap12.wsfvt.test;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import jaxws.proxy.common.Constants;
import jaxws.proxy.soap12.wsfvt.soap12doclit.ProxySOAP12Doc;
import jaxws.proxy.soap12.wsfvt.soap12doclit.ProxySOAP12DocLitWrService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for SOAP1.2 Binding
 * 
 * @endpoint ProxySOAP12PortImpl
 * @wsdl proxy_soap12.wsdl
 */
public class SOAP12Test extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	static final String SOAP12DOCLITWR_ENDPOINT = Constants.SOAP12_BASE + "/services/ProxySOAP12DocLitWrService";
	static final String SOAP12ENVELOPECHECK_ENDPOINT = Constants.SOAP12_BASE + "/services/EnvelopeVersionCheck";
	public SOAP12Test(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(SOAP12Test.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Try sending a DocLitWrapped ping message over SOAP 1.2
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try sending a DocLitWrapped ping message over SOAP 1.2",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAP12DocLitPing() {
		ProxySOAP12Doc port = new ProxySOAP12DocLitWrService()
				.getProxySOAP12DocLitWrPort();

		// send the message tp the sender servlet to check soap envelope version
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				SOAP12DOCLITWR_ENDPOINT);

		System.out.println(getName() + ": binding="
				+ ((BindingProvider) port).getBinding().getClass());

		String expect = "ping";
		String ret = port.ping(expect);

		assertTrue("Unexpected value returned", ret.equals(expect));
	}

	/**
	 * @testStrategy Try sending a SOAP 1.2 message to the sender servlet to
	 *               verify that SOAP 1.2 envelope was used
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Try sending a SOAP 1.2 message to the sender servlet to verify that SOAP 1.2 envelope was used",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSOAP12Envelope() {
		ProxySOAP12Doc port = new ProxySOAP12DocLitWrService()
				.getProxySOAP12DocLitWrPort();

		// send the message tp the sender servlet to check soap envelope version
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				SOAP12ENVELOPECHECK_ENDPOINT);

		String expect = "#check_soapEnvelopeVersion#";
		String ret = port.ping(expect);

		if (!ret.equals("soap1.2")) {
			fail("Message was sent over unexpected SOAP envelope version of: "
					+ ret);
		}
	}

}
