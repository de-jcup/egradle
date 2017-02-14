package de.jcup.egradle.eclipse.plugin.sdk.internal.util;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.junit.Test;

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
