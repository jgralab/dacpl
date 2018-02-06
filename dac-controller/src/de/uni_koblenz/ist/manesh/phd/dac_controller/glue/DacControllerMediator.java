/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_controller.glue;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.Mediator;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MediatorBuilder;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_code.EventHandler;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_code.IEventDispatcher;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_code.MessageApi;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_code.RoomAccessApi;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.DacRuleInterpreter;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.IRoomEventData;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.IRuleExecution;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.AdaptionApi;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.ModelAccessApi;
import de.uni_koblenz.ist.manesh.phd.dac_controller.ports.manage.IAdapation;

/**
 * @author Mahdi Derakhshanmanesh
 * @author Thomas Iguchi
 */
public interface DacControllerMediator extends Mediator, IEventDispatcher,
		IRuleExecution, IAdapation, IRoomEventData {

	public static MediatorBuilder getBuilder() {
		return MediatorBuilder.forMediatorType(DacControllerMediator.class)
				.addMoCoObjectTypes(DacRuleInterpreter.class,
						EventHandler.class, MessageApi.class,
						RoomAccessApi.class, ModelAccessApi.class,
						AdaptionApi.class);
	}

}
