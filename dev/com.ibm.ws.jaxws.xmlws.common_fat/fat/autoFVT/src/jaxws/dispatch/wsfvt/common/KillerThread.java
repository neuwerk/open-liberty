//
// @(#) 1.1 WautoFVT/src/jaxws/dispatch/wsfvt/common/KillerThread.java, WAS.websvcs.fvt, WSFPB.WFVT 8/24/06 10:33:26 [9/1/06 10:58:12]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 05/31/06 sedov    LIDB3296.42        New File
// 08/23/06 sedov    LIDB3296-42.02     Beta Drop
//

package jaxws.dispatch.wsfvt.common;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import javax.xml.ws.Service;

/**
 * KillerThread is used to stop the Executor if it doesn't stop
 * after a specified timeout. useful for invokeAsync hanging 
 */
public class KillerThread extends Thread{

	private Service svc = null;
	
	private boolean isKilled = false;
	private boolean interrupt = false;
	
	private int waitUntilKillingSec = 120;
	
	public KillerThread(Service service, int client_max_sleep_sec) {
		this.waitUntilKillingSec = client_max_sleep_sec;
		this.svc = service;
	}

	public void run(){
		Executor e = svc.getExecutor();
		
		System.out.println("KillerThread: " + e.getClass().getName());
		
		ExecutorService es = (ExecutorService) e;

		int i = waitUntilKillingSec;
		while (i > 0 && !interrupt){
					
			try {
				//System.out.println("KillerThread: going to sleep");
				Thread.sleep(1000);
				i --;
			} catch (InterruptedException e1) {
				System.out.println("KillerThread: interrupted " + svc.getServiceName());
			}
		}
		
		// force executor to stop
		if (!interrupt){
			System.out.println("KillerThread: Stopping executor");
			isKilled = true;
			es.shutdownNow();
		}
	}

	public boolean isKilled() {
		return isKilled;
	}
	
	public void abort(){
		this.interrupt = true;
		
		// we reached the end of execution...allow 
		// 
		this.interrupt();
	}
}
