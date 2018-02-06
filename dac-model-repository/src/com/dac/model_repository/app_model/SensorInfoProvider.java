/**
 *
 */
package com.dac.model_repository.app_model;

import java.io.IOException;

import com.dac.model_repository.port.function.ISensorInfo;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.MoCoModelObject;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.jgralab.greql.GreqlEnvironment;
import de.uni_koblenz.jgralab.greql.GreqlQuery;
import de.uni_koblenz.jgralab.greql.evaluator.GreqlEnvironmentAdapter;

/**
 * @author Mahdi Derakhshanmanesh
 *
 */
public class SensorInfoProvider extends MoCoModelObject implements ISensorInfo {
	@Export
	@Override
	public String getDoorIdForCardReaderId(String sensorId) {
		try {
			final DacGraph model = getDefaultModelConnection().getRawModel();

			synchronized (model) {
				// Build query with parameter.
				final GreqlQuery q = GreqlQuery
						.createQuery("using sName : theElement(from s : V{rooms.CardReaderSensor}, d : V{rooms.Door} with s.uniqueName = sName and d-->{rooms.HasCardReaderSensor}s reportSet d.uniqueName end)");
				final GreqlEnvironment env = new GreqlEnvironmentAdapter();
				env.setVariable("sName", sensorId);
				return (String) q.evaluate(model, env);
			}
		} catch (final IOException e) {
			throw new IllegalStateException(
					"Could not get a valid model connection", e);
		}
	}

	@Export
	@Override
	public String getProductId(String sensorId) {
		try {
			final DacGraph model = getDefaultModelConnection().getRawModel();

			synchronized (model) {
				// Build query with parameter.
				final GreqlQuery q = GreqlQuery
						.createQuery("using pName : theElement(from s : V{rooms.Sensor} with s.uniqueName = pName report s.productId end)");

				final GreqlEnvironment env = new GreqlEnvironmentAdapter();
				env.setVariable("pName", sensorId);
				return q.evaluate(model, env).toString();
			}
		} catch (final IOException e) {
			throw new IllegalStateException(
					"Could not get a valid model connection", e);
		}
	}

	@Export
	@Override
	public String getRoomIdForRoomSensorId(String sensorId) {
		try {
			final DacGraph model = getDefaultModelConnection().getRawModel();

			synchronized (model) {
				// Build query with parameter.
				final GreqlQuery q = GreqlQuery
						.createQuery("using sName : theElement(from s : V{rooms.RoomSensor}, r : V{rooms.Room} with s.uniqueName = sName and r-->{rooms.HasRoomSensor}s reportSet r.uniqueName end)");
				final GreqlEnvironment env = new GreqlEnvironmentAdapter();
				env.setVariable("sName", sensorId);
				return q.evaluate(model, env).toString();
			}
		} catch (final IOException e) {
			throw new IllegalStateException(
					"Could not get a valid model connection", e);
		}
	}
}
