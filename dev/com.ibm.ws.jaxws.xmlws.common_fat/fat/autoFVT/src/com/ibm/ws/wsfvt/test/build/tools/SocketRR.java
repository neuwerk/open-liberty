//
// @(#) 1.5 autoFVT/src/com/ibm/ws/wsfvt/test/build/tools/SocketRR.java, WAS.websvcs.fvt, WASX.FVT 2/11/10 13:43:52 [2/11/10 18:48:28]
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/30/2004  ulbricht    D250032         New File
// 02/24/2005  ulbricht    D257747         Just print necessary req/resp msg
// 04/01/2005  ulbricht    D265105         Need to write "POST" messages
// 04/11/2006  ulbricht    D347064         Remove checks before writing       
// 01/27/2009  btiffany    572061          Add interruptible read, verbose mode
// 01/29/2010  btiffany    636770          Update to latest version of socketrr from jax-rpc bucket. 
// 01/29/2010  btiffany    636770          Increase buffer size 10x to get around timing problem on windows. 
// 02/11/2010  btiffany    638705          write verbose mode output to proxy log, flush socket more
// 07/01/2010  btiffany    652168          Increase timeout for z.
//
package com.ibm.ws.wsfvt.build.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;

/**
 *
 */
public class SocketRR extends Thread {

    Socket inSocket  = null;
    Socket outSocket  = null;
    InputStream in = null;
    OutputStream out = null;
    boolean xmlFormat;
    volatile boolean done = false;
    Connection myConnection = null;
    String reqRespType = null;
    File reqRespOutput = null;
    boolean verbose = false;

    /**
     *
     */
    public SocketRR(Connection c,
                    Socket inputSocket,
                    InputStream inputStream,
                    Socket outputSocket,
                    OutputStream outputStream,
                    boolean format,
                    String type, 
                    File outFile) {
        inSocket = inputSocket;
        in = inputStream;
        outSocket = outputSocket;
        out = outputStream;
        xmlFormat = format;
        myConnection = c;
        reqRespType = type;
        reqRespOutput = outFile;
        verbose = Connection.verbose;
        start();
    }

    /**
     *
     */
    public boolean isDone() {
        return done;
    }
    
    // in verbose mode, log to file and console
    public synchronized void verboseout(String s){
    	if(verbose){
            s = "SocketRR: "+s;
            
            // everyone needs to call the same synchronized logging method
            myConnection.verboseout(s);   
            //System.out.println(s);  - don't do this, thread may deadlock.
            
        }
    }

