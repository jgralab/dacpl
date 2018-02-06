package de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.Util;

/**
 * This class realizes a person as a {@link NamedElement} and an
 * {@link IRenderable}. A person can have a certain role, an id and it can have
 * a location (i.e., a {@link Room}).
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class Person extends NamedElement implements IRenderable {

	/**
	 * This enumeration represents the foreseen roles of persons.
	 * 
	 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
	 * 
	 */
	public static enum RoleType {
		ADMIN_STAFF, ENGINEER, GUEST, IT_STAFF, MANAGER, VIP
	}

	private RoleType role;

	private int id;

	private Room location;

	/**
	 * This constructor creates a new person object.
	 * 
	 * @param role
	 *            The role of the person.
	 * @param name
	 *            The name of the person. May not be null.
	 * @param published
	 *            The {@link Room} in which the person is.
	 */
	public Person(final RoleType role, final String name, final Room location) {
		super(name);
		this.role = role;
		this.id = new Random().nextInt(); // TODO Check distribution.
		setLocation(location);
	}

	@Override
	public void render() {
		System.out.println("Person [id=" + id + ", role=" + role + ", name="
				+ name + ", location=" + location.getName() + "]");
	}

	/**
	 * This method gets the {@link RoleType} of the person.
	 * 
	 * @return The role of the person.
	 */
	public RoleType getRole() {
		return role;
	}

	/**
	 * This method gets the id of the person.
	 * 
	 * @return The id of the person.
	 */
	public int getId() {
		return id;
	}

	/**
	 * This method gets the {@link Room} in which the person is.
	 * 
	 * @return The {@link Room} the person is in.
	 */
	public Room getLocation() {
		return location;
	}

	/**
	 * This method sets the (new) location of the person.
	 * 
	 * @param newLocation
	 *            A valid {@link Room} reference.
	 * @throws IllegalArgumentException
	 *             If the newLocation is <code>null</code>.
	 */
	public void setLocation(final Room newLocation) {
		if (newLocation == null) {
			throw new IllegalArgumentException(
					"Attempted to set a null location");
		} else {
			if (newLocation != this.location) {
				// Init requires this case check.
				if (this.location != null) {
					this.location.removePerson(this);
				}

				// Needs to be set here, otherwise we end up in a
				// stack-overflow after next lines!
				this.location = newLocation;

				if (!newLocation.contains(this)) {
					newLocation.addPerson(this);
				}
			}
		}
	}

	/**
	 * This method returns a random {@link Person} from a set of persons.
	 * 
	 * @param persons
	 *            A {@link Set} of persons.
	 * @return A random person from the input {@link Set}.
	 */
	public static final Person getRandomPerson(final Set<Person> persons) {
		return (Person) Util.getRandomEntryFromSet(persons);
	}

	/**
	 * This method finds a {@link Set} of {@link Person} by their
	 * {@link RoleType}.
	 * 
	 * @param type
	 *            The {@link RoleType} to be looked for.
	 * @param persons
	 *            The {@link Set} of persons to search in.
	 * @return A {@link Set} of {@link Person} that are in the set, may be
	 *         empty.
	 */
	public static final Set<Person> findPersonsByRole(final RoleType type,
			final Set<Person> persons) {
		Set<Person> personsWithRole = new LinkedHashSet<>();
		for (Person p : persons) {
			if (p.getRole().equals(type)) {
				personsWithRole.add(p);
			}
		}
		return personsWithRole;
	}

	/**
	 * This method finds a {@link Person} by name.
	 * 
	 * @param name
	 *            The name of the {@link Person} to look for.
	 * @param persons
	 *            The {@link Set} of persons to search in.
	 * @return A {@link Person} if in the set, otherwise <code>null</code>.
	 */
	public static final Person findPersonByName(final String name,
			final Set<Person> persons) {
		return NamedElement.findElementByNameInCollection(name, persons);
	}

}
