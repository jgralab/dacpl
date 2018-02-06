package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features;

public class IllegalFeatureSelectionException extends IllegalArgumentException {
	public IllegalFeatureSelectionException(String message,
			Exception cause) {
		super(message + ". " + cause.getMessage(), cause);
	}

}
