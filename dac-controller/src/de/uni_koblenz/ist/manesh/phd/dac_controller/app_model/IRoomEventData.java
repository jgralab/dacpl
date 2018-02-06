package de.uni_koblenz.ist.manesh.phd.dac_controller.app_model;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

public interface IRoomEventData {

	String getRoomByPerson(String personId);

	void removePersonFromRoom(String personId, String roomId);

	void addPersonToRoom(String personId, String roomId);

	boolean updateEvent(String subjectId, Verb verb, String objectId);

	boolean lockDoor(String doorId, boolean lock);
	
}
