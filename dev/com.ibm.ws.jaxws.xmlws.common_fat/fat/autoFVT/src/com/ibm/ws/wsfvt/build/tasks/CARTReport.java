/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/CARTReport.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/1/07 14:15:41 [8/8/12 06:56:15]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect  Description
 * -----------------------------------------------------------------------------
 * 01/15/2007  smithd      415287          New File
 * 01/19/2007  smithd      415795          Update platform support
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * This class is used to report the test results of a full automated run to the CART system. The results will be
 * gathered from the log files.
 * 
 */
public class CARTReport extends Task {

	public String build = null;
	public String release = null;
	public String platform = null;
	public String url = null;
	public String summaryHTMLFile = null;

	public String passed = null;
	public String failed = null;
	public String attempted = null;
	public String blocked = null;
	public String total = null;

	private static final String SERVLET = "http://bvtstatus.austin.ibm.com:9080/Cart/CartGetReportServlet";
	// TODO private static final String ID = "WebServicesRuntime_FVT";
	private static final String ID = "WebServices_FVT";

	private static final String FVT_WINDOWS = "Win";
	private static final String CART_WINDOWS = "Win_ia32";
	private static final String FVT_LINUX = "Linux";
	private static final String CART_LINUX = "Linux_ia32";
	private static final String FVT_AIX = "AIX";
	private static final String CART_AIX = "AIX_ppc32";
	private static final String FVT_SOLARIS = "Solaris";
	private static final String CART_SOLARIS = "Solaris_sparc";
	private static final String FVT_HP = "HP";
	private static final String CART_HP = "HPUX_parisc";
	private static final String FVT_ZOS = "z/OS";
	private static final String CART_ZOS = "z/OS_31";
	private static final String FVT_ISERIES = "OS/400";
	private static final String CART_ISERIES = "i5/OS_ppc64";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		if (build == null) {
			throw new BuildException("Must provide a build");
		}
		if (release == null) {
			throw new BuildException("Must provide a release");
		}
		if (platform == null) {
			throw new BuildException("Must provide a platform");
		}
		if (url == null) {
			throw new BuildException("Must provide a url");
		}
		if (summaryHTMLFile == null) {
			throw new BuildException("Must provide a summary html file");
		}

		// Transform the platform names to the respective CART platform names
		if (platform.startsWith(FVT_WINDOWS)) {
			platform = CART_WINDOWS;
		} else if (platform.startsWith(FVT_LINUX)) {
			platform = CART_LINUX;
		} else if (platform.startsWith(FVT_AIX)) {
			platform = CART_AIX;
		} else if (platform.startsWith(FVT_SOLARIS)) {
			platform = CART_SOLARIS;
		} else if (platform.startsWith(FVT_HP)) {
			platform = CART_HP;
		} else if (platform.startsWith(FVT_ZOS)) {
			platform = CART_ZOS;
		} else if (platform.startsWith(FVT_ISERIES)) {
			platform = CART_ISERIES;
		} else {
			throw new BuildException("Unsupported platform: " + platform);
		}

		// TODO remove this, but for now only use release "wasx"
		release = "wasx";

		// Populate the statistics (passed, failed, total, etc)
		discoverStatistics();

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("Action", "submitreport");
		hm.put("Id", ID);
		hm.put("build", build);
		hm.put("Reason", "Initial");
		hm.put("Release", release);
		hm.put("platform", platform);
		hm.put("Passed", passed);
		hm.put("Failed", failed);
		hm.put("Attempted", attempted);
		hm.put("Blocked", blocked);
		hm.put("Total", total);
		hm.put("url", url);
		hm.put("Submitter", "Dylan Smith");

