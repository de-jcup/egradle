package de.jcup.egradle.core.outline.groovyantlr;

import static org.junit.Assert.*;

import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.junit.Test;

public class GroovyTokenTypeDebugInfoInspectorTest {

	@Test
	public void for_field_ANNOTATION_int_number_the_name_ANNOTATION_is_returned() {
		/* prepare */
		GroovyTokenTypeDebugInfoInspector inspector = new GroovyTokenTypeDebugInfoInspector();
		
		/* execute */
		String name = inspector.getGroovyTokenTypeName(GroovyTokenTypes.ANNOTATION);
		
		/* test  */
		assertEquals("ANNOTATION",name);
	
	}
	
	@Test
	public void for_unknown_field_int_number_the_name_with_three_question_operators_is_returned() {
		/* prepare */
		GroovyTokenTypeDebugInfoInspector inspector = new GroovyTokenTypeDebugInfoInspector();
		
		/* execute */
		String name = inspector.getGroovyTokenTypeName(Integer.MAX_VALUE);
		
		/* test  */
		assertEquals("???",name);
	
	}

}
