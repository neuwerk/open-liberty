package soapendpoint.wsfvt.server.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * parameter element
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Param1", namespace = "http://jaxb.server.wsfvt.soapendpoint", propOrder = { "value1" })
public class Param1 {

	String value1;

	/**
	 * get the value
	 * 
	 * @return
	 */
	public String getValue1() {
		return value1;
	}

	/**
	 * set the value
	 * 
	 * @param value1
	 */
	public void setValue1(String value1) {
		this.value1 = value1;
	}
}
