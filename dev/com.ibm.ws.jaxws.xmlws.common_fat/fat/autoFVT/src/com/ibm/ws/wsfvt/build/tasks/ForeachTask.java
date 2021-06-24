//
// @(#) 1.4 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ForeachTask.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:26 [8/8/12 06:56:43]
//
// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date        UserId      Defect          Description
// ----------------------------------------------------------------------------
// 12/11/2003  ulbricht    D185535         Forking on install/uninstall tasks needs
//                                            some JVM args
// 08/19/2004  ulbricht    D225528         Need extra JVM params for z/OS
// 11/17/2004  ulbricht    D246124         Add fail on error attribute
// 03/04/2005  ulbricht    D261009         Add support for diff profiles
// 03/24/2005  ulbricht    D268461         Add support for diff server names
// 05/02/2005  ulbricht    D272639         Add jvmarg element feature
// 05/10/2005  ulbricht    D273448         Fix WS-Security FVT
// 09/15/2005  ulbricht    305732          Fix deprecation warnings
// 01/15/2007  jramos      413702          Subtarget was not being sent to task
// 05/22/2007  jramos      440922          Changes for Pyxis
// 10/22/07    jramos      476750          Use TopologyDefaults tool and ACUTE 2.0 api
// 10/29/2008  jramos      559143          Incorporate Simplicity
//

/*
* The Apache Software License, Version 1.1
*
* Copyright (c) 1999 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution, if
*    any, must include the following acknowlegement:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowlegement may appear in the software itself,
*    if and wherever such third-party acknowlegements normally appear.
*
* 4. The names "The Jakarta Project", "Ant", and "Apache Software
*    Foundation" must not be used to endorse or promote products derived
*    from this software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache"
*    nor may "Apache" appear in their names without prior written
*    permission of the Apache Group.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation.  For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*/
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.ParamSet;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * Call a target foreach entry in a set of parameters based on a fileset.
 *  <pre>
 *    <target name="target1">
 *      <foreach target="target2">
 *        <param name="param1">
 *            <fileset refid="fset1"/>
 *        </param>
 *        <param name="param2">
 *          <item value="jar" />
 *          <item value="zip" />
 *        </param>
 *       </foreach>
 *    </target>
 *
 *    <target name="target2">
 *      <echo message="prop is ${param1}.${param2}" />
 *    </target>
 * </pre>
 * <br>
 * Really this just a wrapper around "AntCall"
 * <br>
 * Added a "type" attribute that works precisely like its equivalent
 * in <code>ExecuteOn</code>.  It allows the user
 * to specify whether directories, files, or both directories and files
 * from the filesets are included as entries in the parameter set.
 *
 * @author <a href="mailto:tpv@spamcop.net">Tim Vernum</a>
 * @author Davanum Srinivas
 */
public class ForeachTask extends Task {
    private Ant callee;
    private Java callee2;
    private String subTarget;
    private Vector params;
    private Hashtable properties;
    // must match the default value of Ant#inheritAll
    private boolean inheritAll = true;
    // must match the default value of Ant#inheritRefs
    private boolean inheritRefs = false;
    private boolean fork = false;
    private boolean verbose = false;
    // if true - stop foreach from continuing
    private boolean failOnError = true;
    private String errorLog = null;
    private List buildFileJvmArgs = null;

    public ForeachTask() {
        params = new Vector();
        properties = new Hashtable();
    }

    public void init() {
    }

    /**
     * If true, pass all properties to the new Ant project.
     * Defaults to true.
     */
    public void setInheritAll(boolean inherit) {
       inheritAll = inherit;
    }

    /**
     * If true, pass all references to the new Ant project.
     * Defaults to false
     * @param inheritRefs new value
     */
    public void setInheritRefs(boolean inheritRefs) {
        this.inheritRefs = inheritRefs;
    }

    /**
     * Target to execute, required.
     */
    public void setTarget(String target) {
        subTarget = target;
    }

    /**
     * If true, forks the ant invocation.
     *
     * @param f "true|false|on|off|yes|no"
     */
    public void setFork(boolean f) {
        fork = f;
    }

    /**
     * Enable verbose output when signing
     * ; optional: default false
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * This attribute is only used when using forked
     * mode.  This will allow the foreach task to continue
     * iterating even though the current iteration has
     * failed.  The default is true which causes the foreach
     * to stop and throw a BuildException.
     *
     * @param failOnError default is true which causes
     *                    foreach to stop and throw
     *                    BuildException
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * This method will set the log file where build
     * errors will be written when the failOnError
     * attribute is set to true.
     *
     * @param errorLog The path and file for error log
     */
    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

    public ParamSet createParam() {
        ParamSet param = new ParamSet();
        params.addElement(param);
        return param;
    }

    private void buildProperty(String propName, String propValue) {
        properties.put(propName, propValue);
    }

    private void executeTarget() {
        if (subTarget == null) {
            throw new BuildException("Attribute target is required.",
                                     getLocation());
        }
        if(fork) {
            executeForkedAntTask();
        } else {
            executeAntTask();
        }
    }

