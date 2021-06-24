//
// @(#) 1.1 autoFVT/src/jaxws/badprovider/wsfvt/server/PrivateConstructorBadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/20/06 16:59:33 [8/8/12 06:55:21]
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

import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import jaxws.badprovider.wsfvt.common.Constants;

/**
 * Bad provider test - correctly annotated by the default
 * constructor is private
 */
@BindingType(value=javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace=Constants.WSDL_NAMESPACE,
		serviceName="PrivateConstructorService",
		portName="PrivateConstructorPort",
		wsdlLocation="WEB-INF/wsdl/PrivateConstructor.wsdl")
public class PrivateConstructorBadPortImpl implements Provider<Source>{

	private PrivateConstructorBadPortImpl(){}
	
	public Source invoke(Source arg0) {
		return PingProvider.invoke(arg0);
	}

}
