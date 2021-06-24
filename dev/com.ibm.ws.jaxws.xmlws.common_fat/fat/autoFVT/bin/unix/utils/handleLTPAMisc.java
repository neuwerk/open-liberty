/*
 * 1.1, 1/4/08
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 12/06/2007  gkuo                           created 
 *        
 */
//package com.ibm.ws.wsfvt.build.tasks;

import java.io.*;
import java.util.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 *  The EnableLTPA class is a custom task for enable LTPA security
 *  The first version need to be run in the local system to the dmgr or default server
 *  If dmgr or defayult server is not running in the same system. This will have quite some troubles
 *  This will change the sas.client.props in the local profile 
 *      but not the othe profiles, such as:
 *      other nodes in local system or even other systems.
 *
 *  The class conforms to the standards set in the Ant
 *  build framework for creating custom tasks.
 */
public class handleLTPAMisc extends Task {

    private static String sLtpaHost      = "";
    private static String sLtpaPort      = "";
    private static String sLtpaUser      = "";
    private static String sLtpaPassword  = "";
    private static String sLtpaDomain    = "";

    // no default value
    private static String sFvtBaseDir    = "";  // Fvt Base Direwctory

    private static String secondProf     = "";
    private static String curProfPath    = "";


    private static String sSoapHost      = "localhost";
    private static String sSoapPort      = "8880";

    private static String sCellName      = "bcell1";  // cellname

    static{
        Properties topologyProps = new Properties();
        try{
            FileInputStream fis = new FileInputStream( "ltps.props" );  // Should we do encoding here?
            topologyProps.load( fis );

            sLtpaHost           = (String)topologyProps.get( "LTPAHost");
            sLtpaPort           = (String)topologyProps.get( "LTPAPort");
            sLtpaUser           = (String)topologyProps.get( "LTPAUser");
            sLtpaPassword       = (String)topologyProps.get( "LTPAPassword");
            sLtpaDomain         = (String)topologyProps.get( "LTPADomain");

        } catch( Exception e ){
            e.printStackTrace( System.out);
        }
    }

    /**
     * This method executes the command for starting 
     * the WebSphere Application Server.
     *
     * @throws BuildException Any kind of BuildException
     */
    public void execute() throws BuildException {

        log("EnableLTPA has the following values: "
            + getLineSeparator()
            + "  ltpaHost     = '" + sLtpaHost     + "'"
            + getLineSeparator()
            + "  ltpaPort     = '" + sLtpaPort     + "'"
            + getLineSeparator()
            + "  ltpaUser     = '" + sLtpaUser     + "'"
            + getLineSeparator()
            + "  ltpaPassword = '" + sLtpaPassword + "'"
            + getLineSeparator()
            + "  ltpaDomain   = '" + sLtpaDomain   + "'"
            //,Project.MSG_VERBOSE);
            , Project.MSG_INFO);
         if( secondProf.length() > 0 ){
             sCellName = "WAS00Network";
             executeND();
         } else {
             sCellName =  "bcell1";
             executeBase();
         }
    }

    public void executeND() throws BuildException {
        log("executeND"
            , Project.MSG_INFO);

        try {

            log("update wsadmin.properties(soap port), ipc.client.props, sas.client.props and soap.client.props "
                , Project.MSG_INFO);
            // handle properties
            // ToDo: Need to handle the remote sas.client.props an soap.client.props
            updateProps( curProfPath );

            if( secondProf.length() > 0 ){
                updateProps( secondProf );
            }
            // update the FVT_HOME/src/xmls/properties.xml   userID and Password
            updatePropsXml( sFvtBaseDir );
            updateTopologyProps( sFvtBaseDir );

            if( isZos()){
                log("update SAF and appEnabled in security.xml"
                    , Project.MSG_INFO);
                // ToDo: Handle SAF and run wsc2n.sh to sync
                updateSAF_appE( curProfPath );
            }

        } catch(Exception e) {
        	throw new BuildException("Error Stopping WebSphere", e);
        }

    }

    public void executeBase() throws BuildException {
        log("executeBase"
            , Project.MSG_INFO);

        try {
            log("update sas.client.props an soap.client.props "
                , Project.MSG_INFO);
            // handle properties
            updateProps( curProfPath );

            // update the FVT_HOME/src/xmls/properties.xml   userID and Password
            updatePropsXml( sFvtBaseDir ); 

            if( isZos()){
                log("update SAF in security.xml"
                    , Project.MSG_INFO);
                updateSAF_appE( curProfPath );
            }

        } catch(Exception e) {
        	throw new BuildException("Error Stopping WebSphere", e);
        }

    }
    // private static String sFvtBaseDir    = "";  // Fvt Base Direwctory
    // 
    // private static String secondProf     = "";
    // private static String curProfPath    = "";

