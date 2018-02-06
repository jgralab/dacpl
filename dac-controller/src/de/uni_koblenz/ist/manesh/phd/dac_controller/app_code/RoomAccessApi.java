/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_controller.app_code;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.code.MoCoCodeObject;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.IRoomEventData;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Entity;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MethodCategory;

/**
 * 
 * @author Mahdi Derakhshanmanesh
 * @author Thomas Iguchi
 * 
 */
public class RoomAccessApi extends MoCoCodeObject implements IRoomAccess {
	final private static Logger log = Logger.getLogger("RoomAccessApi");
	final private Map<String, TimerTask> mAutoCloseTasks;
	final private Timer mTimer;

	public RoomAccessApi() {
		mAutoCloseTasks = new HashMap<>();
		mTimer = new Timer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.ist.manesh.phd.dac_controller.app_code.IRoomAccess#denyAccess
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	@Export(category = MethodCategory.ACTION)
	public void denyAccess(String personId, final String doorId) {
		getMediatorAs(IEventDispatcher.class).getEventBus().publish(
				Entity.DAC_CONTROLLER, null, Verb.DENIES_ACCESS, Entity.DOOR,
				doorId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.ist.manesh.phd.dac_controller.app_code.IRoomAccess#grantAccess
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	@Export(category = MethodCategory.ACTION)
	public void grantAccess(String personId, final String doorId) {
		final IRoomEventData rda = getMediatorAs(IRoomEventData.class);
		final TimerTask timerTask = mAutoCloseTasks.remove(doorId);
		if (timerTask != null)
			timerTask.cancel();

		if (rda.lockDoor(doorId, false)) {
			// Fake a h/w signal. Should be delegated to the driver first
			// (conceptually).
			getMediatorAs(IEventDispatcher.class).getEventBus().publish(
					Entity.DOOR, doorId, Verb.UNLOCKS, null, null);

			final TimerTask t = new TimerTask() {
				@Override
				public void run() {
					rda.lockDoor(doorId, true);
					getMediatorAs(IEventDispatcher.class).getEventBus()
							.publish(Entity.DOOR, doorId, Verb.LOCKS, null,
									null);
				}
			};

			mAutoCloseTasks.put(doorId, t);
			mTimer.schedule(t, 5000);
		} else {
			log.severe("Cannot grant access to room '" + doorId
					+ "'. Does not exist.");
		}
	}
}
