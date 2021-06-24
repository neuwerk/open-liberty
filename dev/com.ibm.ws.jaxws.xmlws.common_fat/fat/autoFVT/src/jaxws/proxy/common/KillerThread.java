package jaxws.proxy.common;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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
				System.out.println("KillerThread: going to sleep");
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
