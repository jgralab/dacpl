package de.uni_koblenz.ist.manesh.phd.case_studies.dspl;

import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.access_control.AccessController;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.access_control.AccessRulesCodeGenerator;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.IRenderable;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Person.RoleType;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Room;
import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.Room.RoomType;

/**
 * This class contains the main method to run the simulation. The main logic of
 * the simulation is that a set of persons exist with their assigned roles.
 * There are different types of rooms and there are access control rules
 * according to which a person may enter the room or not. The simulation runs
 * infinitely and chooses one person each step and tries to move him/her to a
 * new room. The set of rules is checked, then. The person is only moved to
 * another room if the rules allow this. Rules are written in a small custom DSL
 * and the rules are placed in "access-rules.txt".
 * 
 * The whole setup is derived from our published DSPL 12' concept paper.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class Simulation implements IRenderable {

	private final Set<Room> rooms;

	private final Set<Person> persons;

	private final AccessController accessController;

	// TODO How to represent the feature configuration?

	/**
	 * This method runs the simulation.
	 * 
	 * @param args
	 *            This param is ignored.
	 */
	public static void main(String[] args) {
		final Simulation sim = new Simulation();
		try {
			sim.run();
		} catch (InterruptedException e) {
			System.err.println("Simulation terminated in an unexpected way");
			e.printStackTrace();
		}
	}

	/**
	 * This constructor initializes the rooms and persons sets as well as
	 * creates the persons and rooms programmatically. Moreover it loads the
	 * access control rules from a text file.
	 */
	public Simulation() {
		rooms = new LinkedHashSet<>();
		createRooms();

		persons = new LinkedHashSet<>();
		createPersons();

		try {
			this.accessController = new AccessController("access-rules.txt");
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(
					"Passed illegal access rules file to simulation", e);
		}
	}

	/**
	 * This method triggers and executes the actual simulation loop.
	 * 
	 * @throws InterruptedException
	 */
	public void run() throws InterruptedException {
		for (int i = 0; true; i++) {
			System.out.println("=============================================");
			System.out.println("SIM Step " + i);
			System.out.println("=============================================");

			// Update simulation data.
			introduceOrRemoveVIPRandomly();
			tryToMovePerson(Person.getRandomPerson(persons),
					Room.getRandomLocation(rooms));

			// Visualize state.
			render();

			// Wait.
			Thread.sleep(3000);
		}
	}

	/**
	 * This method displays the current state of all persons and their location
	 * (i.e., the room they are in). Probably, this method shall be overridden
	 * in another simulation class to really render the state visually. It does
	 * not exactly equal {@link Simulation#toString()}, because this would
	 * result in an infinite loop due to the bidirectional dependencies between
	 * persons and rooms.
	 */
	@Override
	public void render() {
		// Visualize person data.
		System.out.println("Simulation [");
		System.out.println("persons=");
		for (Person p : persons) {
			p.render();
		}
		System.out.println("]");
		System.out.println();

		// Visualize rule data.
		System.out.println("Access Control Rules:");
		System.out.println(AccessRulesCodeGenerator
				.constructRulesString(accessController
						.getCurrentAccessControlRules()));
		System.out.println();
	}

	private void createRooms() {
		rooms.add(new Room(RoomType.MANAGEMENT_ROOM));
		rooms.add(new Room(RoomType.MAIN_OFFICE));
		rooms.add(new Room(RoomType.IT_ROOM));
		rooms.add(new Room(RoomType.ENGINEERING_ROOM_1));
		rooms.add(new Room(RoomType.ENGINEERING_ROOM_2));
		rooms.add(new Room(RoomType.ENGINEERING_ROOM_3));
		rooms.add(new Room(RoomType.SEMINAR_ROOM));
	}

	private void createPersons() {
		persons.add(new Person(RoleType.MANAGER, "Manager", Room
				.findRoomByName(RoomType.MANAGEMENT_ROOM, rooms)));
		persons.add(new Person(RoleType.ENGINEER, "Engineer1", Room
				.findRoomByName(RoomType.ENGINEERING_ROOM_1, rooms)));
		persons.add(new Person(RoleType.ENGINEER, "Engineer2", Room
				.findRoomByName(RoomType.ENGINEERING_ROOM_2, rooms)));
		persons.add(new Person(RoleType.ENGINEER, "Engineer3", Room
				.findRoomByName(RoomType.ENGINEERING_ROOM_3, rooms)));
		persons.add(new Person(RoleType.ADMIN_STAFF, "Admin Staff", Room
				.findRoomByName(RoomType.MAIN_OFFICE, rooms)));
		persons.add(new Person(RoleType.IT_STAFF, "IT Staff", Room
				.findRoomByName(RoomType.IT_ROOM, rooms)));
		persons.add(new Person(RoleType.GUEST, "Guest", Room.findRoomByName(
				RoomType.SEMINAR_ROOM, rooms)));
	}

	private boolean tryToMovePerson(final Person p, final Room r) {
		if (p == null || r == null) {
			throw new IllegalArgumentException("");
		} else {
			// Rules must allow change. Do not change to the same room.
			if (accessController.isAllowedToAccess(p, r)
					&& p.getLocation() != r) {
				System.out.println("Moving " + p.getName() + " from "
						+ p.getLocation().getName() + " to " + r.getName());
				System.out.println();

				p.setLocation(r);
				return true;
			} else {

				System.out.println("Failed to move " + p.getName() + " from "
						+ p.getLocation().getName() + " to " + r.getName());
				System.out.println();
				return false;
			}
		}
	}

	private void introduceOrRemoveVIPRandomly() {
		// Decide if something shall change or not.
		if (new Random().nextBoolean()) {
			// Pick the first VIP from the set.
			Set<Person> vipSet = Person.findPersonsByRole(Person.RoleType.VIP,
					persons);
			if (!vipSet.isEmpty()) {
				// FIXME Check order of method calls here and in else case.
				Person vip = vipSet.iterator().next();
				persons.remove(vip);
				accessController
						.sendEvent(
								AccessController.ChangeEventType.VIP_DEPARTURE_REGISTERED,
								vip.getLocation());
			} else {
				Room mainOffice = Room.findRoomByName(RoomType.MAIN_OFFICE,
						rooms);
				if (mainOffice == null) {
					throw new RuntimeException(
							"Could not find a valid starting room for visiting VIP");
				} else {
					persons.add(new Person(RoleType.VIP, "Someone Special",
							mainOffice));
					accessController
							.sendEvent(
									AccessController.ChangeEventType.VIP_ARRIVAL_REGISTERED,
									mainOffice);
				}
			}
		} else {
			// Do not change anything.
		}
	}

}
