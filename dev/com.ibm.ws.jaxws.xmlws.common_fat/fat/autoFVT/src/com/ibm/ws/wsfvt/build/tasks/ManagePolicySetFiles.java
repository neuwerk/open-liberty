/*
 * @(#) 1.5 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ManagePolicySetFiles.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/21/09 08:52:05 [8/8/12 06:40:29]
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
 * 06/18/07    jramos      446691             New File
 * 10/29/2008  jramos      559143             Incorporate Simplicity
 * 05/21/2009  jramos      592238             Correct config file path
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

/**
 * The ManagePolicyFiles class is a custom task for managing policy files
 * 
 * The class conforms to the standards set in the Ant build framework for
 * creating custom tasks.
 */
public class ManagePolicySetFiles extends Task {

    private Path buildpath;
    private boolean copyfiles = true;
    private Node node = null;
    private String relativeDestDir = null;
    private String destDirPrefix = null;
    private boolean failonerror = true;

    public void execute() throws BuildException {
        if (buildpath == null) {
            throw new BuildException("No buildpath specified");
        }

        if (relativeDestDir == null) {
            throw new BuildException("No relative destination directory specified");
        }

        final String[] filenames = buildpath.list();
        final int count = filenames.length;
        if (count < 1) {
            log("No policy set files to copy.", Project.MSG_WARN);
            return;
        }

        try {
            // get the node of the cell manager (root node)
            node = TopologyDefaults.getDefaultAppServer().getCell().getManager().getNode();
            destDirPrefix = node.expandString(node.getCell().getConfigPath());
            
            for (int i = 0; i < count; ++i) {
                if (copyfiles) {
                    copyPolicySetFile(new File(filenames[i]));
                } else {
                    deletePolicySetFile(new File(filenames[i]));
                }
            }
        } catch (Exception e) {
            if(failonerror)
                throw new BuildException(e);
        }

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
     * Copy a policy set file to the configuration directory of the root node
     * 
     * @param file
     *            The file to copy
     * @throws Exception
     */
    private void copyPolicySetFile(File file) throws Exception {
        RemoteFile source = Machine.getLocalMachine().getFile(file.getAbsolutePath().replace('\\', '/'));
        RemoteFile dest = node.getMachine().getFile(destDirPrefix + relativeDestDir + "/" + file.getName());
        dest.getParentFile().mkdirs();
        source.copyToDest(dest);
    }

    /**
     * Delete a policy set file from the configuration directory of the root
     * node
     * 
     * @param file
     *            The file to copy
     * @throws Exception
     */
    private void deletePolicySetFile(File file) throws Exception {
        RemoteFile rfile = node.getMachine().getFile(destDirPrefix + relativeDestDir + "/" + file.getName());
        rfile.delete();
    }

    public void setCopyfiles(boolean copyfiles) {
        this.copyfiles = copyfiles;
    }

    /**
     * Adds a file set to the implicit build path.
     * <p>
     * <em>Note that the directories will be added to the build path
     * in no particular order, so if order is significant, one should
     * use a file list instead!</em>
     * 
     * @param set
     *            the file set to add.
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
     * @param list
     *            the file list to add.
     */
    public void addFilelist(FileList list) {
        getBuildpath().addFilelist(list);
    }

    /**
     * Set the relative destination directory. This is the path relative to
     * rootNodeProfileDir/config/cells/cellName. For example, if the relative
     * destination directory is set to
     * /PolicySets/BasicBA/PolicyTypes/WSSecurity, the policy set files will be
     * copied to or deleted from
     * rootNodeProfileDir/config/cells/cellName/PolicySets/BasicBA/PolicyTypes/WSSecurity
     * 
     * @param relativeDestDir
     *            The path relative the the root nodes config directory
     */
    public void setRelativeDestDir(String relativeDestDir) {
        this.relativeDestDir = relativeDestDir;
    }
    
    public void setFailonerror(boolean failonerror) {
        this.failonerror = failonerror;
    }
}
