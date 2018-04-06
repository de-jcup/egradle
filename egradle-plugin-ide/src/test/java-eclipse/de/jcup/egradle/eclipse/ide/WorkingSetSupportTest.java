/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.eclipse.ide;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.eclipse.ide.WorkingSetSupport.WorkingSetData;

public class WorkingSetSupportTest {

	private WorkingSetSupport supportToTest;
	private IWorkingSetManager mockedManager;
	private IWorkingSet mockedWorkingSet1;
	private IProject mockedProject1;
	private IProject mockedProject2;
	private IAdaptable mockedProject1Adaptable;
	private IAdaptable mockedProject2Adaptable;
	private IProject mockedProject3;
	private IAdaptable mockedProject3Adaptable;
	private IWorkingSet mockedWorkingSet2;

	@Before
	public void before() {
		supportToTest = new WorkingSetSupport();
		mockedManager = mock(IWorkingSetManager.class);

		mockedWorkingSet1 = mock(IWorkingSet.class, "workingset1");
		mockedWorkingSet2 = mock(IWorkingSet.class, "workingset2");

		mockedProject1 = mock(IProject.class, "project1");
		mockedProject1Adaptable = mock(IAdaptable.class, "project1-adapter");

		mockedProject2 = mock(IProject.class, "project2");
		mockedProject2Adaptable = mock(IAdaptable.class, "project2-adapter");

		mockedProject3 = mock(IProject.class, "project3");
		mockedProject3Adaptable = mock(IAdaptable.class, "project3-adapter");

		when(mockedProject1.getName()).thenReturn("project1");
		when(mockedProject2.getName()).thenReturn("project2");
		when(mockedProject3.getName()).thenReturn("project3");

		when(mockedProject1Adaptable.getAdapter(IProject.class)).thenReturn(mockedProject1);
		when(mockedProject2Adaptable.getAdapter(IProject.class)).thenReturn(mockedProject2);
		when(mockedProject3Adaptable.getAdapter(IProject.class)).thenReturn(mockedProject3);

	}

	@Test
	public void resolve__manager_null_returns_not_null() {
		assertNotNull(supportToTest.resolveWorkingSetsForProjects(new ArrayList<>(), null));
	}

	@Test
	public void resolve__projects_null_returns_empty_list() {
		assertEquals(new ArrayList<>(), supportToTest.resolveWorkingSetsForProjects(null, mockedManager));
	}

	@Test
	public void resolve__workingset_1_contains_project1__so_one_entry_for_workingset_is_returned() {
		/* prepare */
		ArrayList<IProject> projects = new ArrayList<>();
		projects.add(mockedProject1);
		projects.add(mockedProject2);

		when(mockedManager.getAllWorkingSets()).thenReturn(new IWorkingSet[] { mockedWorkingSet1 });
		when(mockedWorkingSet1.getElements()).thenReturn(new IAdaptable[] { mockedProject1Adaptable });

		/* execute */
		List<WorkingSetData> workingSetDataList = supportToTest.resolveWorkingSetsForProjects(projects, mockedManager);

		/* test */
		assertNotNull(workingSetDataList);
		assertEquals(1, workingSetDataList.size());
		WorkingSetData data = workingSetDataList.iterator().next();

		assertEquals(mockedWorkingSet1, data.workingSet);
		assertTrue(data.projectNamesContainedBefore.contains("project1"));
		assertFalse(data.projectNamesContainedBefore.contains("project2"));

	}

	@Test
	public void resolve__workingset_1_contains_project1__workingset2_contains_project2__so_two_entries_with_data_returned() {
		/* prepare */
		ArrayList<IProject> projects = new ArrayList<>();
		projects.add(mockedProject1);
		projects.add(mockedProject2);

		when(mockedManager.getAllWorkingSets()).thenReturn(new IWorkingSet[] { mockedWorkingSet1, mockedWorkingSet2 });
		when(mockedWorkingSet1.getElements()).thenReturn(new IAdaptable[] { mockedProject1Adaptable });
		when(mockedWorkingSet2.getElements()).thenReturn(new IAdaptable[] { mockedProject1Adaptable });

		/* execute */
		List<WorkingSetData> workingSetDataList = supportToTest.resolveWorkingSetsForProjects(projects, mockedManager);

		/* test */
		assertNotNull(workingSetDataList);
		assertEquals(2, workingSetDataList.size());
		Iterator<WorkingSetData> iterator = workingSetDataList.iterator();
		WorkingSetData data1 = iterator.next();
		WorkingSetData data2 = iterator.next();

		assertEquals(mockedWorkingSet1, data1.workingSet);
		assertTrue(data1.projectNamesContainedBefore.contains("project1"));

		assertEquals(mockedWorkingSet2, data2.workingSet);
		assertFalse(data2.projectNamesContainedBefore.contains("project2"));

	}

