package de.jcup.egradle.core.model;

public enum ItemType {
	UNKNOWN,

	VARIABLE, 
	
	CLOSURE, 
	
	METHOD_CALL, 

	/* gradle special for parts created */
	TASKS, 

	TASK, 
	
	TASK_CLOSURE, 
	
	APPLY_SETUP,

	APPLY_FROM,
	
	APPLY_PLUGIN, 
	
	REPOSITORIES, 
	
	ALL_PROJECTS, 
	
	SUB_PROJECTS, 
	
	DEPENDENCIES, 
	
	DEPENDENCY, 

	MAIN, 
	
	TEST, 
	
	CLEAN, 
	
	BUILDSCRIPT, 

	SOURCESETS, 
	
	CONFIGURATIONS, 
	
	REPOSITORY, 
	
	CLASS, 

	JAR, 
	
	PACKAGE, 
	
	IMPORT, 
	
	DO_FIRST, 
	
	DO_LAST, 
	
	
	
	
	
	
}
