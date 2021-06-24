/*
 * @(#) 1.1 autoFVT/src/common/utils/operations/applicationManagement/installAppParam/MapModulesToServers.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/11/08 10:56:16 [8/8/12 06:41:12]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 */

package common.utils.operations.applicationManagement.installAppParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.websphere.simplicity.Machine;
import common.utils.topology.IServerProcess;

/**
 * The MapModulesToServers class models the properties needed when issuing the -MapModulesToServers option through the
 * wsadmin command.
 */
public class MapModulesToServers extends Module {
	private String server = "";

	/**
	 * Default empty constructor.
	 */
	public MapModulesToServers() {
	}

	/**
	 * Constructor used for initializing the entire MapModulesToServers object.
	 * 
	 * @param moduleName
	 *            The name of this module to map
	 * @param uri
	 *            The URI of the module
	 * @param server
	 *            The server to map this module to, Example value:
	 *            WebSphere:cell=BaseApplicationServerCell,node=IBM-RDZU2YPGE30,server=server1
	 * 
	 */
	public MapModulesToServers(String moduleName, String uri, String server) {
		super(moduleName, uri);
		this.server = server;
	}

	/**
	 * This method is a setter for the server attribute.
	 * 
	 * Example value: WebSphere:cell=BaseApplicationServerCell,node=IBM-RDZU2YPGE30,server=server1
	 * 
	 * @param server
	 *            A String containing the server name
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * This method is a getter for the server attribute.
	 * 
	 * @return A String containing the server name
	 */
	public String getServer() {
		return this.server;
	}

	/**
	 * The getMapModulesToServersContents method formats the properties of the MapModulesToServers class into a form
	 * necessary for issuing commands through the wsadmin command (in jython).
	 * 
	 * @return A String with the format necessary for issuing the -MapModulesToServers command through the wsadmin
	 *         command (in jython).
	 */
	public String getMapModulesToServersContents() {
		return "[[" + moduleName + "] [" + uri + "] [" + server + "]]";
	}

	/**
	 * This method will display the contents of the MapModulesToServers class in a friendly format.
	 * 
	 * @return A String containing the contents of the MapModulesToServers class in a friendly format.
	 */
	public String toString() {
		return getMapModulesToServersContents();
	}

	/**
	 * Returns a MapModulesToServers display string, based off the module(s) found in the <code>earFile</code>. The
	 * code will attempt to find the module display name, but if not, use the module name. It will create a default URI
	 * string based on the module name. And it will use the <code>IServerProcess</code>' deployment target for the
	 * server mapping.
	 * 
	 * @param earFile
	 *            The ear file to install
	 * @param serverProcess
	 *            The <code>IServerProcess</code> to map the modules to
	 * @return The map modules to servers string
	 */
	public static String createMapModulesToServersString(String earFile, IServerProcess serverProcess) {
		String mmtsString = "";
		List<String> modules = null;

		try {
			ZipFile file = new ZipFile(earFile);
			ZipEntry entry = file.getEntry("META-INF/application.xml");
			InputStream is = file.getInputStream(entry);
			Document doc = getDocument(is);
			if (doc != null) {
				modules = getModules(doc, new String[]{"ejb", "web-uri"});
			} else {
				// Couldn't parse application.xml, return null
				return null;
			}

			Map<String, List<IServerProcess>> moduleMap = new HashMap<String, List<IServerProcess>>();
			List<IServerProcess> serverProcesses = new ArrayList<IServerProcess>();
			serverProcesses.add(serverProcess);
			for (String module : modules) {
				moduleMap.put(module, serverProcesses);
			}
			mmtsString = createMapModulesToServersString(earFile, moduleMap);

		} catch (Exception e) {
		}

		return mmtsString;
	}

	/**
	 * Returns a MapModulesToServers display string, based off each module:serverProcess pair. There can be multiple
	 * <code>IServerProcess</code>' for each module. The code will attempt to find the module display name, but if
	 * not, use the module name. It will create a default URI string based on the module name. And it will use each
	 * <code>IServerProcess</code>' deployment target for the server mapping.
	 * 
	 * @param earFile
	 *            The ear file to install.
	 * @param moduleMap
	 *            A <code>Map</code> of module names paired with the (possibly multiple) <code>IServerProcess</code>('s)
	 *            to map it to.
	 * @return The map modules to servers string
	 */
	public static String createMapModulesToServersString(String earFile, Map<String, List<IServerProcess>> moduleMap) {

		String mmtsString = "[";
		try {

			MapModulesToServers mmts = null;
			for (String moduleName : moduleMap.keySet()) {
				String displayName = getModuleDisplayName(earFile, moduleName);
				String uri = moduleName + ","
						+ (moduleName.endsWith(".war") ? "WEB-INF/web.xml" : "META-INF/ejb-jar.xml");
				List<IServerProcess> serverProcesses = moduleMap.get(moduleName);
				String targetServer = null;
				for (IServerProcess serverProcess : serverProcesses) {
					if (targetServer != null) {
						targetServer = targetServer + "+" + serverProcess.getDeploymentTarget();
					} else {
						targetServer = serverProcess.getDeploymentTarget();
					}
				}
				mmts = new MapModulesToServers(displayName, uri, targetServer);
				mmtsString = mmtsString + mmts.getMapModulesToServersContents();
			}
		} catch (Exception e) {
		}

		return mmtsString + "]";
	}

