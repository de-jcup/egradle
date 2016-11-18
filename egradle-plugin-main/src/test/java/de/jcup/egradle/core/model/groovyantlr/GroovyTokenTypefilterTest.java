package de.jcup.egradle.core.model.groovyantlr;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.junit.Test;

import antlr.collections.AST;
public class GroovyTokenTypefilterTest {

	@Test
	public void import_type_is_type_to_filter_then_its_filtered() {
		/* prepare */
		GroovyTokenTypefilter filter = new GroovyTokenTypefilter(GroovyTokenTypes.IMPORT);
		AST ast = mock(AST.class);
		when(ast.getType()).thenReturn(GroovyTokenTypes.IMPORT);
		
		/* execute + test */
		assertTrue(filter.isFiltered(ast));
	}
	
	@Test
	public void import_type_is_not_type_to_filter_then_its_filtered() {
		/* prepare */
		GroovyTokenTypefilter filter = new GroovyTokenTypefilter(GroovyTokenTypes.PACKAGE_DEF);
		AST ast = mock(AST.class);
		when(ast.getType()).thenReturn(GroovyTokenTypes.IMPORT);
		
		/* execute + test */
		assertFalse(filter.isFiltered(ast));
	}

}
