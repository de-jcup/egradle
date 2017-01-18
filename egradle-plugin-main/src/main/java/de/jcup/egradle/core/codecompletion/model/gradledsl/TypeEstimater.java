package de.jcup.egradle.core.codecompletion.model.gradledsl;

import de.jcup.egradle.core.codecompletion.model.Type;

public interface TypeEstimater {

	/**
	 * Estimate type at given position  
	 * @param offset
	 * @return type or <code>null</code> if not resolveable
	 */
	public Type estimate(int offset);
}