package de.jcup.egradle.core.domain;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.TestUtil;
import de.jcup.egradle.core.util.FileSupport;

public class GradleRootProjectTest {
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Test
	public void rootproject1_has_no_gradle_settings_and_so_identified_as_single_project__as_fallback() throws Exception {
		GradleRootProject root1 = new GradleRootProject(TestUtil.ROOTFOLDER_1);
		assertFalse(root1.isMultiProject());
	}
	@Test
	public void rootproject2_is_identified_as_multiproject() throws Exception {
		GradleRootProject root2 = new GradleRootProject(TestUtil.ROOTFOLDER_2);
		assertTrue(root2.isMultiProject());
	}
	
	@Test
	public void rootproject3_is_identified_as_singleproject()throws Exception {
		GradleRootProject root3 = new GradleRootProject(TestUtil.ROOTFOLDER_3);
		assertFalse(root3.isMultiProject());
	}
	
	@Test
	public void rootproject4_is_identified_as_singleproject_even_when_having_an_include_comment_inside ()throws Exception {
		GradleRootProject root4 = new GradleRootProject(TestUtil.ROOTFOLDER_4);
		assertFalse(root4.isMultiProject());
	}
	
	@Test
	public void add_subproject_to_gradle_multiproject_creates_subfolder_with_empty_build_gradle() throws Exception{
		/* prepare */
		File rootFolder = TestUtil.createTempTestFolder();
		
		FileSupport fileSupport = FileSupport.DEFAULT;
		fileSupport.createTextFile(rootFolder, "settings.gradle","include 'core'");
		GradleRootProject rootProject = new GradleRootProject(rootFolder);

		/* check precondition fulfilled*/
		assertTrue("precondition failed: not a multiproject?!", rootProject.isMultiProject());
		
		/* execute */
		rootProject.createNewSubProject("mySubProject");
	
		/* test */
		File subrpojectFolder = new File(rootFolder,"mySubProject");
		assertTrue(subrpojectFolder.exists());
		File subprojecBuildFile = new File(subrpojectFolder,"build.gradle");
		assertTrue(subprojecBuildFile.exists());
		
		assertEquals("", fileSupport.readTextFile(subprojecBuildFile));
	}

	@Test
	public void add_subproject_to_gradle_multiproject_having_a_core_subproject_adds_new_subproject_to_end_of_file_and_keeps_old_content() throws Exception{
		/* prepare */
		File rootFolder = TestUtil.createTempTestFolder();
		
		FileSupport fileSupport = FileSupport.DEFAULT;
		fileSupport.createTextFile(rootFolder, "settings.gradle","/*now we do an include...*/\ninclude 'core'\n/*fantastic...*/");
		GradleRootProject rootProject = new GradleRootProject(rootFolder);
		
		/* check precondition fulfilled*/
		assertTrue("precondition failed: not a multiproject?!", rootProject.isMultiProject());
		
		/* execute */
		rootProject.createNewSubProject("mySubProject");
		
		/* test */
		String content = fileSupport.readTextFile(new File(rootFolder,"settings.gradle"),"\n");
		assertEquals("/*now we do an include...*/\ninclude 'core'\n/*fantastic...*/\ninclude 'mySubProject'", content);
		
	}
	
	
}
