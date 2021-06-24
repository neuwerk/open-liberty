//
// @(#) 1.1 WautoFVT/src/jaxws/proxy/wsfvt/test/ProxyObjectTest.java, WAS.websvcs.fvt, WSFP.WFVT 11/09/06 14:50:56 [11/09/06 14:56:54]
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
// 11/09/06 sedov       404343          New File to verify 393877
//

package jaxws.proxy.doclit.wsfvt.test;

import jaxws.proxy.doclit.wsfvt.doclitwrapped.DocLitWrappedProxy;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ProxyDocLitWrappedService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Proxies use a generic java reflection Proxy API which is capable of receiving
 * any method invocation. This suite will verify that operations derrived from
 * Object are not confused with port operations
 */
public class ProxyObjectTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	public ProxyObjectTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ProxyObjectTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testObject_equals() {
		DocLitWrappedProxy port = getPort();
		boolean b = port.equals(port);

		System.out.println(getName() + "=" + b);
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testObject_hashCode() {
		DocLitWrappedProxy port = getPort();
		int b = port.hashCode();

		System.out.println(getName() + "=" + b);
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testObject_toString() {
		DocLitWrappedProxy port = getPort();
		String b = port.toString();

		System.out.println(getName() + "=" + b);
	}

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testObject_getClass() {
		DocLitWrappedProxy port = getPort();
		Class b = port.getClass();

		System.out.println(getName() + "=" + b);
	}

	/**
	 * Auxiliary method used for obtaining the service and setting any
	 * properties on it
	 * 
	 * @return
	 */
	private DocLitWrappedProxy getPort() {
		ProxyDocLitWrappedService service = new ProxyDocLitWrappedService();
		DocLitWrappedProxy port = service.getProxyDocLitWrappedPort();
		assertNotNull("Port is null", port);

		return port;
	}
}
