/*
 * autoFVT/src/annotations/webservice/WebServiceRunTimeTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 08/03/2006      "                    "                         changed basic class test to supply wsdl for beta.
 *                                                                                              - added interface test with supplied wsdl for beta.
 * 03/05/2007      "                          add javac option for Zos.
 * 05/24/2007  jramos      440922             Integrate ACUTE
 * 10/23/2007  jramos      476750             Use ACUTE 2.0 api and TopologyDefaults
 * 08/07/2008 btiffany    542159              Remove old setup methods, malfunctioning on z/i
 */
package annotations.webservice;
import java.io.File;

import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.ImplementationAdapter;
import annotations.support.OS;
import annotations.support.Support;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.websphere.simplicity.OperatingSystem;
import common.utils.topology.IAppServer;
import common.utils.topology.IMachine;
import common.utils.topology.Ports;
import common.utils.topology.TopologyActions;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 *
 * checks that an annotated webservice runs with the \@WebService
 * annotation parameters interpreted correctly.
 * Does this by invoking a client that looks for the appropriate features that
 * the parameters would have affected, such as package name, class name, etc.
 *
 * Also passes in a variety of mismatched wsdl and java, and make sure service does not deploy.
 * One thing we don't check is extra methods in wsdl not in class.
 * That's explored further in webmethod_g2.runtime
 *
 * @author btiffany
 * Recent change history:
 * 2006-11-08   btiffany    add workaround for wsgen problem on z/os.
 *
 * notes:
 * 12.11 - d0649.23 -
 *   testannotatedServiceWithCachedWsdl -  passed
 *   testAnnotatedServiceWithWsdl - should fail w 390939.1
 *   testBasicService    - passed
 *   testsuppliedwsdlsei - passed
 *   testgeneratedWsdlOnImplWithAnnotations - same service as cached, expect 390939.1
 *   t.testGeneratedWsdlSEI - works with cache hack.
 *
 *
 * 12.18.06 - f0650.10
 *     testAnnotatedServiceWithWsdl - passed
 *     testgeneratedWsdlOnImplWithAnnotations - passed
 *     everything looks good except testchangedwsdl, which looks like a build problem.
 *
 * 02.19.06 r0706.21 removed some invalid tests, everything else looks good.
 *
 * 03.20.06 change erroneous zos encoding option -encoding=ISO8859-1 --> -encoding ISO8859-1
 *
 */

