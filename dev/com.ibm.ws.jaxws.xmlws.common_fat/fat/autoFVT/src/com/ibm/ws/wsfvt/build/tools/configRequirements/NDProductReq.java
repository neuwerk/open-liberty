//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/NDProductReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:44 [8/8/12 06:40:30]
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

import com.ibm.websphere.simplicity.product.InstalledWASProduct;
import com.ibm.websphere.simplicity.product.WASInstallation;
import com.ibm.websphere.simplicity.product.InstalledWASProduct.WASProductID;

/**
 * Require the ND product to be installed
 * 
 * @author jramos
 */
public class NDProductReq implements Requirement <Boolean>{
    
    private boolean requireNDProduct = true;
    private WASInstallation install = null;
    
    public NDProductReq(WASInstallation install) {
        this.install = install;
    }

    public Boolean getReqVal() {
        return this.requireNDProduct;
    }

    public String getRequirementDescr() {
        final String req = "WebSphere ND Product";
        return req;
    }

    public boolean requirementMet() throws Exception {
        if(this.requireNDProduct) {
            InstalledWASProduct product = this.install.getWASProduct(WASProductID.ND);
            if(product == null) {
                return false;
            }
        }
        return true;
    }

    public void setRequirement(boolean reqVal) {
        this.requireNDProduct = reqVal;
    }
    
}
