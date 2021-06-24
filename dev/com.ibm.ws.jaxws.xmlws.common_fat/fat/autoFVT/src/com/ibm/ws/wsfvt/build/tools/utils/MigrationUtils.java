/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/utils/MigrationUtils.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:55 [8/8/12 06:41:04]
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
 * 06/12/08    jramos      524904             New File
 */

 package com.ibm.ws.wsfvt.build.tools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Set;

import com.ibm.websphere.simplicity.BootStrappingProperty;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.ServerType;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * Provides methods to get the source and target topology
 * Objects for a migration configuration. The target (up level)
 * Cell should be the first cell. The source (down level) Cell
 * should be the second cell.
 * 
 * @author jramos
 */
public class MigrationUtils {

    private static final String TARGET_CELL_KEY = BootStrappingProperty.WAS_CELL_KEY + "1";
    private static final String SOURCE_CELL_KEY = BootStrappingProperty.WAS_CELL_KEY + "2";
    
    private static Server sourceServer;
    private static Server targetServer;
    
    /**
     * Get the default target server. Always returns an application server.
     * @return
     */
    public static Server getTargetServer() throws Exception {
        if(targetServer == null) {
            targetServer = TopologyDefaults.getDefaultAppServer();
        }
        return targetServer;
    }
    
    /**
     * Get the default source server. Always returns an application server.
     * @return
     */
    public static Server getSourceServer() throws Exception {
        if(sourceServer == null) {
            sourceServer = findServer(SOURCE_CELL_KEY, ServerType.APPLICATION_SERVER);
        }
        return sourceServer;
    }
    
    /**
     * Get the server in the target cell with the specified type
     * @param serverType The type of server to get
     * @return The server
     */
    public static Server getTargetServer(ServerType serverType) throws Exception {
        return findServer(TARGET_CELL_KEY, serverType);
    }
    
    /**
     * Get the server in the source cell with the specified type
     * @param serverType The type of server to get
     * @return The server
     */
    public static Server getSourceServer(ServerType serverType) throws Exception {
        return findServer(SOURCE_CELL_KEY, serverType);
    }
    
    /**
     * Find a server in a Cell
     * 
     * @param cellKey The key of the Cell to search
     * @param serverType The type of server to find
     * @return The IServerProcess
     */
    private static Server findServer(String cellKey, ServerType serverType) throws Exception {
        Topology.init();
        Cell cell = Topology.getCellByBootstrapKey(cellKey);
        Set<Server> servers = cell.getServers();
        for(Server server : servers) {
            if(server.getServerType() == serverType) {
                return server;
            }
        }
        return null;
    }
    
    
    public static File getProfileMigrationDirectory() throws Exception {
        return (new File(getTargetServer().getNode().getMachine().getTempDir().getAbsolutePath() + "/migration"));
    }
    
    public static String getProfileMigrationDirectoryHost() throws Exception {
        return getTargetServer().getNode().getMachine().getHostname();
    }
    
    public static File getWASPreUpgradeSummaryLog() {
        return (new File(AppConst.FVT_BUILD_WORK_DIR + "/WASPreMigrationSummary.log"));
    }
    
    public static File getWASPostUpgradeSummeryLog() {
        return (new File(AppConst.FVT_BUILD_WORK_DIR + "/WASPostMigrationSummary"));
    }
    
    public static String getWASPreMigrationSummaryContents() throws Exception {
        Machine m = Machine.getLocalMachine();
        RemoteFile f = m.getFile(getWASPreUpgradeSummaryLog().getAbsolutePath());
        String contents = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(f.openForReading()));
            String line = "";
            while((line = br.readLine()) != null) {
                contents += line;
            }
        } finally {
            if(br != null) {
                br.close();
            }
        }
        return contents;
    }
    
    public static String getWASPostMigrationSummaryContents() throws Exception {
        Machine m = Machine.getLocalMachine();
        RemoteFile f = m.getFile(getWASPostUpgradeSummeryLog().getAbsolutePath());
        String contents = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(f.openForReading()));
            String line = "";
            while((line = br.readLine()) != null) {
                contents += line;
            }
        } finally {
            if(br != null) {
                br.close();
            }
        }
        return contents;
    }
}
