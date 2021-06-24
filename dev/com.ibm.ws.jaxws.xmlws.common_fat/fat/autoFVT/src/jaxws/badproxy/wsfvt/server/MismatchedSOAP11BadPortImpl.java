//
// @(#) 1.1 autoFVT/src/jaxws/badproxy/wsfvt/server/MismatchedSOAP11BadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/27/06 09:03:39 [8/8/12 06:55:25]
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

import jaxws.badproxy.wsfvt.common.mismatchedsoap11.MismatchedSOAP11Port;

/**
 * An SEI-based enndpoint that declares use of SOAP1.2 but is deployed with a
 * wsdl that uses SOAP1.1
 * 
 */
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
@WebService(portName="MismatchedSOAP11Port",
		serviceName="MismatchedSOAP11Service",
		targetNamespace="http://MismatchedSOAP11.common.wsfvt.badproxy.jaxws",
		endpointInterface="jaxws.badproxy.wsfvt.common.mismatchedsoap11.MismatchedSOAP11Port",
		wsdlLocation="WEB-INF/wsdl/MismatchedSOAP11.wsdl")
public class MismatchedSOAP11BadPortImpl implements MismatchedSOAP11Port{

	public void ping(Holder<String> ping) {
		
	}

}
