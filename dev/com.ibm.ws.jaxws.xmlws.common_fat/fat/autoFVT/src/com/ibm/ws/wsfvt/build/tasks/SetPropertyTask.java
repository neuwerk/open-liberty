/*
 * @(#) 1.3 SautoFVT/src/com/ibm/ws/wsfvt/build/tasks/SetPropertyTask.java, WAS.websvcs.fvt, SAML10.SFVT, b0909.04 1/28/09 14:08:45 [3/4/09 22:00:45]
 *
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
 * 12/14/2007  gkuo        create             See the description of the class
 */
package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * The SetPropertyTask has
 *   propFile    -- the pathname of the properties file
 *   propKey     -- The key to get the value in the property file
 *                  The key can be get from ReadCellConfig
 *                  Such as:  <readCellConfig prefix="server1" key="defaultServer" verbose="false"/>
 *   property    -- The property to be set in the ANT
 * Such as:
 *   <setProperty propFile="${FVT.base.dir}/src/wssecfvt/kerberos/kdc.props"
 *                propKey="${server1hostName}"
 *                property="kdcSPn"/> 
 *    This will read in the properties in ${FVT.base.dir}/src/wssecfvt/kerberos/kdc.props
 *    Get the value with the default-server1-hostname
 *    then set the property "kdcSPN" to it
 * 
 */
public class SetPropertyTask extends Task {

    private String strPropFile = null;
    private String strPropKey  = null;
    private String strProperty = null;

    /**
     * See the description of this class
     *
     * @throws BuildException Any kind of BuildException
     */
    public void execute() throws BuildException {
        File file = new File( strPropFile );
        if( file.exists() ){
            Properties props = new Properties();
            try{
                FileInputStream input = new FileInputStream( file);
                props.load( input );
                input.close();
            } catch( Exception e){
                e.printStackTrace(System.out);
            }
            String value = (String)props.getProperty( strPropKey );
            if( value == null ){
                String strPropKey1 = strPropKey.toLowerCase();
                value = (String)props.getProperty( strPropKey1 );
                if( value == null ){
                    System.out.println("ERROR: " + strProperty + " does not set, because File '" + 
                                       strPropFile + "' does not have the value of '" + strPropKey + "'" );
                    value = "ERROR: Possible Kerberos Is Not Set Up Properly (no " + strPropKey1 +
                            " on file "+ file.getAbsolutePath() + ")";
                }
            }
            System.out.println( "set property '" + strProperty + " to '" + value + "'" );
            setProjectProp( strProperty, value);

        }  else{ // error properties file does not exist
            System.out.println( "ERROR: " + strPropFile + " does not exist" );
            throw new BuildException("ERROR: " + strPropFile + " does not exist" ); 
        }

    }

    /**
     * See description of the class
     */
    public void setPropFile(String file) {
        strPropFile = file;
    }

    /**
     * See description of the class
     */
    public void setPropKey(String key) {
        strPropKey = key;
    }

    /**
     * See description of the class
     */
    public void setProperty(String property) {
        strProperty = property;
    }

    private void setProjectProp(String name, String value){
            Project p = getProject();
            p.setProperty(name, value);
    }

}
