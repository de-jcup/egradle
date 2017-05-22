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

/**
 * A provider for types
 * @author Albert Tregnaghi
 *
 */
public interface TypeProvider {

	/**
	 * Try to get a type for given name.
	 * @param name
	 * @return type or <code>null</code>
	 */
	public Type getType(String name);
}
