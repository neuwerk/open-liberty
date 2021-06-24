/*
 * @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/StopIhsTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:51 [8/8/12 06:56:44]
 *
 * COMPONENT_NAME: WAS.webservices.fvt
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect       Author           Description
 * ----------  -----------------    ---------        ----------------------------
 * 02/25/2003  LIDB1270.256.09      ulbricht         New file
 * 01/07/2004  D186680              ulbricht         z/OS needs different exec method
 * 06/05/2005  PK05310.1            ulbricht         Add log statements
 * 08/17/2006  D381622              smithd           Add ND support
 * 01/04/2007  D411800              smithd           Changes to executeCommand methods
 * 05/23/2007  440922               jramos           Changes for Pyxis
 * 10/22/2007  476750               jramos           Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/30/2008  559143               jramos           Incorporate Simplicity
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;

/**
 * This class will stop the IBM HTTP Server.
 *
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class StopIhsTask extends Task {

    private File ihsDir;

    /**
     * This method will perform the command necessary to stop IHS.
     *
     * @throws org.apache.tools.ant.BuildException Any kind of build exception
     */
    public void execute() throws BuildException {
        
        try {
            Machine localMachine = Machine.getLocalMachine();
            
            log("StopIhsTask properties:"
                + System.getProperty("line.separator")
                + "    ihsDir               = " + ihsDir.getPath()
                + System.getProperty("line.separator"),
                Project.MSG_VERBOSE);
    
            if (!ihsDir.exists()) {
                throw new BuildException("IHS installation directory "
                                         + "not found at " + ihsDir.getPath() + ".");
            }
    
            String ihsCommand = null;
            List<String> params = new ArrayList<String>();
            ihsCommand = ihsDir.getPath() + "/" + "bin" + "/";
    
            // Command is different for starting IHS depending on OS
            if (localMachine.getOperatingSystem() == OperatingSystem.WINDOWS) {
                ihsCommand += "Apache.exe";
                params.add("-k stop");
            } else {
                ihsCommand += "apachectl";
                params.add("stop");
            }

            localMachine.execute(ihsCommand, params.toArray(new String[0]));
        } catch (Exception e) {
            throw new BuildException(e);
        }

    }

    /**
     * This method accepts a directory where IBM HTTP Server is installed.
     *
     * @param _ihsDir A directory where the ejbdeploy can work
     */
    public void setIhsDir(File _ihsDir) {
        ihsDir = _ihsDir;
    }

}
