//
// @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tools/StreamGobbler.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:21 [8/8/12 06:56:44]
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 08/09/2004  ulbricht    D222747         New File
// 05/25/2007  jramos      440922          Changes for Pyxis
// 11/03/2008  jramos      550143          Incorporate Simplicity
//

package com.ibm.ws.wsfvt.build.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ibm.websphere.simplicity.Machine;

/**
 * This is a class used for reading streams and writing
 * them to a file.
 */
public class StreamGobbler extends Thread {

     InputStream is;
     String baseDir;
     String type;

     PrintToFile file = new PrintToFile();
                         
     /**
      * ctor
      *
      * @param is The input stream
      * @param basedir The dir of where to write output file
      * @param type A prefix added to the beginning of any output
      */
     StreamGobbler(InputStream is, String baseDir, String type) {
          this.is = is;
          this.baseDir = baseDir;
          this.type = type;
     }
                         
     /**
      * The run method does all the work for the thread.  This is
      * where the stream is read and written to a file.
      */
     public void run() {
        try {
            String encoding = Machine.getLocalMachine().getOperatingSystem().getDefaultEncoding();
            InputStreamReader isr = new InputStreamReader(is, encoding);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(type + line);
                file.printBuf(baseDir + File.separator + AppConst.logFile,
                              line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        } catch (Exception ee) {
            ee.printStackTrace();  
        }
    }

}

