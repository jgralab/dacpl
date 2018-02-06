package com.dac.model_repository.app_model;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.model.AbstractModelManager;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraph;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.DacGraphFactory;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.eca_rules.Rules;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.DacGraphFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.EcaRulesFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features.FeaturesFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.persons.PersonsFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.rooms.RoomsFacadeImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.features.Features;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.DacGraphFactoryImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.impl.std.DacGraphImpl;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.persons.Persons;
import de.uni_koblenz.ist.manesh.phd.case_studies.dac.rooms.Rooms;
import de.uni_koblenz.ist.manesh.phd.mdbc.ModelConnection;
import de.uni_koblenz.ist.manesh.phd.mdbc.ModelQueryException;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.exception.GraphIOException;

/**
 * 
 * @author Mahdi Derakhshanmanesh
 */
public class ModelRepositoryModelManager extends AbstractModelManager {
	
	private InputStream getModelInputStream() throws IOException {
		/*
		 * TODO The location of the model is hard-coded in this example. Can be
		 * a generic implementation in core using some property! The default
		 * connection could be retrieved based on the file location.
		 */
		return getClass().getResourceAsStream("/res/room_model.tg");
	}

	@Override
	protected ModelConnection createDefaultModelConnection() throws IOException {
		DacGraphFactory f = new DacGraphFactoryImpl();
		DacGraphFacadeImpl.registerImplementationClasses(f);

		try {
			final DacGraph model = (DacGraph) GraphIO.loadGraphFromStream(getModelInputStream(), null, null, f,
					ImplementationType.STANDARD, null, DacGraphImpl.class.getClassLoader());

			return new ModelConnection() {
				@Override
				public void rollback() throws ModelQueryException {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean isReadOnly() throws ModelQueryException {
					return false;
				}

				@Override
				public boolean isClosed() throws ModelQueryException {
					return false;
				}

				@Override
				public <T> T getRawModel() throws ModelQueryException {
					return (T) model;
				}

				@Override
				public <T> T getRawMetaModel() throws ModelQueryException {
					return (T) model.getSchema();
				}

				@Override
				public void commit() throws ModelQueryException {
					throw new UnsupportedOperationException();
				}

				@Override
				public void close() throws ModelQueryException {
					throw new UnsupportedOperationException();
				}
			};
		} catch (GraphIOException e) {
			throw new IllegalStateException(e);
		}
	}

}
