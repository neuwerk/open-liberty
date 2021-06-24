/*
 *
 * IBM Confidential OCO Source Material
 * 5724-i63 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect   Author     Description
 * ----------  ---------------  --------   ---------------------------------
 * 06/26/2012  736024           syed       Created for OAuth tests
 *
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ibm.websphere.simplicity.Topology;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.ProgramOutput;

import com.ibm.websphere.simplicity.Node;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.websphere.simplicity.WebSphereTopologyType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

import com.ibm.websphere.simplicity.Cell;


/**
 * This class will import the signer certificate from an external STS to 
 * the default SSL trust store on the test machine.
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class ConfigOAuthDerby extends Task {

    private String derbyDbLoc =  AppConst.FVT_HOME + "/build/work/oauth20/sql/";

    private String sqlFile = derbyDbLoc + "createTables.sql";


    /**
     * This method will the derby ij command to create derby database
     * and to add oauth clients to the database.
     *
     * @throws org.apache.tools.ant.BuildException Any kind of build exception
     */
    public void execute() throws BuildException {
        
        try {

            OperatingSystem osName = OperatingSystem.WINDOWS;

            Topology.init();

            Machine localMachine = Machine.getLocalMachine();

            String derbyIjCmd = null;
            File derbyExecFile = null; 

            // Command is different depending on OS
            osName = localMachine.getOperatingSystem();
            if (osName == OperatingSystem.WINDOWS) {
                // System.out.println("It's WINDOWS...");
                derbyExecFile = new File(AppConst.WAS_HOME + "/"
                                       + "derby/bin/embedded/"
                                       + "ij.bat");
                derbyIjCmd = derbyExecFile.getPath();

            } else {
                // System.out.println("It's NOT WINDOWS, assume Unix, Linux ");
                derbyExecFile = new File(AppConst.WAS_HOME + "/"
                                       + "derby/bin/embedded/"
                                       + "ij.sh");
                derbyIjCmd = derbyExecFile.getPath();
            }

            String[] params = new String[] {sqlFile};

            System.out.println("Derby command: " + derbyIjCmd);
    
            if (!derbyExecFile.exists()) {

                throw new BuildException("Derby executable file "
                                         + "not found at " + derbyExecFile.getPath()
                                         + ".");

            }

            // Execute ij to configure derby db
            ProgramOutput po = localMachine.execute(derbyIjCmd, params);

            System.out.println("Derby returned: " + po.getStdout() +
                                " " +  po.getStderr() ); 
        } catch (Exception e) {
            System.out.println("Derby ij returned Exception"); 
            throw new BuildException(e);
	}

    }

    /**
     * This method accepts file name of derby sql file
     *
     * @param _sqlFile is derby SQL file name
     */
    public void setSqlFile(String sqlFile) {

        this.sqlFile = derbyDbLoc + sqlFile;

        return;
    }

}
