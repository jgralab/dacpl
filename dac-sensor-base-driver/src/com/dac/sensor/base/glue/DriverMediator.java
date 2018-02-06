package com.dac.sensor.base.glue;

import com.dac.sensor.base.ports.function.IBaseDriver;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.Mediator;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MediatorBuilder;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MoCoObject;

/**
 * This file was generated on 2014-05-16 19:14:20.274. It can be extended
 * manually. Regeneration will override all changes, though!
 */

public interface DriverMediator extends Mediator, IBaseDriver {
	public static MediatorBuilder getBuilder(MoCoObject provider) {
		return MediatorBuilder.forMediatorType(DriverMediator.class)
				.addMoCoObjects(provider);
	}
}
