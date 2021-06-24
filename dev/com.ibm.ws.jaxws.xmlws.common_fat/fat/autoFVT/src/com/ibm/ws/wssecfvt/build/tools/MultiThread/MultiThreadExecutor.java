/*
 *
 * 1.1, autoFVT/src/com/ibm/ws/wssecfvt/build/tools/MultiThread/MultiThreadExecutor.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01, 5/2/12
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2011
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect    Author		Marker	Description
 * ----------  --------------   -----------	------	----------------------------
 * 11/20/11                     gkuo         Create for multi-thread tests
 *        
 */

/*
 * This class reuse java ThreadPoolExecutor to run the multi-thread stress tests
 * ThreadPoolExecutor is not perfect for the multi-thread stress test. 
 * But it has quite some methods that we need.
 *
 * The class will try to: 
 * 1) Counting test Error. If test errotrs exceed the maximum errors allowed. It ought to do shutdownNow();
 * 2) Checking potential deadlock:
 *    When tests are running, the main will sleep until the "testingSeconds + consideredHangSeconds" expired.
 *    Then it will check if each MTTester completed OK. If not,  we will consider it's a dead lock
 *    We can set the consideredHandSeconds to tune for better results.
 * 
 *
 * Usage:
 *    Create a new class which extend MTTester. And code the runTest() method to run a single test. 
 *    runTest() returns true to show test OK and return false to show error.  
 *    The runTest() need to display info for debugging in case errors.
 *    The runTest should not block the InterruptedException. It's caused by the Thread to interrupt 
 *
 * FYI...
 *   In order to run MultiThreadTests, we need much more jvm memory than normal tests
 *   Edit the properties.xml in FVT\ws\code\websvcs.fvt\src\xmls and enlarge the xms and xmx. See example:
 *   From:
 *     <property name="junit.jvm.xms" value="-Xms40m"/>
 *     <property name="junit.jvm.xmx" value="-Xmx256m"/>
 *   To:
 *     <property name="junit.jvm.xms" value="-Xms512m"/>
 *     <property name="junit.jvm.xmx" value="-Xmx1024m"/>
 *   
 * Example: 
 *    Testcase examples please see MultiThreadTests.java.txt
 *
 */

package com.ibm.ws.wssecfvt.build.tools.MultiThread;

import java.util.concurrent.*;
import java.util.*;

public class MultiThreadExecutor {

    int  maxError                   = 0; // Maximum test errors before we terminate the test 
    int  testingSeconds             = 0;
    int  consideredHangSeconds      = 0;  // If a test hand more than this time, considered it's a dead lock or Hang
    ArrayList<MTTester> testers     = new ArrayList<MTTester>();
    int  errorEncounted             = 0;  // The error amount that the tests encounted
    ThreadPoolExecutor testExecutor = null;

    // The main thread which runs MultiThreadExecutor
    Thread          mainMTEThreadd  = null;

    public void addTester(MTTester mtTester ){
        testers.add( mtTester );
        mtTester.setParent( this );
    }

    /*
     *  This will kick off the MYTesters
     *  The Executor will try to terminate the tests after it runs iTestingSeconds + iConsideredHangSeconds
     *
     *  Parameters:
     *    iMaxErrors -- If tests exceed max errors, we will interrupt the tests. 
     *                  Otherwise run tests untiltestingSeconds expired then check the errors
     *    iTestingSeconds --- How long the MultiThread tests is supposed to run.
     *    iConsideredHangSeconds --- A test whose hang time exceed this value is considered hang (1 tests) or dead lock( >= 2 test).
     */
    public boolean executeTests(int iMaxErrors, int iTestingSeconds, int iConsideredHangSeconds ){
        mainMTEThreadd = Thread.currentThread();

        // Initialize the test
        maxError              = iMaxErrors;
        testingSeconds        = iTestingSeconds;
        consideredHangSeconds = iConsideredHangSeconds;
        errorEncounted        = 0;    // reset the error enconuted
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(testers.size()); 

        testExecutor = new ThreadPoolExecutor(testers.size(), // minimun threads in the pool
                                              testers.size(), // maximum threads in the pool
                                              testingSeconds + consideredHangSeconds, //Keep alive time. NotApplicable in this case
                                              TimeUnit.SECONDS,                       // KeepAliveTime unit
                                              queue);                                 // The Runnable queue. Not Applicable in this case
        return  runAllTests();
    }


