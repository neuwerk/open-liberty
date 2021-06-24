//
// @(#) 1.15 autoFVT/src/jaxws/async/wsfvt/common/Constants.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/24/09 17:10:58 [8/8/12 06:55:19]
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
// 09/18/06 sedov       LIDB3296.38     New File
// 12/20/06 sedov       409973          Added stack walking code
// 02/05/07 sedov       418799          Added SERVER default value
// 05/15/07 sedov       439294          Increased isAsleep polling interval & timeout
// 10/17/07 sedov       472557          Added TcpMon port
//
package jaxws.async.wsfvt.common;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 * This class holds constant strings such as endpoint addresses and common
 * conversion methods
 */
public class Constants {

	// server hostName and WC port
	private static final String SERVER = getServer("@HOST@:@PORT@");
	
	/**
	 * Port on which TCPMonitor should listen on
	 */
	public static final int TCPMON_LISTENER_PORT = com.ibm.ws.wsfvt.build.tools.tcpmon.TCPMonEvenListener.TCPMON_LISTENER_PORT;
	
	public static final String WSDL_NAMESPACE = "http://common.wsfvt.async.jaxws";

	public static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope";

	public static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";

	public static final QName SERVICE_QNAME = new QName(WSDL_NAMESPACE,
			"AsyncService");

	public static final QName PORT_QNAME = new QName(WSDL_NAMESPACE,
			"AsyncPort");

	// Endpoint addresses
	public static final String BASE_ENDPOINT = "http://" + SERVER
			+ "/jw.async/services/";

	public static final String DOCLITWR_ASYNC_ENDPOINT = BASE_ENDPOINT
			+ "AsyncDocLitWrappedService";

	// how long the server should seep for before returning a response
	public static final long SERVER_SLEEP_SEC = 120;

	// maximum amount of time to wait for async operation to complete
	public static final int CLIENT_MAX_SLEEP_SEC = 120;

	// maximum amount of time to wait for async operation to complete
	public static final int CLIENT_SHORT_SLEEP_SEC = 15;

	// maximum number of times the client should check to see if
	// the server received sleep request
	public static final int MAX_ISASLEEP_CHECK = 30; // 10*30 = 5 min

	// number of sec to sleep in between isAsleep checks
	public static final int SLEEP_ISASLEEP_SEC = 10; //10 sec

	/**
	 * Wrapper code in case we are running in eclipse
	 * @param server
	 * @return
	 */
	private static String getServer(String server) {
		if (server.indexOf('@') != -1)
			server="localhost:9080";
		
		System.out.println("Constants.getServer=" + server);
		
		return server;
	}	
	
	/**
	 * Determine if the stacktrace of t (or t itself) contains an instance of find
	 * @param t
	 * @param find
	 * @return
	 */
	public static boolean checkStack(Throwable t, Class find){		
		Throwable cur = t;
		boolean found = false;
		do {
			found = cur.getClass().isAssignableFrom(find);
			cur = cur.getCause();
		} while (!found && cur != null);
		
		return found;
	}
	
	/**
	 * Log just the exception names in the stack trace. useful for exception checking tests
	 * @param t
	 */
	public static void logStack(Throwable t){
		Throwable cur = t;
		do {
			System.out.println(cur.getClass().getName() + " MSG=" + cur.getMessage());
			cur = cur.getCause();
		} while (cur != null);
	}
	
	public static void setSoapAction(Object port, String action){
		Map<String, Object> rc = ((BindingProvider) port).getRequestContext();
		rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
		rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, action);
	}	
}
