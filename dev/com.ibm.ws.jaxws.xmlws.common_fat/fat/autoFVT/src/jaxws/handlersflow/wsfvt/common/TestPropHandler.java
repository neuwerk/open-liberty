//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId         Defect          Description
// ----------------------------------------------------------------------------
// 05/10/2007  mzheng         443868          New File
//

package jaxws.handlersflow.wsfvt.common;

import java.io.File;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.ProtocolException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.ibm.ws.wsfvt.build.tools.AppConst;


/**
 * This class implements a JAX-WS SOAPHandler:
 * (1) The log filename and test commands are retrieved from MessageContext
 *     properties
 * (2) Every callback is logged to handlers flow log file
 * (3) Per test command, handleFault() and handleMessage() return different
 *     status or take different actions
 * (4) For client outbound, put MessageContext properties to SOAP message 
 *     header attributes
 * (5) For server inbound, process SOAP message header and put header 
 *     attributes to MessageContext properties
 */
public class TestPropHandler implements SOAPHandler <SOAPMessageContext> {

    private String _handlerName = "TestPropHandler";
    private String _role = "common";
    private static Log _log = null;

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
    }


    public void setParams(String name, String role) {
        _handlerName = name;
        _role = role;
        setDefaultLog();
    }


    /**
     * @see javax.xml.ws.handler.soap.SOAPHandler#getHeaders()
     */
    public Set<QName> getHeaders() {
        return null;
    }


    /**
     * @see javax.xml.ws.handler.Handler#handleFault(MessageContext)
     */
    public boolean handleFault(SOAPMessageContext smc) {
        setLog((MessageContext) smc);

        boolean isOutbound = ((Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

        // Log execution flow
        String curFlow = _role + "_" + _handlerName + "_handleFault" + 
                         (isOutbound ? "_Outbound:" : "_Inbound:");
        _log.log(curFlow);

        // Execute command targeted for this handler
        ArrayList<String> curPath = (ArrayList) smc.get(TestConstants.FAULT_PATH_PROPERTY);

        // command is a 4-tuple {role, handler_name, direction, action}
        if (curPath != null &&
            curPath.get(0).equals(_role) &&
            curPath.get(1).equals(_handlerName) && 
            isRightDirection(isOutbound, curPath.get(2))) {

            // reset FAULT_PATH_PROPERTY to null after executing command
            smc.put(TestConstants.FAULT_PATH_PROPERTY, null);

            if (curPath.get(3).equals(TestConstants.PATH_TRUE)) {
                return true;
            } else if (curPath.get(3).equals(TestConstants.PATH_FALSE)) {
                return false;
            } else if (curPath.get(3).equals(TestConstants.PATH_PROTOCOL_EXCEPTION)) {
                throw new ProtocolException(curPath.get(3) + " thrown in " + curPath.get(0) + " " + curPath.get(1) + " handleFault() " + curPath.get(2));
            } else if (curPath.get(3).equals(TestConstants.PATH_RUNTIME_EXCEPTION)) {
                throw new RuntimeException(curPath.get(3) + " thrown in " + curPath.get(0) + " " + curPath.get(1) + " handleFault() " + curPath.get(2));
            }
        }
        return true;
    }


    /**
     * @see javax.xml.ws.handler.Handler#close(MessageContext)
     */
    public void close(MessageContext mc) {
        setLog(mc);

        // Log execution flow
        String curFlow = _role + "_" + _handlerName + "_close:";
        _log.log(curFlow);
    }


    /**
     * @see javax.xml.ws.handler.Handler#handleMessage(MessageContext)
     */
    public boolean handleMessage(SOAPMessageContext smc) {
        boolean isOutbound = ((Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

        // Execute command targeted for this handler
        ArrayList<String> msgPath = null;
        ArrayList<String> faultPath = null;
  
        if (isOutbound && _role.equals(TestConstants.CLIENT_ROLE)) {
            msgPath = (ArrayList) smc.get(TestConstants.MESSAGE_PATH_PROPERTY);
            faultPath = (ArrayList) smc.get(TestConstants.FAULT_PATH_PROPERTY);

            Hashtable attrTable = new Hashtable();

            // Get SERVER_LOG_PROPERTY
            String serverLogFile = (String) smc.get(TestConstants.SERVER_LOG_PROPERTY);
            if (serverLogFile != null) {
                attrTable.put(TestConstants.SERVER_LOG_PROPERTY, serverLogFile);
            }

            // Get MESSAGE_PATH_PROPERTY intended for server
            if (msgPath != null && !msgPath.get(0).equals(_role)) {
                attrTable.put(TestConstants.MESSAGE_PATH_PROPERTY, msgPath);
            }

            // Get FAULT_PATH_PROPERTY intended for server
            if (faultPath != null && !faultPath.get(0).equals(_role)) {
                attrTable.put(TestConstants.FAULT_PATH_PROPERTY, faultPath);
            }

            // Add above properties to SOAP header element
            toHeader(smc, attrTable);
        } else if (!isOutbound && _role.equals(TestConstants.SERVER_ROLE)) {
            // Get SERVER_LOG_PROPERTY from SOAP header
            // Get MESSAGE_PATH_PROPERTY intended for server from SOAP header
            // Get FAULT_PATH_PROPERTY intended for server from SOAP header
            toMessageContext(smc);
        }
        msgPath = (ArrayList) smc.get(TestConstants.MESSAGE_PATH_PROPERTY);
        faultPath = (ArrayList) smc.get(TestConstants.FAULT_PATH_PROPERTY);

        setLog((MessageContext) smc);

        // Log execution flow
        String curFlow = _role + "_" + _handlerName + "_handleMessage" +
                         (isOutbound ? "_Outbound:" : "_Inbound:");
        _log.log(curFlow);

        // command is a 4-tuple {role, handler_name, direction, action}
        if (msgPath != null &&
            msgPath.get(0).equals(_role) &&
            msgPath.get(1).equals(_handlerName) && 
            isRightDirection(isOutbound, msgPath.get(2))) {

            // reset MESSAGE_PATH_PROPERTY to null after executing command
            smc.put(TestConstants.MESSAGE_PATH_PROPERTY, null);

            if (msgPath.get(3).equals(TestConstants.PATH_TRUE)) {
                return true;
            } else if (msgPath.get(3).equals(TestConstants.PATH_FALSE)) {
                return false;
            } else if (msgPath.get(3).equals(TestConstants.PATH_PROTOCOL_EXCEPTION)) {
                throw new ProtocolException(msgPath.get(3) + " thrown in " + msgPath.get(0) + " " + msgPath.get(1) + " handleMessage() " + msgPath.get(2));
            } else if (msgPath.get(3).equals(TestConstants.PATH_RUNTIME_EXCEPTION)) {
                throw new RuntimeException("RuntimeException thrown in " + msgPath.get(0) + " " + msgPath.get(1) + " handleMessage() " + msgPath.get(2));
            }
        }
        return true;
    }


    private boolean isRightDirection(boolean isOutbound, String dirString) {
        if ((isOutbound && dirString.equals(TestConstants.OUTBOUND)) || 
            (!isOutbound && dirString.equals(TestConstants.INBOUND))) {
            return true;
        } else {
            return false;
        }
    }


    private void setLog(MessageContext mc) {
        String logFile = null;
        if (_role.equals(TestConstants.CLIENT_ROLE)) {
            logFile = (String)mc.get(TestConstants.CLIENT_LOG_PROPERTY);
        } else {
            logFile = (String)mc.get(TestConstants.SERVER_LOG_PROPERTY);
        }

        if (logFile != null) {
            _log.setLogfileName(logFile);
        }
    }


    private void setDefaultLog() {
        String logFile = null;
        if (_role.equals(TestConstants.CLIENT_ROLE)) {
            logFile = AppConst.FVT_HOME + File.separator + "logs" + File.separator + TestConstants.DEFAULT_LOGFILE;
        } else {
            logFile = System.getProperty("user.install.root") + File.separator + "logs" + File.separator + TestConstants.DEFAULT_LOGFILE;
        }

        if (logFile != null) {
            _log = new Log(logFile);
        }
    }


    /**
     * This method processes MessageContext properties, creates a SOAP header,
     * put the properties as header attributes to be sent to server.
     */
    private void toHeader(SOAPMessageContext smc, Hashtable attrTable) {
        try {
            SOAPMessage msg = smc.getMessage();

            if (msg == null) {
                System.err.println("Failed to get SOAP Message!");
                return;
            }

            SOAPPart sp = msg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();
            SOAPHeader sh = se.getHeader();
            if (sh == null) {
                sh = se.addHeader();
            }

            sh.extractHeaderElements(TestConstants.SOAP_HEADER_ACTOR);
            Name hName = se.createName(TestConstants.SOAP_HEADER_NAME,
                                       TestConstants.SOAP_HEADER_PREFIX,
                                       TestConstants.SOAP_HEADER_NAMESPACE);

            SOAPHeaderElement she = sh.addHeaderElement(hName);
            she.setActor(TestConstants.SOAP_HEADER_ACTOR);

            String value = (String) attrTable.get(TestConstants.SERVER_LOG_PROPERTY);
            if (value != null  &&  value.length() > 0) {
                Name propName = se.createName(TestConstants.SERVER_LOG_PROPERTY);
                she.addAttribute(propName, value);
            }

            ArrayList<String> pathList = (ArrayList) attrTable.get(TestConstants.MESSAGE_PATH_PROPERTY);
            if (pathList != null && pathList.size() > 0) {
                Name propName = se.createName(TestConstants.MESSAGE_PATH_PROPERTY);
                she.addAttribute(propName, toString(pathList));
            }

            pathList = (ArrayList) attrTable.get(TestConstants.FAULT_PATH_PROPERTY);
            if (pathList != null && pathList.size() > 0) {
                Name propName = se.createName(TestConstants.FAULT_PATH_PROPERTY);
                she.addAttribute(propName, toString(pathList));
            }

            msg.saveChanges();
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getName());
            e.printStackTrace();
        }
        return;
    }


    /** 
     * This method processes SOAP message header, retrieves header attributes 
     * and put them to MessageContext properties.
     */
    private void toMessageContext(SOAPMessageContext smc) {
        try {
            SOAPMessage msg = smc.getMessage();

            if (msg == null) {
                System.err.println("Failed to get SOAP Message!");
                return;
            }

            SOAPPart sp = msg.getSOAPPart();
            SOAPEnvelope se = sp.getEnvelope();

            SOAPHeader sh = se.getHeader();
            if (sh == null) {
                // Nothing to process
                return;
            }

            SOAPHeaderElement she = null;

            Iterator it = sh.extractHeaderElements(TestConstants.SOAP_HEADER_ACTOR);
            while (it != null && it.hasNext()) {
                she = (SOAPHeaderElement) it.next();
                if (she.getElementName().getLocalName().equals(TestConstants.SOAP_HEADER_NAME)) {
                    break;
                }
            }

            if (she != null) {
                it = she.getAllAttributes();
                while (it != null && it.hasNext()) {
                    Name attrName = (Name) it.next();
                    String propName = attrName.getLocalName();
                    if (propName != null) {
                        if (propName.equals(TestConstants.SERVER_LOG_PROPERTY)) {
                            smc.put(TestConstants.SERVER_LOG_PROPERTY, 
                                    she.getAttributeValue(attrName));
                            smc.setScope(TestConstants.SERVER_LOG_PROPERTY,
                                    MessageContext.Scope.APPLICATION);
                        } else if (propName.equals(TestConstants.MESSAGE_PATH_PROPERTY)) {
                            smc.put(TestConstants.MESSAGE_PATH_PROPERTY,
                                    (ArrayList) toList(she.getAttributeValue(attrName)));
                        } else if (propName.equals(TestConstants.FAULT_PATH_PROPERTY)) {
                            smc.put(TestConstants.FAULT_PATH_PROPERTY,
                                    (ArrayList) toList(she.getAttributeValue(attrName)));
                        } 
                    } 
                }
            }

            msg.saveChanges();
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getName());
            e.printStackTrace();
        }
        return;
    }


    /**
     * This method converts a list of Strings to a single String, seperated 
     * by blank space
     */
    private String toString(ArrayList<String> list) {
        String init = new String("");
        String result = init;

        for (int i = 0; i < list.size(); i++) {
            result = init + list.get(i) + " ";
            init = result;
        }
        return result;
    }


    /**
     * This methos breaks a String into tokens, using space a delimiter.  
     * Returns a list of Strings.
     */
    private ArrayList<String> toList(String pathString) {
        ArrayList<String> retList = new ArrayList();

        StringTokenizer st = new StringTokenizer(pathString, " ", false);
        while (st.hasMoreTokens()) {
            retList.add(st.nextToken());
        }
        return retList;
    }
}
