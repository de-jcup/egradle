package de.jcup.egradle.core.outline;

import static org.junit.Assert.*;

import org.junit.Test;

import de.jcup.egradle.core.outline.OutlineItem;

public class OutlineItemTest {

	@Test
	public void item_tree_works() {
		OutlineItem item1 = new OutlineItem();
		OutlineItem item2 = new OutlineItem();
		OutlineItem item3 = new OutlineItem();
		OutlineItem item4 = new OutlineItem();
		OutlineItem item5 = new OutlineItem();
		
		/* execute */
		item1.add(item2);
		item1.add(item3);
		item3.add(item4);
		item3.add(item5);
		
		/* test */
		assertEquals(2,item1.getChildren().length);
		assertEquals(item2, item1.getChildren()[0]);
		assertEquals(item3, item1.getChildren()[1]);
		
		assertEquals(2,item3.getChildren().length);
	}
	
}
