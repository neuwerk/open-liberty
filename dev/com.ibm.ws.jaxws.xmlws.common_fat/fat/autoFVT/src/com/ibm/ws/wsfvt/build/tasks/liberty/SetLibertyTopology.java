/*
 * @(#) 1.8 FVT/ws/code/websvcs.fvt/src/com/ibm/ws/wsfvt/build/tasks/SetFrameworkTopology.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/26/09 16:15:25 [8/8/12 06:41:15]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 01/15/09    jramos       571132            New File
 * 02/05/09    jramos       573731            Update keystore in boostrapping.properties
 * 03/18/09    jramos       575547            Always create test server
 * 03/23/09    jramos       581370            Update keystore location before calling Topology.init()
 * 08/25/09    jramos       608573            Use different server names for the multi node config
 */

package com.ibm.ws.wsfvt.build.tasks.liberty;



import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


import com.ibm.websphere.simplicity.Topology;

public class SetLibertyTopology extends Task {

       
    public void execute() throws BuildException {
        try {            
        	Topology.init();
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }   
    
    
    public static void main(String[] args) {
        SetLibertyTopology task = new SetLibertyTopology();        
        task.execute();
    }
}
