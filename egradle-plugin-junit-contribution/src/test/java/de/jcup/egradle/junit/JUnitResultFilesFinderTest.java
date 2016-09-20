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
 package de.jcup.egradle.junit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class JUnitResultFilesFinderTest {

	private static File PARENT_OF_TEST = new File("egradle-plugin-junit-contribution/src/test/res/");
	static{
		if (! PARENT_OF_TEST.exists()){
			/* fall back - to be testable von eclipse in sub projects and also from root project.*/
			PARENT_OF_TEST = new File("src/test/res/");
		}
	}
	private static final File ROOTFOLDER_1 = new File(PARENT_OF_TEST,"rootproject1");
	private static final File ROOTFOLDER_EMPTY = new File(PARENT_OF_TEST,"rootproject-empty");
	private JUnitResultFilesFinder collectorToTest;

	@Before
	public void before() {
		collectorToTest = new JUnitResultFilesFinder();
	}
	
	@Test(expected=IOException.class)
	public void test_throws_filenotfound_exception_when_directory_not_exists() throws IOException  {
		assertNotNull(collectorToTest.findTestFilesInRootProjectFolder(new File("./nirvana/never"+System.currentTimeMillis())));
	}
	
	@Test
	public void test_empty_collection_when_directory_empty_but_exists()  throws IOException {
		Collection<File> collectTestFilesFromRootProjectFolder = collectorToTest.findTestFilesInRootProjectFolder(ROOTFOLDER_EMPTY);
		assertNotNull(collectTestFilesFromRootProjectFolder);
		assertTrue(collectTestFilesFromRootProjectFolder.isEmpty());
	}
	
	@Test
	public void test_all_three_testfiles_are_collected()  throws IOException {
		Collection<File> result = collectorToTest.findTestFilesInRootProjectFolder(ROOTFOLDER_1);
		assertNotNull(result);
		
		assertEquals("Expected 3 results but got:"+result.size(), 3, result.size());
		
	}

}
