package jaxws.proxy.multi.wsfvt.server;

import javax.jws.WebService;

import jaxws.proxy.multi.wsfvt.multi.*;

/**
 * Endpoint for DocLitWr multiport-service
 * @wsdl: proxy_multi.wsdl ProxyMultiPortService1/MultiPort1  
 */
@WebService(
		serviceName = "ProxyMultiPortService1",
		portName = "MultiPort1",
		targetNamespace = "http://multi.wsfvt.multi.proxy.jaxws",
		endpointInterface = "jaxws.proxy.multi.wsfvt.multi.DocLitWrMultiPort",
		wsdlLocation = "WEB-INF/wsdl/proxy_multi.wsdl")
public class ProxyMultiP1PortImpl implements DocLitWrMultiPort {

	public String ping(String messageIn) {

		return this.getClass().getSimpleName();
	}

}
