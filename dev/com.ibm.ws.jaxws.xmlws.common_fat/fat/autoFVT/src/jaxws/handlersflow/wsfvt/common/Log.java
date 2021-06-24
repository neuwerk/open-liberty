//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId         Defect          Description
// ----------------------------------------------------------------------------
// 05/10/2007  mzheng         443868          New File
//

package jaxws.handlersflow.wsfvt.common;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.xml.soap.SOAPMessage;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * This class implements a simple log utility
 */
public class Log {
    private String _logfileName;
    
    private FileOutputStream _fos;
    private OutputStreamWriter _osw;
    private PrintWriter _pw;

    public Log(String logfileName) {
        _logfileName = logfileName;
    }
    
    public synchronized void setLogfileName(String logfileName) {
        if (logfileName != null) {
            _logfileName = logfileName;
        }
    }

    public String getLogfileName() {
        return _logfileName;
    }

    /**
     * This method writes the input string to the output file.
     *
     * @param A string containing a message to be logged.
     */
    public synchronized void log(String message) {
        try {
            openLog();
            _pw.print(message);
            closeLog();
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getName());
            e.printStackTrace();
        }
    }
    
    /**
     * This method writes the input SOAP message to the output file.
     *
     * @param A SOAP message to be logged.
     */
    public synchronized void log(SOAPMessage sm) {
        try {
            openLog();
            sm.writeTo(_fos);
            _pw.println("\n");
            closeLog();
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getName());
            e.printStackTrace();
        }
    }

    private void openLog() {
        try {
            _fos = new FileOutputStream(_logfileName, true);
            _osw = new OutputStreamWriter(_fos, AppConst.DEFAULT_ENCODING);
            _pw = new PrintWriter(_osw);
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getName());
            e.printStackTrace();
        }
    }
    
    private void closeLog() {
        try {
            _pw.close();
            _osw.close();
            _fos.close();
        } catch (Exception e) {
            System.err.println("Caught " + e.getClass().getName());
            e.printStackTrace();
        }
    }
}
