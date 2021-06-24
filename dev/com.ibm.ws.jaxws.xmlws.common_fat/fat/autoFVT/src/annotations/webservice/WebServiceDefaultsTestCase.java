/*
 * autoFVT/src/annotations/webservice/WebServiceDefaultsTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 *
 */
package annotations.webservice;

import junit.framework.*;
import annotations.support.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;


import junit.textui.TestRunner;

/**
 * test that @WebService default conditions are not violated under two cases:
 * 1) wsdl2java produces java with valid @WebService defaults
 * 2) java2wsdl rejects java files with illegal @WebService annotations
 * @author btiffany
 */
public class WebServiceDefaultsTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

        private StringBuffer messages = new StringBuffer();
        private static String workDir ="null";
        private static boolean setupRun = false;
        private boolean goterror = false;
        ImplementationAdapter imp = Support.getImplementationAdapter("ibm", workDir);

        static{
                workDir = Support.getFvtBaseDir()+"/build/work/WebServiceDefaultsTestCase";
        }


        // framework reqmt.
        public WebServiceDefaultsTestCase( String s){
            super(s);
        }
        // for bw compat with runsetups
        public WebServiceDefaultsTestCase(){
            super("setup");
        }

         /**
         * main method for running/debugging outside of harness.
     * @param args Command line arguments
     */
        public static void main(String[] args) {
                TestRunner.run(suite());
        }

    /**
     * The suite method returns the tests to run from this suite.
     * all methods starting with "test" will be be run.
     *
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
        return new TestSuite(WebServiceDefaultsTestCase.class);
    }

    // junit runs this before every test
    public void setUp(){
        if (true) {return; } //542159 - this is all in ant now.
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want cleanup to run once per suite
    	System.out.println("******** WebServiceDefaultsTestCase.setup running ********");
    	// imp.cleanWorkingDir();
    	setupRun = true;
    }

    /**
     * Check that wsdl-independent requirements of @Webservice annotations are met
     * by a class file generated by wsdl2java.
     *
     * @testStrategy - check that generated sei for a simple web service doesn't
     * violate any of the fundamental restrictions on the annotation. For a generated
     * interface, this test can't check much.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- check that generated sei for a simple web service doesn't  violate any of the fundamental restrictions on the annotation. For a generated interface, this test can't check much. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWebServiceDefaults1(){
    	System.out.println("******** WebServiceDefaultsTestCase.testWebServiceDefaults1 running ********");
    	//imp.w2j ("", "src/annotations/webservice/testdata/WebServiceDefaultsFooService2.wsdl" );    	
    	doTestWebServiceDefaults("myfoo.Foo");   	
    }

    /**
     * given any class, checks that basic requirements for use of the annotation are met.
     */
    void doTestWebServiceDefaults(String classname){
    	// these @WebService conditions must be met regardless of what the wsdl looks like.
    	AnnotationHelper ah = new AnnotationHelper(classname, workDir);
    	//AnnotationHelper ah = new AnnotationHelper(classname);
    	
    	// see if req'd. @WebService is present
    	Annotation an = ah.getClassAnnotation("WebService");
    	assertNotNull("Every class must have @WebService annotation", an);
    	
    	// if endpoint interface is specified, check that illegal annotations
    	// are not present
    	if (!ah.isInterface() && ah.getAnnotationElement(an, "endpointInterface").length() >0 ){
    		// illegal params on WebService class    		
    		storedAssertFalse("jsr181 3.1 name not allowed with endpointInterface",
    				ah.getAnnotationElement(an, "name").length() >0);
    		storedAssertFalse("jsr181 3.1 targetNameSpace not allowed with endpointInterface",
    				ah.getAnnotationElement(an, "targetNamespace").length() >0);
    		storedAssertFalse("jsr181 3.1 wsdlLocation not allowed with endpointInterface",
    				ah.getAnnotationElement(an, "wsdlLocation").length() >0);
    		
    		// these can't be present if an interface is used.
    		HashMap h = ah.getMethodAnnotationNames();
    		String [] illegal = {"WebMethod", "OneWay", "WebParam", "WebResult", "SOAPBinding"};
    		for (String s: illegal){    			
    			// look for each illegal in the hashmap
    			storedAssertFalse("specifying endpoint disallows " +s, h.containsValue(s));    			
    		}   		
    	}
    	
    	if (ah.isInterface() && ah.getAnnotationElement(an, "endpointInterface").length() >0 ){
    		assertFalse("can't have endpointinterface param on an interface",true);
    	}
    	

    	// check for illegal class modifiers and required constructor
    	if (!ah.isInterface()){
    		int m = ah.getClas().getModifiers();    	    	
	    	storedAssertFalse("jsr181 3.1 class cannot be abstract", Modifier.isAbstract(m));
	    	storedAssertFalse("jsr181 3.1 class cannot be final", Modifier.isFinal(m));
	    	storedAssertTrue("jsr181 3.1 class must be public", Modifier.isPublic(m));
	    	//	    	 check for default public constructor
	    	try{
	    		//System.out.println(ah.getClas().getConstructor().getDeclaringClass().toString());
	    		ah.getClas().getConstructor();
	    		// actually, if it's not public, reflection won't find it at all.
	    		//storedAssertTrue("jsr181 3.1 constructor must be public ", Modifier.isPublic(i));
	    	} catch (NoSuchMethodException e){
	    		storedAssertTrue("jsr181 3.1 must have default public constructor", false);
	    	}
    	}	
    	
    	
    	// check that finalize method is not present
    	boolean finalizePresent = true;
    	try{
    		// interesting that this does not pick up the inherited finalize method
    		// but getMethods() will.
    		Method n = ah.getClas().getMethod("finalize");
    	} catch (NoSuchMethodException e){
    		finalizePresent = false;
    	}
    	storedAssertFalse("jsr181 3.1 can't have finalize method", finalizePresent);   	
    	
    	    	
    	
    	// if any tests failed, assert in junit now.
    	assertFalse(messages.toString(), goterror);
    	return;
    }

    /**
     * Now for some negative tests.
     *
     * @testStrategy check that improper java classes are rejected by the tooling.
     * A java class with missing anno, final class, endpoint decl. on interface,
     * missing public constructor are attempted.
     * All should fail.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="check that improper java classes are rejected by the tooling. A java class with missing anno, final class, endpoint decl. on interface, missing public constructor are attempted. All should fail.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWebServiceDefaults2(){    	
    	
    	// first feed it a valid file, j2w should succeed.
    	try{
    		imp.j2w("", "src/annotations/webservice/testdata/WebServiceDefaultsValid1.java",
    				"annotations.webservice.testdata.WebServiceDefaultsValid1");
    	} catch (Exception e){ e.printStackTrace();}
    	assertTrue("Valid1.wsdl is missing", new File(workDir+"/Valid1.wsdl").exists() );
    	
    	// now, feed it an invalid file, it should fail
    	// @webservice anno is missing on this one.
    	try{
    		imp.j2w("", "src/annotations/webservice/testdata/WebServiceDefaultsImproper1.java",
    				"annotations.webservice.testdata.WebServiceDefaultsImproper1");
    	} catch (Exception e){}
    	assertFalse("wsdl should not have been generated when @WebService is missing",
    			new File(workDir+"/WebServiceDefaultsImproper1Service.wsdl").exists() );

    	
    	// a file with invalid parms, class is final
        try{
                imp.j2w("", "src/annotations/webservice/testdata/WebServiceDefaultsImproper2.java",
                                "annotations.webservice.testdata.WebServiceDefaultsImproper2");
        } catch (Exception e){}
        assertFalse("wsdl should not have been generated for final class",
                        new File(workDir+"/WebServiceDefaultsImproper2Service.wsdl").exists() );

        // a file with invalid parms, endpointinterface parm points to nonexistent interface
        try{
                imp.j2w("", "src/annotations/webservice/testdata/WebServiceDefaultsImproper3.java",
                                "annotations.webservice.testdata.WebServiceDefaultsImproper3");
        } catch (Exception e){}
        assertFalse("wsdl should not have been generated for missing enpdoint IF",
                        new File(workDir+"/WebServiceDefaultsImproper3Service.wsdl").exists() );

        // a file without a public constructor
        try{
                imp.j2w("", "src/annotations/webservice/testdata/WebServiceDefaultsImproper_NoPublicConstructor.java",
                                "annotations.webservice.testdata.WebServiceDefaultsImproper_NoPublicConstructor");
        } catch (Exception e){}
        assertFalse("wsdl should not have been generated for private constructor",
                        new File(workDir+"/WebServiceDefaultsImproper_NoPublicConstructorService.wsdl").exists() );

        // need to check for >1 wsdl file in case naming was not what we expected.
                String searchpath = imp.getWorkingDir();
                java.io.File f = new File(searchpath);
                File [] ff = f.listFiles();
                int j=0;
                String wsdlfiles="";
                if (ff.length >1){
                        for(int i=0; i < ff.length; i++ ){
                                if( ff[i].toString().endsWith(".wsdl")){
                                        j++;
                                        wsdlfiles = wsdlfiles + "  "+ff[i].toString();                          }
                        }
                        if ( j > 1 ){
                                fail("too many wsdl files generated: "+wsdlfiles);
                        }

                }


    }


    /**
     * store up all failed asserts and spit them out at the end, so we don't have to wrapper up
     * a whole bunch of junit methods.
     */
    void storedAssertTrue(String s, boolean b){
        if (!b){
        messages.append(s + "\n");
        goterror = true;
        }
    }

    void storedAssertFalse(String s, boolean b){
        storedAssertTrue(s, !b);
    }

}

