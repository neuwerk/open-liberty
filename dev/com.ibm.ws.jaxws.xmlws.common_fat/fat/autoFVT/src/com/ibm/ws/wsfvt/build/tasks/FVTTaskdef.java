/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

import org.apache.tools.ant.taskdefs.Taskdef;
import java.util.Properties;

/**
 * Adds a task definition to the current project, such that this new task can be
 * used in the current project. Two attributes are needed, the name that identifies
 * this task uniquely, and the full name of the class (including the packages) that
 * implements this task.</p>
 * <P>This FVTTaskdef is trying to prevent duplicated ClassLoader and class instance</p>
 * <p>You can also define a group of tasks at once using the file or
 * resource attributes.  These attributes point to files in the format of
 * Java property files.  Each line defines a single task in the
 * format:</p>
 * <pre>
 * taskname=fully.qualified.java.classname
 * </pre>
 * @since Ant 1.1
 * @ant.task category="internal"
 */
public class FVTTaskdef extends Taskdef {

    static Properties props = new Properties();

    public FVTTaskdef() {
        super();
    }

    // Override the super.createLoader
    protected ClassLoader createLoader(){
        String strKey = "FVTTaskdef." + getName() + ".classloader";
        // System.out.println( "FVTTaskdef:" + strKey ); // for debugging. ToBeRemoved
        ClassLoader clRet = (ClassLoader)props.get( strKey );
        if( clRet != null ) return clRet;
        // System.out.println( "********:" + strKey ); // for debugging. ToBeRemoved
        clRet = super.createLoader();
        props.put( strKey, clRet);
        return clRet;
    }

}
