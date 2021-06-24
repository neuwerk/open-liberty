//
// 1.1, 12/11/08
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

package com.ibm.ws.wsfvt.test.build.tools;

import java.util.*;
import java.io.*;
import junit.framework.*;

public class AuditParser{
    String   _TestName = "";
    String   _result   = "";
    String[] _aRecords = null;
    List<AuditRecord> _auditRecords = null;
    int      _iBeginLine = -1;

    public AuditParser( String TestName, String strResult ){
        _TestName = TestName  == null ? "NULL" : TestName;
        _result   = strResult == null ? "NULL" : strResult;
        if( strResult != null && strResult.length() > 0 ){
            _auditRecords = new ArrayList<AuditRecord>();
            parseRecords( strResult );
        }
    }

    public AuditParser( String TestName, String[] aRecords, int iBeginLine ){
        _TestName   = TestName  == null ? "NULL" : TestName;
        _result     = "NULL"; // in case no records at all
        _aRecords   = aRecords  == null ? new String[0] : aRecords;
        _auditRecords = new ArrayList<AuditRecord>();
        _iBeginLine = iBeginLine;
        for( int iI = 0; iI < _aRecords.length; iI ++ ){
            parseRecord( _aRecords[ iI ] );
        }
    }

    public AuditParser( String TestName, List<String> records ){
        _TestName   = TestName  == null ? "NULL" : TestName;
        _result     = "NULL"; // in case no records at all
        _aRecords   = new String[ records.size() ];
        _auditRecords = new ArrayList<AuditRecord>();
        for( int iI = 0; iI < _aRecords.length; iI ++ ){
            _aRecords[ iI ] = records.get( iI ); 
            // System.out.println( "auditParser:" + _aRecords[ iI ] );
            parseRecord( _aRecords[ iI ] );
        }
    }

    private void parseRecords( String strResult ){
         StringTokenizer st = new StringTokenizer( strResult, "\n\r" );
         _aRecords   = new String[0]; 
         while( st.hasMoreTokens() ){
            String record    = (String)st.nextToken();
            String[] records = new String[ _aRecords.length + 1 ];
            int iI = 0;
            for( ; iI < _aRecords.length; iI ++ ){
                records[ iI ] = _aRecords[ iI ];
            }
            records[ iI ] = record;
            _aRecords = records;
            parseRecord( record );
         }
    }                           

    private void parseRecord( String strRecord ){
        if( !strRecord.startsWith("****" ) ) { // if not no record
            _auditRecords.add( new AuditRecord( strRecord ) );   
        }
    }                           

    public List<AuditRecord> getAuditRecords(){
        return _auditRecords;
    }

    public void printLog(PrintStream ps){
        System.out.println( "TestCase: '" + _TestName + "'");
        if( _auditRecords != null ){
            int iSize = _auditRecords.size();
            for( int iI = 0; iI < iSize; iI ++ ){
                ((AuditRecord)_auditRecords.get( iI )).printLog( ps);
            }
        } else{
            ps.println(  _result                       );
        }
    }
    public void printLog(){
        printLog( System.out );
    }

    public int search( Properties propKeys, int iBegin ){
        int iSize = _auditRecords.size();
        for( ; iBegin < iSize; iBegin ++ ){
            AuditRecord arec = _auditRecords.get( iBegin );
            if( arec.search( propKeys ) ){
                return iBegin;
            }
        }
        return -1; // not found
    }
}
