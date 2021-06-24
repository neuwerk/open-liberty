/*
 * @(#) 1.17 FVT/ws/code/websvcs.fvt/src/com/ibm/ws/wsfvt/build/tasks/ReadCellConfig.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:35 [8/8/12 06:55:38]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 11/01/2006  sedov       402504             New File
 * 11/03/2006  smithd      400682             Upgrade to use findIServerProcess, portMap
 * 11/03/2006  sedov       403119             Added topology properties and verbose flag
 * 11/07/2006  sedov       403497             Added deploy target
 * 03/28/2007  ulbricht    428364             Add ability to get encoded password
 * 04/18/2007  ulbricht    429141             Remove print stack trace
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 09/12/2007  sedov       466314             Temporary fix to support AdminAgent
 * 10/22/07    jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/25/07    sedov       476185             Added AdminAgent as root server support
 * 11/08/07    sedov       481501             Added profileName
 * 03/13/08    sedov       403499             Read security from server's cell
 * 06/12/08    jramos      524904             Add support for downLevelServer for migration and mixed cell testing
 * 10/29/2008  jramos      559143             Incorporate Simplicity
 */
package com.ibm.ws.wsfvt.build.tasks.liberty;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.Topology;



public class ReadCellConfig extends Task {

	private String key = null;
	private String prefix = null;	
	private boolean isVerbose = false;    
    
	public void execute() {

		if (key == null || key.length() == 0) {
			// We can't allow for a null or 0 length key
			key = "defaultServer";
		}

		if (prefix == null || prefix.length() == 0) {
			// If they don't specify a prefix, we make it empty
			prefix = "";
		} else {
			// Append the "." (dot) at the end to allow for easy usability
			if (!prefix.endsWith(".")) {
				prefix = prefix + ".";
			}
		}
        
        Server server = null; 
		try {
			System.out.println("Call liberty ReadCellConfig...");
			Topology.init();
			// otherwise look up the server described by the server key
			List<Cell> cells = Topology.getCells();
			for (Cell cell : cells) {
				Set<Server> servers = cell.getServers();
				for (Server s : servers) {
					if (s.getBootstrapFileKey().equals(key)) {
						server = s;
						break;
					}
				}
			}

			if (server != null) {
				log("Resolved " + key + " to " + server.getNode().getName()
						+ ", " + server.getName());
				populateValuesIntoProject(server.getCell(),
						(ApplicationServer) server, prefix);
			} else {
				throw new BuildException("Unable to resolve '" + key
						+ "' to server");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log(e.getMessage());
			throw new BuildException(e);
		}
		
	}

	/**
	 * Populates the <code>Servers</code>'s generic and port values in the Project, so that any ant task can
	 * use them.
	 * 
	 * @param isp
	 *            The Server to populate
	 * @param prefix
	 *            The prefex to use when setting these values into the Project
	 */
	private void populateValuesIntoProject(Cell cell, ApplicationServer server, String prefix) throws Exception {		

		String portName = null;
		Integer portValue = null;

		// Populate the generic values for the cell
		// always use "localhost" as hostname in case unexpected network issue in build env
		//setProperty(prefix + "hostName", server.getNode().getMachine().getHostname());
		setProperty(prefix + "hostName", "localhost");
		setProperty(prefix + "cellName", server.getCell().getName());
		setProperty(prefix + "nodeName", server.getNode().getName());
		setProperty(prefix + "serverName", server.getName());
		setProperty(prefix + "profileDir", server.getNode().getProfileDir());
		setProperty(prefix + "propertiesDir", server.getNode().getWASInstall().getInstallRoot() + "/properties");
		setProperty(prefix + "wasBaseDir", server.getNode().getWASInstall().getInstallRoot());
		setProperty(prefix + "fvtBaseDir", server.getNode().getMachine().getTempDir().getAbsolutePath());
		setProperty(prefix + "profileName", server.getNode().getProfileName());
		
		setProperty(prefix + "deployTarget", server.getMappingName());		

		/* The value of property "http.Default.Port" from bootstrapping.properties will be used during jaxws_fat
         * compilation phase to generate jaxws test artifacts. Its value sets to WC_defaulthost port.
         */
        PortType[] ports = PortType.values();
        for(int i = 0; i < ports.length; ++i) {
            portName = ports[i].getPortName();
            portValue = server.getPortNumber(ports[i]);
            if(portValue != null) {
                setProperty(prefix + portName, "" + portValue.intValue());
            }
            System.out.println("Liberty ReadCellConfig set " + prefix + portName + "=" + portValue.intValue() + " from server instance");
        }
		
        /* jaxws_fat_server.HTTP_default property only exist in TestBuild.xml scope,
         * not in buildTest.xml scope. 
         * 
		// ensure WC_defaulthost and WC_defaulthost_secure is correct
		if (getProject().getProperty("jaxws_fat_server.HTTP_default") != null) {
			setProperty(prefix + "WC_defaulthost", getProject().getProperty("jaxws_fat_server.HTTP_default"));
			
			System.out.println("Liberty ReadCellConfig set " + prefix + "WC_defaulthost=" 
			+ getProject().getProperty("jaxws_fat_server.HTTP_default") + " from jaxws_fat_server.HTTP_default");
		}
		
		if (getProject().getProperty("jaxws_fat_server.HTTP_default.secure") != null){
			setProperty(prefix + "WC_defaulthost_secure", getProject().getProperty("jaxws_fat_server.HTTP_default.secure"));
			
			System.out.println("Liberty ReadCellConfig set " + prefix + "WC_defaulthost_secure=" 
					+ getProject().getProperty("jaxws_fat_server.HTTP_default.secure") + " from jaxws_fat_server.HTTP_default.secure");
		}
		*/
			
	}

	private void setProperty(String name, String value) throws Exception {
		
		Project p = getProject();
		p.setProperty(name, value);
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setVerbose(String verbose) {
		this.isVerbose = Boolean.parseBoolean(verbose);
	}	
	
	public void setKey(String key) {
		this.key = key;
	}

    /**
     * This method will encode the password retrieved from the
     * topologyProps.props file.  The implementation of this
     * method has been designed to avoid compile time dependencies
     * on WAS jars.
     *
     * If an exception occurs trying to get the encoded password,
     * the plain text pasword will be returned.
     *
     * @param plainTextPassword The plain text password
     * @return Encoded password if possible
     */
    private String getEncodedPassword(String plainTextPassword) {
        String encodedPassword = plainTextPassword;
        try {
            Class cls = Class.forName("com.ibm.ws.security.util.PasswordUtil");
            Class parttypes[] = new Class[1];
            parttypes[0] = String.class;
            Method meth = cls.getMethod("passwordEncode", parttypes);
            Object arglist[] = new Object[1];
            arglist[0] = plainTextPassword;
            Object retObj = meth.invoke(null, arglist);
            encodedPassword = (String)retObj;
        } catch (Throwable t) {
            // make a best attempt to get encoded password, but
            // if not just return plain text password
        }
        return encodedPassword;
    }

    public static void main(String[] args) {
        ReadCellConfig task = new ReadCellConfig();
        task.setKey("defaultServer");
        task.setPrefix("default");
        task.execute();
    }
}
