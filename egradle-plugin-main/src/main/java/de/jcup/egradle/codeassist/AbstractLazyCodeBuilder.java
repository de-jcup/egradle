package de.jcup.egradle.codeassist;

import de.jcup.egradle.codeassist.SourceCodeInsertionSupport.InsertionData;

public abstract class AbstractLazyCodeBuilder implements LazyCodeBuilder {

	private static SourceCodeInsertionSupport insertSupport = new SourceCodeInsertionSupport();
	private String code;
	private String template;
	private int cursorPos;

	public String getCode(AbstractProposalImpl proposal, String textBeforeColumn) {
		ensureData(proposal, textBeforeColumn);
		return code;
	}

	private void ensureData(AbstractProposalImpl proposal, String textBeforeColumn) {
		ensureTemplate();
		if (code == null) {
			InsertionData insertData = insertSupport.prepareInsertionString(template, textBeforeColumn);

			this.cursorPos = insertData.cursorOffset;
			this.code = insertData.sourceCode;

			if (code == null) {
				code = "";
			}
		}

	}
	

	public String getTemplate() {
		ensureTemplate();
		return template;
	}
	
	private void ensureTemplate() {
		if (template==null){
			template=createTemplate();
			if (template==null){
				template="";
			}
		}
	}

	protected abstract String createTemplate();

	@Override
	public int getCursorPos(AbstractProposalImpl proposal, String textBeforeColumn) {
		ensureData(proposal, textBeforeColumn);
		return cursorPos;
	}

}
