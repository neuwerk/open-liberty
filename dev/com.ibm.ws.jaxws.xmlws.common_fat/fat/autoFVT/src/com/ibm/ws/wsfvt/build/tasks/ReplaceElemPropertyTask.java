/*
 * 1.1, 12/14/07
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 12/10/2007 gkuo                            create
 * 06/23/08   syed          531842            Delete bindings.org file after replacing pattern. 
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.ws.wsfvt.build.tools.AppConst;

import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.Project;

/**
 *  The  replace will do two kinds of jobs
 *  If token is not set, then we do the first action otherwise the second one:
 *
 *  Fisrt: Replace the value in a XML element which format is
 *     <properties name="${propertyName}"  value="${value}"/>
 *
 *  The ANT taks will search the files in the filesets or the file  
 *  and looks for the ${propertyName} and replace trhe value with the value in below.
 *  Such as:  
 *     <replaceElemProp propertyName="com.ibm.wsspi.wssecurity.krbtoken.serviceSPN"
 *                      value="${server1KdcSPN}">
 *         <fileset dir="${FVT.base}/autoFVT/src/wssecfvt/kerberos/wssapi">
 *            <include name="%%/bindings.xml"/> 
 *         </fileset>
 *     </replaceElemProp>
 *
 *  The task will search all the bindings.xml under  
 *       ${FVT.base}/autoFVT/src/wssecfvt/kerberos/wssapi
 *  And replace all
 *     <properties name="com.ibm.wsspi.wssecurity.krbtoken.serviceSPN"  value="?!@#$%^"/>
 *  to
 *     <properties name="com.ibm.wsspi.wssecurity.krbtoken.serviceSPN"  value="${server1KdcSPN}"/>
 *   
 *
 * Second replace a token in a file to the value.
 * Such as:
 *     <replaceElemProp file="${FVT.base}/autoFVT/src/wssecfvt/kerberos/wssapi/krbwssapi/apisutil/WssecfvtAPIsConst.java"
 *                      token="TO_BE_REPLACED_KDC_SPN"
 *                      value="${server1KdcSPN}" />
 *
 *  The task will open file ${FVT.base}/autoFVT/src/wssecfvt/kerberos/wssapi/krbwssapi/apisutil/WssecfvtAPIsConst.java
 *  And replace the exact substring "TO_BE_REPLACED_KDC_SPN" to the value of ${server1KdcSPN}
 *
 */
public class ReplaceElemPropertyTask extends Task {

    private Path buildpath  = null;
    private String fileName = null;
    private String token    = null;

    private String propertyName;
    private String value;
    private String propertyName2;
    private String value2;
    private String propertyName3;
    private String value3;

    /**
     */
    public void execute() throws BuildException {
        if (buildpath == null && fileName == null) {
            System.out.println("WARNING: Build Error!!! No xml files specified");
            log("WARNING: Build Error!!! No xml files specified", Project.MSG_WARN);
            // throw new BuildException("No xml files specified");
        } 
        
        String[] filenames = null;
        if( buildpath != null ){
            filenames = buildpath.list();
        }
        int count = (filenames == null ? 0 : filenames.length );

        if( fileName != null ){ 
            String[] ofn = filenames;
            if( ofn == null ) ofn = new String[0];
            filenames = new String[ count + 1 ];
            //System.arrayCopy( ofn, 0, filenames, 0, count ++ );
            for( int iI = 0; iI < count; iI ++ ){
                filenames[ iI ] = ofn[ iI ];
            }
            filenames[ count ++ ] = fileName;
        }

        if (count < 1) {
            log("No files to iterate on", Project.MSG_WARN);
            return;
        }

        // Set null propertyName to impossible string
        if( propertyName  == null ){propertyName  = "!@#$%^&*";}
        if( propertyName2 == null ){propertyName2 = "!@#$%^&*";}
        if( propertyName3 == null ){propertyName3 = "!@#$%^&*";}

        if( token != null ){
            // token = "${" + token + "}";
            for( int iI = 0; iI < count; iI ++ ){
                executeToken( filenames[ iI ] );
            }
        } else {
            for( int iI = 0; iI < count; iI ++ ){
                executeFile( filenames[ iI ] );
            }
        }
    }

