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
package de.jcup.egradle.codeassist.dsl;

public interface ModifiableType extends Type {

	/**
	 * Mixin methods and properties from given mixin type
	 * 
	 * @param mixinType
	 * @param reason
	 */
	public void mixin(Type mixinType, Reason reason);

	/**
	 * Add an extension to this type
	 * 
	 * @param extensionId
	 * @param extensionType
	 * @param reason
	 */
	public void addExtension(String extensionId, Type extensionType, Reason reason);

	/**
	 * Inherit methods and properties from super type. If this type is an
	 * interface it can only extend interface types, otherwise it can only
	 * extend from (one) super type. The method does not throw any error when
	 * trying to do a wrong extension but does nothing
	 * 
	 * @param superType
	 */
	public void extendFrom(Type superType);

	/**
	 * Set <code>true</code> when this type is part of official gradle DSL
	 * documentation
	 * 
	 * @param partOfGradleDSLDocumentation
	 */
	public void setDocumented(boolean partOfGradleDSLDocumentation);

	public void setDescription(String description);

}
