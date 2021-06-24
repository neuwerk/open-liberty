/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/MigrationReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:41 [8/8/12 06:41:08]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 06/30/08    jramos      532963             New File
 * 07/10/08    jramos      535565             Handle exception if two cells are not defined
 * 10/31/08    jramos      559143             Incorporate Simplicity
 */

package com.ibm.ws.wsfvt.build.tools.configRequirements;

import com.ibm.websphere.simplicity.Server;
import com.ibm.ws.wsfvt.build.tools.utils.MigrationUtils;

public class MigrationReq implements Requirement<Boolean>{

    public String getRequirementDescr() {
        return "Migration environment. Two cells, one being a WSFP cell.";
    }

    public Boolean getReqVal() {
        return true;
    }

    public boolean requirementMet() {
        Server targetServer;
        Server sourceServer;
        
        try {
            targetServer = MigrationUtils.getTargetServer();
        } catch(Exception e) {
            System.out.println("Error obtaining target server: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
        try {
            sourceServer = MigrationUtils.getSourceServer();
        } catch(Exception e) {
            System.out.println("Error obtaining source server: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
        return targetServer != null && sourceServer != null;
    }

    
}
