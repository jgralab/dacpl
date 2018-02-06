package de.uni_koblenz.ist.manesh.phd.dac_config.moco_code;

import org.osgi.framework.BundleException;

public interface IActivateComponent {
	void start(String bundleId) throws BundleException;

	void stop(String bundleId) throws BundleException;
}
