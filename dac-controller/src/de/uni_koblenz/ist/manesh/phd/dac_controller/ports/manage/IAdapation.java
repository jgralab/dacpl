/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_controller.ports.manage;

/**
 * 
 * TODO Discuss: These are not directly used by an external admin console as it
 * is simpler to use the invoke method provided by the mediator object.
 * 
 * @author Mahdi Derakhshanmanesh
 * 
 */
public interface IAdapation {
	void adaptRulesForVIP();

	// FIXME Move these methods to PFunction or even to dac-model-repository?
	void addPerson(String name, String role);

	void addRole(String name);

	void plotModelState();

	void popModelState();

	void pushModelState();

	boolean removePerson(String name);

	boolean removeRole(String name);

	String listPersons();

	String listRoles();
}
