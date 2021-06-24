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
             serviceName="EchoMessageSOAP11Service",
             portName="EchoMessageSOAP11Port",
             endpointInterface="jaxws.handlerdeploy.wsfvt.server.EchoMessagePortType")

@HandlerChain(file="handlers.xml", name="")

public class EchoMessageSOAP11Impl {

    public String echoMessage(String request) {
        if (request != null) {
            return (request + "SEI_SOAP11:");
        } else {
            throw new WebServiceException("EchoMessageSOAP11Impl:echoMessage() received null request string");
        }
    }
}
