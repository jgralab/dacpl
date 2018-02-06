/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_controller.app_code;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

/**
 * @author Mahdi Derakhshanmanesh
 * 
 */
public interface IEventDispatcher {
	void dispatch(String subjectId, Verb verb, String objectId);

	EventBus getEventBus();

	void init(EventBus eb);
}
