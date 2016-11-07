package de.jcup.egradle.core.token.parser;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.internal.matchers.Equals;

import de.jcup.egradle.core.TestUtil;
import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenChainer;
import de.jcup.egradle.core.token.TokenType;

public class TokenParserTest {

	private TokenParser parserToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before() {
		parserToTest = new TokenParser();
		parserToTest.enableTraceMode();
	}
	
	@Test
	public void test__def_variable_equal_1__is_divided_in_4_tokens() throws IOException{
		String text = "def variable=1";
		/* prepare */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");
		
		List<Token> children = result.getRoot().getChildren();
		assertEquals(4,children.size());
		
	}
	
	@Test
	public void two_tokens_test1_and_test2_are_returned_when_lines_are_separated_by_backlash_n() throws IOException{
		String text = "test1\ntest2";
		/* prepare */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");
		Token test1 = result.getRoot().getFirstChild();
		Token test2 = test1.goForward();
		
		assertEquals("test1",test1.getValue());
		assertEquals("test2",test2.getValue());
	}
	
	@Test
	public void two_tokens_test1_and_test2_are_returned_when_lines_are_separated_by_backlash_r_and_n() throws IOException{
		String text = "test1\r\ntest2";
		/* prepare */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");
		Token test1 = result.getRoot().getFirstChild();
		Token test2 = test1.goForward();
		
		assertEquals("test1",test1.getValue());
		assertEquals("test2",test2.getValue());
	}
	
	@Test
	public void visit_lines__test_backslash_r_backslash_n_delimiters_are_still_in_lines() throws IOException{
		String text = "package de.jcup.egradle.examples\r\n";
		/* prepare */
		parserToTest = new TokenParser(){
			@Override
			protected void visit(String line) {
				/* test */
				super.visit(line);
				assertEquals("package de.jcup.egradle.examples\r\n",context.getLines().get(0));
			}
		};
		parserToTest.parse(createStringInputStream(text), "UTF-8");
	}
	
	@Test
	public void visit_lines__test_backslash_n_delimiters_are_still_in_lines() throws IOException{
		String text = "package de.jcup.egradle.examples\n";
		/* prepare */
		parserToTest = new TokenParser(){
			@Override
			protected void visit(String line) {
				/* test */
				super.visit(line);
				assertEquals("package de.jcup.egradle.examples\n",context.getLines().get(0));
			}
		};
		parserToTest.parse(createStringInputStream(text), "UTF-8");
	}

	@Test
	/**
	 * \r = CR (Carriage Return) // Used as a new line character in Mac OS
	 * before X
	 * 
	 * @throws IOException
	 */
	public void with_backslash_r__package_and_next_import_has_two_tokens__import_has_correct_pos() throws IOException {
		/* prepare */
		String text = "package de.jcup.egradle.examples\r\n\r\n";
		int expectedImportOffset = text.length();
		text += "import org.gradle.api.DefaultTask";

		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");

		/* test */
		Token packageToken = result.getRoot().getFirstChild();
		Token packageNameToken = packageToken.goForward();
		Token importToken = packageNameToken.goForward();
		Token importNameToken = importToken.goForward();

		assertEquals("import", importToken.getValue());
		assertEquals("org.gradle.api.DefaultTask", importNameToken.getValue());

		assertEquals(expectedImportOffset, importToken.getOffset());
	}

	@Test
	/**
	 * \r\n = CR + LF // Used as a new line character in Windows
	 * 
	 * @throws IOException
	 */
	public void with_backslash_r_backslash_n__package_and_next_import_has_two_tokens__import_has_correct_pos()
			throws IOException {
		/* prepare */
		String text = "package de.jcup.egradle.examples\r\n\r\n";
		int expectedImportOffset = text.length();
		text += "import org.gradle.api.DefaultTask";

		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");

		/* test */
		Token packageToken = result.getRoot().getFirstChild();
		Token packageNameToken = packageToken.goForward();
		Token importToken = packageNameToken.goForward();
		Token importNameToken = importToken.goForward();

		assertEquals("import", importToken.getValue());
		assertEquals("org.gradle.api.DefaultTask", importNameToken.getValue());

		assertEquals(expectedImportOffset, importToken.getOffset());
	}

