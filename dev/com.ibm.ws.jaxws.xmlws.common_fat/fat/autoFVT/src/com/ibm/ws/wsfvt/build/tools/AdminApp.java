/*
 * @(#) 1.9 autoFVT/src/com/ibm/ws/wsfvt/build/tools/AdminApp.java, WAS.websvcs.fvt, WAS70.FVT, cf091004.53 2/2/09 08:41:50 [2/1/10 23:23:44]
 *
 * IBM Confidential OCO Source Material
 * 5724-i63 (C) COPYRIGHT International Business Machines Corp. 2003, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect       Author           Description
 * ----------  -----------------    ---------        ----------------------------
 * 03/28/2003  D162190              ulbricht         Default in wsadmin is to deployejb
 * 04/13/2003  LIDB1738.09.07       ulbricht         Add option to -deployws
 * 05/13/2003  D163811              ulbricht         Add option for listener port 
 * 10/05/2003  D178846              ulbricht         Allow install from any directory
 * 10/09/2003  D179331              ulbricht         Remove executeRunning method; use local
 *                                                     var instead and execute method 
 * 01/07/2004  D186584              ulbricht         Change install location
 * 01/07/2004  D186680              ulbricht         z/OS needs different exec method         
 * 01/08/2004  D186806              ulbricht         z/OS needs encoding
 * 01/30/2004  D188860              ulbricht         Node and cell names changed
 * 03/03/2004  LIDB2931-05.3        ulbricht         Add edit binding options
 * 04/21/2004  D199591              ulbricht         Uninstall hanging
 * 05/27/2004  D207227              ulbricht         Add ClientBindPreferredPort
 * 06/24/2004  D211929              ulbricht         Modify path for profiles
 * 07/09/2004  D215233              ulbricht         Change method for getting cell name
 * 08/19/2004  D225528              ulbricht         z/OS needs different encoding        
 * 03/10/2005  D260687              ulbricht         Add support for post app install jacl
 * 04/15/2005  D269183              ulbricht         Add support for iSeries
 * 05/16/2005  D274529              ulbricht         Security FVT does not need profile
 * 04/25/2006  365131               jramos           Add executeInstallGetResult() and executeGetResult()
 * 04/28/2006  365687               ulbricht         Add port number to wsadmin execution
 * 05/10/2006  367045               ulbricht         Uninstall needs to account for conntype
 * 05/27/2006  370654               ulbricht         Allow install of WAR files
 * 08/15/2006  381622               smithd           Add ND support
 * 10/31/2006  401994               sedov            Add defaultbinding.virtual.host and .datasource.jndi support
 * 04/16/2007  432444               jramos           change call to getProductVersion to getProductID
 * 05/24/2007  440922               jramos           Changes for Pyxis
 * 10/22/2007  476750               jramos           Use TopologyDefaults tool and ACUTE 2.0 api
 * 11/01/2007  479154               sedov            Language defaults to jython
 * 05/02/2008  517704               jramos           Add support for UseWSFEP61ScanPolicy property when starting wsadmin in local mode
 * 10/31/2008  559143               jramos           Rework using Simplicity
 * 01/21/2009  570716               jramos           Update for war file installs
 */
package com.ibm.ws.wsfvt.build.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.websphere.simplicity.BootStrappingProperty;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.OperationResults;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Scope;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.WebSphereTopologyType;
import com.ibm.websphere.simplicity.WsadminConnectionOptions;
import com.ibm.websphere.simplicity.application.Application;
import com.ibm.websphere.simplicity.application.ArchiveType;
import com.ibm.websphere.simplicity.application.AssetModule;
import com.ibm.websphere.simplicity.application.InstallWrapper;
import com.ibm.websphere.simplicity.configuration.ConfigurationProvider;


/**
 * The AdminApp class models the properties and commands necessary when executing commands using the wsadmin and
 * $AdminApp commands.
 */
public class AdminApp {

	private RemoteFile earFile = null;
	private RemoteFile warFile = null;
	private String appName = "";
	private boolean local = false;
	private boolean useDefaultBindings = false;
	private boolean deployEjb = false;
	private boolean deployWs = false;
    private boolean useWSFEP61ScanPolicy = true;
    private boolean useMetaDataFromBinary = false;
	private String contextRoot = null;

    private Map<AssetModule, List<Scope>> mmts = new HashMap<AssetModule, List<Scope>>();
    
	private String defaultBindingVirtualHost = null;
	private String defaultBindingDataSourceJNDI = null;
	private String dataSourceFor20EJBModules = null;
	private String dataSourceFor20CMPBeans = null;
	private String dataSourceFor10EJBModules = null;
	private String dataSourceFor10CMPBeans = null;	
    
