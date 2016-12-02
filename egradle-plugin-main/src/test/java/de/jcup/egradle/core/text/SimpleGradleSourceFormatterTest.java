package de.jcup.egradle.core.text;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class SimpleGradleSourceFormatterTest {

	@Test
	public void double_quotes_example1__not_changed_because_curly_brackets_in_string() throws Exception {
		String example1 = load("/string-format-example-double-quotes-1.gradle");
		SimpleGradleSourceFormatter formatter = new SimpleGradleSourceFormatter();
		String result = formatter.format(example1, "UTF-8");

		/*
		 * expect nothing changed, because curly brackets are inside the string!
		 */
		assertEquals(example1, result);
	}
	
	@Test
	public void double_quotes_example2__not_changed_because_curly_brackets_in_string() throws Exception {
		String example1 = load("/string-format-example-double-quotes-2.gradle");
		SimpleGradleSourceFormatter formatter = new SimpleGradleSourceFormatter();
		String result = formatter.format(example1, "UTF-8");

		/*
		 * expect nothing changed, because curly brackets are inside the string!
		 */
		assertEquals(example1, result);
	}

	@Test
	public void single_quotes_example_1__not_changed_because_curly_brackets_in_string() throws Exception {
		String example1 = load("/string-format-example-single-quotes-1.gradle");
		SimpleGradleSourceFormatter formatter = new SimpleGradleSourceFormatter();
		String result = formatter.format(example1, "UTF-8");

		/*
		 * expect nothing changed, because curly brackets are inside the string!
		 */
		assertEquals(example1, result);
	}
	
	@Test
	public void single_quotes_example_2__not_changed_because_curly_brackets_in_string() throws Exception {
		String example1 = load("/string-format-example-single-quotes-2.gradle");
		SimpleGradleSourceFormatter formatter = new SimpleGradleSourceFormatter();
		String result = formatter.format(example1, "UTF-8");

		/*
		 * expect nothing changed, because curly brackets are inside the string!
		 */
		assertEquals(example1, result);
	}

	@Test
	public void slash_string_1__not_changed_because_curly_brackets_in_string() throws Exception {
		String example1 = load("/string-format-example-slashy-1.gradle");
		SimpleGradleSourceFormatter formatter = new SimpleGradleSourceFormatter();
		String result = formatter.format(example1, "UTF-8");

		/*
		 * expect nothing changed, because curly brackets are inside the string!
		 */
		assertEquals(example1, result);
	}
	
	@Test
	public void slash_string_2__not_changed_because_curly_brackets_in_string() throws Exception {
		String example1 = load("/string-format-example-slashy-2.gradle");
		SimpleGradleSourceFormatter formatter = new SimpleGradleSourceFormatter();
		String result = formatter.format(example1, "UTF-8");

		/*
		 * expect nothing changed, because curly brackets are inside the string!
		 */
		assertEquals(example1, result);
	}

	@Test
	public void method_containing_if_statement_is_formatted_correct() throws Exception {
		/* @formatter:off*/
		String code ="def method(boolean variable1){if(variable1){println('hello world')}}";
		
		SimpleGradleSourceFormatter f = new SimpleGradleSourceFormatter();
		String result = f.format(code, "UTF-8");
		
		String expected = 
		"def method(boolean variable1) {\n"+
		"   if(variable1) {\n"+
		"      println('hello world')\n"+
		"   }\n"+
		"   \n"+
		"}\n";

		assertEquals(expected,result);

		/* @formatter:on*/
	}

	@Test
	public void method_containing_gstring_with_curlybraces_having_line_breaks__string_is_not_touched()
			throws Exception {
		/* @formatter:off*/
		String code = 
				"def method(boolean variable1) {\n"+
				"   if(variable1) {"+
				"      println(\"hello {\"+\n"+
				"               \"world}\"\n"+
				"   }\n"+
				"   \n"+
				"}\n";
		
		SimpleGradleSourceFormatter f = new SimpleGradleSourceFormatter();
		String result = f.format(code, "UTF-8");

		String expected = 
				"def method(boolean variable1) {\n"+
				"   if(variable1) {\n"+
				"      println(\"hello {\"+\n"+
				"      \"world}\"\n"+
				"   }\n"+
				"   \n"+
				"}\n";
			   

		assertEquals(expected,result);

		/* @formatter:on*/
	}

	@Test
	public void method_containing_if_statement_is_formatted_correct_def__three_spaces_method__is_reduced_to_def_one_space_method()
			throws Exception {
		/* @formatter:off*/
		String code ="def   method(boolean variable1){if(variable1){println('hello world')}}";
		
		SimpleGradleSourceFormatter f = new SimpleGradleSourceFormatter();
		String result = f.format(code, "UTF-8");
		
		String expected = 
		"def method(boolean variable1) {\n"+
		"   if(variable1) {\n"+
		"      println('hello world')\n"+
		"   }\n"+
		"   \n"+
		"}\n";

		assertEquals(expected,result);

		/* @formatter:on*/
	}

	@Test
	public void method_containing_if_statement_is_formatted_correct_also_when_many_multiple_linebreaks_direct_after_method_def_followed_with_4_spaces()
			throws Exception {
		/* @formatter:off*/
		String code ="def method(boolean variable1)\n\n\n\n\n\n    {if(variable1){println('hello world')}}";
		
		SimpleGradleSourceFormatter f = new SimpleGradleSourceFormatter();
		String result = f.format(code, "UTF-8");
		
		String expected = 
		"def method(boolean variable1) {\n"+
		"   if(variable1) {\n"+
		"      println('hello world')\n"+
		"   }\n"+
		"   \n"+
		"}\n";

		assertEquals(expected,result);

		/* @formatter:on*/
	}

	@Test
	public void method_containing_if_statement_is_formatted_correct_even_when_gstring_contained_with_variable()
			throws Exception {
		/* @formatter:off*/
		String code ="def method(boolean variable1){if(variable1){println(\"variable1 was:${variable1}\")}}";
		
		SimpleGradleSourceFormatter f = new SimpleGradleSourceFormatter();
		String result = f.format(code, "UTF-8");
		
		String expected = 
		"def method(boolean variable1) {\n"+
		"   if(variable1) {\n"+
		"      println(\"variable1 was:${variable1}\")\n"+
		"   }\n"+
		"   \n"+
		"}\n";

		assertEquals(expected,result);

		/* @formatter:on*/
	}

	@Test
	public void method_containing_if_statement_is_formatted_correct_even_when_normal_string_contained_with_curly_brackets()
			throws Exception {
		/* @formatter:off*/
		String code ="def method(boolean variable1){if(variable1){println('testcase for curly braces in normal string: { or } or again { shall be no problem')}}";
		
		SimpleGradleSourceFormatter f = new SimpleGradleSourceFormatter();
		String result = f.format(code, "UTF-8");
		
		String expected = 
		"def method(boolean variable1) {\n"+
		"   if(variable1) {\n"+
		"      println('testcase for curly braces in normal string: { or } or again { shall be no problem')\n"+
		"   }\n"+
		"   \n"+
		"}\n";

		assertEquals(expected,result);

		/* @formatter:on*/
	}

	private String load(String name) throws IOException {
		InputStream stream = getClass().getResourceAsStream(name);
		if (stream==null){
			throw new FileNotFoundException("Cannot get stream to:"+name);
		}

		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(reader);

		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			if (br.ready()){
				sb.append(System.getProperty("line.separator"));
			}
		}
		return sb.toString();

	}

}
