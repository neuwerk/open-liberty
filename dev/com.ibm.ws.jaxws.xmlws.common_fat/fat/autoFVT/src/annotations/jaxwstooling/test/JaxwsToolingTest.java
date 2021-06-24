/*
 *  @(#) 1.22 autoFVT/src/annotations/jaxwstooling/test/JaxwsToolingTest.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/29/11 14:29:58 [8/8/12 06:55:59]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Defect    Description
 * -----------------------------------------------------------------------------
 * 09/28/06    euzunca               new file
 * 05/11/07    btiffany              change signature we're looking for on some of the negative
 *                         calls so we can pick it up on AS400.  There, since output
 *                         is a mix of ascii and ebcdic, we have to look for output
 *                         from the java itself, not the shell script that invokes it. 
 * 0524/2007   jramos      440922    Integrate ACUTE
 * 08/14/2007  btiffany              change signature again, as java6 has a slightly 
 * 						   different exception signature, it seems to put the
 * 						   an instance number in the exception, for example:
 * 							at com.sun.tools.ws.wscompile.CompileTool46run(CompileTool.java:538)
 * 
 * 						   Also clarified the messages a bit.
 * 10/23/2007  jramos      476750 Use ACUTE 2.0 api and TopologyDefaults
 * 03/03/2008  btiffany    496682 fix test because wsgen now produces different message.
 * 08/06/2008  btiffany    541401 disable on z, tooling scripts not supported there.
 * 01/07/2010  btiffany    633901 skip test_PK92522_WASWSDLGenerator on z too. 
 * 03/14/2010  lizet               JAXWS 2.2 Upgrade 
 * 03/28/11    jtnguyen    690045 wrong java location on iSeries 
 * 09/29/2011  wconti      PM37886.fvt Test Axis2Utils.getLibDirPath() returns correct cp for wsgen
 *
 */
package annotations.jaxwstooling.test;

import java.io.File;

import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.OS;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.RemoteFileFilter;
import com.ibm.websphere.simplicity.Server;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ConfigRequirement;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.websphere.simplicity.OperatingSystem;
import common.utils.topology.IMachine;
import common.utils.topology.MachineFactory;
import common.utils.topology.TopologyActions;


/**
 * notes 1.18.07 harness shows test_wsimport_invalid_path failing on linux
 * f0702.04, this is def 410423
 * 
 * 4.9.07 removed wsdl uri test on unix, invalid, ref def 410433.
 * 
 * 
 */
