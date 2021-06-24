/*
 * @(#) 1.3 WautoFVT/src/com/ibm/ws/wsfvt/build/tasks/AppXml.java, WAS.websvcs.fvt, WSFP.WFVT 1/9/04 15:12:08 [10/31/06 08:53:30]
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
 * 11/14/2006  sedov       D404343            New File
 * 04/16/2008  sedov       D513284            Update for jee 5
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A generic application.xml file writer for ANT. Supports application.xml
 * versioning
 * 
 */
public class AppXml extends Task {

	private static int moduleDefaultId = 0;

	private String id = "Application_ID";

	private String displayName = null;

	private String version = "current";

	private String file = "application.xml";

	private List<WebModule> webModules = new LinkedList<WebModule>();

	private List<EjbModule> ejbModules = new LinkedList<EjbModule>();

	private List<JavaModule> javaModules = new LinkedList<JavaModule>();

	public void execute() {

		Document doc = null;
		try {
			// Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
		} catch (Exception e) {
			throw new BuildException("Unable to create DOMFactory", e);
		}

		// create a document based on a specific version
		if ("current".equals(version) || "5".equals(version)) {
			createAppXml(doc, 5);
		} else if ("1.4".equals(version)) {
			createAppXml(doc, 4);
		} else {
			throw new BuildException("Unexpected version specified: "
					+ this.version);
		}

		try {
			// use specific Xerces class to write DOM-data to a file:
			XMLSerializer serializer = new XMLSerializer();
			serializer.setOutputCharStream(new java.io.FileWriter(this.file));
			serializer.serialize(doc);
		} catch (Exception e) {
			throw new BuildException("Unable to write '" + this.file + "'", e);
		}

	}

	/**
	 * Create a application.xml
	 */
	private void createAppXml(Document doc, int level) {

		Element root = doc.createElement("application");
		doc.appendChild(root);

		if (level == 5) {
			//jee5
			// <application id="Application_ID" version="5" xmlns="http://java.sun.com/xml/ns/javaee"
			// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			// xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_5.xsd">
			root.setAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
			root.setAttribute("id", this.id);
			root.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			root.setAttribute("version", "5");
			root.setAttribute("xsi:schemaLocation",
							"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_5.xsd");
		} else {
			// j2ee 1.4
			root.setAttribute("xmlns", "http://java.sun.com/xml/ns/j2ee");
			root.setAttribute("id", this.id);
			root.setAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			root.setAttribute("version", "1.4");
			root.setAttribute(
							"xsi:schemaLocation",
							"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/application_1_4.xsd");
		}

		// add display-name
		if (this.displayName != null) {
			Element dspName = doc.createElement("display-name");
			dspName.setTextContent(this.displayName);
			root.appendChild(dspName);
		}

		// add web modules first
		if (this.webModules != null && this.webModules.size() > 0) {
			// create the servlets
			for (WebModule wm : webModules) {
				Element module = doc.createElement("module");
				module.setAttribute("id", wm.getId());

				Element web = doc.createElement("web");
				module.appendChild(web);

				if (wm.getWar() == null)
					throw new BuildException(
							"WebModule is missing warFile attribute.");
				Element webUri = doc.createElement("web-uri");
				webUri.setTextContent(wm.getWar());
				web.appendChild(webUri);

				if (wm.getContextRoot() == null)
					throw new BuildException(
							"WebModule is missing contextRoot attribute.");
				Element contextRoot = doc.createElement("context-root");
				contextRoot.setTextContent(wm.getContextRoot());
				web.appendChild(contextRoot);

				root.appendChild(module);
			}
		}

		// add java modules
		if (this.javaModules != null && this.javaModules.size() > 0) {
			// create the servlets
			for (JavaModule jm : javaModules) {
				Element module = doc.createElement("module");
				module.setAttribute("id", jm.getId());

				
				if (jm.getJar() == null)
					throw new BuildException(
							"JavaModule is missing jar attribute.");				
				Element java = doc.createElement("java");
				module.appendChild(java);
				java.setTextContent(jm.getJar());

				root.appendChild(module);
			}
		}

		// add EJB modules
		if (this.ejbModules != null && this.ejbModules.size() > 0) {
			// create the servlets
			for (EjbModule em : ejbModules) {
				Element module = doc.createElement("module");
				module.setAttribute("id", em.getId());

				if (em.getJar() == null)
					throw new BuildException(
							"EjbModule is missing jar attribute.");					
				Element java = doc.createElement("ejb");
				module.appendChild(java);
				java.setTextContent(em.getJar());

				root.appendChild(module);
			}
		}
	}

	public void addWebModule(WebModule module) {
		this.webModules.add(module);
	}

	public void addEjbModule(EjbModule module) {
		this.ejbModules.add(module);
	}

	public void addJavaModule(JavaModule module) {
		this.javaModules.add(module);
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Models web module in app.xml
	 */
	public static class WebModule {

		private String id = "WebModule_" + (moduleDefaultId++);

		private String war = null;

		private String contextRoot = null;

		public String getContextRoot() {
			return contextRoot;
		}

		public String getId() {
			return id;
		}

		public String getWar() {
			return war;
		}

		public void setContextRoot(String contextRoot) {
			this.contextRoot = contextRoot;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setWar(String webUri) {
			this.war = webUri;
		}
	}

	/**
	 * Models ejb module in app.xml
	 */
	public static class EjbModule {

		private String id = "EjbModule_" + (moduleDefaultId++);

		private String jar = null;

		public String getId() {
			return id;
		}

		public String getJar() {
			return jar;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setJar(String jar) {
			this.jar = jar;
		}
	}

	/**
	 * Models java module in app.xml
	 */
	public static class JavaModule {

		private String id = "JavaModule_" + (moduleDefaultId++);

		private String jar = null;

		public String getId() {
			return id;
		}

		public String getJar() {
			return jar;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setJar(String jar) {
			this.jar = jar;
		}
	}
}
