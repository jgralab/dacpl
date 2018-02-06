package com.dac.sensor.base.app_code;

import com.dac.model_repository.port.function.ISensorInfo;
import com.dac.sensor.base.ports.function.IBaseDriver;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventListener;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.code.MoCoCodeObject;

public abstract class BaseDriverMoCoObject extends MoCoCodeObject implements
		EventListener, IBaseDriver {
	protected EventBus mBus;

	private ISensorInfo mSensorInfo;

	@Export
	@Override
	public ISensorInfo getSensorInfo() {
		return mSensorInfo;
	}

	@Export
	@Override
	public void setEventBus(EventBus bus) {
		mBus = bus;
		bus.registerListenerForVerb(Verb.DETECTS, this);
	}

	@Export
	@Override
	public void setSensorInfoService(ISensorInfo sensorInfo) {
		mSensorInfo = sensorInfo;
	}

	@Export
	@Override
	public void unregisterFromEventBus() {
		mBus.unregisterListener(this);
	}
}
