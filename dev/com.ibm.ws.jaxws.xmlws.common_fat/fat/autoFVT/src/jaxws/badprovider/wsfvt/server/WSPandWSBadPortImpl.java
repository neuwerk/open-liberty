package jaxws.badprovider.wsfvt.server;

import javax.jws.WebService;
import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.badprovider.wsfvt.common.Constants;

/**
 * Bad provider test - endpoint carries both WSP and WS annotatins
 *
 */
@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace=Constants.WSDL_NAMESPACE,
		serviceName="InvalidBindingService",
		portName="InvalidBindingPort",
		wsdlLocation="WEB-INF/wsdl/WSPandWS.wsdl")
@WebService
public class WSPandWSBadPortImpl implements Provider<Source>{

	public Source invoke(Source arg) {
		return PingProvider.invoke(arg);
	}

}