    /**
     * See description of the class
     */
    public void executeFile( String strFile) throws BuildException {

        System.out.println("changing property in " + strFile);
        try{
            File file = new File( strFile ); 
            if (!file.exists()) {
                throw new BuildException(file.getPath() + "does not exist.");
            }

            File orgFile = new File(file.getPath() + ".org");
            backupOrgFile( file.getAbsolutePath(), orgFile.getAbsolutePath() );
            File delOrig = new File(orgFile.getAbsolutePath());

            FileInputStream fis = new FileInputStream(orgFile);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(fis, AppConst.DEFAULT_ENCODING));

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw =
                 new OutputStreamWriter(fos, AppConst.DEFAULT_ENCODING);
            PrintWriter pw = new PrintWriter(osw);

            boolean tokenFound = false;
            String  strProperty = null;
            String  strValue = null;
            String line = null;

            while ((line = br.readLine()) != null) {
                if( !tokenFound ) {
                    if( line.indexOf( propertyName ) > -1 ){
                        tokenFound  = true;
                        strProperty = propertyName;
                        strValue    = value;
                    }else if( line.indexOf( propertyName2 ) > -1 ){
                        tokenFound = true;
                        strProperty = propertyName2;
                        strValue   = value2;
                    }else if( line.indexOf( propertyName3 ) > -1 ){
                        tokenFound = true;
                        strProperty = propertyName3;
                        strValue   = value3;
                    }
                    if (tokenFound ){
                        int index = line.indexOf( "<" );
                        String strSub = line.substring(0, index) + "<properties name=\"";
                        if( line.indexOf( "/>" ) > -1 ){
                            pw.println( strSub + strProperty +
                                        "\" value=\"" + strValue + "\"/>" );
                            tokenFound = false;
                        } else {
                            pw.println( strSub + strProperty +
                                        "\"" );
                        }
                    } else{
                        if (line != null) {
                            pw.println(line);
                        }
                    }
                } else {
                    if (tokenFound && line.indexOf( "/>" ) > -1 ){
                        pw.println( "value=\"" + strValue + "\" />" );
                        tokenFound = false;
                    } else{     
                    }
                }
            }

            br.close();
            fis.close();

            pw.close();
            osw.close();
            fos.close();
            // delete orig file
            delOrig.delete();
        } catch (EOFException eof) {
            // It's ok to reach the end of file
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }

    }

    /**
     * See description of the class
     */
    public void executeToken( String strFile) throws BuildException {

        System.out.println("changing property in " + strFile);
        try{
            File file = new File( strFile ); 
            if (!file.exists()) {
                throw new BuildException(file.getPath() + "does not exist.");
            }

            File orgFile = new File(file.getPath() + ".org");
            backupOrgFile( file.getAbsolutePath(), orgFile.getAbsolutePath() );
            File delOrig = new File(orgFile.getAbsolutePath());

            FileInputStream fis = new FileInputStream(orgFile);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(fis, AppConst.DEFAULT_ENCODING));

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw =
                 new OutputStreamWriter(fos, AppConst.DEFAULT_ENCODING);
            PrintWriter pw = new PrintWriter(osw);

            String line = null;

            while ((line = br.readLine()) != null) {
                if( line.indexOf( token ) > -1 ){
                    line = replace( line, token, token, value );
                }
                pw.println(line);
            }

            br.close();
            fis.close();

            pw.close();
            osw.close();
            fos.close();
            // delete orig file
            delOrig.delete();
           
        } catch (EOFException eof) {
            // It's ok to reach the end of file
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }

    }

    /**
     * See description of the class
     */
    public void setPropertyName(String propertyName ) {
        this.propertyName = propertyName;
    }

    /**
     * See description of the class
     */
    public void setValue(String _value) {
        value = _value;
    }

    /**
     * See description of the class
     */
    public void setPropertyName2(String propertyName ) {
        this.propertyName2 = propertyName;
    }

    /**
     * See description of the class
     */
    public void setValue2(String _value) {
        value2 = _value;
    }

    /**
     * See description of the class
     */
    public void setPropertyName3(String propertyName ) {
        this.propertyName3 = propertyName;
    }

    /**
     * See description of the class
     */
    public void setValue3(String _value) {
        value3 = _value;
    }


    /**
     * See description of the class
     */
    public void setFile(String _file) {
        fileName = _file;
    }

    /**
     * See description of the class
     */
    public void setToken(String _token) {
        token = _token;
    }

    public void backupOrgFile( String strFile, String strOrg){
        File fOrg = new File( strOrg );
        if( !fOrg.exists() ){
            System.out.println( "backup " + strFile );
            System.out.println( "    to " + strOrg );
            System.out.println( "    doing " + strOrg );
            try{
                execCopyFile( strFile, fOrg );
            } catch( Exception e ){
                e.printStackTrace( System.out );
            }
        } else {
            System.out.println( strOrg + " exists" );
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

    /**
     * Adds a directory set to the implicit build path.
     * <p>
     * <em>Note that the directories will be added to the build path
     * in no particular order, so if order is significant, one should
     * use a file list instead!</em>
     *
     * @param  set the directory set to add.
     */
    public void addDirset(DirSet set) {
        getBuildpath().addDirset(set);
    }

    /**
     * Adds a file set to the implicit build path.
     * <p>
     * <em>Note that the directories will be added to the build path
     * in no particular order, so if order is significant, one should
     * use a file list instead!</em>
     *
     * @param  set the file set to add.
     */
    public void addFileset(FileSet set) {
        getBuildpath().addFileset(set);
    }

    /**
     * Adds an ordered file list to the implicit build path.
     * <p>
     * <em>Note that contrary to file and directory sets, file lists
     * can reference non-existent files or directories!</em>
     *
     * @param  list the file list to add.
     */
    public void addFilelist(FileList list) {
        getBuildpath().addFilelist(list);
    }

    /**
     * Set the buildpath to be used to find sub-projects.
     *
     * @param  s an Ant Path object containing the buildpath.
     */
    public void setBuildpath(Path s) {
        getBuildpath().append(s);
    }
    
    /**
     * Creates a nested build path, and add it to the implicit build path.
     *
     * @return the newly created nested build path.
     */
    public Path createBuildpath() {
        return getBuildpath().createPath();
    }

    /**
     * Creates a nested <code>&lt;buildpathelement&gt;</code>,
     * and add it to the implicit build path.
     *
     * @return the newly created nested build path element.
     */
    public Path.PathElement createBuildpathElement() {
        return getBuildpath().createPathElement();
    }


    /**
     * Gets the implicit build path, creating it if <code>null</code>.
     *
     * @return the implicit build path.
     */
    private Path getBuildpath() {
        if (buildpath == null) {
            buildpath = new Path(getProject());
        }
        return buildpath;
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

}
