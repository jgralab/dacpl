package de.uni_koblenz.ist.manesh.phd.dac_rule_editor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.RulesFacade;
import de.uni_koblenz.ist.manesh.phd.dac_swt_host.SwtHost;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private RuleEditorMainWindow window;
	private ServiceReference<SwtHost> hostSr;
	private EventBus evtBus;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		final ServiceReference<IModelAccess> masr = bundleContext
				.getServiceReference(IModelAccess.class);
		final IModelAccess ma = bundleContext.getService(masr);
		DacGraph graph = ma.getModelConnection().getRawModel();
		final ServiceReference<EventBus> eventSr = bundleContext
				.getServiceReference(EventBus.class);
		evtBus = bundleContext.getService(eventSr);
		window = new RuleEditorMainWindow((RulesFacade) graph.getFirstRules(), evtBus);
		hostSr = bundleContext
				.getServiceReference(SwtHost.class);
		SwtHost service = bundleContext.getService(hostSr);
		service.addChild(window);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		SwtHost service = bundleContext.getService(hostSr);
		evtBus.unregisterListener(window);
		service.closeChild(window);
	}

}
