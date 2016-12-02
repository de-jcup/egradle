package de.jcup.egradle.core.text;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleGradleSourceFormatterTest {

	@Test
	public void method_containing_if_statement_is_formatted_correct() throws Exception{
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
	public void method_containing_gstring_with_curlybraces_having_line_breaks__string_is_not_touched() throws Exception{
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
	public void method_containing_if_statement_is_formatted_correct_def__three_spaces_method__is_reduced_to_def_one_space_method() throws Exception{
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
	public void method_containing_if_statement_is_formatted_correct_also_when_many_multiple_linebreaks_direct_after_method_def_followed_with_4_spaces() throws Exception{
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
	public void method_containing_if_statement_is_formatted_correct_even_when_gstring_contained_with_variable() throws Exception{
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
	public void method_containing_if_statement_is_formatted_correct_even_when_normal_string_contained_with_curly_brackets() throws Exception{
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

}
