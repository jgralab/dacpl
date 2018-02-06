/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_simulation_2d.glue;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.Mediator;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MediatorBuilder;
import de.uni_koblenz.ist.manesh.phd.dac_simulation_2d.app_model.DacModelRenderer;
import de.uni_koblenz.ist.manesh.phd.dac_simulation_2d.app_model.ISimulation;

/**
 * @author Mahdi Derakhshanmanesh
 * 
 */
public interface DacSimulation2DMediator extends Mediator, ISimulation {
	public static MediatorBuilder getBuilder() {
		return MediatorBuilder.forMediatorType(DacSimulation2DMediator.class)
				.addMoCoObjectTypes(DacModelRenderer.class);
	}
}
