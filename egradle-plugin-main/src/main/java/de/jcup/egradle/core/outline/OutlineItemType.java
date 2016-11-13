package de.jcup.egradle.core.outline;

public enum OutlineItemType {
	UNKNOWN,

	VARIABLE, 
	
	CLOSURE, 
	
	METHOD_CALL, 

	/* gradle special for parts created */
	TASK_SETUP, 
	
	TASK_CLOSURE, 
	
	APPLY_SETUP,

	APPLY_FROM,
	
	APPLY_PLUGIN, 
	
	
}
