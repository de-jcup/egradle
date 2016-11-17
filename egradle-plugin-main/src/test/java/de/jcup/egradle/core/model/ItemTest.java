package de.jcup.egradle.core.model;

import static org.junit.Assert.*;

import org.junit.Test;

import de.jcup.egradle.core.model.Item;

public class ItemTest {

	@Test
	public void item_tree_works() {
		Item item1 = new Item();
		Item item2 = new Item();
		Item item3 = new Item();
		Item item4 = new Item();
		Item item5 = new Item();
		
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
