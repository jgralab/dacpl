package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

public class TokenizerException extends Exception {
    private static final long serialVersionUID = 1L;

    public TokenizerException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public TokenizerException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public TokenizerException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public TokenizerException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public TokenizerException(String string, int line, int pos) {
        super("Tokenizing error: " + string + " (at line " + line
                + ", position " + pos);
    }

}
