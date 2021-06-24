/*
 *   1.1, 8/8/12
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
 *
 */

package com.ibm.ws.wsfvt.build.types;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.BuildException;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Moved out of MatchingTask to make it a standalone object that could
 * be referenced (by scripts for example).
 *
 */
public class Include extends DataType {
    MyFileSet         _parent = null;
    String            _name   = "";
    ArrayList<String> _listToken = null;


    public Include(MyFileSet parent) {
        super();
        debug( "Include:" + parent );
        _parent = parent;
    }

    public void setName(String name) {
        debug( "setName:" + name );
        _name = name;
        _parent.setSearchSub( name.indexOf( "**") >= 0 );
    }

    public String getName() {
        debug( "getName:" + _name );
        return _name;
    }

    static final boolean _bDebug = com.ibm.ws.wsfvt.build.tasks.CopyH._bDebug;
    protected void debug( String str ){
        if( _bDebug ){
            System.out.println( "Include   --" + str  );
        }
    }

    public ArrayList<String> getTokenList(){
        if( _listToken == null ){
            debug( "create new token list" );
            _listToken = createListToken();
        }
        return (ArrayList<String>)_listToken.clone();
    }

    int _iSlash = 0;
    protected ArrayList<String> createListToken(){
        String strName = getName();
        StringTokenizer st = new StringTokenizer( strName, "*/", true );
        ArrayList<String> list  = new ArrayList<String>();
        boolean bLastStar   = false;
        boolean bLastStars  = false;
        boolean bLastSlash  = false;
        int iCount = 0;
        while( st.hasMoreTokens() ){
            String token =  (String)st.nextToken();
            if( token.equals( "*" )){
                if( bLastStar ){
                    // found an "**"  or "***" etc
                    changeLastToken( list, "**" );
                    bLastStars = true;
                } else{
                    list.add( token );
                    bLastStars = false;
                }
                bLastStar  = true;
                bLastSlash = false;
            } else{
                if( token.equals( "/" ) ){
                    if( (!bLastSlash) && ( iCount > 0 ) ){
                        list.add( token );
                        if( ! bLastStars ) _iSlash ++;
                    }
                    bLastStars = false;
                    bLastSlash = true;
                    bLastStar  = false;
                }else{
                    list.add( token );
                    bLastStars = false;
                    bLastStar  = false;
                    bLastSlash = false;
                }
            }
            iCount ++;
        }
        return list;
    }

    protected void changeLastToken( ArrayList<String> list, String token ){
        int iLast = list.size() - 1;
        iLast = iLast < 0 ? 0 : iLast;
        list.set( iLast, token );
    }

    protected boolean match( String strFile, final ArrayList<String> listDir ){
        int iDirSize = listDir.size();
        debug( "  Match " + iDirSize);
        ArrayList<String> list = getTokenList();

        //if( _iSlash > iDirSize ){ // not enough path depth
        //    return false;
        //}

        // the matchFileName is a destructive method on list
        if( ! matchFileName( strFile, list )){
            return false;
        }

        if( ! matchPath( listDir, list )){
            return false;
        }

        return true;
    }

    // the matchFileName is a destructive method on list
    protected boolean matchFileName( String strFile, ArrayList<String> list ){
        int iSize   = list.size();
        if( iSize == 0 ) return false;  // nothing to match
        boolean bMatch = false;
        int     iIndex = strFile.length();
        for( int iEnding = iSize - 1; iEnding >= 0; iEnding -- ){
            String token = list.get( iEnding );
            debug( "token:" + token + "  -- " + strFile );
            if( token.equals( "/" )  ){
                list.remove( iEnding );
                return bMatch;    // filename ends
            } else if( token.equals( "**" )  ){
                return true;      // anything, good,
            } else if( token.equals( "*" )  ){
                list.remove( iEnding );
                bMatch = true;    // anything, true for now
            } else{
                int iTmp = strFile.indexOf( token );
                if( iTmp < 0 || iTmp > iIndex ){
                    return false;
                } else{
                    list.remove( iEnding );
                    bMatch = true;
                    iIndex = iTmp;
                }
            }
        }
        return bMatch;
    }


    // the matchFileName is a destructive method on list
    protected boolean matchPath( final ArrayList<String> listDir, ArrayList<String> list ){
        int iTSize   = list.size();
        int iPSize   = listDir.size();
        if( iTSize == 0 ){
            return iPSize == 0;  // nothing to match
        }
        boolean bAny   = false;
        boolean bMatch = false;
        iPSize --;
        for( int iEnding = iTSize - 1; iEnding >= 0; iEnding -- ){
            String token = list.get( iEnding );
            debug( "token:" + token + "  -- " + iPSize );
            if( iPSize < 0 ){
                return token.equals( "**" ); // unless it's "**". And this is not perfect neither
            }
            if( token.equals( "/" )  ){
            } else if( token.equals( "**" )  ){
                if( iEnding == 0 ) return true;
                bMatch = true;      // anything, good,
                bAny   = true;      // This is not a perfect solution but ought to be good enough for FVT
                // If iEnding is not 1, this method is wrong
            } else if( token.equals( "*" )  ){
                iPSize--;
                bMatch = true;    // anything, true for now. This is not perfect neither
            } else{
                if( bAny ){  // this is not good, neither. But only for FVT
                    String strPath  = listDir.get( 0 );
                    return( strPath.indexOf( token ) >= 0 );
                } else{
                    String strPath  = listDir.get( iPSize );
                    bMatch = strPath.indexOf( token ) >= 0;
                    if( ! bMatch ) return false;
                    iPSize--;
                }
            }
        }
        // This is really not good at all. Bit for FVT, it ought to work...
        return bMatch;
    }



}
