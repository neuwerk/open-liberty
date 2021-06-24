
package jaxws.attachment.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getCheckAttachmentServicePortResponse", namespace = "http://server.attachment.jaxws")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCheckAttachmentServicePortResponse", namespace = "http://server.attachment.jaxws")
public class GetCheckAttachmentServicePortResponse {

    @XmlElement(name = "return", namespace = "")
    private CheckAttachmentPortType _return;

    /**
     * 
     * @return
     *     returns CheckAttachmentPortType
     */
    public CheckAttachmentPortType get_return() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void set_return(CheckAttachmentPortType _return) {
        this._return = _return;
    }

}
