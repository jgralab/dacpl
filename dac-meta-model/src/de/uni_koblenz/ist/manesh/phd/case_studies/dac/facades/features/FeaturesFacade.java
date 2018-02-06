package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features;

public interface FeaturesFacade {
	void selectFeatures(FeatureSelectionCallback callback, String... featureIDs)
			throws IllegalFeatureSelectionException, Throwable;

	boolean validate();

	public Exception getLastValidationException();
}
