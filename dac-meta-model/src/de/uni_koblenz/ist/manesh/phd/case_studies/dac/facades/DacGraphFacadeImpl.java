package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraphFactory;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.NamedElement;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Rules;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.EcaRulesFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features.FeaturesFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.persons.PersonsFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.rooms.RoomsFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Features;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.DacGraphFactoryImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.DacGraphImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Persons;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Rooms;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.exception.GraphIOException;
import de.uni_koblenz.jgralab.impl.ConsoleProgressFunction;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;
import de.uni_koblenz.jgralab.utilities.tg2dot.dot.GraphVizLayouter;
import de.uni_koblenz.jgralab.utilities.tg2dot.dot.GraphVizOutputFormat;

public class DacGraphFacadeImpl extends DacGraphImpl implements DacGraphFacade {
	final private static Logger log = Logger.getLogger(DacGraphFacadeImpl.class
			.getName());

	private final Stack<byte[]> modelHistory = new Stack<>();

	public DacGraphFacadeImpl(String id, int vMax, int eMax) {
		super(id, vMax, eMax);
	}
	
	
	public static void registerImplementationClasses(DacGraphFactory factory) {
		factory.setGraphImplementationClass(DacGraph.GC, DacGraphFacadeImpl.class);
		factory.setVertexImplementationClass(Features.VC, FeaturesFacadeImpl.class);
		factory.setVertexImplementationClass(Rules.VC, EcaRulesFacadeImpl.class);
		factory.setVertexImplementationClass(Rooms.VC, RoomsFacadeImpl.class);
		factory.setVertexImplementationClass(Persons.VC, PersonsFacadeImpl.class);
	}

	public NamedElement findNamedElementByName(String name) {
		if (name == null)
			return null;
		final Iterable<NamedElement> nes = getNamedElementVertices();

		for (final NamedElement ne : nes) {
			if (name.equals(ne.get_uniqueName()))
				return ne;
		}

		return null;
	}

	@Override
	public void plotModelState() {
		try {
			plotGraph(new File("./res/"), this);
		} catch (final IOException e) {
			log.log(Level.SEVERE, "Failed to plot rules graph: " + this, e);
		}
	}

	@Override
	public void popModelState() {
		try {
			final ByteArrayInputStream in = new ByteArrayInputStream(
					modelHistory.pop());

			synchronized (this) {
				final DacGraphFactory gf = new DacGraphFactoryImpl() {
					@SuppressWarnings("unchecked")
					@Override
					public <G extends Graph> G createGraph(
							de.uni_koblenz.jgralab.schema.GraphClass gc,
							String id, int vMax, int eMax) {
						// Reuse already existing graph
						return (G) DacGraphFacadeImpl.this;
					};
				};
				registerImplementationClasses(gf);

				for (Iterator<Vertex> iter = getGraph().vertices().iterator(); iter
						.hasNext(); iter = getGraph().vertices().iterator()) {
					iter.next().delete();
				}
				for (Iterator<Edge> iter = getGraph().edges().iterator(); iter
						.hasNext(); iter = getGraph().edges().iterator()) {
					iter.next().delete();
				}

				GraphIO.loadGraphFromStream(in, null, null, gf,
						ImplementationType.STANDARD,
						new ConsoleProgressFunction(),
						DacGraph.class.getClassLoader());
			}
		} catch (final EmptyStackException e) {
			log.warning("No model state available on the model history stack.");
		} catch (final GraphIOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void pushModelState() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		synchronized (this) {
			try {
				save(out);
			} catch (final GraphIOException e) {
				throw new IllegalStateException(e);
			}
			modelHistory.push(out.toByteArray());
		}
	}

	// FIXME Move to a base class in core.
	private static void plotGraph(File projectDir, Graph g) throws IOException {
		synchronized (g) {
			final Tg2Dot t2d = new Tg2Dot();
			t2d.setGraph(g);
			t2d.setGraphVizOutputFormat(GraphVizOutputFormat.PNG);
			t2d.setGraphVizLayouter(GraphVizLayouter.DOT);

			final File path = new File(projectDir, "rules_graph_plot_"
					+ new java.util.Date() + ".png");
			t2d.setOutputFile(path.getAbsolutePath());
			t2d.convert();
		}
	}
}
