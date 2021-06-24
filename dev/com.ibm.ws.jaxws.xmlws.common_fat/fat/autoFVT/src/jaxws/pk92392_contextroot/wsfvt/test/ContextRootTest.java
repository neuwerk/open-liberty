//
// @(#) 1.1.1.2 autoFVT/src/jaxws/pk92392_contextroot/wsfvt/test/ContextRootTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/10/10 10:05:28 [8/8/12 06:58:52]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 08/13/10 jtnguyen    PK92392.fvt         New File
// 09/09/10 jtnguyen    PK92392.fvt.1       Changed the values in wsdl to look for 

package jaxws.pk92392_contextroot.wsfvt.test;

import java.util.LinkedList;


import jaxws.pk92392_contextroot.wsfvt.client.CalculatorClient;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Server;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Scope;
import com.ibm.websphere.simplicity.application.ApplicationManager;
import com.ibm.websphere.simplicity.application.EnterpriseApplication;
import com.ibm.websphere.simplicity.application.InstallWrapper;


    /*
	 * This test whether a service having context root '/' is reachable or not.
	 * Cases:
	 * - with wsdl (single wsdl packaged with the app)
	 * - with wsdl (wsdl imports a xsd file and both are packaged with the app)
	 * - with wsdl (no wsdl in the app)
	 * After each test, the app must be uninstalled before the next test can be
	 * run because all apps use the same context root = "/". 
     */

public class ContextRootTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase{
	
	    private static String hostandport = null;

        private static Server server = null;
        private static String hostname = null;
        private static Integer port = 0;
        
        private static Cell cell = null;
        private static String fvthome = null;
        private static int url_response_timeout = 60;  // how long to wait for a url to respond
        private String expectedTns = "targetNamespace=\"http://server.wsfvt.pk92392_contextroot.jaxws/\""; 
        private String serviceName = null;
        private String appName = null;
        //"definitions name=\"CalculatorService\" targetNamespace=\"http://server.wsfvt.pk92392_contextroot.jaxws/\"";     
 
        private String endpointUrl = null;

	
	/**
     * Constructor to create a test case with a given name.
     * 
     * @param name
     *            The name of the test case
     */
    public ContextRootTest(String name) {
        super(name);
    }

