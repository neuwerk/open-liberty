/*
 * @(#) 1.18 autoFVT/src/annotations/soapbinding/SOAPBindingRuntimeTestCase.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:22 [7/11/07 13:10:55]
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
 * 11/03/2006  btiffany    403039             note sei covered elsewhere, cleanup                        
 * 05/24/2007  jramos      440922             Integrate ACUTE
 *
 */

package annotations.soapbinding;
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
 * @throws Exception Any kind of exception
 *
 * @testStrategy Check that annotation appears on classes generated from wsdl,
 * that method override of class style works, that service works.  Check that improper
 * mutation is caught. 
 *   
 * @author btiffany
 * 
 * notes:
 * 12/9 d0649.23
 *       - doclitbare on class is passing d0649.23
 *       - dynwsdl test failing with old schema retrieval problem
 *      added proper host and port resolution
 *      - testImplServiceCachedWsdl() - looks like failing with variation of 405902 
 *              - dlb on method no workie. - 405902.1
 *      -rpclit failing w 409337 - Holder problems
 *      
 * 12/13 f0650.10 
 *      -dlboc still passes.
 *      - cached still fails on 405902.1.
 *      - rpclit is still failing, but 409337 was for webparamanno.ear, checking...stil there, reopened
 *        now 409337.1
 *      - dlb on meth (cached wsdl test, used dynamic this time ) - still fails, still open, dcut 12/14
 *      
 * 1/18 g0702.02 - all failing with demarshal probs, 402006.1, 405749.1, 414477    
 * 
 * 1/22 h0702.29 - dlw unmarshall failing.  will open 404081.1 on THAT.
 *                 would expect dlb unmarshal to fail due to 404081. 
 *                 
 *                 rpc-lit not throwing ws exception on returning null.
 *                 open def on THAT. - 416118
 *                 
 *                 Beta case failing with 415406 "this is not oneway...."
 *                 Supposedly fixed on n0703.03
 *      
 * 2/05 n0704.24 404081.1 is fixed. However bare methods aren't invoking. Added
 * objectfactory to ear, ref 409137.3, that fixed it.
 * 
 * Now down to 416118, rpc-lit method returning null.
 * 
 * 02/12 p0705.23 416118 still with us, 418587 is NOT yet in this build.   
 *      
 *
 */

public class SOAPBindingRuntimeTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	private static String workDir = null;
	private static ImplementationAdapter imp = null; 
	private static boolean setupRun = false; 	// for junit
    private static String hostAndPort = null;
    private static IAppServer server = null;
	
	static{		
		workDir = Support.getFvtBaseDir()+"/build/work/soapbindingruntime";
		imp = Support.getImplementationAdapter("ibm",workDir);
        server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                + server.getPortMap().get(Ports.WC_defaulthost);
	}

	public SOAPBindingRuntimeTestCase(String name){
		super(name);
	}
	
	public static void main(String[] args) throws Exception {	
        SOAPBindingRuntimeTestCase tc = null;
        if(false) TestRunner.run(suite());
        else{
            tc = new SOAPBindingRuntimeTestCase("");   
          // tc.testRpcDescopeCandidateService();
           // tc.testBetaService_docLitBareOnClass();
          // tc.testImplServiceCachedWsdl();
          tc.testImplServiceDynamicWsdl();
          tc.testSEIServiceDynamicWsdl();
          //tc.testMixedServiceDynamicWsdl();
            
        }
	}
	

    /**
     * junit needs this.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(SOAPBindingRuntimeTestCase.class); 
    }   
    
    // junit will run this before every test
    public void setUp(){
        // buildTest.xml takes care of it.
    } 
    
    void setupClient(){
    }
    
    /**
     * @testStrategy test that a class annotated with doclitbare can be 
     * compiled, deployed, and invoked from the client.  We don't test doclit wrapped because
     * that's the default we're using everywhere else. 
     * 
     * Server side = server target in buildTest.xml, AnnotationsSoapBindingBeta.ear
     * SoapBindAnno3Impl is the impl class, SEI is generated from SoapBindAnno3ImplService.wsdl,
     * which contains two doclit bare invocations due to a bare annotation on the class.
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testBetaService_docLitBareOnClass() throws Exception{
        System.out.println("******* testBetaService_docLitBareOnClass is starting ********");
        // http://btiffany.austin.ibm.com:9080/soapbinding/SoapBindAnno3ImplService
        String url = hostAndPort+"/soapbinding/SoapBindAnno3ImplService?wsdl";
        
        String expected = "echodlw replies: hello there dlwechodlb replies: hello dlb";
        String actual = (new annotations.soapbinding.testdata.SoapBindAnno3Client()).getResult(url);
        assertTrue("unexpected result. Expected = "+expected+ "\nActual = "+actual,
                    actual.compareTo(expected)==0);        
        System.out.println("******* test passed ********");
        
    }
    
    /**
     * testStrategy - change sei over to wrapped with doing nothing else, and deploy.
     * That's something a user might do.  Should get at least a graceful failure.
     * 
     * Despite the name, due to beta limitation of no duplicate service names,
     * this can't be installed to server if testBetaService is also installed.
     * 
     * 
     * 02.16.07 - This test will fail as we actually can package a method with bare
     * wsdl and annotated it wrapped, and it will invoke and deploy.  But I don't think
     * this is a valid defect.  The annotations spec only requires the runtime to 
     * check for consistency between annotations, not between annotations and wsdl.
     * Removing this test --bt.
     * 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testBadBetaService() throws Exception{
        System.out.println("******* testbadBetaService is starting ********");
        // http://btiffany.austin.ibm.com:9080/soapbinding/SoapBindAnno3ImplService
        String url = hostAndPort+"/soapbindingbad/SoapBindAnno3ImplService?wsdl";
        
        /* we should get rc 23, webservice exception */
        String expected = "rc=23";
        String actual = (new annotations.soapbinding.testdata.SoapBindAnno3Client()).getResult(url);
        assertTrue("unexpected result. Expected = "+expected+ "\nActual = "+actual,
                    actual.compareTo(expected)==0);
        System.out.println("******* testBadBetaService passed ********");
        
    }
    
    
    /**
     * @testStrategy  test that doclit wrapped and doclit bare annotated methods 
     * have beans generated, interface generated, be deployed, and invoked from 
     * unmanaged client.   This test uses an  impl class, no sei, expects
     * the server to generate the wsdl on the fly, and expects ?wsdl to work,
     * all things that were broken in beta. 
     * 
     * Uses AnnotationsSoapBindingImpl.ear with SoapBindRtImpl.java as impl class.
     * 
     * This has a DLB anno on the class, with a DLW anno on a method, so it also
     * checks anno override at the method level. 
     * 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testImplServiceDynamicWsdl() throws Exception {
    	System.out.println("********** testImplServiceDynaicWsdl is running **********");      	
        String url = hostAndPort+"/soapbindingimpl/SoapBindRtImplService?wsdl";        
        String expected = "echodlb replies: hello dlbechodlw replies: hello there dlw";
        String actual = (new annotations.soapbinding.testdata.SoapBindRtImplClient()).getResult(url);
        assertTrue("unexpected result. Expected = "+expected+ "\nActual = "+actual,
                    actual.compareTo(expected)==0);
        System.out.println("******* test passed ********");
    }
    
    /** same as ImplService, except annos are on sei */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testSEIServiceDynamicWsdl() throws Exception {
        System.out.println("********** testSEIServiceDynaicWsdl is running **********");       
        String url = hostAndPort+"/soapbindingimpl2/SoapBindRtImplService?wsdl";        
        String expected = "echodlb replies: hello dlbechodlw replies: hello there dlw";
        String actual = (new annotations.soapbinding.testdata.SoapBindRtImplClient()).getResult(url);
        assertTrue("unexpected result. Expected = "+expected+ "\nActual = "+actual,
                    actual.compareTo(expected)==0);
        System.out.println("******* test passed ********");
    }
    
    /*
     * - working as designed, test is invalid.  
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _testMixedServiceDynamicWsdl() throws Exception {
        System.out.println("********** (def 421740) testMixedServiceDynaicWsdl is running **********");       
        String url = hostAndPort+"/soapbindingimpl3/SoapBindRtImplService?wsdl";        
        String expected = "echodlb replies: hello dlbechodlw replies: hello there dlw";
        String actual = (new annotations.soapbinding.testdata.SoapBindRtImplClient()).getResult(url);
        assertTrue("unexpected result. Expected = "+expected+ "\nActual = "+actual,
                    actual.compareTo(expected)==0);
        System.out.println("******* test passed ********");
    }
    
    /**
     * @testStrategy - same as testImplService except we are working around
     * lack of dynamic wsdl
     * @throws Exception
     * Uses AnnotationsSoapBindingImpl.ear with SoapBindRtImpl.java as impl class.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testImplServiceCachedWsdl() throws Exception {
        System.out.println("********** testImplServiceCachedWsdl is running **********");
        
        IAppServer server = QueryDefaultNode.defaultAppServer;
        String hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                + server.getPortMap().get(Ports.WC_defaulthost);
        
        //String hostAndPort= com.ibm.ws.wsfvt.build.tools.AntProperties.getServerURL();
        String relativeUrl = "/soapbindingimpl/SoapBindRtImplService";
        
        // To test before dynamic wsdl is available, patch up the wsdl
        // we produced when building the client and use that. 
        String wsdlFile = Support.getFvtBaseDir()+"/build/work/annotations/soapbinding/serverimpl/SoapBindRtImplService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        
        //String url = imp.getServerURL()+"/soapbindingimpl/SoapBindRtImplService?wsdl";
        String url= "file:" + newWsdlFile;
        String expected = "echodlb replies: hello dlbechodlw replies: hello there dlw";
        String actual = (new annotations.soapbinding.testdata.SoapBindRtImplClient()).getResult(url);
        assertTrue("unexpected result (def 405902?). Expected = "+expected+ "\nActual = "+actual,
                    actual.compareTo(expected)==0);
        System.out.println("******* test passed ********");
        
        
    }
    

    /**
     * @testStrategy test a service based on rpc-literal-wrapped.
     * This will also test null value handling (jsr224 3.6.2.3) and 
     * appropriate use of holders in rpc style
     * 
     *  **** descoping candidate ***
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="test a service based on rpc-literal-wrapped. This will also test null value handling (jsr224 3.6.2.3) and appropriate use of holders in rpc style  **** descoping candidate *** ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRpcOnImpl() throws Exception {
        
        System.out.println("********** testRpcOnImpl is running **********");
        String url = null;
        boolean debug = false;
        
        if (debug){
            // for debugging, use static wsdl so we can change the port.
            String relativeUrl = "http://localhost:19080/soapbindingrpc/SoapBindRpcImplService"; 
            String wsdlFile = Support.getFvtBaseDir()+"/build/work/annotations/soapbinding/serverrpc/SoapBindRpcImplService.wsdl";
            String wsdl = (Operations.readTextFile(new File(wsdlFile)));
            String newWsdlFile = wsdlFile + "static";
            wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", relativeUrl );
            Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
            url= "file:" + newWsdlFile;
        } else {        
            url = hostAndPort +"/soapbindingrpc/SoapBindRpcImplService?wsdl";
        }    
        
        try{
            int rc = (new annotations.soapbinding.testdata.SoapBindRpcClient()).runtest(url);
            assertTrue("unexpected return code from client. Expected rc=0, got rc="+ String.valueOf(rc),
                    rc==0);
        } catch (Exception e){
            e.printStackTrace(System.out);
            fail("received unexpected exception");
        }
        
 
        System.out.println("******* test passed ********");
    }
    
    /**
     * same as impl test except annotation is on an sei.
     * 
     * 
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testRpcOnSei() throws Exception {
        
        System.out.println("********** testRpcOnSei is running **********");
        String url = null;
        boolean debug = false;
        
        if (debug){
            // for debugging, use static wsdl so we can change the port.
            String relativeUrl = "http://localhost:19080/soapbindingrpc/SoapBindRpcImplService"; 
            String wsdlFile = Support.getFvtBaseDir()+"/build/work/annotations/soapbinding/serverrpc/SoapBindRpcImplService.wsdl";
            String wsdl = (Operations.readTextFile(new File(wsdlFile)));
            String newWsdlFile = wsdlFile + "static";
            wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", relativeUrl );
            Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
            url= "file:" + newWsdlFile;
        } else {        
            url = hostAndPort +"/soapbindingrpc2/SoapBindRpcImplService?wsdl";
        }    
        
        try{
            int rc = (new annotations.soapbinding.testdata.SoapBindRpcClient()).runtest(url);
            assertTrue("unexpected return code from client. Expected rc=0, got rc="+ String.valueOf(rc),
                    rc==0);
        } catch (Exception e){
            e.printStackTrace(System.out);
            fail("received unexpected exception");
        }
        
 
        System.out.println("******* test passed ********");
    }

}
