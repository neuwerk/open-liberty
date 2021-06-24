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

package jaxws.handlersflow.wsfvt.server;

import java.io.File;
import javax.annotation.Resource;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

import jaxws.handlersflow.wsfvt.common.TestConstants;
import jaxws.handlersflow.wsfvt.common.Log;

@WebService (targetNamespace="http://handlersflow.jaxws",
             wsdlLocation="WEB-INF/wsdl/CountString.wsdl",
             serviceName="CountStringService",
             portName="CountStringPort",
             endpointInterface="jaxws.handlersflow.wsfvt.server.CountStringPortType")

@HandlerChain(file="handlers.xml", name="")

public class CountStringImpl {
    @Resource
    WebServiceContext wsContext;

    private static Log _log = new Log(System.getProperty("user.install.root") + File.separator + "logs" + File.separator + TestConstants.DEFAULT_LOGFILE);

    public int countString(String request) {
        MessageContext mc = wsContext.getMessageContext();
        setLog(mc);
        _log.log("Server_countString:");

        if (request != null) {
            return request.length();
        } else {
            throw new WebServiceException("CountStringImpl:countString() received null request string");
        }
    }


    public void sendString(String input) {
        MessageContext mc = wsContext.getMessageContext();
        setLog(mc);
        _log.log("Server_sendString:");

        if (input == null) {
            throw new WebServiceException("CountStringImpl:countString() received null string for one-way operation");
        }
    }


    private void setLog(MessageContext mc) {
        String logFile = null;
        logFile = (String) mc.get(TestConstants.SERVER_LOG_PROPERTY);

        if (logFile != null) {
            _log.setLogfileName(logFile);
        }
    }
}
