/**
 * 
 */
package com.dac.sensor.base.ports.function;

import com.dac.model_repository.port.function.ISensorInfo;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.IEventBusObserver;

/**
 * @author Mahdi Derakhshanmanesh
 *
 */
public interface IBaseDriver extends IEventBusObserver {
	ISensorInfo getSensorInfo();
	void setSensorInfoService(ISensorInfo sensorInfo);
}
