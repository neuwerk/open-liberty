/*
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 04/13/2003  ulbricht    LIDB1738.09.07     Add option to install with -deployws
 * 05/13/2003  ulbricht    D163811            Add option to set listener port
 * 10/05/2003  ulbricht    D178846            Allow ear install location to be set
 * 02/20/2004  ulbricht    D258191            Add batch mode install
 * 03/10/2005  ulbricht    D260687            Add classloaderMode
 * 03/14/2005  ulbricht    D261009            Default install mode
 * 04/22/2005  ulbricht    D269183.2          Set profile
 * 04/27/2005  ulbricht    D269183.5          Add map modules to server for iSeries
 * 05/06/2005  ulbricht    D273758            Default to module name if no display-name
 * 05/27/2006  ulbricht    D370654            Add support to install WAR files
 * 08/15/2006  smithd      D381622            Add Cell support (ND support)
 * 10/23/2006  smithd      D395172            Improve usability in Cell
 * 10/31/2006  sedov       D401994            Add defaultbinding.virtual.host and .datasource.jndi support
 * 11/04/2006  sedov       D403119            Allow "root" special as server key
 * 01/15/2007  jramos      413702             Add call to StartAppsTask.execute()
 * 01/22/2007  jramos      416174             Remove call to StartAppsTask.execute()
 * 01/23/2007  jramos      416386             restore setLocalInstall()
 * 03/14/2007  ulbricht    426391             Add policy set copy
 * 03/14/2007  jramos      426382             Change fvtRootLocation to be remote directory if using remote host
 * 03/23/2007  jramos      413680.1           Get fvtRootLocation from isp if using remote host
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 10/22/2007  jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/25/2007  sedov       476185             Added AdminAgent as root server support
 * 11/02/2007  sedov       479154             Added failonerror option
 * 04/03/2008  btiffany    510181             add some debug statements for app start problems
 * 05/02/2008  jramos      517704             Add support to set UseWSFEP61ScanPolicy property for local installs
 * 06/12/2008  jramos      524904             Add support to install to downLevelServer for migration and mixed cell
 * 10/31/2008  jramos      559143             Rework using Simplicity
 * 01/21/2009  jramos      570716             Update for war files
 * 03/04/2009  jramos      573363.11          Add support to install to downLevelServer, wsfp, was61 for mixed cell
 * 05/07/2009  jramos      589140             Sync nodes after copying policy sets
 * 09/23/2009  jramos      615280             Always log build file and app name when errors occur
 * 05/10/2010  btiffany    651727             Add logStartupFailures setter, default true
 * 12/01/2010  btiffany    678043.1           Don't log install or startup failures to install error log when failonerror=false. 
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Scope;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.ServerType;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.websphere.simplicity.application.AssetModule;
import com.ibm.websphere.simplicity.application.EnterpriseApplication;
import com.ibm.websphere.simplicity.product.InstalledWASProduct;
import com.ibm.websphere.simplicity.product.InstalledWASProduct.WASProductID;
import com.ibm.ws.wsfvt.build.tools.AdminApp;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.PrintToFile;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The InstallApp class is a custom task for installing an application into the WebSphere
 * Application Server.
 * 
 * The class conforms to the standards set in the Ant build framework for creating custom tasks.
 */
public class InstallApp extends Task {

    private String earName = null;
    private String appName = "";
    private String installRootLocation = null;
    private String installMode = "com.ibm.ws.wsfvt.build.install.HybridBatchInstallImpl";
    private boolean localInstall = true;
    private boolean useDefaultBindings = false;
    private boolean deployEjb = false;
    private boolean deployWs = false;
    private boolean mapModulesToServers = false;
    private Boolean useWSFEP61ScanPolicy = null;
    private boolean useMetaDataFromBinary = false;
    private String classLoaderMode = null;
    private String serverName = null;
    private String warName = null;
    private String contextRoot = null;
    private String serverKey = null;

    private String defaultBindingVirtualHost = null;
    private String defaultBindingDataSourceJNDI = null;
    private String dataSourceFor20EJBModules = null;
    private String dataSourceFor20CMPBeans = null;
    private String dataSourceFor10EJBModules = null;
    private String dataSourceFor10CMPBeans = null;

