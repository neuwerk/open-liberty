package jaxws.badprovider.wsfvt.server;

import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.badprovider.wsfvt.common.Constants;

/**
 * Bad provider test - endpoint carries WSP but doesn't implement a provider
 *
 */
@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace=Constants.WSDL_NAMESPACE,
		serviceName="NonProviderWSPService",
		portName="NonProviderWSPPort",
		wsdlLocation="WEB-INF/wsdl/NonProviderWSP.wsdl")
public class NonProviderWSPBadPortImpl {

	public Source invoke(Source arg) {
		return PingProvider.invoke(arg);
	}
}
