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
 package de.jcup.egradle.integration.test;

import static de.jcup.egradle.integration.TypeAssert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.integration.IntegrationTestComponents;
public class TypeImporterIntegrationTest {

	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();
	private FilesystemFileLoader fileLoader;

	@Before
	public void before(){
		fileLoader = components.getFileLoader();
	}
	
	@Test
	public void load_project_and_repository_handler_not_mixed_in__do_mixin_and_check_correct() throws Exception{
		Type projectType = fileLoader.loadType("org.gradle.api.Project");
		Type repositoryHandlerType = fileLoader.loadType("org.gradle.api.artifacts.dsl.RepositoryHandler");
		
		/* check preconditions */
		assertType(projectType).hasName("org.gradle.api.Project");
		assertType(projectType).hasNotMethod("flatDir", "groovy.lang.Closure");
		assertType(repositoryHandlerType).hasName("org.gradle.api.artifacts.dsl.RepositoryHandler");
		assertType(repositoryHandlerType).hasMethod("flatDir", "groovy.lang.Closure");

		/* execute */
		XMLType projectXMLType = (XMLType) projectType;
		projectXMLType.mixin(repositoryHandlerType, null);
		
		/* test */
		assertType(projectType).hasMethod("flatDir", "groovy.lang.Closure");
		
	}
	
	

}
