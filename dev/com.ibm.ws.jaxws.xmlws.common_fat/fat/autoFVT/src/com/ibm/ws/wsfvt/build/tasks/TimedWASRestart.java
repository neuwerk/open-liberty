/*
 * @(#) 1.7 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/TimedWASRestart.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/21/08 12:27:32 [8/8/12 06:40:26]
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
 * 01/21/2007  jramos      415986             New File
 * 01/25/2007  jramos      416853             Make static data non-static
 * 05/22/2007  jramos      440922             Changes for Pyxis
 * 10/22/2007  jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 11/05/2007  jramos      480872             Set project in StartWAS and StopWAS tasks
 * 10/30/2008  jramos      559143             Incorporate Simplicity
 * 11/21/2008  jramos      566458             Modify log location
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * This task will restart WAS after a specified time interval
 * 
 * @author jramos
 * 
 */
public class TimedWASRestart extends Task {
    
    private static boolean firstTime = true;

	private String timeLogLoc = AppConst.TIMED_WAS_RESTART_LOG;

	private String timeIntervalString = "";
	private long timeInterval = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {
		
		if(timeIntervalString == null || timeIntervalString.equals(""))
			throw new BuildException("Invalid time interval specified.");
		try {
			this.timeInterval = Long.valueOf(timeIntervalString).longValue();
			System.out.println("TimeInterval: " + timeInterval);
		} catch(NumberFormatException e) {
			e.printStackTrace();
			throw new BuildException(e.getMessage(), e);
		}
        
		// get last restart time if log exists
		String lastRestartString = "";
		long lastRestart = 0;
		File file = new File(timeLogLoc);
		BufferedReader br = null;
		if (file.exists()) {
			try {
				br = new BufferedReader(new FileReader(file));
				lastRestartString = br.readLine();
				if (lastRestartString != null)
					lastRestart = Long.valueOf(lastRestartString).longValue();
				System.out.println("lastRestart: " + lastRestart);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new BuildException(e.getMessage(), e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException(e.getMessage(), e);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new BuildException(e.getMessage(), e);
			} finally {
				try {
					if(br != null)
						br.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// compare current time to last restart
		long timeGap = 0;
		long currentTime = 0;
		Date date = new Date();
		currentTime = date.getTime();
		timeGap = currentTime - lastRestart;
		System.out.println("timeGap: " + timeGap);

        if(firstTime) {
            firstTime = false;
            // make sure the server is up
            StartWAS startWAS = new StartWAS();
            startWAS.setProject(getProject());
            startWAS.setTaskName("StartWAS");
            startWAS.execute();
            writeLog(file);
            return;
        }

		// restart WAS if the specified time interval has passed
		if(timeGap >= this.timeInterval) {
            StopWAS stopWAS = new StopWAS();
            stopWAS.setProject(getProject());
            stopWAS.setTaskName("StopWAS");
            stopWAS.execute();
            StartWAS startWAS = new StartWAS();
            startWAS.setProject(getProject());
            startWAS.setTaskName("StartWAS");
            startWAS.execute();
		}
        
        writeLog(file);
	}
    
    private void writeLog(File file) {
        // write new restart time to log
        PrintStream ps = null;
        try {
            file.delete();
            ps = new PrintStream(new FileOutputStream(file, false));
            Date date = new Date();
            ps.println(date.getTime());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new BuildException(e.getMessage(), e);
        } finally {
            if(ps != null)
                ps.close();
        }
    }

	/**
	 * The amount of time to that should pass since the last restart
	 * before performing another restart
	 * 
	 * @param interval The time interval to wait
	 */
	public void setInterval(String _interval) {
		this.timeIntervalString = _interval;
	}
}
