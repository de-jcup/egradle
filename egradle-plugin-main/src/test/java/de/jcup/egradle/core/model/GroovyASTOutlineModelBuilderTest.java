package de.jcup.egradle.core.model;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

public class GroovyASTOutlineModelBuilderTest {

	
	@Test
	public void test() {
		/* prepare */
		String text = "def variable1='Hello world... from groovy'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GroovyASTOutlineModelBuilder b = new GroovyASTOutlineModelBuilder(is);
		
		/* execute*/
		OutlineModel model = b.build();
		
		/* test */
		Item[] items = model.getRoot().getChildren();
		
		assertEquals(1, items.length);
		Item variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getText());
//		variableDefItem.print(System.out);
	
		int i=0;
		Item[] data = variableDefItem.getChildren();
		assertEquals("MODIFIERS", data[i++].getText());
		assertEquals("TYPE", data[i++].getText());
		assertEquals("variable1", data[i++].getText());
		assertEquals("=", data[i].getText());
		assertEquals("Hello world... from groovy", data[i].getChildren()[0].getText());
		assertEquals(4,data.length);
	}
	
	@Test
	public void testWithMoreData() {
		/* prepare */
		String text = "def private String variable1='Hello world... from groovy'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GroovyASTOutlineModelBuilder b = new GroovyASTOutlineModelBuilder(is);
		
		/* execute*/
		OutlineModel model = b.build();
		
		/* test */
		Item[] items = model.getRoot().getChildren();
		
		assertEquals(1, items.length);
		Item variableDefItem = items[0];
		assertEquals("VARIABLE_DEF",variableDefItem.getText());
		variableDefItem.print(System.out);
	
		int i=0;
		Item[] data = variableDefItem.getChildren();
		assertEquals("MODIFIERS", data[i].getText());
		assertEquals("private", data[i++].getChildren()[0].getText());
		assertEquals("TYPE", data[i].getText());
		assertEquals("String", data[i++].getChildren()[0].getText());
		assertEquals("variable1", data[i++].getText());
		assertEquals("=", data[i].getText());
		assertEquals("Hello world... from groovy", data[i].getChildren()[0].getText());
	}

}
