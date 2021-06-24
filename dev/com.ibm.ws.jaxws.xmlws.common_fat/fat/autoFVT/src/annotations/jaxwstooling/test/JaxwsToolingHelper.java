/*
 * autoFVT/src/annotations/jaxwstooling/test/JaxwsToolingHelper.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Description
 * -----------------------------------------------------------------------------
 * 12/07/2009  samerrel    New File
 *                      
 *
 */
package annotations.jaxwstooling.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class JaxwsToolingHelper{
	
	private static final String filename = "TraceSettings.properties";
	
	private static java.util.List<String> cache = new ArrayList<String>();
	
	private static String filepath = null;
	
	/**
	 * This method modifies the content of the file TraceSettings.properties
	 * @param filepath
	 */
	public static void modify(String filepath) {
		
		try {	
		    System.out.println("Modifying TraceSettings.properties file");
			JaxwsToolingHelper.filepath = filepath;
			RandomAccessFile ra = new RandomAccessFile(filepath+filename, "rw");
			System.out.println("Reading the TraceSettings.properties file");
			read(ra); //read the complete file
			
			ra.setLength(0); //delete the contents of the file
			System.out.println("Rewriting the file");
			rewrite(ra);	//rewrite the contents with required parameters. 
			
			ra.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param ra
	 * @throws IOException
	 */
	private static void read(RandomAccessFile ra) throws IOException{
		
		String line;		
		while((line = ra.readLine())!=null){				
			
			if(!line.startsWith("traceFileName")){
//				cache.add(line);					
			}else{
				cache.add("#"+line); // comment the original traceFileName variable
			}
			
		}		
	}
	
	/**
	 * 
	 * @param ra
	 * @throws IOException
	 */
	private static void rewrite(RandomAccessFile ra) throws Exception{
		
		for(String s: cache)
			ra.writeBytes(s+"\r\n");
		boolean isWindows = System.getProperty("os.name").contains("Windows");
		String sep = isWindows ? "\r\n" : "\n"; 
		
		ra.seek(ra.length());  //Seek to end of file
		//rewrite the traceFileName variable with your filename and enable logging.
		String path = filepath.replace("\\", "/");
		String traceString = sep + "traceFileName="+path+"JaxwsToolingTrace.log" + sep + "com.ibm.ws.websvcs.wsdl.*=all=enabled";
		FileDescriptor f = ra.getFD();
		
		System.out.println("Writing: " + traceString + " to " + f.toString());
	    ra.writeBytes(traceString);  //Write at the end of file
	    ra.close();
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	public static boolean searchTraceLog(String string){
		boolean exist = false;
		File file = new File(filepath+"JaxwsToolingTrace.log");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			try {
				while((line = br.readLine())!=null){
					if(line.contains(string)){
						exist=true;
						break;
					}					
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exist;
	}
	
	public static void main(String[] args){
		System.out.println("Hello World!!");
	}

}

