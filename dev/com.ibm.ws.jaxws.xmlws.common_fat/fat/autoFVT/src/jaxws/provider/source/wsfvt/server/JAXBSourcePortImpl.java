//
// @(#) 1.1 WautoFVT/src/jaxws/provider/wsfvt/server/soap11/JAXBSourcePortImpl.java, WAS.websvcs.fvt, WSFP.WFVT 8/30/06 17:43:39 [12/5/06 09:08:05]
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.provider.common.Constants;
import jaxws.provider.wsfvt.common.ObjectFactory;
import jaxws.provider.wsfvt.common.StringArray;

/**
 * Test implementing a provider using JAXB programming model. Basically the test
 * is whether the JAXBSource can be handled
 */

@ServiceMode(value = Service.Mode.PAYLOAD)
@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
@WebServiceProvider(
		targetNamespace = Constants.WSDL_NAMESPACE,
		serviceName = "SOAP11JAXBSourceService",
		portName = "SOAP11JAXBSourcePort",			
		wsdlLocation = "WEB-INF/wsdl/Provider_SOAP11JAXBSource.wsdl")
public class JAXBSourcePortImpl implements Provider<Source> {

	public Source invoke(Source arg) {

		JAXBSource jbsrc = null;
		try {
			// unmarshall request into JAXB Object
			JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller u = jc.createUnmarshaller();
			u.unmarshal(arg);

			// create reply message
			ObjectFactory fac = new ObjectFactory();
			StringArray sa = fac.createStringArray();
			JAXBElement<StringArray> twoWayReply = fac.createTwoWayResponse(sa);
			sa.getValue().add("test_message");
			jbsrc = new JAXBSource(jc, twoWayReply);
		} catch (Exception e) {
			throw new WebServiceException(e);
		}

		return jbsrc;
	}
}
