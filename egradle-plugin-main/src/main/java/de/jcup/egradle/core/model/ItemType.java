/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
