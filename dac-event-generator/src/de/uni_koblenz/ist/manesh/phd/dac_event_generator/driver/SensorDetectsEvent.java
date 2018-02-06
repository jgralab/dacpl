package de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Entity;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

public class SensorDetectsEvent extends Event {
	public String personId;
	public String sensorId;

	public SensorDetectsEvent(int lineNumber) {
		super(lineNumber);
	}

	@Override
	public void send(EventBus bus) {
		bus.publish(Entity.SENSOR, sensorId, Verb.DETECTS, Entity.PERSON,
				personId);
	}
}
