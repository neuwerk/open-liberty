/*
 * @(#) 1.8 FVT/ws/code/websvcs.fvt/src/com/ibm/ws/wsfvt/build/tasks/StopWAS.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 8/31/09 13:07:58 [8/8/12 06:56:44]
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 01/08/2003  ulbricht    D154318.5          Ant could not find the StopWAS class
 * 05/20/2003  ulbricht    D166543            Add username & password for security
 * 04/28/2005  ulbricht    D269183.5          Add profile for iSeries
 * 08/14/2006  smithd      D381622            Integrate the Cell object (ND support)
 * 05/23/2007  jramos      440922             Changes for Pyxis
 * 10/22/2007  jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/22/2007  sedov       476785             Print more detail on start error
 * 06/12/2008  jramos      524904             Add support for second cell for migration testing
 * 10/30/2008  jramos      559143             Incorporate Simplicity
 * 08/27/2009  jramos      608573             put try/catch around each stopListening call
 */
package com.ibm.ws.wsfvt.build.tasks.liberty;



import org.apache.tools.ant.BuildException;


/**
 * The StopWAS class is a custom task for stopping WebSphere Application Server.
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class StopLiberty extends ParentTask {
	
	/**
     * This method executes the command to stop WebSphere Application Server.
     * 
     * @throws BuildException
     *             An error encountered while stopping WebSphere
     */
    public void execute() throws BuildException {
		try {				
			libServer.stopServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
        new StopLiberty().execute();
    }
}
