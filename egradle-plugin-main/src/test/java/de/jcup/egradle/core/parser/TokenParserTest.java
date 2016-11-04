package de.jcup.egradle.core.parser;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.internal.matchers.Equals;

import de.jcup.egradle.core.TestUtil;
public class TokenParserTest {

	private TokenParser parserToTest;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before(){
		parserToTest = new TokenParser();
	}
	

	@Test
	public void parsing_null_throws_illegal_argument_exception() throws IOException {
		expectedException.expect(IllegalArgumentException.class);
		parserToTest.parse(null);
	}
	
	@Test
	public void parsing_stream_which_throws_an_ioexception_rethrows_the_exception() throws IOException {
		/* prepare */
		InputStream is = mock(InputStream.class);
		when(is.available()).thenReturn(1000);
		IOException exception = new IOException();
		when(is.read(any(),Matchers.anyInt(),Matchers.anyInt())).thenThrow(exception);
		
		/* test (after execute)*/
		expectedException.expect(new Equals(exception));
		
		
		/* execute */
		parserToTest.parse(is);
	}
	
	@Test
	public void parsing_stream_without_content_returns_an_result_without_children() throws IOException {
		/* prepare */
		InputStream is = createStringInputStream("");
		
		/* execute */
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		assertNotNull(result);
		
		List<Token> content = result.getTokens();
		assertNotNull(content);
		assertEquals(0,content.size());
	}
	
	
	@Test
	public void parsing_child1b_in_child1_in_parent1_tokens_have_correct_offsets() throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		
		/* @formatter:off*/
		int offset0 = sb.length();
		sb.append("parent1 {\n");
		sb.append("    ");
		int offset1 = sb.length();
		sb.append(      "child1 {\n");
		sb.append("              ");
		int offset2 = sb.length();
		sb.append(               "child1b {\n");
		sb.append("               }");
		sb.append("      }");
		sb.append("}");
		/* @formatter:on*/
		
		InputStream is = createStringInputStream(sb);
		
		/* execute */
		TokenParserResult ast = parserToTest.parse(is);
		
		/* test */
		List<Token> content = ast.getTokens();
		Token parent1 = content.iterator().next();
		Token child1 = parent1.getChildren().iterator().next();
		Token child1b = child1.getChildren().iterator().next();

		assertEquals(offset0, parent1.getOffset());
		assertEquals(offset1, child1.getOffset());
		assertEquals(offset2, child1b.getOffset());
	}
	
	@Test
	public void parsing_test1234_without_content_returns_result_with_test1234_as_element_with_linenumber_0__offset_0() throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		
		sb.append("test1234 {\n");
		sb.append("}");
		
		InputStream is = createStringInputStream(sb);
		
		/* execute */
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		assertNotNull(result);
		List<Token> content = result.getTokens();
		assertNotNull(content);
		assertEquals(1,content.size());
		Token element = content.iterator().next();
		
		assertEquals("test1234", element.getName());
		assertEquals(0, element.getLineNumber());
		assertEquals(0, element.getOffset());
	}
	
	@Test
	public void parsing_test1234_with_child1_as_content_element_returns_result_with_test1234_as_element_with_linenumber_0_offset_0_containing_child1_at_line_number3_offset_as_expected() throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		
		sb.append("test1234 {\n"); //lnr:0
		sb.append("\n");           //lnr:1
		sb.append("\n");           //lnr:2
		sb.append("child1 {\n");   //lnr:3
		sb.append("}");            //lnr:4;
		sb.append("}");            //lnr:5;

		InputStream is = createStringInputStream(sb);
		
		/* execute */
		parserToTest.enableDebugMode();
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		assertNotNull(result);
		List<Token> content = result.getTokens();
		assertNotNull(content);
		assertEquals(1,content.size());
		Iterator<Token> iterator = content.iterator();
		Token element1 = iterator.next();
		
		assertEquals("test1234", element1.getName());
		assertEquals(0, element1.getLineNumber());
		List<Token> childElements = element1.getChildren();
		assertNotNull(childElements);
		assertEquals(1, childElements.size());
		Token childElement = childElements.iterator().next();
		assertEquals("child1", childElement.getName());
		assertEquals(3, childElement.getLineNumber());
		assertEquals(13, childElement.getOffset());
	}
	
	@Test
	public void parsing_test1234_and_test12345_without_content_returns_result_with_test1234_and_test12345_as_elements_with_linenumber_0_and_3() throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		
		sb.append("test1234 {\n"); //lnr:0
		sb.append("}\n");            //lnr:1
		sb.append("\n");           //lnr:2
		sb.append("test12345 {\n");//lnr:3
		sb.append("}");            //lnr:4;
		
		InputStream is = createStringInputStream(sb);
		
		/* execute */
		TokenParserResult ast = parserToTest.parse(is);
		
		/* test */
		assertNotNull(ast);
		List<Token> content = ast.getTokens();
		assertNotNull(content);
		assertEquals(2,content.size());
		Iterator<Token> iterator = content.iterator();
		Token element1 = iterator.next();
		Token element2 = iterator.next();
		
		assertEquals("test1234", element1.getName());
		assertEquals(0, element1.getLineNumber());
		assertEquals("test12345", element2.getName());
		assertEquals(3, element2.getLineNumber());
	}
	

	@Test
	public void rootproject4_test1_file() throws IOException{
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST1_GRADLE);

		parserToTest.enableDebugMode();
		TokenParserResult result = parserToTest.parse(fis);
		
		List<Token> elements = result.getTokens();
		assertEquals(1, elements.size());
		Token element = elements.iterator().next();
		assertEquals("allprojects", element.getName());
		List<Token> childElements = element.getChildren();
		assertEquals(1, childElements.size());
		Token childElement = childElements.iterator().next(); 
		assertEquals("repositories" ,childElement.getName());
	}
	
	@Test
	public void rootproject4_test2_file() throws IOException{
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST2_GRADLE);
		
		/* execute*/
		TokenParserResult ast = parserToTest.parse(fis);
		
		/* test */
		List<Token> elements = ast.getTokens();
		assertEquals(1, elements.size());
		Token element = elements.iterator().next();
		assertEquals("allprojects", element.getName());
		List<Token> childElements = element.getChildren();
		assertEquals(1, childElements.size());
		Token childElement = childElements.iterator().next(); 
		assertEquals("repositories" ,childElement.getName());
	}
	
	@Test
	public void rootproject4_test3_file() throws IOException{
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST3_GRADLE);
		
		/* execute*/
		TokenParserResult ast = parserToTest.parse(fis);
		
		/* test */
		List<Token> elements = ast.getTokens();
		assertEquals(1, elements.size());
		Token element = elements.iterator().next();
		assertEquals("allprojects", element.getName());
		List<Token> childElements = element.getChildren();
		assertEquals(1, childElements.size());
		Token childElement = childElements.iterator().next(); 
		assertEquals("repositories" ,childElement.getName());
	}
	

	private InputStream createStringInputStream(StringBuilder sb) {
		String string = sb.toString();
		return createStringInputStream(string);
	}


	private InputStream createStringInputStream(String string) {
		ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes());
		return is;
	}

}
