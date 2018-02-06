package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Action;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.EventType;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Rule;

public class DacDecompiler {
	final private DacGraph mGraph;

	public DacDecompiler(DacGraph graph) {
		mGraph = graph;
	}

	public String decompile() {
		StringBuilder sb = new StringBuilder();

		synchronized (mGraph) {
			Iterable<Rule> rules = mGraph.getRuleVertices();
			Map<EventType, List<Rule>> onEvents = new HashMap<>();

			for (Rule r : rules) {
				EventType et = r.get_eventType();
				List<Rule> onEventRules = onEvents.get(et);

				if (onEventRules == null) {
					onEventRules = new ArrayList<>();
					onEvents.put(et, onEventRules);
				}

				onEventRules.add(r);
			}

			Set<Entry<EventType, List<Rule>>> entries = onEvents.entrySet();

			for (Entry<EventType, List<Rule>> entry : entries) {
				EventType et = entry.getKey();
				List<Rule> onEventRules = entry.getValue();
				Collections.sort(onEventRules, new Comparator<Rule>() {
					@Override
					public int compare(Rule o1, Rule o2) {
						return o1.get_priority() - o2.get_priority();
					}
				});

				sb.append("ON (");
				sb.append(et.get_id());
				sb.append(") {");
				sb.append(System.lineSeparator());

				for (Rule r : onEventRules) {
					sb.append("    RULE ");
					sb.append('"');
					sb.append(r.get_uniqueName());
					sb.append('"');
					sb.append(" WHEN");
					sb.append(System.lineSeparator());
					sb.append(autoBreak(
							r.get_condition()
									.get_greqlBooleanExpression()
									.substring(
											DacCompiler.GREQL_PREFIX.length()),
							80, 8));
					sb.append(System.lineSeparator());
					sb.append("    DO");
					sb.append(System.lineSeparator());

					for (Action a : r.get_actions()) {
						sb.append("        ");
						sb.append(a.get_methodName());
						sb.append("(");
						boolean first = false;

						for (String s : a.get_params()) {
							if (first) {
								sb.append(", ");
							}

							if (s.startsWith("__")) {
								sb.append('?');
								s = s.substring(2);
							}

							sb.append('"');
							sb.append(s);
							sb.append('"');
							first = true;
						}

						sb.append(")");
						sb.append(";");
						sb.append(System.lineSeparator());
					}

					sb.append("    END");
					sb.append(System.lineSeparator());
					sb.append(System.lineSeparator());
				}

				sb.append("}");
				sb.append(System.lineSeparator());
				sb.append(System.lineSeparator());
			}
		}

		return sb.toString();
	}

	private static String autoBreak(String line, int maxLen, int indent) {
		char[] in = new char[indent];
		Arrays.fill(in, ' ');
		String ins = new String(in);

		if (line.length() + indent > maxLen) {
			StringBuilder sb = new StringBuilder();
			String[] chunks = line.split(" ");
			int lineLen = indent;
			sb.append(ins);

			for (String chunk : chunks) {
				if (lineLen > indent) {
					chunk = " " + chunk;
				}

				lineLen += chunk.length();

				if (lineLen > maxLen) {
					sb.append(System.lineSeparator());
					sb.append(ins);
					lineLen = chunk.length() + indent;
				}

				sb.append(chunk);
			}

			return sb.toString();
		} else {
			return ins + line;
		}
	}
}
