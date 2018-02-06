package de.uni_koblenz.ist.manesh.phd.dac_event_generator.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver.EventScript;
import de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver.SyntaxError;

public class EventScriptTest extends TestCase {

	@Test
	public void testSimpleScript() {
		InputStream is = getClass().getResourceAsStream("./script1.txt");

		try {
			String script = streamToString(is, "UTF-8");
			is.close();

			try {
				EventScript.compile(script);
			} catch (SyntaxError e) {
				fail("Could not parse script. Reason: " + e.getMessage());
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String streamToString(InputStream is, String encoding)
			throws IOException {
		InputStreamReader reader = new InputStreamReader(is, encoding);
		StringBuilder out = new StringBuilder();
		final char[] buffer = new char[0x10000];
		int read;

		do {
			read = reader.read(buffer, 0, buffer.length);

			if (read > 0) {
				out.append(buffer, 0, read);
			}
		} while (read != -1);

		return out.toString();
	}
}
