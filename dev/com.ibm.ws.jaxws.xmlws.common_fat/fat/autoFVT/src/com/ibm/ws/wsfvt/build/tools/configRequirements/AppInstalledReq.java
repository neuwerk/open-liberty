//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/configRequirements/AppInstalledReq.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:33 [8/8/12 06:40:30]
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

import java.util.LinkedList;
import java.util.List;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.ws.wsfvt.build.tools.AdminApp;

/**
 * Require applications to be installed
 * 
 * @author jramos
 */
public class AppInstalledReq implements Requirement <List<String>>{
    
    private List<String> reqAppsInstalled = new LinkedList<String>();
    private Cell cell = null;
    
    public AppInstalledReq(Cell cell) {
        this.cell = cell;
    }

    public List<String> getReqVal() {
        return this.reqAppsInstalled;
    }

    public String getRequirementDescr() {
        final String req = "Application Installed";
        return req;
    }

    public boolean requirementMet() throws Exception {
        boolean reqMet = true;
        if (this.reqAppsInstalled.size() > 0) {
            AdminApp adminApp = new AdminApp(this.cell);
            for (String appName : reqAppsInstalled) {
                adminApp.setAppName(appName);
                if (!adminApp.isAppInstalled()) {
                    System.out.println(appName + " not installed.");
                    reqMet = false;
                }
            }
        }
        return reqMet;
    }

    public void addAppInstallReq(String app) {
        this.reqAppsInstalled.add(app);
    }
    
}