    public void setFvtBaseDir(String fvtBaseDir   ){
        this.sFvtBaseDir   = fvtBaseDir   ;
    }


    public void setCurProfPath  (String curProfPath  ){
        this.curProfPath  = curProfPath  ;
    }

    public void setSecondProfilePath(String profilePath   ){
        this.secondProf   = profilePath   ;
    }

    public void setLtpaHost     (String ltpaHost     ){
        this.sLtpaHost     = ltpaHost     ;
    }

    public void setLtpaPort     (String ltpaPort     ){
        this.sLtpaPort     = ltpaPort     ;
    }

    public void setLtpaUser     (String ltpaUser     ){
        this.sLtpaUser     = ltpaUser     ;
    }

    public void setLtpaPassword (String ltpaPassword ){
        this.sLtpaPassword = ltpaPassword ;
    }

    public void setLtpaDomain   (String ltpaDomain   ){
        this.sLtpaDomain   = ltpaDomain   ;
    }

    public void setSoapHost   (String soapHost   ){
        this.sSoapHost   = soapHost   ;
    }
    public void setSoapPort   (String soapPort   ){
        this.sSoapPort   = soapPort   ;
    }

    private static boolean isZos(){
        // ToDo: Need to handle remote Host situation
        String strOsName = (String)System.getProperty( "os.name" ).toLowerCase();
        boolean isZos = strOsName.indexOf("z/os"  ) > -1 ||
                        strOsName.indexOf("os/390") > -1;
        return isZos;
    }

    private String getBatchPostfix(){
        // ToDo: Need to handle remote Host situation
        String strOsName = (String)System.getProperty( "os.name" ).toLowerCase();
        boolean isZos = strOsName.indexOf("z/os"  ) > -1 ||
                        strOsName.indexOf("os/390") > -1;
        if( isZos ) return ".sh";
        if( strOsName.indexOf("win") > -1){
            return ".bat";
        }
        if( strOsName.indexOf("os/2") > -1 ){
            return ".cmd";
        }
        if( strOsName.indexOf("os/400") > -1 ){
            return ""; // no postfix in iSeries
        }

        return ".sh";
    }
        
    public String getLineSeparator(){
        return System.getProperty( "line.separator" );
    }

    public String getFileSeparator(){
        return System.getProperty( "file.separator" );
    }

    public void updateProps( String path ) {

        String strSas = path + getFileSeparator() + "properties" +
                               getFileSeparator() + "sas.client.props";
        String strSasOrg = strSas + ".org";
        backupOrgFile( strSas, strSasOrg );
        try{
            FileInputStream fisSas = new FileInputStream( strSasOrg );
            Properties props = new Properties();
            props.load( fisSas );
            fisSas.close();

            props.setProperty( "com.ibm.CORBA.loginSource", "properties" );
            props.setProperty( "com.ibm.CORBA.loginUserid", sLtpaUser );
            props.setProperty( "com.ibm.CORBA.loginPassword", sLtpaPassword );

            FileOutputStream fosSas = new FileOutputStream( strSas );
            PrintStream ps = new PrintStream( fosSas );
            writeProps( props, ps );
            ps.close();

        } catch( Exception e ){
            e.printStackTrace( System.out );
        }

        String strSoap = path + getFileSeparator() + "properties" +
                                getFileSeparator() + "soap.client.props";
        String strSoapOrg = strSoap + ".org";
        backupOrgFile( strSoap, strSoapOrg );
        try{
            FileInputStream fisSoap = new FileInputStream( strSoapOrg );
            Properties props = new Properties();
            props.load( fisSoap );
            fisSoap.close();

            props.setProperty( "com.ibm.SOAP.securityEnabled", "true" );
            props.setProperty( "com.ibm.SOAP.loginUserid", sLtpaUser );
            props.setProperty( "com.ibm.SOAP.loginPassword", sLtpaPassword );

            FileOutputStream fosSoap = new FileOutputStream( strSoap );
            PrintStream ps = new PrintStream( fosSoap );
            writeProps( props, ps );
            ps.close();

        } catch( Exception e ){
            e.printStackTrace( System.out );
        }

        String strWsadmin = path + getFileSeparator() + "properties" +
                                   getFileSeparator() + "wsadmin.properties";
        String strWsadminOrg = strWsadmin + ".org";
        backupOrgFile( strWsadmin, strWsadminOrg );
        try{
            FileInputStream fisWsadmin = new FileInputStream( strWsadminOrg );
            Properties props = new Properties();
            props.load( fisWsadmin );
            fisWsadmin.close();

            // com.ibm.ws.scripting.connectionType=SOAP
            // com.ibm.ws.scripting.port=8880
            // com.ibm.ws.scripting.host=localhost
            props.setProperty( "com.ibm.ws.scripting.connectionType", "SOAP"  );
            props.setProperty( "com.ibm.ws.scripting.port", sSoapPort );
            props.setProperty( "com.ibm.ws.scripting.host", sSoapHost );

            FileOutputStream fosWsadmin = new FileOutputStream( strWsadmin );
            PrintStream ps = new PrintStream( fosWsadmin );
            writeProps( props, ps );
            ps.close();

        } catch( Exception e ){
            e.printStackTrace( System.out );
        }


        String strIpc = path + getFileSeparator() + "properties" +
                               getFileSeparator() + "ipc.client.props";
        String strIpcOrg = strIpc + ".org";
        File fIpc = new File(strIpc);
        if( fIpc.exists() ){
            backupOrgFile( strIpc, strIpcOrg );
            try{
                FileInputStream fisIpc = new FileInputStream( strIpcOrg );
                Properties props = new Properties();
                props.load( fisIpc );
                fisIpc.close();

                // com.ibm.IPC.securityEnabled=false
                // com.ibm.IPC.loginUserid=
                // com.ibm.IPC.loginPassword=
                // com.ibm.IPC.loginSource=
                props.setProperty( "com.ibm.IPC.securityEnabled", "true" );
                props.setProperty( "com.ibm.IPC.loginUserid"    , sLtpaUser );
                props.setProperty( "com.ibm.IPC.loginPassword"  , sLtpaPassword );
                props.setProperty( "com.ibm.IPC.loginSource"    , "" );

                FileOutputStream fosIpc = new FileOutputStream( strIpc );
                PrintStream ps = new PrintStream( fosIpc );
                writeProps( props, ps );
                ps.close();

            } catch( Exception e ){
                e.printStackTrace( System.out );
            }
        }
    }

