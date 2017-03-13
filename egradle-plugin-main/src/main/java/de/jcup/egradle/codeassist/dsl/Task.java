package de.jcup.egradle.codeassist.dsl;

public interface Task extends Comparable<Task>{

	public String getName();
	
	public Type getType();
	
	public String getTypeAsString();
}
