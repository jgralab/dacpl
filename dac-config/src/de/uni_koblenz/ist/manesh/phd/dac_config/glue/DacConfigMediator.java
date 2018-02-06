package de.uni_koblenz.ist.manesh.phd.dac_config.glue;

import org.osgi.framework.BundleContext;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.Mediator;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MediatorBuilder;
import de.uni_koblenz.ist.manesh.phd.dac_config.moco_code.ComponentActivator;
import de.uni_koblenz.ist.manesh.phd.dac_config.moco_code.IActivateComponent;
import de.uni_koblenz.ist.manesh.phd.dac_config.moco_model.FeatureSelector;
import de.uni_koblenz.ist.manesh.phd.dac_config.port.function.IFeatureSelect;

public interface DacConfigMediator extends Mediator, IFeatureSelect,
		IActivateComponent {
	public static MediatorBuilder getBuilder(BundleContext ctx, IModelAccess ma) {
		return MediatorBuilder.forMediatorType(DacConfigMediator.class)
				.addMoCoObjects(new ComponentActivator(ctx),
						new FeatureSelector(ma));
	}
}
