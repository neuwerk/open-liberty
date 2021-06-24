/*
 * @(#) 1.8 autoFVT/src/annotations/webendpoint/WebEndpointTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/7/07 20:15:17 [8/8/12 06:54:50]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 *                         LIDB3296.31.01     new file
 * 08/15/2007  btiffany    459641             add noargs constructor for runSetups call
 * 12/07/2007  ulbricht    488152             add direct access to method
 *
 */

package annotations.webendpoint;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.AnnotationHelper;
import annotations.support.ImplementationAdapter;
import annotations.support.Support;
/**
 * @throws Exception Any kind of exception
 *
 * @testStrategy - check that @WebEndPoint appears on the getport methods of the generated
 *  service (client) class, and has the proper value.  There isn't much else to check here, as the standard
 * doesn't have anything to say about how this is to be used.
 *
 * @author btiffany
 *
 */
public class WebEndpointTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        private static String workDir = null;
        private static ImplementationAdapter imp = null; 
        private static boolean setupRun = false;        // for junit

        // framework reqmt.
        public WebEndpointTestCase( String s){
            super(s);
        }
        public WebEndpointTestCase(){
            super("nothing");
        }
        
        protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
            System.out.println("Do not need suiteSetup since no application is installed");    
        }

        static{
                // set the working directory to be <work area>/<classname>
                workDir = WebEndpointTestCase.class.getName();
                int p = workDir.lastIndexOf('.');
                workDir = Support.getFvtBaseDir()+"/build/work/"+workDir.substring(p+1)+"/fromwsdl";
				imp = Support.getImplementationAdapter("ibm",workDir);
        }

        public static void main(String[] args) throws Exception {
                TestRunner.run(suite());
        }


    /**
     * junit needs this.
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(WebEndpointTestCase.class);
    }

    // junit will run this before every test
    public void setUp(){
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite
    	setupRun = true;
    	imp.cleanWorkingDir();
    	imp.w2j("", "src/annotations/webendpoint/testdata/WebEndpointTestImplService.wsdl");
    	//	 insert setup for anything that needs to be installed and deployed on server here.
    	
    	// if we are not in the harness, restart server after deployment
    	//if (!Support.inHarness()) imp.restart();
    }

    /**
     * go look for the service class generated by wsdl2java and make sure the annotation is present
     * @testStrategy - check that @WebEndPoint appears on the getport methods of the generated
     *  service (client) class, and has the proper value.  There isn't much else to check here, as the standard
     * doesn't have anything to say about how this is to be used.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- check that @WebEndPoint appears on the getport methods of the generated service (client) class, and has the proper value.  There isn't much else to check here, as the standard doesn't have anything to say about how this is to be used. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testGeneratedJava() throws Exception {
    	System.out.println("********** testGeneratedJava() is running **********");
    	AnnotationHelper anh = new AnnotationHelper("annotations.webendpoint.testdata.WebEndpointTestImplService", imp.getWorkingDir());
    	String expect ="WebEndpointTestImplPort";
    	String actual= anh.getMethodElement("getWebEndpointTestImplPort", null, "WebEndpoint", "name");
    	assertTrue("annotation value expected was: "+expect +"but is: "+actual
    				, actual.compareTo(expect)==0);    	
    }

}
