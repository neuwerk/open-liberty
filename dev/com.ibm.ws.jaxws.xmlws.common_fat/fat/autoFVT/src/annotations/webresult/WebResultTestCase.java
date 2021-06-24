/*
 * autoFVT/src/annotations/webresult/WebResultTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 05/30/2006  btiffany    LIDB3296.31.01     new file
 * 08/16/2007   "          459641             fix constructor for framework
 * 09/26/207   "				   since framework no longer likes no-args constructor, run setup during test phase
 *
 */

package annotations.webresult;
import junit.framework.*;

import javax.wsdl.*;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.namespace.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

import annotations.support.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;



import junit.textui.TestRunner;

/**
 * Checks that @WebResult annotation maps correctly to wsdl from java,
 * and vice versa.
 *
 *
 *  The file WebResultTestImpl.java has some trivial methods that mutate the
 *         webResult anno. as follows:
 *
 *         echo is a default method with no parameter settings
 *         echo111 has name changed to echo111InvocationResult, this should show up
 *            as the element name within the types section of the wsdl.
 *
 *         echo222 has the namespace changed to echo222ns.
 *         So, in the wsdl, the namespace used to describe this parmater should be echo222ns.
 *
 *         echo333 contains header=true, so this response should have a header designation
 *         within the bindings section of the wsdl.  (This actually generates illegal wsdl,
 *         ref defect 386472, but the header part is there.)
 *
 *         echo444 changes both the partname and the name, per jsr224 2.3.1, and/or jsr181 4.5.1
 *         the partname should show up as wsdl:part if the saop sytle is document bare or rpc.
 *
 *
 *  5.4.2007 disable tests for cancelled / deferred defects 386472, 393806
 *
 * @author btiffany
 *
 */
