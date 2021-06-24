//
// @(#) 1.2 SautoFVT/src/com/ibm/ws/wsfvt/build/tools/tcpmon/TCPMonEvenListener.java, WAS.websvcs.fvt, SAML10.SFVT, i4b0913.10 1/8/08 11:21:13 [4/7/09 22:11:21]
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
// 09/10/09 gkuo        612335           define a single location for TCPMON_LISTENER_PORT 
//
package com.ibm.ws.wsfvt.build.tools.tcpmon;

/**
 * Listener interface for intercepting HTTP messages
 */
public interface TCPMonEvenListener {

       public static int TCPMON_LISTENER_PORT = 9999; // define a common TCPMON Listener port so, we can change it easily.
	/**
	 * Listener notification that event has been received
	 * @param isInbound message direction, returnes true when receiving response
	 * @param event byte content of the HTTP payload, http headers are removed
	 * @return message to send. a null means send what you have
	 */
	public String receiveEvent(boolean isInbound, String event);
}
