//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId       Defect          Description
// ----------------------------------------------------------------------------
// 05/10/2007  mzheng       443868          New File
// 03/07/08  mzheng         502861          Fix file format issue with z/OS
//

package jaxws.handlersflow.wsfvt.common;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.EOFException;
import java.io.InputStreamReader;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * This class defines all test constants 
 */
public class TestConstants {

    // Constants for request context property names 
    public final static String MESSAGE_PATH_PROPERTY = "HandleMsgPath";

    public final static String FAULT_PATH_PROPERTY = "HandleFaultPath";

    public final static String CLIENT_LOG_PROPERTY = "clientLog";

    public final static String SERVER_LOG_PROPERTY = "serverLog";

    // Constants for log file names
    public final static String CLIENT_LOGFILE = "handlersflow_client.log";

    public final static String SERVER_LOGFILE = "handlersflow_server.log";

    public final static String DEFAULT_LOGFILE = "handlersflow.log";

    // Constants for handler roles
    public final static String CLIENT_ROLE = "Client";

    public final static String SERVER_ROLE = "Server";

    // Constants for message directions
    public final static String OUTBOUND = "Outbound";

    public final static String INBOUND = "Inbound";

    // Constants for handleMessage() and handleFault() return status
    public final static String PATH_TRUE = "true";

    public final static String PATH_FALSE = "false";

    public final static String PATH_PROTOCOL_EXCEPTION = "ProtocolException";

    public final static String PATH_RUNTIME_EXCEPTION = "RuntimeException";

    // Constants for SOAP header
    public final static String SOAP_HEADER_ACTOR = "http://localhost.austin.ibm.com/jaxws-handlersflow";

    public final static String SOAP_HEADER_NAME = "PropHeader";

    public final static String SOAP_HEADER_NAMESPACE = "http://handlersflow.jaxws";

    public final static String SOAP_HEADER_PREFIX = "dummy_ns";

    public static String getLogFileContents(String logFilePath) {
        String inputLine = "";
        try {
            File logFile = new File(logFilePath);

            FileInputStream fis = new FileInputStream(logFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis, AppConst.DEFAULT_ENCODING));

            inputLine = in.readLine();
            in.close();
            fis.close();
        } catch (EOFException eof) {
            // Ok to reach the end of file
        } catch (Exception e) {
            System.err.println("Caught unexpected " + e.getClass().getName() + " in getLogFileContents()");
            e.printStackTrace();
        }
        return inputLine;
    }
}
