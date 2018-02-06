package de.uni_koblenz.ist.manesh.phd.dac_controller.app_model;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MethodCategory;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.MoCoModelObject;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.NamedElement;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.EventObject;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.EventType;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Rules;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.RulesFacade;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.persons.PersonsFacade;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.rooms.RoomsFacade;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Door;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Room;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

public class ModelAccessApi extends MoCoModelObject implements IRoomEventData {

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public String getRoomByPerson(String name) {
		DacGraph model = getModel();
		PersonsFacade persons = (PersonsFacade) model.getFirstPersons();
		return persons.getRoomByPerson(name);
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public void removePersonFromRoom(String personId, String roomId) {
		DacGraph model = getModel();
		RoomsFacade rooms = (RoomsFacade) model.getFirstRooms();
		rooms.removePersonFromRoom(personId, roomId);
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public void addPersonToRoom(String personId, String roomId) {
		DacGraph model = getModel();
		RoomsFacade rooms = (RoomsFacade) model.getFirstRooms();
		rooms.addPersonToRoom(personId, roomId);
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public boolean lockDoor(String doorId, boolean lock) {
		DacGraph model = getModel();
		RoomsFacade rooms = (RoomsFacade) model.getFirstRooms();
		return rooms.lockDoor(doorId, lock);
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public boolean updateEvent(String subjectId, Verb verb, String objectId) {
		DacGraph model = getModel();
		RulesFacade rules = (RulesFacade) model.getFirstRules();
		return rules.updateEvent(subjectId, verb, objectId);
	}

	protected DacGraph getModel() {
		try {
			return getDefaultModelConnection().getRawModel();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
