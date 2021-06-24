/*
 * @(#) 1.8 autoFVT/src/annotations/support/OS.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/18/07 08:56:18 [8/8/12 06:54:35]
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
import java.nio.charset.*;
import java.util.StringTokenizer;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
* @author btiffany
*
* Class for invoking operating system commands and a few general purpose helpers. 
* This one tries to work just like a command line.
* Commands will run from fvt.base.dir, which should also be where they run in eclipse. 
*
* 
*/
public class OS {
	private static Process p;
	private static StringBuffer sb = new StringBuffer();
	private static int exitcode = 0;	
	private static boolean debug = false;
	private static LibertyServer lserver=LibertyServerFactory.getLibertyServer("jaxws_fat_server", null, false);
	
	public static void main(String [] args){
		
		// for debugging.  Also may need to enable the print statements in doexec if you're having problems.
		debug = true;
		System.out.println("low level call:");
		try{
			doexec(args, null, ".");		
			System.out.println("output:\n"+sb.toString());
			System.out.println("return code: "+getLastRC());
		} catch (Exception e){
			e.printStackTrace();
            
		}
		
		System.out.println("normal call:");
		sb = new StringBuffer();		
		String allargs = "";
	       for(String s: args){
				allargs = allargs+" "+s;			
	       }	
	    try{
			System.out.println("output:\n"+runs(allargs));
			System.out.println("return code: "+getLastRC());
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * runs command and returns exit code of command that ran
	 */
	public static int run(String oscmd){
		myrun("",oscmd);
		System.out.println("return code: "+ exitcode);
		return exitcode;		
	}
	
	/**
	 * runs command and returns exit code of command that ran
	 */
	public static int run(String startdir, String oscmd){
		myrun(startdir ,oscmd);
		System.out.println("return code: "+ exitcode);
		return exitcode;		
	}
	
	/**
	 * @return exit code of the last command that ran.
	 * Useful if you need both the string and the RC.
	 */
	public static int getLastRC(){
		return exitcode;
	}
	
	/**
	 * Runs command and returns console output of command that ran.
     * Runs in directory FvtBaseDir. 
	 * (Interleaving of stderr and stdout may differ from a real console).
	 */
	public static String runs(String oscmd){
		myrun("", oscmd);
		return sb.toString();
	}
	
	/**
	 * runs command and returns console output of command that ran.
	 * (Interleaving of stderr and stdout may differ from a real console).
     * @param startdir - starting directory, or fvtbasedir if null
     * @param oscmd - command to run
	 */
	public static String runs(String startdir, String oscmd){
		myrun(startdir, oscmd);
		return sb.toString();
	}
	
	private static void myrun(String dir, String oscmd){
       // run something.  Start in dir. 
       // to run internals like "dir", you must invoke with "cmd.exe /c dir"
       // for shell scripts it's probably something like "ksh script.sh"     
		sb.setLength(0);  // clear buffer
       boolean windows = false;
       boolean os400 = false;
       String cmdprefix="";
       String changedircmd="";
       
       String cmd=null;
       // for FVT, need to adjust starting directory between eclipse and the harness.
       String setInitialDirCmd ="cd "+ Support.getFvtBaseDir()+" && ";
       String startdir = Support.getFvtBaseDir();
       if (dir.length()>0){        
           startdir = dir;
       }
       
       if (System.getProperty("os.name").toString().contains("Windows")) windows=true; 
       if (System.getProperty("os.name").toString().contains("OS/400")) os400=true; 
       if (dir.length()>0){    	   
    	   changedircmd="cd \""+dir+"\" && ";
       }       
       // for exec to work right on unix, we have to pass a carefully crafted array
       String [] cmda = new String[3];
       cmda[0] = "sh";
       cmda[1] ="-c";
       if (windows){
        cmda[0]="cmd.exe ";
        cmda[1]="/c";
       }
       String buf = fixPsep(setInitialDirCmd) + fixPsep(changedircmd) + oscmd;       
       if (!os400){
           cmda[2] = buf;
           doexec(cmda, null, ".");
       }
       else {
           // for os400, we're not in qshell envir, so can't run
           // qshell commands, so can't change directories inside
           // command. So we do it thru exec call.
           // We could set this property when we start the JVM, but it would
           // probably break staf:
           //System.setProperty("os400.runtime.exec", "QSHELL");
           
           // So instead we have to do something really hideous, write out
           // a script and then invoke it.
           // The bang line on the script will get it run under qsh
           // even though our native exec process isn't defaulted to qsh. 
           
           // If the script's output emanates from some java process,
           // like wsgen or ant, it will be "readable".  If it's from a native
           // process, like ls or rm, it's in ebcdic.  I haven't figured out
           // how to get it all to one codepage before we read it back in.
           
           StringTokenizer st = new StringTokenizer(oscmd);
           st.countTokens();
           cmda = new String[st.countTokens()];
           int i=0;
           while(st.hasMoreTokens()){
               cmda[i++] = st.nextToken();
           }
           System.out.println("exec in directory: " +startdir);

           try{
               File f = new File("/tmp/temp.sh");
               if (f.exists()) f.delete();           
               PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(f)));
               p.write("#!/usr/bin/qsh \n");
               p.write(oscmd+ "\n");
               p.close();                     
           
           } catch( Exception e){
               e.printStackTrace();
               throw new RuntimeException("error writing temporary file /tmp/temp.sh");
           }
           
           cmda = new String [1];
           cmda[0] = "/tmp/temp.sh";
           System.out.println("temp.sh contents: "+oscmd);

           doexec(cmda, null, startdir);
       }
       
       
       

   }  // end void run method
	
