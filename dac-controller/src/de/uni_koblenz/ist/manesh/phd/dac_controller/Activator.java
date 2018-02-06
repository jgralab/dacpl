package de.uni_koblenz.ist.manesh.phd.dac_controller;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MediatorBuilder;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_model.DacControllerModelManager;
import de.uni_koblenz.ist.manesh.phd.dac_controller.glue.DacControllerMediator;
import de.uni_koblenz.ist.manesh.phd.dac_controller.ports.manage.IAdapation;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Entity;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventListener;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

public class Activator implements BundleActivator {

	private class ControllerEventListener implements EventListener {
		final private DacControllerMediator mController;

		public ControllerEventListener(DacControllerMediator m) {
			mController = m;
		}

		@Override
		public boolean onEvent(Entity subject, String subjectId, Verb verb,
				Entity object, String objectId) {
			mController.dispatch(subjectId, verb, objectId);
			return false;
		}
	}

	public static BundleContext context;
	private ControllerEventListener mListener;
	private EventBus mEventBus;
	private ServiceRegistration<IAdapation> mRulesManRef;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Activator.start()");
		Activator.context = bundleContext;
		final ServiceReference<EventBus> ebref = Activator.context
				.getServiceReference(EventBus.class);
		mEventBus = Activator.context.getService(ebref);
		final ServiceReference<IModelAccess> ref = Activator.context
				.getServiceReference(IModelAccess.class);
		final IModelAccess ma = Activator.context.getService(ref);
		// final DacCompiler compiler = new DacCompiler((DacGraph) ma
		// .getModelConnection().getRawModel());
		// final InputStream is = ScriptLoader.open(getClass(), "rules_1.txt");
		// compiler.compile(is, "UTF-8");
		// TODO Usually, this class shall not be instantiated like this?
		// Results in a crash!
		final MediatorBuilder builder = DacControllerMediator.getBuilder();
		DacControllerModelManager mm = new DacControllerModelManager();
		mm.setModelConnection(ma.getModelConnection());
		builder.setModelManager(mm);
		DacControllerMediator mediator = builder.build();
		mediator.init(mEventBus);
		mListener = new ControllerEventListener(mediator);
		mEventBus.registerListenerForAny(mListener);

		mRulesManRef = context
				.registerService(IAdapation.class, mediator, null);
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
		mEventBus.unregisterListener(mListener);
		mRulesManRef.unregister();
	}

	static BundleContext getContext() {
		return context;
	}

}
