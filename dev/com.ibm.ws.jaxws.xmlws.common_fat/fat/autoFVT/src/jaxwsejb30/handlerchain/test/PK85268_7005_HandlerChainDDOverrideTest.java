/**
 * autoFVT/src/jaxwsejb30/handlerchain/test/PK85268_7005_HandlerChainDDOverrideTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 
 * 
 * IBM Confidential OCO Source Material
 * (C) COPYRIGHT International Business Machines Corp. 2006, 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date       UserId        Defect        Description
 * ----------------------------------------------------------------------------
 * 04/10/2009 btiffany     582987.1        New File
 * 11/10/2010 btiffany     678119          Tolerate wccm warnings in output string
 * 
 */
package jaxwsejb30.handlerchain.test;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import jaxwsejb30.handlerchain.server.WarEchoAnnoImpl;
import jaxwsejb30.handlerchain.server.WarEchoAnnoImplService;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.Utils;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import common.utils.execution.ExecutionFactory;
import common.utils.execution.IExecution;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.TopologyActions;
import common.utils.topology.visitor.QueryDefaultNode;


/**
 * This test checks that handlerchain declarations in the deployment descriptors of 
 * webservices.xml function and override annotations.  
 * 
 * @author btiffany
 *
 */
public class PK85268_7005_HandlerChainDDOverrideTest extends FVTTestCase {
    private static IAppServer server = QueryDefaultNode.defaultAppServer;
    private static String testMachine = server.getMachine().getHostname();
//    private  static String testPort =  (server.getPortMap().get(Ports.BOOTSTRAP_ADDRESS)).toString();
    private  static String defaultPort =  (server.getPortMap().get(Ports.WC_defaulthost)).toString(); 
    private static String fvtHostName=  TopologyActions.FVT_HOSTNAME;

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
    
    /**
     * This method controls which test methods to run.
     * 
     * @return A suite of tests to run
     */
    public static Test suite() {
        
        return new TestSuite(PK85268_7005_HandlerChainDDOverrideTest.class);
    }
    
    /**
     * test an war module with a handlerchain annotation, no dd override,
     * we should receive a response indicating the annotation is in effect
     * We already test this extensively elsewhere so it's not implemented here.
     * @throws Exception
     */
    public void _testWarNoDD() throws Exception {        
    }
    
  //==================================================================
  // The methods suiteSetup() and suiteTeardown() has been removed from
  // here. The reason is becaues they override the same method in the
  // parent class. In the parent class, the suiteSetup() will start
  // server before test case is executed, and suiteTeardown() will stop
  // server after test case execution. Therefore, if this class
  // override these methods, it need to ensure the methods of parent 
  // class are invoked. Since in these class these two method only 
  // output some message to system standard output. There is no much
  // value to keep them. So just removed.
  //==================================================================
  
    
    /**
     * test a war module with the handlerchain annotation overridden by dd.
     * The response should reflect the handler specified in webservices.xml
     * @throws Exception
     */
    public void testWarDDO() throws Exception {
       // String urlString="http://localhost:9081/"jaxwsejb30handlerchain_warechoannoimpl/warechoannoimplservice?wsdl";
        String urlString="http://"+ testMachine+ ":"+ defaultPort + "/jaxwsejb30handlerchain_warechoannoimpl/warechoannoimplservice?wsdl";
        System.out.println("using supplied wsdl url: "+urlString );
        java.net.URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        QName qn = new QName("http://server.handlerchain.jaxwsejb30/", "WarEchoAnnoImplService");
        WarEchoAnnoImpl s = new WarEchoAnnoImplService(url, qn).getWarEchoAnnoImplPort();
        System.out.println("invoking impl1Echo");
        String result = s.echo ("hello");  
        System.out.println("result = "+result);
        // if override did not work, we would get 
        //helloWASSOAPHandler_Inbound:WASSOAPHandler_Outbound
        String expected = "helloAnotherHandler_Inbound:AnotherHandler_Outbound:";        
        System.out.println("expected= "+ expected);
        assertTrue("unexpected result", result.compareTo(expected)==0);
    }

    
//==================================================================
// The test case testMgdClientDDO is commented out.
// The reason is that this case depends on launchClient script to
// start managed client, however the launchClient script is not
// available in Liberty yet.
//==================================================================

//    /**
//     * test an app running inside the managed client environment 
//     * with a handlerchain configured in application-client.xml, and an annotation,
//     * dd should override annotation. 
//     * @throws Exception
//     */
//    public void testMgdClientDDO() throws Exception {
//        
//        //String urlString="http://"+ testMachine+ ":"+ defaultPort + "/jaxwsejb30handlerchain_warechoannoimpl/warechoannoimplservice?wsdl";
//        String result = callLaunchClient("jaxwsejb30hcclient.ear", null, null, null);
//        String expected = "helloAnotherHandler_Outbound:AnotherHandler_Inbound:AnotherHandler_Outbound:AnotherHandler_Inbound:";
//        System.out.println("expecting to find this within actual: " + expected);
//        System.out.println("actual: " + result);
//        assertTrue("received wrong result", (result.contains(expected)));
//        
//        expected = "Systemerr: null";
//        System.out.println("expecting to find this within actual, which means systemerr was empty: " + expected);
//        assertTrue("received wrong result, Systemerr was not empty", (result.contains(expected)));
//        
//    }
//    
//    private String callLaunchClient(String earName, String clientNo, String args1, String args2) throws Exception {
//
//        IExecution execution = ExecutionFactory.getExecution();
//        
//        String cmd =  AppConst.WAS_HOME + "/bin/launchClient"
//                     + execution.executeGetLocalExecFileSuffix();
//
//        List parms = new ArrayList();
//        parms.add((AppConst.FVT_HOME
//                  + "/build/installableApps/" + earName).replace('\\', '/'));
//
//        parms.add("-CCBootstrapHost=" + testMachine);
//        parms.add("-CCBootstrapPort=" + testPort);
//
//        if (clientNo != null) {
//           parms.add(clientNo);
//        }
//
//        if (args1 != null) {
//           parms.add(args1);
//        }
//
//       if (args2 != null) {
//           parms.add(args2);
//        }
//       System.out.println("arg1, arg2 " + args1  + " " + args2 + "\n");
//         String actual = execution.executeCommand(cmd,
//                                                   parms,
//                                                   fvtHostName,
//                                                   AppConst.WAS_HOME + "/bin",
//                                                   new String[] {});
//          String sysOutData = Utils.parseSTAFResult("System.out", actual);
//          String sysErrData = Utils.parseSTAFResult("System.err", actual);
//          
//          return ("Systemout: "+sysOutData + " Systemerr: "+sysErrData);
//
//
//      }
}
