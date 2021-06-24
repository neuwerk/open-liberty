/*
 * @(#) 1.7 autoFVT/src/annotations/webfault/exceptionnaming/test/WebFaultExceptionNamingTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 7/26/07 09:28:57 [8/8/12 06:55:03]
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
 * 08/02/2006  euzunca     LIDB3296.31.01     new file
 * 01/04/2007  btiffany                       clarified testStrategy docs.
 * 
 */

package annotations.webfault.exceptionnaming.test;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.WebFault;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

import annotations.support.WSDLEvaluator;

/*
 * These are tooling tests to verify that webfault annotations map properly
 * between java and wsdl.  
 *
 *
 */
public class WebFaultExceptionNamingTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

	private static String buildDir = null;
	private static String testDir  = null;
	static {
		buildDir = AppConst.FVT_HOME + File.separator +
		          "build";
		testDir = "annotations" + File.separator +
			      "webfault" + File.separator +
			      "exceptionnaming";
	}

	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public WebFaultExceptionNamingTest(String name) {
		super(name);
	}
	
	protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        System.out.println("Do not need suiteSetup since no application is installed");    
    }

	/*
	 * This test method will verify that the name of the global element declaration 
	 * for a mapped exception is the name of the Java exception.
	 * 
	 * @testStrategy This test generates JAX-WS portable artifacts used 
	 *               in JAX-WS web services. A Webfault annotation 
	 *               customizes the name of the exception, changing it from 
     *               "WebFaultExceptionNamingException" to "NamingException.
	 *               The .xsd file is checked for correct exception name. 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test generates JAX-WS portable artifacts used  in JAX-WS web services. A Webfault annotation customizes the name of the exception, changing it from \"WebFaultExceptionNamingException\" to \"NamingException\". The .xsd file is checked for correct exception name.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_j2w_GlobalDeclarionInXSD() throws Exception {
		String xsdFile = buildDir + "/work/" + testDir
				+ "/server1/WebFaultExceptionNamingService_schema1.xsd";
		File f = new File(xsdFile);
		URL url = null;
		try {
	        url = f.toURL();
	    } catch (MalformedURLException e) {
	    }		
		if (f.exists()) {
			NodeList nodes = null;
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(f);
			XPath xpath = XPathFactory.newInstance().newXPath();

			String expression = "schema" + "/element[@name='NamingException']";
			nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);		
			assertTrue("exception declaration not found in xsd..!", nodes.getLength()==1);
		}
		else{
			fail("xsd file not found: cannot check global declaration..!" +
					xsdFile);
		}		
		System.out.println("test_j2w_GlobalDeclarionInXSD(): " +
				"global declaration in xsd file found..!");
	}

	
    /*
	 * ----------------------------------------------------------------------
	 * 9/29: AS THE LATEST TOOLING BEHAVIOR, THIS TEST CASE DOES NOT SEEM TO BE
	 * VALID... INSTEAD, THE FAULT FIELD HAS THE CLASS NAME AS ITS NAME ATTR.
	 * ----------------------------------------------------------------------
	 * This test method will verify that a generated wsdl file contains a
	 * wsdl:fault element as specified in the corresponding Java exception
	 * class.
	 * 
	 * @testStrategy This test generates JAX-WS portable artifacts used in
	 * JAX-WS web services (from java to wsdl). Service implementation specifies
	 * that an exception will be thrown if any of the arguments passed to the
	 * server is a negative integer. This test will check that the generated
	 * wsdl file contains the wsdl:fault element as specified in the
	 * corresponding Java exception.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test generates JAX-WS portable artifacts used in JAX-WS web services (from java to wsdl). Service implementation specifies that an exception will be thrown if any of the arguments passed to the server is a negative integer. This test will check that the generated wsdl file contains the wsdl:fault element as specified in the corresponding Java exception.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _test_j2w_ExceptionInWSDL() throws Exception {
    	String wsdlFile = buildDir + "/work/" + testDir
			+ "/server1/WebFaultExceptionNamingService.wsdl";
		File f = new File(wsdlFile);
		
		if (f.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(f);
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			NamedNodeMap attributes = null;
			NodeList nodes = null;
			Node     fault = null;
			String   faultName = null;
			
			// get the fault element from the wsdl file
			String expression = "definitions"
					+ "/portType[@name='WebFaultExceptionNaming']"
					+ "/operation[@name='multiplyNumbers']" + "/fault";

			nodes = (NodeList) xpath.evaluate(expression, document,
					XPathConstants.NODESET);
			
			//get the "name" attribute, if the fault exists
			if(nodes.getLength() > 0){
				fault = nodes.item(0);
				faultName = WSDLEvaluator.getAttributeValue(fault, "name");
			}else{
				fail("fault element does NOT exist in the wsdl file..!");
			}			
			 
			assertTrue("fault name is WRONG in the wsdl file: (" + 
					faultName + ")", 
					faultName.equals("WebFaultExceptionNamingException"));

			System.out.println("test_j2w_ExceptionInWSDL(): " +
					"name: " + faultName);
		}
	}
	

    /*
	 * This test method will verify that a generated Java exception class file
	 * contains a WebFault annotation as specified in the corresponding wsdl
	 * file.
	 * 
	 * @testStrategy his test generates JAX-WS portable artifacts used in JAX-WS
	 * web services (from wsdl to java). In the wsdl file, there exists a
	 * wsdl:fault element. This test will check that the generated Java
	 * exception class contains the WebFault annotation as specified in the
	 * corresponding wsdl file. This test case also verifies that the exception
	 * class is generated correctly.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="his test generates JAX-WS portable artifacts used in JAX-WS web services (from wsdl to java). In the wsdl file, there exists a wsdl:fault element. This test will check that the generated Java exception class contains the WebFault annotation as specified in the corresponding wsdl file. This test case also verifies that the exception class is generated correctly.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_w2j_ExceptionInJava() throws Exception {
    	Class cls;
    	URL u = new File("C:/eclipse/WautoFVT/build/classes").toURL();
    	URLClassLoader ucu = new URLClassLoader(new URL[] {u});
    	try{
    		cls = ucu.loadClass("annotations.webfault.exceptionnaming.server1.WebFaultExceptionNamingException");
    	}catch(ClassNotFoundException e){
    		fail("exception class <NamingException> NOT found..!");
    		return;
    	}
    	Annotation [] an = cls.getAnnotations();
    	
    	int ix = 0;
    	boolean faultFound = false;
    	while(ix < an.length){
    		if(an[ix].annotationType().getSimpleName().equals("WebFault")){
    			faultFound = true;
    			break;
    		}    			
    		ix++;
    	}
    	if(!faultFound){
    		fail("WebFault annotation is missing..!");
    	}
    	else{
    		assertTrue("WebFault annotation has an INCORRECT name..!",
    				((WebFault)an[ix]).name().equals("NamingException"));
    	}

		System.out.println("test_w2j_ExceptionInJava(): " +
				"WebFault annotation found, " +
				" type: " + an[0].annotationType().getSimpleName() +
				", name: " + ((WebFault)an[0]).name());
	}

    /*
	 * This test method will verify that a generated wsdl file contains the
	 * correct default values for the message/fault elements based on the 
	 * exception class that provides the @WebFault annotation with the 
	 * properties omitted.
	 * 
	 * @testStrategy In a java2wsdl scenario, fault element of the corresponding
	 * operation (method) in the wsdl file should have the correct values as
	 * specified by the spec (section 3.7). This test case checks the name and 
	 * message attributes of the fault element for a given method in the wsdl file.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="In a java2wsdl scenario, fault element of the corresponding operation (method) in the wsdl file should have the correct values as specified by the spec (section 3.7). This test case checks the name and message attributes of the fault element for a given method in the wsdl file.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_w2j_DefaultValuesInWsdl() throws Exception {
		String wsdlFile = buildDir + "/work/" + testDir
				+ "/server2/WebFaultDefaultsService.wsdl";
		File f = new File(wsdlFile);

		if (f.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(new File(wsdlFile));
			XPath xpath = XPathFactory.newInstance().newXPath();

			NamedNodeMap attributes = null;
			NodeList nodes = null;
			Node fault = null;
			String   faultName = null,
				     faultMsg = null;

			// get the fault element from the wsdl file
			String expression = "definitions"
					+ "/portType[@name='WebFaultDefaults']"
					+ "/operation[@name='multiplyNumbers']" + "/fault";

			nodes = (NodeList) xpath.evaluate(expression, document,
					XPathConstants.NODESET);

			// get the "name" attribute, if the fault exists
			if (nodes.getLength() > 0) {
				fault = nodes.item(0);
				faultName = WSDLEvaluator.getAttributeValue(fault, "name");
				faultMsg  = WSDLEvaluator.getAttributeValue(fault, "message");
			} else {
				fail("fault element does NOT exist in the wsdl file..!");
			}

			assertTrue("fault name is WRONG in the wsdl file: ", 
					faultName.equals("WebFaultDefaultsException") &&
					faultMsg.equals("tns:WebFaultDefaultsException"));

			System.out.println("test_j2w_ExceptionInWSDL(): " + "name: "
					+ faultName);
		}
	}
        
    public static junit.framework.Test suite() {
		System.out.println(WebFaultExceptionNamingTest.class.getName());
		return new TestSuite(WebFaultExceptionNamingTest.class);
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}
}
