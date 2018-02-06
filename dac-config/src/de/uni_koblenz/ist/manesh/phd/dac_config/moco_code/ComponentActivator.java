package de.uni_koblenz.ist.manesh.phd.dac_config.moco_code;

import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.code.MoCoCodeObject;

public class ComponentActivator extends MoCoCodeObject implements
		IActivateComponent {
	final private static Logger log = Logger.getLogger(ComponentActivator.class
			.getName());
	final private BundleContext mBundleContext;

	public ComponentActivator(BundleContext ctx) {
		mBundleContext = ctx;
	}

	@Export
	@Override
	public void start(String bundleId) throws BundleException {
		log.info("ComponentActivator.start(" + bundleId + ")");
		Bundle bundle = getBundle(bundleId);

		if (bundle == null) {
			throw new IllegalArgumentException(
					"It seems that an invalid bundleId was passed: " + bundleId);
		}

		if (Bundle.ACTIVE != bundle.getState()) {
			bundle.start();
		}
	}

	@Export
	@Override
	public void stop(String bundleId) throws BundleException {
		log.info("ComponentActivator.stop(" + bundleId + ")");
		Bundle bundle = getBundle(bundleId);

		if (bundle == null) {
			throw new IllegalArgumentException(
					"It seems that an invalid bundleId was passed: " + bundleId);
		} else {
			bundle.stop();
		}
	}

	public Bundle getBundle(String symbolicName) {
		log.info("ComponentActivator.getBundle()");
		Bundle bundle = mBundleContext.getBundle(symbolicName);

		if (bundle == null) {
			Bundle[] bundles = mBundleContext.getBundles();

			for (Bundle b : bundles) {
				if (symbolicName.startsWith(b.getSymbolicName())) {
					bundle = b;
					break;
				}
			}
		}

		return bundle;
	}

	public BundleContext getBundleContext() {
		return mBundleContext;
	}

}
