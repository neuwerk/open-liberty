/*
 * autoFVT/src/com/ibm/ws/wsfvt/build/tasks/EchoTimeTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2004
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 09/17/2007 btiffany                        create
 * 
 * A task to display the current time.  
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 *
 * This class echos the date and time to the console.
 * It takes a parameter named message that it will append to it's output.
 * Unlike the ant Tstamp task, it doesn't set a property that cannot be updated.
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class EchoTimeTask extends Task {
	
	private String _Message = "";
	
	// we will support a parameter named message
	public void setMessage( String s){
		_Message = s;
	}
    
    // not part of ant, just for standalone/debug use.
    public static void main(String[] args){
        new EchoTimeTask().execute();
    }

    public void execute() throws BuildException {
    	// java should have a one-liner for this..
    	
		GregorianCalendar c = new GregorianCalendar();
		c.setTime( new Date(java.lang.System.currentTimeMillis()));
		
		String year = Integer.toString(c.get(Calendar.YEAR)) ;
		
		String month = "00"+Integer.toString(c.get(Calendar.MONTH) +1);
		month = month.substring(month.length() -2, month.length()); 
		
		String day = "00"+Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		day = day.substring(day.length() -2, day.length());
		
		String hour = "00"+ Integer.toString(c.get(Calendar.HOUR_OF_DAY));
		hour = hour.substring(hour.length()-2, hour.length());
		
		String min = "00"  + Integer.toString(c.get(Calendar.MINUTE));
		min = min.substring(min.length()-2, min.length());
		
		String sec = "00" +  Integer.toString(c.get(Calendar.SECOND));
		sec = sec.substring(sec.length() -2, sec.length());

		String now =  year + month + day +" " + hour + min + sec;		
		System.out.println("time: "+ now + "  "+ _Message);
    	
    }
        
 

}
