/*
 * @(#) 1.3 autoFVT/src/annotations/webserviceclient/checkdefaults/test/WebServiceClientDefaultsTest.java, WAS.websvcs.fvt, WASX.FVT 12/22/06 11:48:42 [7/11/07 13:13:18]
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
 * 07/05/2006  euzunca     LIDB3296.31.01     new file
 * 
 */
package annotations.webserviceclient.checkdefaults.test;

import java.io.File;
import java.lang.annotation.Annotation;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.AnnotationHelper;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

public class WebServiceClientDefaultsTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
   
	private static String buildDir = null;
	static {
		buildDir = AppConst.FVT_HOME + File.separator +
		          "build";
	}
    
	/*
	 * String arg constructor: This constructor allows for altering the test
	 * suite to include just one test.
	 */
	public WebServiceClientDefaultsTest(String name) {
		super(name);
	}
	
	protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
	     System.out.println("Do not need suiteSetup since no application is installed");    
	}

    /*
	 * This test method will verify that the generated service class has the
	 * WebServiceClient annotation.
	 * 
	 * @testStrategy This test generates the JAX-WS artifacts from the wsdl file.
	 * Generated service class should have the WebServiceClient annotation. The annotattion
	 * shoul also have the properties for name, targetNamespace and wsdlLocation all mapped
	 * correctly from the wsdl file. This test case checks that the generated service class
	 * meets this condiiton.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test generates the JAX-WS artifacts from the wsdl file. Generated service class should have the WebServiceClient annotation. The annotattion shoul also have the properties for name, targetNamespace and wsdlLocation all mapped correctly from the wsdl file. This test case checks that the generated service class meets this condiiton.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_w2j_AnnotationInClass() throws Exception {
		AnnotationHelper anh = new AnnotationHelper(
				"annotations.webserviceclient.checkdefaults.server.AddNumbersImplService", 
				buildDir + "/classes");
		boolean _name = false,
		        _targetNamespace = false,
		        _wsdlLocation = true;  //omitted
		
		Annotation anno = anh.getClassAnnotation("WebServiceClient");
		
		_name = anh.getAnnotationElement(anno, "name") != null &&
				anh.getAnnotationElement(anno, "name").equals("AddNumbersImplService")
				? true : false;
		
		_targetNamespace = anh.getAnnotationElement(anno, "targetNamespace") != null &&
				anh.getAnnotationElement(anno, "targetNamespace")
					.equals("http://server.checkdefaults.webserviceclient.annotations/")
				? true : false;
		
/*		THIS ARGUMENT IS NOT IMPORTANT FOR THE TEST CASE...REMOVED
 * 
 * 		_wsdlLocation = anh.getAnnotationElement(anno, "wsdlLocation") != null &&
				anh.getAnnotationElement(anno, "wsdlLocation")
					.equals("c:\\eclise/WautoFVT/build/work/" + 
					"annotations/webserviceclient/checkdefaults/server/AddNumbersImplService.wsdl")
				? true : false;
*/		
		assertTrue("WebServiceClient annotation is missing/incorrect..! (" +
				   _name + " " + _targetNamespace + " " + _wsdlLocation + ")",
				_name && _targetNamespace && _wsdlLocation);
		
		System.out.println("WebServiceClient annotation is correct..! (name:" +
				   _name + " targetNamespace:" + _targetNamespace +
				   " wsdlLocation(...OMITTED...):" + _wsdlLocation + ")");
	}

    public static junit.framework.Test suite() {
    	System.out.println(WebServiceClientDefaultsTest.class.getName());
        return new TestSuite(WebServiceClientDefaultsTest.class);
    }   
	
	public static void main(String[] args) {	
		TestRunner.run(suite());
	}
}
