//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/ClusterReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:36 [8/8/12 06:40:30]
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
 * Require a cluster
 * 
 * @author jramos
 */
public class ClusterReq implements Requirement <Boolean>{
    
    private boolean requireCluster = true;
    private Cell cell = null;
    
    public ClusterReq(Cell cell) {
        this.cell = cell;
    }

    public Boolean getReqVal() {
        return this.requireCluster;
    }

    public String getRequirementDescr() {
        final String req = "cluster";
        return req;
    }

    public boolean requirementMet() throws Exception {
        if (this.requireCluster
                && !(this.cell.getTopologyType() == WebSphereTopologyType.ND && cell.getClusters() != null && cell.getClusters().size() > 0)) {
            return false;
        }
        return true;
    }

    public void setRequirement(boolean reqVal) {
        this.requireCluster = reqVal;
    }
    
}
