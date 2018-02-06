/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_controller.app_model;

import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MethodCategory;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.MoCoModelObject;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.ActionProvider;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.RulesFacade;

/**
 * FIXME I started with a CodeVAUObject because the model is passed in from
 * outside. There is no model manager required... Discuss! This should be a
 * ModelVAUObject.
 * 
 * @author Mahdi Derakhshanmanesh
 * @author Thomas Iguchi
 * 
 */
public class DacRuleInterpreter extends MoCoModelObject implements IRuleExecution, ActionProvider {
	final private static Logger log = Logger.getLogger(DacRuleInterpreter.class.getName());

	/*
	 * FIXME params should be removed as this is already a ModelVAUObject.
	 * Breaks the design.
	 */
	@Export(category = MethodCategory.INTERPRET)
	@Override
	public void execute() {
		DacGraph model;

		try {
			model = getDefaultModelConnection().getRawModel();
		} catch (Exception e1) {
			throw new IllegalStateException(e1);
		}

		if (model == null) {
			throw new IllegalArgumentException("Attempted to execute a rule on an invalid model");
		}

		RulesFacade rules = (RulesFacade) model.getFirstRules();

		rules.execute(this);
	}

	@Override
	public void onAction(String name, Object... args) throws NoSuchMethodException, Throwable {
		getMediator().invokeMethod(name, args);
	}
}
