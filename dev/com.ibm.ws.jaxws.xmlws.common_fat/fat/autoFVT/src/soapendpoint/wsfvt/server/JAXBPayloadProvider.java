package soapendpoint.wsfvt.server;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;

import javax.xml.ws.soap.SOAPBinding;

import soapendpoint.wsfvt.server.jaxb.ObjectFactory;
import soapendpoint.wsfvt.server.jaxb.Param1;

/**
 * Provider that returns a <code>JAXBSource</code> using the
 * Constants.SOAP_HTTP_BINDING.
 */
@WebServiceProvider()
@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class JAXBPayloadProvider implements Provider<Source> {

	public Source invoke(Source arg0) {
		try {
			JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);

			// create reply message
			ObjectFactory fac = new ObjectFactory();
			Param1 p1 = fac.createParam1();
			p1.setValue1("hello world");

			return new JAXBSource(jc, fac.createResponse(p1));
		} catch (Exception e) {
			throw new WebServiceException(e);
		}
	}

}
