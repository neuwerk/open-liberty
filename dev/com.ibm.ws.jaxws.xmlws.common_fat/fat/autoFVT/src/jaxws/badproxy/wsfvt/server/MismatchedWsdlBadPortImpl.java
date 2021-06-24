//
// @(#) 1.1 autoFVT/src/jaxws/badproxy/wsfvt/server/MismatchedWsdlBadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/27/06 09:03:41 [8/8/12 06:55:25]
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

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;

/**
 * An SEI-based enndpoint that does not use an SEI. The operation
 * specified in WSDL is not exposed by this service
 */
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@WebService(name="MismatchedWsdlPort",
		portName="MismatchedWsdlPort",
		serviceName="MismatchedWsdlService",
		targetNamespace="http://MismatchedWsdl.common.wsfvt.badproxy.jaxws",
		wsdlLocation="WEB-INF/wsdl/MismatchedWsdl.wsdl")
public class MismatchedWsdlBadPortImpl{

	public void operationNotInWsdl(Holder<String> ping) {
		
	}

}
