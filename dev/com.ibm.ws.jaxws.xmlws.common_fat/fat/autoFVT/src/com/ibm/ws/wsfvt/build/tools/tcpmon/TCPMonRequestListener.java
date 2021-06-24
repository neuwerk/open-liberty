//
// 1.3, 4/13/09
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
// 01/08/08 sedov       490688           Synchronized stopFlows
// 04/10/09 gkuo        584703           TCPMonitor need to wait a little while for serverSocket to start
//

package com.ibm.ws.wsfvt.build.tools.tcpmon;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

/**
 * TCPMon request listener thread. This thread is responsible for capturing
 * incoming client connections and assigning them to a new TCP Mon flow
 */
class TCPMonRequestListener extends Thread {
	
	private ServerSocket _serverSocket = null;

	// active flows associated with this client port
	final private List<TCPMonFlow> _connections = new Vector<TCPMonFlow>();
	
	private int _listenPort;

	private boolean _stopRequested = false;

	private TCPMonEvenListener _listener;

	private String _targetHost;

	private int max_flows = 3;
	
	private int _targetPort;

	private boolean DEBUG = TCPMonitor.DEBUG_DEFAULT;

	private int SO_TIMEOUT = TCPMonitor.SO_TIMEOUT_DEFAULT;

	public TCPMonRequestListener(String id, int port) {
		super(id);
		_listenPort = port;
		start();
	}

	/**
	 * Open a service that listens on the specified port and accepts
	 * connections.
	 */
	public void run() {
		try {

			// Create a new service on the specified port
			_serverSocket = new ServerSocket(_listenPort);
                        TCPMonitor.setSocketRunning( true ); // Tell the TCPMonitor that socket is in place

			while (!_stopRequested) {
				// Wait until a request is made, then
				// accept the connection and create
				// a thread to handle it.
				Socket incomingSocket = _serverSocket.accept();
				createFlow(incomingSocket);

				incomingSocket = null;
			}

		} catch (Exception exp) {
			System.out.println("TCPMonRequestListener exception " + exp);
			if (!"socket closed".equals(exp.getMessage())) {
				stopFlows();
			}
		}
	}

	/**
	 * Create a new socket flow for this client and start it
	 * @param incomingSocket
	 */
	public void createFlow(Socket incomingSocket) {

		// Create a new communication (pair) and add it to
		// the list of messages recorded by this monitor.
		TCPMonFlow flow = new TCPMonFlow();
		flow.setListener(_listener);
		flow.setInSocket(incomingSocket);
		flow.setTarget(_targetHost, _targetPort);
		flow.setDebug (DEBUG);
		flow.setSoTimeout(SO_TIMEOUT);
		flow.start();
		
		_connections.add(flow);
		
		if (max_flows == _connections.size()){
			TCPMonFlow f = _connections.get(0);
			f.halt();
			_connections.remove(0);
		}
	}
	
	/**
	 * Stop all flows associated with this client port
	 */
	private void stopFlows() {
		
		synchronized (_connections){
			for (TCPMonFlow flow: _connections){
				flow.stopFlow();
				flow.interrupt();
			}
			_connections.clear();
		}
	}
	
	/**
	 * Safely shut down the service listener and its associated sockets
	 */
	public void halt() {
		try {

			_stopRequested = true;
			// Connect a new temporary socket in order to
			// unblock the accept() method, then close the
			// server socket.
			Socket tmpSocket = new Socket("127.0.0.1", _listenPort);
			if (_serverSocket != null) _serverSocket.close();
			if (tmpSocket != null) tmpSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		stopFlows();
	}

	public void setConnection(int listenPort, String targetHost, int targetPort) {
		this._listenPort = listenPort;
		this._targetHost = targetHost;
		this._targetPort = targetPort;
		
	}

	public void setListener(TCPMonEvenListener listener) {
		this._listener = listener;
		
	}

	public void setDebug(boolean debug) {
		this.DEBUG = debug;
	}

	public void setSoTimeout(int so_timeout) {
		this.SO_TIMEOUT = so_timeout;
	}

}
