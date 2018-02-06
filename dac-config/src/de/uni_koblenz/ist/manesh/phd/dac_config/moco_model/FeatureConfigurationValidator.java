package de.uni_koblenz.ist.manesh.phd.dac_config.moco_model;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features.FeaturesFacade;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Feature;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Relationship;
import de.uni_koblenz.jgralab.algolib.algorithms.search.RecursiveDepthFirstSearch;

/**
 * This class offers functionality to compute if a given feature model
 * configuration is valid or invalid.
 * 
 * @author manesh
 * 
 */
public class FeatureConfigurationValidator {

	private FeaturesFacade cfg;

	public FeatureConfigurationValidator(DacGraph cfg) {
		super();

		if (cfg == null) {
			throw new IllegalArgumentException("Cannot validate a null model");
		}

		this.cfg = (FeaturesFacade) cfg.getFirstFeatures();
	}

	public Exception getLastException() {
		return cfg.getLastValidationException();
	}

	/**
	 * This method uses a DFS approach to walk along all vertices
	 * {@link Feature} and to check them with their parents. Depending on the
	 * {@link Relationship} between them, different selection states are
	 * allowed. Internally, the method uses {@link RecursiveDepthFirstSearch}.
	 * 
	 * @return <code>true</code> iff the model's selection is a valid
	 *         configuration, else <code>false</code>.
	 */
	public boolean validate() {
		return cfg.validate();
	}
	
}
