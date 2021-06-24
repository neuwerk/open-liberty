package common.utils.topology;

import com.ibm.websphere.simplicity.Machine;

public class TopologyActions {

    public static String FVT_HOSTNAME;
    public static IMachine FVT_MACHINE;
    
    static {
        try {
            FVT_MACHINE = new IMachine(Machine.getLocalMachine());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        FVT_HOSTNAME = FVT_MACHINE.getHostname();
    }
}
