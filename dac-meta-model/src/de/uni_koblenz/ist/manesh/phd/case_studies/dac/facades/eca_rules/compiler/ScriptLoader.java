package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScriptLoader {
	public static InputStream open(Class<?> callerClass, String fileName) {
		return callerClass.getResourceAsStream("./"+fileName);
	}

	public static String load(Class<?> callerClass, String fileName) {
		InputStream is = callerClass.getResourceAsStream("./"+fileName);

		try {
			return streamToString(is, "UTF-8");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
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
