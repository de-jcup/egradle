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
		parserToTest.enableDebugMode();
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
		TokenParserResult result = parserToTest.parse(is);
		
		/* test */
		/* @formatter:off */
//			DEBUG:LINE:0:parent1 {
//			DEBUG:LINE:1:    child1 {
//			DEBUG:LINE:2:              child1b {
//			DEBUG:LINE:3:               }      }}
//			TokenParserResult [description=NONE]
//			 Token(0) ['parent1', LNr:0, offset:0, type:UNKNOWN, parent:null]
//			    Token(1) ['{', LNr:0, offset:9, type:BRACE_OPENING, parent:0]
//			    Token(2) ['child1', LNr:1, offset:11, type:UNKNOWN, parent:0]
//			       Token(3) ['{', LNr:1, offset:22, type:BRACE_OPENING, parent:2]
//			       Token(4) ['child1b', LNr:2, offset:24, type:UNKNOWN, parent:2]
//			          Token(5) ['{', LNr:2, offset:46, type:BRACE_OPENING, parent:4]
//			       Token(6) ['}', LNr:3, offset:63, type:BRACE_CLOSING, parent:2]
//			    Token(7) ['}', LNr:3, offset:70, type:BRACE_CLOSING, parent:0]
//			 Token(8) ['}', LNr:3, offset:71, type:BRACE_CLOSING, parent:null]

		/* @formatter:on */
		
		List<Token> content = result.getTokens();
		Token token_0 = content.iterator().next();
		Iterator<Token> iterator = token_0.getChildren().iterator();
		iterator.next();// 1
		Token token_2 = iterator.next();
		iterator = token_2.getChildren().iterator();
		iterator.next();//3
		Token token_4 = iterator.next();

		assertEquals(offset0, token_0.getOffset());
		assertEquals(offset1, token_2.getOffset());
		assertEquals(offset2, token_4.getOffset());
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
		/* @formatter:off */
//			DEBUG:LINE:0:test1234 {
//			DEBUG:LINE:1:}
//			TokenParserResult [description=NONE]
//			 Token(0) ['test1234', LNr:0, offset:0, type:UNKNOWN, parent:null]
//			    Token(1) ['{', LNr:0, offset:10, type:BRACE_OPENING, parent:0]
//			 Token(2) ['}', LNr:1, offset:12, type:BRACE_CLOSING, parent:null]
		/* @formatter:on */
		
		
		assertNotNull(result);
		List<Token> content = result.getTokens();
		assertNotNull(content);
		assertEquals(2,content.size());
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
		/* @formatter:off*/
//			DEBUG:LINE:0:test1234 {
//			DEBUG:LINE:1:}
//			DEBUG:LINE:2:
//			DEBUG:LINE:3:test12345 {
//			DEBUG:LINE:4:}
//				TokenParserResult [description=NONE]
//				 Token(0) ['test1234', LNr:0, offset:0, type:UNKNOWN, parent:null]
//				    Token(1) ['{', LNr:0, offset:10, type:BRACE_OPENING, parent:0]
//				 Token(2) ['}', LNr:1, offset:12, type:BRACE_CLOSING, parent:null]
//				 Token(3) ['test12345', LNr:3, offset:15, type:UNKNOWN, parent:null]
//				    Token(4) ['{', LNr:3, offset:25, type:BRACE_OPENING, parent:3]
//				 Token(5) ['}', LNr:4, offset:27, type:BRACE_CLOSING, parent:null]
		/* @formatter:on*/
		
		assertNotNull(ast);
		List<Token> content = ast.getTokens();
		assertNotNull(content);
		assertEquals(4,content.size());
		Iterator<Token> iterator = content.iterator();
		Token element1 = iterator.next();
		iterator.next();
		Token element3 = iterator.next();
		
		assertEquals("test1234", element1.getName());
		assertEquals(0, element1.getLineNumber());
		assertEquals("test12345", element3.getName());
		assertEquals(3, element3.getLineNumber());
	}
	

	@Test
	public void rootproject4_test1_file() throws IOException{
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST1_GRADLE);

		
		TokenParserResult result = parserToTest.parse(fis);
		
		/* test */
		/* @formatter:off*/
