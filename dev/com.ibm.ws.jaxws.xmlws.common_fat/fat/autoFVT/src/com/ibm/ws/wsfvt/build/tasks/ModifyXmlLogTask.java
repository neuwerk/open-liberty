//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ModifyXmlLogTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 4/2/07 21:53:17 [8/8/12 06:56:42]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 04/02/07 ulbricht    430026          New File
//

package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FilenameFilter;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import javax.xml.transform.stream.StreamResult;

import javax.xml.transform.dom.DOMSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

/**
 * This task was created because I would like the WS-Security
 * xml logs to contain the "bucket" name in the package name
 * so that it is easy to find the WS-Security tests on the
 * HTML report.
 */
public class ModifyXmlLogTask extends Task {

    private String toDir = null;
    private String fromDir = null;
    private static String pkgPrefix = null;

    /**
     * This method will perform the steps necessary to modify the xml logs
     * to the necessary format.
     *
     * @throws org.apache.tools.ant.BuildException Any kind of build exception
     */
    public void execute() throws BuildException {
        
        try {
            log("ModifyXmlLogTask has the following values: "
                + System.getProperty("line.separator")
                + "    fromDir             = " + fromDir
                + "    toDir               = " + toDir
                + System.getProperty("line.separator"),
                Project.MSG_VERBOSE);

            File logsDir = new File(fromDir);
            pkgPrefix = getTestBucketName(logsDir.getPath());
            File[] file = logsDir.listFiles(LogFileFilter.getInstance());

            for (int i = 0; file != null && i < file.length; i++) {
                String strFileName = file[i].getName();
                Document doc = readXmlFile(file[i]);
                if (doc != null) {
                    modifyClassName(doc, "testsuite", "name");
                    modifyClassName(doc, "testcase", "classname");
                    writeXmlFile(doc, toDir
                                      + File.separator
                                      + "TEST-" + pkgPrefix
                                      +  "."
                                      + strFileName.substring(5, strFileName.length()));
                }
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }

    }

    /**
     * This is a setter method for the fromDir attribute.
     *
     * @param _fromDir The directory from which the xml logs will be read
     */
    public void setFromDir(String _fromDir) {
        fromDir = _fromDir;
    }

    /**
     * This is a setter method for the toDir attribute.
     *
     * @param _toDir The directory of where the modified xml logs will be written
     */
    public void setToDir(String _toDir) {
        toDir = _toDir;
    }

    /**
     * This method wlil return the name that will be prefixed to
     * the existing package name.
     *
     * @fileName The directory name
     * @return The prefix name of package
     */
    private static String getTestBucketName(String fileName) {
        int idx = fileName.indexOf("logs");
        fileName = fileName.substring(0, idx - 1);
        File file = new File(fileName);
        return file.getName();
    }

    /**
     * This method will be used to change the attribute values containing
     * the original class name.  The original class name will be changed
     * to contain part of the directory name to help in identifying the
     * tests on the test report.
     *
     * @param doc The DOM Document to modify
     * @param elementName The element name to look for
     * @param attrName The attribute name to modify
     * Wthrows Exception Any kind of exception
     */
    private static void modifyClassName(Document doc, String elementName, String attrName) throws Exception {
        NodeList nl = doc.getElementsByTagName(elementName);
        if (nl != null) {
            for (int j = 0; j < nl.getLength(); j++) {
                Node node = nl.item(j);
                int type = node.getNodeType(); 
                if (type == Node.ELEMENT_NODE) {
                    NamedNodeMap attributeMap = node.getAttributes();
                    Node clsName = attributeMap.getNamedItem(attrName);
                    type = clsName.getNodeType();
                    if (type == Node.ATTRIBUTE_NODE) {
                        String val = clsName.getNodeValue();
                        val = pkgPrefix + "." + val;
                        clsName.setNodeValue(val);
                    }
                }
            }
        }
    }

    /**
     * This method will read the XML file and return
     * its contents.
     *
     * @param fileName The name of the XML file to read
     * @return A DOM Document with the contents of the XML file
     * @throws Exception Any kind of exception
     */
    private static Document readXmlFile(File fileName) throws Exception {
        InputStream is = new FileInputStream(fileName);
        DocumentBuilderFactory dbf = 
        DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(is);
    }

    /**
     * This method will write out the new XML file.
     *
     * @param doc The DOM document to be written to file
     * @param fileName The name of the new xml file
     * @throws Exception Any kind of exception
     */
    private static void writeXmlFile(Document doc, String fileName) throws Exception {
        Source source = new DOMSource(doc);
        File newFile = new File(fileName);
        Result result = new StreamResult(newFile);
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, result);
    }

    /**
     * This is a static inner class that will retrieve all the files
     * starting with "TEST-*" and ending with ".xml" from a directory.
     */
    public static class LogFileFilter implements FilenameFilter {

        private static LogFileFilter instance = new LogFileFilter();

        /**
         * Empty constructor
         */
        private LogFileFilter() {
        }

        /**
         * The constructor is private so only allow access to a LogFileFilter
         * through this getInstance method.
         *
         * @return A LogFileFilter object used to get a list of files
         */
        public static LogFileFilter getInstance() {
            return instance;
        }

        /**
         * This method is required for the FilenameFilter interface to determine
         * which files to retrieve.
         *
         * @param dir A directory
         * @param name A name
         * @return True if the file name starts with TEST- and ends with .xml
         */
        public boolean accept(File dir, String name) {
            return(name.startsWith("TEST-") && name.endsWith(".xml"));
        }

    } 

}