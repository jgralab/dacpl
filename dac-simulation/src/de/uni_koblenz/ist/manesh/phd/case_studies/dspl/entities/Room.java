package de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.Util;

/**
 * This class realizes a room as a {@link NamedElement} and an
 * {@link IRenderable}.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class Room extends NamedElement implements IRenderable {

	private final Set<Person> persons;

	private static final Room NIRVANA = new Room(RoomType.NIRVANA);

	/**
	 * This enum represents a set of predefined rooms. These types are used as
	 * names in the simulation.
	 * 
	 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
	 * 
	 */
	public static enum RoomType {
		MANAGEMENT_ROOM, MAIN_OFFICE, IT_ROOM, ENGINEERING_ROOM_1, ENGINEERING_ROOM_2, ENGINEERING_ROOM_3, SEMINAR_ROOM, NIRVANA;

		public static final Set<String> getAllRoomTypeNamesAsSet() {
			final Set<String> roomNames = new LinkedHashSet<>();
			for (RoomType rt : values()) {
				roomNames.add(rt.name());
			}
			return roomNames;
		}
	}

	/**
	 * This constructor creates a named room, where the name is set to the
	 * string representatin of the {@link RoomType}.
	 * 
	 * @param type
	 *            A {@link RoomType} to be chosen from the set of possible ones.
	 */
	public Room(final RoomType type) {
		this(type.name());
	}

	/**
	 * This constructor creates a named room.
	 * 
	 * @param name
	 *            The name of the room to set.
	 */
	public Room(final String name) {
		super(name);
		persons = new HashSet<Person>();
	}

	/**
	 * This method displays the name of the room in text form.
	 */
	@Override
	public void render() {
		System.out.println("Room [name=" + name + "]");
	}

	/**
	 * This method adds a {@link Person} to this room.
	 * 
	 * @param p
	 *            A {@link Person}, may not be <code>null</code>.
	 * @return <code>true</code> if p could be added, otherwise
	 *         <code>false</code>.
	 */
	public boolean addPerson(final Person p) {
		if (p == null) {
			throw new IllegalArgumentException("Attempted to add a null person");
		} else {
			if (!persons.contains(p)) {
				if (p.getLocation() != this) {
					p.setLocation(this);
				}
				return persons.add(p);
			} else {
				return false;
			}
		}
	}

	/**
	 * This method removes a {@link Person} from this room.
	 * 
	 * @param p
	 *            A {@link Person} to be removed from the room.
	 * @return <code>true</code> if p could be removed, otherwise
	 *         <code>false</code>.
	 */
	public boolean removePerson(final Person p) {
		if (p == null) {
			throw new IllegalArgumentException(
					"Attempted to remove a null person");
		} else {
			if (persons.contains(p)) {
				persons.remove(p);
				p.setLocation(NIRVANA);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * This method checks if a person is in this room or not.
	 * 
	 * @param p
	 *            The {@link Person} to be checked for.
	 * @return <code>true</code> if p is in the room, otherwise
	 *         <code>false</code>.
	 */
	public boolean contains(final Person p) {
		return persons.contains(p);
	}

	/**
	 * This method checks if the room is empty or not.
	 * 
	 * @return <code>true</code> if there are no persons in this room, otherwise
	 *         <code>false</code>.
	 */
	public boolean isEmpty() {
		return persons.isEmpty();
	}

	/**
	 * This method finds a {@link Room} by name using the type of the
	 * {@link Room} as a name.
	 * 
	 * @param type
	 *            The {@link RoomType} to be looked for.
	 * @param rooms
	 *            The {@link Set} of rooms to search in.
	 * @return A {@link Room} if in the set, otherwise <code>null</code>.
	 */
	public static final Room findRoomByName(final RoomType type,
			final Set<Room> rooms) {
		// FIXME Is this method really needed?
		return findRoomByName(type.name(), rooms);
	}

	/**
	 * This method finds a {@link Room} by name.
	 * 
	 * @param name
	 *            The name of the {@link Room} to look for.
	 * @param rooms
	 *            The {@link Set} of rooms to search in.
	 * @return A {@link Room} if in the set, otherwise <code>null</code>.
	 */
	public static final Room findRoomByName(final String name,
			final Set<Room> rooms) {
		return NamedElement.findElementByNameInCollection(name, rooms);
	}

	/**
	 * This method returns a random {@link Room} from the passed {@link Set} of
	 * rooms.
	 * 
	 * @param rooms
	 *            A {@link Set} of rooms, should not be <code>null</code>.
	 * @return A random {@link Room} from the passed {@link Set} of entries.
	 */
	public static final Room getRandomLocation(final Set<Room> rooms) {
		return (Room) Util.getRandomEntryFromSet(rooms);
	}

}
