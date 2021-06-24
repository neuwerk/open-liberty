/**
 *  autoFVT/src/com/ibm/ws/wsfvt/build/tools/ReportsModifier.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 * 
 *  IBM Confidential OCO Source Material
 *  (C) COPYRIGHT International Business Machines Corp. 2006
 *  The source code for this program is not published or otherwise divested
 *  of its trade secrets, irrespective of what has been deposited with the
 *  U.S. Copyright Office.
 * 
 *  Change History:
 *   Date        Author       Feature/Defect       Description
 *   03/05/09    btiffany     578313               New File
 *
 **/ 
package com.ibm.ws.wsfvt.build.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class modifies junit result files to remove known failures one test at a time.
 * 
 * To prevent accidentally removing too much coverage several precautions are taken:
 * - Files to modify are read from a properties file. See ReportsModifier.properties for details.
 * - Any modifications must have an expiration date no more than 90 days in the future.
 * - After a modification expires, it will no longer be performed.  
 * - Affected result files will be renamed to (old name)_WITH_SUPPRESSED_FAILURES to avoid masking. 
 * - explanatory text will be written into the stdout section of the report if it was supplied 
 * 
 * This needs to run in the directory containing the xml files to be modified.
 * ReportsModifier.properties needs to be copied there first.
 * 
 * TODO: convert input properties file into an xml file for better ease of us.
 *  
 * @author btiffany
 *
 */
public class ReportsModifier {
    public static final String FILE_SUFFIX = "_WITH_SUPPRESSED_FAILURES"; // we append this to names of modified files
    public static final String logs_dir=".";
    public static void main(String [] args) throws Exception{
        Properties props = new Properties();
        props.load(new FileInputStream(new File("./ReportsModifier.properties")));
        ReportsModifier me = new ReportsModifier();
        
        // try to process each entry in the props file
        String complexkey, value;
        for (int i=0; i<props.size(); i++){
            complexkey = (String)props.keys().nextElement();
            value = props.getProperty(complexkey);
            me.process(complexkey, value);
        } 
    }
    
