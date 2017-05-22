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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.junit.Test;

import de.jcup.egradle.core.util.FileUtil;

public class FileUtilTest {

	@Test
	public void test_copy_directories_works() throws Exception {
		/* prepare */
		long time = System.nanoTime();
		String content = "content-"+time;
		
		File tempFile = File.createTempFile("prefix-fileutil", "suffix-fileutil");
		File tempParent  = tempFile.getParentFile();
		File src = new File(tempParent, "fileutil-srcFolder-"+time);
		src.deleteOnExit();
		File dest = new File(tempParent, "fileutil-destFolder-"+time);
		dest.deleteOnExit();
		
		File subSrc= new File(src,"subfolder");
		subSrc.mkdirs();
		subSrc.deleteOnExit();
		File contentFile = new File(subSrc,"content.txt");
		try(FileWriter fw = new FileWriter(contentFile)){
			fw.write(content);
		}
		/* check preconditions*/
		assertFalse(dest.exists());
		assertTrue(subSrc.exists());
		assertTrue(contentFile.exists());
		
		File copiedSubSrc = new File(dest, subSrc.getName());
		File copiedContentFile = new File(copiedSubSrc, contentFile.getName());
		assertFalse(copiedSubSrc.exists());
		assertFalse(copiedContentFile.exists());
		
		/* execute */
		FileUtil.copyDirectories(src, dest);
		
		/* test */
		assertTrue(copiedSubSrc.exists());
		assertTrue(copiedContentFile.exists());
		try(BufferedReader br = new BufferedReader(new FileReader(copiedContentFile))){
			String read = br.readLine();
			assertEquals(content,read);
		}
	}

}
