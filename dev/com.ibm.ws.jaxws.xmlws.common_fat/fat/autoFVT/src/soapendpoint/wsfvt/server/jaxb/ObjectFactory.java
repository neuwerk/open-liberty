package soapendpoint.wsfvt.server.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * Simple twoWay response.
 */
@XmlRegistry
public class ObjectFactory {
	private final static QName RESPONSE_QNAME = new QName(
			"http://jaxb.server.wsfvt.soapendpoint", "twoWay");

	/**
	 * create param1
	 * 
	 * @return
	 */
	public Param1 createParam1() {
		return new Param1();
	}

	/**
	 * create the response based off param1
	 * 
	 * @param value
	 * @return
	 */
	@XmlElementDecl(namespace = "http://jaxb.server.wsfvt.soapendpoint", name = "twoWay")
	public JAXBElement<Param1> createResponse(Param1 value) {
		return new JAXBElement<Param1>(RESPONSE_QNAME, Param1.class, null,
				value);
	}
}
