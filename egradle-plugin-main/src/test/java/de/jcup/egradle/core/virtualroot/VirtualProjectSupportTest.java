package de.jcup.egradle.core.virtualroot;

import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.core.domain.GradleRootProject;

public class VirtualProjectSupportTest {

	private static File PARENT_OF_TEST = new File("egradle-plugin-main/src/test/res/");
	static {
		if (!PARENT_OF_TEST.exists()) {
			/*
			 * fall back - to be testable by eclipse in sub projects and also
			 * via gradle from root project.
			 */
			PARENT_OF_TEST = new File("src/test/res/");
		}
	}
	private static final File ROOTFOLDER_1 = new File(PARENT_OF_TEST, "rootproject1");
	private static final File TEST2_TXT_FILE = new File(ROOTFOLDER_1,"test2.txt");
	private static final File TEST1_TXT_FILE = new File(ROOTFOLDER_1,"test1.txt");
	
	private static final File SUBFOLDER1 = new File(ROOTFOLDER_1,"subfolder1");
	private static final File SUBFOLDER2 = new File(ROOTFOLDER_1,"subfolder2");
	
	private static final File SUBFOLDER2_1 = new File(SUBFOLDER2,"subfolder2-1");
	
	private static final File TEST1_1_TXT_FILE = new File(SUBFOLDER1,"test1-1.txt");
	private static final File TEST1_2_TXT_FILE = new File(SUBFOLDER1,"test1-2.txt");
	
	private static final File TEST2_1_TXT_FILE = new File(SUBFOLDER1,"test2-1.txt");
	private static final File TEST2_2_TXT_FILE = new File(SUBFOLDER1,"test2-2.txt");
	
	private static final File TEST2_1_1_TXT_FILE = new File(SUBFOLDER2_1,"test2-1-1.txt");
	
	private VirtualProjectSupport supportToTest;
	private RootProjectVisitor mockedVisitor;
	private GradleRootProject mockedRootProject;

	@Before
	public void before() {
		supportToTest = new VirtualProjectSupport();
		mockedVisitor = mock(RootProjectVisitor.class);

		mockedRootProject = mock(GradleRootProject.class);

	}

	@Test
	public void visitor_is_NOT_called_to_createOrUpdateProject_when_root_project_folder_is_null() throws Exception {
		/* prepare */
		when(mockedRootProject.getFolder()).thenReturn(null);

		/* execute */
		supportToTest.createOrUpdateVirtualRootProject(mockedRootProject, mockedVisitor);

		/* test */
		verify(mockedVisitor,never()).createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME);
	}

	@Test
	public void visitor_is_NOT_called_to_createOrUpdateProject_when_root_project_folder_is_not_null_but_does_not_exist() throws Exception {
		/* prepare */
		File mockRootFolder = mock(File.class);
		when(mockRootFolder.exists()).thenReturn(false);
		
		when(mockedRootProject.getFolder()).thenReturn(mockRootFolder);

		/* execute */
		supportToTest.createOrUpdateVirtualRootProject(mockedRootProject, mockedVisitor);

		/* test */
		verify(mockedVisitor,never()).createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME);

	}
	
	@Test
	public void visitor_is_called_to_createOrUpdateProject_when_root_project_folder_is_not_null_and_exists() throws Exception {
		/* prepare */
		File mockRootFolder = mock(File.class);
		when(mockRootFolder.exists()).thenReturn(true);
		
		when(mockedRootProject.getFolder()).thenReturn(mockRootFolder);

		/* execute */
		supportToTest.createOrUpdateVirtualRootProject(mockedRootProject, mockedVisitor);

		/* test */
		verify(mockedVisitor).createOrRecreateProject(Constants.VIRTUAL_ROOTPROJECT_NAME);

	}

	@Test
	public void visitor_visits_all_files_of_rootfolder() throws Exception {
		/* prepare */
		when(mockedRootProject.getFolder()).thenReturn(ROOTFOLDER_1);

		/* execute */
		supportToTest.createOrUpdateVirtualRootProject(mockedRootProject, mockedVisitor);

		/* test */
		verify(mockedVisitor).createLink(any(), eq(TEST1_TXT_FILE));
		verify(mockedVisitor).createLink(any(), eq(TEST2_TXT_FILE));
	}

	@Test
	public void visitor_visits_all_direct_subfolders_of_rootfolder_but_not_subsubfolders_when_visitor_accepts_all() throws Exception {
		/* prepare */
		when(mockedRootProject.getFolder()).thenReturn(ROOTFOLDER_1);
		when(mockedVisitor.needsFolderToBeCreated(any(), any())).thenReturn(true);
		/* execute */
		supportToTest.createOrUpdateVirtualRootProject(mockedRootProject, mockedVisitor);

		/* test */
		verify(mockedVisitor).createLink(any(), eq(SUBFOLDER1));
		verify(mockedVisitor).createLink(any(), eq(SUBFOLDER2));
	}
	
	@Test
	public void visitor_visits_not_subsubfolders_when_visitor_accepts_all() throws Exception {
		/* prepare */
		when(mockedRootProject.getFolder()).thenReturn(ROOTFOLDER_1);
		when(mockedVisitor.needsFolderToBeCreated(any(), any())).thenReturn(true);
		/* execute */
		supportToTest.createOrUpdateVirtualRootProject(mockedRootProject, mockedVisitor);

		/* test */
		verify(mockedVisitor,never()).createLink(any(), eq(SUBFOLDER2_1));
	}
	
	@Test
	public void visitor_visits_only_subfolder1_of_rootfolder_but_not_subsubfolders_when_visitor_accepts_subfolder1_only() throws Exception {
		/* prepare */
		when(mockedRootProject.getFolder()).thenReturn(ROOTFOLDER_1);
		when(mockedVisitor.needsFolderToBeCreated(any(), eq(SUBFOLDER1))).thenReturn(true);
		when(mockedVisitor.needsFolderToBeCreated(any(), eq(SUBFOLDER2))).thenReturn(false);
		/* execute */
		supportToTest.createOrUpdateVirtualRootProject(mockedRootProject, mockedVisitor);

		/* test */
		verify(mockedVisitor).createLink(any(), eq(SUBFOLDER1));
		verify(mockedVisitor,never()).createLink(any(), eq(SUBFOLDER2));
	}
}
