/*
 * autoFVT/src/annotations/webmethod/WebMethodRunTimeTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 05/24/2007  jramos      440922             Integrate ACUTE
 * 08/14/2007  btiffany    459641             add constructor for framework
 * 08/7/2008   btiffany     542158            disable old setup code.
 *
 */
package annotations.webmethod;
import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.ImplementationAdapter;
import annotations.support.OS;
import annotations.support.Support;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * 
 * checks that an annotated webservice runs with the \@WebMethod 
 * annotation parameters interpreted correctly.
 * The annotation is used to rename the method, and the client is called against
 * that name.  If all went well, the client should get a response.  
 * 
 * @author btiffany
 * 
 * notes: 
 * 12.11.06 - still failing on 409192 - throws ambiguous exception on invalid soap:address url.
 *  however, we need to make the sucker valid and see what happens.
 *   now failing w 392386
 *   
 *   wsdl mismatch failing w 390939.1
 *   
 * 12.14.06 f0650.10
 *   testService, testServiceCachedWsdl  failing 392386 - although we get better error message now.
 *   testWsdlMismatch is passing
 *   
 * 01.23.07 h0702.29  failing w 416110
 *          n0703.10  misrouting to wrong methods, probably soap:action problem.
 * 02.05.07 n0704.24  was working, but when an action was added back in that
 *                    did not match the method name, app does not deploy. But it 
 *                    deploys on a p build on DS's machine on p0704.07
 *                    restart server, ok now, passed!
 *
 */

