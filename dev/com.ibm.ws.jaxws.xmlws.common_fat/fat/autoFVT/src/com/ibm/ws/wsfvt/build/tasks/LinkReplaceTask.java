/*
 * @(#) 1.5 WautoFVT/src/com/ibm/ws/wsfvt/build/tasks/LinkReplaceTask.java, WAS.websvcs.fvt, WSFP.WFVT 9/5/06 16:57:55 [5/21/07 13:18:45]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 04/30/2003  ulbricht    D163619            New File
 * 06/30/2003  ulbricht    D170881            Not finding correct localPart
 * 01/08/2004  ulbricht    D186806            z/OS needs encoding
 * 03/26/2006  ulbricht    D357187            Fine tune indexOf search
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

/**
 * This class will replace the servlet-link or ejb-link in the webservices.xml
 * file.
 *
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class LinkReplaceTask extends Task {

    private File file;
    private String localPart;
    private String token;
    private String value;

    /**
     * This method will create a temp file with the contents of the original
     * file plus the specified token replaced with the value.  The temp file
     * will then be renamed to the original file.
     */
    public void execute() throws BuildException {
        
        if (!file.exists()) {
            throw new BuildException(file.getPath() + "does not exist.");
        }

        File tempFile = new File(file.getPath() + ".temp");

        try {

            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(fis, AppConst.DEFAULT_ENCODING));
            
            FileOutputStream fos = new FileOutputStream(tempFile);
            OutputStreamWriter osw =
                 new OutputStreamWriter(fos, AppConst.DEFAULT_ENCODING);
            PrintWriter pw = new PrintWriter(osw);

            boolean tokenNotFound = true;
            String line = null;
            
            while ((line = br.readLine()) != null) {
                if (tokenNotFound && line.indexOf("<port-component-name>"
                                                  + localPart
                                                  + "</port-component-name>") != -1) {
                    pw.println(line);
                    while ((line = br.readLine()) != null) {
                       int startIndex = line.indexOf(token);
                       if (startIndex != -1) {
                           tokenNotFound = false;
                           int lastIndex = line.lastIndexOf("<");
                           pw.println(line.substring(0, startIndex)
                                      + value
                                      + line.substring(lastIndex, line.length()));
                           line = br.readLine();
                           break;
                       } else {
                           pw.println(line);
                       }
                    }
                }
                if (line != null) {
                    pw.println(line);
                }
            }

            br.close();
            fis.close();

            pw.close();
            osw.close();
            fos.close();

            file.delete();
            tempFile.renameTo(file);

        } catch (EOFException eof) {
            // It's ok to reach the end of file
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }

    }

    /**
     * This method accepts a string that should be
     * searched for and replaced. 
     *
     * @param _token The string to be replaced
     */
    public void setToken(String _token) {
        token = _token;
    }

    /**
     * This method accepts a string that should be
     * string that will replace the token. 
     *
     * @param _token The string that will replace
     *               the token
     */
    public void setValue(String _value) {
        value = _value;
    }

    /**
     * This method accepts a string containing the localpart 
     * for the port.
     *
     * @param _localPart The localpart to search for
     */
    public void setLocalpart(String _localPart) {
        localPart = _localPart;
    }

    /**
     * This method accepts a file that needs the replace of
     * the servlet-link and ejb-link.
     *
     * @param _file The file that requires the replace
     */
    public void setFile(File _file) {
        file = _file;
    }

}
