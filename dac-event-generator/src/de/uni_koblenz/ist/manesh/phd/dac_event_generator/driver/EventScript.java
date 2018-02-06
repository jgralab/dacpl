package de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EventScript {
	final private static String BREAK = "break";
	final private static String DELAY = "delay";
	final private static String DETECTS = "detects";
	final private static String PERSON = "person";
	final private static String SENSOR = "sensor";

	final private List<Event> mEvents;
	private int mStepIndex;

	private EventScript(String script) throws SyntaxError {
		mEvents = new ArrayList<Event>();
		parse(script);
	}

	public static EventScript compile(String script) throws SyntaxError {
		return new EventScript(script);
	}

	public List<Event> getEvents() {
		return mEvents;
	}

	public List<Event> getNextStep() {
		ArrayList<Event> result = new ArrayList<Event>();
		final int size = mEvents.size();
		int i = mStepIndex;

		for (; i < size; ++i) {
			Event e = mEvents.get(i);

			if (e instanceof BreakEvent) {
				break;
			} else {
				result.add(e);
			}
		}

		mStepIndex = i + 1;

		return result;
	}

	public void rewind() {
		mStepIndex = 0;
	}

	private void parse(String script) throws SyntaxError {
		Scanner s = new Scanner(script);
		Scanner l = null;
		int lineNumber = 0;

		try {
			while (s.hasNextLine()) {
				lineNumber++;
				String line = s.nextLine();

				l = new Scanner(line);
				String token = l.next();

				if (BREAK.equalsIgnoreCase(token)) {
					mEvents.add(new BreakEvent(lineNumber));
				} else if (SENSOR.equalsIgnoreCase(token)) {
					SensorDetectsEvent e = new SensorDetectsEvent(lineNumber);
					e.sensorId = l.next();
					token = l.next();

					if (!DETECTS.equalsIgnoreCase(token)) {
						throw new SyntaxError(DETECTS, token);
					}

					token = l.next();

					if (!PERSON.equalsIgnoreCase(token)) {
						throw new SyntaxError(PERSON, token);
					}

					e.personId = l.next();

					if (l.hasNext()) {
						token = l.next();

						if (!DELAY.equalsIgnoreCase(token)) {
							throw new SyntaxError(DELAY, token);
						}

						e.delay = l.nextLong();
					}

					mEvents.add(e);
				} else {
					throw new SyntaxError("sensor or break", token);
				}

				l.close();
				l = null;
			}

			s.close();
		} catch (NoSuchElementException e) {
			throw new SyntaxError("Unexpected end of line.");
		} finally {
			s.close();
			if (l != null)
				l.close();
		}
	}
}
