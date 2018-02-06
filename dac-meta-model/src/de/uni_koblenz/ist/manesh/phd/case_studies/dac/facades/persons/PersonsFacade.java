package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.persons;

public interface PersonsFacade {
	void addPerson(String name, String role);
	boolean removePerson(String name);
	boolean removeRole(String name);
	void addRole(String name);
	String listPersons();
	String listRoles();
	String getRoomByPerson(String name);
}
