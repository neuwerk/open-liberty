package common.utils.topology;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.ServerType;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.websphere.simplicity.product.InstalledWASProduct;
import com.ibm.websphere.simplicity.product.InstalledWASProduct.WASProductID;
import com.ibm.ws.ssl.config.ThreadManager;
import com.ibm.ws.ssl.core.Constants;

public abstract class INodeContainer {

    protected Cell cell;
    protected Node node;
    protected IWASInstall install;

    public INodeContainer(Cell cell, Node node) {
        this.cell = cell;
        this.node = node;
        try {
            this.install = new IWASInstall(node.getWASInstall());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Node getSimplicityNode() {
        return this.node;
    }

    public Cell getCell() {
        return this.cell;
    }

    public String getCellName() {
        return this.cell.getCellName();
    }

    public String getFvtWorkDir() {
        try {
            return this.node.getMachine().getTempDir().getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getJsseSslProps() {
        Map<String, String> sslProps = new HashMap<String, String>();

        String password = "WebAS";
        sslProps.put("javax.net.ssl.trustStore", getProfileDir() + "/etc/trust.p12");
        sslProps.put("javax.net.ssl.trustStorePassword", password);
        sslProps.put("javax.net.ssl.trustStoreType", "PKCS12");
        sslProps.put("javax.net.ssl.keyStore", getProfileDir() + "/etc/key.p12");
        sslProps.put("javax.net.ssl.keyStorePassword", password);
        sslProps.put("javax.net.ssl.keyStoreType", "PKCS12");

        if (! isIBMJSSEPresented()) {
            // try use the SUN jdk's SSL props and set them in the threadlocal
            Properties sunSslProps = generateSunJsseSslProps();
            ThreadManager.getInstance().setPropertiesOnThread(sunSslProps);
        }
        return sslProps;
    }

    private boolean isIBMJSSEPresented() {
        try {
            Class.forName(Constants.IBMJSSE2);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    /**
     * Add the extended SSL props for SSL engine to use.
     * @param sslProps
     */
    private Properties generateSunJsseSslProps() {
        Properties sslProps = new Properties();

        String password = "WebAS";
        // set trustStore related props
        sslProps.put(Constants.SSLPROP_TRUST_STORE_FILE_BASED, Constants.TRUE);
        sslProps.put(Constants.SSLPROP_TRUST_STORE, getProfileDir() + "/etc/trust.p12");
        sslProps.put(Constants.SSLPROP_TRUST_STORE_PASSWORD, password);
        sslProps.put(Constants.SSLPROP_TRUST_STORE_TYPE, "PKCS12");
        sslProps.put(Constants.SSLPROP_TRUST_STORE_PROVIDER, "SunJCE");
        // set keyStore related props
        sslProps.put(Constants.SSLPROP_KEY_STORE_FILE_BASED, Constants.TRUE);
        sslProps.put(Constants.SSLPROP_KEY_STORE, getProfileDir() + "/etc/key.p12");
        sslProps.put(Constants.SSLPROP_KEY_STORE_PASSWORD, password);
        sslProps.put(Constants.SSLPROP_KEY_STORE_TYPE, "PKCS12");
        sslProps.put(Constants.SSLPROP_KEY_STORE_PROVIDER, "SunJCE");

        // put "com.ibm.ssl.contextProvider" : "SunJSSE"
        sslProps.put(Constants.SSLPROP_CONTEXT_PROVIDER, Constants.SUNJSSE_NAME);
        // put "com.ibm.ssl.protocol" : "SSL"
        sslProps.put(Constants.SSLPROP_PROTOCOL, Constants.PROTOCOL_SSLV3);
        // set ssl prop alias
        sslProps.put(Constants.SSLPROP_ALIAS, "SunJDKDefaultSSLConfigSettings");
        return sslProps;
    }

    public String getKey() {
        return this.node.getBootstrapFileKey();
    }

    public IMachine getMachine() {
        try {
            return new IMachine(this.node.getMachine());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract NodeContainerType getNodeContainerType();

    public String getNodeName() {
        return this.node.getName();
    }

    public String getProfileDir() {
        try {
            return this.node.getProfileDir();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getProfileName() {
        try {
            return this.node.getProfileName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public IServerProcess getRootServerProcess() {
        try {
            Server server = this.node.getManager();
            return getServerProcess(server);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<IServerProcess> getServerProcesses() {
        try {
            Set<Server> servers = this.node.getServers();
            Set<IServerProcess> ret = new HashSet<IServerProcess>();
            for(Server server : servers) {
                IServerProcess s = getServerProcess(server);
                if(s != null)
                    ret.add(s);
            }
            return ret;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public File getTopologyFile() throws Exception {
        return Topology.getBootStrappingFile();
    }

    public WASVersion getWASVersion() {
        try {
            WebSphereVersion version = this.node.getBaseProductVersion();
            if(version.toString().startsWith("7.0")) {
                return WASVersion.PYXIS;
            } else {
                Set<InstalledWASProduct> products = this.node.getInstalledWASProducts();
                for(InstalledWASProduct product : products) {
                    if(product.getProductId() == WASProductID.WEBSERVICES) {
                        return WASVersion.WEB_SERVICE_FEATURE_PACK;
                    }
                }
                if(version.toString().startsWith("6.1")) {
                    return WASVersion.WAS_6_1;
                } else if(version.toString().startsWith("6.0.2")) {
                    return WASVersion.WAS_6_0_2;
                } else {
                    return WASVersion.UNKNOWN_VERSION;
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public IWASInstall getWASInstall() {
        return this.install;
    }

    private IServerProcess getServerProcess(Server server) {
        ServerType type = server.getServerType();
        if(type == ServerType.ADMIN_AGENT) {
            return new IAdminAgent(this, server);
        } else if(type == ServerType.APPLICATION_SERVER) {
            return new IAppServer(this, server);
        } else if(type == ServerType.DEPLOYMENT_MANAGER) {
            return new IDmgr(this, server);
        } else if(type == ServerType.NODE_AGENT) {
            return new INodeAgent(this, server);
        } else {
            return null;
        }
    }
}