		submitReport(hm);
	}

	/**
	 * This method will discover the statistics of the JUnit tests. This method relies on the <code>reportFile</code>
	 * containing specific JUnit generated summary information. From that, it will determine the total number of tests
	 * run, and the number failed and errors (which will be added together to form the failures). The rest of the
	 * information is gather from those entries and stored in the instance variables.
	 * 
	 */
	private void discoverStatistics() {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(this.summaryHTMLFile);
			br = new BufferedReader(fr);
			String line = null;
			while (br.ready()) {
				line = br.readLine();
				if (line.indexOf("<th>Tests</th><th>Failures</th><th>Errors</th><th>Success rate</th>"
						+ "<th>Time</th>") != -1) {
					// we're at the summary total portion of the file
					while (br.ready()) {
						line = br.readLine();
						// The next line that has a TD tag, is our line
						if (line.indexOf("<td>") != -1) {
							StringTokenizer st = new StringTokenizer(line, "<td>");
							while (st.hasMoreTokens()) {
								// The first value is the total number
								this.total = st.nextToken();
								// The next is the end of the total
								st.nextToken();
								// The next value is the num failed
								this.failed = st.nextToken();
								// And the end of the failed
								st.nextToken();
								// And finally the num errors
								this.failed = new Integer(this.failed) + new Integer(st.nextToken()) + "";
								// done here...
								break;
							}
							// done looking..
							break;
						}
					}
					// done looking
					break;
				}
			}

			// Now compute the rest...
			this.attempted = this.total;
			this.blocked = "0";
			this.passed = new Integer(this.total) - new Integer(this.failed) + "";

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// do nothing
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	/**
	 * Sends the Map containing the report information to the CART servlet. The response is a String array that will
	 * contain two elements. The first is the response message, and the second is the response code. The response code
	 * is returned.
	 * 
	 * @param reportMap
	 *            The Map containing the report information to send to the CART servlet
	 * @return The response code from the CART servlet (0 is no errors)
	 */
	public int submitReport(Map reportMap) {
		String[] response = null;
		try {
			URL servletUrl = new URL(SERVLET);
			HttpURLConnection huc = (HttpURLConnection) servletUrl.openConnection();
			huc.setDoInput(true);
			huc.setDoOutput(true);
			huc.setRequestMethod("POST");
			huc.connect();
			OutputStream os = huc.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(reportMap);
			oos.close();
			InputStream is = huc.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			response = (String[]) ois.readObject();
			System.out.println("Cart Response Message : " + response[0]);
			System.out.println("Cart Response Code : " + response[1]);
			ois.close();
			huc.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Integer(response[1]).intValue();
	}

	/**
	 * @return the attempted
	 */
	public String getAttempted() {
		return attempted;
	}
	/**
	 * @param attempted
	 *            the attempted to set
	 */
	public void setAttempted(String attempted) {
		this.attempted = attempted;
	}
	/**
	 * @return the blocked
	 */
	public String getBlocked() {
		return blocked;
	}
	/**
	 * @param blocked
	 *            the blocked to set
	 */
	public void setBlocked(String blocked) {
		this.blocked = blocked;
	}
	/**
	 * @return the build
	 */
	public String getBuild() {
		return build;
	}
	/**
	 * @param build
	 *            the build to set
	 */
	public void setBuild(String build) {
		this.build = build;
	}
	/**
	 * @return the failed
	 */
	public String getFailed() {
		return failed;
	}
	/**
	 * @param failed
	 *            the failed to set
	 */
	public void setFailed(String failed) {
		this.failed = failed;
	}
	/**
	 * @return the passed
	 */
	public String getPassed() {
		return passed;
	}
	/**
	 * @param passed
	 *            the passed to set
	 */
	public void setPassed(String passed) {
		this.passed = passed;
	}
	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return platform;
	}
	/**
	 * @param platform
	 *            the platform to set
	 */
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	/**
	 * @return the release
	 */
	public String getRelease() {
		return release;
	}
	/**
	 * @param release
	 *            the release to set
	 */
	public void setRelease(String release) {
		this.release = release;
	}
	/**
	 * @return the summaryHTMLFile
	 */
	public String getSummaryHTMLFile() {
		return summaryHTMLFile;
	}
	/**
	 * @param summaryHTMLFile
	 *            the summaryHTMLFile to set
	 */
	public void setSummaryHTMLFile(String summaryHTMLFile) {
		this.summaryHTMLFile = summaryHTMLFile;
	}
	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}
	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url.replaceAll(" ", "%20");
	}

	public static void main(String args[]) {
		CARTReport cart = new CARTReport();
		cart.setBuild("f0701.16");
		cart.setRelease("WSFP.WFVT");
		cart.setPlatform("Linux");
		cart.setUrl("http://ws-rhel4-2.austin.ibm.com:7696/~tester/tests/WSFP.WIWAS/f0701.16/"
				+ "Linux/2.6.9-34.ELsmp-x86/01-11-2007-11:11/index.html");
		cart.setSummaryHTMLFile("C:/overview-summary.html");
		cart.execute();

		System.out.println(cart.getPassed());
		System.out.println(cart.getFailed());
		System.out.println(cart.getAttempted());
		System.out.println(cart.getBlocked());
		System.out.println(cart.getTotal());
	}

}
