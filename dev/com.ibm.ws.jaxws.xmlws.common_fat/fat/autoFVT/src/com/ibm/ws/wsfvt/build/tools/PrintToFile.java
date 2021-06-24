/*
 * @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tools/PrintToFile.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/22/07 07:27:12 [8/8/12 06:56:48]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2004
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 01/08/2004  ulbricht    D186806            z/OS needs encoding
 *
 */
package com.ibm.ws.wsfvt.build.tools;

import java.io.*;
import java.util.*;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/*
** This class printToFile is to create a handle for printing purpose, the test driver program will need to
** create an instance of this class and pass in the filename and the string to write to the output file
** Or the filename and the stack trace exception message to the output file. It can be write to many files
*/

public class PrintToFile {

    // constructor
    public PrintToFile() {
    }

    // print buffer string to the filename
    public void printBuf(String fn, String buf) {
        try {
            boolean appendToFile = true;
            FileOutputStream outfile = new FileOutputStream(fn, appendToFile);
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(outfile, AppConst.DEFAULT_ENCODING));
            bw.flush();
            bw.write(buf);
            bw.newLine();
            //bw.newLine();
            bw.close();
            outfile.close();
        } catch (IOException io) {
            System.err.println("\nIOException: printToFile class: printBuf()");
            io.printStackTrace();
        }
    }

    // print exception message to the filename 
    public void printException(String fn, Exception ex) {
        try {
            boolean appendToFile = true;
            OutputStream outfile = new FileOutputStream(fn, appendToFile);
            OutputStreamWriter osw = 
                new OutputStreamWriter(outfile, AppConst.DEFAULT_ENCODING);
            PrintWriter pw = new PrintWriter(osw, true);
            ex.printStackTrace(pw);
            pw.println();
            pw.close();
            osw.close();
            outfile.close();
        } catch (IOException io) {
            System.err.println("\nIOException: printToFile class: printException()");
            io.printStackTrace();
        }
    }

    // print new line to the filename
    public void printNewLine(String fn) {
        try {
            boolean appendToFile = true;
            FileOutputStream outfile = new FileOutputStream(fn, appendToFile);
            BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(outfile, AppConst.DEFAULT_ENCODING));
            bw.flush();
            bw.newLine();
            bw.close();
            outfile.close();
        } catch (IOException io) {
            System.err.println("\nIOException: printToFile class: printNewLine()");
            io.printStackTrace();
        }

    }

} //end printToFile class
