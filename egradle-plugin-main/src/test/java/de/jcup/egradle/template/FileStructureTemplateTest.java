package de.jcup.egradle.template;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class FileStructureTemplateTest {

	private Properties properties;
	private File contentRootFolder;
	private FileStructureTemplate templateToTest;
	private String time;

	@Before
	public void before() throws Exception{
		time = Long.toString(System.nanoTime());
		
		contentRootFolder = File.createTempFile("test-file-structure-", time);
		contentRootFolder.delete();// was a file...
		contentRootFolder.mkdirs();
		contentRootFolder.deleteOnExit();
		
		properties = mock(Properties.class);
		
		templateToTest = new FileStructureTemplate(contentRootFolder, properties);
	}

	@Test
	public void apply_from_to_target_copies_textfile1() throws Exception {
		/* prepare */
		String content = "textfile content -  "+time;

		File textFile1 = new File(contentRootFolder, "textfile1.txt");
		textFile1.createNewFile();
		try(FileWriter fw = new FileWriter(textFile1)){
			fw.write(content);
		}
		
		File targetFolder = File.createTempFile("test-file-structure-target", Long.toString(System.nanoTime()));
		targetFolder.delete();// was a file...
		
		/* execute */
		templateToTest.applyTo(targetFolder);
		
		/* test */
		File[] files = targetFolder.listFiles();
		assertNotNull(files);
		assertEquals(1,files.length);
		assertEquals(new File(targetFolder,"textfile1.txt"),files[0]);
		
		
	}
	
	@Test
	public void apply_from_to_target_copies_underscore_textfile1_to_textfile1() throws Exception {
		/* prepare */
		String content = "textfile content -  "+time;

		File textFile1 = new File(contentRootFolder, "_textfile1.txt");
		textFile1.createNewFile();
		try(FileWriter fw = new FileWriter(textFile1)){
			fw.write(content);
		}
		
		File targetFolder = File.createTempFile("test-file-structure-target", Long.toString(System.nanoTime()));
		targetFolder.delete();// was a file...
		
		/* execute */
		templateToTest.applyTo(targetFolder);
		
		/* test */
		File[] files = targetFolder.listFiles();
		assertNotNull(files);
		assertEquals(1,files.length);
		assertEquals(new File(targetFolder,"textfile1.txt"),files[0]);
		
		
	}
}
