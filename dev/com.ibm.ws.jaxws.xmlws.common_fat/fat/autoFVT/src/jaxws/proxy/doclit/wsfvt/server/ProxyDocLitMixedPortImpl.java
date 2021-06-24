package jaxws.proxy.doclit.wsfvt.server;

import javax.jws.WebService;

import jaxws.proxy.common.Constants;

import jaxws.proxy.doclit.wsfvt.doclitmixed.*;

/**
 * Endpoint for mixed doc/lit - doc/lit-wrapped invocations. Will roundtrip messages
 * when appropriate. Will throw an exception if request to by the client.
 * @wsdl proxy_doclitmixed.wsdl 
 */
@WebService(
		serviceName = "ProxyDocLitMixedService",
		portName = "ProxyDocLitMixedPort",
		targetNamespace = "http://doclitmixed.wsfvt.doclit.proxy.jaxws",
		endpointInterface = "jaxws.proxy.doclit.wsfvt.doclitmixed.DocLitMixedProxy",
		wsdlLocation = "WEB-INF/wsdl/proxy_doclitmixed.wsdl")
public class ProxyDocLitMixedPortImpl implements DocLitMixedProxy {

	public String twoWay(String twowayStr) {
		
		return twowayStr;
	}

	public YetAnotherReturnType another(AnotherReturnType allByMyself) {
		YetAnotherReturnType yart = new YetAnotherReturnType();
		yart.setReturnStr(Constants.THE_INT);
		return yart;
	}

    public YetAnotherReturnType twoWayEmpty(){
    	YetAnotherReturnType ret = new YetAnotherReturnType();
    	ret.setReturnStr(Constants.THE_INT);
    	return ret;
    }
}
