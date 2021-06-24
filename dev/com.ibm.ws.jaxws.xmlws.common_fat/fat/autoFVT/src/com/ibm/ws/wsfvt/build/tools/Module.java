/*
 * @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tools/Module.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/22/07 07:27:08 [8/8/12 06:56:48]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 05/13/2003  ulbricht    D163811            Add module description
 *
 */
package com.ibm.ws.wsfvt.build.tools;

/**
 * The module class contains the basic attributes
 * of a module, such as its name, description and uri.
 */
public class Module {

    protected String moduleName = "";
    protected String moduleDesc = "";
    protected String uri = "";

    /**
     * A no arg constructor to create a Module object.
     */
    public Module() {
    }

    /**
     * A two arg constructor to create a Module object.
     *
     * @param moduleName A String containing the module name
     * @param uri A String containing the module uri
     */
    public Module(String moduleName, String uri) {
        this.moduleName = moduleName;
        this.uri = uri;
    }

    /**
     * A three arg constructor to create a Module object.
     *
     * @param moduleName A String containing the module name
     * @param uri A String containing the module uri
     * @param moduleDesc A String containing the module description
     */
    public Module(String moduleName, String uri, String moduleDesc) {
        this(moduleName, uri);
        this.moduleDesc = moduleDesc;
    }

    /**
     * A setter method for the module name.
     * 
     * @param moduleName A String containing the name of the module
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * A getter method for the module name.
     *
     * @return A String containing the name of the module
     */
    public String getModuleName() {
        return this.moduleName;
    }

    /**
     * A setter method for the URI.
     *
     * @param uri A String containing the name of the URI 
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * A getter method for the URI.
     *
     * @return A String containing the name of the URI
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * A setter method for the module name.
     * 
     * @param moduleDesc A String containing the module desc
     */
    public void setModuleDesc(String moduleDesc) {
        this.moduleDesc = moduleDesc;
    }

    /**
     * A getter method for the module description.
     *
     * @return A String containing the name of the module description
     */
    public String getModuleDesc() {
        return this.moduleDesc;
    }

}
