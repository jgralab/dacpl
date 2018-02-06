package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.persons;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MethodCategory;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MoCoObject.Export;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.DacGraphFacade;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.persons.PersonsImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Role;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Room;
import de.uni_koblenz.jgralab.Graph;

public class PersonsFacadeImpl extends PersonsImpl implements PersonsFacade {

	public PersonsFacadeImpl(int id, Graph g) {
		super(id, g);
	}
	
	@Override
	public void addPerson(String name, String role) {
		DacGraph model = getGraph();
		
		synchronized (model) {
			// Multiple persons may have same name.
			Person p = model.createPerson();
			p.set_uniqueName(name);
			p.set_visibleName(name);

			// Reuse roles from graph if existing.
			Role pRole = findRoleByName(role);
			if (pRole == null) {
				pRole = model.createRole();
				pRole.set_uniqueName(role);
				pRole.set_visibleName(role);
			}

			p.add_roles(pRole);

			// Find hallway as starting point.
			for (Room room : model.getRoomVertices()) {
				if (room.get_uniqueName().equalsIgnoreCase("Hallway")) {
					p.add_room(room);
					break;
				}
			}
		}
	}

	@Override
	public void addRole(String name) {
		DacGraph model = getGraph();

		synchronized (model) {
			if (findRoleByName(name) == null) {
				Role r = model.createRole();
				r.set_uniqueName(name);
				r.set_visibleName(name);
			}
		}
	}

	@Override
	public DacGraph getGraph() {
		return (DacGraph) super.getGraph();
	}

	@Override
	public boolean removePerson(String name) {
		DacGraph model = getGraph();
		
		synchronized (model) {
			Person p = findPersonByName(name);
			if (p != null) {
				getGraph().deleteVertex(p);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean removeRole(String name) {
		final DacGraph model = getGraph();

		synchronized (model) {
			Role r = findRoleByName(name);
			if (r != null) {
				getGraph().deleteVertex(r);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String listPersons() {
		final DacGraph model = getGraph();
		StringBuilder sb = new StringBuilder();

		synchronized (model) {
			for (Person p : model.getPersonVertices()) {
				sb.append(p.get_uniqueName());
				sb.append(":");
				for (Role r : p.get_roles()) {
					sb.append(' ');
					sb.append(r.get_uniqueName());
				}
				sb.append('\n');
			}
		}

		return sb.toString();
	}

	@Override
	public String listRoles() {
		final DacGraph model = getGraph();
		StringBuilder sb = new StringBuilder();

		synchronized (model) {
			for (Role r : model.getRoleVertices()) {
				sb.append(r.get_uniqueName());
				sb.append(":");
				for (Person p : r.get_person()) {
					sb.append(' ');
					sb.append(p.get_uniqueName());
				}
				sb.append('\n');
			}
		}

		return sb.toString();
	}

	private Person findPersonByName(String name) {
		final DacGraph model = getGraph();
	
		synchronized (model) {
			for (Person p : model.getPersonVertices()) {
				if (p.get_uniqueName().equalsIgnoreCase(name)) {
					return p;
				}
			}
		}
	
		return null;
	}

	private Role findRoleByName(String name) {
		final DacGraph model = getGraph();
	
		synchronized (model) {
			for (Role r : model.getRoleVertices()) {
				if (r.get_uniqueName().equalsIgnoreCase(name)) {
					return r;
				}
			}
		}
	
		return null;
	}

	@Override
	public String getRoomByPerson(String name) {
		final DacGraphFacade model = (DacGraphFacade) getGraph();
		
		synchronized (model) {
			Person p = (Person) model.findNamedElementByName(name);
			if (p == null)
				return null;
			Room r = p.get_room();
			if (r != null) {
				return r.get_uniqueName();
			}

			return null;
		}
	}

}