    boolean bInterrupted = false;
    /*
     * Whenencount erro, the tester ought display the information of the failures
     * And necessary infor for debugging or trace
     */
    synchronized public void encountError(MTTester tester){
        errorEncounted ++;
        System.out.println( "**Error happened " + errorEncounted + " times" );
        if( errorEncounted > maxError ){
            if(! bInterrupted ){ 
                bInterrupted = true;
                // do not interrupt the parent twice. Otherwise the test may be terminated
                // And junit will be in a weird state
                try{
                    stopTests( false);
                    mainMTEThreadd.interrupt();
                } catch( Exception e ){
                    e.printStackTrace( System.out );
                }
            }
        }
    }

    boolean runAllTests(){
        // Let check if any tester hangs
        // consideredHangSeconds
        long lTestBeginTime = new Date().getTime();

        for( int iI = 0; iI < testers.size(); iI ++ ){
            MTTester tester = testers.get( iI );
            tester.setTestingSeconds( testingSeconds );
            tester.setParent( this );
            testExecutor.execute( tester );
        }

        // Ask the test executor 1) Do not accept any new MTTester 
        testExecutor.shutdown();
        
        boolean bCompleted   = false;
        try {
            // true  : tests completed. 
            // false : test is not completed and timeout happened
            bCompleted = testExecutor.awaitTermination( testingSeconds, TimeUnit.SECONDS);
        } catch ( Exception e ) {
            //e.printStackTrace( System.out );
            String strExc = e.getClass().getName();
            System.out.println( "Exception " + strExc + " happened: Msg(" + e.getMessage() + ")" );
        }

        // Let's set a flag to stop all tester if they are still running
        stopTests( false );

        if( errorEncounted > 0 ){
            // SInce errors encounted, let's stop all testers
            testExecutor.shutdownNow();
            endingReport();
            System.out.println( "The test encounted " + errorEncounted + " errors. The tester ought to display the error information already" );
            return false;
        }

        if( bCompleted ){ // All the tester has completed their tests and no error encounted 
            endingReport();
            return true;  // succeeded
        }

        System.out.println( "Tests are not fully stop. Let's wait more time (consideredHangSeconds)" );

        // Let's wait more time (consideredHangSeconds)
        try {
            // true  : tests completed. 
            // false : test is not completed and timeout happened
            bCompleted = testExecutor.awaitTermination( consideredHangSeconds, TimeUnit.SECONDS);
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
        }


        if( errorEncounted > 0 ){
            // SInce max error encounted, let's stop all testers
            testExecutor.shutdownNow();
            endingReport();
            System.out.println( "The test encounted " + errorEncounted + " errors. The tester ought to display the error ninf already" );
            return false;
        }

        if( bCompleted ){ // All the tester has completed their tests and no error encounted 
            endingReport();
            return true;  // succeeded
        } else{ // Hang happened
            endingReport();
            int iStillRunningTasks = 0;// Let's check how many task is going on
            for( int iI = 0; iI < testers.size(); iI ++ ){
                MTTester tester = testers.get( iI );
                if( !tester.isCompleted() ) iStillRunningTasks ++;
            }

            // Let's set a flag to stop all tester and all             endingReport();
            stopTests( true );

            // let's stop all testers. Even it's not guaranted to work
            testExecutor.shutdownNow();
            System.out.println( "Running tasks:" + iStillRunningTasks );
            System.out.println( "*** Error: Even the consideredHangSeconds expired but some tests are not completed" );
            if( iStillRunningTasks > 1) {
                System.out.println( "*** Possible Hang or Threads dead lock" );
            } else {
                System.out.println( "*** Possible Hang" );
            }
            // let's stop all testers again. since it's not guaranted to work
            testExecutor.shutdownNow();
            return false;
        }

    }
 
    void endingReport(){
        for( int iI = 0; iI < testers.size(); iI ++ ){
            MTTester tester = testers.get( iI );
            tester.report();
        }
    }


    // This does not terminate the tester immediately. But
    // set a flag to them to stop running when they can
    void stopTests( boolean bInterrupt){
        for( int iI = 0; iI < testers.size(); iI ++ ){
            MTTester tester = testers.get( iI );
            tester.stopTest(bInterrupt);
        }
        sleep( 1000 );  // let's sleep a second
    }


    void sleep( long millionSeconds ){
        try{
            Thread.currentThread().sleep( millionSeconds );
        } catch( Exception e ){
        }
    }
}
