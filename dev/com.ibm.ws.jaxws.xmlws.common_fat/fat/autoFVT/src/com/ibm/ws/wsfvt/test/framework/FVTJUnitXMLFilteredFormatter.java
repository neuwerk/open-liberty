package com.ibm.ws.wsfvt.test.framework;

import java.io.OutputStream;
import java.lang.reflect.Method;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;

/**
 * Formatter that filter suites based on release filter. Release filter is
 * specified via system property release.filter
 */
public class FVTJUnitXMLFilteredFormatter extends FVTJUnitXMLFormatter {

	private String filter = getFilter();

	private boolean filterSuite = false;

	private boolean filterTest = false;

	private int runs = 0;

	private int fails = 0;

	private int errors = 0;

	/**
	 * Utility method to obtain the filter. * and ** references are translated
	 * to null, indicating no filtering
	 * 
	 * @return
	 */
	public static String getFilter() {
		String filter = System.getProperty("release.filter");
		if (filter != null) {
			filter = filter.trim();
			if (filter.equals("*") || filter.equals("**")) {
				filter = null;
			}
		}

		return filter;
	}

	/**
	 * Determine if the entire suite should be filtered out. If the suite
	 * contains an un-annotated test method or an annotated method where "since"
	 * filed matches the filter that suite is not filtered out
	 * 
	 * @param t
	 * @return returns true to indicate that suite should be filtered out
	 */
	public static boolean filterSuite(Class t) {

		String filter = getFilter();

		if (filter == null || t == null) return false;

		Class c = t;
		boolean flt = true;
		try {
			// go though this method by method looking for offending annotations
			// if at least one empty or one matching filter is found then
			// we move on with reporting the suite

			flt = true;
			Method[] methods = c.getMethods();
			for (Method m : methods) {
				if (m.getName().startsWith("test")
						&& (m.getParameterTypes() == null || m
								.getParameterTypes().length == 0)) {

					FvtTest ft = m.getAnnotation(FvtTest.class);
					if (ft != null) {
						flt &= filter.indexOf(ft.since().toString()) == -1;
					} else {
						flt &= false;
					}

				}
			}
		} catch (Exception e) {
			flt = false;
		}

		return flt;
	}

	/**
	 * Determine if the test should be filtered out. If the test is un-annotated
	 * or has annotation where "since" filed matches then the test is not
	 * filtered out.
	 * 
	 * @param t
	 * @return returns true to indicate that test should be filtered out
	 */
	public static boolean filterTest(Test t) {

		String filter = getFilter();

		if (t == null || filter == null) return false;

		boolean flt = false;
		if (t instanceof FVTTestCase) {
			try {
				FVTTestCase ftc = (FVTTestCase) t;

				Method m = ftc.getClass().getMethod(ftc.getName(),
						(Class[]) null);
				FvtTest ft = m.getAnnotation(FvtTest.class);

				if (ft != null) {
					flt = filter.indexOf(ft.since().toString()) == -1;
				} else {
					flt = false;
				}

				return flt;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public void startTestSuite(JUnitTest suite) throws BuildException {

		runs = 0;
		fails = 0;
		errors = 0;

		if (filter != null) {
			try {
				filterSuite = filterSuite(Class.forName(suite.getName()));
			} catch (Exception e) {
				filterSuite = false;
			}
			filterTest = filterSuite;
		} else {
			filterSuite = false;
			filterTest = false;
		}

		if (!filterSuite) {
			super.startTestSuite(suite);
		}

	}

	public void endTestSuite(JUnitTest suite) throws BuildException {
		if (!filterSuite) {
			suite.setCounts(runs, fails, errors);
			super.endTestSuite(suite);
		}
	}

	public void setOutput(OutputStream arg0) {
		if (!filterSuite) {
			super.setOutput(arg0);
		}
	}

	public void setSystemError(String arg0) {
		if (!filterSuite) {
			super.setSystemError(arg0);
		}
	}

	public void setSystemOutput(String arg0) {
		if (!filterSuite) {
			super.setSystemOutput(arg0);
		}
	}

	public void addError(Test test, Throwable t) {
		if (!filterSuite && !filterTest) {
			errors++;
			super.addError(test, t);
		}
	}

	public void addFailure(Test test, AssertionFailedError t) {
		if (!filterSuite && !filterTest) {
			fails++;
			super.addFailure(test, t);
		}
	}

	public void endTest(Test test) {
		if (!filterSuite && !filterTest) {
			super.endTest(test);
		}
	}

	public void startTest(Test test) {
		if (!filterSuite) {
			filterTest = filterTest(test);
		}

		if (!filterTest) {
			runs++;
			super.startTest(test);
		}
	}
}
