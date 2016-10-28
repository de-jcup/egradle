package de.jcup.egradle.core;

import static de.jcup.egradle.core.TestUtil.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GradleImportScannerTest {
	
	private GradleImportScanner scannerToTest;

	@Before
	public void before() throws IOException{
		scannerToTest = new GradleImportScanner();
	}
	
	@Test
	public void scanEclipseProjectFolders_rootfolder3_result_contains_only_single_project_itself(){
		List<File> result = scannerToTest.scanEclipseProjectFolders(ROOTFOLDER_3);
		assertNotNull(result);
		assertTrue(result.contains(ROOTFOLDER_3));
		assertEquals(1,result.size());
	}

	
	@Test
	public void scanEclipseProjectFolders_null__result_is_an_empty_list() {
		List<File> result = scannerToTest.scanEclipseProjectFolders(null);
		assertNotNull(result);
		assertEquals(0,result.size());
	}
	
	@Test
	public void scanEclipseProjectFolders_rootfolder2__result_contains_two_eclipse_projects_folders_but_not_the_none_eclipse_folders() {
		List<File> result = scannerToTest.scanEclipseProjectFolders(ROOTFOLDER_2);
		
		assertNotNull(result);
		assertTrue(result.contains(ROOTFOLDER_2_ECLIPSE_PROJECT1));
		assertTrue(result.contains(ROOTFOLDER_2_ECLIPSE_PROJECT2));
		
		assertFalse(result.contains(ROOTFOLDER_2_NO_ECLIPSE_PROJECT1));
		assertFalse(result.contains(ROOTFOLDER_2_NO_ECLIPSE_PROJECT2));
		assertFalse(result.contains(ROOTFOLDER_2)); // root folder2 DOES contain a .project. But being root project it will be ignored because imports will use virtual root project instead!
		
		assertEquals(2,result.size());
	}

}
