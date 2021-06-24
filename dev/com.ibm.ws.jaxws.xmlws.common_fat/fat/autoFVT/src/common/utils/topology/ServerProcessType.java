package common.utils.topology;

public enum ServerProcessType {

    DEPLOYMENT_MANAGER("DEPLOYMENT_MANAGER"), 
    NODE_AGENT("NODE_AGENT"), 
    APPLICATION_SERVER("APPLICATION_SERVER"),
    JOB_MANAGER("JOB_MANAGER"),
    ADMIN_AGENT("ADMIN_AGENT"),
    WEB_SERVER("WEB_SERVER");

    private final String serverProcessType;
    private ServerProcessType(String serverProcessType) {
        this.serverProcessType = serverProcessType;
    }

    /**
     * Returns the server process type
     * 
     * @return The server process type
     */
    public String getServerProcessType() {
        return this.serverProcessType;
    }
}
