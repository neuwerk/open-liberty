//
// @(#) 1.5 autoFVT/src/jaxws/badprovider/wsfvt/test/BadProvidersTest.java, WAS.websvcs.fvt, WASX.FVT 2/1/07 11:56:17 [7/11/07 13:14:50]
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
// 11/15/06 sedov       D404343         Removed dependency on AdminCommandFactory
// 01/29/07 sedov       D417317         Updated for Validation defect 409915
// 02/01/07 sedov       D417716         Will not check logs on non-windows platforms
//
package jaxws.badprovider.wsfvt.test;

import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;
import javax.xml.ws.soap.SOAPBinding;

import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import jaxws.badprovider.wsfvt.common.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test case for invalid types of Providers - providers that can be deployed but
 * can never be started because of invalid configuration (such as invalid
 * combination of annotations). This is tested by first pinging the service to
 * make sure we get a 404/500 and when we do, we look for an exception in the
 * server log
 */
public class BadProvidersTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	LogCommands log = null;

	private String VALIDATION_ERROR = "has thrown exception, unwinding now";
	

	public BadProvidersTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(BadProvidersTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("====================== " + getName()
				+ " =========================");
        String logPath = TopologyDefaults.libServer.getLogsRoot()+"/console.log";
        System.out.println("logRootPath ="+ logPath);
		log = new LogCommands(Constants.WAS_HOST_NAME, Constants.WAS_HTTP_PORT,
				"/jaxws.badprovider.logger/logger", LogCommands.LogFile.sysout,
				Constants.WAS_SERVER_NAME,logPath);
	}

	/**
	 * @testStrategy Test to see if default values for BindingType and
	 *               ServiceMode (should be SOAP11/PAYLOAD)
	 * @wsdl provider.wsdl
	 * @target DefaultsBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_anno_defaults() throws Exception {
		final String endpoint = Constants.DEFAULT_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = false;
		final boolean expectInvokeFail = false;

		runTest("Defaults", endpoint, binding, doRestart, expectStartFail,
				expectInvokeFail, doServletErrorCheck, null);
	}

	/**
	 * @testStrategy Test for a provider with an invalid bindingId (as
	 *               "BadBindingId")
	 * @wsdl provider.wsdl
	 * @target InvalidBindingBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_annot_BT_invalid() throws Exception {
		final String endpoint = Constants.BADBINDING_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("InvalidBinding", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Test for a provider a WSP annotation but it does not
	 *               impelment a Provider interface
	 * @wsdl provider.wsdl
	 * @target InvalidBindingBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_anno_WSP_notImplementsProvider() throws Exception {
		final String endpoint = Constants.NonProviderWSP_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("NonProviderWSP", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Test for a provider a WSP annotation but it does not
	 *               impelment a Provider interface
	 * @wsdl provider.wsdl
	 * @target InvalidBindingBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_anno_WS_implementsProvider() throws Exception {
		final String endpoint = Constants.WSProvider_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("WSProvider", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}	
	
	/**
	 * @testStrategy Test for a provider without WebServiceProvider annotation
	 * @wsdl provider.wsdl
	 * @target NoAnnotationBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_anno_WSP_missing() throws Exception {
		final String endpoint = Constants.NOANNOTATION_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = false;
		final boolean expectInvokeFail = true;

		runTest("NoAnnotation", endpoint, binding, doRestart, expectStartFail,
				expectInvokeFail, doServletErrorCheck, null);
	}	
	
	/**
	 * @testStrategy Test for a provider with both WSP and WS annotations
	 * @wsdl provider.wsdl
	 * @target InvalidBindingBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_anno_WSP_andWS() throws Exception {
		final String endpoint = Constants.WSPandWS_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("WSPandWS", endpoint, binding, doRestart, expectStartFail,
				expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}	
	
	/**
	 * @testStrategy Test for a provider with a private defautl constructor
	 * @wsdl provider.wsdl
	 * @target PrivateConstructorBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_type_privateDefaultConstructor() throws Exception {
		final String endpoint = Constants.PRIVATE_CTOR_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("PrivateConstructor", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Test for a provider without a type (should result in a
	 *               raw/object type)
	 * @wsdl provider.wsdl
	 * @target NoAnnotationBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_type_untyped() throws Exception {
		final String endpoint = Constants.UNTYPED_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("UntypedProvider", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Test for a provider typed with an unsupported type (typed
	 *               as Exception)
	 * @wsdl provider.wsdl
	 * @target UnsupportedTypeBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_type_unsupported() throws Exception {
		final String endpoint = Constants.UNSUPPORTED_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = false;

		runTest("UnsupportedProvider", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, null);
	}



	/**
	 * @testStrategy Test for invalid combination: SOAPMessage/SOAP11/Payload
	 * @wsdl provider.wsdl
	 * @target SOAP11PaylaodSoapMessageBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_type_SOAPMessage_SOAP11Payload()
			throws Exception {
		final String endpoint = Constants.S11PayloadSoapMessage_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("SOAP11PayloadSoapMessage", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Test for invalid combination: SOAPMessage/SOAP12/Payload
	 * @wsdl provider.wsdl
	 * @target SOAP12PayloadSoapMessageBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_type_SOAPMessage_SOAP12Payload()
			throws Exception {
		final String endpoint = Constants.S12PayloadSoapMessage_ADDRESS;
		final String binding = SOAPBinding.SOAP12HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("SOAP12PayloadSoapMessage", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Test for invalid combination: SOAPMessage/HTTP/Message
	 * @wsdl provider.wsdl
	 * @target HTTPMessageSoapMessageBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_type_SOAPMessage_HTTPMessage() throws Exception {
		final String endpoint = Constants.XMLSoapMessage_ADDRESS;
		final String binding = HTTPBinding.HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("HTTPMessageSoapMessage", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Test for invalid combination: DataSource/SOAP11
	 * @wsdl provider.wsdl
	 * @target SOAP11PayloadDataSourceBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_type_DataSource_SOAP11Message() throws Exception {
		final String endpoint = Constants.S11MessageDataSource_ADDRESS;
		final String binding = SOAPBinding.SOAP11HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("SOAP11MessageDataSource", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Test for invalid combination: DataSource/SOAP12
	 * @wsdl provider.wsdl
	 * @target SOAP12PayloadDataSourceBadPortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProvider_type_DataSource_SOAP12Payload() throws Exception {
		final String endpoint = Constants.S12PayloadDataSource_ADDRESS;
		final String binding = SOAPBinding.SOAP12HTTP_BINDING;
		final boolean doRestart = true;
		final boolean doServletErrorCheck = false;
		final boolean expectStartFail = true;
		final boolean expectInvokeFail = true;

		runTest("SOAP12PayloadDataSource", endpoint, binding, doRestart,
				expectStartFail, expectInvokeFail, doServletErrorCheck, VALIDATION_ERROR);		
	}

	/**
	 * The actual method that runs the test. We will restart the application to
	 * check for any exceptions generated during start up, try to invoke it and
	 * then check the system_out.log for any exceptions
	 * 
	 * @param applicationBaseName
	 *            base application name. This is the XYZ part of XYZBadPortImpl
	 * @param endpoint
	 *            endpoint URL to use to ping the service
	 * @param binding
	 *            bindingId used by the service
	 * @param restartApp
	 *            restart the application
	 * @param startShouldFail
	 *            its ok for the application to not start
	 * @param invokeShouldFail
	 *            its ok for the invoke to return 404 or 500
	 * @param doServletErrorCheck
	 *            look for [servlet error] in was system_out.log
	 * @return returns the contents of system_out.log that resulted from this
	 *         test
	 * @throws Exception
	 *             when one of the tests fails
	 */
	private String runTest(String applicationBaseName, String endpoint,
			String binding, boolean restartApp, boolean startShouldFail,
			boolean invokeShouldFail, boolean doServletErrorCheck, String logKeyLineMatch)
			throws Exception {	
		String osName = System.getProperty("os.name");
		String appName = "jaxws.badprovider." + applicationBaseName+"App.ear";

		// skip test if security is enabled
		if (Boolean.parseBoolean(Constants.SEC_ENABLED)){
			System.out.println(getName() + ": skipping, security is enabled");
			return "";
		}
		
		// we will only do log checking on Windows, on all other platforms we will
		// resort to weak validatioin by pining and restarting
		System.out.println(getName() + ": OS=" + osName);
		boolean isOsWindows = (osName.indexOf("Windows") != -1);
        System.out.println("restarting application");        
        TopologyDefaults.libServer.restartDropinsApplication(appName);

        System.out.println("restart done");
		
		// mark the log file. We are interested in capturing any data
		// from the startup and invoke only
		log.mark();


		// ping the service to see if it responds
		try {
			// create a dispatch for the endpoint
			Dispatch<Source> src = getDispatch(binding, endpoint);
			Source msg = Constants.toStreamSource(Constants.TWOWAY_MSG);
			System.out.println("msg="+Constants.toString(msg));

			Source reply = src.invoke(msg);
			System.out.println("reply="+Constants.toString(reply));
			System.out.println(getName() + ": invoke suceeded: " + Constants.toString(msg));
			
			if (invokeShouldFail)
				throw new Exception(
						"Invoke succeeded against an endpoint that should not be active");
		} catch (Exception e) {
			System.out.println(getName() + ": invoke failed: " + e);
			//if (!invokeShouldFail) throw e;
		}

		// we made it this far, get the log
		String was_sys_out = log.readUpdates();

		System.out
				.println("====================== Begin SYSTEM_OUT.LOG ==========================");
		System.out.println(was_sys_out);
		System.out
				.println("====================== End SYSTEM_OUT.LOG ============================");

		if (startShouldFail){
			String searchString = "Application started: jaxws.badprovider."
				+ applicationBaseName;
			
		System.out.println("Serching output for: " + searchString);
		if (isOsWindows &&was_sys_out.indexOf(searchString) != -1)
			throw new Exception(
					"Detected 'Application started' block for jaxws.badprovider." + 
							applicationBaseName);
		
		}
		
		//
		if (isOsWindows && doServletErrorCheck) {
			String searchString = "[Servlet Error]-[jaxws.badprovider.wsfvt.server."
					+ applicationBaseName + "BadPortImpl]";
			System.out.println("Serching output for: " + searchString);
			if (was_sys_out.indexOf(searchString) == -1)
				throw new Exception(
						"Unable to find [Servlet Error] block in SYSTEM_OUT for "
								+ appName);
		}
		
		if (isOsWindows && logKeyLineMatch != null){
			boolean isFound = (was_sys_out.indexOf(logKeyLineMatch)!= -1);
			System.out.println("Searching SystemOut.log for '" + logKeyLineMatch + "'...found=" + isFound);
			
			if (!isFound){
				throw new Exception("'" + logKeyLineMatch + "' not found in SystemOut.log");
			}
		}
		
		return was_sys_out;
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
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				Service.Mode.PAYLOAD);

		assertNotNull("Dispatch not null", dispatch);

		return dispatch;
	}
}
