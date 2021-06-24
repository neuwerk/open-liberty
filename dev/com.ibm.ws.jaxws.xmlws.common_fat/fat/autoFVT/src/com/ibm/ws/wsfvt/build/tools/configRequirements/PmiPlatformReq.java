//
// @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/PmiPlatformReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:50 [8/8/12 06:40:30]
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
//  07/29/08    jramos       540203               Add iSeries to nonPmiPlatforms
//  08/07/08    jramos       540203.1             Back out 540203 changes
//  10/31/08    jramos       559143               Incorporate Simplicity
//

package com.ibm.ws.wsfvt.build.tools.configRequirements;

import java.util.HashSet;
import java.util.Set;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.OperatingSystem;

/**
 * Require a platform that supports a JVM Pmi
 * 
 * @author jramos
 */
public class PmiPlatformReq implements Requirement <Boolean> {

    // platforms that do not support JVM Pmi
    private OperatingSystem[] nonPmiPlatforms = { OperatingSystem.HP, OperatingSystem.SOLARIS };
    
    private Boolean requirePmiPlatform = true;
    private Cell cell = null;
    
    public PmiPlatformReq(Cell cell) {
        this.cell = cell;
    }

    public Boolean getReqVal() {
        return this.requirePmiPlatform;
    }

    public String getRequirementDescr() {
        final String req = "JVM PMI Capable Platform";
        return req;
    }

    public boolean requirementMet() throws Exception {
        if(this.requirePmiPlatform) {
            Set<OperatingSystem> nonPmiPlats = new HashSet<OperatingSystem>();
            for(OperatingSystem os : nonPmiPlatforms) {
                nonPmiPlats.add(os);
            }
            Set<Node> nodes = cell.getNodes();
            Set<OperatingSystem> nodePlatforms = new HashSet<OperatingSystem>();
            for(Node node : nodes) {
                nodePlatforms.add(node.getMachine().getOperatingSystem());
            }
            int initNumPlats = nodePlatforms.size();
            nodePlatforms.removeAll(nonPmiPlats);
            int finalNumPlats = nodePlatforms.size();
            if(finalNumPlats < initNumPlats) {
                // one or more nodes were on non Pmi platforms
                return false;
            }
        }
        return true;
    }

    public void setRequirement(boolean reqVal) {
        this.requirePmiPlatform = reqVal;
    }
    
}
