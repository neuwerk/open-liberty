/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/SessionCheckTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/18/07 15:20:48 [8/8/12 06:57:05]
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
 * 09/11/07    sedov       413702             New File
 * 09/18/07    jramos      467003             Fix catch in writeFileContents
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
 * Task that executes subtasks once per session. A session is new if<br>
 * <ul>
 * <li> sessionId is null (or ${xxxx}) 
 * <li> sessionId log file does not exist
 * <li> sessionId and the logged sessionIds do not match
 * </ul>
 *  
 * @author sedov
 *
 */
public class SessionCheckTask extends Task implements TaskContainer{

	private String sessionId = null;
	private String sessionlog = null;
	
	private List<Task> tasks = new LinkedList<Task>();
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setSessionlog(String sessionlog) {
		this.sessionlog = sessionlog;
	}

	public void execute() throws BuildException {
		if (sessionId != null &&
				sessionId.startsWith("${") && sessionId.endsWith("}")){
			sessionId = null;
		}
		
		String logSessionId = readFileContents(sessionlog);
		log("Current SessionId=" + sessionId + ", stored SessionId=" + logSessionId);
		
		boolean cont = (sessionId == null || logSessionId == null || !sessionId.equals(logSessionId));
		
		if (cont){
			// execute all the tasks one by one
			// if they fail, then a session log is not written
			log("Executing the block");
			for (Task t: tasks){
				t.setOwningTarget(getOwningTarget());
				t.setProject(getProject());
				t.perform();
			}
			
			// write the session id log
			if (sessionId != null){
				log("Logging sessionId " + sessionId + " to " + sessionlog);
				writeFileContents(sessionlog, sessionId);
			}
		} else {
			log("This block was already executed this session");
		}
	}
	
	private String readFileContents(String file){
		
		log("enter readFileContents()");
		
		String str = null;
		
		if (file == null){
			log("readFileContents() file = null");
			return null;
		}
		File f = new File(file);
		
		if (!f.exists() || !f.canRead()){
			log("readFileContents " + f.exists() + "/" + f.canRead());
			return null;
		}
		
	    try {
	        BufferedReader in = new BufferedReader(new FileReader(f));
	        str = in.readLine();
	        in.close();
	    } catch (IOException e) {
	    	log("Error reading " + file + ": " + e);
	    }
	    
		return str;
	}
	
	private void writeFileContents(String file, String contents){
		
		//log("enter writeFileContents()");
		
		BufferedWriter out = null;
		
		if (file == null){
			//log("writeFileContents() file = null");
			return;
		}
		File f = new File(file);
		
		if (f.exists() && !f.canWrite()){
			//log("writeFileContents() can't write");
			return;
		}
		
	    try {
	        out = new BufferedWriter(new FileWriter(f));
	        out.write(contents);

	    } catch (IOException e) {
	    	log("Error writing " + file + ": " + e);
	    } finally {
	    	try {
	    		if (out != null) 
	    			out.close();
			} catch (IOException e) {
			}
	    }
	}

	public void addTask(Task task) {
		tasks.add(task);
	}
}
