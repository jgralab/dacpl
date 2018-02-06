package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

public class DacTokenizer extends Tokenizer {
	@Override
	protected void registerSymbols() {
		setUseCStyleComments(true);
		setAllowUnknownSymbols(true);
		setUseSymbolTable(false);
		registerSymbol("ON", Token.KEY_ON);
		registerSymbol("(", Token.KEY_PAREN_OPEN);
		registerSymbol(")", Token.KEY_PAREN_CLOSE);
		registerSymbol("{", Token.KEY_BRACE_OPEN);
		registerSymbol("}", Token.KEY_BRACE_CLOSE);
		registerSymbol("RULE", Token.KEY_RULE);
		registerSymbol("WHEN", Token.KEY_WHEN);
		registerSymbol("DO", Token.KEY_DO);
		registerSymbol(".", Token.KEY_POINT);
		registerSymbol(",", Token.KEY_COMMA);
		registerSymbol(";", Token.KEY_SEMICOLON);
		registerSymbol("?", Token.KEY_QUESTIONMARK);
		registerSymbol("END", Token.KEY_END);
	}
}
