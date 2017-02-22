package de.jcup.egradle.sdk.builder.util;

import de.jcup.egradle.codeassist.dsl.Method;

/**
 * A special visitor which will estimate/calculte/use delegation target of a method
 * @author Albert Tregnaghi
 *
 */
public interface DelegationTargetMethodVisitor {

	/**
	 * Visits method
	 * @param method
	 * @return delegation target for this method or <code>null</code> if not resolveable
	 */
	public String getDelegationTargetAsString(Method method);
}
