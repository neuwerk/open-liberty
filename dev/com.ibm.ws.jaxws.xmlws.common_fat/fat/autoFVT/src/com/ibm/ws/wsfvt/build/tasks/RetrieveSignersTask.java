/*
 * @(#) 1.6 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/RetrieveSignersTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:39 [8/8/12 06:55:15]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 08/15/2006  jramos      D381622            Create file (for ND support)
 * 01/04/2007  smithd      D411800            Changes to executeCommand methods
 * 03/01/2007  jramos      423736             Correct path to profile bin directory
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 10/22/07    jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/29/2008  jramos      559143             Incorporate Simplicity
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.Dmgr;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.WebSphereTopologyType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This class will execute retreiveSigners on all dmgrs and nodes in the WAS
 * cell
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 * 
 * @author jramos
 * 
 */
public class RetrieveSignersTask extends Task {

    /**
     * This method will run retrieveSingers on all dmgrs and nodes in the
     * default cell
     */
    public void execute() {
        
        try {
            Cell cell = TopologyDefaults.getDefaultAppServer().getCell();
            
            String cmd = null;
            String[] params = null;
            int i = 2;
            if (cell.getSecurityConfiguration().getGlobalSecurityDomain().isGlobalSecurityEnabled()) {
                params = new String[13];
                params[i++] = "-user";
                params[i++] = cell.getConnInfo().getUser();
                params[i++] = "-password";
                params[i++] = cell.getConnInfo().getPassword();
            } else {
                params = new String[9];
            }
            params[i++] = "-conntype";
            params[i++] = "SOAP";
            if (cell.getTopologyType() == WebSphereTopologyType.ND) {
                // dmgr
                Dmgr dmgr = (Dmgr)cell.getManager();
                params[0] = "CellDefaultTrustStore";
                params[1] = "ClientDefaultTrustStore";
                params[i++] = "-port";
                params[i++] = dmgr.getPortNumber(ConnectorType.SOAP).toString();
                params[i++] = "-autoAcceptBootstrapSigner";
                params[i++] = "-host";
                params[i++] = dmgr.getNode().getMachine().getHostname();
                Set<Node> nodes = cell.getNodes();
                // do the dmgr first
                cmd = dmgr.getNode().getProfileDir() + "/bin/retrieveSigners" + dmgr.getNode().getMachine().getOperatingSystem().getDefaultScriptSuffix();
                ProgramOutput po = dmgr.getNode().getMachine().execute(cmd, params);
                System.out.println(po.getStdout());
                // now the rest of the nodes
                for (Node node : nodes) {
                    if(!node.equals(dmgr.getNode())) {
                        cmd = node.getProfileDir() + "/bin/retrieveSigners" + node.getMachine().getOperatingSystem().getDefaultScriptSuffix();
                        po = node.getMachine().execute(cmd, params);
                        System.out.println(po.getStdout());
                    }
                }
            } else if (cell.getTopologyType() == WebSphereTopologyType.BASE) {
                // BASE server
                ApplicationServer appServer = (ApplicationServer)cell.getManager();
                params[0] = "NodeDefaultTrustStore";
                params[1] = "ClientDefaultTrustStore";
                params[i++] = "-host";
                params[i++] = appServer.getNode().getMachine().getHostname();
                params[i++] = "-port";
                params[i++] = appServer.getPortNumber(ConnectorType.SOAP).toString();
                params[i++] = ""; // only 8 parameters for BASE
                cmd = appServer.getNode().getProfileDir() + "/bin/retrieveSigners" + appServer.getNode().getMachine().getOperatingSystem().getDefaultScriptSuffix();
                ProgramOutput po = appServer.getNode().getMachine().execute(cmd, params);
                System.out.println(po.getStdout());
            }
        } catch (Exception ee) {
            throw new BuildException(ee);
        }
    }
}
