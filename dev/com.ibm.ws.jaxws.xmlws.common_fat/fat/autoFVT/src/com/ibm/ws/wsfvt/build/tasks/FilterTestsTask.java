/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/FilterTestsTask.java, WAS.websvcs.fvt, WASX.FVT, ss0827.12 1/24/08 18:10:19 [7/8/08 08:00:32]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 06/18/07    jramos      453487             New File
 * 11/10/08    btiffany    563429             add notExcludedDirs property
 * 11/12/09    jramos      626728             Use test delimiter when populating ANT
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * The FilterTestsTask class is a custom task for filtering test
 * directories based on provided release filters
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class FilterTestsTask extends Task {
    
    private String releaseFilter  = null;
    private String excludedDirs   = null;
    private String notExcludedDirs   = null;  //subset of excludedDirs we want to include.
    private String delimiter      = null;
    private String includedDirs   = null;
    private boolean debug = false;
    
    private static final String testListProp = "test.list";
    private static final String srcDir = (AppConst.FVT_HOME + "/src").replace('\\', '/');
    
    public void execute() {
        try {
          String[] filters = null;
          List<String> testDirs = new LinkedList<String>();
          
          if(this.releaseFilter          == null || 
             this.releaseFilter.length() == 0    || 
             this.releaseFilter.equals("**")        ){
             this.releaseFilter = "";  // empty string means no filter needed
          }
          if(this.excludedDirs == null || this.excludedDirs.length() == 0){
              this.excludedDirs = ""; // empty string means no filter needed 
          }
            if(this.notExcludedDirs == null || this.notExcludedDirs.length() == 0){
              this.notExcludedDirs = ""; // empty string means no filter needed 
          }
          if(this.includedDirs == null || this.includedDirs.length() ==0)
              throw new BuildException("invalid includeddirs");
          if(this.delimiter == null || delimiter.length() == 0)
              throw new BuildException("invalid delimiter");

          if( ( this.releaseFilter.length() == 0) && (this.excludedDirs.length() == 0)){
              // don't need to filter; just set the property
              getProject().setProperty(testListProp, getProject().getProperty("test.name"));
              System.out.println( "Nothing to work on FilterTests task" );
              return;
          }


          // get list of test directories that contain tests with the correct annotation
          List<String> includedExpressions = new LinkedList<String>();
          StringTokenizer tokenizer = new StringTokenizer(includedDirs, delimiter);
          while(tokenizer.hasMoreTokens()) {
              includedExpressions.add(tokenizer.nextToken() + "/buildTest.xml");
          }
          
          // select buildTest files
          Path path = new Path(getProject());
          FileSet fileSet = new FileSet();
          fileSet.setDir(new File(srcDir));
          for(String includedExpression : includedExpressions) {
              fileSet.createInclude().setName(includedExpression);
          }

          // execlude the excluded file anmes
          if( excludedDirs.length() != 0 ){
              // get list of excluded directories 
              List<String> excludedExpressions = new LinkedList<String>();
              StringTokenizer excludedTokenizer = new StringTokenizer(excludedDirs, delimiter);
              while(excludedTokenizer.hasMoreTokens()) {
                  excludedExpressions.add(excludedTokenizer.nextToken() + "/buildTest.xml");
              }

              for(String excludedExpression : excludedExpressions) {
                  fileSet.createExclude().setName(excludedExpression);
              }
          }

          path.addFileset(fileSet);


          String[] buildFilesArray;
          buildFilesArray = path.list();   // get all the build file directories
          
          // now we need to do something ant can't do well - add some directories that might
          // have been excluded. 
       
          if( notExcludedDirs.length() != 0 ){
              Path npath = new Path(getProject());
              FileSet nfileSet = new FileSet();
              nfileSet.setDir(new File(srcDir));
              // get list of Notexcluded directories 
              List<String> nexcludedExpressions = new LinkedList<String>();
              StringTokenizer nexcludedTokenizer = new StringTokenizer(notExcludedDirs, delimiter);
              while(nexcludedTokenizer.hasMoreTokens()) {
                  nexcludedExpressions.add(nexcludedTokenizer.nextToken() + "/buildTest.xml");
              }

              for(String nexcludedExpression : nexcludedExpressions) {
                  nfileSet.createInclude().setName(nexcludedExpression);
              }
              
              npath.addFileset(nfileSet);
              String[] nbuildFilesArray;
              nbuildFilesArray = npath.list();
              
              // now append this new list to existing list of dirs. 
             
              java.util.ArrayList <String> newArray = new ArrayList <String>();
              int i= 0;
              // copy the old list
              for(i=0; i< buildFilesArray.length; i++){
                  if(debug) System.out.println("debug: copying dir entry: "+ buildFilesArray[i]);  
                  newArray.add(buildFilesArray[i]);
              }
              
              // copy the new items unless they're dups
              for( int j=0; j< nbuildFilesArray.length; j++ ){
                  //scan for duplicates and skip if found
                  boolean dup = false;
                  for(int k=0; k< buildFilesArray.length; k++ ){                      
                      if(buildFilesArray[k].compareTo(nbuildFilesArray[j]) == 0){
                          dup = true;
                          if(debug) System.out.println("debug: dup: "+ nbuildFilesArray[j]);
                          break;                         
                      }
                  }
                  if(!dup) {
                      newArray.add(nbuildFilesArray[j]);
                      if (debug) System.out.println("debug: added: "+ nbuildFilesArray[j]);
                  }     
              }
              // cvt back to type we need.
              buildFilesArray = newArray.toArray(buildFilesArray);
          }
          // end of adding files despite excludes. Whew.   
          
          if(releaseFilter.length() != 0 ) {
              // convert String to array
              filters = releaseFilter.split(delimiter);
          }

          // scan directories for test classes that contain annotations
          for(int i = 0; i < buildFilesArray.length; ++i) {
              File nextFile = new File(buildFilesArray[i]);
              boolean bAdding = filters        == null ||     // If no filters
                                filters.length == 0    ||     // or filters has no items
                                scanTestDir(nextFile.getParentFile(), filters) ; // or directory has the release specified
              String testDir = nextFile.getParentFile().getAbsolutePath().replace('\\', '/');
              if (debug) System.out.println("testdir = "+ testDir);
              testDir = testDir.substring(srcDir.replace('\\', '/').length() );
              // path seems inconsistent between platforms, sometimes picks up a leading /, sometimes not.
              if (testDir.startsWith("/")) {
                testDir = testDir.substring(1);
              }
              if (debug) System.out.println("testdir = "+ testDir);
              if( bAdding ){
                  testDirs.add(testDir);
              }
          }

          // populate ANT project with new list of test directories
          String testDirsString = "";
          for(String testDir : testDirs) {
              testDirsString += (testDir + this.delimiter);
          }
          getProject().setProperty(testListProp, testDirsString);

        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }
    
    /**
     * Scan a directory
     * 
     * @param testDir Directory to scan
     * @param filters Release filters
     * @return
     */
    private boolean scanTestDir(File testDir, String[] filters) {
        File[] files = testDir.listFiles();
        
        for(int i = 0; i < files.length; ++i) {
            if(files[i].isDirectory()) {
                if(scanTestDir(files[i], filters))
                    return true;
            } else if(files[i].getName().endsWith("Test.java")
                    || files[i].getName().endsWith("Tests.java")
                    || files[i].getName().endsWith("TestCase.java")) {
                if(processFile(files[i], filters))
                    return true;
            }
        }
        
        return false;
    }
    
    /**
     * Process a file for annotations
     * 
     * @param file The file to process
     * @param filters Release filters to find in the annotations
     * @return
     */
    private boolean processFile(File file, String[] filters) {
        String anno1String = "since=FvtTestSuite.Releases.";
        String anno2String = "since=Releases.";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            StringTokenizer tokenizer = null;
            String token = null;
            boolean publicFound = false;
            boolean voidFound = false;
            boolean testFound = false;
            boolean annotationFound = false;
            
            while((line = br.readLine()) != null) {
                tokenizer = new StringTokenizer(line, " \t\r\n\f(),");
                while(tokenizer.hasMoreTokens()) {
                    // check if this line has an annotation or is the start of a test method declaration
                    // we need to do it this way because of possible text spacing variations
                    token = tokenizer.nextToken();
                    if(token.indexOf("public") != -1) {
                        // start of method declaration
                        publicFound = true;
                    } else if(publicFound && token.indexOf("void") != -1) {
                        // getting closer to being a test method delcaration
                        voidFound = true;
                    } else if(publicFound && !voidFound) {
                        // method didn't start with "public void" so this isn't a test method
                        publicFound = voidFound = testFound = false;
                    } else if(voidFound && token.indexOf("test") != -1) {
                        // test method!!! method starts with "public void test"
                        testFound = true;
                    } else if(voidFound && !testFound) {
                        // method name doesn't start with "test"
                        publicFound = voidFound = testFound = false;
                        break;
                    } else if(token.indexOf(anno1String) != -1) {
                        line = line.substring(line.indexOf(anno1String) + anno1String.length());
                        annotationFound = true;
                        break;
                    } else if(token.indexOf(anno2String) != -1) {
                        line = line.substring(line.indexOf(anno2String) + anno2String.length());
                        annotationFound = true;
                        break;
                    }
                }
                
                if(annotationFound) {
                    // check if it is one of the release filters specified
                    for(int i = 0; i < filters.length; ++i) {
                        if(line.startsWith(filters[i]))
                            return true;
                    }
                }
                if(!annotationFound && testFound) {
                    // method was found with no annotion... we have to run this unfortunately
                    return true;
                }
                if(annotationFound && testFound) {
                    // we made it this far, found an annotation and a test method, but the annotaion wasn't in our filter
                    // lets reset
                    publicFound = false;
                    voidFound = false;
                    testFound = false;
                    annotationFound = false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
        
        return false;
    }
    
    public void setReleasefilter(String releaseFilter) {
        releaseFilter = releaseFilter.trim();
        this.releaseFilter = releaseFilter;
    }

    public void setExcludeddirs(String excludedDirs) {
        excludedDirs = excludedDirs.trim();
        this.excludedDirs = excludedDirs;
    }
    
    public void setNotexcludeddirs(String notExcludedDirs) {
        notExcludedDirs = notExcludedDirs.trim();       
        this.notExcludedDirs = notExcludedDirs;
    }

    public void setIncludeddirs(String includedDirs) {
        includedDirs = includedDirs.trim();
        this.includedDirs = includedDirs;
    }
    
    public void setdelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
