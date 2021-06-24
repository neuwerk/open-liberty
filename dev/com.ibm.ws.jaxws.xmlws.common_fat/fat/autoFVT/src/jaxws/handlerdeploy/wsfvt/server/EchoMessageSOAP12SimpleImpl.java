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

import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import javax.jws.WebService;
import javax.jws.HandlerChain;

@WebService (targetNamespace="http://handlerdeploy.jaxws",
             wsdlLocation="WEB-INF/wsdl/EchoMessage.wsdl",
             serviceName="EchoMessageSOAP12SimpleService",
             portName="EchoMessageSOAP12SimplePort",
             endpointInterface="jaxws.handlerdeploy.wsfvt.server.EchoMessagePortType")

@BindingType (value=javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)

public class EchoMessageSOAP12SimpleImpl {

    public String echoMessage(String request) {
        if (request != null) {
            return (request + "Simple_SEI_SOAP12:");
        } else {
            throw new WebServiceException("EchoMessageSOAP12SimpleImpl:echoMessage() received null request string");
        }

    }
}