    /**
     * The main method.
     * 
     * @param args
     *            The command line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(ContextRootTest.class);
    }
	
	protected void suiteSetup(ConfigRequirement req) throws Exception {
		
	}
	
	protected void suiteTeardown() throws Exception {
		
	}
	
	protected void setUp() throws Exception{
            try {           
	    
                server = TopologyDefaults.getDefaultAppServer();
                hostname = server.getNode().getHostname();
                port = server.getPortNumber(PortType.WC_defaulthost);
                fvthome = AppConst.FVT_HOME;
                hostandport = "http://" + hostname+ ":" + port;
                cell = TopologyDefaults.getDefaultAppServer().getCell();

            }catch (Exception e) {
                e.printStackTrace();                        
            }

            
	}
	
	/**
	 * @testStrategy This test creates a JAX-WS proxy and sends two numbers 
	 * 				 to the server, where both the numbers are added and the
	 * 				 result is sent back. 
         * In this case:
         *   - the wsdl file does not import the .xsd or other .wsdl
         *   - the wsdl is packaged with the EAR
	 *   - the service has @WebService with wsdlLocation specified
	 */
	@com.ibm.ws.wsfvt.test.framework.FvtTest(description="This test whether a JAX-WS service having context root '/' is reachable or not",
		    expectedResult="",
		    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
	public void test_pk92392_withSingleWSDL() throws Exception {
		appName = "CalculatorSingleWSDL";
                installApps(appName);
                 
                serviceName = appName + "Service";
                endpointUrl = hostandport + "/" + serviceName;
                System.out.println("-- endpointUrl = " + endpointUrl);
         
                viewWsdlPage(serviceName,endpointUrl);

        // test runtime
        CalculatorClient client =  new CalculatorClient();		
        try {
                int res = client.invokeCalculatorSingleWSDLService(5,6);
                assertTrue(res== 11);
        } catch (Exception e) {
            e.printStackTrace(); 
            // clean up
            unInstallApps(appName);       

            fail("Test failed with exception.");
        }
                
        // clean up
        unInstallApps(appName);       

 	}


	/**
	 * @testStrategy This test creates a JAX-WS proxy and sends two numbers 
	 * 				 to the server, where both the numbers are added and the
	 * 				 result is sent back. 
         * In this case:
         *   - the wsdl file imports the .xsd 
         *   - the wsdl and xsl are packaged with the EAR
	 *   - the service has @WebService with wsdlLocation specified
	 */

        // Julie: commented out until defect 661432 is fixed
	public void _test_pk92392_withImportWSDL() throws Exception{

            String appName = "CalculatorImportWSDL";
            installApps(appName);  

            serviceName = appName + "Service";
            endpointUrl = hostandport + "/" + serviceName;
            System.out.println("-- endpointUrl = " + endpointUrl);
    
            viewWsdlPage(serviceName,endpointUrl);
    
            // test runtime
            CalculatorClient client =  new CalculatorClient();		
            try {
                    int res = client.invokeCalculatorImportWSDLService(5,6);
                    assertTrue(res== 11);
            } catch (Exception e) {
                e.printStackTrace(); 
                // clean up
                unInstallApps(appName);       
    
                fail("Test failed with exception.");
            }       
            
            // clean up
            unInstallApps(appName);       

	}
	/**
	 * This test whether a service having context root '/' is reachable or not
	 * 
	 * @testStrategy This test creates a JAX-WS proxy and sends two numbers 
	 * 				 to the server, where both the numbers are added and the
	 * 				 result is sent back. There is no wsdl file in EAR.
	 */


        // Julie: commented out until defect 661432 is fixed
	public void _test_pk92392_withoutWSDL() throws Exception{
		String appName = "CalculatorNoWSDL";
                installApps(appName);  

                serviceName = appName + "Service";
                endpointUrl = hostandport + "/" + serviceName;
                System.out.println("-- endpointUrl = " + endpointUrl);

                viewWsdlPage(serviceName,endpointUrl);

            // test runtime
            CalculatorClient client =  new CalculatorClient();		
            try {
                    int res = client.invokeCalculatorNoWSDLService(5,6);
                    assertTrue(res== 11);
            } catch (Exception e) {
                e.printStackTrace(); 
                // clean up
                unInstallApps(appName);       
    
                fail("Test failed with exception.");
            }       
            
            // clean up
            unInstallApps(appName);       

	}

	public void invokeClient(String endpointUrl) throws Exception {
            CalculatorClient client =  new CalculatorClient();		
            try {
                    int res = client.invokeCalculatorImportWSDLService(5,6);
                    assertTrue(res== 11);
            } catch (Exception e) {
                e.printStackTrace();  
                fail("Test failed with exception.");
            }
	}

     public void viewWsdlPage(String serviceName, String endpointUrl){
    	 
        String webpage = endpointUrl + "?wsdl";
        System.out.println("-- webpage = " + webpage);
        String result = null;

        result = Operations.getUrlContents(webpage,url_response_timeout);
        //debug
        System.out.println(result);
        
        // the wsdl page display is different in different platforms,
        // but it should contain the service name and target name space
       
        assertTrue("did not see expected string in wsdl page.  Expected =" 
        		   + serviceName + " and " + expectedTns + "\n actual = \n "+ result, 
                   ((result.indexOf(serviceName)!= -1) && (result.indexOf(expectedTns)!= -1)));   
    }

    /**
     * install an app.  It is expected that the name of the ear file
     * be the application name + ".ear".
     * @param appName
     * @throws Exception
     */
    private void installApps(String appName) throws Exception  {
        System.out.println("installing app "+appName);     
               
        String ear = (fvthome + "/build/installableApps/"+appName+ ".ear").replace('\\', '/');
        ApplicationManager appmgr = cell.getApplicationManager();
        Machine localMachine = Machine.getLocalMachine();
        RemoteFile appFile = new RemoteFile(localMachine, ear);       
        
        InstallWrapper wrapper = appmgr.getInstallWrapper(appFile);
        
        LinkedList<Scope> scopes = new LinkedList<Scope>(); 
        scopes.add(TopologyDefaults.getDefaultAppServer());        
        wrapper.getMapModulesToServers().setTarget(scopes);
        wrapper.getAppDeploymentOptions().setApplicationName(appName);
        
        wrapper.getAppDeploymentOptions().setUseDefaultBindings(true);
        try{
            System.out.println("install of app success? "+appmgr.install(wrapper).isSuccess());
        } catch (com.ibm.websphere.simplicity.exception.ApplicationAlreadyInstalledException e){
             System.out.println("app already installed, not a problem");
             e.printStackTrace();             
        }     
     
        System.out.println("syncing and saving workspace");
        cell.getWorkspace().saveAndSync();
        System.out.println("waiting for app to be ready");
        waitForAppReady(appName);
        System.out.println("starting app");
        try{
            appmgr.getApplicationByName(appName).start(true);
        } catch (com.ibm.ws.exception.RedundantStateChangeException e){
             System.out.println("caught exception starting app, maybe already installed and started.  Stacktrace:");
             e.printStackTrace(System.out);
        }    
       
    }

    /**
     * install an app.  It is expected that the name of the ear file
     * be the application name + ".ear".
     * @param appName
     * @throws Exception
     */
    private void unInstallApps(String appName) throws Exception  {

        System.out.println("uninstalling app "+appName);     
        ApplicationManager appmgr = cell.getApplicationManager();
        try{
        	
            System.out.println("uninstall of app success? "+appmgr.uninstall(appName).isSuccess());
            Thread.sleep(5000);

        } catch (Exception e){
             e.printStackTrace();  
             throw new Exception();  // if app can't be uninstalled, next test is invalid
        }     
     
        System.out.println("syncing and saving workspace");
        cell.getWorkspace().saveAndSync();       
    }

    private void waitForAppReady(String appName) throws Exception {
        int timeout = 120;
        ApplicationManager mgr = cell.getApplicationManager();
        EnterpriseApplication app = (EnterpriseApplication)mgr.getApplicationByName(appName);
        while(timeout-- > 0 && !app.isAppReady(true)) {
            try {
                Thread.sleep(1000);
            } catch(Exception e) {}
        }
    }
    

}
