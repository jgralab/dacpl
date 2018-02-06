package com.dac.model_repository.gen;

import java.io.File;
import java.io.IOException;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DACMetaModel;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Alternative;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Exclusion;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Feature;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Features;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Implication;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Mandatory;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Optional;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Or;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Relationship;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.solution.OSGiBundle;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Persons;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Role;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.CameraSensor;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.CardReaderSensor;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Door;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Room;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Rooms;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.exception.GraphIOException;
import de.uni_koblenz.jgralab.graphvalidator.GraphValidator;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;
import de.uni_koblenz.jgralab.utilities.tg2dot.dot.GraphVizLayouter;
import de.uni_koblenz.jgralab.utilities.tg2dot.dot.GraphVizOutputFormat;

public class ModelGenerator {
	// final private static Random RND = new Random();

	public static void main(String[] args) {
		final DacGraph graph = DACMetaModel.instance().createDacGraph(
				ImplementationType.STANDARD);
		// Single event object is reused during runtime
		graph.createEventObject();
		graph.createRules();
		buildFloorPlan(graph);
		createFeatureModel(graph);

		File projectDir;

		if (args != null && args.length > 0) {
			projectDir = new File(args[0]);
		} else {
			projectDir = new File(".");
		}

		validateGraph(projectDir, graph);
		saveGraph(projectDir, graph);
		System.out.println("Done.");
	}

	private static void buildFloorPlan(DacGraph graph) {
		final Room whole = createRoom(graph, "Hallway", 0, 0, 8, 8);
		final Room r1 = createRoom(graph, "R1", 0, 0, 2, 3);
		final Room r2 = createRoom(graph, "R2", 2, 0, 3, 3);
		final Room r3 = createRoom(graph, "R3", 0, 4, 2, 2);
		final Room r4 = createRoom(graph, "R4", 0, 6, 2, 2);
		final Room r5 = createRoom(graph, "R5", 2, 4, 3, 4);
		final Room r6 = createRoom(graph, "R6", 5, 4, 3, 4);

		createCameraSensor(r1, "S1", 0, 0);
		createCameraSensor(r2, "S1", 2, 0);
		createCameraSensor(r3, "S1", 0, 4);
		createCameraSensor(r4, "S1", 0, 6);
		createCameraSensor(r5, "S1", 3, 8);
		createCameraSensor(r6, "S1", 7, 8);
		createCameraSensor(whole, "S1", 8, 4);

		Door door = createDoor(graph, "R1D", 1, 3, false);
		linkRooms(whole, door, r1);
		createCardReaderSensor(door);

		door = createDoor(graph, "R2D1", 3, 3, false);
		linkRooms(whole, door, r2);
		createCardReaderSensor(door);

		door = createDoor(graph, "R2D2", 5, 1, true);
		linkRooms(whole, door, r2);
		createCardReaderSensor(door);

		linkRooms(r5, createDoor(graph, "R3D", 2, 4, true), r3);
		linkRooms(r5, createDoor(graph, "R4D", 2, 6, true), r4);

		door = createDoor(graph, "R5D1", 3, 4, false);
		linkRooms(whole, door, r5);
		createCardReaderSensor(door);

		linkRooms(r6, createDoor(graph, "R5D2", 5, 5, true), r5);

		door = createDoor(graph, "R6D", 6, 4, false);
		linkRooms(whole, door, r6);
		createCardReaderSensor(door);

		final Role manager = createRole(graph, "MANAGER");
		final Role employee = createRole(graph, "EMPLOYEE");
		final Role vip = createRole(graph, "VIP");
		final Role guest = createRole(graph, "GUEST");

		createPerson(graph, "P1", employee).add_room(whole);
		createPerson(graph, "P2", manager).add_room(whole);
		createPerson(graph, "P3", vip).add_room(whole);
		createPerson(graph, "P4", guest).add_room(whole);
		// createPerson(graph, "P5").add_room(whole);
		// createPerson(graph, "P6").add_room(whole);
		// createPerson(graph, "P7").add_room(whole);
		// createPerson(graph, "P8").add_room(whole);
	}

