//
// 1.10, 11/24/09, autoFVT/src/com/ibm/ws/wssecfvt/build/tools/ExecPythonCmd.java, WAS.wssecurity.fvt, WAS85.FVT, cf011231.01
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect           Description
// ----------------------------------------------------------------------------
// 02/24/09 gkuo        F1065-3012      Handle wsadmin common routines
// 03/18/09 gkuo        F1065-4824      enahnced to handle the arrays with "\\r\\n" from jython
// 04/23/09 gkuo        F1065-8848      enahnced to handle the UserName and Password for cell
// 11/24/09 gkuo        629644          When restart the server, also restart the wsadmin session

package com.ibm.ws.wssecfvt.build.tools;


// for common routines on running wsadmin tasks
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;
import com.ibm.websphere.simplicity.wsadmin.Wsadmin;
import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.Node; 
import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ConnectionInfo;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *  Running the jython wsadmin command 
 *  It initializes itself with all default values/methods 
 *  And simplify the parameters. But this does not handle anything that is not default
 *  Let's try to keep this class as simple as possible.
 */
public class ExecPythonCmd  {
	
    Exception _latestException = null;
	
    /**
     *  public constructor  
     *  doing nothing in the beginning.
     */
    public ExecPythonCmd(){
    }

    /**
     * restart the WAS in ND and Base and prevent hanging on wsadmin session
     * This do save and syncNode, then StopWAS and StartWAS
     *    without touching the wsadmin sessions.
     *
     * In a lot of cases, we restart the WAS after we call wsadmin commands to
     * change the environment. So, it's good to be in this class.
     *
     */
    public void restartWAS(){
           Cell cell = getCell();

           // sync node before stop
           //In some occassion, we may want to save and syncNode after cell start
           try{
               cell.getWorkspace().saveAndSync();
           } catch( Exception e ){
               e.printStackTrace( System.out );
           }

           try{
               cell.stop();
               cell.start();
               cell.disconnect();
               cell.connect();
           } catch( Exception e ){
               e.printStackTrace( System.out );
           }
    }

    /**
     * Keep the default cell and wsadmin session here
     * Not sure if the wsadmin  can be put in static method
     * But our junit is spawn in a separate JVM, so, it's probably OK for now.
     * And we do not need to call the Wsadmin.getProviderInstance( cell ); all the time
     */
    static Cell    _cell    = null;
    static Wsadmin _wsadmin = null;

    /**
     * Get back the default cell.
     * @return default Cell
     */
    public Cell getCell(){
        if( _cell == null ){
            try{
                _cell    = TopologyDefaults.getDefaultAppServer().getCell(); // get simplicity default Cell
            } catch( Exception e ){
                e.printStackTrace( System.out );
            }
        }
        return _cell;
    }

    /**
     *   Get back the config path of the default cell.
     *   @returns the path: <WAS_ROOT>/profiles/<profileName>/config/cells/<cellName>
     *            Example: "/WebSphere/profiles/AppSrv01/config/cells/testNode01Cell"
     */
    public String getCellConfigPath() {   
        String cellConfigPath = "";
        try {
           Cell cell = getCell();
           cellConfigPath = cell.getConfigPath();
        } catch(Exception e) {
           e.printStackTrace( System.out );
        }
        return cellConfigPath;
    }

    /**
     *  @return the Machine object of the default cell
     */
    public Machine getCellMachine() {
        Machine machine = null;
        try {
           Cell cell = getCell();
           ApplicationServer                  server = cell.getManager();
           com.ibm.websphere.simplicity.Node  node   = server.getNode(); // class duplicated 

           machine  = node.getMachine();
        } catch(Exception e) {
           e.printStackTrace( System.out );
        }
        return machine;
    }

    /**
     *  Currently, this only initialize once in a junit jvm session.
     *  Create another method, if you need to init the Wsadmin again 
     *  @return The default provider Wsadmin session.
     */
    public Wsadmin getWsadmin(){
        if(  _wsadmin == null ){
            try{
                Cell cell = getCell(); // get simplicity default Cell
                _wsadmin  = Wsadmin.getProviderInstance( cell ); // get default wsadmin session (default cell)
            } catch( Exception e ){
                e.printStackTrace( System.out );
            }
        }
        return _wsadmin;
    }

