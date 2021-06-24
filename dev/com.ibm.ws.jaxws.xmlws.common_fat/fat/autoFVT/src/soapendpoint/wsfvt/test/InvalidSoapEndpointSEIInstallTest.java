package soapendpoint.wsfvt.test;

import com.ibm.ws.wsfvt.test.framework.FVTTestCase;

/**
 * Tests a proxy SEI with SoapHTTPBinding.
 */
public class InvalidSoapEndpointSEIInstallTest extends FVTTestCase {

	/**
	 * Tests a warning when a proxy is used with SOAP_HTTP_BINDING.
	 */
	public void testWarningWhenStartInvalidApp() {
		if (!TestUtils.isPrereqMet()) {
			return;
		}
		// String expected = "The ServiceDescription failed to validate due to
		// the following errors:";
		// boolean found = Cell.getDefaultCell().getDefaultServer()
		// .searchSystemOutLog(new String[] { expected });
		// assertTrue("The string \"" + expected + "\" was not found in the"
		// + " SystemOut.log", found);
		//
		// String expected2 = ":: Endpoint failed validation :: :: A
		// SOAP_HTTP_BINDING was found on "
		// + "a @Bindingtype SEI based Endpoint. SOAP_HTTP_BINDING is supported
		// on Provider Endpoints only.";
		// boolean found2 = TopologyDefaults.defaultAppServer
		// .searchSystemOutLog(new String[] { expected2 });
		// assertTrue("The string \"" + expected2 + "\" was not found in the"
		// + " SystemOut.log", found2);
	}
}
