/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_controller.app_code;

import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.code.MoCoCodeObject;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.IRoomEventData;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.IRuleExecution;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

/**
 * @author Mahdi Derakhshanmanesh
 * @author Thomas Iguchi
 */
public class EventHandler extends MoCoCodeObject implements IEventDispatcher {
	final private static Logger log = Logger.getLogger("EventHandler");
	private EventBus mEventBus;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.IEventDispatcher
	 * #dispatch(java.lang.String,
	 * de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb,
	 * java.lang.String)
	 */
	@Export
	@Override
	public void dispatch(String subjectId, Verb verb, String objectId) {
		IRoomEventData rda = getMediatorAs(IRoomEventData.class);
		if (subjectId == null || objectId == null)
			return;

		if (Verb.ENTERS_ROOM == verb) {
			String oldRID = rda.getRoomByPerson(subjectId);
			if (oldRID != null)
				rda.removePersonFromRoom(subjectId, oldRID);

			rda.addPersonToRoom(subjectId, objectId);
		} else {
			if (rda.updateEvent(subjectId, verb, objectId)) {
				getMediatorAs(IRuleExecution.class).execute();
			}
		}
	}

	@Export
	public EventBus getEventBus() {
		return mEventBus;
	}

	@Export
	@Override
	public void init(EventBus eb) {
		mEventBus = eb;
	}

}
