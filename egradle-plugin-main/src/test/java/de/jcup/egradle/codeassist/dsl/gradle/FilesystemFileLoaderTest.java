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
 package de.jcup.egradle.codeassist.dsl.gradle;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.codeassist.dsl.ApiMappingImporter;
import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader;
import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader.DSLFolderNotSetException;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLPlugins;
import de.jcup.egradle.codeassist.dsl.XMLPluginsImporter;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.codeassist.dsl.XMLTypeImporter;
import de.jcup.egradle.core.TestUtil;
public class FilesystemFileLoaderTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private File dslFolder;
	private XMLTypeImporter mockedTypeImporter;
	private XMLPluginsImporter mockedPluginsImporter;
	private ApiMappingImporter mockedApiMappingImporter;
	
	@Before
	public void before(){
		dslFolder = new File(TestUtil.SRC_TEST_RES_FOLDER,"dsl/3.0");
		mockedTypeImporter = mock(XMLTypeImporter.class);
		mockedPluginsImporter = mock(XMLPluginsImporter.class);
		mockedApiMappingImporter = mock(ApiMappingImporter.class);
	}
	

	public void filesystem_loader_constructor_with_null_apiImporter_importers_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		new FilesystemFileLoader(mockedTypeImporter, mockedPluginsImporter, null);
	}
	
	@Test
	public void filesystem_loader_constructor_with_null_typeImporter_importers_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		new FilesystemFileLoader(null, mockedPluginsImporter, mockedApiMappingImporter);
	}
	
	@Test
	public void filesystem_loader_constructor_with_null_pluginImporter_importers_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		new FilesystemFileLoader(mockedTypeImporter, null, mockedApiMappingImporter);
	}
	
	//testfile_type_Action_xml__is_loaded_for_type_Action_and_contains_data
	@Test
	public void calls_xmlimporter_and_returns_importer_result() throws Exception {
		/* prepare */
		FilesystemFileLoader loaderToTest = new FilesystemFileLoader(mockedTypeImporter,mockedPluginsImporter, mockedApiMappingImporter);
		loaderToTest.setDSLFolder(dslFolder);
		
		XMLType mockedType = mock(XMLType.class);
		when(mockedTypeImporter.importType(any(InputStream.class))).thenReturn(mockedType);
		XMLPlugins xmlPlugins = mock(XMLPlugins.class);
		when(xmlPlugins.getPlugins()).thenReturn(Collections.emptySet());
		when(mockedPluginsImporter.importPlugins(any(InputStream.class))).thenReturn(xmlPlugins);
		
		/* execute */
		Type type = loaderToTest.loadType("org.gradle.api.TestAction");
		
		/* test */
		assertEquals(mockedType, type);
		
	}
	
	@Test
	public void throws_file_notfound_exception_when_file_not_available() throws Exception {
		/* prepare */
		FilesystemFileLoader loaderToTest = new FilesystemFileLoader(mockedTypeImporter, mockedPluginsImporter, mockedApiMappingImporter);
		loaderToTest.setDSLFolder(dslFolder);
		XMLType mockedType = mock(XMLType.class);
		when(mockedTypeImporter.importType(any(InputStream.class))).thenReturn(mockedType);
		XMLPlugins xmlPlugins = mock(XMLPlugins.class);
		when(xmlPlugins.getPlugins()).thenReturn(Collections.emptySet());
		when(mockedPluginsImporter.importPlugins(any(InputStream.class))).thenReturn(xmlPlugins);
		
		/* test -after*/
		expectedException.expect(FileNotFoundException.class);
		
		/* execute */
		loaderToTest.loadType("somewhere.over.the.rainbow.ActionNotExisting");
	}
	
	@Test
	public void throws_io_exception_when_dslfolder_not_set() throws Exception {
		/* prepare */
		FilesystemFileLoader loaderToTest = new FilesystemFileLoader(mockedTypeImporter, mockedPluginsImporter, mockedApiMappingImporter);
		loaderToTest.setDSLFolder(null);
		

		/* test -after*/
		expectedException.expect(DSLFolderNotSetException.class);
		
		/* execute */
		Type result = loaderToTest.loadType("somewhere.over.the.rainbow.ActionNotExisting");
		assertNull(result);
	
	}

}
