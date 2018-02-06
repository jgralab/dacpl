package de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Entity;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventListener;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

public class EventBusImpl implements EventBus {
	final private static Logger log = Logger.getLogger("EventBus");
	final private Set<EventListener> mAnyListeners;
	final private Map<Entity, Set<EventListener>> mObjectListeners;
	final private Map<Entity, Set<EventListener>> mSubjectListeners;
	final private Timer mTimer;
	final private Map<Verb, Set<EventListener>> mVerbListeners;

	public EventBusImpl() {
		mVerbListeners = new HashMap<Verb, Set<EventListener>>();
		mSubjectListeners = new HashMap<Entity, Set<EventListener>>();
		mObjectListeners = new HashMap<Entity, Set<EventListener>>();
		mAnyListeners = new HashSet<>();
		mTimer = new Timer();
	}

	@Override
	public void publish(Entity subject, String subjectId, Verb verb,
			Entity object, String objectId) {
		log.info("Publishing: " + subject + " " + subjectId + " " + verb + " "
				+ object + " " + objectId);

		synchronized (mAnyListeners) {
			for (final EventListener el : mAnyListeners) {
				el.onEvent(subject, subjectId, verb, object, objectId);
			}
		}

		synchronized (mVerbListeners) {
			final Set<EventListener> set = mVerbListeners.get(verb);

			if (set != null) {
				for (final EventListener l : set) {
					l.onEvent(subject, subjectId, verb, object, objectId);
				}
			}
		}

		synchronized (mSubjectListeners) {
			final Set<EventListener> set = mSubjectListeners.get(subject);

			if (set != null) {
				for (final EventListener l : set) {
					l.onEvent(subject, subjectId, verb, object, objectId);
				}
			}
		}

		if (object != null) {
			synchronized (mObjectListeners) {
				final Set<EventListener> set = mObjectListeners.get(object);

				if (set != null) {
					for (final EventListener l : set) {
						l.onEvent(subject, subjectId, verb, object, objectId);
					}
				}
			}
		}
	}

	@Override
	public void publishDelayed(long delay, final Entity subject, final String subjectId,
			final Verb verb, final Entity object, final String objectId) {
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				publish(subject, subjectId, verb, object, objectId);
			}
		}, delay);
	}

	@Override
	public void registerListenerForAny(EventListener listener) {
		mAnyListeners.add(listener);
	}

	@Override
	public synchronized void registerListenerForObject(Entity object,
			EventListener listener) {
		Set<EventListener> listeners = mObjectListeners.get(object);

		if (listeners == null) {
			listeners = new HashSet<EventListener>();
			mObjectListeners.put(object, listeners);
		}

		listeners.add(listener);
	}

	@Override
	public synchronized void registerListenerForSubject(Entity subject,
			EventListener listener) {
		Set<EventListener> listeners = mSubjectListeners.get(subject);

		if (listeners == null) {
			listeners = new HashSet<EventListener>();
			mSubjectListeners.put(subject, listeners);
		}

		listeners.add(listener);
	}

	@Override
	public synchronized void registerListenerForVerb(Verb verb,
			EventListener listener) {
		Set<EventListener> listeners = mVerbListeners.get(verb);

		if (listeners == null) {
			listeners = new HashSet<EventListener>();
			mVerbListeners.put(verb, listeners);
		}

		listeners.add(listener);
	}

	@Override
	public synchronized void unregisterListener(EventListener listener) {
		mAnyListeners.remove(listener);

		Collection<Set<EventListener>> values = mVerbListeners.values();

		for (final Set<EventListener> set : values) {
			set.remove(listener);
		}

		values = mSubjectListeners.values();

		for (final Set<EventListener> set : values) {
			set.remove(listener);
		}

		values = mObjectListeners.values();

		for (final Set<EventListener> set : values) {
			set.remove(listener);
		}
	}
}
