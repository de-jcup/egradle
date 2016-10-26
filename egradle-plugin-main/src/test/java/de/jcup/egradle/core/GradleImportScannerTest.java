package de.jcup.egradle.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.domain.GradleRootProject;
import static de.jcup.egradle.core.TestUtil.*;
import static org.junit.Assert.*;

public class GradleImportScannerTest {
	
	private GradleImportScanner scannerToTest;

	@Before
	public void before() throws IOException{
		scannerToTest = new GradleImportScanner();
	}
	
	@Test
	public void scan_with_null_returns_empty_list() {
		List<File> result = scannerToTest.scanEclipseProjectFolders(null);
		assertNotNull(result);
		assertEquals(0,result.size());
	}
	
	@Test
	public void rootfolder2_contains_two_eclipse_projects_but_not_the_none_eclipse_folders() {
		List<File> result = scannerToTest.scanEclipseProjectFolders(ROOTFOLDER_2);
		
		assertNotNull(result);
		assertTrue(result.contains(ROOTFOLDER_2_ECLIPSE_PROJECT1));
		assertTrue(result.contains(ROOTFOLDER_2_ECLIPSE_PROJECT2));
		
		assertFalse(result.contains(ROOTFOLDER_2_NO_ECLIPSE_PROJECT1));
		assertFalse(result.contains(ROOTFOLDER_2_NO_ECLIPSE_PROJECT2));
		
		assertEquals(2,result.size());
	}

}
