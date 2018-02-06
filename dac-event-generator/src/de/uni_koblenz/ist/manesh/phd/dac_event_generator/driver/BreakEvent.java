package de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;

public class BreakEvent extends Event {
	public BreakEvent(int lineNumber) {
		super(lineNumber);
	}

	@Override
	public void send(EventBus bus) {
		throw new UnsupportedOperationException("Break events cannot be published.");
	}
}
