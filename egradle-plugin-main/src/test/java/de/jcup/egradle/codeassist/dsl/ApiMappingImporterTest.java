package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.TestUtil;

public class ApiMappingImporterTest {


	private ApiMappingImporter importerToTest;
	private File dslFolder;
	private File mappingsFile;

	@Before
	public void before(){
		importerToTest = new ApiMappingImporter();
		dslFolder = new File(TestUtil.PARENT_OF_TEST,"dsl/3.0");
		mappingsFile = new File(dslFolder,"api-mapping.txt");
	}
	
	@Test
	public void all_two_mappings_are_loaded() throws Exception{
		
		 /* execute */
		 Map<String,String> map = importerToTest.importMapping(new FileInputStream(mappingsFile));
		 
		 /* test */
		 assertNotNull(map);
		 
		 Set<String> keys = map.keySet();
		 assertEquals(2, keys.size());
		 
		 assertTrue(keys.contains("Delete"));
		 assertTrue(keys.contains("ShortNameTestAction"));
		 
		 assertEquals("org.gradle.api.tasks.Delete", map.get("Delete"));
		 assertEquals("org.gradle.api.TestAction", map.get("ShortNameTestAction"));
	}

}
