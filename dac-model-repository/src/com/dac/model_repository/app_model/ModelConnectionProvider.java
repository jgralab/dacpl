/**
 * 
 */
package com.dac.model_repository.app_model;

import java.io.IOException;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.MoCoModelObject;
import de.uni_koblenz.ist.manesh.phd.mdbc.ModelConnection;

/**
 * @author Mahdi Derakhshanmanesh
 * 
 */
public class ModelConnectionProvider extends MoCoModelObject implements
		IModelAccess {
	@Override
	@Export
	public ModelConnection getModelConnection() {
		try {
			return getDefaultModelConnection();
		} catch (final IOException e) {
			throw new IllegalStateException(
					"Could not get a valid model connection", e);
		}
	}
}
