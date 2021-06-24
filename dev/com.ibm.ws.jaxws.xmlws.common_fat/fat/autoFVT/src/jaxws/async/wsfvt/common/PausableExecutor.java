//
// @(#) 1.2 WautoFVT/src/jaxws/async/wsfvt/common/PausableExecutor.java, WAS.websvcs.fvt, WSFP.WFVT 9/18/06 16:54:50 [11/16/06 08:39:08]
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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A custom executor that can be paused for testing of exceptions that occur
 * before an item is executed. This executor provides absolue control over its
 * thread size via size parameter in the constructor
 */
public class PausableExecutor extends ThreadPoolExecutor {
	private boolean isPaused;

	private ReentrantLock pauseLock = new ReentrantLock();

	private Condition unpaused = pauseLock.newCondition();

	public PausableExecutor(int size) {
		super(size, size, 1, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(size));
	}
	
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		pauseLock.lock();
		try {
			while (isPaused)
				unpaused.await();
		} catch (InterruptedException ie) {
			t.interrupt();
		} finally {
			pauseLock.unlock();
		}
	}

	public void pause() {
		pauseLock.lock();
		try {
			isPaused = true;
		} finally {
			pauseLock.unlock();
		}
	}

	public void resume() {
		pauseLock.lock();
		try {
			isPaused = false;
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}
}
