/*
 * @(#) 1.10 autoFVT/src/annotations/support/AnnotationHelper.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/7/07 22:26:50 [8/8/12 06:54:34]
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 05/30/2006  btiffany    LIDB3296.31.01     new file
 * 10/03/2007  ulbricht    466202             Update for Java 6
 * 12/07/2007  ulbricht    488152             add direct method access
 *
 */
package annotations.support;
import javax.wsdl.*;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.namespace.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 * constructed from a Class, provides convenience methods for extracting annotations
 * from the class and it's methods, and for examining their contents
 * @author btiffany
 *
 */
public class AnnotationHelper {
	/**
	 * constructor 
	 */
	private Class cls = null;
	private String clsName = null;
	private HashMap methodMap = null;
	
	/**
	 * constructor
	 * @param absoluteClass
	 * it's an error to use this unless your generated files are on existing classpath
	 */
	/*public AnnotationHelper(String absoluteClass){
		this(absoluteClass,".");
	}
	*/
	
	/**
	 * constructor.  
	 * @param absoluteClass. The canonical class name.
	 * @param baseDir - the directory to append to the classpath before loading
	 */
	public AnnotationHelper(String absoluteClass, String baseDir) {
		URL u = null;
		try{
			/*
			 * in order to find our generated classes, 
			 * we need to fix up the classpath on the fly, so can't use the 
			 * system class loader intact.  use a url loader and make the directory
			 * a url, so the loader will look there AFTER the system classpath
             * (would have liked to prepend, but couldn't figure out how)  
			 */		

			try {
			u = new File(baseDir).toURL();
			} catch (Exception e){
				e.printStackTrace();
			}
			
			URLClassLoader ucu = new URLClassLoader(new URL[] {u});

                        try {
                            cls = ucu.loadClass(absoluteClass);
                        } catch (ClassNotFoundException e) {
                            cls = Class.forName(absoluteClass, false, ucu);
                        }

			clsName = absoluteClass;			
		} catch(ClassNotFoundException e){
  			System.out.println("appended url to classpath: "+u.toString());
			throw new RuntimeException("AnnotationHelper couldn't load class:"+absoluteClass);			
		}
	}	
	
	/**
	 * 
	 * @return class of object this helper was created on
	 */
	public Class getClas(){ return cls; }
	
	/**
	 * 
	 * @return name of class this helper was created on
	 */
	public String getClasName() { return clsName;}
	
	public boolean isInterface() {return cls.isInterface();}
	
	/**
	 * 
	 * @return array of annotations - all annotations on the class
	 */
	public Annotation [] getClassAnnotations(){
		return cls.getAnnotations();
	}
	
	/**
	 * 
	 * @param annotationName - annotation to look for
	 * @return - the annotation if present on the class, else null
	 */
	public Annotation getClassAnnotation(String annotationName){
		Annotation [] an = getClassAnnotations();
		/* 
		 * call getName or toString and look for the name we want.
		 */
		for (Annotation a : an){
			if (a.annotationType().getName().indexOf(annotationName) >=0) return a;						
		}
		return null;		
	}
	
	/**
	 * @return the value of an element within a class annotation, or null if not found.
	 */
	public String getClassElement(String annotationName, String elementName){
		Annotation a = getClassAnnotation(annotationName);
		if (a == null) return null; else return getAnnotationElement (a,"elementName");		
	}
	
	/**
	 * Hashmap of every method, annotation. 
	 * Annotations are stored by their name, not full path.
	 * If there are no annotations on method, null is stored.
	 * i.e. WebMethod, not javax.jws.WebMethod, not @WebMethod
	 * @returns a hashmap of string methodname, string annotationname
	 */
	public HashMap<String, String> getMethodAnnotationNames(){
		methodMap = new HashMap<String, String>();		
		Method [] ma = cls.getMethods();		
		boolean noAnns;
		for (Method m: ma){
			Annotation [] aa = m.getAnnotations();
			noAnns = true;
			for (Annotation a: aa){
				noAnns = false;
				String buf = a.annotationType().getName();
				int lastdot = buf.lastIndexOf(".");
				methodMap.put(m.getName(), buf.substring(lastdot +1));									
			}
			if (noAnns)methodMap.put(m.getName(), null);
		}
		return methodMap;		
	}
	
	/**
	 * 
	 * @param methodName		- method to look at
	 * @param annotationName	- annotation on method, omit leading @
	 * @return annotation or null, throws runtime exc. if ambiguous.
	 */
	public Annotation getMethodAnnotation(String methodName, String annotationName){
		// could have problems with simple substring match being too ambiguous, we'll see. 
		// first, try to find the method
		// throw exception if more than one matches the name spec.
		Method [] ma = cls.getMethods();
		Method tmp = null;
		int i=0;
		for (Method m: ma){
			if(m.getName().indexOf(methodName) > -1){
				tmp = m;
				i++;				
			}			
		}
		if (i != 1 ) throw new RuntimeException(methodName+": none or too many methods found: "+ i);

                Annotation a = getAnnotation(tmp, annotationName);
                
                return a;

	}
	
