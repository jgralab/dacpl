package com.dac.model_repository.glue;

import com.dac.model_repository.app_model.ModelConnectionProvider;
import com.dac.model_repository.app_model.ModelRepositoryModelManager;
import com.dac.model_repository.app_model.SensorInfoProvider;
import com.dac.model_repository.port.function.ISensorInfo;
import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.Mediator;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MediatorBuilder;

/**
 * 
 * @author Mahdi Derakhshanmanesh
 */
public interface ModelRepositoryMediator extends Mediator, IModelAccess,
		ISensorInfo {

	public static MediatorBuilder getBuilder() {
		return MediatorBuilder.forMediatorType(ModelRepositoryMediator.class)
				.addMoCoObjectTypes(ModelConnectionProvider.class,
						SensorInfoProvider.class).setModelManagerType(
						ModelRepositoryModelManager.class);
	}
}
