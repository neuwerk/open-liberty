//
// @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/MixedCellReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/18/09 14:40:01 [8/8/12 06:40:30]
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
//  08/13/09    jramos       606983               Correct logic
//

package com.ibm.ws.wsfvt.build.tools.configRequirements;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.WebSphereTopologyType;
import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.websphere.simplicity.product.InstalledWASProduct;
import com.ibm.websphere.simplicity.product.InstalledWASProduct.WASProductID;

/**
 * Require a mixed cell
 * 
 * @author jramos
 */
public class MixedCellReq implements Requirement <Map<String, Boolean>>{
    
    private boolean requireMixedCell = true;
    private boolean requireCurrentLevel = false;
    private boolean requirePyxisNode = false;
    private boolean requireWSFPNode = false;
    private boolean require61Node = false;
    private Cell cell = null;
    
    public MixedCellReq(Cell cell) {
        this.cell = cell;
    }

    public Map<String, Boolean> getReqVal() {
        Map<String, Boolean> values = new HashMap<String, Boolean>();
        values.put("mixedCell", requireMixedCell);
        values.put("currentLevel", requireCurrentLevel);
        values.put("pyxisNode", requirePyxisNode);
        values.put("wsfpNode", requireWSFPNode);
        values.put("61Node", require61Node);
        return values;
    }

    public String getRequirementDescr() {
        String req = "Mixed Cell";
        if(requireCurrentLevel) {
            req += (", Current Level Node");
        }
        if(requirePyxisNode) {
            req += (", Pyxis Node");
        }
        if(requireWSFPNode) {
            req += (", WSFP Node");
        }
        if(require61Node) {
            req += (", 6.1 Node");
        }
        return req;
    }

    public boolean requirementMet() throws Exception {
        if(this.cell.getTopologyType() != WebSphereTopologyType.ND) {
            return false;
        }
        Set<Node> nodes = cell.getNodes();
        boolean currentLevelReqMet = !this.requireCurrentLevel;
        boolean pyxisReqMet = !this.requirePyxisNode;
        boolean wsfpReqMet = !this.requireWSFPNode;
        boolean sixOneReqMet = !this.require61Node; 
        for(Node node : nodes) {
            if(node.getBaseProductVersion().equals(node.getCell().getManager().getNode().getBaseProductVersion())
                && node.getBaseProductVersion().compareToPartial(new WebSphereVersion("7.0")) >= 0)
                currentLevelReqMet = true;
            if(node.getBaseProductVersion().toString().startsWith("7.0"))
                pyxisReqMet = true;
            else if(node.getBaseProductVersion().toString().startsWith("6.1")) {
                Set<InstalledWASProduct> products = node.getInstalledWASProducts();
                for(InstalledWASProduct product : products) {
                    if(product.getProductId().equals(WASProductID.WEBSERVICES)) {
                        wsfpReqMet = true;
                        continue;
                    }
                }
                sixOneReqMet = true;
            }
        }
        return (currentLevelReqMet && pyxisReqMet && wsfpReqMet && sixOneReqMet);
    }

    public void setReqMixedCell(boolean reqVal) {
        this.requireMixedCell = reqVal;
    }
    
    public void setReqPyxisNode(boolean reqVal) {
        this.requirePyxisNode = reqVal;
    }
    
    public void setReqWSFPNode(boolean reqVal) {
        this.requireWSFPNode = reqVal;
    }
    
    public void setReq61Node(boolean reqVal) {
        this.require61Node = reqVal;
    }
    
    public void setReqCurrentLevelNode(boolean reqVal) {
        this.requireCurrentLevel = reqVal;
    }
}