    public void updatePropsXml( String path ) { // FvtBaseDir

        String strXml = path + getFileSeparator() + "src" +
                               getFileSeparator() + "xmls" +
                               getFileSeparator() + "properties.xml";
        String strXmlOrg = strXml + ".org";
        backupOrgFile( strXml, strXmlOrg );

        try{
            FileInputStream  fisXml = new FileInputStream( strXmlOrg );
            BufferedReader   br      = new BufferedReader( new InputStreamReader( fisXml, "ASCII" ) );
            FileOutputStream fosXml  = new FileOutputStream( strXml );
            PrintStream      ps      = new PrintStream( fosXml );
            String strData = null;
            while( (strData = br.readLine()) != null ){
                // <property name="dist.security.user.name" value="j2ee"/>
                // <property name="dist.security.password"  value="wsj2ee1a"/>
                // <property name="zos.security.user.name" value="IBMUSER"/>
                // <property name="zos.security.password" value="IBMUSER"/>    
                // User ID
                strData = replaceValue( strData,
                                        "dist.security.user.name",
                                        "value=\"", "value=\"" + sLtpaUser+"\"" );
                strData = replaceValue( strData,
                                        "zos.security.user.name",
                                        "value=\"", "value=\"" + sLtpaUser+"\"" );
                strData = replaceValue( strData,
                                        "zos.security.password",
                                        "value=\"", "value=\"" + sLtpaPassword+"\"" );
                strData = replaceValue( strData,
                                        "dist.security.password",
                                        "value=\"", "value=\"" + sLtpaPassword+"\"" );
                ps.println( strData );
            }
            br.close();
            ps.close();

        } catch( Exception e ){
            e.printStackTrace( System.out );
        }

    }

    public void updateTopologyProps( String path ) { // FvtBaseDir

        String strXml = path + getFileSeparator() + "topologyProps.props";
        String strXmlOrg = strXml + ".orgz";
        File fXml = new File(strXml);
        System.out.println( "topologyProps.props:" + strXml );
        if( fXml.exists() ){
            backupOrgFile( strXml, strXmlOrg );
            try{
                FileInputStream fisXml = new FileInputStream( strXmlOrg );
                Properties props = new Properties();
                props.load( fisXml );
                fisXml.close();

                // was-cell-1.isSecurityEnabled:false
                // was-cell-1.WASUsername:
                // was-cell-1.WASPassword:
                props.setProperty( "was-cell-1.isSecurityEnabled", "true" );
                props.setProperty( "was-cell-1.WASUsername"    , sLtpaUser );
                props.setProperty( "was-cell-1.WASPassword"  , sLtpaPassword );

                FileOutputStream fosXml = new FileOutputStream( strXml );
                PrintStream ps = new PrintStream( fosXml );
                writePropsQ( props, ps );
                ps.close();

            } catch( Exception e ){
                e.printStackTrace( System.out );
            }
        }

    }

