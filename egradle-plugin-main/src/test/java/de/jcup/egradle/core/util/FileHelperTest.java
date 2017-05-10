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
 package de.jcup.egradle.core.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.util.FileHelper;

public class FileHelperTest {
	
	private FileHelper fileHelpertoTest;
	
	@Before
	public void before(){
		fileHelpertoTest = new FileHelper();
	}
	
	
	@Test
	public void null_is_subfolder_of_null_returns_false() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(null,null));
	}
	
	@Test
	public void folder_is_subfolder_of_null_returns_false() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(new File("folder"),null));
	}
	
	@Test
	public void null_is_subfolder_of_folder_returns_false() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(null,new File("folder")));
	}

	@Test
	public void current_parent_childfolder__is_subfolder_of_current_parent() {
		assertTrue(fileHelpertoTest.isDirectSubFolder(new File("./parent/childfolder"), new File("./parent")));
	}
	
	@Test
	public void current_parent1_childfolder__is_NOT_subfolder_of_current_parent2() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(new File("./parent1/childfolder"), new File("./parent2")));
	}
	
	@Test
	public void parent_childfolder__is_subfolder_of_parent() {
		assertTrue(fileHelpertoTest.isDirectSubFolder(new File("parent/childfolder"), new File("parent")));
	}
	
	@Test
	public void parent_childfolderA_childfolderA2__is_subfolder_of_parent() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(new File("parent/childfolderA/childfolderA2"), new File("parent")));
	}
	
	@Test
	public void parent1_childfolder__is_NOT_subfolder_of_parent2() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(new File("parent1/childfolder"), new File("parent2")));
	}
	
	@Test
	public void parent1_childfolder__is_NOT_subfolder_of_parent2_childfolder() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(new File("parent1/childfolder"), new File("parent2/childfolder")));
	}
	
	@Test
	public void parent_childfolder1__is_NOT_subfolder_of_parent_childfolder2() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(new File("parent/childfolder1"), new File("parent/childfolder2")));
	}
	
	@Test
	public void parent__is_NOT_subfolder_of_parent() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(new File("parent"), new File("parent")));
	}
	
	@Test
	public void parent__is_NOT_subfolder_of_parent_childfolder() {
		assertFalse(fileHelpertoTest.isDirectSubFolder(new File("parent"), new File("parent/childfolder")));
	}

}