	@Test
	/**
     * \n = LF (Line Feed) // Used as a new line character in Unix/Mac OS X
	 * @throws IOException
	 */
	public void with_backslash_n__package_and_next_import_has_two_tokens__import_has_correct_pos() throws IOException {
		/* prepare */
		String text = "package de.jcup.egradle.examples\n\n";
		int expectedImportOffset = text.length();
		text += "import org.gradle.api.DefaultTask";

		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");

		/* test */
		Token packageToken = result.getRoot().getFirstChild();
		Token packageNameToken = packageToken.goForward();
		Token importToken = packageNameToken.goForward();
		Token importNameToken = importToken.goForward();

		assertEquals("import", importToken.getValue());
		assertEquals("org.gradle.api.DefaultTask", importNameToken.getValue());

		assertEquals(expectedImportOffset, importToken.getOffset());
	}

	@Test
	public void test_with_parameter_paramToken___test_token_contains_parameter_token() throws IOException {
		/* prepare */
		String text = "test (paramToken)";

		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");

		/* test */
		Token test = result.getRoot().getFirstChild();
		assertFalse(test.hasChildren());
		assertTrue(test.canGoForward());

		Token bracket1 = test.goForward();
		assertEquals(TokenType.BRACKET_OPENING, bracket1.getType());
		assertTrue(bracket1.hasChildren());

		Token paramToken = bracket1.getFirstChild();
		assertEquals("paramToken", paramToken.getValue());

		Token bracket2 = bracket1.goForward();
		assertEquals(TokenType.BRACKET_CLOSING, bracket2.getType());

	}

	/**
	 * Real world problem - single line comment did manipulate next token pos
	 * 
	 * @throws IOException
	 */
	@Test
	public void integration_single_line_comment_has_no_wrong_position_effect_to_following_token() throws IOException {
		/* prepare */

		/* @formatter:off*/
		String text =
		"maven{\n"+
		"	";
		int expectedBlaOffset=text.length();
		text+="//bla\n"+
		"	";
		int expectedTestOffset=text.length();
		
		text+="test other\n"+
		"}\n";
		/* @formatter:on*/

		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");

		/* test */
		Token maven = result.getRoot().getFirstChild();
		Token openBrace = maven.goForward();
		assertEquals(TokenType.BRACE_OPENING, openBrace.getType());

		Token bla_comment = openBrace.getFirstChild();
		assertEquals("//bla", bla_comment.getValue());
		Token test = bla_comment.goForward();
		assertEquals(expectedBlaOffset, bla_comment.getOffset());
		assertEquals("//bla".length(), bla_comment.getLength());
		int diff = test.getOffset() - bla_comment.getOffset();
		assertEquals("Hmm.. problem - diff between test off set and comment lenth=" + diff, expectedTestOffset,
				test.getOffset());

	}

	/**
	 * Real world problem - wrong next token name creation after multi line
	 * comment
	 * 
	 * @throws IOException
	 */
	@Test
	public void integration_test_muli_line_comment_has_no_wrong_additional_child() throws IOException {
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST4_GRADLE);

		/* execute */
		TokenParserResult result = parserToTest.parse(fis, "UTF-8");

		/* test */
		/* @formatter:off*/
//		maven{
//			/* xxxx
//			   So for temporary workaround until available in maven central we use temp-m2-repo
//			   */
//		}
		/* @formatter:on*/

		Token maven = result.getRoot().getFirstChild();
		Token openBrace = maven.goForward();
		assertEquals(TokenType.BRACE_OPENING, openBrace.getType());

		Token comment = openBrace.getFirstChild();
		assertFalse(comment.canGoForward());

		Token closeBrace = openBrace.goForward();

