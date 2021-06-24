//
// @(#) 1.14 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ExecuteMoonstone.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/26/11 14:54:28 [8/8/12 06:41:16]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2011
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// ...      jramos
//          jtnguyen    674645          fixed windows JVM issue
//          jramos      682634          added iSeries support
//          jtnguyen    686221          fixed NPE on Z on moonstone
// 01/26/11 jtnguyen    686221.1        backed out the fix for Z


package com.ibm.ws.wsfvt.build.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Cell;
import com.ibm.websphere.simplicity.LocalFile;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Node;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Topology;

/**
 * This Ant task controls test execution betweeen the autowas server and the
 * target FVT machine. It handles zipping up tests on the autowas machine,
 * transferring to the FVT machine, unzipping, compiling and executing, then
 * transferring results back to the autowas machine. It is needed because the
 * normal paradigm of executing on the autowas machine against some remote WAS
 * install does not adequately test webservices tooling, and because these tests
 * require a copy of WAS in order to compile, which is not present on the
 * autowas machine. For example, to always build on an autowas linux box (even
 * if we could) would mean that we never exercise the build time tooling on any
 * other platform. Note that for z/os, the build time tooling is present but
 * unsupported, so there are three machines involved, the autowas machine, a
 * helper machine, and the z/os machine. In this case, this task coordinates
 * between the autowas and helper machine. This execute phase of this task then
 * calls an ant script (zos/autowas/build.xml) which compiles the tests on the
 * helper machine then transfers and executes them on the z machine.
 */
public class ExecuteMoonstone extends Task {

    // note on testCell and executionCell:
    //
    // for FVT on distributed,  testCell is the same as executionCell
    // for FVT on Z: there are 2 different cells
    // testCell      - the cell on machine the test will run.  This is the Z/OS machine
    // executionCell - on the machine where things started to run initially.  This is the windows or linux
    //                 machine where we compile the test cases
    
    private Cell    testCell        = null;              
    private Cell    executionCell   = null;
    private String  archiveBuildXml = "archive_build.xml";
    private String  websvcsZip      = "websvcs.fvt.zip";
    private String  grouping        = "runFvt";
    private String  zFvtHome        = "";
    private String  zGroupingDef    = "";
    private boolean isZOS          = false;
    private boolean isWin          = false;


    public void execute() throws BuildException {
        try {
            // first step is determine if this is a Z/OS run
            OperatingSystem os = null;
            Topology.init();
            List<Cell> cells = Topology.getCells();
            for (Cell cell : cells) {
                os = cell.getManager().getNode().getMachine().getOperatingSystem();
                if (OperatingSystem.ZOS.equals(os)) {
                    isZOS = true;
                    testCell = cell;
                    if (this.zFvtHome.length() == 0 || this.zFvtHome.equals("${zFvtHome}")) {
                        Machine zOSMachine = testCell.getManager().getNode().getMachine();
                        this.zFvtHome =
                            zOSMachine.execute("echo", new String[] {"$HOME"}).getStdout().trim();
                    }
                } else {
                    executionCell = cell;
                    if (OperatingSystem.WINDOWS.equals(os))
                        isWin = true;                
                }
            }
            if (!isZOS)
                testCell = Topology.getCells().get(0);

            setup();
            packageTests();
            copyTests();
            unpackageTests();
            executeTests();
            collectResults();
            cleanup();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException(e);
        } finally {
            try {
                if (testCell != null)
                    testCell.disconnect();
                if (executionCell != null)
                    executionCell.disconnect();
            } catch (Exception e) {
            }
        }

    }

