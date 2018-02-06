package de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;

public abstract class Event {
	public int lineNumber;
	public long delay;
	private long startTS;

	public Event(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void start() {
		startTS = getTime();
	}

	private static long getTime() {
		return System.nanoTime() / 1000000;
	}

	public boolean isReady() {
		return (getTime() - startTS) >= delay;
	}

	public abstract void send(EventBus bus);
}
