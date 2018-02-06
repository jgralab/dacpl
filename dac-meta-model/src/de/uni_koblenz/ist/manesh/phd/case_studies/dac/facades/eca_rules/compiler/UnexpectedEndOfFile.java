package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler;

public class UnexpectedEndOfFile extends SyntaxErrorException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UnexpectedEndOfFile() {
        super("Unexpected end of file.");
    }

}
