/*
 * @(#) 1.3 autoFVT/src/annotations/support/LogfileChecker.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/5/07 14:45:09 [8/8/12 06:54:34]
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
 * 05/30/2006  btiffany    LIDB3296.31.01     new file
 */
package annotations.support;
import java.io.*;

/**
 * @author btiffany
 * retrieves a logfile and scans it for strings or prints it.
 * Used to Require STAF - rewritten to use local log files only.
 * 
 * Will only report on contents of log that were added AFTER
 * this object is created
 * 
 * To use, create one of these for each log file you want to watch
 * when your testcase starts.  
 * 
 * When you need to check the log, call
 * check refresh it and scan it for strings.
 * 
 * Or see routinescan if you're checking for recurring error messages
 * 
 * Then if needed call getString to get the new contents of the log for display. 
 *
 */

public class LogfileChecker {
	private boolean lastScanMatched = false;
	private String host;
	private String fqname;
	private String localfqName;
	private String localBaseName;
	private long	initialSize;
	private boolean updateHasOccurred = false;
	private String [] badThings ={
			"exception",
			"error",
			"fatal",
			"E   SRVE"  // WAS error messages
		};
	
	
	/**
	 * constructor.  Pass it fully qualified path to file. 
	 * @param fqname
	 */
	public LogfileChecker(String fqname){
		this.fqname = fqname;	
		String buf, basename;
		int i;
		buf = fqname.replace('\\','/');
		i = buf.lastIndexOf('/');
		basename = buf.substring(i+1);
		this.localBaseName = basename;
		this.localfqName = fqname;
		// now get the size
		initialSize = getFileSize(fqname);
	}
	
	// for debug
	public static void main(String[] args){
		LogfileChecker l = new LogfileChecker("d:/tmp/test.tmp");
		System.out.println(l.check("holy cow"));
		OS.run("echo holy cow >>d:\\tmp\\test.tmp");
		System.out.println(l.check("holy cow"));
		
	}
	
	/**
	 * get last match result, true if matched.
	 */
	public boolean getLastScanResult(){
		return lastScanMatched;
	}
	
	/**
	 * fetch remote file to local machine if it has grown.
	 * @return true if file was fetched.
	 */
	private boolean fetchFile(){
		// bring the entire log file to the local machine
		// but only if it enlarged since we first checked it
		long i = 0;
		boolean result;
		
		if ((i = getFileSize(fqname) )== initialSize){
			System.out.println("file unchanged, nothing to do");
			result = false;
		}
		else{
			i = i - initialSize;
			if (i<0){
				throw new RuntimeException(
						"log file has shrunk! \n "+
						"turn off log rotation on server");
			}
			//System.out.println("file size increased by " +i);

			result = true;
		}
		return result;		
	}
	
	/**
	 *	scans file for 
	 * any of the strings in target that have been appended to file
	 * since this object was created.
	 * 
	 * @returns first matching line if something found, else null;
	 * 
	 * Performs a case-insensitive scan.
	 *  
	 */
	public String check(String[] targets){
		String result = null;
		if(fetchFile()){
		// scan it
			updateHasOccurred = true;
			result =  fileContains(targets, initialSize);
		}
		return result;
	}
	
	/**
	 *	scans file for 
	 * any of the strings in target that have been appended to file
	 * since this object was created.
	 * 
	 * @returns first matching line if something found, else null;
	 * 
	 * Performs a case-insensitive scan.
	 *  
	 */
	public String check(String target){
		String [] s = { target };
		return check(s);
	}
	
	
	
	/**
	 * Customize the default error phrases to be used with routineScan.
	 * If you only need to scan for something once,
	 * consider using updateAndScan instead.
	 * @param phrases
	 */
	public void setErrorPhrases(String[] phrases){
		this.badThings = phrases;
	}
	
	/**
	 * Scans for some common error words.
	 * @throws RunException if keyword found in log.
	 * 
	 * Use setErrorPhrases to update the words to check for.
	 *
	 */
	public void routineScan(){		
		String scanResult;			
		scanResult = check(badThings);
		if(updateHasOccurred){			 
			//logInfo("log file contained:" +getString());
			if (scanResult != null ){
				System.err.println("found log error: "+ scanResult);				
				throw new RuntimeException("error found in log file");			
			}			
		}	
	}
	
	/**
	 * and adds html formatting to file contents 
	 * to dump it to the rft run log or the console.
	 * Does NOT retrieve new copy of file, use getAndScan to cause that. 
	 * @return revised contents, or empty string if none.
	 */	
	public String getString(){
		String result = "";
		if (!updateHasOccurred){ return result;};
		
		if (true){		// decided not to retrieve it again - expensive.
			StringBuffer sb = new StringBuffer();
			sb.append("<tt><pre>\n");
			sb.append("from "+ host + " " + fqname +":<br>");
			
			// print it
			try{
				BufferedReader r = new BufferedReader(
						new FileReader(localfqName));
				r.skip(initialSize);
				while (r.ready()){
					sb.append(r.readLine());
					sb.append("\n<br>");
				}
				r.close();
			}catch(IOException e){
				System.out.println(e.toString());
				throw new RuntimeException("LogFileChecker exception: "+e.toString());
			}	
			sb.append("</tt></pre>\n");
			result = sb.toString();
		}
		return result;		
	}
	
	/**
	 * gets size of a file, expects staf to be on remote host.
	 * @param fqname - fully qualified path and file name
	 * @returns size,  
	 */
	private static long getFileSize(String fqname){		
		File f = new File(fqname);
		return f.length();
	}
	

	/**
	 *  Return line of file if match found, else null
	 *  Start search from start_position bytes into file.
	 * Case insensitive.
	 *  Can be used to search entire file, not just part after initialization.
	 */
	public String fileContains(String[] target, long start_position){
		String buf = null;
		String result = null;
		int i = 0;
		try{
			BufferedReader r = new BufferedReader(
					new FileReader(localfqName));
			r.skip(start_position);
			while (r.ready()){
				buf = r.readLine();
				for(i=0; i<target.length; i++){
					if (buf.toLowerCase().indexOf(target[i].toLowerCase()) > -1){
						result = buf;
						lastScanMatched = true;
						break;
					}
				}	
			}
			r.close();
		}catch(IOException e){
			System.out.println(e.toString());
			throw new RuntimeException("LogFileChecker exception: "+e.toString());
		}		
		return result;
	}
		

}
