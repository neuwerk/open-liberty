package jaxws.proxy.soap12.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.proxy.soap12.wsfvt.soap12doclit.*;

/**
 * Endpoint for SOAP12 invocations
 * @wsdl proxy_soap12.wsdl 
 */

@WebService(serviceName="ProxySOAP12DocLitWrService",
		portName="ProxySOAP12DocLitWrPort",
		targetNamespace = "http://soap12doclit.wsfvt.soap12.proxy.jaxws",
		endpointInterface = "jaxws.proxy.soap12.wsfvt.soap12doclit.ProxySOAP12Doc",
		wsdlLocation="WEB-INF/wsdl/proxy_soap12doclitwr.wsdl")
@BindingType(value=SOAPBinding.SOAP12HTTP_BINDING)
public class ProxySOAP12DocLitPortImpl implements ProxySOAP12Doc {

	public String ping(String request) {
		return request;
	}

}
