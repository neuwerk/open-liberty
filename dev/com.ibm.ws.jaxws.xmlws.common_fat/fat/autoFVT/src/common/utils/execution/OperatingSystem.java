package common.utils.execution;

public enum OperatingSystem {

    WINDOWS("WINDOWS"), ISERIES("ISERIES"), ZOS("ZOS"), LINUX("LINUX"), HP("HP"), SOLARIS("SOLARIS"), AIX("AIX");

    private final String osName;
    private OperatingSystem(String osName) {
        this.osName = osName;
    }

    /**
     * Returns the String version of this Operating System
     * 
     * @return The String version of this Operating System
     */
    public String getOSName() {
        return this.osName;
    }

    /**
     * Returns an OperatingSystem for the <code>osName</code> passed in. This <code>osName</code> should be based on
     * the Java System OS name returned.
     * 
     * @param osName
     *            The os name, based on the System OS name.
     * @return An OperatingSystem representation of the osName String
     */
    public static OperatingSystem getOS(String osName) {
        String OS_NAME_WINDOWS = "Win";
        String OS_NAME_ISERIES = "OS400";
        String OS_NAME_ZOS = "OS/390";
        String OS_NAME_ZOS_2 = "z/OS";
        String OS_NAME_LINUX = "Linux";
        String OS_NAME_HP = "HP";
        String OS_NAME_SOLARIS = "Solaris";
        String OS_NAME_SOLARIS_2 = "Sun";
        String OS_NAME_AIX = "AIX";

        OperatingSystem os = null;
        if (osName.indexOf(OS_NAME_WINDOWS) != -1) {
            os = OperatingSystem.WINDOWS;
        } else if (osName.indexOf(OS_NAME_ISERIES) != -1) {
            os = OperatingSystem.ISERIES;
        } else if ((osName.indexOf(OS_NAME_ZOS) != -1) || (osName.indexOf(OS_NAME_ZOS_2) != -1)) {
            os = OperatingSystem.ZOS;
        } else if (osName.indexOf(OS_NAME_LINUX) != -1) {
            os = OperatingSystem.LINUX;
        } else if (osName.indexOf(OS_NAME_HP) != -1) {
            os = OperatingSystem.HP;
        } else if ((osName.indexOf(OS_NAME_SOLARIS) != -1) || (osName.indexOf(OS_NAME_SOLARIS_2) != -1)) {
            os = OperatingSystem.SOLARIS;
        } else if (osName.indexOf(OS_NAME_AIX) != -1) {
            os = OperatingSystem.AIX;
        } else {
            // unknown os
            os = null;
        }
        return os;
    }
}
