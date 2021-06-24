/*
 */

package saaj.client.util;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.soap.SOAPMessage;


/**
 * SOAP Monitor Service constants
 */

public class SocketUtil {
    private int     _port     = SAAJHelper.SERVER_PORT;
    private String  _host     = "localhost";
    private boolean _bInit    = false;
    private Socket  _socket;
    private InputStream  _in  = null;
    private OutputStream _out = null;

    private String MESSAGE_BEGIN = SAAJHelper.MESSAGE_BEGIN;
    private String MESSAGE_END   = SAAJHelper.MESSAGE_END;
    private String SOCKET_END    = SAAJHelper.SOCKET_END;

    public SocketUtil(){
        init();
    }
    // Do not do any init() until the last minute
    private boolean init(){
        System.out.println( "Connecting..." );
        _bInit = connectTo();
        return _bInit;
    }

    protected boolean _bIpAddress = false;
    protected String  _strIpAddress;

    private boolean connectTo( )
    {
       String strShow = "Connectting to " + _host + ":" + _port;
       String strErr  = "Error: can not connect to " + _host + ":" + _port;
       System.out.println( strShow );

       String strRemoteIP = null;
       try{
           InetAddress inta   = InetAddress.getByName( _host );
           strRemoteIP        = inta.getHostAddress();
       } catch( Exception e ){
           strRemoteIP = _host;
       }

       try
       {
           System.out.println( "Connecting to " + strRemoteIP + " : " + _port );
           _socket = new Socket( strRemoteIP, _port ); // Open a socket connect to SOAPMonitorHandler
       }
       catch( Exception e )
       {
          e.printStackTrace();
          return false;
       }

       // Open ObjectIO Stream
       try {
         // Use object streams for input and output
         _out = _socket.getOutputStream();
         _in  = _socket.getInputStream();
         _out.flush();
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }
       return true;
    }


    public void close(){
        try{
           _out.close();
           _in.close();
           _socket.close();
        } catch( Exception e ){
              e.printStackTrace();
        }
        _bInit = false;
    }

    public String sendSOAPMessage( SOAPMessage soapMessage ){
        try{
            writeString( MESSAGE_BEGIN );
            soapMessage.writeTo( _out );
            writeString( "" );
            writeString( SAAJHelper.MESSAGE_END   );
            return readSoapMessage();
        } catch( Exception e ){
            e.printStackTrace();
        }
        return "ERROR: Did not get back the SOAPMessage as expected. (SocketUtil)";
    }


    /**
     * Thread for handling the server socket
     */
    public String readSoapMessage(){
        try{
            String str = readline();
            String strSoapMessage = "";
            while ( str.indexOf(SOCKET_END ) < 0 ) {
                if( str.equals( MESSAGE_BEGIN) ){
                    strSoapMessage = readline();
                    str = readline();
                    while( (!str.equals( MESSAGE_END ))&& 
                           (!str.equals( SOCKET_END  )) ) {
                        System.out.println( "ERROR:SAAJFvt did not get " + MESSAGE_END );
                        strSoapMessage = strSoapMessage.concat( "\n" + str );
                        str = readline();
                    }
                    return strSoapMessage;
                } else {
                    str = readline();
                }
            }
        } catch( Exception e ){
            e.printStackTrace( );
        }
        return "ERROR: SocketUtil error in reading SOAPMessage";
    }

    String readline()throws IOException {
        StringBuffer strBuf = new StringBuffer( "" );
        int iChar = _in.read();
        while( iChar != '\n' && iChar >= 0 ){
            strBuf.append( (char)iChar );
            iChar = _in.read();
        }
        String str = strBuf.toString();
        System.out.println( "SAAJFvt socket read in '" +  str + "'" );
        if( str.length() == 0 && iChar < 0){  // in case socket ends
            return SOCKET_END;
        }
        return str;
    }

    void writeString(String str) throws IOException {
        byte[] byteArray = str.getBytes("utf8");
        _out.write( byteArray );
        _out.write( '\n' );
        _out.flush();
    }
}
