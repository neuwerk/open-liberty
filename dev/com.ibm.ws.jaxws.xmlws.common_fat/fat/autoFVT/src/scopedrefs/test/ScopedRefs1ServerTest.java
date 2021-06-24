//
// @(#) 1.4 autoFVT/src/scopedrefs/test/ScopedRefs1ServerTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 3/24/11 15:05:00 [8/8/12 06:58:00]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/01/10    btiffany   F743-34250        intial module tests
// 11/17/10    btiffany   F743-34252        add basic java:app tests.
// 12/08/10    btiffany   F743-34262        restrict global tests on z due to complications arising from multi-server test.
// 01/14/11    btiffany   681019.1          update for ltpa security 
// 03/24/11    btiffany   693901            specify bootstrap port for launchclient so it doesn't try to talk to dmgr.

package scopedrefs.test;
import scopedrefs.client.CommonTestClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.test.framework.FVTTestCase;
import common.utils.topology.Cell;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.OperatingSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
  

/**
 * This class will test that java:module, java:app and, eventually, java:global references
 * work appropriately with jax-ws web services.
 * 
 * Tests are named according to the type of reference they are exercising.
 * For example, test_mod_... is testing a module ref.  test_glo... is testing a global ref.
 * 
 * All the clients to access the various webservices for testing are newed up during
 * suiteSetup.  Then the tests use those clients to invoke a service, which in turn will
 * lookup and invoke another service to check that the lookup reference is working.  
 * For example client --> service1 --> service2. 
 * Service1 will lookup and invoke service2, then service2  will return a 
 * string back to Service1 and then back to the client for checking. 
 * 
 * See readme.txt for a more detailed explanation of the design. 
 * 
 * Since: version 8
 *  
 */
public class ScopedRefs1ServerTest extends FVTTestCase {   
   
    static String port = null;
//    static String bootStrapPort = null; // the bootstrap port of the app server (not the dmgr)
    static String hostAndPort= null;    
    static boolean serverSideApp2PortNumberHasBeenUpdated = false;
    private static Cell cell = null;
    
    /** 
     * The main method.  Only used for debugging.
     * @param args The command line arguments
     */
    public static void main(String[] args) throws java.lang.Exception  {
       // TestRunner.run(suite());
        ScopedRefs1ServerTest t = new ScopedRefs1ServerTest("x");
        t.suiteSetup2();
        t.test_100_app_between_ejb_and_war();
        /*
        t.test_10_mod_between_ejbs_usingrefinxml();
        t.test_15_mod_between_ejbs_usingrefinannotation();      
        */
    }
    