public class WebResultTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        private static String workDir = null;
        ImplementationAdapter imp = Support.getImplementationAdapter("ibm",workDir);
        private static boolean setupRun = false;        // for junit

        static{
                // set the working directory to be <work area>/<classname>
                workDir = WebResultTestCase.class.getName();
                int p = workDir.lastIndexOf('.');
                workDir = Support.getFvtBaseDir()+"/build/work/"+workDir.substring(p+1)+"/fromjava";
        }

        // framework reqmt.
        public WebResultTestCase( String s){
            super(s);
        }
        public WebResultTestCase( ){
            super("nothing");
        }
        
        protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
            System.out.println("Do not need suiteSetup since no application is installed");    
        }

        public static void main(String[] args) throws Exception {
                if (true){              TestRunner.run(suite());}
                else{
                        WebResultTestCase t = new WebResultTestCase();
                        //t.testJava2Wsdl3();
                        /*
                t.imp.setWorkingDir(t.imp.getWorkingDir()+"/../fromwsdl");
                t.imp.cleanWorkingDir();
                OS.copy(Support.getFvtBaseDir()+"/src/annotations/webresult/server/WebResultWsdlTest.wsdl",
                                t.imp.getWorkingDir());
                t.imp.w2j("",t.imp.getWorkingDir()+"/WebResultWsdlTest.wsdl");
                */
                        t.testJava2Wsdl4();
                }
        }


    /**
     * junit needs this.
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
             return new TestSuite(WebResultTestCase.class);
    }

    // junit will run this before every test
    public void setUp(){
        // if standalone, junit runs this
        // in harness, annotations ant script will run this explicitly with Run_Setup set
        //if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
        if (setupRun) return;   // we only want to run this once per suite
        setupRun = true;
        // imp.cleanWorkingDir();
		System.out.println("running setup");
        imp.j2w("","src/annotations/webresult/server/WebResultTestImpl.java",
    			"annotations.webresult.server.WebResultTestImpl");
        imp.j2w("","src/annotations/webresult/server/WRonVoid.java",
                "annotations.webresult.server.WRonVoid");
    	// now go the other way and produce an SEI
    	imp.setWorkingDir(imp.getWorkingDir()+"/../fromwsdl");
    	//imp.cleanWorkingDir();    	
    	imp.w2j("",Support.getFvtBaseDir()+"/src/annotations/webresult/server/WebResultTestImplService.wsdl");    	
    }

    /**
     * @testStrategy check that name parameter has passed to wsdl
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJava2Wsdl1() throws Exception {
    	// inspect wsdl using xpath
    	// http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/package-summary.html
    	imp.setWorkingDir(imp.getWorkingDir()+"/../fromjava");
    	
    	String infil = imp.getWorkingDir()+"/WebResultTestImplService_schema1.xsd";
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(infil));		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		
		// look for element name, should match annnotation parameter
		String expression = "schema/complexType[@name='echo111Response']/sequence/element[@name='echo111InvocationResult']";		
		
		nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);		
		
		assertTrue("element name echo111InvocationResult not found in wsdl", nodes.getLength()==1);
		System.out.println("xpath found: "+nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());		
    	
    }

    /**
     * @testStrategy check that namespace parameter has passed to wsdl
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJava2Wsdl2() throws Exception {
    	imp.setWorkingDir(imp.getWorkingDir()+"/../fromjava");
    	// inspect wsdl using xpath
    	// http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/package-summary.html
    	
    	String infil = imp.getWorkingDir()+"/WebResultTestImplService_schema2.xsd";
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(infil));		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		
		// look for element name, should match annnotation parameter
		String expression = "schema[@targetNamespace='echo222ns']";		
		
		nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);		
		
		assertTrue("namespace echo222ns not found in wsdl", nodes.getLength()==1);
		System.out.println("xpath found: "+nodes.item(0).getAttributes().getNamedItem("targetNamespace").getNodeValue());		
    	
    }

    /**
     * @testStrategy check that header parameter has passed to wsdl
     * deliberately renamed to disable it for now. 8.29.06
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void ___testJava2Wsdl3() throws Exception {
    	imp.setWorkingDir(imp.getWorkingDir()+"/../fromjava");
    	// inspect wsdl using xpath
    	// http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/package-summary.html
    	
    	String infil = imp.getWorkingDir()+"/WebResultTestImplService.wsdl";
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(infil));		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = null;
		
		// look for element name, should match annnotation parameter
		//"definitions/binding/operation[@name='echoObject7']/input/header[@message='tns:echoObject7']";
		String expression = "definitions/binding/operation[@name='echo333']/output/header[@message='tns:echo333Response']";		
		
		nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);		
		
		assertTrue("header message not found in wsdl", nodes.getLength()==1);
		System.out.println("xpath found: "+nodes.item(0).getAttributes().getNamedItem("message").getNodeValue());		
    	
    }

    /**
     * @testStrategy checks that partName trumps name when both are specified.
     * looks like this is busted.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJava2Wsdl4() throws Exception {
    	imp.setWorkingDir(imp.getWorkingDir()+"/../fromjava");    	
        String infil = imp.getWorkingDir()+"/WebResultTestImplService.wsdl";

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new File(infil));
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = null;

        // look for element name, should match annnotation parameter
        //"definitions/binding/operation[@name='echoObject7']/input/header[@message='tns:echoObject7']";
        String expression = "definitions/message/part[@name='echo444result']";

        nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

        assertTrue("partname echo444result not found in wsdl", nodes.getLength()==1);
        System.out.println("xpath found: "+nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());
    }

    /**
     * @testStrategy starting from wsdl,
     * check that all generated methods have @webresult, and params are correct
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWsdl2Java1() throws Exception {
    	imp.setWorkingDir(imp.getWorkingDir()+"/../fromwsdl");
    	AnnotationHelper anh = new AnnotationHelper("annotations.webresult.server.WebResultTestImpl", imp.getWorkingDir());
    	Annotation a1 = anh.getMethodAnnotation("echo111","WebResult");
    	Annotation a2 = anh.getMethodAnnotation("echo222","WebResult");    	
       	//Annotation a3 = anh.getMethodAnnotation("echo333","WebResult");    	
    	Annotation a4 = anh.getMethodAnnotation("echo444","WebResult");
    	
    	assertNotNull("annotation a1 missing ",a1);
    	assertNotNull("annotation a2 missing ",a2);    	
    	//assertNotNull("annotation a3 missing ",a3);
    	assertNotNull("annotation a4 missing ",a4);
    	
        // did we change the name ok?
    	String expect1="echo111InvocationResult";
    	String a1s = anh.getMethodElement("echo111","WebResult","name");
    	System.out.println(a1s);
    	
    	// did we change the namespeace ok?
    	String expect2 = "echo222ns";
    	String a2s = anh.getMethodElement("echo222","WebResult","targetNamespace");
    	System.out.println(a2s);
    	
        /*
    	// did we change the header parm ok?
    	String expect3 = "true";
    	String a3s = anh.getMethodElement("echo333","WebResult","header");
    	System.out.println(a3s);
        */
    	

    	// partname - this is only used if rpc style, or document bare.
        String expect4 = "echo444result";
        String a4s = anh.getMethodElement("echo444","WebResult","partName");
        System.out.println(a4s);



    	assertTrue("unexpected @webresult element value " +a1s, a1s.compareTo(expect1)==0 );
    	assertTrue("unexpected @webresult element value " +a2s, a2s.compareTo(expect2)==0 );    	
        assertTrue("unexpected @webresult element value " +a4s, a4s.compareTo(expect4)==0 );

        // when we put this test back in, we need to regenerate the wsdl file used to test.
        // I could not tweak it to pass both the header test and the partname test. 8.29.06
        // assertTrue("unexpected @webresult element value " +a3s, a3s.compareTo(expect3)==0 );
        //fail("defect 386472,  header param generates invalid wsdl");
    }

    /**
     * @testStrategy - check that @Webresult on void is rejected by tooling.
     * (test suggested by J. B.)
     * removed 5.4.2007 as def 393806 is deferred.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- check that @Webresult on void is rejected by tooling. (test suggested by J. B.) removed 5.4.2007 as def 393806 is deferred.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testWRonVoid() throws Exception{
        imp.setWorkingDir(imp.getWorkingDir()+"/../fromjava");
        String infil = imp.getWorkingDir()+"/WRonVoidService.wsdl";
        File f = new File(infil);
        assertFalse("defect 393806 - shouldn't generate wsdl for @webResult on void",
                    f.exists());

    }
    	
    	
    	


}
