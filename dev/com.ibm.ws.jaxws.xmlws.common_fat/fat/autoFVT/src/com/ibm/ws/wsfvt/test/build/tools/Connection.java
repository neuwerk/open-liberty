//
// @(#) 1.6 autoFVT/src/com/ibm/ws/wsfvt/test/build/tools/Connection.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 3/4/10 14:41:41 [8/8/12 06:56:44]
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/30/2004  ulbricht    D250032         New File
// 02/21/2007  ulbricht    D421970         Add write to file
// 01/24/2007  btiffany                    add comments, verbose mode.
// 05/08/2009  btiffany    589400          remove hardcoded port 80
// 10/04/2009  bluk        616624          replaced entire file with the one from webservices
// 02/11/2010  btiffany    638705          write verbose mode output to proxy log
// 03/04/2010  btiffany    641467          Fix an intermittent deadlock condition
//
package com.ibm.ws.wsfvt.build.tools;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;

import java.net.URL;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Date;

/**
 *  A tcp-mon like class that can also act like a proxy 
 *   (that is, the output host and port will be determined by the content of the 
 *    get/put line of the message)
 *  When invoked it will run until its java process is externally terminated. 
 */
public class Connection extends Thread {

    Socket inSocket = null;
    Socket outSocket = null;
    SocketRR rr1 = null;
    SocketRR rr2 = null;
    InputStream inputStream = null;
    static File outFile = null;
    static boolean format = false;
    static int socket = 80;
    static boolean verbose = false;
    static java.util.Date processCreatedAt = null;
    
    public void verboseout(String s){
        if (verbose) {
            s = "Connection: "+s;
            //System.out.println(s);  // don't bother, stream isn't connected, and can deadlock. 
            synchronized(this){
                try{
                    FileOutputStream fos = new FileOutputStream(outFile, true);
                    fos.write((s+"\n").getBytes());               
                    fos.flush();
                    fos.close();
                } catch (Exception e1) {}
            }
        }
    }

    /**
     *
     */
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {            
            if (args[i].equals("-o") && ((i + 1) <= args.length)) {
                //if (args[++i].equals("System.out")) {
                //    outputStream = System.out;
                //} else {
                    outFile = new File(args[++i]);                    
                //    fos = new FileOutputStream(outFile, true);
                //    outputStream = fos;
                //}
            } else if (args[i].equals("-s") && ((i + 1) <= args.length)) {
                socket = new Integer(args[++i]).intValue();
            } else if (args[i].equals("-f") && ((i + 1) <= args.length)) {
                format = new Boolean(args[++i]).booleanValue();
            } else if (args[i].equals("-v") && (i   <= args.length)) {                
                verbose = true;                
            }
        }
        
        //verboseout("Running in verbose mode");
        processCreatedAt = new Date();
        // open a listener socket
        ServerSocket ss = new ServerSocket(socket); 
        Connection c = null;
        
