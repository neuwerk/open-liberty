/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/types/MyFileSet.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/13/09 13:17:27 [8/8/12 06:06:52]
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

package com.ibm.ws.wsfvt.build.types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.ws.wsfvt.build.tasks.CopyH;
import com.ibm.ws.wsfvt.build.tasks.DeleteH;

/**
 * Moved out of MatchingTask to make it a standalone object that could
 * be referenced (by scripts for example).
 *
 */
public class MyFileSet extends DataType {

    List<DataType> _listInc = new ArrayList<DataType>(10);
    String   _dirpath = "";
    String   _dir     = "";
    CopyH    _parentC = null;
    DeleteH  _parentD = null;
    boolean  _bSearchSub = false;


    public MyFileSet(CopyH parent) {
        super();
        debug( "MyFileSet:" + parent );
        _parentC = parent;
    }

    public MyFileSet(DeleteH parent) {
        super();
        debug( "MyFileSet:" + parent );
        _parentD = parent;
    }

    public FileSet transferToFileSet()throws BuildException {
        debug( "transferToFileSet:"  );
        FileSet fileSet = new FileSet();
        fileSet.setDir( new File( _dir ) );
        for( DataType dt : _listInc ){
            if( dt instanceof Include ){
                Include inc = (Include)dt;
                debug( " inc:" + dt + " name:" + inc.getName() );
                PatternSet.NameEntry entry = fileSet.createInclude();
                entry.setName( inc.getName());
            }
        }
        return fileSet;
    }



