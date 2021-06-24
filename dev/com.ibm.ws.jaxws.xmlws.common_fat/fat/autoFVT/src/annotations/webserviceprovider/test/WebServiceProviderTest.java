/*
 * @(#) 1.8 autoFVT/src/annotations/webserviceprovider/test/WebServiceProviderTest.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:19 [7/11/07 13:13:26]
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
 *  09/01/06   btiffany    LIDB3296.31.01     new file
 *  11/03/06   btiffany    403039             handle operationmismatch better.
 *  02/14/07    "                             remove Antproperties calls, broken on z.
 * 05/24/2007  jramos      440922             Integrate ACUTE
 *
 */

package annotations.webserviceprovider.test;

import javax.xml.ws.WebServiceException;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.ImplementationAdapter;
import annotations.support.Support;
import annotations.webserviceprovider.client.ProvFqProxyClient;
import annotations.webserviceprovider.client.ProviderTestClient;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/*
 * notes:
 * 12.19.06 f0651.02 - testopmismatch is passing, but the error message is flaky.
 * technically speaking, this is all passing.  Checking in. 
 * 
 * 01.17.07 g0702.02 - dynamic wsdl generation is being attempted inappropriately.
 * Everything's failing. def 415307.  My error - if you give a wsdl url beneath the correct 
 * servicename, that is the error you will get.  Adjusted url's and tests passed. Opmismatch is
 * throwing an npe, but at least it's not invoking. 
 * 
 * 01.19.07 all passes with Nikhil's patch.
 * 01.22.07 h0702.29 - all passes.
 * 03.20.07 v0710.23 - was passing, thought was regression, but we were dispatching
 * because "echo" matched an element name in the wsdl, although it did not match
 * an op name.  Fixed wsdl. 
 * 
 */
