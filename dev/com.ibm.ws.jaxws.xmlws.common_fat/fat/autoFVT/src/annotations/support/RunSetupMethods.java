package annotations.support;


import javax.jws.*;

import junit.framework.TestCase;
import java.io.*;
import java.util.*;
import junit.textui.TestRunner;
import junit.framework.*;
import java.lang.reflect.*;
import java.lang.reflect.AccessibleObject;



/**
 * Run each testcase's setUp method using reflection.
 * This allows us to write tests that run self-contained under junit,
 * then do the setup portion in ant to be consistent with the FVT harness. 
 * Requires each TestCase method to have a no-args constructor.
 * @author btiffany
 *
 */
public class RunSetupMethods  {
	int x=1;
	ArrayList<File> allFiles = new ArrayList();	
	
	
	public static void main(String [] args){		
		
		// haven't found a way to search for classes directly, so use the file system.
		ArrayList<String> a = new ArrayList();		
		String buf = null;
		RunSetupMethods rsm = new RunSetupMethods();
		// in Ant, we will be running from build/classes, and everything should be under there.
		String searchpath = Support.getFvtBaseDir()+"/build/classes/annotations";
		if (args.length >0 ) searchpath = args[0];
		System.out.println("RunSetupMethods starting with search path: "+searchpath); 
		java.io.File f = new File(searchpath);
		// recurse down the file tree
		rsm.addFiles(f);
		
		// now dig through the list of all files and pull out only the
		// *TestCase.class files
		System.out.println("will run setUp() in these classes:");
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
		
		// run it		
		int i=0; String s=null; Class c = null;		 
		ClassLoader cl = java.lang.ClassLoader.getSystemClassLoader();
		for (; i< a.size();  i++){
			try{
				s=a.get(i);
				System.out.println("invoking setUp() on "+s);

                                try {
                                    c = cl.loadClass(s);
                                } catch (ClassNotFoundException e) {
                                    c = Class.forName(s, false, cl);
                                }

				// create a new instance, cast it to a TestCase, and invoke it's setup() method.
				Method m = c.getMethod("setUp", (Class[] )null );
				Object t =  c.newInstance();
				m.invoke(t, (java.lang.Object [])null );
				
				/* if we wanted to invoke main, we'd do it like this:
				 	Class ca = String[].class;
					m = cls.getMethod("main",  ca );
					//t =  cls.newInstance();  // don't need 'cause it's static				
					String[] args = {"something"};
					m.invoke(null, new Object[] {args} );
					
			    */		
			} catch (Exception e){
				e.printStackTrace();
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
