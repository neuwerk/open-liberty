/*
 * autoFVT/src/annotations/webservice/WebServicePackageMappingTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 08/07/2008 btiffany    542159              Remove old setup methods, malfunctioning on z/i
 */
package annotations.webservice;
import junit.framework.*;

import javax.wsdl.*;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.namespace.*;

import annotations.support.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;


import junit.textui.TestRunner;

/**
 * based on 224 sec 3.2.
 * checks that if no package specified, required annotation parm is present, and
 * that package names map properly to wsdl namespace with and without annotation.
 * Of questionable usefulness, why would anyone deploy without a package name??
 * @author btiffany
 *
 */
public class WebServicePackageMappingTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        static String workDir=null;
        ImplementationAdapter imp = Support.getImplementationAdapter("ibm", workDir);

        static{
                workDir = Support.getFvtBaseDir()+"/build/work/WebServicePackageMappingTestCase";
        }

        // framework reqmt.
        public WebServicePackageMappingTestCase( String s){
            super(s);
        }
        // for bw compat with runsetups
        public WebServicePackageMappingTestCase(){
            super("setup");
        }


        public static void main(String[] args) throws Exception {
        //WebServicePackageMappingTestCase t = new  WebServicePackageMappingTestCase();
        //t.testNoPackage();
                TestRunner.run(suite());
        }

        public void setUp(){
        if (true) {return; } //542159 - this is all in ant now.
        // if standalone, junit runs this
        // in harness, annotations ant script will run this explicitly with Run_Setup set
        if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
        }


    /**
     * The suite method returns the tests to run from this suite.
     * The Java2WsdlTestCaseCase class is specified which means
     * all methods starting with "test" will be be run.
     *
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
        return new TestSuite(WebServicePackageMappingTestCase.class);
    }

    /**
     * @testStrategy - compile  and run wsgen on two java files that do not have a package statement in them.
     * The first has a namespace attribute on the annotation which should define the package.  The second
     * does not and should be rejected by the tooling.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testNoPackage() throws Exception {
    	System.out.println("************** running testNoPackage()***************");
    	// TODO: wsgen appears to be BUSTED on this one.
    	try{    		
            imp.compile("src/annotations/webservice/testdata/WebServicePackageMapping1.java", "");
            imp.wsgen("","WebServicePackageMapping1");
    	} catch( Exception e){}
    	String expectedFile= workDir+"/WebServicePackageMapping1Service.wsdl";
    	assertTrue("386055 - wsdl not generated for legal packageless java class", new File(expectedFile).exists() );
    	
    	// now try the case that should not work
    	try{
            imp.compile("src/annotations/webservice/testdata/WebServicePackageMapping2.java", "");
            imp.wsgen("","WebServicePackageMapping2");
    	} catch( Exception e){}
    	expectedFile= workDir+"/WebServicePackageMapping2Service.wsdl";
    	assertFalse("wsdl generated for illegal packagelss java class" , new File(expectedFile).exists() );
    	
    }

    /**
     * @testStrategy - check that package names in java map to namespaces in wsdl per spec.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPackageMapping() throws Exception {
    	System.out.println("************** running testPackageMapping()***************");
//    	imp.j2w("","src/annotations/webservice/testdata/WebServicePackageMapping3.java",
///    			"annotations.webservice.testdata.WebServicePackageMapping3");
    	
    	//  now inspect the wsdl.
    	WSDLFactory factory = WSDLFactory.newInstance();
    	WSDLReader reader = factory.newWSDLReader();
    	reader.setFeature("javax.wsdl.verbose", true);
    	reader.setFeature("javax.wsdl.importDocuments", true);
    	// wsdl name changes!
    	Definition def = reader.readWSDL(null, workDir+"/WebServicePackageMapping3Service.wsdl");    	
    	
    		    	
    	Map m = def.getServices();
    	Collection<Service> c = m.values();	    	
    	assertTrue("no or too many services found", c.size()== 1);
    	for (Service s: c){
    		String actual= s.getQName().getNamespaceURI();
    		// jsr 224 sec 3.2    		
    		String expected = "http://testdata.webservice.annotations/";
    		//System.out.println(actual+"<\n"+expected+"<\n"+actual.compareTo(expected)+"\n"+actual.length()+"\n"+expected.length());
    		assertTrue("wrong namespace in wsdl, actual="+actual+"< expected="+expected+"<",
    				actual.indexOf(expected)==0 && actual.length()== expected.length());
    	}    	
    }

    /**
     * @testStrategy - check that namespace param in annotation maps to wsdl per spec.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPackageMapping2() throws Exception {
    	System.out.println("************** running testPackageMapping2()***************");    	
//    	imp.j2w("","src/annotations/webservice/testdata/WebServicePackageMapping4.java",
//    			"annotations.webservice.testdata.WebServicePackageMapping4");
    	
    	//  now inspect the wsdl.
    	WSDLFactory factory = WSDLFactory.newInstance();
    	WSDLReader reader = factory.newWSDLReader();
    	reader.setFeature("javax.wsdl.verbose", true);
    	reader.setFeature("javax.wsdl.importDocuments", true);
    	// wsdl name changes!
    	Definition def = reader.readWSDL(null, workDir+"/WebServicePackageMapping4Service.wsdl");    	
    	
    		    	
    	Map m = def.getServices();
    	Collection<Service> c = m.values();	    	
    	assertTrue("no or too many services found", c.size()== 1);
    	for (Service s: c){
    		String actual= s.getQName().getNamespaceURI();
    		// jsr 224 sec 3.2    		
    		String expected = "customized";
    		//System.out.println(actual+"<\n"+expected+"<\n"+actual.compareTo(expected)+"\n"+actual.length()+"\n"+expected.length());
    		assertTrue("wrong namespace in wsdl, actual="+actual+"< expected="+expected+"<",
    				actual.indexOf(expected)==0 && actual.length()== expected.length());
    	}    	
    }
	

}
