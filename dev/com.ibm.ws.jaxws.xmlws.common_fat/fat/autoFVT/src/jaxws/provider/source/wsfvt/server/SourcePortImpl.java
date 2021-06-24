package jaxws.provider.source.wsfvt.server;

//import javax.annotation.Resource;
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


@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace = "http://common.wsfvt.provider.jaxws",
		serviceName = "SOAP11SourceService",
		portName = "SOAP11SourcePort",			
		wsdlLocation = "WEB-INF/wsdl/Provider_SOAP11Source.wsdl")
public class SourcePortImpl implements Provider<Source> {

	//@Resource
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
