/*
 * @(#) 1.6 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/BatchIOU.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:02 [8/8/12 06:40:28]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect  Description
 * -----------------------------------------------------------------------------
 * 05/23/2007  jramos      440922          New File
 * 07/12/2007  jramos      451621          Change implementation to use BatchedIOU
 * 10/22/2007  jramos      476750          Use TopologyDefaults tool and ACUTE 2.0 api
 * 04/03/2008  btiffany    510181          send log statements to stdout as well...
 */

package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * The BatchIOU class is a custom task for executing a batched install/uninstall
 * script
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class BatchIOU extends Task {

    private String lang = "jython";
    private String connType = "SOAP";
    private String scriptFile = null;
    private String serverKey = null;
    private boolean install = true;
    private boolean startApps = true;
    
    public void log(String s){
        System.out.println("BatchIOU: "+s);    
        super.log(s);
    }    

    public void execute() throws BuildException {

//        if (scriptFile == null)
//            throw new BuildException("scriptFile must be set.");
//
//        boolean local = connType.equalsIgnoreCase("NONE");
//
//        IServerProcess server = null;
//        if (serverKey == null) {
//            // since they didn't specify which server to use... we'll hope they
//            // want the default
//            server = TopologyDefaults.defaultAppServer;
//        } else if ("root".equals(serverKey)) {
//            // install the app on dmgr if ND or the app server if BASE
//            Cell c = TopologyDefaults.defaultAppServerCell;
//            if (c.getTopology() == Topology.ND)
//                server = ((IDmgrNodeContainer) c.getRootNodeContainer()).getDmgr();
//            else
//                server = ((IBaseNodeContainer) c.getRootNodeContainer()).getAppServer();
//        } else {
//            int indexOfDot = serverKey.indexOf(".");
//            String cellKey = serverKey.substring(0, indexOfDot);
//            Cell cell = CellFactory.getCell(CommonUtilsConst.DEFAULT_TOPOLOGY_FILE, cellKey);
//            ServerProcessVisitor visitor = new ServerProcessVisitor();
//            visitor.setKey(serverKey);
//            cell.accept(visitor);
//            if (visitor.getResult().size() > 0) {
//                server = visitor.getResult().iterator().next();
//            } else {
//                // we're hosed
//                throw new BuildException("Server with serverKey " + serverKey + " was not found.");
//            }
//        }
//        
//        AdminApp adminApp = new AdminApp(server.getCell());
//        adminApp.setLocal(local);
//        BatchedIOU batchedIOU = new BatchedIOU(new File(scriptFile));
//        if(install) {
//            log("BatchIOU is calling batchedIOU.batchInstall with script: "+scriptFile +", local="+local+", startapps="+startApps);
//            batchedIOU.batchInstall(adminApp, startApps);
//        } else {
//            batchedIOU.batchUninstall(adminApp);
//        }
    }

    public String getConnType() {
        return connType;
    }

    public void setConnType(String connType) {
        this.connType = connType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }
    
    public boolean isInstall() {
        return install;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }

    public void setStartApps(boolean startApps) {
        this.startApps = startApps;
    }
    
    public boolean isStartApps() {
        return this.startApps;
    }
}
