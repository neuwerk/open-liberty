/*
 * @(#) 1.3 WautoFVT/src/com/ibm/ws/wsfvt/build/tasks/WebXml.java, WAS.websvcs.fvt, WSFP.WFVT 1/9/04 15:12:08 [10/31/06 08:53:30]
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
 * 10/19/2007  sedov       D476378            Added EJB-REF
 * 04/16/2008  sedov       D513284            Updates for jee 5
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
 * A generic web.xml file writer for ANT. Supports web.xml versioning
 */
public class WebXml extends Task {

	private String id = "web-app";
	
	private String displayName = null;
	
	private String version = "current";
	
	private String file = "web.xml";
	
	private Listener listener = null;
	
	private List<Servlet> servlets = new LinkedList<Servlet>();
	
	private List<EjbRef> ejbRefs = new LinkedList<EjbRef>();
	
	public void execute() {
			
		Document doc = null;
		try {
	        // Create a builder factory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setValidating(false);
	        DocumentBuilder builder = factory.newDocumentBuilder(); 
	        doc = builder.newDocument();

		} catch (Exception e){
        	throw new BuildException("Unable to create DOMFactory", e);			
		}
        
		// create a document based on a specific version
        if ("current".equals(version) || "2.5".equals(version)){
        	createWebXml(doc, 25);
        } else if ("2.4".equals(version)){
        	createWebXml(doc, 24);
        } else {
        	throw new BuildException("Unexpected version specified: " + this.version);
        }
        
        try {
            // use specific Xerces class to write DOM-data to a file:
        	XMLSerializer serializer = new XMLSerializer();
	        serializer.setOutputCharStream(new java.io.FileWriter(this.file));
	        serializer.serialize(doc);
        } catch (Exception e){
        	throw new BuildException("Unable to write '" + this.file + "'", e);
        }
	}
	
	/**
	 * Create a web.xml based on 2.4 spec
	 */
	private void createWebXml(Document doc, int level){
        
        Element root = doc.createElement("web-app");
        doc.appendChild(root);
        
		if (level == 25) {
			// jee 5 (version 2.5)
			// <web-app id="WebApp_ID" version="2.5" xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation=
			// "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
	        root.setAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
	        root.setAttribute("id", this.id);
	        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	        root.setAttribute("version", "2.5");
	        root.setAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd");
		} else {
			// j2ee 1.4 (version 2.4 descriptor)
			root.setAttribute("xmlns", "http://java.sun.com/xml/ns/j2ee");
	        root.setAttribute("id", this.id);
	        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	        root.setAttribute("version", "2.4");
	        root.setAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd");
		}

        // add display-name
        if (this.displayName != null){
	        Element dspName = doc.createElement("display-name");
	        dspName.setTextContent(this.displayName);
	        root.appendChild(dspName);
        }
        
        // add listener
        if (this.listener != null && this.listener.getListener() != null){
	        Element dspName = doc.createElement("listener-class");
	        dspName.setTextContent(this.listener.getListener());
	        root.appendChild(dspName); 
        }
        
        // add servlets and servlet mappings
        if (this.servlets != null && this.servlets.size() > 0){
        	// create the servlets
        	for (Servlet s: servlets){
    	        Element elServlet = doc.createElement("servlet");
    	        
    	        Element elServletName = doc.createElement("servlet-name");
    	        String servletName = s.getName()==null?s.getServletClass():s.getName();
   	        	elServletName.setTextContent(servletName);
    	        elServlet.appendChild(elServletName);
    	        
    	        Element elServletClass = doc.createElement("servlet-class");
    	        elServletClass.setTextContent(s.getServletClass());
    	        elServlet.appendChild(elServletClass);
    	        
    	        // load on startup is optional
    	        if (s.isLoadOnStartup()){
    	        	Element elLoadOnStartup = doc.createElement("load-on-startup");
    	        	elLoadOnStartup.setTextContent("1");
    	        	elServlet.appendChild(elLoadOnStartup);
    	        }
    	        
    	        // security role ref is optional
    	        if (s.getSecurityRoleRef() != null){
    	        	Element elSecurityRoleRef = doc.createElement("security-role-ref");
    	        	elSecurityRoleRef.setTextContent(s.getSecurityRoleRef());
    	        	elServlet.appendChild(elSecurityRoleRef);
    	        }    	        
    	        	
    	        root.appendChild(elServlet);         		
        	}
        	
        	// create the servlets
        	for (Servlet s: servlets){
        		// only process the mapping if it was specified
        		if (s.getUrlPattern() != null && s.getUrlPattern().length() > 0){
	    	        Element elMapping = doc.createElement("servlet-mapping");
	    	        
	    	        Element elServletName = doc.createElement("servlet-name");
	    	        String servletName = s.getName()==null?s.getServletClass():s.getName();
	    	        elServletName.setTextContent(servletName);
	    	        elMapping.appendChild(elServletName);
	    	        
	    	        Element elUrlPattern = doc.createElement("url-pattern");
	    	        elUrlPattern.setTextContent(s.getUrlPattern());
	    	        elMapping.appendChild(elUrlPattern);
	    	        	
	    	        root.appendChild(elMapping);   
        		}
        	}  
        	
        	// create the ejb references
        	for (EjbRef e: ejbRefs){
        		// only process the mapping if it was specified
    	        Element elEjbRef = doc.createElement("ejb-ref");
    	        
    	        Element elEjbRefName = doc.createElement("ejb-ref-name");
    	        elEjbRefName.setTextContent(e.getEjbRefName());
    	        elEjbRef.appendChild(elEjbRefName);
    	        
    	        Element elEjbRefType = doc.createElement("ejb-ref-type");
    	        elEjbRefType.setTextContent(e.getEjbRefType());
    	        elEjbRef.appendChild(elEjbRefType);
    	        
    	        Element elHome = doc.createElement("home");
    	        elHome.setTextContent(e.getHome());
    	        elEjbRef.appendChild(elHome);
    	        
    	        Element elRemote = doc.createElement("remote");
    	        elRemote.setTextContent(e.getRemote());
    	        elEjbRef.appendChild(elRemote);
    	        	
    	        if (e.getEjbLink() != null){
	    	        Element elEjbLink = doc.createElement("ejb-link");
	    	        elEjbLink.setTextContent(e.getEjbLink());
	    	        elEjbRef.appendChild(elEjbLink);
    	        }
    	        
    	        root.appendChild(elEjbRef);   
        	} 
        }
	}
	
