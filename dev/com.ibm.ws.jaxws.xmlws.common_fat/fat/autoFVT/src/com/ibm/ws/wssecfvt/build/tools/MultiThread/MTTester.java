/*
 *
 * 1.1, autoFVT/src/com/ibm/ws/wssecfvt/build/tools/MultiThread/MTTester.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01, 5/2/12
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
 *
 * Usage:
 *    Create a new class which extend MTTester. And override the runTest() method to run a single test. 
 *    runTest() returns true to show test OK and return false to show error.  
 *    The runTest() need to display info for debugging in case errors.
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

public class MTTester implements Runnable {
    Thread thisThread = null;

    int iTestRun     = 0;
    int iError       = 0;
    int iSucceed     = 0;

    String _strID    = null;
    int  testingSeconds = 0;

    long lRunBegin   = 0;
    long lLatestTest = 0;

    boolean bStopTest = false;

    boolean bCompleted = false;

    MultiThreadExecutor parentExecutor = null;

    public MTTester(String strID ){
        _strID = strID;
    }

    public void setParent( MultiThreadExecutor parent ){
        parentExecutor = parent;
    }

    public void setTestingSeconds( int iTestingSeconds ){
        testingSeconds = iTestingSeconds;
    }

    public boolean isCompleted(){
        return bCompleted;
    }

    public String getID(){
        return _strID;
    }

    public void stopTest(boolean bInterrupt){
        bStopTest = true;
        if( thisThread!= null && bInterrupt){
            thisThread.interrupt();
        }
    }

    //
    //  The runTest() should NOT call this method.
    //  It will be called when runTest() return false
    //  
    void encountError(){
        System.out.println( "Error: Test ID:" + _strID + "(ran " + iTestRun + " time) Error encounted." );
        parentExecutor.encountError(this);
    }

    synchronized void interruptedEncounted(Exception e){
        System.out.println( "******************************************");
        System.out.println( "**** Test ID:" + _strID + "(ran " + iTestRun + " time) thread isinterrupted " + e.getMessage() );
        System.out.println( "**** The stack trace shows up when we have too many errors and terminate the tests" );
        System.out.println( "**** Or when the thread(s) is/are hung");
        e.printStackTrace(System.out);
        System.out.println( "******************************************");
        bStopTest = true;
    }

    public void run(){
        thisThread= Thread.currentThread();

        lRunBegin    = (new Date()).getTime();
        lLatestTest  = lRunBegin;
        // Let run until the testingSeconds expired
        while((( (lLatestTest - lRunBegin) / 1000 ) < testingSeconds) &&
              ( ! bStopTest)  ){
            boolean bTestPassed = false;
            try{
                iTestRun ++;
                bTestPassed = runTest();
                if( bTestPassed ){
                     iSucceed ++;
                 } else {
                     iError ++;
                     encountError();
                 }
            } catch( InterruptedException e ){
                interruptedEncounted(e);
            } catch( Exception e ){
                if( Thread.currentThread().isInterrupted() ){
                    interruptedEncounted(e);
                } else{
                    iError ++;
                    System.out.println( "Test ID:" + _strID + "(ran " + iTestRun + " time) hit Exception:" + e.getMessage() );
                    e.printStackTrace(System.out);
                    parentExecutor.encountError(this);
                }
            }

            lLatestTest = (new Date()).getTime();
        }
        bCompleted = true;
    }

    public void report(){
        long lTime = (new Date()).getTime() - lRunBegin;
        System.out.println( "TestName  : " + _strID  + " Runtime(millisecond):" +  lTime );
        System.out.println( "  test ran: " + iTestRun + " Passed: " + iSucceed + "  Error: " + iError   );
        if( !bCompleted ){
            System.out.println( "  ***The test either did not complete or not complete normally***" );
        }
    }

    // To be overidden
    // Do not block the InterruptedException becuase it's very likely caused by
    //     the ThreadPoolExecutor.shutdownNow(). 
    //     
    // return: 
    //   true   -- this test succeeded
    //   false  -- this test failed
    // 
    //
    public boolean runTest() throws Exception{
        return true;
    }

}