    private void executeForkedAntTask() {
        if (callee == null) {
            callee2 = (Java) getProject().createTask("java");
            callee2.setOwningTarget(getOwningTarget());
            callee2.setTaskName(getTaskName());
            callee2.setLocation(getLocation());
            callee2.setClassname("org.apache.tools.ant.Main");
            callee2.setAppend(true);
            callee2.setFork(true);
        }
        String systemClassPath = System.getProperty("java.class.path");
        callee2.setClasspath(new Path(getProject(), systemClassPath));
        String args = "";
        if((new File((String) properties.get("file"))).isFile())
        	args += "-buildfile " + properties.get("file");
        args += " " +subTarget+" -Dfile="+properties.get("file")+" -DinstallTest="+properties.get("installTest")+" -DuninstallTest="+properties.get("uninstallTest");
        Commandline.Argument arguments = callee2.createArg();
        arguments.setLine(args);
        StringBuffer vmArgs = new StringBuffer();
        if (buildFileJvmArgs != null && !buildFileJvmArgs.isEmpty()) {
            for (int i = 0; i < buildFileJvmArgs.size(); i++) {
                vmArgs.append(" " + (String)buildFileJvmArgs.get(i));
            }
        } else {
            // This was added to prevent other components from having
            // to modify their build files to add the jvmarg elements.
            // New jvmargs should be specified in the build file
            // instead of adding them into here.
            vmArgs.append("-DFVT.base.dir="
                          + getProject().getProperty("FVT.base.dir")
                          + " -DWAS.base.dir="
                          + getProject().getProperty("WAS.base.dir")
                          + " -Dprofile="
                          + getProject().getProperty("profile")
                          + " -Dserver="
                          + getProject().getProperty("server.name"));

        }
        try {
			if (Machine.getLocalMachine().getOperatingSystem() == OperatingSystem.ZOS) {
				vmArgs.append(" -Dfile.encoding=" + AppConst.DEFAULT_ENCODING
						+ " -Xnoargsconversion");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new BuildException(e1);
		}
        Commandline.Argument vmArguments = callee2.createJvmarg();
        vmArguments.setLine(vmArgs.toString());
        if (verbose) {
            callee2.createArg().setValue("-verbose");
        }
        if (callee2.executeJava() != 0) {
            if (failOnError) {
                throw new BuildException("Execution of ANT Task failed");
            } else if (errorLog != null) {
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
                    out.println(date + " : Failed running file : "
                                + properties.get("file"));
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
    }

    private void executeAntTask() {
        if (callee == null) {
            callee = (Ant) getProject().createTask("ant");
            callee.setOwningTarget(getOwningTarget());
            callee.setTaskName(getTaskName());
            callee.init();
        }

        callee.setAntfile(getProject().getProperty("ant.file"));
        callee.setTarget(subTarget);
        callee.setInheritAll(inheritAll);
        callee.setInheritRefs(inheritRefs);
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String val = (String) properties.get(key);
            Property prop = callee.createProperty();
            prop.setName(key);
            prop.setValue(val);
        }
        callee.execute();
        System.gc();
        System.gc();
        System.gc();
    }

    /**
     * This method is used to recursively iterate through
     * each parameter set.
     * It ends up being something like:
     * <pre>
     *    for( i=0; i< params[0].size ; i++ )
     *       for( j=0; j < params[1].size ; j++ )
     *          for( k=0; k < params[2].size ; k++ )
     *             executeTarget( params[0][i], params[1][j] , params[2][k] ) ;
     * </pre>
     */
    private void executeParameters(int paramNumber) {
        if (paramNumber == params.size()) {
            executeTarget();
        } else {
            ParamSet paramSet = (ParamSet) params.elementAt(paramNumber);
            Enumeration values = paramSet.getValues(getProject());
            while (values.hasMoreElements()) {
                String val = (String) values.nextElement();
                buildProperty(paramSet.getName(), val);
                executeParameters(paramNumber + 1);
            }
        }
    }

    public void execute() {
        if (subTarget == null) {
            throw new BuildException("Attribute target is required.", getLocation());
        }
        executeParameters(0);
    }

    /**
     * This is a method to allow using an element
     * with a tag name of <jvmarg> to pass JVM
     * args to the new Ant command when forking
     * is enabled.
     *
     * @return Contains the JVM arg
     */
    public JvmArg createJvmArg() {
        return new JvmArg();
    }

    /**
     * This is an inner class to facilitate the use
     * the <jvmarg> element.
     */
    public class JvmArg {

        private String value = null;

        /**
         * A setter for the JVM arg value.
         *
         * @param _value The JVM arg
         */
        public void setValue(String _value) {
            value = _value;
            addJvmArg();
        }

        /**
         * This method will add the JVM arg to a
         * collection object that will later be used
         * to form the Ant command when forking is
         * enabled.
         */
        private void addJvmArg() {
            if (value != null) {
                if (buildFileJvmArgs == null) {
                    buildFileJvmArgs = new ArrayList();
                }
                buildFileJvmArgs.add(value);
            }
        }

    }

}