    /**
     * check that one ejb can lookup and invoke another in the same module
     * using a module ref.  Normally, each ejb bean is in its own namespace.
     * 
     * we call b2 which in turn invokes b1 through a module reference 
     * @throws Exception
     */    
    // http://localhost:9080/scopedrefsapp1mod1/B1Service, B2..
    //  context root = name of jar file and is set by endptenabler
    public void test_10_mod_between_ejbs_usingrefinxml() throws Exception{
        String result = CommonTestClient.app1mod1ejb2_invokeapp1mod1ejb1();
        String expected = "a1m1b1";
        System.out.println("exected = "+ expected);
        System.out.println("result: " + result);
        assertTrue(result.contains(expected));     
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     * check that one ejb can lookup and invoke another in the same module
     * using a module ref.  
     * We call b1 which in turn invokes b2 through a reference.
     * This is similar to test_10 except the reference was established by
     * using an @WebServiceRef annotation instead of putting it into ejb-jar.xml.
     * @throws Exception
     */
    public void test_15_mod_between_ejbs_usingrefinannotation() throws Exception{
        String result = CommonTestClient.app1mod1ejb1_invokeapp1mod1ejb2();
        String expected = "a1m1b2";
        System.out.println("exected = "+ expected);
        System.out.println("result: " + result);        
        assertTrue(result.contains(expected)); 
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     * check that one ejb can lookup and invoke another in the same war module
     * using a module ref.
     * 
     * This SHOULD work regardless, as there is only one namespace in
     * a war module, as opposed to one per bean in an ejb module.
     * We're checking that nothing's broken here.     
     *   
     * We call b1 which in turn invokes b2 through a reference.
     * This is similar to test_10 except the reference was established by
     * using an @WebServiceRef annotation instead of putting it into ejb-jar.xml.
     * @throws Exception
     */
    public void test_20_mod_between_ejbs_in_war() throws Exception{
       String result = CommonTestClient.app1mod2ejb2_invokeapp1mod2ejb1();
       System.out.println("result: " + result);      
       String expected = "a1m2b1";
       System.out.println("exected = "+ expected);
       assertTrue(result.contains(expected));     
       assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }   
    public void test_25_mod_between_ejbs_in_war() throws Exception{
        String result = CommonTestClient.app1mod2ejb1_invokeapp1mod2ejb2();
        String expected = "a1m2b2";
        System.out.println("exected = "+ expected);
        System.out.println("result: " + result);        
        assertTrue(result.contains(expected));  
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
     }   
    
    /**
     * check that module refs work between a servlet and ejb in a war.
     * @throws Exception
     */ 
    // http://localhost:9080/scopedrefsapp1mod3/B2Service, B1Service
    public void test_30_mod_between_ejb_and_servlet_in_war() throws Exception{
        String result = CommonTestClient.app1mod3ejb2_invokeapp1mod3ejb1();
        String expected = "a1m3b1";
        System.out.println("exected = "+ expected);
        System.out.println("result:>" + result+"<");        
        assertTrue(result.contains(expected));  
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
        
    }
    public void test_35_mod_between_servlet_and_ejb_in_war() throws Exception{
        String result = CommonTestClient.app1mod3ejb1_invokeapp1mod3ejb2();
        System.out.println("result: " + result);        
        String expected = "a1m3b2";
        System.out.println("exected = "+ expected);
        assertTrue(result.contains(expected)); 
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
        
    }
    
    /**
     * test that two module level refs do NOT work across modules.
     * The jndi ref used was declared in module2's web.xml.
     * It should not be visible to module 3.  
     * @throws Exception
     */
    public void test_140_mod_between_modules_negative() throws Exception{
        scopedrefs.generatedclients.app1.mod3.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod3.b1.B1Service();
        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:module/env/service/a1m2b1");
        System.out.println("client received: "+ result);
        assertTrue("calling service did not receive the expected naming exception", result.contains("javax.naming.NameNotFoundException"));
        
    }
    
    
    /**
     * Test that a ref declared in ejb-jar of module1 can be used by module3.  
     * @throws Exception
     */
    public void test_100_app_between_ejb_and_war() throws Exception{
        scopedrefs.generatedclients.app1.mod3.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod3.b1.B1Service();
        //                                      <service-ref-name>java:app/env/service/a1m1b2_appref</service-ref-name>
        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:app/env/service/a1m1b2_appref");
        
        System.out.println("actual = "+ result);
        String expected = "a1m1b2";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     *  a reference is declared in a1m3b2.  Then 
     *  m2b1 tries to invoke m1b1 using that reference. 
     */
    public void test_110_app_between_ejb_and_ejb_in_war() throws Exception{
        //java:app/env/service/a1m1b1
        scopedrefs.generatedclients.app1.mod2.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod2.b1.B1Service();

        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:app/env/service/a1m1b1");
        
        System.out.println("actual = "+ result);
        String expected = "a1m1b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
        
    }
    
    
    /**
     * A applications scoped reference to a1m2b2 is declared in a1m2b1 
     * Then a1m3b1 tries to use it.   
     * 
     * a1m3b1_lookup_and_invoke_a1m2b2      app          a1m2b1      ejb-inwar anno      war
     * @throws Exception
     */
    public void test_120_app_between_ejb_in_war_and_war() throws Exception{
        //java:app/env/service/a1m2b2
        scopedrefs.generatedclients.app1.mod3.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod3.b1.B1Service();

        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:app/env/service/a1m2b2");
        
        System.out.println("actual = "+ result);
        String expected = "a1m2b2";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
        
    }
    
    /**
     * a application scoped reference declared in a1m3b1 is used by a1m1b1 to invoke a1m2b1
     * @throws Exception
     */
    public void test_130_app_between_war_and_ejb() throws Exception{
        //java:app/env/service/a1m2b1
        scopedrefs.generatedclients.app1.mod1.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod1.b1.B1Service();

        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:app/env/service/a1m2b1");
        
        System.out.println("actual = "+ result);
        String expected = "a1m2b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));    
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     * a application scoped reference declared in a1m3b1 is used by a1m2b1 to invoke itself.
     * Since the ref is app scoped, it should work. 
     * @throws Exception
     */
    public void test_131_app_between_war_and_ejb() throws Exception{
        //java:app/env/service/a1m2b1
        scopedrefs.generatedclients.app1.mod2.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod2.b1.B1Service();

        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:app/env/service/a1m2b1");
        
        System.out.println("actual = "+ result);
        String expected = "a1m2b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));        
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     * a application scoped reference declared in a1m3b1 is used by a1m3b1 to invoke a1m2b1
     * Sincde the ref is app scoped, it should work. 
     * @throws Exception
     */
    public void test_132_app_between_war_and_ejb() throws Exception{
        //java:app/env/service/a1m2b1
        scopedrefs.generatedclients.app1.mod3.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod3.b1.B1Service();

        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:app/env/service/a1m2b1");
        
        System.out.println("actual = "+ result);
        String expected = "a1m2b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));  
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);     
    }
    
    /** 
     * Check at one application can invoke a service in another using a global reference
     * defined in a different application.
     * 
     * Application1 will attempt to invoke a service in application2 using a ref defined in app2.
     * The reference is defined in a2m3b2 by injection. 
     * 
     */
    
    public void test_200_glo_between_ejb_and_war() throws Exception{
        if (isZos()) return;        
        updateApp2PortNumberOnServerSide();
        scopedrefs.generatedclients.app1.mod2.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod2.b1.B1Service();

        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:global/env/service/a2m3b1_injection_from_a2m3b2");
        
        System.out.println("actual = "+ result);
        String expected = "a2m3b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));   
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     * Check that a global ref defined in app1 to a service in app2 can be used
     * by app1 to invoke the service in app2. 
     * 
     */
    public void test_210_glo_between_war_and_war() throws Exception{
        if (isZos()) return;     
        updateApp2PortNumberOnServerSide();
        scopedrefs.generatedclients.app1.mod3.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod3.b1.B1Service();
        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:global/env/service/a2m3b1_dd_from_a1m1b1");
        
        System.out.println("actual = "+ result);
        String expected = "a2m3b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected)); 
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     * a ref declared in app2 m3b1 (servlet in war) is used by an ejb in app1
     * @throws Exception
     */
    public void test_220_glo_between_war_and_ejb() throws Exception{
        if (isZos()) return;       
        updateApp2PortNumberOnServerSide();
        scopedrefs.generatedclients.app1.mod1.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod1.b1.B1Service();
        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:global/env/service/a2m1b1_injected_from_a2m3b1");
        
        System.out.println("actual = "+ result);
        String expected = "a2m1b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     * test global ref declared in one ejb and used in another. 
     * @throws Exception
     */
    public void test_230_glo_between_ejb_and_ejb() throws Exception{
        if (isZos()) return;        
        updateApp2PortNumberOnServerSide();
        String wsdlURL = hostAndPort +  "/scopedrefsapp2mod3/B2Service?wsdl";
        scopedrefs.generatedclients.app2.mod3.b2.B2Service b2 =
             new scopedrefs.generatedclients.app2.mod3.b2.B2Service(new URL(wsdlURL));
                
        String result = b2.getB2Port().lookupAndInvokeAnyJndiRef("java:global/env/service/a1m1b1_dd_from_a1m2b1");
        
        System.out.println("actual = "+ result);
        String expected = "a1m1b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));
        assertTrue("result longer than expected, may contain a warning or exception", result.length() <= 7);
    }
    
    /**
     * test a global ref between two modules in the same app.  It should work.
     * It's failing but is out of scope for  F743-34252 so commenting out for now. 
     * Def. 680842.
     * 
     * The reference is defined in app2, module1's ejb-jar.xml. 
     */
    public void test_240_glo_between_two_modules_same_app() throws Exception{        
        if (isZos()) return;
        updateApp2PortNumberOnServerSide();
        //  http://localhost:9080/scopedrefsapp2mod3/B1Service, B2..
        String wsdlURL = hostAndPort +  "/scopedrefsapp2mod1/B1Service?wsdl";
        scopedrefs.generatedclients.app2.mod1.b1.B1Service b1 =
             new scopedrefs.generatedclients.app2.mod1.b1.B1Service(new URL(wsdlURL)); 
        //scopedrefs.generatedclients.app2.mod1.b1.B1Service b1 = new scopedrefs.generatedclients.app2.mod1.b1.B1Service();
        String result = b1.getB1Port().lookupAndInvokeAnyJndiRef("java:global/env/service/a2m3b1_dd_from_a2m1b1");
        System.out.println("actual = "+ result);
        String expected = "a2m3b1";
        System.out.println("exected = "+ expected);
        assertTrue("did not get expected result", result.contains(expected));
        assertTrue("result longer than expected, may contain a warning or exception, see sysout.  680842 perhaps? ", result.length() <= 7);
    }
    
    /**
     * check that an app run in the managed client container, aka the appclient, aka the launchclient,
     * can see java:global references defined in other apps on the server.    
     * 
     * Note that the syntax used on the appclient needs to be different, it's similar to what shows
     * up in the server's dumpnamespace command except "(top)" becomes "cell".
     * Naming guy says this is as designed.
     * 
     * @throws Exception
     */
    // Liberty does not support appclient programming model. 
    /* public void test_300_glo_between_appclient_and_server() throws Exception{
        Machine localMachine = Machine.getLocalMachine(); 
        if (localMachine.getOperatingSystem()== OperatingSystem.ISERIES
             || localMachine.getOperatingSystem()== OperatingSystem.ZOS ){
            warning("test not yet supported on I or Z ");
            return;
        }        
        String cmd = "launchClient";
        if (localMachine.getOperatingSystem()== OperatingSystem.WINDOWS){
            cmd += ".bat";
        } else {
            cmd += ".sh";            
        }      
        String portInfo = " -CCBootstrapPort=" + bootStrapPort.trim() + " ";
        // if admin sec is on, we have to add some params
        String secparms = "";        
        if (cell.isSecurityEnabled()) {
            String user = cell.getWasUserName();
            String password =  cell.getWasPassword();
            secparms = " -CCDcom.ibm.CORBA.loginUserid=" + user + 
              "  -CCDcom.ibm.CORBA.loginPassword=" + password + 
              "   -CCDcom.ibm.CORBA.loginSource=none" ;
        }
        
        String app = AppConst.FVT_HOME + "/build/installableApps/scopedrefslaunchclientapp.ear";
        String jndiname = "cell/applications/env/service/a1m1b1_dd_from_a1m2b1";  // from test230, so test230 should be passing.
        String tracespec = "-CCtrace=com.ibm.ws.websvcs.*=all" ;
        tracespec = "";
        String result = runWasScript(cmd, app + "  " + portInfo + tracespec + " " + secparms + " " + jndiname);        
        String expected = "Appclient response: a1m1b1";
        System.out.println("expected = " + expected);
        System.out.println("actual = "+ result);        
        assertTrue("did not get expected result, see systemout", result.contains(expected));        
        
    }*/
    
    /**
     * call an endpoint impl in each app that will set a static field containing the 
     * port number for application 2 endpoints.  That field value will then be used 
     * by the app2 clients whenever they are called during a test.  
     * 
     * Since the wsdllocation is the location in the fvt tree, 
     * REPLACE_WITH_ACTUAL_URL URL isn't going to work and we have to do this. 
     *
     */
   private void updateApp2PortNumberOnServerSide() throws Exception {
       if (serverSideApp2PortNumberHasBeenUpdated ){ return; }
       System.out.println("updating port numbers of app2 clients contained within app1 on the server side.");
       scopedrefs.generatedclients.app1.mod1.b1.B1Service b1 = new scopedrefs.generatedclients.app1.mod1.b1.B1Service();
       b1.getB1Port().setStaticApp2HttpPort(port);
       System.out.println("updating port numbers of app2 clients contained within app2 on the server side.");
       String wsdlURL = "http://127.0.0.1:" + port + "/scopedrefsapp2mod1/B1Service?wsdl";
       scopedrefs.generatedclients.app2.mod1.b1.B1Service b21 = 
            new scopedrefs.generatedclients.app2.mod1.b1.B1Service(new URL(wsdlURL));
       b21.getB1Port().setStaticApp2HttpPort(port);
       serverSideApp2PortNumberHasBeenUpdated = true;
       System.out.println("server side update complete");
   }
    
    
    
    /**
     * run a websphere script on the local machine. 
     * @param scriptname - name of the script in the profile dir. Don't include the path. 
     * @param parms - all arguments to the script, specified in a single string. 
     * @return - whatever text output the script produces, or stacktrace if it doesn't run.  
     */
    private String runWasScript(String scriptname,  String parms){
        ProgramOutput output = null; 
        try{
            Machine localMachine = Machine.getLocalMachine();       
            Node mySP = TopologyDefaults.getDefaultAppServer().getCell().getManager().getNode();
            String profileBaseBincmd = mySP.getProfileDir() + "/bin/"+ scriptname;
            String workDir = AppConst.FVT_HOME + "/build/work/tmp";
            (new File(workDir)).mkdir();            
            System.out.println("running command: " + profileBaseBincmd + " " + parms);
            
            // break up the parms String into the array that the simplicity api requires.
            List<String> parmslist = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(parms);
            while(st.hasMoreTokens()){
                parmslist.add(st.nextToken());
            }      
            output = localMachine.execute(profileBaseBincmd, 
               parmslist.toArray(new String[parmslist.size()]), 
               workDir, new java.util.Properties());

        } catch (Exception e){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);            
            e.printStackTrace(ps);
            ps.close();
            return "Exception occured executing command: \n" + baos.toString();  // send back to caller 
        }
         
        String result = "Stdout: " + output.getStdout() + "\n"+ "Stderr: " + output.getStderr();
        return result;
    }
    
    // skip if on z.
    // see buildTest.xml for more info.
    private boolean isZos(){
        if(System.getProperty("os.name").toLowerCase().contains("z/os")){
            String msg = "This test not supported on z/os because burned-in proxy client url's are required.";
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
    public ScopedRefs1ServerTest(String name) {
        super(name);
    }
    
    /** 
     * This method controls which test methods to run.
     * @return A suite of tests to run
     */
    public static Test suite() {
        boolean runall = true;
        if(runall){
            return new TestSuite(ScopedRefs1ServerTest.class);
        } else {
                // for debug use.
                System.out.println("WARNING: some tests are disabled");
                TestSuite ts = new TestSuite();           
                //ts.addTest(new ScopedRefs1ServerTest("test_230_glo_between_ejb_and_ejb"));
                ts.addTest(new ScopedRefs1ServerTest("test_300_glo_between_appclient_and_server"));            
                return ts;           
        }
    } 
    // perform setup for the testcase - vanilla junit method
    protected void setUp() throws java.lang.Exception {
    }
    
    // make sure everything is running at the end of each test
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
        port = TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost).toString();  
//        bootStrapPort= TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.BOOTSTRAP_ADDRESS).toString(); 
        
        hostAndPort  = "http://" + host + ":" + port ;       
        System.out.println("hostAndPort = "+hostAndPort );
        cell = TopologyDefaults.defaultAppServerCell;
        
    }
    
    //  our nonportable test teardown method
    protected void suiteTeardown() throws java.lang.Exception {
    	super.suiteTeardown();
        System.out.println("suiteTeardown() called");
    }
    

}    