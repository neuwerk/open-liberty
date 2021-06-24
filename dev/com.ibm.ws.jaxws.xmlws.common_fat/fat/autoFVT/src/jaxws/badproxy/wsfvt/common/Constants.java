//
// @(#) 1.5 WautoFVT/src/jaxws/badproxy/wsfvt/common/Constants.java, WAS.websvcs.fvt, WSFP.WFVT, a0706.23 1/30/07 11:56:34 [2/16/07 17:57:09]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect             Description
// ----------------------------------------------------------------------------
// 05/31/06   sedov       LIDB3296.41-02     New File
// 11/15/06   sedov       D404343            Removed dependency on AdminCommandFactory
// 01/30/07   sedov       D417716            Added new tests

package jaxws.badproxy.wsfvt.common;


public class Constants {

	// substitution variables
	public static final String WAS_HOST_NAME = resolve("@HOST@", "localhost");
	public static final String WAS_HTTP_PORT = resolve("@HTTP_PORT@", "9080");
	public static final String WAS_SOAP_PORT = resolve("@SOAP_PORT@", "8880");
	public static final String WAS_SERVER_NAME = resolve("@SERVER_NAME@", "server1");
	
	public static final String SSL_CERT_PATH = "@SEC_SSL_CERT_PATH@";
	public static final String SEC_USERNAME = "@SEC_USERNAME@";
	public static final String SEC_PASSWORD = "@SEC_PASSWORD@";
	public static final String SEC_ENABLED = resolve("@SEC_ENABLED@", "false");
	
	private static final String SERVER = WAS_HOST_NAME + ":" + WAS_HTTP_PORT;
	
	// Bad providers
	public static final String MISMATCHED_SOAP11_NAME = "MismatchedSOAP11";
	public static final String MISMATCHED_SOAP12_NAME = "MismatchedSOAP12";
	
	public static final String VALIDATE_SOAP12MTOM_NAME = "MTOMSoap12";
	public static final String VALIDATE_SOAP12NoBT_NAME = "NoBTSoap12";
	
	public static final String MISMATCHED_WSDL_NAME = "MismatchedWsdl";
	public static final String MISMATCHED_SEI_NAME = "MismatchedSEI";
	
	public static final String NOWSDL_SOAP11_NAME = "NoWsdlSOAP11";
	public static final String NOWSDL_SOAP12_NAME = "NoWsdlSOAP12";
	public static final String NOWSDL_HTTP_NAME = "NoWsdlHTTP";
	
	public static final int SOCKET_READ_TIMEOUT = 15000;
	
	public static String getAddress(String appName){
		return "http://" + SERVER + "/jaxws.badproxy." + appName + "/services/" + appName + "Service";
	}

	private static String resolve(String actual, String def) {
		if (actual.indexOf('@') == -1){
			return actual;
		}else{
			return def;
		}
	}	
	
}
