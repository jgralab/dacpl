package de.uni_koblenz.ist.manesh.phd.case_studies.dspl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.ist.manesh.phd.case_studies.dspl.access_control.AccessRulesCodeGenerator;

/**
 * This JUnit class implements tests for the {@link AccessRulesCodeGenerator}.
 * It especially checks if the generated string conforms to the intended output
 * - including formatting.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class AccessRulesCodeGeneratorTest {

	// System.getProperty("line.separator"); returns "\r\n"
	public static final String NEW_LINE = "\n";

	private static File accessRulesFile;

	private Map<String, Set<String>> rulesSampleData;

	private AccessRulesCodeGenerator gen;

	@Before
	public void setUp() throws Exception {
		try {
			accessRulesFile = File.createTempFile("access-rules-test", ".ar");
			System.out.println("Created temporary access rules file here:\n\t"
					+ accessRulesFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		gen = new AccessRulesCodeGenerator(accessRulesFile);
		rulesSampleData = TestDataGenerator.createSampleAccessRulesWithoutVIP();
	}

	@Test
	public void testRun() {
		// The file passed to the code generator must be empty at first.
		assertTrue(accessRulesFile.length() <= 0);

		gen.run(rulesSampleData);

		// The file passed to the code generator must be non-empty for sure.
		assertTrue(accessRulesFile.length() > 0);

		// TODO Parse file and compare strings again.
	}

	@Test
	public void testConstructRulesString() {
		// Rules syntax must match exactly.
		assertTrue(gen
				.constructRulesString(rulesSampleData)
				.equals("MANAGER => MANAGEMENT_ROOM, SEMINAR_ROOM"
						+ NEW_LINE
						+ "ENGINEER => ENGINEERING_ROOM_1, ENGINEERING_ROOM_2, ENGINEERING_ROOM_3, SEMINAR_ROOM"
						+ NEW_LINE
						+ "ADMIN_STAFF => MANAGEMENT_ROOM, MAIN_OFFICE"
						+ NEW_LINE + "IT_STAFF => IT_ROOM, SEMINAR_ROOM"
						+ NEW_LINE + "GUEST => SEMINAR_ROOM"));

		// The file passed to the code generator must be still empty.
		assertTrue(accessRulesFile.length() <= 0);
	}

}
