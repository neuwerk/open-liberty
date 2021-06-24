/*
 *  1.1, 2/11/10, autoFVT/src/com/ibm/ws/wsfvt/build/tasks/RunIfTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp.  2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 01/07/09    gkuo        
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

/**
 * Task that executes subtasks  
 *   You can not set both osname or osexcluded. Othewise the behavior is weird 
 *   When it's in the osname platform then run its subTasks
 *   or When the OS is not in the osexcluded then run its subtask.
 * <ul>
 * <li> osname       need to equals to java.getProperty( "os.name" ) 
 * <li> osexcluded   need to excluded the OS which equals to java.getProperty( "os.name" ) 
 * </ul>
 *  
 * @author gkuo
 *
 */
public class RunIfTask extends Task implements TaskContainer{

        String strOsName     = null;
        String strOsExcluded = null;
        
        private List<Task> tasks = new LinkedList<Task>();
        
        public void setOsName(String osName) {
                this.strOsName = osName;
        }

        public void setOsExcluded(String osExcluded) {
                this.strOsExcluded = osExcluded;
        }

        public void addTask(Task task) {
                tasks.add(task);
        }

        public void execute() throws BuildException {
                boolean bDo   = checkConditions();
                if (bDo){
                        // execute all the tasks one by one
                        // if they fail, then a session log is not written
                        log("Executing the block");
                        for (Task t: tasks){
                                t.setOwningTarget(getOwningTarget());
                                t.setProject(getProject());
                                t.perform();
                        }
                        
                } else {
                        log("This block is not executed");
                }
        }
        
        private boolean checkConditions(){
            String strLocalOs = System.getProperty( "os.name" );
                if( strOsName != null ){
                    log( "run-if localOS='" + strLocalOs + "' os='" + strOsName + "'" );
                    return strLocalOs.equalsIgnoreCase(  strOsName );
                }
                if( strOsExcluded != null ){
                    log( "run-if localOS='" + strLocalOs + "' osExcluded='" + strOsExcluded + "'" );
                    return ! strLocalOs.equalsIgnoreCase( strOsExcluded );
                }
                log( "run-if localOS='" + strLocalOs + "' nothing is set in osName and osExcluded" );
                return true; /* run it if nothing is not set*/

        }
        
}
