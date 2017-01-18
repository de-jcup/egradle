package de.jcup.egradle.core.codecompletion.model.gradledsl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import de.jcup.egradle.core.TestUtil;
import de.jcup.egradle.core.codecompletion.model.Type;
import static org.mockito.Mockito.*;
public class FilesystemFileLoaderTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private File dslFolder;
	private XMLDSLTypeImporter mockedImporter;
	
	@Before
	public void before(){
		dslFolder = new File(TestUtil.PARENT_OF_TEST,"dsl/3.0");
		mockedImporter = mock(XMLDSLTypeImporter.class);
	}
	
	@Test
	public void filesystem_loader_constructor_with_null_folder_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		new FilesystemFileLoader(null,mockedImporter);
	}
	
	@Test
	public void filesystem_loader_constructor_with_null_importer_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		new FilesystemFileLoader(dslFolder,null);
	}
	//testfile_type_Action_xml__is_loaded_for_type_Action_and_contains_data
	@Test
	public void calls_xmlimporter_and_returns_importer_result() throws Exception {
		/* prepare */
		FilesystemFileLoader loaderToTest = new FilesystemFileLoader(dslFolder,mockedImporter);
		Type mockedType = mock(Type.class);
		when(mockedImporter.importType(any(InputStream.class))).thenReturn(mockedType);
		
		/* execute */
		Type type = loaderToTest.load("org.gradle.api.TestAction");
		
		/* test */
		assertEquals(mockedType, type);
		
	}
	
	@Test
	public void throws_file_notfound_exception_when_file_not_available() throws Exception {
		/* prepare */
		FilesystemFileLoader loaderToTest = new FilesystemFileLoader(dslFolder,mockedImporter);
		Type mockedType = mock(Type.class);
		when(mockedImporter.importType(any(InputStream.class))).thenReturn(mockedType);

		/* test -after*/
		expectedException.expect(FileNotFoundException.class);
		
		/* execute */
		loaderToTest.load("somewhere.over.the.rainbow.ActionNotExisting");
	}

}
