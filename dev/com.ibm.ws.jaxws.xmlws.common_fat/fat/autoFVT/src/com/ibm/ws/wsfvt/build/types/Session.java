/*
 * @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/types/Session.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/18/07 15:19:34 [8/8/12 06:40:29]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 09/18/2007  jramos      467003             New File
 */
package com.ibm.ws.wsfvt.build.types;

import java.util.Date;

public class Session {

    private String sessionID;
    
    public Session() {
        Date date = new Date();
        sessionID = "" + date.getTime();
    }
    
    public void setSessionID(String sessionID) {
        // only set this session if given a valid ID
        if(sessionID != null && !sessionID.startsWith("${")) {
            this.sessionID = sessionID;
        }
    }
    
    public String getSessionID() {
        return this.sessionID;
    }
    
    public String toString() {
        return this.sessionID;
    }
}
