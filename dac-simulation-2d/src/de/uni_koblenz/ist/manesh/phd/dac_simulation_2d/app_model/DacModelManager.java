/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_simulation_2d.app_model;

import java.io.IOException;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.AbstractModelManager;
import de.uni_koblenz.ist.manesh.phd.mdbc.ModelConnection;

/**
 * @author Mahdi Derakhshanmanesh
 * @deprecated
 */
@Deprecated
public class DacModelManager extends AbstractModelManager {

	private ModelConnection modelConnection;

	@Override
	public ModelConnection getDefaultModelConnection() throws IOException {
		return modelConnection;
	}

	public void setModelConnection(ModelConnection modelConnection) {
		this.modelConnection = modelConnection;
	}

	@Override
	protected ModelConnection createDefaultModelConnection() throws IOException {
		return null;
	}
}
