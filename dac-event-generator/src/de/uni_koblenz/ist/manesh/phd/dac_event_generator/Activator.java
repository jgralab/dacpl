package de.uni_koblenz.ist.manesh.phd.dac_event_generator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.uni_koblenz.ist.manesh.phd.dac_event_generator.gui.EventEditorMainWindow;
import de.uni_koblenz.ist.manesh.phd.dac_swt_host.SwtHost;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;

public class Activator implements BundleActivator {

	private static BundleContext context;

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
		final ServiceReference<EventBus> sr = bundleContext
				.getServiceReference(EventBus.class);
		final EventBus bus = bundleContext.getService(sr);
		final EventEditorMainWindow win = new EventEditorMainWindow(bus);
		ServiceReference<SwtHost> hostSr = bundleContext
				.getServiceReference(SwtHost.class);
		SwtHost service = bundleContext.getService(hostSr);
		service.addChild(win);
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
	}

	static BundleContext getContext() {
		return context;
	}

}
