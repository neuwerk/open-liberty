//
// @(#) 1.3 WautoFVT/src/jaxws/dispatch/wsfvt/server/SOAP11DispatchPortImpl.java, WAS.websvcs.fvt, WSFPB.WFVT 8/24/06 10:31:23 [9/1/06 10:58:13]
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
// 05/31/06 sedov    LIDB3296.42        New File
// 08/23/06 sedov    LIDB3296-42.02     Beta Drop

package jaxws.dispatch.wsfvt.server;

import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.dispatch.wsfvt.common.Constants;


/**
 * Test for sending/receiving messages over SOAP 1.1 Binding. This
 * endpoint will receive messages and reformat them for the
 * delegate SourceProvider.
 */
@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
@WebServiceProvider(wsdlLocation = "WEB-INF/wsdl/DispatchSOAP11.wsdl",
                    serviceName="DispatchSOAP11",
					portName="DispatchSOAP11Port",
					targetNamespace=Constants.WSDL_NAMESPACE)
public class SOAP11DispatchPortImpl implements Provider<Source> {

	public Source invoke(Source input) throws WebServiceException {

		// read annotations
		BindingType bt =this.getClass().getAnnotation(BindingType.class);
		ServiceMode sm =this.getClass().getAnnotation(ServiceMode.class);
		
		// delegate handling to a shared implementation 
		SourceProvider srcprov = new SourceProvider(bt.value(), sm.value());
		return srcprov.invoke(input);
	}
}