	/**
	 * This method will take an InputStream and return a Document object so that DOM APIs can be used to read the XML
	 * file.
	 * 
	 * @param is
	 *            The file to be parsed
	 * @return A Document object that can be traversed with DOM
	 * @throws Exception
	 *             Any kind of exception
	 */
	private static Document getDocument(InputStream is) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(is);
		return doc;
	}

	/**
	 * This method will find all the modules in the ear that are to be installed.
	 * 
	 * @param doc
	 *            The DOM element document
	 * @param elementNames
	 *            The elements to be searched for
	 * @return A List of the modules (jars and wars)
	 */
	private static List<String> getModules(Document doc, String[] elementNames) {
		List<String> modules = new ArrayList<String>();
		for (int i = 0; i < elementNames.length; i++) {
			NodeList nl = doc.getElementsByTagName(elementNames[i]);
			if (nl != null) {
				for (int j = 0; j < nl.getLength(); j++) {
					Node node = nl.item(j);
					int type = node.getNodeType();
					if (type == Node.ELEMENT_NODE) {
						Node firstChild = node.getFirstChild();
						type = firstChild.getNodeType();
						if (type == Node.TEXT_NODE) {
							modules.add(firstChild.getNodeValue());
						}
					}
				}
			}
		}
		return modules;
	}

	/**
	 * This method will get the display name from the module.
	 * 
	 * @param fileName
	 *            The file name to look for display name in
	 * @param module
	 *            The module in the ear file or war name
	 * @return The display name of the module
	 */
	private static String getModuleDisplayName(String fileName, String module) {
		String displayName = null;
		String workDir = null;
		ZipFile file = null;
		ZipEntry entry = null;
		InputStream is = null;
		try {
            workDir = Machine.getLocalMachine().getTempDir().getAbsolutePath();
			boolean isWar = module.endsWith(".war");
			String dd = isWar ? "WEB-INF/web.xml" : "META-INF/ejb-jar.xml";
			String moduleType = isWar ? "web-app" : "ejb-jar";

			String moduleFile = null;
			if (!fileName.equals(module)) {
				getFileFromZipFile(fileName, module, workDir);
				moduleFile = workDir + "/" + module;
			} else {
				moduleFile = module;
			}

			file = new ZipFile(moduleFile);
			entry = file.getEntry(dd);
			is = file.getInputStream(entry);

			Document doc = getDocument(is);

			NodeList nl = doc.getElementsByTagName("display-name");
			if (nl != null) {
				for (int j = 0; j < nl.getLength(); j++) {
					Node node = nl.item(j);
					Node parent = node.getParentNode();
					int type = parent.getNodeType();
					if (type == Node.ELEMENT_NODE) {
						String nodeName = parent.getNodeName();
						if (nodeName.equals(moduleType)) {
							Node firstChild = node.getFirstChild();
							type = firstChild.getNodeType();
							if (type == Node.TEXT_NODE) {
								displayName = firstChild.getNodeValue();
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// do nothing
				}
			}
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					// do nothing
				}
			}
			File delFile = new File(workDir + "/" + module);
			if (delFile.exists()) {
				delFile.delete();
			}
		}
		if (displayName == null) {
			displayName = module;
		}
		return displayName;
	}

	/**
	 * This method will retrieve a file from a zip file. It does not have to deal with encoding because we are writing
	 * bytes.
	 * 
	 * @param outerFile
	 *            The zip file that contains the desired file
	 * @param innerFile
	 *            The file to be retrieved from the zip file
	 * @param destDir
	 *            The directory where the innerFile will be written
	 * @throws IOException
	 *             Any kind of IO Exception
	 * @throws FileNotFoundException
	 *             If the file is not found
	 * @throws ZipException
	 *             Any kind of zip exception
	 */
	private static void getFileFromZipFile(String outerFile, String innerFile, String destDir) throws IOException,
			FileNotFoundException, ZipException {

		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		ZipInputStream zis = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(new File(outerFile));
			zis = new ZipInputStream(fis);
			ZipEntry zipEntry = null;
			zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (!zipEntry.toString().equals(innerFile)) {
					zipEntry = zis.getNextEntry();
					continue;
				}
				// if we're here, we've found the zipEntry
				fos = new FileOutputStream(destDir + "/" + innerFile);

				int n;
				byte[] buf = new byte[1024];
				while ((n = zis.read(buf, 0, 1024)) > -1)
					fos.write(buf, 0, n);

				fos.close();
				zis.closeEntry();
				// we're done, let's break out of this while loop
				break;
			}
		} catch (Exception e) {
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (zos != null) {
					zos.close();
				}
				if (zis != null) {
					zis.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				// let it go!
			}
		}
	}
}