    private void setup() throws Exception {
        log("Writing properties into bootstrapping.properties that are needed to run on the remote machine.");
        PrepareBootstrappingProps prepareBootstrapFile = new PrepareBootstrappingProps();
        if (isZOS)
            prepareBootstrapFile.setZOSCellKey(testCell.getBootstrapFileKey());
        prepareBootstrapFile.execute();

        // copy the bootstrapping.properties file into websvcs.fvt
        String fvtBaseDir = ((new File(".")).getAbsolutePath() + "/autoFVT");
        LocalFile bootstrapFile = new LocalFile(Topology.getBootStrappingFile().getAbsolutePath());
        LocalFile frameworkBootstrapFile = new LocalFile(fvtBaseDir + "/bootstrapping.properties");
        log("Copying autoWAS bootstrapping.properties located at " + bootstrapFile
            .getAbsolutePath()
            + " to the framework located at "
            + frameworkBootstrapFile.getAbsolutePath()
            + ".");
        bootstrapFile.copyToDest(frameworkBootstrapFile);

        LocalFile keystore =
            new LocalFile(Topology.getBootstrapFileOps().getConfigurationProvider()
                .getProperty("keystore"));
        LocalFile frameworkKeystore =
            new LocalFile(fvtBaseDir + "/common/files/" + keystore.getName());
        log("Copying the autowas keystore file " + keystore.getAbsolutePath()
            + " to the framework at location "
            + frameworkKeystore.getAbsolutePath()
            + ".");
        keystore.copyToDest(frameworkKeystore);

        // edit ezwas.props
        if (isZOS) {
            String ezwasFile = fvtBaseDir + "/zos/autowas/ezwas.props";
            Properties ezwasProps = new Properties();
            ezwasProps.load(new FileInputStream(ezwasFile));
            log(this.zFvtHome);
            ezwasProps.setProperty("ezwas.remote.test.dir", this.zFvtHome);
            String wasInstallRoot =
                executionCell.getManager().getNode().getWASInstall().getInstallRoot();
                
            if (isWin) {  // is distributed fvt machine a windows?
                wasInstallRoot = wasInstallRoot.replace('/','\\');  // we need backslashes
                log("wasInstallRoot for distributed: " + wasInstallRoot);  
            }

            ezwasProps.setProperty("ezwas.local.was.home.dir", wasInstallRoot);
            String profileDir = executionCell.getManager().getNode().getProfileDir();
            String antHome = profileDir + "/autoFVT/common/jars/ant";
            ezwasProps.setProperty("ezwas.local.ant.home.dir", antHome);
            ezwasProps.store(new FileOutputStream(ezwasFile), "");
        }
    }

    private void packageTests() throws Exception {
        Machine machine = Machine.getLocalMachine();
        String cmd = "zip";
        String[] params = new String[] {"-ru", websvcsZip, "autoFVT"};
        log("Zipping up the fvt code...");
        ProgramOutput po = machine.execute(cmd, params);
        log(po.getStdout());
        log(po.getStderr());
    }

    private void copyTests() throws Exception {
        LocalFile fvtCode = new LocalFile(websvcsZip);
        Machine destMachine = executionCell.getManager().getNode().getMachine();
        RemoteFile dest = new RemoteFile(destMachine, getRemoteWebsvcsDest(), websvcsZip);
        log("Copying the FVT code to the execution machine " + destMachine.getHostname());
        fvtCode.copyToDest(dest);
    }

    private void unpackageTests() throws Exception {
        // unzip is not guaranteed to be on the target machine.
        // Also, on iSeries, JAVA_HOME is not located at WAS_HOME/java.
        // To make sure we can unzip the files we'll use an ANT script
        // and run it through ws_ant.
        RemoteFile parent = getRemoteWebsvcsDest();
        RemoteFile remoteWebsvcsZip = new RemoteFile(parent.getMachine(), parent, websvcsZip);
        Node managerNode = executionCell.getManager().getNode();
        String binDir = managerNode.getProfileDir() + "/bin";

        log("Copying " + archiveBuildXml + " to " + binDir);
        RemoteFile remoteUnzipBuildXml =
            new RemoteFile(parent.getMachine(), binDir + "/" + archiveBuildXml);
        LocalFile localUnzipBuildXml =
            new LocalFile("autoFVT/common/files/" + archiveBuildXml);
        localUnzipBuildXml.copyToDest(remoteUnzipBuildXml);

        String ws_ant_cmd =
            binDir + "/ws_ant"
                + managerNode.getMachine().getOperatingSystem().getDefaultScriptSuffix();
        String[] args =
            new String[] {"-f", remoteUnzipBuildXml.getAbsolutePath(), "unzip_websvcs_zip",
                "-Dwebsvcs_zip=" + remoteWebsvcsZip.getAbsolutePath(),
                "-DprofileDir=" + managerNode.getProfileDir(),
                "-Dfvttop=" + managerNode.getProfileDir()};
        log("Unzipping tests on execution machine");
        ProgramOutput po = null;
        if(remoteWebsvcsZip.getMachine().getOperatingSystem() == OperatingSystem.ISERIES) {
            po = remoteWebsvcsZip.getMachine().executeQSH(ws_ant_cmd, args, binDir, null);
        } else {
            po = remoteWebsvcsZip.getMachine().execute(ws_ant_cmd, args, binDir);
        }
        log(po.getStdout());
        log(po.getStderr());
    }

