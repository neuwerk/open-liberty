/*
 * @(#) 1.3 autoFVT/src/annotations/webfault/multipleexceptions/test/MultipleExceptionsTest.java, WAS.websvcs.fvt, WASX.FVT 4/24/07 11:18:47 [7/11/07 13:11:49]
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
 * 08/14/2006  euzunca     LIDB3296.31.01     new file
 */
package annotations.webfault.multipleexceptions.test;

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

import annotations.support.WSDLEvaluator;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

/*
 * This is tooling tests to check that when multiple exceptions with the
 * same name are present, they are handled correctly.
 * 
 * notes:
 * 2007.01.04 bt - f0651.08 - failing on 394382 but I'll bet this gets permfailed or returned.
 * The WebFault annotation could be used to remove the failure, which was probably Sun's intent. 
 */
public class MultipleExceptionsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
   
	private static String buildDir = null;
	private static String testDir  = null;
	
	static {
		buildDir = AppConst.FVT_HOME + File.separator +
		          "build";
		testDir = "annotations" + File.separator +
			      "webfault" + File.separator +
			      "multipleexceptions";
	}
    
	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public MultipleExceptionsTest(String name) {
		super(name);
	}
	
	protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
	     System.out.println("Do not need suiteSetup since no application is installed");    
	 }

    /*
	 * This test method will verify that the generated wsdl file is valid in
	 * the case that the service uses multiple exceptions with the same name.
	 * 
	 * @testStrategy This test generates JAX-WS portable artifacts used in
	 * JAX-WS web services (from java to wsdl). Service uses two exception classes.
	 * The exception classes are in different packages and have different names.
	 * The name collision is introduced by @WebFault annotation. This test will 
	 * check that tooling this correctly and the generated wsdl file is valid.
	 * 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_j2w_CheckDuplicateFaults_byAnnotation() throws Exception {
		String wsdlFile = buildDir + "/work/" + testDir
				+ "/server/MultipleExceptionsImplService.wsdl";
		File f = new File(wsdlFile);
		boolean faultsHaveUniqueNames = true;
		
		if (f.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(f);
			XPath xpath = XPathFactory.newInstance().newXPath();

			String expression = "definitions/portType/operation";
			NodeList ops = (NodeList) xpath.evaluate(expression, document,
					XPathConstants.NODESET);
			int numOfOps = ops.getLength();
			while(numOfOps > 0){
				numOfOps--;
				String opName = WSDLEvaluator.getAttributeValue(ops.item(numOfOps), "name");
				expression = "definitions/portType/operation[@name='" +
					opName + "']/fault";
				NodeList faults = (NodeList) xpath.evaluate(expression, document,
						XPathConstants.NODESET);
				faultsHaveUniqueNames = WSDLEvaluator.checkUniqueness(faults, "name");
			}

			assertTrue("fault names are NOT unique in the wsdl..! (" +
					faultsHaveUniqueNames + ")" + WSDLEvaluator.out,
					faultsHaveUniqueNames);
		} else {
			fail("wsdl file not found: cannot check fault names..! " + wsdlFile);
		}
		System.out.println("fault names are unique in the wsdl: " + faultsHaveUniqueNames);
	}

    /*
	 * This test method will verify that the generated wsdl file is valid in
	 * the case that the service uses multiple exceptions with the same name.
	 * 
	 * @testStrategy This test generates JAX-WS portable artifacts used in
	 * JAX-WS web services (from java to wsdl). Service uses two exception classes.
	 * The exception classes are in different packages but the class names are the same.
	 * This test will check that the generated wsdl file reflects this correctly and
	 * is valid.
     * 
     * 4.24.07 394382 has been moved to sev5, which means is may be
     * implemented someday, but there is no outlook, so this test is
     * being disabled.  It can be reactivated if the function ever 
     * becomes available.
     * 
	 * 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test generates JAX-WS portable artifacts used in JAX-WS web services (from java to wsdl). Service uses two exception classes. The exception classes are in different packages but the class names are the same. This test will check that the generated wsdl file reflects this correctly and is valid.  4.24.07 394382 has been moved to sev5, which means is may be implemented someday, but there is no outlook, so this test is being disabled.  It can be reactivated if the function ever becomes available.  ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _test_j2w_CheckDuplicateFaults_byDefault() throws Exception {
		String wsdlFile = buildDir + "/work/" + testDir
				+ "/server1/MultipleExceptionsImplService.wsdl";
		File f = new File(wsdlFile);
		boolean faultsHaveUniqueNames = true;
		
		if (f.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.parse(f);
			XPath xpath = XPathFactory.newInstance().newXPath();

			String expression = "definitions/portType/operation";
			NodeList ops = (NodeList) xpath.evaluate(expression, document,
					XPathConstants.NODESET);
			int numOfOps = ops.getLength();
			while(numOfOps > 0){
				numOfOps--;
				String opName = WSDLEvaluator.getAttributeValue(ops.item(numOfOps), "name");
				expression = "definitions/portType/operation[@name='" +
					opName + "']/fault";
				NodeList faults = (NodeList) xpath.evaluate(expression, document,
						XPathConstants.NODESET);
				faultsHaveUniqueNames = WSDLEvaluator.checkUniqueness(faults, "name");
			}

			assertTrue("def. 394382 - fault names are NOT unique in the wsdl..! (" +
					faultsHaveUniqueNames + ")" + WSDLEvaluator.out,
					faultsHaveUniqueNames);
		} else {
			fail("wsdl file not found: cannot check fault names..! " + wsdlFile);
		}
		System.out.println("fault names are unique in the wsdl: " + faultsHaveUniqueNames);
	}
    
    public static junit.framework.Test suite() {
    	System.out.println(MultipleExceptionsTest.class.getName());
        return new TestSuite(MultipleExceptionsTest.class);
    }   
	
	public static void main(String[] args) {	
		TestRunner.run(suite());
	}
}
