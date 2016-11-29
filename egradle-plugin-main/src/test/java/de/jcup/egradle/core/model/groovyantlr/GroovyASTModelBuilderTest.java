/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.core.model.groovyantlr;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class GroovyASTModelBuilderTest {

	
	@Test
	public void test_one_variable_defintion_in_one_line() throws Exception{
		/* prepare */
		String text = "def variable1='Hello world... from groovy'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GroovyASTModelBuilder b = new GroovyASTModelBuilder(is);
		
		/* execute*/
		Model model = b.build(null);
		
		/* test */
		Item[] items = model.getRoot().getChildren();
		
		assertEquals(1, items.length);
		Item variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getName());
//		variableDefItem.print(System.out);
	
		int i=0;
		Item[] data = variableDefItem.getChildren();
		assertEquals("MODIFIERS", data[i++].getName());
		assertEquals("TYPE", data[i++].getName());
		assertEquals("variable1", data[i++].getName());
		assertEquals("=", data[i].getName());
		assertEquals("Hello world... from groovy", data[i].getChildren()[0].getName());
		assertEquals(4,data.length);
	}
	
	@Test
	public void test2_variable_definitions_in_two_lines() throws Exception{
		/* prepare */
		String text = "def variable1='Hello world... from groovy'\n def variable2='Hello world... from groovy'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GroovyASTModelBuilder b = new GroovyASTModelBuilder(is);
		
		/* execute*/
		Model model = b.build(null);
		
		/* test */
		Item[] items = model.getRoot().getChildren();
		
		assertEquals(2, items.length);
		Item variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getName());
//		variableDefItem.print(System.out);
	
		int i=0;
		Item[] data = variableDefItem.getChildren();
		assertEquals("MODIFIERS", data[i++].getName());
		assertEquals("TYPE", data[i++].getName());
		assertEquals("variable1", data[i++].getName());
		assertEquals("=", data[i].getName());
		assertEquals("Hello world... from groovy", data[i].getChildren()[0].getName());
		assertEquals(4,data.length);
		
		variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getName());
//		variableDefItem.print(System.out);
	
		i=0;
		variableDefItem = items[1];
		data = variableDefItem.getChildren();
		assertEquals("MODIFIERS", data[i++].getName());
		assertEquals("TYPE", data[i++].getName());
		assertEquals("variable2", data[i++].getName());
		assertEquals("=", data[i].getName());
		assertEquals("Hello world... from groovy", data[i].getChildren()[0].getName());
		assertEquals(4,data.length);
	}
	
	@Test
	public void testWithMoreData() throws Exception{
		/* prepare */
		String text = "def private String variable1='Hello world... from groovy'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GroovyASTModelBuilder b = new GroovyASTModelBuilder(is);
		
		/* execute*/
		Model model = b.build(null);
		
		/* test */
		Item[] items = model.getRoot().getChildren();
		
		assertEquals(1, items.length);
		Item variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getName());
	
		int i=0;
		Item[] data = variableDefItem.getChildren();
		assertEquals("MODIFIERS", data[i].getName());
		assertEquals("private", data[i++].getChildren()[0].getName());
		assertEquals("TYPE", data[i].getName());
		assertEquals("String", data[i++].getChildren()[0].getName());
		assertEquals("variable1", data[i++].getName());
		assertEquals("=", data[i].getName());
		assertEquals("Hello world... from groovy", data[i].getChildren()[0].getName());
	}

}
