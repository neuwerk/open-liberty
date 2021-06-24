/*
 * autoFVT/src/annotations/oneway/OneWayTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 08/08/2007    "                            add new constructor for framework
 *
 */
package annotations.oneway;
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
 *
 * Checks \@OneWay annotation compliance to standard
 *
 * @author btiffany
 *
 */
public class OneWayTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        // TODO: define this:  private String workDir="gen";
        private static String workDir = null;
        private static boolean setupRun = false;        // for junit
        ImplementationAdapter imp = Support.getImplementationAdapter("ibm",workDir);

        static{
                workDir = Support.getFvtBaseDir()+"/build/work/OneWayTestCase";
        }

        // framework reqmt
        public OneWayTestCase(String s){
            super(s);
        }


        public static void main(String[] args) {
                TestRunner.run(suite());

        }
    /**
     * The suite method returns the tests to run from this suite.
     * The Java2WsdlTestCaseCase class is specified which means
     * all methods starting with "test" will be be run.
     *
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
            return new TestSuite(OneWayTestCase.class);
    }

    // junit runs this before every test
    public void setUp(){
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite
    	//imp.cleanWorkingDir();
    	setupRun = true;
    }

    /**
     * @testStrategy given a java file with @Oneway annotation, see if wsdl can be
     * generated for it.  Not inspecting the wsld for correctness yet.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="given a java file with @Oneway annotation, see if wsdl can be  generated for it.  Not inspecting the wsld for correctness yet.",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testj2wDefaults() throws Exception {
    	System.out.println("******* runnning testj2wDefaults **********\n");
    	// default case that should work
    	imp.j2w("","src/annotations/oneway/testdata/OneWayDefaults.java",
    			"annotations.oneway.testdata.OneWayDefaults");
    	String expected = workDir+"/OneWayDefaultsService.wsdl";
    	assertTrue("wsdl file:" + expected+ " was not generated", new File(expected).exists());    	
    }

    /**
     * @testStrategy check that return value is disallowd
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testj2wReturn() throws Exception {
    	// now OneWay with a return value
    	System.out.println("******* runnning testj2wReturn **********\n");
    	try{
    		imp.j2w("","src/annotations/oneway/testdata/OneWayImproper1.java",
    				"annotations.oneway.testdata.OneWayImproper1");
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	String expected = workDir+"/OneWayImproper1Service.wsdl";
    	assertFalse("wsdl file should not have been generated:" + expected+ " generated", new File(expected).exists());
    }

    /**
     * checks that \@WebMethod is present
     * @throws Exception
     *
     * This test is invalid, because jsr250 2.1.2 states that an @WebMethod can be inferred
     * from the @WebService annotation if not explicitly present on any methods within the class.
     * Wsgen does not expose an @Oneway annotated method unless it also
     * carries an explicit or implicit @WebMethod, so it is working correctly.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testj2wWebmethod() throws Exception {
       	// now OneWay with missing @WebMethod, see jsr181 2.5.1
    	System.out.println("******* runnning testj2wWebmethod **********\n");
    	try{
    		imp.j2w("","src/annotations/oneway/testdata/OneWayImproper2.java",
    				"annotations.oneway.testdata.OneWayImproper2");
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	String expected = workDir+"/OneWayImproper2Service.wsdl";
    	assertFalse("(def 386252) wsdl file should not have been generated:" + expected+ " generated", new File(expected).exists());
    }

    /**
     * @testStrategy that java2wsdl won't generate wsdl for a
     * @oneway annotated method that throws and exception
     * @throws Exception
     *

     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testj2wException() throws Exception {
       	// now OneWay that throws Exception, should fail
    	System.out.println("******* runnning testj2wException **********\n");
    	try{
    		imp.j2w("","src/annotations/oneway/testdata/OneWayImproper3.java",
    				"annotations.oneway.testdata.OneWayImproper3");
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	String expected = workDir+"/OneWayImproper3Service.wsdl";
    	assertFalse("wsdl file should not have been generated:" + expected+ " generated", new File(expected).exists());
    }


    /**
     * test w2j.
     * 224 2.3 says oneway method must be annotated
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testw2jDefault() throws Exception {
    	System.out.println("******* runnning testw2jDefault **********\n");
    	imp.w2j("", "src/annotations/oneway/testdata/OneWayDefaultW1.wsdl");
    	String expected = OS.fixPsep(workDir+"/gen/OneWayDefaultsW1.java");
    	assertTrue("java file should have been generated:" + expected, new File(expected).exists());
    	// method should have the oneway annotatoin
    	AnnotationHelper anh = new AnnotationHelper("gen.OneWayDefaultsW1",workDir);
    	assertNotNull("OneWay annotation missing in generated java",
    					anh.getMethodAnnotation("doSomething", "Oneway"));    	
    }

}
