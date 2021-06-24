/*
 * @(#) 1.9 autoFVT/src/annotations/support/ImplementationAdapter.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/5/07 14:45:03 [8/8/12 06:54:34]
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
 *
 */
package annotations.support;
import java.io.*;
import java.net.*;
import java.net.URLClassLoader;
import java.lang.reflect.*;

//import org.eclipse.ant.core.AntRunner;

/**
 * An interface to contain the calls to the Jax-WS and IBM implementations.   
 * By calling through this, rather than directly, it might be possible to switch the tests
 * from one implementation to another without rewriting anything.
 * Create this through a call to Support.
 * @author btiffany
 *
 */
public interface ImplementationAdapter {
	static OS os = new OS();			// some os access helpers

	/**
	 * sets working directory in which the output of all javacs, j2w, w2j will go 
	 * defaults to "gen" if never called.
	 * Suggest you set to a different dir. for each test case so generated artifacts aren't overwritten or intermixed.
	 * @param dir
	 */
	public void setWorkingDir(String dir);	
	public String getWorkingDir();
	public void cleanWorkingDir();

	/**
	 * wsdl to java
	 * @param opts
	 * @param wsdlfile - path to file from eclipse root
	 */
	public void w2j(String opts, String wsdlfile);
	
	/**
	 *  java to wsdl.
	 * @param javafile  - path to the file(s) to generate wsdl for, ex: src/x/y/*java.
	 * @param packagePath - the canonical path to the compiled class file, ex x.y.MyService
	 * @param opts - other options 
	 * @return a generated wsdl file in the working directory. Class files are also generated at (workingDir)/package_name
	 * @throws a runtime exception if conversion fails
	 * 
	 */
	public void j2w(String opts, String javafile, String packagePath);
	
	/**
	 *  
	 * @param warfileName - name of war file.  Will be located in workingDir/war ex:foo.war
	 * @param url         - service name, description, and deploy context root. ex: myurl 
	 * 
 	 * @param implClass - the canonical path to the compiled class file, ex com.ibm.Bigclass
 	 * @param seiClass - canonical path to SEI if used, otherwise empty string. 
 	 * @param includeWsdl - set true if wsdl should go in the war file 
     * wsdl files should be in workingdir.
	 * 
	 */
	public void war(String warfileName, String url, String implClass, boolean includeWsdl);
	public void war(String warfileName, String url, 
            String implClass, String seiClass, boolean includeWsdl);
    
    // had to add this for cases where wsdlname <> warfilename
    /**
     *  
     * @param warfileName - name of war file.  Will be located in workingDir/war
     * @param url         - service name, description, and deploy context root. 
     * 
     * @param className - the canonical path to the compiled class file, ex com.ibm.Bigclass
     * @param seiName - canonical path to SEI if used, otherwise empty string. 
     * @param wsdlFile - name of wsdl file to declare in web.xml, or empty string to omit
     * wsdl files should be in workingdir.
     */
    public void war(String warfileName, String url, String className, String seiName, String wsdlFile);
    
    /**
     *  
     * @param warfileName - name of war file.  Will be located in workingDir/war
     * @param earContextRoot - ear context root.
     * @param url         - service name, description, and deploy context root. 
     * 
     * @param className - the canonical path to the compiled class file, ex com.ibm.Bigclass
     * @param seiName - canonical path to SEI if used, otherwise empty string. 
     * @param wsdlFile - name of wsdl file to declare in web.xml, or empty string to omit 
     * wsdl files should be in workingdir.
     */
    public void war(String warfileName, String earContextRoot, 
            String url, String className, String seiName, String wsdlFile);
	
	/**
	 * take a war file, and deploy it to Tomcat/ibm
	 * if we are replacing an existing file on the server, it looks like
	 * you'll need to restart it for it to take. 
	 * 
	 * uses predefined sunDeployDir, edit or write a setter if you need to change it.  
	 */
	public void deploy(String warfileName);
	
	
	/**
	 * invoke a generated java file's main method.
	 * assumes classpath off of workingDir.  Depending on what you are doing,
	 * maybe you should be doing this in Ant.  
	 * @param className - canonical name of class to invoke
	 * @return return code of main method.
	 */
	public int invoke(String className, String args);
	

	/**
	 * compile a file to work with the generated WebClient classes
	 * Depending on what you are doing,
	 * maybe you should be doing this in Ant.
	 * @param sourceFile file to compile, relative to eclipse project root.
	 * @param relative_path_from_workding_dir - where to put the class(es), relative to workingDir
     * @throws RuntimeException if compilation fails
	 */
	public void compile(String sourceFile, String relative_path_from_workding_dir);
	
	/**
	 * invoke wsdl generation tool without compiler first.
	 * assumes classfile and wsdl go in workingdir
	 * @param opts - options
	 * @param packagePath - canonical path to class file
	 */
	public void wsgen(String opts, String packagePath);
	
	/**
	 * returns name of server's stdout console log
	  */
	public String getConsoleOutLogfileName();
	
	/**
	 * returns name of server's stderr console log
	 */
	public String getConsoleErrLogfileName();
	
	/**
	 * returns url of server
	 * @return
	 */
	public String getServerURL();
	
	/**
	 * start server
	 * 
	 */
	public void restart();
	

}


