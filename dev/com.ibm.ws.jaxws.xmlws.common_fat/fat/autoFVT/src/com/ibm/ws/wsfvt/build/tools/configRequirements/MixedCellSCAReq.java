//
// @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/MixedCellSCAReq.java, WAS.websvcs.fvt.admin, WAS85.FVT, cf011231.01 12/6/10 23:21:40 [8/8/12 06:14:44]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
//  Date        Author       Feature/Defect       Description
//  12/06/2010    whsu       743-36552            prerequsite for mixedcell(v8 + scafp v7 
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
 * Require a mixed cell with v8 + v7 scafp
 * 
 */
public class MixedCellSCAReq implements Requirement <Map<String, Boolean>>{
    
    private boolean requireMixedCell = true;
    private boolean requireCurrentLevel = false;
    private boolean requirePyxisNode = false;
    private boolean requireSCAV7Node = false;
    private boolean requireWSFPNode = false;
    private boolean require61Node = false;
    private Cell cell = null;
    
    public MixedCellSCAReq(Cell cell) {
        this.cell = cell;
    }

    public Map<String, Boolean> getReqVal() {
        Map<String, Boolean> values = new HashMap<String, Boolean>();
        values.put("mixedCell", requireMixedCell);
        values.put("currentLevel", requireCurrentLevel);
        values.put("pyxisNode", requirePyxisNode);
        values.put("scav7Node", requireSCAV7Node);
        return values;
    }

    public String getRequirementDescr() {
        String req = "Mixed Cell with SCAFP V7";
        if(requireCurrentLevel) {
            req += (", Current Level Node");
        }
        if(requirePyxisNode) {
            req += (", Pyxis Node");
        }
        if(requireSCAV7Node) {
            req += (", SCA V7 Node");
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
        boolean scav7ReqMet = !this.requireSCAV7Node;
        for(Node node : nodes) {
            if(node.getBaseProductVersion().equals(node.getCell().getManager().getNode().getBaseProductVersion())
                && node.getBaseProductVersion().compareToPartial(new WebSphereVersion("7.0")) >= 0)
                currentLevelReqMet = true;
            if(node.getBaseProductVersion().toString().startsWith("7.0")) {
                pyxisReqMet = true;
                Set<InstalledWASProduct> products = node.getInstalledWASProducts();
                for(InstalledWASProduct product : products) {
                    if(product.getProductId().equals(WASProductID.SCA)) {
                        scav7ReqMet = true;
                        //continue;
                        break;
                    }
                }
            }
             
        }
        return (currentLevelReqMet && scav7ReqMet && pyxisReqMet);
    }

    public void setReqMixedCell(boolean reqVal) {
        this.requireMixedCell = reqVal;
    }
    
    public void setReqPyxisNode(boolean reqVal) {
        this.requirePyxisNode = reqVal;
    }
    
    public void setReqSCAV7Node(boolean reqVal) {
        this.requireSCAV7Node = reqVal;
    }
    

    public void setReqCurrentLevelNode(boolean reqVal) {
        this.requireCurrentLevel = reqVal;
    }
}
