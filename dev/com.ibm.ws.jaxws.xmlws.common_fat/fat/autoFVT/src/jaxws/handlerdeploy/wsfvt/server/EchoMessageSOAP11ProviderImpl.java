//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId       Defect        Description
// ----------------------------------------------------------------------------
// 04/10/2007  mzheng       435342        New File
// 03/07/2008  mzheng       502861        Use to diff impl to workaround 502858
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
             serviceName="EchoMessageSOAP11Provider",
             portName="EchoMessageSOAP11ProviderPort")

@ServiceMode(value=Service.Mode.MESSAGE)

@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)

@HandlerChain(file="handlers.xml", name="")

public class EchoMessageSOAP11ProviderImpl implements Provider<SOAPMessage> {

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
                respString = reqString + "Provider_SOAP11:";
            } else {
                throw new WebServiceException("EchoMessageSOAP11ProviderImpl:invoke() received null request string");
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
            throw new WebServiceException(e.getClass().getName() + " was caught in EchoMessageSOAP11ProviderImpl:invoke()");
        }
    }
}


/*** Comment out due to prereq.java defect 502858
import java.io.StringReader;
import javax.jws.HandlerChain;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

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
             serviceName="EchoMessageSOAP11Provider",
             portName="EchoMessageSOAP11ProviderPort")

@ServiceMode(value=Service.Mode.PAYLOAD)

@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)

@HandlerChain(file="handlers.xml", name="")

public class EchoMessageSOAP11ProviderImpl implements Provider<Source> {

    public Source invoke(Source request) {
        try {
            DOMResult dom = new DOMResult();
            Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

            transformer.transform(request, dom);

            // This is the document node
            Node node = dom.getNode();

            // This is the operation node - echoMessage element
            Node root = node.getFirstChild();

            // This is the parameter node - request
            Node reqNode  = root.getFirstChild();
            String reqString = reqNode.getFirstChild().getNodeValue();
            String respString = null;

            if (reqString != null) {
                respString = reqString + "Provider_SOAP11:";
            } else {
                throw new WebServiceException("EchoMessageSOAP11ProviderImpl:invoke() received null request string");
            }

            return createRespSource(respString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebServiceException(e.getClass().getName() + " was caught in EchoMessageSOAP11ProviderImpl:invoke()");
        }
    }

    private Source createRespSource(String resp) {

        try {
            String schemaNS = this.getClass().getAnnotation(WebServiceProvider.class).targetNamespace() + "/xsd";
            String respString = "<echoMessageResponse xmlns=\"" +
                            schemaNS + "\">" + "<response>" +
                            resp + "</response></echoMessageResponse>";

            Source retSource = new StreamSource(new StringReader(respString));

            return retSource;
        } catch (Exception e) {
            throw new WebServiceException(e.getClass().getName() + " was caught in EchoMessageSOAP11ProviderImpl:createRespSource()");
        }
    }
}
***/
