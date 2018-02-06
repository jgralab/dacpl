package de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities;

import java.util.Collection;

/**
 * This abstract class provides a name field for each deriving class.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public abstract class NamedElement {

	protected String name;

	/**
	 * This constructor sets the name of the object to be created.
	 * 
	 * @param name
	 *            A {@link String} that may not be null.
	 * 
	 * @throws IllegalArgumentException
	 *             If passed name is <code>null</code>.
	 */
	public NamedElement(final String name) {
		super();
		setName(name);
	}

	/**
	 * This method returns the name of the object.
	 * 
	 * @return The name of the object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the name of the object.
	 * 
	 * @param name
	 *            The name to set.
	 * @throws IllegalArgumentException
	 *             If passed name is <code>null</code>.
	 */
	public void setName(final String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"Attempted to create a named element with a null name");
		} else {
			this.name = name;
		}
	}

	/**
	 * This method searches for a {@link NamedElement} or deriving type in a
	 * passed {@link Collection}.
	 * 
	 * @param name
	 *            The name of the element to look for.
	 * @param elements
	 *            A {@link Collection} of elements.
	 * @return The found element, otherwise <code>null</code>.
	 * @throws IllegalArgumentException
	 *             If (name == null || elements == null || elems.isEmpty()).
	 */
	public static final <T extends NamedElement> T findElementByNameInCollection(
			final String name, final Collection<T> elements) {
		if (name == null || elements == null || elements.isEmpty()) {
			throw new IllegalArgumentException(
					"Attempted to seach for an invalid name or in an empty set of elements");
		} else {
			for (T ne : elements) {
				if (ne.getName().equals(name)) {
					return ne;
				}
			}
			return null;
		}
	}

}
