//
// @(#) 1.1 autoFVT/src/jaxws/badproxy/wsfvt/server/MTOMSoap12BadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/30/07 11:45:23 [8/8/12 06:56:17]
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
// 01/30/07   sedov       D417716            New file
//

package jaxws.badproxy.wsfvt.server;

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;

import jaxws.badproxy.wsfvt.common.mtomsoap12.MTOMSoap12Port;

/**
 * An SEI-based endpoint that declares use of SOAP1.2 MTOM and is deployed with
 * SOAP 1.2 wsdl. This is to validate that validatio picks up soap12=soap12mtom
 */
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING)
@WebService(portName="MTOMSoap12Port",
		serviceName="MTOMSoap12Service",
		targetNamespace="http://MTOMSoap12.common.wsfvt.badproxy.jaxws",
		endpointInterface="jaxws.badproxy.wsfvt.common.mtomsoap12.MTOMSoap12Port",
		wsdlLocation="WEB-INF/wsdl/MTOMSoap12.wsdl")
public class MTOMSoap12BadPortImpl implements  MTOMSoap12Port{

	public void ping(Holder<String> ping) {
		
	}

}