//		TokenParserResult [description=NONE]
//				 Token(0) ['allprojects', LNr:0, offset:0, type:UNKNOWN, parent:null]
//				    Token(1) ['{', LNr:0, offset:12, type:BRACE_OPENING, parent:0]
//				    Token(2) ['repositories', LNr:1, offset:14, type:UNKNOWN, parent:0]
//				       Token(3) ['{', LNr:2, offset:30, type:BRACE_OPENING, parent:2]
//				       Token(4) ['mavenLocal()', LNr:3, offset:32, type:UNKNOWN, parent:2]
//				       Token(5) ['mavenCentral()', LNr:4, offset:47, type:UNKNOWN, parent:2]
//				    Token(6) ['}', LNr:5, offset:68, type:BRACE_CLOSING, parent:0]
//				 Token(7) ['}', LNr:7, offset:73, type:BRACE_CLOSING, parent:null]
		/* @formatter:on*/
		
		List<Token> elements = result.getTokens();
		assertEquals(2, elements.size());
		Token token_0 = elements.iterator().next();
		assertEquals("allprojects", token_0.getName());
		List<Token> token1_2_and_6 = token_0.getChildren();
		assertEquals(3, token1_2_and_6.size());
		Iterator<Token> iterator = token1_2_and_6.iterator();
		Token token_1 = iterator.next(); 
		assertEquals("{", token_1.getName());
		Token token_2 = iterator.next(); 
		assertEquals("repositories" ,token_2.getName());
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
//		TokenParserResult [description=NONE]
//		 Token(0) ['allprojects', LNr:0, offset:0, type:UNKNOWN, parent:null]
//		    Token(1) ['{', LNr:0, offset:12, type:BRACE_OPENING, parent:0]
//		    Token(2) ['/ a comment */', LNr:2, offset:18, type:COMMENT__MULTI_LINE, parent:0]
//		    Token(3) ['/ another comment , but complete line', LNr:3, offset:35, type:COMMENT__SINGLE_LINE, parent:0]
//		    Token(4) ['repositories', LNr:4, offset:73, type:UNKNOWN, parent:0]
//		       Token(5) ['{', LNr:4, offset:87, type:BRACE_OPENING, parent:4]
//		       Token(6) ['mavenLocal()', LNr:5, offset:89, type:UNKNOWN, parent:4]
//		       Token(7) ['mavenCentral()', LNr:6, offset:104, type:UNKNOWN, parent:4]
//		    Token(8) ['}', LNr:7, offset:125, type:BRACE_CLOSING, parent:0]
//		 Token(9) ['}', LNr:10, offset:135, type:BRACE_CLOSING, parent:null]
		/* @formatter:on*/
		List<Token> elements = result.getTokens();
		assertEquals(2, elements.size());
		Token token_0 = elements.iterator().next();
		assertEquals("allprojects", token_0.getName());
		List<Token> childElements = token_0.getChildren();
		assertEquals(5, childElements.size());
		Iterator<Token> iterator = childElements.iterator();
		Token token_1 = iterator.next(); 
		assertEquals(TokenType.BRACE_OPENING, token_1.getType());
		Token token_2 = iterator.next(); 
		assertEquals(TokenType.COMMENT__MULTI_LINE, token_2.getType());
		Token token_3 = iterator.next(); 
		assertEquals(TokenType.COMMENT__SINGLE_LINE, token_3.getType());
		Token token_4 = iterator.next(); 
		assertEquals("repositories" ,token_4.getName());
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
//		TOKENPARSERRESULT [DESCRIPTION=NONE]
//		 TOKEN(0) ['ALLPROJECTS', LNR:0, OFFSET:0, TYPE:UNKNOWN, PARENT:NULL]
//		    TOKEN(1) ['{', LNR:0, OFFSET:12, TYPE:BRACE_OPENING, PARENT:0]
//		    TOKEN(2) ['/ A COMMENT */', LNR:2, OFFSET:18, TYPE:COMMENT__MULTI_LINE, PARENT:0]
//		    TOKEN(3) ['REPOSITORIES', LNR:3, OFFSET:33, TYPE:UNKNOWN, PARENT:0]
//		       TOKEN(4) ['{', LNR:4, OFFSET:49, TYPE:BRACE_OPENING, PARENT:3]
//		       TOKEN(5) ['MAVENLOCAL()', LNR:5, OFFSET:51, TYPE:UNKNOWN, PARENT:3]
//		       TOKEN(6) ['MAVENCENTRAL()', LNR:6, OFFSET:66, TYPE:UNKNOWN, PARENT:3]
//		    TOKEN(7) ['}', LNR:7, OFFSET:87, TYPE:BRACE_CLOSING, PARENT:0]
//		 TOKEN(8) ['}', LNR:9, OFFSET:92, TYPE:BRACE_CLOSING, PARENT:Null]

		/* @formatter:on*/
		List<Token> elements = result.getTokens();
		assertEquals(2, elements.size());
		Token element = elements.iterator().next();
		assertEquals("allprojects", element.getName());
		List<Token> childElements = element.getChildren();
		assertEquals(4, childElements.size());
		Iterator<Token> iterator = childElements.iterator();
		Token childElement = iterator.next(); //1
		childElement = iterator.next(); //2
		childElement = iterator.next();//3 
		assertEquals("repositories" ,childElement.getName());
		iterator = childElement.getChildren().iterator();
		childElement = iterator.next(); //4
		childElement = iterator.next(); //5

		assertEquals("mavenLocal()",childElement.getName());
		childElement = iterator.next();//3 
		assertEquals("mavenCentral()",childElement.getName());
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
