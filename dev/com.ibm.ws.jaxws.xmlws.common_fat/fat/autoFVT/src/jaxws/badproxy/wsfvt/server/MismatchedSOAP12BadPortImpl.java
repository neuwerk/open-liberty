//
// @(#) 1.1 autoFVT/src/jaxws/badproxy/wsfvt/server/MismatchedSOAP12BadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/27/06 09:03:40 [8/8/12 06:55:25]
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

import jaxws.badproxy.wsfvt.common.mismatchedsoap12.MismatchedSOAP12Port;

/**
 * An SEI-based enndpoint that declares use of SOAP1.1 but is deployed with a
 * wsdl that uses SOAP 1.2
 * 
 */
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@WebService(portName="MismatchedSOAP12Port",
		serviceName="MismatchedSOAP12Service",
		targetNamespace="http://MismatchedSOAP12.common.wsfvt.badproxy.jaxws",
		endpointInterface="jaxws.badproxy.wsfvt.common.mismatchedsoap12.MismatchedSOAP12Port",
		wsdlLocation="WEB-INF/wsdl/MismatchedSOAP12.wsdl")
public class MismatchedSOAP12BadPortImpl implements MismatchedSOAP12Port{

	public void ping(Holder<String> ping) {
		
	}

}
