/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/DeleteH.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:21 [8/8/12 06:06:52]
 *   
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect  Description
 * -----------------------------------------------------------------------------
 * 04/15/2007  gkuo                        New File
 * 10/23/2008  jramos      559143         Incorporate Simplicity
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.FileSet;

import com.ibm.ws.wsfvt.build.types.MyFileSet;

/**
 *  This DeleteH is written to comply with the multi-machine copy
 *  I did not intend to implement it to full functions.
 *  So, quite some detail functions are not handled, such as: filter
 *
 *  In brief, at the writing moment, the DeleteH only handle
 *   copy local file or myfileset to the remote machine.
 *   Or remote file or myfileset to local machine
 *  ** myfileset is a simple fileset which can handle dir attribute and simple include for now...
 *
 *  You can substitute the ANT Delete with this DeleteH. At the writing moment, it ought to do
 *  the same as ANT Delete if no remoteHost are specified.
 *
 */
public class DeleteH extends Delete {

    ArrayList<MyFileSet> _listMyFileSet = new ArrayList<MyFileSet>( 5 );
    String _fvtHostname = common.utils.topology.TopologyActions.FVT_HOSTNAME;
    String _hostname  = common.utils.topology.TopologyActions.FVT_HOSTNAME;

    /**
     * DeleteH task constructor.
     */
    public DeleteH() {
        super();
    }


    /**
     * Performs the copy operation.
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {
        if( _hostname.equalsIgnoreCase( _fvtHostname ) ){
            for( MyFileSet mft : _listMyFileSet ){
                FileSet fs = mft.transferToFileSet();
                addFileset( fs );
            }
            super.execute();
            return;
        }
        //ListRemoteDirectory execList = new ListRemoteDirectory();
        try {
            for( MyFileSet mft : _listMyFileSet ){
                debug( "calling delete Remote files" );
                ArrayList<String> listDeleted = mft.deleteRemoteFiles( _hostname );
                for( String strDelFN : listDeleted ){
                    System.out.println( "deleted '" + strDelFN );
                }
            }
        } catch(Exception e) {
            throw new BuildException(e);
        }
    }


    /**
     * Add a name entry to the listMyFileSet list.
     * @return <code>MyFileSet</code>.
     */
    public MyFileSet createMyFileSet() throws BuildException {
        debug( "createMyFileSet" );
        MyFileSet myFileSet = new MyFileSet( this );
        _listMyFileSet.add( myFileSet );
        return myFileSet;
    }

    public void setHostname(String hostname) {
        debug( "setHostname:" + hostname );
        _hostname = hostname;
    }

    public static final boolean _bDebug = CopyH._bDebug;
    protected void debug( String str ){
        if( _bDebug ){
            System.out.println( "DeleteH   --" + str  );
        }
    }

}