        /**
         * This method will look for an annotation on a method.
         * 
         * @param m The method
         * @param annotationName The name of the annotation to look for
         * @return An annotation
         */
        public Annotation getAnnotation(Method m, String annotationName) {

            Annotation[] an = m.getAnnotations();

            for (Annotation a : an) {
                //System.out.println(a.annotationType().getName().toString());			
                if (a.annotationType().getName().endsWith(annotationName)) {
                    return a;
                }
            }

            // then, on that method, try to find the annotation
            return null;

        }

        /**
         * This is a method that allows for getting the method annotation by
         * matching directly on a certain method by looking at the input
         * arguments to the method.
         *
         * @param methodName The name of the method to look for
         * @param args The input argument types to the method
         * @param annotationName The name of the annotation to look for
         * @return annotation or null, throws runtime exc. if ambiguous.
         * @throws NoSuchMethodException if the method does not exist
         */
        public Annotation getMethodAnnotation(String methodName, Class[] args, String annotationName) throws NoSuchMethodException {
            Method method = cls.getMethod(methodName, args);
            Annotation a = getAnnotation(method, annotationName);
            return a;
        }

	/**
	 * returns the value of an element within an annotation on a method
	 * @param method	- method name
	 * @param annoName	- annotation name, omitting package and leading \@
	 * @param eleName	- name of the element
	 * @return value of the element, or null if annotation or element not present. 
	 */
	public String getMethodElement(String method, String annoName, String eleName){
		return getAnnotationElement( getMethodAnnotation(method, annoName), eleName);		
	}
	
	/**
	 * returns the value of an element within an annotation on a method
	 * @param method	- method name
         * @param args          - method arguments
	 * @param annoName	- annotation name, omitting package and leading \@
	 * @param eleName	- name of the element
	 * @return value of the element, or null if annotation or element not present. 
         * @throws NoSuchMethodException if the method does not exist
	 */
	public String getMethodElement(String method, Class[] args, String annoName, String eleName) throws NoSuchMethodException {
            return getAnnotationElement( getMethodAnnotation(method, args, annoName), eleName);		
	}
	
	/**
	 * 
	 * @param a the annotation to look at
	 * @param elementName the element to get the value of
	 * @return the value of the element as a string, or null if element not found
	 */
	public String getAnnotationElement(Annotation a, String elementName){
		// first, we have to get the elements.
		// incredibly, the most efficient thing seems to be toString.
		// (so much for the mighty reflection api in this case)
		// example:
		// @javax.jws.WebService(name=fred, targetNamespace=, serviceName=, wsdlLocation=, endpointInterface=)
		String buf = a.toString();
		// find the element
		int pos = buf.indexOf(elementName+"=");		
		if (pos == -1) return null;
		int len = elementName.length()+1;
		// find where the comma is that ends the description
		// we found the element, so only comma or ) could be next
		int endpos = buf.indexOf(",", pos + len);
		// if we're at end of string, look for ), not , 
		if (endpos == -1) endpos = buf.indexOf(")", pos + len);
		//if (pos + len == endpos) return null;
		return (buf.substring(pos + len, endpos));
		
		
	}
	
	/**
	 * find the value of an annotation element on a parameter annotation
	 * @param methodName - method to search in
	 * @param param		 - int suggesting which parameter to look at, 0=1st param, etc.
	 * 				       call with -1 to just search 'em all.
	 * @param annoName	 - name of annotation to search for element in, omit leading \@
	 * @param elemName	 - name of the element  
	 * @return			 - value of the element, or null if not found.
	 * @throws			 - runtime exception if search results ambiguous or method not found. 
	 */
	public String getParamElement(String methodName,  int param, String annoName, String elementName ){
		// first, get the method
		Method [] ma = cls.getMethods();
		Method m = null;
		int i=0;
		for (Method ml: ma){
			if(ml.getName().compareTo(methodName) ==0){				
				i++;	
				m = ml;
			}			
		}
		if (i != 1) throw new RuntimeException("no or too many methods found:"+ i);		
		
		Annotation [][] aaa = m.getParameterAnnotations();
		//System.out.println(m.getName());
		// each method param has an array of annotations, in parameter order.
		int paramSequence = 0;
		if (param >= aaa.length) throw new RuntimeException("not enough parameters");
		String match =null;
		int matchcount = 0;
		for (Annotation [] aa: aaa){	// for each parameter... 
			// if only looking at one parameter, skip over the others
			if (param != -1 && paramSequence++ != param) continue;
			for (Annotation a: aa){		// for each annotation on each parameter...
				
				//System.out.println(a.annotationType().getName());
				//System.out.println(a.annotationType().getName().endsWith(annoName));
				if (a.annotationType().getName().endsWith(annoName)) 
					match = getAnnotationElement(a, elementName);
				    matchcount++;
			}

		}
		if (matchcount >1) {
			throw new RuntimeException("too many matches");
		}
		return match;		
	}
	
	/**
	 * search all params on a method for a given param and elementname  and return element's value
	 * 
	 * @param methodName - method to search in	  
	 * @param annoName	 - name of annotation to search for element in, omit leading @
	 * @param elemName	 - name of the element  
	 * @return			 - value of matching element, or null if not found.
	 * @throws			 - runtime exception if search results ambiguous or method not found. 
	 */
	public String getParamElement(String methodName,  String annoName, String elementName ){
		return getParamElement(methodName, -1, annoName, elementName );
	}



}


