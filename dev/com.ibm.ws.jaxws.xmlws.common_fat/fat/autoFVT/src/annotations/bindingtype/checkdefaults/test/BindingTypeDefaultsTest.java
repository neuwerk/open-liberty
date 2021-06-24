/*
 * @(#) 1.3 autoFVT/src/annotations/bindingtype/checkdefaults/test/BindingTypeDefaultsTest.java, WAS.websvcs.fvt, WASX.FVT 1/8/07 08:53:08 [7/11/07 13:09:30]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 07/10/2006  euzunca     LIDB3296.31.01     new file
 * 
 */
package annotations.bindingtype.checkdefaults.test;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


//import annotations.bindingtype.checkdefaults.client.AddNumbersClient;

import annotations.bindingtype.checkdefaults.client.AddNumbersClient11;
import annotations.bindingtype.checkdefaults.client.AddNumbersClient12;

import com.ibm.ws.wsfvt.build.tools.AppConst;


/*
 * notes:
 * 01.04.07 f0651.08 - looks good except for known problem 411303
 */
public class BindingTypeDefaultsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static String buildDir = null;

	private static String testDir = null;
	static {
		buildDir = AppConst.FVT_HOME + File.separator + "build";
		testDir = "annotations" + File.separator + "bindingtype" + File.separator
				+ "checkdefaults";		
	}

	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public BindingTypeDefaultsTest(String name) {
		super(name);
	}

	/*
	 * This test method will verify that the published endpoint implementation
	 * of a class contains the correct binding as specified by the BindingType annotation.
	 * 
	 * @testStrategy This test case generates the JAX-WS artifacts from the 
	 * java file. The service endpoint implementation (the service class) 
	 * does not specify a binding type so the java2wsdl tool must set the 
	 * binding type to default, which is Soap11Http. This test case checks 
	 * that the generated wsdl file has the correct binding type.
     * 
     * We're looking at the port address.  Soap11 should be declared
     * soap:address location=.... Soap12 should be soap12:address....
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test case generates the JAX-WS artifacts from the  java file. The service endpoint implementation (the service class) does not specify a binding type so the java2wsdl tool must set the binding type to default, which is Soap11Http. This test case checks that the generated wsdl file has the correct binding type.  We're looking at the port address.  Soap11 should be declared soap:address location=.... Soap12 should be soap12:address....",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_j2w_BindingType_Soap11Http_Default() throws Exception {
		String wsdlFile = buildDir + "/work/" + testDir
				+ "/serverDefault/gen/AddNumbersImplService.wsdl";
		File f = new File(wsdlFile);
		boolean _fileExists = f.exists();
		NodeList nodes = null;
        		
		if (f.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(new File(wsdlFile));
			XPath xpath = XPathFactory.newInstance().newXPath();

			String expression = "definitions"
					+ "/service[@name='AddNumbersImplService']"
					+ "/port[@name='AddNumbersImplPort']" + "/*";

			nodes = (NodeList) xpath.evaluate(expression, document,
					XPathConstants.NODESET);
		}
		
        
		assertTrue("soap binding not found in wsdl: " + "(" + _fileExists + ")",
				_fileExists &&
				(nodes.getLength() > 0) && 
				nodes.item(0).getNodeName().equals("soap:address"));
				                            
		System.out.println("xpath found: " + nodes.getLength() + " "
				+ nodes.item(0).getNodeName());
	}

	/*
	 * This test method will verify that the published endpoint implementation
	 * of a class contains the correct binding as specified by the BindingType annotation.
	 * 
	 * @testStrategy This test case generates the JAX-WS artifacts from the 
	 * java file. The service endpoint implementation (the service class) 
	 * specifies Soap11Http binding type and the java2wsdl tool must set the 
	 * binding type accordingly. This test case checks that the generated
	 * wsdl file has the correct binding type.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test case generates the JAX-WS artifacts from the  java file. The service endpoint implementation (the service class) specifies Soap11Http binding type and the java2wsdl tool must set the binding type accordingly. This test case checks that the generated wsdl file has the correct binding type.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_j2w_BindingType_Soap11Http() throws Exception {
		String wsdlFile = buildDir + "/work/" + testDir
				+ "/server11/gen/AddNumbersImplService.wsdl";
		File f = new File(wsdlFile);
		boolean _fileExists = f.exists();
		NodeList nodes = null;
		
		if (f.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(new File(wsdlFile));
			XPath xpath = XPathFactory.newInstance().newXPath();

			String expression = "definitions"
					+ "/service[@name='AddNumbersImplService']"
					+ "/port[@name='AddNumbersImplPort']" + "/*";
			
			nodes = (NodeList) xpath.evaluate(expression, document,
					XPathConstants.NODESET);
		}
		
		assertTrue("soap binding not found in wsdl: " + "(" + _fileExists + ")",
				_fileExists &&
				(nodes.getLength() > 0) && 
				nodes.item(0).getNodeName().equals("soap:address"));
				                            
		System.out.println("xpath found: " + nodes.getLength() + " "
				+ nodes.item(0).getNodeName());
	}

	/*
	 * This test method will verify that the published endpoint implementation
	 * of a class contains the correct binding as specified by the BindingType annotation.
	 * 
	 * @testStrategy This test case generates the JAX-WS artifacts from the 
	 * java file. The service endpoint implementation (the service class) 
	 * specifies Soap12Http binding type and the java2wsdl tool must set the 
	 * binding type accordingly. This test case checks that the generated
	 * wsdl file has the correct binding type.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test case generates the JAX-WS artifacts from the  java file. The service endpoint implementation (the service class) specifies Soap12Http binding type and the java2wsdl tool must set the binding type accordingly. This test case checks that the generated wsdl file has the correct binding type.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_j2w_BindingType_Soap12Http() throws Exception {
		String wsdlFile = buildDir + "/work/" + testDir
				+ "/server12/gen/AddNumbersImplService.wsdl";
		File f = new File(wsdlFile);
		boolean _fileExists = f.exists();
		NodeList nodes = null;
		
		if (f.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(new File(wsdlFile));
			XPath xpath = XPathFactory.newInstance().newXPath();

			String expression = "definitions"
					+ "/service[@name='AddNumbersImplService']"
					+ "/port[@name='AddNumbersImplPort']" + "/*";

			nodes = (NodeList) xpath.evaluate(expression, document,
					XPathConstants.NODESET);
		}
		
		assertTrue("soap binding not found in wsdl: " + "(" + _fileExists + ")",
				_fileExists &&
				(nodes.getLength() > 0) && 
				nodes.item(0).getNodeName().equals("soap12:address"));
				                            
		System.out.println("xpath found: " + nodes.getLength() + " "
				+ nodes.item(0).getNodeName());
	}

	/*
	 * This test method will verify that a service annotated with Soap11Http
	 * binding works correctly.
	 * 
	 * @testStrategy This test case invokes a service that is annotated with 
	 * Soap11Http binding and verifies that the service works correctly.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test case invokes a service that is annotated with  Soap11Http binding and verifies that the service works correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void test_runtime_BindingType_Soap11Http() throws Exception {
    	int result = 0;
    	result = AddNumbersClient11.addNumbers(10, 20);
 		assertTrue("Unexpected result..! (" + result + ")", (result == 30));
 		System.out.println("test_runtime_NoWsdl(): " + 
 				"service responded with the correct answer...(" + result + ")");
	}
    
	/*
	 * This test method will verify that a service annotated with Soap12Http
	 * binding works correctly.
	 * 
	 * @testStrategy This test case invokes a service that is annotated with 
	 * Soap12Http binding and verifies that the service works correctly.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test case invokes a service that is annotated with  Soap12Http binding and verifies that the service works correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void test_runtime_BindingType_Soap12Http() throws Exception {
    	int result = 0;
    	result = AddNumbersClient12.addNumbers(10, 20);
 		assertTrue("Def 411303? - Unexpected result..! (" + result + ")", (result == 30));
 		System.out.println("test_runtime_NoWsdl(): " + 
 				"service responded with the correct answer...(" + result + ")");
	}
    
	public static junit.framework.Test suite() {
		System.out.println(BindingTypeDefaultsTest.class.getName());
		return new TestSuite(BindingTypeDefaultsTest.class);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}
}
