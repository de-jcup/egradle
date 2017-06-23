package de.jcup.egradle.core.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class GradleInfoSupportTest {

	private static final String BUILD_GRADLE = "build.gradle";
	private static final String SETTINGS_GRADLE = "settings.gradle";
	private GradleInfoSupport supportToTest;

	@Before
	public void before() {
		supportToTest = new GradleInfoSupport();
	}

	@Test
	public void multiProject__a_folder_containing_gradle_settings_is_recognized_as_root_project_folder() {
		/* prepare */
		File mockedFolder = createMockedFolder("mockedFolder");
		when(mockedFolder.listFiles()).thenReturn(new File[] { new File(SETTINGS_GRADLE) });

		/* execute */
		boolean isGradleRootProjectFolder = supportToTest.isGradleRootProjectFolder(mockedFolder);

		/* test */
		assertTrue(isGradleRootProjectFolder);
	}

	@Test
	public void singleProject__a_folder_containing_only_build_gradle_and_parent_folder_has_no_gradle_settings_inside_is_recognized_as_root_project_folder() {
		/* prepare */
		File mockedParentFolder = createMockedFolder("mockedParentFolder");
		File mockedFolder = createMockedFolder("mockedFolder");

		when(mockedParentFolder.listFiles()).thenReturn(new File[] { mockedFolder });
		when(mockedFolder.getParentFile()).thenReturn(mockedParentFolder);

		when(mockedFolder.listFiles()).thenReturn(new File[] { new File(BUILD_GRADLE) });

		/* execute */
		boolean isGradleRootProjectFolder = supportToTest.isGradleRootProjectFolder(mockedFolder);

		/* test */
		assertTrue(isGradleRootProjectFolder);
	}

	@Test
	public void multiProject__a_folder_containing_only_build_gradle_and_parent_folder_has_gradle_settings_inside_is_NOT_recognized_as_root_project_folder() {
		/* prepare */
		File mockedParentFolder = createMockedFolder("mockedParentFolder");
		File mockedFolder = createMockedFolder("mockedFolder");

		when(mockedParentFolder.listFiles()).thenReturn(new File[] { mockedFolder, new File(SETTINGS_GRADLE) });

		when(mockedFolder.listFiles()).thenReturn(new File[] { new File(BUILD_GRADLE) });
		when(mockedFolder.getParentFile()).thenReturn(mockedParentFolder);

		/* execute */
		boolean isGradleRootProjectFolder = supportToTest.isGradleRootProjectFolder(mockedFolder);

		/* test */
		assertFalse(isGradleRootProjectFolder);
	}

	@Test
	public void multiProject__a_folder_containing_no_files_is_not_recognized_as_root_project_folder() {
		/* prepare */
		File mockedFolder = createMockedFolder("mockedFolder");
		when(mockedFolder.listFiles()).thenReturn(new File[] {});

		/* execute */
		boolean isGradleRootProjectFolder = supportToTest.isGradleRootProjectFolder(mockedFolder);

		/* test */
		assertFalse(isGradleRootProjectFolder);
	}

	private File createMockedFolder(String name) {
		File mockedFolder = mock(File.class,name);
		when(mockedFolder.exists()).thenReturn(true);
		when(mockedFolder.isDirectory()).thenReturn(true);
		return mockedFolder;
	}

}
