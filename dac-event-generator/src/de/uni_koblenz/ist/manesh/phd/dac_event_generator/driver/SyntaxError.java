package de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver;

public class SyntaxError extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SyntaxError(String message) {
		super(message);
	}

	public SyntaxError(String expected, String found) {
		super(expected + " expected, but found '" + found + "' instead.");
	}
}
