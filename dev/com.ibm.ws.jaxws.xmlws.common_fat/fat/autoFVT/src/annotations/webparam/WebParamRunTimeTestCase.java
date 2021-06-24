/*
 * @(#) 1.16 autoFVT/src/annotations/webparam/WebParamRunTimeTestCase.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:09 [7/11/07 13:12:28]
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
 *
 */
package annotations.webparam;
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
 * Checks that when @WebParam annotations are altered and redeployed so as to be
 * inconsistent with the wsdl, the service will not invoke.
 *
 * @author btiffany
 *
 * notes:
 * 12.11.06 d0649.23 - pretty much all dead on 409337.1
 *                   - betadynamic probably dead  410861 - partial wsdl
 *
 *
 * 12.14.06 f0650.10 - didn't look at closely, looks like all still dead on 409337.1
 *          betadynamic still failing to deploy, but 410861 is closed, checking.
 *            - bug in install.xml
 *            - passed.
 *
 * 01.09.07 f0651.08 - works ok until we hit passing params in the header, known problem.
 *          wsgen is throwing exception complaining about in annotation, on webparamanno5.ear,
 *          which is appropriate.
 *
 * 02.05.07 n0704.24 generalchecks was passing except for the very last one, changed
 *          partname.  Repackaged with objectfactory since that one is bare.
 *          Didn't help.      419155
 * 02.09.07 Sam thinks 419155 is failing due to bare problems, not partname,
 *          and will dup to 404081
 *
 * 02.16.07 change test slightly so positive test doesn't have to pass before
 *          neg. tests will run
 *
 *          open 421387 on testBetaDynamicPartialWsdl.  This used to pass
 *          with a zero byte wsdl file.  Now it fails, even if we provide
 *          meaningful schema.
 *
 * 03.19.07 v0710.23 - everything passing except the negative tests, which it looks like I've
 * written incorrectly.  Corrected.
 *
 * 05.02.07 def 427307 is deferred, so disabled testchanged namespace, mode, and partname,
 *          which were failing under this defect.
 */
