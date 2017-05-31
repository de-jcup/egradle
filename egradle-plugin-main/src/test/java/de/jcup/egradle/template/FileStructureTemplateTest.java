package de.jcup.egradle.template;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.util.DirectoryCopySupport;
import de.jcup.egradle.core.util.FileSupport;
import de.jcup.egradle.template.FileStructureTemplate.TemplateContentTransformerFactory;

public class FileStructureTemplateTest {

	private Properties properties;
	private File contentRootFolder;
	private FileStructureTemplate templateToTest;

	@Rule
	public ExpectedException expected = ExpectedException.none();
	private FileSupport mockedFileSupport;
	private DirectoryCopySupport mockedCopySupport;
	private TemplateContentTransformer mockedContentTransformer;
	private TemplateContentTransformerFactory mockedContentTransformerFactory;
	
	@Before
	public void before() throws Exception{
		
		contentRootFolder = mock(File.class);
		properties = mock(Properties.class);
		mockedContentTransformer = mock(TemplateContentTransformer.class);
		
		mockedContentTransformerFactory = mock(TemplateContentTransformerFactory.class);
		when(mockedContentTransformerFactory.createTemplateContentTransformer(any(Properties.class))).thenReturn(mockedContentTransformer);

		templateToTest = new FileStructureTemplate("name", contentRootFolder,"description");
		
		mockedCopySupport = mock(DirectoryCopySupport.class);
		mockedFileSupport = mock(FileSupport.class);
		
		templateToTest.copySupport=mockedCopySupport;
		templateToTest.fileSupport=mockedFileSupport;
		templateToTest.contentTransformerFactory=mockedContentTransformerFactory;
	}
	
	@Test
	public void apply_from__null_throws_IllegalArgument() throws Exception{
		expected.expect(IllegalArgumentException.class);
		
		/* execute */
		templateToTest.applyTo(null,properties);
		
	}
	@Test
	public void a_template_does_create_per_default_a_content_transformer_factory_which_does_create_a_transformer(){
		/* prepare */
		Properties p = new Properties();
		templateToTest = new FileStructureTemplate("name", contentRootFolder,"description");

		/* check preconditions */
		assertNotNull(templateToTest.contentTransformerFactory);
		
		/* execute */
		TemplateContentTransformer transformer = templateToTest.contentTransformerFactory.createTemplateContentTransformer(p);
		
		/* test */
		assertNotNull(transformer);
	}
	
	@Test
	public void a_template_does_call_the_content_transformer_factory_with_given_properties() throws Exception{
		/* prepare */
		File mockedTargetFolder = buildDirectoryMock();
		Properties p = new Properties();
		
		/* execute */
		templateToTest.applyTo(mockedTargetFolder, p);
		
		/* test */
		verify(mockedContentTransformerFactory).createTemplateContentTransformer(p);
	}
	
	@Test
	public void apply_from__new_target_file_calls_copy_support_with_an_template_file_name_transformer() throws Exception{
		/* prepare */
		File mockedTargetFolder = buildDirectoryMock();
		
		/* execute */
		templateToTest.applyTo(mockedTargetFolder,properties);
		
		/* test */
		verify(mockedCopySupport).copyDirectories(eq(contentRootFolder), eq(mockedTargetFolder), any(TemplateFileNameTransformer.class), eq(Boolean.TRUE));
		
	}

	private File buildDirectoryMock() {
		File mockedTargetFolder= mock(File.class);
		when(mockedTargetFolder.isDirectory()).thenReturn(true);
		when(mockedTargetFolder.listFiles()).thenReturn(new File[]{});
		return mockedTargetFolder;
	}
	
	@Test
	public void apply_from__new_target_file_calls_file_support_to_write_file() throws Exception{
		/* prepare */
		File mockedTargetFile= mock(File.class);
		File mockedTargetFolder = mock(File.class);
		
		when(mockedTargetFolder.listFiles()).thenReturn(new File[]{mockedTargetFile});
		when(mockedTargetFolder.isDirectory()).thenReturn(true);

		when(mockedTargetFile.isFile()).thenReturn(true);

		when(mockedContentTransformer.transform(any())).thenReturn("");
		
		/* execute */
		templateToTest.applyTo(mockedTargetFolder,properties);
		
		/* test */
		verify(mockedFileSupport).writeTextFile(eq(mockedTargetFile),any(String.class)); 
		
	}
	
}
