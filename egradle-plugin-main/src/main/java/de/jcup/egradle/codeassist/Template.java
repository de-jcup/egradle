package de.jcup.egradle.codeassist;

public class Template {

	private String name;
	private String content;
	
	
	
	public Template(String name, String content) {
		this.name = name;
		this.content = content;
	}

	public String getName(){
		return name;
	}
	
	public String getContent() {
		return content;
	}
	

}
