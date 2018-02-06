package de.uni_koblenz.ist.manesh.phd.dac_config.moco_model;

import java.util.logging.Logger;

import org.osgi.framework.BundleException;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.MoCoModelObject;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features.FeatureSelectionCallback;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features.FeaturesFacade;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features.IllegalFeatureSelectionException;
import de.uni_koblenz.ist.manesh.phd.dac_config.moco_code.IActivateComponent;
import de.uni_koblenz.ist.manesh.phd.dac_config.port.function.IFeatureSelect;
import de.uni_koblenz.ist.manesh.phd.mdbc.ModelQueryException;

public class FeatureSelector extends MoCoModelObject implements IFeatureSelect {
	final private static Logger log = Logger.getLogger(FeatureSelector.class
			.getSimpleName());

	private DacGraph model;

	public FeatureSelector(IModelAccess ma) {
		try {
			model = ma.getModelConnection().getRawModel();
		} catch (ModelQueryException e) {
			throw new IllegalStateException(e);
		}
	}

	@Export
	@Override
	public void selectFeatures(String... featureIDs)
			throws IllegalFeatureSelectionException, BundleException {
		FeaturesFacade f = (FeaturesFacade) model.getFirstFeatures();
		IActivateComponent ac = getMediatorAs(IActivateComponent.class);

		try {
			f.selectFeatures(new FeatureSelectionCallback() {
				@Override
				public void stopImplementation(String id) throws Throwable {
					ac.stop(id);
				}

				@Override
				public void startImplementation(String id) throws Throwable {
					ac.start(id);
				}
			}, featureIDs);
		} catch (Throwable e) {
			if (e instanceof BundleException)
				throw (BundleException) e;

			throw new IllegalStateException(e);
		}
	}
}
