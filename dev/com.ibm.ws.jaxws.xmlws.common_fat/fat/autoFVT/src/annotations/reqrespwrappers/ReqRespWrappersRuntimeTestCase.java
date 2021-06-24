/*
 * autoFVT/src/annotations/reqrespwrappers/ReqRespWrappersRuntimeTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 
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
 * see cmvc history        LIDB3296.31.01     new file
 * 05/24/2007  jramos      440922             Integrate ACUTE
 * 8/14/2007   btiffany                       constuctor changes for framework
 *
 */

package annotations.reqrespwrappers;
import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.ImplementationAdapter;
import annotations.support.Support;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * @testStrategy - given java with these annotations, generate a web service
 * and client from it with all values non-defaulted,, and invoke it to
 * assure that all the namespaces and packages change correctly.  
 *   
 * @author btiffany
 * 
 * notes: 
 * 12/9 d0649.23:
 * dyn case still failing w schema retrieval problem.
 * static case failing on soapbody dispatching problem 408954.2, duped to 404081
 * 
 * 12/13 f0650.10
 * now we're in wrapped mode, and also getting a failure on the server side. 411548.
 * dynamic looks the same. 
 * 
 * 01/17 g0702.02 Now we have soap:action dispatching problems blocking verification 
 * of 411548. May be able to attempt on cached wsdl with >=g0703.05, ref 401723.
 * 
 * 01.22 h0702.29 All these old defects are cleared or inapplicable. 
 * Using wrapped mode now so 404081 is n/a.   
 * Getting new sandesha exception on server side.  Opened 416057.
 * 
 * 01.23 N0703.10 echo2 getting endpoint reference not found.  Since it get's morphed 
 * into bare, it's almost certainly 404081.
 * 
 * 02.04 n0704.24 def 411548 is cleared.  Still have epr not found on echo2. Adding 
 * ObjectFactory to ear.  Open def 419104
 * 
 * 03.07 419104 will be coming back as a dup, but problem persists. Open 419104.1
 * just to report some logging weaknesses as sev3.
 * Add some more methods to do better diagnosis.
 * Remove fully qualified annotation test, as, 
 *  - per jsr224 7.3
 *      "When staring from java... only the classname element is required in this
 *     case" -- That test is therefore invalid and the client attempts it
 *     but does not check it's return code.       
 * 
 * 
 *
 */
public class ReqRespWrappersRuntimeTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	private static String workDir = null;
	private static ImplementationAdapter imp = null; 
	private static boolean setupRun = false; 	// for junit
	
	static{
		// set the working directory to be <work area>/<classname>
		workDir = ReqRespWrappersRuntimeTestCase.class.getName();
		int p =	workDir.lastIndexOf('.');
		workDir = Support.getFvtBaseDir()+"/build/work/"+workDir.substring(p+1)+"/server"; 
		imp = Support.getImplementationAdapter("ibm",workDir);
	}
	
	// framework reqmt.
	public ReqRespWrappersRuntimeTestCase( String s){
		super(s);
	}
	
	public ReqRespWrappersRuntimeTestCase( ){
		super("nothing");
	}

	public static void main(String[] args) throws Exception {	
        if (false){
            TestRunner.run(suite());
        }
        else{
           new ReqRespWrappersRuntimeTestCase().testServiceWithDyamicWsdl();
            new ReqRespWrappersRuntimeTestCase()._testServiceWithCachedWsdl();
        }
	}
	

    /**
     * junit needs this.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {  
            return new TestSuite(ReqRespWrappersRuntimeTestCase.class); 
    }   
    
    // junit will run this before every test
    public void setUp(){
    	
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite 
    	setupRun = true;
    	//imp.cleanWorkingDir();
    	imp.setWorkingDir(imp.getWorkingDir()+"/../server");
    	// setupServer();
    	setupClient();
    	// insert setup work here
    	if (!Support.inHarness()) imp.restart();
    	
    }
    
	public void setupServer(){   
        /* moved to buildtest.xml 
    	imp.cleanWorkingDir();	
        // copy src file to workdir so it goes into war 
        annotations.support.OS.copy("src/annotations/reqrespwrappers/server/ReqRespRuntimeCheck.java",
                                     imp.getWorkingDir());
       	imp.j2w("","src/annotations/reqrespwrappers/server/ReqRespRuntimeCheck.java",
		   "annotations.reqrespwrappers.server.ReqRespRuntimeCheck"); 
		imp.war("reqrespanno.war","reqrespanno", "annotations.reqrespwrappers.server.ReqRespRuntimeCheck", false);		
		imp.deploy("reqrespanno.war");
        */		
	}
	

	public void setupClient(){
		// 	now generate the client support files
		imp.setWorkingDir(imp.getWorkingDir()+"/../client");
		// MIGRATION CHANGE: Liberty build ID cannot run ant command on build engines, so do not clean
		// client working directory to let the artifacts generated during case compilation phase there.
		//imp.cleanWorkingDir();		
		imp.w2j("",imp.getWorkingDir()+"/../server/ReqRespRuntimeCheckService.wsdl");
		
		 // now compile the test client written earlier. 
		 imp.compile("src/annotations/reqrespwrappers/client/ReqRespRuntimeClient.java", "");		 
	}
    
	/**
	 * @testStrategy: verify that a service impl. class annotated with these annotations in both 
	 * bare and fully defined forms can have server classes and client generated, and be deployed
	 * and invoked. 
	 * @throws Exception
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServiceWithDyamicWsdl() throws Exception {
    	System.out.println("********** testServiceWithDynamicWsdl() is running **********");
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        
        IAppServer server = QueryDefaultNode.defaultAppServer;
        String hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                + server.getPortMap().get(Ports.WC_defaulthost);
        
        //String hostAndPort= com.ibm.ws.wsfvt.build.tools.AntProperties.getServerURL();
        String url= hostAndPort + "/reqrespanno/ReqRespRuntimeCheckService?wsdl";
 
		// see if our service  deployed 
		boolean result = Operations.getUrlStatus(url, 30);
		assertTrue("service did not deploy: "+url, result);
		
		// see if we can invoke the client.		
		
		int b = imp.invoke("annotations.reqrespwrappers.client.ReqRespRuntimeClient",
				url);
		assertTrue("client invocation returned nonzero", b==0);        
        System.out.println("********** testServiceWithDynamicWsdl() passed **********");
		 
    }
    
    /**
     * use a wsdl file on the client so we can test function until dynamic wsdl works.
     * See other testcase for the rest of the story....
     * 
     * We're using this for debugging with tcpmon and patching the soapAction
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testServiceWithCachedWsdl() throws Exception {       
        System.out.println("********** testServiceWithCachedWsdl() is running **********");
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");
       
        IAppServer server = QueryDefaultNode.defaultAppServer;
        String hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                + server.getPortMap().get(Ports.WC_defaulthost);
        
        //String hostAndPort= com.ibm.ws.wsfvt.build.tools.AntProperties.getServerURL();
        String relativeUrl = "/reqrespanno/ReqRespRuntimeCheckService";
        
        // To test before dynamic wsdl is available, patch up the wsdl
        // we produced when building the client and use that. 
        String wsdlFile = imp.getWorkingDir()+"/../server/ReqRespRuntimeCheckService.wsdl";
        if (wsdlFile.contains(":")) {
            wsdlFile = wsdlFile.substring(2);  // strip drive letter
        }
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        //wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+ relativeUrl );
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080" + relativeUrl );
        wsdl = wsdl.replace("<soap:operation soapAction=\"\"/>","<!-- soap action removed -->");
        //wsdl = wsdl.replace("<soap:operation soapAction=\"\"/>","<soap:operation soapAction=\"frustrate_me\"/>");
        
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        String wsdlurl = "file:" + newWsdlFile;
        System.out.println("wsdlurl = "+wsdlurl);
        
 
        // see if our service  deployed  - should get axis2 status page
        String url= hostAndPort + relativeUrl;
        boolean result = Operations.getUrlStatus(url, 30);
        assertTrue("service did not deploy", result);
        
        
        
        // see if we can invoke the client.
        
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        int b = imp.invoke("annotations.reqrespwrappers.client.ReqRespRuntimeClient",
                wsdlurl);
        assertTrue("def 404081 on echo2 invoke - client invocation returned nonzero", b==0);
        System.out.println("********** test  passed **********");
         
    }
    
    
}
