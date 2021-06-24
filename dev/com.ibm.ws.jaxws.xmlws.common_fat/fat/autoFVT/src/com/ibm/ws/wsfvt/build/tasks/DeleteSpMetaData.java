/*
 * @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/DeleteSpMetaData.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01 10/12/11 15:27:47 [8/8/12 06:33:12]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * ----------------------------------------------------------------------------
 * 09/26/11    syed        718227             Created for SAML Web SSO tests
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.wsfvt.build.tools.AppConst;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.websphere.simplicity.ConnectorType;
import com.ibm.websphere.simplicity.ConnectionInfo;

/**
 * The DeleteSpMetaData class is a custom task that copied the SP meta data file
 * from the local SP machine to the remote TFIM IdP.
 * The class conforms to the standards set in the Ant build framework for 
 * creating custom tasks.
 */
public class DeleteSpMetaData extends Task {


    // IdP properties, filled in by set methods

    private String spHostname =    "";
    private String idpHostname =   "";
    private String idpPortnum =    "";
    private String idpConnType =   "";
    private String idpAdminID =    "";
    private String idpAdminPswd =  "";

    public void execute() throws BuildException {

        try {

            Node node = TopologyDefaults.getDefaultAppServer().getNode();
            File localDir = new File(AppConst.FVT_HOME + "/build/work/samlwebsso/spmetadata");

            Machine remhost = null;
            Integer idpPort = new Integer(idpPortnum);
            ConnectorType remConnType =  ConnectorType.valueOf(idpConnType);

            ConnectionInfo cinfo = new ConnectionInfo(remConnType,
                                   idpHostname, idpPort,
                                   idpAdminID,  idpAdminPswd);

            remhost = Machine.getMachine(cinfo);

            String destFile = "/tmp/" + spHostname + ".xml";
            RemoteFile dest = new RemoteFile(remhost, destFile);

            dest.delete();

        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public void setSpHostname(String spHostname) {
        this.spHostname = spHostname;
    }

    public void setIdpHostname(String idpHostname) {

        this.idpHostname = idpHostname;
    }

    public void setIdpPortnum(String idpPortnum) {
        this.idpPortnum = idpPortnum;
    }

    public void setIdpConnType(String idpConnType) {
        this.idpConnType = idpConnType;
    }

    public void setIdpAdminID(String idpAdminID) {
        this.idpAdminID = idpAdminID;
    }

    public void setIdpAdminPswd(String idpAdminPswd) {
        this.idpAdminPswd = idpAdminPswd;
    }

}
