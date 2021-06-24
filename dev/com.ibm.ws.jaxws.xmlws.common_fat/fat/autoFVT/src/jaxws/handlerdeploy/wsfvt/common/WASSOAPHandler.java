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

package jaxws.handlerdeploy.wsfvt.common;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import org.w3c.dom.Node;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class WASSOAPHandler implements SOAPHandler <SOAPMessageContext> {

    private String _handlerName = null;
    private String _role = null;

    @PostConstruct
    public void init() {

    }


    @PreDestroy
    public void destroy() {

    }


    public void setParams(String name, String role) {
        _handlerName = name;
        _role = role;

    }


    /**
     * @see javax.xml.ws.handler.soap.SOAPHandler#getHeaders()
     */
    public Set<QName> getHeaders() {
        return null;
    }


    /**
     * @see javax.xml.ws.handler.soap.SOAPHandler#handleFault(SOAPMessageContext)
     */
    public boolean handleFault(SOAPMessageContext smc) {
        return true;
    }


    /**
     * @see javax.xml.ws.handler.soap.SOAPHandler#close(MessageContext)
     */
    public void close(MessageContext mc) {

    }


    /**
     * @see javax.xml.ws.handler.soap.SOAPHandler#handleMessage(SOAPMessageContext)
     *
     * Changes request string in SOAP message.
     */
    public boolean handleMessage(SOAPMessageContext smc) {
        boolean isOutbound = ((Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

        /*
         * SOAP request message:
         *
         * <?xml version="1.0" encoding="UTF-8"?>
         * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
         *   <soapenv:Body>
         *     <echoMessage xmlns="http://handlerdeploy.jaxws/xsd">
         *       <request>Client_Hello:</request>
         *     </echoMessage>
         *   </soapenv:Body>
         * </soapenv:Envelope>
         */

        SOAPMessage msg = smc.getMessage();
        SOAPMessage newMsg = null;

        if (isOutbound) {
            newMsg = getSOAPMessage(msg, _handlerName + "_Outbound:");
        } else {
            newMsg = getSOAPMessage(msg, _handlerName + "_Inbound:");
        }

        if (newMsg != null) {
            smc.setMessage(newMsg);
            return true;
        } else {
            return false;
        }
    }


    /**
     * This method retrieves the request string in SOAP message and
     * modifies the SOAP message.
     */
    private SOAPMessage getSOAPMessage(SOAPMessage msg, String str) {
        SOAPMessage message = msg;
        try {
            SOAPBody messageBody = message.getSOAPBody();

            /* echoMessage node */
            Iterator i = messageBody.getChildElements();
            SOAPBodyElement bodyEle = (SOAPBodyElement) i.next();

            /* request node */
            Node child = bodyEle.getFirstChild();

            /* #text node */
            Node reqNode = child.getFirstChild();
            String curStr = reqNode.getNodeValue();

            reqNode.setNodeValue(curStr + str);
            message.saveChanges();
            return message;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + " was caught in WASSOAPHandler:getSOAPMessage()");
            e.printStackTrace();
            return null;
        }
    }
}
