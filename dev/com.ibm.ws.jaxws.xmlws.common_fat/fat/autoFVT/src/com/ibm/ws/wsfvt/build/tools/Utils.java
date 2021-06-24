/*
 * @(#) 1.7 autoFVT/src/com/ibm/ws/wsfvt/build/tools/Utils.java, WAS.websvcs.fvt, WASX.FVT, kk0918.33 1/9/09 12:36:35 [5/12/09 07:53:04]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 01/08/2004  ulbricht    D186806            z/OS needs encoding
 * 01/21/2004  scheu       LIDB2929.2         mixed content remover
 * 01/22/2004  scheu       LIDB2929.2.1       mixed content remover
 * 10/18/2006  ulbricht    397547             add staf result parsing method
 * 02/11/2007  ulbricht    420171             add test case count method
 * 06/05/2007  jramos      440922             add log parser methods
 * 12/01/2007  ulbricht    490228             add filter for system.err
 * 01/04/2008  ulbricht    490754             update pattern match
 * 01/15/2008  ulbricht    426904             change for unix platforms
 * 11/03/2008  jramos      559143             Incorporate Simplicity
 * 04/29/2010  btiffany    649945             Searchfile could miss some hits if >1 lines with hit.
 *
 */
package com.ibm.ws.wsfvt.build.tools;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

import java.lang.reflect.Method;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Server;
import common.utils.topology.IServerProcess;

/** 
 * This class is used for some common utilities that can be used
 * throughout the test suite.
 */
public class Utils {

    // default constructor
    public Utils() {
    }

    private static PrintToFile file = new PrintToFile();

    // utils function
    public static void dbg(Object o)
    {
        System.out.println(o);
    }

    public static void print(Process process) {
        try {
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(is, AppConst.DEFAULT_ENCODING));
            String str = null;
            while ((str = br.readLine()) != null) {
                dbg(str);
                file.printBuf(AppConst.logFile, str);
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
            file.printException(AppConst.errorFile, ioex);
        }
    }

    public static String printS(Process process) {
        String result = null;
        try {
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(is, AppConst.DEFAULT_ENCODING));
            String str = null;
            while ((str = br.readLine()) != null) {
                dbg(str);
                file.printBuf(AppConst.logFile, str);
                result = new String(str);
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
            file.printException(AppConst.errorFile, ioex);
        }
        return result;
    }

    public static void printError(Process process) {
        try {
            InputStream es = process.getErrorStream();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(es, AppConst.DEFAULT_ENCODING));
            String str = null;
            while ((str = br.readLine()) != null) {
                dbg(str);
                file.printBuf(AppConst.logFile, str);
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
            file.printException(AppConst.errorFile, ioex);
        }
    }

