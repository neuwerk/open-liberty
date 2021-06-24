/*
 * @(#) 1.5 autoFVT/src/annotations/Template.java, WAS.websvcs.fvt, WASX.FVT 7/28/06 09:55:12 [7/11/07 13:11:25]
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
 *
 */

package annotations;
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
 * @author your name in lights
 *
 */
public class Template extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	private static String workDir = null;
	ImplementationAdapter imp = Support.getImplementationAdapter("default",workDir);
	private static boolean setupRun = false; 	// for junit
	
	static{
		// set the working directory to be <work area>/<classname>
		workDir = Template.class.getName();
		int p =	workDir.lastIndexOf('.');
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
         return new TestSuite(Template.class); 
    }   
    
	public Template(String str) {
		super(str);
	}
	
	public Template(){
		super();
	}
    
    // junit will run this before every test
    public void setUp(){
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite 
    	setupRun = true;
    	imp.cleanWorkingDir();     	
    	//	 insert setup for anything that needs to be installed and deployed on server here.
    	
    	// if we are not in the harness, restart server after deployment
    	if (!Support.inHarness()) imp.restart();
    }
    
    /** @testStrategy document your strategy here
     * 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSomething() throws Exception {
    	System.out.println("********** test???() is running **********");
    	// insert a test here, add more testXXX methods for more tests.    
    }
    
    
}
