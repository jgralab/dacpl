package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

public class UnclosedStringLiteralException extends StringLiteralException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UnclosedStringLiteralException(String message, int line, int pos) {
        super(message, line, pos);
    }

    public UnclosedStringLiteralException(int line, int pos) {
        super("Unclosed string literal", line, pos);
    }
}
