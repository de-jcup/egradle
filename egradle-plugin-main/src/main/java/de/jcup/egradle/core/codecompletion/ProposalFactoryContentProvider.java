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
	 * @param cursorOffset index where cursor is
	 * @return relevant editor source code at given cursor position or <code>null</code>
	 */
	public String getEditorSourceEnteredAt(int cursorOffset);

	/**
	 * @param offset
	 * @return line of offset or -1
	 */
	public int getLineAt(int offset);
	
	/**
	 * 
	 * @param offset
	 * @return offset of first character in given line or -1
	 */
	public int getOffsetOfFirstCharacterInLine(int offset);

}
