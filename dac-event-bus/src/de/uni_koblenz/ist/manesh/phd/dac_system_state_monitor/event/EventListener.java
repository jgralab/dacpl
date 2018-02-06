package de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event;

public interface EventListener {
	boolean onEvent(Entity subject, String subjectId, Verb verb, Entity object,
			String objectId);
}
