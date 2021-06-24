//
// 1.3, 4/10/09
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect           Description
// ----------------------------------------------------------------------------
// 05/18/07 sedov       440313           New File
// 04/10/09 gkuo        594703           Ask callers to wait for socket to start, but no more than 20 seconds
//

package com.ibm.ws.wsfvt.build.tools.tcpmon;
import java.lang.ThreadDeath;

/**
 * Main interface class into TCPMon utility. Respoinsible for managing
 * TCPMon instances and the state of the associated request listener
 */
public class TCPMonitor {

        private static boolean _SocketRunning = false;
	public static boolean DEBUG_DEFAULT = false;
	public static int SO_TIMEOUT_DEFAULT = 30000;
	
	private static boolean DEBUG = false;
	
	private static int SO_TIMEOUT = SO_TIMEOUT_DEFAULT;
	
	// singleton instance
	private static TCPMonitor instance = null;
	
	// instance id of this TCPMon...this is how we can tell if
	// we should create a new instance of a TCPMon or reuse
	// an existing one
	private String instanceId = null; 

	// The listener service for handling incoming requests
	public TCPMonRequestListener _requestListener = null;
	
	// singleton constructor
	private TCPMonitor() {}

	/**
	 * Destroys the instance of TCPMon and all of its active connections
	 * @param instance
	 */
	public static synchronized void terminate(TCPMonitor instance) {
		System.out.println("TCPMonitor terminating: " + instance.instanceId);
		
		instance._requestListener.halt();
		instance = null;
	}
	
	/**
	 * Create a new monitor which listens on the
	 * 
	 * @listenPort and forwards messages to the
	 * @targetPort on
	 * @targetHost.
	 * 
	 * @param listenPort
	 *            The local port where clients will attempt to connect
	 * @param targetHost
	 *            The actual host where the service is located
	 * @param targetPort
	 *            The actual port on the service host where the service is
	 *            located
	 */
	public synchronized static TCPMonitor instance(int listenPort, String targetHost, int targetPort, TCPMonEvenListener listener){
                boolean bWaitMore = false;
		String newInstanceId = "localhost:" + listenPort + "-" + targetHost + ":" + targetPort;
		if (instance == null){
                        bWaitMore = true;
                        setSocketRunning( false );
			System.out.println("TCPMonitor created new connection: " + newInstanceId);
			TCPMonitor tcp = new TCPMonitor();
			
			tcp.instanceId = newInstanceId;
			tcp._requestListener = new TCPMonRequestListener(newInstanceId, listenPort);
			tcp._requestListener.setConnection(listenPort, targetHost, targetPort);
			tcp._requestListener.setListener(listener);
			tcp._requestListener.setDebug(DEBUG);
			tcp._requestListener.setSoTimeout(SO_TIMEOUT);
			
			instance = tcp;
		} else {
			if (!instance.instanceId.equalsIgnoreCase(newInstanceId)){
				terminate(instance);
                                setSocketRunning( false );
				return instance(listenPort, targetHost, targetPort, listener);
			} else {
				System.out.println("TCPMonitor reusing connection: " + newInstanceId);
                                instance._requestListener.setListener(listener);
			}
		}
		
                // Let's wait for serverSocket for at least 20 second.
                int iCnt = 0;
                while( iCnt < 10 && !getSocketRunning() ){
                    System.out.println( "Waiting for socket to start (" + iCnt + ") for 2 seconds" );
                    iCnt ++;
                    // and wait 2 seconds
                    try{
                        Thread.currentThread().sleep( 2000 );
                    }catch( Exception e ){
                    }
                }

                if( iCnt >= 10 ){
                    System.out.println( "Something must be wrong. The nserverSocket was not started in 20 seconds" );
                } else{
                    // Wait 3 more seconds to let the serverSocket thread fully started. Never know how slow a machine can be
                    if( bWaitMore ){
                        try{
                            System.out.println( "Waiting 3 more seconds. Never know how slow a machine can be" );
                            Thread.currentThread().sleep( 3000 );
                        }catch( Exception e ){
                        }
                    }
                }

		return instance;
	}

        public static void setSocketRunning( boolean bRunning){
            _SocketRunning = bRunning;
        }

        public static boolean getSocketRunning(){
            return _SocketRunning;
        }

}
