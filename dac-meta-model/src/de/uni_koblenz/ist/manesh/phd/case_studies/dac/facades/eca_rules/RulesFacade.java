package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules;

import java.io.IOException;
import java.io.InputStream;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.SyntaxErrorException;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.TokenizerException;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

public interface RulesFacade {
	void adaptRulesForVIP();

	boolean updateEvent(String subjectId, Verb verb, String objectId);

	public void compile(InputStream script, String charset)
			throws IOException, TokenizerException, SyntaxErrorException;
	
	public String decompile();
	
	void execute(ActionProvider provider);
}
