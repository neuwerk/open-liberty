/*
 * @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ApplicationXmlTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:00 [8/8/12 06:56:47]
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
 * 10/05/2003  ulbricht    D178846            New File
 * 11/10/2003  ulbricht    D182360            Change display name
 * 01/08/2004  ulbricht    D186806            z/OS needs encoding
 * 05/23/2007  jramos      440922             Change for Pyxis
 * 10/22/2007  jramos      476750             Use TopologyDefaults tool
 * 10/22/2008  jramos      559143             Incorporate Simplicity
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

import com.ibm.websphere.simplicity.Machine;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * This class will create an entire application xml file
 * or add in a war to the application xml.
 */
public class ApplicationXmlTask extends Task {

    private File applicationXmlFile;
    private String webUri;
    private String contextRoot;
    private boolean append;

    /**
     * This method will create or add the web application to
     * the application.xml file.
     *
     * @throws BuildException Any kind of build exception
     */
    public void execute() throws BuildException {

        if (applicationXmlFile == null) {
            throw new BuildException("applicationXmlFile must be specified.");
        }

        try {

            if (append && applicationXmlFile.exists()) {
                FileInputStream fis = new FileInputStream(applicationXmlFile);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(fis, AppConst.DEFAULT_ENCODING));

                File newFile = new File(applicationXmlFile.getPath() + ".tmp");
                FileOutputStream fos = new FileOutputStream(newFile);
                OutputStreamWriter osw 
                    = new OutputStreamWriter(fos, AppConst.DEFAULT_ENCODING);
                PrintWriter out = new PrintWriter(osw);

                String inputLine = "";
                boolean appFound = false;

                while ((inputLine = in.readLine()) != null) {
                    // Avoid duplicate entries in the application.xml
                    if (!appFound && inputLine.indexOf("<web-uri>"+webUri+"</web-uri>") != -1) {
                        appFound = true;
                        out.println(inputLine);
                    } else if (!appFound && inputLine.indexOf("</application>") != -1) {
                        out.println(createModuleElement());
                        out.println(inputLine);
                    } else {
                        out.println(inputLine);
                    }
                }

                in.close();
                fis.close();
                out.close();
                osw.close();
                fos.close();

                applicationXmlFile.delete();
                newFile.renameTo(applicationXmlFile);

            } else {
                FileOutputStream fos = new FileOutputStream(applicationXmlFile);
                OutputStreamWriter osw =
                     new OutputStreamWriter(fos, AppConst.DEFAULT_ENCODING);
                PrintWriter out = new PrintWriter(osw);
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println("<!DOCTYPE application PUBLIC \"-//Sun Microsystems, Inc.//DTD J2EE Application 1.3//EN\" \"http://java.sun.com/dtd/application_1_3.dtd\">");
                out.println();
                out.println("    <application id=\"Application_ID\">");
                out.println("      <display-name>AxisFvtImports</display-name>");
                out.println(createModuleElement());
                out.println("    </application>");
                out.close();
                osw.close();
                fos.close();
            }

        } catch (EOFException eof) {
            // It's ok to reach the end of file
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        } catch(Exception e) {
            throw new BuildException(e);
        }

    }

    /** 
     * This method will create the module element and its children.
     *
     * @return The module element and its children
     */
    private String createModuleElement() throws Exception {
        StringBuffer sb = new StringBuffer();
        Machine machine = TopologyDefaults.getDefaultAppServer().getNode().getMachine();
        
        sb.append("      <module id=\"WebModule_"
                  + System.currentTimeMillis() + "\">"
                  + machine.getOperatingSystem().getLineSeparator());
        sb.append("        <web>" + machine.getOperatingSystem().getLineSeparator());
        sb.append("          <web-uri>" + webUri + "</web-uri>"
                  + machine.getOperatingSystem().getLineSeparator());
        sb.append("          <context-root>" + contextRoot + "</context-root>"
                  + machine.getOperatingSystem().getLineSeparator());
        sb.append("        </web>" + machine.getOperatingSystem().getLineSeparator());
        sb.append("      </module>" + machine.getOperatingSystem().getLineSeparator());
        return sb.toString();
    }

    /**
     * This method will set the location of the application.xml
     * file to be added to or created.
     *
     * @param _applicationXmlFile The application xml file to 
     *                            be created or added to
     */
    public void setApplicationXmlFile(File _applicationXmlFile) {
        applicationXmlFile = _applicationXmlFile;
    }

    /** 
     * This method will allow the <context-root> to be set
     * for the web application in the application.xml.
     *
     * @param _contextRoot The user to be used for security
     */
    public void setContextRoot(String _contextRoot) {
        contextRoot = _contextRoot;
    }

    /**
     * This method will allow the <web-uri> to be set
     * for the web application in the application.xml.
     *
     * @param _webUri The password to be used for security
     */
    public void setWebUri(String _webUri) {
        webUri = _webUri;
    }

    /**
     * This method will set whether to append the application
     * to the existing application.xml file or to create
     * a new application.xml file with a single application.
     *
     * @param _append True if application is to be added
     *                to existing application.xml
     */
    public void setAppend(boolean _append) {
        append = _append;
    }

}