	@Test
	public void restore__workingset_1__will_have_project_1_after_restore() {
		/* prepare */
		ArrayList<IProject> projects = new ArrayList<>();
		projects.add(mockedProject1);
		projects.add(mockedProject2);

		List<WorkingSetData> workingSetDataList = new ArrayList<>();
		WorkingSetData data = new WorkingSetData();
		data.projectNamesContainedBefore.add("project1");
		data.workingSet = mockedWorkingSet1;
		workingSetDataList.add(data);

		/* execute */
		supportToTest.restoreWorkingSetsForProjects(workingSetDataList, projects, mockedManager);

		/* test */
		verify(mockedWorkingSet1).setElements(eq(new IAdaptable[] { mockedProject1 }));
	}

	@Test
	public void restore__workingset_1__will_have_project_1_and_2_after_restore() {
		/* prepare */
		ArrayList<IProject> projects = new ArrayList<>();
		projects.add(mockedProject1);
		projects.add(mockedProject2);

		List<WorkingSetData> workingSetDataList = new ArrayList<>();
		WorkingSetData data = new WorkingSetData();
		data.projectNamesContainedBefore.add("project1");
		data.projectNamesContainedBefore.add("project2");
		data.workingSet = mockedWorkingSet1;

		workingSetDataList.add(data);

		/* execute */
		supportToTest.restoreWorkingSetsForProjects(workingSetDataList, projects, mockedManager);

		/* test */
		verify(mockedWorkingSet1).setElements(eq(new IAdaptable[] { mockedProject1, mockedProject2 }));
	}

	@Test
	public void restore__workingset_1__will_have_project_1_and_2__and_contains_project_3_which_was_added_before__after_restore() {
		/* prepare */
		ArrayList<IProject> projects = new ArrayList<>();
		projects.add(mockedProject1);
		projects.add(mockedProject2);

		when(mockedWorkingSet1.getElements()).thenReturn(new IAdaptable[] { mockedProject3Adaptable });

		List<WorkingSetData> workingSetDataList = new ArrayList<>();
		WorkingSetData data = new WorkingSetData();
		data.projectNamesContainedBefore.add("project1");
		data.projectNamesContainedBefore.add("project2");
		data.workingSet = mockedWorkingSet1;

		workingSetDataList.add(data);

		/* execute */
		supportToTest.restoreWorkingSetsForProjects(workingSetDataList, projects, mockedManager);

		/* test */
		verify(mockedWorkingSet1)
				.setElements(eq(new IAdaptable[] { mockedProject3Adaptable, mockedProject1, mockedProject2 }));
	}

	@Test
	public void restore__workingset_1__will_have_project_1_and_2__and_contains_project_2_and_3_which_was_added_before__after_restore() {
		/* prepare */
		ArrayList<IProject> projects = new ArrayList<>();
		projects.add(mockedProject1);
		projects.add(mockedProject2);

		when(mockedWorkingSet1.getElements())
				.thenReturn(new IAdaptable[] { mockedProject3Adaptable, mockedProject2Adaptable });

		List<WorkingSetData> workingSetDataList = new ArrayList<>();
		WorkingSetData data = new WorkingSetData();
		data.projectNamesContainedBefore.add("project1");
		data.projectNamesContainedBefore.add("project2");
		data.workingSet = mockedWorkingSet1;

		workingSetDataList.add(data);

		/* execute */
		supportToTest.restoreWorkingSetsForProjects(workingSetDataList, projects, mockedManager);

		/* test */
		verify(mockedWorkingSet1)
				.setElements(eq(new IAdaptable[] { mockedProject3Adaptable, mockedProject2Adaptable, mockedProject1 }));
	}

}
