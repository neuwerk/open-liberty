//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/AdminAgentTopologyReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:31 [8/8/12 06:40:30]
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
//  10/31/08    jramos       559143               Incorporate Simplicity
//

package com.ibm.ws.wsfvt.build.tools.configRequirements;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.WebSphereTopologyType;

/**
 * Require an AdminAgent topology.
 * 
 * @author jramos
 */
public class AdminAgentTopologyReq implements Requirement <Boolean>{
    
    private boolean requireAATopology = true;
    private Cell cell;
    
    public AdminAgentTopologyReq(Cell cell) {
        this.cell = cell;
    }

    public Boolean getReqVal() {
        return new Boolean(this.requireAATopology);
    }

    public String getRequirementDescr() {
        final String req = "AdminAgent Topology";
        return req;
    }

    public boolean requirementMet() throws Exception {
        if(this.requireAATopology) {
            return (cell.getTopologyType() == WebSphereTopologyType.ADMIN_AGENT);
        }
        return true;
    }

    public void setRequirement(boolean reqVal) {
        this.requireAATopology = reqVal;
    }
    
}
