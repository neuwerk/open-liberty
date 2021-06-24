//
// @(#) 1.14 autoFVT/src/jaxws/badproxy/wsfvt/test/BadProxyTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/28/07 15:34:20 [8/8/12 06:55:25]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect             Description
// ----------------------------------------------------------------------------
// 05/31/06   sedov       LIDB3296.41-02     New File
// 11/15/06   sedov       D404343            Removed dependency on AdminCommandFactory
// 01/29/07   sedov       D417317            Validation updates
// 01/30/07   sedov       D417716            Added new tests
// 02/01/07   sedov       D417716            Will not check logs on non-windows platforms
// 04/20/07   sedov       D433811            Fixed NPE in runTest
// 04/30/07   sedov       D433811.1          Additional checks for security, and non distribted platforms
// 11/28/07   sedov       D486125            Disabled @BT=HTTP test case
//
package jaxws.badproxy.wsfvt.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import jaxws.badproxy.wsfvt.common.*;
import jaxws.badproxy.wsfvt.common.asyncmethods.AsyncMethodsSEI;
import jaxws.badproxy.wsfvt.common.asyncmethods.AsyncMethodsService;
import jaxws.badproxy.wsfvt.common.mismatchedsei.MismatchedSEIPort;
import jaxws.badproxy.wsfvt.common.mismatchedsei.MismatchedSEIService;
import jaxws.badproxy.wsfvt.common.mismatchedsoap11.MismatchedSOAP11Port;
import jaxws.badproxy.wsfvt.common.mismatchedsoap11.MismatchedSOAP11Service;
import jaxws.badproxy.wsfvt.common.mismatchedsoap12.MismatchedSOAP12Port;
import jaxws.badproxy.wsfvt.common.mismatchedsoap12.MismatchedSOAP12Service;
import jaxws.badproxy.wsfvt.common.mismatchedwsdl.MismatchedWsdlPort;
import jaxws.badproxy.wsfvt.common.mismatchedwsdl.MismatchedWsdlService;
import jaxws.badproxy.wsfvt.common.mtomsoap12.MTOMSoap12Port;
import jaxws.badproxy.wsfvt.common.mtomsoap12.MTOMSoap12Service;
import jaxws.badproxy.wsfvt.common.nowsdlsoap11.NoWsdlSOAP11Port;
import jaxws.badproxy.wsfvt.common.nowsdlsoap11.NoWsdlSOAP11Service;
import jaxws.badproxy.wsfvt.common.nowsdlsoap12.NoWsdlSOAP12Port;
import jaxws.badproxy.wsfvt.common.nowsdlsoap12.NoWsdlSOAP12Service;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class BadProxyTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	LogCommands log = null;

	private String VALIDATION_ERROR = "javax.xml.ws.WebServiceException";
	
	public BadProxyTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(BadProxyTest.class);
		
		//TestSuite suite = new TestSuite();
		//suite.addTest(new BadProxyTest("testProxy_validate_bt_SOAP12MTOM_wsdl_SOAP12"));
		
		return suite;
	}

	public void setUp() {
		System.out.println("====================== " + getName()
				+ " =========================");
		log = new LogCommands(Constants.WAS_HOST_NAME, Constants.WAS_HTTP_PORT,
				"/jaxws.badproxy.logger/logger", LogCommands.LogFile.sysout,
				Constants.WAS_SERVER_NAME);
	}

	/**
	 * @testStrategy Deploy an endpoint with a SOAP12 wsdl with BidningType
	 *               specifying SOAP11. The endpoint should not be started and a
	 *               mismatch exception logged
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint with a SOAP12 wsdl with BidningType specifying SOAP11. The endpoint should not be started and a mismatch exception logged",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_mismatch_bt_SOAP11_wsdl_SOAP12() throws Throwable {

		String appBaseName = Constants.MISMATCHED_SOAP11_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = true;
		boolean failInvoke = true;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);
			MismatchedSOAP11Port port = (new MismatchedSOAP11Service())
					.getMismatchedSOAP11Port();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": exception creating proxy: " + e);
		}

		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Deploy an endpoint with a SOAP12 wsdl with BidningType
	 *               specifying SOAP11. The endpoint should not be started and a
	 *               mismatch exception logged
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint with a SOAP12 wsdl with BidningType specifying SOAP11. The endpoint should not be started and a mismatch exception logged",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_validate_bt_SOAP12MTOM_wsdl_SOAP12() throws Throwable {

		String appBaseName = Constants.VALIDATE_SOAP12MTOM_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = false;
		boolean failInvoke = false;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);
			MTOMSoap12Port port = (new MTOMSoap12Service())
					.getMTOMSoap12Port();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": exception creating proxy: " + e);
		}

		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, null);
	}	
	
	/**
	 * @testStrategy Deploy an endpoint with a SOAP12 wsdl with BidningType
	 *               specifying SOAP11. The endpoint should not be started and a
	 *               mismatch exception logged
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint with a SOAP12 wsdl with BidningType specifying SOAP11. The endpoint should not be started and a mismatch exception logged",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_validate_NoBT_wsdl_SOAP12() throws Throwable {

		String appBaseName = Constants.VALIDATE_SOAP12NoBT_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = true;
		boolean failInvoke = true;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);
			MismatchedSOAP11Port port = (new MismatchedSOAP11Service())
					.getMismatchedSOAP11Port();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": exception creating proxy: " + e);
		}

		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, VALIDATION_ERROR);
	}	
	
	/**
	 * @testStrategy Deploy an endpoint with a SOAP11 wsdl with BidningType
	 *               specifying SOAP12. The endpoint should not be started and a
	 *               mismatch exception logged
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint with a SOAP11 wsdl with BidningType specifying SOAP12. The endpoint should not be started and a mismatch exception logged",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_mismatch_bt_SOAP12_wsdl_SOAP11() throws Throwable {
		String appBaseName = Constants.MISMATCHED_SOAP12_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = true;
		boolean failInvoke = true;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);
			MismatchedSOAP12Port port = (new MismatchedSOAP12Service())
					.getMismatchedSOAP12Port();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": exception creating proxy: " + e);
		}
		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Deploy an endpoint without a wsdl/sei. Expect the runtime
	 *               to generate the correct WSDL based on BindingType
	 *               annotation
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint without a wsdl/sei. Expect the runtime to generate the correct WSDL based on BindingType annotation",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_genWsdl_noWsdl_btSOAP11() throws Throwable {
		String appBaseName = Constants.NOWSDL_SOAP11_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = false;
		boolean failInvoke = false;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);
			QName serviceName = new QName(
					"http://NoWsdlSOAP11.common.wsfvt.badproxy.jaxws",
					"NoWsdlSOAP11Service");
			URL wsdlLoc = new URL(endpoint + "?wsdl");

			System.out.println("Reading WSDL from: " + wsdlLoc);
			
			// configure a proxy - for this test we force the generated wsdl to
			// be read
			NoWsdlSOAP11Service service = new NoWsdlSOAP11Service(wsdlLoc, serviceName);
			NoWsdlSOAP11Port port = service.getNoWsdlSOAP11Port();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": Exception creating proxy: " + e);
		}

		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, null);
	}

	/**
	 * @testStrategy Deploy an endpoint without a wsdl/sei. Expect the runtime
	 *               to generate the correct WSDL based on BindingType
	 *               annotation
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint without a wsdl/sei. Expect the runtime to generate the correct WSDL based on BindingType annotation",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_genWsdl_noWsdl_btSOAP12() throws Throwable {
		String appBaseName = Constants.NOWSDL_SOAP12_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = false;
		boolean failInvoke = false;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);
			/*QName serviceName = new QName(
					"http://NoWsdlSOAP12.common.wsfvt.badproxy.jaxws",
					"NoWsdlSOAP12Service");
			URL wsdlLoc = new URL(endpoint + "?wsdl");//*/

			// configure a proxy - for this test we force the generated wsdl to
			// be read
			NoWsdlSOAP12Service service = new NoWsdlSOAP12Service();
			NoWsdlSOAP12Port port = service.getNoWsdlSOAP12Port();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": exception creating proxy: " + e);
		}

		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, null);
	}

	/**
	 * @testStrategy Deploy an endpoint without a wsdl/sei. The runtime cannot
	 *               generate a wsdl for a bean endpoint with HTTPBidning
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint without a wsdl/sei. The runtime cannot generate a wsdl for a bean endpoint with HTTPBidning",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testProxy_genWsdl_noWsdl_btHTTP() throws Throwable {
		String appBaseName = Constants.NOWSDL_HTTP_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = true;
		boolean failInvoke = true;
		boolean doServletErrorCheck = false;

		// the wsdl should never be generated/endpoint should never be
		// published...so we will simply restart the app and look
		// or errors
		runTest(appBaseName, doRestartApp, failAppStartup, null, failInvoke,
				doServletErrorCheck, "cannot be used to generate a WSDL");
	}

	/**
	 * @testStrategy Deploy an endpoint that does not use an SEI, an operation
	 *               that is defined in wsdl is not implemented in the endpoint
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint that does not use an SEI, an operation that is defined in wsdl is not implemented in the endpoint",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_mismatch_wsdl() throws Throwable {
		String appBaseName = Constants.MISMATCHED_WSDL_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = true;
		boolean failInvoke = true;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);

			// configure a proxy - for this test we force the generated wsdl to
			// be read
			MismatchedWsdlService service = new MismatchedWsdlService();
			MismatchedWsdlPort port = service.getMismatchedWsdlPort();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": exception creating proxy: " + e);
		}

		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * @testStrategy Deploy an endpoint that was generated using enableAsyncMapping cusomization
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint that was generated using enableAsyncMapping cusomization",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_asyncMethods() throws Throwable {
		String appBaseName = "AsyncMethods";
		boolean doRestartApp = true;
		boolean failAppStartup = true;
		boolean failInvoke = true;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);

			// configure a proxy - for this test we force the generated wsdl to
			// be read
			AsyncMethodsService service = new AsyncMethodsService();
			AsyncMethodsSEI port = service.getAsyncMethodsPort();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": exception creating proxy: " + e);
		}

		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, VALIDATION_ERROR);
	}
	
	/**
	 * @testStrategy Deploy an endpoint that references an SEI (via
	 *               endpointInterface), but an operation that is defined in sei
	 *               is not implemented in the endpoint
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Deploy an endpoint that references an SEI (via endpointInterface), but an operation that is defined in sei is not implemented in the endpoint",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testProxy_mismatch_sei() throws Throwable {
		String appBaseName = Constants.MISMATCHED_SEI_NAME;
		boolean doRestartApp = true;
		boolean failAppStartup = true;
		boolean failInvoke = true;
		boolean doServletErrorCheck = false;
		InvokeConfig ic = null;

		// configure a proxy
		try {
			String endpoint = Constants.getAddress(appBaseName);

			// configure a proxy - for this test we force the generated wsdl to
			// be read
			MismatchedSEIService service = new MismatchedSEIService();
			MismatchedSEIPort port = service.getMismatchedSEIPort();
			Map<String, Object> rc = ((BindingProvider) port)
					.getRequestContext();
			rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

			// create an invoke object
			ic = new InvokeConfig();
			ic.port = port;
			ic.operationToCall = "ping";
			ic.operationParameters = new Object[] { new Holder<String>("string") };
		} catch (Exception e) {
			System.out.println(getName() + ": exception creating proxy: " + e);
		}

		runTest(appBaseName, doRestartApp, failAppStartup, ic, failInvoke,
				doServletErrorCheck, VALIDATION_ERROR);
	}

	/**
	 * The actual method that runs the test. We will restart the application to
	 * check for any exceptions generated during start up, try to invoke it and
	 * then check the system_out.log for any exceptions
	 * 
	 * @param applicationBaseName
	 *            base application name. This is the XYZ part of XYZBadPortImpl
	 * @param restartApp
	 *            restart the application
	 * @param startShouldFail
	 *            its ok for the application to not start
	 * @param invokeConfig
	 *            configuration object that will do that actual invoke
	 * @param invokeShouldFail
	 *            its ok for the invoke to return 404 or 500
	 * @param doServletErrorCheck
	 *            look for [servlet error] in was system_out.log
	 * @return returns the contents of system_out.log that resulted from this
	 *         test
	 * @throws Exception
	 *             when one of the tests fails
	 */
	private String runTest(String applicationBaseName, boolean restartApp,
			boolean startShouldFail, InvokeConfig invokeConfig,
			boolean invokeShouldFail, boolean doServletErrorCheck, String textToFind)
			throws Throwable {

		System.out.println(getName() + ": enter runTest");
		
		String osName = System.getProperty("os.name");
		String appName = "jaxws.badproxy." + applicationBaseName;

		// skip test if security is enabled
		if (Boolean.parseBoolean(Constants.SEC_ENABLED)){
			System.out.println(getName() + ": skipping, security is enabled");
			return "";
		}
		
		// skip test if running on a non-distributed platform
		if ("OS/400".equalsIgnoreCase(osName)){
			System.out.println(getName() + ": skipping, non-distributed platform");
			return "";
		}
	
		// we will only do log checking on Windows, on all other platforms we will
		// resort to weak validatioin by pining and restarting
		System.out.println(getName() + ": OS=" + osName);
		boolean isOsWindows = (osName.indexOf("Windows") != -1);		

		System.out.println(getName() + ": stopApp block (restart=" + restartApp + ")");
		
		// Stop the application: it is possible it didn't start at first
		// so it doesn't mean anythign if I can't stop it. But we will
		// log the exception anyway for reference purposes
		if (restartApp) {
			System.out.println(getName() + ": stopping app: " + appName);
			boolean stopOk = AdminCommands.getState(appName) != AdminCommands.AppState.STARTED;
			try {
				AdminCommands.stopApplication(appName);
			} catch (Exception e) {
				System.out.println(getName() + ": error restarting " + e);
			}
			System.out.println(getName() + ": app_stopped=" + stopOk);
		}

		System.out.println(getName() + ": marking log");
		
		// mark the log file. We are interested in capturing any data
		// from the startup and invoke only
		log.mark();

		System.out.println(getName() + ": startApp block (restart=" + restartApp + ")");
		
		// stop the application...most applications should
		// never start
		if (restartApp) {
			System.out.println(getName() + ": starting app: " + appName);
			try {
				AdminCommands.startApplication(appName);
				if (startShouldFail)
					throw new Exception(
							"Start suceeded for an application that should not start");
			} catch (Exception ex) {
				System.out.println(getName() + ": " + ex);
			}
			System.out.println(getName() + ": app_started");
		}
		
		System.out.println(getName() + ": invokeApp block (shouldFail=" + invokeShouldFail + ")");

		// ping the service to see if it responds
		try {
			boolean invokeSuccess = invokeConfig.invoke();

			System.out.println(getName() + ": invoke suceeded: "
					+ invokeSuccess);

			if (invokeShouldFail)
				throw new Exception(
						"Invoke succeeded against an endpoint that should not be active");
		} catch (Throwable e) {
			System.out.println(getName() + ": invoke failed: " + e);
			if (!invokeShouldFail) throw e;
		}

		System.out.println(getName() + ": read SystemOut.log");
		
		// we made it this far, get the log
		String was_sys_out = "";
		try {
			if (isOsWindows){
				was_sys_out = log.readUpdates();
		
				System.out
						.println("====================== Begin SYSTEM_OUT.LOG ==========================");
				System.out.println(was_sys_out);
				System.out
						.println("====================== End SYSTEM_OUT.LOG ============================");
			}
		} catch (Exception e){
			System.out.println(getName() + ": error reading systemout.log: " + e);
		}
		
		// the tests that require reading systemout.log will only
		// work on windows platforms
		if (isOsWindows){
			
			System.out.println(getName() + ": checking SystemOut.log startShouldFail=" + startShouldFail);
			
			if (startShouldFail) {
				String searchString = "Application started: jaxws.badproxy."
						+ applicationBaseName;
	
				System.out.println("Serching output for: " + searchString);
				if (was_sys_out.indexOf(searchString) != -1)
					throw new Exception(
							"Detected 'Application started' block for jaxws.badproxy."
									+ applicationBaseName);
			}

			System.out.println(getName() + ": checking SystemOut.log doServletErrorCheck=" + doServletErrorCheck);
			
			if (doServletErrorCheck) {
				String searchString = "[Servlet Error]-[jaxws.badproxy.wsfvt.server."
						+ applicationBaseName + "BadPortImpl]";
				System.out.println("Serching output for: " + searchString);
				if (was_sys_out.indexOf(searchString) == -1)
					throw new Exception(
							"Unable to find [Servlet Error] block in SYSTEM_OUT for "
									+ appName);
			}
	
			System.out.println(getName() + ": checking SystemOut.log textToFind=" + textToFind);
			
			if (textToFind != null){
				boolean isFound = (was_sys_out.indexOf(textToFind)!= -1);
				System.out.println("Searching SystemOut.log for '" + textToFind + "'...found=" + isFound);
				
				if (!isFound){
					throw new Exception("Searching SystemOut.log for '" + textToFind + "'...found=" + isFound);
				}
			}
		}
		
		System.out.println(getName() + ": exit (" + was_sys_out + ")");
		
		return was_sys_out;
	}

	/**
	 * Configuration object used for invoke testing. Since we do not have generic
	 * methods as we did with dispatch, we will lookup a specific method using
	 * introspection and then invoke it with provided parameters
	 */
	class InvokeConfig {
		public Object port = null;

		public String operationToCall = null;

		public Object[] operationParameters = null;

		public boolean invoke() throws Throwable {
			Method[] methods = port.getClass().getMethods();
			for (Method m : methods) {
				if (m.getName().equals(operationToCall)) {
					System.out.println("Method=" + m.getName());
					Class[] params = m.getParameterTypes();
					for (Class p: params){
						System.out.println("Param=" + p.getName());
					}
					try {
					m.invoke(port, operationParameters);
					} catch (InvocationTargetException ite){
						if (ite.getCause() != null)
							throw ite.getCause();
						else
							throw ite;
					}
					return true;
				}
			}

			return false;
		}
	}
}
