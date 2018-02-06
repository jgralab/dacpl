package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Action;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.EventObject;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.EventType;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Rule;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.DacGraphFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.DacCompiler;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.DacDecompiler;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.SyntaxErrorException;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.TokenizerException;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.eca_rules.RulesImpl;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql.GreqlEnvironment;
import de.uni_koblenz.jgralab.greql.GreqlQuery;
import de.uni_koblenz.jgralab.greql.evaluator.GreqlEnvironmentAdapter;

public class EcaRulesFacadeImpl extends RulesImpl implements RulesFacade {
	final private static Logger log = Logger.getLogger(EcaRulesFacadeImpl.class
			.getName());
	final private DacCompiler compiler;
	final private DacDecompiler decompiler;

	public EcaRulesFacadeImpl(int id, Graph g) {
		super(id, g);
		compiler = new DacCompiler(getGraph());
		decompiler = new DacDecompiler(getGraph());
	}
	
	@Override
	public DacGraph getGraph() {
		return (DacGraph) super.getGraph();
	}

	@Override
	public void compile(InputStream script, String charset)
			throws IOException, TokenizerException, SyntaxErrorException {
		compiler.compile(script, charset);
	}

	@Override
	public String decompile() {
		return decompiler.decompile();
	}

	@Override
	public void execute(ActionProvider provider) {
		DacGraph model = getGraph();
		
		synchronized (model) {
			Rule targetRule = null;
			List<Rule> sortedRules = new ArrayList<Rule>();

			for (final Rule r : model.getRuleVertices()) {
				sortedRules.add(r);
			}

			// Rules shall be executed in a "fire first" manner.
			Collections.sort(sortedRules, new Comparator<Rule>() {
				@Override
				public int compare(Rule o1, Rule o2) {
					return o1.get_priority() - o2.get_priority();
				}
			});

			EventObject eo = model.getEventObjectVertices().iterator().next();

			for (Rule r : sortedRules) {
				if (r.get_eventType() == eo.get_eventType()) {
					try {
						if (conditionMet(r, eo)) {
							targetRule = r;
							break;
						}
					} catch (final Exception e) {
						log.severe("Condition evaluation failed: "
								+ e.getMessage());
						continue;
					}
				}
			}

			if (targetRule == null) {
				log.warning("No rules for event type "
						+ (eo.get_eventType() == null ? eo.get_eventType()
								.get_id() : null));
			} else {
				for (final Action a : targetRule.get_actions()) {
					final String methodName = a.get_methodName();
					final List<String> converted = new ArrayList<>();

					for (String param : a.get_params()) {
						if (param.startsWith("__")) {
							try {
								param = evaluateObjectProperty(eo,
										param.substring(2));
							} catch (final Exception e) {
								log.severe("Object property evaluation failed: "
										+ e.getMessage());
								continue;
							}
						}

						converted.add(param);
					}

					try {
						Object[] args;

						/*
						 * Interpreter magic for supporting formatted printing
						 * with variable argument list. Without this explicit
						 * switch, there is no way to use reflection to resolve
						 * and invoke the correct method on a target object.
						 */
						if ("print".equals(methodName)) {
							args = new Object[] {
									converted.remove(0),
									converted.toArray(new String[converted
											.size()]) };
						} else {
							args = converted.toArray(new String[converted
									.size()]);
						}

						provider.onAction(methodName, args);
					} catch (final NoSuchMethodException e) {
						log.severe("Method '" + methodName
								+ "' does  not exist.");
					} catch (Throwable e) {
						log.severe("Exception while executing function: " + e);
					}
				}
			}
		}		
	}

	@Override
	public void adaptRulesForVIP() {
		DacGraph model = getGraph();

		synchronized (model) {
			// Quick hack: assume the rule is there and shift it from the bottom
			// to
			// the top. The interpreter works in a "fire first" manner.
			Rule firstRule = model.getFirstRule();
			Rule lastRule = null;
			for (Rule r : model.getRuleVertices()) {
				lastRule = r;
			}

			if (firstRule != lastRule) {
				int tmpPrio = lastRule.get_priority();
				lastRule.set_priority(firstRule.get_priority());
				firstRule.set_priority(tmpPrio);
			}
		}
	}

	@Override
	public boolean updateEvent(String subjectId, Verb verb, String objectId) {
		DacGraphFacadeImpl model = (DacGraphFacadeImpl) getGraph();
	
		synchronized (model) {
			final EventObject eo = model.getEventObjectVertices().iterator()
					.next();
			final EventType et = getEventTypeForId(model, verb.name());
			if (et == null)
				return false;
			eo.remove_eventType();
			eo.remove_subject();
			eo.remove_object();
			eo.add_eventType(et);
			eo.add_subject(model.findNamedElementByName(subjectId));
			eo.add_object(model.findNamedElementByName(objectId));
	
			return true;
		}
	}

	private boolean conditionMet(Rule r, EventObject eo) {
		// Evaluate the condition GReQL on the model.
		final String condition = r.get_condition().get_greqlBooleanExpression();
		final GreqlQuery query = GreqlQuery.createQuery(condition);
		return (boolean) query.evaluate(r.getGraph());
	}

	private String evaluateObjectProperty(EventObject eo, String substring) {
		final String queryExp = "using e : " + substring;
		final GreqlQuery query = GreqlQuery.createQuery(queryExp);
		final GreqlEnvironment env = new GreqlEnvironmentAdapter();
		env.setVariable("e", eo);
		try {
			final Object res = query.evaluate(eo.getGraph(), env).toString();
			return res != null ? res.toString() : null;
		} catch (final Exception e) {
			log.log(Level.SEVERE, "Query evaluation failed for: " + queryExp, e);
			e.printStackTrace();
			return null;
		}
	}

	private EventType getEventTypeForId(DacGraph model, String eventId) {
		if (eventId == null)
			return null;
		final Iterable<EventType> types = model.getEventTypeVertices();

		for (final EventType type : types) {
			if (eventId.equalsIgnoreCase(type.get_id())) {
				return type;
			}
		}

		return null;
	}
}