public class WebServiceRunTimeTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        private static String workDir = null;
        private static String initialWorkDir = null;
        private static String hostAndPort = null;
        private static ImplementationAdapter imp = null; 
        private static boolean setupRun = false;        // for junit


        // framework reqmt.
        public WebServiceRunTimeTestCase( String s){
            super(s);
        }
        // for bw compat with runsetups
        public WebServiceRunTimeTestCase(){
            super("setup");
        }

        static{
                // set the working directory to be Build/work/<classname>/server
                workDir = WebServiceRunTimeTestCase.class.getName();
                int p = workDir.lastIndexOf('.');
                workDir = Support.getFvtBaseDir()+"/build/work/"+workDir.substring(p+1)+"/serverb";
				imp = Support.getImplementationAdapter("default",workDir);

        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);

        }

        public static void main(String[] args) throws Exception {
                // we'll only use this for development/debug
                if (false){
                TestRunner.run(suite());
                }
                else{
                        WebServiceRunTimeTestCase t = new WebServiceRunTimeTestCase();
            t.testBasicService();
            t.testSuppliedWsdlSEI();
            t.testGeneratedWsdlSEI();
            t.testAnnotatedServiceWithCachedWSDL();
            t.testAnnotatedServiceWithWsdl();
            t.testGeneratedWsdlOnImplWithAnnotations();
            // neg tests that should all fail
            t.testChangedEndpoint();
            t.testChangedName();
            t.testChangedWsdlLocation();
		
		}	
	}
	
    // junit runs this before every test
    public void setUp() throws Exception {
        if (true) {return; } //542159 - this is all in ant now.
    	// if standalone, junit runs this
    	// in harness, annotations ant script will run this explicitly with Run_Setup set
    	if (Support.inHarness() && System.getProperty("Run_Setup")==null ) return;
    	if (setupRun) return;	// we only want to run this once per suite
    	setupRun = true;
    	System.out.println ("************* WebServiceRunTimeTestCase setting up *************");
        setUpBasicService();
        setUpSeiService();
        setUpOthers();

         // junit will then check all the url's
         if(!Support.inHarness()) imp.restart();

         System.out.println("******* setup complete ********");
    }
    private void setUpBasicService() throws Exception {
        /*
        imp.setWorkingDir(imp.getWorkingDir()+"/../serverb");
    	imp.cleanWorkingDir();
        // build a super basic web service with default annotation
        OS.copy("src/annotations/webservice/testdata/WebServiceBasicRuntime.java",
                imp.getWorkingDir());
        imp.j2w("", "src/annotations/webservice/testdata/WebServiceBasicRuntime.java",
        "annotations.webservice.testdata.WebServiceBasicRuntime");

        WsdlFlattener.flatten(imp.getWorkingDir()+"/WebServiceBasicRuntimeService.wsdl");
        OS.copy(imp.getWorkingDir()+"/WebServiceBasicRuntimeService.wsdlflat",
                imp.getWorkingDir()+"/WebServiceBasicRuntimeService.wsdl");
        OS.delete(imp.getWorkingDir()+"/WebServiceBasicRuntimeService.wsdlflat");

        imp.war("websvcannob.war",
                "websvcannob",
                "websvcannob",
                "annotations.webservice.testdata.WebServiceBasicRuntime",
                "",
                "WebServiceBasicRuntimeService.wsdl");
        imp.deploy("websvcannob.war");


        // now build the basic service's client and helpers w wsdl2java
        imp.setWorkingDir(imp.getWorkingDir()+"/../clientb");
        imp.cleanWorkingDir();
        imp.w2j("",imp.getWorkingDir()+"/../serverb/WebServiceBasicRuntimeService.wsdl");

         // now compile the test client written earlier.
         OS.copy(Support.getFvtBaseDir()+"/src/annotations/webservice/testdata/WebServiceBasicRuntimeClient.java",
                 imp.getWorkingDir());
         imp.compile("src/annotations/webservice/testdata/WebServiceBasicRuntimeClient.java", "");
         */
    }

    private void setUpSeiService(){
        /*
        // now we try testing a service based on an SEI, instead of a class.
        // This is a  simple case, single method.

        imp.setWorkingDir(imp.getWorkingDir()+"/../serverIf");
        imp.cleanWorkingDir();

        // build a valid webservice using an interface.  This time we do not supply the wsdl.
        OS.mkdir(imp.getWorkingDir()+"/annotations/webservice/testdata");
        // we can't put the src files in the same dir as class files,
        // due to z series problem - wsgen will try to read them, which will fail on z, because
        // they are in wrong encoding for z.
        OS.copy("src/annotations/webservice/testdata/WebServiceRuntimeIfc.java_nowsdl",
                imp.getWorkingDir()+"/WebServiceRuntimeIfc.java");
        OS.copy("src/annotations/webservice/testdata/WebServiceRuntimeImpl.java",
                imp.getWorkingDir());
        imp.compile(imp.getWorkingDir()+"/WebServiceRuntimeIfc.java","");
        imp.j2w("", Support.getFvtBaseDir()+"/src/annotations/webservice/testdata/WebServiceRuntimeImpl.java",
        "annotations.webservice.testdata.WebServiceRuntimeImpl");
        /* took this out after beta.
         OS.delete(imp.getWorkingDir()+"/*.wsdl");
         OS.delete(imp.getWorkingDir()+"/*.xsd");
         // copy in the flattened wsdl with the complextype elements patched for beta.
         OS.copy("src/annotations/webservice/testdata/WebServiceIfc.wsdl",
         imp.getWorkingDir());
        */

        /*
        imp.war("websvcannoIf.war",
                "websvcannoIf",
                "annotations.webservice.testdata.WebServiceRuntimeImpl",
                "annotations.webservice.testdata.WebServiceRuntimeIfc",
                false);
        imp.deploy("websvcannoIf.war");


        // now deploy another, only difference is we supply the wsdl
        // copy in the flattened wsdl with the complextype elements patched for beta.
         imp.setWorkingDir(imp.getWorkingDir()+"/../server11");
         imp.cleanWorkingDir();

         OS.copy("src/annotations/webservice/testdata/WebServiceIfc.wsdl",
                 imp.getWorkingDir());
         OS.copy("src/annotations/webservice/testdata/WebServiceRuntimeIfc.java",
                 imp.getWorkingDir()+"/WebServiceRuntimeIfc.java");
         OS.copy("src/annotations/webservice/testdata/WebServiceRuntimeImpl.java",
                 imp.getWorkingDir());
         imp.compile(imp.getWorkingDir()+"/WebServiceRuntimeIfc.java","");
         imp.j2w("", imp.getWorkingDir()+"/WebServiceRuntimeImpl.java",
         "annotations.webservice.testdata.WebServiceRuntimeImpl");

        imp.war("websvcanno11.war",
                 "websvcanno11",
                 "services/Valid1",
                 "annotations.webservice.testdata.WebServiceRuntimeImpl",
                 "",
                 "WebServiceIfc.wsdl");
        imp.deploy("websvcanno11.war");

        // build the client for the interface-based services.
        imp.setWorkingDir(imp.getWorkingDir()+"/../client2");
        imp.cleanWorkingDir();
        imp.w2j("",imp.getWorkingDir()+"/../serverIf/WebServiceRuntimeImplService.wsdl");

         // now compile the test client written earlier.
        OS.copy("src/annotations/webservice/testdata/WebServiceRuntimeIFClient.java",
             imp.getWorkingDir()+"/annotations/webservice/testdata");
        imp.compile(imp.getWorkingDir()+"/annotations/webservice/testdata/WebServiceRuntimeIFClient.java", "");
        */
    }

    private void setUpOthers() throws Exception {
        /*
        // next...
        // build an annotated impl web service without wsdl
        imp.setWorkingDir(imp.getWorkingDir()+"/../server");
        imp.cleanWorkingDir();

        OS.copy("src/annotations/webservice/testdata/WebServiceRuntime.java_nowsdl",
                imp.getWorkingDir()+"/WebServiceRuntime.java");
		imp.j2w("", imp.getWorkingDir()+"/WebServiceRuntime.java",
		"annotations.webservice.testdata.WebServiceRuntime");

        // still need wsdl to build the client
        WsdlFlattener.flatten(imp.getWorkingDir()+"/Valid1.wsdl");
        OS.delete(imp.getWorkingDir()+"/*.wsdl");
        OS.delete(imp.getWorkingDir()+"/*.xsd");
        OS.copy(imp.getWorkingDir()+"/Valid1.wsdlflat", imp.getWorkingDir()+"/Valid1.wsdl");
        OS.delete(imp.getWorkingDir()+"/Valid1.wsdlflat");
        // bit of a hack here - save off wsdl for cached wsdl test
        OS.mkdir(imp.getWorkingDir()+"/../cached_wsdl");
        OS.copy(imp.getWorkingDir()+"/Valid1.wsdl",imp.getWorkingDir()+"/../cached_wsdl" );


		imp.war("websvcanno.war",
                "websvcanno",
                "services/valid1",
                "annotations.webservice.testdata.WebServiceRuntime",
                "",
                "");		
		imp.deploy("websvcanno.war");
		
		// now build a client and helpers w wsdl2java
		imp.setWorkingDir(imp.getWorkingDir()+"/../client");
		imp.cleanWorkingDir();
		imp.w2j("",imp.getWorkingDir()+"/../server/Valid1.wsdl");
		
		 // now compile the test client written earlier.
		 imp.compile("src/annotations/webservice/testdata/WebServiceRuntimeClient.java", "");		
		
		 // now build some invalid webservices that have had the annotation changed
		 // so they are inconsistent with the wsdl.  Package the wsdl in the war file.
		 imp.setWorkingDir(imp.getWorkingDir()+"/../server");
		 String path="src/annotations/webservice/testdata/";
		 // deployAlteredJava compiles, war's, and deploys the webservice.
		 //deployAlteredJava("websvcanno2", path+"WebServiceRuntimeChangedServiceName.java");	
		 deployAlteredJava("websvcanno3", path+"WebServiceRuntimeChangedName.java");	
		 //deployAlteredJava("websvcanno4", path+"WebServiceRuntimeChangedTns.java");
		 deployAlteredJava("websvcanno5",path+"WebServiceRuntimeChangedEndpoint.java");
         deployAlteredJava("websvcanno9",path+"WebServiceRuntimeChangedWsdlLoc.java");
		 // this one should work.
		 deployAlteredJava("websvcanno6",path+"WebServiceRuntime.java");
		
		 */

    }
		

    /**
     * @testStrategy - just see if we can retrieve the wsdl from the most basic
     * impl-based service.
     * This includes a flattened wsdl in the war file.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- just see if we can retrieve the wsdl from the most basic impl-based service. This includes a flattened wsdl in the war file. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testBasicService(){
        System.out.println("***** checking basic service ******");
      imp.setWorkingDir(imp.getWorkingDir()+"/../clientb");
      String wsdl = Operations.getUrlContents(hostAndPort+"/websvcannob/websvcannob?wsdl", 30);
      System.out.println(wsdl);
      assertTrue("wsdl not retrieved", wsdl.contains("</definitions>"));
      assertTrue("wrong wsdl retrieved", wsdl.contains("WebServiceBasicRuntimeService"));
      int rc = imp.invoke("annotations.webservice.testdata.WebServiceBasicRuntimeClient",
              hostAndPort+"/websvcannob/websvcannob?wsdl");
      assertTrue("client invocation failed for basic service", rc==0);
      System.out.println("********* basic service test passed ***********");
    }
 	
	/**
	 * @testStrategy tests that deployment and invoke of a basic annotated webservice
	 * implementation class works.  Uses WebServiceRuntime.java and
	 * WebServiceRuntimeClient.java.  Includes a wsdl.
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="tests that deployment and invoke of a basic annotated webservice implementation class works.  Uses WebServiceRuntime.java and WebServiceRuntimeClient.java.  Includes a wsdl. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAnnotatedServiceWithWsdl(){	
		System.out.println("************ running testAnnotatedServiceWithWsdl() ******************");
		imp.setWorkingDir(imp.getWorkingDir()+"/../client");		
		int b = imp.invoke("annotations.webservice.testdata.WebServiceRuntimeClient",
				hostAndPort+"/websvcanno6/services/valid1?wsdl");
		assertTrue("client invocation failed for websvcanno6", b==0);
        System.out.println("********* test passed ***********");
	}

    /**
     * make sure generated wsdl works too. Only difference between the war files
     * should be absense of wsdl.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testGeneratedWsdlOnImplWithAnnotations(){
        System.out.println("************ running testGeneratedWsdlOnImplWithAnnotations ******************");
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        // implmenentation class with dynamic wsdl.
        int b = imp.invoke("annotations.webservice.testdata.WebServiceRuntimeClient",
                hostAndPort+"/websvcanno/services/valid1?wsdl");
        assertTrue("client invocation failed for websvcanno", b==0);
        System.out.println("********* test passed ***********");
    }

    /**
     * same as  testGeneratedWsdlOnImplWithAnnotations except we're testing ability
     * to build up service without wsdl, since dyn gen wsdl is going to be late.
     *
     * Wsdl is cached on the client side, not available to the server.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAnnotatedServiceWithCachedWSDL() throws Exception {
        System.out.println("************ running testAnnotatedServiceWithCachedWsdl() ******************");
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");

        // To test before dynamic wsdl is available, patch up the wsdl
        // we produced when building the client and use that.
        imp.setWorkingDir(imp.getWorkingDir()+"/../cached_wsdl");
        String wsdlFile = imp.getWorkingDir()+"/Valid1.wsdl";
        if (wsdlFile.contains(":")) {
            wsdlFile = wsdlFile.substring(2);  // strip drive letter
        }
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/websvcanno/services/valid1";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+ relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        String url = "file:" + newWsdlFile;

        // implmenentation class with dynamic wsdl.
        imp.setWorkingDir(imp.getWorkingDir()+"/../client");
        int b = imp.invoke("annotations.webservice.testdata.WebServiceRuntimeClient",
                url);
        assertTrue("client invocation failed for websvcanno", b==0);
        System.out.println("********* test passed ***********");
    }
	
	/**
	 * TODO: assure IBM imp uses same url pattern, or this test is no good.  If it does not
	 * use this pattern, then testValidServiceClass should fail.
	 *
	 * @testStrategy attempt to deploy webservice where annotation has been changed
	 * to mismatch wsdl.  Servicename, name, tns, and endpoint parameters have been
	 * altered.  All should fail to deploy.  Also have one deployed with mismatching wsdl,
	 * which should not deploy.
     *
     * 2.19.2007 - This is not a valid test because the app should regenerate wsdl when
     * "partial" wsdl is supplied per jsr224 5.2.5
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="attempt to deploy webservice where annotation has been changed to mismatch wsdl.  Servicename, name, tns, and endpoint parameters have been altered.  All should fail to deploy.  Also have one deployed with mismatching wsdl, which should not deploy.  2.19.2007 - This is not a valid test because the app should regenerate wsdl when \"partial\" wsdl is supplied per jsr224 5.2.5 ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testChangedServiceName(){
		System.out.println("************ running testChangedServiceName() ******************");
        // first we make sure server is up
        System.out.println("*** trying basic service first ****");
        testBasicService();
        System.out.println("**** basic service succeeded, now trying negative test ***");
		// try to read url and assure that service did not deploy.
		boolean  result = Support.fetchurl(hostAndPort+"/websvcanno2/services/valid1?wsdl", 5);
		assertFalse("error:  web service should not have deployed: 2 ", result);
        System.out.println("********* test passed ***********");
    }

    /**
     * @testStrategy attempt to deploy webservice with annotation parameter changed
     * to mismatch wsdl, should not be able to invoke.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="attempt to deploy webservice with annotation parameter changed to mismatch wsdl, should not be able to invoke. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testChangedName(){
        System.out.println("************ running testChangedName() ******************");
        // first we make sure server is up
        System.out.println("*** trying basic service first ****");
        testBasicService();
        System.out.println("**** basic service succeeded, now trying negative test ***");

		boolean result = Support.fetchurl(hostAndPort+"/websvcanno3/services/valid1?wsdl", 5);		
		assertFalse("error:  web service should not have deployed: 3", result);
        System.out.println("********* test passed ***********");
    }

    /*
    * 2.19.2007 - This is not a valid test because the app should regenerate wsdl when
    * "partial" wsdl is supplied per jsr224 5.2.5
    */

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testChangedNamespace(){
        System.out.println("************ running testChangedNamespace() ******************");
        // first we make sure server is up
        System.out.println("*** trying basic service first ****");
        testBasicService();
        System.out.println("**** basic service succeeded, now trying negative test ***");

		boolean result = Support.fetchurl(hostAndPort+"/websvcanno4/services/valid1?wsdl", 5);
		assertFalse("error:  web service should not have deployed: 4", result);
        System.out.println("********* test passed ***********");
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testChangedEndpoint(){
        System.out.println("************ running testChangedEndpoint() ******************");
        // first we make sure server is up
        System.out.println("*** trying basic service first ****");
        testBasicService();
        System.out.println("**** basic service succeeded, now trying negative test ***");

		boolean result = Support.fetchurl(hostAndPort+"/websvcanno5/services/valid1?wsdl", 5);
		assertFalse("error:  web service should not have deployed: 5", result);
        System.out.println("********* test passed ***********");
    }

    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testChangedWsdlLocation(){
        System.out.println("************ running testChangedWsdl() ******************");
        // first we make sure server is up
        System.out.println("*** trying basic service first ****");
        testBasicService();
        System.out.println("**** basic service succeeded, now trying negative test ***");

		boolean result = Support.fetchurl(hostAndPort+"/websvcanno9/services/valid1?wsdl", 5);
		assertFalse("error:  web service should not have deployed: 9", result);
        System.out.println("********* test passed ***********");
	}
	
	/**
	 * This tests a simple class with a simple SEI, and expects the server to generate the wsdl.	
	 * There's more testing of interfaces in webservice_g2.interfaces and
	 * webservice_g2.inheritance. Uses
     * WebServiceRuntimeImpl.java, WebServiceRuntimeIfc.java_nowsdl
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testGeneratedWsdlSEI() throws Exception {
		System.out.println("************ running testGeneratedWsdlSEI() ******************");
		imp.setWorkingDir(imp.getWorkingDir()+"/../client2");

        int b = 0;
        /* begin hack - cached wsdl */
        /*
        String newWsdlFile = "/tmp/temp.wsdlstatic";
        String wsdl = Operations.getUrlContents(
                hostAndPort+"/websvcannoIf/websvcannoIf/webserviceruntimeimplservice.wsdl", 30);
        String xsd =  Operations.getUrlContents(
                hostAndPort+"/websvcannoIf/websvcannoIf/WebServiceRuntimeImplService_schema1.xsd", 30);
        String newSchFile = "/tmp/WebServiceRuntimeImplService_schema1.xsd";
        Operations.writeFile(newSchFile, xsd, AppConst.DEFAULT_ENCODING );

        String relativeUrl = "http://localhost:19080/websvcannoIf/websvcannoIf";
        wsdl = wsdl.replace("http://btiffany:9080/websvcannoIf/websvcannoIf", relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        */
        //String url = "file:" + newWsdlFile;
        String url = hostAndPort+"/websvcannoIf/websvcannoIf?wsdl";
        b = imp.invoke("annotations.webservice.testdata.WebServiceRuntimeIFClient", url);

        /*
         b = imp.invoke("annotations.webservice.testdata.WebServiceRuntimeIFClient",
                "http://localhost:19080"+"/websvcannoIf/websvcannoIf/webserviceruntimeimplservice.wsdl");
		 b = imp.invoke("annotations.webservice.testdata.WebServiceRuntimeIFClient",
				hostAndPort+"/websvcannoIf/websvcannoIf/webserviceruntimeimplservice.wsdl");
        */
		assertTrue("client invocation failed for generated wsdl and SEI, websvcannoIf", b==0);
        System.out.println("********* test passed ***********");
		
	}
	
	/**
	 * This tests a simple class with a simple SEI, and supplies the wsdl. 	
	 * There's more testing of interfaces in webservice_g2.interfaces and
	 * webservice_g2.inheritance.
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testSuppliedWsdlSEI(){
		System.out.println("************ running testSuppliedWsdlSEI() ******************");
		imp.setWorkingDir(imp.getWorkingDir()+"/../client2");
		int b = imp.invoke("annotations.webservice.testdata.WebServiceRuntimeIFClient",
				hostAndPort+"/websvcanno11/services/Valid1?wsdl");
		assertTrue("client invocation failed for SEI, websvcanno11", b==0);
        System.out.println("********* test passed ***********");
		
	}
	
	/**
	 * replace correct impl class file with a modified file.
	 * @param warfilename - name of warfile, without extension
	 * @param Filepath - relative path to file from fvt dir.
	 */
	private void deployAlteredJava(String warfileName, String Filepath){		   	
	   	int rc = 0;	   	
	
		rc= OS.copy(Filepath,
				 imp.getWorkingDir()+"/annotations/webservice/testdata/WebServiceRuntime.java");
		assertTrue("copy failed", rc==0);
		String cp = "-cp ."+System.getProperty("path.separator")+"\""+
		System.getProperty("java.class.path")+"\"";		

        String zosOpts="";
        IMachine machine = TopologyActions.FVT_MACHINE;
        if(machine.getOperatingSystem() == OperatingSystem.ZOS){
            zosOpts = "-encoding ISO8859-1 ";
        }

		System.out.println(
				OS.runs(imp.getWorkingDir(), "javac "+zosOpts+ cp +" -d . annotations/webservice/testdata/WebServiceRuntime.java")
				);
		assertTrue("compile failed", OS.getLastRC() ==0);
		
		// deploy
		imp.war(warfileName+".war",
                warfileName,
                "services/valid1",
                "annotations.webservice.testdata.WebServiceRuntime",
                "",
                "Valid1.wsdl");		
		imp.deploy(warfileName+".war");
			
	}

    /**
     * The suite method returns the tests to run from this suite.
     * The Java2WsdlTestCaseCase class is specified which means
     * all methods starting with "test" will be be run.
     *
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
            return new TestSuite(WebServiceRunTimeTestCase.class);
    }
}
