package de.jcup.egradle.codeassist;

import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.core.model.Model;

/**
 * Provides content necessary for proposal factories
 * @author albert
 *
 */
public interface ProposalFactoryContentProvider {

	/**
	 * @return model
	 */
	public Model getModel();

	/**
	 * @return relevant editor source code at given cursor position or <code>null</code>
	 */
	public String getEditorSourceEnteredAtCursorPosition();

	/**
	 * 
	 * @return offset of first character in given line or -1
	 */
	public int getOffsetOfFirstCharacterInLine();

	/**
	 * @return column of given offset
	 */
	public String getLineTextBeforeCursorPosition();

	public GradleFileType getFileType();

}