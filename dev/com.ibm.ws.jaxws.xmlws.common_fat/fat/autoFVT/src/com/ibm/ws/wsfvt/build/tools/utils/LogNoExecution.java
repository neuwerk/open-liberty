//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/utils/LogNoExecution.java, WAS.websvcs.fvt, WASX.FVT 3/23/07 14:00:08 [7/11/07 13:14:08]
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
// 03/16/2007  jramos      426986          New File
// 03/23/2007  jramos      428286          Add constructor

package com.ibm.ws.wsfvt.build.tools.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.TestCase;

import com.ibm.ws.wsfvt.build.tools.AppConst;

public class LogNoExecution extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
	
	private static final String LEVEL_PACKAGE = "package";
	private static final String LEVEL_SUITE = "suite";
	private static final String LEVEL_TEST_CASE = "test_case";
	
	public LogNoExecution() {
		super("");
	}

	/**
	 * Log a test package that is not being executed.
	 * 
	 * @param packageName The test package that shows on the JUnit
	 *            report
	 * @param message A message to log
	 */
	public static void logPackage(String packageName, String message) {
		logNoExecution(LEVEL_PACKAGE, packageName, null, null, message);
	}
	/**
	 * Log a test case that is not being executed.
	 * 
	 * @param packageName The test package that shows on the JUnit
	 *            report
	 * @param suiteName The test suite name that shows on the JUnit
	 *            report
	 * @param message A message to log
	 */
	public static void logTestSuite(String packageName, String suiteName, String message) {
		logNoExecution(LEVEL_SUITE, packageName, suiteName, null, message);
	}

	/**
	 * Log a test case that is not being executed.
	 * 
	 * @param packageName The test package that shows on the JUnit
	 *            report
	 * @param suiteName The test suite name that shows on the JUnit
	 *            report
	 * @param testCase The test case name that shows on the JUnit
	 *            report
	 * @param message A message to log
	 */
	public static void logTestCase(String packageName, String suiteName, String testCase, String message) {
		logNoExecution(LEVEL_TEST_CASE, packageName, suiteName, testCase, message);
	}
	
	/**
	 * Write an entry into the log
	 * 
	 * @param level The loggin level
	 * @param packageName The test package that shows on the JUnit
	 *            report
	 * @param suiteName The test class name that shows on the JUnit
	 *            report
	 * @param testCase The test case name that shows on the JUnit
	 *            report
	 * @param message A message to log
	 */
	private static void logNoExecution(String level, String packageName, String suiteName, String testCase, String message) {
		FileOutputStream fos = null;
		PrintStream ps = null;
		try {
			fos = new FileOutputStream((new File(AppConst.NO_EXECUTION_LOG)), true);
			ps = new PrintStream(fos);
			if(level.equals(LEVEL_PACKAGE)) {
				ps.println("PACKAGE NOT EXECUTED:");
			} else if(level.equals(LEVEL_SUITE)) {
				ps.println("TEST SUITE NOT EXECUTED:");
			} else if(level.equals(LEVEL_TEST_CASE)) {
				ps.println("TEST CASE NOT EXECUTED:");
			}
			ps.println("TestPackage:" + packageName);
			if(suiteName != null)
				ps.println("Suite:" + suiteName);
			if(testCase != null)
				ps.println("TestCase:" + testCase);
			ps.println("Message:" + message);
			ps.println("-------------------------");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Could not write to the " + AppConst.NO_EXECUTION_LOG + " log file. Failing this test case so that the lack of execution does not go unreported.");
		}
		finally {
			if(ps != null)
				ps.close();
			else if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
