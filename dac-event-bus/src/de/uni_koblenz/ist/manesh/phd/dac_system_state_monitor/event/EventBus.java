package de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event;

/**
 * Interface for event bus that is used throughout the DAC application for
 * communicating person, door and sensor-related events.
 *
 * @author ti
 */
public interface EventBus {
	/**
	 * Publishes an event following a subject-verb-object phrase pattern.
	 *
	 * @param subject
	 *            Subject entity type.
	 * @param subjectId
	 *            Subject entity unique identifier.
	 * @param verb
	 *            The verb being performed by subject.
	 * @param object
	 *            Optional object entity type (may be null).
	 * @param objectId
	 *            Optional object entitiy unique identifier (may be null).
	 */
	void publish(Entity subject, String subjectId, Verb verb, Entity object,
			String objectId);

	/**
	 * Publishes a delayed event following a subject-verb-object phrase pattern.
	 *
	 * @param delay
	 *            Event delay in milliseconds.
	 * @param subject
	 *            Subject entity type.
	 * @param subjectId
	 *            Subject entity unique identifier.
	 * @param verb
	 *            The verb being performed by subject.
	 * @param object
	 *            Optional object entity type (may be null).
	 * @param objectId
	 *            Optional object entitiy unique identifier (may be null).
	 */
	void publishDelayed(long delay, Entity subject, String subjectId,
			Verb verb, Entity object, String objectId);

	void registerListenerForAny(EventListener listener);

	/**
	 * Registers an event listener for an object {@link Entity}.
	 *
	 * @param subject
	 *            Object entity.
	 * @param listener
	 *            Event listener.
	 */
	void registerListenerForObject(Entity object, EventListener listener);

	/**
	 * Registers an event listener for a subject {@link Entity}.
	 *
	 * @param subject
	 *            Subject entity.
	 * @param listener
	 *            Event listener.
	 */
	void registerListenerForSubject(Entity subject, EventListener listener);

	/**
	 * Registers an event listener for a {@link Verb}.
	 *
	 * @param verb
	 *            Verb to listen to.
	 * @param listener
	 *            Event listener.
	 */
	void registerListenerForVerb(Verb verb, EventListener listener);

	/**
	 * Unregisters an event listener.
	 *
	 * @param listener
	 *            Event listener to unregister.
	 */
	void unregisterListener(EventListener listener);
}
