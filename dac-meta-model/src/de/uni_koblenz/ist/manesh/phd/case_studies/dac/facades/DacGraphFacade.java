package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.NamedElement;

public interface DacGraphFacade {
	public NamedElement findNamedElementByName(String name);
	
	void plotModelState();

	void popModelState();

	void pushModelState();
}
