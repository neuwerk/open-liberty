/*
 * @(#) 1.9 autoFVT/src/annotations/webresult_g2/runtime/test/WebResult_g2RuntimeTest.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:07 [7/11/07 13:12:36]
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
 *  08/02/06   btiffany   LIDB3296.31.01     new file
 * 05/24/2007  jramos      440922             Integrate ACUTE
 *
 */

package annotations.webresult_g2.runtime.test;
import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.Support;

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
 * Notes 12.11.06 
 *   webresultannog2 - found error in war file, fixed.
 *                      passed in wrap style, removing 405902 w.a. & retrying. - failed 
 *                      due to 405902.1 - soap routing problems.
 *                      
 *   12.14.06 f0650.10
 *    testWebResultBeta - passed
 *    testWebResultRuntimeCachedWsdl -
 *    testWebResultRuntimeDynamicWsdl -  both failing same way. - 405902.1, duped to 416191
 *    
 * 01.26.07 n0703.19
 *    beta test failing with 416195 opened by Min. "number of child objects..."
 *    This is due to soap:action="", we'll wait for that fix. 
 *     
 *    call to echo failing unable to unmarshal - but doesn't look like the others.
 *    same thing with locatecustomer2
 *    both of these are bare, so 416191 is probably the cause, nothing new to do here....
 *    
 * 02.06.07 n0724.04 wrapped methods working, header case failing since it gets
 * unwrapped.  Rebuild ear using xjc, now bare working and wrapped cases are failing. sheesh.   
 * I think what's going on is that the namespace is changed by the webservice annotation
 * and the xjc beans are generated in THAT package, which the server does not look for.
 * Other than that, everything can be made to work.  Changing the package of the server
 * and removing namespace customization would let the test pass, 
 * but we'll hold off on that until they can look at the xjc classpath
 * problem, opened 419201 on that. 
 * 
 * Refactored into different package to get around packagge lookup problems.
 * Didn't work, jaxb beans overwrite derived classes. So rather than try
 * to adapt wrapped methods, try to get bare method to work. 
 * 
 * Changed echo method to use a derived object, still failing.
 * We're basically going to give up on webresult(header=true), see 409137.3,
 * due to limitations in handling bare style invokes. 
 * Test client is changed to not call that method. 
 * 
 * 4/5/07 v0713.14 - Everything is passing.  beta test is failing but that
 * looks like some sort of remant from some QoS testing done on same machine.
 * 
 * 5.2.07 fix bad url on testWebResultBeta
 *                  
 *
 */
public class WebResult_g2RuntimeTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
    
    private final static String hostAndPort;
    private String expectedResult = "stoneage_barney_rubble_mystery_barney_tbd__server replies: testing";
    

    static{
        // figure out host and port.
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
    }
    
	public static void main(String[] args) throws Exception {	
		TestRunner.run(suite());
        /*
          
         
        WebResult_g2RuntimeTest t = new WebResult_g2RuntimeTest();        
        //t.testWebResultBeta();
        //t.testWebResultRuntimeCachedWsdl();
        t.testWebResultRuntimeDynamicWsdl();
        */
        

	}
	

    /**
     * junit needs next 3.  
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
         return new TestSuite(WebResult_g2RuntimeTest.class); 
    }   
    
	public WebResult_g2RuntimeTest(String str) {
		super(str);
	}
	
	public WebResult_g2RuntimeTest(){
		super();
	}
    
    // junit will run this before every test
    public void setUp(){    
    }
    
    /** @testStrategy deploy a webservice with two methods having the @Webresult
     * annotation.  One is defaulted, one has every parameter defined.
     * Generate derived classes, invoke the client, and make sure it all works.
     * Gets interesting because the header parameter on webresult forces jaxws into
     * unwrapped style. 
     * 
     * @throws Exception
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWebResultRuntimeDynamicWsdl() throws Exception {
    	System.out.println("********** testWebResultRuntimeDynamicWsdl() is running **********");
    	
    	// the client won't compile in eclipse, but once built from ant,
    	// this test should be runnable from eclipse.
    	//String url =  AntProperties.getServerURL()+"/AnnotationsWebResultg2Runtime/webresultg2runtime?wsdl";
        String url =  hostAndPort+"/webresultannog2/webresultannog2?wsdl";
    	String result = annotations.webresult_g2.runtime.client.WebResult_g2RuntimeClient.runme(url);
    	assertTrue("received wrong result, actual= "+result + 
    			"\n expected = "+expectedResult, result.compareTo(expectedResult)==0);        
        System.out.println("********** test passed **********");
    
    }
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWebResultRuntimeCachedWsdl() throws Exception {
        System.out.println("********** testWebResultRuntimeCachedWsdl() is running **********");
        
        // To test before dynamic wsdl is available, patch up the wsdl
        // we produced when building the client and use that. 
        String wsdlFile = Support.getFvtBaseDir()+"/build/work/annotations/webresult_g2/runtime/WebResultCheckService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webresultannog2/webresultannog2";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        String url = "file:" + newWsdlFile;
        
        
        // the client won't compile in eclipse, but once built from ant,
        // this test should be runnable from eclipse.
        //String url =  AntProperties.getServerURL()+"/AnnotationsWebResultg2Runtime/webresultg2runtime?wsdl";
        String result = annotations.webresult_g2.runtime.client.WebResult_g2RuntimeClient.runme(url);
        assertTrue("received wrong result, actual= "+result + 
                "\n expected = "+expectedResult, result.compareTo(expectedResult)==0);
        System.out.println("********** test passed **********");
    
    }  
    
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testWebResultBeta() throws Exception {
        System.out.println("********** testWebResultBeta() is running **********");
        
        // the client won't compile in eclipse, but once built from ant,
        // this test should be runnable from eclipse.
        //String url =  AntProperties.getServerURL()+"/webresultannobeta/webresultannobeta/wsdl/WRCBetaImplService.wsdl";
        String url =  hostAndPort + "/webresultannobeta/webresultannobeta?wsdl";
        String result = annotations.webresult_g2.runtime.client.WRCBetaClient.runme(url);
        String expected = "echo1 call returns: calling echo1echo2 call returns: calling echo2";
        assertTrue("received wrong result, actual= "+result + 
                "\n expected = "+expected, result.compareTo(expected)==0);
        System.out.println("*********** test passed **************");
    
    }
    
}
