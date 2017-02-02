package de.jcup.egradle.codeassist.dsl.gradle;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.codeassist.dsl.ApiMappingImporter;
import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader;
import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader.DSLFolderNotSetException;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLDSLPluginsImporter;
import de.jcup.egradle.codeassist.dsl.XMLDSLTypeImporter;
import de.jcup.egradle.core.TestUtil;
public class FilesystemFileLoaderTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private File dslFolder;
	private XMLDSLTypeImporter mockedTypeImporter;
	private XMLDSLPluginsImporter mockedPluginsImporter;
	private ApiMappingImporter mockedApiMappingImporter;
	
	@Before
	public void before(){
		dslFolder = new File(TestUtil.PARENT_OF_TEST,"dsl/3.0");
		mockedTypeImporter = mock(XMLDSLTypeImporter.class);
		mockedPluginsImporter = mock(XMLDSLPluginsImporter.class);
		mockedApiMappingImporter = mock(ApiMappingImporter.class);
	}
	

	public void filesystem_loader_constructor_with_null_apiImporter_importers_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		new FilesystemFileLoader(mockedTypeImporter, mockedPluginsImporter, null);
	}
	
	@Test
	public void filesystem_loader_constructor_with_null_typeImporter_importers_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		new FilesystemFileLoader(null, mockedPluginsImporter, mockedApiMappingImporter);
	}
	
	@Test
	public void filesystem_loader_constructor_with_null_pluginImporter_importers_throws_illegal_argument() {
		expectedException.expect(IllegalArgumentException.class);
		new FilesystemFileLoader(mockedTypeImporter, null, mockedApiMappingImporter);
	}
	
	//testfile_type_Action_xml__is_loaded_for_type_Action_and_contains_data
	@Test
	public void calls_xmlimporter_and_returns_importer_result() throws Exception {
		/* prepare */
		FilesystemFileLoader loaderToTest = new FilesystemFileLoader(mockedTypeImporter,mockedPluginsImporter, mockedApiMappingImporter);
		loaderToTest.setDSLFolder(dslFolder);
		
		Type mockedType = mock(Type.class);
		when(mockedTypeImporter.importType(any(InputStream.class))).thenReturn(mockedType);
		when(mockedPluginsImporter.importPlugins(any(InputStream.class))).thenReturn(Collections.emptySet());
		
		/* execute */
		Type type = loaderToTest.loadType("org.gradle.api.TestAction");
		
		/* test */
		assertEquals(mockedType, type);
		
	}
	
	@Test
	public void throws_file_notfound_exception_when_file_not_available() throws Exception {
		/* prepare */
		FilesystemFileLoader loaderToTest = new FilesystemFileLoader(mockedTypeImporter, mockedPluginsImporter, mockedApiMappingImporter);
		loaderToTest.setDSLFolder(dslFolder);
		Type mockedType = mock(Type.class);
		when(mockedTypeImporter.importType(any(InputStream.class))).thenReturn(mockedType);
		when(mockedPluginsImporter.importPlugins(any(InputStream.class))).thenReturn(Collections.emptySet());
		
		/* test -after*/
		expectedException.expect(FileNotFoundException.class);
		
		/* execute */
		loaderToTest.loadType("somewhere.over.the.rainbow.ActionNotExisting");
	}
	
	@Test
	public void throws_io_exception_when_dslfolder_not_set() throws Exception {
		/* prepare */
		FilesystemFileLoader loaderToTest = new FilesystemFileLoader(mockedTypeImporter, mockedPluginsImporter, mockedApiMappingImporter);
		loaderToTest.setDSLFolder(null);
		

		/* test -after*/
		expectedException.expect(DSLFolderNotSetException.class);
		
		/* execute */
		Type result = loaderToTest.loadType("somewhere.over.the.rainbow.ActionNotExisting");
		assertNull(result);
	
	}

}
