/*
 * 1.4, 12/14/11, autoFVT/src/com/ibm/ws/wsfvt/test/build/tools/AuditHelper.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01
 *
 */

package com.ibm.ws.wsfvt.test.build.tools;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;

import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.ws.wsfvt.build.tools.AppConst;

public class AuditHelper extends AuditVerifier {

    TestCase   _testCase = null;
    public AuditHelper( TestCase testCase ){
        super( true );
        _testCase  = testCase;                  
    }

    public AuditHelper( TestCase testCase, boolean skipBaseEvents ){
        super( skipBaseEvents );
        _testCase  = testCase;                  
    }


    public void createLog() {
            String strClassName   = _testCase.getClass().getName();
            int    iIndex         = strClassName.lastIndexOf( "." );
            if( iIndex >= 0 ){
                strClassName      = strClassName.substring( iIndex );    
            }
            String strTestLogName = getAuditTestLogFilePathname( strClassName, _testCase.getName() );
            if( strTestLogName !=  null ){ // else already printed error messages
                try{
                    List<String> logLines = collectLogSilently();
                    File outp   = new File( strTestLogName ); 
                    OutputStreamWriter osw  = 
                         new OutputStreamWriter( new FileOutputStream( outp), "ASCII" );
                    PrintWriter      ps   = new PrintWriter( osw, true);
                    for( int iI = 0; iI < logLines.size(); iI ++ ){ // let print the log out
                        String strTmp =  logLines.get( iI );
                        ps.print( strTmp );
                        ps.print( "\n" );
                    }
                    ps.close();
                } catch(Exception e) {
                    System.out.println("Exception caught  create log:" + e);
                    e.printStackTrace(System.out);
                }
            }
    }

    protected String getAuditTestLogFilePathname( String className, String testName) {
        try {
            String fvtBaseDir   = System.getProperty( "FVT.base.dir" );
            String logPath      = fvtBaseDir.concat( "/build/work/" );
            String logAuditname = "BinaryAudit" + className +
                                             "_" + testName;
            String retPathname  = logPath.concat( "/" + logAuditname + ".log" );
            // System.out.println( "auditLogPathName:" + retPathname);
        
            return retPathname;
        } catch(Exception e) {
            System.out.println("Exception caught formulating path names:" + e);
            e.printStackTrace(System.out);
            return null;
        }
    }

}