public class WebServiceProviderTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    
    static boolean onSun = false;
    static String hostAndPort = null;
	private static String workdir = Support.getFvtBaseDir()+"/build/work/annotations/WebServiceProvider";
    private static ImplementationAdapter imp = Support.getImplementationAdapter("default",workdir);
    static{
        // figure out host and port.
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
    }
	
	public static void main(String[] args) throws Exception {	
		if(true){
            TestRunner.run(suite());
        }
        else{
            WebServiceProviderTest t = new WebServiceProviderTest("");
            
            t.confirmServerWorking();
            t.testAnnoDefaults();
            t.testAnnoDefined();
            t.testBetaAnnoDefined();            
            t.testOperationMismatch();
            
        }    
	}
	

    /**
     * junit needs next 3.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(WebServiceProviderTest.class); 
    }   
    
	public WebServiceProviderTest(String str) {
		super(str);
	}
	
	public WebServiceProviderTest(){
		super();
	}

		
    /**
     * @testStrategy 
     * Due to the low level nature of this api and tests performed elsewhere, testing here
     * is limited.  The overall coverage picture is approximately as follows:
     * wsdl2java - not supported for providers and dispatch
     * java2wsdl - also not supported for providers and dispatch
     * check illeagal use with @webService annotation, missing annotation, 
     * and illegal private constructor - done by D.S. in badprovider tests
     * deploy and invoke with all parameters defined - checked in jaxws.provider
     * 
     * mapping of operations in wsdl - not really supported, the way provider seems to work 
     * is the entire message body is handed to the provider, 
     * based on the servicename and portname defined in the annotation.   There is only one
     * method in providers, invoke(), so operation mapping doesn't make sense.  But perhaps the wsdl
     * should be checked before the op is invoked. 
     * 
     * 
     * Tests we need to cover here:
     * - check that annotation works with catalog (tbd) 
     * - basic deployment with no parameters defined on annotation
     * - basic deployment with all parameters defined on annotation
     * - change java annotation or wsdl and verify that when runtime reads wsdl,
     *   it does not invoke because wsdl and annotation do not match. 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testInfo(){		
	}
	
	/**
	 * @testStrategy - verify that metadata can be used to find wsdl, etc.  
	 *  Unknown how IBM will imp. this at present.
     *   - catalog descoped for now. 
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- verify that metadata can be used to find wsdl, etc.   Unknown how IBM will imp. this at present. - catalog descoped for now. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testMetaDataMisMatch(){
        fail("we need to see if we even look at metadata for this case");
    }
    
    /**
     * @testStrategy - verify that wsdl can be pulled from the catalog. 
     *  Unknown how IBM will imp. the catalog at present.
     *  descoped for now.  
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- verify that wsdl can be pulled from the catalog.  Unknown how IBM will imp. the catalog at present. descoped for now. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void _testCatalog(){
		fail("we need to write this test");
	}
	
	/**
	 * @testStrategy - check that a provider, with minimal values on the annotation, can work.
	 * This one uses a dispatch client, while all the others use a proxy client.   
     * 
     * uses testprov.ear
     * 
     * 12.5.06 - failing for what we think is 409858 
     * 12.19.06 passing on f0651.02
	 *
	 */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- check that a provider, with minimal values on the annotation, can work. This one uses a dispatch client, while all the others use a proxy client.  uses testprov.ear  12.5.06 - failing for what we think is 409858 12.19.06 passing on f0651.02 ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void testAnnoDefaults(){
        System.out.println("**** running testAnnoDefaults()*********");
        String url = hostAndPort+"/testprov/ProvBasicService";
        //String url = "http://localhost:19080/testprov/ProvBasicService";        
        String result = ProviderTestClient.runClient(url);
         
        String expected = "Hello Server";
        assertTrue("\nunexpected result from client,\n Actual  = " + result + "\n expected = "+expected , result.contentEquals(expected));
        System.out.println("**** testAnnoDefaults()passed *********");
    }
    
    /**
     * @testStrategy test a provider with all annotation parameters set to non-default values
     * can be invoked. 
     * uses testprovfq.ear
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="test a provider with all annotation parameters set to non-default values can be invoked. uses testprovfq.ear ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAnnoDefined(){
        System.out.println("**** running testAnnoDefined()*********");
        // now why does it map to http://localhost:9080/providerfq/testprovfq?wsdl ?
        // because that's the name of the war file, and that's what tomcat uses. duh.
        String url = hostAndPort+"/testprovfq/testprovfq?wsdl";
        if(!onSun) url = hostAndPort+"/testprovfq/ProvFqService?wsdl";
        String result = ProvFqProxyClient.runclient(url);
        String expected = "ProvFq received:calling Mr. echo";
        assertTrue("\nunexpected result from client,\n Actual  = " + result + "\n expected = "+expected , result.contentEquals(expected));
        System.out.println("**** testAnnoDefined()passed *********");

    }
    
    /** @testStragegy - same as testAnnoDefined but use explicit wsdl url. 
     * 
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBetaAnnoDefined(){
        System.out.println("******* testBetaAnnoDefined starting*****");
        confirmServerWorking();
        System.out.println("******* testBetaAnnoDefined passed*****");

    }
    
    /**
     * call a positive test that should always work, before we call negative 
     * tests that should not work.
     *
     */
    void confirmServerWorking(){
        //if(true) return;
        System.out.println("** running basic positive test **");
        // now why does it map to http://localhost:9080/providerfq/testprovfq?wsdl ?
        // because that's the name of the war file, and that's what tomcat uses. duh.
        String url = hostAndPort+"/testprovfq/testprovfq?wsdl";
        String url2 = url;
        if(!onSun){
             url= hostAndPort+"/testprovfq/ProvFqService?wsdl";
          
        }
        boolean serviceAlive;
        serviceAlive = com.ibm.ws.wsfvt.build.tools.utils.Operations.getUrlStatus(url, 30);
        assertTrue("service did not deploy", serviceAlive); 
        String result = ProvFqProxyClient.runclient(url);
        String expected = "ProvFq received:calling Mr. echo";
        assertTrue("\nunexpected result from client,\n Actual  = " + result + "\n expected = "+expected , result.contentEquals(expected));
        System.out.println("** basic positive test passed **");
    }
    
    
    /**
     * @testStrategy try to invoke a provider that has been deployed with a wsdl whose
     * operation name does not match what is was when we generated the service client.
     * The service should not invoke.  Alt imp.  fails this test.
     * 
     * Can't find this anywhere in the 224 standard, but we know it's wrong to not honor
     * the wsdl contract. 
     * 
     * Uses Provfq.java + ProvFQService_opmismatch.wsdl in testprovfq_opmismatch.ear
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="try to invoke a provider that has been deployed with a wsdl whose operation name does not match what is was when we generated the service client. The service should not invoke.  Alt imp.  fails this test.  Can't find this anywhere in the 224 standard, but we know it's wrong to not honor the wsdl contract.  Uses Provfq.java + ProvFQService_opmismatch.wsdl in testprovfq_opmismatch.ear",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testOperationMismatch(){
        System.out.println("**** running testOperationMismatch*********");
        confirmServerWorking();  // call this first on all negative tests
        // now why does it map to http://localhost:9080/providerfq/testprovfq?wsdl ?
        // because that's the name of the war file, and that's what tomcat uses. duh.
        String url = hostAndPort+"/testprovfq_opmismatch/testprovfq?wsdl";
        
        boolean usingTcpMon = false;  // for debug
        if (!onSun){
            url=hostAndPort+"/testprovfq_opmismatch/ProvFqService?wsdl";
            if (usingTcpMon) url="file:/tmp/ProvFqService.wsdl";
        }
        boolean serviceAlive = true;
        
        if (!usingTcpMon) serviceAlive = com.ibm.ws.wsfvt.build.tools.utils.Operations.getUrlStatus(url, 30);
        if(serviceAlive ){
            try{
                //String result = ProviderTestClient.runClient(url);
                String result = ProvFqProxyClient.runclient(url);
                String expected = "WebServiceException";
                assertTrue("\n def. 402660 - should not be able to invoke with op mismatch \n - unexpected result from client,\n Actual  = " + result + "\n expected = "+expected , result.contains(expected));       
            } catch (WebServiceException e) {
                // it's ok if we get an exception. 
                e.printStackTrace(System.out);                
            }
        }  
                
        System.out.println("**** testOperationMismatch passed *********");
    }
    
    /**
     * @testStrategy try to invoke a provider that has been deployed with a wsdl whose
     * service name does not match the annotation.
     * The service should not invoke (jsr224 7.0) Alt imp.  fails this test.
     * 
     * uses provfq.java plus modified wsdl
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="try to invoke a provider that has been deployed with a wsdl whose service name does not match the annotation. The service should not invoke (jsr224 7.0) Alt imp.  fails this test.  uses provfq.java plus modified wsdl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServiceNameMismatch(){
        System.out.println("**** running testServiceNameMismatch*********");
        confirmServerWorking();  // call this first on all negative tests
        String url = hostAndPort+"/testprovfq_snmismatch/testprovfq?wsdl";
        if (!onSun) url = hostAndPort+"/testprovfq_snmismatch/ProvFqServicexx";
        boolean serviceAlive = com.ibm.ws.wsfvt.build.tools.utils.Operations.getUrlStatus(url, 30);
        if(serviceAlive){
            String result = ProviderTestClient.runClient(url);
            String expected = "WebServiceException";
            assertTrue("\n unexpected result from client,\n Actual  = " + result + "\n expected = "+expected , result.contains(expected));
        }
        System.out.println("**** testServiceNameMismatch passed *********");
    }
    
    /**
     * @testStrategy try to invoke a provider that has been deployed with a wsdl whose
     * port name does not match the annotation.  The service should not invoke
     * (jsr224 7.0) Alt imp.  fails this test.
     * 
     * uses provfq.java plus modified wsdl
     */
    /*
     *Comment out this case because that CXF allows the portName/serviceName in annotation and wsdl are inconsistent for web service provider. See:  
     * org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean.initializeWSDLOperationsForProvider() 
     **/
    /*
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="try to invoke a provider that has been deployed with a wsdl whose port name does not match the annotation.  The service should not invoke (jsr224 7.0) Alt imp.  fails this test.  uses provfq.java plus modified wsdl",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testPortNameMismatch(){
        System.out.println("**** running testPortNameMismatch*********");
        confirmServerWorking();  // call this first on all negative tests
        String url = "http://localhost:9080/testprovfq_pnmismatch/testprovfq?wsdl";        
        if (!onSun) url = hostAndPort+ "/testprovfq_portnamemismatch/ProvFqService";
        boolean serviceAlive = com.ibm.ws.wsfvt.build.tools.utils.Operations.getUrlStatus(url, 30);
        if(serviceAlive){
            String result = ProviderTestClient.runClient(url);
            String expected = "WebServiceException";
            assertTrue("\n unexpected result from client,\n Actual  = " + result + "\n expected = "+expected , result.contains(expected));       
        }
        System.out.println("**** testPortNameMismatch passed *********");
    }
    */
    /**
     * @testStrategy check that a webservice annotated with @WebService and @WebServiceProvider
     * can't get deployed. 
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="check that a webservice annotated with @WebService and @WebServiceProvider can't get deployed. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testIllegalMixAtTooling(){
        System.out.println("**** running testIllegalMixAtTooling*********");                
        String testfile=Support.getFvtBaseDir()+"/src/annotations/webserviceprovider/etc/testdata/Mismatch.java";
        try{
            imp.j2w("", testfile, "test.Mismatch");
        } catch (RuntimeException e){    
            e.printStackTrace();
            System.out.println("**** testIllegalMixAtTooling passed *********");
            return;
        }
        // our tooling does not let this go through wsgen.
        // however, our runtime does not use wsgen.  So, see the runtime test below. 
        fail("Mismatch.java should have failed at wsgen");      
      
    }    

    /**@testSTrategy check that class with both @WebService and @WebServiceProvider
     * will not invoke.
     * 
     * (coded 9.19, but have never run this one)
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testIllegalMixAtRuntime(){
        System.out.println("**** running testIllegalMixAtRuntime*********");
        confirmServerWorking();  // call this first on all negative tests
        System.out.println("********* running testIllegalMixAtRuntime **********");
        String url = "http://localhost:9080/testprovillegal/testprovillegal?wsdl";
        if (!onSun) url = hostAndPort+"/testprovillegal/ProviderService?wsdl";
        boolean serviceAlive = com.ibm.ws.wsfvt.build.tools.utils.Operations.getUrlStatus(url, 30);
        if(serviceAlive){
            String result = ProviderTestClient.runClient(url);
            String expected = "WebServiceException";
            assertTrue("\n unexpected result from client,\n Actual  = " + result + "\n expected = "+expected , result.contains(expected));
        }    
        System.out.println("********* testIllegalMixAtRuntime passed **********");
    }

}