    private boolean failOnError = true;
    private boolean logStartupFailures = true;

    private List<PolicySet> policySets = null;

    // this parameter is constructed during execute
    private AdminApp adminApp = null;

    public void log(String s) {
        super.log(s);
    }

    /**
     * This method executes the command to install the application into the WebSphere Application
     * Server or writes a script that can be fed into wsadmin to install a batch of applications at
     * one time.
     * 
     * @throws BuildException Any install related exceptions
     */
    public void execute() throws BuildException {
        if (serverKey == null) {
            serverKey = "defaultServer";
        }

        System.out.println("InstallApp " + appName + " on " + serverKey);
        log("InstallApp has the following values: " + System.getProperty("line.separator")
                + "    earName             = " + earName + System.getProperty("line.separator")
                + "    warName             = " + warName + System.getProperty("line.separator")
                + "    appName             = " + appName + System.getProperty("line.separator")
                + "    localInstall        = " + localInstall + System.getProperty("line.separator")
                + "    contextRoot         = " + contextRoot + System.getProperty("line.separator")
                + "    deployEjb           = " + deployEjb + System.getProperty("line.separator")
                + "    deployWs            = " + deployWs + System.getProperty("line.separator")
                + "    mapModulesToServers = " + mapModulesToServers + System.getProperty("line.separator")
                + "    useDefaultBindings  = " + useDefaultBindings + System.getProperty("line.separator")
                + "    installRootLocation = " + installRootLocation + System.getProperty("line.separator")
                + "    installMode         = " + installMode + System.getProperty("line.separator")
                + "    classLoaderMode     = " + classLoaderMode + System.getProperty("line.separator")
                + "    serverName          = " + serverName + System.getProperty("line.separator")
                + "    serverKey           = " + serverKey + System.getProperty("line.separator")
                + "    defaultbinding.virtual.host = " + defaultBindingVirtualHost
                + System.getProperty("line.separator") + "    defaultbinding.datasource.jndi = "
                + defaultBindingDataSourceJNDI + System.getProperty("line.separator")
                + "    DataSourceFor20EJBModules = " + dataSourceFor20EJBModules + System.getProperty("line.separator")
                + "    DataSourceFor10EJBModules = " + dataSourceFor10EJBModules + System.getProperty("line.separator")
                + "    DataSourceFor20CMPBeans           = " + dataSourceFor20CMPBeans
                + System.getProperty("line.separator") + "    DataSourceFor10CMPBeans           = "
                + dataSourceFor10CMPBeans + System.getProperty("line.separator"), Project.MSG_VERBOSE);

        if ((warName == null) && (earName == null)) {
            throw new BuildException("Either the warName or earName must be" + " specified.");
        }

        if ((warName != null) && (earName != null)) {
            throw new BuildException("Only the warName or earName can be" + " be specified at one time.");
        }

        if ((warName != null) && (contextRoot == null)) {
            throw new BuildException("A contextRoot must be specified when" + " installing a WAR file.");
        }

        if (installRootLocation == null) {
            throw new BuildException("You must specify an installRootLocation");
        }

        // Check to see that the app is at least there to install before continuing
        File warFile = new File(installRootLocation + "/" + warName);
        File earFile = new File(installRootLocation + "/" + earName);
        if (earFile.exists() || warFile.exists()) {
            // we continue
        } else {
            System.out.println("Neither the ear nor the war file exist, can not continue install");
            // throw new BuildException("Neither the ear nor war file exist, can not continue
            // install");
            return;
        }

        Server server = null;
        try {            
            if ("defaultServer".equals(serverKey)) {
                server = TopologyDefaults.getDefaultAppServer();
            } else if ("root".equals(serverKey)) {
                server = TopologyDefaults.getRootServer();
            } else if ("downLevelServer".equals(serverKey)
                    || "wsfp".equals(serverKey)
                    || "was61".equals(serverKey)) {
                // lets try to find a down level server in the config; assumption is made that
                // baseline version is highest
                WebSphereVersion baseLineVersion = TopologyDefaults.getDefaultAppServer().getNode()
                        .getBaseProductVersion();
                List<Cell> cells = Topology.getCells();
                // loop through all the nodes in the cell
                for (Cell c : cells) {
                    Set<Node> nodes = c.getNodes();
                    for (Node node : nodes) {
                        
                        boolean match  = false;
                        
                        if( "downLevelServer".equals(serverKey) && !node.getBaseProductVersion().equals(baseLineVersion)) {
                            match = true;
                        } else if("wsfp".equals(serverKey)) {
                            Set<InstalledWASProduct> products = node.getInstalledWASProducts();
                            for(InstalledWASProduct product : products) {
                                if(product.getProductId().equals(WASProductID.WEBSERVICES)) {
                                    match = true;
                                    break;
                                }
                            }
                        } else if("was61".equals(serverKey) && node.getBaseProductVersion().toString().indexOf("6.1") != 1) {
                            match  = true;
                            Set<InstalledWASProduct> products = node.getInstalledWASProducts();
                            for(InstalledWASProduct product : products) {
                                if(product.getProductId().equals(WASProductID.WEBSERVICES) || product.getProductId().equals(WASProductID.EJB3)) {
                                    match  = false;
                                    break;
                                }
                            }
                        }
                        
                        if (match) {
                            // look for an appserver
                            Set<Server> servers = node.getServers();
                            for (Server downServer : servers) {
                                if (downServer.getServerType() == ServerType.APPLICATION_SERVER) {
                                    server = downServer;
                                    break;
                                }
                            }
                        }
                    }
                    if (server != null) {
                        break;
                    }
                }
                if (server == null) {
                    throw new BuildException("Unable to find a down level server in the configuration.");
                }
            } else {
                List<Cell> cells = Topology.getCells();
                for (Cell cell : cells) {
                    Set<Server> servers = cell.getServers();
                    for (Server s : servers) {
                        if (s.getBootstrapFileKey().equals(serverKey)) {
                            server = s;
                            break;
                        }
                    }
                }
                if (server == null) {
                    // we're hosed
                    throw new BuildException("Server with serverKey " + serverKey + " was not found.");
                }
            }

            log("Installing to server " + server.getNode().getName() + ", " + server.getName());

            localInstall = server.getConnInfo().getConnType() == ConnectorType.NONE;

            adminApp = new AdminApp(server.getCell());
            RemoteFile appFile = null;
            if (earFile.exists()) {
                appFile = Machine.getLocalMachine().getFile(earFile.getAbsolutePath());
                adminApp.setEarFile(appFile);
            } else {
                appFile = Machine.getLocalMachine().getFile(warFile.getAbsolutePath());
                adminApp.setWarFile(appFile);
            }
            adminApp.setAppName(appName);
            if(warFile.exists()) {
                adminApp.setContextRoot(contextRoot);
            }
            adminApp.setLocal(localInstall);
            adminApp.setDeployEjb(deployEjb);
            adminApp.setDeployWs(deployWs);
            adminApp.setUseDefaultBindings(useDefaultBindings);
            if(useWSFEP61ScanPolicy == null && localInstall) {
                useWSFEP61ScanPolicy = true;
            } else if(useWSFEP61ScanPolicy == null) {
                useWSFEP61ScanPolicy = false;
            }
            adminApp.setUseWSFEP61ScanPolicy(useWSFEP61ScanPolicy);

            adminApp.setDefaultBindingVirtualHost(defaultBindingVirtualHost);
            adminApp.setDefaultBindingDataSourceJNDI(defaultBindingDataSourceJNDI);
            adminApp.setDataSourceFor10CMPBeans(dataSourceFor10CMPBeans);
            adminApp.setDataSourceFor20CMPBeans(dataSourceFor20CMPBeans);
            adminApp.setDataSourceFor10EJBModules(dataSourceFor10EJBModules);
            adminApp.setDataSourceFor20EJBModules(dataSourceFor20EJBModules);

            if(useMetaDataFromBinary) {
                adminApp.setUseMetaDataFromBinary(true);
            }

            mapModulesToServers(adminApp, server);
            try {
                log(adminApp.installApp());
            } catch (Exception e) {
                e.printStackTrace();
                // logging an install fail will cause the BuildTestCase to fail, don't log if we allow failures. 
                if(failOnError){
                    PrintToFile file = new PrintToFile();
                    file.printBuf(AppConst.INSTALL_ERR_LOG, super.getLocation().getFileName());
                    file.printNewLine(AppConst.INSTALL_ERR_LOG);
                    file.printBuf(AppConst.INSTALL_ERR_LOG, "Application " + this.appName + " failed to install.");
                    file.printNewLine(AppConst.INSTALL_ERR_LOG);
                    file.printException(AppConst.INSTALL_ERR_LOG, e);
                }  else {
                    System.out.println("This install failure was not written to the install log because failOnError was set to false");
                }
            }            
        } catch (Exception e) {
            e.printStackTrace();
            if(failOnError){
                PrintToFile file = new PrintToFile();
                file.printBuf(AppConst.INSTALL_ERR_LOG, super.getLocation().getFileName());
                file.printNewLine(AppConst.INSTALL_ERR_LOG);
                file.printBuf(AppConst.INSTALL_ERR_LOG, "InstallAppTask could not configure " + this.appName + ", so it failed to install.");
                file.printBuf(AppConst.INSTALL_ERR_LOG, "Application " + this.appName + " failed to install.");
                file.printNewLine(AppConst.INSTALL_ERR_LOG);
                file.printException(AppConst.INSTALL_ERR_LOG, e);
                throw new BuildException("Error during installation: " + e, e);
            } else {
                System.out.println("This install failure was not written to the install log because failOnError was set to false");
            }
        }

        if (policySets != null) {
            for (Iterator i = policySets.iterator(); i.hasNext();) {
                // copy dir to dir
                PolicySet ps = (PolicySet) i.next();
                try {
                    log("PolicySet " + System.getProperty("line.separator") + "    from: " + ps.getFrom()
                            + System.getProperty("line.separator") + "    to: " + ps.getTo()
                            + System.getProperty("line.separator") + "    host: " + ps.getHost()
                            + System.getProperty("line.separator"), Project.MSG_VERBOSE);
                    RemoteFile srcDir = Machine.getLocalMachine().getFile(ps.getFrom());
                    RemoteFile destDir = Machine.getMachine(
                            Topology.getBootstrapFileOps().getMachineConnectionInfo(ps.getHost())).getFile(ps.getTo());
                    srcDir.copyToDest(destDir, true, true);
                    server.getWorkspace().saveAndSync();
                } catch (Exception e) {
                    e.printStackTrace();
                    PrintToFile file = new PrintToFile();
                    file.printBuf(AppConst.INSTALL_ERR_LOG, super.getLocation().getFileName());
                    file.printNewLine(AppConst.INSTALL_ERR_LOG);
                    file.printBuf(AppConst.INSTALL_ERR_LOG, "InstallAppTask could not configure policy sets for " + this.appName + 
                        ", so it failed to install.");
                    file.printBuf(AppConst.INSTALL_ERR_LOG, "Application " + this.appName + " failed to install.");
                    file.printNewLine(AppConst.INSTALL_ERR_LOG);
                    file.printException(AppConst.INSTALL_ERR_LOG, e);
                    if (failOnError) {
                        throw new BuildException("Error while installing policy sets: " + e, e);
                    } else {
                        log("Error while installing policy sets: " + e);
                    }
                }
            }
        }

        // now start the app if not in local mode
        try {
            if(!localInstall) {
                EnterpriseApplication app = (EnterpriseApplication)server.getCell().getApplicationManager().getApplicationByName(appName);
                int timeout = 120;
                System.out.println("Waiting for the application to be ready.");
                while(timeout > 0 && !app.isAppReady(true)) { --timeout; }
                System.out.println("Stopping and starting the application.");
                // try a stop here just in case. Sometimes WebSphere is funny and shows the app as started when it's really not
                try {
                    app.stop(true, 10000);
                } catch(Exception e){
                    System.out.println("Non-fatal Exception stopping the application.");
                }
                app.start(true);
                // sleep 10 seconds to make sure the app is really ready
                try {
                    Thread.sleep(10000);
                } catch(Exception e){}
            }
        } catch(Exception e) {
            e.printStackTrace();
            if(logStartupFailures){
                PrintToFile file = new PrintToFile();
                file.printBuf(AppConst.INSTALL_ERR_LOG, super.getLocation().getFileName());
                file.printNewLine(AppConst.INSTALL_ERR_LOG);
                 file.printBuf(AppConst.INSTALL_ERR_LOG, "Application " + this.appName + " failed to start.");
                file.printBuf(AppConst.INSTALL_ERR_LOG, "Application " + this.appName + " failed to install.");
                file.printNewLine(AppConst.INSTALL_ERR_LOG);
                file.printException(AppConst.INSTALL_ERR_LOG, e);
            } else{
               System.out.println("This startup failure was not written to the install log because logStartupFailures was set to false");
            }
            if(failOnError) {
                throw new BuildException("Error starting application: " + appName, e);
            } else {
                log("Error starting application: " + appName);
            }
        }
    }

