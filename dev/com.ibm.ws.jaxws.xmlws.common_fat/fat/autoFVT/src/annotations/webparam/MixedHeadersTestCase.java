/*
 * @(#) 1.6 autoFVT/src/annotations/webparam/MixedHeadersTestCase.java, WAS.websvcs.fvt, WASX.FVT 5/24/07 14:27:47 [7/11/07 13:12:20]
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
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.ImplementationAdapter;
import annotations.support.Support;
import annotations.webparam.headertests.HeaderTestClient;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * checks that we can't produce an incorrect invoke or response when parameters and results
 * are moved from soap headers to bodies and vice versa.  
 * 
 * here is the truth table for what we're doing, note how the evil client
 * tries sends requests and tries to receive responses in wrong part of soap message 
 * compared to what the server is expecting.
 * b = body, h=header
 * normal client      client_in  server_in  client_out  server_out
 *  method:
 *  IHOB                  h         h           b           b
 *  Ibob                  b         b           b           b
 *  NIbOb                 b         b           b           b
 *  Iboh                  b         b           h           h
 *  
 *  note the body/header mismatches below, these should all fail.
 * evil client
 *  method:
 *  IHOB                  b <-----> h           b           b
 *  Ibob                  h <-----> b           b           b
 *  NIbOb                 b         b           h <------>  b
 *  Iboh                  b         b           b <------>  h
 * 
 * Notes 12/14/06 f0650.10
 *    normalheaders - new defect, 411819.  call with input in header is failing.
 *      input and output in body works fine. 
 *      stopping for the day.
 * 
 * 
 */
public class MixedHeadersTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	private static String workDir = null;
    private final static String hostAndPort;
    private static ImplementationAdapter imp = Support.getImplementationAdapter("ibm", Support.getFvtBaseDir()+"/build/work/webparamheaders2");
	static{
        // figure out host and port.
        IAppServer server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
                           + server.getPortMap().get(Ports.WC_defaulthost);
    }

	public static void main(String[] args) throws Exception {
		// we'll only use this for development/debug
		if (true){
		TestRunner.run(suite());
		}
		else{
			MixedHeadersTestCase t = new MixedHeadersTestCase();
			//t.testNormalCalls();
            t.testAbnormalCalls();
		}	
	}
    
    
    /**
     * @testStrategy - invoke all the methods with properly formed soap messages and
     * make sure everything works.  This needs to pass before attempting the next one even
     * makes sense.
     *
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- invoke all the methods with properly formed soap messages and make sure everything works.  This needs to pass before attempting the next one even makes sense. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testNormalCalls() throws Exception{
        System.out.println("********* testNormalCalls starting *********");
        String wsdlurl;
        // hack - cached wsdl so we can tcpmon the thing
        /*
        String wsdlFile ="/eclipse/WautoFVT/build/work/webparamheaders/wsgen/HeaderTestService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webparamheadertest/HeaderTestService";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        wsdlurl = "file:" + newWsdlFile;
        */       

        
        String url = hostAndPort+"/webparamheadertest/HeaderTestImplService?wsdl";
        wsdlurl = url;
        String expected = "InputHeaderOutputBody received: Hello there service InputBodyOutputBody received: Hello there service InputBodyOutputHeader received: Hello there service ";
        String result = new HeaderTestClient().run(wsdlurl);
        assertTrue("wrong result from client,\n expected= "+expected +"\n  actual= " + result, result.compareTo(expected)==0);        
        System.out.println("********* test passed *******");
    }
    
    /**
     * @testStrategy - here we use a client constructed to send parameters and receive
     * results in the wrong places in the soap message.  Nothing should succeed. Every
     * method should get an exception on either the server or client side. 
     *
     * 
     *
     */
    
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- here we use a client constructed to send parameters and receive results in the wrong places in the soap message.  Nothing should succeed. Every method should get an exception on either the server or client side.   ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void testAbnormalCalls()throws Exception {
        System.out.println("********* testAbNormalCalls starting *********");
        String wsdlurl;
        // hack - cached wsdl so we can tcpmon the thing
        
        /*
        String wsdlFile ="/eclipse/WautoFVT/build/work/webparamheaders2/wsgen_evil/HeaderTestService.wsdl";
        String wsdl = (Operations.readTextFile(new File(wsdlFile)));
        String newWsdlFile = wsdlFile + "static";
        String relativeUrl = "/webparamheadertest/HeaderTestService";
        wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080" + relativeUrl );
        Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );
        wsdlurl = "file:" + newWsdlFile;
        */
               
        
        // we want to call the same client with a different sei class,
        // for that we have to spawn a new jvm with a different classpath,
        // use the an imp adapter to do that.
        // note that ant's classpath still needs to be in the system classpath to 
        // pick up some other things we need, like the service class.
        
        // now that directory will be in front of the classpath.
        // crude, but effective.
        String url = hostAndPort+"/webparamheadertest/HeaderTestImplService?wsdl";
        
        wsdlurl = url;
        String expected = "";
        int rc = imp.invoke("annotations.webparam.headertests.HeaderTestClient",
                 wsdlurl+" "+ expected);
        assertTrue("wrong result from client", rc==0);
        
        System.out.println("********* test passed *******");
    }
    
    public void setUp() {
    }
    
    
    /**
     * The suite method returns the tests to run from this suite.  
     * all methods starting with "test" will be be run.     *
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {    
            return new TestSuite(MixedHeadersTestCase.class); 
    }
    
    public MixedHeadersTestCase(String str) {
        super(str);
    }
    
    public MixedHeadersTestCase(){
        super();
    }
}
