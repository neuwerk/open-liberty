//
// @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tools/GenerateXMLs.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/6/06 17:06:09 [8/8/12 06:55:16]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 08/03/06 smithd      LIDB3992-28.1   New File
//

package com.ibm.ws.wsfvt.build.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Generates the xml files for a JUnit report. These are the TEST-* reports. Since the jython tests can not be easily
 * loaded into the JUnit framework, I am writing this class to generate the xml files to emulate a JUnit run. The
 * junitreport ant task will then pick up these xml files and add them to the html report.
 * 
 * @author smithd
 */
public class GenerateXMLs {

	/**
	 * Generates the xml files necessary for the JUnit report. These files are generated off of a html output file that
	 * was the result of a jython wsadmin run.
	 * 
	 * @param htmlLogFile
	 *            The fully qualified path to the html log file
	 * @param outputLocation
	 *            The location where the xml files will be stored
	 * @param packageName
	 *            The name of the package tested
	 */
	public static void genXMLs(String htmlLogFile, String outputLocation, String packageName) {

		PrintStream xmlFile = null;
		FileInputStream htmlOutputFile = null;
		Properties propFile = new Properties();

		try {
			xmlFile = new PrintStream(new FileOutputStream(outputLocation + "/TEST-" + packageName + ".xml"));
			htmlOutputFile = new FileInputStream(htmlLogFile);
			propFile.load(htmlOutputFile);
			// After we load the properties, we must reload the file input stream
			htmlOutputFile.close();
			htmlOutputFile = new FileInputStream(htmlLogFile);

			createXMLSummary(xmlFile, propFile, htmlOutputFile, packageName);
			htmlOutputFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find the file.  inputLocation: " + " htmlLogFile: " + htmlLogFile);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (xmlFile != null) {
				xmlFile.close();
			}
		}
	}

	/**
	 * Creates the XML TEST summary for the specific test package. This will only specify the failed, package name, and
	 * total tests -- along with the SystemOut from the test run.
	 * 
	 * @param outputXML
	 * @param prop
	 * @param inputHTML
	 * @param packageName
	 * @throws IOException
	 */
	private static void createXMLSummary(PrintStream outputXML, Properties prop, FileInputStream inputHTML,
			String packageName) throws IOException {
		String passed = prop.getProperty("passed");
		String failed = prop.getProperty("failed");
		String total = new Integer(passed) + new Integer(failed) + "";
		outputXML.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		outputXML.println("<testsuite errors=\"0\" failures=\"" + failed + "\" name=\"" + packageName + "\" tests=\""
				+ total + "\">");
		outputXML.println("<system-out>");
		BufferedReader br = new BufferedReader(new InputStreamReader(inputHTML));

		while (br.ready()) {
			outputXML.println(br.readLine());
		}
		outputXML.println("</system-out>");
		outputXML.println("<system-err><![CDATA[]]></system-err>");
		outputXML.println("</testsuite>");
	}
}
