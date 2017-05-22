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
