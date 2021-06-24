//
// @(#) 1.2 WautoFVT/src/jaxws/async/wsfvt/common/KillerThread.java, WAS.websvcs.fvt, WSFP.WFVT 9/18/06 16:54:48 [11/16/06 08:39:07]
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
// 09/18/06 sedov    LIDB3296.38        New File
//

package jaxws.async.wsfvt.common;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.ws.Service;

public class KillerThread extends Thread{

	private Service svc = null;
	
	private boolean isKilled = false;
	private boolean interrupt = false;
	
	private int waitUntilKillingSec = 30;
	
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
				System.out.println("KillerThread: interrupted");
			}
		}
		
		// force executor to stop
		if (!interrupt){
			isKilled = true;
			es.shutdownNow();
		}
	}

	public boolean isKilled() {
		return isKilled;
	}
	
	public void abort(){
		this.interrupt = true;
		this.interrupt();
	}
}
