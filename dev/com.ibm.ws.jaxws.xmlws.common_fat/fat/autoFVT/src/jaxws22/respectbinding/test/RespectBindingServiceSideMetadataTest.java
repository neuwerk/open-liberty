//
// @(#) 1.2 autoFVT/src/jaxws22/respectbinding/test/RespectBindingServiceSideMetadataTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/16/10 15:41:08 [8/8/12 06:57:38]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/03/10 btiffany                    New File
//

package jaxws22.respectbinding.test;


import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import common.utils.execution.ExecutionFactory;
import common.utils.execution.IExecution;
import common.utils.topology.Cell;
import common.utils.topology.IMachine;
import common.utils.topology.INodeContainer;
import common.utils.topology.IWASInstall;
import common.utils.topology.TopologyActions;


/**
 * This class will test that the use of jsr109v1.3 deploymment descriptors
 * and annotations 
 * governing respectbinding server-side behavior works as expected. 
 * 
 * 
 * 
 * Since: version 8
 * RTC Story: 18111, 23303, task 25543
 *  
 */
public class RespectBindingServiceSideMetadataTest extends FVTTestCase {
 
  private static String hostandPort = null;
  
  static {
	  try {
		  String host = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
		  String port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();                 
        
		  hostandPort  = "http://" + host + ":" + port ;
	  } catch (Exception e) {
		  // do nothing
	  }
  }
 

  
  //The main method.  Only used for debugging.
  public static void main(String[] args) throws Exception {
    boolean debug = true; 
    if(debug == false){
        TestRunner.run(suite());
    } else {
         RespectBindingServiceSideMetadataTest t = new RespectBindingServiceSideMetadataTest("x");
         //t.testValidRequiredAnnoTrueOk();
    }  
     
 }
  
  /**
   * test naming convention:
   * test(feature configuration in wsdl)(anno and dd configuration)(expected result)
   * for example:
   *    testInvalidRequiredAnnoTrueDDFalseOk
   *    wsdl contains an invalid and required extension element
   *    annotation on impl is true
   *    config in dd is false.
   *    App should start and be ok (because dd overrides anno andn disables validation)
   *    
   * test cases ending with "fail" correspond to apps that should not start. 
   * 
   * If neither, just need to preserve whatever we do. 
   * @throws Exception
   */
  public void testValidRequiredAnnoTrueFail() throws Exception {
      // context root jaxws22rb_validRequiredAnnoTrue
      checkDeployment("/jaxws22rb_validRequiredAnnoTrue", false);       
  }
  
  public void testValidRequiredDDTrueFail() throws Exception {
      checkDeployment("/jaxws22rb_validRequiredDDTrue", false);
  }
  public void testValidRequiredAnnoTrueDDFalseOk() throws Exception {
      checkDeployment("/jaxws22rb_validRequiredAnnoTrueDDFalse", true);
  }
  public void testValidRequiredNoFeatureOk() throws Exception {
      checkDeployment("/jaxws22rb_validRequiredNoFeature", true);
  }
  
  private void checkDeployment(String contextroot, boolean shouldDeploy) throws Exception {
      String url=hostandPort + contextroot + "/EchoService";
      boolean deployed = Operations.getUrlContents(url, 10).contains("This is a CXF Web Service");
      if(shouldDeploy){
          assertTrue("app should have deployed", deployed);
      } else {
          assertFalse("app should not have deployed", deployed);
      }
      System.out.println("passed");
  }
  
