package de.uni_koblenz.ist.manesh.phd.dac_controller.app_model;

import java.io.IOException;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.AbstractModelManager;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.mdbc.ModelConnection;
import de.uni_koblenz.ist.manesh.phd.mdbc.ModelQueryException;

public class DacControllerModelManager extends AbstractModelManager {
	private ModelConnection mCon;

	public void setModelConnection(ModelConnection con) {
		mCon = con;
	}

	protected DacGraph getGraph() {
		try {
			return (DacGraph) mCon.getRawModel();
		} catch (ModelQueryException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	protected ModelConnection createDefaultModelConnection() throws IOException {
		return mCon;
	}
}
