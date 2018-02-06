package de.uni_koblenz.ist.manesh.phd.case_studies.dspl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.entities.NamedElement;

/**
 * This JUnit class tests the functionality provided by the abstract base class
 * {@link NamedElement}.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class NamedElementTest {

	@Test
	public void test() {
		Set<NamedElement> elems = new LinkedHashSet<>();
		elems.add(new NamedElement("A") {
		});
		elems.add(new NamedElement("B") {
		});
		elems.add(new NamedElement("C") {
		});
		elems.add(new NamedElement("D") {
		});

		assertTrue(NamedElement.findElementByNameInCollection("A", elems) != null);
		assertTrue(NamedElement.findElementByNameInCollection("B", elems) != null);
		assertTrue(NamedElement.findElementByNameInCollection("C", elems) != null);
		assertTrue(NamedElement.findElementByNameInCollection("D", elems) != null);

		assertFalse(NamedElement.findElementByNameInCollection("X", elems) != null);
		assertFalse(NamedElement.findElementByNameInCollection("a", elems) != null);
		assertFalse(NamedElement.findElementByNameInCollection("0", elems) != null);
	}

}
