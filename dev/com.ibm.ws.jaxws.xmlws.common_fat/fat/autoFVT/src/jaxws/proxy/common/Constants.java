//
// @(#) 1.15 autoFVT/src/jaxws/proxy/common/Constants.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 2/5/07 09:30:24 [8/8/12 06:55:12]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect           Description
// ----------------------------------------------------------------------------
// 05/31/06 sedov       LIDB3296.42-01   New File
// 09/11/06 sedov       389840           Added reference for wsdl and java types
// 12/04/06 sedov       408880           Added exception stack checking routine
// 01/23/07 sedov       415799           Added constant for fault detail
// 02/05/07 sedov       418799           Added SERVER default value
//
package jaxws.proxy.common;

import javax.xml.namespace.QName;

/** 
 * Constants used by tests and servers
 */
public class Constants {

	public static final boolean DEBUG = false;

	public static final String SERVER = getServer("@HOST@:@PORT@");
	
	// some messages that can be sent
	public static final String THE_STRING = "TheString";
	public static final String THE_ID_STRING = "#idYourSelf#";
	public static final int THE_INT = 41;
	
	/** requests for specific exceptions to be trown **/
	// a wsdl:fault is thrown
	public static final String THE_FAULT_STRING = "PleaseThrowAFault";
	
	// name of the detail for SOAPFaultTests
	public static final String THE_DETAIL_NAME = "testFaultDetail";
	
	// a WebServiceException is thrown
	public static final String THE_WSE_STRING = "PleaseThrowAWSE";
	
	// for polymorphic exception bean, the base fault is thrown
	public static final String THE_BASE_FAULT = "PleaseThrowABaseFault";
	
	// for polymorphic exception bean, the extending fault is thrown
	public static final String THE_POLYMORPHIC_FAULT = "PleaseThrowAPolymorphicFault";
	
	// throw a wsdl:fault with faultInfo set to null
	public static final String THE_NULL_FAULTINFO_FAULT = "NullFaultInfo";
	
	// throow a WebServiceException with the wsdl:fault as cause
	public static final String THE_WSE_WITH_SPECIFIC_FAULT = "WSEWithSpecificFault";

	// throw an unexpec
	public static final String THE_UNEXPECTED_EXCEPTION = "UnexpectedException";
	
	// request to make one of the return parameters null
	public static final String THE_RETURN_NULL_STRING = "ReturnANull";	
	
	// soap envelope versions
	public static final String SOAP11_ENVELOPE = "http://www.w3.org/2003/05/soap-envelope";
	public static final String SOAP12_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
	
	// replies for when endpoint is asked to idYourSelf
	public static final String REPLY_WRAPPED = "DocLitWrappedEndpoint";
	public static final String REPLY_UNWRAPPED = "DocLitUnwrappedEndpoint";
		
	// service and port QNames
	public static final QName DOCLITWR_PORT = new QName("http://doclitwrapped.common.wsfvt.proxy.jaxws", "ProxyDocLitWrappedPort");
	public static final QName DOCLITWR_SERVICE = new QName("http://doclitwrapped.common.wsfvt.proxy.jaxws", "ProxyDocLitWrappedService");
	public static final QName DOCLITUNWRAPPED_SERVICE = new QName("http://doclitwrapped.common.wsfvt.proxy.jaxws", "ProxyDocLitUnwrappedService");
	
	public static final int CLIENT_MAX_SLEEP_SEC = 60;

	// boolean to enable getCuase checking for RunTimeException tests
	public static final boolean TYPE_CHECKING_ENABLED = true;
	
	// base endpoints
	public static final String DOCLIT_BASE = "http://" + Constants.SERVER + "/jwpr.doclit";
	public static final String JAVATYPES_BASE = "http://" + Constants.SERVER + "/jwpr.javatypes";
	public static final String MSGCTXT_BASE = "http://" + Constants.SERVER + "/jwpr.msgctxt";
	public static final String MULTI_BASE = "http://" + Constants.SERVER + "/jwpr.multi";
	public static final String RPCLIT_BASE = "http://" + Constants.SERVER + "/jwpr.rpclit";
	public static final String SOAP12_BASE = "http://" + Constants.SERVER + "/jwpr.soap12";
	public static final String WSDLTYPES_BASE = "http://" + Constants.SERVER + "/jwpr.wsdltypes";

	/**
	 * Wrapper code in case we are running in eclipse
	 * @param server
	 * @return
	 */
	private static String getServer(String server) {
		if (server.indexOf('@') != -1)
			server="localhost:8080";
		
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
}
