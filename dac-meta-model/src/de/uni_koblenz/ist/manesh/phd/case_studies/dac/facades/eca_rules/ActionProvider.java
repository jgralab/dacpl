package de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules;

public interface ActionProvider {
	void onAction(String name, Object... args) throws NoSuchMethodException, Throwable;
}
