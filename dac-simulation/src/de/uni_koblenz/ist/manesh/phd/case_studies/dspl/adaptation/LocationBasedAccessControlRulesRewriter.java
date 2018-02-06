package de.uni_koblenz.ist.manesh.phd.case_studies.dspl.adaptation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.TestDataGenerator;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Room;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Room.RoomType;

/**
 * This class implements a simple adaptation manager that changes the access
 * control rules in response to a change event of the rules file. Iff the role
 * "VIP" is used somewhere in the file and iff the location tracking feature
 * (TODO) is enabled, then all existing rules are rewritten such that only the
 * MANAGER and VIP roles are allowed to access the same room(s) as the VIP.
 * 
 * This realization changes the script file, i.e., it reads it and writes it. We
 * do not work with a clearly defined AST of the language, but work with an
 * internal {@link Map} representation (for simplicity). Technically, the script
 * is a model, too.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class LocationBasedAccessControlRulesRewriter {

	/**
	 * This method changes the content of the passed rules list if it contains a
	 * VIP role. In that case, all other roles are adjusted in such a way that
	 * only managers may enter the same rooms as the VIP. If managers were not
	 * able to enter the room before, then they are granted access to it.
	 * 
	 * @param currentRules
	 *            A {@link Map} (roleName -> {roomName}).
	 * @param currVIPLocation
	 *            The {@link Room} the VIP is in at the moment.
	 * @return <code>true</code> iff the method changed the passed rules,
	 *         otherwise <code>false</code>.
	 */
	public static void adaptRulesForVIP(
			final Map<String, Set<String>> currentRules,
			final Room currVIPLocation) {
		// FIXME Persons that were in the Room before need to be dropped out,
		// first (or we assume this is a fine "in-between" state).
		if (currentRules == null || currentRules.isEmpty()) {
			throw new IllegalArgumentException(
					"Attempted to adapt an invalid set of access control rules");
		} else if (currVIPLocation == null) {
			throw new IllegalArgumentException(
					"Attempted to adapt a rule set with an invalid VIP position");
		} else {
			// Check all currently active rules. If role is a manager allow
			// access to VIP's rooms. Otherwise, remove current VIP room from
			// role. Track if something changed.
			final Map<String, Set<String>> newRules = new LinkedHashMap<>(
					currentRules.size());
			// The VIP may enter all rooms.
			newRules.put(Person.RoleType.VIP.name(),
					RoomType.getAllRoomTypeNamesAsSet());

			// Build the new rules where managers and vips can the same room,
			// but others may not access.
			for (String role : currentRules.keySet()) {
				Set<String> rooms = new LinkedHashSet<>();
				for (String roomName : currentRules.get(role)) {
					if (!currVIPLocation.getName().equals(roomName)) {
						rooms.add(roomName);
					} else if (currVIPLocation.getName().equals(roomName)
							&& (role.equals(Person.RoleType.MANAGER.name()) || role
									.equals(Person.RoleType.VIP.name()))) {
						rooms.add(roomName);
					} else {
						// Do not allow other roles access to this room.
					}
				}
				// All managers may enter the VIP's current rooms.
				if (role.equals(Person.RoleType.MANAGER.name())) {
					rooms.add(currVIPLocation.getName());
					// FIXME When VIP leaves, room may need to be removed from
					// manager's list if it was not in original rule set.
				}
				newRules.put(role, rooms);
			}

			// Update content of passed list.
			// TODO Optimize: only copy if something changed.
			currentRules.clear();
			currentRules.putAll(newRules);
		}
	}

	/**
	 * This method resets the content of the passed access rules to a default
	 * configuration, i.e., without a VIP.
	 * 
	 * @param rules
	 *            A {@link Map} that represents access rules.
	 * @throws IllegalArgumentException
	 *             if rules is <code>null</code>.
	 * 
	 */
	public static void resetRulesToDefault(Map<String, Set<String>> rules) {
		if (rules == null) {
			throw new IllegalArgumentException(
					"Attempted to reset adaptation for a null rule set");
		} else {
			rules.clear();
			rules.putAll(TestDataGenerator.createSampleAccessRulesWithoutVIP());
			// FIXME The TestDataGenerator should not be used here...
		}
	}

}
