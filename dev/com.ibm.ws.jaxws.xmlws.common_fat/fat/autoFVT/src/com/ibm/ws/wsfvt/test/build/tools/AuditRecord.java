//
// 1.4, 1/20/09
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

public class AuditRecord {

    static Hashtable allKeys  = new Hashtable();

    Properties listProperties = new Properties();
    List<String> listKeys   = new ArrayList<String>();
    List<String> listValues = new ArrayList<String>();


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

    public AuditRecord( String strRecord ) { 
        int index   = 0;
        int iEndDex = 0;
        int iLength = strRecord.length();
        while( index < iLength ){ 
            iEndDex = strRecord.indexOf( " | ", index );
            if( iEndDex == -1 ){
               iEndDex = iLength;
            }
            String strAttrib = strRecord.substring( index, iEndDex );
            int iEqual = strAttrib.indexOf( " = " );
            if( iEqual >= 0 ){
                String strKey   = strAttrib.substring( 0, iEqual);
                String strValue = strAttrib.substring( iEqual + 3 );
                if( strKey.equals( "OutcomeReasonCode" ) ){
                    for( int iI = 0; iI < aOutcomeReasonCode.length; iI ++ ){ 
                        if( strValue.equals( aOutcomeReasonCode[ iI ] ) ){
                            strValue = strValue + "(" + 
                                       aOutcomeReasonText[ iI ] + ")";
                            break;
                        }
                    }
                }
                handleAllKeys(strKey, strValue );
                listProperties.put( strKey, strValue );
                listKeys  .add( strKey    );
                listValues.add( strValue );
            }
            index = iEndDex + 3;
        }
    }

    protected void handleAllKeys( String strKey, String strValue ){
        Object obj = allKeys.get( strKey ); 
        if( obj == null ){
            //System.out.println( "Adding Key:'" + strKey + "'" ); 
            List<String>listValues = new ArrayList<String>(); 
            listValues.add( strValue );
            allKeys.put( strKey, listValues );
        } else{
            List<String> listValues = (List<String>)obj;
            if( !listValues.contains( strValue ) ){
                listValues.add( strValue );
                Collections.sort( listValues );
            }
        }
    }

    static public boolean hasKey( String strKey ){
        Object obj = allKeys.get( strKey ); 
        // System.out.println( "Key'" + strKey+ "'" + obj );
        return !(obj == null );
    }

    static public boolean hasKeyValue( String strKey, String strValue ){
        Object obj = allKeys.get( strKey ); 
        if( obj == null ){
            return false;
        }
        List<String> listValues = (List<String>)obj;
        return listValues.contains( strValue );
    }

    static public Enumeration getAllKeys(){
        return allKeys.keys();
    }

    static public List<String> getAllValues(String strKey){
        Object obj = allKeys.get( strKey ); 
        if( obj == null ){
            return null;
        }
        List<String> listValues = (List<String>)obj;
        return listValues;
    }

    public boolean search( Properties propKeys ){
        Enumeration enum1 = propKeys.propertyNames();
        while( enum1.hasMoreElements() ){
            String strKey      = (String)enum1.nextElement();
            String strValue    = propKeys.getProperty( strKey );
            String strRecValue = listProperties.getProperty( strKey );
            if( strRecValue != null ){
                if( !strRecValue.equals( strValue ) ) return false;
            }  else{
                if(!( strValue == null || strValue.equalsIgnoreCase( "null" ) ) )
                    return false;
            }
        }
        return true;
    }

    public boolean matchValues( String[][] aKeyValues, int iRec ){
        for( int iI = 0; iI < aKeyValues.length; iI ++ ){
            String[] keyValue = aKeyValues[ iI ];
            if( !matchValue( keyValue, iRec ) ){
                //System.out.println( "MisMatching(" + iI + ")" );
                return false;
            }
        }
        //System.out.println( "Matching" );
        return true;
    }

    public boolean matchValue( String[] keyValue, int iRec ){
        String strKey   = keyValue[ 0 ];
        String strValue = keyValue[ 1 ];

        return matchValue( strKey, strValue, iRec );
    }

    public boolean matchValue( String strKey, String strValue, int iRec ){
        int iBegin = listKeys.indexOf( strKey );
        int iEnd   = listKeys.lastIndexOf( strKey );
        if( iBegin != iEnd ){
            System.out.println( "***Seq(" + getValue( "Seq" ) + ") Key was duplicated:" + iBegin + "<->" + iEnd ); 
            printDupLog();
            return false;
        }
        if( iBegin == -1 ){  
            // The may generate many outputs
            System.out.println( "****Attribute Key not found:'" + strKey + "'" );
            return false;
        }
        String value = listValues.get( iBegin );
        if( value.equals( strValue ) ){
            return true;
        }                   

        // handle the value which has the ^ in the begin.
        if( strValue.startsWith( "^" ) ){
            String hasStr = strValue.substring( 1 );
            if( value.indexOf( hasStr ) >= 0 ){
                return true;
            }
        }
        System.out.println( " Seq(" + getValue( "Seq" ) + ") '" + strKey + "'  '" + value + "'<-->'" + strValue + "'" );
        return false;
    }

    public String getValue( String strKey ){
        int iBegin = listKeys.indexOf( strKey );
        if( iBegin == -1 ){  
            // The may generate many outputs
            System.out.println( "****Attribute Key not found:'" + strKey + "'" );
            return "";
        }
        String value = listValues.get( iBegin );
        return value;
    }

    public void printLog( PrintStream ps){
        //listProperties.list( os );
        ps.println( ".***" );
        int iSize = listKeys.size();
        for( int iI = 0; iI < iSize; iI ++ ){
            String strKey   = listKeys.get( iI );
            //String strValue = (String)listProperties.get( strKey );
            String strValue  = listValues.get( iI );
            String strValue1 = (String)listProperties.get( strKey );
            ps.println( "....'" + strKey + "' = '" + strValue + "'" );
            if( !strValue1.equals( strValue ) ){
                ps.println( "..Error  Value'" + strValue + "' <> '" + strValue1 + "'" );
            }
        }
    }

    public void printLog(){
        printLog( System.out );
    }

    public void printDupLog( PrintStream ps){
        //listProperties.list( os );
        int iSize = listKeys.size();
        for( int iI = 0; iI < iSize; iI ++ ){
            String strKey    = listKeys.get( iI );
            String strValue  = listValues.get( iI );
            String strValue1 = (String)listProperties.get( strKey );
            if( !strValue1.equals( strValue ) ){
                ps.println( "*** Dup key=" + strKey + " Value'" + strValue + "' <> '" + strValue1 + "'" );
            }
        }
    }

    public void printDupLog(){
        printDupLog( System.out );
    }

}
