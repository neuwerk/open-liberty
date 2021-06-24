//
// @(#) 1.5 autoFVT/src/com/ibm/ws/wsfvt/test/build/BuildTestCase.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:08:12 [8/8/12 06:54:29]
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 12/02/2004  ulbricht    D246124         New File
// 02/25/2005  ulbricht    D258191         Verify file is greater than 0 size
// 08/16/2006  smithd      D381622         Add ND support (moved Operations class)
// 03/16/2007  jramos      426986          Add testNoExecution()
// 05/24/2007  jramos      440922          Changes for Pyxis
// 10/22/2007  jramos      476750          Use TopologyDefaults tool and ACUTE 2.0 api
// 10/23/2008  jramos      559143          Incorporate Simplicity

package com.ibm.ws.wsfvt.test.build;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.Operations;

/**
 * This class will check for various failures that might
 * occur during the building, installing, and uninstalling
 * of the test suite.
 */
public class BuildTestCase extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {

    /**
     * Class constructor requiring a String parameter
     * specifying the name of the test to run.
     *
     * @param name Name of test to run
     */
    public BuildTestCase(String name) {
        super(name);
    }

    /**
     * This method will run tests specified from the suite
     * method using the text user interface TestRunner.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * The suite method returns the tests to run from
     * this suite.  The PublishWsdlTestCase class is specified
     * which means all methods starting with "test" will be
     * be run.
     *
     * @return A Test object containing tests to be run
     */
    public static Test suite() {
        return new TestSuite(BuildTestCase.class);
    }

    /**
     * This will test whether there was a build failure during
     * the test run.
     *
     * @throws Exception Any kind of exception
     */
    public void testBuildFailure() throws Exception {
        File bldErrLogFile = new File(AppConst.BLD_ERR_LOG);
        checkLogFile("build", bldErrLogFile);
    }

    /**
     * This will test whether there were any install failures
     * during the test run.
     *
     * @throws Exception Any kind of exception
     */
    public void testInstallFailure() throws Exception {
        File installErrLogFile = new File(AppConst.INSTALL_ERR_LOG);
        checkLogFile("install", installErrLogFile);
    }

    /**
     * This will test whether there were any uninstall failures
     * during the test run.
     *
     * @throws Exception Any kind of exception
     */
    public void testUninstallFailure() throws Exception {
        File uninstallErrLogFile = new File(AppConst.UNINSTALL_ERR_LOG);
        checkLogFile("uninstall", uninstallErrLogFile);

    }
    
    /**
     * This will test whether there were any tests not run
     * during the test run
     *
     * @throws Exception Any kind of exception
     */
    public void testNoExecution() throws Exception {
    	File notExectedLogFile = new File(AppConst.NO_EXECUTION_LOG);
    	checkLogFile("not executed", notExectedLogFile);
    }

    /**
     * A general purpose method for checking the different
     * types of log files.
     *
     * @param type The type of log file
     * @param logFile The log file to be checked
     *
     * @throws Exception Any kind of exception
     */
    private void checkLogFile(String type, File logFile) throws Exception {
        if ((logFile.exists()) && (logFile.length() > 0)) {
            fail("There were errors during the " + type + " step. "
                 + "The contents of the " + type + " log error file are: "
                 + System.getProperty("line.separator")
                 + Operations.readTextFile(logFile));
        }
    }

}
