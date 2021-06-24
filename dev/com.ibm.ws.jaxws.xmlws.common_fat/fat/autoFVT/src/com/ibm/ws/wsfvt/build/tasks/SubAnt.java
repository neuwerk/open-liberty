/*
 * @(#) 1.9 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/SubAnt.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 12/16/10 10:19:05 [8/8/12 06:40:28]
 *
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect  Description
 * -----------------------------------------------------------------------------
 * 05/23/2007  jramos      440922          New File
 * 07/09/2007  jramos      450742          Change implementation of execute(File, File)
 * 07/27/2007  jramos      454362          Add executeForked
 * 08/07/2007  jramos      454362.1        Delete addConfiguredTarget()
 * 10/22/2007  jramos      476750          Use TopologyDefaults tool and ACUTE 2.0 api
 * 10/30/2008  jramos      559143          Incorporate Simplicity
 * 07/29/2010  lizet       F1149.1-24048   set endorsed dir  
 * 12/13/2010  btiffany    670291.1        update endorsed path for sun & hp.

 */

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * Calls a given target for all defined sub-builds. This is an extension
 * of ant for bulk project execution.
 * <p>
 * <h2> Use with directories </h2>
 * <p>
 * subant can be used with directory sets to execute a build from different directories.
 * 2 different options are offered
 * </p>
 * <ul>
 * <li>
 * run the same build file /somepath/otherpath/mybuild.xml
 * with different base directories use the genericantfile attribute
 * </li>
 * <li>if you want to run directory1/build.xml, directory2/build.xml, ....
 * use the antfile attribute. The base directory does not get set by the subant task in this case,
 * because you can specify it in each build file.
 * </li>
 * </ul>
 * @since Ant1.6
 * @ant.task name="subant" category="control"
 */
