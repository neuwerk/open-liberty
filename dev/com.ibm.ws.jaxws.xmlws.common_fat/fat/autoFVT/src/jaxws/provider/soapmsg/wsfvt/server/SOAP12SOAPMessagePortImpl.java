//
// @(#) 1.2 autoFVT/src/jaxws/provider/soapmsg/wsfvt/server/SOAP12SOAPMessagePortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/5/06 12:10:32 [8/8/12 06:55:10]
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
// 12/05/06 sedov       408880          Refactored to new package

package jaxws.provider.soapmsg.wsfvt.server;

import javax.annotation.Resource;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.provider.common.Constants;
import jaxws.provider.common.StringProvider;


@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider(
		targetNamespace = Constants.WSDL_NAMESPACE,
		serviceName = "SOAP12SOAPMessageService",
		portName = "SOAP12SOAPMessagePort",	
		wsdlLocation = "WEB-INF/wsdl/Provider_SOAP12SOAPMessage.wsdl")
public class SOAP12SOAPMessagePortImpl implements Provider<SOAPMessage> {

	// request for the context to be injected
	@Resource
	private WebServiceContext ctxt;

	public SOAPMessage invoke(SOAPMessage arg) {
		// read annotations
		BindingType bt =this.getClass().getAnnotation(BindingType.class);
		ServiceMode sm =this.getClass().getAnnotation(ServiceMode.class);
		
		// create a new StringProvider to which we delegate all calls
		StringProvider sp = new StringProvider(bt.value(), sm.value(), this);
		
		// invoke the provider
		String message = Constants.toString(arg);
		String response = sp.invoke(message, ctxt);
		
		return Constants.toSOAPMessage(response);
	}
}
