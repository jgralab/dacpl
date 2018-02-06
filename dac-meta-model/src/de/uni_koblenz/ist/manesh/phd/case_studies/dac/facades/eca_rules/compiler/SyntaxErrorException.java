package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.Token;

public class SyntaxErrorException extends Exception {
    private static final long serialVersionUID = 1L;
    private Token token;

    public SyntaxErrorException(String message) {
        super(message);
    }

    public SyntaxErrorException(Token token) {
        super("Syntax error in line " + token.line + ", pos " + token.pos);
        this.token = token;
    }

    public SyntaxErrorException(String message, Token token) {
        super(message + " in line " + token.line + ", pos " + token.pos);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