		assertEquals(TokenType.BRACE_CLOSING, closeBrace.getType());
		assertEquals("}", closeBrace.getValue());

	}

	/**
	 * Tried to Real world example
	 * 
	 * @throws IOException
	 */
	@Test
	public void test_muli_line_comment_has_no_wrong_additional_child() throws IOException {
		/* prepare */
		/* @formatter:off*/
		String text = 
				"parent {\n"
				+ "    /* a multi line - \n"
				+ "      commment...\n"
				+ "      with\n"
				+ "      many\n"
				+ "      lines!\n"
				+ "     */\n"
				+ "    //url \"http://docbook4j.googlecode.com/svn/m2-repo/releases/\"\n"
				+ "    url \"http://docbook4j.googlecode.com/svn/m2-repo/releases/\n"
				+ "}";
		/* @formatter:off*/
		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");
		
		/* test */
		Token parent = result.getRoot().getFirstChild();
		Token bracet = parent.goForward();
		Token multiLineComment = bracet.getFirstChild();
		Token singleLineComment = multiLineComment.goForward();
		Token url = singleLineComment.goForward();
		Token urlString = url.goForward();
		
		assertEquals(TokenType.COMMENT__MULTI_LINE, multiLineComment.getType());
		assertEquals(TokenType.COMMENT__SINGLE_LINE, singleLineComment.getType());
		assertFalse(urlString.canGoForward());
	}
	@Test
	public void test_muli_line_comment_length() throws IOException{
		/* prepare */
		String text = "/* a multi line\n * commment...\nwith\nmany\nlines!\n */";
		int expectedLength= text.length();
		
		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");
		
		/* test */
		Token comment = result.getRoot().getFirstChild();
		assertEquals(expectedLength, comment.getLength());
	}
	
	@Test
	public void a_single_line_comment_followed_by_new_line_and_token_has_correct_length() throws IOException{
		/* prepare */
		String text = "//123456";
		int expectedLength = text.length();
		text=text+"\notherToken";
		
		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");
		
		/* test */
		Token comment = result.getRoot().getFirstChild();
		assertEquals(expectedLength, comment.getLength());
	}
	
	@Test
	public void an_apply_from_with_variables__and_otherToken__the_other_token_has_correct_posis_correct_parsed() throws IOException{
		/* prepare */
		String text = "apply from: \"${rootProject.projectDir}/libraries.gradle\"";
		int expectedPos = text.length();
		text=text+"otherToken";
		
		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");
		
		/* test */
		Token apply = result.getRoot().getFirstChild();
		Token from = apply.goForward();
		Token string = from.goForward();
		Token other= string.goForward();
		
		assertEquals(expectedPos, other.getOffset());
	}
	
	@Test
	public void a_string_with_braces_inside_has_excpected_length() throws IOException{
		/* prepare */
		String text = "\"${rootProject.projectDir}/libraries.gradle\"";
		int length = text.length();
		
		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");

		/* test */
		Token string = result.getRoot().getFirstChild();
		
		assertEquals(length, string.getLength());
	}
	
	@Test
	public void an_apply_from_with_variables__with_space_and_otherToken__the_other_token_has_correct_posittion_and_name() throws IOException{
		/* prepare */
		String text = "apply from: \"${rootProject.projectDir}/libraries.gradle\""+" ";
		int expectedPos = text.length();
		text=text+"otherToken";
		
		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");

		/* test */
		Token apply = result.getRoot().getFirstChild();
		Token from = apply.goForward();
		Token string = from.goForward();
		Token other= string.goForward();
		
		assertEquals(expectedPos, other.getOffset());
		assertEquals("otherToken",other.getValue());
	}
	
	@Test
	public void an_apply_from_with_variables__is_correct_parsed() throws IOException{
		/* prepare */
		String text = "apply from: \"${rootProject.projectDir}/libraries.gradle\"";
		
		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(text), "UTF-8");
		
		/* test */
		Token apply = result.getRoot().getFirstChild();
		Token from = apply.goForward();
		Token string = from.goForward();
		
		assertEquals("apply", apply.getValue());
		assertEquals("from:", from.getValue());
		assertEquals("${rootProject.projectDir}/libraries.gradle", string.getValue());
		
	}
	
	@Test
	public void after_a_multiline_comment_a_new_multiline_follows__the_new_token_offset_is_at_start_of_new_comment() throws IOException{
		/* prepare */
		StringBuilder sb = new StringBuilder();
		
		sb.append("                      /* comment1...*/\n");
		sb.append("                      ");
		int expectedPos=sb.length();
		sb.append("/* comment2...*/\n");
	
		/* execute */
		TokenParserResult result = parserToTest.parse(createStringInputStream(sb), "UTF-8");
	
		/* test */
		Token comment1 = result.getRoot().getFirstChild();
		Token comment2 = comment1.goForward();
		
		assertEquals(expectedPos, comment2.getOffset());
	}
	
	@Test
	public void aDoubleQuoteString_Containing_text_with_SingleQuotes_is_one_token_only() throws IOException{
		/* execute*/
		TokenParserResult result = parserToTest.parse(createStringInputStream("\"'test'\""), "UTF-8");
	
		
		/* test */
		Token first = result.getRoot().getFirstChild();
		assertEquals("'test'", first.getValue());
		assertEquals(TokenType.GSTRING, first.getType());
	}
	
	@Test
	public void a_multiline_comment_with_aDoubleQuoteString_and_SingleQuotes_is_one_token_only() throws IOException{
		/* execute*/
		String comment = "/*\"'test'\" */";
		TokenParserResult result = parserToTest.parse(createStringInputStream(comment), "UTF-8");
		
		/* test */
		Token first = result.getRoot().getFirstChild();
		assertEquals(comment, first.getValue());
		assertEquals(TokenType.COMMENT__MULTI_LINE, first.getType());
	}
	
	@Test
	public void a_single_line_comment_with_aDoubleQuoteString_and_SingleQuotes_is_one_token_only() throws IOException{
		/* execute*/
		String comment = "//\"'test'\" */";
		TokenParserResult result = parserToTest.parse(createStringInputStream(comment), "UTF-8");
		
		/* test */
		Token first = result.getRoot().getFirstChild();
		assertEquals(comment, first.getValue());
		assertEquals(TokenType.COMMENT__SINGLE_LINE, first.getType());
	}
	
	@Test
	public void parsing_does_execute_token_chainer_to_root_token() throws IOException {
		/* prepare */
		TokenChainer mockedChainer = mock(TokenChainer.class);
		parserToTest.tokenChainer=mockedChainer;
		
		/* execute*/
		TokenParserResult result = parserToTest.parse(createStringInputStream("parent"), "UTF-8");
		
		/* test */
		verify(mockedChainer).chain(result.getRoot());
	}

	@Test
	public void parsing_null_throws_illegal_argument_exception() throws IOException {
		expectedException.expect(IllegalArgumentException.class);
		parserToTest.parse(null, "UTF-8");
	}
	
	@Test
	public void parseOnlyParenthisResultsInTwoTokensInSameHierarchy() throws Exception{
		String text ="{}";
		InputStream is = createStringInputStream(text);
		/* execute */
		TokenParserResult result = parserToTest.parse(is, "UTF-8");
		
		/* test */
		assertEquals(2,result.getRoot().getChildren().size());
	}
	
	@Test
	public void parseElementWithOnlyOneParenthisResultsInThreeTokens_but_no_children() throws Exception{
		String text ="parent {}";
		InputStream is = createStringInputStream(text);
		/* execute */
		TokenParserResult result = parserToTest.parse(is, "UTF-8");
		
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
		TokenParserResult result = parserToTest.parse(is, "UTF-8");
		
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
		TokenParserResult result = parserToTest.parse(is, "UTF-8");
		
		/* test */
		assertEquals(3,result.getRoot().getChildren().size());
		
		Token parent = result.getRoot().getFirstChild();
		assertEquals("parent",parent.getValue());
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
		parserToTest.parse(is, "UTF-8");
	}
	
	@Test
	public void parsing_stream_without_content_returns_an_result_without_children() throws IOException {
		/* prepare */
		InputStream is = createStringInputStream("");
		
		/* execute */
		TokenParserResult result = parserToTest.parse(is, "UTF-8");
		
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
		TokenParserResult result = parserToTest.parse(is, "UTF-8");

		/* test */
		/* @formatter:off */
        // DEBUG:LINE:0:parent1 {
        // DEBUG:LINE:1:    child1 {
        // DEBUG:LINE:2:              child1b {
        // DEBUG:LINE:3:               }      }}

		/* @formatter:on */
		Token parent1 = result.getRoot()
				.getFirstChild(); /* parent1 -> braces token */
		Token child1 = parent1.goForward()
				.getFirstChild(); /* child1 -> braces token */
		Token child1b = child1.goForward().getFirstChild();

		assertEquals(offset0, parent1.getOffset());
		assertEquals(offset1, child1.getOffset());
		assertEquals(offset2, child1b.getOffset());
	}

	@Test
	public void parsing_test1234_without_content_returns_result_with_test1234_as_element_with_linenumber_0__offset_0()
			throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();

		sb.append("test1234 {\n");
		sb.append("}");

		InputStream is = createStringInputStream(sb);

		/* execute */
		TokenParserResult result = parserToTest.parse(is, "UTF-8");

		/* test */
		assertNotNull(result);
		Token test1234 = result.getRoot().getFirstChild();

		assertEquals("test1234", test1234.getValue());
		assertEquals(0, test1234.getLineNumber());
		assertEquals(0, test1234.getOffset());
	}

	@Test
	public void parsing_test1234_with_child1_as_content_element_returns_result_with_test1234_as_element_with_linenumber_0_offset_0_containing_child1_at_line_number3_offset_as_expected()
			throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();

		sb.append("test1234 {\n"); // lnr:0
		sb.append("\n"); // lnr:1
		sb.append("\n"); // lnr:2
		sb.append("child1 {\n"); // lnr:3
		sb.append("}"); // lnr:4;
		sb.append("}"); // lnr:5;

		InputStream is = createStringInputStream(sb);

		/* execute */

		TokenParserResult result = parserToTest.parse(is, "UTF-8");

		/* test */

		Token test1234 = result.getRoot().getFirstChild();
		Token child1 = test1234.goForward().getFirstChild();

		assertEquals("test1234", test1234.getValue());
		assertEquals(0, test1234.getLineNumber());

		assertEquals("child1", child1.getValue());
		assertEquals(3, child1.getLineNumber());
		assertEquals(13, child1.getOffset());
	}

	@Test
	public void parsing_test1234_and_test12345_without_content_returns_result_with_test1234_and_test12345_as_elements_with_linenumber_0_and_3()
			throws IOException {
		/* prepare */
		StringBuilder sb = new StringBuilder();
		sb.append("test1234 {\n"); // lnr:0
		sb.append("}\n"); // lnr:1
		sb.append("\n"); // lnr:2
		sb.append("test12345 {\n");// lnr:3
		sb.append("}"); // lnr:4;

		InputStream is = createStringInputStream(sb);

		/* execute */
		TokenParserResult result = parserToTest.parse(is, "UTF-8");

		/* test */
		assertNotNull(result);
		Token test1234 = result.getRoot().getFirstChild();
		Token test12345 = test1234.goForward().goForward().goForward();

		assertEquals("test1234", test1234.getValue());
		assertEquals(0, test1234.getLineNumber());
		assertEquals("test12345", test12345.getValue());
		assertEquals(3, test12345.getLineNumber());
	}

	@Test
	public void rootproject4_test1_file() throws IOException {
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST1_GRADLE);

		TokenParserResult result = parserToTest.parse(fis, "UTF-8");

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
		Token bracket1 = mavenlocal.goForward();
		Token bracket2 = bracket1.goForward();

		Token mavenCentral = bracket2.goForward();

		assertEquals("allprojects", allProjects.getValue());
		assertEquals("repositories", repositories.getValue());
		assertEquals("mavenLocal", mavenlocal.getValue());
		assertEquals("mavenCentral", mavenCentral.getValue());
	}

	@Test
	public void rootproject4_test2_file() throws IOException {
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST2_GRADLE);

		/* execute */
		TokenParserResult result = parserToTest.parse(fis, "UTF-8");

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
		Token mavenCentral = mavenlocal.goForward().goForward().goForward(); // ()...

		assertEquals("allprojects", allProjects.getValue());
		assertEquals("repositories", repositories.getValue());
		assertEquals("mavenLocal", mavenlocal.getValue());
		assertEquals("mavenCentral", mavenCentral.getValue());
	}

	@Test
	public void rootproject4_test3_file() throws IOException {
		/* prepare */
		FileInputStream fis = new FileInputStream(TestUtil.ROOTFOLDER_4_TEST3_GRADLE);

		/* execute */
		TokenParserResult result = parserToTest.parse(fis, "UTF-8");

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
		assertEquals("allprojects", allProjects.getValue());
		Token comment = allProjects.goForward().getFirstChild();
		assertEquals(TokenType.COMMENT__MULTI_LINE, comment.getType());
		Token repositories = comment.goForward();
		assertEquals("repositories", repositories.getValue());
		Token mavenlocal = repositories.goForward().getFirstChild();
		Token mavencentral = mavenlocal.goForward().goForward().goForward();

		assertEquals("mavenLocal", mavenlocal.getValue());
		assertEquals("mavenCentral", mavencentral.getValue());
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
