package soapendpoint.wsfvt.server;

import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.soap.SOAPMessage;

/**
 * Provider that has no WSDL and uses a SoapMessage. This Provider returns the
 * expected SOAP version back.
 */
@WebServiceProvider(targetNamespace = "http://www.ibm.com/websphere/sibx/ServiceGateway/Binding", serviceName = "BothSOAPVerisonsExport_ServiceGatewayHttpService", portName = "BothSOAPVerisonsExport_ServiceGatewayHttpPort")
@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class OMElementProvider implements Provider<SOAPMessage> {

	public SOAPMessage invoke(SOAPMessage arg0) {

		/*
		 * <?xml version="1.0" encoding="utf-8"?><soapenv:Envelope
		 * xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope"
		 * xmlns:env="http://www.w3.org/2003/05/soap-envelope"><soapenv:Body><invokeOp>Hello
		 * World</invokeOp></soapenv:Body></soapenv:Envelope>	zxz
		 */
		System.out.println("Invoking me");
		System.out.println(arg0);
		System.out.println(arg0.getClass().getName());
		/*
		OMElement firstChild = arg0.getFirstElement();
		while (firstChild.getFirstElement() != null) {
			firstChild = firstChild.getFirstElement();
		}
		firstChild.setText("Goodbye!");
		*/
		System.out.println(arg0);
		return arg0;
	}
}
