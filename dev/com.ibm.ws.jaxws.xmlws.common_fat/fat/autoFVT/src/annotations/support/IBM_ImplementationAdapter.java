/*
 *  @(#) 1.21 autoFVT/src/annotations/support/IBM_ImplementationAdapter.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/16/10 09:27:18 [8/8/12 06:54:47] 
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 06/15/2006  btiffany    LIDB3296.31.01     new file
 * 05/25/2007  jramos      440922             Changes for Pyxis
 * 04/14/2010  btiffany    647521             add osoutput field
 * 06/15/2010  btiffany    654366             stop calling wsimport.sh on z, unsupported.
 * 07/29/2-10  lizet       F1149.1-24048      set endorsed dir  
 * 12/13/2010  btiffany    670291.1           update endorsed path for sun & hp.
 *
 */
package annotations.support;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import common.utils.topology.IAppServer;
import common.utils.topology.Ports;
import common.utils.topology.visitor.QueryDefaultNode;

//import org.eclipse.ant.core.AntRunner;

/**
 * An class to contain the calls to the IBM and other implementations.   
 * By calling through this, rather than directly, it might be possible to switch the tests
 * from one implementation to another without rewriting anything.
 * Create this through a call to Support.
 * @author btiffany
 * 
 * 2007.03.05 - add some ant defines required for compile calls in zos.
 * may still need to convert wsimport and wsgen to ant tasks for z. 
 * 
 * 2007.04.16 - remove .sh extensions on os400, pass codepage to antsupport.xml,
 *              same a z.
 *              
 * 2007.04.23 - unset classpath before calling antsupport, wsimport, wsgen
 * to better handle dirty zos environments.     
 * 2007.04.24 - can't do that, need jstaf.jar.         
 *
 */
public class IBM_ImplementationAdapter implements ImplementationAdapter {
	private String workingDir = "gen";	// where we'll do our war, j2w, w2j, etc.
	private String absoluteWorkingDir = null; 
	static OS os = new OS();			// some os access helpers
	
	// parms for war and deploy
	static String serverURL=null;
	static String DeployDir = Support.getFvtBaseDir()+"/classes/lib";
	
	static String WASHome =  System.getenv("WAS_TOP");
	static String ANTHome = System.getenv("ANT_HOME");
	static String FVT_base_dir = System.getenv("FVT_TOP")+"/autoFVT";
	
	// param for getting console logfiles
	// we have to read profile.dir from src/xmls/properties.xml
	static String WASProfileDir = null;
	
	static String consoleOutLogfile= null;
	static String consoleErrLogfile= null;
	static String serverName = null;
	static String defaultHost = null;
	static String defaultPort = null;
	static boolean static_init_done = false;
    static String antPrefix = "";
    // Liberty wsgen and wsimport does not have file extension for unix/linux platforms
    static String scriptExtension = "";
    static boolean isos400 = false;
    static boolean iszos = false;
    static boolean iswindows = false;
    String osoutput = null;    // store last output of os.runs
	
	// things to scan wsgen tooling output for
	String[] badThings = {"Exception", "Problem encountered", "error:"};
	
	
	ImplementationAdapter adapterDelegate = null;
	
	public static void main( String args [] ){
		// just for debugging
		IBM_ImplementationAdapter i = new IBM_ImplementationAdapter(".");
		System.out.println(i.consoleErrLogfile);
		i.w2j("", args[0] );
	}
	
