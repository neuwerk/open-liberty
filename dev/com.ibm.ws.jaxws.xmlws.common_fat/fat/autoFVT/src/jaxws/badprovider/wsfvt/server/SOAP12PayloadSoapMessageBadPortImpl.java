//
// @(#) 1.1 autoFVT/src/jaxws/badprovider/wsfvt/server/SOAP12PayloadSoapMessageBadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/20/06 16:59:36 [8/8/12 06:55:23]
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

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import jaxws.badprovider.wsfvt.common.Constants;

/**
 * Bad provider test - SOAP12/Paylaod SOAPMessage, invalid combination
 *
 */
@BindingType(value=javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace=Constants.WSDL_NAMESPACE,
		serviceName="SOAP12PayloadSoapMessageService",
		portName="SOAP12PayloadSoapMessagePort",
		wsdlLocation="WEB-INF/wsdl/SOAP12PayloadSoapMessage.wsdl")
public class SOAP12PayloadSoapMessageBadPortImpl implements Provider<SOAPMessage>{

	public SOAPMessage invoke(SOAPMessage arg0) {
		return PingProvider.invoke(arg0);
	}

	

}
