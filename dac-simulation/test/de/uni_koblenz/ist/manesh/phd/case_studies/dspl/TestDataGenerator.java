package de.uni_koblenz.ist.manesh.phd.case_studies.dspl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Room;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Room.RoomType;

/**
 * This internal class contains reusable data for JUnit test cases.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class TestDataGenerator {

	/**
	 * This method creates a {@link Map} with person role names and a
	 * {@link Set} of room names that may be accessed by that person. The data
	 * can be represented textually as follows:<br>
	 * <br>
	 * 
	 * MANAGER => MANAGEMENT_ROOM, SEMINAR_ROOM<br>
	 * 
	 * ENGINEER => ENGINEERING_ROOM_1, ENGINEERING_ROOM_2, ENGINEERING_ROOM_3,
	 * SEMINAR_ROOM<br>
	 * 
	 * ADMIN_STAFF => MANAGEMENT_ROOM, MAIN_OFFICE<br>
	 * 
	 * IT_STAFF => IT_ROOM, SEMINAR_ROOM<br>
	 * 
	 * GUEST => SEMINAR_ROOM
	 */
	@SuppressWarnings("serial")
	public static Map<String, Set<String>> createSampleAccessRulesWithoutVIP() {
		return new LinkedHashMap<String, Set<String>>() {
			{
				put(Person.RoleType.MANAGER.name(),
						new LinkedHashSet<String>() {
							{
								add(RoomType.MANAGEMENT_ROOM.name());
								add(RoomType.SEMINAR_ROOM.name());
							}
						});
				put(Person.RoleType.ENGINEER.name(),
						new LinkedHashSet<String>() {
							{
								add(RoomType.ENGINEERING_ROOM_1.name());
								add(RoomType.ENGINEERING_ROOM_2.name());
								add(RoomType.ENGINEERING_ROOM_3.name());
								add(RoomType.SEMINAR_ROOM.name());
							}
						});
				put(Person.RoleType.ADMIN_STAFF.name(),
						new LinkedHashSet<String>() {
							{
								add(RoomType.MANAGEMENT_ROOM.name());
								add(RoomType.MAIN_OFFICE.name());
							}
						});
				put(Person.RoleType.IT_STAFF.name(),
						new LinkedHashSet<String>() {
							{
								add(RoomType.IT_ROOM.name());
								add(RoomType.SEMINAR_ROOM.name());
							}
						});
				put(Person.RoleType.GUEST.name(), new LinkedHashSet<String>() {
					{
						add(RoomType.SEMINAR_ROOM.name());
					}
				});
			}
		};
	}

	@SuppressWarnings("serial")
	public static Map<String, Set<String>> createSampleAccessRulesWithVIP() {
		Map<String, Set<String>> rules = createSampleAccessRulesWithoutVIP();
		rules.put(Person.RoleType.VIP.name(), new LinkedHashSet<String>() {
			{
				add(Room.RoomType.ENGINEERING_ROOM_1.name());
			}
		});
		return rules;
	}

}