    private void executeTests() throws Exception {
        Machine m = executionCell.getManager().getNode().getMachine();
        String profileDir = executionCell.getManager().getNode().getProfileDir();
        String fvtHome = profileDir + "/autoFVT";
        log("Running chmod on FVT tree");
        String chmod = "chmod";
        String[] chmodArgs = new String[] {"-R", "777", fvtHome};
        ProgramOutput po = m.execute(chmod, chmodArgs, fvtHome);
        log(po.getStdout());
        log(po.getStderr());
        // find JAVA_HOME from ws_ant
        String binDir = profileDir + "/bin";
        log("Copying " + archiveBuildXml + " to " + binDir);
        RemoteFile remoteUnzipBuildXml = new RemoteFile(m, binDir + "/" + archiveBuildXml);
        LocalFile localUnzipBuildXml =
            new LocalFile("autoFVT/common/files/" + archiveBuildXml);
        localUnzipBuildXml.copyToDest(remoteUnzipBuildXml);
        log("Finding the value of JAVA_HOME");
        String ws_ant_cmd = binDir + "/ws_ant" + m.getOperatingSystem().getDefaultScriptSuffix();
        String[] antArgs =
            new String[] {"-buildfile", remoteUnzipBuildXml.getAbsolutePath(), "get_java_home"};
        if(m.getOperatingSystem() == OperatingSystem.ISERIES) {
            po = m.executeQSH(ws_ant_cmd, antArgs, binDir, null);
        } else {
            po = m.execute(ws_ant_cmd, antArgs, binDir);
        }
        String output = po.getStdout();
        log(po.getStdout());
        log(po.getStderr());
        StringTokenizer st = new StringTokenizer(output, "\r\n\f\t");
        String java_home = st.nextToken();
        while (java_home.indexOf("JAVA_HOME") == -1 && st.hasMoreTokens())
            java_home = st.nextToken();
        java_home =
            java_home.substring(java_home.indexOf("JAVA_HOME=") + "JAVA_HOME=".length()).trim().replace("\\", "/");
        log("JAVA_HOME is " + java_home);
        log("Executing test cases...");
        String ant = fvtHome + "/common/jars/ant/bin/ant";
        if (m.getOperatingSystem().equals(OperatingSystem.WINDOWS))
            ant += ".bat";
        if (!isZOS)
            antArgs =
                new String[] {"-buildfile", fvtHome + "/src/xmls/executeFVT.xml",
                    "-DgroupingTarget=" + this.grouping};
        else
            antArgs =
                new String[] {"-buildfile", fvtHome + "/zos/autowas/build.xml",
                    "-DzGroupingDef=" + this.zGroupingDef};
        Properties envVars = new Properties();
        envVars.put("JAVA_HOME", java_home);
        envVars.put("ANT_HOME", fvtHome + "/common/jars/ant");
        po = m.execute(ant, antArgs, envVars);
        log(po.getStdout());
        log(po.getStderr());
    }