public class SubAnt
             extends Task {

    private Path buildpath;

    private Ant ant = null;
    private String target = null;
    private String antfile = "build.xml";
    private File genericantfile = null;
    private boolean verbose = false;
    private boolean inheritAll = false;
    private boolean inheritRefs = false;
    private boolean failOnError = true;
    private String output  = null;
    private String errorLog = null;
    private String param = null;
    private boolean fork = false;

    private Vector properties = new Vector();
    private Vector references = new Vector();
    private Vector propertySets = new Vector();

    /** the targets to call on the new project */
    private Vector/*<TargetElement>*/ targets = new Vector();

    /**
     * Pass output sent to System.out to the new project.
     *
     * @param output a line of output
     * @since Ant 1.6.2
     */
    public void handleOutput(String output) {
        if (ant != null) {
            ant.handleOutput(output);
        } else {
            super.handleOutput(output);
        }
    }

    /**
     * Process input into the ant task
     *
     * @param buffer the buffer into which data is to be read.
     * @param offset the offset into the buffer at which data is stored.
     * @param length the amount of data to read
     *
     * @return the number of bytes read
     *
     * @exception IOException if the data cannot be read
     *
     * @see Task#handleInput(byte[], int, int)
     *
     * @since Ant 1.6.2
     */
    public int handleInput(byte[] buffer, int offset, int length)
        throws IOException {
        if (ant != null) {
            return ant.handleInput(buffer, offset, length);
        } else {
            return super.handleInput(buffer, offset, length);
        }
    }

    /**
     * Pass output sent to System.out to the new project.
     *
     * @param output The output to log. Should not be <code>null</code>.
     *
     * @since Ant 1.6.2
     */
    public void handleFlush(String output) {
        if (ant != null) {
            ant.handleFlush(output);
        } else {
            super.handleFlush(output);
        }
    }

    /**
     * Pass output sent to System.err to the new project.
     *
     * @param output The error output to log. Should not be <code>null</code>.
     *
     * @since Ant 1.6.2
     */
    public void handleErrorOutput(String output) {
        if (ant != null) {
            ant.handleErrorOutput(output);
        } else {
            super.handleErrorOutput(output);
        }
    }

    /**
     * Pass output sent to System.err to the new project.
     *
     * @param output The error output to log. Should not be <code>null</code>.
     *
     * @since Ant 1.6.2
     */
    public void handleErrorFlush(String output) {
        if (ant != null) {
            ant.handleErrorFlush(output);
        } else {
            super.handleErrorFlush(output);
        }
    }
    
    /**
     * Runs the various sub-builds
     */
    public void execute() {
        if (buildpath == null) {
            System.out.println("Build Failed!!!");
            throw new BuildException("No buildpath specified");
        }
        
        final String[] filenames = buildpath.list();
        final int count = filenames.length;
        if (count < 1) {
            log("No sub-builds to iterate on", Project.MSG_WARN);
            return;
        }
/*
    //REVISIT: there must be cleaner way of doing this, if it is merited at all
        if (subTarget == null) {
            subTarget = getOwningTarget().getName();
        }
*/
        BuildException buildException = null;
        for (int i = 0; i < count; ++i) {
            File file = null;
            String subdirPath = null;
            Throwable thrownException = null;
            try {
                File directory = null;
                file = new File(filenames[i]);
                if (file.isDirectory()) {
                    if (verbose) {
                        subdirPath = file.getPath();
                        log("Entering directory: " + subdirPath + "\n", Project.MSG_INFO);
                    }
                    if (genericantfile != null) {
                        directory = file;
                        file = genericantfile;
                    } else {
                        file = new File(file, antfile);
                    }
                }
                if(this.fork) {
                    executeForked(file);
                } else {
                    execute(file, directory);
                }
                System.gc();
                if (verbose && subdirPath != null) {
                    log("Leaving directory: " + subdirPath + "\n", Project.MSG_INFO);
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                if (!(getProject().isKeepGoingMode())) {
                    if (verbose && subdirPath != null) {
                        log("Leaving directory: " + subdirPath + "\n", Project.MSG_INFO);
                    }
                    System.out.println("Build Failed!!!");
                    throw ex; // throw further
                }
                thrownException = ex;
            } catch (Throwable ex) {
                if (!(getProject().isKeepGoingMode())) {
                    if (verbose && subdirPath != null) {
                        log("Leaving directory: " + subdirPath + "\n", Project.MSG_INFO);
                    }
                    System.out.println("Build Failed!!!");
                    throw new BuildException(ex);
                }
                thrownException = ex;
            }
            if (thrownException != null) {
                if (thrownException instanceof BuildException) {
                    log("File '" + file
                        + "' failed with message '"
                        + thrownException.getMessage() + "'.", Project.MSG_ERR);
                    // only the first build exception is reported
                    if (buildException == null) {
                        buildException = (BuildException) thrownException;
                    }
                } else {
                    log("Target '" + file
                        + "' failed with message '"
                        + thrownException.getMessage() + "'.", Project.MSG_ERR);
                    thrownException.printStackTrace(System.err);
                    if (buildException == null) {
                        buildException =
                            new BuildException(thrownException);
                    }
                }
                if (verbose && subdirPath != null) {
                    log("Leaving directory: " + subdirPath + "\n", Project.MSG_INFO);
                }
            }
        }
        // check if one of the builds failed in keep going mode
        if (buildException != null) {
            System.out.println("Build Failed!!!");
            throw buildException;
        }
    }

    /**
     * Runs the given target on the provided build file.
     *
     * @param  file the build file to execute
     * @param  directory the directory of the current iteration
     * @throws BuildException is the file cannot be found, read, is
     *         a directory, or the target called failed, but only if
     *         <code>failOnError</code> is <code>true</code>. Otherwise,
     *         a warning log message is simply output.
     */
    private void execute(File file, File directory) throws BuildException {
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            String msg = "Invalid file: " + file;
            if (failOnError) {
                throw new BuildException(msg);
            }
            log(msg, Project.MSG_WARN);
            return;
        }

        ant = createAntTask(directory);
        String antfilename = file.getAbsolutePath();
        ant.setAntfile(antfilename);
        if(param != null) {
            Property prop = ant.createProperty();
            prop.setName(param);
            prop.setValue(file.getAbsolutePath().replace('\\', '/'));
        }
        try {
            ant.execute();
        } catch (BuildException e) {
            if (failOnError) {
                throw e;
            }
            log("Failure for target '" + target
               + "' of: " +  antfilename + "\n"
               + e.getMessage(), Project.MSG_WARN);
        } catch (Throwable e) {
            if (failOnError) {
                throw new BuildException(e);
            }
            log("Failure for target '" + target
                + "' of: " + antfilename + "\n"
                + e.toString(),
                Project.MSG_WARN);
        } finally {
            ant = null;
        }    
    }

    /**
     * Runs the given target on the provided build file in a separate JVM process
     * 
     * @param file the build file to execute
      * @throws BuildException if the file cannot be found, read, is
     *         a directory, or the target called failed, but only if
     *         <code>failOnError</code> is <code>true</code>. Otherwise,
     *         a warning log message is simply output.
     */
    private void executeForked(File file) throws BuildException {
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            logBuildFailure("Invalid file", file);
            String msg = "Invalid file: " + file;
            System.out.println("Build Failed!!!");
            if (failOnError) {
                throw new BuildException(msg);
            }
            log(msg, Project.MSG_WARN);
            return;
        }

        Java java = (Java) getProject().createTask("java");
        java.setOwningTarget(getOwningTarget());
        java.setTaskName(getTaskName());
        java.setLocation(getLocation());
        java.setClassname("org.apache.tools.ant.Main");
        java.setAppend(true);
        java.setFork(true);
        String systemClassPath = System.getProperty("java.class.path");
        java.setClasspath(new Path(getProject(), systemClassPath));
        String args = "";
        args += "-buildfile " + file.getAbsolutePath().replace('\\', '/');
        if(getProject().getProperty("is.hot.install") != null && getProject().getProperty("is.hot.install").equals("true"))
            args += " setHotInstall";
        args += " setenv";
        if(this.target == null) {
            // find default target
            String buildFileContents = "";
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line = "";
                while((line = br.readLine()) != null) {
                    buildFileContents += line;
                }
            } catch(Exception e) {
                throw new BuildException(e);
            } finally {
                if(br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {}
                }
            }
            int startIndex = buildFileContents.indexOf("default=\"") + "default=\"".length();
            this.target = buildFileContents.substring(startIndex, buildFileContents.indexOf("\"", startIndex));
        }
        args += " " + this.target;
        if(param != null) {
            args += " -D" + param + "=" + file.getAbsolutePath().replace('\\', '/');
        }
        args += " -DinstallTest=installTest.xml";
        args += " -DuninstallTest=uninstallTest.xml";
        for (Enumeration i = properties.elements(); i.hasMoreElements();) {
            Property prop = (Property) i.nextElement();
            args += " -D" + prop.getName() + "=" + prop.getValue();
        }
        Commandline.Argument arguments = java.createArg();
        arguments.setLine(args);
        StringBuffer vmArgs = new StringBuffer();
        vmArgs.append("-DFVT.base.dir=" + getProject().getProperty("FVT.base.dir") + " -DWAS.base.dir="
                      + getProject().getProperty("WAS.base.dir") +
                      " -Djava.endorsed.dirs="+ getProject().getProperty("WAS.endorsed.dirs") +
                      " -DsimplicityConfigProps=" + getProject().getProperty("simplicityConfigProps")
                      + " -DbootstrappingPropsFile=" + getProject().getProperty("bootstrappingPropsFile"));
        try {
            if (Machine.getLocalMachine().getOperatingSystem() == OperatingSystem.ZOS) {
                vmArgs.append(" -Dfile.encoding=" + AppConst.DEFAULT_ENCODING + " -Xnoargsconversion");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new BuildException(e1);
        }
        Commandline.Argument vmArguments = java.createJvmarg();
        vmArguments.setLine(vmArgs.toString());
        if (verbose) {
            java.createArg().setValue("-verbose");
        }
        if (java.executeJava() != 0) {
            if (failOnError) {
                throw new BuildException("Execution of ANT Task failed");
            } else if (errorLog != null) {
                FileOutputStream fos = null;
                OutputStreamWriter osw = null;
                PrintWriter out = null;
                try {
                    fos = new FileOutputStream(errorLog, true);
                    osw = new OutputStreamWriter(fos, AppConst.DEFAULT_ENCODING);
                    out = new PrintWriter(osw);
                    Calendar cal = Calendar.getInstance();
                    Date date = cal.getTime();
                    out.println(date + " : Failed running file : " + file.getAbsolutePath().replace('\\', '/'));
                } catch (Exception e) {
                    e.printStackTrace();
                    // let it go!
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (osw != null) {
                            osw.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method builds the file name to use in conjunction with directories.
     *
     * <p>Defaults to "build.xml".
     * If <code>genericantfile</code> is set, this attribute is ignored.</p>
     *
     * @param  antfile the short build file name. Defaults to "build.xml".
     */
    public void setAntfile(String antfile) {
        this.antfile = antfile;
    }

    /**
     * This method builds a file path to use in conjunction with directories.
     *
     * <p>Use <code>genericantfile</code>, in order to run the same build file
     * with different basedirs.</p>
     * If this attribute is set, <code>antfile</code> is ignored.
     *
     * @param afile (path of the generic ant file, absolute or relative to
     *               project base directory)
     * */
    public void setGenericAntfile(File afile) {
        this.genericantfile = afile;
    }

    /**
     * Sets whether to fail with a build exception on error, or go on.
     *
     * @param  failOnError the new value for this boolean flag.
     */
    public void setFailonerror(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * The target to call on the different sub-builds. Set to "" to execute
     * the default target.
     * @param target the target
     * <p>
     */
    //     REVISIT: Defaults to the target name that contains this task if not specified.
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Enable/ disable verbose log messages showing when each sub-build path is entered/ exited.
     * The default value is "false".
     * @param on true to enable verbose mode, false otherwise (default).
     */
    public void setVerbose(boolean on) {
        this.verbose = on;
    }

    /**
     * Corresponds to <code>&lt;ant&gt;</code>'s
     * <code>output</code> attribute.
     *
     * @param  s the filename to write the output to.
     */
    public void setOutput(String s) {
        this.output = s;
    }

    /**
     * Corresponds to <code>&lt;ant&gt;</code>'s
     * <code>inheritall</code> attribute.
     *
     * @param  b the new value for this boolean flag.
     */
    public void setInheritall(boolean b) {
        this.inheritAll = b;
    }

    /**
     * Corresponds to <code>&lt;ant&gt;</code>'s
     * <code>inheritrefs</code> attribute.
     *
     * @param  b the new value for this boolean flag.
     */
    public void setInheritrefs(boolean b) {
        this.inheritRefs = b;
    }

    /**
     * Corresponds to <code>&lt;ant&gt;</code>'s
     * nested <code>&lt;property&gt;</code> element.
     *
     * @param  p the property to pass on explicitly to the sub-build.
     */
    public void addProperty(Property p) {
        properties.addElement(p);
    }

    /**
     * Corresponds to <code>&lt;ant&gt;</code>'s
     * nested <code>&lt;reference&gt;</code> element.
     *
     * @param  r the reference to pass on explicitly to the sub-build.
     */
    public void addReference(Ant.Reference r) {
        references.addElement(r);
    }

    /**
     * Corresponds to <code>&lt;ant&gt;</code>'s
     * nested <code>&lt;propertyset&gt;</code> element.
     * @param ps the propertset
     */
    public void addPropertyset(PropertySet ps) {
        propertySets.addElement(ps);
    }

    /**
     * Adds a directory set to the implicit build path.
     * <p>
     * <em>Note that the directories will be added to the build path
     * in no particular order, so if order is significant, one should
     * use a file list instead!</em>
     *
     * @param  set the directory set to add.
     */
    public void addDirset(DirSet set) {
        getBuildpath().addDirset(set);
    }

    /**
     * Adds a file set to the implicit build path.
     * <p>
     * <em>Note that the directories will be added to the build path
     * in no particular order, so if order is significant, one should
     * use a file list instead!</em>
     *
     * @param  set the file set to add.
     */
    public void addFileset(FileSet set) {
        getBuildpath().addFileset(set);
    }

    /**
     * Adds an ordered file list to the implicit build path.
     * <p>
     * <em>Note that contrary to file and directory sets, file lists
     * can reference non-existent files or directories!</em>
     *
     * @param  list the file list to add.
     */
    public void addFilelist(FileList list) {
        getBuildpath().addFilelist(list);
    }

    /**
     * Set the buildpath to be used to find sub-projects.
     *
     * @param  s an Ant Path object containing the buildpath.
     */
    public void setBuildpath(Path s) {
        getBuildpath().append(s);
    }
    
    /**
     * Set the error log to write build failures to
     * 
     * @param errorLog The full path to the error log to write to
     */
    public void setErrorLog(String errorLog) {
    	this.errorLog = errorLog;
    }

    /**
     * Creates a nested build path, and add it to the implicit build path.
     *
     * @return the newly created nested build path.
     */
    public Path createBuildpath() {
        return getBuildpath().createPath();
    }

    /**
     * Creates a nested <code>&lt;buildpathelement&gt;</code>,
     * and add it to the implicit build path.
     *
     * @return the newly created nested build path element.
     */
    public Path.PathElement createBuildpathElement() {
        return getBuildpath().createPathElement();
    }

    /**
     * Gets the implicit build path, creating it if <code>null</code>.
     *
     * @return the implicit build path.
     */
    private Path getBuildpath() {
        if (buildpath == null) {
            buildpath = new Path(getProject());
        }
        return buildpath;
    }

    /**
     * Buildpath to use, by reference.
     *
     * @param  r a reference to an Ant Path object containing the buildpath.
     */
    public void setBuildpathRef(Reference r) {
        createBuildpath().setRefid(r);
    }
    
    /**
	 * Set the parameter name that the build file will be passed to
	 * the target as
	 * 
	 * @param param The name of the parameter for the build file
	 */
    public void setParam(String param) {
    	this.param = param;
    }
    
    public void setFork(boolean fork) {
        this.fork = fork;
    }

    /**
     * Assigns an Ant property to another.
     *
     * @param  to the destination property whose content is modified.
     * @param  from the source property whose content is copied.
     */
    private static void copyProperty(Property to, Property from) {
        to.setName(from.getName());

        if (from.getValue() != null) {
            to.setValue(from.getValue());
        }
        if (from.getFile() != null) {
            to.setFile(from.getFile());
        }
        if (from.getResource() != null) {
            to.setResource(from.getResource());
        }
        if (from.getPrefix() != null) {
            to.setPrefix(from.getPrefix());
        }
        if (from.getRefid() != null) {
            to.setRefid(from.getRefid());
        }
        if (from.getEnvironment() != null) {
            to.setEnvironment(from.getEnvironment());
        }
        if (from.getClasspath() != null) {
            to.setClasspath(from.getClasspath());
        }
    }
    
    /**
     * Log a build failure to the error log
     * 
     * @param msg The message to log
     * @param file The build file to log
     */
    private void logBuildFailure(String msg, File file) {
    	if(errorLog != null) {
        	FileOutputStream fos = null;
            OutputStreamWriter osw = null;
            PrintWriter out = null;
            try {
                fos = new FileOutputStream(errorLog, true);
                osw = new OutputStreamWriter(fos,
                                             AppConst.DEFAULT_ENCODING);
                out = new PrintWriter(osw);
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                out.println(date + " : " + msg + " : " + file.getAbsolutePath());
            } catch(Exception e) {
                e.printStackTrace();
                // let it go!
            } finally {
                try {
                    if (out != null) { out.close(); }
                    if (osw != null) { osw.close(); }
                    if (fos != null) { fos.close(); }
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
    	}
    }

    /**
     * Creates the &lt;ant&gt; task configured to run a specific target.
     *
     * @param directory : if not null the directory where the build should run
     *
     * @return the ant task, configured with the explicit properties and
     *         references necessary to run the sub-build.
     */
    private Ant createAntTask(File directory) {
        Ant ant = (Ant) getProject().createTask("ant");
        ant.setOwningTarget(getOwningTarget());
        ant.setTaskName(getTaskName());
        ant.init();
        if (target != null && target.length() > 0) {
            ant.setTarget(target);
        }


        if (output != null) {
            ant.setOutput(output);
        }

        if (directory != null) {
            ant.setDir(directory);
        }

        ant.setInheritAll(inheritAll);
        for (Enumeration i = properties.elements(); i.hasMoreElements();) {
            copyProperty(ant.createProperty(), (Property) i.nextElement());
        }

        for (Enumeration i = propertySets.elements(); i.hasMoreElements();) {
            ant.addPropertyset((PropertySet) i.nextElement());
        }

        ant.setInheritRefs(inheritRefs);
        for (Enumeration i = references.elements(); i.hasMoreElements();) {
            ant.addReference((Ant.Reference) i.nextElement());
        }

        return ant;
    }


} // END class SubAnt
