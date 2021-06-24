//
// @(#) 1.2 WautoFVT/src/jaxws/provider/wsfvt/server/soap11/DOMSourcePortImpl.java, WAS.websvcs.fvt, WSFP.WFVT 8/30/06 17:00:35 [12/5/06 09:08:05]
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
// 08/31/06 sedov       LIDB3296-42.03  Beta drop

package jaxws.provider.source.wsfvt.server;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.provider.common.Constants;



/**
 * Test implementing a provider using DOM programming model. Basically the test
 * is whether the DOMSource can be handled
 */

@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
@WebServiceProvider(
		targetNamespace = Constants.WSDL_NAMESPACE,
		serviceName = "SOAP11DOMSourceService",
		portName = "SOAP11DOMSourcePort",		
		wsdlLocation = "WEB-INF/wsdl/Provider_SOAP11DOMSource.wsdl")
public class DOMSourcePortImpl implements Provider<Source> {

	public Source invoke(Source arg) {

		String payload = Constants.TWOWAY_MSG_RESPONSE;
		DOMSource reply = Constants.toDOMSource(payload);

		return reply;
	}
}
