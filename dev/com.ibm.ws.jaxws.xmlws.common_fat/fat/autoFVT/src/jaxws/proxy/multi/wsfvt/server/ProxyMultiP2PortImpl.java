package jaxws.proxy.multi.wsfvt.server;

import javax.jws.WebService;

import jaxws.proxy.multi.wsfvt.multi.*;

/**
 * Endpoint for RpcLit multiport-service
 * @wsdl: proxy_multi.wsdl Service1/Port2  
 */
@WebService(
		serviceName = "ProxyMultiPortService1",
		portName = "MultiPort2",
		targetNamespace = "http://multi.wsfvt.multi.proxy.jaxws",
		endpointInterface = "jaxws.proxy.multi.wsfvt.multi.DocLitBarePort",
		wsdlLocation = "WEB-INF/wsdl/proxy_multi.wsdl")
public class ProxyMultiP2PortImpl implements DocLitBarePort {

	public PingResponse barePing(Ping request) {
		PingResponse resp = new PingResponse();
		resp.setMessageOut(this.getClass().getSimpleName());
		return resp;
	}

}
