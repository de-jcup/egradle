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

}
