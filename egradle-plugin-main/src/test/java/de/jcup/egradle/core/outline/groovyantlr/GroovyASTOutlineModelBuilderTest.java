package de.jcup.egradle.core.outline.groovyantlr;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineModel;
import de.jcup.egradle.core.outline.groovyantlr.GroovyASTOutlineModelBuilder;

public class GroovyASTOutlineModelBuilderTest {

	
	@Test
	public void test_one_variable_defintion_in_one_line() throws Exception{
		/* prepare */
		String text = "def variable1='Hello world... from groovy'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GroovyASTOutlineModelBuilder b = new GroovyASTOutlineModelBuilder(is);
		
		/* execute*/
		OutlineModel model = b.build();
		
		/* test */
		OutlineItem[] items = model.getRoot().getChildren();
		
		assertEquals(1, items.length);
		OutlineItem variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getName());
//		variableDefItem.print(System.out);
	
		int i=0;
		OutlineItem[] data = variableDefItem.getChildren();
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
		GroovyASTOutlineModelBuilder b = new GroovyASTOutlineModelBuilder(is);
		
		/* execute*/
		OutlineModel model = b.build();
		
		/* test */
		OutlineItem[] items = model.getRoot().getChildren();
		
		assertEquals(2, items.length);
		OutlineItem variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getName());
//		variableDefItem.print(System.out);
	
		int i=0;
		OutlineItem[] data = variableDefItem.getChildren();
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
		GroovyASTOutlineModelBuilder b = new GroovyASTOutlineModelBuilder(is);
		
		/* execute*/
		OutlineModel model = b.build();
		
		/* test */
		OutlineItem[] items = model.getRoot().getChildren();
		
		assertEquals(1, items.length);
		OutlineItem variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getName());
	
		int i=0;
		OutlineItem[] data = variableDefItem.getChildren();
		assertEquals("MODIFIERS", data[i].getName());
		assertEquals("private", data[i++].getChildren()[0].getName());
		assertEquals("TYPE", data[i].getName());
		assertEquals("String", data[i++].getChildren()[0].getName());
		assertEquals("variable1", data[i++].getName());
		assertEquals("=", data[i].getName());
		assertEquals("Hello world... from groovy", data[i].getChildren()[0].getName());
	}

}
