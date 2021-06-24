package jaxws.proxy.rpclit.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.proxy.rpclit.wsfvt.soap12rpclit.*;

/**
 * Endpoint for SOAP12 invocations
 * @wsdl proxy_soap12.wsdl 
 */

@WebService(
		serviceName = "ProxySOAP12RpcLitService",
		portName = "ProxySOAP12RpcLitPort",
		targetNamespace = "http://soap12rpclit.wsfvt.rpclit.proxy.jaxws",
		endpointInterface = "jaxws.proxy.rpclit.wsfvt.soap12rpclit.ProxySOAP12Rpc",
		wsdlLocation = "WEB-INF/wsdl/proxy_soap12rpclit.wsdl")
		
@BindingType(value=SOAPBinding.SOAP12HTTP_BINDING)
public class ProxySOAP12RpcLitPortImpl implements ProxySOAP12Rpc {

	public Message ping(Message request1, Message request2) {
		
		return request2;
	}

}