	final private static CameraSensor createCameraSensor(Room room, String id,
			int x, int y) {
		final DacGraph graph = (DacGraph) room.getGraph();
		final CameraSensor s = graph.createCameraSensor();
		final String name = room.get_uniqueName() + id;
		s.set_uniqueName(name);
		s.set_productId("Camera");
		s.set_x(x);
		s.set_y(y);
		graph.createHasRoomSensor(room, s);

		return s;
	}

	final private static CardReaderSensor createCardReaderSensor(Door door) {
		final DacGraph graph = (DacGraph) door.getGraph();
		final CardReaderSensor s = graph.createCardReaderSensor();
		final String name = door.get_uniqueName() + "S";
		s.set_uniqueName(name);
		s.set_productId("Card-Reader");
		s.set_x(door.get_x());
		s.set_y(door.get_y());
		graph.createHasCardReaderSensor(door, s);

		return s;
	}

	final private static Door createDoor(DacGraph graph, String id, int x,
			int y, boolean isVertical) {
		final Door d = graph.createDoor();
		d.set_uniqueName(id);
		d.set_x(x);
		d.set_y(y);
		d.set_vertical(isVertical);
		d.set_locked(true);

		return d;
	}

	final private static Person createPerson(DacGraph graph, String name,
			Role role) {
		final Persons personsModel = createPersons(graph);
		final Person p = graph.createPerson();
		personsModel.add_persons(p);
		p.set_uniqueName(name);
		p.add_roles(role);
		return p;
	}

	private static Persons createPersons(DacGraph graph) {
		Persons personsModel = graph.getFirstPersons();
		
		if (personsModel == null) {
			personsModel = graph.createPersons();
		}
		
		return personsModel;
	}

	final private static Role createRole(DacGraph graph, String name) {
		final Role r = graph.createRole();
		r.set_uniqueName(name);

		return r;
	}

	final private static Room createRoom(DacGraph graph, String id, int x,
			int y, int width, int height) {
		final Rooms roomsModel = getRooms(graph);
		final Room r = graph.createRoom();
		roomsModel.add_rooms(r);
		r.set_x(x);
		r.set_y(y);
		r.set_width(width);
		r.set_height(height);
		r.set_uniqueName(id);

		return r;
	}

	private static Rooms getRooms(DacGraph graph) {
		Rooms roomsModel = graph.getFirstRooms();
		
		if (roomsModel == null) {
			roomsModel = graph.createRooms();
		}
		
		return roomsModel;
	}

	final private static void linkRooms(Room source, Door door, Room target) {
		door.add_sourceRoom(source);
		door.add_targetRoom(target);
	}

	private static void plotGraph(File projectDir, Graph g) throws IOException {
		final Tg2Dot t2d = new Tg2Dot();
		t2d.setGraph(g);
		t2d.setGraphVizOutputFormat(GraphVizOutputFormat.PNG);
		t2d.setGraphVizLayouter(GraphVizLayouter.DOT);
		final File path = new File(projectDir, "graph_plot.png");
		t2d.setOutputFile(path.getAbsolutePath());
		t2d.convert();
	}

