/** 
 * @(#) 1.1 autoFVT/src/jaxws/proxy/javatypes_jar/wsfvt/test/TypesFromJavaJarTest.java, WAS.websvcs.fvt, WASX.FVT 1/19/07 11:28:33 [7/11/07 13:16:21]
 *
 * IBM Confidential OCO Source Material
 * (C) COPYRIGHT International Business Machines Corp. 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        Author       Feature/Defect          Description
 * 01/19/07    sedov        D415799                 New File
 **/

package jaxws.proxy.javatypes_jar.wsfvt.test;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import jaxws.proxy.common.Constants;
import jaxws.proxy.javatypesjar.wsfvt.javatypesjar.CustomComplexType;
import jaxws.proxy.javatypesjar.wsfvt.javatypesjar.JavaTypesJarPort;
import jaxws.proxy.javatypesjar.wsfvt.javatypesjar.ProxyJavaTypesJarService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Tests for having wsgen beans in a separate jar
 */
public class TypesFromJavaJarTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	static final String ENDPOINT = 	"http://" + Constants.SERVER + "/jwpr.jar/services/ProxyJavaTypesJarService";
	
	public TypesFromJavaJarTest(String str) {
		super(str);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(TypesFromJavaJarTest.class);
		return suite;
	}

	public void setUp() {
		System.out.println("==================== " + getName());
	}

	/**
	 * @testStrategy Send a simple int
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a simple int",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypesJar_java_int() {
		JavaTypesJarPort port = getPort();

		int expected = 41;
		int actual = 0;

		actual = port.pingInt(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send a simple char
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send a simple char",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypesJar_java_string() {
		JavaTypesJarPort port = getPort();

		String expected = getName();
		String actual = null;

		actual = port.pingString(expected);

		assertEquals("Unexpected response", expected, actual);
	}

	/**
	 * @testStrategy Send an unannotated java bean
	 */		
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Send an unannotated java bean",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testTypesJar_pojo() throws Exception {
		JavaTypesJarPort port = getPort();

		CustomComplexType expected = new CustomComplexType();
		CustomComplexType actual = null;

		expected.setFirstName("Joe");
		expected.setLastName("Bob");
		expected.setId(1234);
		
		actual = port.pingJavaBean(expected);

		assertEquals("Unexpected response", expected.getFirstName(), actual.getFirstName());
		assertEquals("Unexpected response", expected.getLastName(), actual.getLastName());
		assertEquals("Unexpected response", expected.getId(), actual.getId());
	}
	
	/**
	 * Utility method to obtain a proxy instance
	 * @return
	 */
	private JavaTypesJarPort getPort() {
		ProxyJavaTypesJarService service = new ProxyJavaTypesJarService();
		JavaTypesJarPort port = service.getJavaTypesJarPort();
		assertNotNull("Port is null", port);

		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ENDPOINT);

		System.out.println("Endpoint=" + ENDPOINT);
		
		return port;
	}
}
