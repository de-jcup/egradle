package de.jcup.egradle.template;

public enum Features implements Feature{

	NEW_PROJECT__TYPE_MULTI_PROJECT("feature.newproject.type.multiproject"),
	NEW_PROJECT__TYPE_SINGLE_PROJECT("feature.newproject.type.singleproject"),
	
	;
	
	private String id;
	
	private Features(String id){
		this.id=id;
	}
	
	public String getId(){
		return id;
	}
	
}
