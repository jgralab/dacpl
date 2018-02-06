package de.uni_koblenz.ist.manesh.phd.dac_command_line;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import de.uni_koblenz.ist.manesh.phd.dac_config.port.function.IFeatureSelect;
import de.uni_koblenz.ist.manesh.phd.dac_controller.ports.manage.IAdapation;
import de.uni_koblenz.ist.manesh.phd.dac_swt_host.SwtHost;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		final IAdapation rulesMan = getServiceObject(IAdapation.class);
		final IFeatureSelect featureSel = getServiceObject(IFeatureSelect.class);
		CmdMainWindow window = new CmdMainWindow(rulesMan, featureSel);
		SwtHost service = getServiceObject(SwtHost.class);
		service.addChild(window);
	}

	private static <T> T getServiceObject(Class<T> clazz) {
		ServiceTracker<T, T> st = new ServiceTracker<T, T>(getContext(), clazz,
				null);
		st.open();
		try {
			return st.getService();
		} finally {
			st.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