    /**
     * This method was created because OS400 uses a default server of the profile name.
     * Unfortunately, when installing the applications the default was "server1". This is a way to
     * get around that problem.
     * 
     * @param adminApp The adminApp object contains info about the app being installed
     * @param server The server to map the modules to
     * @throws Exception Any kind of build related exception
     */
    private void mapModulesToServers(AdminApp adminApp, Server server) throws Exception {
        Set<AssetModule> modules = adminApp.getModules();
        List<Scope> servers = new ArrayList<Scope>();
        servers.add(server);
        for (AssetModule module : modules) {
            adminApp.addMapModulesToServers(module, servers);
        }
    }

    /**
     * This method accepts a string containing name of the ear to install.
     * 
     * @param earName Name of the ear file to install
     */
    public void setEarName(String earName) {
        this.earName = earName;
    }

    /**
     * This method accepts a string containing directory of the base WAS install.
     * 
     * @param installRootLocation Name of the base WAS install directory
     */
    public void setInstallRootLocation(String installRootLocation) {
        this.installRootLocation = installRootLocation;
    }

    /**
     * This method accepts a string containing directory of the base FVT install.
     * 
     * @param fvtRootLocation Name of the base FVT install directory
     */
    public void setFvtRootLocation(String fvtRootLocation) {
//        this.fvtRootLocation = fvtRootLocation;
    }