    /**
     *  @return the policy set is ND or Base
     */
    static boolean bExecutedPSN = false;
    static boolean bPolicySetND = false;
    public boolean isPolicySetND(){
        if( bExecutedPSN ) return bPolicySetND;
        myPrintln( "check if NDPolicySet Existed" );
        Wsadmin wsadmin = getWsadmin();
        //it ought to be jython session
        try{
            String strResult = wsadmin.executeCommand( "AdminTask.validatePolicySet('-policySet \"WS-I RSP ND\"')" );
            myPrintln( "isPolicySetND retruned:" + strResult );
            bExecutedPSN = true;
            bPolicySetND = getWsadminBoolean( strResult );
        } catch( Exception e ){
            myPrintln( "isPolicySetND getException:" + e.getClass().getName()  );
            myPrintln( "   Exception  Message     :" + e.getMessage() );
            bExecutedPSN = true;
            bPolicySetND = false;
        }
        return bPolicySetND;
    }

    /**
     * unquote the string.
     * because jython tends to return the quouted results 
     * @retrurn the unquoted string
     */
    public String unquotedStr( String strResult ){
        strResult = strResult.trim();
        if( strResult.startsWith( "'"  ) ||
            strResult.startsWith( "\"" )   )
            return strResult.substring( 1, strResult.length() -1 );
        return strResult;
    }

    /**
     * jython return 'true' and '' to indicate
     *   the wsadmin command runs OK
     * @retrurn wsadmin runs OK, without exception
     */
    public boolean getWsadminBoolean( String strResult ){
        strResult = unquotedStr( strResult );
        return (strResult.equalsIgnoreCase( "true" )  ||
                strResult.length() == 0  );  // AdminConfig.save returns a empty string if succeeded
    }

    /**
     *  If str is a numeric string
     *  This is useful when there are ID in numeric form
     *  @return true if str is a numeric
     */
    public boolean isNumeric( String str ){
        if( str == null ) return false;
        try{
            str = unquotedStr( str );
            Integer.parseInt( str );
            // no erro. Then it's a numeric
            return true;
        }catch( Exception e){
            return false;
        }
    }

    /**
     *  See cmd()
     *  The cmd returns the message from the Exceptio n it got. 
     *  We can call this method to get the real Exception
     *  @return latest Exception. This could return null, if no Exception in the latest call of cmd()
     */
    public Exception getException(){
        return _latestException; 
    }

    /**
     *  See cmd()
     *  The cmd returns whether latest cmd got Exception
     *  @return latest cmd() got Exception
     */
    public boolean wasException(){
        return _latestException != null; 
    }

    /**
     *  See cmd()
     *  check if the latest cmd() get array(s)
     *  @return latest cmd() got Exception
     */
    public boolean wasArray(){
        return _props != null; 
    }

    /**
     *  See cmd()
     *  check if the latest cmd() get array(s)
     *  @return latest cmd() got Exception
     */
    public boolean wasArrays(){
        return _props != null; 
    }

    /**
     *  Submit a wsadmin jython command
     *  This method will get default provider wsadmin and handle the command
     *  If cmd() got an exception from simiplicity, it won't throw it up.
     *    Instead it returns the message from getMessage() of the Exception.
     *     Please call wasException() or getException() to get the latest Exception
     *  In case you are expecting  cmd to return array, please call
     *    wasArray() wasArrays(), getArray() or getArrays to get the latest arrays()
     *  The latest Exception or array(s) will be reset to null when you call a new cmd()
     *  @return the result returned from wsadmin
     *          or a warning message which indicate an array or arrays was returned by jython
     * 
     */
    public String cmd( String strCommand ){
        _latestException  = null;
        _listProps        = null;
        _props            = null;
        Wsadmin wsadmin   = getWsadmin();
        String strResult  = "";
        try {
            myPrintln( "cmd:\"" + strCommand + "\"" );
            strResult     = wsadmin.executeCommand( strCommand );
            String strPre = "out=\"";

            if( strResult.startsWith( "\"[" ) ||
                strResult.startsWith( "'["  )   ){
                // Ok we get an array. We can only handle a single layer array and not more
                handleArrays(  strResult );
                // Get an array, please call getArray() or getArrays() immediately
                strResult = "Get an array. call getArray() or getArrays() immediately";
                myPrintln( strPre + strResult  + "\"" );
            } else{
                if( strResult.indexOf( "\n" ) >= 0 ){ // multi line
                    StringTokenizer st = new StringTokenizer(strResult, "\n\r" );
                    while( st.hasMoreTokens() ){
                        String strR = (String)st.nextToken();
                        myPrintln( strPre + strR  + "\"" );
                        strPre = "    \"";
                    }
                } else{
                    myPrintln( strPre + strResult  + "\"" );
                }
            }

            myPrintln();
        } catch(Exception ee) {
            //myPrintln("get expeception:'" + ee.getMessage() + "'");
            ee.printStackTrace( System.out );
            _latestException = ee;
            return ee.getMessage();
        }
        return strResult;
    }