    /**
     *
     */
    public void run() {
        try {
            verboseout(reqRespType + "run entered");
            byte[]      buffer = new byte[44096];
            int         saved = 0 ;
            int         len ;
            int         reqSaved = 0 ;
            int         tabWidth = 3 ;

            // The following variables are used for xml pretty printing
            int         indent = 0;
            byte[]      tempBuffer = new byte[8192];
            boolean     inXML = false ;
            boolean     prevTagIsStartTag = false;
            boolean     onMargin = false;

            
            a:
            for ( ; ; ) {
                if (done) {
                    break;
                }
                len = buffer.length;
                // Used to be 1, but if we block it doesn't matter 
                // however 1 will break with some servers, including apache
                if (len == 0) {
                    len = buffer.length;
                }
                if (saved + len > buffer.length) {
                    len = buffer.length - saved;
                }
                int len1 = 0;

                while (len1 == 0) {
                    try {
                    	verboseout(this.reqRespType+" entering read");
                        //len1 = in.read(buffer, saved, len);
                    	// 572061 call a read that will timeout after 4 sec.
                    	// so we don't stick around for minutes waiting for keepalive to close
                    	// httpprops test will wait around for 5, so we'll end after 4 sec.
                        // 652168 - chg to 20 sec. 
                        
                    	len1 = interruptibleRead(in, buffer, saved, len, 20);
                        verboseout(this.reqRespType+" exiting read, len1=" + len1);
                    } catch (Exception ex) {
                    	verboseout(this.reqRespType+" caught read exception");
                        if (done && saved == 0) {
                            break a;  // exit the entire for loop, lose buffer
                        }
                        len1 = -1;
                        break;
                    }
                }
                len = len1;

                if (len == -1 && saved == 0) {
                    break;
                }
                if (len == -1) {
                    done = true;
                }

                // No matter how we may (or may not) format it, send it
                // on unformatted - we don't want to mess with how its
                // sent to the other side, just how its displayed
                if ( out != null && len > 0 ) {
                    verboseout(reqRespType + " starting write to socket");
                    out.write( buffer, saved, len );
                    out.flush();  // 638705 THIS was what was missing! 
                    verboseout(reqRespType + " ending write to socket");
                }

                if (xmlFormat) {
                     // deleted, recover from old version if ever needed. 
                     writeReqResp(buffer, len);
              
                } else {
                    // write to log file
                    writeReqResp(buffer, len);
                }
                
            } // end for
            
            // Only set the 'done' flag if we were reading from a
            // Socket - if we were reading from an input stream then
            // we'll let the other side control when we're done
            //      if ( inSocket != null ) done = true ;
        } catch ( Exception e ) {
            e.printStackTrace();
            if(verbose){
               try{
                   FileOutputStream fos = new FileOutputStream(reqRespOutput, true);
                    e.printStackTrace( (new PrintWriter(fos,true)) );
                    fos.flush(); fos.close();
               } catch(Exception f){}
            }
        } finally {
            verboseout(reqRespType + " finally block");
            done = true;
            try {
                if (out != null) {
                    out.flush();
                    if (null != outSocket) {
                        outSocket.shutdownOutput();
                    } else {
                        out.close();
                    }
                    out = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    if (inSocket != null) {
                        inSocket.shutdownInput();
                    } else {
                        in.close();
                    }
                    in = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            myConnection.wakeUp();
        }
        verboseout(this.reqRespType+" run method ending, thread done.");
    } // end run method

    /**
     * write the first part of the output, before we start capturing data.
     */
    public synchronized void writeReqRespProlog() throws Exception {
        //System.out.println("*********** Logging to: " + reqRespOutput.getPath() + " ************");       
        FileOutputStream fos = new FileOutputStream(reqRespOutput, true);
        fos.write(new String("\n*** Begin: "
                             + reqRespType
                             + " ***\n").getBytes());        
        fos.flush();
        fos.close();
    }
    
    /**
     * write the last part of the output, after we're done capturing data.
     * This may or may not get called, if this process gets killed before socket closes.
     * @param buffer
     * @param len
     * @throws Exception
     */
    public synchronized void writeReqRespEpilog() throws Exception {
    	FileOutputStream fos = new FileOutputStream(reqRespOutput, true);
        fos.write(new String("\n*** End or data flush to socket of: "
                             + reqRespType
                             + " ***\n").getBytes());
        fos.flush();
        fos.close();
    }
    
    /**
     * write each block of data.  Called ASAP after the socket read. 
     * @param buffer
     * @param len
     * @throws Exception
     */
    public synchronized void writeReqResp(byte[] buffer, int len) throws Exception {  
    	writeReqRespProlog();
        String str = new String(buffer, 0, len);
        FileOutputStream fos = new FileOutputStream(reqRespOutput, true);
        fos.write(buffer, 0, len);        
        fos.flush();
        fos.close();
        writeReqRespEpilog();
    }

    /**
     *
     */
    public void halt() {
        try {
            if (inSocket != null) {
                inSocket.close();
            }
            if (outSocket != null) {
                outSocket.close();
            }
            inSocket = null ;
            outSocket = null ;
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            in = null;
            out = null;
            done = true;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * An implementation of read that won't block until keepalive closes the socket minutes later.
     * 
     * 
     * @param in -inputsteam
     * @param b - bytearray for read
     * @param offset - for read
     * @param len - for read
     * @param timeout -kill the read after this many sec and return eof
     * @return - like a normal read,  or fake eof (-1) if interrupted. 
     */
    public int interruptibleRead(InputStream in, byte[] b, int offset, int len, int timeout){
    	 InterruptibleReader r = new InterruptibleReader(in, b, offset, len);
    	 r.start();
    	 long starttime = System.currentTimeMillis();
    	 long endtime = starttime + (timeout * 1000);
    	 long now = starttime;
    	 while(now < endtime){
    		 if( r.done ){ return r.bytesRead; }
    		 try{
    			 Thread.sleep(1000);
    			 now = System.currentTimeMillis();
    		 } catch (Exception e){};	 
    	 }
    	 verboseout(this.reqRespType+" interrupting stalled read");
    	 r.interrupt();
    	 return -1;
    }
    
    /**
     * an inner class that allows a call to read to run on a separate thread,
     * so it can be interrupted. 
     * @author btiffany
     *
     */
    public class InterruptibleReader extends Thread{
    	public boolean done = false;
    	public int     bytesRead = 0; 
    	InputStream in = null; 
    	byte[] b = null;
    	int offset = 0;
    	int len = 0;
    	
    	// constructor
    	InterruptibleReader(InputStream ain, byte[] ab, int aoffset, int alen){    		
    		in = ain;
    		b = ab;
    		offset = aoffset;
    		len = alen;
    	}
    	 
    	
    	public void run(){
    		
    		    try{
    		    	bytesRead = in.read(b, offset, len);
    		    	done = true;
    		    } catch (java.io.IOException e){
    		    	done = true; 
    		    	bytesRead = -1;  // simulate eof
    		    }
    			
    	}
    
    }

}
