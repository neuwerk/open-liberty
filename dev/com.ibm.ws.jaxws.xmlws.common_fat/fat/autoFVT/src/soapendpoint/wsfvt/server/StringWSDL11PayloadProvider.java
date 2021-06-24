package soapendpoint.wsfvt.server;

import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.transform.dom.DOMSource;

/**
 * Provider with PAYLOAD Mode on Server, will receive soap11 and soap12
 * requests.
 * This has a WSDL 1.1 specified in its <code>@WebServiceProvider</code> annotation.
 */
@WebServiceProvider(targetNamespace = "http://ws.apache.org/axis2", serviceName = "StringWSDL11PayloadProviderService", portName = "StringWSDL11PayloadProviderServicePort", wsdlLocation = "WEB-INF/wsdl/StringProvider.wsdl")
@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class StringWSDL11PayloadProvider implements Provider<DOMSource> {

	public DOMSource invoke(DOMSource obj) {
		if (obj == null) {
			return null;
		}
		String requestStr = XMLUtils.getContentFromDOMSource(obj);
		if (requestStr == null || requestStr.isEmpty()) {
			return obj;
		}
		
		if (requestStr.contains("<invokeOp>soap11 request</invokeOp>")) {
			return XMLUtils.createDOMSourceFromString("<invoke>String Provider SOAP 1.1 message received</invoke>");
		} else if (requestStr.contains("<invokeOp>soap12 request</invokeOp>")) {
			return XMLUtils.createDOMSourceFromString("<invoke>String Provider SOAP 1.2 message received</invoke>");
		}
		return obj;
	}
}
