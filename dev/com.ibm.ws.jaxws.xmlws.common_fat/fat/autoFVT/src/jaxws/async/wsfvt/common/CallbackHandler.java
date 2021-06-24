//
// @(#) 1.8 autoFVT/src/jaxws/async/wsfvt/common/CallbackHandler.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/11/07 15:22:10 [8/8/12 06:55:19]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect           Description
// ----------------------------------------------------------------------------
// 09/18/06 sedov       LIDB3296.38      New File
// 09/26/06 sedov       393143           Changed Constants import to jaxws.async
// 05/11/07 sedov       438719           Added more trace
//
package jaxws.async.wsfvt.common;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

/**
 * Generic Async callback handler. The get method emulates Response.get by
 * throwning an exception if one is received.
 */
public class CallbackHandler<T> implements AsyncHandler<T> {

	private String id = "CallbackHandler"; // default id if default ctor is used
	
	private T response = null;

	private Response<T> resp = null;

	private Exception exception = null;

	public CallbackHandler() {
	}

	public CallbackHandler(String id) {
		this.id = id;
	}
	
	public void handleResponse(Response<T> response) {
		System.out.println("CallbackHandler[" + id + "].handleResponse()");
		
		try {
			T res = (T) response.get();
			this.response = res;
			this.resp = response;
			System.out.println("CallbackHandler[" + id + "].handleResponse Response.get()=" + res);
			System.out.println(BeanProfiler.profile(res));
		} catch (Exception e) {
			System.out.println("CallbackHandler[" + id + "].handleResponse Response.get()=" + e);
			this.exception = e;
		}
	}

	public Response<T> getResponse() {
		return this.resp;
	}

	public T get() throws Exception {
		
		if (exception != null){
			System.out.println("CallbackHandler[" + id + "].get() = exception");
			throw exception;
		} else {
			System.out.println("CallbackHandler[" + id + "].get() = value");
			return this.response;
		}
	}

}