public class JaxwsToolingTest extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    static OS os = new OS();
    private static String buildDir = null;
    private static String baseDir = null;
    private static String testDir = null;
    private static String sep = null;
    private static String scriptExtension = ".sh";
    private static IMachine localMachine = null;
    static {
        sep = File.separator;
        baseDir = AppConst.FVT_HOME + sep + "src";
        buildDir = AppConst.FVT_HOME + sep + "build";
        testDir = "annotations" + sep + "jaxwstooling";

        try {
            localMachine = MachineFactory.getMachine(
                                                     TopologyDefaults.defaultAppServerCell.getTopologyFile(),
                                                     TopologyActions.FVT_HOSTNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if ( localMachine.getOperatingSystem() == OperatingSystem.WINDOWS ) {
                scriptExtension = ".bat";
                buildDir = buildDir.replace('/', '\\');
                baseDir = baseDir.replace('/', '\\');

            }
        } catch (Exception e) {
            e.printStackTrace();
            // we'll default to Windows in this case
            System.out.println("Error getting local machine. Use default values.");
        }
    }

    private static Node node;
    private static Machine machine;
    private static Server server = null;
    private static String hostname = null;
    private static Integer port = 0;
    private static String hostandport =  null;
    
    /*
     * String arg constructor: This constructor allows for altering the test
     * suite to include just one test.
     */
    public JaxwsToolingTest(String name) {
        super(name);
    }

    /*
     * This test method will verify that the command line invocation of wsgen
     * tool executes correctly
     * 
     * @testStrategy This test invoked the wsgen script with all the possible
     * command line options and a dummy SEI name. The call should fail due to
     * 'class not found'.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test invoked the wsgen script with all the  possible command line options and a dummy SEI name. The call should fail due to 'class not found'.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _test_wsgen_full_negative() throws Exception {
        String cmd = buildDir + sep + "work" + sep + testDir + sep + "bin" + sep;
        cmd = cmd + "wsgencall_full_neg" + scriptExtension;
        String output = OS.runs(cmd);
        int rc = OS.getLastRC();
        System.out.println(output + ": " + rc);
        assertTrue(cmd + ": " + output, output.contains("error:"));
    }

    private boolean isZ() {
        if ( localMachine.getOperatingSystem() == OperatingSystem.ZOS ) {
            System.out.println("jaxws tooling is not supported on Z/OS");
            return true;
        }
        return false;
    }

    /*
     * This test method will verify that the command line invocation of wsgen
     * tool executes correctly
     * 
     * @testStrategy This test invoked the wsgen script with all the possible
     * command line options and a valid SEI name. The call should execute
     * correctly and generate the jax-ws artifacts.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test invoked the wsgen script with all the  possible command line options and a valid SEI name. The call should execute correctly and generate the jax-ws artifacts.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _test_wsgen_full_positive() throws Exception {
        String cmd = buildDir + sep + "work" + sep + testDir + sep + "bin" + sep;
        cmd = cmd + "wsgencall_full_neg" + scriptExtension;

        String output = OS.runs(cmd);
        int rc = OS.getLastRC();
        System.out.println(output + ": " + rc);
        assertTrue(cmd + ": " + output, output.contains("call succeeded..!"));
    }

    /*
     * @testStrategy This test method will check 1.1, 1.2 and dummy soap
     * protocol entries... check also 1.2 without extension
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "This test method will check 1.1, 1.2 and dummy soap protocol entries... check also 1.2 without extension  ", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_wsgen_soap() throws Exception {
        if ( isZ() ) {
            return;
        }
        String cmd_begin = buildDir + sep + "work" + sep + testDir + sep + "bin" + sep;
        String cmd1, cmd2, cmd3;
        boolean _11, _12, _inv;
        cmd1 = cmd_begin + "wsgencall_soap11" + scriptExtension;
        cmd1 = buildExecutedCmd(cmd1);
        
        cmd2 = cmd_begin + "wsgencall_soap12" + scriptExtension;
        cmd2 = buildExecutedCmd(cmd2);
        
        cmd3 = cmd_begin + "wsgencall_soapInvalid" + scriptExtension;
        cmd3 = buildExecutedCmd(cmd3);

        // look for a processing message that strongly suggests success.
        // due to as400 we can't use retcode or script output.
        String searchString = "annotations\\jaxwstooling\\server\\jaxws\\AddTwoNumbers.java";
        System.out.println("looking for " + searchString + " in output");
        System.out.println("cmd1: "+cmd1);
        
        String output11 = OS.runs(cmd1);
        int rc = OS.getLastRC();
        _11 = output11.contains(searchString);
        System.out.println(output11);
        System.out.println("search result: " + _11);

        System.out.println("looking for " + searchString + " in output");
        String output12 = OS.runs(cmd2);
        rc = OS.getLastRC();
        _12 = output12.contains(searchString);
        System.out.println(output12);
        System.out.println("search result: " + _12);

        // look for the stacktrace fragment indicating a failure
        searchString = "is not a supported protocol";
        String outputInv = OS.runs(cmd3);
        rc = OS.getLastRC();
        _inv = outputInv.contains(searchString);
        System.out.println(outputInv);
        System.out.println("search result: " + _inv);

        assertTrue("Expected wsgen output was not seen for one or more tests. \n"
                   + "Soap11  option: search result expected = true, actual = " + _11 + " \n"
                   + "Soap12: option  search result expected = true, actual = " + _12 + " \n"
                   + "invalid option: search result expected = true, actual = " + _inv + " \n",
                   _11 && _12 && _inv);
    }

    /*
     * @testStrategy -servicename and -portname may only be used together with
     * -wsdl option. This test case checks this constraint.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "-servicename and -portname may only be used together with -wsdl option. This test case checks this constraint.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _test_wsgen_wsdloption() throws Exception {
        if ( isZ() ) {
            return;
        }
        String cmd = buildDir + sep + "work" + sep + testDir + sep + "bin" + sep;

        cmd = cmd + "wsgencall_wsdlOption" + scriptExtension;

        String output = OS.runs(cmd);
        int rc = OS.getLastRC();
        System.out.println(output + ": " + rc);
        assertTrue(cmd + ": " + output, output.contains("error:"));
    }

    /*
     * @testStrategy - check invalid path for -wsdlLocation, -d, -s, -b testing
     * for notification in sysout, not a nonzero return code.
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "- check invalid path for -wsdlLocation, -d, -s, -b testing for notification in sysout, not a nonzero return code.", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void test_wsimport_invalidPaths() throws Exception {
        if ( isZ() ) {
            return;
        }
        String cmd_begin = buildDir + sep + "work" + sep + testDir + sep + "bin" + sep;
        String cmd1, cmd2, cmd3, cmd4;

        /* these are the names of scripts that make assorted calls to wsimport */
        cmd1 = cmd_begin + "wsimportcall_path_d" + scriptExtension;
        cmd1 = buildExecutedCmd(cmd1);
        
        cmd2 = cmd_begin + "wsimportcall_path_s" + scriptExtension;
        cmd2 = buildExecutedCmd(cmd2);
        // cmd3 = cmd_begin + "wsimportcall_uri_wsdl" + scriptExtension;
        cmd4 = cmd_begin + "wsimportcall_binding" + scriptExtension;
        cmd4 = buildExecutedCmd(cmd4);

        String output1 = OS.runs(cmd1);
        int rc = OS.getLastRC();
        // boolean path_d = output1.contains("error: directory not found");
        String expected = "Usage: wsimport";
        boolean path_d = output1.contains(expected);
        System.out.println("expected was:" + expected);
        System.out.println("output was: " + output1 + ": " + rc);
        if ( !path_d ) {
            System.out.println("***** expected string not found ***** ");
        }

        String output2 = OS.runs(cmd2);
        rc = OS.getLastRC();
        // boolean path_s = output2.contains("error: directory not found");
        boolean path_s = output2.contains(expected);
        System.out.println("expected was:" + expected);
        System.out.println("output was: " + output2 + ": " + rc);
        if ( !path_s ) {
            System.out.println("***** expected string not found ***** ");
        }

        // ref 410433, this test is invalid on unix
        // ref 614758, this test is invalid completely.
        /*
         * String output3 = "n/a"; boolean wsdl_uri = true; if
         * (localMachine.getOperatingSystem() == OperatingSystem.WINDOWS){
         * output3 = OS.runs(cmd3); // although it fails, invalid URI case does
         * not have return code of 1. // wsimport still fails throwing a
         * MalformedURLException... rc = OS.getLastRC(); // expected =
         * "MalformedURLException"; expected = "unknown protocol"; wsdl_uri =
         * output3.contains(expected);
         * System.out.println("expected was:"+expected);
         * System.out.println("output was: "+output3 + ": " + rc); if
         * (!wsdl_uri){
         * System.out.println("***** expected string not found ***** "); } }
         * else {System.out.println(
         * "wsimportcall_uri_wsdl.sh test is not valid on unix, skipped."); }
         */
        String output4 = OS.runs(cmd4);
        rc = OS.getLastRC();

        boolean binding = output4.contains("FileNotFoundException")
                          | output4.contains("directory not found")
                          | output4.contains("No such file or directory")
                          | output4.contains("No such path or directory");
        System.out.println("expected was \"No such...\" or \"directory not found\"");
        System.out.println("output was: " + output4 + ": " + rc);

        if ( !binding ) {
            System.out.println("***** expected string not found ***** ");
        }

        assertTrue("One or more subtests failed: \n" + " path_d:   " + path_d + "\n path_s: "
                   + path_s + "\n binding: " + binding, path_d && path_s && binding);

    }

    /*
     * This test method will verify that
     * 
     * @testStrategy invalid package name by '-p' option
     */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description = "invalid package name by '-p' option", expectedResult = "", since = com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
    public void _test_wsimport_invalidPackage() throws Exception {
        String cmd = buildDir + sep + "work" + sep + testDir + sep + "bin" + sep;

        cmd = cmd + "wsimportcall_package" + scriptExtension;

        String output = OS.runs(cmd);
        int rc = OS.getLastRC();
        System.out.println(output + ": " + rc);
        assertTrue(
                   cmd + ": " + output,
                   output.contains("error: The package name '22222.33333.44444' used for this schema is not a valid package name."));
    }

    // Exclusion reason:
    // Comment this case because Liberty tools jar does not support "com.ibm.ws.websvcs.wsdl.WASWSDLGenerator" defined in tWAS thin client.
    /*
     * <!-- PK92522 Starts--> This test method will verify that
     * 
     * @testStrategy Exception thrown by wsgen tool while generating classes for
     * a JAX-WS web services in the presence of a invalid/corrupted java file.
     */
     /*
    public void test_PK92522_WASWSDLGenerator() throws Exception {
	    if ( isZ() ) {
            return;
        }


        String workDir = buildDir + sep + "work" + sep + testDir + sep + "waswsdlgenerator";
        JaxwsToolingHelper.modify(workDir + sep);
        String javaDir = null;       // location of java 
        String toolsjarDir =  null;  // location of tools.jar */
        /*
         * As per the email I sent, here is some sample code to help you write
         * the test for this. We will use the new FVT frameworks code
         * (Simplicity) to get the machine we'd like to execute the command on
         * instead of the old (Acute) machine classes used above.
         */

        /*
        // 690045 - find java location and tools.jar location
        // on iSeries, JAVA_HOME=/QOpenSys/QIBM/ProdData/JavaVM/<jdk version>/32bit|64bit/jre
        // and tools.jar is at   /QOpenSys/QIBM/ProdData/JavaVM/<jdk version>/32bit|64bit/lib
        
        // Liberty does not include JDK in the installation      
        //if (localMachine.getOperatingSystem() == OperatingSystem.ISERIES) {
            javaDir = System.getProperty("java.home");   
            toolsjarDir = javaDir + "/../lib" ;
        //} else {                     
        //    javaDir = node.getWASInstall().getInstallRoot() + "/java";           
        //    toolsjarDir = javaDir + "/lib";
        //}
                              
        String cmd = javaDir + "/bin/java";
        
        // debug
        System.out.println("javaDir=" + javaDir);
        System.out.println("toolsjarDir=" + toolsjarDir);
       
        
        // Gets the path separator ; for windows and : for Linux/Unix
        String pthsep = machine.getOperatingSystem().getPathSeparator(); */
        // So we don't have to worry about if the thinclient is *_x.0.0.jar
        // Liberty does not have thinclient jar, so we use the one copied from tWAS.
        /*RemoteFileFilter filter = new RemoteFileFilter() {

            @Override
            public boolean accept(RemoteFile file) {
                try {
                    return file.getName().contains("com.ibm.jaxws.thinclient");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
        RemoteFile[] runtimes = machine.getFile(node.getWASInstall().getInstallRoot() + "/runtimes").list(
                                                                                                          filter,
                                                                                                          false);
        String thinclientJar = runtimes[0].getName();*/
        /*String thinclientJar = "com.ibm.jaxws.thinclient_8.5.0.jar";
        System.out.println("thinclientJar=" + thinclientJar);*/

        /*
         * The command parameters go here after "java"
         * this is where you'll put the -classpath
         * <classpath>, -DtraceSettingsFile=TraceSettings.properties,
         * WASWSDLGenerator classname and the classpath SEI classname
         */
         
        
        
        /*String[] parameters = new String[] {
                                            "-cp",
                                            AppConst.FVT_HOME + sep + "common" + sep + "jars" + sep + "twas" + sep
                                                    + thinclientJar + pthsep + AppConst.WAS_HOME + sep 
                                                    + "lib" + sep + "com.ibm.ws.jaxws.tools.2.2.1_1.0.jar" + pthsep 
                                                    + toolsjarDir + sep + "tools.jar"
                                                    + pthsep + ".",
                                                    
                                                    //+ AppConst.WAS_HOME
                                                    //+ sep + "java" + sep + "lib" + sep
                                                    //+ "tools.jar" + pthsep + ".",                                                    
                                            
                                            "com.ibm.ws.websvcs.wsdl.WASWSDLGenerator", workDir,
                                            "annotations.jaxwstooling.server.JaxwsTooling" };
        // use machine.execute w/ the command parameters and workdir
        ProgramOutput out = machine.execute(cmd, parameters, workDir);
        // I like to print out some messages so I can see if something happens
        // when the tests are run later on.
        System.out.println("cmd: " + out.getCommand());
        System.out.print("Params: ");                      
        for ( String p : parameters ) {
            System.out.print(p + " ");
        }
        System.out.println("out: " + out.getStdout());
        if ( out.getStderr() != null || !out.getStderr().equals("") )
            System.out.println("out err: " + out.getStderr());*/

        /*
         * Here you will need to do an assert and look through the file the
         * TraceSettings.properties file created. I'd look for a small string
         * that you can check exists or not, like the part where it says it
         * found a bad java file and is restarting.
         */
        /*System.out.println("Checking the trace...");
        assertTrue(
                   "test failed. " + cmd + ":" + out,
                   JaxwsToolingHelper.searchTraceLog("One or more invalid java files are detected.  These will be renamed and wsgen will be run a second time."));
    }*/

    /*
     * <!-- PM37886.fvt -->
     * This test method verifies that WSDL generation was successful during 
     * application deployment. This test involves the JaxwsToolingApp.ear 
     * being installed, and as part of that process, WASWSDLGenerator should 
     * have generated a WSDL for the SEI. The SEI, annotations.jaxwstooling.server.JaxwsTooling,
     * references annotations.jaxwstooling.server.JaxwsToolingException, which 
     * is packaged in the EAR's utility JAR. 
     * 
     */
    public void test_PM37886_WSDLGeneration() throws Exception {
    	String qmark_wsdl_URL = hostandport + "/" + "JaxwsToolingWeb/JaxwsToolingService?wsdl";
    	// If WSDL generation was successful during deployment, the ?wsdl request should 
    	// return a 200 status, so Operations.getUrlStatus() should return true
    	boolean statusOkay = Operations.getUrlStatus(qmark_wsdl_URL, 60);
    	assertTrue("HTTP status code should be a 200", statusOkay);
    	
    }
    
    public static junit.framework.Test suite() {
        System.out.println(JaxwsToolingTest.class.getName());
        return new TestSuite(JaxwsToolingTest.class);
    }

    @Override
    protected void suiteSetup(ConfigRequirement testSkipCondition) throws Exception {
        super.suiteSetup(testSkipCondition);
        node = TopologyDefaults.getDefaultAppServer().getNode();
        machine = node.getMachine();
        server = TopologyDefaults.getDefaultAppServer();
        hostname = server.getNode().getHostname();
        port = server.getPortNumber(PortType.WC_defaulthost);
        hostandport = "http://" + hostname + ":" + port;
    }
    
    private String buildExecutedCmd(String cmd) {
        String result = cmd;
        if (".sh".equals(scriptExtension)) {
            result = "chmod a+x "+cmd+" && "+cmd;
        }
        
        return result;
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

}
