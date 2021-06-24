/*
 *  @(#) 1.1 autoFVT/src/com/ibm/ws/wssecfvt/build/tools/MessageListener.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01 12/28/09 14:21:23 [8/8/12 06:32:40]
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5630-A36, 5630-A37, 5724-D18, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *  Date      Author      Fea/Def          Description
 *  ----------------------------------------------------------------------
 *  12/22/09  syed        PK67282.fvt      Added for tcpmon negative tests
 *
 */

package com.ibm.ws.wssecfvt.build.tools;

import com.ibm.ws.wsfvt.build.tools.tcpmon.TCPMonEvenListener;

/*
 * Implemtation of TCPMonEvenListener for intercepting HTTP messages.
 * Events without payload are not considered.
 */
public class MessageListener implements TCPMonEvenListener {
    
    String inMsg = null;

    String outMsg = null;

    /**
     * Listener notification that event has been received
     * @param isInbound message direction, returnes true when receiving response
     * @param event byte content of the HTTP payload, http headers are removed
     * @return message to send. a null means send what you have
     */
    public String receiveEvent(boolean isInbound, String event){
        System.out.println("=================================");
        if (isInbound) {
            System.out.println("Message in: ");
            inMsg = new String(event);
        } else {
            System.out.println("Message out: ");
            outMsg = new String(event);
        }
        System.out.println(event);
        System.out.println("=================================");

        // sent the message as-is
        return null;
    }

    public String getInboundMsg() {
        return inMsg;
    }

    public String getOutboundMsg() {
        return outMsg;
    }

}
