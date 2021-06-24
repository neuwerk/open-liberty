// a generated, then modified bean so we can see which bean is being used. 
package jaxws.dynamicbeangen.server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getStackTraceResponse", namespace = "http://server.dynamicbeangen.jaxws/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getStackTraceResponse", namespace = "http://server.dynamicbeangen.jaxws/")
public class GetStackTraceResponse {

    @XmlElement(name = "return", namespace = "")
    private String _return="This is a modified bean packaged with the app";

    /**
     * 
     * @return
     *     returns String
     */
    public String getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    // we don't know why, but we have to set this in both places. jaxb mystery.
    public void setReturn(String _return) {
        this._return ="This is a modified bean packaged with the app";
    }

}

