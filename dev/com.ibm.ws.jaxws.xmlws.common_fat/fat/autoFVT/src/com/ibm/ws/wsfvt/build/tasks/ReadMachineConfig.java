package com.ibm.ws.wsfvt.build.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Topology;

/**
 * Like ReadCellConfig only for Machines
 *
 */
public class ReadMachineConfig extends Task {

    private String hostname = null;
    private String prefix = null;
    private boolean isVerbose = false;
    
    public void execute() throws BuildException {
        if(hostname == null) {
            throw new BuildException("hostname cannot be null");
        }
        if (prefix == null || prefix.length() == 0) {
            // If they don't specify a prefix, we make it the hostname
            prefix = hostname + ".";
        } else {
            // Append the "." (dot) at the end to allow for easy usability
            if (!prefix.endsWith(".")) {
                prefix = prefix + ".";
            }
        }
        
        try {
            ConnectionInfo connInfo = Topology.getBootstrapFileOps().getMachineConnectionInfo(hostname);
            Machine m = Machine.getMachine(connInfo);
            setProperty(prefix + "osVersion", m.getOSVersion());
            if(m.getConnInfo().getUser() != null) {
                setProperty(prefix + "user", m.getUsername());
            } else if(isVerbose){
                // can't put null values in a Hashtable
                log("user is null");
            }
            if(m.getConnInfo().getKeystore() != null) {
                setProperty(prefix + "keystore", m.getConnInfo().getKeystore().getAbsolutePath());
            } else if(isVerbose){
                log("keystore is null");
            }
            if(m.getConnInfo().getPassword() != null) {
                setProperty(prefix + "password", m.getPassword());
            } else if(isVerbose){
                log("password is null");
            }
            setProperty(prefix + "rawOSName", m.getRawOSName());
            setProperty(prefix + "tempDir", m.getTempDir().getAbsolutePath());
            setProperty(prefix + "OS", "" + m.getOperatingSystem());
            setProperty(prefix + "defaultEncoding", m.getOperatingSystem().getDefaultEncoding());
            setProperty(prefix + "defaultScriptSuffix", m.getOperatingSystem().getDefaultScriptSuffix());
            setProperty(prefix + "fileSep", m.getOperatingSystem().getFileSeparator());
            setProperty(prefix + "envVarSetter", m.getOperatingSystem().getEnvVarSet());
            setProperty(prefix + "lineSep", m.getOperatingSystem().getLineSeparator());
            setProperty(prefix + "pathSep", m.getOperatingSystem().getPathSeparator());
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        }
    }
    
    private void setProperty(String name, String value) {
        if(isVerbose) {
            log(name + "=" + value);
        }
        getProject().setProperty(name, value);
    }

    public void setVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
