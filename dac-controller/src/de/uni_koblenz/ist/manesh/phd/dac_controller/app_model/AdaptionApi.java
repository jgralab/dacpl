package de.uni_koblenz.ist.manesh.phd.dac_controller.app_model;

import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MethodCategory;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.MoCoModelObject;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.DacGraphFacade;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.RulesFacade;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.persons.PersonsFacade;
import de.uni_koblenz.ist.manesh.phd.dac_controller.app_code.IEventDispatcher;
import de.uni_koblenz.ist.manesh.phd.dac_controller.ports.manage.IAdapation;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Entity;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

/**
 * A set of methods that can be used to realize access and/or adaptation rules.
 * 
 * @author Mahdi Derakhshanmanesh
 */
public class AdaptionApi extends MoCoModelObject implements IAdapation {
	final private static Logger log = Logger.getLogger(AdaptionApi.class
			.getName());

	// Conceptually, this adaptation needs to be invoked whenever the VIP
	// changes location. The encoded transformation can be triggered from the
	// console or from an ECA rule (e.g., from an adaptation manager).
	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public void adaptRulesForVIP() {
		// Get the loaded model from memory.
		final DacGraph model = getGraph();
		RulesFacade rules = (RulesFacade) model.getFirstRules();
		rules.adaptRulesForVIP();
		notifyModelChanged();
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public void addPerson(String name, String role) {
		// Get the loaded model from memory.
		final DacGraph model = getGraph();
		PersonsFacade persons = (PersonsFacade) model.getFirstPersons();
		persons.addPerson(name, role);
		notifyModelChanged();
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public void addRole(String name) {
		// Get the loaded model from memory.
		final DacGraph model = getGraph();
		PersonsFacade persons = (PersonsFacade) model.getFirstPersons();
		persons.addRole(name);
		notifyModelChanged();
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public void plotModelState() {
		final DacGraphFacade model = (DacGraphFacade) getGraph();
		
		if (model != null) {
			model.plotModelState();
		} else {
			log.severe("Failed to plot rules graph. It was null.");
		}
	}

	@Override
	@Export(category = MethodCategory.MODEL_STATE)
	public void popModelState() {
		final DacGraphFacade model = (DacGraphFacade) getGraph();
		
		if (model != null) {
			model.popModelState();
			notifyModelChanged();
		} else {
			log.severe("Failed to pop model state. It was null.");
		}
	}

	@Override
	@Export(category = MethodCategory.MODEL_STATE)
	public void pushModelState() {
		final DacGraphFacade model = (DacGraphFacade) getGraph();
		
		if (model != null) {
			model.pushModelState();
			
			// TODO Needed?
			notifyModelChanged();
		} else {
			log.severe("Failed to push model state. It was null.");
		}
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public boolean removePerson(String name) {
		final DacGraph model = getGraph();
		PersonsFacade persons = (PersonsFacade) model.getFirstPersons();
		
		try {
			return persons.removePerson(name);
		} finally {
			notifyModelChanged();
		}
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public boolean removeRole(String name) {
		final DacGraph model = getGraph();
		PersonsFacade persons = (PersonsFacade) model.getFirstPersons();
		
		try {
			return persons.removeRole(name);
		} finally {
			notifyModelChanged();
		}
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public String listPersons() {
		final DacGraph model = getGraph();
		PersonsFacade persons = (PersonsFacade) model.getFirstPersons();
		return persons.listPersons();
	}

	@Export(category = MethodCategory.MODEL_STATE)
	@Override
	public String listRoles() {
		final DacGraph model = getGraph();
		PersonsFacade persons = (PersonsFacade) model.getFirstPersons();
		return persons.listRoles();
	}

	protected DacGraph getGraph() {
		try {
			return getDefaultModelConnection().getRawModel();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private void notifyModelChanged() {
		getMediatorAs(IEventDispatcher.class).getEventBus().publish(
				Entity.DAC_CONTROLLER, null, Verb.ADAPTS, Entity.MODEL, null);
	}
}