    public void writeProps( Properties props, PrintStream ps ){
        Enumeration enum1 = props.propertyNames();
        while( enum1.hasMoreElements()){
            String strKey   = (String)enum1.nextElement();
            String strValue = (String)props.get( strKey );
            if( strValue == null ) strValue = "";
            ps.println( strKey + "=" + strValue );
        }
        ps.close();
    }

    public void writePropsQ( Properties props, PrintStream ps ){
        Enumeration enum1 = props.propertyNames();
        while( enum1.hasMoreElements()){
            String strKey   = (String)enum1.nextElement();
            String strValue = (String)props.get( strKey );
            if( strValue == null ) strValue = "";
            ps.println( strKey + ":" + strValue );
        }
        ps.close();
    }


    public void updateSAF_appE( String path ) {
        String strSec = path + getFileSeparator() + "config" +
                               getFileSeparator() + "cells" + 
                               getFileSeparator() + sCellName +
                               getFileSeparator() + "security.xml";
        String strOrg = strSec + ".org";
        backupOrgFile( strSec, strOrg );

        try{
            FileInputStream  fisOrg  = new FileInputStream( strOrg );
            BufferedReader   br      = new BufferedReader( new InputStreamReader( fisOrg, "ASCII" ) );
            FileOutputStream fosSas  = new FileOutputStream( strSec );
            PrintStream      ps      = new PrintStream( fosSas );
            String strData = null;
            while( (strData = br.readLine()) != null ){
                // SAF
                strData = replace( strData,
                                   "com.ibm.security.SAF.authorization",
                                   "value=\"true\"", "value=\"false\"" );
                strData = replace( strData,
                                   "com.ibm.security.SAF.delegation",
                                   "value=\"true\"", "value=\"false\"" );
                // make sure appEnabled is enabled
                strData = replace( strData,
                                   "appEnabled=\"false\"",
                                   "appEnabled=\"false\"", "appEnabled=\"true\"" );
                ps.println( strData );
            }
            br.close();
            ps.close();
        } catch( Exception e ){
            e.printStackTrace( System.out );
        }

        //List<String> params = new ArrayList<String>();
        //params.add( "-X" );
        //execLocal( "../../../bin/wsc2n", params ); // /WebSPhere/Base/AppServer/profiles/default/bin/../../../bin/wsc2n .sh
    }

    public String replace( String strOrg, 
                           String strKey,
                           String strToken,
                           String strNew
                         ) {

        if( strOrg.indexOf( strKey ) >= 0 ){
            int iIndex = strOrg.indexOf( strToken );
            if( iIndex >= 0 ){
                return strOrg.substring( 0, iIndex ) +
                       strNew + 
                       strOrg.substring( iIndex + strToken.length() );
            }
        }
        return strOrg;
    }

    public String replaceValue( String strOrg, 
                                String strKey,
                                String strToken,
                                String strNew
                         ) {

        if( strOrg.indexOf( strKey ) >= 0 ){
            int iIndex = strOrg.indexOf( strToken );
            if( iIndex >= 0 ){
                // strToken -> "value = \"".   Look for the ending quote.
                int iEnd = strOrg.indexOf( "\"", iIndex + strToken.length() );
                return strOrg.substring( 0, iIndex ) +
                       strNew + 
                       strOrg.substring( iEnd + 1 );
            }
        }
        return strOrg;
    }

    public void backupOrgFile( String strFile, String strOrg){
        System.out.println( "backup " + strFile );
        System.out.println( "    to " + strOrg );
        File fOrg = new File( strOrg );
        if( !fOrg.exists() ){
            System.out.println( "    doing " + strOrg );
            try{
                execCopyFile( strFile, fOrg );
            } catch( Exception e ){
                e.printStackTrace( System.out );
            }
        }
    }

    public void execCopyFile( String strFrom, File fTo){
        File fFrom = new File( strFrom );
        try{
            FileInputStream  finp = new FileInputStream ( fFrom );
            FileOutputStream fout = new FileOutputStream( fTo  );
            int in = 0;
            while( (in = finp.read()) != -1 ){
                fout.write( in );
            }
            finp.close();
            fout.close();
        } catch( Exception e ){
            e.printStackTrace( System.out );
        }
    }

}
