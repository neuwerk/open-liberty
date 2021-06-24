package common.utils.topology;

import java.io.File;

import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.Topology;

public class MachineFactory {

    public static IMachine getMachine(File topologyFile, String hostname) {
        if(!Topology.getBootStrappingFile().equals(topologyFile)) {
            throw new IllegalArgumentException("Incorrect topology file " + topologyFile + ". Current topology file is " + Topology.getBootStrappingFile());
        }
        try {
            ConnectionInfo c = Topology.getBootstrapFileOps().getMachineConnectionInfo(hostname);
            Machine m = Machine.getMachine(c);
            return new IMachine(m);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
