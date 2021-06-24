/*
 * @(#) 1.16 autoFVT/src/annotations/support/Support.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 7/27/07 12:13:45 [8/8/12 06:54:35]
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
 * 05/25/2007  jramos      440922             Changes for Pyxis
 * 07/26/2007  jramos      453487.1           Use AppConst.FVT_HOME
 */
package annotations.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * the "general store" for all your interface implementation and environment adaptation needs.
 * (The shelves are rather bare now, we stock only the jwsdp_Imp.Adapter) 
 * 
 * @author btiffany
 *
 */
public class Support {
	
	/**
	 * 
	 * @param name - type of adapter - one of default, sun, ibm.
	 * @param workingDir - directory where this adapter will do its work.
	 * @return - an adapter object
	 */
	public static ImplementationAdapter getImplementationAdapter( String name, String workingDir){
		if (name.compareTo("default") == 0) return new IBM_ImplementationAdapter(workingDir);
		//if (name.compareTo("sun") == 0) return new Sun_ImplementationAdapter(workingDir); 
		if (name.compareTo("ibm") == 0) return new IBM_ImplementationAdapter(workingDir);		
		throw new RuntimeException("unsupported implementation type:"+name);	
	}
	
	/**
	 * see if we are running inside the harness (unittest.bat, ant, etc.) or standalone
	 * if we find buildNumber env. var set, we're in the harness.
	 * @return
	 */
	public static boolean inHarness(){
		String fvtBaseDir=System.getenv("buildNumber");
    	if (fvtBaseDir != null) return true;
    	return false;		
	}
	
	public static void main(String [] args){
		// for debug
		
		System.out.println( new Support().fetchurl("http://localhost:9080/websvcanno10/websvcanno10?wsdl",10,false));
	}
	
	/**
	 * get absolute path to FVT.base.dir, i.e FVT/WS.(blah)/fvt.
	 * Use this so file system commands can run consistently regardless of where they are invoked from
	 * eclipse, ant, or command line. 
     * 
     * In unittest.sh, FVT_base_dir is not defined in the environment, so have
     * to derive it from FVT_TOP 
	 */
	public static String getFvtBaseDir(){		
	       String fvtBaseDir=AppConst.FVT_HOME;
	       // strip the drive letter\ if present
	       if( fvtBaseDir.charAt(1)==':') fvtBaseDir = fvtBaseDir.substring(2);	       
	       fvtBaseDir = fvtBaseDir.replace('\\','/');
	       return fvtBaseDir;	    
	}
	
	/**
	 * see if we can get a good connection to a url.  return true if we can.
	 * @deprecated - use Operations.java
	 * @param url
	 * @param timeoutSec
	 * @return
	 */
	public static boolean fetchurl(String url, int timeoutSec){
		return( fetchurl(url, timeoutSec, true).compareTo("true")==0 );	
	}	
	
	/**
	 * see if a url can be reached and the contents retrieved.
	 * @deprecated - use Operations.java
	 * @param url
	 * @param timeoutSec - timeout to stablish initial connection
	 * @param ignoreContents - just check the connection. Don't bother retreiving contents
	 * @return the contents
	 */
	public static String fetchurl(String url, int timeoutSec, boolean ignoreContents){
		URL u = null;
		HttpURLConnection h = null;
		String resp = null;
		int responseCode = 0;
		boolean gotresponse = false;
		byte[] buf = null;
		try{
			u = new URL(url);		    
		} catch (MalformedURLException e){
			throw new RuntimeException("malformed url");
		}
		int i=0;
		System.out.println("trying: "+url); 
		do{
			try{
				h = (HttpURLConnection) u.openConnection();
				h.setRequestMethod("GET");
				h.connect();
				responseCode = h.getResponseCode();				
			} catch( IOException e){
				i++;
				//System.out.println("waiting for server: "+url);
				try {Thread.sleep(1000);} catch( InterruptedException x ){}	
				continue;
			}
			gotresponse = true;			
			break;
			
		} while(i < timeoutSec);	
		
		if(gotresponse){
			if (responseCode <200 || responseCode >300){
				System.out.println("got error response: "+ responseCode);
				gotresponse = false;
			} else{
				System.out.println("got response:" +responseCode);	
			}
			
		} else{
			System.out.println("no response, timed out");
		}
		
		if (ignoreContents ) {
			h.disconnect();
			if (gotresponse) return "true"; else return "false"; 
		}
		
		// otherwise, go get the contents	
		try{
			InputStream is = h.getInputStream();	
			buf = new byte[h.getContentLength()];
			is.read(buf);	
			h.disconnect();			
			return new String(buf);  // how we convert byte[] to String...
		} catch (IOException e){
			return "io exception";
		}
		
	}

}
