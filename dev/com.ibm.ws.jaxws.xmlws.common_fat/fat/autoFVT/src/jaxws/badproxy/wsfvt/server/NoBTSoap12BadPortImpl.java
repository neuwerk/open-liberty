//
// @(#) 1.1 autoFVT/src/jaxws/badproxy/wsfvt/server/NoBTSoap12BadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/30/07 11:45:24 [8/8/12 06:56:17]
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
import javax.xml.ws.Holder;

import jaxws.badproxy.wsfvt.common.nobtsoap12.NoBTSoap12Port;


/**
 * An SEI-based enndpoint that does not declare [at]BindingType, this should
 * confuse the validator into assuming default value for BT of soap11, but the
 * wsdl is soap1.2
 */
@WebService(portName="NoBTSoap12Port",
		serviceName="NoBTSoap12Service",
		targetNamespace="http://NoBTSoap12.common.wsfvt.badproxy.jaxws",
		endpointInterface="jaxws.badproxy.wsfvt.common.nobtsoap12.NoBTSoap12Port",
		wsdlLocation="WEB-INF/wsdl/NoBTSoap12.wsdl")
public class NoBTSoap12BadPortImpl implements NoBTSoap12Port{

	public void ping(Holder<String> ping) {
		
	}

}
