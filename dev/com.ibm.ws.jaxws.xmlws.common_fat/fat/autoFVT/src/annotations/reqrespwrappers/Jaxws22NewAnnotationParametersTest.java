//
// @(#) 1.3 autoFVT/src/annotations/reqrespwrappers/Jaxws22NewAnnotationParametersTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 7/27/10 10:51:37 [8/8/12 06:57:47]
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
// 07/16/10 btiffany                    New File
// 07/27/10 btiffany    662771          Different exception on win08
//

package annotations.reqrespwrappers;
import annotations.reqrespwrappers.server.*;


import java.io.File;
import java.net.URL;
import javax.xml.ws.WebServiceException;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;


/**
 * This class will test that tooling (wsgen) properly processes these new 
 * annotation parameters:
 *  partName on RequestWrapper, ResponseWrapper
 *  messageName on WebFault
 *  
 *  As these wsdl elements are intermediate elements and do not affect the
 *  final soap message on the wire, there is no runtime check.  This
 *  test merely watches for any regression in wsgen behavior. 
 *  
 *  An impl is compiled and wsdl produced during build time in buildTest.xml,
 *  this test inspects that wsdl for the correct customizations.
 *  
 *  We considered having the runtime validate packaged wsdl against these parameters
 *  but rejected it since they have no effect on the soap message. 
 * 
 * Since: version 8
 * RTC Story: 15874, task 31465, 28815
 *  
 */
public class Jaxws22NewAnnotationParametersTest extends FVTTestCase {   
   
    static String hostAndPort= null;
    static String wsdlFile = null;
    
    /** 
     * The main method.  Only used for debugging.
     * @param args The command line arguments
     */
    public static void main(String[] args) throws java.lang.Exception  {
       TestRunner.run(suite());
       // Jaxws22NewAnnotationParametersTest t = new Jaxws22NewAnnotationParametersTest("x");
       //  t.suiteSetup2();
       //t. testBasicEjbsInWarUsingHTTP();
    }
    
    public void testRequestWrapperPartName() throws java.lang.Exception {
       if (isZos()){ return; };
       boolean test =  Operations.readTextFile(new File(wsdlFile)).contains(
               "<part name=\"customizedRequestPart\" element=\"tns:requestOp\"/>");
       assertTrue("expected customization not found in wsdl", test);
          
    }
    
    public void testResponseWrapperPartName() throws java.lang.Exception {
        if (isZos()){ return; };
        boolean test = Operations.readTextFile(new File(wsdlFile)).contains(
                "<part name=\"customizedResponsePart\" element=\"tns:responseOpResponse\"/>");
        assertTrue("expected customization not found in wsdl", test);
    
    }
    
    public void testFaultMessage() throws java.lang.Exception {
        if (isZos()){ return; };
        boolean test = Operations.readTextFile(new File(wsdlFile)).contains(
                "<message name=\"customizedWebFaultMessage\">");
        assertTrue("expected customization not found in wsdl", test);
    
    }
    
    /**
     * To check for other inconsistencies in the wsdl, invoke a client and make 
     * sure we get as far as it complaining about not being able to reach the service.
     * There is no service deployed for this, so we can't -really- invoke it. 
     * We just don't want to see some wsdl validation error when we try.
     * @throws java.lang.Exception
     */
    public void testClient() throws java.lang.Exception{
        String[] expected = {"java.net.SocketException", 
                             "java.net.UnknownHostException",
                             "java.nio.channels.UnresolvedAddressException"};
        
        if (isZos()){ return; };
        try{
           String result =  (new Jaxws22ImplCheckService(new URL("file:/"+wsdlFile))).getJaxws22ImplCheckPort().
                requestOp("hello");
           System.out.println("result =" + result);
        } catch (WebServiceException e){ 
            for(int i=0; i<expected.length; i++){
                if(e.getMessage().contains(expected[i]) ){
                    System.out.println("caught expected exception");
                    return; 
                }    
            }                            
            e.printStackTrace(System.out);
        }
        fail("did not get expected exception message: "+expected);
        
    }
    
    // skip if on z.
    private boolean isZos(){
        if(System.getProperty("os.name").toLowerCase().contains("z/os")){
            String msg = "jaxws tooling and therefore this test not supported on z/os";
            System.out.println(msg);
            warning(msg);
            return true;
            
        } else {return false; }
    }
    
    
   // ================== junit plumbing beneath here ==================
    
    /** 
     * A constructor to create a test case with a given name.
     * @param name The name of the test case to be created
     */
    public Jaxws22NewAnnotationParametersTest(String name) {
        super(name);
    }
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        return new TestSuite(Jaxws22NewAnnotationParametersTest.class);   
    }  

 
    // perform setup for each testcase - vanilla junit method
    protected void setUp() throws java.lang.Exception {
    }
    
    // teardown for teach testcase
    public void tearDown()throws java.lang.Exception{
    }
    
    // our nonportable test setup method
    protected void suiteSetup(ConfigRequirement cr) throws java.lang.Exception {
        super.suiteSetup(cr);
        System.out.println("suiteSetup() called");
        suiteSetup2();
    }
    
    // for debug, a method we can call outside junit. 
    private void suiteSetup2()throws java.lang.Exception {
        String host = TopologyDefaults.getDefaultAppServer().getNode().getHostname();
        String port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();  
        hostAndPort  = "http://" + host + ":" + port ;       
        System.out.println("hostAndPort = "+hostAndPort );        
        wsdlFile = AppConst.FVT_BUILD_WORK_DIR.replace("\\","/")
            + "/annotations/reqrespwrappers/jaxws22/Jaxws22ImplCheckService2.wsdl";
        System.out.println("Contents of generated wsdl file:");
        System.out.println(Operations.readTextFile(new File(wsdlFile)));
        
    }
    
//  our nonportable test teardown method
    protected void suiteTeardown() throws java.lang.Exception {
        super.suiteTeardown();
        System.out.println("suiteTeardown() called");
    }
    

}    