//
// @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tools/AppConst.java, WAS.websvcs.fvt, WASX.FVT 11/21/08 12:28:59 [12/16/08 14:37:33]
//
// IBM Confidential OCO Source Material
// 5724-i63 (C) COPYRIGHT International Business Machines Corp. 2002, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Date        Feature/Defect       Author           Description
// ----------  -----------------    ---------        ----------------------------
// 11/21/2002  LIDB1269.521.1       ulbricht         Add Default Server Name
// 12/02/2003  D156003              ulbricht         Add Simple Client Jar
// 03/12/2003  LIDB1270.256.11      ulbricht         Add IBM Http Server Default Location
// 04/04/2003  LIDB1738.25.01       ulbricht         Add line separator constant
// 05/05/2003  D161842              ulbricht         Samples changed location for ptf
// 06/24/2003  D170481              ulbricht         Samples changed location for ASV51X
// 10/19/2003  D179331.2            ulbricht         Allow server side trace to be enabled
// 10/30/2003  D181424              ulbricht         Add constants for future z/OS changes
// 12/01/2003  D158794.1            ulbricht         Add client trace variables
// 01/19/2004  D187625              ulbricht         Change StockQuote Sample URL
// 04/01/2004  D196864              ulbricht         Add WAS.node for MultiProtocol tests
// 04/16/2004  D198902              ulbricht         Location of IHS changed for v6.0
// 06/18/2004  D210605              ulbricht         Add proxy server constants
// 06/24/2004  D211929              ulbricht         Product file name changes
// 07/09/2004  D215233              ulbricht         Add constant for profile name
// 07/20/2004  D217923              ulbricht         Change Node name to Cell name
// 08/09/2004  D222747              ulbricht         Add default z/OS encoding
// 12/02/2004  D246124              ulbricht         Add build error log file
// 02/25/2005  D258191              ulbricht         Break log file names down
// 03/11/2005  D261009              ulbricht         Don't default profile name
// 03/24/2005  D268461              ulbricht         Get server name from sys props
// 04/22/2005  D269183.2            ulbricht         Allow profile to be set
// 06/14/2005  D282781              ulbricht         Change log dirs
// 07/29/2005  294431               ulbricht         Add rmi port constant
// 08/02/2006  381622               smithd           Add ND support
// 10/23/2006  395172               smithd           Improve usability in Cell
// 10/30/2006  399149               btiffany         Set fvt_home from environment if not set as property.
//                                                     That lets us run from eclipse or command line.
// 11/02/2006  400682               jramos           FVT_HOSTNAME is now set using the new QueryOSUtil.getLocalHostname methods
// 01/08/2007  413702               smithd           Allow fvtHostname to be specified
// 02/09/2007  419306               btiffany         Infer fvt_home from environment's FVT_TOP if property not available.
// 03/16/2007  426986               jramos           Add properties for no execution log
// 04/09/2007  431249               jramos           Add constant for startApps.log
// 05/24/2007  440922               jramos           Changes for Pyxis
// 11/21/2008  566458               jramos           Update log locations

package com.ibm.ws.wsfvt.build.tools;

import java.io.File;

/**
 * This class contains constants that are useful for all of the tests.
 */
public class AppConst {

	// if we are running outside of the framework, read FVT_HOME from
	// the environment.
	static {
		String fvh = System.getProperty("FVT.base.dir");
		if (fvh == null) {
			fvh = System.getenv("FVT_TOP") + "/autoFVT";
		}
		FVT_HOME = fvh;
	}

	public static final String FVT_HOME;
	/**
	 * The location of the FVT build/work directory
	 */
	public static final String FVT_BUILD_WORK_DIR = AppConst.FVT_HOME + "/build/work";
	
	// Log Files
	public static final String logFile = "results" + File.separator + "junit" + File.separator + "fvt.log";
	public static final String errorFile = "results" + File.separator + "junit" + File.separator + "error.log";
	
	// Misc
	/**
	 * This is the local WAS install which the automation requires to
	 * exist. WARNING!!!! This should not be used if looking for the
	 * WAS_HOME of a server in the Cell
	 */
	public static final String WAS_HOME = System.getProperty("WAS.base.dir");

	public static final String BLD_ERR_LOG_NAME = "build-error.log";
	public static final String BLD_ERR_LOG = FVT_HOME + File.separator + "results" + File.separator + "junit" + File.separator + BLD_ERR_LOG_NAME;

	public static final String INSTALL_ERR_LOG_NAME = "install-error.log";
	public static final String INSTALL_ERR_LOG = FVT_HOME + File.separator + "results" + File.separator + "junit" + File.separator + INSTALL_ERR_LOG_NAME;

	public static final String UNINSTALL_ERR_LOG_NAME = "uninstall-error.log";
	public static final String UNINSTALL_ERR_LOG = FVT_HOME + File.separator + "results" + File.separator + "junit" + File.separator + UNINSTALL_ERR_LOG_NAME;

	public static final String NO_EXECUTION_LOG_NAME = "not-executed.log";
	public static final String NO_EXECUTION_LOG = FVT_HOME + File.separator + "results" + File.separator + "junit" + File.separator + NO_EXECUTION_LOG_NAME;
    
    public static final String TIMED_WAS_RESTART_LOG_NAME = "timedRestart.log";
    public static final String TIMED_WAS_RESTART_LOG = FVT_HOME + File.separator + "results" + File.separator + "junit" + File.separator + TIMED_WAS_RESTART_LOG_NAME;

	public static final String DEFAULT_ENCODING = "ISO8859-1";
	public static final String DEFAULT_ZOS_ENCODING = "cp1047";

	public static final String DEFAULT_TICKER_SYMBOL = "XXX";
	
	// Samples
	public static final String STOCKQUOTE_SERVICE = "/StockQuote/services/xmltoday-delayed-quotes";

	public static final boolean SERVER_TRACE_ENABLED = new Boolean(System.getProperty(
			"server.trace", "false")).booleanValue();
	public static final boolean CLIENT_TRACE_ENABLED = new Boolean(System.getProperty(
			"client.trace", "false")).booleanValue();
	public static String SERVER_TRACE_PKG_NAME = System.getProperty("server.trace.package.name",
			"com.ibm.ws.webservices.*");
	public static String CLIENT_TRACE_PKG_NAME = System.getProperty("client.trace.package.name",
			"com.ibm.ws.webservices.*");

	public static final String HTTP_PROXY_HOST = System.getProperty("http.proxyHost");
	public static final String HTTP_PROXY_PORT = System.getProperty("http.proxyPort");
}
