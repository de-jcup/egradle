package de.jcup.egradle.codeassist;

import static org.junit.Assert.*;

import org.junit.Test;

import de.jcup.egradle.core.model.Item;

public class ItemPathCreatorTest {
	
	@Test
	public void path_for_task_xyz__is_truncated_to_task_only(){
		/* prepare */
		ItemPathCreator creatorToTest = new ItemPathCreator();
		
		Item root = new Item();
		root.setName("root");
		
		Item task = new Item();
		task.setName("task xyz");
		
		root.add(task);
		
		/* execute */
		String path = creatorToTest.createPath(task);
		
		/* test */
		assertEquals("task",path);
	}

	@Test
	public void path_for_null_is_an_empty_string(){
		ItemPathCreator creatorToTest = new ItemPathCreator();
		assertEquals("", creatorToTest.createPath(null));
	}
	
	@Test
	public void item1_with_parent1_having_parent_0_returns_parent1dotitem1_as_id() {
		/* prepare */
		ItemPathCreator creatorToTest = new ItemPathCreator();
		
		Item root = new Item();
		root.setName("root");
		
		Item parent1 = new Item();
		parent1.setName("parent1");
		
		Item item1 = new Item();
		item1.setName("item1");
		
		root.add(parent1);
		parent1.add(item1);
		
		/* execute */
		String path = creatorToTest.createPath(item1);
		
		/* test */
		assertEquals("parent1.item1",path);
		
	}

	@Test
	public void rootItem_path_is_empty_string() {
		/* prepare */
		ItemPathCreator creatorToTest = new ItemPathCreator();
		
		Item root = new Item();
		root.setName("root");
		
		/* execute */
		String path = creatorToTest.createPath(root);
		
		/* test */
		assertEquals("",path);
		
	}
	
	@Test
	public void item_1_as_child_ofrootItem_path_contains_only_item1() {
		/* prepare */
		ItemPathCreator creatorToTest = new ItemPathCreator();
		
		Item root = new Item();
		root.setName("root");
		Item item1 = new Item();
		item1.setName("item1");
		
		root.add(item1);
		
		/* execute */
		String path = creatorToTest.createPath(item1);
		
		/* test */
		assertEquals("item1",path);
		
	}
}
