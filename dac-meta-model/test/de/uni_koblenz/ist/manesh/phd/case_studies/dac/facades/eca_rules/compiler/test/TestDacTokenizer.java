package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.test;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.ScriptLoader;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.DacTokenizer;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.TokenizerException;

public class TestDacTokenizer extends TestCase {
	public void testSimpleExample() {
		InputStream is = ScriptLoader.open(getClass(), "rules_1.txt");
		DacTokenizer t = new DacTokenizer();

		try {
			t.tokenize(is, "UTF-8");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			fail("Could not parse correct example");
		}

		while(t.hasNext()) {
			System.out.println(t.next());
		}
	}
}
