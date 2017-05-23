package de.jcup.egradle.template;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TemplateFileNameTransformerTest {

	private TemplateFileNameTransformer transformerToTest;
	private Properties properties;

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Before
	public void before(){
		properties= new Properties();
		transformerToTest=new TemplateFileNameTransformer(properties);
	}
	
	@Test
	public void constructor_with_null_throws_illegal_argument() {
		expected.expect(IllegalArgumentException.class);
		
		transformerToTest=new TemplateFileNameTransformer(null);
	}

	@Test
	public void null_is_transformed_to__null() {
		assertEquals(null, transformerToTest.transform(null));
	}
	
	@Test
	public void underscore_file1_is_replaced_to_file1() {
		assertEquals("file1", transformerToTest.transform("_file1"));
	}
	
	@Test
	public void file1_is_kept_as_file1() {
		assertEquals("file1", transformerToTest.transform("file1"));
	}
	
	@Test
	public void $filename_is_kept_as_$filename_when_no_property_with_name_filename() {
		assertEquals("$filename", transformerToTest.transform("$filename"));
	}
	
	@Test
	public void $filename_is_transformed_to_TestFile_when_property_with_name_filename_is_set_to_TestFile() {
		properties.put("filename", "TestFile");
		assertEquals("TestFile", transformerToTest.transform("$filename"));
	}
	
	@Test
	public void $filename_is_kept_as_$filename_when_property_with_name_filename_is_empty() {
		properties.put("filename", "");
		assertEquals("$filename", transformerToTest.transform("$filename"));
	}
	
	@Test
	public void $egradle_template_subprojectname_separated_by_dot_is_transformed() {
		properties.put("egradle.template.subprojectname", "testSubProject");
		assertEquals("testSubProject", transformerToTest.transform("$egradle.template.subprojectname"));
	}
}
