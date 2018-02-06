package de.uni_koblenz.ist.manesh.phd.case_studies.dspl.access_control;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.adaptation.LocationBasedAccessControlRulesRewriter;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Room;

/**
 * This class realizes a virtual access controller that checks whether a person
 * in a certain role is allowed to access a certain room or not. Rules are
 * encoded in a simple textual DSL ("role => allowedRoom1, allowedRoom2, ...,
 * allowedRoomN").
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class AccessController {

	/**
	 * This enum type defines a set of events the {@link AccessController} can
	 * handle properly.
	 */
	public enum ChangeEventType {
		VIP_ARRIVAL_REGISTERED, VIP_DEPARTURE_REGISTERED
	}

	private final File accessRulesFile;

	private final AccessRulesParser rulesParser;

	private final Map<String, Set<String>> role2AllowedRooms;

	private final AccessRulesCodeGenerator rulesCodeGen;

	/**
	 * This constructor creates the internally needed parser and the internal
	 * data structure for storing accessible rooms per role.
	 * 
	 * @param accessRules
	 *            The location of a text file with access rules.
	 * @throws FileNotFoundException
	 *             If the passed accessRules is invalid and no {@link Scanner}
	 *             can be created. FIXME Shall this be handled internally at
	 *             this point?
	 * @throws IllegalArgumentException
	 *             If the path of accessRules is invalid (e.g.,
	 *             <code>null</code> or the empty string).
	 */
	public AccessController(String accessRules) throws FileNotFoundException {
		if (accessRules == null || accessRules.isEmpty()
				|| !new File(accessRules).exists()) {
			throw new IllegalArgumentException(
					"Attempted to initialize an AccessController with an invalid handle to access rules");
		} else {
			accessRulesFile = new File(accessRules);
			rulesParser = new AccessRulesParser(accessRulesFile);
			role2AllowedRooms = new LinkedHashMap<String, Set<String>>();
			updateRulesInMemory();
			rulesCodeGen = new AccessRulesCodeGenerator(accessRulesFile);
		}
	}

	/**
	 * This method checks if a {@link Person} is allowed to access a certain
	 * {@link Room} according to the internal representation of access rules.
	 * 
	 * @param p
	 *            The {@link Person} to check by role.
	 * @param r
	 *            The {@link Room} to be accessed by the {@link Person}.
	 * @return <code>true</code> if p may enter, otherwise <code>false</code>.
	 */
	public boolean isAllowedToAccess(final Person p, final Room r) {
		// Check timestamp and parse file again only if the file changed.
		if (rulesParser.rulesFileChanged()) {
			updateRulesInMemory();
		}

		return checkAccessRulesForRole(p.getRole(), r);
	}

	/**
	 * This method handles incoming change events and reacts accordingly by
	 * adapting the parsed access control rules.
	 * 
	 * TODO The rules file should be only updated on request to avoid file
	 * writing overhead. It shall be also possible to commit a manually edited
	 * file and to parse it.
	 * 
	 * @param event
	 *            The {@link ChangeEventType} to handle. Currently,
	 *            {@link ChangeEventType#VIP_ARRIVAL_REGISTERED} and
	 *            {@link ChangeEventType#VIP_DEPARTURE_REGISTERED} are
	 *            supported.
	 * @param currVIPLocation
	 *            The {@link Room} the VIP is in at the moment.
	 * @throws IllegalArgumentException
	 *             if event is <code>null</code> or an unknown type.
	 */
	public void sendEvent(final ChangeEventType event, Room currVIPLocation) {
		// TODO The controller should be able to find the information required
		// (less coupling). Passing the room here is for simplicity.
		System.out.println("AccessController received: " + event
				+ " and VIP is in " + currVIPLocation.getName());

		switch (event) {
		case VIP_ARRIVAL_REGISTERED:
			LocationBasedAccessControlRulesRewriter.adaptRulesForVIP(
					role2AllowedRooms, currVIPLocation);
			// rulesCodeGen.run(role2AllowedRooms);
			break;
		case VIP_DEPARTURE_REGISTERED:
			LocationBasedAccessControlRulesRewriter
					.resetRulesToDefault(role2AllowedRooms);
			// rulesCodeGen.run(role2AllowedRooms);
			break;
		default:
			throw new IllegalArgumentException(
					"An unknown event (possibly null) was passed to the access controller");
		}
	}

	public boolean checkAccessRulesForRole(final Person.RoleType rt,
			final Room r) {
		Set<String> allowedRooms = role2AllowedRooms.get(rt.name());
		if (allowedRooms != null && !allowedRooms.isEmpty()) {
			for (String roomName : allowedRooms) {
				// TODO: override equals!
				// FIXME assumes room names are unique!
				if (roomName.equals(r.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public Map<String, Set<String>> getCurrentAccessControlRules() {
		return Collections.unmodifiableMap(role2AllowedRooms);
	}

	private void updateRulesInMemory() {
		role2AllowedRooms.clear();
		rulesParser.reset();
		while (rulesParser.hasNextRulePotentially()) {
			Map<String, Set<String>> rule = rulesParser.parseNextRule();
			// Skip empty lines.
			if (rule != null && !rule.isEmpty()) {
				// Check if role type exists.
				String roleTypeName = rule.keySet().iterator().next();
				Person.RoleType role = null;
				for (Person.RoleType rt : Person.RoleType.values()) {
					if (rt.name().equalsIgnoreCase(roleTypeName)) {
						role = rt;
					}
				}

				/*
				 * TODO Room names are not checked here. Technically rooms may
				 * be added that do not exist.
				 */
				Set<String> roomNames = rule.get(roleTypeName);
				if (role == null || roomNames.isEmpty()) {
					throw new RuntimeException(
							"Could not identify role or rooms");
				} else {
					role2AllowedRooms.put(role.name(), roomNames);
				}
			}
		}
	}

}
