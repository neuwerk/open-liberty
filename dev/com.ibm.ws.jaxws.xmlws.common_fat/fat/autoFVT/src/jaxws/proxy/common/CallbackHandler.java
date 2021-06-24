//
// @(#) 1.1 WautoFVT/src/jaxws/proxy/wsfvt/common/CallbackHandler.java, WAS.websvcs.fvt, WSFPB.WFVT 8/31/06 17:12:07 [9/13/06 16:04:38]
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
//

package jaxws.proxy.common;

import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;


/**
 * Generic Async callback handler. The get method emulates Response.get by
 * throwning an exception if one is received. 
 */
public class CallbackHandler<T> implements AsyncHandler<T> {

	private T response = null;
	private Response<T> resp = null;
	
	private Exception exception = null;

	public void handleResponse(Response<T> response) {

		try {
			T res = (T) response.get();
			this.response = res;
			this.resp = response;
		} catch (Exception e) {
			this.exception = e;
		}
	}

	public Response<T> getResponse(){
		return this.resp;
	}
	
	public T get() throws Exception {

		if (exception != null)
			throw exception;
		return this.response;
	}
	
	/**
	 * Auxiliary method used to wait for a monitor for a certain amount of time
	 * before timing out
	 * 
	 * @param monitor
	 */
	public void waitBlocking(Future<?> monitor) throws Exception {
		// wait for request to complete
		int sec = Constants.CLIENT_MAX_SLEEP_SEC;
		while (!monitor.isDone()) {
			Thread.sleep(1000);
			sec--;
			if (sec <= 0) break;
		}

		if (sec <= 0)
			throw new Exception("Stopped waiting for Async response after "
					+ Constants.CLIENT_MAX_SLEEP_SEC + " sec");
	}	

}
