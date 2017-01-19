package de.jcup.egradle.codecompletion;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codecompletion.SourceCodeInsertionSupport;
import de.jcup.egradle.codecompletion.SourceCodeInsertionSupport.InsertionData;

public class SourceCodeInsertionSupportTest {

	private SourceCodeInsertionSupport supportToTest;

	@Before
	public void before() {
		supportToTest = new SourceCodeInsertionSupport();
	}

	@Test
	public void string_to_prepare_is_null_returns_insertion_data_object() {
		assertNotNull("",supportToTest.prepareInsertionString(null));
	}
	
	@Test
	public void string_to_prepare_is_null_returns_emtpy_string_as_sourcecode() {
		assertEquals("",supportToTest.prepareInsertionString(null).sourceCode);
	}
	
	@Test
	public void string_to_prepare_is_null_returns_n1_as_cursoroffset() {
		assertEquals(-1,supportToTest.prepareInsertionString(null).cursorOffset);
	}

	@Test
	public void cursor_variable_in_code_is_replaced_and_result_contains_relative_cursor_offset() {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		sb.append("012345 $cursor bla");

		/* execute */
		InsertionData result = supportToTest.prepareInsertionString(sb.toString());
		
		/* test */
		assertEquals("012345  bla",result.sourceCode);
		assertEquals(7, result.cursorOffset);
	}
	
	@Test
	public void transform_textBefore_to_indent__space_123_tab_codePart__will_be_space_space_space_space_tab(){
		String transformed = supportToTest.transformTextBeforeToIndentString(" 123\tcodePart");
		assertEquals("    \t", transformed);
	}
	
	@Test
	public void transform_textBefore_to_indent__123_tab_codePart__will_be_space_space_space_tab(){
		String transformed = supportToTest.transformTextBeforeToIndentString("123\tcodePart");
		assertEquals("   \t", transformed);
	}
	
	@Test
	public void transform_textBefore_to_indent__123_space_codePart__will_be_space_space_space_space(){
		String transformed = supportToTest.transformTextBeforeToIndentString("123 codePart");
		assertEquals("    ", transformed);
	}
	
	@Test
	public void transform_textBefore_to_indent__1_space_XX_space_will_be_5xSpace(){
		String transformed = supportToTest.transformTextBeforeToIndentString("1 XX ");
		assertEquals("     ", transformed);
	}
	
	@Test
	public void transform_textBefore_to_indent__123456XX_will_be_empty(){
		String transformed = supportToTest.transformTextBeforeToIndentString("123456XX");
		assertEquals("", transformed);
	}
	
	@Test
	public void transform_textBefore_to_indent__123456XX_space_will_be_9xSpace(){
		String transformed = supportToTest.transformTextBeforeToIndentString("123456XX ");
		assertEquals("         ", transformed);
	}
	
	@Test
	public void indention_for_textBeforeColumn_having_8_characters_create_new_lines_with_same_indention_but_remains_only_whitespaces__until_last_sibling_which_is_handled_as_edited_part_of_code(){
		/* prepare */
		StringBuilder origin = new StringBuilder();
		origin.append("parent {\n");
		origin.append("  child\n");
		origin.append("}\n");

		/* execute */
		InsertionData result = supportToTest.prepareInsertionString(origin.toString()," 2345\tco");
		
		/* test */
		StringBuilder expected = new StringBuilder();
		expected.append("parent {\n");
		expected.append("     \t  child\n");
		expected.append("     \t}\n");
		
		assertEquals(expected.toString(),result.sourceCode);
	}
	
	

	@Test
	public void indention_for_textBeforeColumn_having_one_tab_character_create_new_lines_with_same_cursorPos_replaced_correctly(){
		/* prepare */
		StringBuilder origin = new StringBuilder();
		origin.append("parent {\n");
		origin.append("  $cursor\n");
		origin.append("}\n");

		/* execute */
		InsertionData result = supportToTest.prepareInsertionString(origin.toString(),"\t");
		
		/* test */
		StringBuilder expected = new StringBuilder();
		expected.append("parent {\n");
		expected.append("\t  ");
		int expectedPos = expected.length();
		expected.append("\n");
		expected.append("\t}\n");
		
		assertEquals(expected.toString(),result.sourceCode);
		assertEquals(expectedPos,result.cursorOffset);
	}
	
	

}
