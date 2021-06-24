//
// @(#) 1.1 autoFVT/src/jaxws/badprovider/wsfvt/server/SOAP12PayloadDataSourceBadPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/20/06 16:59:35 [8/8/12 06:55:23]
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

import javax.activation.DataSource;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.badprovider.wsfvt.common.Constants;

/**
 * Bad provider test - SOAP12/Any DataSource, invalid combination
 *
 */
@BindingType(value=SOAPBinding.SOAP12HTTP_BINDING)
@ServiceMode(value=Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace=Constants.WSDL_NAMESPACE,
		serviceName="SOAP12PayloadDataSourceService",
		portName="SOAP12PayloadDataSourcePort",
		wsdlLocation="WEB-INF/wsdl/SOAP12PayloadDataSource.wsdl")
public class SOAP12PayloadDataSourceBadPortImpl implements Provider<DataSource>{

	public DataSource invoke(DataSource arg0) {
		return PingProvider.invoke(arg0);
	}
}
