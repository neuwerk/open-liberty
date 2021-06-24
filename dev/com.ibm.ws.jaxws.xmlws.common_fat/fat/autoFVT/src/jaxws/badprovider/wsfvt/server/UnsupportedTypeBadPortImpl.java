//
// @(#) 1.1 autoFVT/src/jaxws/badprovider/wsfvt/server/UnsupportedTypeBadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/20/06 16:59:37 [8/8/12 06:55:23]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 07/31/06 sedov       LIDB3296.42     New File
//

package jaxws.badprovider.wsfvt.server;

import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import jaxws.badprovider.wsfvt.common.Constants;

/**
 * Bad provider test - Provider is not typed
 *
 */
@BindingType(value=javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace=Constants.WSDL_NAMESPACE,
		serviceName="UnsupportedTypeService",
		portName="UnsupportedTypePort",
		wsdlLocation="WEB-INF/wsdl/UnsupportedType.wsdl")
public class UnsupportedTypeBadPortImpl implements Provider<Exception>{

	public Exception invoke(Exception arg0) {
		return new Exception (PingProvider.invoke((Object) arg0).toString());
	}

}
