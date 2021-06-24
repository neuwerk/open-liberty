/*
 * autoFVT/src/annotations/webmethod/WebMethodNameClashTestCase.java, WAS.websvcs.fvt, WASX.FVT, yy0800.16
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
 * 08/14/2007  btiffany    459641             add constructor for framework
 * 08/07/2008  btiffany    542158             remove old setup code malfunctioning on i,z
 *
 */
package annotations.webmethod;
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


public class WebMethodNameClashTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	private static String workDir=null;	
	ImplementationAdapter imp = Support.getImplementationAdapter("ibm", workDir);	
	private static boolean setupRun = false; 	// for junit
	
	// for reliability, we need this to be an absolute path. 
	static{		
		workDir = Support.getFvtBaseDir()+"/build/work/WebMethodNameClashTestCase"; 
	}
	
	// framework reqmt.
	public WebMethodNameClashTestCase( String s){
		super(s);
	}

	public WebMethodNameClashTestCase( ){
		super("nothing");
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
    	System.out.println(WebMethodNameClashTestCase.class.getName());
        return new TestSuite(WebMethodNameClashTestCase.class);
       
    }   
    
    // junit runs this before every test
    public void setUp(){
        if( true ) { return; } // 542158
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite    	
    	imp.cleanWorkingDir();
    	setupRun = true;
    }
    
    /**
     * @testStrategy based on 224 sec 3.2.
     * induce a method name clash by annotating a method so it's portname is the same as
     * another method. wsdl should not be generated.
     *   
     * @author btiffany
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testDefaults1() throws Exception{
        System.out.println("This is a negative test.  The java file should compile but wsdl should not be generated.");
    	try {
    		imp.j2w("", "src/annotations/webmethod/testdata/WebMethodNameClash1.java",
    				"annotations.webmethod.testdata.WebMethodNameClash1");
    	} catch( Exception e){
    		e.printStackTrace();
    	}
    	File f=new File(workDir+"/WebMethodNameClash1Service.wsdl");
    	assertFalse("wsdl should not have been generated", f.exists() );
	 		    
    }
}
