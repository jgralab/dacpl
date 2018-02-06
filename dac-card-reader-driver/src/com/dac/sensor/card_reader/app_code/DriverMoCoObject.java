package com.dac.sensor.card_reader.app_code;

import com.dac.model_repository.port.function.ISensorInfo;
import com.dac.sensor.base.app_code.BaseDriverMoCoObject;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Entity;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

public class DriverMoCoObject extends BaseDriverMoCoObject {
	final private static String PRODUCT_ID = "Card-Reader";

	@Override
	public boolean onEvent(Entity subject, String subjectId, Verb verb,
			Entity object, String objectId) {
		if(verb == Verb.DETECTS) {
			final ISensorInfo si = getSensorInfo();
			final String productId = si.getProductId(subjectId);

			if(PRODUCT_ID.equals(productId)) {
				final String doorId = si.getDoorIdForCardReaderId(subjectId);
				mBus.publish(object, objectId, Verb.REQUESTS_ACCESS,
						Entity.DOOR, doorId);
				
				return true;
			}
		}

		return false;
	}
}
