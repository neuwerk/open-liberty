//
// @(#) 1.1.1.6 autoFVT/src/jaxws/dispatch/wsfvt/test/ServiceTest.java, WAS.websvcs.fvt, WASX.FVT 4/23/07 09:42:19 [7/11/07 13:15:20]
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
// 01/20/07 sedov    415799             removed test_blah
// 04/23/07 sedov    433821             each test uses unique service qname
//
package jaxws.dispatch.wsfvt.test;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
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
 * Tests for Service object. Will tryto create various servcie artifcats
 * (services, ports and dispatches) with invalid parameters. All tests are
 * expected to throw a WebServiceException with an appropriate getCause which
 * provides additional details
 * 
 * Note: Service.create(null, ..) is not an exception
 */
public class ServiceTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public ServiceTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ServiceTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/** **************** Service.create tests **************************** */

	/**
	 * @testStrategy Test for WebServiceException on Service.create(null) - null
	 *               service name
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_serviceCreate_nullServiceName() throws Exception {
		try {
			Service.create(null);

			fail("WebServiceException is expected when calling Service.create(null)");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on Service.create(new
	 *               QName("","")) - empty service name
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_serviceCreate_emptyServiceName() throws Exception {
		try {
			Service.create(new QName("", ""));
			fail("WebServiceException is expected when calling Service.create(new QName(``, ``))");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on Service.create(..., ...) -
	 *               ?wsdl results in host not found
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_serviceCreate_WsdlLocation_noHost() throws Exception {
		try {
			Service.create(
					new URL(Constants.INVALID_ENDPOINT_ADDRESS + "?wsdl"),
					Constants.SERVICE_QNAME);
			fail("WebServiceException is expected when calling Service.create('badURL.com?wsdl', ..)");
		} catch (WebServiceException wse) {
			System.out
					.println("test_serviceCreate_WsdlLocation_noHost: " + wse);
		} catch (Exception e) {
			fail("Unexpected exception on Service.create(new URL(``), ..): "
					+ e);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on Service.create(..., ...) -
	 *               ?wsdl results in 404 not found
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_serviceCreate_WsdlLocation_404NotFound() throws Exception {
		try {
			Service.create(new URL(Constants.BASE_ENDPOINT
					+ "NoExistingService?wsdl"), Constants.SERVICE_QNAME);
			fail("WebServiceException is expected when calling Service.create('badURL.com?wsdl', ..)");
		} catch (WebServiceException wse) {
			System.out
					.println("test_serviceCreate_WsdlLocation_noHost: " + wse);
		} catch (Exception e) {
			fail("Unexpected exception on Service.create(new URL(``), ..): "
					+ e);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on Service.create(..., ...) -
	 *               service name does not match any in the wsdl
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_serviceCreate_serviceNameValidation() throws Exception {
		try {
			// create a service whose name does not match any in the WSDL,
			// should throw an exception
			Service.create(
					new URL(Constants.SOAP11_ENDPOINT_ADDRESS + "?wsdl"),
					new QName("http://example.com", "ServiceName"));

			fail("WebServiceException is expected when calling Service.create(wsdlLocation, serviceNameNotInWSDL)");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		} catch (Exception e) {
			fail("Unexpected exception on Service.create(wsdlLocation, serviceNameNotInWSDL): "
					+ e);
		}
	}

	/** **************** Service.addPort() tests ************************* */

	/**
	 * @testStrategy Test for WebServiceException on service.addPort(null, ...,
	 *               ...) - null port name
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_addPort_nullPortName() throws Exception {
		Service service = Service.create(Constants.SERVICE_QNAME);

		try {
			// add a port, but do not specify a portname
			service.addPort(null, SOAPBinding.SOAP11HTTP_BINDING,
					Constants.SOAP11_ENDPOINT_ADDRESS);

			fail("WebServiceException should be thrown when addPort(null, .., ..) is called.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		} catch (Exception e) {
			fail("Unexpected exception on addPort(null, .., ..): " + e);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on service.addPort(new
	 *               QName("", ""), ..., ...) - null port name
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_addPort_emptyPortName() throws Exception {
		Service service = Service.create(Constants.SERVICE_QNAME);

		try {
			// add a port, but make the port name empty
			service.addPort(new QName("", ""), SOAPBinding.SOAP11HTTP_BINDING,
					Constants.SOAP11_ENDPOINT_ADDRESS);

			fail("WebServiceException should be thrown when addPort(new QName(``, ``), .., ..) is specified.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		} catch (Exception e) {
			fail("Unexpected exception on addPort(new QName(``, ``), .., ..): "
					+ e);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on service.addPort(..., null,
	 *               ...) - null bindingId
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
	/*
	 * public void test_addPort_nullBinding() throws Exception { Service service =
	 * Service.create(Constants.SERVICE_QNAME);
	 * 
	 * try { // add a port, but do not specify a portname
	 * service.addPort(Constants.PORT_QNAME, null,
	 * Constants.SOAP11_ENDPOINT_ADDRESS);
	 * 
	 * fail("WebServiceException should be thrown when addPort(.., null, ..) is
	 * called."); } catch (WebServiceException wse) {
	 * System.out.println(getName() + ": " + wse); } }//
	 */

	/**
	 * @testStrategy Test for WebServiceException on service.addPort(..., "",
	 *               ...) - empty bindingId
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_addPort_emptyBinding() throws Exception {
		Service service = Service.create(Constants.SERVICE_QNAME);

		try {
			// add a port, but do not specify a portname
			service.addPort(Constants.PORT_QNAME, "",
					Constants.SOAP11_ENDPOINT_ADDRESS);

			fail("WebServiceException should be thrown when addPort(.., '', ..) is called.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on service.addPort(...,
	 *               "BadBindingId", ...) - unsupported bindingId
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_addPort_invalidBinding() throws Exception {
		Service service = Service.create(Constants.SERVICE_QNAME);

		try {
			// add a port, but do not specify a portname
			service.addPort(Constants.PORT_QNAME,
					"ThisBindingProbablydoesNotExist",
					Constants.SOAP11_ENDPOINT_ADDRESS);

			fail("WebServiceException should be thrown when addPort(.., 'ThisBindingProbablydoesNotExist', ..) is called.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for default binding use on service.addPort(..., null,
	 *               ...) - null bindingId
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_addPort_nullBinding() throws Exception {
		Service service = Service.create(new QName(Constants.WSDL_NAMESPACE,
				getName()));

		service.addPort(Constants.PORT_QNAME, null,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		Dispatch<String> dispatch = service.createDispatch(
				Constants.PORT_QNAME, String.class, Service.Mode.PAYLOAD);
		dispatch.invoke(Constants.TWOWAY_MSG);
	}

	/**
	 * @testStrategy Test for WebServiceException on service.addPort(..., ...,
	 *               null) - null endpoint address, this is valid
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_addPort_nullEndpointAddress() throws Exception {
		Service service = Service.create(new QName(Constants.WSDL_NAMESPACE,
				getName()));

		// add a port, but do not specify an endpoint address
		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				null);
		Dispatch<String> dispatch = service.createDispatch(
				Constants.PORT_QNAME, String.class, Service.Mode.PAYLOAD);

		Map<String, Object> rc = ((BindingProvider) dispatch)
				.getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		dispatch.invoke(Constants.TWOWAY_MSG);
	}

	/**
	 * @testStrategy Test for WebServiceException on service.addPort(..., ...,
	 *               "") - empty endpoint address
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_addPort_emptyEndpointAddress1() throws Exception {
		Service service = Service.create(new QName(Constants.WSDL_NAMESPACE,
				getName()));

		try {
			// add a port, but do not specify an endpoint address
			service.addPort(Constants.PORT_QNAME,
					SOAPBinding.SOAP11HTTP_BINDING, "");

			Dispatch<String> dispatch = service.createDispatch(
					Constants.PORT_QNAME, String.class, Service.Mode.PAYLOAD);

			dispatch.invoke(Constants.TWOWAY_MSG.replace("test_message",
					getName()));

			fail("WebServiceException should be thrown when invoke() is done after addPort(.., .., '') is specified.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/** **************** Service.createDisptach tests ******************** */

	/**
	 * @testStrategy Test for default mode use on service.addPort(..., ...,
	 *               null) - null ServiceMode, should default to PAYLAOD
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_addPort_nullServiceMode() throws Exception {
		Service service = Service.create(new QName(Constants.WSDL_NAMESPACE,
				getName()));

		service.addPort(Constants.PORT_QNAME, null,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		Dispatch<String> dispatch = service.createDispatch(
				Constants.PORT_QNAME, String.class, null);
		dispatch.invoke(Constants.TWOWAY_MSG);
	}

	/**
	 * @testStrategy Test for WebServiceException on
	 *               service.createDispatch(null, ..., ...) - null port name
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_createDisptach_nullPortname() throws Exception {
		Service service = Service.create(Constants.SERVICE_QNAME);

		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			Dispatch<Source> dispatch = service.createDispatch(null,
					Source.class, Service.Mode.PAYLOAD);

			fail("WebServiceException should be thrown when createDisptach(null, .., ..) is specified.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on service.createDispatch(new
	 *               QName("", ""), ..., ...) - empty port name
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_createDisptach_emptyPortName() throws Exception {
		Service service = Service.create(Constants.SERVICE_QNAME);

		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			Dispatch<Source> dispatch = service.createDispatch(
					new QName("", ""), Source.class, Service.Mode.PAYLOAD);

			fail("WebServiceException should be thrown when createDisptach(new QName('', ''), .., ..) is specified.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on
	 *               service.createDispatch(NonExistentPortName, ..., ...) -
	 *               port does not exist in a service
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_createDispatch_invalidPortName() throws Exception {
		Service service = Service.create(new QName("http://test.com",
				"test_createDispatch_PortMustExist"));

		try {
			service.createDispatch(Constants.PORT_QNAME, Source.class,
					Service.Mode.PAYLOAD);
			fail("WebServiceException should be thrown when a createDispatch(new QName('http://test.com', 'doesNotExist'), .., ..)");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on service.createDispatch(...,
	 *               Exception.class, ...) - unsupported type
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_createDisptach_invalidType() throws Exception {
		Service service = Service.create(Constants.SERVICE_QNAME);

		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			Dispatch<Exception> dispatch = service
					.createDispatch(Constants.PORT_QNAME, Exception.class,
							Service.Mode.PAYLOAD);

			fail("WebServiceException should be thrown when createDisptach(.., Exception.class, ..) is specified.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on service.createDispatch(...,
	 *               Object.class, ...) - unsupported type
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_createDisptach_invalidType2() throws Exception {
		Service service = Service.create(Constants.SERVICE_QNAME);

		service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
				Constants.SOAP11_ENDPOINT_ADDRESS);

		try {
			Dispatch<Object> dispatch = service.createDispatch(
					Constants.PORT_QNAME, Object.class, Service.Mode.PAYLOAD);

			fail("WebServiceException should be thrown when createDisptach(.., Object.class, ..) is specified.");
		} catch (WebServiceException wse) {
			System.out.println(getName() + ": " + wse);
		}
	}

	/**
	 * @testStrategy Test for WebServiceException on service.createDispatch(...,
	 *               ..., null) - invalid Service.Mode
	 * @wsdl DispatchSOAP11.wsdl
	 * @target DispatchSimplePortImpl
	 */
	/*
	 * public void test_createDisptach_nullServiceMode() throws Exception {
	 * Service service = Service.create(new QName("http://test.com",
	 * "test_createDisptach_NullServiceMode"));
	 * 
	 * service.addPort(Constants.PORT_QNAME, SOAPBinding.SOAP11HTTP_BINDING,
	 * Constants.SOAP11_ENDPOINT_ADDRESS);
	 * 
	 * try { Dispatch<Source> dispatch = service.createDispatch(
	 * Constants.PORT_QNAME, Source.class, null);
	 * 
	 * fail("WebServiceException should be thrown when createDisptach(.., ..,
	 * null) is specified."); } catch (WebServiceException wse) {
	 * System.out.println(getName() + ": " + wse); } }//
	 */

	/**
	 * Auxiliary method used to wait for a monitor for a certain amount of time
	 * before timing out
	 * 
	 * @param monitor
	 */
	private void waitBlocking(Future<?> monitor) throws Exception {
		// wait for request to complete
		int sec = Constants.CLIENT_MAX_SLEEP_SEC;
		while (!monitor.isDone()) {
			Thread.sleep(1000);
			sec--;
			if (sec <= 0) break;
		}

		if (sec <= 0)
			fail("Stopped waiting for Async response after "
					+ Constants.CLIENT_MAX_SLEEP_SEC + " sec");
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint) {
		return getDispatch(mode, endpoint, SOAPBinding.SOAP11HTTP_BINDING);
	}

	/**
	 * Auxiliary method, generates a Dispatch object on demand
	 * 
	 * @param mode
	 * @return
	 */
	private Dispatch<Source> getDispatch(Service.Mode mode, String endpoint,
			String binding) {
		Service service = Service.create(new QName(Constants.WSDL_NAMESPACE,
				getName()));
		service.addPort(Constants.PORT_QNAME, binding, endpoint);
		javax.xml.ws.Dispatch<Source> dispatch = null;
		dispatch = service.createDispatch(Constants.PORT_QNAME, Source.class,
				mode);

		assertNotNull("Dispatch Object is null", dispatch);

		return dispatch;
	}
}
