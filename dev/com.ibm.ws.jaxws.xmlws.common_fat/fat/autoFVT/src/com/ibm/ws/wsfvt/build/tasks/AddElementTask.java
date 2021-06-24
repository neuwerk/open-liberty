/*
 * @(#) 1.3 WautoFVT/src/com/ibm/ws/wsfvt/build/tasks/AddElementTask.java, WAS.websvcs.fvt, WSFP.WFVT 9/5/06 16:57:46 [5/21/07 13:18:41]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2004
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 01/08/2004  ulbricht    D186806            z/OS needs encoding
 * 08/17/2006  smithd      D381622            Add ND support
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
import com.ibm.ws.wsfvt.build.tools.utils.Operations;

/**
 * This class will add an element from one file into another.  For example,
 * a webservices-desc needs to added from one webservices.xml to another.
 *
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class AddElementTask extends Task {

    private String targetFileName;
    private String destFileName;
    private String element;

    /**
     * This method will create a temp file with the contents of the original
     * file plus the specified element from the target file.  The temp file
     * will then be renamed to the original file.
     */
    public void execute() throws BuildException {
        
        File targetFile = getFile(targetFileName);
        File destFile = getFile(destFileName);
        File tempFile = getFile(destFileName + ".temp");

        try {

            FileInputStream destFis = new FileInputStream(destFile);
            BufferedReader dest = new BufferedReader(
                new InputStreamReader(destFis, AppConst.DEFAULT_ENCODING));
            
            FileInputStream targetFis = new FileInputStream(targetFile);
            BufferedReader target = new BufferedReader(
                new InputStreamReader(targetFis, AppConst.DEFAULT_ENCODING));

            FileOutputStream tempFos = new FileOutputStream(tempFile);
            OutputStreamWriter osw = 
                new OutputStreamWriter(tempFos, AppConst.DEFAULT_ENCODING);
            PrintWriter temp = new PrintWriter(osw);

            boolean firstElementFound = false;
            String destLine = null;
            String targetLine = null;
            
            while ((destLine = dest.readLine()) != null) {
                if (!firstElementFound && destLine.indexOf("<" + element) > 0) {
                    firstElementFound = true;
                    while ((targetLine = target.readLine()) != null) {
                        if (targetLine.indexOf("<" + element) > 0) {
                            do {
                                temp.println(targetLine);
                                targetLine = target.readLine();
                            } while (targetLine.indexOf("</" + element + ">") < 0);
                            temp.println(targetLine);
                            break;
                        }
                    }
                }
                temp.println(destLine);
            }

            dest.close();
            destFis.close();

            target.close();
            targetFis.close();

            temp.close();
            osw.close();
            tempFos.close();

            destFile.delete();
            tempFile.renameTo(destFile);

        } catch (EOFException eof) {
            // It's ok to reach the end of file
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }

    }

    /**
     * This method returns a File object based off an input String.
     *
     * @param A String containing the the name of the file
     * @return A File object of the input String
     */
    private File getFile(String fileName) {
        return new File(Operations.escapeFileSeparator(fileName));
    }

    /**
     * This method accepts a string containing the name of the file
     * containing the element to copy into a new file.
     * 
     * @param File name with the element to copy to a new file
     */ 
    public void setTargetFileName(String _targetFileName) {
        targetFileName = _targetFileName;
    }

    /**
     * This method accepts a string containing the name of the file
     * containing the file to place the new element.
     * 
     * @param File name where the element will be placed
     */ 
    public void setDestFileName(String _destFileName) {
        destFileName = _destFileName;
    }

    /**
     * This method accepts a string containing the name of the element 
     * to be added.
     *
     * @param The element to be added to the destination file
     */
    public void setElement(String _element) {
        element = _element;
    }

}
