//
// @(#) 1.2 autoFVT/src/com/ibm/ws/wsfvt/build/tools/tcpmon/TCPMonFlow.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 9/16/09 12:03:11 [8/8/12 06:56:43]
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
//

package com.ibm.ws.wsfvt.build.tools.tcpmon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * A TCPMonFlow object connects the client port to the service port and manages
 * the request/response pair. Because HTTP is a stateless connection, the
 * lifespan of the connection is a single request/response pair (or timeout due
 * to error.)
 */
class TCPMonFlow extends Thread {

	private boolean DEBUG = true;

	static int flowId = 0;

	private int myFlowId = 0;
		
	private String _serviceHost;

	private int _servicePort;

	private Socket _clientSocket = null;

	private Socket _serviceSocket = null;

	private TCPMonEvenListener _listener;

	private int SO_TIMEOUT = 10000;

	private boolean _isActive = true;
	
	private enum READ_STATE { STATE_STATUS, STATE_HEADER, STATE_DATA , STATE_DONE, STATE_DONE_EOF};

	/**
	 * Create a new connection, given a socket created by this port's
	 * SocketWaiter
	 * 
	 * @param incomingSocket
	 * @param serviceHost
	 * @param servicePort
	 */
	public TCPMonFlow() {
		super("TCPMon.RequestListener-" + flowId);
		myFlowId = flowId++;
	}

	/**
	 * 
	 */
	public void run() {
		System.out.println("TCPMonFlow starting new flow " + myFlowId);
		try {

			// Create a socket for communicating to the service.
			_serviceSocket = new Socket(_serviceHost, _servicePort);

			_clientSocket.setKeepAlive(true);
			_serviceSocket.setKeepAlive(true);
			
			_isActive = true;
			while (_isActive) {
				
				flow(_clientSocket, _serviceSocket, _listener, false);
				
				if (_isActive)
					flow(_serviceSocket, _clientSocket, _listener, true);
			}

		} catch (Exception e) {
			if (DEBUG) {
				System.out.println("Flow exception " + e);
			}
		}
		
		halt();
		
		System.out.println("TCPMonFlow stopped flow " + myFlowId);
	}

