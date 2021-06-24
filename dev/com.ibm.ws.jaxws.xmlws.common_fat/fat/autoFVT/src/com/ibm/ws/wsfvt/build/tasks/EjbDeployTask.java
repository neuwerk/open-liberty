/*
 * @(#) 1.6 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/EjbDeployTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:23 [8/8/12 06:56:43]
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2003, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect       Author           Description
 * ----------  -----------------    ---------        ----------------------------
 * 02/17/2003  D158799              ulbricht         New file
 * 01/07/2004  D186680              ulbricht         z/OS needs different exec method
 * 05/04/2005  D269183.6            ulbricht         Change for iSeries
 * 10/30/2006  401700               jramos           Corrected logic
 * 01/04/2007  411800               smithd           Changes to executeCommand methods
 * 04/16/2007  432350               ulbricht         Various fixes
 * 05/23/2007  440922               jramos           Changes for Pyxis
 * 10/17/2007  475591               sedov            Added logging & BuildException
 * 10/22/2007  476750               jramos           Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/22/2008  jramos      559143          Incorporate Simplicity
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * This class will call the ejbdeploy.bat/sh file to run ejbdeploy on
 * an EJB.  This task can be performed through the application install
 * process, but possibly the classes generated through ejbdeploy are
 * needed in a client application.
 *
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class EjbDeployTask extends Task {

    private File inputEar;
    private File workDir;
    private File outputEar;

    /**
     * This method will perform the ejbdeploy command on an Ear file containing
     * an EJB.
     *
     * @throws org.apache.tools.ant.BuildException Any kind of build exception
     */
    public void execute() throws BuildException {
        
    	String lineSep = System.getProperty("line.separator");
        log("EjbDeployTask has the following values: "
            + lineSep
            + "    inputEar            = "
            + inputEar != null ? inputEar.getPath() : "null"
            + lineSep
            + "    workDir             = "
            + workDir != null ? workDir.getPath() : "null"
            + lineSep
            + "    outputEar           = "
            + outputEar != null ? outputEar.getPath() : "null"
            + lineSep,
            Project.MSG_VERBOSE);
        
        String ejbDeploy = null;
        try {
            ejbDeploy = AppConst.WAS_HOME.replace('\\', '/') + "/bin/ejbdeploy"
                    + Machine.getLocalMachine().getOperatingSystem().getDefaultScriptSuffix();
        } catch (Exception e) {
            throw new BuildException(e);
        }
        
        File ejbDeployFile = new File(ejbDeploy);

        if (ejbDeployFile == null || !ejbDeployFile.exists()) {
            throw new BuildException("The " + ejbDeployFile.getPath()
                                     + " doesn't exist.");
        }

        if (inputEar == null || !inputEar.exists()) {
            throw new BuildException("The " + inputEar.getPath()
                                     + " doesn't exist.");
        }

        if (workDir == null || !workDir.exists()) {
            throw new BuildException("The " + workDir.getPath()
                                     + " doesn't exist.");
        }

        String outputFileName = outputEar.getName();
        if (!(outputFileName.endsWith(".ear")
               || outputFileName.endsWith(".jar"))) {
            throw new BuildException("The deployed filename must end "
                                     + "in '.jar' or '.ear'.");
        }

        String[] parms = new String[3];
        parms[0] = inputEar.getPath();
        parms[1] = workDir.getPath();
        parms[2] = outputEar.getPath();

        try {
            Machine local = Machine.getLocalMachine();
            String reply = local.execute(ejbDeploy, parms).getStdout();
        	log(reply.replaceAll("[\r\n]+", "\n"));
        	
        	if (reply.indexOf("0 Errors") == -1){
        		throw new Exception("EjbDeploy reported errors");
        	}
        } catch (Exception e) {
        	throw new BuildException(e);
        }

    }

    /**
     * This method sets the input ear file on which ejbdeploy will
     * work.
     *
     * @param _inputEar File name with the element to copy to a new
     *            file
     */ 
    public void setInputEar(File _inputEar) {
        inputEar = _inputEar;
    }

    /**
     * This method sets the name for the output file from the ejbdeploy
     * command.
     * 
     * @param _outputEar File name where the element will be placed
     */ 
    public void setOutputEar(File _outputEar) {
        outputEar = _outputEar;
    }

    /**
     * This method accepts a directory where the ejbdeploy command
     * will work.
     *
     * @param _workDir A directory where the ejbdeploy can work
     */
    public void setWorkDir(File _workDir) {
        workDir = _workDir;
    }

}
