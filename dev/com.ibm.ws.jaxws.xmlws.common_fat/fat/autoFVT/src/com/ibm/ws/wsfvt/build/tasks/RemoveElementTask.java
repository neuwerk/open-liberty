/*
 * 1.1, 5/22/07
 * 
 * COMPONENT_NAME: WAS.webservices.fvt
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2002, 2004
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *
 * Date        Feature/Defect       Author           Description
 * ----------  -----------------    ---------        ----------------------------
 * 12/03/2002  LIDB1738.26.2        ulbricht         Allow entire element stanza to be deleted
 * 03/11/2003  160775               ulbricht         Use File object as input arg to setFileName
 * 01/08/2004  D186806              ulbricht         z/OS needs encoding
 *        
 */
package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 *  The RemoveElementTask class is a custom task for removing
 *  an element from a file.  I wanted to use the replace task
 *  included with Ant, but it did not allow for replacing 
 *  any string which contained the '<' character.
 *
 *  The class conforms to the standards set in the Ant
 *  build framework for creating custom tasks.
 */
public class RemoveElementTask extends Task {

    private File fileName;
    private String element;

    /**
     * This method will create a temp file with the contents of the original
     * file without specified element.  The temp file will then be renamed
     * to the original file.
     *
     * @throws BuildException Any kind of File I/O type of exception
     */
    public void execute() throws BuildException {
        
        if (!fileName.exists()) {
            throw new BuildException("The file " + fileName.getPath() + " does not exist.");
        }

        File newFile = new File(fileName.getPath() + ".temp");

        try {

            FileInputStream fis = new FileInputStream(fileName);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(fis, AppConst.DEFAULT_ENCODING));

            FileOutputStream fos = new FileOutputStream(newFile);
            OutputStreamWriter osw =
                new OutputStreamWriter(fos, AppConst.DEFAULT_ENCODING);
            PrintWriter out = new PrintWriter(osw);

            String inputLine = "";
            boolean elementFound = false;
            
            while ((inputLine = in.readLine()) != null) {
                // Only remove the first element found.  You'll have to
                // run this task multiple times to remove more than one element!
                if (!elementFound && inputLine.indexOf("<" + element + ">") != -1) {
                    elementFound = true;
                    while (inputLine != null && inputLine.indexOf("</" + element + ">") < 0) {
                        inputLine = in.readLine();
                    }
                } else {
                    out.println(inputLine);
                }
            }

            in.close();
            fis.close();

            out.close();
            osw.close();
            fos.close();

            fileName.delete();
            newFile.renameTo(fileName);

        } catch (EOFException eof) {
            // It's ok to reach the end of file
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }

    }

    /**
     * This method accepts a string containing the name of the file
     * containing the element to remove.
     * 
     * @param fileName File with the element to remove
     */ 
    public void setFileName(File fileName) {
        this.fileName = fileName;
    }

    /**
     * This method accepts a string containing the name of the element 
     * to be removed.
     *
     * @param element The element to be removed from the file
     */
    public void setElement(String element) {
        this.element = element;
    }

}