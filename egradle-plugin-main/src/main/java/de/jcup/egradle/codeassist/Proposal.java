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
 package de.jcup.egradle.codeassist;

public interface Proposal extends Comparable<Proposal>{

	/**
	 * 
	 * @return name, never <code>null</code>
	 */
	String getLabel();

	/**
	 * @return code, never <code>null</code>
	 */
	String getCode();
	
	/**
	 * Returns tempalte used on apply
	 * @return template, never <code>null</code>
	 */
	String getTemplate();
	
	/**
	 * 
	 * @return description or <code>null</code>
	 */
	String getDescription();

	/**
	 * Returns cursor position or -1
	 * @return cursor position or -1
	 */
	int getCursorPos();
}