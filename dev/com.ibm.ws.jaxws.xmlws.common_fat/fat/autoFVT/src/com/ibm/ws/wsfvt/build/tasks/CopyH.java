/*
 * @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/CopyH.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:06:08 [8/8/12 06:06:52]
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
 * 10/22/2008  jramos      559143          Incorporate Simplicity
 *
 */

package com.ibm.ws.wsfvt.build.tasks;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.ws.wsfvt.build.types.MyFileSet;

/**
 *  This CopyH is written to comply with the multi-machine copy
 *  I did not intend to implement it to full functions.
 *  So, quite some detail functions are not handled, such as: filter
 *
 *  In brief, at the writing moment, the CopyH only handle
 *   copy local file or myfileset to the remote machine.
 *   Or remote file or myfileset to local machine
 *  ** myfileset is a simple fileset which can handle dir attribute and simple include for now...
 *
 *  You can substitute the ANT Copy with this CopyH. At the writing moment, it ought to do
 *  the same as ANT Copy if no remoteHost are specified.
 *
 */
public class CopyH extends Copy {

    ArrayList<MyFileSet> _listMyFileSet = new ArrayList<MyFileSet>( 5 );
    String _fvtHostname  = common.utils.topology.TopologyActions.FVT_HOSTNAME;
    String _toHostname   = common.utils.topology.TopologyActions.FVT_HOSTNAME;
    String _fromHostname = common.utils.topology.TopologyActions.FVT_HOSTNAME;
    String _toDir = "";

    /**
     * CopyH task constructor.
     */
    public CopyH() {
        super();
    }


    /**
     * Performs the copy operation.
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {
        if( _toHostname  .equalsIgnoreCase( _fvtHostname ) &&
            _fromHostname.equalsIgnoreCase( _fvtHostname )   ){
            for( MyFileSet mft : _listMyFileSet ){
                FileSet fs = mft.transferToFileSet();
                addFileset( fs );
            }
            super.execute();
            return;
        }
        if( (!_toHostname  .equalsIgnoreCase( _fvtHostname )) &&
            (!_fromHostname.equalsIgnoreCase( _fvtHostname ))   ){
            throw new BuildException( "One of the from or to Hostanme must be localhost for now");
        }


        _toDir = destDir.getAbsolutePath();
        if( !_toHostname.equalsIgnoreCase( _fvtHostname )){
            ConnectionInfo connInfo = null;
            Machine machine = null;
            OperatingSystem os = null;
            try {
                connInfo = Topology.getBootstrapFileOps().getMachineConnectionInfo(_toHostname);
                machine = Machine.getMachine(connInfo);
                os = machine.getOperatingSystem();
            } catch (Exception e1) {
                throw new BuildException(e1);
            }
            debug( os.toString() );
            _toDir = destDir.getAbsolutePath();
            if( os != OperatingSystem.WINDOWS ){
                if( _toDir.indexOf( ":" ) == 1 ){
                    _toDir = _toDir.substring( 2 );
                }
                _toDir = _toDir.replace( "\\", "/" );
            }
            debug( " todir : '" + _toDir + "'" );
            // destFile
            // testing
            for( MyFileSet mft : _listMyFileSet ){
                ArrayList<String> listFilePath = mft.getLocalFileList();
                String strDirPath = mft.getDirPath();
                int iDirLen = strDirPath.length();
                for( String str1 : listFilePath ){
                    debug( "*****  '" + str1       + "'" );
                    String strFN = str1.substring( iDirLen );
                    if( os != OperatingSystem.WINDOWS ){
                        strFN = strFN.replace( "\\", "/" );
                    }
                    debug( _toDir + strFN );
                    try{
                        RemoteFile local = Machine.getLocalMachine().getFile(str1);
                        RemoteFile dest = machine.getFile(_toDir + strFN);
                        local.copyToDest(dest);
                        System.out.println( "copied " + _toDir + strFN );
                    } catch( Exception e ){
                        debug( "Execption :'" + e + "'" );
                        try{
                            StringTokenizer st = new StringTokenizer( strFN, "\\/" );
                            StringBuffer sb = new StringBuffer();
                            while( st.hasMoreTokens() ){
                                sb.append( "/" + (String)st.nextToken() );
                                String strDir1 = sb.toString();
                                try{
                                    if( st.hasMoreTokens() ){
                                        machine.getFile(_toDir + strDir1).mkdirs();
                                        System.out.println( "**created " + _toDir + strDir1 );
                                    }
                                } catch( Exception ee2 ){
                                }
                            }
                            RemoteFile local = Machine.getLocalMachine().getFile(str1);
                            RemoteFile dest = machine.getFile(_toDir + strFN);
                            local.copyToDest(dest);
                            System.out.println( "**copied " + _toDir + strFN );
                        } catch( Exception ee1 ){ 
                        }
                    }
                }
            }
        }  else { // (_fromHostname.equalsIgnoreCase( _fvtHostname )) from host is not local
            try{
                //ListRemoteDirectory execList = new ListRemoteDirectory();
                for( MyFileSet mft : _listMyFileSet ){
                    debug( "calling copy Remote files" );
                    ArrayList<String> listCopied = mft.copyRemoteFiles( _fromHostname, _toHostname, _toDir );
                    for( String strDelFN : listCopied ){
                        System.out.println( "copied '" + strDelFN );
                    }
                }
             } catch( Exception ee ){ 
                 System.out.println( "Hit ExecutionException:'" + ee.getMessage() + "'" );
             }

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

    public void setToHostname(String toHostname) {
        debug( "setToHostname:" + toHostname );
        _toHostname = toHostname;
    }

    public void setFromHostname(String fromHostname) {
        debug( "setFromHostname:" + fromHostname );
        _fromHostname = fromHostname;
    }

    public static final boolean _bDebug = false;
    protected void debug( String str ){
        if( _bDebug ){
            System.out.println( "CopyH     --" + str  );
        }
    }

}

