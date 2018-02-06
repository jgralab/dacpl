package de.uni_koblenz.ist.manesh.phd.dac_config.port.function;

import org.osgi.framework.BundleException;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features.IllegalFeatureSelectionException;

public interface IFeatureSelect {
	void selectFeatures(String... featureIDs)
			throws IllegalFeatureSelectionException, BundleException;
}