    /**
     * Performs the delete operation.
     * @exception BuildException if an error occurs
     */
    //public ArrayList<String> deleteRemoteFiles( String strHostname, ListRemoteDirectory execList) throws BuildException {
    public ArrayList<String> deleteRemoteFiles( String strHostname) throws Exception {
        ArrayList<String> listDeleted = new ArrayList<String>();
        // IExecution exec = (IExecution)execList;
        Machine         machine = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(strHostname));
        OperatingSystem os      = machine.getOperatingSystem();
        String          sep     = (  os == OperatingSystem.WINDOWS ? "\\" : "/" );
        //execList.setRecurse( true );
        try{
            RemoteFile[] aFN = machine.getFile(_dir).list(true);
            if( aFN != null ){
                for( int iI = 0; iI < aFN.length; iI ++ ){
                    debug( "@@ " + aFN[ iI ]  );
                    ArrayList<String> listDir = new ArrayList<String>();
                    String strFileName = getFileName( getRFN(aFN[ iI ], _dir, sep ), listDir );
                    if( matchFile( strFileName, listDir, _listInc ) ){
                        debug( "  deleting" );
                        String strFile = aFN[ iI ].getAbsolutePath();
                        debug( "  deleting'" + strFile + "'" );
                        try{
                            machine.getFile( strFile ).delete();
                            listDeleted.add( strFile );
                        } catch( Exception ee ){
                            // doing nothing. We might be deleteing some matched Directory
                        }
                    }
                }
            }
        } finally{
            // execList.setRecurse( false );
        }
        return listDeleted;
    }



    //public ArrayList<String> copyRemoteFiles( String strHostname, ListRemoteDirectory execList) throws BuildException {
    public ArrayList<String> copyRemoteFiles( String strFromHostname, String strToHostname  , String toDir) throws Exception {
        ArrayList<String> listCopied = new ArrayList<String>();
        // IExecution exec = (IExecution)execList;
        Machine from = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(strFromHostname));
        Machine to = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(strToHostname));
        OperatingSystem fromOS   = from.getOperatingSystem();
        OperatingSystem   toOS   = to.getOperatingSystem();
        String fromSep = (fromOS == OperatingSystem.WINDOWS ? "\\" : "/" );
        String   toSep = (  toOS == OperatingSystem.WINDOWS ? "\\" : "/" );

        //execList.setRecurse( true );
        try{
            RemoteFile[] aFN = from.getFile(_dir).list(true);
            if( aFN != null ){
                for( int iI = 0; iI < aFN.length; iI ++ ){
                    debug( "@@ " + aFN[ iI ]  );
                    ArrayList<String> listDir = new ArrayList<String>();
                    String strFromFileName = getFileName( getRFN( aFN[ iI ], _dir, fromSep ), listDir );
                    if( matchFile( strFromFileName, listDir, _listInc ) ){
                        debug( "  copying" );
                        String strFromFile = aFN[ iI ].getAbsolutePath();
                        String strToFile   = getToFN( toDir, listDir, strFromFileName, toSep );
                        debug( "  copying'" + strFromFile + "' to '" + strToFile+ "'");
                        try{
                            RemoteFile fromFile = from.getFile(strFromFile);
                            RemoteFile toFile = to.getFile(strToFile);
                            fromFile.copyToDest(toFile);
                            listCopied.add( strFromFile );
                            listCopied.add( strToFile );
                        } catch( Exception ee ){
                            // doing nothing. We might be copieing some matched Directory
                        }
                    }
                }
            }
        } finally{
            // execList.setRecurse( false );
        }
        return listCopied;
    }



    protected String getFileName( String strFN, ArrayList<String> listDir ){
        StringTokenizer st = new StringTokenizer( strFN, "/\\" );  // no matter Winodws or Unix
        String strRet = null;
        while( st.hasMoreTokens() ){
            if( strRet != null ){
                listDir.add( strRet );
            }
            strRet = st.nextToken();
        }
        return strRet;
    }



    protected String getToFN( String strDir, ArrayList<String> listDir, String strFN, String sep ){
        StringBuffer buffer = new StringBuffer( strDir );
        if( !strDir.endsWith( sep )) buffer.append( sep );  // make sure: dir always has a sep in the end
        for( String dir : listDir ){
            buffer.append( dir );
            buffer.append( sep );
        }
        buffer.append( strFN );
        return buffer.toString();
    }

    /**
     * Sets the base-directory for this instance.
     * @param dir the directory's <code>File</code> instance.
     */
    public void setDir(String dir) throws BuildException {
        debug( "setDir:" + dir );
        _dir = dir;
    }


    /**
     * Add a name entry to the include list.
     * @return <code>Include</code>.
     */
    public Include createInclude() throws BuildException {
        debug( "createInclude" );
        Include include = new Include( this );
        _listInc.add( include );
        return include;
    }


    public boolean setSearchSub( boolean bSearch ){
        _bSearchSub = bSearch || _bSearchSub;
        debug( "setSearchSub:" + _bSearchSub );
        return _bSearchSub;
    }

    static final boolean _bDebug = CopyH._bDebug;
    protected void debug( String str ){
        if( _bDebug ){
            System.out.println( "MyFileSet --" + str  );
        }
    }

    String strBeginDir  = _dir;
    ArrayList<String> _localList = null;
    public ArrayList<String> getLocalFileList() {
        debug( "getLocalFileList:" + _dir );
        if( _localList != null ){
            return _localList;
        }

        _localList = new ArrayList<String>(30);
        ArrayList<String> list = new ArrayList<String>(30);
        File dir = new File( _dir );
        if( dir.exists() ){
            _dirpath = dir.getAbsolutePath();
            searchFile( dir, _listInc, list  );
        }
        return _localList;
    }

    public String getDirPath() {

        return _dirpath;
    }

    public void searchFile( File dir, List<DataType> listInc, ArrayList<String>listDir )
    {
       String strDirPath = dir.getAbsolutePath();
       
       String[] astr = null;
       try
       {
          astr = dir.list();
       }
       catch( SecurityException e )
       {
          System.out.println( "The directory: " + dir.getAbsolutePath() +
                              " has a securityViolation" );
          e.printStackTrace();
          return;
       }

       for( int iI = 0; iI < astr.length; iI ++ )
       {
          File ft = new File( dir, astr[ iI ] );
          if( ft.isDirectory() )
          {
              ArrayList<String> listClone = ((ArrayList<String>)listDir.clone());
              listClone.add( ft.getName() );
              searchFile( ft, listInc, listClone );   // recursive search
          }
          else
          {
               String strPath = ft.getAbsolutePath();
               if( matchFile( ft.getName(), listDir, listInc ) ){
                   _localList.add( strPath );
               } else{
                   debug( " no match '" + strPath + "'"  );
               }
          }
       }
    }


    protected boolean matchFile( String strFileName, ArrayList<String> listDir, List<DataType> listInc ){
        debug( " matchFile '" + strFileName + "'"  );
        boolean bMatch = false;
        for( DataType dt : listInc ){
            debug( " dt '" + dt + "'"  );
            if( dt instanceof Include ){
                debug( " dt " );
                bMatch = match( (Include)dt, strFileName, listDir );
                if( bMatch ) break;
            }
        }
        // If exclude doing it here
        // String strMisMatch = null;
        // for( DataType dt : listExc ){
        //     if( dt instanceof Include ){
        //         strMisMatch = match( (Include)dt, strFileName, listDir );
        //         if( strMisMatch != null ){
        //             strMatch = null;
        //             break;
        //         }
        //     }
        // }

        return bMatch;
    }

    // return null it not match
    // return string is match
    protected boolean match( Include dt, String strFile, ArrayList<String> listDir ){
        return dt.match( strFile, listDir );
    }

    // This is a quick-and-dirty function to get a RelativeFile pathname of rf  
    // from path dir. (We do not have the luxury to make it slow-but-clean) 
    protected String getRFN( RemoteFile rf, String dir, String sep){
        String strPath = rf.getAbsolutePath();
        String strRet  = strPath;
        if( strPath.startsWith( dir ) ){
            strRet  = strPath.substring( dir.length() );
            while( strRet.startsWith(sep ) ){
                strRet = strRet.substring( 1 ); 
            }
        }
        return strRet;
    }

}
