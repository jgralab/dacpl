/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_simulation_2d.app_model;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.IEventBusObserver;

/**
 * @author Mahdi Derakhshanmanesh
 * 
 */
public interface ISimulation extends IEventBusObserver {
	void run();

	void setModelAccess(IModelAccess ma);
}
