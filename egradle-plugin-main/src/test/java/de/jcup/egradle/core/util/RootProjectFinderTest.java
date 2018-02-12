package de.jcup.egradle.core.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.TestUtil;

public class RootProjectFinderTest {

	private RootProjectFinder finderToTest;
	
	
	@Before
	public void before(){
		finderToTest= new RootProjectFinder();
	}
	
	@Test
	public void rootfolder_4_test1_gradle_file_has_rootfolder_4_as_root_folder() {
		File folder = finderToTest.findRootProjectFolder(TestUtil.ROOTFOLDER_4_TEST1_GRADLE);
		assertEquals(TestUtil.ROOTFOLDER_4,folder);
	}
	
	@Test
	public void rootfolder_2_no_eclipse_project2_readme_has_rootfolder2() {
		File folder = finderToTest.findRootProjectFolder(TestUtil.ROOTFOLDER_2_NO_ECLIPSE_PROJECT2_README);
		assertEquals(TestUtil.ROOTFOLDER_2,folder);
	}
	
	@Test
	public void rootfolder1_parent_has_null_gradle_rootfolder() {
		File folder = finderToTest.findRootProjectFolder(TestUtil.ROOTFOLDER_1.getParentFile());
		assertNull(folder);
	}

}
