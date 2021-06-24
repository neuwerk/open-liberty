//
// 1.1, %%, autoFVT/src/com/ibm/ws/wsfvt/test/build/tools/AuditReadBackLog.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date      UserId       Defect           Description
// ----------------------------------------------------------------------------
// 12/08/09  Gordon Kuo                    Create

package com.ibm.ws.wsfvt.test.build.tools;

import java.util.*;
import java.io.*;
import junit.framework.*;


public  class AuditReadBackLog extends AuditLog {

    public AuditReadBackLog( String TestName, String auditLogFile){
        String strFvtBaseDir = System.getProperty( "FVT.base.dir" );

        _auditlogfile = strFvtBaseDir + "/build/work/" + auditLogFile;
        _TestName     = TestName;
    }

    public List<String> getLogRecords(){
        String strLog = tailAuditLog();

        List<String>records = new ArrayList<String>();
        System.out.println( "getLogRecords:" );
        System.out.println( strLog );

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
                       records.add( record );
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
                records.add( record );
            }
        } else{
            records.add( "****No auditing records found in '" + _auditlogfile + "'");
        }
        return records;
    }

}