	/**
	 * 	read profiles.xml.  That's expensive, so only do it once. 
	 *
	 */
	void static_init(){
		if( static_init_done) return;
		checkValid("WAS_TOP", WASHome);
		checkValid("ANT_HOME", ANTHome);
		checkValid("FVT_TOP", FVT_base_dir);
        IAppServer server = QueryDefaultNode.defaultAppServer;
        WASProfileDir = server.getNodeContainer().getProfileDir();
		//WASProfileDir = getAntProperty("profile.dir", FVT_base_dir+"/src/xmls/properties.xml");
		// WASProfileDir = WASProfileDir.replace("${WAS.base.dir}", WASHome);
		checkValid("profileDir", WASProfileDir);
        serverName=  server.getServerName();
		//serverName = getAntProperty("server.name", FVT_base_dir+"/src/xmls/properties.xml");
		checkValid("serverName", serverName);
		consoleOutLogfile = OS.fixPsep(WASProfileDir+"/logs/"+serverName+"/SystemOut.log");
		consoleErrLogfile = OS.fixPsep(WASProfileDir+"/logs/"+serverName+"/SystemErr.log");
        
        // use this instead 

        defaultHost = "http://" + server.getMachine().getHostname();
        defaultPort =  server.getPortMap().get(Ports.WC_defaulthost).toString();
        
		//defaultHost = getAntProperty("new.default.host", FVT_base_dir+"/src/xmls/properties.xml");
		//defaultPort = getAntProperty("new.wc.defaulthost", FVT_base_dir+"/src/xmls/properties.xml");
		checkValid("ant host", defaultHost);
		checkValid("ant port", defaultPort);
		serverURL="http://"+defaultHost+":"+defaultPort;
        
 
        
        if (System.getProperty("os.name").toString().contains("OS/400")) isos400=true;
        if (System.getProperty("os.name").toString().contains("z/OS")) iszos=true;
        if (System.getProperty("os.name").toString().contains("indow")) iswindows=true;
        
        
        if(iszos ){
            //antPrefix = "unset CLASSPATH && export CLASSPATH && export ANT_OPTS=\"-Dfile.encoding=ISO8859-1 -Xnoargsconversion\" && ";
            antPrefix = "export ANT_OPTS=\"-Dfile.encoding=ISO8859-1 -Xnoargsconversion\" && ";
        }       
        if (isos400){
            scriptExtension="";
        }
        if (iswindows){
            scriptExtension=".bat";
        }
		static_init_done = true;
	}
	
	
	/**
	 * constructor.
	 * @param workingDir - for best results, an absolute path.
	 */
	public IBM_ImplementationAdapter(String workingDir){	
		static_init();		// set static vars, first time only
		setWorkingDir(workingDir);
		
	}
	
	String getAntProperty(String propName, String Filename){
		String searchResult = null;
		String search = "name=\""+propName+"\"";
		String buf = null;
		try{
			BufferedReader br = new BufferedReader(new FileReader(Filename));
			while(br.ready()){
				buf = br.readLine();
				if (buf.indexOf(search) > -1) break;			
			}
			//System.out.println(sb.toString());
			br.close();
		}
		catch (FileNotFoundException e ){
			e.printStackTrace();
			return null;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}	
		
		int i = buf.indexOf("value=\"");
		
		if(i == -1) return null;
		int j = buf.indexOf("\"", i+7);
		searchResult = buf.substring(i+7, j );	
		return searchResult;
	}
	
	void checkValid(String name, String var){
		if (var==null || var.length() == 0){
			throw new RuntimeException("environment variable "+name+" has improper value: "+var);
		}
	}

