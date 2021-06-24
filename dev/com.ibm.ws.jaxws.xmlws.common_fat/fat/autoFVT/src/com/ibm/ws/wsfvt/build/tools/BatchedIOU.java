/*
 * @(#) 1.9 autoFVT/src/com/ibm/ws/wsfvt/build/tools/BatchedIOU.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:12 [8/8/12 06:40:29]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect     Description
 * -----------------------------------------------------------------------------
 * 07/12/2007  jramos      451621             New File
 * 10/22/2007  jramos      476750             Use TopologyDefaults tool and ACUTE 2.0 api
 * 04/03/2008  gkuo                           add 8-second nap between AppInstall and AppStart
 * 04/03/2008  btiffany    510181             Add some debug statements
 * 06/12/2008  jramos      524904             Add support to install/uninstall to cells other than was-cell-1
 */

package com.ibm.ws.wsfvt.build.tools;


/**
 * This tool is for writing a placeholder script to store batched applications
 * to install/uninstall and to perform batched installs/uninstalls using ACUTE
 * 
 * @author jramos
 * 
 */
public class BatchedIOU {

//    private File batchList = null;
//
//    public BatchedIOU(File batchList) {
//        this.batchList = batchList;
//    }
//
//    /**
//     * Write the application to the list of batched applications. The list will
//     * hold store all the data necessary for an install such as application
//     * name, ear location, and install parameters
//     * 
//     * @param adminApp
//     *            The AdminApp object holding the data for the application
//     */
//    public void writeApp(AdminApp adminApp, boolean install) {
//        PrintStream ps = null;
//        try {
//            if(!adminApp.getLocal()) {
//                ps = new PrintStream(new FileOutputStream(batchList, true));
//                // If not in local mode, then lets write information that can be used by
//                // the ACUTE install operation
//                ps.print("application:");
//    
//                // app name
//                ps.print(adminApp.getAppName() + "<break>");
//    
//                // hostname
//                ps.print(adminApp.getServerProcess().getMachine().getHostname() + "<break>");
//    
//                // installRootLocation
//                String installRoot = adminApp.getInstallRootLocation();
//                IServerProcess process = adminApp.getServerProcess();
//                String installFileName = null;
//                if (adminApp.getEarFile() != null) {
//                    installFileName = adminApp.getEarFile();
//                } else {
//                    installFileName = adminApp.getWarFile();
//                }
//                if (installRoot != null) {
//                    if (installRoot.equals(process.getWASInstall().getInstallRoot())) {
//                        installRoot = (installRoot + process.getMachine().getFileSeparator() + "installableApps"
//                                + process.getMachine().getFileSeparator() + installFileName).replace('\\', '/');
//                    } else {
//                        installRoot = (installRoot + process.getMachine().getFileSeparator() + installFileName).replace(
//                                '\\', '/');
//                    }
//                } else {
//                    installRoot = (AppConst.FVT_HOME + process.getMachine().getFileSeparator() + "build"
//                            + process.getMachine().getFileSeparator() + "installableApps"
//                            + process.getMachine().getFileSeparator() + installFileName).replace('\\', '/');
//                }
//                ps.print(installRoot + "<break>");
//                
//                // cell key
//                ps.print(adminApp.getServerProcess().getCell().getKey() + "<break>");
//    
//                // param map
//                for (InstallAppParm parm : adminApp.getOperationParamMap().keySet()) {
//                    ps.print(parm + "<break>");
//                    ps.print(adminApp.getOperationParamMap().get(parm) + "<break>");
//                }
//                ps.println();
//                ps.close();
//            } else {
//                // if in local mode, we have to use ACUTE's wsadmin operation since only a connected install
//                // is supported for the ACUTE install operation; lets write the install script
//                boolean batchListExists = batchList.exists();
//                ps = new PrintStream(new FileOutputStream(batchList, true));
//                ps.println("#application:");
//                if (!batchListExists) {
//                    ps.println("from com.ibm.ws.scripting import ScriptingException");
//                    ps.println("from java.io import FileOutputStream");
//                    ps.println("from java.io import PrintStream");
//                    if(install) {
//                        ps.println("outFile = PrintStream(FileOutputStream(\'" + (AppConst.FVT_HOME.replace('\\', '/') + "/logs/was/" + AppConst.INSTALL_ERR_LOG_NAME) + "\'))");
//                    } else {
//                        ps.println("outFile = PrintStream(FileOutputStream(\'" + (AppConst.FVT_HOME.replace('\\', '/') + "/logs/was/" + AppConst.UNINSTALL_ERR_LOG_NAME) + "\'))");
//                    }
//                }
//                ps.println("try:");
//                StringTokenizer tokenizer;
//                if(install) {
//                    tokenizer = new StringTokenizer(adminApp.getInstallScriptCmd(), "\r\n");
//                } else {
//                    tokenizer = new StringTokenizer(adminApp.getUninstallScriptCmd(), "\r\n");
//                }
//                while(tokenizer.hasMoreTokens()) {
//                    ps.println("    " + tokenizer.nextToken());
//                }
//                ps.println("except ScriptingException, e:");
//                if(install) {
//                    ps.println("    outFile.println(\'INSTALL FAIL: " + adminApp.getEarFile() + "\')");
//                } else {
//                    ps.println("    outFile.println(\'UNINSTALL FAIL: " + adminApp.getEarFile() + "\')");
//                }
//                ps.println("    outFile.println(e)");
//                
//            }
//        } catch (Exception e) {
//            PrintToFile file = new PrintToFile();
//            file.printException(AppConst.INSTALL_ERR_LOG, e);
//        }
//    }
//
//    /**
//     * Perform a batch install using ACUTE
//     * 
//     * @param server
//     *            The server process that the apps will be installed through
//     */
//    public void batchInstall(AdminApp adminApp, boolean startApps) {
//        if(!adminApp.getLocal()) {
//            // use ACUTE if this is not a local install; this supports mutliple cells
//            Object[] appInfo = readApps(true);
//            if (appInfo[0] != null && appInfo[1] != null) {
//                for(String cellKey : ((Map<String, List<InstallAppParmContainer>>)appInfo[1]).keySet()) {
//                    List<String> appNames = ((Map<String, List<String>>)appInfo[0]).get(cellKey);
//                    Cell cell = CellFactory.getCell(CommonUtilsConst.DEFAULT_TOPOLOGY_FILE, cellKey);
//                    
//                    System.out.println("List of apps to install and start from cell " + cellKey + ": "+appNames.toString());
//                    
//                    List<InstallAppParmContainer> containerList = ((Map<String, List<InstallAppParmContainer>>)appInfo[1]).get(cellKey);
//                    IInstallApp installApp = InstallAppFactory.getInstance(cell.getRootNodeContainer().getRootServerProcess());
//                    System.out.println("BatchedIOU.batchinstall is installing applications, startApps = "+startApps);
//                    Result<String> result = installApp.installAppsForceContinue(containerList);
//                    String resultString = result.getResult();
//                    System.out.println(resultString);
//                    // look for errors in the Result
//                    PrintToFile file = new PrintToFile();
//                    String startException = "Exception installing application:";
//                    String endException = "End Exception";
//                    int startIndex = 0;
//                    int endIndex = 0;
//                    String subResult = "";
//                    while (startIndex != -1) {
//                        startIndex = resultString.indexOf(startException);
//                        endIndex = resultString.indexOf(endException);
//                        if (startIndex != -1) {
//                            startIndex = startIndex + startException.length();
//                            subResult = resultString.substring(startIndex, endIndex);
//                            // need to do this because framework does not collect install and start logs.
//                            System.out.println("*** " + subResult);
//                            file.printBuf(AppConst.INSTALL_ERR_LOG, "*** " + subResult);
//                            resultString = resultString.substring(endIndex + endException.length());
//                        }
//                    }
//        
//                    // start the applications
//                    System.out.println("startApps  = "+startApps);
//                    if(startApps) {
//                        // sync the nodes
//                        if(cell.getTopology() == Topology.ND) {
//                           ISyncNode syncNodes = SyncNodeFactory.getInstance();
//                           syncNodes.syncNodes((IDmgrNodeContainer)cell.getRootNodeContainer());
//                        }
//                        if (adminApp.getServerProcess().getMachine().getOperatingSystem() == OperatingSystem.ZOS) {
//                            sleepForSeconds( 70 );   /* add a 70-second nap if zOS */
//                        } else{
//                            sleepForSeconds( 12 );   /* add a 12-second nap between AppInstall and AppStart*/
//                        }
//                        IStartApp startApp = StartAppFactory.getInstance(cell);
//                        for (String app : appNames) {
//                            System.out.println("Starting application " + app);
//                            try {
//                                System.out.println(startApp.startApp(app));
//                            } catch (ExecutionException e) {
//                                e.printStackTrace();
//                                PrintToFile ptf = new PrintToFile();
//                                System.out.println("*** Application " + app + " failed to start.");
//                                ptf.printBuf(AppConst.INSTALL_ERR_LOG, "*** Application " + app + " failed to start.");
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            try {
//                // execute a wsadmin script with connType NONE if this is local;
//                // for now only a single cell is supported!!!! This will not work for migration tests!!!
//                PrintStream ps = new PrintStream(new FileOutputStream(batchList, true));
//                ps.println("AdminConfig.save()");
//                IExecWSAdmin wsadmin = ExecWSAdminFactory.getInstance(TopologyDefaults.defaultAppServerCell);
//                Map<ExecWSAdminParm, String> params = new HashMap<ExecWSAdminParm, String>();
//                params.put(ExecWSAdminParm.CONNTYPE, "NONE");
//                params.put(ExecWSAdminParm.LANG, "jython");
//                params.put(ExecWSAdminParm.JAVAOPTION, "\"-Dcom.ibm.websphere.webservices.UseWSFEP61ScanPolicy=true\"");
//                System.out.println(wsadmin.executeScriptFile(this.batchList, TopologyActions.FVT_HOSTNAME, params));
//            } catch(Exception e) {
//                e.printStackTrace();
//                PrintToFile ptf = new PrintToFile();
//                ptf.printBuf(AppConst.INSTALL_ERR_LOG, e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * Perform a batch uninstall using ACUTE
//     * 
//     * @param server
//     *            The server process that the apps will be uninstalled through
//     */
//    public void batchUninstall(AdminApp adminApp) {
//        if(!adminApp.getLocal()) {
//            // use ACUTE if this is not a local uninstall; this supports mutliple cells
//            Object[] appInfo = readApps(false);
//            if (appInfo[0] != null) {
//                for(String cellKey : ((Map<String, List<String>>)appInfo[0]).keySet()) {
//                    List<String> appNames = ((Map<String, List<String>>)appInfo[0]).get(cellKey);
//                    Cell cell = CellFactory.getCell(CommonUtilsConst.DEFAULT_TOPOLOGY_FILE, cellKey);
//                    System.out.println("Uninstalling applications from " + cellKey + "...");
//                    IUninstallApp uninstallApp = UninstallAppFactory.getInstance(cell.getRootNodeContainer().getRootServerProcess());
//                    Result<String> result = uninstallApp.uninstallAppsForceContinue(appNames);
//                    String resultString = result.getResult();
//                    System.out.println(resultString);
//                    // look for errors in the Result
//                    PrintToFile file = new PrintToFile();
//                    String startException = "Exception uninstalling application:";
//                    String endException = "End Exception";
//                    int startIndex = 0;
//                    int endIndex = 0;
//                    String subResult = "";
//                    while (startIndex != -1) {
//                        startIndex = resultString.indexOf(startException);
//                        endIndex = resultString.indexOf(endException);
//                        if (startIndex != -1) {
//                            startIndex = startIndex + startException.length();
//                            subResult = resultString.substring(startIndex, endIndex);
//                            file.printBuf(AppConst.UNINSTALL_ERR_LOG, "*** " + subResult);
//                            resultString = resultString.substring(endIndex + endException.length());
//                        }
//                    }
//                }
//            }
//        } else {
//            // execute a wsadmin script with connType NONE if this is local;
//            // for now only a single cell is supported!!!! This will not work for migration tests!!!
//            try {
//                PrintStream ps = new PrintStream(new FileOutputStream(batchList, true));
//                ps.println("AdminConfig.save()");
//                IExecWSAdmin wsadmin = ExecWSAdminFactory.getInstance(TopologyDefaults.defaultAppServerCell);
//                Map<ExecWSAdminParm, String> params = new HashMap<ExecWSAdminParm, String>();
//                params.put(ExecWSAdminParm.CONNTYPE, "NONE");
//                params.put(ExecWSAdminParm.LANG, "jython");
//                System.out.println(wsadmin.executeScriptFile(this.batchList, TopologyActions.FVT_HOSTNAME, params));
//            } catch(Exception e) {
//                e.printStackTrace();
//                PrintToFile ptf = new PrintToFile();
//                ptf.printBuf(AppConst.UNINSTALL_ERR_LOG, e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * Read the application data from the file containing the list of installed
//     * applications
//     * 
//     * @param install
//     *            True if this is for an install; False if for an uninstall
//     * @return A 2 element Object array. Element 0 hold a List<String> of
//     *         appNames. Element 1 holds a List<InstallAppParmContainer> of
//     *         InstallAppParmContainer's
//     */
//    private Object[] readApps(boolean install) {
//        BufferedReader br = null;
//        Map<String, List<String>> appNames = new HashMap<String, List<String>>();
//        Map<String, List<InstallAppParmContainer>> containerMap = new HashMap<String, List<InstallAppParmContainer>>();
//        String cellKey = null;
//        try {
//            br = new BufferedReader(new FileReader(batchList));
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                String prefix = "application";
//                String delim = "<break>";
//                line = line.substring(prefix.length() + 1);
//
//                // read app name
//                String appName = line.substring(0, line.indexOf(delim));
//                line = line.substring(line.indexOf(delim) + delim.length());
//
//                // read hostname
//                String hostname = line.substring(0, line.indexOf(delim));
//                line = line.substring(line.indexOf(delim) + delim.length());
//
//                // read installRoot
//                String installRoot = line.substring(0, line.indexOf(delim));
//                line = line.substring(line.indexOf(delim) + delim.length());
//                
//                // read the cellkey
//                cellKey = line.substring(0, line.indexOf(delim));
//                line = line.substring(line.indexOf(delim) + delim.length());
//
//                // read params
//                Map<InstallAppParm, String> params = new HashMap<InstallAppParm, String>();
//                String param = null;
//                String paramVal = null;
//                int index = 0;
//                while (index < line.length()) {
//                    param = line.substring(index, line.indexOf(delim, index));
//                    index = line.indexOf(delim, index) + delim.length();
//                    paramVal = line.substring(index, line.indexOf(delim, index));
//                    index = line.indexOf(delim, index) + delim.length();
//                    if (paramVal.equals("null")) {
//                        paramVal = null;
//                    }
//                    params.put(InstallAppParm.valueOf(param), paramVal);
//                }
//                List<String> appNameList = appNames.get(cellKey);
//                if(appNameList == null) {
//                    appNameList = new ArrayList<String>();
//                }
//                appNameList.add(appName);
//                appNames.put(cellKey, appNameList);
//                List<InstallAppParmContainer> containerList = containerMap.get(cellKey);
//                if(containerList == null) {
//                    containerList = new ArrayList<InstallAppParmContainer>();
//                }
//                containerList.add(new InstallAppParmContainer(hostname, installRoot, params));
//                containerMap.put(cellKey, containerList);
//            }
//        } catch (Exception ee) {
//            ee.printStackTrace();
//            PrintToFile file = new PrintToFile();
//            if (install)
//                file.printBuf(AppConst.INSTALL_ERR_LOG, "Could not read list of batched applications\n"
//                        + ee.getMessage());
//            else
//                file.printBuf(AppConst.UNINSTALL_ERR_LOG, "Could not read list of batched applications\n"
//                        + ee.getMessage());
//        } finally {
//            try {
//                if (br != null)
//                    br.close();
//            } catch (IOException e) {
//                // let go
//            }
//        }
//
//        Object[] returnVal = new Object[3];
//        returnVal[0] = appNames;
//        returnVal[1] = containerMap;
//        return returnVal;
//    }
//
//    // Sleep seconds specified in the seconds parameter...
//    public static void sleepForSeconds(int seconds) {
//            System.out.println( "**Sleeping " + seconds + " seconds" );
//            long sleepTime = seconds * 1000;
//            long start     = System.currentTimeMillis();
//            try {
//                    Thread.sleep(sleepTime);
//            } catch (Exception e) {
//                    long end = System.currentTimeMillis();
//                    if ( (end - start) < sleepTime ){
//                        System.out.println("ERROR: Slept for less than " + seconds + " seconds.");
//                    }
//            }
//    }
}
