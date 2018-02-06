package de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.impl.EventBusImpl;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private ServiceRegistration<EventBus> mServiceRegistration;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		mServiceRegistration = bundleContext.registerService(
				EventBus.class, new EventBusImpl(), null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		mServiceRegistration.unregister();
	}
}
