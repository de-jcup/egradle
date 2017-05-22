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

import java.util.Map;
import java.util.Set;

/**
 * Represents a type. In java or groovy this can be a class, an interface , an enum etc.
 * @author Albert Tregnaghi
 *
 */
public interface Type extends LanguageElement, GradleDocumentationInfo, Comparable<Type>{

	/**
	 * Return properties (defined + inherited), never <code>null</code>
	 * @return properties, never <code>null</code>
	 */
	public Set<Property> getProperties();
	
	/**
	 * Return defined properties, never <code>null</code>
	 * @return properties, never <code>null</code>
	 */
	public Set<Property> getDefinedProperties();
	
	/**
	 * Return methods (defined + inherited), never <code>null</code>
	 * @return methods, never <code>null</code>
	 */
	public Set<Method> getMethods();
	
	/**
	 * Return defined method (not inherited), never <code>null</code>
	 * @return methods, never <code>null</code>
	 */
	public Set<Method> getDefinedMethods();

	/**
	 * Return interfaces as type reference set, never <code>null</code>.
	 * Only direct inherited interface types will be returned. Not full hierarchy!
	 * @return set, never <code>null</code>
	 */
	public Set<TypeReference> getInterfaces();

	/**
	 * 
	 * @return a short/simple name
	 */
	public String getShortName();
	
	public Map<String, Type> getExtensions();
	
	/**
	 * Returns the reason for given extension id
	 * @param extensionId
	 * @return reason or <code>null</code> if no reason specified
	 */
	public Reason getReasonForExtension(String extensionId);
	
	/**
	 * Returns reason for element. A reason will contain information about plugin or super type.
	 * reason will be null if element is not contained, or element is defined in type itself.
	 * @param element
	 * @return reason or <code>null</code> when no reason is specified
	 */
	public Reason getReasonFor(LanguageElement element);

	/**
	 * Returns the version for this type
	 * @return version, never null. If not version set "current" will be returned
	 */
	public String getVersion();

	/**
	 * Returns super type as string or <code>null</code>
	 * @return name of super type or <code>null</code>
	 */
	public String getSuperTypeAsString();
	
	public Type getSuperType();

	public boolean isImplementingInterface(String type);
	
	public boolean isDescendantOf(String type);
	
	public boolean isInterface();


}
