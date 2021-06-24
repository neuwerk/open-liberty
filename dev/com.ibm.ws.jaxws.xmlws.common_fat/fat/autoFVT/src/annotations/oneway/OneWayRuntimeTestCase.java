/*
 * @(#) 1.30 autoFVT/src/annotations/oneway/OneWayRuntimeTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 3/6/12 15:21:31 [8/8/12 06:54:34]
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
 * 05/30/2006  btiffany    LIDB3296.31.01     new file
 * 06/09/2006   "                             add setup method
 * 05/24/2007  jramos      440922             Integrate ACUTE
 * 08/08/2007    "                            add new constructor for framework
 * 03/06/2012  jtnguyen    729894             removed 2 tests from Z run due to 711990/WRTC51305
 *
 */
package annotations.oneway;

import java.io.File;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import annotations.support.ImplementationAdapter;
import annotations.support.LogfileChecker;
import annotations.support.Support;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;
import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

/**
 * @testStrategy checks @oneway behavior at runtime.
 * so far, just checks that a oneway method can be generated and work.
 * invokes a one-way method and looks for signature in server log file.
 * @author btiffany
 *
 * notes:
 * 12.9 - still having probs with schema resolution 390939.1,
 *        oneway running synchronous - 392654
 *        partial wsdl fail 410861
 *        408808 is functional but still tossing exceptions in the syslog - reopened
 *
 * 12.13 F0650.10
 *         still running synchronous. - reopened 392654
 *         looks like 390939.1 is fixed- yea! - but def is still in oepn state.
 *         looks like 410861 is passed.
 *         looks like 408808 passed
 *
 * 01.15.07 f0651.08 - still running synchronous, but I could have sworn we had one that passed.
 *          f0701.22 - still running synchronous, per harness report.  reopening defect.
 *                     harness shows ran sync on g0700.12
 *
 * 01.17.07 g0702.02 blocked by 414477
 * 01.19.07 applied NT's patch but still seeing the unmarhsal problem here. Since it's a
 * oneway, it won't return to the client, will only appear in the trace log.
 *
 * Rebuilt and redeployed and now we get nothing in any log, just no invoke.
 *
 * 01.22.07  h0702.29 - still getting demarshal probs in server trace.log, no invoke.
 *           opeend 416191
 * 01.23.07 n0703.10 - still have it.
 *
 * 02.05.07 n0704.24 - marshalling good, but still synchronous, reopened 392654
 *
 * 03.05.07 changed test so we don't have to read server log.
 *
 * 05.01.07 passed!  v0712.02
 *
 * 05.04.07 trying bad url, etc. to get a false success. got one, added testHttp500 case.
 * Opened defect.
 *
 */