	private static String[] setEnv(String[] envp,int startIndex) {
		
		if (envp==null||envp.length-startIndex<8)
			return envp;
		
		//let's add the following env vars		    		   
//		   <env key="JAVA_HOME" value="${java.home}/../" />
//         <env key="JUNIT_HOME" value="${fvtBaseDir}/common/jars" />
//         <env key="ANT_OPTS" value="-DFVT.base=${fvttop} -DWAS.base.dir=${washome} -Djava.endorsed.dirs=${fvtBaseDir}/common/jars/endorsed" />
//         <env key="FVT_TOP" value="${fvttop}" />
//         <env key="WAS_TOP" value="${washome}" />
//         <env key="ANT_HOME" value="${ant.home}" />
//         <env key="PATH" value="${ant.home}/bin${path.separator}${java.home}/bin${path.separator}${env.PATH}" />
//         <env key="CLASSPATH" path="${ref.to.envclasspath}:${env.CLASSPATH}"/>
	   String FVT_TOP=System.getenv("FVT_TOP");
 	   String WAS_HOME=System.getenv("WAS_TOP");
 	   String ANT_HOME=System.getenv("ANT_HOME");
 	   String FVT_base_dir = System.getenv("FVT_TOP")+"/autoFVT";
 	   String CLASSPATH=System.getenv("CLASSPATH");
 	   String PATH=System.getenv("PATH");
	   envp[startIndex]="JAVA_HOME="+lserver.getMachineJavaJDK();			    	  
	   envp[startIndex+1]="FVT_TOP="+FVT_TOP;
	   envp[startIndex+2]="JUNIT_HOME="+FVT_base_dir+"/common/jars";
	   envp[startIndex+3]="WAS_TOP="+WAS_HOME;
 	   envp[startIndex+4]="ANT_HOME="+ANT_HOME;	
 	   envp[startIndex+5]="ANT_OPTS=-DFVT.base="+FVT_TOP+" -DWAS.base.dir="+WAS_HOME+" -Djava.endorsed.dirs="+FVT_base_dir+"/common/jars/endorsed";
 	   envp[startIndex+6]="PATH="+PATH;
       envp[startIndex+7]="CLASSPATH="+CLASSPATH;
	   System.out.println("if Mac OS, set enviroment vars to subprocess...");
		return envp;
	}
	
