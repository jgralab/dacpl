package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

public class IllegalNumberFormatException extends TokenizerException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public IllegalNumberFormatException(int line, int pos) {
        super("Illegal number format", line, pos);
    }

}
