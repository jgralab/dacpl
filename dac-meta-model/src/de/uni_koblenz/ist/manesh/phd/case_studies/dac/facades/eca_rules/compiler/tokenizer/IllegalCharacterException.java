package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer;

public class IllegalCharacterException extends TokenizerException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public IllegalCharacterException(char currentChar, int line, int pos) {
        super("Illegal character '" + currentChar + "'", line, pos);
    }

}
