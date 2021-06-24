//
// @(#) 1.1 autoFVT/src/mtom21/security/wsfvt/server/SecurityMTOMVerifyMTOMOff.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/15/08 14:39:30 [8/8/12 06:41:00]
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

package mtom21.security.wsfvt.server;

import java.util.Iterator;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.MTOM;

import org.test.mtom21.securityverifymtom.*;

/**
* - This class provides the server side implementation for JAX-WS Provider<MESSAGE>
* - for SOAP11 Binding
* - with Mode = MESSAGE.
* 
* The receiving message and the sending back message
* must have the headers defined in wsdl.
*/

@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider(
        targetNamespace = "http://org/test/mtom21/SecurityVerifyMTOM",
        serviceName = "SecurityVerifyMTOMOff",
        portName = "SecurityVerifyMTOMOffPort",
        wsdlLocation="WEB-INF/wsdl/SecurityVerifyMTOM.wsdl")

@MTOM(enabled=false)

public class SecurityMTOMVerifyMTOMOff implements Provider<SOAPMessage> {
  
    public SecurityMTOMVerifyMTOMOff() {
    }
    
   /**
    * - This class provides the server side implementation for JAX-WS Provider<MESSAGE>
    * - for SOAP11 Binding
    * - with Mode = MESSAGE.
    * 
    * The receiving message and the sending back message
    * must have the headers defined in wsdl.
    */
    public SOAPMessage invoke(SOAPMessage request) {
      System.out.println("--------------------------------------");
      System.out.println("enableSOAPBinding - SecurityMTOMVerifyMTOMOff: Request received");

      try {
          String response = "";
          
          // verify that the request has no attachment
          Iterator iter = request.getAttachments();
          if(iter.hasNext()) {
              // no attachment found; verification fails
              response = "MTOMON";
          } else {
              // get the data from the SOAPMessage
              iter = request.getSOAPBody().getChildElements();
              SOAPElement soapElement = null;
              String name = "";
              String temp = name;
              while (name.indexOf("imageData") == -1 && iter != null && iter.hasNext()) {
                  soapElement = (SOAPElement) iter.next();
                  try {
                     temp = soapElement.getNodeName();
                  } catch(Exception e) {
                      System.out.println("Error retrieving name of SOAPElement Object.");
                      break;
                  }
                  name = temp;
                  System.out.println("name: " + name);
                  iter = soapElement.getChildElements();
              }
              Object o = iter.next();
              response = soapElement.getValue();
          }
          
          // construct response
          SOAPMessage responseMsg = MessageFactory.newInstance().createMessage();
          SOAPBody body = responseMsg.getSOAPBody();
          SOAPBodyElement payload = body.addBodyElement(new QName("http://org/test/mtom21/SecurityVerifyMTOM", "sendImageResponse"));
          SOAPElement output = payload.addChildElement("output");
          SOAPElement imageData = output.addChildElement("imageData");
          imageData.addTextNode(response);
          return responseMsg;
      }catch(Exception e) {
          System.out.println(e.getMessage());
      }
      
      return null;

  }
         
}