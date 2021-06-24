package annotations.support;


import javax.jws.*;

import junit.framework.TestCase;
import java.io.*;
import java.util.*;

import junit.textui.TestRunner;
import junit.framework.*;



/**
 * Run all the tests in src/annotations  and summarize results.
 * For use within eclipse. 
 * @author btiffany
 *
 */
public class RunEverything extends com.ibm.ws.wsfvt.test.framework.FVTTestCase {
        int x=1;
        static ArrayList<File> allFiles = new ArrayList();
        
        public static void main(String [] args){
        	RunEverything rsm = new RunEverything();
        	String buf=null;
        	ArrayList<String> a = new ArrayList();
    		// in Ant, we will be running from build/classes, and everything should be under there.
    		String searchpath = Support.getFvtBaseDir()+"/build/classes/annotations"; 
    		java.io.File f = new File(searchpath);
    		// recurse down the file tree
    		rsm.addFiles(f);
    		
    		// now dig through the list of all files and pull out only the
    		// *TestCase.class files
    		System.out.println("will run these classes:");
    		String sbuf = null;
    		String psep = System.getProperty("file.separator");
    		for (File f2: rsm.allFiles){	
    			try{
    				sbuf = f2.getCanonicalPath();
    				if (sbuf.endsWith("TestCase.class")){
    					// make the canonical path to the class file				
    					buf = sbuf.substring(sbuf.indexOf("annotations"));
    					buf = buf.replace(psep,".").substring(0,buf.length() -6);
    					System.out.println(buf);
    					a.add(buf);						
    				}
    			} catch( IOException e){
    				e.printStackTrace();
    			}
    		}

        // run all the tests in all the classes
        int i=0; String s=null; Class c = null;
		ArrayList<TestResult> tr = new ArrayList<TestResult>();
		ClassLoader cl = java.lang.ClassLoader.getSystemClassLoader();
		for (; i< a.size();  i++){
			try{
				s=a.get(i);
				System.out.println("loading "+s);

                                try {
                                    c = cl.loadClass(s);
                                } catch (ClassNotFoundException e) {
                                    c = Class.forName(s, false, cl);
                                }

				TestSuite ts = (TestSuite)(c.getMethod("suite", (Class[])null)).invoke(null);
				TestRunner t = new TestRunner();
				// run the tests and add the result object to the collection
				tr.add(t.doRun(ts, false));
			} catch( Exception e){
				e.printStackTrace();
			}			
		}
		
		// iterate over all the TestResults and print out a summary
		i=0;
		int r, e, x = 0;		
		for (; i< tr.size();  i++){
			
			r = tr.get(i).runCount();			
			e = tr.get(i).errorCount();
			x = tr.get(i).failureCount();
			buf = tr.get(i).getClass().getName();
			System.out.println(a.get(i)+ " run:"+r+" fail:"+x+" error:"+e);
			if (x>0 ){
				Enumeration<TestFailure> et = tr.get(i).failures();
				while(et.hasMoreElements()){
					TestFailure tf = et.nextElement();
					System.out.println("    failure:"+tf.toString());
					
				}
			}
		}
		

	}
    
	/**
	 * recursively add files to list 
	 * @param Filef  - a starting directory.
	 * expects ArrayList<File> allFiles to be static in the class
	 */
	void addFiles(File f){
		//System.out.println(f.toString());
		if (f.isDirectory()){
			File [] ff = f.listFiles();
			for (File f2: ff){
				addFiles(f2);	// recursion			
			}
		}	else {
			//System.out.println("adding "+f.toString());
			allFiles.add(f);
		}
	}
	
	
}
