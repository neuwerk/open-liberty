//
// 1.6, 4/20/10, autoFVT/src/com/ibm/ws/wsfvt/test/build/tools/AuditLog.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect           Description
// ----------------------------------------------------------------------------
// 11/25/08 Gordon Kuo                   Create
// 12/18/08 jramos      566458           Use Simplicity API

package com.ibm.ws.wsfvt.test.build.tools;

import java.util.*;
import java.io.*;
import junit.framework.*;

/* getting the WSSecurity Auditing log file pathname*/
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.test.build.tools.AuditParser;
import com.ibm.ws.wsfvt.test.build.tools.AuditRecord;
import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.Server;


public  class AuditLog {


    protected String  _TestName     = "";
    protected String  _auditlogfile = ""; // to be set later
    protected long    _lStartLoc    = 0;
    protected String  _strResult    = "";
    protected String  _strCharSet   = "ASCII";
    protected boolean _bSkipBase    = true;


    static final String[] aOutcomeReasonCode = new String[] {
        "1"  /* SEC_CONTEXT_NONEXIST    */,
        "4"  /* TOKEN_EXPIRED           */,
        "5"  /* AUTHN_SUCCESS           */,
        "6"  /* ACCESS_SUCCESS          */,
        "8"  /* AUTHZ_SUCCESS           */,
        "13" /* AUTHENTICATION_FAILED   */,
        "15" /* AUTHN_DENIED            */,
        "28" /* INVALID_UIDPSWD         */,
        "43" /* AUTHENTICATION_FAILURE  */,
        "86" /* SUCCESSFUL_DELEGATION   */,
        "89" /* SEC_HEADER_MISSING      */,
        "90" /* TIMESTAMP               */,
        "91" /* TIMESTAMP_BAD           */,
        "92" /* CONFIDENTIALITY         */,
        "93" /* CONFIDENTIALITY_BAD     */,
        "94" /* DECRYPTION_ERROR        */,
        "95" /* VALID_SIGNATURE         */,
        "96" /* INVALID_SIGNATURE       */,
        "97" /* INTEGRITY               */,
        "98" /* INTEGRITY_BAD           */,
        "99" /* AUTHN_LOGIN_EXCEPTION   */,
        "100" /* AUTHN_PRIVILEDGE_ACTION_EXCEPTION */
    };

    static final String[] aOutcomeReasonText = new String[] {
        /* "1"  */  "SEC_CONTEXT_NONEXIST"   ,
        /* "4"  */  "TOKEN_EXPIRED"          ,
        /* "5"  */  "AUTHN_SUCCESS"          ,
        /* "6"  */  "ACCESS_SUCCESS"         ,
        /* "8"  */  "AUTHZ_SUCCESS"          ,
        /* "13" */  "AUTHENTICATION_FAILED"  ,
        /* "15" */  "AUTHN_DENIED"           ,
        /* "28" */  "INVALID_UIDPSWD"        ,
        /* "43" */  "AUTHENTICATION_FAILURE" ,
        /* "86" */  "SUCCESSFUL_DELEGATION"  ,
        /* "89" */  "SEC_HEADER_MISSING"     ,
        /* "90" */  "TIMESTAMP"              ,
        /* "91" */  "TIMESTAMP_BAD"          ,
        /* "92" */  "CONFIDENTIALITY"        ,
        /* "93" */  "CONFIDENTIALITY_BAD"    ,
        /* "94" */  "DECRYPTION_ERROR"       ,
        /* "95" */  "VALID_SIGNATURE"        ,
        /* "96" */  "INVALID_SIGNATURE"      ,
        /* "97" */  "INTEGRITY"              ,
        /* "98" */  "INTEGRITY_BAD"          ,
        /* "99" */  "AUTHN_LOGIN_EXCEPTION"  ,
        /* "100" */ "AUTHN_PRIVILEDGE_ACTION_EXCEPTION"
    };


    protected AuditLog(){
    }

    public AuditLog( String TestName, String auditLogFile){
        _auditlogfile = auditLogFile;
        _TestName     = TestName;
        _lStartLoc    = getFileLength();
    }


    public AuditLog( String TestName, boolean bSkipBase ){
        _auditlogfile = getAuditLogFilePathname();
        _TestName     = TestName;
        _lStartLoc    = getFileLength();
        _bSkipBase    = bSkipBase;
    }

    protected String getAuditLogFilePathname() {
        try {
            ApplicationServer defAppServer = TopologyDefaults.getDefaultAppServer();
            Node              defNode      = defAppServer.getNode();
            String            defProfile   = defNode.getProfileDir();
            String            logPath      = defProfile.concat( "/logs/" + 
                                                             defAppServer.getName() );
            String            logAuditname = "BinaryAudit_" + 
                                             defNode.getCellName() + "_" +
                                             defNode.getName() + "_" +
                                             defAppServer.getName();

            String osName = System.getProperty( "os.name" ); // if zos -- "z/OS"
            if( osName.equals( "z/OS" ) ){ // if zOS
                logAuditname += "_SR";
            }
            String            retPathname  = logPath.concat( "/" + logAuditname + ".log" );

            System.out.println("BinaryAudit pathName:'" + retPathname + "'" );
        
            return retPathname;
        } catch(Exception e) {
            System.out.println("Exception caught formulating path names:" + e);
            e.printStackTrace(System.out);
            return null;
        }
    }

