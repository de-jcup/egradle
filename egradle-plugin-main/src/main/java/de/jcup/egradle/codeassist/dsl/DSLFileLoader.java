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

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.jcup.egradle.codeassist.CodeCompletionService;

public interface DSLFileLoader extends CodeCompletionService{

	/**
	 * Loads DSL type for given full type name
	 * @param name name of type
	 * @return type
	 * @throws IOException
	 */
	Type loadType(String name) throws IOException;

	Set<Plugin> loadPlugins() throws IOException;

	Map<String, String> loadApiMappings() throws IOException;
	
}
