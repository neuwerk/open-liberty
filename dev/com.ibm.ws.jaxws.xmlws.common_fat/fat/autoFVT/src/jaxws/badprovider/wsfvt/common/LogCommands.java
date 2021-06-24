//
// @(#) 1.1 WautoFVT/src/jaxws/badprovider/wsfvt/common/LogCommands.java, WAS.websvcs.fvt, WSFP.WFVT 9/20/06 16:59:25 [9/26/06 10:27:14]
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
//

package jaxws.badprovider.wsfvt.common;

/**
 * WebSphere log file viewer - client. This class is designed to work in a
 * distributed environment and to minimize network traffic. The client will
 * invoke mark() to capture the current log position then perform some operation
 * then invoke readUpdates() which will return all new log entries and update
 * marked position. This will account for log rollover as well.
 */
public class LogCommands {
	
	// which logfile to read
	public enum LogFile {
		sysout, syserr, trace
	}

	private LogFile log = LogFile.sysout;

	private String server = "server1";

	private String path = null; // path under which the servlet is exposed

	private String host = null; // hostname that hosts the servlet

	private String port = null; // http port on the server
	
	private String logPath = null;//log file path for liberty server

	private LogCommands() {}

	public LogCommands(String host, String port, String path, LogFile log,
			String server, String logPath) {

		this.host = host;
		this.port = port;
		if (log != null) this.log = log;
		if (server != null) this.server = server;
		this.path = path;
		this.logPath = logPath;
	}		
	
	private HTTPSocketHandler getHandler(){
		HTTPSocketHandler handler = null;
		handler = new HTTPSocketHandler();
		handler.openSocket(host, Integer.parseInt(port));
		handler.setReadTimeout(Constants.SOCKET_READ_TIMEOUT);
		return handler;
	}

	/**
	 * Mark the current location in the log file
	 */
	public void mark() {
		HTTPSocketHandler handler = getHandler();
		handler.doGet(path + "?log=" + log + "&marked=true&view=none&server="
				+ server);
		handler.closeSocket();
	}

	/**
	 * Read all updates from the log since last mark()
	 * @return
	 */
	public String readUpdates() {
		HTTPSocketHandler handler = getHandler();

		String ret = handler.doGet(path + "?log=" + log
				+ "&marked=true&view=update&server=" + server+"&logPath="+logPath);
		handler.closeSocket();
		if (handler.getLastHTTPStatusCode() != 200) {
			System.out
					.println("HTTP Code = " + handler.getLastHTTPStatusCode());
			System.out
			.println("Error? = " + handler.getSocketErrorDetail());
			
			return "";
		}

		return ret;
	}

	/**
	 * Read the enture log. marking information is presernved but not used
	 * @return
	 */
	public String readAll() {
		HTTPSocketHandler handler = getHandler();
		
		String ret = handler.doGet(path + "?log=" + log
				+ "&marked=true&view=all&server=" + server);
		handler.closeSocket();
		
		if (handler.getLastHTTPStatusCode() != 200) {
			System.out
					.println("HTTP Code = " + handler.getLastHTTPStatusCode());
			return null;
		}

		return ret;
	}
}
