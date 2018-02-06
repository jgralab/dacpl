package com.dac.sensor.base;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.dac.model_repository.port.function.ISensorInfo;
import com.dac.sensor.base.glue.DriverMediator;
import com.dac.sensor.base.ports.function.IBaseDriver;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MediatorBuilder;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.code.MoCoCodeObject;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;

public abstract class BaseActivator implements BundleActivator {

	private static BundleContext context;
	private IBaseDriver driver;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		BaseActivator.context = bundleContext;

		final ServiceReference<EventBus> sr = bundleContext
				.getServiceReference(EventBus.class);
		final EventBus bus = bundleContext.getService(sr);
		final ServiceReference<ISensorInfo> ssr = bundleContext
				.getServiceReference(ISensorInfo.class);
		final ISensorInfo sensorInfo = bundleContext.getService(ssr);
		driver = createDriver();
		driver.setEventBus(bus);
		driver.setSensorInfoService(sensorInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		BaseActivator.context = null;
		driver.unregisterFromEventBus();
	}

	private IBaseDriver createDriver() {
		return DriverMediator.getBuilder(createDriverMoCoObject()).build();
	}

	abstract protected MoCoCodeObject createDriverMoCoObject();

	static BundleContext getContext() {
		return context;
	}
}
