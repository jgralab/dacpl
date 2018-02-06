package de.uni_koblenz.ist.manesh.phd.dac_config.moco_model;

import static org.junit.Assert.*;

import org.junit.Test;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DACMetaModel;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraphFactory;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.DacGraphFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Alternative;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Feature;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Mandatory;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Optional;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Or;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.DacGraphFactoryImpl;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.exception.GraphIOException;
import de.uni_koblenz.jgralab.impl.ConsoleProgressFunction;

/**
 * This class covers essential unit tests to ensure that feature configurations
 * get validated properly. The tests are not complete, i.e., especially multiple
 * levels of features, cross-tree relationships and GREQL constraints have not
 * yet been taken into account.
 * 
 * @author Mahdi Derakhshanmanesh
 * 
 */
public class FeatureConfigurationValidatorTest {

	@Test(expected = IllegalArgumentException.class)
	public final void testFeatureConfigurationValidatorNullModel() {
		new FeatureConfigurationValidator(null);
	}

	@Test
	public final void testValidateEmptyModel() {
		DacGraph model = createEmptyGraph();

		assertNotNull(model);

		FeatureConfigurationValidator validator = new FeatureConfigurationValidator(
				model);
		assertFalse(validator.validate());
	}

	@Test
	public final void testValidateRootOnly() {
		// Create a model with only a single feature in it.
		DacGraph model = createEmptyGraph();
		assertNotNull(model);
		Feature root = model.createFeature();
		root.set_uniqueName("RootFeature");

		// By construction, this should hold. Only one feature is in the model.
		int featureCount = 0;
		for (@SuppressWarnings("unused")
		Feature f : model.getFeatureVertices()) {
			featureCount++;
		}
		assertEquals(1, featureCount);

		FeatureConfigurationValidator validator = new FeatureConfigurationValidator(
				model);

		// A model with only a single feature has only one valid cfg. The root
		// is always mandatory. TODO Check definition!
		root.set_selected(false);
		assertFalse(validator.validate());
		root.set_selected(true);
		assertTrue(validator.validate());
	}

	@Test
	public final void testValidateOptional() {
		// Create a model with a root feature and an optional one attached.
		DacGraph model = createEmptyGraph();
		assertNotNull(model);
		Feature root = model.createFeature();
		root.set_uniqueName("RootFeature");
		Feature optional = model.createFeature();
		optional.set_uniqueName("OptionalFeature");
		Optional opt = model.createOptional();
		opt.add_source(root);
		opt.add_targets(optional);

		FeatureConfigurationValidator validator = new FeatureConfigurationValidator(
				model);

		// Optional feature can remain unselected.
		root.set_selected(true);
		optional.set_selected(false);
		assertTrue(validator.validate());

		// Optional feature can be selected, too.
		optional.set_selected(true);
		assertTrue(validator.validate());
	}

	@Test
	public final void testValidateMandatory() {
		// Create a model with a root feature and a mandatory one attached.
		DacGraph model = createEmptyGraph();
		assertNotNull(model);
		Feature root = model.createFeature();
		root.set_uniqueName("RootFeature");
		Feature mandatory = model.createFeature();
		mandatory.set_uniqueName("MandatoryFeature");
		Mandatory man = model.createMandatory();
		man.add_source(root);
		man.add_targets(mandatory);

		FeatureConfigurationValidator validator = new FeatureConfigurationValidator(
				model);

		// Mandatory feature must be selected.
		root.set_selected(true);
		mandatory.set_selected(false);
		assertFalse(validator.validate());

		mandatory.set_selected(true);
		assertTrue(validator.validate());
	}

	@Test
	public final void testValidateXOr() {
		// Create a model with a root feature and an xor (alternative) one
		// attached.
		DacGraph model = createEmptyGraph();
		assertNotNull(model);
		Feature root = model.createFeature();
		root.set_uniqueName("RootFeature");
		Feature xor1 = model.createFeature();
		xor1.set_uniqueName("AlternativeFeature1");
		Alternative alt = model.createAlternative();
		alt.add_source(root);
		alt.add_targets(xor1);

		FeatureConfigurationValidator validator = new FeatureConfigurationValidator(
				model);

		// A single alternative must be selected.
		root.set_selected(true);
		xor1.set_selected(false);
		assertFalse(validator.validate());

		root.set_selected(true);
		xor1.set_selected(true);
		assertTrue(validator.validate());

		// With multiple alternatives, only one may be selected.
		Feature xor2 = model.createFeature();
		xor2.set_uniqueName("AlternativeFeature2");
		alt.add_targets(xor2);
		xor2.set_selected(false);
		assertTrue(validator.validate());

		// Two alternatives may not be selected at once.
		xor2.set_selected(true);
		assertFalse(validator.validate());
	}

	@Test
	public final void testValidateOr() {
		// Create a model with a root feature and an or one attached.
		DacGraph model = createEmptyGraph();
		assertNotNull(model);
		Feature root = model.createFeature();
		root.set_uniqueName("RootFeature");
		Feature or1 = model.createFeature();
		or1.set_uniqueName("OrFeature1");
		Or or = model.createOr();
		or.add_source(root);
		or.add_targets(or1);

		FeatureConfigurationValidator validator = new FeatureConfigurationValidator(
				model);

		// A single or must be selected.
		root.set_selected(true);
		or1.set_selected(false);
		assertFalse(validator.validate());

		root.set_selected(true);
		or1.set_selected(true);
		assertTrue(validator.validate());

		// With multiple alternatives, many can be selected.
		Feature or2 = model.createFeature();
		or2.set_uniqueName("OrFeature2");
		or.add_targets(or2);
		or2.set_selected(false);
		assertTrue(validator.validate());

		// Two or features may be selected at once.
		or2.set_selected(true);
		assertTrue(validator.validate());
	}

	@Test
	public final void testValidateSampleModel1() {
		// Use the DAC study's standard feature configuration.
		DacGraph model = (DacGraph) loadModelFromFile("test/de/uni_koblenz/ist/manesh/phd/dac_config/moco_model/dac-cfg-standard.tg");
		FeatureConfigurationValidator validator = new FeatureConfigurationValidator(
				model);
		assertTrue(validator.validate());
	}

	@Test
	public final void testValidateSampleModel2() {
		// Use the DAC study's premium feature configuration.
		DacGraph model = (DacGraph) loadModelFromFile("test/de/uni_koblenz/ist/manesh/phd/dac_config/moco_model/dac-cfg-premium.tg");
		FeatureConfigurationValidator validator = new FeatureConfigurationValidator(
				model);
		assertTrue(validator.validate());
	}

	private static final Graph loadModelFromFile(String path) {
		if (path == null || path.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Attempted to load a model file from empty name");
		} else {
			DacGraphFactory f = new DacGraphFactoryImpl();
			f.setGraphImplementationClass(DacGraph.GC, DacGraphFacadeImpl.class);

			DacGraph model = null;
			try {
				model = (DacGraph) GraphIO.loadGraphFromFile(path, f,
						new ConsoleProgressFunction());
				assertNotNull(model);
			} catch (GraphIOException e) {
				// e.printStackTrace();
				fail(e.getMessage());
			}

			return model;
		}
	}

	private DacGraph createEmptyGraph() {
		DacGraphFactory f = new DacGraphFactoryImpl();
		f.setGraphImplementationClass(DacGraph.GC, DacGraphFacadeImpl.class);
		DacGraph model = DACMetaModel.instance().createDacGraph(f);
		return model;
	}

}
