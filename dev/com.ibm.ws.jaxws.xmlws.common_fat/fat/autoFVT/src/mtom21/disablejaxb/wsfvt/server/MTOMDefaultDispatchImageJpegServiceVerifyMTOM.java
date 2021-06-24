//
// @(#) 1.1 autoFVT/src/mtom21/disablejaxb/wsfvt/server/MTOMDefaultDispatchImageJpegServiceVerifyMTOM.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:27:19 [8/8/12 06:40:44]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 03/10/08 jramos      LIDB4418-12.01  New File

package mtom21.disablejaxb.wsfvt.server;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.MTOM;

@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider(
        targetNamespace = "urn://DisableJAXBDispatch_imagejpeg.mtom21.test.org",
        serviceName = "MTOMDefaultDispatchImageJpegServiceVerification",
        portName = "AttachmentServicePortVerify",
        wsdlLocation="WEB-INF/wsdl/MTOMDefaultDispatchImageJpeg.wsdl")
@MTOM(enabled=false)
		
public class MTOMDefaultDispatchImageJpegServiceVerifyMTOM implements Provider<SOAPMessage> {
    
    public SOAPMessage invoke(SOAPMessage request) {
        try {
            String response = null;
            if (request.getAttachments().hasNext()) {
                response = "MTOMON";
            } else {
                response = "MTOMOFF";
            }

            // construct response
            SOAPMessage responseMsg = MessageFactory.newInstance().createMessage();
            SOAPBody body = responseMsg.getSOAPBody();
            SOAPBodyElement payload = body.addBodyElement(new QName("urn://DisableJAXBDispatch_imagejpeg.mtom21.test.org", "sendImageResponseVerification"));
            SOAPElement output = payload.addChildElement("output");
            output.addTextNode(response);
            return responseMsg;
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return null;
    }
}