	/**
	 * Handle a directional flow of HTTP Traffic. Capture http headers and data, dispatch the
	 * request to the handler for reprocessing and then send the data to the server
	 * @param _inSocket
	 * @param _outSocket
	 * @param listener
	 * @param isInbound
	 * @throws IOException
	 */
	private void flow(Socket _inSocket, Socket _outSocket,
			TCPMonEvenListener listener, boolean isInbound) throws IOException {

		final String FLOW = isInbound ? "InFlow" : "OutFlow" + "-" + myFlowId;
		
		InputStream _inStream = _inSocket.getInputStream();
		OutputStream _outStream = _outSocket.getOutputStream();
		
		StringBuffer lineBuffer = new StringBuffer();
		
		boolean isDoneLine = false;

		byte[] buffer = new byte[1];

		long max_length = -1;

		READ_STATE state = READ_STATE.STATE_STATUS;
		
		TCPMonDispatcher dispatch = new TCPMonDispatcher(isInbound, _outSocket.getInetAddress().getHostName(), _outSocket.getPort());
		
		_inSocket.setSoTimeout(SO_TIMEOUT);

		if (DEBUG) {
			System.out.println(FLOW + ".flow enter");
		}

		try {

			// vertical loop, read all lines
			while (state != READ_STATE.STATE_DONE && state != READ_STATE.STATE_DONE_EOF) {

				isDoneLine = false;
				lineBuffer.setLength(0);

				// line scanning loop
				while (!isDoneLine) {
					try {
						// read 1 byte of data from stream
						int count = _inStream.read(buffer, 0, buffer.length);
						isDoneLine = (count < 1);

						if (count != -1) {
							// if we've reached the end of line then exit this
							// loop
							isDoneLine = (buffer[0] == '\n');

							lineBuffer.append((char) buffer[0]);
						} else {
							// otherwise we;re done reading data altogether
							isDoneLine = true;
							state = READ_STATE.STATE_DONE_EOF;
						}

					} catch (Exception ex) {
						if (DEBUG) {
							System.out.println(FLOW + ".readLoop exception "
									+ ex);
						}
						
						// thread forcibly interrupted
						if (ex instanceof InterruptedException) {
							_isActive = false;
							state = READ_STATE.STATE_DONE;
						}
						
						// socket timeoed out but we haven't read any data
						if (ex instanceof SocketTimeoutException){
							_isActive = (state != READ_STATE.STATE_STATUS);
						}
						isDoneLine = true;
						state = READ_STATE.STATE_DONE;
					}

					// stop reading if we've reached the max_length
					if (state == READ_STATE.STATE_DATA
							&& max_length > 0
							&& (dispatch.getDataLength() + lineBuffer.length()) == max_length) {
						isDoneLine = true;
						state = READ_STATE.STATE_DONE;

						if (DEBUG) {
							System.out.println(FLOW + ".flow reached data maxLength");
						}
					}

				} // while (line scan)

				
				switch (state){
					case STATE_STATUS:
						dispatch.addHttpStatus(lineBuffer.toString());
						state = READ_STATE.STATE_HEADER;
						break;
						
					case STATE_HEADER:

						// get the content length
						String line = lineBuffer.toString();
						if (line.startsWith("Content-Length: ")) {
							max_length = 0;
							for (byte c : line.getBytes("ASCII")) {
								if (c >= '0' && c <= '9')
									max_length = max_length * 10 + (c - '0');
							}

							if (DEBUG) {
								System.out.println(FLOW + ".flow content length = "
										+ max_length);
							}
						}
						
						if (lineBuffer.length() < 3) {
							state = READ_STATE.STATE_DATA;

							if (DEBUG) {
								System.out.println(FLOW + ".flow begin body");
							}

							if (max_length == 0) {
								state = READ_STATE.STATE_DONE;
							}
						} else {
							dispatch.addHeader(line);
						}
						break;
						
					case STATE_DATA:
						dispatch.addData(lineBuffer.toString());
						break;
						
					case STATE_DONE:
						if (_isActive)
							dispatch.addData(lineBuffer.toString());
						break;
						
					case STATE_DONE_EOF:
						if (dispatch.hasHttpData())
							state = READ_STATE.STATE_DONE;
						break;
				}


			}// while !isDone (vertical scan)

			// dispatch the message to the listener for interrogation
			// otherwise just pass the data along
			if (_isActive && state == READ_STATE.STATE_DONE) {
				
				if (_serviceSocket != null && !_serviceSocket.isClosed()){
					dispatch.dispatch(listener);
					_outStream.write(dispatch.toHttpByteArray());
				} else {
					_isActive = false;
				}
			}

		} catch (Exception e) {
			System.out.println(FLOW + ".flow exception: " + e);
			_isActive = false;
		} finally {

			if (DEBUG) {
				System.out.println(FLOW + ".flow finally");
			}
			_outStream.flush();
		}
	}

	synchronized void wakeUp() {
		this.notifyAll();
	}

	public void halt() {
		try {
			// Close the client socket.
			if (_clientSocket != null) _clientSocket.close();
			_clientSocket = null;
			// Close the service socket.
			if (_serviceSocket != null) _serviceSocket.close();
			_serviceSocket = null;

		} catch (Exception e) {
			com.ibm.ws.ffdc.FFDCFilter.processException(e,
					"com.ibm.ws.webservices.engine.utils.tcpmon.halt", "990",
					this);
			e.printStackTrace();
		}
	}

	public void setListener(TCPMonEvenListener _listener) {
		this._listener = _listener;
	}

	public void setInSocket(Socket incomingSocket) {
		this._clientSocket = incomingSocket;
	}

	public void setTarget(String host, int port) {
		this._serviceHost = host;
		this._servicePort = port;

	}

	public void setDebug(boolean debug) {
		this.DEBUG = debug;
	}

	public void setSoTimeout(int so_timeout) {
		this.SO_TIMEOUT = so_timeout;
	}

	public void stopFlow() {
		if (DEBUG) {
			System.out.println("Flow.stopFlow");
		}
		this._isActive = false;
		halt();
	}
	
}
