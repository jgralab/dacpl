package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.rooms;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.DacGraphFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.rooms.RoomsImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Door;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Room;
import de.uni_koblenz.jgralab.Graph;

public class RoomsFacadeImpl extends RoomsImpl implements RoomsFacade {

	public RoomsFacadeImpl(int id, Graph g) {
		super(id, g);
	}
	
	@Override
	public DacGraphFacadeImpl getGraph() {
		return (DacGraphFacadeImpl) super.getGraph();
	}
	
	public void removePersonFromRoom(String personId, String roomId) {
		DacGraphFacadeImpl model = getGraph(); 

		synchronized (model) {
			Room r = (Room) model.findNamedElementByName(roomId);
			Person p = (Person) model.findNamedElementByName(personId);

			if (r != null && p != null) {
				r.remove_persons(p);
			}
		}
	}

	@Override
	public void addPersonToRoom(String personId, String roomId) {
		DacGraphFacadeImpl model = getGraph(); 

		synchronized (model) {
			Room r = (Room) model.findNamedElementByName(roomId);
			Person p = (Person) model.findNamedElementByName(personId);

			if (r != null && p != null) {
				r.add_persons(p);
			}
		}
	}

	@Override
	public boolean lockDoor(String doorId, boolean lock) {
		DacGraphFacadeImpl model = getGraph(); 

		synchronized (model) {
			Door r = (Door) model.findNamedElementByName(doorId);

			if (r != null) {
				r.set_locked(lock);
				return true;
			}
		}

		return false;
	}
}
