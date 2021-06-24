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

/* getting the WSSecurity Auditing log file pathname*/



public  class AuditVerifier {

    AuditLog _auditLog     = null;
    boolean  _bSkipBase    = true;
    String   _testName     = "";

    int                _iLastFound   = -1;
    List<String>       _listRecord   = null;
    List<AuditRecord>  _auditRecords = null;

    /**
     *
     * Constructor to read back an sperated auditing records
     *
     */
    public AuditVerifier( String testName, String readBackLogFileName ){
        _testName     = testName;
        _auditLog     = new AuditReadBackLog( testName, readBackLogFileName );
    }

    public AuditVerifier( String testName, boolean bSkipBase ){
        _testName     = testName;
        _bSkipBase    = bSkipBase;
        init();
    }

    // This assume the testing method is calling this constructor
    // Otherwise, we need to call the other constructor
    public AuditVerifier( boolean bSkipBase){
        _bSkipBase    = bSkipBase;
        StackTraceElement[] aSTE = new Exception().getStackTrace();
        for( int iI = 1; iI < aSTE.length; iI ++ ){
            _testName  = aSTE[iI].getMethodName();
            if( _testName.startsWith( "test" ) ){
                break;
            }
        }
        init();
    }

    protected AuditVerifier(){ // for clone only
    }

    protected void init(){
        System.out.println( "----------" + _testName + "--------" );
        _auditLog = new AuditLog( _testName, _bSkipBase );
    }

    // This will allow the AuditLog to continue collecting new records 
    public void resetAuditLog(){
        _listRecord = null;
    }

    // This will set the audit records to the current ending spot
    // and collect brand new records from here on
    public void newAuditLog(){
        _auditLog = new AuditLog( _testName, _bSkipBase );
    }

    // This will collect the auditing records.
    // But after the first call, it won't collect any new auditing records
    // unless resetAuditLog() or newAuditLog() are called.
    public List<String> collectLog(){
        if( _listRecord == null ){
            System.out.println( "---------- auidt records ----" + _testName + "--------" );
            _listRecord = _auditLog.getLogRecords();
            for( int iI = 0; iI < _listRecord.size(); iI ++ ){ // let print the log out
                System.out.println( _listRecord.get( iI ) );
            }
            System.out.println( "----------end of auidt records ----" + _testName + "--------" );
        }
        return _listRecord;
    }

    // This will collect the auditing records.
    // But after the first call, it won't collect any new auditing records
    // unless resetAuditLog() or newAuditLog() are called.
    public List<String> collectLogSilently(){
        if( _listRecord == null ){
            _listRecord = _auditLog.getLogRecords();
        }
        return _listRecord;
    }

    protected List<AuditRecord> collectAuditRecord(){
        if( _auditRecords == null ) {
            AuditParser auditParser = new AuditParser( _testName, collectLog() );
            _auditRecords = auditParser.getAuditRecords();
        }
        return _auditRecords;
    }

    // int iBegin -- begins with 0
    // int iEnd   -- the last AuditRecord to be verified 
    // return -1 if aKeyValue does not match any records
    public int verifyAuditRecord( String[][] aKeyValue, int iBegin, int iEnd ){
        collectAuditRecord(); // make sure audit record were collected
        if( iBegin < 0 ) iBegin = 0;
        int iFound = -1;
        for( int iI = iBegin; iI <= iEnd; iI ++ ){
             //System.out.println( "Searching AuditRecord [" + iI + "]" );
             AuditRecord auditRecord = _auditRecords.get( iI );
             if( auditRecord.matchValues( aKeyValue, iI ) ){
                 iFound = iI;
                 System.out.println( "Found: Seq(" + auditRecord.getValue( "Seq" ) + ")" );
                 break;
             }
        }
        _iLastFound = iFound;
        return iFound; 
    }

    // return -1 if aKeyValue does not match any records
    public int verifyAuditRecord( String[][] aKeyValue, int iBegin ){
        collectAuditRecord(); // make sure audit record were collected
        return verifyAuditRecord( aKeyValue, iBegin, _auditRecords.size() -1 );
    }

    // return -1 if aKeyValue does not match any records
    public int verifyAuditRecord( String[][] aKeyValue ){
        collectAuditRecord(); // make sure audit record were collected
        return verifyAuditRecord( aKeyValue, 0, _auditRecords.size() -1 );
    }

    // return -1 if aKeyValue does not match any records
    public int verifyAuditRecordAfter( String[][] aKeyValue ){
        collectAuditRecord(); // make sure audit record were collected
        return verifyAuditRecord( aKeyValue, _iLastFound, _auditRecords.size() -1 );
    }


    public AuditVerifier cloneVerifier(){
        AuditVerifier auditVerifier = new AuditVerifier();
        auditVerifier._auditLog     = this._auditLog;
        auditVerifier._bSkipBase    = this._bSkipBase;
        auditVerifier._testName     = this._testName;
        auditVerifier._iLastFound   = this._iLastFound;
        // do not clone _listRecord and _auditRecords
        // so the auditRecords can be parsed again
        return auditVerifier;
    }

    public boolean logFileExists(){
        return _auditLog.logFileExists();
    }

    public String getLogFilePathName(){
        return _auditLog.getLogFilePathName();
    }

}

