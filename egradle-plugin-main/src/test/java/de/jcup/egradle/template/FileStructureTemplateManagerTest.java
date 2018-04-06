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
package de.jcup.egradle.template;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.RootFolderProvider;
import de.jcup.egradle.core.util.LogAdapter;

public class FileStructureTemplateManagerTest {

	private FileStructureTemplateManager managerToTest;
	private RootFolderProvider mockedRootFolderProvider;

	@Before
	public void before() {
		mockedRootFolderProvider = mock(RootFolderProvider.class);
		managerToTest = new FileStructureTemplateManager(mockedRootFolderProvider, mock(LogAdapter.class));
	}

	@Test
	public void when_rootfolder_provider_returns_null_an_empty_template_list_will_be_returned() throws Exception {
		/* prepare */
		when(mockedRootFolderProvider.getRootFolder()).thenReturn(null);

		/* execute */
		List<FileStructureTemplate> fileStructureTemplates = managerToTest.getTemplates();

		/* test */
		assertNotNull(fileStructureTemplates);
		assertEquals(0, fileStructureTemplates.size());

	}

	@Test
	public void when_rootfolder_provider_returns_folder_but_folder_does_not_exist_an_empty_template_list_will_be_returned()
			throws Exception {
		/* prepare */
		when(mockedRootFolderProvider.getRootFolder()).thenReturn(new File("./i-am-not-existing-in-now-case"));

		/* execute */
		List<FileStructureTemplate> fileStructureTemplates = managerToTest.getTemplates();

		/* test */
		assertNotNull(fileStructureTemplates);
		assertEquals(0, fileStructureTemplates.size());

	}

	@Test
	public void for_a_rootfolder_containing_two_empty_subfolders_the_manager_returns_two_templates_with_names_and_description_not_null()
			throws Exception {
		/* prepare */
		File rootFolder = File.createTempFile("test-file-structure-", Long.toString(System.nanoTime()));
		rootFolder.delete();// was a file...
		rootFolder.mkdirs();
		rootFolder.deleteOnExit();

		File subFolder1 = new File(rootFolder, "template1");
		File subFolder2 = new File(rootFolder, "template2");

		subFolder1.mkdirs();
		subFolder2.mkdirs();

		when(mockedRootFolderProvider.getRootFolder()).thenReturn(rootFolder);

		/* execute */
		List<FileStructureTemplate> fileStructureTemplates = managerToTest.getTemplates();

		/* test */
		assertNotNull(fileStructureTemplates);
		assertEquals(2, fileStructureTemplates.size());
		List<File> templateFiles = new ArrayList<>();
		for (FileStructureTemplate template : fileStructureTemplates) {
			templateFiles.add(template.getPathToContent());
			assertNotNull(template.getName());
			assertNotNull(template.getDescription());
		}
		assertTrue(templateFiles.contains(subFolder1));
		assertTrue(templateFiles.contains(subFolder2));

	}

}
