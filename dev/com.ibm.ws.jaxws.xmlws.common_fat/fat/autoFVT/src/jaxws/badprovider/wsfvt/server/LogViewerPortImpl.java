//
// @(#) 1.3 autoFVT/src/jaxws/badprovider/wsfvt/server/LogViewerPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/29/07 17:15:02 [8/8/12 06:55:21]
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
// 07/31/06 sedov       LIDB3296.42     New File
// 01/29/07 sedov       D417317         Added PROFILE_DIR
//

package jaxws.badprovider.wsfvt.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* WebSphere log file viewer - client. This class is designed to work in a
* distributed environment and to minimize network traffic. The client will
* invoke mark() to capture the current log position then perform some operation
* then invoke readUpdates() which will return all new log entries and update
* marked position. This will account for log rollover as well.
***/
public class LogViewerPortImpl extends javax.servlet.http.HttpServlet {

	private static long logLengthMarked = 0;

	private static LogName logPrevMarked = null;

	private enum LogName {
		sysout, syserr, trace
	}

	// none - same as update but no data travels back on the wire, updates mark
	// state
	// update - show new entries in the log since last view, updates mark state
	// all - show the entire log, does not affect mark state
	private enum ViewMode {
		none, update, all
	}

	final static String sysout = "SystemOut";

	final static String syserr = "SystemErr";

	final static String trace = "trace";

	private static final boolean DEBUG = true;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletOutputStream out = resp.getOutputStream();

		// Log to read..can be systemout, systemerr or trace
		LogName log = getParam(req, "log", LogName.sysout);

		// View mode...show nothing, just the updates or everything
		ViewMode view = getParam(req, "view", ViewMode.all);

		// server name...default is server1
		String server = getParam(req, "server", "server1");

		// marked mode switch
		boolean mark = getParam(req, "marked", Boolean.FALSE);
		


		// findout where the log file is stored
		String serverRootPath = System.getProperty("server.root", "@PROFILE_DIR@");
		
		String logPath = getParam(req, "logPath", serverRootPath+"/usr/servers/jaxws_fat_server/logs/jaxws_fat_server/console.log");
		
		// get the log file
		File logFile = new File(logPath);

		// determine if marking information is still valid
		// if we have a new log file then reset the marking

		if (!log.equals(logPrevMarked)) {

			logPrevMarked = null;
			logLengthMarked = 0;
		}

		if (DEBUG)
			System.out.println("log=" + log + " view=" + view + " marked=" + mark
					+ " server=" + server);

		// if we want to only read updates then mark this position
		switch (view) {
		// show the entire log...marking information is not
		// updated
		case all:
			if (DEBUG) System.out.println("ALL");

			printToOutStream(out, logFile, 0);
			break;

		// show updates since last mark
		// if not marking then behaves same as all
		case update:
			if (DEBUG) System.out.println("UPDATE");

			if (mark == false) {
				if (DEBUG) System.out.println("UPDATE+NOT_MARK");

				logPrevMarked = null;
				logLengthMarked = 0;
				printToOutStream(out, logFile, 0);
			} else if (mark && logLengthMarked > logFile.length()) {
				if (DEBUG) System.out.println("UPDATE+ROLLOVER");

				File rollover = logRolloverLogFileName(log, new File(
						serverRootPath, "logs/" + server));

				if (DEBUG) System.out.println("Rollover file " + rollover);

				if (rollover != null)
					printToOutStream(out, rollover, logLengthMarked);

				printToOutStream(out, logFile, 0);
				logLengthMarked = logFile.length();

			} else {

				if (DEBUG) System.out.println("UPDATE+MARK");

				printToOutStream(out, logFile, logLengthMarked);
				logLengthMarked = logFile.length();

			}

			break;

		// update the marking information but do not print anything
		case none:
			if (DEBUG) System.out.println("NONE");

			if (mark == true) {
				logPrevMarked = log;
				logLengthMarked = logFile.length();
			}
		}

	}

	private void printToOutStream(ServletOutputStream out, File f1, long offset)
			throws IOException {

		if (DEBUG) System.out.println("Printing " + f1 + " from " + offset);

		// print contents of file1
		// the user could have specified an offset, we will assume that it is
		// valid
		if (f1 != null) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(f1));
				String str;

				in.skip(offset);
				while ((str = in.readLine()) != null) {
					out.println(str);
				}
				in.close();
			} catch (IOException e) {
				out.println("Unable to access: " + f1.getAbsolutePath());
				out.println(e.getMessage());
			}
		}

	}

	private String logToLogFileName(LogName log) {

		if (log == null) return sysout + ".log";

		switch (log) {
		case sysout:
			return sysout + ".log";
		case syserr:
			return syserr + ".log";
		case trace:
			return trace + ".log";
		}

		return null;
	}

	private File logRolloverLogFileName(LogName log, File path) {

		String log_name = sysout;

		if (log != null) {
			switch (log) {
			case sysout:
				log_name = sysout;
			case syserr:
				log_name = syserr;
			case trace:
				log_name = trace;
			default:
				log_name = sysout;
			}
		}

		// find the rollover file
		File youngest = null;
		for (File f : path.listFiles()) {
			if (f.isFile()) {
				// if it is a file that is named as name_08_09_....log
				if (f.getName().indexOf(log_name) == 0
						&& f.getName().indexOf(log_name + ".log") == -1)

				// look for most recently modified file with this name
					if (youngest == null)
						youngest = f;
					else if (youngest.lastModified() < f.lastModified())
						youngest = f;
			}
		}

		return youngest;
	}

	private <T> T getParam(HttpServletRequest req, String param, T defValue) {
		String obj = req.getParameter(param);
		T ret = null;

		// if param is null then we return the default value
		if (obj == null) return defValue;

		// if we cannot cast the object to its type then
		if (defValue instanceof Boolean) {
			return (T) Boolean.valueOf(obj);
		} else if (defValue instanceof Integer) {
			return (T) Integer.valueOf(obj);
		} else if (defValue instanceof String){
			return (T) obj;
		} else if (defValue instanceof LogName){
			return (T) LogName.valueOf(obj);
		} else if (defValue instanceof ViewMode){
			return (T) ViewMode.valueOf(obj);
		}

		return ret;
	}
}
