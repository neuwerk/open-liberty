/*
 * autoFVT/src/annotations/soapbinding/SOAPBindingTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 *                         LIDB3296.31.01     new file
 * 8/15/2007   btiffany    459641             fix constructor for framework changes
 *
 */

package annotations.soapbinding;
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
 * @throws Exception Any kind of exception
 *
 * @testStrategy Check that illegal variations of this annotation do not produce wsdl.
 * Check that legal ones do.
 * Check that default use of the annotation works.
 * See the runtime test for wsdl2java testing
 *
 * @author btiffany
 *
 */
public class SOAPBindingTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        private static String workDir = null;
        ImplementationAdapter imp = Support.getImplementationAdapter("ibm",workDir);
        private static boolean setupRun = false;        // for junit

        // framework reqmt.
        public SOAPBindingTestCase( String s){
                super(s);
        }

        public SOAPBindingTestCase( ){
                super("nothing");
        }

        static{
                workDir = Support.getFvtBaseDir()+"/build/work/annotations/soapbinding/j2w";
	}

	public static void main(String[] args) throws Exception {	
		if (true){
			TestRunner.run(suite());
		} else{
			SOAPBindingTestCase tc = new SOAPBindingTestCase();
            tc.testW2J();
		}			
	}	

    /**
     * junit needs this.
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(SOAPBindingTestCase.class);
    }

    // junit will run this before every test
    public void setUp(){
    }

    /**
     * @testStrategy generate java from soapbindanno3wsdl, and inspect
     * it for proper annotation parameters.  This uses both doclit-bare and
     * doclit-wrapped methods in the same wsdl.
     *
     *  moved from runtime test 10.03.06
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="generate java from soapbindanno3wsdl, and inspect  it for proper annotation parameters.  This uses both doclit-bare and doclit-wrapped methods in the same wsdl.  moved from runtime test 10.03.06",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testW2J() throws Exception {
        System.out.println("********** testW2J is running **********");
        imp.setWorkingDir(workDir+"/../w2j");
        imp.w2j("","src/annotations/soapbinding/testdata/SoapBindAnno3.wsdl");
        AnnotationHelper anh = new AnnotationHelper("annotations.soapbinding.testdata.SoapBindAnno3",
                                                    imp.getWorkingDir());
        // these should not be present
        assertNull("unexpected reqwrapper anno on bare method ", anh.getMethodAnnotation("echodlb","RequestWrapper"));
        assertNull("unexpected respwrapper anno on bare method ", anh.getMethodAnnotation("echodlb","ResponseWrapper"));

        // this one should
        assertNotNull("missing soapbinding wrapper on method ", anh.getMethodAnnotation("echodlb","SOAPBinding"));
        String actual = anh.getMethodElement("echodlb","SOAPBinding","parameterStyle");
        String expect = "BARE";
        assertTrue("binding anno is: "+actual+" expected: "+expect, expect.compareTo(actual)==0);
        System.out.println("******** testw2j passed ************");
    }

    /**
     * @testStrategy check that illegal combinations of parameters on this annotation
     * do not produce wsdl, and that legal combinations do.
     *
     * Note that DocEncWrap/Bare, RPCEncWrap/Bare is not supported on method or class.
     * RPCLitW is only supported on class.
     * DocLitBare/Wrap should be fully supported on methods and class.
     * The java classes are named after the type of annotation they are using,
     * for example blah_dewc.java is testing document, encoded, wrapped on a class.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="check that illegal combinations of parameters on this annotation do not produce wsdl, and that legal combinations do.  Note that DocEncWrap/Bare, RPCEncWrap/Bare is not supported on method or class. RPCLitW is only supported on class. DocLitBare/Wrap should be fully supported on methods and class. The java classes are named after the type of annotation they are using, for example blah_dewc.java is testing document, encoded, wrapped on a class. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testJ2WIllegalVariations() throws Exception {
    	System.out.println("********** testJ2WIllegalVariations is running **********");
       	imp.setWorkingDir(imp.getWorkingDir()+"/../j2w");   	
       	// first, try a legal one to make sure tooling is working period.
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_dlbm_legal.java", true);
      	genWsdl("src/annotations/soapbinding/testdata/SoapBindAnno2.java", true);
       	// now try a bunch that should not work      	
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_debc.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_debm.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_dewc.java", false);
      	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_dewm.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_rebc.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_rebm.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_rewc.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_rewm.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_rlbc.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_rlbm.java", false);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_rlwm.java", false);
       	// check the legal cases
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_dlbm_legal.java", true);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_dlbc_legal.java", true);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_default_legal.java", true);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_dlwc_legal.java", true);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_dlwm_legal.java", true);
       	genWsdl("src/annotations/soapbinding/testdata/Sbj2w_rlwc_legal.java", true);
       	genWsdl("src/annotations/soapbinding/testdata/SoapBindAnno.java", false);
        System.out.println("********** testJ2WIllegalVariations passed **********");


    }

    /**
     *
     * @param sourcefile
     * @param shouldWork - boolean, should conversion to wsdl work or not.
     * @throws Exception
     */
    void genWsdl(String sourcefile, boolean shouldWork) throws Exception {    	
       	boolean success = true;
       	String classname = ( sourcefile.replaceAll("/",".").substring(4, sourcefile.length()-5));
       	try{
       		imp.wsgen("", classname);
       	} catch( RuntimeException e ){
       		System.out.println(e.getMessage());
       		success = false;
       	}
       	if(!shouldWork){
       		assertFalse("wsdl should not have been produced for: "+ sourcefile, success);
       	} else {
       		assertTrue("wsdl should have been produced for: "+ sourcefile, success);
       		
       	}
       	
    }
}
