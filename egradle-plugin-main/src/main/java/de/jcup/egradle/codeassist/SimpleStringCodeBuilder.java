package de.jcup.egradle.codeassist;

public class SimpleStringCodeBuilder implements LazyCodeBuilder{

	private String code;
	
	public SimpleStringCodeBuilder(String code){
		this.code=code;
	}

	@Override
	public String getCode(AbstractProposalImpl proposal, String leadingText) {
		if (code==null){
			return "";
		}
		return code;
	}

	@Override
	public int getCursorPos(AbstractProposalImpl proposal, String leadingText) {
		return -1;
	}

	@Override
	public String getTemplate() {
		return code;
	}

}
