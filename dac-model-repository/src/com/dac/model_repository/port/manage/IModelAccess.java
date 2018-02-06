/**
 * 
 */
package com.dac.model_repository.port.manage;

import de.uni_koblenz.ist.manesh.phd.mdbc.ModelConnection;

/**
 * @author Mahdi Derakhshanmanesh
 *
 */
public interface IModelAccess {
	ModelConnection getModelConnection();
}
