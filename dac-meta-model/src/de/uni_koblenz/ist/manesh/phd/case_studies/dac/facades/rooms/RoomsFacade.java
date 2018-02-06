package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.rooms;

public interface RoomsFacade {

	void removePersonFromRoom(String personId, String roomId);
	
	void addPersonToRoom(String personId, String roomId);
	
	boolean lockDoor(String doorId, boolean lock);
}
