package de.jcup.egradle.codeassist;

public interface LazyCodeBuilder {

	String getCode(AbstractProposalImpl proposal, String leadingText);

	int getCursorPos(AbstractProposalImpl proposal, String leadingText);
	
	/**
	 * 
	 * @return tempalte, never <code>null</code>
	 */
	String getTemplate();

}