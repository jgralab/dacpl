package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features;

public interface FeatureSelectionCallback {

	void startImplementation(String id) throws Throwable;

	void stopImplementation(String id) throws Throwable;

}