    private void process(String key, String value) throws Exception {
        int lasthit = 0;
        String filename ;
        String testname ;
        String failureType; 
        String endDate;
        Document dom;
        boolean testfailure = false;
        boolean testerror = false;        
        java.util.GregorianCalendar now = new java.util.GregorianCalendar();
        GregorianCalendar endDatec = new GregorianCalendar();
        
        //      parse the key into its component parts
        try{
            filename = key.substring(lasthit, (lasthit = key.indexOf('!', lasthit)));
            testname = key.substring(lasthit +1, (lasthit = key.indexOf('!', lasthit+1)));
            failureType = key.substring(lasthit +1, (lasthit = key.indexOf('!', lasthit+1)));
            endDate = key.substring(lasthit +1);
            
            if(failureType.equals("failure")) { testfailure = true;; }
            if(failureType.equals("error")) { testerror = true;; }
            if(!testfailure && !testerror){
                System.out.println("invalid entry: "+ key);
                return;                
            }
         
            // make sure the rule is not expired or expiration date set too far in future
            endDatec.clear();
            String buf1, buf2, buf3;
            endDatec.set(Integer.valueOf(buf1 = endDate.substring(0,4)),
                       Integer.valueOf(buf2 = endDate.substring(4,6)) -1, 
                       Integer.valueOf(buf3 = endDate.substring(6, 8)));
        } catch (Exception e){
            System.out.println("invalid entry: "+ key);
            return;
        }
         
         // if enddate is in the past, or > 90 days in the future, date is out of range. 
         long timeDifference = endDatec.getTimeInMillis() - now.getTimeInMillis();
         //7776000000  90*24*3600*1000
         //7433682000  
         long n = 90L * 24L * 3600L * 1000L;
         if ((timeDifference < 0 ) |( timeDifference > n ) ){
             System.out.println("invalid date in entry: "+ key);
             return;             
         }
        
        // look for the filename, if don't find it, look for renamed file
        File f;
        String buf = logs_dir+"/"+ filename+".xml";        
        if (!(f = new File(logs_dir+"/"+ filename+".xml")).exists()) {
            if(!(f = new File(logs_dir+"/"+ filename+FILE_SUFFIX+".xml")).exists()){
                return;
            }
        }
        System.out.println("found file: " + f.getName());
        
        // open the file and see if there are any failures and/or errors
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        dom = db.parse(f);
        
        //get the root element, 
        Element root = dom.getDocumentElement();
        int errors = Integer.valueOf(root.getAttribute("errors"));
        int failures = Integer.valueOf(root.getAttribute("failures"));
        String name = root.getAttribute("name");
        
        // no failures, no problem!
        if(errors + failures == 0 ){ return; }
        if(testfailure && failures == 0 ){return; }
        if(testerror && errors == 0 ){return; }
                
        NodeList temp = root.getElementsByTagName("system-out");
        Element sysout = (Element)temp.item(0);
        
        
        // look through the testcases for the one we want
        NodeList tests = root.getElementsByTagName("testcase");  
        for(int i = 0 ; i < tests.getLength();i++) {
            Element test = (Element)tests.item(i);
            // modify the numbers if appropriate to do so
            if (test.getAttribute("name").equals(testname)){
                // change package name for reports
                int ofs = name.lastIndexOf(".");
                String newpkgname = root.getAttribute("name");
                if(!name.contains(FILE_SUFFIX)){
                    newpkgname = name.substring(0, ofs) + FILE_SUFFIX + "." + name.substring(ofs+1);
                }
                
                // cook the numbers
                if( test.getElementsByTagName("failure").getLength() >0 && testfailure ){ 
                    root.setAttribute("failures", String.valueOf(failures -1));
                    root.setAttribute("name", newpkgname);
                    test.setAttribute("name", testname+"_SUPPRESSED");
                    buf = sysout.getTextContent() +
                    "\n======== failure suppression information for " 
                    + testname +" =========\n"+ 
                    value + 
                    "\n=============================================\n\n";
                    sysout.setTextContent(buf);
                    System.out.println("removing failure of "+ testname);
                    buf = sysout.getTextContent();
                    
                    rewriteXmlFile(f, dom);
                } 
                if( test.getElementsByTagName("error").getLength() >0 && testerror ){
                    root.setAttribute("errors", String.valueOf(errors -1));
                    root.setAttribute("name", newpkgname);
                    test.setAttribute("name", testname+"_SUPPRESSED");
                    buf = sysout.getTextContent() +
                    "\n======== error suppression information for " 
                    + testname +" =========\n"+ 
                    value + 
                    "\n=============================================\n\n";
                    sysout.setTextContent(buf);                   
                    
                    System.out.println("removing error of "+ testname);
                    rewriteXmlFile(f, dom);
                }
                break;    
            }
            
        } // end for
        
    }  // end fn

    /**
     * write a dom object to a file
     * @param f - file name
     * @param dom - dom object
     * @throws Exception
     */
    void rewriteXmlFile(File f, Document dom) throws Exception {
        boolean eraseOriginal = false;   
        
        File forig = f;
        String cpath; 
        // if we haven't already renamed the file, prepare that now.
        // the html reporter doesn't rrequire this, it's just to be clear that 
        // we have altered the reports.
        if(! (cpath = f.getCanonicalPath()).contains(FILE_SUFFIX)){
            eraseOriginal = true;
            String fname = cpath.substring(0,cpath.length() -4)+ FILE_SUFFIX + ".xml";
            f = new File(fname);            
        }
        
        // rewrite
        OutputFormat format = new OutputFormat(dom);
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer( new FileOutputStream(f), format);
        serializer.serialize(dom);        
        
        // back up original and erase               
        if(eraseOriginal){
            File bak = new File(forig.getCanonicalPath()+".bak");
            forig.renameTo(bak);
            forig.delete();
        }
        
    }
    
    void copyFile(File in, File out) throws Exception {
        FileInputStream fis  = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } 
        catch (Exception e) {
            throw e;
        }
        finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }
      }

    

}
