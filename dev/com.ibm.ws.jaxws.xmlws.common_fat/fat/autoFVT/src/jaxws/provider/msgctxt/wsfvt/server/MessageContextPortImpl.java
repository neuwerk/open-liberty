//
// @(#) 1.1 autoFVT/src/jaxws/provider/msgctxt/wsfvt/server/MessageContextPortImpl.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/3/06 10:06:43 [8/8/12 06:55:26]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date      UserId      Defect          Description
// ----------------------------------------------------------------------------
// 10/02/06  sedov       394989          New File
//

package jaxws.provider.wsfvt.server;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;

import jaxws.provider.wsfvt.common.Constants;

/**
 * Server Side tests for reading and writing MessageContext properties from
 * Provider endpoints. Each test will attempt to read or write to a specific
 * MessageContext property
 * 
 * The client will receive either a twoWayResponse with some #text# describing
 * the outcome or a WebServiceException indicating the test failed.
 */
@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(
		targetNamespace = Constants.WSDL_NAMESPACE,
		serviceName = "MessageContextService",
		portName = "MessageContextPort",
		wsdlLocation = "WEB-INF/wsdl/Provider_MessageContext.wsdl")
public class MessageContextPortImpl implements Provider<Source> {

	@Resource
	private WebServiceContext ctxt;

	public Source invoke(Source arg0) {

		String message = Constants.toString(arg0);
		String command = Constants.getValueBetweenHashes(message);

		String ret = null;
		try {
			if (command == null || command.length() == 0) {
				throw new WebServiceException("Empty operation name specified");
			} else if (ctxt == null){
				ret = "WebServiceContext is null";
			} else if (ctxt.getMessageContext() == null){
				ret = "MessageContext is null";
			} else if (command.equals("doServletContext_Read")) {
				ret = doServletContext_Read(ctxt);
			} else if (command.equals("doServletRequest_Read")) {
				ret = doServletRequest_Read(ctxt);
			} else if (command.equals("doServletResponse_Read")) {
				ret = doServletResponse_Read(ctxt);
			} else if (command.equals("doServletSession_Read")) {
				ret = doServletSession_Read(ctxt);
			} else if (command.equals("doResponseCode_Read")) {
				ret = doResponseCode_Read(ctxt);
			} else if (command.equals("doResponseHeaders_Read")) {
				ret = doResponseHeaders_Read(ctxt);
			} else if (command.equals("doRequestMethod_Read")) {
				ret = doRequestMethod_Read(ctxt);
			} else if (command.equals("doRequestHeaders_Read")) {
				ret = doRequestHeaders_Read(ctxt);
			} else if (command.equals("doWsdlDescription_Read")) {
				ret = doWsdlDescription_Read(ctxt);				
			} else if (command.equals("doWsdlService_Read")) {
				ret = doWsdlService_Read(ctxt);	
			} else if (command.equals("doWsdlPort_Read")) {
				ret = doWsdlPort_Read(ctxt);	
			} else if (command.equals("doWsdlInterface_Read")) {
				ret = doWsdlInterface_Read(ctxt);	
			} else if (command.equals("doWsdlOperation_Read")) {
				ret = doWsdlOperation_Read(ctxt);					
			} else {
				throw new WebServiceException(
						"Invalid operation name specified: " + command);
			}
		} catch (Exception e) {
			// catch any exceptions thrown from below
			throw new WebServiceException(e);
		}

		// embed the results in a twoWayResponse message
		if (ret != null) {
			ret = Constants.TWOWAY_MSG_RESPONSE.replace("test_message", "#"
					+ ret + "#");
		}

		return Constants.toStreamSource(ret);
	}