	private static void saveGraph(File projectDir, DacGraph graph) {
		final File resDir = new File(projectDir, "res");
		resDir.mkdirs();
		final File targetFile = new File(resDir, "room_model.tg");

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
			final File path = new File(projectDir, "ValidationReport.html");
			gv.createValidationReport(path.getAbsolutePath());
			plotGraph(projectDir, graph);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates a feature model with a standard configuration.
	 * 
	 * @param graph
	 */
	private static void createFeatureModel(DacGraph graph) {
		// CONFIG_STANDARD
		// Feature root = createFeature(graph, "DacsFeatures", true);
		// Feature ruleType = createFeature(graph, "AccessRuleType", true);
		// Feature fixed = createFeature(graph, "Fixed", true);
		// Feature adaptable = createFeature(graph, "Adaptable", false,
		// "dac-rule-editor", "dac-command-line");
		// Feature userIdent = createFeature(graph, "UserIdentification", true);
		// Feature cardReader = createFeature(graph, "CardReader", true,
		// "dac-card-reader-driver");
		// Feature camera = createFeature(graph, "Camera", false,
		// "dac-camera-driver");

		// CONFIG_PREMIUM
		 Feature root = createFeature(graph, "DacsFeatures", true);
		 Feature ruleType = createFeature(graph, "AccessRuleType", true);
		 Feature fixed = createFeature(graph, "Fixed", false);
		 Feature adaptable = createFeature(graph, "Adaptable", true,
		 "dac-rule-editor", "dac-command-line");
		 Feature userIdent = createFeature(graph, "UserIdentification", true);
		 Feature cardReader = createFeature(graph, "CardReader", true,
		 "dac-card-reader-driver");
		 Feature camera = createFeature(graph, "Camera", true,
		 "dac-camera-driver");
		//
		// All unselected. Plain model for first selection phase.
		// TODO: Some plugins that are currently set to true in run
		// configuration could be added here explicitly, e.g., for the
		// DacsFeratures node.
//		Feature root = createFeature(graph, "DacsFeatures", false);
//		Feature ruleType = createFeature(graph, "AccessRuleType", false);
//		Feature fixed = createFeature(graph, "Fixed", false);
//		Feature adaptable = createFeature(graph, "Adaptable", false,
//				"dac-rule-editor");
//		Feature userIdent = createFeature(graph, "UserIdentification", false);
//		Feature cardReader = createFeature(graph, "CardReader", false,
//				"dac-card-reader-driver");
//		Feature camera = createFeature(graph, "Camera", false,
//				"dac-camera-driver");

		// Do wiring.
		linkFeatures(root, Mandatory.class, ruleType);
		linkFeatures(root, Mandatory.class, userIdent);
		linkFeatures(ruleType, Alternative.class, fixed, adaptable);
		linkFeatures(userIdent, Or.class, cardReader, camera);
	}

	private static Feature createFeature(DacGraph graph, String name,
			boolean isSelected, String... bundleIds) {
		Features featuresModel = createFeatures(graph);
		Feature root = graph.createFeature();
		featuresModel.add_features(root);
		root.set_uniqueName(name);
		root.set_selected(isSelected);

		if (bundleIds != null) {
			for (String bundleId : bundleIds) {
				if (bundleId != null && !bundleId.trim().isEmpty()) {
					OSGiBundle bundle = graph.createOSGiBundle();
					bundle.set_uniqueName(bundleId);
					graph.createImplementedBy(root, bundle);
				}

			}
		}

		return root;
	}

	private static Features createFeatures(DacGraph graph) {
		Features featuresModel = graph.getFirstFeatures();
		
		if (featuresModel == null) {
			featuresModel = graph.createFeatures();
		}
		
		return featuresModel;
	}

	final private static void linkFeatures(Feature source,
			Class<? extends Relationship> relType, Feature... targets) {
		DacGraph model = (DacGraph) source.getGraph();
		Relationship rel;

		if (relType == Mandatory.class) {
			rel = model.createMandatory();
		} else if (relType == Optional.class) {
			rel = model.createOptional();
		} else if (relType == Alternative.class) {
			rel = model.createAlternative();
		} else if (relType == Or.class) {
			rel = model.createOr();
		} else if (relType == Implication.class) {
			rel = model.createImplication();
		} else if (relType == Exclusion.class) {
			rel = model.createExclusion();
		} else {
			throw new IllegalArgumentException(
					"Attempted to link featurs with unknown relationship type: "
							+ relType);
		}

		rel.add_source(source);

		for (Feature target : targets) {
			rel.add_targets(target);
		}
	}
}