public class WebMethodRunTimeTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	private static String workDir = null;
	private static ImplementationAdapter imp = null; 
	private static boolean setupRun = false; 	// for junit
    private static String hostAndPort = null;
	
	static{
		workDir = Support.getFvtBaseDir()+"/build/work/WebMethodRunTimeTestCase/server";
		imp = Support.getImplementationAdapter("ibm",workDir);
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                + server.getPortMap().get(Ports.WC_defaulthost);
	}
	
	public WebMethodRunTimeTestCase( String s){
		super(s);
		
	}
	
	public WebMethodRunTimeTestCase(){
		super("nothing");
		
	}

	public static void main(String[] args) throws Exception {	
		if(true){
			TestRunner.run(suite());
		} else{
			//new WebMethodRunTimeTestCase().testServiceCachedWsdl();
            new WebMethodRunTimeTestCase().testService();
            new WebMethodRunTimeTestCase().testWsdlMismatch();

		}

	}	

    /**
     * The suite method returns the tests to run from this suite.
     * The Java2WsdlTestCaseCase class is specified which means
     * all methods starting with "test" will be be run.
     *
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {    
            return new TestSuite(WebMethodRunTimeTestCase.class); 
    }   
    
    // junit runs this before every test
    public void setUp(){
        if (true) { return; } // 524158
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite
       	setupRun = true;
    	imp.cleanWorkingDir();
    	setupServer();
    	setupClient();
    	setupWsdlMismatch();  
    	if (!Support.inHarness()) imp.restart();
 
    }
    
	public void setupServer(){    	    	  	
    	imp.cleanWorkingDir();
        OS.copy("src/annotations/webmethod/testdata/WebMethodRuntime.java",
                imp.getWorkingDir()); 
		imp.j2w("", "src/annotations/webmethod/testdata/WebMethodRuntime.java", 
				"annotations.webmethod.testdata.WebMethodRuntime");
       // replace the generated wsdl with ours, since beta can't handle
       // the xsd stuff yet. 
       /* 
       OS.copy("src/annotations/webmethod/testdata/WebMethodRuntimeService.wsdl",
                    imp.getWorkingDir());
       OS.delete(imp.getWorkingDir()+"/*xsd");
       */
		imp.war("webmethodanno.war",
                "webmethodanno", 
                "annotations.webmethod.testdata.WebMethodRuntime", 
                 true);		
		imp.deploy("webmethodanno.war");		
	}
	
	/*
	 * if everything worked right, we should be able to invoke the service
	 */
	public void setupClient(){
		// 	now generate the client support files
		imp.setWorkingDir(imp.getWorkingDir()+"/../client");
		imp.cleanWorkingDir();
		//imp.w2j("",imp.getServerURL()+"/webmethodanno/webmethodanno?wsdl");
		imp.w2j("",imp.getWorkingDir()+"/../server/WebMethodRuntimeService.wsdl");
		
		 // now compile the test client written earlier. 
		 imp.compile("src/annotations/webmethod/testdata/WebMethodRuntimeClient.java", "");		 
	}
	
	/**
	 * @testStrategy check that a service with a method renamed through annotation works correctly.
     * 
     * uses webmethodanno.ear at /webmethodanno/webmethodanno
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="check that a service with a method renamed through annotation works correctly.  uses webmethodanno.ear at /webmethodanno/webmethodanno ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testService(){		
		System.out.println("************ running testService()************");
		imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        
		int b = imp.invoke("annotations.webmethod.testdata.WebMethodRuntimeClient",
				hostAndPort+"/webmethodanno/webmethodanno?wsdl");
                
        /*
        int b = imp.invoke("annotations.webmethod.testdata.WebMethodRuntimeClient",
                hostAndPort+"/webmethodanno/webmethodanno/wsdl/WebMethodRuntimeService.wsdl");
        */        
        
        /*
        int b = imp.invoke("annotations.webmethod.testdata.WebMethodRuntimeClient",
                "file:/eclipse/WautoFVT/build/work/WebMethodRunTimeTestCase/server/WebMethodRuntimeService.wsdl"
                );
        */         


		assertTrue("409192(?) - client invocation failed", b==0);
 
        System.out.println("************ test passed **********");
		 
	}   
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServiceCachedWsdl() throws Exception {
        // To test before dynamic wsdl is available, patch up the wsdl
        // we produced when building the client and use that. 
        System.out.println("************ running testServiceCachedWsdl()************");
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        String wsdlFile = Support.getFvtBaseDir()+"/build/work/WebMethodRunTimeTestCase/server/WebMethodRuntimeService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webmethodanno/webmethodanno";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        String url = "file:" + newWsdlFile;
        
        int b = imp.invoke("annotations.webmethod.testdata.WebMethodRuntimeClient", url );
        assertTrue("392386? - client invocation failed", b==0);
        System.out.println("************ test passed **********");
    }

	/**
	 * set up for the wsdl mismatch test
	 *
	 */
	public void setupWsdlMismatch(){
		imp.setWorkingDir(imp.getWorkingDir()+"/../server2");
		imp.cleanWorkingDir();		
		// build the service like we did before.
		imp.j2w("", "src/annotations/webmethod/testdata/WebMethodRuntime.java", 
				"annotations.webmethod.testdata.WebMethodRuntime");
		
		// now replace the class file without regenerating the wsdl.
		OS.delete(imp.getWorkingDir()+"/webmethod/testdata/WebMethodRuntime.class");
		OS.copy("src/annotations/webmethod/testdata/WebMethodRuntime2.java",
				 imp.getWorkingDir()+"/annotations/webmethod/testdata/WebMethodRuntime.java");
		
		imp.compile(imp.getWorkingDir()+"/annotations/webmethod/testdata/WebMethodRuntime.java", ".");
		
		// and deploy it. 
		imp.war("webmethodanno2.war","webmethodanno2", "annotations.webmethod.testdata.WebMethodRuntime", true);		
		imp.deploy("webmethodanno2.war");
		
	}
	
	/**
	 * @testStrategy test that a revised method annotation that mismatches wsdl
	 * (due to the method being renamed) will not invoke.  
	 * Testing of methods in wsdl but not in the bean are NOT done here,
	 * see webmethod_g2.runtime for THAT. 
	 * 
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="test that a revised method annotation that mismatches wsdl (due to the method being renamed) will not invoke. Testing of methods in wsdl but not in the bean are NOT done here, see webmethod_g2.runtime for THAT. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testWsdlMismatch(){
				
		System.out.println("************ running testWsdlMismatch()************");		
		// if engine is really good, it won't deploy
		// if it deploys, may have to test with invoke.
		//boolean result = Support.fetchurl(imp.getServerURL()+"/webmethodanno/webmethodanno?wsdl", 3);
		//assertFalse("service should not have deployed, but did ", result);
		
		// it's not that good. 
		// we also need to check which type of exception is thrown, but because invoke()
		// starts a separate jvm (only way to get jax-ws generated classes onto the
		// classpath), we won't get the exception back here.  Instead, the 
		// client has to check it and set a return code, which we CAN check here.
	
		imp.setWorkingDir(imp.getWorkingDir()+"/../client");
		
		int b = imp.invoke("annotations.webmethod.testdata.WebMethodRuntimeClient",
			hostAndPort+"/webmethodanno2/webmethodanno2?wsdl");
		
		assertFalse("client invocation should not have succeeded", b==0);
		assertTrue("client should have received WebService exception", b >= 10);
		
		System.out.println("************ test passed **********");
	}
	
	/**
	 * @testStrategy - check that when the exclude parameter is changed from false to true on
	 * an existing war file, and only the implementation bean recompiled, that the runtime honors
	 * the changed annotation.  This test is performed elsewhere, in the
	 * webmethod_g2RuntimeTest.testSwapAndExclude4() method, and is only noted here for documentation
	 * completeness.  
	 * 
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- check that when the exclude parameter is changed from false to true on an existing war file, and only the implementation bean recompiled, that the runtime honors the changed annotation.  This test is performed elsewhere, in the webmethod_g2RuntimeTest.testSwapAndExclude4() method, and is only noted here for documentation completeness.  ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testRuntimeExclusionChange(){
		// documentation placeholder only
	}

 
}
