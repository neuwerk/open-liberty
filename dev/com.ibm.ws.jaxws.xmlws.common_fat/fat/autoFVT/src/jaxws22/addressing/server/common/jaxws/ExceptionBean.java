
package jaxws22.addressing.server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2-b05-
 * Generated source version: 2.2
 * 
 */
@XmlRootElement(name = "Exception", namespace = "http://server.addressing.jaxws22/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Exception", namespace = "http://server.addressing.jaxws22/")
public class ExceptionBean {

    private String message;

    /**
     * 
     * @return
     *     returns String
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * 
     * @param message
     *     the value for the message property
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
