package de.uni_koblenz.ist.manesh.phd.case_studies.dspl.access_control;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * This class realizes a simple parser for access control rules. A Rule has the
 * form: "role => allowedRoom1, allowedRoom2, ..., allowedRoomN". The parser is
 * able to identify if the file with rules changed (using its timestamp).
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class AccessRulesParser {

	private long lastTimeStamp;

	private final File accessRulesFile;

	private Scanner scan;

	private static final String SINGLE_LINE_COMMENT = "//";

	/**
	 * This constructor creates a new parser and initializes the {@link Scanner}
	 * object used for actually reading the accessRulesFile.
	 * 
	 * @param accessRulesFile
	 *            A {@link File} that may not be <code>null</code> and must
	 *            really exist.
	 * @throws FileNotFoundException
	 *             If accessRulesFile was invalid and the {@link Scanner} could
	 *             not be initialized.
	 * @throws IllegalArgumentException
	 *             If accessRulesFile is <code>null</code> or does not exist.
	 */
	public AccessRulesParser(final File accessRulesFile)
			throws FileNotFoundException {
		super();
		if (accessRulesFile == null || !accessRulesFile.exists()) {
			throw new IllegalArgumentException(
					"Attempted to initialize the AccessRulesParser with an invalid handle to access ruled");
		} else {
			this.accessRulesFile = accessRulesFile;
			reset();
		}
	}

	/**
	 * This method initiates a new {@link Scanner} object for parsing the access
	 * control rules again.
	 * 
	 * @throws RuntimeException
	 *             If the internally handled accessRulesFile are invalid for
	 *             some (unlikely) reason.
	 */
	public void reset() {
		try {
			this.scan = new Scanner(this.accessRulesFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"Could not find access rules file during reset of scanner object",
					e);
		}
	}

	/**
	 * This method checks if there is a new line to be parsed. Potentially, in
	 * this line a new rule is encoded. The line may be empty though or may
	 * contain a comment - thus it is only "potentially" the next rule.
	 * 
	 * @return <code>true</code> if there is a new line to be parsed
	 *         (independent from its content), otherwise <code>false</code>.
	 */
	public boolean hasNextRulePotentially() {
		assert scan != null;
		// Could be also an empty line!
		return scan.hasNextLine();
	}

	/**
	 * This method parses the next line available and returns the rule in the
	 * form of a {@link Map} (Role -> {Room.name}).
	 * 
	 * @return A {@link Map} representation of the parsed single rule, otherwise
	 *         <code>null</code> (e.g., if no rule exists, the line is empty or
	 *         a comment).
	 */
	public Map<String, Set<String>> parseNextRule() {
		if (!hasNextRulePotentially()) {
			return null;
		}

		// Identify the role and the rooms.
		String line = scan.nextLine();
		String[] split = line.split("=>");

		// Skip commented and empty lines.
		if (line.startsWith(SINGLE_LINE_COMMENT) || line.trim().isEmpty()) {
			return null;
		} else {
			if (split.length != 2 || split[0] == null || split[1].isEmpty()
					|| split[1] == null || split[1].isEmpty()) {
				scan.close();
				throw new RuntimeException("Syntax error detected in line: '"
						+ line + "'");
			} else {
				String roleTypeToken = split[0].trim();
				String[] roomTokens = split[1].split(",");

				// LinkedHashMap and LinkedHashSet keep insertion order.
				Map<String, Set<String>> rule = new LinkedHashMap<>();
				Set<String> roomNames = new LinkedHashSet<>();
				for (String r : roomTokens) {
					roomNames.add(r.trim());
				}
				rule.put(roleTypeToken, roomNames);
				return rule;
			}
		}
	}

	/**
	 * This method checks if the file with access control rules changed, based
	 * on its timestamp of the last modification.
	 * 
	 * @return <code>true</code> if the file with rules changed compared to last
	 *         check, otherwise <code>false</code>.
	 */
	public boolean rulesFileChanged() {
		final long currTimeStamp = accessRulesFile.lastModified();
		if (lastTimeStamp != currTimeStamp) {
			lastTimeStamp = currTimeStamp;
			return true;
		} else {
			return false;
		}
	}

}
