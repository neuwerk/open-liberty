package com.ibm.ws.wsfvt.build.tasks.liberty;

import java.util.Collection;

import org.apache.tools.ant.Task;

import com.ibm.websphere.simplicity.Topology;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

public abstract class ParentTask extends Task {
	
	private static final String DEFAULT_SERVER_NAME = "jaxws_fat_server";	
	
	protected static final String START_MESSAGE_CODE = "CWWKF0011I";
    protected static final String STOP_MESSAGE_CODE = "CWWKE0036I";
    protected static final String START_APP_MESSAGE_CODE_REG = "CWWKZ0001I.*";
    protected static final String STOP_APP_MESSAGE_CODE_REG = "CWWKZ0009I.*";
    
    protected static LibertyServer libServer;
	protected static String libertyServerName;
    
    static {
    	try {
    		Topology.init();    		
			Collection<LibertyServer> knownLibertyServers = LibertyServerFactory.getKnownLibertyServers("com.ibm.websphere.simplicity.Topology");
			for (LibertyServer server : knownLibertyServers) {
				libServer = server;
				libertyServerName = libServer.getServerName();
				// only use first defined server
				break;				
			}
			
			if (libServer == null) {
				libServer = LibertyServerFactory.getLibertyServer(DEFAULT_SERVER_NAME, null, false);
				libertyServerName = libServer.getServerName();
			}
    	} catch (Exception e) {
    		System.out.println("Got exception: " + e.getMessage());
    		e.printStackTrace();    		
    	}
    }
    
    public boolean ensureServerStarted() {
		try {
			String gotMsg = null;
			if (!libServer.isStarted()) {
				libServer.startServer();
				gotMsg = libServer.waitForStringInLog(START_MESSAGE_CODE,
						10 * 1000, libServer.getDefaultLogFile());
				if (gotMsg != null) {
					System.out.println(libServer.getServerName()
							+ " has started.");
					return true;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			System.out.println("Got exception: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
    }
    
    public LibertyServer getLibServer() {
		return libServer;
	}	
    
    public String getServerName() {
    	return libertyServerName;
    }

}
