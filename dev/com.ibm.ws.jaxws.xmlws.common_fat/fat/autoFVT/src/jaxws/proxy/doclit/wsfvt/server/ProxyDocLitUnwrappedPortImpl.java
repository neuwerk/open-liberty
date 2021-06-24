package jaxws.proxy.doclit.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import jaxws.proxy.common.Constants;

import jaxws.proxy.doclit.wsfvt.doclitunwrapped.DocLitUnwrappedProxy;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.*;

/**
 * Endpoint for Document/literal-UNwrapped invocations. Will roundtrip messages
 * when appropriate. Will throw an exception if request to by the client.
 * Namespaces match 
 * @wsdl proxy_doclit_unwr.wsdl + poroxy_unwrapped.xml 
 */
@WebService(
		serviceName="ProxyDocLitUnwrappedService",
		portName="ProxyDocLitWrappedPort",
		targetNamespace = "http://doclitwrapped.wsfvt.doclit.proxy.jaxws",
		wsdlLocation="WEB-INF/wsdl/proxy_doclit_unwr.wsdl",	
		endpointInterface = "jaxws.proxy.doclit.wsfvt.doclitunwrapped.DocLitUnwrappedProxy")
public class ProxyDocLitUnwrappedPortImpl implements DocLitUnwrappedProxy {

	public void oneWayVoid(OneWayVoid allByMyself) {
		// do nothing
	}

	public void oneWay(OneWay allByMyself) {
		//do nothing
	}

	public void twoWayHolder(Holder<TwoWayHolder> allByMyself) {
		// do nothing, just rounftrip the message
	}

	public ReturnType twoWay(TwoWay allByMyself) {
		ReturnType rt = new ReturnType();
		
		if (allByMyself.getTwowayStr().equals(Constants.THE_ID_STRING)){		
			rt.setReturnStr(Constants.REPLY_UNWRAPPED);
		}
		else{
			rt.setReturnStr(allByMyself.getTwowayStr());
		}
		
		return rt;
	}


}
