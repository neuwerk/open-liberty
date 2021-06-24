package soapendpoint.wsfvt.server;

import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;

/**
 * Tests that a Source Provider can be installed.
 */
@WebServiceProvider
@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class SOAPBindingSourceProvider implements javax.xml.ws.Provider<Source> {

	public Source invoke(Source arg0) {
		return arg0;
	}

}
