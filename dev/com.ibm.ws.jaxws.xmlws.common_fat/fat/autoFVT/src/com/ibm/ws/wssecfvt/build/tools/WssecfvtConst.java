/*
 *
 * @(#) 1.7 autoFVT/src/com/ibm/ws/wssecfvt/build/tools/WssecfvtConst.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:08:17 [8/8/12 06:30:19]
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2002
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect    Author		Marker	Description
 * ----------  --------------    -----------	------	----------------------------
 * 04/10/2003  LIDB2108.17       syed				wssecfvt constants
 * 06/23/2003  170386            syed				Added Was_Hostname
 * 10/19/2005                    djswenso       @djs1       Modified launchclient cmd to remove
 *										    the .sh extionsion and add the 
 *										    -profileName parm for profile support
 * 06/12/2006                    Todd Roling                Updated for WAS 6.1 FP (JAX-WS)
 * 05/02/2008  517514            jramos             Use correct server for get_portnumber()
 *        
 */

package com.ibm.ws.wssecfvt.build.tools;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.PortType;
import com.ibm.ws.wsfvt.build.tools.utils.TopologyDefaults;

public class WssecfvtConst {

    @Deprecated
    public static final String PROTOCOL = "http://";
    @Deprecated
    public static final String SERVICE_NAME = "FVTVersionSEIService";
    @Deprecated
    public static final String TEST_STRING_IN = "<invoke>WSSECFVT Version: 2.0</invoke>";
    @Deprecated
    public static final String TEST_STRING_OUT = "<provider><message>WSSECFVT Version: 2.0</message></provider>";

    @Deprecated
    public static String Was_Hostname = get_hostname();
    @Deprecated
    public static String Ws2Ws_Hostname1 = get_hostname1();
    @Deprecated
    public static String Ws2Ws_Hostname2 = get_hostname2();
    @Deprecated
    public static String Ws2Ws_Portname1 = get_portname1();
    @Deprecated
    public static String Ws2Ws_Portname2 = get_portname2();
    @Deprecated
    public static String Was_Profilename = get_profilename();
    @Deprecated
    public static String Was_Portnumber = get_portnumber();

    // @djs1 - BEGIN
    @Deprecated
    public static final String J2EE_CLIENT_CMD = get_J2EE_client_command();

    private static String get_J2EE_client_command() {
        try {
            ApplicationServer server = TopologyDefaults.getDefaultAppServer();
            String fileSep = server.getNode().getMachine().getOperatingSystem().getFileSeparator();
            return (server.getNode().getWASInstall().getInstallRoot() + fileSep + "bin" + fileSep + "launchClient"
                    + server.getNode().getMachine().getOperatingSystem().getDefaultScriptSuffix() + " -profileName "
                    + server.getNode().getProfileName() + " " + server.getNode().getProfileDir()
                    + fileSep + "installableApps" + fileSep + "WssecFvtClients.ear -CCexitVM=true ");
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // @djs1 - END

    private static String get_hostname() {

        try {
            return TopologyDefaults.getRootServer().getNode().getMachine().getHostname();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String get_hostname1() {

        return get_hostname();

    }

    private static String get_hostname2() {

        return get_hostname();

    }

    private static String get_portname1() {

        return get_portnumber();
    }

    private static String get_portname2() {

        return get_portnumber();

    }

    private static String get_profilename() {
        
        try {
            return TopologyDefaults.getRootServer().getNode().getProfileName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String get_portnumber() {

        try {
            return "" + TopologyDefaults.getDefaultAppServer().getPortNumber(PortType.WC_defaulthost);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
