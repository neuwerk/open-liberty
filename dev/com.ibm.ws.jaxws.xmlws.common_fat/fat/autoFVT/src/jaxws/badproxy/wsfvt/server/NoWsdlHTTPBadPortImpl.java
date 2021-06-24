//
// @(#) 1.1 autoFVT/src/jaxws/badproxy/wsfvt/server/NoWsdlHTTPBadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/27/06 09:03:41 [8/8/12 06:55:25]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect             Description
// ----------------------------------------------------------------------------
// 05/31/06   sedov       LIDB3296.41-02     New File
//

package jaxws.badproxy.wsfvt.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * An endpoint without a wsdl or an SEI. Expecting that a wsdl
 * generation will fail as HTTP bidning is invalid for Bean endpoints.
 */
@BindingType(value = javax.xml.ws.http.HTTPBinding.HTTP_BINDING)
@WebService(name="NoWsdlHTTPPort",
		portName="NoWsdlHTTPPort",
		serviceName="NoWsdlHTTPService",
		targetNamespace="http://NoWsdlHTTP.common.wsfvt.badproxy.jaxws")
public class NoWsdlHTTPBadPortImpl {

    @WebMethod(action = "http://NoWsdlHTTP.common.wsfvt.badproxy.jaxws/ping")
    @RequestWrapper(localName = "ping", targetNamespace = "http://common.wsfvt.badproxy.jaxws", className = "jaxws.badproxy.wsfvt.common.Ping")
    @ResponseWrapper(localName = "pingResponse", targetNamespace = "http://common.wsfvt.badproxy.jaxws", className = "jaxws.badproxy.wsfvt.common.PingResponse")
    public void ping(
        @WebParam(name = "ping", targetNamespace = "", mode = WebParam.Mode.INOUT)
        Holder<String> ping){
		
	}

}