	public void addListener(Listener listener){
		this.listener = listener;
	}
	
	public void addServlet(Servlet servlet){
		this.servlets.add(servlet);
	}
	
	public void addEjbRef(EjbRef ejbRef){
		this.ejbRefs.add(ejbRef);
	}
	
	public static class Listener {
		private String listener = null;

		public void setListener(String listener) {
			this.listener = listener;
		}

		public String getListener() {
			return listener;
		}
	}
	
	public static class EjbRef {
		private String ejbRefName = null;
		private String ejbRefType = null;
		private String home = null;
		private String remote = null;
		private String ejbLink = null;
		public String getEjbLink() {
			return ejbLink;
		}
		public String getEjbRefName() {
			return ejbRefName;
		}
		public String getEjbRefType() {
			return ejbRefType;
		}
		public String getHome() {
			return home;
		}
		public String getRemote() {
			return remote;
		}
		public void setEjbLink(String ejbLink) {
			this.ejbLink = ejbLink;
		}
		public void setEjbRefName(String ejbRefName) {
			this.ejbRefName = ejbRefName;
		}
		public void setEjbRefType(String ejbRefType) {
			this.ejbRefType = ejbRefType;
		}
		public void setHome(String home) {
			this.home = home;
		}
		public void setRemote(String remote) {
			this.remote = remote;
		}

	}

	public static class Servlet {
		private String name = null;
		private String servletClass = null;
		private String urlPattern = null;
		private String securityRoleRef = null;
		
		private boolean loadOnStartup = true;
		
		public void setLoadOnStartup(boolean loadOnStartup) {
			this.loadOnStartup = loadOnStartup;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setServletClass(String servletClass) {
			this.servletClass = servletClass;
		}
		public void setUrlPattern(String urlPattern) {
			this.urlPattern = urlPattern;
		}
		public boolean isLoadOnStartup() {
			return loadOnStartup;
		}
		public String getName() {
			return name;
		}
		public String getServletClass() {
			return servletClass;
		}
		public String getUrlPattern() {
			return urlPattern;
		}
		public String getSecurityRoleRef() {
			return securityRoleRef;
		}
		public void setSecurityRoleRef(String securityRoleRef) {
			this.securityRoleRef = securityRoleRef;
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getFile() {
		return file;
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
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
}
