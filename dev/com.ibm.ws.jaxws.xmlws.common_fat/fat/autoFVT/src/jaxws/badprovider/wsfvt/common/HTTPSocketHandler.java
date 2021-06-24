//
// @(#) 1.1 WautoFVT/src/jaxws/badprovider/wsfvt/common/HTTPSocketHandler.java, WAS.websvcs.fvt, WSFP.WFVT 9/20/06 16:59:25 [9/26/06 10:27:14]
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HTTP Managed Socket Class Enables HTTP POST & Binary communication over a
 * socket
 */
public class HTTPSocketHandler {

	enum HTTPOp {
		GET, POST
	}

	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("HTTPSocketSender Utility");
			System.out.println("Usage: <url> <file-name> <content-type>");
			System.exit(1);
		}

		HTTPSocketHandler socket;

		socket = new HTTPSocketHandler();
		socket.setReadTimeout(60 * 1000);

		// close the socket right away
		socket.setKeepAlive(false);

		// set the mandatory SOAPAction header
		socket.setHeader("SOAPAction", "\"\"");
		socket.setHeader("Content-Type", args[2]);
		socket
				.setHeader(
						"Accept",
						"text/xml, application/xop+xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");

		URL url = null;
		try {
			url = new URL(args[0]);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		socket.openSocket(url.getHost(), url.getPort());

		String message = "";
		System.out.println("Reading " + args[1]);
		try {
			BufferedReader in = new BufferedReader(new FileReader(args[1]));
			String str;
			while ((str = in.readLine()) != null) {
				message += str;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String ret = socket.doPost(url.getPath(), message);

		System.out.println("============ HTTP Request ==============");
		System.out.println(message);

		System.out.println("============ " + socket.getLastHTTPStatusString()
				+ " ============");
		System.out.println(ret);

		socket.closeSocket();
		socket = null;
	}

	// socket info
	private Socket socket = null;

	private BufferedWriter wr = null;

	private BufferedReader rd = null;

	// status holders
	private String socket_error_detail = null;

	private int http_status_code = -1;

	private String http_status_string = "";

	// option holders
	private int read_timeout = 10000;

	private boolean keep_alive = true;

	private String hostName = null;

	private Map<String, String> headers = new HashMap<String, String>();

	// debug options
	private static final boolean DEBUG = false;

	/**
	 * @param keep_alive
	 */
	public void setKeepAlive(boolean keep_alive) {
		this.keep_alive = keep_alive;
	}

	public void setReadTimeout(int timeout) {
		this.read_timeout = timeout;
	}

	public void setHeader(String header, String value) {
		this.headers.put(header, value);
	}

	/**
	 * @return
	 */
	public String getSocketErrorDetail() {
		return socket_error_detail;
	}

	/**
	 * @return
	 */
	public int getLastHTTPStatusCode() {
		return http_status_code;
	}

	/**
	 * @return
	 */
	public String getLastHTTPStatusString() {
		return http_status_string;
	}

	/**
	 * @param hostname
	 * @param port
	 * @return
	 */
	public boolean openSocket(String hostname, int port) {
		boolean ret = false;
		socket_error_detail = null;

		hostName = hostname;

		// Create a socket to the host
		InetAddress addr;
		try {
			addr = InetAddress.getByName(hostname);
			socket = new Socket(addr, port); // open a connection
			socket.setKeepAlive(this.keep_alive); // keep the connection alive
			socket.setSoTimeout(this.read_timeout); // how long to read data for

			// open read and write streams
			wr = new BufferedWriter(new OutputStreamWriter(socket
					.getOutputStream(), "UTF8"));

			rd = new BufferedReader(new InputStreamReader(socket
					.getInputStream(), "UTF8"));

			ret = true;
		} catch (Exception e) {
			socket_error_detail = e.getClass() + ": " + e.getMessage();
			ret = false;
		}

		return ret;
	}

	public String doGet(String path) {
		return invokeServer(path, null, HTTPOp.GET);
	}

	/**
	 * Do HTTP POST Request/Reply
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public String doPost(String path, String data) {
		return invokeServer(path, data, HTTPOp.POST);
	}

	private String invokeServer(String path, String data, HTTPOp method) {
		socket_error_detail = null;

		if (method == null) method = HTTPOp.GET;

		if (socket == null) {
			socket_error_detail = "Socket closed";
			return null;
		}

		String ret = null;
		String header = null;
		try {
			// Can I write data to the socket?
			if (socket.isOutputShutdown()) {
				socket_error_detail = "Socket.Write is shutdown";
				return null;
			}

			// Begin constructing HTTP Header
			if (DEBUG)
				System.out.println("##################### HTTP-Request:");

			header = method + " " + path + " HTTP/1.1" + "\r\n";
			header += "Host: " + hostName + "\r\n";
			header += "User-Agent: HTTPSockethandler" + "\r\n";

			// add custom headers
			Iterator<String> it = headers.keySet().iterator();
			while (it.hasNext()) {
				String hdr = it.next();
				header += hdr + ": " + headers.get(hdr) + "\r\n";
			}
			
			if (method == HTTPOp.POST)
				header += "Content-Length: " + data.length() + "\r\n";
			
			if (keep_alive)
				header += "Connection: Keep-Alive" + "\r\n";
			
			// empty line between header and data
			header += "\r\n";

			if (DEBUG) System.out.println(header + data);

			// Send headers
			wr.write(header);

			// send data only if POST
			if (method == HTTPOp.POST) wr.write(data);

			wr.flush();

			if (socket.isInputShutdown()) {
				socket_error_detail = "Socket.Read is shutdown";
				return null;
			}

			// Get response
			StringBuffer buf = new StringBuffer();
			String temp = null;
			boolean content = false;

			if (DEBUG)
				System.out.println("##################### HTTP-Response:");

			try {
				socket.setSoTimeout(this.read_timeout);
				while ((temp = rd.readLine()) != null) {
					if (DEBUG) System.out.println(temp);

					// capture status code & status text
					if (temp.startsWith("HTTP")) {
						http_status_string = temp;
						int s1 = temp.indexOf(' ');
						int s2 = temp.indexOf(' ', s1 + 1);

						http_status_code = Integer.parseInt(temp.substring(
								s1 + 1, s2));
					}

					// do not return header to the client
					if (content) {
						buf.append(temp);
						buf.append("\r\n");
					} else {
						// content begins when an empty line is read
						content = temp.equals("");
					}

				}
				// this exception is ok, it signifies end of data
				// read from the socket
			} catch (SocketTimeoutException e) {}

			ret = buf.toString();

		} catch (Exception e) {
			socket_error_detail = e.getClass() + ":" + e.getMessage();
			ret = null;
		}

		return ret;
	}

	/**
	 * @return
	 */
	public boolean closeSocket() {
		if (socket == null) return false;

		boolean ret = false;

		try {
			wr.close();
			rd.close();

			socket.close();

			ret = true;

		} catch (IOException e) {
			socket_error_detail = e.getClass() + ":" + e.getMessage();
			ret = false;
		}

		return ret;
	}

	/**
	 * @return
	 */
	public boolean isAvailable() {
		return (socket != null);
	}
}