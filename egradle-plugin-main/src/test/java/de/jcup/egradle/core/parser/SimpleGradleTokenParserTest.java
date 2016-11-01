package de.jcup.egradle.core.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
public class SimpleGradleTokenParserTest {

	private SimpleGradleTokenParser parserToTest;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before(){
		parserToTest = new SimpleGradleTokenParser();
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
	public void parsing_stream_without_content_returns_an_ast_without_children() throws IOException {
		/* prepare */
		ByteArrayInputStream is = new ByteArrayInputStream("".getBytes());
		
		/* execute */
		GradleAST result = parserToTest.parse(is);
		
		/* test */
		assertNotNull(result);
		
		List<AbstractGradleToken> content = result.getRootElements();
		assertNotNull(content);
		assertEquals(0,content.size());
	}
	
	@Test
	public void parsing_test1234_without_content_returns_ast_with_test1234_as_element_with_linenumber_0__offset_0() throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		
		sb.append("test1234 {\n");
		sb.append("}");
		
		InputStream is = createStringInputStream(sb);
		
		/* execute */
		GradleAST ast = parserToTest.parse(is);
		
		/* test */
		assertNotNull(ast);
		List<AbstractGradleToken> content = ast.getRootElements();
		assertNotNull(content);
		assertEquals(1,content.size());
		AbstractGradleToken element = content.iterator().next();
		
		assertEquals("test1234", element.getName());
		assertEquals(0, element.getLineNumber());
		assertEquals(0, element.getOffset());
	}
	
	@Test
	public void parsing_test1234_with_child1_as_content_element_returns_ast_with_test1234_as_element_with_linenumber_0_offset_0_containing_child1_at_line_number3_offset_as_expected() throws IOException {
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
		GradleAST ast = parserToTest.parse(is);
		
		/* test */
		assertNotNull(ast);
		List<AbstractGradleToken> content = ast.getRootElements();
		assertNotNull(content);
		assertEquals(1,content.size());
		Iterator<AbstractGradleToken> iterator = content.iterator();
		AbstractGradleToken element1 = iterator.next();
		
		assertEquals("test1234", element1.getName());
		assertEquals(0, element1.getLineNumber());
		List<AbstractGradleToken> childElements = element1.getElements();
		assertNotNull(childElements);
		assertEquals(1, childElements.size());
		AbstractGradleToken childElement = childElements.iterator().next();
		assertEquals("child1", childElement.getName());
		assertEquals(3, childElement.getLineNumber());
		assertEquals(13, childElement.getOffset());
	}
	
	@Test
	public void parsing_test1234_and_test12345_without_content_returns_ast_with_test1234_and_test12345_as_elements_with_linenumber_0_and_3() throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		
		sb.append("test1234 {\n"); //lnr:0
		sb.append("}\n");            //lnr:1
		sb.append("\n");           //lnr:2
		sb.append("test12345 {\n");//lnr:3
		sb.append("}");            //lnr:4;
		
		InputStream is = createStringInputStream(sb);
		
		/* execute */
		GradleAST ast = parserToTest.parse(is);
		
		/* test */
		assertNotNull(ast);
		List<AbstractGradleToken> content = ast.getRootElements();
		assertNotNull(content);
		assertEquals(2,content.size());
		Iterator<AbstractGradleToken> iterator = content.iterator();
		AbstractGradleToken element1 = iterator.next();
		AbstractGradleToken element2 = iterator.next();
		
		assertEquals("test1234", element1.getName());
		assertEquals(0, element1.getLineNumber());
		assertEquals("test12345", element2.getName());
		assertEquals(3, element2.getLineNumber());
	}
	

	@Test
	public void rootproject4_test1_file() throws IOException{
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST1_GRADLE);
		
		GradleAST ast = parserToTest.parse(fis);
		List<AbstractGradleToken> elements = ast.getRootElements();
		assertEquals(1, elements.size());
		AbstractGradleToken element = elements.iterator().next();
		assertEquals("allprojects", element.getName());
		List<AbstractGradleToken> childElements = element.getElements();
		assertEquals(1, childElements.size());
		AbstractGradleToken childElement = childElements.iterator().next(); 
		assertEquals("repositories" ,childElement.getName());
	}
	
	@Test
	public void rootproject4_test2_file() throws IOException{
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST2_GRADLE);
		
		/* execute*/
		GradleAST ast = parserToTest.parse(fis);
		
		/* test */
		List<AbstractGradleToken> elements = ast.getRootElements();
		assertEquals(1, elements.size());
		AbstractGradleToken element = elements.iterator().next();
		assertEquals("allprojects", element.getName());
		List<AbstractGradleToken> childElements = element.getElements();
		assertEquals(1, childElements.size());
		AbstractGradleToken childElement = childElements.iterator().next(); 
		assertEquals("repositories" ,childElement.getName());
	}
	
	@Test
	public void rootproject4_test3_file() throws IOException{
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST3_GRADLE);
		
		/* execute*/
		GradleAST ast = parserToTest.parse(fis);
		
		/* test */
		List<AbstractGradleToken> elements = ast.getRootElements();
		assertEquals(1, elements.size());
		AbstractGradleToken element = elements.iterator().next();
		assertEquals("allprojects", element.getName());
		List<AbstractGradleToken> childElements = element.getElements();
		assertEquals(1, childElements.size());
		AbstractGradleToken childElement = childElements.iterator().next(); 
		assertEquals("repositories" ,childElement.getName());
	}
	

	private InputStream createStringInputStream(StringBuilder sb) {
		ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		return is;
	}

}
