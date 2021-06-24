//
// @(#) 1.1 autoFVT/src/jaxws/badprovider/wsfvt/server/PingProvider.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/20/06 16:59:32 [8/8/12 06:55:21]
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

package jaxws.badprovider.wsfvt.server;

import javax.activation.DataSource;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;

import jaxws.badprovider.wsfvt.common.Constants;


public class PingProvider {

	public static Object invoke(Object arg0) {
		String response = Constants.TWOWAY_MSG_RESPONSE;
		return Constants.toStreamSource(response);
	}

	public static Source invoke(Source arg0) {
		String response = Constants.TWOWAY_MSG_RESPONSE;
		return Constants.toStreamSource(response);
	}	
	
	public static String invoke(String arg) {

		return Constants.TWOWAY_MSG_RESPONSE;
	}
	
	public static DataSource invoke(DataSource arg) {

		return null;
	}	
	
	public static SOAPMessage invoke(SOAPMessage arg) {

		return Constants.toSOAPMessage(Constants.TWOWAY_MSG_RESPONSE);
	}

}
