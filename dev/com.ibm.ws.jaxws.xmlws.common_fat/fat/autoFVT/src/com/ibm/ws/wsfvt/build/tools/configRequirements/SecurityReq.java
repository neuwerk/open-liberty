//
// @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/SecurityReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:53 [8/8/12 06:40:30]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
//  Date        Author       Feature/Defect       Description
//  11/14/07    jramos       483501               New File
//  01/08/08    sedov        490688               handle require security disabled case
//  10/31/08    jramos       559143               Incorporate Simplicity

package com.ibm.ws.wsfvt.build.tools.configRequirements;

import com.ibm.websphere.simplicity.Cell;

/**
 * Require security to be enabled
 * 
 * @author jramos
 */
public class SecurityReq implements Requirement <Boolean>{
    
    private boolean requireSecuityEnabled = true;
    private Cell cell = null;
    
    public SecurityReq(Cell cell) {
        this.cell = cell;
    }

    public Boolean getReqVal() {
        return this.requireSecuityEnabled;
    }

    public String getRequirementDescr() {
        final String req = "WebSphere Security";
        return req;
    }

    public boolean requirementMet() throws Exception {
    	// for this requirement to be met, security requirement flag
    	// must match security enablement at runtime. Else requirement is
    	// not met
        return (this.requireSecuityEnabled == this.cell.getSecurityConfiguration().getGlobalSecurityDomain().isGlobalSecurityEnabled());
    }

    public void setReqSecurityEnabled(boolean reqVal) {
        this.requireSecuityEnabled = reqVal;
    }
    
}