public class OneWayRuntimeTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

        private static String workDir =null;
        private static boolean setupRun = false;
    private static String hostAndPort = null;
    private static IAppServer server = null;
    private static ImplementationAdapter imp = null;

        // we need an absolute path to this directory for things to be reliable.
        static{
                workDir = Support.getFvtBaseDir()+"/build/work/OneWayRunTimetestCase";
				imp = Support.getImplementationAdapter("ibm", workDir);
        server = QueryDefaultNode.defaultAppServer;
        hostAndPort = "http://" + server.getMachine().getHostname() + ":"
        + server.getPortMap().get(Ports.WC_defaulthost);
        }


        // framework reqmt
        public OneWayRuntimeTestCase(String s){
            super(s);
        }

         /**
         * main method for running/debugging outside of harness.
     * @param args Command line arguments
     */
        public static void main(String[] args) throws Exception {
                if (false){
                        TestRunner.run(suite());
                } else {
            IAppServer debugs = QueryDefaultNode.defaultAppServer;
            debugs.getPortMap().get(Ports.WC_defaulthost);
            OneWayRuntimeTestCase t = new OneWayRuntimeTestCase("x");
            String [] s = {"Tue_Jan_09_07:27:16_CST_2007_delay OneWayDefaults ending"};
                        //System.out.println(server.searchSystemOutLog(s));


            t.testService();
            t.testHttp500();
            /*
            t.testTrueOnewayCachedWsdl();
            t.testTrueOnewayBetaDynamicWsdl();
             t.testTrueOnewayBeta();
            t.testTrueOnewayDynamicWsdl();
            */

                }
        }


        /**
     * The suite method returns the tests to run from this suite.
     * @return A Test object containing tests to be run
     */
    public static junit.framework.Test suite() {
            return new TestSuite(OneWayRuntimeTestCase.class);
    }


        /**
         * @testStrategy - invoke basic one way service and see if we can find
     * it's signature in the server logfile
     *
     * uses onewayanno.ear, containing annotations.oneway.testdata.OneWayDefaults
     * at /onewayanno/onewayanno
         */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- invoke basic one way service and see if we can find it's signature in the server logfile  uses onewayanno.ear, containing annotations.oneway.testdata.OneWayDefaults at /onewayanno/onewayanno",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
        public void testService(){
                System.out.println("*************** starting testService()**************");
                // invoke a method in the client code, and see what happens

                imp.setWorkingDir(imp.getWorkingDir()+"/client");
        //String url = imp.getServerURL()+"/onewayanno/OneWayDefaultsService?wsdl");
        String url = hostAndPort + "/onewayanno/onewayanno?wsdl";

        String input = new java.util.Date().toString()+ " hello there server";
        input = input.replace(' ','_');
       annotations.oneway.testdata.OneWayRuntimeTestCaseClient.runtest(url, input, 1);

       String status = annotations.oneway.testdata.OneWayRuntimeTestCaseClient.getOneWayStatus();
       //System.out.println("service method status is: "+ status);
       //assertTrue("service failed to run", status.contains("done")  );
       System.out.println("******* test passed **********");
        }

   /**
    * @testStrategy invoke service with @Webmethod missing.  Should either
    * fail or succeed cleanly, but not explode in some undiagnosable mess.
    * Based on what we know now, it should work.
    *
    * changed 11.30.06 to add a second method with @WebM anno, then see
    * if we can invoke the first.  Client shouldn't even be able to
    * construct the service.
    *
    * 12.09.06 - removing for now, as service is not deployed in a way it could ever be invoked.
    *
    */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void _testOneWayWithoutWebMethod(){
        System.out.println("*************** starting testOneWayWithoutWebMethod()**************");
        imp.setWorkingDir(imp.getWorkingDir()+"/client");
        String url = hostAndPort + "/onewayannoillegal/onewayannoillegal?wsdl";

        // since this is negative test, make sure service is up.
        assertTrue("service did not come up", Operations.getUrlStatus(url, 30));

        String input = new java.util.Date().toString()+ " hello there server";
        input = input.replace(' ','_');

        int rc = imp.invoke("annotations.oneway.testdata.OneWayRuntimeTestCaseClient", url+ " "+input);
        assertTrue("wrong return code from client, expecting 12 ", rc==12);

        /*
        // look at the log file on the server and make sure the method ran.
        String [] search1 = { input };
        System.out.println("searching SystemOut.log for "+search1[0]);
        boolean hit = server.searchSystemOutLog(search1);
        assertTrue("service failed to run", hit );
        */
        System.out.println("******* test passed **********");
   }

   /**
    * @testStrategy invoke the server, request a 1 minute delay, then call getOneWayStatus
    * to verify that we got control back from the oneway method before it ended.
    *
    */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="invoke the server, request a 1 minute delay, then call getOneWayStatus to verify that we got control back from the oneway method before it ended. ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testTrueOnewayDynamicWsdl(){
       if (System.getProperty("os.name").toString().contains("z/OS")) {
           System.out.println("Skipped test on Z/OS due to 711990/WRTC51305");
	   } else {	   
	
		  System.out.println("*************** starting testTrueOnewayDynamicWsdl ************");

		   imp.setWorkingDir(imp.getWorkingDir()+"/client");

		   //String hostAndPort= com.ibm.ws.wsfvt.build.tools.AntProperties.getServerURL();
		   String url= hostAndPort + "/onewayanno/onewayanno?wsdl ";

		   // When we pass the word "delay" into the service, it should sleep for 60 sec.
		   String input = new java.util.Date().toString()+ " delay";
		   input = input.replace(' ','_');
		   long time = System.currentTimeMillis()/1000;
		   System.out.println("invoking client");
		   annotations.oneway.testdata.OneWayRuntimeTestCaseClient.runtest(url, input, 1);
		   time = System.currentTimeMillis()/1000 - time;
		   System.out.println("response time was: "+ Long.toString(time) + " seconds");

		   // now go see if the doSomething method is still running - it should be.
		   // String status = annotations.oneway.testdata.OneWayRuntimeTestCaseClient.getOneWayStatus();
		   // System.out.println("service method status is: "+ status);
		   // assertTrue("service failed to run, or ran as two -way ", status.contains("running")  );
		   assertTrue("service ran as two way", time < 30.0 );

		   System.out.println("invoking a second time");

		   // now try a second invoke to see if we've really done it right.
			input = new java.util.Date().toString()+ " delay";
		   input = input.replace(' ','_');
		   time = System.currentTimeMillis()/1000;
		   System.out.println("invoking client");
		   annotations.oneway.testdata.OneWayRuntimeTestCaseClient.runtest(url, input,1);
		   time = System.currentTimeMillis()/1000 - time;
		   System.out.println("response time was: "+ Long.toString(time) + " seconds");

		   // now go see if the doSomething method is still running - it should be.
		   // String status = annotations.oneway.testdata.OneWayRuntimeTestCaseClient.getOneWayStatus();
		   // System.out.println("service method status is: "+ status);
		   // assertTrue("service failed to run, or ran as two -way ", status.contains("running")  );
		   assertTrue("service ran as two way", time < 30.0 );

		   System.out.println("******* test passed **********");
	   }
   }

   /**
    * @TestStrategy same as testTrueOneway except we work around lack of dynamic wsdl
    *
    */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testTrueOnewayCachedWsdl() throws Exception{
   
       if (System.getProperty("os.name").toString().contains("z/OS")) {
           System.out.println("Skipped test on Z/OS due to 711990/WRTC51305");
	   } else {	   

		   System.out.println("*************** starting testTrueOnewayCachedWsdl()**************");
		   imp.setWorkingDir(workDir);
		   //String hostAndPort= com.ibm.ws.wsfvt.build.tools.AntProperties.getServerURL();
		   String relativeUrl = "/onewayanno/onewayanno";

		   // build up a local wsdl file we can use
		   String wsdlFile = imp.getWorkingDir()+"/OneWayDefaultsService.wsdl";
		   if (wsdlFile.contains(":")) {
			   wsdlFile = wsdlFile.substring(2);  // strip drive letter
		   }
		   String wsdl = (Operations.readTextFile(new File(wsdlFile)));
		   String newWsdlFile = wsdlFile + "static";
		   wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+ relativeUrl );
		   Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );

		   String input = new java.util.Date().toString()+ " delay";
		   input = input.replace(' ','_');

		   // invoke
		   // When we pass the word "delay" into the service, it should sleep for 60 sec.
		   long time = System.currentTimeMillis()/1000;
		   System.out.println("invoking client");
		   String url = "file:"+ newWsdlFile;
		   annotations.oneway.testdata.OneWayRuntimeTestCaseClient.runtest(url, input, 1);
		   time = System.currentTimeMillis()/1000 - time;
		   System.out.println("response time was: "+ Long.toString(time) + " seconds");

		   // now go see if the doSomething method is still running - it should be.
		   String status = annotations.oneway.testdata.OneWayRuntimeTestCaseClient.getOneWayStatus();
		   System.out.println("service method status is: "+ status);
		   assertTrue("service failed to run, or ran as two -way ", time <30.0   );
		   System.out.println("******* test passed **********");
	  }
   }

   /**
    * @TestStrategy supply a wsdl to a url that cannot process a post request.
    * Per spec (224 10.4.1.2) client should not
    * return until it gets the response, so this will test that:
    * a) we wait for the response and, bonus,
    * b) we can tell a good response from a bad one.
    *
    */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testHttp500() throws Exception{
       System.out.println("*************** starting testHttp500()**************");
       imp.setWorkingDir(workDir);

       // build up a local wsdl file we can use
       String wsdlFile = imp.getWorkingDir()+"/OneWayDefaultsService.wsdl";
       if (wsdlFile.contains(":")) {
           wsdlFile = wsdlFile.substring(2);  // strip drive letter
       }
       String wsdl = (Operations.readTextFile(new File(wsdlFile)));
       String newWsdlFile = wsdlFile + "staticbadurl";
       // send the post to the wsdl url, which can't handle a post.
       wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", hostAndPort+"/onewayanno/onewayanno/onewaydefaultsservice.wsdl" );
       //wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080"+"/onewayanno/onewayanno/onewaydefaultsservice.wsdl" );

       //wsdl = wsdl.replace("REPLACE_WITH_ACTUAL_URL", "http://localhost:19080/simple/ss" );
       Operations.writeFile(newWsdlFile, wsdl, AppConst.DEFAULT_ENCODING );

       String input = new java.util.Date().toString()+ " failure_expected_here_delay";
       input = input.replace(' ','_');

       // invoke, client returns 12 on webservice exception, which is what we are expecting.
       String url = "file:"+ newWsdlFile;
       boolean caughtExc = false;
       int rc = annotations.oneway.testdata.OneWayRuntimeTestCaseClient.runtest(url, input, 1);
       assertTrue("client invocation should not have succeeded (def 436989?)", rc==12 );
       System.out.println("******* test passed **********");
   }

   /**
    * @testStrategy - modify the server for this test so we can test within Beta limitations.
    * uses @Oneway on interface and other workarounds as needed to get the server going.
    *
    * The service for this method is created by buildTest.xml, not by setup methods like
    * everything else!
    */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testTrueOnewayBeta(){
       System.out.println("*************** starting testTrueOnewayBeta()**************");
        String url = hostAndPort+"/onewayannobeta/onewayannobeta?wsdl";
        // see if our service  deployed
        boolean result = Operations.getUrlStatus(url, 30);
        assertTrue("service did not deploy: "+url, result);

       // setting workdir puts it in the classpath for invoke.
       imp.setWorkingDir(Support.getFvtBaseDir()+"/build/work/onewayannobeta");

       System.out.println(new java.util.Date().toString()+" invoking service without delay");
       annotations.oneway.testdata.OneWayRuntimeTestCaseClient.runtest(url, "are_you_there?", 1);
       System.out.println(new java.util.Date().toString()+" service returned");

       System.out.println(new java.util.Date().toString() + " invoking service with delay");


       String input = new java.util.Date().toString()+ " delay";
       input = input.replace(' ','_');
       long time = System.currentTimeMillis()/1000;
       System.out.println("invoking client");
       annotations.oneway.testdata.OneWayRuntimeTestCaseClient.runtest(url, input, 1);
       System.out.println(new java.util.Date().toString()+ " service returned");
       time = System.currentTimeMillis()/1000 - time;
       System.out.println("response time was: "+ Long.toString(time) + " seconds");

       // now go see if the doSomething method is still running - it should be.
       String status = annotations.oneway.testdata.OneWayRuntimeTestCaseClient.getOneWayStatus();
       System.out.println("service method status is: "+ status);
       assertTrue("service failed to run, or ran as two -way ", time < 30.0 );
       System.out.println("********* test passed *********");
   }

   /**
    * @testStrategy - test that we can gen wsdl for oneway with an sei.
    *
    * Uses onewayannobeta2.ear at /onewayannobeta2/onewayannobeta
    *
    */
    @com.ibm.ws.wsfvt.test.framework.FvtTest(description="- test that we can gen wsdl for oneway with an sei.  Uses onewayannobeta2.ear at /onewayannobeta2/onewayannobeta ",
    expectedResult="",
    since=com.ibm.ws.wsfvt.test.framework.FvtTest.Releases.WSFP)
   public void testTrueOnewayBetaDynamicWsdl(){
       System.out.println("*************** starting testTrueOnewayBetaDynamicWsdl()**************");
        // get a checker so we can look at the log.
       LogfileChecker l = new LogfileChecker(imp.getConsoleOutLogfileName());
       // setting workdir puts it in the classpath for invoke.
       imp.setWorkingDir(Support.getFvtBaseDir()+"/build/work/onewayannobeta");

       System.out.println("invoking service with delay");

       // now build a unique timestamped input argument so we can scan the logfile for it
       // and not risk duplicate hits.
       String input = new java.util.Date().toString()+ " delay";
       input = input.replace(' ','_');
       long time = System.currentTimeMillis()/1000;
       // Liberty does not support /?wsdl style
       // String url= hostAndPort+"/onewayannobeta2/onewayannobeta/?wsdl";
       String url= hostAndPort+"/onewayannobeta2/onewayannobeta?wsdl";
       System.out.println("invoking client");
       annotations.oneway.testdata.OneWayRuntimeTestCaseClient.runtest(url, input, 1);
       System.out.println("service returned");
       time = System.currentTimeMillis()/1000 - time;
       System.out.println("response time was: "+ Long.toString(time) + " seconds");

       // now go see if the doSomething method is still running - it should be.
       String status = annotations.oneway.testdata.OneWayRuntimeTestCaseClient.getOneWayStatus();
       System.out.println("service method status is: "+ status);
       assertTrue("service failed to run, or ran as two -way ", time < 30.0   );

       System.out.println("********* test passed *********");
   }



}

