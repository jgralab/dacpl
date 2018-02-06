package com.dac.model_repository;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.dac.model_repository.glue.ModelRepositoryMediator;
import com.dac.model_repository.port.function.ISensorInfo;
import com.dac.model_repository.port.manage.IModelAccess;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ServiceRegistration<IModelAccess> mModelAccessRef;
	private ServiceRegistration<ISensorInfo> mSensorInfoRef;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		final ModelRepositoryMediator m = ModelRepositoryMediator.getBuilder()
				.build();

		mModelAccessRef = bundleContext.registerService(IModelAccess.class, m,
				null);
		mSensorInfoRef = bundleContext.registerService(ISensorInfo.class, m,
				null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		mModelAccessRef.unregister();
		mSensorInfoRef.unregister();
		Activator.context = null;
	}

	static BundleContext getContext() {
		return context;
	}

}
