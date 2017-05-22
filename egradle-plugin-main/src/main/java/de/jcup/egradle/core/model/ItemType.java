/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.core.model;

public enum ItemType {
	UNKNOWN,

	VARIABLE, 
	
	ENUM_CONSTANT,

	/* nearly same as a variable but has no modifiers*/
	ASSIGNMENT, 
	
	CLOSURE, 
	
	METHOD_CALL, 

	/* gradle special for parts created */
	TASKS, 

	TASK, 
	
	APPLY_SETUP,

	APPLY_FROM,
	
	APPLY_PLUGIN, 
	
	REPOSITORIES, 
	
	ALL_PROJECTS, 
	
	SUB_PROJECTS, 
	
	PROJECT,
	
	DEPENDENCIES, 
	
	DEPENDENCY, 

	MAIN, 
	
	TEST, 
	
	CLEAN, 
	
	BUILDSCRIPT, 

	SOURCESETS, 
	
	CONFIGURATIONS, 
	
	REPOSITORY, 
	
	ECLIPSE, 

	ENUM,
	
	INTERFACE,
	
	CLASS, 

	JAR, 
	
	WAR, 
	
	EAR, 
	
	ZIP,
	
	CONFIGURE,
	
	PACKAGE, 
	
	IMPORT, 
	
	DO_FIRST, 
	
	DO_LAST, 
	
	AFTER_EVALUATE,
	
	/**
	 * A method (defintion)
	 */
	METHOD, 
	
	CONSTRUCTOR, 
	
	 
	
	
	
}
