package com.ibm.ws.wsfvt.build.tasks;

import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.BootStrappingProperty;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.configuration.ConfigurationProvider;
import com.ibm.websphere.simplicity.product.WASInstallation;
import com.ibm.websphere.simplicity.product.InstalledWASProduct.WASProductID;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This task adds some non-standard properties to bootstrapping.properties
 * that are needed to execute unittest.bat/sh. These properties are not
 * provided by autoWAS and must be obtained via Simplicity.
 */
public class PrepareBootstrappingProps extends Task {
    
    private String zOSCellKey;

    public void execute() throws BuildException {
        try {
            ConfigurationProvider cp = Topology.getBootstrapFileOps().getConfigurationProvider();
            if(zOSCellKey == null) {
                Node node = TopologyDefaults.getDefaultAppServer().getCell().getManager().getNode();
                WASInstallation wasInstall = node.getWASInstall();
                String installRoot = wasInstall.getInstallRoot();
                if(node.getMachine().getOperatingSystem().equals(OperatingSystem.WINDOWS))
                    // backslash is needed for Windows
                    installRoot = installRoot.replace('/', '\\');
                cp.setProperty("localWASHome", installRoot);
                String buildNumber = null;
                // Base product will either be BASE, ND, or Express
                if(wasInstall.getWASProduct(WASProductID.BASE) != null) {
                    buildNumber = wasInstall.getWASProduct(WASProductID.BASE).getBuildLevel();
                } else if(wasInstall.getWASProduct(WASProductID.ND) != null) {
                    buildNumber = wasInstall.getWASProduct(WASProductID.ND).getBuildLevel();
                } else if(wasInstall.getWASProduct(WASProductID.EXPRESS) != null) {
                    buildNumber = wasInstall.getWASProduct(WASProductID.EXPRESS).getBuildLevel();
                }
                cp.setProperty("wasBuildNumber", buildNumber);
            } else {
                Topology.init();
                Cell cell = Topology.getCellByBootstrapKey(zOSCellKey);
                String wasCellPropPrefix = BootStrappingProperty.WAS_CELL_KEY.toString();
                String zOSPrefix = wasCellPropPrefix + "1";
                Node node = cell.getManager().getNode();
                cp.setProperty(zOSPrefix + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.ROOT_NODE_HOSTNAME, node.getMachine().getHostname());
                cp.setProperty(zOSPrefix + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.ROOT_NODE_PROFILE_PATH, node.getProfileDir());
                cp.setProperty(zOSPrefix + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.CONN_TYPE, node.getConnInfo().getConnType().toString());
                cp.setProperty(zOSPrefix + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.CONN_PORT, node.getConnInfo().getPort() + "");
                cp.setProperty(zOSPrefix + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.WAS_USERNAME, node.getConnInfo().getUser());
                cp.setProperty(zOSPrefix + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.WAS_PASSWORD, node.getConnInfo().getPassword());
                
                // clear out other cells
                Properties cellProps = cp.getProperties(wasCellPropPrefix);
                for(String prop : cellProps.stringPropertyNames()) {
                    if(!prop.startsWith(zOSPrefix))
                        cp.removeProperty(prop);
                }
            }
            cp.writeProperties();
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }
    
    public void setZOSCellKey(String zOSCellKey) {
        this.zOSCellKey = zOSCellKey;
    }
}