    public static void printProcess(Process proc, String baseDir) {
        try {
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), baseDir, "ERROR>");
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), baseDir, "");
    
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
    
            // any error???
            int exitVal = proc.waitFor();
            dbg("ExitValue: " + exitVal);
        } catch (Throwable t) {
            t.printStackTrace();
        //} catch (InterruptedException ie) {
        //    ie.printStackTrace();
        }
    }

    public static boolean asciiFileCopy(File From, File To) {
        boolean traceModified = false;
	String inputLine = "";

        try {
            FileInputStream fis = new FileInputStream(From);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(fis, AppConst.DEFAULT_ENCODING));

            FileOutputStream fos = new FileOutputStream(To);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            PrintWriter out = new PrintWriter(osw);

            while ((inputLine = in.readLine()) != null) {
		out.println(inputLine);
            }

            in.close();
            fis.close();

            out.close();
            osw.close();
            fos.close();

	    if ( To.canRead() ) {
		traceModified = true;
	    } else {
		traceModified = false;
	    }
        } catch (EOFException eof) {
            // Ok to reach the end of file
        } catch (Exception e) {
            e.printStackTrace();
        }

        return traceModified;
    }

    /**
     * removeMixedContent
     * brain dead code that can be used to remove mixed content
     * from xml messages.
     * @param input xml string
     * @return String with mixed content removed.
     */
    public static String removeMixedContent(String input) {
        String[] parts = input.split(">\\s*<");
        StringBuffer result = new StringBuffer();
        for (int i=0; i < parts.length; i++) {
            result.append(parts[i]);
            if ((i+1) < parts.length) {
                result.append("><");
            }
        }
        return result.toString();
    }

    /**
     * This is a method to parse the STAF result string to get
     * the data in the System.out or System.err that is returned
     * after executing a command in STAF.
     *
     * @param stafResult The result string from STAF
     * @return The System.out or System.err data from the STAF result
     */
    public static String parseSTAFResult(String type, String stafResult) {
        String returnData = null;
        String systemOut = "System.out: ";
        String systemErr = "System.err: ";
        int begIdx;
        int endIdx;
        if (type.equals("System.out")) {
            begIdx = stafResult.indexOf(systemOut) + systemOut.length();
            endIdx = stafResult.indexOf(systemErr);
            returnData = stafResult.substring(begIdx, endIdx);
        } else {
            begIdx = stafResult.indexOf(systemErr) + systemOut.length(); 
            returnData = stafResult.substring(begIdx);
        }
        returnData = returnData.trim();
        if (returnData.length() == 0) {
            returnData = null;
        }
        // filter some of the System.err data
        if ((returnData != null) && (type.equals("System.err"))) {
            String osName = System.getProperty("os.name");
            String endLine = null;
            if (osName.indexOf("Windows") != -1) {
                endLine = "\r\n";
            } else {
                endLine = "\n";
            }
            // Example
            // Dec 22, 2007 4:59:53 PM com.ibm.ffdc.impl.Ffdc set
            // INFO: FfdcProvider installed:com.ibm.ejs.ras.ffdc.FfdcProvider@6c576c57
            String str = "[A-Z]{1}[a-z]{2} [0-9]{1,2}+, [0-9]{4} [0-9]{1,2}+:[0-9]{1,2}+:[0-9]{1,2}+ [A-Z]{2} "
                           + "com.ibm.ffdc.impl.Ffdc set" + endLine + "INFO: FfdcProvider installed:com.ibm.ejs.ras.ffdc.FfdcProvider@[a-zA-Z|0-9]+";
            Pattern p = Pattern.compile(str);
            Matcher m = p.matcher(returnData);
            if (m.find()) {
                returnData = m.replaceAll("");
                if ((returnData.length() == 0) || (returnData.equals(""))) {
                    returnData = null;
                }
            }
        }
        return returnData; 
    }

    /**
     * This is a utility method to get the number of test methods
     * in a JUnit test case class.
     *
     * @param class The JUnit test case class
     * @return The number of methods starting with test
     * @throws Exception Any kind of exception
     */
    public static int getTestCaseNum(Class c) throws Exception {
        int count = 0;
        Method m[] = c.getDeclaredMethods();
        for (int i = 0; i < m.length; i++) {
            String methodName = m[i].getName();
            if (methodName.startsWith("test")) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * This is a method to read through a file searching for a statement.
     * 
     * @param fileName
     *            The file to search
     * @param searchString
     *            An array of Strings to search for in consecutive order
     * @param serverProcess
     *            The IServerProcess whose file should be searched
     * @return A boolean indicating if the search string was found
     */
    public static boolean searchFile(String fileName, String[] searchString, Server serverProcess) {
        return searchFile(fileName, searchString, serverProcess, null);
    }
    
    /**
     * This is a method to read through a file searching for a statement.
     * 
     * @param fileName
     *            The file to search
     * @param searchString
     *            An array of Strings to search for in consecutive order
     * @param serverProcess
     *            The Server whose file should be searched
     * @param hits - an arraylist to put the matching lines in, or null if not desired.
     * @return A boolean indicating if the search string was found
     * 
     * 
     * 
     */
    public static boolean searchFile(String fileName, String[] searchString, Server serverProcess, ArrayList<String> hits) {

        boolean searchStringFound = false;

        try {
            String myFileName = AppConst.FVT_BUILD_WORK_DIR + "/" + new File(fileName).getName();
            // we have to copy the file to the LOCALHOST machine
            RemoteFile src = null;
            RemoteFile dest = null;
            try {
                src = serverProcess.getNode().getMachine().getFile(fileName);
                dest = Machine.getLocalMachine().getFile(myFileName);
                src.copyToDest(dest);
            } catch (Exception e) {
                System.out.println("Error copying file: \"" + fileName
                        + "\"  Check " + AppConst.logFile + " for details.");
                e.printStackTrace();
                return false;
            }
            FileInputStream fis = new FileInputStream(myFileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis,
                    AppConst.DEFAULT_ENCODING));

            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(searchString[0]) > -1) {
                    searchStringFound = true;
                   if (hits!= null) {
                       hits.add(line);
                   } else {
                       // if not building list (hits==null), once we get the first match, we are done.
                       if(searchStringFound) {break;}
                   }
                   
                    // now while we have the line in the buffer, if there were other search stings,
                    // check for them too.
                    
                    // strange..
                    // if no more lines to read, set back to false, even if 
                    // we got a match on the first array element - bug or mysterious feature? 
                    for (int i = 1; i < searchString.length; i++) {
                        if ((line = br.readLine()) != null) {
                            if (line.indexOf(searchString[i]) > -1) {
                                searchStringFound = true;   
								if (hits!= null) {hits.add(line);}
                            }
                        } else {
                            searchStringFound = false;
                            break;
                        }
                    }
                }
            }

            br.close();
            fis.close();

            // now we have to copy it back
            try {
                src.copyFromSource(dest);
            } catch (Exception e) {
                System.out.println("Error copying file: \"" + myFileName
                        + "\"  Check " + AppConst.logFile + " for details.");
                e.printStackTrace();
                return false;
            }
        } catch (EOFException eof) {
            // It's ok to reach end of file
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return searchStringFound;

    }
    
    /**
     * This method searches through the trace log files for a string.
     * 
     * @param searchString
     *            An array of Strings to search for in consecutive order
     * @param serverProcess
     *            The IServerProcess whose file should be searched
     * @return A boolean indicating if the search string was found
     */
    public static boolean searchWebSphereTraceLog(String[] searchString, Server serverProcess) {
                return (searchLogsDir(searchString, "trace[\\S]*", serverProcess, null));
    }
    
    /**
     * This method searches through the trace log files for a string.
     * 
     * @param searchString
     *            An array of Strings to search for in consecutive order
     * @param serverProcess
     *            The IServerProcess whose file should be searched
     * @return A boolean indicating if the search string was found
     */
    public static boolean searchWebSphereTraceLog(String[] searchString, Server serverProcess,  ArrayList<String> hits) {
        return (searchLogsDir(searchString, "trace[\\S]*", serverProcess, hits));
    }

    /**
     * This method searchs through the SystemOut.log files for a string.
     * 
     * @param searchString
     *            An array of string to search for in consecutive order
     * @param serverProcess
     *            The IServerProcess whose file should be searched
     * @return True if the search string was found
     */
    public static boolean searchSystemOutLog(String[] searchString, Server serverProcess) {
        return (searchLogsDir(searchString, "SystemOut[\\S]*", serverProcess, null));
    }
    

    /**
     * This method searchs through the SystemOut.log files for a string.
     * 
     * @param searchString
     *            An array of string to search for in consecutive order
     * @param serverProcess
     *            The IServerProcess whose file should be searched
     * @return True if the search string was found
     */
    public static boolean searchSystemOutLog(String[] searchString, Server serverProcess, ArrayList<String> hits) {
        return (searchLogsDir(searchString, "SystemOut[\\S]*", serverProcess, hits));
    }

    /**
     * This method searchs through the SystemOut.log files for a string.
     * 
     * @param searchString
     *            An array of string to search for in consecutive order
     * @param serverProcess
     *            The IServerProcess whose file should be searched
     * @return True if the search string was found
     */
    @Deprecated
    public static boolean searchSystemOutLog(String[] searchString, IServerProcess serverProcess) {
        return (searchLogsDir(searchString, "SystemOut[\\S]*", serverProcess.getSimplicityServer(), null));
    }

    /**
     * Searches the file specified by the <code>namePattern</code> for a
     * specified string.
     * 
     * @param searchString
     *            An array of string to search for in consecutive order
     * @param namePattern
     *            The pattern for the name of the file(s)
     * @param serverProcess
     *            The IServerProcess whose logs should be searched
     * @param hits - an arraylist to put the matching lines in, or null if not desired.            
     * @return True if the search string was found
     */
    private static boolean searchLogsDir(String[] searchString, String namePattern, Server serverProcess, ArrayList<String> hits) {
        boolean searchStringFound = false;
        try {
            String searchDir = serverProcess.getNode().getProfileDir() + "/logs/"
                    + serverProcess.getName();
            RemoteFile[] listing = serverProcess.getNode().getMachine().getFile(searchDir).list(false);

            for (int i = 0; i < listing.length && !searchStringFound; i++) {
                if (listing[i].getName().matches(namePattern)) {
                    System.out.println(listing[i] + " matched pattern " + namePattern);
                    searchStringFound = searchFile(listing[i].getAbsolutePath(), searchString, serverProcess, hits);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return searchStringFound;
    }
}