    private void collectResults() throws Exception {
        // zip is not guaranteed to be on the target machine.
        // Also, on iSeries, JAVA_HOME is not located at WAS_HOME/java.
        // To make sure we can zip the files we'll use an ANT script
        // and run it through ws_ant.
        log("Zipping up the reports...");
        Machine m = null;
        String profileDir = null;
        String fvtTop = null;
        if (!isZOS) {
            m = executionCell.getManager().getNode().getMachine();
            profileDir = executionCell.getManager().getNode().getProfileDir();
            fvtTop = profileDir;
        } else {
            m = testCell.getManager().getNode().getMachine();
            profileDir = testCell.getManager().getNode().getProfileDir();
            fvtTop = this.zFvtHome;
        }
        String binDir = profileDir + "/bin";
        log("Copying " + archiveBuildXml + " to " + binDir);
        RemoteFile remoteUnzipBuildXml = new RemoteFile(m, binDir + "/" + archiveBuildXml);
        LocalFile localUnzipBuildXml =
            new LocalFile("autoFVT/common/files/" + archiveBuildXml);
        localUnzipBuildXml.copyToDest(remoteUnzipBuildXml);

        String ws_ant_cmd = binDir + "/ws_ant" + m.getOperatingSystem().getDefaultScriptSuffix();
        String[] args =
            new String[] {"-f", remoteUnzipBuildXml.getAbsolutePath(), "zip_results",
                "-Dfvttop=" + fvtTop};
        String fvtHome = fvtTop + "/autoFVT";
        ProgramOutput po = null;
        if(m.getOperatingSystem() == OperatingSystem.ISERIES) {
            po = m.executeQSH(ws_ant_cmd, args, fvtHome, null);
        } else {
            po = m.execute(ws_ant_cmd, args, fvtHome);
        }
        log(po.getStdout());
        log(po.getStderr());
        log("Copying the results back to the autowas client...");
        LocalFile dest = new LocalFile("results.zip");
        RemoteFile source = m.getFile(fvtHome + "/results.zip");
        dest.copyFromSource(source);
        log("Unzipping the reports...");
        String cmd = "unzip";
        args = new String[] {"results.zip"};
        po = Machine.getLocalMachine().execute(cmd, args);
        log(po.getStdout());
        log(po.getStderr());
    }

    private void cleanup() throws Exception {
        if (isZOS) {
            log("Zipping up the built tests");
            Machine zOSMachine = testCell.getManager().getNode().getMachine();
            String installRoot = testCell.getManager().getNode().getWASInstall().getInstallRoot();
            /*
             * zip up the tests just in case for problem determination
             */
            RemoteFile fvtBuiltZip = zOSMachine.getFile(this.zFvtHome + "/FVT-built.zip");
            if (fvtBuiltZip.exists()) {
                fvtBuiltZip.delete();
            }
            String cmd = installRoot + "/java/bin/jar";
            String[] args = new String[] {"cvf", this.zFvtHome + "/FVT-built.zip", "FVT"};
            ProgramOutput po = zOSMachine.execute(cmd, args, this.zFvtHome);
            log(po.getStdout());
            log(po.getStderr());

            /*
             * delete the tests
             */
            RemoteFile fvtTopRemoteFile = zOSMachine.getFile(this.zFvtHome + "/FVT");
            fvtTopRemoteFile.delete();

            RemoteFile fvtZip = zOSMachine.getFile(this.zFvtHome + "/websvcs.zip");
            if (fvtZip.exists()) {
                fvtZip.delete();
            }
        }
    }

    private RemoteFile getRemoteWebsvcsDest() throws Exception {
        RemoteFile dest = null;
        Machine destMachine = executionCell.getManager().getNode().getMachine();
        dest = destMachine.getFile(executionCell.getManager().getNode().getProfileDir());
        log("Destination directory for websvcs code on host " + destMachine.getHostname()
            + " is "
            + dest.getAbsolutePath());
        return dest;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    public void setZFvtHome(String zFvtHome) {
        this.zFvtHome = zFvtHome;
    }

    public void setZGroupingDef(String zGroupingDef) {
        this.zGroupingDef = zGroupingDef;
    }
}
