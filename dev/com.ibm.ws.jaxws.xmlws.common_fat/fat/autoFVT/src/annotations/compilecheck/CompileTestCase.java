/*
 * Level: 7
 * Release: 1
 * Version: 1.7
 * Last Rev Date:  8/8/07
 * W Key:  autoFVT/src/annotations/compilecheck/CompileTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 *
 */

package annotations.compilecheck;
import junit.framework.*;

import javax.wsdl.*;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.namespace.*;

import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

import annotations.support.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;



import junit.textui.TestRunner;
/**
 * @throws Exception Any kind of exception
 *
 * @author btiffany
 *
 */
public class CompileTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        private static String workDir = null;
        ImplementationAdapter imp = Support.getImplementationAdapter("default",workDir);
        private static boolean setupRun = false;        // for junit

        static{
                // set the working directory to be <work area>/<classname>
                workDir = CompileTestCase.class.getName();
                int p = workDir.lastIndexOf('.');
                workDir = Support.getFvtBaseDir()+"/build/work/"+workDir.substring(p+1);
        }

        public static void main(String[] args) throws Exception {
                TestRunner.run(suite());
        }


    /**
     * junit needs next 3.
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(CompileTestCase.class);
    }

        public CompileTestCase(String str) {
                super(str);
        }

        public CompileTestCase(){
                super();
        }

    // junit will run this before every test
    public void setUp(){
        // if standalone, junit runs this
        // in harness, annotations ant script will run this explicitly with Run_Setup set
        if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
        if (setupRun) return;   // we only want to run this once per suite
        setupRun = true;
        imp.cleanWorkingDir();
        //       insert setup for anything that needs to be installed and deployed on server here.

        // if we are not in the harness, restart server after deployment
        if (!Support.inHarness()) imp.restart();
    }

    /** @testStrategy check that a class with ALL annotations can be compiled.
     *
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testCompile() throws Exception {
        System.out.println("********** testCompile() is running **********");
        try{
            imp.compile("src/annotations/compilecheck/testdata/CompileCheck.java","");
        } catch (Exception e ){
            fail("CompileCheck.java could not be compiled");
        }

    }


    /**
    * @testStrategy - print the cmvc level of fvt build we're using, which we're always interested in.
    *
    *
    */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPrintCMVCInfo() throws Exception {
        System.out.println("********** testPrintCMVCInfo() is running **********");
        System.out.println(" SCCS Level: 7  \n SCCS Release: 1 \n ");
        System.out.println(" CMVC W key: autoFVT/src/annotations/compilecheck/CompileTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 \n ");

    }
    
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        System.out.println("Do not need suiteSetup since no application is installed");    
    }

}
