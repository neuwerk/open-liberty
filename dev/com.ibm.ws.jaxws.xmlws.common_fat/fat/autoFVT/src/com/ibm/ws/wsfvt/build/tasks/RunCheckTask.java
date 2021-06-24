/*
 *  1.2, 6/19/09
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
 * Task that executes subtasks once 
 *   When it (finds the found-string) AND (does not find the notfound string.)
 *   When found or notfound string is not set. Thecondition is considered true.
 *   ** If filename is null or not readable, then the subtasks will be executed not matter what 
 * <ul>
 * <li> found    is null (need not check) or a pattern to be checked in the file 
 * <li> notfound is null (need not check) or a pattern not to be found in the file
 * </ul>
 *  
 * @author gkuo
 *
 */
public class RunCheckTask extends Task implements TaskContainer{

        private String found    = null;
        private String notfound = null;
        private String filename = null;
        private String exist    = null;
        
        private List<Task> tasks = new LinkedList<Task>();
        
        public void setExist(String exist) {
                this.exist = exist;
        }

        public void setFound(String found) {
                this.found = found;
        }

        public void setNotfound(String notfound) {
                this.notfound = notfound;
        }

        public void setFilename(String filename) {
                this.filename = filename;
        }

        public void addTask(Task task) {
                tasks.add(task);
        }

        public void execute() throws BuildException {
                log( "filename='" + filename + "' found='" + found + "' notfound='" + notfound + "' exist='" + exist + "'" );
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
                if( filename == null || filename.length() == 0 )
                {
                    log("No filename found '" + filename + "'" );
                    return true;
                }

                File f = new File(filename);
                if ((!f.exists()) || (!f.canRead())){
                        log("readFileContents " + f.exists() + "/" + f.canRead() );
                        return true;
                }

                if( exist!= null ){
                    if( exist.equalsIgnoreCase( "true" ) ){
                        return f.exists();
                    } else{
                        return !f.exists();
                    }
                }

                boolean bFound         = (found    == null || found.length()    == 0 );
                boolean bNotfound      = (notfound == null || notfound.length() == 0 );
                boolean bStillNotfound = true;
                try {
                    BufferedReader in = new BufferedReader(new FileReader(f));

                    String str = in.readLine();
                    while( str !=  null ){
                        if( ! bFound ){
                            if( str.indexOf( found ) >= 0 ) {
                                log( "Found condition pass. Found '" + found  + "'" );
                                bFound = true;
                            }
                        }
                        if( bStillNotfound && !bNotfound){
                            if( str.indexOf( notfound ) >= 0 ) {
                                log( "Notfound condition failed. Found '" + notfound  + "'" );
                                bStillNotfound = false;
                            }
                        }
                        str = in.readLine();
                    }
                    in.close();
                } catch (IOException e) {
                    log("Error reading " + filename + ": " + e);
                }

                if( bStillNotfound ) bNotfound = true ; // If notfound string is not there, then OK no matter what
                // else                 bNotfound = false; // (this can not be reached)

                return bFound && bNotfound;
        }
        
}
