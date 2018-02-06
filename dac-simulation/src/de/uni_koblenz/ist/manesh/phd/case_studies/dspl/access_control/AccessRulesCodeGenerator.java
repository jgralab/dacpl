package de.uni_koblenz.ist.manesh.phd.case_studies.dspl.access_control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the generation of access control rules in a simple
 * textual notation. No comments are written, just pure rules.
 * 
 * @author Mahdi Derakhshanmanesh {manesh@uni-koblenz.de}
 * 
 */
public class AccessRulesCodeGenerator {

	private static final String THEN_SYMBOL = " => ";

	private final File accessRulesFile;

	public AccessRulesCodeGenerator(final File accessRulesFile) {
		if (accessRulesFile == null) {
			String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(Calendar.getInstance().getTime());
			this.accessRulesFile = new File(timeLog);
		} else {
			this.accessRulesFile = accessRulesFile;
		}
	}

	public void run(final Map<String, Set<String>> rules) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(accessRulesFile));
			writer.write(constructRulesString(rules));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Always close the file.
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String constructRulesString(
			final Map<String, Set<String>> rules) {
		String concreteSyntax = "";
		int i = 0;
		for (String role : rules.keySet()) {
			concreteSyntax += role + THEN_SYMBOL;
			Set<String> rooms = rules.get(role);
			int j = 0;
			for (String roomName : rooms) {
				concreteSyntax += roomName + (j < rooms.size() - 1 ? ", " : "");
				j++;
			}
			if (i < rules.keySet().size() - 1) {
				concreteSyntax += "\n";
			}
			i++;
		}
		return concreteSyntax;
	}
}
