package com.dac.sensor.camera;

import com.dac.sensor.base.BaseActivator;
import com.dac.sensor.camera.app_code.DriverMoCoObject;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.code.MoCoCodeObject;


public class Activator extends BaseActivator {
	@Override
	protected MoCoCodeObject createDriverMoCoObject() {
		return new DriverMoCoObject();
	}
}
