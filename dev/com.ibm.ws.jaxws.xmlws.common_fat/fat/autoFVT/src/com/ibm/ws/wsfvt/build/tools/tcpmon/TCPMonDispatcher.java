//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/tcpmon/TCPMonDispatcher.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/16/09 12:00:16 [8/8/12 06:56:43]
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
// 05/18/07 sedov       440313           New File
//

package com.ibm.ws.wsfvt.build.tools.tcpmon;

import java.util.HashMap;
import java.util.Map;

/**
 * Dispatcher is responsible for taking raw HTTP data and processing it into logical
 * HTTP headers and data objects, and interfacing with the listener
 */
class TCPMonDispatcher {
	private Map<String, String> httpHeaders = new HashMap<String, String>();
	private StringBuffer data = new StringBuffer();
	private String httpStatus = null;
	private String host = null;
	private int port = 0;
	private boolean inFlow = false;
	
	public TCPMonDispatcher(boolean inFlow, String host, int port){
		this.inFlow  = inFlow;
		this.host = host;
		this.port = port;
	}
	
	public void addHttpStatus(String status){
		this.httpStatus = status;
	}
	
	public void addHeader(String header){
		if (header.length() > 4){
			//System.out.println("Header: " + header);
			int sep = header.indexOf(":");
			String key = header.substring(0, sep).trim();
			String value = header.substring(sep + 1).trim();
			httpHeaders.put(key, value);
		}
	}
	
	public void addData(String data){
		//System.out.println("Data: " + data);
		this.data.append(data);
	}
	
	public void dispatch(TCPMonEvenListener listener){
		String theData = data.toString();
		
		if (listener != null) {
			try { 
				theData = listener.receiveEvent(inFlow, theData);
				
				// do not send empty data...so returning a null is the same
				// as returning theData
				if (theData == null || theData.length() == 0){
					theData = data.toString();
				} else {
					httpHeaders.put("Content-Length", Integer.toString(theData.length()));
				}
				
			} catch (Exception e) { 
				System.out.println("TCPMonDispatcher error invoking listener: " + e);
			}
		}
		
		data.setLength(0);
		data.append(theData);
	}
	
	public byte[] toHttpByteArray(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(httpStatus);
		
		if (httpHeaders.containsKey("Host")){
			buffer.append("Host: " + host + ":" + port + "\r\n");
			httpHeaders.remove("Host");
		}
		
		for (String key: httpHeaders.keySet()){
			String val = httpHeaders.get(key);
			
			buffer.append(key + ": " + val);
			
			if (!val.endsWith("\r\n")){
				buffer.append("\r\n");
			}
		}
		buffer.append("\r\n");
		buffer.append(this.data);
		
		//System.out.println("-------Dispatch---------");
		//System.out.println(buffer.toString());
		byte[] bytes = null;
                try{
                    bytes = buffer.toString().getBytes("ASCII");
                }catch( Exception e ){
                }
                return bytes;
	}
	
	public int getDataLength(){
		return data.length();
	}
	
	public boolean hasHttpData(){
		return (httpStatus != null && httpStatus.length() > 0);
	}
}