        // for every attempt to connect, spin a thread. See "run" for what happens next. 
        for ( ; ; ) {
            c = new Connection(ss.accept());
        }
    }

    /**
     * Ctor accepting a socket to listen on
     *
     * @param s The socket to listen for requests
     */
    public Connection(Socket s) {
        inSocket = s;
        start();
    }

    /**
     * Ctor accepting an input stream from a socket
     *
     * @param in The input stream from a socket to listen on
     */
    public Connection(InputStream in) {
        inputStream = in;
        start();
    }

    /**
     * The run method for the thread.
     */
    public void run() {
    	verboseout(" accepted a connection on "+ socket);
        try {
            InputStream  tmpIn1  = inputStream;
            OutputStream tmpOut1 = null;

            InputStream  tmpIn2  = null;
            OutputStream tmpOut2 = null;

            if (tmpIn1 == null) {
                tmpIn1  = inSocket.getInputStream();
            }

            if (inSocket != null) {
                tmpOut1 = inSocket.getOutputStream();
            }

            String bufferedData = null;
            StringBuffer buf = null;

            byte[] b = new byte[1];

            buf = new StringBuffer();
            String s;

            // read until we've drained the buffer, 
            // keep appending it to a string until we reach a newline. 
            boolean firstread = true;
            for ( ; ; ) {
                int len;
                len = tmpIn1.read(b, 0, 1);
                if ( len == -1 ) {
                    // if this is the first read, give sender time to load the buffer after establishing the connection
                    if( firstread){
                        firstread = false;
                        verboseout("no incoming data on the socket yet, will retry one time");
                        Thread.sleep(1000);
                        continue;
                    }
                    verboseout("read ending");
                    break;
                }
                firstread = false;
                s = new String(b);
                buf.append(s);
				verboseout("buf = "+buf.toString());
                if (b[0] != '\n') {
                    continue;
                }
                break;
            }

            
            bufferedData = buf.toString();

            String targetHost = null;
            int targetPort = -1;       
          
            
            
            
            // if we are processing a get or post, interrogate 
            // for the destination host and port, so we can 
            // open a connection to the correct destination
            
            // a process should never span more than one test.
            verboseout("The proxy process was first created at "+processCreatedAt.toString() + 
                     "\nfirst line of data in was: " +bufferedData);
            if (bufferedData.startsWith("GET ") ||
                bufferedData.startsWith("POST ")) {
                verboseout("extracting url");
                int  start, end;
                URL  url ;
                //POST http://localhost:9082/wihttpprops/EchoService HTTP/1.0
                start = bufferedData.indexOf(' ') + 1;
               
                while ( bufferedData.charAt(start) == ' ') {
                    start++;
                }               
                
                end = bufferedData.indexOf(' ', start);
                String urlString = bufferedData.substring(start, end);                

                if (urlString.charAt(0) == '/') {
                    urlString = urlString.substring(1);
                }                
                url = new URL(urlString);
                
                targetHost = url.getHost();                

                targetPort = url.getPort();               

                verboseout("url, host, port= " + urlString + " " + targetHost + " " + targetPort );

                if (targetPort == -1) {
                    targetPort = 80 ;
                }
                bufferedData = bufferedData.substring(0, start) +
                               url.getFile() +
                               bufferedData.substring(end);
            }
            
          
            // now create a connection on host, port 
            verboseout(" opening a socket to host "+ targetHost+ "on port "+ targetPort);
            outSocket = new Socket(targetHost, targetPort);

            tmpIn2  = outSocket.getInputStream();
            tmpOut2 = outSocket.getOutputStream();

            // write the data to the desination host and port 
            if (bufferedData != null) {
                byte[] data = bufferedData.getBytes();
                tmpOut2.write(data);
                verboseout("writing initial line of data to output: "+ (new String(data)));
            }

            
            // Above, we just took care of opening the output port and writing
            // the first line of data.  But there may be more.  We're going to
            // fire up separate threads to move stuff between the requester
            // and responder, and log it as we go. 
            
            // to avoid blocking on a keepalive connection, socketrr will
            // stop reading after a timeout period, which might be troublesome
            // if the response streams in very slowly.  But so far it hasn't 
            // been a problem, compared to what it was doing before, which
            // was blocking and losing the request or resonse altogehter. 
            
            // this deals with writing the data that was intercepted to 
            // the file. 
            
            // start the request forwarder
            rr1 = new SocketRR(this, inSocket, tmpIn1, outSocket,
                               tmpOut2, format, "Request", outFile);
            
            // start the response forwarder
            rr2 = new SocketRR( this, outSocket, tmpIn2, inSocket,
                                tmpOut1, format, "Response", outFile);

            // loop as long as either thread isn't done. 
            while ( rr1 != null || rr2 != null ) {
                // Only loop as long as the connection to the target
                // machine is available - once that's gone we can stop.
                // The old way, loop until both are closed, left us
                // looping forever since no one closed the 1st one.
                // while( !rr2.isDone() ) 
                if (null != rr1 && rr1.isDone()) {
                    rr1 = null;
                }
                if (null != rr2 && rr2.isDone()) {
                    rr2 = null;
                }

                //  Thread.sleep( 10 );
                synchronized ( this) {
                    this.wait(1000); //Safety just incase we're not told to wake up.
                }
            }

        // some tests have a method that probes the Connection's port to 
        // be sure it is up and listening before starting the tests.
        // The action of probing will cause an empty line to be read
        // and land us here.  If that's what happens, we don't want to 
        // get excited over the exception being thrown.  
        } catch ( Exception e ) {
            try {
                FileOutputStream fos = new FileOutputStream(outFile, true);
                if(verbose){
                    fos.write("Connection.run caught an unexpected exception and will now halt: \n ".getBytes());
                    fos.write("This can be caused by a test probing the port\n".getBytes());
                    e.printStackTrace( (new PrintWriter(fos,true)) );
                }  else {     // preserve legacy behavior for other tests that might read this output
                    fos.write((e.toString() + "\n").getBytes());                
                }
                fos.flush();
                fos.close();
            } catch (Exception e1) {
            }
            //e.printStackTrace();
             verboseout("halt starting");
             halt(); 
             verboseout("halt ending");
            
        } finally {
            verboseout ("Connection.run ending");
        }
    }

    /**
     *
     */
    synchronized void wakeUp() {
        this.notifyAll();
    }

    /**
     *
     */
    public void halt() {
        try {
            if (rr1 != null) {
                rr1.halt();
                rr1.interrupt();
                
            }
            if (rr2 != null) {
                rr2.halt();
            }
            if (inSocket != null) {
                inSocket.close();
            }
            inSocket = null ;
            if (outSocket != null) {
                outSocket.close();
            }
            outSocket = null ;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void remove() {
        try {
            halt();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


}