    /**
     * This method accepts a string containing name of the application to be installed under.
     * Without setting the appName, the wsadmin command will create its own appName from the
     * application.xml file.
     * 
     * @param appName Name of applications to be installed under
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    /**
     * This method determines if application startup failures will be logged to the install
     * error log. Default is true.  There may be some negative tests which deliberately
     * install an app that cannot be started, this may be useful in that case.
     * 
     * @param appName Name of applications to be installed under
     */
    public void setLogStartupFailures(boolean logStartupFailures) {
        this.logStartupFailures = logStartupFailures;
        
    }

    /**
     * This method accepts a boolean indicating whether the WebSphere Application Server will be
     * running when install is attempted. If the server will not be running, localInstall will be
     * true.
     * 
     * @param localInstall A boolean indicating if a local install should be tried
     */
    public void setLocalInstall(boolean localInstall) {
        this.localInstall = localInstall;
    }

    /**
     * This method accepts a boolean indicating whether the WebSphere Application Server will be
     * install the application with the usedfaultbindings switch set to true.
     * 
     * @param useDefaultBindings A boolean indicating if usedefaultbindings will be turned on.
     */
    public void setUseDefaultBindings(boolean useDefaultBindings) {
        this.useDefaultBindings = useDefaultBindings;
    }

    /**
     * This method accepts a boolean indicating whether the WebSphere Application Server will deploy
     * EJBs.
     * 
     * @param deployEjb A boolean indicating if deployejb option will be turned on.
     */
    public void setDeployEjb(boolean deployEjb) {
        this.deployEjb = deployEjb;
    }

