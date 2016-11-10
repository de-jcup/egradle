package de.jcup.egradle.core.outline.groovyantlr;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineItemType;
import de.jcup.egradle.core.outline.OutlineModel;

public class WantedOutlineModelBuilderTest {

	@Test
	public void test1_variable_definitions_in_one_line__item_created_has_correct_type() throws Exception {
		/* prepare */
		String text = "def variable1='Hello world'";
		InputStream is = new ByteArrayInputStream(text.getBytes());
		WantedOutlineModelBuilder b = new WantedOutlineModelBuilder(is);

		/* execute */
		OutlineModel model = b.build();

		/* test */
		OutlineItem[] items = model.getRoot().getChildren();

		assertEquals(1, items.length);
		OutlineItem variable1Item = items[0];

		assertEquals(OutlineItemType.VARIABLE, variable1Item.getItemType());
		assertEquals("variable1", variable1Item.getName());
		assertEquals(text.length(), variable1Item.getLength());
		assertEquals(0, variable1Item.getOffset());

	}

	@Test
	public void test2_variable_definitions_in_two_lines__types_and_offset_are_correct() throws Exception {
		/* prepare */
		String text = "def variable1='Hello world... from groovy'\n\n\n";
		int expectedOffsetOfVariable2 = text.length();
		text += "def variable2='Hello world... from groovy'";
		System.out.println("length2=" + text.length());
		InputStream is = new ByteArrayInputStream(text.getBytes());
		WantedOutlineModelBuilder b = new WantedOutlineModelBuilder(is);

		/* execute */
		OutlineModel model = b.build();

		/* test */
		OutlineItem[] items = model.getRoot().getChildren();

		assertEquals(2, items.length);
		OutlineItem variable1Item = items[0];
		OutlineItem variable2Item = items[1];

		assertEquals(OutlineItemType.VARIABLE, variable1Item.getItemType());
		assertEquals("variable1", variable1Item.getName());
		assertEquals(0, variable1Item.getOffset());

		assertEquals(OutlineItemType.VARIABLE, variable2Item.getItemType());
		assertEquals("variable2", variable2Item.getName());
		/*
		 * FIXME ATR, 10.11.2016: the offset calculation does not work - groovy
		 * antlr ast does NOT count white spaces. also i am not sure about the
		 * support of different line endings - see SourceBuffer which only
		 * supports \n at least at first glance. Maybe it makes sense to combine
		 * own token parser algorithm with this one - use token parser
		 * calculation and fetch info about column and line too. use calculated
		 * offset by mapping with column and line...
		 */
		assertEquals(expectedOffsetOfVariable2, variable2Item.getOffset());
	}

}
