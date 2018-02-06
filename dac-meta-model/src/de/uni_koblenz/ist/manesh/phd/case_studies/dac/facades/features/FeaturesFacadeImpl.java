package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Alternative;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Exclusion;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Feature;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.ImplementedBy;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Implication;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Mandatory;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Optional;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Or;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Relationship;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.solution.ImplementationElement;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.solution.OSGiBundle;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.features.FeaturesImpl;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.algorithms.AlgorithmTerminatedException;
import de.uni_koblenz.jgralab.algolib.algorithms.search.RecursiveDepthFirstSearch;
import de.uni_koblenz.jgralab.algolib.algorithms.search.visitors.DFSVisitorAdapter;

public class FeaturesFacadeImpl extends FeaturesImpl implements
		FeaturesFacade {

	public FeaturesFacadeImpl(int id, Graph g) {
		super(id, g);
	}

	final private static Logger log = Logger.getLogger(FeaturesFacadeImpl.class
			.getSimpleName());

	final private HashSet<Feature> featuresToSelect =  new HashSet<>();
	final private HashSet<Feature> featuresToDeselect =  new HashSet<>();

	@Override
	public void selectFeatures(FeatureSelectionCallback callback, String... featureIDs)
			throws IllegalFeatureSelectionException, Throwable {
		log.info("Selecting features: " + featureIDs);
		determineFeatureSelection(featureIDs);
		plotFeatureGraph((DacGraph) getGraph());

		if (!validate()) {
			log.severe("Invalid feature selection.");
			rollback();
			throw new IllegalFeatureSelectionException(
					"Detected invalid feature configuration",
					getLastValidationException());
		}

		commit(callback);
	}
	
	@Override
	public DacGraph getGraph() {
		return (DacGraph) super.getGraph();
	}

	/**
	 * This method uses a DFS approach to walk along all vertices
	 * {@link Feature} and to check them with their parents. Depending on the
	 * {@link Relationship} between them, different selection states are
	 * allowed. Internally, the method uses {@link RecursiveDepthFirstSearch}.
	 * 
	 * @return <code>true</code> iff the model's selection is a valid
	 *         configuration, else <code>false</code>.
	 */
	public boolean validate() {
		// A feature configuration requires at least one feature.
		// FIXME Will be caught by model evaluator?
		if (countFeatures() <= 0) {
			return false;
		}
	
		RecursiveDepthFirstSearch rdfs = new RecursiveDepthFirstSearch(getGraph());
	
		// TODO We need to think about the edge directions in the schema.
		rdfs.setTraversalDirection(EdgeDirection.INOUT);
	
		// TODO We need to implement visitors cleanly for each type in the
		// meta-model, i.e., we need to define the semantics of interpreting a
		// configuration model.
		rdfs.addVisitor(new DFSVisitorAdapter() {
			@Override
			public void visitVertex(Vertex v)
					throws AlgorithmTerminatedException {
				super.visitVertex(v);
				if (v instanceof Feature) {
					Feature curr = (Feature) v;
					System.out.println("Visiting Feature Vertex: "
							+ curr.get_uniqueName() + "\t\tSelectionState : "
							+ curr.is_selected());
	
					Feature parent = getParent(curr);
					Relationship in = curr.get_incoming();
					if (parent != null) {
						if (curr.is_selected() && !parent.is_selected()) {
							markInvalidAndTerminate(curr, parent);
						} else if (curr.is_selected() && parent.is_selected()) {
							if (in != null) {
								if (in instanceof Mandatory) {
									// curr && parent selected is fine.
								} else if (in instanceof Optional) {
									// curr && parent selected is fine.
								} else if (in instanceof Alternative) {
									validateLocalAlternativeSelection(curr,
											parent, in);
								} else if (in instanceof Or) {
									validateLocalOrSelection(curr, parent, in);
								} else {
									throw new AlgorithmTerminatedException(
											"Unknown relationship type detected: "
													+ in.getClass().getName());
								}
							}
						} else if (!curr.is_selected() && parent.is_selected()) {
							if (in instanceof Mandatory) {
								markInvalidAndTerminate(curr, parent);
							} else if (in instanceof Optional) {
								// Unselected is fine.
							} else if (in instanceof Alternative) {
								validateLocalAlternativeSelection(curr, parent,
										in);
							} else if (in instanceof Or) {
								validateLocalOrSelection(curr, parent, in);
							}
						} else if (!curr.is_selected() && !parent.is_selected()) {
							// TODO investigate
						}
					} else {
						// The feature without a parent is the root...
						if (curr.equals(findRootFeature())) {
							// Roots must always be selected.
							if (!curr.is_selected()) {
								markInvalidAndTerminate(curr, null);
							} else {
								// all fine.
							}
						} else {
							throw new AlgorithmTerminatedException(
									"Feature without parent detected that is not the root ("
											+ findRootFeature()
													.get_uniqueName() + "): "
											+ curr.get_uniqueName());
						}
					}
				} else if (v instanceof Relationship) {
					Relationship r = (Relationship) v;
					Feature alpha = r.get_source();
					Feature omega = getFirstTarget(r);
	
					if (v instanceof Implication) {
						Implication i = (Implication) v;
						// Selected alpha implies selected omega.
						if (i.get_source().is_selected()
								&& !getFirstTarget(i).is_selected()) {
							markInvalidAndTerminateRelationship(
									Implication.class, alpha, omega);
						}
					} else if (v instanceof Exclusion) {
						Exclusion e = (Exclusion) v;
						// Selected alpha implies unselected omega.
						if (e.get_source().is_selected()
								&& getFirstTarget(e).is_selected()) {
							markInvalidAndTerminateRelationship(
									Exclusion.class, alpha, omega);
						}
					} else {
						// Other relationships are covered before.
					}
				} else {
					// TODO In an integrated meta-model, other types will be
					// found, as well!
					// throw new AlgorithmTerminatedException(
					// "Unknown vertex type detected: "
					// + v.getClass().getName());
				}
			}
	
			private void validateLocalOrSelection(Feature curr, Feature parent,
					Relationship in) throws AlgorithmTerminatedException {
				int selectionCount = 0;
				int targetCount = 0;
				for (Feature t : in.get_targets()) {
					if (t.is_selected()) {
						selectionCount++;
					}
					targetCount++;
				}
				if (selectionCount < 1 || selectionCount > targetCount) {
					markInvalidAndTerminate(curr, parent);
				}
			}
	
			private void validateLocalAlternativeSelection(Feature curr,
					Feature parent, Relationship in)
					throws AlgorithmTerminatedException {
				int selectionCount = 0;
				for (Feature t : in.get_targets()) {
					if (t.is_selected()) {
						selectionCount++;
					}
				}
				if (selectionCount != 1) {
					markInvalidAndTerminate(curr, parent);
				}
			}
	
			private int getIteratorSize(Iterator<?> it) {
				int i = 0;
				while (it.hasNext()) {
					it.next();
					i++;
				}
				return i;
			}
	
			private void markInvalidAndTerminate(Feature curr, Feature parent)
					throws AlgorithmTerminatedException {
				String msg = "The current configuration is invalid. Stopped at feature: ";
				if (curr != null) {
					msg += curr.get_uniqueName();
				}
				if (parent != null) {
					msg += " and parent feature: " + parent.get_uniqueName();
				}
				throw new AlgorithmTerminatedException(msg);
			}
	
			private void markInvalidAndTerminateRelationship(
					Class<? extends Relationship> relType, Feature alpha,
					Feature omega) throws AlgorithmTerminatedException {
				String msg = "The current configuration has an invalid "
						+ relType.getSimpleName()
						+ " relationship with source feature: "
						+ alpha.get_uniqueName() + " and target feature: "
						+ omega.get_uniqueName();
				throw new AlgorithmTerminatedException(msg);
			}
	
			private Feature getFirstTarget(Relationship rel) {
				Feature omega = null;
				for (Feature f : rel.get_targets()) {
					omega = f;
					break;
				}
				return omega;
			}
		});
	
		try {
			rdfs.execute();
		} catch (AlgorithmTerminatedException e) {
			mLastException = e;
			return false;
		}
	
		return true;
	}

	public Exception getLastValidationException() {
		return mLastException;
	}

	private void determineFeatureSelection(String... featureIDs) {
		Arrays.sort(featureIDs);
		DacGraph model = getGraph();
		
		synchronized (model) {
			for (Feature f : model.getFeatureVertices()) {
				if (Arrays.binarySearch(featureIDs, f.get_uniqueName()) >= 0) {
					if (!f.is_selected()) {
						f.set_selected(true);
						featuresToSelect.add(f);
					}
				} else {
					// In case the feature was selected but is not part of the
					// passed selection, then deselect it.
					if (f.is_selected() && !featuresToSelect.contains(f)) {
						f.set_selected(false);
						featuresToDeselect.add(f);
					}
				}
			}

			log.info("To Select: " + featuresToString(featuresToSelect));
			log.info("To Deselect: " + featuresToString(featuresToDeselect));
		}
	}

	private static final String featuresToString(Collection<Feature> features) {
		StringBuilder featureNames = new StringBuilder();

		for (Feature f : features) {
			if (featureNames.length() > 0)
				featureNames.append(", ");
			featureNames.append(f.get_uniqueName());
		}

		return featureNames.toString();
	}

	private void commit(FeatureSelectionCallback callback) throws Throwable {
		synchronized (getGraph()) {
			for (Feature f : featuresToSelect) {
				if (f.getDegree(ImplementedBy.EC) > 0) {
					Iterable<ImplementationElement> implElems = f
							.get_implElement();
					for (ImplementationElement ie : implElems) {
						if (ie instanceof OSGiBundle) {
							OSGiBundle bundle = (OSGiBundle) ie;
							callback.startImplementation(bundle.get_uniqueName());
						}
					}
				}
			}

			featuresToSelect.clear();

			for (Feature f : featuresToDeselect) {
				if (f.getDegree(ImplementedBy.EC) > 0) {
					Iterable<ImplementationElement> implElems = f
							.get_implElement();
					for (ImplementationElement ie : implElems) {
						if (ie instanceof OSGiBundle) {
							OSGiBundle bundle = (OSGiBundle) ie;
							callback.stopImplementation(bundle.get_uniqueName());
						}
					}
				}
			}

			featuresToDeselect.clear();
		}
	}

	private void rollback() {
		log.info("Rolling back selection changes.");

		synchronized (getGraph()) {
			for (Feature f : featuresToSelect) {
				f.set_selected(false);
			}

			for (Feature f : featuresToDeselect) {
				f.set_selected(true);
			}
		}
	}

	private void plotFeatureGraph(DacGraph dg) {
		synchronized (getGraph()) {
			for (Feature f : dg.getFeatureVertices()) {
				log.info(f.get_uniqueName() + " " + f.is_selected());
			}
		}
	}

	// private static void plotGraph(Graph g, String projectDir)
	// throws IOException {
	// final Tg2Dot t2d = new Tg2Dot();
	// t2d.setGraph(g);
	// t2d.setGraphVizOutputFormat(GraphVizOutputFormat.PNG);
	// t2d.setGraphVizLayouter(GraphVizLayouter.DOT);
	// t2d.setOutputFile(projectDir + "/graph_plot.png");
	// t2d.convert();
	// }

	
	// -----------------------------------
	
	private Exception mLastException;
	
	private int countFeatures() {
		int featureCount = 0;
		for (Feature f : getGraph().getFeatureVertices()) {
			featureCount++;
		}
		return featureCount;
	}

	private Feature findRootFeature() {
		return getGraph().getFirstFeature();
	}

	private Feature getParent(Feature child) {
		assert child != null;
		Relationship incomingEdge = child.get_incoming();
		return incomingEdge != null ? incomingEdge.get_source() : null;
	}
	
}
