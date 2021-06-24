/*
 * @(#) 1.3.1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tools/AntProperties.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/16/10 09:27:28 [8/8/12 06:54:59]
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 7.25.2006   btiffany    LIDB3296.31.01     new file
 * 05/24/2007  jramos      440922             Changes for Pyxis
 * 09/06/2007  ulbricht    439706             More change for Pyxis
 * 12/14/2010  btiffany    670292.1           handle jvm properties.
 *
 */
package com.ibm.ws.wsfvt.build.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * makes properties from .../websvcs.fvt/instance.xml and websvcs.fvt/src/xmls/properties.xml
 * available at runtime.
 * 
 * Does not use an xml parser, so won't work if files aren't defined one property per line.
 
 * 
 * Some frequently desired variables have explicit getters, the rest can be obtained from
 * calling getAntPropety(String).  call listProps to just list everything.
 * 
 *  TODO: use a real xml parser
 *	@author btiffany
 */
public class AntProperties {
	static String WASHome =  System.getenv("WAS_TOP");
	static String ANTHome = System.getenv("ANT_HOME");
	static String FVT_base_dir = System.getenv("FVT_TOP")+"/autoFVT";
	static HashMap<String,String> h = new HashMap<String,String>();
	static boolean verbose = false;
	
	static{		
		staticrun();
	}
	
	/* this runs when class is initialized */
	private static void staticrun(){       
		checkValid("WAS_TOP", WASHome);
		checkValid("ANT_HOME", ANTHome);
		File inst  = new File(FVT_base_dir+"/instance.xml");
		File prop  = new File(FVT_base_dir+"/src/xmls/properties.xml");
		loadFile(inst);
		loadFile(prop);
		resolveRefs();  // resolve all ${....} variables		
	}

	/**
	 * only use for debug
	 */
	public static void main(String[] args) {
		AntProperties.verbose = true;  
		// run the static code again in verbose mode 
		staticrun();
		System.out.println(getAntProperty("enable.security"));
		System.out.println(getAntProperty("not.there"));
		

	}
	
	public static String getServerURL(){
		String defaultHost = getAntProperty("new.default.host");
		String defaultPort = getAntProperty("new.wc.defaulthost");
		return "http://"+defaultHost+":"+defaultPort;		
	}
	
	public static String getConsoleErrLogfileName(){
		String  WASProfileDir = getAntProperty("profile.dir");
		String serverName = getAntProperty("server.name");	
		return fixPsep(WASProfileDir+"/logs/"+serverName+"/SystemErr.log");
		
	}
	
	public static String getConsoleOutLogfileName(){
		String  WASProfileDir = getAntProperty("profile.dir");
		String serverName = getAntProperty("server.name");	
		return fixPsep(WASProfileDir+"/logs/"+serverName+"/SystemOut.log");
		
	}
	
	public static String getFvtBaseDir(){
		return fixPsep(h.get("FVT.base.dir"));
	}
	
	public static String getAntProperty(String s){
		return h.get(s);		
	}
	
	public static  String fixPsep(String cmd){		
		if(System.getProperty("os.name").toString().contains("Windows")){
			return cmd.replace('/','\\');			
		} else{
			return cmd.replace('\\','/');
		}
		
	}
	
	public static void loadFile(File f){		
		String buf = null;
		String name = null;
		String value = null;
		int i, j, k, l = 0;
		boolean comment = false;
		try{
			BufferedReader br = new BufferedReader(new FileReader(f));
			while(br.ready()){
				buf = br.readLine();
				// crude comment support
				if ((buf.indexOf("<!--"))> -1)  comment = true;
				if ((buf.indexOf("-->")) > -1)  comment = false;
				
				if (!comment && buf.contains("name=") && buf.contains("value=")){
					i = buf.indexOf("name=")+6;  // start
					j = buf.indexOf("\"",i);	 // finish "
					k = buf.indexOf("value=")+7; // start
					l = buf.indexOf("\"",k);	// finish
					
					name = buf.substring(i,j);
					value = buf.substring(k,l);
					if(verbose) System.out.println("added: "+name+" "+value);
					h.put(name, value);				
				}
							
			}
			br.close();
		}
		catch (FileNotFoundException e ){
			throw new RuntimeException("unable to read properties file "+f.getPath());				
		}
		catch (IOException e){
			throw new RuntimeException("unable to read properties file "+f.getPath());				
		}		
	}
	
	public static void listProps(){		
		 Set ks = h.keySet();
		 Iterator<String> it = ks.iterator();
		 String key = null;
		 while (it.hasNext()){
			 key = it.next();
			 System.out.println(key +" = "+h.get(key));
		 }
	}
	
	/* resolve variables */	
	public static void resolveRefs(){
		 Set ks = h.keySet();
		 String key = null;
		 String value = null;
		 String variable = null;
		 String replace = null;
		 int i=0, j=0, variablesFound= 0, variablesResolved=0;
		 int passes = 0;
		 
		 /* go through the properties looking for ${} variables and
		  * substitute them until there aren't any left.
		  */
		 do{
			 variablesFound= 0; variablesResolved = 0;
			 Iterator<String> it = ks.iterator();
			 while (it.hasNext()){
				key = 	it.next();
				value = h.get(key);
				i = value.indexOf("${");  // start
				j = value.indexOf("}");	 // finish "
				if( i> -1){
					variable = value.substring(i+2, j);
					variablesFound++;
					if(verbose) System.out.println("value, variable=" +value+" , "+variable);
					// look it up and try to resolve it
					replace = h.get(variable);
					if(replace != null){				
						h.put(key, value.replace("${"+variable+"}",replace));
						variablesResolved++;
						if (verbose) System.out.println("updated: "+h.get((key)));
					}				
				}

			 } 
			if (variablesFound ==0) break;  // we're done
			if (variablesResolved ==0){		// we're stuck            
                    // 670291.1 - some properties, like path.separator, come directly from the jvm.
                    // try that if they can't be looked up explicitly		     		
                    if (verbose) System.out.println("Warning: could not resolve: " + variable + ", trying system properties");
                    replace = System.getProperty(variable);
                    if (replace != null){                    
                        h.put(variable, replace);
                        variablesResolved++;                    
                        if (verbose) System.out.println("variable " + variable + 
                            " updated to jvm system property: " + replace);                    
                    } else {
                        throw new RuntimeException("AntProperties.java could not resolve: "+variable);
                    }                    
			}
			 if(verbose) System.out.println("passes:"+  ++passes);
		 }	while(true);
		
	}
	
	static void checkValid(String name, String var){
		if (var==null || var.length() == 0){
			throw new RuntimeException("environment variable "+name+" has improper value: "+var);
		}
	}

}
