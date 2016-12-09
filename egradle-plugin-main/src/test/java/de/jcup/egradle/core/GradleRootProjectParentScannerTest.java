package de.jcup.egradle.core;

import static de.jcup.egradle.core.TestUtil.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
public class GradleRootProjectParentScannerTest {

	private static final int DEFAULT_TEST_DEEPNESS = 3;
	private GradleRootProjectParentScanner scannerToTest;

	@Before
	public void before(){
		scannerToTest = new GradleRootProjectParentScanner();
	}
	
	@Test
	public void scanForParentRootProject_ROOTFOLDER_2_ECLIPSE_PROJECT2_README__root_project_is_root_folder2() {
		/* execute */
		File result = scannerToTest.scanForParentRootProject(ROOTFOLDER_2_ECLIPSE_PROJECT2_README,DEFAULT_TEST_DEEPNESS);
		
		/* test */
		assertEquals(ROOTFOLDER_2,result);
		
	}
	
	@Test
	public void scanForParentRootProject_ROOTFOLDER_2_ECLIPSE_PROJECT2_README__root_project_is_not_NULL_for_infinite_deepness() {
		/* execute */
		File result = scannerToTest.scanForParentRootProject(ROOTFOLDER_2_ECLIPSE_PROJECT2_README,-1);
		
		/* test */
		assertEquals(ROOTFOLDER_2,result);
		
	}
	
	@Test
	public void scanForParentRootProject_ROOTFOLDER_2_ECLIPSE_PROJECT2_README__root_project_is_NULL_WHEN_DEEPNESS_IS_1() {
		/* execute */
		File result = scannerToTest.scanForParentRootProject(ROOTFOLDER_2_ECLIPSE_PROJECT2_README,1);
		
		/* test */
		assertEquals(ROOTFOLDER_2,result);
		
	}
	
	@Test
	public void scanForParentRootProject_ROOTFOLDER_2_ECLIPSE_PROJECT2__root_project_is_root_folder2() {
		/* execute */
		File result = scannerToTest.scanForParentRootProject(ROOTFOLDER_2_ECLIPSE_PROJECT2,DEFAULT_TEST_DEEPNESS);
		
		/* test */
		assertEquals(ROOTFOLDER_2,result);
	}
	
	@Test
	public void scanForParentRootProject_ROOTFOLDER_2__root_project_is_root_folder2() {
		/* execute */
		File result = scannerToTest.scanForParentRootProject(ROOTFOLDER_2,DEFAULT_TEST_DEEPNESS);
		
		/* test */
		assertEquals(ROOTFOLDER_2,result);
	}
	
	@Test
	public void scanForParentRootProject_ROOTFOLDER_2_GET_PARENT__root_project_is_NULL() {
		/* execute */
		File result = scannerToTest.scanForParentRootProject(ROOTFOLDER_2.getParentFile(),DEFAULT_TEST_DEEPNESS);
		
		/* test */
		assertEquals(null,result);
	}
	
	@Test
	public void scanForParentRootProject_FILE_NOT_EXISTING__root_project_is_NULL() {
		/* execute */
		File result = scannerToTest.scanForParentRootProject(new File("./i-will-not-exist-in-this-context..............."),DEFAULT_TEST_DEEPNESS);
		
		/* test */
		assertEquals(null,result);
	}
	
	@Test
	public void scanForParentRootProject_NULL__root_project_is_NULL() {
		/* execute */
		File result = scannerToTest.scanForParentRootProject(null,3);
		
		/* test */
		assertEquals(null,result);
	}

}