    private Cell cell = null;
	
	public AdminApp(Cell cell) throws Exception {
		this.cell = cell;
	}

	/**
	 * This method sets the ear file that AdminApp will execute a command against.
	 * 
	 * @param earFile
	 *            A RemoteFile containing the name of an ear file
	 */
	public void setEarFile(RemoteFile earFile) {
		this.earFile = earFile;
	}

	/**
	 * This method sets the war file that AdminApp will execute a command against.
	 * 
	 * @param earFile
	 *            A RemoteFile containing the name of an ear file
	 */
	public void setWarFile(RemoteFile warFile) {
		this.warFile = warFile;
	}

	/**
	 * This method returns the name of the ear file that AdminApp will run the commands against.
	 * 
	 * @return A RemoteFile containing the name of an ear file
	 */
	public RemoteFile getEarFile() {
		return this.earFile;
	}

	/**
	 * This method sets the name of the app for the AdminApp command, so that it doesn't derive its own name for the
	 * application.
	 * 
	 * @param appName
	 *            A String containing the app name
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * This method returns a String containing the app name.
	 * 
	 * @return A String containing the app name
	 */
	public String getAppName() {
		return this.appName;
	}

	/**
	 * This method sets a local boolean variable to determine whether the AdminApp command will be executing against a
	 * running application server.
	 * 
	 * @param local
	 *            A boolean parameter set to true if the application server is not running
	 */
	public void setLocal(boolean local) {
		this.local = local;
	}

	/**
	 * This method sets a boolean variable to determine whether the AdminApp install command will use the -deployejb
	 * option.
	 * 
	 * @param deployEjb
	 *            A boolean parameter set to true if the application server is not running
	 */
	public void setDeployEjb(boolean deployEjb) {
		this.deployEjb = deployEjb;
	}

	/**
	 * This method sets boolean to determine whether the AdminApp install command will use the -deployws option.
	 * 
	 * @param deployWs
	 *            True if the -deployws option should be used when installing an application
	 */
	public void setDeployWs(boolean deployWs) {
		this.deployWs = deployWs;
	}

	/**
	 * This method sets a local boolean variable to determine whether the AdminApp command will be executing against a
	 * running application server.
	 * 
	 * @param useDefaultBindings
	 *            A boolean parameter set to true if the application server is not running
	 */
	public void setUseDefaultBindings(boolean useDefaultBindings) {
		this.useDefaultBindings = useDefaultBindings;
	}

