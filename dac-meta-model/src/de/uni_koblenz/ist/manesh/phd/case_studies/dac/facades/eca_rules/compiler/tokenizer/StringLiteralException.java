package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

public class StringLiteralException extends TokenizerException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public StringLiteralException(String message, int line, int pos) {
        super(message, line, pos);
    }

}
