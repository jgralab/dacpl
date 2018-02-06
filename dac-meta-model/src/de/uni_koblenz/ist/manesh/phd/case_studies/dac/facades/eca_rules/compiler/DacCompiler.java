package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.pcollections.ArrayPVector;
import org.pcollections.PVector;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Action;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Condition;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.EventType;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Rule;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Rules;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.DacTokenizer;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.Token;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.TokenizerException;

public class DacCompiler {
	public static final String GREQL_PREFIX = "let e := V{eca_rules.EventObject}[0] in";
	final private static Logger log = Logger.getLogger("DacCompiler");

	final private DacGraph mGraph;
	private DacTokenizer mTokens;

	// FIXME Access to the raw model should be encapsulated via the IModelAccess
	// interface of the dac-model-repository MoCo. This is a shortcut!
	public DacCompiler(DacGraph graph) {
		mGraph = graph;
	}

	public void compile(InputStream script, String charset) throws IOException,
			TokenizerException, SyntaxErrorException {
		mTokens = new DacTokenizer();
		mTokens.tokenize(script, charset);

		synchronized (mGraph) {
			clearRulesFromGraph();

			try {
				start();
			} catch (final SyntaxErrorException e) {
				clearRulesFromGraph();
				throw e;
			}
		}
	}

	public DacGraph getGraph() {
		return mGraph;
	}

	private void clearRulesFromGraph() {
		for (Iterator<Action> iter = mGraph.getActionVertices().iterator(); iter
				.hasNext(); iter = mGraph.getActionVertices().iterator()) {
			iter.next().delete();
		}

		for (Iterator<Condition> iter = mGraph.getConditionVertices()
				.iterator(); iter.hasNext(); iter = mGraph
				.getConditionVertices().iterator()) {
			iter.next().delete();
		}

		for (Iterator<Rule> iter = mGraph.getRuleVertices().iterator(); iter
				.hasNext(); iter = mGraph.getRuleVertices().iterator()) {
			iter.next().delete();
		}

		for (Iterator<EventType> iter = mGraph.getEventTypeVertices()
				.iterator(); iter.hasNext(); iter = mGraph
				.getEventTypeVertices().iterator()) {
			iter.next().delete();
		}
	}

	private EventType getEventTypeForId(Token eventId) {
		final Iterable<EventType> types = mGraph.getEventTypeVertices();

		for (final EventType type : types) {
			if (eventId.getLiteral().equalsIgnoreCase(type.get_id())) {
				return type;
			}
		}

		final EventType et = mGraph.createEventType();
		et.set_id(eventId.getLiteral());

		return et;
	}

	private void ruleAccessRule(EventType onEvent, int priority)
			throws SyntaxErrorException {
		expectToken(Token.KEY_RULE);
		final Token ruleName = expectStringLiteral();

		final Rules rules = mGraph.getFirstRules();
		final Rule rule = mGraph.createRule();
		rules.add_rules(rule);
		rule.set_priority(priority);
		rule.set_uniqueName(ruleName.getLiteral());
		rule.add_eventType(onEvent);

		final Condition cond = mGraph.createCondition();
		rule.add_condition(cond);

		expectToken(Token.KEY_WHEN);
		final StringBuilder greql = new StringBuilder();
		greql.append(GREQL_PREFIX);
		boolean previousWasIdentifier = true;

		while (peekToken().key != Token.KEY_DO) {
			final Token t = nextToken();

			if (t.key == Token.KEY_IDENTIFIER) {
				if (previousWasIdentifier) {
					greql.append(' ');
				}

				previousWasIdentifier = true;
			} else {
				previousWasIdentifier = false;
			}

			if (t.key == Token.KEY_STRING) {
				greql.append(" '" + t.getLiteral() + "' ");
			} else {
				greql.append(t.getLiteral());
			}
		}

		log.info("Adding new rule with name '" + ruleName.value
				+ "' and condition:");
		log.info(greql.toString());

		cond.set_greqlBooleanExpression(greql.toString());
		ruleActionBlock(rule);
	}

	private void ruleActionBlock(Rule rule) throws SyntaxErrorException {
		expectToken(Token.KEY_DO);

		while (peekToken().key != Token.KEY_END) {
			final Token methodName = expectIdentifier();
			final List<String> args = scanArguments();
			expectToken(Token.KEY_SEMICOLON);

			final Action action = mGraph.createAction();
			action.set_methodName(methodName.getLiteral());
			final PVector<String> pargs = ArrayPVector.empty();
			action.set_params(pargs.plusAll(args));
			rule.add_actions(action);

			log.info("Action: " + methodName.getLiteral() + "("
					+ action.get_params() + ")");
		}

		expectToken(Token.KEY_END);
	}

	private void ruleOnEvent() throws SyntaxErrorException {
		expectToken(Token.KEY_ON);
		expectToken(Token.KEY_PAREN_OPEN);
		final Token eventId = expectIdentifier();
		log.info("Compiling new rules for event type " + eventId.value);
		expectToken(Token.KEY_PAREN_CLOSE);
		expectToken(Token.KEY_BRACE_OPEN);
		scanRuleStatements(getEventTypeForId(eventId));
		expectToken(Token.KEY_BRACE_CLOSE);
	}

	//
	// private String scanPropertyExpression() throws SyntaxErrorException {
	// final StringBuilder pe = new StringBuilder();
	// pe.append("__");
	// pe.append(expectIdentifier().value);
	//
	// while (peekToken().key == Token.KEY_POINT) {
	// nextToken();
	// pe.append(".");
	// pe.append(expectIdentifier().value);
	// }
	//
	// return pe.toString();
	// }

	private List<String> scanArguments() throws SyntaxErrorException {
		final List<String> args = new ArrayList<String>();
		expectToken(Token.KEY_PAREN_OPEN);

		do {
			final Token token = peekToken();

			if (token.key == Token.KEY_QUESTIONMARK) {
				nextToken();
				final Token query = expectStringLiteral();
				args.add("__" + query.getLiteral());
			} else if (token.key == Token.KEY_STRING) {
				args.add(token.value.toString());
				nextToken();
			} else {
				throw new SyntaxErrorException("Illegal argument expression",
						token);
			}

			if (peekToken().key != Token.KEY_PAREN_CLOSE) {
				expectToken(Token.KEY_COMMA);
			}
		} while (peekToken().key != Token.KEY_PAREN_CLOSE);

		expectToken(Token.KEY_PAREN_CLOSE);

		return args;
	}

	private void scanRuleStatements(EventType onEvent)
			throws SyntaxErrorException {
		int priority = 0;

		while (peekToken().key != Token.KEY_BRACE_CLOSE) {
			ruleAccessRule(onEvent, priority++);
		}
	}

	private void start() throws SyntaxErrorException {
		while (hasNextToken()) {
			ruleOnEvent();
		}
	}

	protected Token expectIdentifier() throws SyntaxErrorException {
		return expectToken(Token.KEY_IDENTIFIER);
	}

	protected Token expectStringLiteral() throws SyntaxErrorException {
		return expectToken(Token.KEY_STRING);
	}

	protected Token expectToken(int key) throws SyntaxErrorException {
		final Token token = mTokens.next();

		if (token.key != key) {
			throw new SyntaxErrorException("Expected "
					+ mTokens.getLiteralForSymbol(key), token);
		}

		return token;
	}

	protected boolean hasNextToken() {
		return mTokens.hasNext();
	}

	protected Token nextToken() {
		return mTokens.next();
	}

	protected Token peekNextToken() {
		return mTokens.peekNext();
	}

	protected Token peekToken() {
		return mTokens.peek();
	}
}