	private static void doexec(String [] cmd, String [] envp, String startdir){
	       char cbuf[] = new char[1000];
	       int i = -1;
	       int j = -1;       
	       int rc;
	       boolean done=true;
	       String allargs = "";
	       for(String s: cmd){
	    	    if (debug) System.out.println(">"+s+"<");
				allargs = allargs + s + " ";			
	       }	
	       System.out.println("OS.java run: "+allargs);
           File f = new File(startdir);
	       try {
	           // Start the other process
               // helps, but apparently only works before jvm instantiated.
               /*
                // tried this but it won't pick up property either.
               System.setProperty("os400.runtime.exec", "QSHELL");
               ProcessBuilder pb = new ProcessBuilder(allargs);
               File f = new File(startdir);
               pb.directory(f);
               pb.redirectErrorStream(true);
               
               Process p = pb.start();
               */	    	   
	    	   String osName=System.getProperty("os.name").toLowerCase();	    	   
	    	   if ((osName.indexOf("mac")>-1&&osName.indexOf("os")>-1)) {
		    	   if (envp==null) {
		    		   envp=new String[8];
		    		   envp=setEnv(envp,0);
		    	   }
		    	   else {
		    		   String[] envtmp=new String[envp.length+8];
		    		   for (int index=0;index<envp.length;index++) {
		    			   envtmp[index]=envp[index];
		    		   }
		    		  
		    		   envtmp=setEnv(envtmp,envp.length);
		    		   envp=envtmp;
		    	   }
		    	   
	    	   }
	    	   
	           Process p = Runtime.getRuntime().exec(cmd, envp, f);
	           // The reader for subprocess stdout
	           BufferedReader in = new BufferedReader(
                  //  new InputStreamReader(p.getInputStream(), "IBM500"));
	               new InputStreamReader(p.getInputStream() ));

	           // The reader for subprocess stderr
	           BufferedReader inErr = new BufferedReader(
	               new InputStreamReader(p.getErrorStream()));

	           // the writer to stdin
	           BufferedWriter out = new BufferedWriter(
	               new OutputStreamWriter(p.getOutputStream()));

	           //send command - could do this repeatedly & re-use single process.
	           //out.write("dir  ");
	           //out.newLine();            // the "enter" key
	           //out.flush();

	           // we have to keep draining these streams in a tight loop or the process can block.
	           // test the process exit value so we don't end too early before the process even starts.
	           do{
	               done=true;
	               i=j= -1;
	               //System.out.println("draining");
	               if (in.ready()){
	                  i=in.read(cbuf,0,999);
	                  if (i>0) sb.append(cbuf, 0, i);
	               }
	               //System.out.println("draining err"+ inErr.ready());
	               if (inErr.ready()){
	                  j=inErr.read(cbuf,0,999);
	                  if (j>0) sb.append(cbuf, 0, j);
	               }

	               try{
	                    rc=p.exitValue();
	               }
	               catch ( IllegalThreadStateException e){
	                // System.out.println("not done");
	                   java.lang.Thread.sleep(100);
	                   done=false;
	               }
	               //System.out.println(i+" "+j);
	           }while( !done || i> -1 || j> -1 );


	           exitcode = p.exitValue();

	           // Clean up
	           p.destroy();
	           p.waitFor();           
	           in.close();
	           inErr.close();           
	           out.close();
	       }
	       catch (Exception e) {
	    	   System.out.println("caught this:");
	           e.printStackTrace();
	           exitcode= -1;
	           //sb = null; - bug - that's a static object you're nulling there. 
               // 5.17.07 return the exception info in the stringbuffer.
               ByteArrayOutputStream b = new ByteArrayOutputStream();
               PrintWriter p = new PrintWriter(b, true);               
               e.printStackTrace(p);
               sb.append(b.toString());
	       }
	       
	}
	
	/**
	 * platform independent file copy.
	 * @param source
	 * @param target
	 * @return rc of copy
	 */
	public static int copy(String source, String target){
		String command = "cp ";
		String psep="/";
		if(System.getProperty("os.name").toString().contains("Windows")){
			source = source.replace('/','\\');
			target = target.replace('/','\\');
			command = "copy ";
			psep = "\\";
		}
		System.out.println(runs(command+" " + source+ " "+ target) );
		return getLastRC();
	}

	
	public static int delete(String fname){
		String command = "rm ";
		String psep="/";
		if(System.getProperty("os.name").toString().contains("Windows")){
			fname= fname.replace('/','\\');			
			command = "del /q ";
			psep = "\\";
		}
		System.out.println(runs(command+" " + fname+ " >nul") );
		return getLastRC();
	}	
	
	public static int mkdir(String fname){
		String command = "mkdir -p ";
		String psep="/";
		if(System.getProperty("os.name").toString().contains("Windows")){
			fname= fname.replace('/','\\');			
			command = "mkdir ";
			psep = "\\";
		}
		System.out.println(runs(command+" " + fname) );
		return getLastRC();
		
	}
	/**
	 * fix the path separators in a string, returning something proper for the platform.
	 * @param cmd
	 * @return
	 */
	public static  String fixPsep(String cmd){		
		if(System.getProperty("os.name").toString().contains("Windows")){
			return cmd.replace('/','\\');			
		} else{
			return cmd.replace('\\','/');
		}
		
	}

}

