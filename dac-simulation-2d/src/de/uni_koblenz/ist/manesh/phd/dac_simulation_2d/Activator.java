package de.uni_koblenz.ist.manesh.phd.dac_simulation_2d;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.phd.dac_simulation_2d.glue.DacSimulation2DMediator;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private DacSimulation2DMediator mediator;

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
		mediator = DacSimulation2DMediator.getBuilder().build();

		final ServiceReference<EventBus> sr = bundleContext
				.getServiceReference(EventBus.class);
		final EventBus bus = bundleContext.getService(sr);
		final ServiceReference<IModelAccess> masr = bundleContext
				.getServiceReference(IModelAccess.class);
		final IModelAccess ma = bundleContext.getService(masr);
		mediator.setEventBus(bus);
		mediator.setModelAccess(ma);
		// TODO Register service interface. Running here now...
		mediator.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		mediator = null;
	}

	static BundleContext getContext() {
		return context;
	}

}
