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

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceException;

@WebService (targetNamespace="http://handlerdeploy.jaxws",
             wsdlLocation="WEB-INF/wsdl/EchoMessage.wsdl",
             serviceName="EchoMessageSOAP11SimpleService",
             portName="EchoMessageSOAP11SimplePort",
             endpointInterface="jaxws.handlerdeploy.wsfvt.server.EchoMessagePortType")

public class EchoMessageSOAP11SimpleImpl {

    public String echoMessage(String request) {
        if (request != null) {
            return (request + "Simple_SEI_SOAP11:");
        } else {
            throw new WebServiceException("EchoMessageSOAP11SimpleImpl:echoMessage() received null request string");
        }
    }
}
