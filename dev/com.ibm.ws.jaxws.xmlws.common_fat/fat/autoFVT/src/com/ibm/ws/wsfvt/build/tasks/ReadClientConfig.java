/*
 * 
 * @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ReadClientConfig.java, WAS.websvcs.fvt, WASX.FVT 10/5/10 15:33:56 [10/6/10 00:41:43]
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 10/04/2010  whsu        668510             FAT:extend serviceref appclient testcases for appclient image 
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.ServerType;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.websphere.simplicity.WebSphereTopologyType;
import com.ibm.websphere.simplicity.WebSphereVersion;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.AntProperties;


public class ReadClientConfig extends Task {

	private String key = null;
	private String prefix = null;	
	private boolean isVerbose = false;
        private Machine localMachine = null;
    
	public void execute() {

		if (key == null || key.length() == 0) {
			// We can't allow for a null or 0 length key
			key = "appclient";
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
        
           try {
            localMachine = Machine.getLocalMachine();
    
    		log("ReadClientConfig has the following values: " + localMachine.getOperatingSystem().getLineSeparator()
    				+ "    key                   = '" + key + "'" + localMachine.getOperatingSystem().getLineSeparator()
    				+ "    prefix                = '" + prefix + "'" + localMachine.getOperatingSystem().getLineSeparator(),
    				Project.MSG_VERBOSE);
    
    		// process client key
    		if ("client".equals(key)) {
    			populateValuesIntoProject(prefix);
    		}   else {
                throw new BuildException("Unable to resolve '" + key + "' to appclient");
            }
        } catch(Exception e) {
            e.printStackTrace();
            log(e.getMessage());
            throw new BuildException(e);
        }
		
	}

	/**
	 * Populates property values in the Project, so that any ant task can
	 * use them.
	 * 
	 * @param prefix
	 *            The prefex to use when setting these values into the Project
	 */
	private void populateValuesIntoProject(String prefix) throws Exception {
		log("ReadClientConfig populating properties for key: " + key + localMachine.getOperatingSystem().getLineSeparator(),
				Project.MSG_VERBOSE);

            Properties bootP = new Properties();
            try {
               String fvtRoot = AppConst.FVT_HOME;
               fvtRoot = fvtRoot.replace('\\', '/');
               bootP.load(new FileInputStream(fvtRoot + "/bootstrapping.properties"));
            } catch (IOException ioe) {
               System.out.println("Failed to load bootStrap...\n");
               ioe.printStackTrace();
            }
           
            String appClientHome = null;
            String clientHostName = null;

            appClientHome = bootP.getProperty("appclient.install.1.installRoot");
            clientHostName = bootP.getProperty("appclient.install.1.hostname");

            if (appClientHome == null || appClientHome == "") 
                   appClientHome = AntProperties.getAntProperty("appclient.install.1.installRoot");

            if (clientHostName == null || clientHostName == "") 
                   clientHostName = AntProperties.getAntProperty("appclient.install.1.hostname");

            System.out.println("DEBUG : prefix : " + prefix + "\n");

            System.out.println("DEBUG : appClientHome : " + appClientHome + "\n");
            System.out.println("DEBUG : clientHostName : " + clientHostName + "\n");

  
	    // Populate  values for the client 
            if (appClientHome != null && appClientHome != "")
		setProperty(prefix + "install", appClientHome);
            if (clientHostName != null && clientHostName != "")
		setProperty(prefix + "host", clientHostName);
						
       }
       

	 private void setProperty(String name, String value) throws Exception {
		if (isVerbose){
			log(name + "=" + value + localMachine.getOperatingSystem().getLineSeparator(),
					Project.MSG_ERR);			
		}
		Project p = getProject();
		p.setProperty(name, value);
	  }
	
	 public void setPrefix(String prefix) {
		this.prefix = prefix;
	 }

         public void setKey(String key) {
		this.key = key;
	 }

         public void setVerbose(String verbose) {
                this.isVerbose = Boolean.parseBoolean(verbose);
         }
  
        public static void main(String[] args) {
        ReadClientConfig task = new ReadClientConfig();
        task.setKey("appclient");
        task.setPrefix("install");
        task.execute();
    }
}
