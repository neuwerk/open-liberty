package common.utils.execution;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Topology;

public class IExecution {

    public static final String LOCALHOST = "LOCALHOST";
    
    private static final String FILE_SUFFIX_DEFAULT = ".sh";
    private static final String FILE_SUFFIX_WINDOWS = ".bat";
    private static final String FILE_SUFFIX_I = "";
    private static final String FILE_ENCODING_DEFAULT = "ISO8859-1";
    private static final String FILE_ENCODING_Z = "cp1047";
    
    @SuppressWarnings("unchecked")
    public String executeCommand(String command, List parms, String hostname) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            Object[] temp = new Object[0];
            if(parms != null) {
                temp = parms.toArray(new Object[0]);
            }
            String[] parameters = new String[temp.length];
            for(int i = 0; i < temp.length; ++i) {
                parameters[i] = (String)temp[i];
            }
            ProgramOutput ps = m.execute(command, parameters);
            return "System.out: " + ps.getStdout() + "\n\nSystem.err: " + ps.getStderr();
        } catch(Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public String executeCommand(String command, List parms, String hostname, String workDir, String[] envVars) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            Object[] temp = new Object[0];
            if(parms != null) {
                temp = parms.toArray(new Object[0]);
            }
            String[] parameters = new String[temp.length];
            for(int i = 0; i < temp.length; ++i) {
                parameters[i] = (String)temp[i];
            }
            Properties envVariables = new Properties();
            String prop = null;
            String value = null;
            if(envVars != null) {
                for(int i = 0; i < envVars.length; ++i) {
                    prop = envVars[i].split("=")[0];
                    value = envVars[i].split("=")[1];
                    envVariables.setProperty(prop, value);
                }
            }
            ProgramOutput ps = m.execute(command, parameters, workDir, envVariables);
            return "System.out: " + ps.getStdout() + "\n\nSystem.err: " + ps.getStderr();
        } catch(Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String executeCopyDirectory(String srcDir, String srcHost, String destDir, String destHost) throws ExecutionException {
        try {
            Machine srcM = getMachine(srcHost);
            Machine destM = getMachine(destHost);
            RemoteFile srcD = srcM.getFile(srcDir);
            RemoteFile destD = destM.getFile(destDir);
            boolean result = srcD.copyToDest(destD, true, true);
            if(!result) {
                throw new ExecutionException("Failed to copy directory.");
            }
            return "";
        } catch(Exception e) {
            throw new ExecutionException(e); 
        }
    }
    
    public String executeCopyFile(String srcFile, String srcHost, String destFile, String destHost) throws ExecutionException {
        try {
            Machine srcM = getMachine(srcHost);
            Machine destM = getMachine(destHost);
            RemoteFile srcF = srcM.getFile(srcFile);
            RemoteFile destF = destM.getFile(destFile);
            boolean result = srcF.copyToDest(destF);
            if(!result) {
                throw new ExecutionException("Failed to copy file.");
            }
            return "";
        } catch(Exception e) {
            throw new ExecutionException(e); 
        }
    }
    
    public String executeCreateDirectory(String hostname, String dir) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            RemoteFile d = m.getFile(dir);
            if(!d.exists()) {
                boolean result = d.mkdirs();
                if(!result) {
                    throw new ExecutionException("Failed to create directory.");
                }
            }
            return "";
        } catch(Exception e) {
            throw new ExecutionException(e); 
        }
    }
    
    public String executeDeleteFileSystemEntry(String hostname, String entry) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            RemoteFile f = m.getFile(entry);
            boolean result = f.delete();
            if(!result) {
                throw new ExecutionException("Failed to delete file system entry.");
            }
            return "";
        } catch(Exception e) {
            throw new ExecutionException(e); 
        }
    }
    
    public String executeGetExecFileSuffix(OperatingSystem os) throws ExecutionException {
        if (os == OperatingSystem.WINDOWS) {
            return FILE_SUFFIX_WINDOWS;
        } else if (os == OperatingSystem.ISERIES) {
            return FILE_SUFFIX_I;
        } else {
            return FILE_SUFFIX_DEFAULT;
        }
    }
    
    public String executeGetFileContents(String file, String hostname) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            RemoteFile f = m.getFile(file);
            InputStream is = f.openForReading();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ret = "";
            String line = null;
            while((line = br.readLine()) != null) {
                ret += line;
            }
            br.close();
            return ret;
        } catch(Exception e) {
            throw new ExecutionException(e); 
        }
    }
    
    public String executeGetFileSeparator(String hostname) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            return m.getOperatingSystem().getFileSeparator();
        } catch(Exception e) {
            throw new ExecutionException(e); 
        }
    }
    
    public String executeGetLineSeparator(String hostname) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            return m.getOperatingSystem().getLineSeparator();
        } catch(Exception e) {
            throw new ExecutionException(e); 
        }
    }
    
    public String executeGetLocalExecFileSuffix() throws ExecutionException {
        try {
            return Machine.getLocalMachine().getOperatingSystem().getDefaultScriptSuffix();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String executeGetLocalFileSeparator() throws ExecutionException {
        try {
            return Machine.getLocalMachine().getOperatingSystem().getFileSeparator();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String executeGetLocalHostname() throws ExecutionException {
        try {
            return Machine.getLocalMachine().getHostname();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String executeGetLocalLineSeparator() throws ExecutionException {
        try {
            return Machine.getLocalMachine().getOperatingSystem().getLineSeparator();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public OperatingSystem executeGetLocalOperatingSystem() throws ExecutionException {
        try {
            return OperatingSystem.valueOf(Machine.getLocalMachine().getOperatingSystem().toString());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String executeGetLocalOSEncoding() throws ExecutionException {
        try {
            return Machine.getLocalMachine().getOperatingSystem().getDefaultEncoding();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String executeGetLocalPathSeparator() throws ExecutionException {
        try {
            return Machine.getLocalMachine().getOperatingSystem().getPathSeparator();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public OperatingSystem executeGetOperatingSystem(String hostname) throws ExecutionException {
        try {
            return OperatingSystem.valueOf(getMachine(hostname).getOperatingSystem().toString());
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String executeGetOSEncoding(OperatingSystem os) throws ExecutionException {
        if (os == OperatingSystem.ZOS) {
            return FILE_ENCODING_Z;
        } else {
            return FILE_ENCODING_DEFAULT;
        }
    }
    
    public String executeGetPathSeparator(String hostname)  throws ExecutionException {
        try {
            return getMachine(hostname).getOperatingSystem().getPathSeparator();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String[] executeListDirectory(String directory, String hostname) throws ExecutionException {
        return executeListDirectoryRecursive(directory, hostname, null);
    }
    
    public String[] executeListDirectory(String directory, String hostname, String type) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            RemoteFile dir = m.getFile(directory);
            RemoteFile[] children = dir.list(false);
            String[] ret = new String[children.length];
            for(int i = 0; i < children.length; ++i) {
                ret[i] = children[i].getName();
            }
            return ret;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    public String[] executeListDirectoryRecursive(String directory, String hostname, String type) throws ExecutionException {
        try {
            Machine m = getMachine(hostname);
            RemoteFile dir = m.getFile(directory);
            RemoteFile[] children = dir.list(true);
            if(type != null && ("F".equals(type) || "D".equals(type))) {
                List<RemoteFile> temp = new ArrayList<RemoteFile>();
                for(int i = 0; i < children.length; ++i) {
                    if(("F".equals(type) && children[i].isFile()) || ("D".equals(type) && children[i].isDirectory())) {
                        temp.add(children[i]);
                    }
                }
                children = new RemoteFile[temp.size()];
                children = temp.toArray(children);
            }
            String[] ret = new String[children.length];
            for(int i = 0; i < children.length; ++i) {
                ret[i] = children[i].getAbsolutePath();
            }
            return ret;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
    
    private Machine getMachine(String hostname) throws Exception {
        ConnectionInfo c = Topology.getBootstrapFileOps().getMachineConnectionInfo(hostname);
        Machine m = Machine.getMachine(c);
        return m;
    }
}
