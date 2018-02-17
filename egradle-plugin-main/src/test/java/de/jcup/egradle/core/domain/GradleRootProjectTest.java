package de.jcup.egradle.core.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import de.jcup.egradle.core.TestUtil;

public class GradleRootProjectTest {
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

}
