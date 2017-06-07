package de.jcup.egradle.template;

public enum Features implements Feature{

	NEW_PROJECT__TYPE_MULTI_PROJECT("feature.newproject.type.multiproject"),
	
	NEW_PROJECT__SUPPORTS_JAVA("feature.newproject.supports.java"),
	
	NEW_PROJECT__SUPPORTS_GRADLEWRAPPER("feature.newproject.supports.gradlewrapper"),
	
	NEW_PROJECT__SUPPORTS_HEADLESS_IMPORT("feature.newproject.supports.headlessimport")
	;
	
	private String id;
	
	private Features(String id){
		this.id=id;
	}
	
	public String getId(){
		return id;
	}
	
}
