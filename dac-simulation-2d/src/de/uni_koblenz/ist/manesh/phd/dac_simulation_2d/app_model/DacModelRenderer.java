/**
 *
 */
package de.uni_koblenz.ist.manesh.phd.dac_simulation_2d.app_model;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.dac.model_repository.port.manage.IModelAccess;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.MoCoModelObject;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.CardReaderSensor;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Door;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Person;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Room;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Sensor;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Entity;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventListener;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;
import de.uni_koblenz.ist.manesh.phd.mdbc.ModelConnection;

/**
 * Interprets a {@link DacGraph} and renders it using AWT and Swing.
 *
 * TODO Is this really app_model?
 *
 * @author Mahdi Derakhshanmanesh
 * @author Thomas Iguchi
 */
public class DacModelRenderer extends MoCoModelObject implements EventListener,
		ISimulation {
	/**
	 * An internally used {@link Canvas} subclass to render into similar to a
	 * canvas. This {@link Canvas} is placed inside the main {@link JFrame}. It
	 * triggers rendering of all model elements in its
	 * {@link RenderArea#paint(Graphics)} method.
	 *
	 * @author Mahdi Derakhshanmanesh
	 */
	@SuppressWarnings("serial")
	private class RenderArea extends Canvas implements MouseMotionListener {
		public RenderArea() {
			setBackground(Color.white);
			addMouseMotionListener(this);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// Ignore.
		}

		/*
		 * Permanently checks the current mouse position, translated into world
		 * space, with the positions of persons. If a person is found, a string
		 * is rendered below it.
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			// FIXME This is terrible and experimental code.
			final Graphics2D g2d = ((Graphics2D) getGraphics());
			final AffineTransform at = g2d.getTransform();
			at.translate(xOffset, yOffset);
			at.scale(xScaleFactor, yScaleFactor);

			final Point2D worldSpace = new Point2D.Double();
			try {
				at.inverseTransform(new Point2D.Double(e.getX(), e.getY()),
						worldSpace);
			} catch (final NoninvertibleTransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (final Ellipse2D eli : personShapes) {
				if (eli.contains(worldSpace.getX(), worldSpace.getY())) {
					at.transform(
							new Point2D.Double(eli.getCenterX(), eli
									.getCenterY()), worldSpace);
					// FIXME How to get model data access?
					g2d.drawString("Person!", (float) worldSpace.getX(),
							(float) (worldSpace.getY() + (at.getScaleX() * eli
									.getHeight())));
				}
			}
			// FIXME Our code does not have a main loop and no double buffering.
			// Needed?
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			synchronized (model) {
				// Prepare the render context. Offset and scale factors are
				// assumed
				// to be set whenever the "componentResized" event is received.
				final Graphics2D g2d = (Graphics2D) g;
				g2d.translate(xOffset, yOffset);
				g2d.scale(xScaleFactor, yScaleFactor);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				// Render the rooms and persons.
				// Moved persons rendering into room rendering because of
				// special
				// rendering logic that depends on a particular room and the
				// amount
				// of persons in that room!
				for (final Room r : model.getRoomVertices()) {
					paintModelElement(g2d, r);
				}

				// Render doors on top.
				for (final Door d : model.getDoorVertices()) {
					paintModelElement(g2d, d);
				}

				// Render sensors on top.
				for (final Sensor s : model.getSensorVertices()) {
					paintModelElement(g2d, s);
				}
			}
		}
	}

	private static final Logger log = Logger.getLogger(DacModelRenderer.class
			.getName());

	private static final String FRAME_TITLE = "Dynamic Access Control System - Simulation";

	// The resolution with which the JFrame window is initialized.
	private final Dimension startRes = new Dimension(800, 600);

	/*
	 * There is a virtual internal resolution that stays the same. Positions can
	 * be rendered with fixed values. Then, the scaling is calculated based on
	 * the real on-screen resolution. The render area is centered and there is
	 * some free space (left, right, top, bottom) in case the rendered area
	 * cannot fit perfectly into the screen area.
	 */
	private final Dimension virtualRes = new Dimension(8, 8);
	private double xOffset;
	private double xScaleFactor = 1;
	private double yOffset;
	private double yScaleFactor = 1;

	// Caching a reference to the model required in each rendering loop.
	private DacGraph model;

	List<Ellipse2D> personShapes = new ArrayList<>();

	private EventBus mEventBus;

	final private Set<String> mFlashingSensorIds = new HashSet<>();

	private RenderArea ra;

	private JFrame frame;

	private ModelConnection mModelConnection;

	@Override
	public boolean onEvent(Entity subject, String subjectId, Verb verb,
			Entity object, String objectId) {
		if (Verb.DETECTS == verb) {
			flashSensor(subjectId);
		}

		if (frame != null) {
			ra.repaint();
			// EventQueue.invokeLater(new Runnable() {
			// @Override
			// public void run() {
			//
			// }
			// });
		} else {
			log.info("Attempted to call repaint() on invalid frame.");
		}

		return false;
	}

	/**
	 * Triggers the execution of the simulation. Starts with opening a
	 * {@link JFrame} window and rendering of rooms, doors, persons, sensors
	 * etc. Actual rendering is encapsulated by the internal {@link RenderArea}
	 * class, a {@link JPanel} subclass.
	 */
	@Override
	@Export
	public void run() {
		log.info("Started running the DACModelRenderer...");
		// Init the local model reference once.
		try {
			model = mModelConnection.getRawModel();
		} catch (final Exception e) {
			throw new RuntimeException(
					"Failed to get a valid DAC model reference.", e);
		}

		// Initialize the Swing/AWT GUI elements for painting.
		frame = new JFrame(FRAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(startRes);
		frame.setMinimumSize(frame.getSize());

		// Log exit to console.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				log.info("Fini!");
				// System.exit(0);
			}
		});

		// Create the render area and listen to rescale event.
		ra = new RenderArea();
		ra.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				recomputeScaleFactors(e.getComponent().getSize());
			}
		});
		frame.add(ra);

		// Trigger resize manually. Not really needed.
		// recomputeScaleFactors(frame.getSize());
	}

	@Override
	@Export
	public void setEventBus(EventBus bus) {
		mEventBus = bus;

		for (final Verb v : Verb.values()) {
			bus.registerListenerForVerb(v, this);
		}
	}

	@Export
	@Override
	public void setModelAccess(IModelAccess ma) {
		mModelConnection = ma.getModelConnection();
	}

	@Override
	@Export
	public void unregisterFromEventBus() {
		mEventBus.unregisterListener(this);
		mEventBus = null;
	}

	private void flashSensor(String sensorId) {
		log.info("Flashing sensor " + sensorId);
		mFlashingSensorIds.add(sensorId);
	}

	/**
	 * Returns the color a person shall be rendered with. May change depending
	 * on the role, for example.
	 *
	 * @param p
	 *            The {@link Person} to render.
	 * @return A {@link Color} to use for rendering the referenced
	 *         {@link Person}.
	 */
	private Color getColorForPerson(Person p) {
		// TODO Adjust color for different roles.
		return Color.BLUE;
	}

	/**
	 * Draws a {@link Door} that is either open (green) or closed (red). Assumes
	 * that doors are rendered on top of rooms.
	 *
	 * @param g2d
	 *            {@link Graphics2D} context. May not be <code>null</code>.
	 * @param d
	 *            {@link Door} to draw. May not be <code>null</code>.
	 */
	private void paintModelElement(Graphics2D g2d, Door d) {
		final AffineTransform transform = g2d.getTransform();
		final AffineTransform saved = (AffineTransform) transform.clone();
		// Local transformation matrix (we draw into origin)
		final AffineTransform local = new AffineTransform();

		final Room targetRoom = d.get_targetRoom();
		float doorOrientation;

		if (d.is_vertical()) {
			// Determine a door orientation factor that specifies
			// the direction in which it swings open. The direction
			// depends on the relative location of the room
			if (targetRoom.get_x() < d.get_x()) {
				doorOrientation = 1;
			} else {
				doorOrientation = -1;
			}

			local.rotate(Math.toRadians(90), d.get_x(), d.get_y());
		} else {
			// See comment above about door orientation factor
			if (targetRoom.get_y() < d.get_y()) {
				doorOrientation = -1;
			} else {
				doorOrientation = 1;
			}
		}

		local.translate(d.get_x(), d.get_y());
		transform.concatenate(local);
		final Stroke oldStroke = g2d.getStroke();
		final Color oldColor = g2d.getColor();
		final Shape rect = new Rectangle2D.Double(0, 0, 0.8, 0);

		// Draw hole into wall
		g2d.setStroke(new BasicStroke(0.25f));
		g2d.setTransform(transform);
		g2d.setColor(Color.WHITE);
		g2d.draw(rect);

		local.setToIdentity();

		// Rotate door in case it's unlocked
		if (d.is_vertical()) {
			float deg = 90;
			if (!d.is_locked())
				deg += doorOrientation * 45;
			local.rotate(Math.toRadians(deg), d.get_x(), d.get_y());
		} else if (!d.is_locked()) {
			local.rotate(Math.toRadians(doorOrientation * 45), d.get_x(),
					d.get_y());
		}

		// Draw door
		local.translate(d.get_x(), d.get_y());
		transform.setTransform(saved);
		transform.concatenate(local);
		g2d.setTransform(transform);
		g2d.setStroke(new BasicStroke(0.25f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g2d.setColor(d.is_locked() ? Color.RED : Color.GREEN);
		g2d.draw(rect);

		// Restore previous drawing settings
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
		g2d.setTransform(saved);
	}

	private void paintModelElement(Graphics2D g2d, Person p, double x, double y) {
		final Stroke oldStroke = g2d.getStroke();
		final Color oldColor = g2d.getColor();
		final Shape shape = new Ellipse2D.Double(x + 0.15, y + 0.15, 0.25, 0.25);

		// Experimental!
		personShapes.add((Ellipse2D) shape);

		g2d.setColor(getColorForPerson(p));
		g2d.fill(shape);
		g2d.setStroke(new BasicStroke(0.25f));
		// Restore previous drawing settings
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
	}

	private void paintModelElement(Graphics2D g2d, Room r) {
		// Set render modes.
		final Stroke oldStroke = g2d.getStroke();
		final Color oldColor = g2d.getColor();
		final Stroke roomStroke = new BasicStroke(0.25f);
		g2d.setStroke(roomStroke);
		g2d.setColor(Color.BLACK);

		final int roomWidth = r.get_width();
		final int roomHeight = r.get_height();
		final int roomX = r.get_x();
		final int roomY = r.get_y();

		g2d.drawRect(roomX, roomY, roomWidth, roomHeight);

		// Restore render modes.
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);

		// Render all persons in the current room as filled circles into the
		// current room
		final Iterable<Person> persons = r.get_persons();
		int xx = 0;
		int yy = 0;
		int xdisp = 0;
		int ydisp = 0;
		double px, py;
		final DacGraph g = (DacGraph) r.getGraph();

		layouting: for (final Person p : persons) {
			// Keeps track of a free spot within the current room
			// that is not occupied by another inner room through
			// intersection
			boolean spotFound;

			// Search for a free spot
			do {
				spotFound = true;
				/*
				 * Cram people a bit together in a room (basically double the
				 * grid space in a room, hence factor 0.5)
				 */
				px = roomX + xdisp + xx * 0.5;
				py = roomY + ydisp + yy * 0.5;

				/*
				 * A room may fully contain another room (such as the "Hallway"
				 * that surrounds any other room). We need to find room
				 * intersections in order to find a free spot on the grid that
				 * is not occupied by a different room.
				 */
				for (final Room otherRoom : g.getRoomVertices()) {
					/*
					 * Don't check neighboring rooms that share one wall with
					 * the current room and make sure that the other room to
					 * check against does not fully enclose the current room.
					 */
					if (otherRoom == r || encloses(otherRoom, r)
							|| neighbors(r, otherRoom))
						continue;
					if (pointInRoom(otherRoom, xx + roomX, yy + roomY)) {
						// Intersection detected. Need to search for another
						// free spot
						spotFound = false;
						xdisp = otherRoom.get_x();
						ydisp = otherRoom.get_y();
						break;
					}
				}

				// Auto-layouting. Persons are rendered into
				// a room starting from the upper left corner.
				if (++xx + xdisp * 2 >= roomWidth * 2) {
					xdisp = 0;
					ydisp = 0;
					xx = 0;
					yy++;

					if (yy >= roomHeight * 2) {
						log.severe("Cannot render person. Room is full");
						break layouting;
					}
				}
			} while (!spotFound);

			paintModelElement(g2d, p, px, py);
		} // END FOR (layouting)
	}

	private void paintModelElement(Graphics2D g2d, Sensor s) {
		final Stroke oldStroke = g2d.getStroke();
		final Color oldColor = g2d.getColor();
		final Shape shape = new Rectangle2D.Double(s.get_x(), s.get_y(), 0.04,
				0.04);

		Color col;

		if (s instanceof CardReaderSensor) {
			col = Color.ORANGE;
		} else {
			col = Color.MAGENTA;
		}

		if (mFlashingSensorIds.remove(s.get_uniqueName())) {
			col = Color.CYAN;
		}

		g2d.setColor(col);
		g2d.setStroke(new BasicStroke(0.25f));
		g2d.draw(shape);
		// Restore previous drawing settings
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
	}

	private void recomputeScaleFactors(Dimension currRes) {
		final float wantedRatio = (float) virtualRes.width / virtualRes.height;
		final float curRatio = (float) currRes.width / currRes.height;
		int wantedWidth, wantedHeight;

		if (curRatio > wantedRatio) {
			// Window is wider (adjust width)

			if (wantedRatio > 1f) {
				// Wanted area is a rectangle that is wider than tall
				wantedWidth = Math.round(currRes.width / wantedRatio);
				wantedHeight = Math.round(wantedWidth / wantedRatio);
			} else {
				// Wanted area is a rectangle that is taller than wide
				wantedHeight = Math.round(currRes.height / wantedRatio);
				wantedWidth = Math.round(wantedHeight / wantedRatio);
			}
		} else {
			// Window is taller (adjust height)

			if (wantedRatio > 1f) {
				// Wanted area is a rectangle that is wider than tall
				wantedHeight = Math.round(currRes.height / wantedRatio);
				wantedWidth = Math.round(wantedHeight / wantedRatio);
			} else {
				// Wanted area is a rectangle that is taller than wide
				wantedWidth = Math.round(currRes.width / wantedRatio);
				wantedHeight = Math.round(wantedWidth / wantedRatio);
			}
		}

		xScaleFactor = wantedWidth / virtualRes.width;
		yScaleFactor = wantedHeight / virtualRes.height;
		xOffset = (currRes.width - wantedWidth) / 2.0;
		yOffset = (currRes.height - wantedHeight) / 2.0;

		log.fine("Resize: xScaleFactor: " + xScaleFactor + " yScaleFactor: "
				+ yScaleFactor + " xOffset: " + xOffset + " yOffset: "
				+ yOffset);
	}

	/**
	 * Checks if <code>outerRoom</code> fully encloses <code>innerRoom</code>.
	 * This is computed by checking if both of two diagonal points (corners) of
	 * the inner room are located in the outer room.
	 *
	 * @param outerRoom
	 *            A room that is supposed the outer room.
	 * @param innerRoom
	 *            A room that is supposed to be an inner room fully enclosed by
	 *            the outer one.
	 *
	 * @return <code>true</code> if <code>outerRoom</code> fully encloses inner
	 *         room, else <code>false</code>.
	 */
	public static boolean encloses(Room outerRoom, Room innerRoom) {
		final int x1 = innerRoom.get_x();
		final int y1 = innerRoom.get_y();
		final int x2 = x1 + innerRoom.get_width();
		final int y2 = y1 + innerRoom.get_height();

		return pointInRoom(outerRoom, x1, y1) && pointInRoom(outerRoom, x2, y2);
	}

	/**
	 * Returns the XOR (x ^ y) of two boolean values.
	 *
	 * @param x
	 *            First boolean value.
	 * @param y
	 *            Second boolean value.
	 * @return <code>true</code> if x != y, else <code>false</code>.
	 */
	public static boolean logicalXOR(boolean x, boolean y) {
		// Same as: ((x || y) && !(x && y));
		// Same as: x != y
		return x ^ y;
	}

	/**
	 * Checks if two rooms share one (and only one) wall.
	 *
	 * @param r
	 *            The first {@link Room}.
	 * @param otherRoom
	 *            The second {@link Room}.
	 * @return True if the two rooms are neighbors.
	 */
	public static boolean neighbors(Room r, Room otherRoom) {
		// Share only one wall
		final int rLeftX = r.get_x();
		final int rTopY = r.get_y();
		final int rRightX = rLeftX + r.get_width();
		final int rBottomY = rTopY + r.get_height();
		final int oLeftX = otherRoom.get_x();
		final int oTopY = otherRoom.get_y();
		final int oRightX = oLeftX + otherRoom.get_width();
		final int oBottomY = oTopY + otherRoom.get_height();

		final boolean touchesLeftX = rLeftX >= oLeftX && rLeftX <= oRightX;
		final boolean touchesRightX = rRightX >= oLeftX && rRightX <= oRightX;
		final boolean touchesTopY = rTopY >= oTopY && rTopY <= oBottomY;
		final boolean touchesBottomY = rBottomY >= oTopY
				&& rBottomY <= oBottomY;

		final boolean touchesNorth = (touchesLeftX || touchesRightX)
				&& touchesTopY;
		final boolean touchesSouth = (touchesLeftX || touchesRightX)
				&& touchesBottomY;
		final boolean touchesWest = touchesLeftX
				&& (touchesTopY || touchesBottomY);
		final boolean touchesEast = touchesRightX
				&& (touchesTopY || touchesBottomY);

		return logicalXOR(touchesNorth,
				logicalXOR(touchesSouth, logicalXOR(touchesWest, touchesEast)));
	}

	/**
	 * Checks if a point(given by x and y integers) is inside a 2D room or not. <br>
	 * TODO Currently, this method does not take a wall-thickness into account.
	 *
	 * @param r
	 *            The {@link Room} to check.
	 * @param x
	 *            The x value of the point.
	 * @param y
	 *            The y value of the point.
	 * @return <code>true</code> if the point is inside the room, else
	 *         <code>false</code>.
	 */
	public static boolean pointInRoom(Room r, int x, int y) {
		return x >= r.get_x() && x <= (r.get_x() + r.get_width())
				&& y >= r.get_y() && y <= (r.get_y() + r.get_height());
	}
}
