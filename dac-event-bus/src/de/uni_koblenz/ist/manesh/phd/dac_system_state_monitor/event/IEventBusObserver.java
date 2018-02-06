/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event;

/**
 * @author Mahdi Derakhshanmanesh
 *
 */
public interface IEventBusObserver {
	void setEventBus(EventBus bus);

	void unregisterFromEventBus();
}
