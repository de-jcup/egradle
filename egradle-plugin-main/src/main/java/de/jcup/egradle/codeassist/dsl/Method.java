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
 package de.jcup.egradle.codeassist.dsl;

import java.util.List;

/**
 * Represents a method / function
 * @author Albert Tregnaghi
 *
 */
public interface Method extends LanguageElement, TypeChild, GradleDocumentationInfo, Comparable<Method>{

	public Type getReturnType();
	
	public String getReturnTypeAsString();
	
	
	public Type getDelegationTarget();
	
	/**
	 * Returns delegation target or <code>null</code>
	 * @return delegation target or <code>null</code>
	 */
	public String getDelegationTargetAsString();
	
	/**
	 * Returns parameters never <code>null</code>
	 * @return parameter list, never <code>null</code>
	 */
	public List<Parameter> getParameters();
	
}
