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
		parserToTest.enableTraceMode();
	}
	
	@Test
	public void parsing_does_execute_token_chainer_to_root_token() throws IOException {
		/* prepare */
		TokenChainer mockedChainer = mock(TokenChainer.class);
		parserToTest.tokenChainer=mockedChainer;
		
		/* execute*/
		TokenParserResult result = parserToTest.parse(createStringInputStream("parent"));
		
		/* test */
		verify(mockedChainer).chain(result.getRoot());
	}

	@Test
	public void parsing_null_throws_illegal_argument_exception() throws IOException {
		expectedException.expect(IllegalArgumentException.class);
		parserToTest.parse(null);
	}
	
	@Test
	public void parseOnlyParenthisResultsInTwoTokensInSameHierarchy() throws Exception{
		String text ="{}";
		InputStream is = createStringInputStream(text);
		/* execute */
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		assertEquals(2,result.getRoot().getChildren().size());
	}
	
	@Test
	public void parseElementWithOnlyOneParenthisResultsInThreeTokens_but_no_children() throws Exception{
		String text ="parent {}";
		InputStream is = createStringInputStream(text);
		/* execute */
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		assertEquals(3,result.getRoot().getChildren().size());
		Token parent = result.getRoot().getFirstChild();
		assertEquals(0,parent.getChildren().size());
	}
	
	@Test
	public void parseElementNoWhitespaceWithOnlyOneParenthisResultsInThreeTokens_no_Children() throws Exception{
		String text ="parent{}";
		InputStream is = createStringInputStream(text);
		/* execute */
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		assertEquals(3,result.getRoot().getChildren().size());
		Token parent = result.getRoot().getFirstChild();
		assertEquals(0,parent.getChildren().size());
	}
	
	@Test
	public void parseParentWithOneChild_results_in_3_maintokens_and_one_child() throws Exception{
		String text ="parent {child}";
		InputStream is = createStringInputStream(text);
		/* execute */
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		assertEquals(3,result.getRoot().getChildren().size());
		
		Token parent = result.getRoot().getFirstChild();
		assertEquals("parent",parent.getName());
		Token braceOpen = parent.goForward();
		assertEquals(1,braceOpen.getChildren().size());
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
		
		List<Token> content = result.getRoot().getChildren();
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
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		/* @formatter:off */
        // DEBUG:LINE:0:parent1 {
        // DEBUG:LINE:1:    child1 {
        // DEBUG:LINE:2:              child1b {
        // DEBUG:LINE:3:               }      }}

		/* @formatter:on */
		Token parent1 = result.getRoot().getFirstChild(); /* parent1 -> braces token */
		Token child1 = parent1.goForward().getFirstChild(); /* child1 -> braces token */
		Token child1b = child1.goForward().getFirstChild();

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
		Token test1234 = result.getRoot().getFirstChild();
		
		assertEquals("test1234", test1234.getName());
		assertEquals(0, test1234.getLineNumber());
		assertEquals(0, test1234.getOffset());
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
		
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		
		Token test1234 = result.getRoot().getFirstChild();
		Token child1 = test1234.goForward().getFirstChild();
		
		assertEquals("test1234", test1234.getName());
		assertEquals(0, test1234.getLineNumber());
		
		assertEquals("child1", child1.getName());
		assertEquals(3, child1.getLineNumber());
		assertEquals(13, child1.getOffset());
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
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		assertNotNull(result);
		Token test1234 = result.getRoot().getFirstChild();
		Token test12345= test1234.goForward().goForward().goForward();
		
		assertEquals("test1234", test1234.getName());
		assertEquals(0, test1234.getLineNumber());
		assertEquals("test12345", test12345.getName());
		assertEquals(3, test12345.getLineNumber());
	}
	

	@Test
	public void rootproject4_test1_file() throws IOException{
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST1_GRADLE);

		
		TokenParserResult result = parserToTest.parse(fis);
		
		/* test */
		/* @formatter:off*/
//		TRACE:0:allprojects{
//		TRACE:1:	
//	    TRACE:2:	repositories {
//	    TRACE:3:		mavenLocal()
//	    TRACE:4:		mavenCentral()
//	    TRACE:5:    }
//	    TRACE:6:		
//	    TRACE:7:}
		/* @formatter:on*/
		
		Token allProjects = result.getRoot().getFirstChild();
		Token repositories = allProjects.goForward().getFirstChild();
		Token mavenlocal = repositories.goForward().getFirstChild();
		Token mavenCentral = mavenlocal.goForward();
		
		assertEquals("allprojects", allProjects.getName());
		assertEquals("repositories" ,repositories.getName());
		assertEquals("mavenLocal()" ,mavenlocal.getName());
		assertEquals("mavenCentral()" ,mavenCentral.getName());
	}
	
	@Test
	public void rootproject4_test2_file() throws IOException{
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST2_GRADLE);
		
		/* execute*/
		TokenParserResult result = parserToTest.parse(fis);
		
		/* test */
		
		/* @formatter:off*/
//		DEBUG:LINE:0:allprojects{
//		DEBUG:LINE:1:	
//		DEBUG:LINE:2:	/* a comment */
//		DEBUG:LINE:3:	// another comment , but complete line
//		DEBUG:LINE:4:	repositories {
//		DEBUG:LINE:5:		mavenLocal()
//		DEBUG:LINE:6:		mavenCentral()
//		DEBUG:LINE:7:    }
//		DEBUG:LINE:8:    
//		DEBUG:LINE:9:		
//		DEBUG:LINE:10:}
		/* @formatter:on*/
		Token allProjects = result.getRoot().getFirstChild();
		Token aComment = allProjects.goForward().getFirstChild();
		Token anotherComment = aComment.goForward();
		Token repositories = anotherComment.goForward();
		
		Token mavenlocal = repositories.goForward().getFirstChild();
		Token mavenCentral = mavenlocal.goForward();
		
		assertEquals("allprojects", allProjects.getName());
		assertEquals("repositories" ,repositories.getName());
		assertEquals("mavenLocal()" ,mavenlocal.getName());
		assertEquals("mavenCentral()" ,mavenCentral.getName());
	}
	
	@Test
	public void rootproject4_test3_file() throws IOException{
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST3_GRADLE);
		
		/* execute*/
		TokenParserResult result = parserToTest.parse(fis);
		
		/* test */
		/* @formatter:off*/
//		DEBUG:LINE:0:ALLPROJECTS{
//		DEBUG:LINE:1:	
//		DEBUG:LINE:2:	/* A COMMENT */
//		DEBUG:LINE:3:	
//		DEBUG:LINE:4:	REPOSITORIES {
//		DEBUG:LINE:5:		MAVENLOCAL()
//		DEBUG:LINE:6:		MAVENCENTRAL()
//		DEBUG:LINE:7:    }
//		DEBUG:LINE:8:		
//		DEBUG:LINE:9:}

		/* @formatter:on*/
		Token allProjects = result.getRoot().getFirstChild();
		assertEquals("allprojects", allProjects.getName());
		Token comment = allProjects.goForward().getFirstChild();
		assertEquals(TokenType.COMMENT__MULTI_LINE, comment.getType());
		Token repositories = comment.goForward();
		assertEquals("repositories", repositories.getName());
		Token mavenlocal = repositories.goForward().getFirstChild();
		Token mavencentral = mavenlocal.goForward();
		
		assertEquals("mavenLocal()",mavenlocal.getName());
		assertEquals("mavenCentral()",mavencentral.getName());
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
