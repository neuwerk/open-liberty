//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/FileUtils.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/24/07 10:59:03 [8/8/12 06:54:47]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 05/18/06 ulbricht    LIDB4401-28.01  New File
// 05/24/07 jramos      440922          Changes for Pyxis
//

package com.ibm.ws.wsfvt.build.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This file is for public static file type utility methods.
 */
public class FileUtils {

    /**
     * This method will copy a file.
     *
     * @param fromFile The file to be copied
     * @param toFile The file to be created
     * @throws FileNotFoundException If the file cannot be found
     * @throws IOException Any kind of I/O exception
     */
    public static void copyFile(String fromFile, String toFile) 
                                      throws FileNotFoundException,
                                             IOException {
        FileInputStream fis = new FileInputStream(fromFile);
        byte[] bytes = new byte[fis.available()];
        fis.close();

        FileOutputStream fos = new FileOutputStream(toFile);
        fos.write(bytes);
        fos.close();
    }

}
