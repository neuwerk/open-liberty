package jaxws.proxy.doclit.wsfvt.server;

import java.math.BigInteger;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import jaxws.proxy.common.Constants;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.CustomException;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.DocLitWrappedProxy;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.ExceptionTypeEnum;
import jaxws.proxy.doclit.wsfvt.doclitwrapped.MyComplexType;

/**
 * Endpoint for Document/literal-wrapped invocations. Will roundtrip messages
 * when appropriate. Will throw an exception if request to by the client.
 * 
 * @wsdl proxy_doclitwr.wsdl
 */

@WebService(serviceName = "ProxyDocLitWrappedService", portName = "ProxyDocLitWrappedPort", targetNamespace = "http://doclitwrapped.wsfvt.doclit.proxy.jaxws", endpointInterface = "jaxws.proxy.doclit.wsfvt.doclitwrapped.DocLitWrappedProxy", wsdlLocation = "WEB-INF/wsdl/proxy_doclitwr.wsdl")
public class ProxyDocLitWrappedPortImpl implements DocLitWrappedProxy {

	public void oneWayVoid() {
		// do nothing

	}

	public void oneWay(String onewayStr) {
		// do nothing
	}

	public void twoWayHolder(Holder<String> twoWayHolderStr,
			Holder<Integer> twoWayHolderInt) {
		// do nothing, roundtrip the message

	}

	public String twoWay(String twowayStr) {

		if (twowayStr.equals(Constants.THE_ID_STRING))
			return Constants.REPLY_WRAPPED;
		else
			return twowayStr;
	}

	public void twoWayInOut(Holder<String> twowayStr, BigInteger onewayInt) {
		// do nothing...just roundtrip the twoWayStr

	}

	public void twoWaySilly() {
		// do nothing...just testing to see if implementation can handle empty
		// wrapper objects
	}

	public void oneWayException(String exceptionMsg) {
		// in case we are requested to throw an exception
		if (Constants.THE_WSE_STRING.equals(exceptionMsg)) { throw new WebServiceException(
				Constants.THE_WSE_STRING); }
	}

	public void twoWayMulti(int a, Holder<String> c, MyComplexType b,
			Holder<MyComplexType> d, Holder<Integer> e, Holder<MyComplexType> f) {
		e.value = Constants.THE_INT;
		f.value = new MyComplexType();
		f.value.setA(42);
		f.value.setB(c.value);
	}

	public void twoWayOut(String in, Holder<String> out1,
			Holder<MyComplexType> out2) {
		out1.value = in;
		out2.value = new MyComplexType();
		out2.value.setA(42);
		out2.value.setB(in);
	}

	public String twoWayException(ExceptionTypeEnum exceptionType)
			throws CustomException {

		switch (exceptionType) {
		case UNCHECKED:
			return Integer.toString(10 / 0);
		case WSE:
			throw new WebServiceException(Constants.THE_WSE_STRING);
		case SOAP_FAULT_NO_DETAIL:
			SOAPFactory fac1;
			SOAPFault fault1 = null;

			try {
				fac1 = SOAPFactory.newInstance();

				fault1 = fac1.createFault();
				fault1.setFaultString(Constants.THE_FAULT_STRING);

			} catch (Exception e) {}

			throw new SOAPFaultException(fault1);			
		case SOAP_FAULT_WITH_DETAIL:
			SOAPFactory fac2;
			SOAPFault fault2 = null;

			String NS = ProxyDocLitWrappedPortImpl.class.getAnnotation(
					WebService.class).targetNamespace();
			QName qnTwoWayException = new QName(NS, Constants.THE_DETAIL_NAME);
			QName qnClientFault = new QName(NS, "Client");

			try {
				fac2 = SOAPFactory.newInstance();

				fault2 = fac2.createFault("TestSoapFault", qnClientFault);
				//fault2.setFaultActor("server");

				Detail detail = fault2.addDetail();
				SOAPElement twoWayException = detail
						.addChildElement(qnTwoWayException);
				twoWayException.setTextContent(Constants.THE_FAULT_STRING);
			} catch (Exception e) {}

			throw new SOAPFaultException(fault2);
		case WSDL_FAULT:
			throw new CustomException(Constants.THE_FAULT_STRING, "faultinfo");
		}

		return "twoWayException";
	}

	public String twoWayNonVoidHolder(String in, Holder<BigInteger> inout,
			Holder<String> out) {
		out.value = in;
		
		return in;
	}

	// soap:Action tests. 1x series tests for 2 operations with the same
	// soap action. 2x series tests 2 oeprations with no soap action
	// in both cases requests should be routed based on soap:body
	
	public String soapAction1A(String param1) {
		return "soapAction1A";
	}

	public String soapAction1B(String param1) {
		return "soapAction1B";
	}
	
	public String soapAction2A(String param1) {
		return "soapAction2A";
	}

	public String soapAction2B(String param1) {
		return "soapAction2B";
	}	
}