    /**
     * This method allow the install mode to be set.
     * 
     * @param installMode The implementation of install that should be used.
     */
    public void setInstallMode(String installMode) {
        this.installMode = installMode;
    }

    /**
     * This method accepts a boolean indicating whether the application should be installed using
     * the -deployws option.
     * 
     * @param deployWs True if the application should be installed using the -deployws option
     */
    public void setDeployWs(boolean deployWs) {
        this.deployWs = deployWs;
    }

    /**
     * This method accepts a boolean indicating whether the the install task should generate map
     * modules to servers information when installing the module through wsadmin.
     * 
     * For the OS400 platform, this attribute is always set to true. The normal default is false.
     * 
     * @param mapModulesToServers True if map modules to server information should be generated
     */
    public void setMapModulesToServers(boolean mapModulesToServers) {
        this.mapModulesToServers = mapModulesToServers;
    }

    /**
     * This method will set the classloaderMode of a module within an application. The Ant xml file
     * must have the classLoaderMode attribute in this format:
     * 
     * <module>=<class loader mode>,<module>=<class loader mode>
     * 
     * For example: AxisCoexistenceApp.war=PARENT_LAST
     * 
     * @param classLoaderMode A String containing first the module name = and then the class loader
     *            mode
     */
    public void setClassLoaderMode(String classLoaderMode) {
        this.classLoaderMode = classLoaderMode;
    }

