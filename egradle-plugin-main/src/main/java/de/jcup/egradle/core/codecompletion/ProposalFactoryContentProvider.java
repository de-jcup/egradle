package de.jcup.egradle.core.codecompletion;

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
	 * @return line of offset or -1
	 */
	public int getLineAtCursorPosition();
	
	/**
	 * 
	 * @return offset of first character in given line or -1
	 */
	public int getOffsetOfFirstCharacterInLine();

	/**
	 * @return column of given offset
	 */
	public String getLineTextBeforeCursorPosition();

}
