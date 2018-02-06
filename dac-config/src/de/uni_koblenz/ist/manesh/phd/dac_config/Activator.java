package de.uni_koblenz.ist.manesh.phd.dac_config;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.phd.dac_config.glue.DacConfigMediator;
import de.uni_koblenz.ist.manesh.phd.dac_config.port.function.IFeatureSelect;

public class Activator implements BundleActivator {
	private static String[] CONFIG_STANDARD = { "DacsFeatures",
			"AccessRuleType", "Fixed", "UserIdentification", "CardReader" };

	private static String[] CONFIG_PREMIUM = { "DacsFeatures",
			"AccessRuleType", "Adaptable", "UserIdentification", "CardReader",
			"Camera" };

	private static BundleContext context;
	private ServiceRegistration<IFeatureSelect> featureSelectReg;

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
		IModelAccess ma = getServiceObject(IModelAccess.class);
		DacConfigMediator mediator = DacConfigMediator.getBuilder(
				bundleContext, ma).build();
		mediator.selectFeatures(CONFIG_PREMIUM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		featureSelectReg.unregister();
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
}
