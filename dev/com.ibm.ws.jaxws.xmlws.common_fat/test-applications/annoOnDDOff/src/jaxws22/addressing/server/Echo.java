//A test service for addressing configuration

//Package does not match the eclipse directory. Needs to be that way
// so webservice.xml and web.xml can be easily recycled across
// multiple services.

package jaxws22.addressing.server; // don't change this package

import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.*;
import javax.xml.namespace.QName;
import javax.xml.ws.*;
import javax.xml.ws.soap.*;
import javax.xml.ws.handler.*;
import javax.xml.ws.handler.soap.*;
import javax.annotation.*;




// dd will override all settings if that's working.
@WebService()
@Addressing(enabled = true, required = true, responses = AddressingFeature.Responses.NON_ANONYMOUS)
public class Echo {
	@Resource()
	WebServiceContext wsc;

	public String echo(String in) throws java.lang.Exception {
		System.out.println("annoOnDDOff echo called with arg:" + in);

		// send back the request headers if asked for them
		// this is an undocumented an unsupported use of the handler extensions
		// FIS that
		// allows direct access to headers. It's not supported in an impl but it
		// works.
		// It's easier than adding a handler, which is the traditional approach.
		// ref http://www-01.ibm.com/support/docview.wss?rs=180&uid=swg1PK84170
		if (in.contains("SEND_BACK_MY_HEADERS")) {
			MessageContext mcx = wsc.getMessageContext();
			StringBuffer sb = new StringBuffer("");
			try {
				
		        List headerList = (List)mcx.get("org.apache.cxf.headers.Header.list");
	            Method mthd=null; 
	            for (int i=0;i<headerList.size();i++) {	                
	                Object header=headerList.get(i);
	            	if (mthd==null) {
	            		mthd=header.getClass().getMethod("getName", null);
	            	}	            	
	            	QName headerName = (QName)mthd.invoke(header, null);
	            	
	                sb.append("inbound header:" + "wsa:"+headerName.getLocalPart()+"\n");
	            }
			}catch(Exception e) {
				e.printStackTrace(System.out);
			}
			
			
			System.out.println("annoOnDDOff echo called soap headers:" + sb.toString());
			return sb.toString();
		} else {
			return "server replies: " + in;
		}
	}
}
