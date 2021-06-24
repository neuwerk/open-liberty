package jaxws.proxy.multi.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.BindingType;

import jaxws.proxy.multi.wsfvt.multi.*;

/**
 * Endpoint for DocLitWr multiport-service
 * @wsdl: proxy_multi.wsdl Service2/Port3 
 */
@WebService(
		serviceName = "ProxyMultiPortService2",
		portName = "MultiPort3",
		targetNamespace = "http://multi.wsfvt.multi.proxy.jaxws",
		endpointInterface = "jaxws.proxy.multi.wsfvt.multi.DocLitWrMultiPort",
		wsdlLocation = "WEB-INF/wsdl/proxy_multi.wsdl")
@BindingType(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class ProxyMultiP3PortImpl implements DocLitWrMultiPort {

	public String ping(String messageIn) {
		return this.getClass().getSimpleName();
	}

}