  /*
   * For all the invalid ones, recommend we initially build with a valid wsdl first,
   * and confirm that they deploy and we can retrieve their url's and fail.
   * Then change the wsdl to one with an invalid binding. That way, when they don't 
   * deploy, we're really testing that the wsdl is what caused it, and not just that
   * we got the build or testcase wrong. 
   */
  public void testInvalidRequiredNoFeatureOk() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredNoFeature", true);
  }
  public void testInvalidRequiredAnnoTrueFail() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredAnnoTrue", false);      
  }
  public void testInvalidRequiredAnnoTrueOnInputFail() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredAnnoTrueOnInput", false);  
  }
  public void testInvalidRequiredAnnoTrueOnOutputFail() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredAnnoTrueOnInput", false);  
  }
  public void testInvalidRequiredDDTrueFail() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredDDTrue", false);  
  }
  public void testInvalidRequiredDDTrueOnOperationFail() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredDDTrueOnOperation", false);  
  }
  public void testInvalidRequiredDDTrueOnFaultFail() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredDDTrueOnFault", false);  
  }
  public void testInvalidRequiredAnnoFalseDDTrueFail() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredAnnoFalseDDTrue", false);  
  }
  
  public void testInvalidRequiredDDFalseOk() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredDDFalse", true);      
  }
  public void testInvalidRequiredAnnoFalseOk()  throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredAnnoFalse", true);
      
  }

  public void testInvalidRequiredAnnoTrueDDFalseOk() throws Exception {
      checkDeployment("/jaxws22rb_invalidRequiredAnnoTrueDDFalse", true);      
  }  // in case we don't have a valid one
  
  public void testValidNotRequiredNoFeatureOk() throws Exception {
      checkDeployment("/jaxws22rb_validNotRequiredNoFeature", true);
  }
  public void testValidNotRequiredAnnoTrueOk() throws Exception {
      checkDeployment("/jaxws22rb_validNotRequiredAnnoTrue", true);
  }
  
  public void testInvalidNotRequiredNoFeatureOk() throws Exception {
      checkDeployment("/jaxws22rb_invalidNotRequiredNoFeature", true);
  }
  public void testInvalidNotRequiredDDTrueOk() throws Exception {
      checkDeployment("/jaxws22rb_invalidNotRequiredDDTrue", true);
  }
  
  
  
  
    
   /** 
     * A constructor to create a test case with a given name.
     * @param name The name of the test case to be created
     */
    public RespectBindingServiceSideMetadataTest(String name) {
        super(name);
    }
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(RespectBindingServiceSideMetadataTest.class);   
    }
    
    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws Exception {
    }
    
    // make sure everything is running at the end of each test
    public void tearDown()throws Exception{
    }
    
    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws Exception {
        super.suiteSetup(cr);
    }
    
    /**
     * install the plugin that checks our wsdl binding extension elements.
     * It's pretty benign, and time consuming to do, so we won't bother to uninstall it. 
     * @throws Exception
     */
    public void installPluginIfNeeded() throws Exception {
        Cell cell = TopologyDefaults.defaultAppServerCell;      
        
        // copy the  plugin to all WAS installations and run osgiCfgInit
        String libDir = AppConst.FVT_HOME + "/build/lib";        
        if( AppConst.FVT_HOME.contains("null/FVT")){
            // little hack for devel. 
            libDir="C:/test/wasx_kk1003.32/autoFVT/build/lib";
        }
        String pluginName = "com.ibm.wsfvt.wsdlvalidator.jar";
        Set<INodeContainer> nodes = cell.getNodeContainers();
        IExecution execution = ExecutionFactory.getExecution();
        String osgiCmd = null;
        boolean needToInstall = false;
        for(INodeContainer node : nodes) {
            IWASInstall install = node.getWASInstall();
            IMachine machine = node.getMachine();
            String pluginsDir = install.getInstallRoot() + "/plugins";
            // test to see if plugin is already installed by attempting to retrieve its contents.
            String contents = null;
            try{
                contents =  execution.executeGetFileContents(pluginsDir+"/"+pluginName, machine.getHostname());
            } catch (common.utils.execution.ExecutionException e){
                if(e.getCause().toString().contains("FileNotFoundException")){
                    System.out.println("plugin not found, we need to install");
                    needToInstall = true;
                    break;                    
                } else {
                    throw e;
                }    
            } // end catch            
            System.out.println("it appears that the test plugin is already present, won't reinstall.");
            continue;
            
        }  // end for
        
        if(!needToInstall){ return; }
        
        System.out.println("stopping the cell");
        try {
            cell.getSimplicityCell().stop();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println("stopped");
        
        for(INodeContainer node : nodes) {
            IWASInstall install = node.getWASInstall();
            IMachine machine = node.getMachine();
            String pluginsDir = install.getInstallRoot() + "/plugins";          
            System.out.println("installing plugin");
            execution.executeCopyFile(libDir+"/"+pluginName, TopologyActions.FVT_HOSTNAME, pluginsDir
                    + "/"+pluginName, machine.getHostname());   
            System.out.println("running osgicfginit");
            osgiCmd = install.getInstallRoot() + "/bin/osgiCfgInit" + machine.getExecFileSuffix();
            System.out.println(execution.executeCommand(osgiCmd, null, machine.getHostname()));
        
        }
        
 
        // restart the servers
        System.out.println("starting the cell");
        try {
            cell.getSimplicityCell().start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println("started,plugin installation completed");

    }
    
    
    //  our nonportable test teardownp method
    protected void suiteTeardown() throws Exception {
        super.suiteTeardown();
        System.out.println("suiteTeardown() called");
    }
    
  
}