	/**
	 * sets working directory in which the output of j2w, w2j will go 
	 * defaults to "gen" if never called.
	 * Suggest you set to a different dir. for each test case so generated artifacts aren't overwritten or intermixed.
	 * @param dir
	 */
	public void setWorkingDir(String dir){
		if (dir==null || dir.length()==0 ) throw new RuntimeException("invalid working directory: "+dir);
		
		// remove any .. mess
		try{ 
			dir = new File(dir).getCanonicalPath();
		} catch( IOException e) {
			e.printStackTrace();
		}
		workingDir = dir;
		os.mkdir(workingDir);		
		absoluteWorkingDir = new File(workingDir).getAbsoluteFile().toString();
	}
		
	
	public String getWorkingDir(){
		return workingDir;
	}
	public void cleanWorkingDir(){
		String cmd=null;
		System.out.println("cleaning");
				
		// safety
		String s = OS.fixPsep(Support.getFvtBaseDir());
		if(!(OS.fixPsep(workingDir)).contains(s)){
			System.out.println("fvt base dir="+s);
			System.out.println("workingdir="+workingDir);
			System.out.println("workingdir does not contain fvt base dir");
			throw new RuntimeException("can't delete outside FVT tree");
		}
		if(System.getProperty("os.name").toString().contains("Windows")){
			cmd = "del /s /q "+workingDir.replace('/','\\')+"\\* >nul" ;
		} else {
			cmd = "rm -rf "+workingDir+"/*";
		}
			
		System.out.println(os.runs(cmd));
	}

	/**
	 * wsdl to java
	 * @param opts
	 * @param wsdlfile - path to file from eclipse root
	 */
	public void w2j(String opts, String wsdlfile){
        // wsimport
        // cmdprefix tries to fix rotten classpath on some z sessions
        String cmdprefix="";
       // if (iszos) cmdprefix = "export CLASSPATH=. && ";
        //String wsi = WASHome+"/bin/wsimport"+ scriptExtension;   
        String wsi = "";
        try {
        	wsi = computeWsimportName();
        } catch (Exception e) {
        	e.printStackTrace();
        	return;
        }
        // Liberty has wsimport command on z/OS as other platforms, so no need to set this anymore
        // if (iszos) wsi = System.getProperty("java.home")+"/bin/wsimport";   // 6.14.2010 wsimport.sh no longer works on z. 
        String chmodString = getChmodString(wsi);
        String cmd= chmodString + cmdprefix + OS.fixPsep(wsi) + " -target 2.2 -keep  -d "+workingDir + " "+opts+" "+wsdlfile;
        String output = os.runs(cmd);
        int rc = os.getLastRC();    
        // silly thing returns zero even if fails, have to check output
        for(String s: badThings){
            if( rc != 0 | output.indexOf(s) > -1 ){
                System.out.println(output);
                throw new RuntimeException("wsimport failed");
            }           
        }   
        System.out.println(output);
        return ;
    }
    
    public boolean isWindows(){
        return java.lang.System.getProperty("os.name").contains("indows");
    }
	
	/**
	 *  java to wsdl.
	 *  
	 *  temporary until ibm's tooling in better shape
	 *  
	 * @param javafile  - the file(s) to generate wsdl for
	 * @param packagePath - the canonical path to the compiled class file, ex com.ibm.Bigclass
	 * @param opts - other options 
	 * @return a generated wsdl file in the working directory. Class files are also generated at (workingDir)/package_name
	 * @throws a runtime exception if conversion fails
	 * 
	 */
	public void j2w(String opts, String javafile, String packagePath){

		// use packagePath to get path to compiled classes
        if (packagePath.contains(".")){
    		String p = packagePath.substring(0,packagePath.lastIndexOf('.'));
    		p = p.replace('.','/');
        }
		
		// wsgen,
		// apt has a little problem - needs classpath to generated classes up front.
		// so avoid it for now.
		// compile puts classes in workingDir by default...
		String sourceFile = fixPsep(javafile);		
		String outdir = workingDir;
		// break pathed sourcefile into path and name for ant, sigh. 
		String srcdir = null;
		String basename = null; 
		String sep = System.getProperty("file.separator");
		int pos = sourceFile.lastIndexOf(sep);
		srcdir=sourceFile.substring(0, pos);
		basename=sourceFile.substring(pos+1);
		
		
		//if (targetDir==null || targetDir.length()!=0 ) outdir = workingDir+"/"+targetDir+ " ";
		String antcmd = getAntChmodString() + antPrefix+ "ant  -Dworkdir="+getWorkingDir()+
		" -Dbasedir="+FVT_base_dir+
		" -Dsrcdir="+srcdir+
		" -Dsrcfile="+basename+
		" -DtargetDir="+outdir+
		" -f  "+FVT_base_dir+"/src/annotations/support/antSupport.xml"+
		" compile";
		//System.out.println(antcmd);
		String buf = OS.runs(antcmd);		
		System.out.println(buf);
		if(buf.contains("FAILED")) throw new RuntimeException("ant compile task failed");	
		
		
		/*
		String cmd="javac -d "+workingDir+ " " +fixPsep(javafile);
		String output = os.runs(cmd);
		int rc = os.getLastRC();		
		if( rc != 0 ) {
			System.err.println(output);
			throw new RuntimeException("javac failure");
		}
		*/
		wsgen(opts, packagePath);
		return;	
	}
	
