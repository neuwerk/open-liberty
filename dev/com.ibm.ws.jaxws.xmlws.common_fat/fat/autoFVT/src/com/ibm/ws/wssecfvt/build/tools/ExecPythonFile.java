/*
 * 1.3, 1/6/10
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 02/19/09     gkuo                         migrating  to moonstone
 */

package com.ibm.ws.wssecfvt.build.tools;

import java.io.*;
import java.util.Vector;
import com.ibm.ws.wsfvt.build.tasks.WsadminScript;

// for common routines on running wsadmin tasks
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Machine;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Enumeration;
/**
 * Migrating from old acute to simplicity.
 * keep the old class path... easier but ugly
 *
 * This class is called to run the jython file.
 * This assume the FVT and Dmgr are in the same machine
 * Unless the Wsadmin class can handle it in different hosts.
 * 
 * This class will check the jython file and see if it needs to be run
 * as a file (indentation always presents in this case). 
 * If so, the file will be executed as a file (of course, not as a criminal). 
 * And calling om.ibm.ws.wsfvt.build.tasks.WsadminScript to run it.
 * 
 * Otherwise, the statements in the file will be excuted line by line 
 * in simplicity Wsadmin default provider instance/session.
 * During executing the statements, this will replace "sys.argv[0]"and
 * "sys.argv[1]" with the parameters passed in 
 * And also change "forceSyncIfND()" to "syncall()" which is
 *  a common routine in the default provider session 
 * Customer of old ExecPythonFile ought to be able to call this one
 * except this is no longer gurantee working in "FVTHost differ from DmgrHost/BaseHost" 
 */
public class ExecPythonFile extends ExecPythonCmd {

        String _strResult = "";
        
        public ExecPythonFile(){
        }

        // variants to accept parameters as: "python file", param1, param2
        // And that's it. No param3
        public String execPythonFile( File file ){
            return execPythonFile( file, (File)null, null, null );
        }
        public String execPythonFile( File file, String strParam1 ){
            return execPythonFile( file, (File)null, strParam1, null );
        }
        public String execPythonFile( File file, String strParam1, String strParam2 ){
            return execPythonFile( file, (File)null, strParam1, strParam2 );
        }

        public String execPythonFile( File file, File profile ){
            return execPythonFile( file, profile, null, null );
        }
        public String execPythonFile( File file, File profile, String strParam1 ){
            return execPythonFile( file, profile, strParam1, null );
        }
        public String execPythonFile( File file, File profile, String strParam1, String strParam2 ){
            System.out.println( "executing jython file " + file.getAbsolutePath() );
            if( !file.exists() ){
                return( "ERROR: The python file does not exist:'" + file.getAbsolutePath() + "'"  );
            }

            Vector<String> vec = getPlainVec( file, strParam1, strParam2 );
            if( vec == null ){ // found indentation
                execWsadminScript( file, profile, strParam1, strParam2);
            }  else{
                execCode( vec );
            }
            return "migrating from acute to simplicity...";
        }


        public String getLatestResult(){
            return _strResult;
        }

        // 
        public String execWsadminScript( File file, File profile, String strParam1, String strParam2 ){
            System.out.println( "executing file with WsadminScript class" );
            WsadminScript wsa = new WsadminScript();
            String strPS = null;
            if( strParam1 != null ){
                strPS = strParam1;
            }
            if( strParam2 != null ){
                strPS +=  " " + strParam2;
            }
            if( strPS != null ){
                System.out.println( "Parameters:'" + strPS + "'" );
                wsa.setParms( strPS);
            }
            if( profile != null ){
                wsa.setProfile( profile ); 
            }
            wsa.setFailOnError( true );
            wsa.setLang( "jython" );
            wsa.setScriptFile( file );
            try{
                wsa.execute();
                _strResult = wsa.getLatestResult();   // The WsadminScript may not return the 
                                                      // stdout. We may need to think about something else
            } catch( Exception e ){
                System.out.println( "get an exception:" + e.getMessage() );
                e.printStackTrace( System.out );
                return "get an exception:" + e.getMessage();
            }
            return "ran in simplicity.WsadminScript";
        }

        public void execCode(Vector<String> vec){
            // excuting the stamenets one by one
            int iTotal = vec.size(); 
            for( int iI = 0; iI < iTotal; iI ++ )
            {
               String strTmp = (String)vec.elementAt( iI );
               try{
                   cmd( strTmp );
               } catch( Exception e ){
                   System.out.println( "got an exception while running the statement:" + e.getMessage() );
                   e.printStackTrace( System.out);
               }
            }
        }

        public void execCode(String[] aStr){
            // excuting the stamenets one by one
            int iTotal = aStr.length;
            for( int iI = 0; iI < iTotal; iI ++ )
            {
               try{
                   cmd( aStr[ iI ] );
               } catch( Exception e ){
                   System.out.println( "got an exception while running the statement:" + e.getMessage() );
                   e.printStackTrace( System.out);
               }
            }
        }

        public Vector<String> getPlainVec( File inp, String strParam1, String strParam2 )
        {
           try
           {
              InputStreamReader inputStream = 
                 new InputStreamReader( new FileInputStream( inp ), "ASCII" );
              BufferedReader dataStream  = new BufferedReader( inputStream);

              Vector<String> vec = new Vector<String>( 800, 800 );
              String strTmp = null;
              boolean bMarking = false;
              while( (strTmp = dataStream.readLine()) != null )
              {
                  //System.out.println( strTmp );
                  if( strTmp.startsWith( "#" ) ){ // it's a comment line. Skip it. No need to exec it
                      continue;
                  }
                  if( strTmp.length() == 0 ) continue;   // no need to exec it, too
                  if( strTmp.startsWith( "   " ) || 
                      strTmp.startsWith( "\t"  )  // tab character is eauvalient to "    "
                    ){// could be an indentation (check 3 characters ought to be enough)
                      // let's see if it's an empty string...
                      strTmp = strTmp.trim();
                      if( strTmp.length() == 0 ) continue;   // empty line. no need to exec it, too

                      // found indentation 
                      dataStream.close();
                      return null; 
                  }

                  if( strTmp.indexOf( "forceSyncIfND()" ) >= 0 ){
                      strTmp = replaceStr( strTmp, "forceSyncIfND()", "syncall()" );
                  } else if(strTmp.indexOf("sys.argv[0]") >= 0){ // sys.argv[0] is an object
                     strTmp = replaceStr( strTmp,"sys.argv[0]", "'" + strParam1 + "'" );
                  } else if(strTmp.indexOf("sys.argv[1]") >= 0){ // sys.argv[1] is an object, too 
                     strTmp = replaceStr( strTmp,"sys.argv[1]", "'" + strParam2 + "'" );
                  } else if(strTmp.indexOf("${param1}") >= 0){
                      strTmp = replaceStr( strTmp,"${param1}", strParam1 );
                  } else if(strTmp.indexOf("${param2}") >= 0){
                      strTmp = replaceStr( strTmp,"${param2}", strParam2  );
                  } // we can try 3rd parameter here... but not necessary for now
                  vec.addElement( strTmp );
              }
              dataStream.close();

              return vec;
           }
           catch( Exception e )
           {
              e.printStackTrace();
           }
           return null;
        }

}
