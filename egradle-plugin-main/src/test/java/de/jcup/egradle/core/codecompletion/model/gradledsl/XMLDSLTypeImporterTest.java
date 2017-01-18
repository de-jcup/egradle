package de.jcup.egradle.core.codecompletion.model.gradledsl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
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
public class XMLDSLTypeImporterTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private File dslFolder;
	private XMLDSLTypeImporter importerToTest;
	private File actionXMLFile;
	
	@Before
	public void before(){
		dslFolder = new File(TestUtil.PARENT_OF_TEST,"dsl/3.0");
		actionXMLFile = new File(dslFolder,"org/gradle/api/Action.xml");
		importerToTest=new XMLDSLTypeImporter();
	}
	
	@Test
	public void testfile_type_Action_xml__is_imported_for_type_Action_and_contains_data() throws Exception {
		/* execute */
		Type type = importerToTest.importType(new FileInputStream(actionXMLFile));		
		
		/* test */
		assertNotNull(type);
		
	}

}
