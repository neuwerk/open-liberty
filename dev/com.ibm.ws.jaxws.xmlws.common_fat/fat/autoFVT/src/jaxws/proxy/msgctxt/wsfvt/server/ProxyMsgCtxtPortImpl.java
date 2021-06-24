//
//  @(#) 1.6 autoFVT/src/jaxws/proxy/msgctxt/wsfvt/server/ProxyMsgCtxtPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/24/07 14:13:45 [8/8/12 06:55:35]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date       UserId      Defect          Description
// ----------------------------------------------------------------------------
// 09/11/06   sedov       LIDB3296.42-01     New File
// 11/10/06   sedov       404343             Changed to use INOUT holder
// 01/24/07   sedov       416630             Fixed wsContextPropRead

package jaxws.proxy.msgctxt.wsfvt.server;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import jaxws.proxy.msgctxt.wsfvt.msgctxt.MessageContextProxy;

@WebService(serviceName = "MsgCtxtService",
		portName = "MsgCtxtPort",
		targetNamespace = "http://msgctxt.wsfvt.msgctxt.proxy.jaxws",
		endpointInterface = "jaxws.proxy.msgctxt.wsfvt.msgctxt.MessageContextProxy", wsdlLocation = "WEB-INF/wsdl/proxy_msgctxt.wsdl")
public class ProxyMsgCtxtPortImpl implements MessageContextProxy {

	@Resource
	WebServiceContext ctxt;

	// flag that indicates that portConstruct was called
	private boolean postConstruct = false;

	@PostConstruct
	public void init() {
		postConstruct = true;
	}

	// test for PostConstruct
	public boolean atPostConstructAvailable() {
		return this.postConstruct;
	}

	// test for Resource
	public boolean atResourceAvailable() {
		return (ctxt != null);
	}

	public void wsContextPropRead(Holder<String> propertyName,
			Holder<String> value, Holder<String> type, Holder<Boolean> isFound) {

		System.out.println(">> wsContextPropRead(" + propertyName.value + ")");
		
		MessageContext msgCtxt = ctxt.getMessageContext();

		if (msgCtxt != null) {
			isFound.value = msgCtxt.containsKey(propertyName.value);
			Object val = msgCtxt.get(propertyName.value);

			System.out.println("msgCtxt.containsKey=" + isFound.value);
			System.out.println("msgCtxt.get=" + val);
			
			if (val != null){
				type.value = val.getClass().getName();
				value.value = val.toString();
			}
		}
		
		System.out.println("<< wsContextPropRead()");
	}

}