    /**
     * This is a setter method for the server name.
     * 
     * @param serverName The name of the server to install app to
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * This is a setter method for the war name.
     * 
     * @param warName The name of the war to install
     */
    public void setWarName(String warName) {
        this.warName = warName;
    }

    /**
     * This is a setter method for the context root.
     * 
     * @param contextRoot The name of the war to install
     */
    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

    /**
     * @param serverKey the serverKey to set
     */
    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    /**
     * This method will allow for PolicySet elements to be added to the InstallApp task.
     * 
     * @return A PolicySet object
     */
    public PolicySet createPolicySet() {
        return new PolicySet();
    }

    /**
     * This class provides a way to set the PolicySet element.
     */
    public class PolicySet {

        private String to = null;
        private String from = null;
        private String host = null;

        /**
         * This method will set the host. This is the host of where the policy set will be copied
         * to.
         * 
         * @param _host The host
         */
        public void setHost(String _host) {
            host = _host;
            addPolicySet();
        }

        /**
         * This method will get the host.
         * 
         * @return The host
         */
        public String getHost() {
            return host;
        }

        /**
         * This method will set the to. This where the PolicySet should be copied to.
         * 
         * @param _to The directory where policy set should be copied
         */
        public void setTo(String _to) {
            to = _to;
            addPolicySet();
        }

        /**
         * This method will set the to. This where the PolicySet should be copied to.
         * 
         * @return The directory where policy set should be copied
         */
        public String getTo() {
            return to;
        }

        /**
         * This method will set where the policy set is to be copied from.
         * 
         * @param _from Where the policy set is copied from
         */
        public void setFrom(String _from) {
            from = _from;
            addPolicySet();
        }

        /**
         * This method will set where the policy set is to be copied from.
         * 
         * @param from Where the policy set is copied from
         */
        public String getFrom() {
            return from;
        }

        /**
         * This method will create the PolicySet object.
         */
        private void addPolicySet() {
            if (to != null && from != null && host != null) {
                if (policySets == null) {
                    policySets = new ArrayList<PolicySet>();
                }
                policySets.add(this);
            }
        }

    }

    public void setDataSourceFor10CMPBeans(String dataSourceFor10CMPBeans) {
        this.dataSourceFor10CMPBeans = dataSourceFor10CMPBeans;
    }

    public void setDataSourceFor10EJBModules(String dataSourceFor10EJBModules) {
        this.dataSourceFor10EJBModules = dataSourceFor10EJBModules;
    }

    public void setDataSourceFor20CMPBeans(String dataSourceFor20CMPBeans) {
        this.dataSourceFor20CMPBeans = dataSourceFor20CMPBeans;
    }

    public void setDataSourceFor20EJBModules(String dataSourceFor20EJBModules) {
        this.dataSourceFor20EJBModules = dataSourceFor20EJBModules;
    }

    public void setDefaultBindingDataSourceJNDI(String defaultBindingDataSourceJNDI) {
        this.defaultBindingDataSourceJNDI = defaultBindingDataSourceJNDI;
    }

    public void setDefaultBindingVirtualHost(String defaultBindingVirtualHost) {
        this.defaultBindingVirtualHost = defaultBindingVirtualHost;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
        this.logStartupFailures = failOnError;
    }

    public void setUseWSFEP61ScanPolicy(boolean useWSFEP61ScanPolicy) {
        this.useWSFEP61ScanPolicy = useWSFEP61ScanPolicy;
    }

    public void setUseMetaDataFromBinary(boolean useMetaDataFromBinary) {
        this.useMetaDataFromBinary = useMetaDataFromBinary;
    }
}
