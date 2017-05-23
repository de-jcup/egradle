package de.jcup.egradle.template;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TemplateContentTransformerTest {

	private Properties properties;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Before
	public void before() {
		properties = new Properties();
	}

	@Test
	public void null_is_transformed_to__null() {
		TemplateContentTransformer transformerToTest = new TemplateContentTransformer(properties);
		assertEquals(null, transformerToTest.transform(null));
	}

	@Test
	public void constructor_with_null_throws_illegal_argument() {
		expected.expect(IllegalArgumentException.class);

		 new TemplateContentTransformer(null);
	}

	@Test
	public void content_is_not_changed_when_properties_empty() {
		/* prepare */
		TemplateContentTransformer transformerToTest = new TemplateContentTransformer(properties);
		
		StringBuilder sb = new StringBuilder();
		sb.append("// a simple comment line #{egradle.template.info} xxx");
		String source = sb.toString();

		/* execute */
		String transformed = transformerToTest.transform(source);

		/* test */
		assertEquals(source, transformed);
	}

	@Test
	public void content_is_changed_when_properties_has_value() {
		/* prepare */
		properties.put("egradle.template.info", "INFO");
		
		TemplateContentTransformer transformerToTest = new TemplateContentTransformer(properties);
		
		StringBuilder sb = new StringBuilder();
		sb.append("// a simple comment line #{egradle.template.info} xxx");
		String source = sb.toString();

		/* execute */
		String transformed = transformerToTest.transform(source);

		/* test */
		assertEquals("// a simple comment line INFO xxx", transformed);
	}

	@Test
	public void content_with_multiple_line_is_changed_where_properties_have_values() {
		/* prepare */
		properties.put("egradle.template.info1", "INFO1");
		properties.put("egradle.template.info3", "INFO3");
		
		TemplateContentTransformer transformerToTest = new TemplateContentTransformer(properties);

		StringBuilder sb = new StringBuilder();
		sb.append("// a simple comment line #{egradle.template.info1} xxx\n");
		sb.append("// a simple comment line #{egradle.template.info2} xxx\n");
		sb.append("// a simple comment line #{egradle.template.info3} xxx\n");
		sb.append("// a simple comment line #{egradle.template.info4} xxx");
		String source = sb.toString();

		/* execute */
		String transformed = transformerToTest.transform(source);

		/* test */
		StringBuilder sb2 = new StringBuilder();
		sb2.append("// a simple comment line INFO1 xxx\n");
		sb2.append("// a simple comment line #{egradle.template.info2} xxx\n");
		sb2.append("// a simple comment line INFO3 xxx\n");
		sb2.append("// a simple comment line #{egradle.template.info4} xxx");
		String expected = sb2.toString();

		assertEquals(expected, transformed);
	}

}