    Properties             _props      = null;
    boolean                _bListProps = true; //
    ArrayList<Properties>  _listProps  = null;
    /**
     * This handle a return value is a single jython array
     * It can handle a result as  "[key value]"
     * It can handle the returning result in a one-layer array. 
     *   Such as: "[  [AKey AValue] [BKey BVale] [CKey [/*[..XPath...]] ]
     * @param strResult result which already unquoted 
     * @return a properties which has key-value pair of the result array
     */
    public Properties handleOneArray( String strResult ) throws Exception {
        Properties props = new Properties();
        try {
            StringTokenizer st = new StringTokenizer( strResult ); // " "
            String strKey   = "";
            String strValue = "";
            boolean bKeyMode = false;
            while( st.hasMoreTokens() ){
                String token = st.nextToken();
                if( token.equals( "[" ) ){
                    // this is a set of multiple properties
                    //   For multi layer-array.. we may want to do something here
                    //   But too risky and complicated to do so...
                    //   A parser is better... 
                    bKeyMode = false;
                    continue;
                }
                if( token.equals( "]" )) {
                    if( bKeyMode ){  // get a empty value
                        props.put(strKey, "" );
                        bKeyMode = false;
                    }
                    // this is the end of a set of multiple properties
                    continue;
                }
                if( token.startsWith( "[/*[" ) ){
                    bKeyMode = false;
                    // This is the beginning of an XPath expression
                    strValue = token;
                    while( st.hasMoreTokens() ){
                        token = st.nextToken();
                        if( token.endsWith( "]]" ) ){
                            // This is the last of the XPath
                            strValue = strValue.concat(" ").concat(token.substring(0, token.lastIndexOf("]")));
                            props.put( strKey, strValue );
                            break;  // go to normal procedure
                        } else{
                            // I have no idea why I'm doing this String/concat stuff,
                            // but Jython won't work unless I do..
                            strValue = strValue.concat(" ").concat(token);
                        }
                    }
                    continue;
                }

                if( token.startsWith( "[" ) ){
                    if( bKeyMode ){ // we get a string 
                        bKeyMode = false;
                        // This is the beginning of an XPath expression
                        strValue = token.substring( 1 );
                        while( st.hasMoreTokens() ){
                            token = st.nextToken();
                            if( token.endsWith( "]" ) ){
                                // This is the last of the XPath
                                // It ought to be "[aaa bbb ccc]"
                                strValue = strValue.concat(" ").concat(token.substring(0, token.indexOf("]")));
                                props.put( strKey, strValue );
                                break;  // go to normal procedure
                            } else{
                                // I have no idea why I'm doing this String/concat stuff,
                                // but Jython won't work unless I do..
                                strValue = strValue.concat(" ").concat(token);
                            }
                        }
                        continue;
                    } else{
                        strKey = token.substring(1);
                        bKeyMode = true;
                    }
                }
                if( token.endsWith("]") ){
                    bKeyMode = false;
                    strValue = token.substring(0, token.length() - 1);
                    // we have a key value pair
                    props.put(strKey, strValue);
                }
            }
        } catch(Exception ee) {
            myPrintln("get expeception:'" + ee.getMessage() + "'");
            throw ee;
        }
        return props;
    }

