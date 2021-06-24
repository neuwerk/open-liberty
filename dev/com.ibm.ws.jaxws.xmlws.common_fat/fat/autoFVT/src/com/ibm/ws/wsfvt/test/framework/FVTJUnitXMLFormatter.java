package com.ibm.ws.wsfvt.test.framework;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitVersionHelper;
import org.apache.tools.ant.taskdefs.optional.junit.XMLConstants;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Custom XML result formatter, this XMLFormatter is a customized version of the
 * std JUnit XML formatter that comes with ANT. This version has been expanded
 * to add support for warnings and additional metadata supplied by the FvtTest
 * and FvtTestSuite annotations
 */
public class FVTJUnitXMLFormatter implements JUnitResultFormatter, XMLConstants {

	/** constant for unnnamed testsuites/cases */
	private static final String UNKNOWN = "unknown";

	private static DocumentBuilder getDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (Exception exc) {
			throw new ExceptionInInitializerError(exc);
		}
	}

	/**
	 * The XML document.
	 */
	private Document doc;

	/**
	 * The wrapper for the whole testsuite.
	 */
	private Element rootElement;

	/**
	 * Element for the current test.
	 */
	private Hashtable testElements = new Hashtable();

	/**
	 * tests that failed.
	 */
	private Hashtable failedTests = new Hashtable();

	/**
	 * Timing helper.
	 */
	private Hashtable testStarts = new Hashtable();

	/**
	 * Where to write the log to.
	 */
	private OutputStream out;

	/** No arg constructor. */
	public FVTJUnitXMLFormatter() {
	}

	/** {@inheritDoc}. */
	public void setOutput(OutputStream out) {
		this.out = out;
	}

	/** {@inheritDoc}. */
	public void setSystemOutput(String out) {
		formatOutput(SYSTEM_OUT, out);
	}

	/** {@inheritDoc}. */
	public void setSystemError(String out) {
		formatOutput(SYSTEM_ERR, out);
	}

	/**
	 * The whole testsuite started.
	 * 
	 * @param suite
	 *            the testsuite.
	 */
	public void startTestSuite(JUnitTest suite) {
		doc = getDocumentBuilder().newDocument();
		rootElement = doc.createElement(TESTSUITE);
		String n = suite.getName();
		rootElement.setAttribute(ATTR_NAME, n == null ? UNKNOWN : n);

		// add the timestamp
		final String timestamp = DateUtils.format(new Date(),
				DateUtils.ISO8601_DATETIME_PATTERN);
		rootElement.setAttribute("timestamp", timestamp);
		// and the hostname.
		rootElement.setAttribute("hostname", getHostname());

		// Output properties
		Element propsElement = doc.createElement(PROPERTIES);
		rootElement.appendChild(propsElement);
		Properties props = suite.getProperties();
		if (props != null) {
			Enumeration e = props.propertyNames();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				Element propElement = doc.createElement(PROPERTY);
				propElement.setAttribute(ATTR_NAME, name);
				propElement.setAttribute(ATTR_VALUE, props.getProperty(name));
				propsElement.appendChild(propElement);
			}
		}

		isFirst = true;
		warnings = 0;
	}

	/**
	 * get the local hostname
	 * 
	 * @return the name of the local host, or "localhost" if we cannot work it
	 *         out
	 */
	private String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "localhost";
		}
	}

	/**
	 * The whole testsuite ended.
	 * 
	 * @param suite
	 *            the testsuite.
	 * @throws BuildException
	 *             on error.
	 */
	public void endTestSuite(JUnitTest suite) throws BuildException {
		rootElement.setAttribute(ATTR_TESTS, "" + suite.runCount());
		rootElement.setAttribute(ATTR_FAILURES, "" + suite.failureCount());
		rootElement.setAttribute(ATTR_ERRORS, "" + (suite.errorCount()));
		rootElement.setAttribute("warnings", "" + warnings);
		rootElement.setAttribute(ATTR_TIME, "" + (suite.getRunTime() / 1000.0));
		if (out != null) {
			Writer wri = null;
			try {
				wri = new BufferedWriter(new OutputStreamWriter(out, "UTF8"));
				wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
				(new DOMElementWriter()).write(rootElement, wri, 0, "  ");
				wri.flush();
			} catch (IOException exc) {
				throw new BuildException("Unable to write log file", exc);
			} finally {
				if (out != System.out && out != System.err) {
					// ported from ant 1.6.1
                    if (wri != null) {
                        try {
                            wri.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
				}
			}
		}
	}

	boolean isFirst = false;

	int warnings = 0;

	Warning warning = null;

	/**
	 * Interface TestListener.
	 * 
	 * <p>
	 * A new Test is started.
	 * 
	 * @param t
	 *            the test.
	 */
	public void startTest(Test t) {

		if (isFirst) {
			if (t instanceof FVTTestCase) {
				FvtTestSuite suite = t.getClass().getAnnotation(
						FvtTestSuite.class);

				if (suite != null) {
					Element descr = doc.createElement("description");
					descr.setTextContent(suite.description());
					rootElement.appendChild(descr);
				}
			}
			isFirst = false;
		}

		testStarts.put(t, new Long(System.currentTimeMillis()));
	}

	/**
	 * Interface TestListener.
	 * 
	 * <p>
	 * A Test is finished.
	 * 
	 * @param test
	 *            the test.
	 */
	public void endTest(Test test) {
		// Fix for bug #5637 - if a junit.extensions.TestSetup is
		// used and throws an exception during setUp then startTest
		// would never have been called
		if (!testStarts.containsKey(test)) {
			startTest(test);
		}

		Element currentTest = null;
		if (!failedTests.containsKey(test)) {
			currentTest = doc.createElement(TESTCASE);
			String n = JUnitVersionHelper.getTestCaseName(test);
			currentTest.setAttribute(ATTR_NAME, n == null ? UNKNOWN : n);
			// a TestSuite can contain Tests from multiple classes,
			// even tests with the same name - we would like to disambiguate them.
			// However, until we do, we need to output something so that the xml is valid.
			currentTest.setAttribute(ATTR_CLASSNAME,JUnitVersionHelper.getTestCaseClassName(test));
			rootElement.appendChild(currentTest);
			testElements.put(test, currentTest);
		} else {
			currentTest = (Element) testElements.get(test);
		}

		if (test instanceof FVTTestCase) {
			FVTTestCase ftc = (FVTTestCase) test;
			if (ftc.getWarning() != null) {
				Element warning = doc.createElement("warning");
				warning.setTextContent(ftc.getWarning().getMessage());
				currentTest.appendChild(warning);
				warnings++;
			}
		}

		Long l = (Long) testStarts.get(test);
		currentTest.setAttribute(ATTR_TIME, ""
				+ ((System.currentTimeMillis() - l.longValue()) / 1000.0));

		addTestSuiteProps(doc, currentTest, test);
	}

	/**
	 * Interface TestListener for JUnit &lt;= 3.4.
	 * 
	 * <p>
	 * A Test failed.
	 * 
	 * @param test
	 *            the test.
	 * @param t
	 *            the exception.
	 */
	public void addFailure(Test test, Throwable t) {
		formatError(FAILURE, test, t);
	}

	/**
	 * Interface TestListener for JUnit &gt; 3.4.
	 * 
	 * <p>
	 * A Test failed.
	 * 
	 * @param test
	 *            the test.
	 * @param t
	 *            the assertion.
	 */
	public void addFailure(Test test, AssertionFailedError t) {
		addFailure(test, (Throwable) t);
	}

	/**
	 * Interface TestListener.
	 * 
	 * <p>
	 * An error occurred while running the test.
	 * 
	 * @param test
	 *            the test.
	 * @param t
	 *            the error.
	 */
	public void addError(Test test, Throwable t) {
		formatError(ERROR, test, t);
	}

	private void formatError(String type, Test test, Throwable t) {
		if (test != null) {
			endTest(test);
			failedTests.put(test, test);
		}

		Element nested = doc.createElement(type);
		Element currentTest = null;
		if (test != null) {
			currentTest = (Element) testElements.get(test);
		} else {
			currentTest = rootElement;
		}

		currentTest.appendChild(nested);

		String message = t.getMessage();
		if (message != null && message.length() > 0) {
			nested.setAttribute(ATTR_MESSAGE, t.getMessage());
		}
		nested.setAttribute(ATTR_TYPE, t.getClass().getName());

		String strace = JUnitTestRunner.getFilteredTrace(t);
		Text trace = doc.createTextNode(strace);
		nested.appendChild(trace);
	}

	private void formatOutput(String type, String output) {
		Element nested = doc.createElement(type);
		rootElement.appendChild(nested);
		nested.appendChild(doc.createCDATASection(output));
	}

	private void addTestSuiteProps(Document doc, Node node, Test test) {

		if (!(test instanceof FVTTestCase)) return;

		FVTTestCase ftc = (FVTTestCase) test;

		FvtTest anno = null;
		try {
			Method m = test.getClass().getMethod(ftc.getName(), (Class[]) null);
			anno = m.getAnnotation(FvtTest.class);
		} catch (Throwable t) {

		}

		if (anno != null) {
			if (anno.description() != null) {
				Element descr = doc.createElement("description");
				descr.setTextContent(anno.description());
				node.appendChild(descr);
			}

			if (anno.description() != null) {
				Element descr = doc.createElement("expected");
				descr.setTextContent(anno.expectedResult());
				node.appendChild(descr);
			}

			if (anno.since() != null) {
				Element since = doc.createElement("since");
				since.setTextContent(anno.since().toString());
				node.appendChild(since);
			}

			if (warning != null) {
				Element warn = doc.createElement("warning");
				warn.setTextContent(warning.getMessage());
				node.appendChild(warn);
				warning = null;
			}

		}
	}
} // XMLJUnitResultFormatter
