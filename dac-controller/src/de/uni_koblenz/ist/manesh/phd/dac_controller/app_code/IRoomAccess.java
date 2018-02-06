package de.uni_koblenz.ist.manesh.phd.dac_controller.app_code;

public interface IRoomAccess {

	void denyAccess(String personId, String doorId);

	void grantAccess(String personId, String doorId);

}