	/**
	 * invoke wsdl generation tool without compiler first.
	 * assumes classfile and wsdl go in workingdir
	 * @param opts - options
	 * @param packagePath - canonical path to class file
	 */
	public void wsgen(String opts, String packagePath){
        //String wsgenname = WASHome+"/bin/wsgen"+ scriptExtension;
        //String wsgenname = "D:/t/jdk/IBMJava6/bin/wsgen.exe";
		String wsgenname = "";
		try {
			wsgenname = computeWsgenName();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
        String psep = isWindows() ? ";": ":";
		String cp = "."+ psep + workingDir;
		String chmodString = getChmodString(wsgenname);
        String cmdprefix="";
        //if (iszos) cmdprefix = "export CLASSPATH=. && ";

        
		String cmd = chmodString + cmdprefix + OS.fixPsep(wsgenname) + " -target 2.2 -cp "+ cp+ " -wsdl -keep  -d "+workingDir +" " +opts+ " "+ packagePath;		
		String output = os.runs(cmd);
		int rc = os.getLastRC();
//		 silly thing returns zero even if fails, have to check output
		for(String s: badThings){
			if( rc != 0 | output.indexOf(s) > -1 ){
				System.out.println(output);				
				throw new RuntimeException("wsgen failure");
			}
		}
		System.out.println(output);				
		
	}
	
	private String getAntChmodString() {
		String antChmodStr = "";				
		if (!isWindows()) {
			String antHome = System.getProperty("ant.home");
			if (null != antHome && !"".equals(antHome)) {
				if (new File(antHome + "/bin/ant").exists()) {
					antChmodStr = "chmod +x " + antHome + "/bin/ant" + " && ";
					System.out.println("antChmodString=" + antChmodStr);
				}
			} else {
				System.out.println("No antChmodString set!");
			}
		}
		return antChmodStr;
	}
	
	private String getChmodString(String cmdName) {
		String chmodstr = "";
		if (!isWindows()) chmodstr = "chmod +x " + cmdName + " && ";
		return chmodstr;
	}
	
	private String computeWsgenName() throws java.io.FileNotFoundException {
		String wsgenName = WASHome+"/bin/jaxws/wsgen"+ scriptExtension;
		if (new File(wsgenName).exists()) {
			System.out.println("Use application server's wsgen command:" + wsgenName);
			return OS.fixPsep(wsgenName);
		} else {
			String cmdSuffixbyPlatform = this.isWindows() ? ".exe" : "";
			wsgenName = System.getProperty("java.home") + "/../bin/wsgen" + cmdSuffixbyPlatform;
			if(new File(wsgenName).exists()) {
				System.out.println("Use JDK's wsgen command:" + wsgenName);
				return OS.fixPsep("\"" + wsgenName + "\"");
			} else {
				throw new java.io.FileNotFoundException("Cannot find valid wsgen command to use");
			}
		}
		
	}
	
	private String computeWsimportName() throws java.io.FileNotFoundException {
		String wsimportName = WASHome+"/bin/jaxws/wsimport"+ scriptExtension;
		if (new File(wsimportName).exists()) { 
			System.out.println("Use application server's wsimport command:" + wsimportName);
			return wsimportName;
		} else {
			String cmdSuffixbyPlatform = this.isWindows() ? ".exe" : "";
			wsimportName = System.getProperty("java.home") + "/../bin/wsimport" + cmdSuffixbyPlatform;
			if(new File(wsimportName).exists()) {
				System.out.println("Use JDK's wsimport command:" + wsimportName);
				return wsimportName;
			} else {
				throw new java.io.FileNotFoundException("Cannot find valid wsimport command to use");
			}
		}
		
	}
	
	/**
	 *  
	 * @param warfileName - name of war file.  Will be located in workingDir/war
	 * @param url         - service name, description, and deploy context root. 
	 * 
 	 * @param className - the canonical path to the compiled class file, ex com.ibm.Bigclass
 	 * @param seiName - canonical path to SEI if used, otherwise empty string. 
 	 * @param includeWsdl - set true if wsdl should go in the war file.  Name of
     * wsdl will have same base name as that of war file. 
     * @param wsdlFile - name of wsdl file to include 
	 * 
	 */
	public void war(String warfileName, String url, String implClassName, boolean includeWsdl){
        
        String wsdlName = "";
        if (includeWsdl) wsdlName = warfileName.substring(0, warfileName.length() -4) + ".wsdl";
		war(warfileName, url, implClassName, "", wsdlName);		
	}
    
    public void war(String warfileName, String url, String className, String seiName, boolean includeWsdl){
        String wsdlName = "";
        if (includeWsdl) wsdlName = warfileName.substring(0, warfileName.length() -4) + ".wsdl";
        war (warfileName, url, className, seiName, wsdlName);
    }
    
    @Deprecated() // sei class isn't needed in dd, as long as it's packaged.
    public void war(String warfileName,  
            String url, String className, String seiName, String wsdlFile){
        war(warfileName, url, url, className, seiName, wsdlFile);        
    }
    
    /**
     *  
     * @param warfileName - name of war file.  base of this is used for service name and description.
     * @param earContextRoot - url to ear context root . 
     * @param url         - url of web.xml.  
     * 
     * @param className - the canonical path to the compiled class file, ex com.ibm.Bigclass
     * @param seiName - canonical path to SEI if used, otherwise empty string. 
     * @param wsdlFile - name of wsdl file to declare in web.xml, or empty string to omit 
     * 
     */
    @Deprecated() // sei class isn't needed in dd, as long as it's packaged.
	public void war(String warfileName, String earContextRoot, 
                      String url, String className, String seiName, String wsdlFile){
        
		String shortName = warfileName.substring(0, warfileName.length() -4);
		// generate the 3 xml files here, plus Sun file for compatibility.
		createEarXmlFiles(shortName, earContextRoot, url, className, seiName, wsdlFile);
		String wsdlFlag="";
		if (wsdlFile.length()>0 ){
			wsdlFlag =" -DincludeWsdl=true";			
		}
		String antcmd = getAntChmodString() + antPrefix + "ant  -Dworkdir="+getWorkingDir()+
						" -Dearname="+shortName+
						wsdlFlag+
						" -f  "+FVT_base_dir+"/src/annotations/support/antSupport.xml"+
						" createEar";
		//System.out.println(antcmd);
		String buf = OS.runs(antcmd);		
		System.out.println(buf);
		if(buf.contains("FAILED")) throw new RuntimeException("ant war task failed");
		
	}
	
    
	void createEarXmlFiles(String shortName, String earContextRoot, 
              String url, String className, String seiName, String wsdlFile){
		
        boolean includeWsdl = false;
        if(wsdlFile.length()>0 ) includeWsdl=true;
        
		// web.xml
		StringBuffer wx = new StringBuffer();
		wx.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");		
		wx.append(" <web-app id=\"WebApp_@name\"\n");
		wx.append("   xmlns=\"http://java.sun.com/xml/ns/j2ee\"\n");
		wx.append("   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		wx.append("   xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\"\n");
		wx.append("   version=\"2.4\">\n\n");
		wx.append("   <display-name>@name</display-name>\n");
		wx.append("   <servlet id=\"@name\">\n");
		wx.append("         <servlet-name>@className</servlet-name>\n");
		wx.append("         <servlet-class>@className</servlet-class>\n");
		wx.append("         <load-on-startup>1</load-on-startup>\n");
		wx.append("   </servlet>\n");
		wx.append("   <servlet-mapping>\n");
		wx.append("         <servlet-name>@className</servlet-name>\n");
		wx.append("         <url-pattern>/@urlcontext</url-pattern>\n");
		wx.append("   </servlet-mapping>\n");
		wx.append("</web-app> \n");

		String wxs = wx.toString().replaceAll("@name",shortName);
        
		wxs = wxs.replaceAll("@urlcontext", url);
        // this is where it differs from sun.
        wxs = wxs.replaceAll("@className", className);   
		try{
            /* - pre Zos 
			BufferedWriter b = new BufferedWriter(new FileWriter(workingDir+"/web.xml"));
			b.write(wxs);
			b.close();
            */
            OutputStreamWriter ow = new OutputStreamWriter( new BufferedOutputStream(
                    new FileOutputStream(new File(workingDir+"/web.xml"))),
                    "ISO-8859-1");
            ow.write(wxs);
            ow.close();
            
		} catch (IOException e){
			e.printStackTrace();
			throw new RuntimeException("i/o error");			
		}
		
		
        if(false){
    		// now ibmservices.xml		
    		wx.setLength(0);
    		// insert template
    		wx.append("<serviceGroup>\n");
    		wx.append(" <service name=\"@name\">\n");
    		wx.append("  <parameter locked=\"false\" name=\"ServiceClass\">@implclass</parameter>\n");
            if(includeWsdl){
                wx.append("  <parameter locked=\"false\" name=\"WsdlLocation\">WEB-INF/wsdl/@wsdlname</parameter>\n");
            }    
    		wx.append(" </service>\n");
    		wx.append("</serviceGroup>\n");
    		wxs = wx.toString().replaceAll("@name",shortName);
    		if(includeWsdl) wxs = wxs.replaceAll("@wsdlname", wsdlFile);
    		wxs = wxs.replaceAll("@implclass", className);
    		wxs = wxs.replaceAll("@seiname", seiName);
    		
    		try{
                
    			BufferedWriter b = new BufferedWriter(new FileWriter(workingDir+"/ibmservices.xml"));
    			b.write(wxs);
    			b.close();
                
    		} catch (IOException e){
    			e.printStackTrace();
    			throw new RuntimeException("i/o error");			
    		}
        }    
		
		// now application.xml	for the ear file	
		wx.setLength(0);
		// insert template
		wx.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		wx.append("<application xmlns=\"http://java.sun.com/xml/ns/j2ee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" id=\"Application_ID\" version=\"1.4\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/application_1_4.xsd\">\n");
		wx.append("  <display-name>@shortname</display-name>\n");
		wx.append("  <module id=\"WebModule_1\">\n");
		wx.append("     <web>\n");
		wx.append("        <web-uri>@warfilename</web-uri>\n");
		wx.append("        <context-root>/@urlcontext</context-root>\n");
		wx.append("     </web>\n");
		wx.append("  </module>\n");
		wx.append("</application>\n");
		wxs = wx.toString().replaceAll("@warfilename",shortName+".war");
		wxs = wxs.replaceAll("@urlcontext", earContextRoot);
		wxs = wxs.replaceAll("@shortname", shortName);		
		
		try{
            /* pre Zos 
			BufferedWriter b = new BufferedWriter(new FileWriter(workingDir+"/application.xml"));
			b.write(wxs);
			b.close();
            */
            OutputStreamWriter ow = new OutputStreamWriter( new BufferedOutputStream(
                    new FileOutputStream(new File(workingDir+"/application.xml"))),
                    "ISO-8859-1");
            ow.write(wxs);
            ow.close();
		} catch (IOException e){
			e.printStackTrace();
			throw new RuntimeException("i/o error");			
		}		
        
        /*
        // now sun-jaxws.xml
        wx.setLength(0);
        
        wx.append("<endpoints xmlns='http://java.sun.com/xml/ns/jax-ws/ri/runtime' version='2.0'> \n");
        wx.append("    <endpoint                                                                  \n");
        wx.append("        name='@name'                                                           \n");
        wx.append("        implementation='@implclass'                                            \n");
        if(seiName.length() >0 ){
            wx.append("        interface='@seiname' \n");
        }
        wx.append("        url-pattern='/@urlcontext'                                           \n");
        if(includeWsdl){
            wx.append("         wsdl='WEB-INF/wsdl/"+wsdlFile+"'\n");
        }
        wx.append( "                                    />\n");
        wx.append("</endpoints>                                                                   \n");
        
        wxs = wx.toString().replaceAll("@name",shortName);
        wxs = wxs.replaceAll("@urlcontext", url);
        wxs = wxs.replaceAll("@implclass", className);
        wxs = wxs.replaceAll("@seiname", seiName);
        
        try{
            BufferedWriter b = new BufferedWriter(new FileWriter(workingDir+"/sun-jaxws.xml"));
            b.write(wxs);
            b.close();
        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("i/o error");            
        }       

        */

	}
	
	/**
	 * take a war file, and deploy it to Tomcat/ibm
	 * here we do nothing, the war task takes care of it. 
	 */
	public void deploy(String warfileName){
			System.out.println ("deploy "+warfileName+" is called, does nothing");
		return;			
	}
	
	
        /**
         * invoke a generated java file's main method.
         * assumes classpath off of workingDir.  In eclipse, this picks up the 
         * system classpath of the eclipse session.  In ant, it picks up whatever
         * classpath is in use when this method gets called. 
         * @param className - canonical name of class to invoke
         * @return true if main exited with return code of zero.
         */
        public int invoke(String className, String args) {
            // plant the classpath in the new jvm
            // put it in quotes to deal with things like "program files"
        	String jvmargs=" -Dcom.ibm.websphere.thinclient=true ";
        	String cp = null;
        	if(isWindows())
        	{
        	  String classPath = fixPsep(workingDir)+System.getProperty("path.separator")+System.getProperty("java.class.path");
        	  String[] classPathArray = classPath.split(";");
        	  List classPathList = Arrays.asList(classPathArray);
        	  try{
            	  File f=createJar(classPathList,className);
            	  String jarPath = f.getAbsolutePath();
//        		  String jarPath = createJar(classPath,className).getAbsolutePath();
                  cp = "-jar "+jarPath;
//           		  cp = "-cp ."+System.getProperty("path.separator")+"\""+
//                         System.getProperty("java.class.path") + "\""; 
              }
              catch (IOException e){ 
      			e.printStackTrace();
    			throw new RuntimeException("i/o error");	
              }

        	}
        	else{
        		 cp = "-cp ."+System.getProperty("path.separator")+"\""+
                      System.getProperty("java.class.path") + "\""+" "+ className;
        		
        	}
//   		 cp = "-cp ."+System.getProperty("path.separator")+"\""+
//                 System.getProperty("java.class.path") + "\""+" "+ className;
            /*String endorsed = " -Djava.endorsed.dirs="+ WASHome.replace("\\","/")+ "/runtimes"+ "/endorsed"
              + System.getProperty("path.separator")
              + WASHome.replace("\\","/")+ "/java/jre/lib/endorsed "; */
            String endorsed = " -Djava.endorsed.dirs="+ FVT_base_dir.replace("\\", "/")+"/common/jars/endorsed ";
            String output= os.runs("cd "+fixPsep(workingDir)+" && java " +endorsed + jvmargs +" "+cp+" "+args);
            osoutput = output; 
            System.out.println(output);
            int rc = os.getLastRC();
      
            System.out.println("return code: "+rc);
            return rc;
      
        }
        
 	   /**
         * Create a jar with just a manifest containing a Main-Class entry 
         *
         * @param   classPath  
         * @return
         * @throws  IOException
         */

        private File createJar(List classPath,
                String mainClass) throws IOException {
        	File file = File.createTempFile("ManifestOnly", ".jar");
        	file.deleteOnExit();
        	FileOutputStream fos = new FileOutputStream(file);
        	JarOutputStream jos = new JarOutputStream(fos);
        	jos.setLevel(JarOutputStream.STORED);
        	JarEntry je = new JarEntry("META-INF/MANIFEST.MF");
        	jos.putNextEntry(je);

        	Manifest man = new Manifest();

        	String cp = "";
        	for (Iterator it = classPath.iterator(); it.hasNext();) {
        		String el = (String) it.next();
        		cp += new File(el).toURI().toURL()+" ";
        	}

        	man.getMainAttributes().putValue("Manifest-Version", "1.0");
        	man.getMainAttributes().putValue("Class-Path", cp.trim());
        	man.getMainAttributes().putValue("Main-Class", mainClass);

        	man.write(jos);
        	jos.close();

        	return file;
        }
        
	private String fixPsep(String cmd){		
		if(System.getProperty("os.name").toString().contains("Windows")){
			return cmd.replace('/','\\');			
		} else{
			return cmd.replace('\\','/');
		}		
	}
	

	/**
	 * compile a file to work with the generated WebClient classes
	 * Depending on what you are doing,
	 * Couldn't be compiled earlier because the wsdl and generated classes weren't around.
	 * @param sourceFile file to compile, relative to eclipse project root.
	 * @param targetDir - where to put the class(es), relative to workingDir
	 */
	public void compile(String sourceFile, String targetDir){
		sourceFile = fixPsep(sourceFile);		
		String outdir = workingDir;
		// break pathed sourcefile into path and name for ant, sigh. 
		String srcdir = null;
		String basename = null; 
		String sep = System.getProperty("file.separator");
		int pos = sourceFile.lastIndexOf(sep);
		srcdir=sourceFile.substring(0, pos);
		basename=sourceFile.substring(pos+1);
        
        		
		if (targetDir==null || targetDir.length()!=0 ) outdir = workingDir+"/"+targetDir+ " ";
		String antcmd = getAntChmodString() + antPrefix +"ant  " +
		" -Dsrcfile="+basename+
		" -Dsrcdir="+srcdir+
		" -DtargetDir="+outdir+
		" -Dworkdir="+FVT_base_dir+		
		" -Dbasedir="+FVT_base_dir+
		" -f  "+FVT_base_dir+"/src/annotations/support/antSupport.xml"+
		" compile";
		//System.out.println(antcmd);
		String buf = OS.runs(antcmd);		
		System.out.println(buf);
		if(buf.contains("FAILED")) throw new RuntimeException("ant compile task failed");		
	}
	
	/**
	 * returns name of server's stdout console log
	  */
	public String getConsoleOutLogfileName(){		 
		return consoleOutLogfile;	
	}
	
	/**
	 * returns name of server's stderr console log
	 */
	public String getConsoleErrLogfileName(){
		return consoleErrLogfile;		
	}
	
	/**
	 * returns url of server
	 * @return
	 */
	public String getServerURL(){
		return serverURL;		
	}
	
	/**
	 * start server
	 * 
	 */
	public void restart(){
		System.out.println("restart called, does nothing");		
	}
	

}