    //Internal method to get the audit log file length before a test is run
    public long getFileLength()
    {
            long fileLength=0;
            try {
                RandomAccessFile raf = new RandomAccessFile(_auditlogfile, "r");        
                fileLength = raf.length();
                raf.close();
            } catch (FileNotFoundException e) {
                System.out.println("Is Audit enabled? FileNotFoundException exception:" + e);
                e.printStackTrace(System.out);
            } catch (IOException e) {
                System.out.println("IOException exception:" + e);
                e.printStackTrace(System.out);
            }
            return fileLength;
    }


    //Internal method to "tail" the audit log to only read events generated for each test
    public String tailAuditLog()
    {
            try {
                RandomAccessFile raf = new RandomAccessFile(_auditlogfile, "r");
                long endPosition = raf.length();
                long charsToRead = endPosition - _lStartLoc;
                byte[] bytes = new byte[(int)charsToRead];

                raf.seek(_lStartLoc);
                raf.readFully(bytes);
                raf.close();
                _strResult = new String(bytes, _strCharSet);
                return _strResult;

            } catch (FileNotFoundException e) {
                System.out.println("Is Audit enabled? FileNotFoundException exception:" + e);
                e.printStackTrace(System.out);
                return null;
            } catch (IOException e) {
                System.out.println("IOException exception:" + e);
                e.printStackTrace(System.out);
                return null;
            }
    }

    public List<String> getLogRecords(){
        String strLog = tailAuditLog();

        List<String>records = new ArrayList<String>();
        //System.out.println( "getLogRecords:" );
        //System.out.println( strLog );

        StringTokenizer st     = new StringTokenizer( strLog, "\n\r" );
        StringBuffer    strBuf = new StringBuffer();
        String          record = null;
        if( st.hasMoreElements() ){
            while( st.hasMoreTokens() ){
               String strToken = (String)st.nextToken();
               if( strToken.startsWith( "Seq =" ) ){
                   if( strBuf.length() > 0 ) {
                       record = strBuf.toString();
                       strBuf = new StringBuffer( strToken);
                       handlRecord( records, record );
                   } else {
                       strBuf.append( strToken );
                   }
               } else{
                   strBuf.append( "\n" + strToken );
               }
            }
            // handle the last audit record which was not handled by handleRecord...
            if( strBuf.length() > 0 ){
                record = strBuf.toString();
                handlRecord( records, record );
            }
        } else{
            records.add( "****No auditing records found in '" + _auditlogfile + "'");
        }
        return records;
    }

    protected void handlRecord( List<String>records, String record ){
        //System.out.println( record );

        if( (!_bSkipBase) || (record.indexOf( "MsgId = " ) >= 0) ){ // DoNotSkipBase or WSSecurity auditing records ?
            String orc    = "OutcomeReasonCode = ";
            int iOrc      = record.indexOf( orc  );
            if( iOrc >= 0 ){
                String strPre   = record.substring( 0, iOrc + orc.length() );
                String strPost1 = record.substring( iOrc + orc.length()  );
                int iSpace      = strPost1.indexOf( " " );
                String strValue = null;
                String strPost  = null;
                if( iSpace >= 0 ){
                    strValue = strPost1.substring( 0, iSpace );
                    strPost  = strPost1.substring( iSpace );
                    for( int iI = 0; iI < aOutcomeReasonCode.length; iI ++ ){
                        if( strValue.equals( aOutcomeReasonCode[ iI ] ) ){
                            strValue = strValue.concat( "(" + aOutcomeReasonText[ iI ] + ")" );
                            break;
                        }
                    }
                } else{
                    strValue = strPost1;
                    strPost  = "";
                }
                record = strPre.concat( strValue ).concat( strPost );
            }
        } else{ // Base Auditing records
            if( record.startsWith( "Seq =" ) ) {
                int index1 = record.indexOf( " | " );
                int index2 = record.indexOf( " | ", index1 + 3 );
                record = record.substring( 0, index2 );
                record = record.concat( "(Base)" );
            } 
        }
        records.add( record );
    }

    public boolean logFileExists(){
        try{
            File logFile = new File( _auditlogfile );
            if( logFile.exists() ) return true;
        } catch( Exception e ){
            e.printStackTrace( System.out );
        }
        System.out.println( "** auditlogefile " + _auditlogfile + " does not exist" );
        return false;
    }

    public String getLogFilePathName(){
        return _auditlogfile;
    }
}

