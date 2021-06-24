package jaxws.proxy.rpclit.wsfvt.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import jaxws.proxy.common.Constants;
import jaxws.proxy.rpclit.wsfvt.rpclit.*;

/**
 * Endpoint for RPC/literal invocations. Will roundtrip messages when
 * appropriate. Will throw an exception if request to by the client.
 * 
 * @wsdl: proxy_rpclit.wsdl
 */
@WebService(serviceName = "ProxyRpcLitService", portName = "ProxyRpcLitPort", targetNamespace = "http://rpclit.wsfvt.rpclit.proxy.jaxws", endpointInterface = "jaxws.proxy.rpclit.wsfvt.rpclit.RpcLitProxy", wsdlLocation = "WEB-INF/wsdl/proxy_rpclit.wsdl")
@BindingType(value = SOAPBinding.SOAP11HTTP_BINDING)
public class ProxyRpcLitPortImpl implements RpcLitProxy {

	public void oneWayEmpty() {
		// do nothing
	}

	public void oneWay(String allByMyself) {

		// do nothing, unless requested to send an exception
		if (allByMyself.equals(Constants.THE_WSE_STRING)) { throw new WebServiceException(
				Constants.THE_WSE_STRING); }
	}

	public String twoWaySimple() {
		return Constants.THE_STRING;
	}

	public String twoWay(String myString, int myInt) {
		return myString;
	}

	public void twoWayReturn(Holder<String> myString, int myInt) {
		// do nothing
	}

	public void twoWayInOut(Holder<String> myString, Holder<Integer> myInt)
			throws SimpleFault {
		// if request is to throw an exception then throw it
		// otherwise just roundtrip the messages
		if (myString.value.equals(Constants.THE_FAULT_STRING)) {
			throw new SimpleFault("message", myString.value);
		} else if (myString.value.equals(Constants.THE_RETURN_NULL_STRING)) {
			// return a null for one of the params...expecting the client to
			// throw a WSE
			myString.value = null;
		}
	}

	public FinancialOperation finop(FinancialOperation request) {

		// polymorphics test. if input amount is positive
		// its a deposit
		FinancialOperation response = null;
		if (request.getAmount() >= 0) {
			response = new Deposit();
			response.setAmount(request.getAmount());
			((Deposit) response).setStatus("deposited");
		} else {
			response = new Withdraw();
			response.setAmount(request.getAmount() * -1);
			((Withdraw) response).setMemo("withdrawn");
		}// */

		return response;
	}

	public void twoWayVoid() {

	}
}