    /**
     * This handle a return value is an single-layer array
     * It can handle a result like: "[Akey value]"
     * It can handle the returning result in a one-layer array. 
     *   Such as: "[  [AKey AValue] [BKey BVale] [CKey [/*[..XPath...]] ]
     * It can handle the returning result in a two-layer arrays. 
     *   Such as: "[ [AKey AValue] [BKey BVale] [CKey [/*[..XPath...]] ]\\r\\n
     *             [ [AKey AValue] [BKey BVale] [CKey [/*[..XPath...]] ]\\r\\n...  
     *            *
     * @param strResult result 
     * @return an ArrayList which contains the Properties of each jython array
     */
    public ArrayList handleArrays( String strResult ) throws Exception {
        _listProps  = new ArrayList<Properties>();
        _bListProps = false;
        try {
            initEpcTokenizer( strResult ); //Handle "\\r\\n" " "
            while( hasMoreTokens() ){
                String token = nextToken();
                // System.out.println( token );
                Properties props = handleOneArray( token );
                _listProps.add( props );
            }
            if( _listProps.size() > 1 ){
                // TBD: We may need to merge the multi properties into one
                // But ought to be OK for now. Since most of our output is a single array
                // System.out.println( "CMD get back more than one array" );
                _props = _listProps.get( 0 ); // This may need to be change
            } else if( _listProps.size() == 1 ){
                _props = _listProps.get( 0 );
            }
        } catch(Exception ee) {
            myPrintln("get expeception:'" + ee.getMessage() + "'");
            throw ee;
        }
        return _listProps;
    }

    /**
     * If you are sure that the jython will return only one array.
     * then you can call this getArray(). It's simpler and
     *  are true in most cases
     * But if it's multi-arrays, then getArrays(0 is recommended
     */
    public Properties getArray(){
        // print the props only once
        if( !_bListProps ){ 
            _bListProps = true;
            printProps( _props );
        }
        return _props;
    }

    /**
     * return all the arrays that jython returns, this could be multi-array
     */
    public ArrayList<Properties> getArrays(){
        // print the props only once
        if( !_bListProps ){ 
            _bListProps = true;
            for( int iI = 0; iI < _listProps.size(); iI ++ ){
                Properties props = _listProps.get( iI );
                printProps( props );
            }
        }
        return _listProps;
    }

    /**
     *   This print out the properties for debugging and tracing
     */
    public void printProps( Properties props ){
        myPrintln( "--------------- list array -----------" );
        Enumeration enum1 = props.keys();
        while( enum1.hasMoreElements() ){
            String strKey   = (String)enum1.nextElement();
            String strValue = (String)props.getProperty( strKey );
            myPrintln( " " + strKey + " = " + strValue );
        }
        myPrintln( "------------- end list array ---------" );
    }

    /**
     *  Pequivalent to System.out.println methods
     *  but prevent weird output when mixed "\n" with "\r\n"
     */
    public void myPrintln( String str ){
        System.out.print(str);
        System.out.print("\n");
    }
    public void myPrintln(  ){
        System.out.print("\n");
    }

    /**
     *   replace all the strOld with strNew in the strTmp
     */
    public String replaceStr( String strTmp, String strOld, String strNew ){
        int index  = 0;
        int index1 = 0;
        while( (index1 = strTmp.indexOf( strOld, index )) >= 0 ){
            index = index1 + strNew.length();
            strTmp = strTmp.substring( 0, index1 ) + strNew + strTmp.substring( index1 + strOld.length() );
        }
        return strTmp;
    }

    /**
     * A simple String Tokenizer for hanlding jython "\\r\\n" delimeter only  
     * This emulate the StringTokenizer but
     * ** Be careful, it can NOT handle multi-string in the same time ***
     */
    String _str = "";
    public void initEpcTokenizer(String str){
          _str = unquotedStr(str ) ;
    }
    public boolean hasMoreTokens(){
        return _str.length() > 0 ;
    }
    public String nextToken(){
        String strRet = _str;
        String strDel = "\\n";
        int index = _str.indexOf( strDel );
        if( index >= 0 ){
            int index1 = _str.indexOf( "\\r\\n" );
            int iLen   = 2;
            if( index1 >= 0 ) {
                strDel = "\\r\\n" ;
                index = index1;
                iLen  = 4;
            }
            strRet = _str.substring( 0, index );
            _str   = _str.substring( index + iLen );
        } else{
            _str = "";
        }
        return strRet;
    }

    /**
     * @return the Username of cell (such as: ltpa user name (testuser)
     */
    public String getCellConnUser(){
        Cell cell = getCell();
        try{
            ConnectionInfo current = cell.getConnInfo();
            return current.getUser();

        }catch( Exception ee ){
            ee.printStackTrace( System.out );
        }
        return "**epc_UserNameInError**";
    }

    /**
     * @return the Password of cell (such as: ltpa user name (testuserpwd)
     */
    public String getCellConnPassword(){
        Cell cell = getCell();
        try{
            ConnectionInfo current = cell.getConnInfo();
            return current.getPassword();
        }catch( Exception ee ){
            ee.printStackTrace( System.out );
        }
        return "**epc_PasswordError**";
    }
}


