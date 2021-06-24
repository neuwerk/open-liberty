/*
 * autoFVT/src/annotations/webservice/WebServiceMatchesWsdlTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 
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


import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import junit.textui.TestRunner;

/**
 *
 * @author btiffany
 * with respect to the @WebService annotation,
 * case 1 - test that generated wsdl is consistent with annotated java
 * case 2 - test that generated java is consistent with wsdl.
 *
 */
public class WebServiceMatchesWsdlTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

        static String workDir=null;
        ImplementationAdapter imp = Support.getImplementationAdapter("ibm", workDir);
        static boolean setupRun = false;
        static{
                workDir = Support.getFvtBaseDir()+"/build/work/WebServiceMatchesWsdlTestCase";
        }

        // framework reqmt.
        public WebServiceMatchesWsdlTestCase( String s){
            super(s);
        }
        // for bw compat with runsetups
        public WebServiceMatchesWsdlTestCase(){
            super("setup");
        }

        public static void main (String [] args) throws Exception{
                WebServiceMatchesWsdlTestCase tc = new WebServiceMatchesWsdlTestCase();
                TestRunner.run(suite());
                //tc.testDerivedWsdl();
                //tc.testDerivedWsdl2();
                //tc.testDerivedJava1();
                System.out.println("done");
        }

    public static junit.framework.Test suite() {
        return new TestSuite(WebServiceMatchesWsdlTestCase.class);
    }

    // junit runs this before every test
    public void setUp(){
        if (true) {return; } //542159 - this is all in ant now.
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite    	
    	imp.cleanWorkingDir();
    	setupRun = true;
    }
	
    /**
     * @testStrategy Generate wsdl from a java file with a bare @WebService anno, i.e.
     * no parameters, and verify that all the wsdl parameters match what the defaults
     * should be in this case.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="Generate wsdl from a java file with a bare @WebService anno, i.e. no parameters, and verify that all the wsdl parameters match what the defaults should be in this case. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	 public void testDerivedWsdl() throws Exception{
		    System.out.println("*****************running testDerivedWsdl1()**************");
		 	// compile and generate wsdl from java file - test with all parms defaulted
		 	String sourcefile ="src/annotations/webservice/testdata/WebServiceMatchesWsdl1.java";
		 	//imp.j2w("",sourcefile, "annotations.webservice.testdata.WebServiceMatchesWsdl1");

		 	//get a helper for the class file so we can peek at the annotations
		 	// no, just hardcode for the expected contents.
		 	//AnnotationHelper anh = new AnnotationHelper("gen.WebServiceWsdlDefaults1");
	    	
		 	// now inspect it.
	    	WSDLFactory factory = WSDLFactory.newInstance();
	    	WSDLReader reader = factory.newWSDLReader();
	    	reader.setFeature("javax.wsdl.verbose", true);
	    	reader.setFeature("javax.wsdl.importDocuments", true);
	    	Definition def = reader.readWSDL(null, workDir+"/WebServiceMatchesWsdl1Service.wsdl");    	
	    	
	    		    	
	    	Map m = def.getServices();
	    	Collection<Service> c = m.values();	    	
	    	assertTrue("no or too many services found", c.size()== 1);
	    	for (Service s: c){
	    		// this should match the serviceName in the annotation
	    		// or classname+"Service" if serviceName is null in annotation
	    		String wsdlname = s.getQName().getLocalPart();
	    		
	    		String expect = "WebServiceMatchesWsdl1Service";
	    		assertTrue("wrong service name, "+wsdlname, wsdlname.compareTo(expect)==0);
	    		
	    		// this should match the targetNameSpace if not default, or
	    		// contain the package name if defaulted
	    		String nspace = s.getQName().getNamespaceURI();
	    		System.out.println(nspace);
	    		assertTrue("wrong namespace," +nspace, nspace.lastIndexOf("testdata.webservice.annotations")>0 );
	    		
	    		// wsdllocation - doesn't make sense to check in this context.
	    		// endpointInterface - doesn't make sense to check
	    		// TODO: portname - need updated version of jsr181.
	    	}    	

	    }
	
	 /**
	  * @testStrategy - generate wsdl from a java file with a WebService annotation
	  * that has all parameters defined.  Verify that wsdl is appropriately modified to
	  * match the annotation.
	  *
	  */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- generate wsdl from a java file with a WebService annotation  that has all parameters defined.  Verify that wsdl is appropriately modified to match the annotation. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	 public void testDerivedWsdl2() throws Exception{
		 System.out.println("*****************running testDerivedWsdl2()**************");
	 	// compile and generate wsdl from java file - test w all parms defined.
	 	String sourcefile ="src/annotations/webservice/testdata/WebServiceMatchesWsdl2.java";
	 	//imp.j2w("", sourcefile, "annotations.webservice.testdata.WebServiceMatchesWsdl2") ;
	 	
	 	// now inspect it.
    	WSDLFactory factory = WSDLFactory.newInstance();
    	WSDLReader reader = factory.newWSDLReader();
    	reader.setFeature("javax.wsdl.verbose", true);
    	reader.setFeature("javax.wsdl.importDocuments", true);
    	// wsdl name changes!
    	Definition def = reader.readWSDL(null, workDir+"/Testsvc.wsdl");    	
    	
    		    	
    	Map m = def.getServices();
    	Collection<Service> c = m.values();	    	
    	assertTrue("no or too many services found", c.size()== 1);
    	for (Service s: c){
    		// this should match the serviceName+"Service"  in the annotation
    		// or classname+Service if serviceName is null in annotation
    		String wsdlname = s.getQName().getLocalPart();
    		String expectedName="testsvc";
    		System.out.println(wsdlname);	    		
    		assertTrue("wrong service name, "+wsdlname+" expected: "+expectedName
    				, wsdlname.compareTo(expectedName)==0);
    		
    		// this should match the targetNameSpace if not default, or
    		// contain the package name if defaulted
    		String nspace = s.getQName().getNamespaceURI();
    		System.out.println(nspace);
    		expectedName="testtns";
    		assertTrue("wrong namespace, found " +nspace+ " expected: "+expectedName,
    				nspace.lastIndexOf(expectedName)==0 );
    		
    		// check portname, should match anno. portname param.
    		expectedName="testport";
    		assertNotNull("could not find port: "+expectedName, s.getPort(expectedName));
    		
    		// check portType, should match anno. name param.
    		String eName = "testname";
    		String aName = s.getPort(expectedName).getBinding().getPortType().getQName().getLocalPart();
    		System.out.println(aName);
    		assertTrue("wrong porttype name, "+aName +" expected: "+eName,
    					eName.compareTo(aName)==0);    		
    		
    		// wsdllocation - doesn't make sense to check in this context.	    		
    		// endpointInterface - doesn't make sense to check
    		
    	}
	 }
	    	
	    	
   	// now go the other way, start from wsdl and annotation should match
	/**
	 * @testStrategy given a wsdl that should cause all parameters of @WebService
	 * to have non-default values, see if this happens.
	 * Note that portName, serviceName, and endpointInterface must be empty on an interface.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="given a wsdl that should cause all parameters of @WebService to have non-default values, see if this happens. Note that portName, serviceName, and endpointInterface must be empty on an interface.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   	public void testDerivedJava1() throws Exception{
   		System.out.println("*****************running testDerivedJava1()**************");
    	// a wsdl that should populate all the @WebService params.
   		String wsdl="src/annotations/webservice/testdata/WebServiceTestDerivedJava1.wsdl";   		
   		//imp.w2j("",wsdl);
   		AnnotationHelper anh = new AnnotationHelper("fuddns.Elmerfudd", workDir);
   		Annotation a =  anh.getClassAnnotation("WebService");   		
   		String name =   anh.getAnnotationElement(a,"name");
   		String tns  =   anh.getAnnotationElement(a,"targetNamespace");
   		String svname = anh.getAnnotationElement(a,"serviceName");
   		String wl =  anh.getAnnotationElement(a,"wsdlLocation");
   		String pname =  anh.getAnnotationElement(a,"portName");
   		String epi   =  anh.getAnnotationElement(a,"endpointInterface");
   		//System.out.println(name +"\n"+ tns  +"\n"+ svname +"\n"+ wl +"\n"+ pname);
   		String expect = "elmerfudd";
   		assertTrue("name= "+name+ " but expected: "+expect, name.compareTo(expect)==0);
   		
   		expect="fuddns";
   		assertTrue("targetnamespace= "+tns+ " but expected: "+expect, tns.compareTo(expect)==0);
   		
   		// servicename not allowed if this is an interface
   		assertTrue("servicename present on interface", anh.isInterface()&& svname.length()== 0);
   		
   		// endpointinterface not allowed if this is an interface
   		assertTrue("endpointinterface present on interface", anh.isInterface() && epi.length()==0);
   		
   		// in w2j mode, this parm should not be null - unless default, that is.
   		//expect= wsdl;
   		//assertTrue("wsdllocation =" + wl + "should contain: "+wsdl, wl.indexOf(wsdl) > -1);
   		
   		// per jsr181 mr2, item 32, the portName element is not allowed on the interface.
   		// so should be null.  - 7.20.06 bt
   		assertTrue("portname present on interface", anh.isInterface() && pname.length()==0);
   		
   		/*
   		expect="elmerfudd1";
   		assertTrue("portname= "+pname+" expected:"+expect, pname.compareTo(expect)==0);
   		*/
   		
   		
   		
   		return;
   	}

}
