package com.ibm.ws.webservices.engine.xmlsoap;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import javax.xml.soap.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;

/**
 * Test for data loss during xml element copy that was corrected on PM37820
 * 
 * @author btiffany
 *
 */
public class PM37820Check{
    public static void main(String[] args) throws Exception {
         check();
    }
    
    public static boolean check() throws Exception {
        
        // create the source document, a DOM document. 
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = db.newDocument();
        Element root = d.createElementNS("http://foo/","document1_root");
        d.appendChild(root);
         
        // create the target document. 
        SOAPFactory sf = new com.ibm.ws.webservices.engine.xmlsoap.SOAPFactory();
        //javax.xml.soap.SOAPElement selem1 = sf.createElement("my_first_child");
        javax.xml.soap.SOAPElement selem2 = sf.createSOAPElement("http://foo2/","my_second_child");
        javax.xml.soap.SOAPElement selem3 = sf.createSOAPElement("http://foo2/", "myfoo2");
        // Element elem = selem3.getAsDOM();
        //selem2.addChildElement(selem3);
        
        SOAPEnvelope se = sf.createSOAPEnvelope();
        javax.xml.soap.SOAPBody sb = se.getBody();
        sb.addChildElement(selem2);        
        sb.addChildElement(selem3);  // works
        
        /*
        System.out.println("soap body:"+ sb);
        System.out.println("first child" + sb.getChildNodes().item(0));
        */
        
        
        // go down the path where we could delete the newchild, but after pm37820 we don't.
        // prepend document1_root to soap body
        // if defect is present, root after will be smaller or empty. 
        // System.out.println("=================== first test, add element from another document  =================");
        String rootBefore = docToString(d); 
        System.out.println("source document before: "+ rootBefore );
        System.out.println("target document before: " + sb);        
        sb.insertBefore(root, sb.getChildNodes().item(0));
        String rootAfter = docToString(d);
        System.out.println("source document after: "+ rootAfter);   // without pm37820 this should show the disappearing data.        
        System.out.println("target document after  " + sb);
        if(rootBefore.compareTo(rootAfter)!= 0){
            System.out.println("source document before and after do not match, data loss has occured, test failed.");
            return false;
        } else {return true; }
    }
    
     // print a node as a string. 
     public static String docToString( Node d) throws Exception {    
    
        //  set up a transformer - all this  just to be able to print out the document.         
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
    
        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(d);
        trans.transform(source, result);
        String xmlString = sw.toString();    
        return xmlString;
    
   }
 }   