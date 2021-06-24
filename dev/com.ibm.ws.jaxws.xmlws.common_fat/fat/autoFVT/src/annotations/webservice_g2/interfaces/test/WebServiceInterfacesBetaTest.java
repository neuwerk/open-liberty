/*
 * autoFVT/src/annotations/webservice_g2/interfaces/test/WebServiceInterfacesBetaTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
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
 * 4.16.08     btiffany    513414 removed hardcoded wsdl url, use ?wsdl
 */

package annotations.webservice_g2.interfaces.test;
import java.io.File;

import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.Support;
import annotations.webservice_g2.interfaces.client.WebServiceInterfacesClient;
import annotations.webservice_g2.interfaces.client.WebServiceInterfacesClientTwo;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;
/**
 * @throws Exception Any kind of exception
 *   
 * @author btiffany
 * 
 * Three ear files used here:
 * interfaces1 - annotated test with sei artifacts that should work for beta
 * interfaces2 - minimally annotated sei that should work for beta
 * interfaces3 - same as interfaces1, but with wsgen artifacts, no wsdl, excluded method.
 * 
 * 5.1.07 remove calls to AntProperties.getHostAndPort 
 */
public class WebServiceInterfacesBetaTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {	
    private final static String hostAndPort;
    static{
        // figure out host and port.
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
    }
	public static void main(String[] args) throws Exception {	
	    if(false){
            TestRunner.run(suite());
        } else {
            WebServiceInterfacesBetaTest t = new WebServiceInterfacesBetaTest();
            //t.testBetaAnnotatedService();  // has both ?wsdl and explicit 
            t.testMinimalBetaService();
            // t.testMinimalBetaServiceExplicitWSDL();

            //t.testServiceCachedWsdl();
            //t.testServiceDynamicWsdl();
        }
	}
    /**
     * junit needs next 3.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(WebServiceInterfacesBetaTest.class); 
    }   
    
	public WebServiceInterfacesBetaTest(String str) {
		super(str);
	}
	
	public WebServiceInterfacesBetaTest(){
		super();
	}
    
    // junit will run this before every test
    public void setUp(){
    }
    
    /**
     * @testStrategy check that a basic service with an sei can be deployed and invoked.
     * This is the minimum, no customization except wsdllocation.  Use generated
     * sei from wsimport for beta, so webmethod exclude won't be working. 
     * Due to def. 390939 we can't use ?wsdl location either.
     * @throws Exception
     * 
     * used for Beta. AnnotationsWebServiceInterfaces2.ear
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testMinimalBetaService() throws Exception {
        System.out.println("**********testMinimalBetaService starting ******************");
        System.out.println("testing service with ?wsdl url");
        try{
            int rc = WebServiceInterfacesClientTwo.runme(hostAndPort+
            "/webserviceinterfacestwo/webserviceinterfacestwo?wsdl");
        }
        catch (Exception e){
            System.out.println("caught exception - see defect 390939.1");
            e.printStackTrace();            
            fail("see defect 390939.1");            
        }
        System.out.println("**********test succeeded ******************");
    }
    
    /**
     * call basic beta service, just see if we can get a result without an exception. 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testMinimalBetaServiceExplicitWSDL() throws Exception {
        System.out.println("**********testMinimalBetaServiceExplicitWSDL starting ******************");
        System.out.println("testing service with explicit wsdl url");
		// Liberty does not support SEI/?wsdl wsdl URL style
        int rc = WebServiceInterfacesClientTwo.runme(hostAndPort+
        "/webserviceinterfacestwo/webserviceinterfacestwo?wsdl");
                System.out.println("**********test succeeded ******************");
    }   

    
    /** @testStrategy check that a webservice with an sei and a fully qualified set of 
     * parameters on the webservice annotation, on both the sei and class, can be 
     * deployed and invoked successfully.  Also, a method was mapped to wsdl,
     * jaxws classes generated, then marked as
     * excluded in the java interface, (something a  developer might reasonably do),
     * this should either fail to deploy or invoke. - had to take this out for beta, maybe do later
     * 
     * This wsdl file imports another wsdl (not on purpose, tooling generated the 
     * Wsdl that way), so we'll also check if both wsdl's and the xsd can be retrieved
     * from the ?wsdl urls as we invoke the service. 
     * 
     * The schema used was patched for complex types defect 395473. 
     * 
     * used for beta, AnnotationsWebServiceInterfaces1.ear
     * 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBetaAnnotatedService() throws Exception {
    	// this will not compile under eclipse, but should run once client is compiled with ant.
    	System.out.println("********** testBetaAnnotatedService() is running (was failing with 390939.1) **********"); 
        
        System.out.println("testing service with explicit wsdl url");
        
        /*
        // was7: no longer valid, relative url off of service name is not guaranteeed.
        int rc = WebServiceInterfacesClient.runme(hostAndPort+                
        "/webserviceinterfaces/webserviceinterfaces/wsdl/ifimplsvc.wsdl");

        assertTrue("unexpected outcome from client invocation ", rc==12);
        */
        
        System.out.println("testing service with ?wsdl url");
    	int rc = WebServiceInterfacesClient.runme(hostAndPort+                
    			"/webserviceinterfaces/webserviceinterfaces?wsdl");
 
    	assertTrue("unexpected outcome from client invocation ", rc==12);
        System.out.println("**********testService succeeded ******************");
    }
    
    /**
     * @testStrategy - same as testBetaService except we have omitted the wsdl from the
     * war file, so runtime is forced to generate it, and used wsgen
     * instead of wsimport.   We should still be able to get 
     * a good invoke.
     * server is based on interfaceTestOne._java_nowsdl, interfacesTestOneImpl
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServiceDynamicWsdl() throws Exception {
        // this will not compile under eclipse, but should run once client is compiled with ant.
        System.out.println("********** testServiceDynamicWsdl() is running **********"); 
        
        System.out.println("testing service with ?wsdl url");
        int rc = WebServiceInterfacesClient.runme(hostAndPort+                
                "/webserviceinterfaces3/webserviceinterfaces?wsdl");
 
        assertTrue("unexpected outcome from client invocation ", rc==12);
        System.out.println("**********test succeeded ******************");
    }
    
    /**
     * @testStrategy - same as testDynamicWsdl except we are caching the wsdl,
     * and we are going to try to invoke an excluded method that's in cached
     * wsdl and in war file. 
     * this ear file is built with wsgen, has fully annno'd interfaces and impl. 
     *  
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testServiceCachedWsdl() throws Exception {
        // this will not compile under eclipse, but should run once client is compiled with ant.        
        System.out.println("********** testServiceCachedWsdl() is running **********"); 
        
        // We use a wsdl that contains the excluded method, the one that was used to build 
        // the client.
        
        String wsdlFile = Support.getFvtBaseDir()+ "/build/work/webservice_g2_interfaces/wsgen/Ifimplsvc.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webserviceinterfaces3/webserviceinterfaces";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort + relativeUrl );
        //wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        String url = "file:" + newWsdlFile;
        int rc = WebServiceInterfacesClient.runme(url);
 
        assertTrue("wrong return code from client, expected=12, actual="+ rc, rc==12);
        System.out.println("**********test succeeded ******************");
    }
    

    
}
