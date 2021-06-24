package jaxws.proxy.doclit.wsfvt.server;

import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.transform.dom.DOMSource;
import jaxws.proxy.common.Constants;

@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
@WebServiceProvider(
		targetNamespace = "http://doclitwrapped.wsfvt.doclit.proxy.jaxws",
		serviceName = "ProviderDocLitWrappedService",
		portName = "ProviderDocLitWrappedPort",		
		wsdlLocation="WEB-INF/wsdl/provider_doclitwr.wsdl")
public class DocLitWrappedProviderPortImpl implements Provider<DOMSource>{

	private static final String NS = "http://doclitwrapped.wsfvt.doclit.proxy.jaxws";
	
	public DOMSource invoke(DOMSource arg0) {
		
		String input = XMLUtils.getContentFromDOMSource(arg0);
		
		String ret = null;
		if (input.indexOf("oneWayVoid") != -1) {
			ret = input;
		} else if ( input.indexOf("oneWay") != -1){
			ret = input;
		} else if (input.indexOf("twoWay") != -1){
			ret = "<ns1:ReturnType xmlns:ns1=\"" + NS + "\"><return_str>" + Constants.THE_STRING + "</return_str></ns1:ReturnType>";
		} else if (input.indexOf("twoWayHolder") != -1){
			ret = input;
		} else if (input.indexOf("twoWayInOut") != -1){
			ret = "<ns1:twoWayInOutResponse xmlns:ns1=\"" + NS + "\"><return_str>" + Constants.THE_STRING + "</return_str></ns1:twoWayInOutResponse>";
		} else if (input.indexOf("twoWaySilly") != -1){
			return XMLUtils.createDOMSourceFromString(input);
		} else {
			return null;
		}
		
		return XMLUtils.createDOMSourceFromString(ret);
	}

}
