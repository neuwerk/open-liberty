/*
 *autoFVT/src/annotations/webparam/WebParamTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 *
 */
package annotations.webparam;
import junit.framework.*;

import javax.wsdl.*;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.namespace.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;
import junit.textui.TestRunner;
import javax.xml.xpath.*;

import org.xml.sax.*;
import org.w3c.dom.*;

import annotations.support.*;

import javax.xml.parsers.*;

/**
 * Checks that @WebParam shapes wsdl properly.
 * Checks that generated java is annotated with @WebParam
 *
 * @author btiffany
 *
 */
public class WebParamTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        private static String workDir = null;
        ImplementationAdapter imp = Support.getImplementationAdapter("ibm",workDir);
        private static boolean setupRun = false;        // for junit
		
		
		// framework reqmt.
	    public static junit.framework.Test suite() {	     
	       return new TestSuite(WebParamTestCase.class);
	    }

        // framework reqmt.
		public WebParamTestCase(){
			super("nothing");
		}
		
		// frameowrk requirement
        public WebParamTestCase( String s){
            super(s);
        }

        static{
                // set the working directory to be <work area>/<classname>
                workDir = Support.getFvtBaseDir()+"/build/work/WebParamTestCase";
        }

        public static void main(String[] args) throws Exception {
                if (true){
                        TestRunner.run(suite());
                } else {
                        WebParamTestCase t = new WebParamTestCase("x");
                        //t.setUp();
                        t.testGeneratedWsdl();
                        //t.testGeneratedJava();
                }
        }




    // junit will run this before every test
    public void setUp(){
        // if standalone, junit runs this
        // in harness, annotations ant script will run this explicitly with Run_Setup set
        if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
        if (setupRun) return;   // we only want to run this once per suite
        setupRun = true;
        System.out.println("***************WebParamTestCase.setup is running ************");
//      imp.cleanWorkingDir();
        OS.mkdir(imp.getWorkingDir()+"/w2j");
        OS.mkdir(imp.getWorkingDir()+"/w2j/../j2w");
        imp.setWorkingDir(imp.getWorkingDir()+"/j2w");
//      imp.j2w("","src/annotations/webparam/testdata/WebParamGeneralChecks.java",
//                      "annotations.webparam.testdata.WebParamGeneralChecks");

        imp.setWorkingDir(imp.getWorkingDir()+"/../w2j");
//      imp.w2j("",imp.getWorkingDir()+"/../j2w"+"/WebParamGeneralChecksService.wsdl");

    }

    /**
     * @testStrategy check that name, targetnamspace, and header annotation params make it into wsdl.
     * inout can't be tested readily without extensive analysis of the wsdl, so it's done in runtime.
     * partname is also covered in runtime
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testGeneratedWsdl() throws Exception {
        System.out.println("******************* testGeneratedWsdl() is running *******************");
        // wsdl was generated in setup()
        imp.setWorkingDir(workDir+"/j2w");

        // inspect wsdl using xpath
        // http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/package-summary.html
        String infil = imp.getWorkingDir()+"/WebParamGeneralChecksService_schema1.xsd";
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = builder.parse(new File(infil));
                XPath xpath = XPathFactory.newInstance().newXPath();
                NodeList nodes = null;

                // look for obj2 param name we defined in the webparam annotation on echoObject2 method
                String expression = "schema/complexType[@name='echoObject2']/sequence/element[@name='obj2']";

                nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

                assertTrue("obj2 param not found in wsdl", nodes.getLength()==1);
                System.out.println("xpath found: "+nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());


                // echoObject6 should have it's namespace changed to foo.myns
                // this test will probably break if the wsdl shape changes from tool to tool
                // first, is the ns defined

                // for some reason, xpath won't process this for namespaces: expression = "schema[@xmlns:ns1='foo.myns']";
                expression = "schema";

                nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
                // but we can dig it out this way...
                String ns = nodes.item(0).getAttributes().getNamedItem("xmlns:ns1").getNodeValue();
                System.out.println("xpath found: "+ns);
                assertTrue("foo.myns namespace not declared in wsdl", ns.compareTo("foo.myns")==0);

                // second, is it referenced.
                //expression = "schema/complexType[@name='echoObject6']/sequence/element";
                expression = "schema/complexType[@name='echoObject6']/sequence/element[@ref='ns1:obj']";
                nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

                // if the expression found anything, we're done, but print it just to be paranoid.
                ns = nodes.item(0).getAttributes().getNamedItem("ref").getNodeValue();
                System.out.println("xpath found: "+ns);
                assertTrue("foo.myns namespace not referenced in wsdl", ns.compareTo("ns1:obj")==0);

                // check for header
                infil = imp.getWorkingDir()+"/WebParamGeneralChecksService.wsdl";
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                document = builder.parse(new File(infil));
                xpath = XPathFactory.newInstance().newXPath();
                //expression = "definitions/binding/operation[@name='echoObject7']/input/soap:header[@message='tns:echoObject7']";
                // see how namespace prefix of soap: has to be omitted...
                expression = "definitions/binding/operation[@name='echoObject7']/input/header[@message='tns:echoObject7']";
                nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

                //              debug
                /*
                Node n=null;
                System.out.println("found :"+ nodes.getLength()+" nodes");
                for(int i=0;  i < nodes.getLength(); i++  ){
                        n = nodes.item(i);
                        //System.out.println(n.getAttributes().getNamedItem ("name").getNodeValue() +" "+n.getNodeName());
                        System.out.println(n.getNodeName());

                }
                */

                // if the expression found anything, we're done, but print it just to be paranoid.
                ns = nodes.item(0).getAttributes().getNamedItem("message").getNodeValue();
                System.out.println("xpath found: "+ns);
                assertTrue("soap header directive not in wsdl", ns.compareTo("tns:echoObject7")==0);

        // now check for partName
        expression = "definitions/message[@name='echoObject8']/part[@name='fobj']";
        nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        assertTrue("partName not passed to wsdl", nodes.getLength() == 1);
    }


    /**
     *
     * @testStrategy generate java from wsdl and verify that every parameter has the @WebParam annotation, as
     * required by jsr224 2.3.1
     * Also verify that non-default params have been passed in from wsdl.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testGeneratedJava() throws Exception {
        System.out.println("******************* testGeneratedJava() is running *******************");
        String sbuf = null;
        imp.setWorkingDir(workDir+"/w2j");
        // get rid of file that confuses classloader in eclipse only
        // we want to load the generated one out of build/work/... NOT this one.
        //OS.delete(Support.getFvtBaseDir()+"/build/classes/annotations/webparam/testdata/WebParamGeneralChecks.class");
        AnnotationHelper anh = new AnnotationHelper("annotations.webparam.testdata.WebParamGeneralChecks",imp.getWorkingDir());
        // check that every method has a @webparam anno. as required by spec.
        // also check that params are set to appropriate values where we can.
        // since these only had one argument, only have to check for one annotation.

        // this one is all defaults but annotation should be present.
        // for some reason it is present in source but disappears from class.
        sbuf = anh.getParamElement("echoObject1","WebParam","name");
        assertNotNull("missing annotation for echoObject1 ", sbuf);

        sbuf = anh.getParamElement("echoObject2","WebParam","name");
        // interesting, if value is default, "arg0", it's dropped from the classfile.
        assertNotNull("missing annotation for echoObject2 ", sbuf);

        sbuf = anh.getParamElement("echoObject4","WebParam","mode");
        // interesting, if value is default, "arg0", it's dropped from the classfile.
        assertNotNull("missing annotation for echoObject4 ", sbuf);

        sbuf = anh.getParamElement("echoObject6","WebParam","targetNamespace");
        // interesting, if value is default, "arg0", it's dropped from the classfile.
        assertNotNull("missing annotation for echoObject6 ", sbuf);

        // there are two webparam annos here, and the header var is set on the second.
        sbuf = anh.getParamElement("echoObject7",1,"WebParam","header");
        // interesting, if value is default, "arg0", it's dropped from the classfile.
        assertNotNull("missing annotation for echoObject7 ", sbuf);

        sbuf = anh.getParamElement("echoObject8","WebParam","partName");
        // interesting, if value is default, "arg0", it's dropped from the classfile.
        assertNotNull("missing annotation for echoObject8 ", sbuf);

        // now check some non-default things.

        sbuf = anh.getParamElement("echoObject6","WebParam","targetNamespace");
        assertTrue("targetnamespace wrong, got:"+sbuf, sbuf.compareTo("foo.myns")==0);

        sbuf = anh.getParamElement("echoObject2","WebParam","name");
        assertTrue("parameter name wrong, got:"+sbuf, sbuf.compareTo("obj2")==0);

        sbuf = anh.getParamElement("echoObject7",1,"WebParam","header");
        assertTrue("parameter header wrong, got:"+sbuf, sbuf.compareTo("true")==0);

        // looks like there is no way for mode to map back from wsdl to java
        // same for partname

    }

}
