/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ListTestDirs.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 10/23/07 09:44:31 [8/8/12 06:40:28]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect  Description
 * -----------------------------------------------------------------------------
 * 05/23/2007  jramos      440922          New File
 * 10/23/2007  sedov       476785          Addressed NPE when dir list is null
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * The ListTestDirs task is a custom ANT task for listing the test directories
 * under <FVT_HOME>/src
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 * 
 * @author jramos
 */
public class ListTestDirs extends Task {

	private File outFile = null;

	public void execute() throws BuildException {
		log("enter ListTestDirs");
		try {
			
			String srcDir = (AppConst.FVT_HOME + "/src").replace('\\', '/');
			File[] fileList = (new File(srcDir)).listFiles();
			
			log("Listing directory " + srcDir);
			
			if (outFile == null) {
				outFile = new File(AppConst.FVT_HOME + "/dirList.txt");
			}
			
	        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
	        
	        try {
	        	if (fileList != null && fileList.length > 0) {
	        		Set<String> exclusions = getExclusionSet();
	        		for (File f: fileList){
	        			if (f.isDirectory() && !exclusions.contains(f.getName())){
	        				out.write(f.getName());
	        				out.write("\n");
	        				log(f.getName());
	        			}
	        		}
	        	}
	        } catch (Exception e){
	        	out.close();
	        	throw e;
	        } finally {
	        	out.close();
	        }

	        log("Listing written to " + outFile);
	        
		} catch (Throwable e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

	public void setOutFile(String outFile) {
		this.outFile = new File(outFile);
	}

	private Set<String> getExclusionSet() {
		Set<String> exclude = new HashSet<String>();
		exclude.add("com");
		exclude.add("jacls");
		exclude.add("jython");
		exclude.add("xmls");
		exclude.add("xsls");
		return exclude;
	}

}
