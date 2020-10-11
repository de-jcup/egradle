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
package de.jcup.egradle.core.virtualroot;

import static de.jcup.egradle.core.TestUtil.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.core.domain.GradleRootProject;

public class VirtualProjectCreatorTest {

    private static final File TEST2_TXT_FILE = new File(ROOTFOLDER_1, "test2.txt");
    private static final File TEST1_TXT_FILE = new File(ROOTFOLDER_1, "test1.txt");

    private static final File SUBFOLDER1 = new File(ROOTFOLDER_1, "subfolder1");
    private static final File SUBFOLDER2 = new File(ROOTFOLDER_1, "subfolder2");

    private static final File SUBFOLDER2_1 = new File(SUBFOLDER2, "subfolder2-1");

    private VirtualProjectCreator creatorToTest;
    private VirtualProjectPartCreator mockedPartCreator;
    private GradleRootProject mockedRootProject;

    @Before
    public void before() {
        creatorToTest = new VirtualProjectCreator();

        mockedPartCreator = mock(VirtualProjectPartCreator.class);
        mockedRootProject = mock(GradleRootProject.class);

    }

    @Test
    public void visitor_is_NOT_called_to_createOrUpdateProject_when_root_project_folder_is_null() throws Exception {
        /* prepare */
        when(mockedRootProject.getFolder()).thenReturn(null);

        /* execute */
        creatorToTest.createOrUpdate(mockedRootProject, mockedPartCreator);

        /* test */
        verify(mockedPartCreator, never()).createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME);
    }

    @Test
    public void visitor_is_NOT_called_to_createOrUpdateProject_when_root_project_folder_is_not_null_but_does_not_exist() throws Exception {
        /* prepare */
        File mockRootFolder = mock(File.class);
        when(mockRootFolder.exists()).thenReturn(false);

        when(mockedRootProject.getFolder()).thenReturn(mockRootFolder);

        /* execute */
        creatorToTest.createOrUpdate(mockedRootProject, mockedPartCreator);

        /* test */
        verify(mockedPartCreator, never()).createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME);

    }

    @Test
    public void visitor_is_called_to_createOrUpdateProject_when_root_project_folder_is_not_null_and_exists() throws Exception {
        /* prepare */
        File mockRootFolder = mock(File.class);
        when(mockRootFolder.exists()).thenReturn(true);

        when(mockedRootProject.getFolder()).thenReturn(mockRootFolder);
        when(mockRootFolder.exists()).thenReturn(true);
        when(mockedPartCreator.createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME)).thenReturn(new Object());

        /* execute */
        creatorToTest.createOrUpdate(mockedRootProject, mockedPartCreator);

        /* test */
        verify(mockedPartCreator).createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME);

    }

    @Test
    public void creator_creates_all_files_of_rootfolder() throws Exception {
        /* prepare */
        when(mockedRootProject.getFolder()).thenReturn(ROOTFOLDER_1);
        when(mockedPartCreator.isLinkCreationNeeded(any(), any())).thenReturn(true);
        when(mockedPartCreator.createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME)).thenReturn(new Object());

        /* execute */
        creatorToTest.createOrUpdate(mockedRootProject, mockedPartCreator);

        /* test */
        verify(mockedPartCreator).createLink(any(), eq(TEST1_TXT_FILE));
        verify(mockedPartCreator).createLink(any(), eq(TEST2_TXT_FILE));
    }

    @Test
    public void creator_creates_all_direct_subfolders_of_rootfolder_but_not_subsubfolders_when_visitor_accepts_all() throws Exception {
        /* prepare */
        when(mockedRootProject.getFolder()).thenReturn(ROOTFOLDER_1);
        when(mockedPartCreator.isLinkCreationNeeded(any(), any())).thenReturn(true);
        when(mockedPartCreator.createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME)).thenReturn(new Object());

        /* execute */
        creatorToTest.createOrUpdate(mockedRootProject, mockedPartCreator);

        /* test */
        verify(mockedPartCreator).createLink(any(), eq(SUBFOLDER1));
        verify(mockedPartCreator).createLink(any(), eq(SUBFOLDER2));
    }

    @Test
    public void creator_creates_not_subsubfolders_when_visitor_accepts_all() throws Exception {
        /* prepare */
        when(mockedRootProject.getFolder()).thenReturn(ROOTFOLDER_1);
        when(mockedPartCreator.isLinkCreationNeeded(any(), any())).thenReturn(true);
        when(mockedPartCreator.createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME)).thenReturn(new Object());

        /* execute */
        creatorToTest.createOrUpdate(mockedRootProject, mockedPartCreator);

        /* test */
        verify(mockedPartCreator, never()).createLink(any(), eq(SUBFOLDER2_1));
    }

    @Test
    public void creator_creates_only_subfolder1_of_rootfolder_but_not_subsubfolders_when_visitor_accepts_subfolder1_only() throws Exception {
        /* prepare */
        when(mockedRootProject.getFolder()).thenReturn(ROOTFOLDER_1);
        when(mockedPartCreator.isLinkCreationNeeded(any(), eq(SUBFOLDER1))).thenReturn(true);
        when(mockedPartCreator.isLinkCreationNeeded(any(), eq(SUBFOLDER2))).thenReturn(false);
        when(mockedPartCreator.createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME)).thenReturn(new Object());

        /* execute */
        creatorToTest.createOrUpdate(mockedRootProject, mockedPartCreator);

        /* test */
        verify(mockedPartCreator).createLink(any(), eq(SUBFOLDER1));
        verify(mockedPartCreator, never()).createLink(any(), eq(SUBFOLDER2));
    }
}