	/**
	 * This method sets the context root to be used when installing an application.
	 * 
	 * @param contextRoot
	 *            The context root to be used for install
	 */
	public void setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;
	}

	/**
	 * This method returns the value of whether the AdminApp command will be executing against a running application
	 * server.
	 * 
	 * @return A boolean variable set to true if the application server will not be running
	 */
	public boolean getLocal() {
		return this.local;
	}

	/**
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}

	/**
	 * @param cell
	 *            the cell to set
	 */
	public void setCell(Cell cell) {
		this.cell = cell;
	}

    /**
     * @param useWSFEP61ScanPolicy
     *                 the value of the UseWSFEP61ScanPolicy property when invoking wsadmin for app install
     */
    public void setUseWSFEP61ScanPolicy(boolean useWSFEP61ScanPolicy) {
        this.useWSFEP61ScanPolicy = useWSFEP61ScanPolicy;
    }
    
	public String getDataSourceFor10CMPBeans() {
		return this.dataSourceFor10CMPBeans;
	}
	
	public String getDataSourceFor10EJBModules() {
		return this.dataSourceFor10EJBModules;
	}
	
	public String getDataSourceFor20CMPBeans() {
		return this.dataSourceFor20CMPBeans;
	}
	
	public String getDataSourceFor20EJBModules() {
		return this.dataSourceFor20EJBModules;
	}

	public String getDefaultBindingDataSourceJNDI() {
		return this.defaultBindingDataSourceJNDI;
	}
	
	public String getDefaultBindingVirtualHost() {
		return this.defaultBindingVirtualHost;
	}
	
	public boolean getDeployEjb() {
		return this.deployEjb;
	}
	
	public boolean getDeployWs() {
		return this.deployWs;
	}
    
    public boolean getUseWSFEP61ScanPolicy() {
        return this.useWSFEP61ScanPolicy;
    }
    
	public boolean getUseDefaultBindings() {
		return this.useDefaultBindings;
	}
	
	public RemoteFile getWarFile() {
		return this.warFile;
	}
    
    public String getContextRoot() {
        return this.contextRoot;
    }
    
	public void setUseMetaDataFromBinary(boolean useMetaDataFromBinary) {
        this.useMetaDataFromBinary = useMetaDataFromBinary;
	}

	public boolean getUseMetaDataFromBinary() {
        return this.useMetaDataFromBinary;
	}


    public InstallWrapper getInstallWrapper() throws Exception {
        InstallWrapper wrapper = null;
        if(earFile != null) {
            wrapper = this.cell.getApplicationManager().getInstallWrapper(this.earFile);
        } else {
            wrapper = this.cell.getApplicationManager().getInstallWrapper(this.warFile, ArchiveType.WAR);
        }
        if(appName != null) {
            wrapper.getAppDeploymentOptions().setApplicationName(appName);
        }
        wrapper.getAppDeploymentOptions().setEjbDeploy(deployEjb);
        wrapper.getAppDeploymentOptions().setWsDeploy(deployWs);
        wrapper.getAppDeploymentOptions().setUseDefaultBindings(useDefaultBindings);

        if (useMetaDataFromBinary) {
            wrapper.getAppDeploymentOptions().setUseBinaryConfig(useMetaDataFromBinary);
         }

        if(contextRoot != null) {
            Set<AssetModule> modules = wrapper.getModules();
            for(AssetModule module : modules) {
                wrapper.getCtxRootForWebMod().setContextRoot(module, contextRoot);
            }
        }
        if(mmts.size() > 0) {
            for(AssetModule module : mmts.keySet()) {
                wrapper.getMapModulesToServers().setTarget(module, mmts.get(module));
            }
        }
        if(defaultBindingVirtualHost != null) {
            
        }
        return wrapper;
    }
    
    public String installApp() throws Exception {
        if(local && cell.getConnInfo().getConnType() != ConnectorType.NONE) {
            cell.disconnect();
            ConnectionInfo current = cell.getConnInfo();
            WsadminConnectionOptions connOptions = (WsadminConnectionOptions)cell.getConnInfo().clone(ConnectorType.NONE, -1, current.getUser(), current.getPassword());
            if(useWSFEP61ScanPolicy) {
                List<String> javaOps = new ArrayList<String>();
                javaOps.add("-Dcom.ibm.websphere.webservices.UseWSFEP61ScanPolicy=true");
                connOptions.setJavaoptions(javaOps);
            }
            cell.pushConnection(connOptions);
            // make this default in bootstrapping props
            ConfigurationProvider c = Topology.getBootstrapFileOps().getConfigurationProvider();
            c.setProperty(cell.getBootstrapFileKey() + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.CONN_TYPE, ConnectorType.NONE.toString());
            c.writeProperties();
        }
        
        InstallWrapper wrapper = getInstallWrapper();
        OperationResults<Application> results = cell.getApplicationManager().install(wrapper);
        System.out.println("Application installed. Saving the workspace.");
        if(cell.getTopologyType() == WebSphereTopologyType.ND)
            System.out.println("Syncing with the nodes during the save.");
        cell.getWorkspace().saveAndSync();
        return results.getSystemOut();
    }
    
    public Application getApplication() throws Exception {
        return cell.getApplicationManager().getApplicationByName(appName);
    }
    
    public String uninstallApp() throws Exception {
        if(cell.getApplicationManager().isInstalled(appName)) {
            if(local && cell.getConnInfo().getConnType() != ConnectorType.NONE) {
                cell.disconnect();
                ConnectionInfo current = cell.getConnInfo();
                WsadminConnectionOptions connOptions = (WsadminConnectionOptions)cell.getConnInfo().clone(ConnectorType.NONE, -1, current.getUser(), current.getPassword());
                cell.pushConnection(connOptions);
                // make this default in bootstrapping props
                ConfigurationProvider c = Topology.getBootstrapFileOps().getConfigurationProvider();
                c.setProperty(cell.getBootstrapFileKey() + BootStrappingProperty.PROPERTY_SEP + BootStrappingProperty.CONN_TYPE, ConnectorType.NONE.toString());
                c.writeProperties();
            }
            
            OperationResults<Boolean> results = cell.getApplicationManager().uninstall(appName);
            cell.getWorkspace().saveAndSync();
            return results.getSystemOut();
        }
        return "Application " + appName + " is not installed.";
    }
    
    public Set<AssetModule> getModules() throws Exception {
        if(cell.getApplicationManager().isInstalled(appName)) {
            return cell.getApplicationManager().getApplicationByName(appName).getModules();
        } else {
            return getInstallWrapper().getModules();
        }
    }

	/**
	 * This method return true if the application is installed
	 * 
	 * @return A boolean value of true if the application is installed
	 */
	public boolean isAppInstalled() throws Exception {
		return cell.getApplicationManager().getApplicationByName(appName) != null;
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
    
    public void addMapModulesToServers(AssetModule module, List<Scope> scopes) {
        mmts.put(module, scopes);
    }
}
