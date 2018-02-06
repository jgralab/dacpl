package de.uni_koblenz.ist.manesh.phd.case_studies.dspl;

import java.util.Random;
import java.util.Set;

/**
 * This class provides a collection of static utility methods to be used in the
 * simulation.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class Util {

	/**
	 * This method returns a new element from the set of entries passed.
	 * 
	 * @param entries
	 *            A valid set of entries of type T. May not be null, otherwise
	 *            an {@link IllegalArgumentException} is thrown.
	 * @return A random element from the passed set of entries.
	 * @throws IllegalArgumentException
	 *             If entries is <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T getRandomEntryFromSet(final Set<T> entries) {
		if (entries == null) {
			throw new IllegalArgumentException(
					"Attempted to get a random entry from an emty set");
		} else {
			final int randPos = new Random().nextInt(entries.size());
			return (T) entries.toArray()[randPos];
		}
	}

}
