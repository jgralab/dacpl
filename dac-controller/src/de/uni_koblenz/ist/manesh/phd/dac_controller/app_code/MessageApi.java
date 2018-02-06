/**
 * 
 */
package de.uni_koblenz.ist.manesh.phd.dac_controller.app_code;

import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.code.MoCoCodeObject;
import de.uni_koblenz.ist.manesh.moco.core.abstract_impl.MethodCategory;

/**
 * @author Mahdi Derakhshanmanesh
 *
 */
public class MessageApi extends MoCoCodeObject {
	
	@Export(category = MethodCategory.ACTION)
	public void print(String msg, String... arg) {
		System.out.printf(msg, (Object[]) arg);
	}

	// TODO Log method.

	// TODO Send Emails method.

}
