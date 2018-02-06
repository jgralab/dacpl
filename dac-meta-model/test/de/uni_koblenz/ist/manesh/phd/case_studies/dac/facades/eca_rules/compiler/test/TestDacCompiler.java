package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DACMetaModel;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.DacCompiler;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.DacDecompiler;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.ScriptLoader;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.SyntaxErrorException;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.compiler.tokenizer.TokenizerException;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.exception.GraphIOException;
import de.uni_koblenz.jgralab.graphvalidator.GraphValidator;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;
import de.uni_koblenz.jgralab.utilities.tg2dot.dot.GraphVizLayouter;
import de.uni_koblenz.jgralab.utilities.tg2dot.dot.GraphVizOutputFormat;

public class TestDacCompiler extends TestCase {
	public void testSimpleExample() {
		final InputStream is = ScriptLoader.open(getClass(), "rules_1.txt");
		final DacGraph graph = DACMetaModel.instance().createDacGraph(
				ImplementationType.STANDARD);
		final DacCompiler compiler = new DacCompiler(graph);

		try {
			compiler.compile(is, "UTF-8");
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		} catch (final TokenizerException e) {
			e.printStackTrace();
			fail("Compile error.");
		} catch (final SyntaxErrorException e) {
			e.printStackTrace();
			fail("Compile error.");
		}

		try {
			plotGraph(new File("."), graph);
			saveGraph(new File("."), graph);
			validateGraph(new File("."), graph);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testUncompile() {
		final InputStream is = ScriptLoader.open(getClass(), "rules_1.txt");
		final DacGraph graph = DACMetaModel.instance().createDacGraph(
				ImplementationType.STANDARD);
		final DacCompiler compiler = new DacCompiler(graph);

		try {
			compiler.compile(is, "UTF-8");

			DacDecompiler uc = new DacDecompiler(graph);
			String source = uc.decompile();
			System.out.print(source);

		} catch (final IOException e) {
			throw new IllegalStateException(e);
		} catch (final TokenizerException e) {
			e.printStackTrace();
			fail("Compile error.");
		} catch (final SyntaxErrorException e) {
			e.printStackTrace();
			fail("Compile error.");
		}
	}

	private static void plotGraph(File projectDir, Graph g) throws IOException {
		final Tg2Dot t2d = new Tg2Dot();
		t2d.setGraph(g);
		t2d.setGraphVizOutputFormat(GraphVizOutputFormat.PNG);
		t2d.setGraphVizLayouter(GraphVizLayouter.DOT);
		final File path = new File(projectDir, "rule_graph.png");
		t2d.setOutputFile(path.getAbsolutePath());
		t2d.convert();
	}

	private static void saveGraph(File projectDir, DacGraph graph) {
		final File targetFile = new File(projectDir, "rule_graph.tg");

		try {
			GraphIO.saveGraphToFile(graph, targetFile.getAbsolutePath(), null);
		} catch (final GraphIOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void validateGraph(File projectDir, DacGraph graph) {
		try {
			final GraphValidator gv = new GraphValidator(graph);
			// gv.validate();
			final File path = new File(projectDir, "rule_graph_validation.html");
			gv.createValidationReport(path.getAbsolutePath());
			plotGraph(projectDir, graph);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
