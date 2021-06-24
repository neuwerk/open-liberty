//
// @(#) 1.1 autoFVT/src/jaxws/provider/rpclit/wsfvt/server/RpcLitPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/30/06 17:43:41 [8/8/12 06:55:10]
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
// 08/31/06 sedov       LIDB3296-42.03  Beta drop

package jaxws.provider.wsfvt.server.soap11;

import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.provider.wsfvt.common.Constants;

@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace = "http://rpclit.common.wsfvt.provider.jaxws",
		serviceName = "SOAP11RpcLitService",
		portName = "SOAP11RpcLitPort",			
		wsdlLocation = "WEB-INF/wsdl/Provider_RpcLit.wsdl")
public class RpcLitPortImpl implements Provider<Source> {

	private static final String ADDNUMBERS_RESPONSE =
	    "<ans:addNumbersResponse xmlns:ans=\"http://rpclit.common.wsfvt.provider.jaxws\">"
        + "<result><value>10</value></result>"
        + "</ans:addNumbersResponse>";
	
	private static final String ADDNUMEBRS_NULL_RESPONSE =
		"<ans:addNumbersResponse xmlns:ans=\"http://rpclit.common.wsfvt.provider.jaxws\">"
        + "<result xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'></result>"
        + "</ans:addNumbersResponse>";
	
	public Source invoke(Source arg) {

		String request = Constants.toString(arg);
		String ret = "";
		
		if (request.indexOf("addNumbers") != -1){
			System.out.println("SOAP11RpcLitPortImpl operation=addNumbers");
			
			// if the requester requests that we send back a null
			// as part of Assertion 3.22 test then send it back
			// the null response otherwise return a normal response
			if (request.indexOf(Constants.THE_RETURN_NULL_INT) == -1)
				ret = ADDNUMBERS_RESPONSE;
			else
				ret = ADDNUMEBRS_NULL_RESPONSE;
			
		} else if (request.indexOf("addStrings") != -1){
			System.out.println("SOAP11RpcLitPortImpl operation=addStrings");
			throw new WebServiceException("addStrings reached endpoint");
			
		} else if (request.indexOf("addFloats") != -1){
			System.out.println("SOAP11RpcLitPortImpl operation=addFloats");
			throw new WebServiceException("addFloats reached endpoint");
			
		} else if (request.indexOf("thisOperationIsNotInWsdl") != -1){
			System.out.println("SOAP11RpcLitPortImpl operation=thisOperationIsNotInWsdl");
			throw new WebServiceException("thisOperationIsNotInWsdl reached endpoint");
			
		} else {
			System.out.println("SOAP11RpcLitPortImpl operation=unknown");
			throw new WebServiceException("Unknown Operation");
		}
		
		return Constants.toStreamSource(ret);
	}
}
