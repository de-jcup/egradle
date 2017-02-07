package de.jcup.egradle.codeassist;

public interface Proposal extends Comparable<Proposal>{

	/**
	 * 
	 * @return name, never <code>null</code>
	 */
	String getLabel();

	/**
	 * @return code, never <code>null</code>
	 */
	String getCode();
	
	/**
	 * FIXME ATR, 07.02.2016: remove this if unnecessary! YAGNI
	 * @deprecated seems to be not used!
	 * @return type or <code>null</code>
	 */
	String getType();
	
	/**
	 * Returns tempalte used on apply
	 * @return template, never <code>null</code>
	 */
	String getTemplate();
	
	/**
	 * 
	 * @return description or <code>null</code>
	 */
	String getDescription();

	/**
	 * Returns cursor position or -1
	 * @return cursor position or -1
	 */
	int getCursorPos();
}