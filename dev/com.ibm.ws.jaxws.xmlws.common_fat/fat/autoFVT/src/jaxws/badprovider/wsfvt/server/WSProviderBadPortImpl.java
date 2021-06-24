package jaxws.badprovider.wsfvt.server;

import javax.jws.WebService;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;

/**
 * Bad provider test - endpoint does not carry the required WSP annotation
 * but carries the WebService (which is conflicting)
 */
@WebService(name="ProviderPortType", portName="WSProviderPort", serviceName="WSProviderService")
public class WSProviderBadPortImpl implements Provider<Source>{

	public Source invoke(Source arg) {
		return PingProvider.invoke(arg);
	}

}