	/**
	 * Test that MessageContext.SERVLET_CONTEXT property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doServletContext_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		// read the property
		ServletContext sc = (ServletContext) mc
				.get(MessageContext.SERVLET_CONTEXT);

		// verify the property value is not null
		if (sc == null)
			throw new Exception("SERVLET_CONTEXT is null");

		return "MessageContext.SERVLET_CONTEXT Read OK";
	}

	/**
	 * Test that MessageContext.SERVLET_REQUEST property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doServletRequest_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		HttpServletRequest sr = (HttpServletRequest) mc
				.get(MessageContext.SERVLET_REQUEST);

		if (sr == null)
			throw new Exception("SERVLET_REQUEST is null");

		return "MessageContext.SERVLET_CONTEXT Read OK";
	}

	/**
	 * Test that MessageContext.SERVLET_RESPONSE property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doServletResponse_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		HttpServletResponse sr = (HttpServletResponse) mc
				.get(MessageContext.SERVLET_RESPONSE);

		if (sr == null)
			throw new Exception("SERVLET_RESPONSE is null");

		return "MessageContext.SERVLET_RESPONSE Read OK";
	}

	/**
	 * Test that MessageContext.SERVLET_SESSION property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doServletSession_Read(WebServiceContext ctxt) throws Exception {
     /* TODO uncomment
		MessageContext mc = getContext();

		HttpSession ss = (HttpSession) mc.get(MessageContext.SERVLET_SESSION);

		if (ss == null)
			throw new Exception("SERVLET_SESSION is null");

		return "MessageContext.SERVLET_SESSION Read OK";
		*/ return null;
	}

	/**
	 * Test that MessageContext.HTTP_RESPONSE_CODE property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doResponseCode_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		Integer rc = (Integer) mc.get(MessageContext.HTTP_RESPONSE_CODE);

		if (rc == null)
			throw new Exception("HTTP_RESPONSE_CODE is null");

		return "MessageContext.HTTP_RESPONSE_CODE Read OK";
	}

	/**
	 * Test that MessageContext.HTTP_RESPONSE_HEADERS property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doResponseHeaders_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		Map<String, List<String>> headers = (Map<String, List<String>>) mc
				.get(MessageContext.HTTP_RESPONSE_HEADERS);

		if (headers == null)
			throw new Exception("HTTP_RESPONSE_HEADERS is null");

		return "MessageContext.HTTP_RESPONSE_HEADERS Read OK";
	}

	/**
	 * Test that MessageContext.HTTP_REQUEST_METHOD property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doRequestMethod_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		String method = (String) mc.get(MessageContext.HTTP_REQUEST_METHOD);

		if (method == null)
			throw new Exception("HTTP_REQUEST_METHOD is null");

		// validate variable
		if (method.compareTo("POST") != 0)
			throw new Exception("Unexpected HTTP_REQUEST_METHOD: " + method);
		
		return "MessageContext.HTTP_REQUEST_METHOD Read OK";
	}

	/**
	 *  Test that MessageContext.HTTP_REQUEST_HEADERS property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doRequestHeaders_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		Map<String, List<String>> headers = (Map<String, List<String>>) mc
				.get(MessageContext.HTTP_REQUEST_HEADERS);

		if (headers == null)
			throw new Exception("HTTP_REQUEST_HEADERS is null");

		// validate property
		if (headers.get("SOAPAction") == null)
			throw new Exception("Unexpected HTTP_REQUEST_HEADERS. SOAPAction header is null");
		
		return "MessageContext.HTTP_REQUEST_HEADERS Read OK";
	}

	/**
	 * Test that MessageContext.WSDL_DESCRIPTION property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doWsdlDescription_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		URI uri = (URI) mc
				.get(MessageContext.WSDL_DESCRIPTION);

		if (uri == null)
			throw new Exception("WSDL_DESCRIPTION is null");

		// validate the property
		if (!uri.toString().equalsIgnoreCase(Constants.MESSAGECONTEXT_SOAP11_ADDRESS + Constants.WSDL_SUFFIX))
			throw new Exception("Unexpected WSDL_DESCRIPTION: " + uri);		
		
		return "MessageContext.WSDL_DESCRIPTION Read OK";
	}

	/**
	 * Test that MessageContext.WSDL_SERVICE property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doWsdlService_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		QName qn = (QName) mc.get(MessageContext.WSDL_SERVICE);

		if (qn == null)
			throw new Exception("WSDL_SERVICE is null");

		// validate the property
		if (!qn.equals(new QName(Constants.WSDL_NAMESPACE, "ProviderService")))
			throw new Exception("Unexpected WSDL_SERVICE: " + qn);			
		
		return "MessageContext.WSDL_SERVICE Read OK";
	}

	/**
	 * Test that MessageContext.WSDL_PORT property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doWsdlPort_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		QName qn = (QName) mc.get(MessageContext.WSDL_PORT);

		if (qn == null)
			throw new Exception("WSDL_PORT is null");

		// validate the property
		if (!qn.equals(new QName(Constants.WSDL_NAMESPACE, "ProviderPort")))
			throw new Exception("Unexpected WSDL_PORT: " + qn);			
		
		return "MessageContext.WSDL_PORT Read OK";
	}

	/**
	 * Test that MessageContext.WSDL_INTERFACE property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doWsdlInterface_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		QName qn = (QName) mc.get(MessageContext.WSDL_INTERFACE);

		if (qn == null)
			throw new Exception("WSDL_INTERFACE is null");

		// validate the property
		if (!qn.equals(new QName(Constants.WSDL_NAMESPACE, "ProviderPortType")))
			throw new Exception("Unexpected WSDL_INTERFACE: " + qn);		
		
		return "MessageContext.WSDL_INTERFACE Read OK";
	}

	/**
	 * Test that MessageContext.WSDL_OPERATION property can be read from Provider
	 * @return
	 * @throws Exception
	 */
	private String doWsdlOperation_Read(WebServiceContext ctxt) throws Exception {

		MessageContext mc = getContext();

		QName qn = (QName) mc.get(MessageContext.WSDL_OPERATION);

		if (qn == null)
			throw new Exception("WSDL_OPERATION is null");
		
		// validate the property
		if (!qn.equals(new QName(Constants.WSDL_NAMESPACE, "twoWay")))
			throw new Exception("Unexpected WSDL_OPERATION: " + qn);

		return "MessageContext.WSDL_OPERATION Read OK";
	}
	
	/**
	 * Auxiliary method used for obtaining the message context
	 * 
	 * @return
	 * @throws Exception
	 */
	private MessageContext getContext() throws Exception {
		if (ctxt == null)
			throw new Exception("WebServicesContext is null");

		MessageContext mc = ctxt.getMessageContext();
		if (mc == null)
			throw new Exception("WebServicesContext.getMessageContext is null");
		return mc;
	}
}
