//
// @(#) 1.1 autoFVT/src/mtom21/threshold/wsfvt/server/VerifyMTOMSource.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:40:13 [8/8/12 06:41:02]
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

package mtom21.threshold.wsfvt.server;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.MTOM;


@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider(
        targetNamespace = "http://ws.apache.org/axis2",
        serviceName = "VerifyMTOMSource",
        portName = "VerifyMTOMSourcePort",
        wsdlLocation="WEB-INF/wsdl/VerifyMTOMSource.wsdl")
@MTOM(enabled=false)

/**
 * This class checks if MTOM is used on the Dispatch Source client. It should always be false.
 */
public class VerifyMTOMSource implements Provider<SOAPMessage> {
  
    public VerifyMTOMSource() {
    }
    
    public SOAPMessage invoke(SOAPMessage request) {
      try {
          String response = "";
          
          // verify that the request has an attachment
          Iterator iter = request.getAttachments();
          if(iter.hasNext()) {
              // we have an attachment so this must be MTOM
              response = "MTOMON";
          } else {
              // no attachment found; verification fails
              response = "MTOMOFF";
          }
          
          // construct response
          SOAPMessage responseMsg = MessageFactory.newInstance().createMessage();
          SOAPBody body = responseMsg.getSOAPBody();
          SOAPBodyElement payload = body.addBodyElement(new QName("http://ws.apache.org/axis2", "outMessage"));
          payload.addTextNode(response);
          return responseMsg;
      }catch(Exception e) {
          System.out.println(e.getMessage());
      }
      
      return null;

  }
         
}