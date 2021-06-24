//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId       Defect          Description
// ----------------------------------------------------------------------------
// 04/10/2007  mzheng       435342          New File
//

package jaxws.handlerdeploy.wsfvt.server;

import java.util.Iterator;
import javax.jws.HandlerChain;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import org.w3c.dom.Node;

@WebServiceProvider (targetNamespace="http://handlerdeploy.jaxws",
             wsdlLocation="WEB-INF/wsdl/EchoMessage.wsdl",
             serviceName="EchoMessageSOAP12Provider",
             portName="EchoMessageSOAP12ProviderPort")

@ServiceMode(value=Service.Mode.MESSAGE)

@BindingType(value=SOAPBinding.SOAP12HTTP_BINDING)

@HandlerChain(file="handlers.xml", name="")

public class EchoMessageSOAP12ProviderImpl implements Provider<SOAPMessage> {

    public SOAPMessage invoke(SOAPMessage msg) {
        try {
            String respString = null;
            SOAPBody body = msg.getSOAPBody();
            String reqString = null;

            SOAPBodyElement bodyEle = (SOAPBodyElement) body.getFirstChild(); 
            if (bodyEle != null &&
                bodyEle.getElementName().getLocalName().equals("echoMessage")) { 
                Node reqNode = bodyEle.getFirstChild();
                if (reqNode != null && 
                    reqNode.getFirstChild() != null) { 
                    reqString = reqNode.getFirstChild().getNodeValue();
                }
            }

            if (reqString != null) {
                respString = reqString + "Provider_SOAP12:";
            } else {
                throw new WebServiceException("EchoMessageSOAP12ProviderImpl:invoke() received null request string");
            }

            SOAPMessage respMsg = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
            SOAPEnvelope respEnv = respMsg.getSOAPPart().getEnvelope();
            SOAPBody respBody = respEnv.getBody();

            // Build the soap body
            SOAPBodyElement respBodyEle =
                respBody.addBodyElement(respEnv.createName("echoMessageResponse", "", bodyEle.getNamespaceURI()));
            respBodyEle.addChildElement("response").addTextNode(respString);

            respMsg.saveChanges();

            return respMsg;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebServiceException(e.getClass().getName() + " was caught in EchoMessageSOAP12ProviderImpl:invoke()");
        }
    }
}
