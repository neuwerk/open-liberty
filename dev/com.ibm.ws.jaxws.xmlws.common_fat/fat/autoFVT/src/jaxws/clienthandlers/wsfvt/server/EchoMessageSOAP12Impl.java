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
//

package jaxws.clienthandlers.wsfvt.server;

import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import javax.jws.WebService;

@WebService (targetNamespace="http://clienthandlers.jaxws",
             wsdlLocation="WEB-INF/wsdl/EchoMessage.wsdl",
             serviceName="EchoMessageSOAP12Service",
             portName="EchoMessageSOAP12Port",
             endpointInterface="jaxws.clienthandlers.wsfvt.server.EchoMessagePortType")

@BindingType (value=javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)

public class EchoMessageSOAP12Impl {

    public String echoMessage(String request) {
        if (request != null) {
            return (request + "Server_SOAP12:");
        } else {
            throw new WebServiceException("EchoMessageSOAP12Impl:echoMessage() received null request string");
        }

    }
}
