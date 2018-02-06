package de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;

public class EvaluatorThread extends Thread {
	final private static Logger log = Logger.getLogger("EvaluatorThread");

	private boolean mIsRunning = true;
	private boolean mWakeUpSignal;
	private boolean mIsAsleep;
	private EventScript mScript;
	private EventBus mEventBus;
	private EvaluationListener mListener;

	@Override
	public void run() {
		while (mIsRunning) {
			synchronized (this) {
				if (!mWakeUpSignal) {
					try {
						mIsAsleep = true;
						wait();
					} catch (InterruptedException e) {
						mIsRunning = false;
						break;
					}
				}

				mIsAsleep = false;
				mWakeUpSignal = false;
			}

			List<Event> nextStep = mScript.getNextStep();

			for (Event e : nextStep) {
				e.start();
			}

			boolean allReady = false;

			runner: do {
				allReady = true;
				Iterator<Event> it = nextStep.iterator();

				while (it.hasNext()) {
					Event e = it.next();

					if (e.isReady()) {
						if(mListener != null) {
							mListener.onEventReady(e);
						}

						e.send(mEventBus);
						it.remove();
					} else {
						allReady = false;
					}
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					mIsRunning = false;
					break runner;
				}
			} while (!allReady);

			log.info("Step completed.");
		}
	}

	public void setEventBus(EventBus bus) {
		mEventBus = bus;
	}

	public void setScript(EventScript script) {
		mScript = script;
		mScript.rewind();
	}

	public boolean step() {
		synchronized (this) {
			if (mIsAsleep) {
				wakeUp();
				return true;
			}
		}

		return false;
	}

	public void setEvaluationListener(EvaluationListener listener) {
		mListener = listener;
	}

	public void shutdown() {
		if (mIsRunning) {
			mIsRunning = false;

			synchronized (this) {
				mWakeUpSignal = true;
				notifyAll();
			}
		}
	}

	private void wakeUp() {
		if (mIsRunning && mIsAsleep && !mWakeUpSignal) {
			synchronized (this) {
				mWakeUpSignal = true;
				notifyAll();
			}
		}
	}
}
