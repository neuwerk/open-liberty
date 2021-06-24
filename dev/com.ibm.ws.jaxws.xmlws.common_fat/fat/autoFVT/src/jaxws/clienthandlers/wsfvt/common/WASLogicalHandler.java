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
// 04/20/2007  mzheng       LIDB3296-40.01  New File
// 07/09/2007  mzheng       449269          Enable test cases
//

package jaxws.clienthandlers.wsfvt.common;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.LogicalMessage;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;

import javax.annotation.PreDestroy;
import javax.annotation.PostConstruct;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.File;

public class WASLogicalHandler implements 
                               LogicalHandler <LogicalMessageContext> {

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


    public boolean handleFault(LogicalMessageContext lmc) {
            return true;
    }


    public void close(MessageContext mc) {

    }


    public boolean handleMessage(LogicalMessageContext lmc) {
        boolean isOutbound = ((Boolean)lmc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

        LogicalMessage lm = lmc.getMessage();
        Source source = lm.getPayload();   
        Source newSource = null;
      
        if (isOutbound) {
            newSource = getDOMSource(source, _handlerName + "_Outbound:");
        } else {
            newSource = getDOMSource(source, _handlerName + "_Inbound:");
        }

        if (newSource != null) {
            lm.setPayload(newSource);
            return true;
        } else {
            return false;
        }
    }


    private Source getDOMSource(Source source, String str) {
        DOMSource newSource = new DOMSource();
 
        try {
            DOMResult dom = new DOMResult();

            Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

            transformer.transform(source, dom);

            /* #document node */
            Node node = dom.getNode();

            /* echoMessage node */
            Node root = node.getFirstChild();

            /* request node */
            Node child  = root.getFirstChild();

            /* #text node */
            Node reqNode = child.getFirstChild();

            // This is the request or response string
            String curStr = reqNode.getNodeValue();

            reqNode.setNodeValue(curStr + str);

            newSource.setNode(node);
            return (Source) newSource;
        } catch (Throwable e) {
            System.err.println(e.getClass().getName() + " was caught in WASLogicalHandler:getDOMSource()\n");
            e.printStackTrace();
            return null;
        }
    }


/*
    public void logLogicalMessage(LogicalMessage lm) {
        try {
            if (lm != null) {
                Source source = lm.getPayload();
                if (source != null) {
                    log(getSourceAsString(source));
                } else {
                    log("payload was null");
                }
            } else {
                log("Logical message was null");
            }
        } catch (Exception e) {
            log("Exception caught in logLogicalMessage", e);
        }

       return;
    }

    public void logJAXB(LogicalMessageContext lmc) {
        LogicalMessage lm = lmc.getMessage();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Object jaxbObject = lm.getPayload(jaxbContext);

            // Modify JAXB Object
            // lm.setPayload(modifiedJaxbObject,jaxbContext);
        } catch (Exception e) {
            log("Exception caught", e);
        }
    } 


    public String getSourceAsString(Source s) {
        try {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    //  transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        OutputStream out = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult();
        streamResult.setOutputStream(out);
        transformer.transform(s, streamResult);
        return streamResult.getOutputStream().toString();
        } catch (Exception e) {
            log("Exception caught in getSourceAsString", e);
        }
        return null;
    }


    private void log(String msg) {
        if (_handlerName != null) {
            _log.log(_handlerName + ": " + msg);
        } else {
            _log.log("Unknown handler: " + msg);
        }
    }


    private void log(String msg, Throwable t) {
        if (_handlerName != null) {
            _log.log(_handlerName + ": " + msg, t);
        } else {
            _log.log("Unknown handler: " + msg, t);
        }
        t.printStackTrace();
    }
*/
}

