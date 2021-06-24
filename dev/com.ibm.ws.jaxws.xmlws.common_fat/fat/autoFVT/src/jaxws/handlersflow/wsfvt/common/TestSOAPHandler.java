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

import java.util.Set;
import java.util.ArrayList;

import javax.xml.namespace.QName;

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
 */
public class TestSOAPHandler implements SOAPHandler <SOAPMessageContext> {

    private String _handlerName = "TestSOAPHandler";
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
        setLog((MessageContext) smc);

        boolean isOutbound = ((Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

        // Log execution flow 
        String curFlow = _role + "_" + _handlerName + "_handleMessage" + 
                         (isOutbound ? "_Outbound:" : "_Inbound:");
        _log.log(curFlow);

        // Execute command targeted for this handler
        ArrayList<String> curPath = (ArrayList) smc.get(TestConstants.MESSAGE_PATH_PROPERTY);

        // command is a 4-tuple {role, handler_name, direction, action}
        if (curPath != null &&
            curPath.get(0).equals(_role) &&
            curPath.get(1).equals(_handlerName) && 
            isRightDirection(isOutbound, curPath.get(2))) {

            // reset MESSAGE_PATH_PROPERTY to null after executing command
            smc.put(TestConstants.MESSAGE_PATH_PROPERTY, null);

            if (curPath.get(3).equals(TestConstants.PATH_TRUE)) {
                return true;
            } else if (curPath.get(3).equals(TestConstants.PATH_FALSE)) {
                return false;
            } else if (curPath.get(3).equals(TestConstants.PATH_PROTOCOL_EXCEPTION)) {
                throw new ProtocolException(curPath.get(3) + " thrown in " + curPath.get(0) + " " + curPath.get(1) + " handleMessage() " + curPath.get(2));
            } else if (curPath.get(3).equals(TestConstants.PATH_RUNTIME_EXCEPTION)) {
                throw new RuntimeException(curPath.get(3) + " thrown in " + curPath.get(0) + " " + curPath.get(1) + " handleMessage() " + curPath.get(2));
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
            logFile = (String) mc.get(TestConstants.CLIENT_LOG_PROPERTY);
        } else {
            logFile = (String) mc.get(TestConstants.SERVER_LOG_PROPERTY);
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
}
