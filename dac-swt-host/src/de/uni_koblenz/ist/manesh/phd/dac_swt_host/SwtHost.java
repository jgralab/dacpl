package de.uni_koblenz.ist.manesh.phd.dac_swt_host;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtHost {
	final private static Logger log = Logger.getLogger(SwtHost.class.getName());
	final private WeakHashMap<ChildShell, WeakReference<Shell>> mRegistry;

	private class UIThread extends Thread {
		boolean isRunning = true;

		@Override
		public void run() {
			log.info("UI thread started");
			display = Display.getDefault();
			shell = new Shell(display, 0);
			shell.setVisible(false);

			while (isRunning) {
				ChildShell cs = null;

				synchronized (mChildShellQueue) {
					cs = mChildShellQueue.poll();
				}

				if (cs != null) {
					log.info("Creating child shell");
					Shell cshell = cs.createShell(shell);
					mRegistry.put(cs, new WeakReference<Shell>(cshell));
				} else {
					synchronized (mChildShellQueue) {
						cs = mChildShellCloseQueue.poll();
					}

					if (cs != null) {
						log.info("Closing child shell");
						WeakReference<Shell> shellRef = mRegistry.remove(cs);
						Shell cshell = shellRef.get();

						if (cshell != null) {
							try {
								cshell.close();
							} catch (SWTException e) {
								log.severe("Exception while trying to close child shell: "
										+ e);
							}
						}
					}
				}

				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

			log.info("UI thread stopped");
		}
	}

	final private ArrayDeque<ChildShell> mChildShellQueue;
	final private ArrayDeque<ChildShell> mChildShellCloseQueue;
	private Display display;
	private Shell shell;
	private UIThread bgThread;

	public SwtHost() {
		mChildShellQueue = new ArrayDeque<>();
		mChildShellCloseQueue = new ArrayDeque<>();
		mRegistry = new WeakHashMap<>();
	}

	public void addChild(ChildShell child) {
		log.info("addChild(" + child + ")");

		synchronized (mChildShellQueue) {
			mChildShellQueue.offer(child);
		}

		if (display != null) {
			display.wake();
		} else {
			synchronized (bgThread) {
				bgThread.notify();
			}
		}
	}

	public void start() {
		log.info("Started...");
		if (bgThread == null || !bgThread.isRunning) {
			bgThread = new UIThread();
			bgThread.start();
		}
	}

	public void stop() throws InterruptedException {
		if (bgThread != null) {
			bgThread.isRunning = false;
			bgThread.join();
			bgThread = null;
		}
	}

	public void closeChild(ChildShell child) {
		log.info("closeChild(" + child + ")");

		synchronized (mChildShellCloseQueue) {
			mChildShellCloseQueue.offer(child);
		}

		if (display != null) {
			display.wake();
		} else {
			synchronized (bgThread) {
				bgThread.notify();
			}
		}
	}

}