// TODO: abstract out hardcoded url's
public class WebParamRunTimeTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        private static String workDir = null;
    private static String hostAndPort;
    private static boolean cachedWsdlReady = false;
    private static String cachedWsdlUrl = null;
    private static ImplementationAdapter imp = null;
        private static boolean setupRun = false;        // for junit

        static{
                // set the working directory to be Build/work/<classname>/server
                workDir = WebParamRunTimeTestCase.class.getName();
                int p = workDir.lastIndexOf('.');
                workDir = Support.getFvtBaseDir()+"/build/work/"+workDir.substring(p+1)+"/server";
                imp = Support.getImplementationAdapter("ibm",workDir);

        // figure out host and port.
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);

        //hostAndPort = "http://localhost:19080";


        }

        public static void main(String[] args) throws Exception {
                // we'll only use this for development/debug
                if (true){
                TestRunner.run(suite());
                }
                else{
                        WebParamRunTimeTestCase t = new WebParamRunTimeTestCase();
                        //t.runlegalService();
            t.testLegalImplServiceDynamicWsdl();
            //t.testBetaSEI();
            //t.testLegalImplServiceCachedWsdl();
           // t.testBetaSEICachedWsdl();
            t.testBetaSEIDynamicPartialWsdl();
                }
        }


    // junit runs this before every test
    public void setUp(){
        // if standalone, junit runs this
        // in harness, annotations ant script will run this explicitly with Run_Setup set
        if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
        if (setupRun) return;   // we only want to run this once per suite
        setupRun = true;
        System.out.println ("************* WebParamRunTimeTestCase setting up *************");
        //imp.cleanWorkingDir();
        setupDefault();
        setupClient();
        setupMutants();
                // junit will then check all the url's
                if(!Support.inHarness()) imp.restart();
                System.out.println("******* setup complete ********");

   }

    public void setupDefault(){
        /* moved to ant */
        /*
        // build a valid webservice
        // changed to not package wsdl 10.20, so we can check dynamic wsdl.
                imp.j2w("", "src/annotations/webparam/testdata/WebParamGeneralChecks.java",
                "annotations.webparam.testdata.WebParamGeneralChecks");
        // add src to the war file.
        OS.copy("src/annotations/webparam/testdata/WebParamGeneralChecks.java",
                imp.getWorkingDir()+"/annotations/webparam/testdata");
                imp.war("webparamanno.war", "webparamanno",
                                "annotations.webparam.testdata.WebParamGeneralChecks", false);
                imp.deploy("webparamanno.war");
        */
    }


    public void setupClient(){
       /* moved to buildtest.xml
        *       // build a valid client
        *       // now build a client and helpers w wsdl2java
        *       imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        *       imp.cleanWorkingDir();
        *       imp.w2j("",imp.getWorkingDir()+"/../server/WebParamGeneralChecksService.wsdl");
        *
        *        // now compile the test client written earlier.
        *        imp.compile("src/annotations/webparam/testdata/WebParamRuntimeClient.java", "");
        */
    }

        public void setupMutants(){
                 // now build some invalid webservices that have had the annotation changed
                 // so they are inconsistent with the wsdl.
                 imp.setWorkingDir(imp.getWorkingDir()+"/../server");
                 String path="src/annotations/webparam/testdata/";
                 deployAlteredJava("webparamanno2", path+"WebParamRuntimeChangedName.java");
                 //deployAlteredJava("webparamanno3", path+"WebParamRuntimeChangedNamespace.java");
		 deployAlteredJava("webparamanno4", path+"WebParamRuntimeChangedHeader.java");
		 //deployAlteredJava("webparamanno5",path+"WebParamRuntimeChangedMode.java");
		 //deployAlteredJava("webparamanno6",path+"WebParamRuntimeChangedPartName.java");		
		
		 // this one should work.
		 deployAlteredJava("webparamanno8",path+"WebParamGeneralChecks.java");
	}	
	
	/**
	 * @testStrategy check that properly annotated service can have wsdl and client
	 * generated, and be deployed and invoked.   The client calls methods on the
	 * server side where the name, targetNamespace and partName attributes are set to
	 * non-defaults, and confirms that the method invokes correctly.
     *
     * We use cached wsdl to work around dynamic wsdl not being available yet.
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="check that properly annotated service can have wsdl and client  generated, and be deployed and invoked.   The client calls methods on the server side where the name, targetNamespace and partName attributes are set to non-defaults, and confirms that the method invokes correctly.  We use cached wsdl to work around dynamic wsdl not being available yet. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testLegalImplServiceCachedWsdl() throws Exception {	
        System.out.println("********* starting testLegalService **********");
        runlegalService();
        System.out.println("********* testLegalService passed **********");
	}

    private void checkBasicServiceCameUp(){
        String url=hostAndPort+"/webparamanno/WebParamGeneralChecksService";
        assertTrue("Basic Service did not come up at "+url, Operations.getUrlStatus(url,30));
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testLegalImplServiceDynamicWsdl() throws Exception {
        System.out.println("********* starting testLegalImplServiceDynamicWsdl **********");
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        String url = hostAndPort+ "/webparamanno/WebParamGeneralChecksService?wsdl";
        int result = imp.invoke("annotations.webparam.testdata.WebParamRuntimeClient", url);
        assertTrue("unexpected invocation failure for WebParamGeneralChecks.java at "+url, result==0);
        System.out.println("********* test passed **********");
    }

   void runlegalService() throws Exception {
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        if( !cachedWsdlReady){
            // To test before dynamic wsdl is available, patch up the wsdl
            // we produced when building the client and use that.
            String wsdlFile = Support.getFvtBaseDir()+"/build/work/WebParamRunTimeTestCase/server/WebParamGeneralChecksService.wsdl";
            String wsdl = (Operations.readTextFile(new File(wsdlFile)));
            String newWsdlFile = wsdlFile + "static";
            String relativeUrl = "/webparamanno/WebParamGeneralChecksService";
            wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+ relativeUrl );
            Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
            cachedWsdlUrl = "file:" + newWsdlFile;
            cachedWsdlReady = true;
        }
        //String serviceurl = imp.getServerURL()+"/webparamanno/webparamanno?wsdl";
        int result = imp.invoke("annotations.webparam.testdata.WebParamRuntimeClient", cachedWsdlUrl);
        assertTrue("unexpected invocation failure for WebParamGeneralChecks.java at "+cachedWsdlUrl, result==0);
    }



	/**
	 * @testStrategy - verify that service with wrong Name parameter won't invoke.
     * To avoid passing when the entire server is toast, we call a legal service first.
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- verify that service with wrong Name parameter won't invoke.  To avoid passing when the entire server is toast, we call a legal service first. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testNameChange()  throws Exception {
	 	// now try some that should not work
        System.out.println("********* starting testNameChange **********");
        System.out.println("** first, we check that the legal service is working** ");
        checkBasicServiceCameUp();
        System.out.println("** legal service ok, proceeding to negative test** ");
		imp.setWorkingDir(imp.getWorkingDir()+"/../client");		
		String serviceurl = hostAndPort+"/webparamanno2/webparamanno2?wsdl";		
		int result = imp.invoke("annotations.webparam.testdata.WebParamRuntimeClient", serviceurl);;
	 	assertFalse("unexpected invocation success for WebParamRuntimeChangedName.java at"+serviceurl, result==0);
        System.out.println("********* test passed **********");
	 	
	}
	/**
	 * @testStrategy - verify that service with wrong Namespace parameter won't invoke
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- verify that service with wrong Namespace parameter won't invoke ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testNamespaceChange()  throws Exception {

	 	// now try some that should not work
        System.out.println("********* starting testNamespaceChange **********");

        System.out.println("** first, we check that the legal service is working** ");
        checkBasicServiceCameUp();
        System.out.println("** legal service ok, proceeding to negative test** ");

		imp.setWorkingDir(imp.getWorkingDir()+"/../client");		
		String serviceurl = hostAndPort+"/webparamanno3/webparamanno3?wsdl";		
		int result = imp.invoke("annotations.webparam.testdata.WebParamRuntimeClient", serviceurl);;
	 	assertFalse("unexpected invocation success for WebParamRuntimeChangedNamespace.java at "+serviceurl, result==0);
        System.out.println("********* testNameSpaceChange passed **********");
	 	
	}
	
	/**
	 * @testStrategy - verify that service with wrong header parameter won't invoke
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- verify that service with wrong header parameter won't invoke ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testHeaderChange() throws Exception {
        System.out.println("********* starting testHeaderChange **********");

        System.out.println("** first, we check that the legal service is working** ");
        checkBasicServiceCameUp();
        System.out.println("** legal service ok, proceeding to negative test** ");

		imp.setWorkingDir(imp.getWorkingDir()+"/../client");		
		String serviceurl = hostAndPort+"/webparamanno4/webparamanno4?wsdl";		
		int result = imp.invoke("annotations.webparam.testdata.WebParamRuntimeClient", serviceurl);;
	 	assertFalse("unexpected invocation success for WebParamRuntimeChangedHeader.java at"+serviceurl, result==0);
        System.out.println("********* testHeaderChange passed **********");

	 	
	}
	
	/**
	 * @testStrategy - verify that service with wrong mode parameter won't invoke
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- verify that service with wrong mode parameter won't invoke ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testModeChange() throws Exception {
        System.out.println("********* starting testModeChange **********");

        System.out.println("** first, we check that the legal service is working** ");
        checkBasicServiceCameUp();
        System.out.println("** legal service ok, proceeding to negative test** ");

		imp.setWorkingDir(imp.getWorkingDir()+"/../client");		
		String serviceurl = hostAndPort+"/webparamanno5/webparamanno5?wsdl";		
		int result = imp.invoke("annotations.webparam.testdata.WebParamRuntimeClient", serviceurl);;
	 	assertFalse("unexpected invocation success for WebParamRuntimeChangedMode.java at"+serviceurl, result==0);
        System.out.println("********* testModeChange passed **********");
	}
	
	/**
	 * @testStrategy - verify that service with wrong Name partname won't invoke.
	 * follow that with a valid invoke attempt to make sure we didn't cause a server failure.
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- verify that service with wrong Name partname won't invoke. follow that with a valid invoke attempt to make sure we didn't cause a server failure. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testPartnameChange() throws Exception {
	 	// now try some that should not work
        System.out.println("********* starting testPartNameChange **********");

        System.out.println("** first, we check that the legal service is working** ");
        checkBasicServiceCameUp();
        System.out.println("** legal service ok, proceeding to negative test** ");

		imp.setWorkingDir(imp.getWorkingDir()+"/../client");		
		String serviceurl = hostAndPort+"/webparamanno6/webparamanno6?wsdl";		
		int result = imp.invoke("annotations.webparam.testdata.WebParamRuntimeClient", serviceurl);;
	 	assertFalse("unexpected invocation success for WebParamRuntimeChangedPartName.java at"+serviceurl, result==0);
	 	
	 	// now try  a valid one
	 	serviceurl = hostAndPort+"/webparamanno8/webparamanno8?wsdl";		
		result = imp.invoke("annotations.webparam.testdata.WebParamRuntimeClient", serviceurl);;
	 	assertTrue("unexpected invocation failure for WebParamGeneralChecks.java at"+serviceurl, result==0);
        System.out.println("********* testPartNamerChange passed **********");
	}

    /**
     * @testStrategy - change name and namespace on a couple of methods with
     * an SEI annotation, and verify that we can deploy and invoke the service
     * correctly.
     *
     * This special test works around all the beta limitations.
     *
     * unlike all the other tests, this one is completely built in ant.
     *
     *
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- change name and namespace on a couple of methods with an SEI annotation, and verify that we can deploy and invoke the service correctly.  This special test works around all the beta limitations.  unlike all the other tests, this one is completely built in ant.   ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBetaSEI(){
        System.out.println("********* starting testBetaSEI **********");
		// Liberty does not support SEI/?wsdl wsdl URL style
        String serviceurl = hostAndPort+"/webparamannobeta/webparamannobeta?wsdl";
        int result = (new annotations.webparam.testdata.WPBClient()).runtest(serviceurl);
        assertTrue("unexpected invocation failure for WPBClient at"+serviceurl, result==0);
        System.out.println("********* test passed **********");
    }

    /*
     * passed on d0648.19
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBetaSEICachedWsdl() throws Exception {
        System.out.println("********* starting testBetaSEICachedWsdl **********");

        // To test before dynamic wsdl is available, patch up the wsdl from the src tree
        // and use that.
        String cachedir = Support.getFvtBaseDir()+"/build/work/webparambeta/cachedWsdl";
        OS.mkdir(cachedir);
        OS.copy(Support.getFvtBaseDir()+"/src/annotations/webparam/testdata/WPBImplService*",
                cachedir);
        String wsdlFile = cachedir+"/WPBImplService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webparamannobeta/webparamannobeta";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+ relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        cachedWsdlUrl = "file:" + newWsdlFile;
        //String serviceurl = hostAndPort+"/webparamannobeta/webparamannobeta/wsdl/WPBImplService.wsdl";
        int result = (new annotations.webparam.testdata.WPBClient()).runtest(cachedWsdlUrl);
        assertTrue("unexpected invocation failure for WPBClient ", result==0);
        System.out.println("********* test passed **********");

    }

    /**
     * @testStrategy same as testBetaSEI except we supply a zero byte wsdl file,
     * which forces the server to generate wsdl.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="same as testBetaSEI except we supply a zero byte wsdl file, which forces the server to generate wsdl. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBetaSEIDynamicPartialWsdl(){
        System.out.println("*********  (def 421387) starting testBetaSEIDynamicPartialWsdl **********");
        String serviceurl = hostAndPort+"/webparamannobeta2/webparamannobeta?wsdl";
        int result = (new annotations.webparam.testdata.WPBClient()).runtest(serviceurl);
        assertTrue("unexpected invocation failure for WPBClient at"+serviceurl, result==0);
        System.out.println("********* test passed **********");
    }

			
	/**
	 * replace correct impl class file with a modified file.
	 * @param warfilename - name of warfile, without extension
	 * @param Filepath - relative path to file from fvt dir.
	 */
	private void deployAlteredJava(String warfileName, String Filepath){		   	
	   	int rc = 0;	   	
	
		rc= OS.copy(Filepath,
				 imp.getWorkingDir()+"/annotations/webparam/testdata/WebParamGeneralChecks.java");
		assertTrue("copy failed", rc==0);		
		//OS.runs(imp.getWorkingDir(), "javac -d . annotations/webparam/testdata/WebParamGeneralChecks.java");
        imp.compile(imp.getWorkingDir()+ "/annotations/webparam/testdata/WebParamGeneralChecks.java", ".");		
		//assertTrue("compile failed", OS.getLastRC() ==0);
		
		// deploy
        System.out.println("creating war/ear file for "+Filepath);
		imp.war(warfileName+".war", warfileName, "annotations.webparam.testdata.WebParamGeneralChecks", true);		
		imp.deploy(warfileName+".war");
			
	}

    /**
     * The suite method returns the tests to run from this suite.
     * all methods starting with "test" will be be run.     *
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
            return new TestSuite(WebParamRunTimeTestCase.class);
    }

    public WebParamRunTimeTestCase(String str) {
        super(str);
    }

    public WebParamRunTimeTestCase(){
        super();
    }
}
