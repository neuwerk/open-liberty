//
// @(#) 1.1 WautoFVT/src/jaxws/provider/wsfvt/server/soap12/SOAP12SourcePortImpl.java, WAS.websvcs.fvt, WSFP.WFVT 8/30/06 17:43:50 [12/5/06 09:08:07]
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

package jaxws.provider.source.wsfvt.server;

import javax.annotation.Resource;
import javax.xml.transform.Source;
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
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace = Constants.WSDL_NAMESPACE,
		serviceName = "SOAP12SourceService",
		portName = "SOAP12SourcePort",			
		wsdlLocation = "WEB-INF/wsdl/Provider_SOAP12Source.wsdl")
public class SOAP12SourcePortImpl implements Provider<Source> {

	@Resource
	private WebServiceContext ctxt;

	public Source invoke(Source arg) {
		// read annotations
		BindingType bt =this.getClass().getAnnotation(BindingType.class);
		ServiceMode sm =this.getClass().getAnnotation(ServiceMode.class);
		
		// create a new StringProvider to which we delegate all calls
		StringProvider sp = new StringProvider(bt.value(), sm.value(), this);

		String message = Constants.toString(arg);
		String response = sp.invoke(message, ctxt);
		
		return Constants.toStreamSource(response);
	}

}
